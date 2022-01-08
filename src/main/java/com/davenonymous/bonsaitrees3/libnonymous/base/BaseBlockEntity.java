package com.davenonymous.bonsaitrees3.libnonymous.base;


import com.davenonymous.bonsaitrees3.libnonymous.serialization.nbt.NBTFieldSerializationData;
import com.davenonymous.bonsaitrees3.libnonymous.serialization.nbt.NBTFieldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.List;

import static net.minecraft.world.level.block.Block.UPDATE_ALL;
import static net.minecraft.world.level.block.Block.UPDATE_CLIENTS;

public class BaseBlockEntity<T extends BaseBlockEntity> extends BlockEntity {
	private boolean initialized = false;


	private List<NBTFieldSerializationData> NBTActions;

	public BaseBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
		super(tileEntityTypeIn, pos, state);

		this.NBTActions = NBTFieldUtils.initSerializableStoreFields(this.getClass());
	}

	public T getSelf(Level level, BlockPos pos) {
		return (T) level.getBlockEntity(pos);
	}

	public void loadFromItem(ItemStack stack) {
		if(!stack.hasTag()) {
			return;
		}

		NBTFieldUtils.readFieldsFromNBT(NBTActions, this, stack.getTag(), data -> data.storeWithItem);
		this.setChanged();
	}

	public void saveToItem(ItemStack stack) {
		CompoundTag compound = createItemStackTagCompound();
		stack.setTag(compound);
	}

	protected CompoundTag createItemStackTagCompound() {
		return NBTFieldUtils.writeFieldsToNBT(NBTActions, this, new CompoundTag(), data -> data.storeWithItem);
	}

	public void notifyClients() {
		this.notifyClients(true);
	}

	public void notifyClients(boolean all) {
		if(level == null) {
			return;
		}

		if(level.isClientSide()) {
			return;
		}

		level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), all ? UPDATE_ALL : UPDATE_CLIENTS);
	}

	@Nullable
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag() {
		return NBTFieldUtils.writeFieldsToNBT(NBTActions, this, super.getUpdateTag(), data -> data.sendInUpdatePackage);
	}

	@Override
	public void handleUpdateTag(CompoundTag tag) {
		if(tag == null) {
			return;
		}

		NBTFieldUtils.readFieldsFromNBT(NBTActions, this, tag, data -> true);
		onDataLoaded();
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		NBTFieldUtils.readFieldsFromNBT(NBTActions, this, pkt.getTag(), data -> data.sendInUpdatePackage);
		onDataLoaded();
	}


	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		NBTFieldUtils.readFieldsFromNBT(NBTActions, this, compound, data -> true);
		onDataLoaded();
	}

	@Override
	protected void saveAdditional(CompoundTag compound) {
		NBTFieldUtils.writeFieldsToNBT(NBTActions, this, compound, data -> true);
	}

	public void onDataLoaded() {
	}

	public void tickBoth() {
	}

	public void tickServer() {
		if(!this.initialized) {
			initialize();
			this.initialized = true;
		}

		tickBoth();
	}


	public void tickClient() {
		tickBoth();
	}

	protected void initialize() {
	}

	public IItemHandler getNeighborInventory(Direction side) {
		var belowBlockEntity = level.getBlockEntity(getBlockPos().relative(side));
		if(belowBlockEntity == null) {
			return null;
		}

		return belowBlockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite()).orElseGet(null);
	}

}
