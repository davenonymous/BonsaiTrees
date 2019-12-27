package com.davenonymous.bonsaitrees2.block;

import com.davenonymous.bonsaitrees2.BonsaiTrees2;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(BonsaiTrees2.MODID)
public class Blockz {
    @ObjectHolder("bonsaipot")
    public static BonsaiPotBlock BONSAIPOT;

    @ObjectHolder("hopping_bonsaipot")
    public static BonsaiPotBlock HOPPING_BONSAIPOT;

    @ObjectHolder("bonsaipot")
    public static TileEntityType<BonsaiPotTileEntity> BONSAIPOT_TILE;

    @ObjectHolder("hopping_bonsaipot")
    public static TileEntityType<HoppingBonsaiPotTileEntity> HOPPING_BONSAIPOT_TILE;

}
