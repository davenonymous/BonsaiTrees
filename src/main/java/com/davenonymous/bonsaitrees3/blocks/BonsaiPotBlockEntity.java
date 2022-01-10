package com.davenonymous.bonsaitrees3.blocks;

import com.davenonymous.bonsaitrees3.config.CommonConfig;
import com.davenonymous.bonsaitrees3.libnonymous.base.BaseBlockEntity;
import com.davenonymous.bonsaitrees3.libnonymous.helper.EnchantmentHelper;
import com.davenonymous.bonsaitrees3.libnonymous.helper.SpawnHelper;
import com.davenonymous.bonsaitrees3.libnonymous.serialization.Store;
import com.davenonymous.bonsaitrees3.registry.SoilCompatibility;
import com.davenonymous.bonsaitrees3.registry.sapling.SaplingInfo;
import com.davenonymous.bonsaitrees3.registry.soil.SoilInfo;
import com.davenonymous.bonsaitrees3.setup.Registration;
import net.minecraft.client.multiplayer.ClientLevel;
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
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
	private final ItemStackHandler soilItemStacks = createSoilInputItemHandler();
	private final LazyOptional<IItemHandler> soilItemStackHandler = LazyOptional.of(() -> soilItemStacks);

	@Store(sendInUpdatePackage = true)
	private final ItemStackHandler saplingItemStacks = createSaplingInputItemHandler();
	private final LazyOptional<IItemHandler> saplingItemStackHandler = LazyOptional.of(() -> saplingItemStacks);

	@Store()
	private final ItemStackHandler outputItemStacks = createOutputItemHandler();
	private final LazyOptional<IItemHandler> outputItemStackHandler = LazyOptional.of(() -> outputItemStacks);

	@Store(sendInUpdatePackage = true)
	private final ItemStackHandler upgradeItemStacks = createUpgradesItemHandler();

	private final LazyOptional<IItemHandler> combinedItemStackHandler = LazyOptional.of(this::createCombinedItemHandler);

	private boolean autoCut = false;
	private boolean hopping = false;
	private boolean hasSilkTouch = false;
	private int fortune = 0;
	private int efficiency = 0;

	public static final ModelProperty<BlockState> SOIL_BLOCK = new ModelProperty<>();
	public static final ModelProperty<FluidState> FLUID_BLOCK = new ModelProperty<>();

	public BonsaiPotBlockEntity(BlockPos pos, BlockState state) {
		super(Registration.BONSAI_POT_BLOCKENTITY.get(), pos, state);
	}


	@Override
	protected void initialize() {
		super.initialize();

		if(getLevel() == null || getLevel().isClientSide()) {
			return;
		}

		if(this.modelRotation == -1) {
			this.modelRotation = this.getLevel().random.nextInt(4);
		}

	}

	@Override
	public void tickBoth() {
		super.tickBoth();

		this.updateInfoObjects();
		this.updateGrowth();
	}

	public void hopOutput() {
		if(!this.hopping) {
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
		}
	}


	@Nonnull
	@Override
	public IModelData getModelData() {
		return new ModelDataMap.Builder().withInitial(SOIL_BLOCK, getSoilBlock()).withInitial(FLUID_BLOCK, getFluidBlock())
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
			return;
		}

		soilInfo = Registration.RECIPE_HELPER_SOIL.getSoilForItem(getLevel(), soilStack);
	}

	public ItemStack setSoil(ItemStack soilStack) {
		var result = soilItemStacks.insertItem(0, soilStack, false);
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
			return;
		}

		saplingInfo = Registration.RECIPE_HELPER_SAPLING.getSaplingInfoForItem(getLevel(), saplingStack);
	}

	public ItemStack setSapling(ItemStack saplingStack) {
		var result = saplingItemStacks.insertItem(0, saplingStack, false);
		updateInfoObjects();

		this.growTicks = 0;
		if(getLevel() != null) {
			this.modelRotation = getLevel().random.nextInt(4);
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
		fortune = 0;
		efficiency = 0;

		for(int slot = 0; slot < this.upgradeItemStacks.getSlots(); slot++) {
			var stack = this.upgradeItemStacks.getStackInSlot(slot);
			if(stack.isEmpty()) {
				continue;
			}

			if(stack.is(Blocks.HOPPER.asItem())) {
				hopping = true;
			}

			if(stack.getItem().canPerformAction(stack, ToolActions.AXE_DIG)) {
				autoCut = true;
			}

			var enchantmentHelper = new EnchantmentHelper(stack);
			if(CommonConfig.sumEnchantmentLevels.get()) {
				fortune += enchantmentHelper.getLevel(Enchantments.BLOCK_FORTUNE);
				efficiency += enchantmentHelper.getLevel(Enchantments.BLOCK_EFFICIENCY);
			} else {
				fortune = Math.max(fortune, enchantmentHelper.getLevel(Enchantments.BLOCK_FORTUNE));
				efficiency = Math.max(efficiency, enchantmentHelper.getLevel(Enchantments.BLOCK_EFFICIENCY));
			}
			hasSilkTouch = hasSilkTouch || enchantmentHelper.has(Enchantments.SILK_TOUCH);
		}

		hopping = CommonConfig.enableHoppingUpgrade.get() && hopping;
		autoCut = CommonConfig.enableAutoCuttingUpgrade.get() && autoCut;
		fortune = CommonConfig.enableFortuneUpgrade.get() ? fortune : 0;
		efficiency = CommonConfig.enableEfficiencyUpgrade.get() ? efficiency : 0;
	}


	protected void updateInfoObjects() {
		updateSaplingInfo();
		updateSoilBlock();
		updateModules();

		if(this.soilInfo != null && this.saplingInfo != null) {
			int ticks = this.saplingInfo.getRequiredTicks();
			float soilModifier = this.soilInfo.getTickModifier();

			this.requiredTicks = (int) Math.ceil(ticks * soilModifier);
			if(efficiency > 0) {
				this.requiredTicks -= Math.floor(ticks * 0.05f * efficiency);
			}
		} else {
			this.requiredTicks = Integer.MAX_VALUE;
		}
	}


	public boolean cutTree() {
		if(this.saplingInfo == null || this.soilInfo == null) {
			this.updateInfoObjects();
		}

		if(this.saplingInfo == null || this.soilInfo == null || this.getLevel() == null) {
			return false;
		}

		if(this.growTicks < this.requiredTicks) {
			return false;
		}

		if(this.getLevel().isClientSide()) {
			return false;
		}

		List<ItemStack> drops = this.saplingInfo.getRandomizedDrops(getLevel().random, fortune, hasSilkTouch);

		// Test if all stacks fit in the output slots
		boolean allFit = true;
		for(ItemStack stack : drops) {
			ItemStack insertedStack = ItemHandlerHelper.insertItemStacked(outputItemStacks, stack, true);
			if(insertedStack.is(stack.getItem()) && insertedStack.getCount() == stack.getCount()) {
				allFit = false;
				break;
			}
		}

		if(allFit) {
			for(ItemStack stack : drops) {
				ItemHandlerHelper.insertItemStacked(outputItemStacks, stack, false);
			}

			this.setGrowTicks(0);
			this.modelRotation = this.getLevel().random.nextInt(4);
			this.setChanged();
			this.notifyClients();
			return true;
		} else {
			return false;
		}

	}

	public boolean isGrowing() {
		return hasSapling() && this.growTicks < this.requiredTicks;
	}

	public double getProgress() {
		if(!hasSapling() || !hasSoil() || this.requiredTicks == 0) {
			return 0;
		}

		return (double) this.growTicks / (double) this.requiredTicks;
	}

	public double getProgress(float partialTicks) {
		if(!hasSapling() || !hasSoil() || this.requiredTicks == 0) {
			return 0;
		}

		double result = ((double) this.growTicks + partialTicks) / (double) this.requiredTicks;
		if(result >= 0.999) {
			result = 1.0d;
		}
		return result;
	}

	public void updateGrowth() {
		if(!hasSapling() || !hasSoil()) {
			this.setGrowTicks(0);
			return;
		}

		if(SoilCompatibility.INSTANCE.isReady && !SoilCompatibility.INSTANCE.canTreeGrowOnSoil(this.saplingInfo, this.soilInfo)) {
			this.setGrowTicks(0);
			return;
		}

		if(getLevel() != null && getProgress() >= 1.0f && autoCut) {
			var success = this.cutTree();
			if(!success) {
				this.setGrowTicks(0);
			} else {
				if(!this.level.isClientSide()) {
					this.hopOutput();
				}
			}
		}

		if(getProgress() < 1.0f) {
			this.setGrowTicks(growTicks + 1);
		}
	}

	public void setGrowTicks(int growTicks) {
		this.growTicks = growTicks;
		this.setChanged();
	}

	public void boostProgress() {
		if(!isGrowing()) {
			return;
		}

		this.growTicks += this.requiredTicks / 4;
		if(this.growTicks >= this.requiredTicks) {
			this.growTicks = this.requiredTicks;
		}

		notifyClients();
	}


	@Override
	public void onDataLoaded() {
		if(this.level != null) {
			this.updateInfoObjects();
			if(this.level instanceof ClientLevel) {
				ModelDataManager.requestModelDataRefresh(this);
			}

			level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), UPDATE_ALL);
		}
	}

	public ItemStackHandler getUpgradeItemStacks() {
		return upgradeItemStacks;
	}

	public static boolean isUpgradeItem(ItemStack stack) {
		if(stack.is(Blocks.HOPPER.asItem())) {
			return true;
		}

		if(stack.getItem().canPerformAction(stack, ToolActions.AXE_DIG)) {
			return true;
		}

		if(stack.getItem() instanceof EnchantedBookItem) {
			var enchantments = new EnchantmentHelper(stack);
			if(enchantments.hasAny(Enchantments.SILK_TOUCH, Enchantments.BLOCK_FORTUNE, Enchantments.BLOCK_EFFICIENCY)) {
				return true;
			}
		}

		return false;
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if(side == null) {
				return combinedItemStackHandler.cast();
			} else if(side == Direction.UP) {
				return saplingItemStackHandler.cast();
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
				var soilInfo = Registration.RECIPE_HELPER_SOIL.getSoilForItem(level, stack);
				return soilInfo != null;
			}

			@Override
			public int getSlotLimit(int slot) {
				return 1;
			}

			@Override
			protected void onContentsChanged(int slot) {
				setChanged();
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
				var saplingInfo = Registration.RECIPE_HELPER_SAPLING.getSaplingInfoForItem(level, stack);
				return saplingInfo != null;
			}

			@Override
			public int getSlotLimit(int slot) {
				return 1;
			}

			@Override
			protected void onContentsChanged(int slot) {
				setChanged();
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