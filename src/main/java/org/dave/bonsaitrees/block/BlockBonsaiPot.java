package org.dave.bonsaitrees.block;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.DyeUtils;
import org.dave.bonsaitrees.BonsaiTrees;
import org.dave.bonsaitrees.api.IBonsaiSoil;
import org.dave.bonsaitrees.api.IBonsaiTreeType;
import org.dave.bonsaitrees.base.BaseBlockWithTile;
import org.dave.bonsaitrees.base.IMetaBlockName;
import org.dave.bonsaitrees.compat.TheOneProbe.ITopInfoProvider;
import org.dave.bonsaitrees.init.Blockss;
import org.dave.bonsaitrees.misc.ConfigurationHandler;
import org.dave.bonsaitrees.render.TESRBonsaiPot;
import org.dave.bonsaitrees.tile.HoppingItemStackBufferHandler;
import org.dave.bonsaitrees.tile.TileBonsaiPot;
import org.dave.bonsaitrees.utility.Logz;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class BlockBonsaiPot extends BaseBlockWithTile<TileBonsaiPot> implements IGrowable, IMetaBlockName, ITopInfoProvider {
    private static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0.065f, 0.005f, 0.065f, 0.935f, 0.185f, 0.935f);
    public static final PropertyBool IS_HOPPING = PropertyBool.create("hopping");

    public BlockBonsaiPot(Material material) {
        super(material);

        this.setHardness(2.0F);
        this.setSoundType(SoundType.WOOD);
        this.setCreativeTab(CreativeTabs.DECORATIONS);
        this.setHarvestLevel("axe", 0);

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
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);

        if(!stack.hasTagCompound()) {
            return;
        }

        if(!(world.getTileEntity(pos) instanceof TileBonsaiPot)) {
            return;
        }

        TileBonsaiPot bonsaiPot = (TileBonsaiPot)world.getTileEntity(pos);
        NBTTagCompound potStack = stack.getTagCompound();
        if(potStack.hasKey("color")) {
            bonsaiPot.setColor(EnumDyeColor.byMetadata(potStack.getInteger("color")));
            bonsaiPot.markDirty();
            world.notifyBlockUpdate(pos, state, state, 11);
        }
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        super.onBlockHarvested(world, pos, state, player);

        if(world.isRemote) {
            return;
        }

        if(player.isCreative()) {
            return;
        }

        if (!(world.getTileEntity(pos) instanceof TileBonsaiPot)) {
            return;
        }

        List<ItemStack> drops = new ArrayList<>();
        TileBonsaiPot pot = (TileBonsaiPot) world.getTileEntity(pos);
        if(pot.hasSapling()) {
            if(pot.isHarvestable()) {
                drops.addAll(pot.getRandomizedDrops());
            }
            drops.add(pot.getSapling());
        }

        if(pot.hasSoil()) {
            drops.add(pot.getSoilStack());
        }

        ItemStack potStack = new ItemStack(Blockss.bonsaiPot, 1, getMetaFromState(state));
        if(pot.getColor() != null) {
            NBTTagCompound potNbt = new NBTTagCompound();
            potNbt.setInteger("color", pot.getColor().getMetadata());
            potStack.setTagCompound(potNbt);
        }

        drops.add(potStack);

        for(ItemStack drop: drops) {
            spawnAsEntity(world, pos, drop);
        }
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        ItemStack result = new ItemStack(Item.getItemFromBlock(this), 1, this.getMetaFromState(state));
        if (!(world.getTileEntity(pos) instanceof TileBonsaiPot)) {
            return result;
        }

        TileBonsaiPot pot = (TileBonsaiPot) world.getTileEntity(pos);
        if(pot.getColor() != null) {
            NBTTagCompound potNbt = new NBTTagCompound();
            potNbt.setInteger("color", pot.getColor().getMetadata());
            result.setTagCompound(potNbt);
        }

        return result;
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

        if (!(world.getTileEntity(pos) instanceof TileBonsaiPot)) {
            return false;
        }

        ItemStack playerStack = player.getHeldItem(EnumHand.MAIN_HAND);
        if(playerStack.isEmpty()) {
            playerStack = player.getHeldItem(EnumHand.OFF_HAND);
        }

        // No items in either of the hands -> no action here
        if(playerStack.isEmpty()) {
            return false;
        }

        if (world.isRemote || !(player instanceof EntityPlayerMP)) {
            return true;
        }

        TileBonsaiPot pot = (TileBonsaiPot) world.getTileEntity(pos);

        // Check for bonemeal first. Allow using bonemeal when clicked on the soil part of the block.
        // Otherwise, when clicked on the bonsai pot itself, tint the block accordingly.
        float epsilon = 0.01f;
        if(Math.abs(hitY - 0.19f) < epsilon && hitX >= 0.13 && hitX <= 0.87 && hitZ >= 0.13 && hitZ <= 0.87) {
            if(playerStack.getItem() == Items.DYE && playerStack.getMetadata() == 15) {
                return false;
            }
        }

        if(DyeUtils.isDye(playerStack)) {
            EnumDyeColor color = DyeUtils.colorFromStack(playerStack).orElse(EnumDyeColor.GRAY);
            if(color == pot.getColor()) {
                return true;
            }

            if(!world.isRemote && !player.isCreative() && !ConfigurationHandler.GeneralSettings.noDyeCost) {
                playerStack.splitStack(1);
            }

            pot.setColor(color);
            return true;
        }

        boolean playerHasSoil = BonsaiTrees.instance.soilRegistry.isValidSoil(playerStack);
        if(playerHasSoil) {
            if(pot.hasSoil()) {
                return false;
            }

            if(pot.hasSapling()) {
                return false;
            }

            if(player.isCreative()) {
                ItemStack soilStack = playerStack.copy();
                soilStack.setCount(1);
                pot.setSoil(soilStack);
            } else {
                pot.setSoil(playerStack.splitStack(1));
            }
            return true;
        }

        boolean playerHasAxe = playerStack.getItem().getHarvestLevel(playerStack, "axe", player, Blocks.PLANKS.getDefaultState()) != -1;
        if(playerHasAxe) {
            // No sapling in pot
            if(!pot.hasSapling()) {
                return false;
            }


            if(pot.isHarvestable() && playerStack.getItemDamage() + 1 < playerStack.getMaxDamage()) {
                pot.dropLoot();
                pot.setSapling(pot.getSapling());
                playerStack.damageItem(1, player);
                return true;
            } else if(pot.getProgress() >= 20 && pot.getProgressPercent() <= 0.75f) {
                // Not ready and still under 90%
                pot.dropSapling();
                return true;
            }

            return true;
        }

        boolean playerHasShovel = playerStack.getItem().getHarvestLevel(playerStack, "shovel", player, Blocks.DIRT.getDefaultState()) != -1;
        if(playerHasShovel) {
            if(pot.hasSapling()) {
                player.sendStatusMessage(new TextComponentTranslation("hint.bonsaitrees.can_not_remove_soil_with_sapling"), true);
                return false;
            }

            if(!pot.hasSoil()) {
                return false;
            }

            pot.dropSoil();
            return true;
        }

        IBonsaiTreeType treeType = BonsaiTrees.instance.typeRegistry.getTypeByStack(playerStack);
        boolean playerHasSapling = treeType != null;
        if(playerHasSapling) {
            if(!pot.hasSoil()) {
                int randomSlot = world.rand.nextInt(BonsaiTrees.instance.soilRegistry.getAllSoils().size());
                Optional<IBonsaiSoil> optionalSoil = BonsaiTrees.instance.soilRegistry.getAllSoils().stream().skip(randomSlot).findFirst();
                if(optionalSoil.isPresent()) {
                    player.sendStatusMessage(new TextComponentTranslation("hint.bonsaitrees.pot_has_no_soil", optionalSoil.get().getSoilStack().getDisplayName()), true);
                } else {
                    Logz.warn("There is no soil available. Please check the config and logs for errors!");
                }

                return false;
            }

            if(pot.hasSapling()) {
                return false;
            }

            if(!BonsaiTrees.instance.soilCompatibility.canTreeGrowOnSoil(treeType, pot.getBonsaiSoil())) {
                player.sendStatusMessage(new TextComponentTranslation("hint.bonsaitrees.incompatible_soil"), true);
                return false;
            }


            if(player.isCreative()) {
                ItemStack soilStack = playerStack.copy();
                soilStack.setCount(1);
                pot.setSapling(soilStack);
            } else {
                pot.setSapling(playerStack.splitStack(1));
            }

            return true;
        }

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
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
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
        }

        if(teBonsai.hasSoil()) {
            probeInfo.horizontal().item(teBonsai.getSoilStack()).itemLabel(teBonsai.getSoilStack());
        }

        if(teBonsai.hasSapling()) {
            probeInfo.progress((int)(teBonsai.getProgressPercent()*100), 100, probeInfo.defaultProgressStyle().suffix("%").filledColor(0xff44AA44).alternateFilledColor(0xff44AA44).backgroundColor(0xff836953));
        }

        if(mode == ProbeMode.EXTENDED) {
            HoppingItemStackBufferHandler hoppingItemBuffer = teBonsai.getHoppingItemBuffer();
            if (hoppingItemBuffer != null && !hoppingItemBuffer.isEmpty()) {
                IProbeInfo line = probeInfo.horizontal();
                for (int srcSlot = 0; srcSlot < hoppingItemBuffer.getSlots(); srcSlot++) {
                    ItemStack stack = hoppingItemBuffer.getStackInSlot(srcSlot);
                    if (stack.isEmpty()) {
                        continue;
                    }

                    line.item(stack);
                }
            }
        }
    }
}