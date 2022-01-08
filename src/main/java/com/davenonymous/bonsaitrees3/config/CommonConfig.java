package com.davenonymous.bonsaitrees3.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class CommonConfig {
	private static final Builder COMMON_BUILDER = new Builder();

	public static ForgeConfigSpec COMMON_CONFIG;

	public static ForgeConfigSpec.BooleanValue sumEnchantmentLevels;

	public static ForgeConfigSpec.BooleanValue enableFortuneUpgrade;
	public static ForgeConfigSpec.BooleanValue enableEfficiencyUpgrade;
	public static ForgeConfigSpec.BooleanValue enableHoppingUpgrade;
	public static ForgeConfigSpec.BooleanValue enableAutoCuttingUpgrade;

	public static ForgeConfigSpec.BooleanValue showChanceInJEI;

	public static ForgeConfigSpec.IntValue extraRollsPerFortuneLevel;
	public static ForgeConfigSpec.DoubleValue extraChancePerFortuneLevel;

	public static ForgeConfigSpec.BooleanValue allowBonemeal;
	public static ForgeConfigSpec.DoubleValue bonemealSuccessChance;

	public static void register() {
		enableFortuneUpgrade = COMMON_BUILDER.comment("Enable fortune upgrades? (Can still be inserted into upgrade slots, but effects are disabled.)").define("enableFortuneUpgrade", true);

		enableEfficiencyUpgrade = COMMON_BUILDER.comment("Enable efficiency upgrades? (Can still be inserted into upgrade slots, but effects are disabled.)").define("enableEfficiencyUpgrade", true);

		enableHoppingUpgrade = COMMON_BUILDER.comment("Enable hopping upgrade? (Can still be inserted into upgrade slots, but effects are disabled.)").define("enableHoppingUpgrade", true);

		enableAutoCuttingUpgrade = COMMON_BUILDER.comment("Enable auto cutting upgrades? (Can still be inserted into upgrade slots, but effects are disabled.)").define("enableAutoCuttingUpgrade", true);

		showChanceInJEI = COMMON_BUILDER.comment("Show chance for drops in JEI").define("showChanceInJEI", true);

		sumEnchantmentLevels = COMMON_BUILDER.comment("Summarize enchantment levels instead of using the highest level? (I.e. is '4 x Fortune III = Fortune XII'?)").define("sumEnchantmentLevels", true);

		extraRollsPerFortuneLevel = COMMON_BUILDER.comment("Drops: How many extra rolls per fortune enchantment level?").defineInRange("extraRollsPerFortuneLevel", 1, 0, 64);

		extraChancePerFortuneLevel = COMMON_BUILDER.comment("Drops: What is the bonus chance to get a successful drop per fortune enchantment level?").defineInRange("extraChancePerFortuneLevel", 0.05d, 0.0d, 1.0d);

		allowBonemeal = COMMON_BUILDER.comment("Allow usage of bone meal on bonsai pots to boost their growth?").define("allowBonemeal", true);

		bonemealSuccessChance = COMMON_BUILDER.comment("How likely is it for bonemeal to succeed in boosting growth?").defineInRange("bonemealSuccessChance", 0.45d, 0.0d, 1.0d);

		COMMON_CONFIG = COMMON_BUILDER.build();

		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_CONFIG);
	}
}