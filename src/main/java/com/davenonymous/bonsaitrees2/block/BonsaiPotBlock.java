package com.davenonymous.bonsaitrees2.block;

import com.davenonymous.bonsaitrees2.api.IBonsaiCuttingTool;
import com.davenonymous.bonsaitrees2.compat.top.ITopInfoProvider;
import com.davenonymous.bonsaitrees2.config.Config;
import com.davenonymous.bonsaitrees2.misc.PotColorizer;
import com.davenonymous.bonsaitrees2.registry.SoilCompatibility;
import com.davenonymous.bonsaitrees2.registry.sapling.SaplingInfo;
import com.davenonymous.bonsaitrees2.registry.soil.SoilInfo;
import com.davenonymous.bonsaitrees2.registry.soil.SoilRecipeHelper;
import com.davenonymous.bonsaitrees2.util.Logz;
import com.davenonymous.libnonymous.base.BaseBlock;
import com.davenonymous.libnonymous.misc.ColorProperty;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.Random;

public class BonsaiPotBlock extends BaseBlock implements IGrowable, IWaterLoggable, ITopInfoProvider {
    private final Random rand = new Random();
    private final VoxelShape shape = VoxelShapes.create(0.065f, 0.005f, 0.065f, 0.935f, 0.185f, 0.935f);
    boolean hopping;

    public BonsaiPotBlock(boolean hopping) {
        super(Properties.create(Material.CLAY, MaterialColor.CLAY)
                .hardnessAndResistance(2.0F)
                .sound(SoundType.WOOD)
                .harvestTool(ToolType.AXE)
                .harvestLevel(0)
                .notSolid()
        );

        this.setDefaultState(this.stateContainer.getBaseState().with(ColorProperty.COLOR, 8).with(BlockStateProperties.WATERLOGGED, Boolean.FALSE));
        this.hopping = hopping;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        if(this.hopping) {
            return new HoppingBonsaiPotTileEntity();
        } else {
            return new BonsaiPotTileEntity();
        }
    }

    public static BonsaiPotTileEntity getOwnTile(IBlockReader world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if(!(te instanceof BonsaiPotTileEntity)) {
            return null;
        }

        return (BonsaiPotTileEntity)te;
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        BonsaiPotTileEntity tile = getOwnTile(worldIn, pos);
        if(tile == null) {
            super.onReplaced(state, worldIn, pos, newState, isMoving);
            return;
        }

        if(tile.hasSapling()) {
            InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), tile.getSaplingStack());
        }

        if(tile.hasSoil()) {
            InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), tile.getSoilStack());
        }

        if(tile.hasSoil() && tile.hasSapling() && tile.getProgress() >= 1.0f) {
            tile.dropLoot();
        }

        super.onReplaced(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (player.isCrouching()) {
            return ActionResultType.FAIL;
        }

        if (!(world.getTileEntity(pos) instanceof BonsaiPotTileEntity)) {
            return ActionResultType.FAIL;
        }

        ItemStack playerStack = player.getHeldItem(Hand.MAIN_HAND);
        if(playerStack.isEmpty()) {
            playerStack = player.getHeldItem(Hand.OFF_HAND);
        }

        // No items in either of the hands -> no action here
        if(playerStack.isEmpty()) {
            return ActionResultType.FAIL;
        }

        if (world.isRemote) {
            return ActionResultType.SUCCESS;
        }

        BonsaiPotTileEntity pot = (BonsaiPotTileEntity)world.getTileEntity(pos);

        // Soil?
        SoilInfo soil = ModObjects.soilRecipeHelper.getSoilForItem(world, playerStack);
        if(soil != null && !pot.hasSoil()) {
            if(player.isCreative()) {
                ItemStack soilStack = playerStack.copy();
                soilStack.setCount(1);
                pot.setSoil(soilStack);
            } else {
                pot.setSoil(playerStack.split(1));
            }
            return ActionResultType.SUCCESS;
        }

        // Sapling?
        SaplingInfo sapling = ModObjects.saplingRecipeHelper.getSaplingInfoForItem(world, playerStack);
        if(sapling != null && !pot.hasSapling()) {
            if(!pot.hasSoil()) {
                SoilInfo randomSoil = ModObjects.soilRecipeHelper.getRandomRecipe(world.getRecipeManager(), world.rand);
                if(randomSoil != null) {
                    player.sendStatusMessage(new TranslationTextComponent("hint.bonsaitrees.pot_has_no_soil", randomSoil.ingredient.getMatchingStacks()[0].getDisplayName()), true);
                } else {
                    Logz.warn("There is no soil available. Please check the config and logs for errors!");
                }

                return ActionResultType.SUCCESS;
            }

            SoilInfo potSoil = ModObjects.soilRecipeHelper.getSoilForItem(world, pot.getSoilStack());
            if(!SoilCompatibility.INSTANCE.canTreeGrowOnSoil(sapling, potSoil)) {
                player.sendStatusMessage(new TranslationTextComponent("hint.bonsaitrees.incompatible_soil"), true);
                return ActionResultType.SUCCESS;
            }

            if(player.isCreative()) {
                ItemStack saplingStack = playerStack.copy();
                saplingStack.setCount(1);
                pot.setSapling(saplingStack);
            } else {
                pot.setSapling(playerStack.split(1));
            }
            return ActionResultType.SUCCESS;
        }

        // Dye?
        DyeColor blockColor = DyeColor.byId(state.get(ColorProperty.COLOR));
        if(Tags.Items.DYES.contains(playerStack.getItem())) {
            DyeColor color = DyeColor.getColor(playerStack);
            if(color != null) {
                if(blockColor == color) {
                    return ActionResultType.SUCCESS;
                }

                if(!player.isCreative() && !Config.NO_DYE_COST.get()) {
                    playerStack.split(1);
                }

                world.setBlockState(pos, state.with(ColorProperty.COLOR, color.getId()), 2);
                return ActionResultType.SUCCESS;
            }
        }

        boolean playerHasAxe = canCutBonsaiTree(playerStack, player);
        if(playerHasAxe) {
            // No sapling in pot
            if(!pot.hasSapling()) {
                return ActionResultType.FAIL;
            }

            boolean inWorkingCondition = !playerStack.isDamageable() || playerStack.getDamage() + 1 < playerStack.getMaxDamage();
            if(pot.getProgress() >= 1.0f && inWorkingCondition) {
                pot.dropLoot();
                pot.setSapling(pot.saplingStack);
                playerStack.attemptDamageItem(1, rand, (ServerPlayerEntity) player);
                return ActionResultType.SUCCESS;
            } else if(pot.growTicks >= 20 && pot.getProgress() <= 0.75f) {
                // Not ready and still under 75%
                pot.dropSapling();
                return ActionResultType.SUCCESS;
            }

            return ActionResultType.SUCCESS;
        }

        boolean playerHasShovel = playerStack.getItem().getHarvestLevel(playerStack, ToolType.SHOVEL, player, Blocks.DIRT.getDefaultState()) != -1;
        if(playerHasShovel) {
            if(pot.hasSapling()) {
                player.sendStatusMessage(new TranslationTextComponent("hint.bonsaitrees.can_not_remove_soil_with_sapling"), true);
                return ActionResultType.FAIL;
            }

            if(!pot.hasSoil()) {
                return ActionResultType.FAIL;
            }

            pot.dropSoil();
            return ActionResultType.SUCCESS;
        }

        return super.onBlockActivated(state, world, pos, player, handIn, hit);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, entity, stack);

        CompoundNBT tag = stack.getTag();
        if(tag == null || !tag.contains("bonsaitrees2:color")) {
            world.setBlockState(pos, state.with(ColorProperty.COLOR, PotColorizer.DEFAULT_COLOR.getId()), 2);
            return;
        }

        int color = tag.getInt("bonsaitrees2:color");
        world.setBlockState(pos, state.with(ColorProperty.COLOR, color), 2);
        return;
    }

    private boolean canCutBonsaiTree(ItemStack stack, PlayerEntity player) {
        if(stack.getItem().getHarvestLevel(stack, ToolType.AXE, player, Blocks.OAK_PLANKS.getDefaultState()) != -1) {
            return true;
        }

        if(stack.getItem() instanceof IBonsaiCuttingTool) {
            return true;
        }

        String regName = stack.getItem().getRegistryName().toString();
        if(Config.ADDITIONAL_CUTTING_TOOLS.get().contains(regName)) {
            return true;
        }

        return false;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(ColorProperty.COLOR);
        builder.add(BlockStateProperties.WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        IFluidState fluidState = context.getWorld().getFluidState(context.getPos());
        return super.getStateForPlacement(context).with(BlockStateProperties.WATERLOGGED, Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.get(BlockStateProperties.WATERLOGGED)) {
            worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
        }

        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
        return !state.get(BlockStateProperties.WATERLOGGED);
    }


    @Override
    public IFluidState getFluidState(BlockState state) {
        return state.get(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    @Override
    public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        return shape;
    }


    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }




    @Override
    public boolean canGrow(IBlockReader world, BlockPos pos, BlockState state, boolean isClient) {
        if(!(world.getTileEntity(pos) instanceof BonsaiPotTileEntity)) {
            return false;
        }

        BonsaiPotTileEntity tile = (BonsaiPotTileEntity) world.getTileEntity(pos);
        return tile.isGrowing();
    }

    @Override
    public boolean canUseBonemeal(World world, Random rand, BlockPos pos, BlockState state) {
        if(!(world.getTileEntity(pos) instanceof BonsaiPotTileEntity)) {
            return false;
        }

        BonsaiPotTileEntity tile = (BonsaiPotTileEntity) world.getTileEntity(pos);
        if(!tile.isGrowing()) {
            return false;
        }

        return (double)world.rand.nextFloat() < 0.45D;
    }

    @Override
    public void grow(ServerWorld world, Random rand, BlockPos pos, BlockState state) {
        if(!(world.getTileEntity(pos) instanceof BonsaiPotTileEntity)) {
            return;
        }

        BonsaiPotTileEntity tile = (BonsaiPotTileEntity) world.getTileEntity(pos);
        tile.boostProgress();
    }


    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
        if(!(world.getTileEntity(data.getPos()) instanceof BonsaiPotTileEntity)) {
            return;
        }

        BonsaiPotTileEntity teBonsai = (BonsaiPotTileEntity) world.getTileEntity(data.getPos());
        if(teBonsai.hasSapling()) {
            probeInfo.horizontal().item(teBonsai.saplingStack).itemLabel(teBonsai.saplingStack);
        }

        if(teBonsai.hasSoil()) {
            probeInfo.horizontal().item(teBonsai.soilStack).itemLabel(teBonsai.soilStack);
        }

        if(teBonsai.hasSapling()) {
            probeInfo.progress((int)(teBonsai.getProgress()*100), 100, probeInfo.defaultProgressStyle().suffix("%").filledColor(0xff44AA44).alternateFilledColor(0xff44AA44).backgroundColor(0xff836953));
        }
    }
}
