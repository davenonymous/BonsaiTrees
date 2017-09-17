package org.dave.bonsaitrees.init;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.dave.bonsaitrees.block.BlockBonsaiPot;

public class Blockss {
    @GameRegistry.ObjectHolder("bonsaitrees:bonsaipot")
    public static BlockBonsaiPot bonsaiPot;

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        bonsaiPot.initModel();
    }
}
