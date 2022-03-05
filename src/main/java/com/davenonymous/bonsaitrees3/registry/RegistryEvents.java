package com.davenonymous.bonsaitrees3.registry;

import com.davenonymous.bonsaitrees3.client.TreeModels;
import com.davenonymous.libnonymous.base.RecipeData;
import com.davenonymous.bonsaitrees3.registry.sapling.SaplingInfo;
import com.davenonymous.bonsaitrees3.registry.sapling.SaplingRecipeHelper;
import com.davenonymous.bonsaitrees3.registry.soil.SoilInfo;
import com.davenonymous.bonsaitrees3.registry.soil.SoilRecipeHelper;
import com.davenonymous.bonsaitrees3.setup.Registration;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegistryBuilder;

import static com.davenonymous.bonsaitrees3.BonsaiTrees3.MODID;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryEvents {

	@SubscribeEvent
	static void onCommonSetup(FMLCommonSetupEvent event) {
		Registration.RECIPE_TYPE_SOIL = RecipeType.register(new ResourceLocation(MODID, "soil").toString());
		Registration.RECIPE_TYPE_SAPLING = RecipeType.register(new ResourceLocation(MODID, "sapling").toString());

		Registration.RECIPE_HELPER_SOIL = new SoilRecipeHelper();
		Registration.RECIPE_HELPER_SAPLING = new SaplingRecipeHelper();
	}

	@SubscribeEvent
	public static void onClientReloadListener(RegisterClientReloadListenersEvent event) {
		var listener = new ResourceManagerReloadListener() {

			@Override
			public void onResourceManagerReload(ResourceManager pResourceManager) {
				TreeModels.init();
			}
		};
		event.registerReloadListener(listener);
	}


}