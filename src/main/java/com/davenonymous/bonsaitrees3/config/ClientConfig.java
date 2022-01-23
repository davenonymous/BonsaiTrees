package com.davenonymous.bonsaitrees3.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class ClientConfig {
	private static final Builder CLIENT_BUILDER = new Builder();

	public static ForgeConfigSpec CLIENT_CONFIG;

	public static ForgeConfigSpec.BooleanValue alwaysRenderAsItem;

	public static void register() {
		alwaysRenderAsItem = CLIENT_BUILDER
				.comment("Always render trees in bonsai pot as their item equivalent.")
				.define("alwaysRenderAsItem", false);

		CLIENT_CONFIG = CLIENT_BUILDER.build();

		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_CONFIG);
	}
}