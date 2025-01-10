package com.davenonymous.bonsaitrees.blocks;

import com.davenonymous.bonsaitrees.datacomponents.RedstoneModeDataComponent;
import com.davenonymous.bonsaitrees.lib.BaseBlockEntity;
import com.davenonymous.bonsaitrees.lib.gui.RedstoneMode;
import com.davenonymous.bonsaitrees.setup.ModBlocks;
import com.davenonymous.bonsaitrees.setup.ModDataComponents;
import com.davenonymous.bonsaitrees.setup.cache.BonsaiCache;
import com.davenonymous.bonsaitrees.setup.cache.SoilCache;
import com.davenonymous.bonsaitrees.setup.data.BonsaiInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

public class BonsaiPotBlockEntity extends BaseBlockEntity {
	public BonsaiPotBlockInventories inventories;
	public BonsaiPotBlockProduction production;
	private boolean firstTick = true;
	private RedstoneMode redstoneMode = RedstoneMode.STOP_ON_POWER;

	public BonsaiPotBlockEntity(BlockPos pos, BlockState blockState) {
		super(ModBlocks.BONSAI_POT_ENTITY.get(), pos, blockState);
		inventories = new BonsaiPotBlockInventories(this, this::onInventoryChange);
		production = new BonsaiPotBlockProduction(this);
	}

	private ItemStack tryInsert(ItemStack stack, ItemStackHandler handler) {
		if(stack.isEmpty()) {
			return stack;
		}
		if(handler.getStackInSlot(0).isEmpty() && handler.isItemValid(0, stack)) {
			ItemStack toInsert = stack.split(1);
			handler.setStackInSlot(0, toInsert);
			setChanged();
			notifyClients(false);
		}

		return stack;
	}

	public ItemStack onItemClicked(ItemStack stack, Player player, InteractionHand hand) {
		tryInsert(stack, inventories.soilInventory);
		tryInsert(stack, inventories.saplingInventory);
		return stack;
	}

	private void onInventoryChange(ItemStackHandler handler, int slot) {
		production.onInventoryChange(handler, slot);

		if(handler == inventories.saplingInventory) {
			level.getLightEngine().checkBlock(getBlockPos());
		}

		setChanged();
		if(handler != inventories.outputInventory) {
			requestModelDataUpdate();
		}
		notifyClients(false);
	}

	public void tick() {
		if(firstTick) {
			production.init();
			level.getLightEngine().checkBlock(getBlockPos());
			firstTick = false;
		}

		production.tick();
	}

	public float getTreeGrowthProgress(float partialTicks) {
		return production.getTreeGrowthProgress(partialTicks);
	}

	public BonsaiPotBlockEntity setRedstoneMode(RedstoneMode redstoneMode) {
		this.redstoneMode = redstoneMode;
		setChanged();
		notifyClients(false);
		return this;
	}

	public RedstoneMode getRedstoneMode() {
		return redstoneMode;
	}

	@Override
	protected void applyImplicitComponents(DataComponentInput componentInput) {
		super.applyImplicitComponents(componentInput);
		inventories.applyImplicitComponents(componentInput);

		var redstoneModeComponent = componentInput.get(ModDataComponents.REDSTONEMODE_COMPONENT);
		if(redstoneModeComponent != null) {
			this.redstoneMode = redstoneModeComponent.mode();
		}
	}

	@Override
	protected void collectImplicitComponents(DataComponentMap.Builder components) {
		super.collectImplicitComponents(components);
		inventories.collectImplicitComponents(components);

		components.set(ModDataComponents.REDSTONEMODE_COMPONENT, new RedstoneModeDataComponent(this.redstoneMode));
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
		super.onDataPacket(net, pkt, lookupProvider);

		if(level == null) {
			return;
		}

		if(level.isClientSide()) {
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
			requestModelDataUpdate();
			if(Minecraft.getInstance().screen instanceof BonsaiPotScreen potScreen) {
				potScreen.fireDataUpdateEvent();
			}
			production.onInventoryChange(inventories.saplingInventory, 0);
			production.onInventoryChange(inventories.soilInventory, 0);
		}
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		inventories.deserializeNBT(registries, tag.getCompound("inv"));
		production.deserializeNBT(registries, tag.getCompound("production"));
		redstoneMode = RedstoneMode.byId(tag.getInt("redstoneMode"));
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.put("inv", inventories.serializeNBT(registries));
		tag.put("production", production.serializeNBT(registries));
		tag.putInt("redstoneMode", redstoneMode.getId());
	}

	public static IItemHandler getCapability(Level level, BlockPos pos, BlockState state, BlockEntity entity, Direction side) {
		if(entity instanceof BonsaiPotBlockEntity bonsaiPot) {
			if(side == Direction.DOWN) {
				return bonsaiPot.inventories.outputInventory;
			}

			return bonsaiPot.inventories.accessibleInventories;
		}

		return null;
	}

	@Override
	public ModelData getModelData() {
		ModelData.Builder modelData = ModelData.builder();

		ItemStack saplingStack = inventories.getSaplingStack();
		if(!saplingStack.isEmpty() && BonsaiCache.BONSAI_BY_ITEM.containsKey(saplingStack.getItem())) {
			BonsaiInfo info = BonsaiCache.BONSAI_BY_ITEM.get(saplingStack.getItem());
			modelData.with(BonsaiPotBlock.SAPLING, info.model());
		}

		ItemStack soilStack = inventories.getSoilStack();
		if(!soilStack.isEmpty() && soilStack.getItem() instanceof BlockItem item) {
			modelData.with(BonsaiPotBlock.SOIL, item.getBlock());
		}

		if(!soilStack.isEmpty() && soilStack.getItem() instanceof BucketItem bucket) {
			modelData.with(BonsaiPotBlock.FLUID_SOIL, bucket.content);
		}

		if(!soilStack.isEmpty() && SoilCache.SOIL_BY_ITEM.containsKey(soilStack.getItem())) {
			modelData.with(BonsaiPotBlock.ITEM_SOIL, soilStack.getItem());
		}

		ItemStack camouflageStack = inventories.getCamouflageStack();
		if(!camouflageStack.isEmpty() && camouflageStack.getItem() instanceof BlockItem item) {
			modelData.with(BonsaiPotBlock.CAMOUFLAGE, item.getBlock());
		}

		return modelData.build();
	}


}
