package com.davenonymous.bonsaitrees3.blocks;

import com.davenonymous.libnonymous.compat.top.ITopInfoProvider;
import com.davenonymous.bonsaitrees3.config.CommonConfig;
import com.davenonymous.libnonymous.base.BaseBlock;
import com.davenonymous.libnonymous.base.BaseLanguageProvider;
import com.davenonymous.libnonymous.registration.CustomBlockStateProperties;
import com.davenonymous.bonsaitrees3.setup.Registration;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.Tags;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.Random;

public class BonsaiPotBlock extends BaseBlock implements BonemealableBlock, ITopInfoProvider {
	private final VoxelShape shape = Shapes.box(0.065f, 0.005f, 0.065f, 0.935f, 0.185f, 0.935f);

	public BonsaiPotBlock() {
		super(Properties.of(Material.STONE).sound(SoundType.GRASS).strength(2.0f).requiresCorrectToolForDrops());

		this.registerDefaultState(this.getStateDefinition().any().setValue(CustomBlockStateProperties.COLOR, DyeColor.LIGHT_GRAY.getId()));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(CustomBlockStateProperties.COLOR);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		var state = super.getStateForPlacement(context);

		var offHandItemStack = context.getPlayer().getItemInHand(InteractionHand.OFF_HAND);
		if(Tags.Items.DYES.contains(offHandItemStack.getItem())) {
			var color = DyeColor.getColor(offHandItemStack);
			if(color != null) {
				state = state.setValue(CustomBlockStateProperties.COLOR, color.getId());
			}
		}

		return state;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
		var blockChanged = !pState.is(pNewState.getBlock());
		var tile = (BonsaiPotBlockEntity) pLevel.getBlockEntity(pPos);
		if(!blockChanged || tile == null || pLevel.isClientSide()) {
			super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
			return;
		}

		tile.dropItemStackContents();

		super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
	}

	@SuppressWarnings("deprecation")
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
		if(level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		if(player.isCrouching()) {
			return InteractionResult.PASS;
		}

		var playerStack = player.getItemInHand(hand);

		var pot = (BonsaiPotBlockEntity) level.getBlockEntity(pos);

		var soilInfo = Registration.RECIPE_HELPER_SOIL.getSoilForItem(level, playerStack);
		if(soilInfo != null && !pot.hasSoil()) {
			if(player.isCreative()) {
				ItemStack soilStack = playerStack.copy();
				soilStack.setCount(1);
				pot.setSoil(soilStack);
			} else {
				pot.setSoil(playerStack.split(1));
			}
			pot.setChanged();
			return InteractionResult.SUCCESS;
		}

		var saplingInfo = Registration.RECIPE_HELPER_SAPLING.getSaplingInfoForItem(level, playerStack);
		if(saplingInfo != null && !pot.hasSapling()) {
			if(player.isCreative()) {
				ItemStack saplingStack = playerStack.copy();
				saplingStack.setCount(1);
				pot.setSapling(saplingStack);
			} else {
				pot.setSapling(playerStack.split(1));
			}
			pot.setChanged();
			return InteractionResult.SUCCESS;
		}


		if(Tags.Items.DYES.contains(playerStack.getItem())) {
			var color = DyeColor.getColor(playerStack);
			if(color != null) {
				level.setBlock(pos, state.setValue(CustomBlockStateProperties.COLOR, color.getId()), UPDATE_CLIENTS);
				return InteractionResult.SUCCESS;
			}
		}

		if(BonsaiPotBlockEntity.isUpgradeItem(playerStack)) {
			var newStack = ItemHandlerHelper.insertItem(pot.getUpgradeItemStacks(), playerStack, true);
			if(!newStack.is(playerStack.getItem()) || newStack.getCount() != playerStack.getCount()) {
				player.setItemInHand(hand, ItemHandlerHelper.insertItem(pot.getUpgradeItemStacks(), playerStack, false));
				return InteractionResult.SUCCESS;
			}
		}

		MenuProvider menuProvider = new MenuProvider() {
			@Override
			public Component getDisplayName() {
				return new TranslatableComponent(BaseLanguageProvider.getContainerLanguageKey(Registration.BONSAI_POT_CONTAINER.get()));
			}

			@Nullable
			@Override
			public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
				return new BonsaiPotContainer(pContainerId, pos, pInventory, pPlayer);
			}
		};
		NetworkHooks.openGui((ServerPlayer) player, menuProvider, pos);
		return InteractionResult.SUCCESS;
	}


	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new BonsaiPotBlockEntity(pos, state);
	}

	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getOcclusionShape(BlockState p_60578_, BlockGetter p_60579_, BlockPos p_60580_) {
		return shape;
	}

	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getCollisionShape(BlockState p_60572_, BlockGetter p_60573_, BlockPos p_60574_, CollisionContext p_60575_) {
		return shape;
	}

	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return shape;
	}

	@Override
	public boolean isValidBonemealTarget(BlockGetter pLevel, BlockPos pPos, BlockState pState, boolean pIsClient) {
		if(!CommonConfig.allowBonemeal.get()) {
			return false;
		}

		var pot = (BonsaiPotBlockEntity) pLevel.getBlockEntity(pPos);
		if(pot == null) {
			return false;
		}

		if(!pot.hasSoil() || !pot.hasSapling()) {
			return false;
		}

		if(!pot.isGrowing()) {
			return false;
		}

		return true;
	}

	@Override
	public boolean isBonemealSuccess(Level pLevel, Random pRandom, BlockPos pPos, BlockState pState) {
		return pRandom.nextDouble() < CommonConfig.bonemealSuccessChance.get();
	}

	@Override
	public void performBonemeal(ServerLevel pLevel, Random pRandom, BlockPos pPos, BlockState pState) {
		var pot = (BonsaiPotBlockEntity) pLevel.getBlockEntity(pPos);
		if(pot == null) {
			return;
		}

		pot.boostProgress();
	}

	@Override
	public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, Player player, Level level, BlockState blockState, IProbeHitData iProbeHitData) {
		if(!(level.getBlockEntity(iProbeHitData.getPos()) instanceof BonsaiPotBlockEntity)) {
			return;
		}

		BonsaiPotBlockEntity pot = (BonsaiPotBlockEntity) level.getBlockEntity(iProbeHitData.getPos());
		if(pot.hasSapling()) {
			iProbeInfo.horizontal().item(pot.getSapling()).itemLabel(pot.getSapling());
		}

		if(pot.hasSoil()) {
			var soilInfo = pot.getSoilInfo();
			if(soilInfo.isFluid) {
				Item bucketItem = soilInfo.fluidState.getType().getBucket();
				ItemStack bucketStack = new ItemStack(bucketItem);
				iProbeInfo.horizontal().item(bucketStack).itemLabel(bucketStack);
			} else {
				var soilStack = new ItemStack(pot.getSoilBlock().getBlock());
				iProbeInfo.horizontal().item(soilStack).itemLabel(soilStack);
			}
		}

		if(pot.hasSapling()) {
			iProbeInfo.progress((int) (pot.getProgress() * 100), 100, iProbeInfo.defaultProgressStyle().suffix("%").filledColor(0xff44AA44).alternateFilledColor(0xff44AA44).backgroundColor(0xff836953));
		}
	}
}