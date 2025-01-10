package com.davenonymous.bonsaitrees.setup;

import com.davenonymous.bonsaitrees.BonsaiTrees;
import com.davenonymous.bonsaitrees.lib.gui.tooltip.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;

import java.util.function.Function;

@EventBusSubscriber(modid = BonsaiTrees.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModTooltipComponents {

	@SubscribeEvent
	public static void onTooltipRegister(RegisterClientTooltipComponentFactoriesEvent event) {
		event.register(ItemStackTooltipComponent.class, Function.identity());
		event.register(HBoxTooltipComponent.class, Function.identity());
		event.register(VBoxTooltipComponent.class, Function.identity());
		event.register(SpriteTooltipComponent.class, Function.identity());
		event.register(StringTooltipComponent.class, Function.identity());
		event.register(IngredientTooltipComponent.class, Function.identity());
		event.register(IngredientBoxTooltipComponent.class, Function.identity());
		event.register(TranslatableTooltipComponent.class, Function.identity());
		event.register(MultiBlockModelTooltipComponent.class, Function.identity());
		event.register(RedstoneModeTooltipComponent.class, Function.identity());
	}
}
