package com.davenonymous.bonsaitrees.setup.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class GameplayConfig {
	public final ModConfigSpec.IntValue BASE_GROW_TICKS;
	public final ModConfigSpec.IntValue CUT_COOLDOWN;
	public final ModConfigSpec.IntValue TOOL_DAMAGE_PER_CUT;
	public final ModConfigSpec.DoubleValue TOOL_DAMAGE_CHANCE;
	public final ModConfigSpec.BooleanValue TOOL_ALLOW_INDESTRUCTIBLE;

	public static int baseGrowTicks;
	public static int cutCooldown;
	public static int toolDamagePerCut;
	public static double toolDamageChance;
	public static boolean toolAllowIndestructible;

	public GameplayConfig(ModConfigSpec.Builder builder) {
		builder.push("gameplay");

		BASE_GROW_TICKS = builder
			.comment("The base number of ticks it takes for a bonsai tree to grow.")
			.translation("bonsaitrees4.configuration.gameplay.baseGrowTicks")
			.defineInRange("baseGrowTicks", 200, 1, Integer.MAX_VALUE);

		CUT_COOLDOWN = builder
			.comment("If a bonsai tree has been cut, but its drops do not fit into the output inventory, how many ticks to wait before trying again.")
			.translation("bonsaitrees4.configuration.gameplay.cutCooldown")
			.defineInRange("cutCooldown", 20, 0, Integer.MAX_VALUE);

		TOOL_DAMAGE_PER_CUT = builder
			.comment("The amount of damage to deal to the tool when cutting a bonsai tree.")
			.translation("bonsaitrees4.configuration.gameplay.toolDamagePerCut")
			.defineInRange("toolDamagePerCut", 1, 0, Integer.MAX_VALUE);

		TOOL_DAMAGE_CHANCE = builder
			.comment("The chance that a tool will take damage when cutting a bonsai tree.")
			.translation("bonsaitrees4.configuration.gameplay.toolDamageChance")
			.defineInRange("toolDamageChance", 1.0f / 3, 0, 1);

		TOOL_ALLOW_INDESTRUCTIBLE = builder
			.comment("If true, indestructible tools (like those from Mystical Agriculture) can be used in bonsai pots.")
			.translation("bonsaitrees4.configuration.gameplay.toolAllowIndestructible")
			.define("toolAllowIndestructible", true);

		builder.pop();
	}

	public void load() {
		baseGrowTicks = BASE_GROW_TICKS.get();
		cutCooldown = CUT_COOLDOWN.get();
		toolDamagePerCut = TOOL_DAMAGE_PER_CUT.get();
		toolDamageChance = TOOL_DAMAGE_CHANCE.get();
		toolAllowIndestructible = TOOL_ALLOW_INDESTRUCTIBLE.get();
	}
}
