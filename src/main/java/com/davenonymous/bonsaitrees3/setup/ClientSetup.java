package com.davenonymous.bonsaitrees3.setup;

import com.davenonymous.bonsaitrees3.BonsaiTrees3;
import com.davenonymous.bonsaitrees3.client.BonsaiPotModelLoader;
import com.davenonymous.bonsaitrees3.client.BonsaiPotRenderer;
import com.davenonymous.bonsaitrees3.client.BonsaiPotScreen;
import com.davenonymous.bonsaitrees3.client.TreeModels;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = BonsaiTrees3.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {
	public static void init(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
            /*
            MenuScreens.register(Registration.POWERGEN_CONTAINER.get(), PowergenScreen::new);
            ItemBlockRenderTypes.setRenderLayer(Registration.POWERGEN.get(), RenderType.translucent());
            PowergenRenderer.register();
             */
			MenuScreens.register(Registration.BONSAI_POT_CONTAINER.get(), BonsaiPotScreen::new);
			TreeModels.init();
			BlockEntityRenderers.register(Registration.BONSAI_POT_BLOCKENTITY.get(), BonsaiPotRenderer::new);
		});
	}

	@SubscribeEvent
	public static void onModelRegistryEvent(ModelRegistryEvent event) {
		ModelLoaderRegistry.registerLoader(BonsaiPotModelLoader.BONSAIPOT_LOADER, new BonsaiPotModelLoader());
	}
}
