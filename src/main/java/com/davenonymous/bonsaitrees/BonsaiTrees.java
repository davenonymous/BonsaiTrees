package com.davenonymous.bonsaitrees;

import com.davenonymous.bonsaitrees.setup.Registration;
import com.davenonymous.bonsaitrees.setup.config.Config;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(BonsaiTrees.MODID)
public class BonsaiTrees {
	public static final String MODID = "bonsaitrees4";
	public static final Logger LOGGER = LogUtils.getLogger();
	public static ModContainer CONTAINER;

	public BonsaiTrees(IEventBus modEventBus, ModContainer modContainer) {
		CONTAINER = modContainer;
		Registration.register(modEventBus);

		modContainer.registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC);
		modContainer.registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC);
	}

	public static ResourceLocation resource(String path) {
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}

}