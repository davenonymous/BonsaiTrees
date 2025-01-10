package com.davenonymous.bonsaitrees.setup.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {
	public final ModConfigSpec.BooleanValue MINMAL_QUADS;
	public final ModConfigSpec.BooleanValue DISABLE_MODEL_CACHE;
	public final ModConfigSpec.BooleanValue SHOW_JEI_SUBTYPES;
	public final ModConfigSpec.BooleanValue SHOW_TREE_IN_SAPLING_TOOLTIP;

	public static boolean minimalQuads;
	public static boolean disableModelCache;
	public static boolean showJEISubtypes;
	public static boolean showTreesInSaplingTooltip;

	public ClientConfig(ModConfigSpec.Builder builder) {
		builder.push("client");

		MINMAL_QUADS = builder
			.comment("Use minimal quads for rendering")
			.translation("bonsaitrees4.configuration.client.minimal_quads")
			.define("minimalQuads", false);

		DISABLE_MODEL_CACHE = builder
			.comment("Disable model cache")
			.translation("bonsaitrees4.configuration.client.disable_model_cache")
			.define("disableModelCache", false);

		SHOW_JEI_SUBTYPES = builder
			.comment("Show all trees in JEI")
			.translation("bonsaitrees4.configuration.client.show_jei_subtypes")
			.define("showJEISubtypes", false);

		SHOW_TREE_IN_SAPLING_TOOLTIP = builder
			.comment("Show the tree model in the sapling tooltip")
			.translation("bonsaitrees4.configuration.client.show_tree_in_sapling_tooltip")
			.define("showTreesInSaplingTooltip", true);

		builder.pop();
	}

	public void load() {
		minimalQuads = MINMAL_QUADS.get();
		disableModelCache = DISABLE_MODEL_CACHE.get();
		showJEISubtypes = SHOW_JEI_SUBTYPES.get();
		showTreesInSaplingTooltip = SHOW_TREE_IN_SAPLING_TOOLTIP.get();
	}
}
