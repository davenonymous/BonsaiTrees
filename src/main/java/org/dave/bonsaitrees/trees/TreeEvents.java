package org.dave.bonsaitrees.trees;

import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.dave.bonsaitrees.BonsaiTrees;
import org.dave.bonsaitrees.init.Blockss;
import org.dave.bonsaitrees.tile.TileBonsaiPot;

public class TreeEvents {
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent breakEvent) {
        if(breakEvent.getWorld().isRemote) {
            return;
        }

        if(breakEvent.getState().getBlock() == Blockss.bonsaiPot && breakEvent.getWorld().getTileEntity(breakEvent.getPos()) instanceof TileBonsaiPot) {
            TileBonsaiPot pot = (TileBonsaiPot) breakEvent.getWorld().getTileEntity(breakEvent.getPos());
            if(!pot.hasSapling()) {
                pot.dropTile();
                return;
            }

            if(pot.getProgress() >= BonsaiTrees.instance.typeRegistry.getGrowTime(pot.getTreeType())) {
                pot.dropLoot();
            }

            pot.dropSapling();
            breakEvent.setCanceled(true);
        }
    }
}
