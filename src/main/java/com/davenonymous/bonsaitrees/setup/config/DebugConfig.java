package com.davenonymous.bonsaitrees.setup.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class DebugConfig {
	public final ModConfigSpec.BooleanValue SHOW_CHANCES;
	public final ModConfigSpec.BooleanValue SHOW_UNKNOWN_LOOT_CONDITIONS;
	public final ModConfigSpec.BooleanValue SHOW_UNUSED_SOIL_RECIPES_IN_JEI;
	public final ModConfigSpec.BooleanValue SHOW_ROLLS_AS_COUNT_IN_JEI;

	public static boolean showChances;
	public static boolean showUnknownLootConditions;
	public static boolean showUnusedSoilRecipesInJEI;
	public static boolean showRollsAsCountInJEI;

	public DebugConfig(ModConfigSpec.Builder builder) {
		builder.push("debug");
		SHOW_CHANCES = builder
			.comment("Show chances in tooltips")
			.translation("bonsaitrees4.configuration.debug.show_chances")
			.define("showChances", false);

		SHOW_UNKNOWN_LOOT_CONDITIONS = builder
			.comment("Show unknown loot conditions in tooltips")
			.translation("bonsaitrees4.configuration.debug.show_unknown_loot_conditions")
			.define("showUnknownLootConditions", false);

		SHOW_UNUSED_SOIL_RECIPES_IN_JEI = builder
			.comment("Show unused soil recipes in JEI")
			.translation("bonsaitrees4.configuration.debug.show_unused_soil_recipes_in_jei")
			.define("showUnusedSoilRecipesInJEI", false);

		SHOW_ROLLS_AS_COUNT_IN_JEI = builder
			.comment("Show rolls as count in JEI")
			.translation("bonsaitrees4.configuration.debug.show_rolls_as_count_in_jei")
			.define("showRollsAsCountInJEI", false);

		builder.pop();
	}

	public void load() {
		showChances = SHOW_CHANCES.get();
		showUnknownLootConditions = SHOW_UNKNOWN_LOOT_CONDITIONS.get();
		showUnusedSoilRecipesInJEI = SHOW_UNUSED_SOIL_RECIPES_IN_JEI.get();
		showRollsAsCountInJEI = SHOW_ROLLS_AS_COUNT_IN_JEI.get();
	}
}
