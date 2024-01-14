package com.davenonymous.bonsaitrees3.blocks;

import com.davenonymous.bonsaitrees3.config.CommonConfig;
import com.davenonymous.bonsaitrees3.registry.SoilCompatibility;
import com.davenonymous.bonsaitrees3.registry.sapling.SaplingInfo;
import com.davenonymous.bonsaitrees3.registry.soil.SoilInfo;
import com.davenonymous.bonsaitrees3.setup.Registration;
import com.davenonymous.libnonymous.base.BaseBlockEntity;
import com.davenonymous.libnonymous.helper.EnchantmentHelper;
import com.davenonymous.libnonymous.helper.InventoryHelper;
import com.davenonymous.libnonymous.helper.RedstoneMode;
import com.davenonymous.libnonymous.helper.SpawnHelper;
import com.davenonymous.libnonymous.serialization.Store;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.world.level.block.Block.UPDATE_ALL;

public class BonsaiPotBlockEntity extends BaseBlockEntity<BonsaiPotBlockEntity> {
	private SoilInfo soilInfo = null;
	private SaplingInfo saplingInfo = null;

	@Store(sendInUpdatePackage = true)
	public int modelRotation = -1;

	@Store(sendInUpdatePackage = true)
	protected int growTicks;

	@Store(sendInUpdatePackage = true)
	protected int requiredTicks;

	@Store(sendInUpdatePackage = true)
	public RedstoneMode redstoneMode = RedstoneMode.IGNORE_POWER;

	@Store(sendInUpdatePackage = true)
	private final ItemStackHandler soilItemStacks = createSoilInputItemHandler();
	private final LazyOptional<IItemHandler> soilItemStackHandler = LazyOptional.of(() -> soilItemStacks);

	@Store(sendInUpdatePackage = true)
	private final ItemStackHandler saplingItemStacks = createSaplingInputItemHandler();

	@Store()
	private final ItemStackHandler outputItemStacks = createOutputItemHandler();
	private final LazyOptional<IItemHandler> outputItemStackHandler = LazyOptional.of(() -> outputItemStacks);

	@Store(sendInUpdatePackage = true)
	private final ItemStackHandler upgradeItemStacks = createUpgradesItemHandler();
	private final LazyOptional<IItemHandler> upgradeItemStackHandler = LazyOptional.of(() -> upgradeItemStacks);

	private final LazyOptional<IItemHandler> combinedItemStackHandler = LazyOptional.of(this::createCombinedItemHandler);

	private boolean autoCut = false;
	private boolean hopping = false;
	private boolean hasSilkTouch = false;
	private boolean hasBeeHive = false;
	//private boolean hasEnergyUpgrade = false;
	private int fortune = 0;
	private int efficiency = 0;
	private List<ItemStack> energyUpgrades = new ArrayList<ItemStack>();

	private int hoppingCooldown = 0;
	private int cuttingCooldown = 0;
	
	private boolean canSaplingGrow = false;

	public static final ModelProperty<BlockState> SOIL_BLOCK = new ModelProperty<>();
	public static final ModelProperty<FluidState> FLUID_BLOCK = new ModelProperty<>();

	public BonsaiPotBlockEntity(BlockPos pos, BlockState state) {
		super(Registration.BONSAI_POT_BLOCKENTITY.get(), pos, state);
	}


	@Override
	protected void initialize() {
		super.initialize();
		
		if (getLevel() == null)
			return;
		
		this.updateSaplingInfo();	// ADDED
		this.updateSoilBlock();		// ADDED
		this.updateModules();		// ADDED

		if(getLevel().isClientSide())
			return;

		if(this.modelRotation == -1) {
			this.modelRotation = this.level.random.nextInt(4);
		}

	}

	@Override
	public void tickBoth() {
		super.tickBoth();

		this.updateInfoObjects();
		this.updateGrowth();
	}

	@Override
	public void tickServer() {
		super.tickServer();

		this.hopOutput();
	}

	public void hopOutput() {
		if(!this.hopping) {
			return;
		}

		if(this.hoppingCooldown > 0) {
			this.hoppingCooldown--;
			return;
		}

		var belowHandler = getNeighborInventory(Direction.DOWN);
		if(belowHandler == null) {
			return;
		}

		boolean changed = false;
		for(int slot = 0; slot < outputItemStacks.getSlots(); slot++) {
			var stack = outputItemStacks.getStackInSlot(slot);
			if(stack.isEmpty()) {
				continue;
			}

			var simulatedReturnStack = ItemHandlerHelper.insertItemStacked(belowHandler, stack, true);
			if(simulatedReturnStack.equals(stack, false)) {
				continue;
			}

			var returnStack = ItemHandlerHelper.insertItemStacked(belowHandler, stack, false);
			outputItemStacks.setStackInSlot(slot, returnStack.copy());
			changed = true;
		}

		if(changed) {
			this.setChanged();
			this.notifyClients(false);
		} else {
			this.hoppingCooldown = CommonConfig.hoppingCooldown.get();
		}
	}


	@Nonnull
	@Override
	public ModelData getModelData() {
		return ModelData.builder().with(SOIL_BLOCK, getSoilBlock()).with(FLUID_BLOCK, getFluidBlock())
				.build();
	}

	@Override
	public AABB getRenderBoundingBox() {
		return new AABB(getBlockPos()).inflate(1.0d).expandTowards(0.0d, 1.0d, 0.0d);
	}


	public boolean hasSoil() {
		var soilStack = soilItemStacks.getStackInSlot(0);
		return !soilStack.isEmpty();
	}

	private void updateSoilBlock() {
		var soilStack = soilItemStacks.getStackInSlot(0);
		if(soilStack.isEmpty()) {
			soilInfo = null;
			canSaplingGrow = false;
			return;
		}

		if (soilInfo == null || !soilInfo.ingredient.test(soilStack)) {
			soilInfo = Registration.RECIPE_HELPER_SOIL.get().getSoilForItem(getLevel(), soilStack);
			canSaplingGrow = SoilCompatibility.INSTANCE.isReady && SoilCompatibility.INSTANCE.canTreeGrowOnSoil(this.saplingInfo, this.soilInfo);
		}
	}

	public ItemStack setSoil(ItemStack soilStack) {
		var result = soilItemStacks.insertItem(0, soilStack, false);
		updateSoilBlock();		// ADDED
		updateInfoObjects();
		this.setChanged();
		this.notifyClients();
		return result;
	}

	public FluidState getFluidBlock() {
		if(this.soilInfo == null) {
			return Fluids.EMPTY.defaultFluidState();
		}

		return this.soilInfo.fluidState;
	}

	public BlockState getSoilBlock() {
		if(this.soilInfo == null) {
			return Blocks.AIR.defaultBlockState();
		}

		return this.soilInfo.blockState;
	}

	public SoilInfo getSoilInfo() {
		return this.soilInfo;
	}


	public boolean hasSapling() {
		var saplingStack = saplingItemStacks.getStackInSlot(0);
		return !saplingStack.isEmpty();
	}

	public ItemStack getSapling() {
		return saplingItemStacks.getStackInSlot(0);
	}

	private void updateSaplingInfo() {
		var saplingStack = saplingItemStacks.getStackInSlot(0);
		if(saplingStack.isEmpty()) {
			saplingInfo = null;
			canSaplingGrow = false;
			return;
		}

		if (saplingInfo == null || !saplingInfo.ingredient.test(saplingStack)) {
			saplingInfo = Registration.RECIPE_HELPER_SAPLING.get().getSaplingInfoForItem(getLevel(), saplingStack);
			canSaplingGrow = SoilCompatibility.INSTANCE.isReady && SoilCompatibility.INSTANCE.canTreeGrowOnSoil(this.saplingInfo, this.soilInfo);
		}
	}

	public ItemStack setSapling(ItemStack saplingStack) {
		var result = saplingItemStacks.insertItem(0, saplingStack, false);
		updateSaplingInfo();	// ADDED
		updateInfoObjects();

		this.growTicks = 0;
		if(getLevel() != null) {
			this.modelRotation = this.level.random.nextInt(4);
		} else {
			this.modelRotation = 0;
		}
		this.setChanged();
		this.notifyClients();
		return result;
	}

	public SaplingInfo getSaplingInfo() {
		return this.saplingInfo;
	}


	private void updateModules() {
		hopping = false;
		autoCut = false;
		hasSilkTouch = false;
		hasBeeHive = false;
		//hasEnergyUpgrade = false;
		fortune = 0;
		efficiency = 0;
		energyUpgrades.clear();

		for(int slot = 0; slot < this.upgradeItemStacks.getSlots(); slot++) {
			var stack = this.upgradeItemStacks.getStackInSlot(slot);
			if(stack.isEmpty()) {
				continue;
			}

			if(stack.is(Blocks.BEE_NEST.asItem()) || stack.is(Blocks.BEEHIVE.asItem())) {
				hasBeeHive = true;
			}

			if(stack.is(Blocks.HOPPER.asItem())) {
				hopping = CommonConfig.enableHoppingUpgrade.get();
			}

			if(stack.getItem().canPerformAction(stack, ToolActions.AXE_DIG)) {
				if(!stack.isDamageableItem() || stack.getDamageValue() < stack.getMaxDamage()) {
					autoCut = CommonConfig.enableAutoCuttingUpgrade.get();
				}
			}

			if (CommonConfig.enableForgeEnergyUpgrade.get()) {
				var optEnergyCap = stack.getCapability(ForgeCapabilities.ENERGY).resolve();
				if(optEnergyCap.isPresent()) {
					var energyCap = optEnergyCap.get();
					if(energyCap.canExtract()) {
						energyUpgrades.add(stack);
					}
				}
			}
			
			if (stack.isEnchanted() || stack.getItem() instanceof EnchantedBookItem) {
				var enchantmentHelper = new EnchantmentHelper(stack);
				if (CommonConfig.enableFortuneUpgrade.get()) {
					var level = enchantmentHelper.getLevel(Enchantments.BLOCK_FORTUNE);
					fortune = CommonConfig.sumEnchantmentLevels.get() ? fortune + level : Math.max(fortune, level);
				}
				if (CommonConfig.enableEfficiencyUpgrade.get()) {
					var level = enchantmentHelper.getLevel(Enchantments.BLOCK_EFFICIENCY);
					efficiency = CommonConfig.sumEnchantmentLevels.get() ? efficiency + level : Math.max(efficiency, level);
				}
				hasSilkTouch = hasSilkTouch || enchantmentHelper.has(Enchantments.SILK_TOUCH);
			}
		}

		//hopping = CommonConfig.enableHoppingUpgrade.get() && hopping;
		//autoCut = CommonConfig.enableAutoCuttingUpgrade.get() && autoCut;
		//fortune = CommonConfig.enableFortuneUpgrade.get() ? fortune : 0;
		//hasEnergyUpgrade = CommonConfig.enableForgeEnergyUpgrade.get() && hasEnergyUpgrade;
		//efficiency = CommonConfig.enableEfficiencyUpgrade.get() ? efficiency : 0;
	}


	protected void updateInfoObjects() {
		//updateSaplingInfo();		MOVED
		//updateSoilBlock();		MOVED
		//updateModules();			MOVED

		if(this.soilInfo != null && this.saplingInfo != null) {
			int ticks = this.saplingInfo.getRequiredTicks();
			float soilModifier = this.soilInfo.getTickModifier();

			this.requiredTicks = (int) Math.ceil(ticks * soilModifier);
			if(efficiency > 0) {
				this.requiredTicks -= Math.floor(ticks * 0.05f * efficiency);
			}

			if(this.requiredTicks < CommonConfig.minimumRequiredTicks.get()) {
				this.requiredTicks = CommonConfig.minimumRequiredTicks.get();
			}
		} else {
			this.requiredTicks = Integer.MAX_VALUE;
		}
	}


	public boolean cutTree(boolean isAutoCut) {
		if(this.saplingInfo == null || this.soilInfo == null) {
			this.updateSaplingInfo();	// ADDED
			this.updateSoilBlock();		// ADDED
			this.updateModules();		// ADDED
			this.updateInfoObjects();
		}

		if(this.saplingInfo == null || this.soilInfo == null || this.getLevel() == null) {
			return false;
		}

		if(this.growTicks < this.requiredTicks * 1000) {
			return false;
		}

		if(this.getLevel().isClientSide()) {
			return false;
		}

		var hasNeigborSignal = this.getLevel().hasNeighborSignal(this.getBlockPos());
		if(this.redstoneMode == RedstoneMode.REQUIRE_POWER && !hasNeigborSignal) {
			return false;
		}

		if(this.redstoneMode == RedstoneMode.REJECT_POWER && hasNeigborSignal) {
			return false;
		}

		List<ItemStack> upgradeItems = InventoryHelper.getStacks(this.getUpgradeItemStacks());
		List<ItemStack> drops = this.saplingInfo.getRandomizedDrops(this.level.random, fortune, hasSilkTouch, hasBeeHive, upgradeItems);
		
		IItemHandler belowHandler = null;
		boolean changed = false;
		boolean canHop = hopping && (belowHandler = getNeighborInventory(Direction.DOWN)) != null;
		
		for (int i = 0; i < drops.size(); i++) {
			ItemStack drop = drops.get(i);
			ItemStack insertedStack = drop;
			ItemStack simulatedStack;

			// Test if any drop fits in internal output slots
			simulatedStack = ItemHandlerHelper.insertItemStacked(outputItemStacks, drop, true);
			if (!simulatedStack.equals(drop, false))
				insertedStack = ItemHandlerHelper.insertItemStacked(outputItemStacks, drop, false).copy();

			// Test if any remaining drop fits in external inventory
			if (canHop && !insertedStack.isEmpty()) {
				simulatedStack = ItemHandlerHelper.insertItemStacked(belowHandler, insertedStack, true);
				if (!simulatedStack.equals(insertedStack, false))
					insertedStack = ItemHandlerHelper.insertItemStacked(belowHandler, insertedStack, false).copy();
			}
			
			// Set changed if any drop fit
			changed = changed || !insertedStack.equals(drop, false);
		}
		
		// If axes should take damage && this was an autocut && the tree was cut
		// Get axe with remaining durability
		if(changed && isAutoCut && CommonConfig.autoCuttingDamagesItems.get()) {
			boolean shouldTakeDamage = level.random.nextDouble() <= CommonConfig.autoCuttingDamageChance.get();
			if(shouldTakeDamage) {
				for(int slotNum = 0; slotNum < this.getUpgradeItemStacks().getSlots(); slotNum++) {
					var stack = this.getUpgradeItemStacks().getStackInSlot(slotNum);
					if(!stack.isEmpty() && stack.getItem().canPerformAction(stack, ToolActions.AXE_DIG)) {
						if(stack.hurt(1, level.random, null)) {
							stack.shrink(1);
							stack.setDamageValue(0);
						}
						break;
					}
				}
			}
		}
		
		// If something changed, update
		if (changed) {
			this.setGrowTicks(0);
			this.modelRotation = this.level.random.nextInt(4);
			this.setChanged();
			this.notifyClients();
		}
		
		// Start growing again if any of the drop stacks fit
		return changed;
	}

	public boolean isGrowing() {
		return hasSapling() && this.growTicks < this.requiredTicks * 1000;
	}

	public double getProgress() {
		if(!hasSapling() || !hasSoil() || this.requiredTicks == 0) {
			return 0;
		}

		return (double) this.growTicks / (double) (this.requiredTicks*1000);
	}

	public double getProgress(float partialTicks) {
		if(!hasSapling() || !hasSoil() || this.requiredTicks == 0) {
			return 0;
		}

		double result = ((double) this.growTicks + partialTicks) / (double) (this.requiredTicks*1000);
		if(result >= 0.999) {
			result = 1.0d;
		}
		return result;
	}

	public void updateGrowth() {
		if(!hasSapling() || !hasSoil() || !canSaplingGrow) {	// Added "canSaplingGrow" to save server ticks
			this.setGrowTicks(0);
			return;
		}

		/* 
		 * THIS IS MOVED INTO "updateSaplingInfo" AND "updateSoilBlock"
		
		if(SoilCompatibility.INSTANCE.isReady && !SoilCompatibility.INSTANCE.canTreeGrowOnSoil(this.saplingInfo, this.soilInfo)) {
			this.setGrowTicks(0);
			return;
		}
		*/

		if(getLevel() != null && getProgress() >= 1.0f && autoCut) {
			if(this.cuttingCooldown > 0) {
				this.cuttingCooldown--;
			} else {
				var success = this.cutTree(true);
				if(!success) {
					this.cuttingCooldown = CommonConfig.cuttingCooldown.get();
				}
			}
		}

		if(getProgress() < 1.0f) {
			int rate = 1000;
			for (int i = 0; i < energyUpgrades.size(); i++) {
				var upgrade = energyUpgrades.get(i);
				var optEnergyCap = upgrade.getCapability(ForgeCapabilities.ENERGY).resolve();
				if(optEnergyCap.isPresent()) {
					var energyCap = optEnergyCap.get();
					if(energyCap.canExtract()) {
						var extracted = energyCap.extractEnergy(CommonConfig.maximumExtractedEnergyPerTick.get(), true);
						if(extracted > 0) {
							extracted = energyCap.extractEnergy(extracted, false);
							rate *= 1.0d + (extracted * CommonConfig.extraGrowthRatioPerFE.get());
						}
					}
				}
			}

			this.setGrowTicks(growTicks + rate);
		}
	}

	public void setGrowTicks(int growTicks) {
		int maxTicks = this.requiredTicks * 1000;
		
		if(growTicks > maxTicks)
			growTicks = maxTicks;
		
		if(growTicks != this.growTicks) {
			this.growTicks = growTicks;
			
			// Call "setChanged" only if grow state is either nothing or full to save server tick time
			if (this.growTicks == 0 || this.growTicks == maxTicks)
				this.setChanged();
			
			//this.setChanged();		DISABLED TO SAVE TICKS, ENABLE IF THERE'S SYNCHRONIZATION PROBLEMS
		}
	}

	public void boostProgress() {
		if(!isGrowing()) {
			return;
		}

		var limit = this.requiredTicks * 1000;
		this.growTicks += limit / 4;
		if(this.growTicks >= limit) {
			this.growTicks = limit;
		}

		notifyClients();
	}


	@Override
	public void onDataLoaded() {
		if(this.level != null) {
			this.updateSaplingInfo();	// ADDED
			this.updateSoilBlock();		// ADDED
			this.updateModules();		// ADDED
			this.updateInfoObjects();
			
			if(this.level.isClientSide) {
				requestModelDataUpdate();
			}

			level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), UPDATE_ALL);
		}
	}

	public ItemStackHandler getUpgradeItemStacks() {
		return upgradeItemStacks;
	}

	public ItemStackHandler getSaplingItemStacks() {
		return saplingItemStacks;
	}

	public ItemStackHandler getSoilItemStacks() {
		return soilItemStacks;
	}

	public ItemStackHandler getOutputItemStacks() {
		return outputItemStacks;
	}

	public static boolean isUpgradeItem(ItemStack stack) {
		// TODO: Create tag to be used for hopping upgrades
		if(CommonConfig.enableHoppingUpgrade.get() && stack.is(Blocks.HOPPER.asItem())) {
			return true;
		}

		// TODO: Create tag to be used for bee type upgrades
		if(stack.is(Blocks.BEEHIVE.asItem()) || stack.is(Blocks.BEE_NEST.asItem())) {
			return true;
		}

		if(CommonConfig.enableAutoCuttingUpgrade.get() && stack.getItem().canPerformAction(stack, ToolActions.AXE_DIG)) {
			return true;
		}

		if(stack.isEnchanted() || stack.getItem() instanceof EnchantedBookItem) {
			var enchantments = new EnchantmentHelper(stack);
			if(enchantments.has(Enchantments.SILK_TOUCH)) {
				return true;
			}

			if(CommonConfig.enableEfficiencyUpgrade.get() && enchantments.has(Enchantments.BLOCK_EFFICIENCY)) {
				return true;
			}

			if(CommonConfig.enableFortuneUpgrade.get() && enchantments.has(Enchantments.BLOCK_FORTUNE)) {
				return true;
			}
		}

		if(CommonConfig.enableForgeEnergyUpgrade.get()) {
			var optEnergyCap = stack.getCapability(ForgeCapabilities.ENERGY);
			if(optEnergyCap.isPresent()) {
				var energyCap = optEnergyCap.resolve().get();
				if(energyCap.canExtract()) {
					return true;
				}
			}
		}

		if(SoilCompatibility.INSTANCE.isUpgradeItem(stack)) {
			return true;
		}

		return false;
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if(cap == ForgeCapabilities.ITEM_HANDLER) {
			if(side == null) {
				return combinedItemStackHandler.cast();
			} else if(side == Direction.UP) {
				return upgradeItemStackHandler.cast();
			} else if(side == Direction.DOWN) {
				return outputItemStackHandler.cast();
			} else {
				return soilItemStackHandler.cast();
			}
		}

		return super.getCapability(cap, side);
	}

	@Nonnull
	private ItemStackHandler createSoilInputItemHandler() {
		return new ItemStackHandler(1) {

			@Override
			public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
				var soilInfo = Registration.RECIPE_HELPER_SOIL.get().getSoilForItem(level, stack);
				return soilInfo != null;
			}

			@Override
			public int getSlotLimit(int slot) {
				return 1;
			}

			@Override
			protected void onContentsChanged(int slot) {
				setChanged();
				updateSoilBlock();		// ADDED
				updateInfoObjects();
				notifyClients();
			}
		};
	}

	@Nonnull
	private ItemStackHandler createSaplingInputItemHandler() {
		return new ItemStackHandler(1) {

			@Override
			public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
				var saplingInfo = Registration.RECIPE_HELPER_SAPLING.get().getSaplingInfoForItem(level, stack);
				return saplingInfo != null;
			}

			@Override
			public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
				var previous = getStackInSlot(slot);
				if(!ItemStack.isSameItemSameTags(previous, stack)) {
					growTicks = 0;
					setChanged();
					notifyClients();
				}
				super.setStackInSlot(slot, stack);
			}

			@Override
			public int getSlotLimit(int slot) {
				return 1;
			}

			@Override
			protected void onContentsChanged(int slot) {
				setChanged();
				updateSaplingInfo();	// ADDED
				updateInfoObjects();
				notifyClients();
			}
		};
	}


	@Nonnull
	private ItemStackHandler createOutputItemHandler() {
		return new ItemStackHandler(6) {
			@Override
			protected void onContentsChanged(int slot) {
				setChanged();
			}
		};
	}


	private ItemStackHandler createUpgradesItemHandler() {
		return new ItemStackHandler(4) {
			@Override
			public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
				return isUpgradeItem(stack);
			}

			@Override
			public int getSlotLimit(int slot) {
				return 1;
			}

			@Override
			protected void onContentsChanged(int slot) {
				setChanged();
				updateModules();		// ADDED
				notifyClients();
			}
		};
	}

	@Nonnull
	private IItemHandler createCombinedItemHandler() {
		return new CombinedInvWrapper(soilItemStacks, saplingItemStacks, upgradeItemStacks) {
			@Nonnull
			@Override
			public ItemStack extractItem(int slot, int amount, boolean simulate) {
				return ItemStack.EMPTY;
			}

			@Nonnull
			@Override
			public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
				return stack;
			}
		};
	}

	public void dropItemStackContents() {
		if(this.getLevel() == null || this.getLevel().isClientSide()) {
			return;
		}

		SpawnHelper.dropItemHandlerContents(this.upgradeItemStacks, this.getLevel(), this.getBlockPos());
		SpawnHelper.dropItemHandlerContents(this.outputItemStacks, this.getLevel(), this.getBlockPos());
		SpawnHelper.dropItemHandlerContents(this.soilItemStacks, this.getLevel(), this.getBlockPos());
		SpawnHelper.dropItemHandlerContents(this.saplingItemStacks, this.getLevel(), this.getBlockPos());
	}


}