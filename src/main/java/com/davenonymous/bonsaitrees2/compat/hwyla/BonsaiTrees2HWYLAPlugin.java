package com.davenonymous.bonsaitrees2.compat.hwyla;

import com.davenonymous.bonsaitrees2.block.BonsaiPotBlock;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;

@WailaPlugin
public class BonsaiTrees2HWYLAPlugin implements IWailaPlugin {

    @Override
    public void register(IRegistrar iRegistrar) {
        iRegistrar.registerComponentProvider(new BonsaiPotComponentProvider(), TooltipPosition.BODY, BonsaiPotBlock.class);
    }
}
