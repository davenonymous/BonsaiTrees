package org.dave.bonsaitrees.block;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.dave.bonsaitrees.BonsaiTrees;
import org.dave.bonsaitrees.base.BaseBlockWithTile;
import org.dave.bonsaitrees.base.IMetaBlockName;
import org.dave.bonsaitrees.compat.ITopInfoProvider;
import org.dave.bonsaitrees.render.TESRBonsaiPot;
import org.dave.bonsaitrees.tile.TileBonsaiPot;
import org.dave.bonsaitrees.trees.TreeTypeRegistry;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockBonsaiPot extends BaseBlockWithTile<TileBonsaiPot> implements IGrowable, IMetaBlockName, ITopInfoProvider {
    private static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0f, 0f, 0f, 1.0f, 0.22f, 1.0f);
    public static final PropertyBool IS_HOPPING = PropertyBool.create("hopping");

    public BlockBonsaiPot(Material material) {
        super(material);

        this.setHardness(2.0F);
        this.setSoundType(SoundType.WOOD);
        this.setCreativeTab(CreativeTabs.DECORATIONS);

        this.setDefaultState(blockState.getBaseState().withProperty(IS_HOPPING, false));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BOUNDING_BOX;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return BOUNDING_BOX;
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(Item.getItemFromBlock(this), 1, this.getMetaFromState(state));
    }

    @Override
    public String getSpecialName(ItemStack stack) {
        return this.getStateFromMeta(stack.getItemDamage()).getValue(IS_HOPPING) ? "hopping" : "";
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        items.add(new ItemStack(this, 1, 0));
        items.add(new ItemStack(this, 1, 1));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, IS_HOPPING);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(IS_HOPPING) ? 1 : 0;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(IS_HOPPING, meta == 1 ? true : false);
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    public boolean canGrow(ItemStack stack) {
        if(stack == ItemStack.EMPTY) {
            return false;
        }

        if(BonsaiTrees.instance.typeRegistry.getTypeByStack(stack) != null) {
            return true;
        }

        return false;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (player.isSneaking()) {
            return false;
        }

        if (world.isRemote || !(player instanceof EntityPlayerMP)) {
            return false;
        }

        if (!(world.getTileEntity(pos) instanceof TileBonsaiPot)) {
            return false;
        }

        // No items in hand -> no action here
        ItemStack playerStack = player.getHeldItemMainhand();
        if(playerStack.isEmpty()) {
            return false;
        }

        TileBonsaiPot pot = (TileBonsaiPot) world.getTileEntity(pos);

        // Plant sapling?
        if(!pot.hasSapling()) {
            if(canGrow(playerStack)) {
                pot.setSapling(playerStack.splitStack(1));
            }
            return true;
        }

        // Harvest sapling?
        boolean playerHasAxe = playerStack.getItem().getHarvestLevel(playerStack, "axe", player, Blocks.PLANKS.getDefaultState()) != -1;
        if(!playerHasAxe) {
            return false;
        }

        if(pot.getProgress() < pot.getTreeType().getGrowTime()) {
            return true;
        }

        int droppedItems = pot.dropLoot();
        pot.setSapling(pot.getSapling());

        playerStack.damageItem(droppedItems, player);

        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileBonsaiPot();
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "hopping=false"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 1, new ModelResourceLocation(getRegistryName(), "hopping=true"));
        ClientRegistry.bindTileEntitySpecialRenderer(TileBonsaiPot.class, new TESRBonsaiPot());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public boolean isBlockNormalCube(IBlockState blockState) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState blockState) {
        return false;
    }

    @Override
    public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient) {
        if (!(world.getTileEntity(pos) instanceof TileBonsaiPot)) {
            return false;
        }

        TileBonsaiPot pot = (TileBonsaiPot) world.getTileEntity(pos);
        return pot.isGrowing();
    }

    @Override
    public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state) {
        if (!(world.getTileEntity(pos) instanceof TileBonsaiPot)) {
            return false;
        }

        TileBonsaiPot pot = (TileBonsaiPot) world.getTileEntity(pos);
        if(!pot.isGrowing()) {
            return false;
        }

        return (double)world.rand.nextFloat() < 0.45D;
    }

    @Override
    public void grow(World world, Random rand, BlockPos pos, IBlockState state) {
        if (!(world.getTileEntity(pos) instanceof TileBonsaiPot)) {
            return;
        }

        TileBonsaiPot pot = (TileBonsaiPot) world.getTileEntity(pos);
        pot.boostProgress();
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        if(!(world.getTileEntity(data.getPos()) instanceof TileBonsaiPot)) {
            return;
        }

        TileBonsaiPot teBonsai = (TileBonsaiPot) world.getTileEntity(data.getPos());
        if(teBonsai.hasSapling()) {
            probeInfo.horizontal().item(teBonsai.getSapling()).itemLabel(teBonsai.getSapling());
            probeInfo.progress((int)(teBonsai.getProgressPercent()*100), 100, probeInfo.defaultProgressStyle().suffix("%").filledColor(0xff44AA44).alternateFilledColor(0xff44AA44).backgroundColor(0xff836953));

        }
    }
}