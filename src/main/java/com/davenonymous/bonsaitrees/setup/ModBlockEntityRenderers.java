package com.davenonymous.bonsaitrees.setup;

import com.davenonymous.bonsaitrees.BonsaiTrees;
import com.davenonymous.bonsaitrees.client.BonsaiPotBlockRenderer;
import com.davenonymous.bonsaitrees.client.BonsaiPotItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = BonsaiTrees.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModBlockEntityRenderers {

	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(
			ModBlocks.BONSAI_POT_ENTITY.get(),
			BonsaiPotBlockRenderer::new
		);
	}

	static class BonsaiPotClientItemExtension implements IClientItemExtensions {
		public final BonsaiPotItemRenderer bonsaiPotItemRenderer = new BonsaiPotItemRenderer();

		@Override
		public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
			return bonsaiPotItemRenderer;
		}
	}

	@SubscribeEvent
	public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
		event.registerItem(new BonsaiPotClientItemExtension(), ModItems.BONSAI_POT_ITEM.get(), ModItems.BONSAI_POT_SMALL_ITEM.get());
	}
}
