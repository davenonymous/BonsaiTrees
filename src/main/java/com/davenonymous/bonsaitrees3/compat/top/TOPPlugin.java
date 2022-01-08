package com.davenonymous.bonsaitrees3.compat.top;

import com.davenonymous.bonsaitrees3.BonsaiTrees3;
import mcjty.theoneprobe.api.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

public class TOPPlugin implements Function<ITheOneProbe, Void> {
	@Override
	public Void apply(ITheOneProbe iTheOneProbe) {
		iTheOneProbe.registerProvider(new IProbeInfoProvider() {
			private final ResourceLocation TOP_ID = new ResourceLocation(BonsaiTrees3.MODID, "default");

			@Override
			public ResourceLocation getID() {
				return TOP_ID;
			}

			@Override
			public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, Player player, Level level, BlockState blockState, IProbeHitData iProbeHitData) {
				if(blockState.getBlock() instanceof ITopInfoProvider provider) {
					provider.addProbeInfo(probeMode, iProbeInfo, player, level, blockState, iProbeHitData);
				}
			}
		});

		return null;
	}
}
