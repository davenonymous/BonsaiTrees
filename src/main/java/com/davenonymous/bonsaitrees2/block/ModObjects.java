package com.davenonymous.bonsaitrees2.block;

import com.davenonymous.bonsaitrees2.BonsaiTrees2;
import com.davenonymous.bonsaitrees2.gui.TreeCreatorContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(BonsaiTrees2.MODID)
public class ModObjects {
    @ObjectHolder("bonsaipot")
    public static BonsaiPotBlock BONSAIPOT;

    @ObjectHolder("hopping_bonsaipot")
    public static BonsaiPotBlock HOPPING_BONSAIPOT;

    @ObjectHolder("bonsaipot")
    public static TileEntityType<BonsaiPotTileEntity> BONSAIPOT_TILE;

    @ObjectHolder("hopping_bonsaipot")
    public static TileEntityType<HoppingBonsaiPotTileEntity> HOPPING_BONSAIPOT_TILE;

    @ObjectHolder("tree_creator")
    public static ContainerType<TreeCreatorContainer> TREE_CREATOR_CONTAINER;
}
