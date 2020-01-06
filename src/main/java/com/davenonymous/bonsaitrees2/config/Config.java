package com.davenonymous.bonsaitrees2.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber
public class Config {
    public static final String CATEGORY_GENERAL = "general";

    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

    public static ForgeConfigSpec COMMON_CONFIG;
    public static ForgeConfigSpec CLIENT_CONFIG;

    public static ForgeConfigSpec.BooleanValue NO_DYE_COST;
    public static ForgeConfigSpec.BooleanValue SHOW_CHANCE_IN_JEI;
    public static ForgeConfigSpec.EnumValue<WaterLogEffect> WATERLOG_EFFECT;

    public static ForgeConfigSpec.ConfigValue<List<String>> ADDITIONAL_CUTTING_TOOLS;



    static {
        COMMON_BUILDER.comment("General Settings").push(CATEGORY_GENERAL);
        setupGeneralConfig(COMMON_BUILDER);
        COMMON_BUILDER.pop();
        COMMON_CONFIG = COMMON_BUILDER.build();

        CLIENT_BUILDER.comment("General Settings").push(CATEGORY_GENERAL);
        setupClientConfig(CLIENT_BUILDER);
        CLIENT_BUILDER.pop();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

    private static void setupGeneralConfig(ForgeConfigSpec.Builder b) {
        NO_DYE_COST = b.comment("If set to true, then dye is not being used up when painting bonsai pots.").define("noDyeCost", false);
        WATERLOG_EFFECT = b.comment("How to handle waterlogged bonsai pots.").defineEnum("waterloggedEffect", WaterLogEffect.DROP_LOOT);
        ADDITIONAL_CUTTING_TOOLS = b.comment("Additional items that are able to cut bonsai trees.").define("additionalCuttingTools", Arrays.asList("minecraft:shears"));
    }

    private static void setupClientConfig(ForgeConfigSpec.Builder b) {
        SHOW_CHANCE_IN_JEI = b.comment("Whether to show the drop chances in JEI").define("showChanceInJEI", true);
    }

    public static void loadConfig(ForgeConfigSpec spec, Path path) {

        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();

        configData.load();
        spec.setConfig(configData);
    }

    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent) {

    }

    @SubscribeEvent
    public static void onReload(final ModConfig.ConfigReloading configEvent) {
    }
}
