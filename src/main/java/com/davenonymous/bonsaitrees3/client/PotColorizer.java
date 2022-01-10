package com.davenonymous.bonsaitrees3.client;

import com.davenonymous.bonsaitrees3.blocks.BonsaiPotBlockEntity;
import com.davenonymous.bonsaitrees3.libnonymous.registration.CustomBlockStateProperties;
import com.davenonymous.bonsaitrees3.setup.NbtConsts;
import com.davenonymous.bonsaitrees3.setup.Registration;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class PotColorizer {
	public static final DyeColor DEFAULT_COLOR = DyeColor.LIGHT_GRAY;

	@SubscribeEvent
	public static void init(ColorHandlerEvent.Block event) {
		final BlockColor potColorHandler = (state, blockAccess, pos, tintIndex) -> {
			if(tintIndex == 130) {
				if(!state.hasProperty(CustomBlockStateProperties.COLOR)) {
					return PotColorizer.DEFAULT_COLOR.getFireworkColor();
				}

				int color = state.getValue(CustomBlockStateProperties.COLOR);
				int rgb = DyeColor.byId(color).getFireworkColor();
				return rgb;
			} else if(blockAccess != null && blockAccess.getBlockEntity(pos) != null) {
				// TODO: Investigate crash with fluid pots
				BonsaiPotBlockEntity potBlock = (BonsaiPotBlockEntity) blockAccess.getBlockEntity(pos);
				if(potBlock.hasSoil() && !potBlock.getSoilInfo().isFluid) {
					return BlockColors.createDefault().getColor(potBlock.getSoilBlock(), blockAccess, pos, tintIndex);
				}

				return PotColorizer.DEFAULT_COLOR.getFireworkColor();
			} else {
				return PotColorizer.DEFAULT_COLOR.getFireworkColor();
			}

		};

		event.getBlockColors().register(potColorHandler, Registration.BONSAI_POT.get());
	}

	@SubscribeEvent
	public static void init(ColorHandlerEvent.Item event) {
		final ItemColor potColorHandler = (stack, tintIndex) -> {
			if(!stack.hasTag()) {
				return PotColorizer.DEFAULT_COLOR.getFireworkColor();
			}

			var tag = stack.getTag();
			if(!tag.contains(NbtConsts.color)) {
				return PotColorizer.DEFAULT_COLOR.getFireworkColor();
			}

			int color = tag.getInt(NbtConsts.color);
			int rgb = DyeColor.byId(color).getFireworkColor();
			return rgb;
		};

		event.getItemColors().register(potColorHandler, Registration.BONSAI_POT.get());
	}
}