package com.davenonymous.bonsaitrees.client;

import com.davenonymous.bonsaitrees.BonsaiTrees;
import com.davenonymous.bonsaitrees.lib.gui.tooltip.MultiBlockModelTooltipComponent;
import com.davenonymous.bonsaitrees.setup.cache.BonsaiCache;
import com.davenonymous.bonsaitrees.setup.config.ClientConfig;
import com.davenonymous.bonsaitrees.setup.data.BonsaiInfo;
import com.mojang.datafixers.util.Either;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;

import java.util.List;

@EventBusSubscriber(modid = BonsaiTrees.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class BonsaiTooltip {

	@SubscribeEvent
	public static void onTooltipCreation(RenderTooltipEvent.GatherComponents event) {
		if(!ClientConfig.showTreesInSaplingTooltip) {
			return;
		}

		var stack = event.getItemStack();
		if(BonsaiCache.BONSAI_BY_ITEM.get(stack.getItem()) instanceof BonsaiInfo bonsai) {
			List<Either<FormattedText, TooltipComponent>> tooltips = event.getTooltipElements();
			var modelComponent = new MultiBlockModelTooltipComponent(bonsai.model(), 48, 48);
			tooltips.add(Either.right(modelComponent));
		}
	}
}
