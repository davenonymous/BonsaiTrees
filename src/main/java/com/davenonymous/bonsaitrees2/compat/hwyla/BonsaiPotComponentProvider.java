package com.davenonymous.bonsaitrees2.compat.hwyla;

import com.davenonymous.bonsaitrees2.block.BonsaiPotTileEntity;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IPluginConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

public class BonsaiPotComponentProvider implements IComponentProvider {

    @Override
    public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
        if(!(accessor.getTileEntity() instanceof BonsaiPotTileEntity)) {
            return;
        }

        BonsaiPotTileEntity teBonsai = (BonsaiPotTileEntity) accessor.getTileEntity();
        if(teBonsai.hasSapling()) {
            ItemStack representation = teBonsai.getSaplingStack();
            if(representation != null) {
                tooltip.add(representation.getDisplayName().applyTextStyle(TextFormatting.GRAY));
            }
        }

        if(teBonsai.hasSoil()) {
            ItemStack representation = teBonsai.getSoilStack();
            tooltip.add(representation.getDisplayName().applyTextStyle(TextFormatting.GRAY));
        }

        if(teBonsai.hasSapling()) {
            tooltip.add(new StringTextComponent(String.format("%s%.1f%%", TextFormatting.YELLOW, teBonsai.getProgress()*100)));
        }

        return;
    }
}
