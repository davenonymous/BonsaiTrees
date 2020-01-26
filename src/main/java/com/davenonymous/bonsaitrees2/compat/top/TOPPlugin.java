package com.davenonymous.bonsaitrees2.compat.top;

/*
import mcjty.theoneprobe.api.*;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import java.util.function.Function;

public class TOPPlugin implements Function<ITheOneProbe, Void> {
    @Override
    public Void apply(ITheOneProbe iTheOneProbe) {
        iTheOneProbe.registerProvider(new IProbeInfoProvider() {
            @Override
            public String getID() {
                return "bonsaitrees2:default";
            }

            @Override
            public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData iProbeHitData) {
                if(blockState.getBlock() instanceof ITopInfoProvider) {
                    ITopInfoProvider provider = (ITopInfoProvider) blockState.getBlock();
                    provider.addProbeInfo(probeMode, iProbeInfo, player, world, blockState, iProbeHitData);
                }
            }
        });
        return null;
    }
}
*/