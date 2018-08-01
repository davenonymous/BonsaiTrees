package org.dave.bonsaitrees.compat.Waila;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import org.dave.bonsaitrees.block.BlockBonsaiPot;
import org.dave.bonsaitrees.tile.TileBonsaiPot;
import org.dave.bonsaitrees.utility.Logz;

import javax.annotation.Nonnull;
import java.util.List;

public class WailaProvider {
    public static void register(IWailaRegistrar registry) {
        Logz.info("Enabled support for Waila/Hwyla");
        registry.registerBodyProvider(new BonsaiPotProvider(), BlockBonsaiPot.class);
    }

    public static class BonsaiPotProvider implements IWailaDataProvider {
        @Nonnull
        @Override
        public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
            if(!(accessor.getTileEntity() instanceof TileBonsaiPot)) {
                return currenttip;
            }

            TileBonsaiPot teBonsai = (TileBonsaiPot) accessor.getTileEntity();
            if(teBonsai.hasSapling()) {
                currenttip.add(TextFormatting.GRAY + teBonsai.getSapling().getDisplayName());
            }

            if(teBonsai.hasSoil()) {
                currenttip.add(TextFormatting.GRAY + teBonsai.getSoilStack().getDisplayName());
            }

            if(teBonsai.hasSapling()) {
                currenttip.add(String.format("%s%.1f%%", TextFormatting.YELLOW, teBonsai.getProgressPercent()*100));
            }

            return currenttip;
        }
    }
}
