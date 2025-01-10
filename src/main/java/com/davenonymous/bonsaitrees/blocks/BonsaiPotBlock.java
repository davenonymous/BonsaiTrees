package com.davenonymous.bonsaitrees.blocks;

import com.davenonymous.bonsaitrees.setup.ModBlocks;
import com.davenonymous.bonsaitrees.setup.ModDataComponents;
import com.davenonymous.bonsaitrees.setup.data.BonsaiInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.*;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.Nullable;

public class BonsaiPotBlock extends Block implements EntityBlock, Equipable {
	private final VoxelShape SHAPE = Shapes.box(0.065f, 0.005f, 0.065f, 0.935f, 0.185f, 0.935f);

	public static final ModelProperty<ResourceLocation> SAPLING = new ModelProperty<>();
	public static final ModelProperty<Block> SOIL = new ModelProperty<>();
	public static final ModelProperty<Fluid> FLUID_SOIL = new ModelProperty<>();
	public static final ModelProperty<Item> ITEM_SOIL = new ModelProperty<>();
	public static final ModelProperty<Block> CAMOUFLAGE = new ModelProperty<>();

	public BonsaiPotBlock(Properties properties) {
		super(properties
			.sound(SoundType.DECORATED_POT)
			.strength(2.0f)
			.requiresCorrectToolForDrops()
		);
	}

	@Override
	public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		if(blockEntityType == ModBlocks.BONSAI_POT_ENTITY.get()) {
			return (level1, blockPos, blockState, blockEntity) -> {
				if(blockEntity instanceof BonsaiPotBlockEntity bonsaiPot) {
					bonsaiPot.tick();
				}
			};
		}
		return null;
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
		if(level.getBlockEntity(pos) instanceof BonsaiPotBlockEntity bonsaiPot) {
			ItemStack clone = new ItemStack(this, 1);
			DataComponentMap components = bonsaiPot.collectComponents();
			if(components.has(ModDataComponents.CAMOUFLAGE_COMPONENT.get())) {
				clone.set(ModDataComponents.CAMOUFLAGE_COMPONENT.get(), components.get(ModDataComponents.CAMOUFLAGE_COMPONENT.get()));
			}
			if(components.has(ModDataComponents.SOIL_COMPONENT.get())) {
				clone.set(ModDataComponents.SOIL_COMPONENT.get(), components.get(ModDataComponents.SOIL_COMPONENT.get()));
			}
			if(components.has(ModDataComponents.SAPLING_COMPONENT.get())) {
				clone.set(ModDataComponents.SAPLING_COMPONENT.get(), components.get(ModDataComponents.SAPLING_COMPONENT.get()));
			}
			if(components.has(ModDataComponents.TOOL_COMPONENT.get())) {
				clone.set(ModDataComponents.TOOL_COMPONENT.get(), components.get(ModDataComponents.TOOL_COMPONENT.get()));
			}
			if(components.has(DataComponents.ENCHANTMENTS)) {
				clone.set(DataComponents.ENCHANTMENTS, components.get(DataComponents.ENCHANTMENTS));
			}
			if(components.has(ModDataComponents.REDSTONEMODE_COMPONENT.get())) {
				clone.set(ModDataComponents.REDSTONEMODE_COMPONENT.get(), components.get(ModDataComponents.REDSTONEMODE_COMPONENT.get()));
			}

			return clone;
		}
		return super.getCloneItemStack(state, target, level, pos, player);
	}

	@Override
	protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
		return getLightEmission(state, level, pos);
	}

	@Override
	public boolean hasDynamicLightEmission(BlockState state) {
		return true;
	}

	@Override
	public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
		if(level.getBlockEntity(pos) instanceof BonsaiPotBlockEntity bonsaiPot) {
			if(bonsaiPot.production.getBonsaiInfo().isPresent()) {
				BonsaiInfo info = bonsaiPot.production.getBonsaiInfo().get();
				if(info.lightEmission().isPresent()) {
					return info.lightEmission().get();
				}
			}
		}
		return 0;
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if(level.isClientSide()) {
			return ItemInteractionResult.SUCCESS;
		}

		if(level.getBlockEntity(pos) instanceof BonsaiPotBlockEntity bonsaiPot) {
			ItemStack stackBeforeClick = stack.copy();
			ItemStack stackAfterClick = bonsaiPot.onItemClicked(stack, player, hand);
			if(ItemStack.matches(stackBeforeClick, stackAfterClick)) {
				player.openMenu(state.getMenuProvider(level, pos), pos);
			}

			return ItemInteractionResult.SUCCESS;
		}

		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if(level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		if(level.getBlockEntity(pos) instanceof BonsaiPotBlockEntity bonsaiPot) {
			player.openMenu(state.getMenuProvider(level, pos), pos);
		}

		return super.useWithoutItem(state, level, pos, player, hitResult);
	}

	@Override
	protected @Nullable MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
		return new SimpleMenuProvider(
			(id, inventory, player) -> new BonsaiPotContainer(id, pos, inventory, player),
			Component.translatable("container.bonsaitrees4.bonsai_pot")
		);
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
		return new BonsaiPotBlockEntity(blockPos, blockState);
	}

	@Override
	protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
		return SHAPE;
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public EquipmentSlot getEquipmentSlot() {
		return EquipmentSlot.HEAD;
	}
}
