package com.davenonymous.bonsaitrees3.datagen.server;

import com.davenonymous.bonsaitrees3.BonsaiTrees3;
import com.davenonymous.libnonymous.datagen.BaseDataProvider;
import com.davenonymous.libnonymous.reflections.BlockStateProviderReflection;
import com.davenonymous.bonsaitrees3.registry.sapling.SaplingDrop;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.HugeFungusConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DatagenSaplings extends BaseDataProvider {
	private static final Logger LOGGER = LogManager.getLogger();

	public DatagenSaplings(DataGenerator generator) {
		super(generator, Type.DATA);
	}

	@Override
	public String getModId() {
		return BonsaiTrees3.MODID;
	}

	@Override
	public void addValues() {
		addSapling(Items.ACACIA_SAPLING, TreeFeatures.ACACIA.value());
		addSapling(Items.BIRCH_SAPLING, TreeFeatures.BIRCH.value());
		addSapling(Items.DARK_OAK_SAPLING, TreeFeatures.DARK_OAK.value());
		addSapling(Items.JUNGLE_SAPLING, TreeFeatures.JUNGLE_TREE.value(), fruitDrop(Items.COCOA_BEANS));
		addSapling(Items.OAK_SAPLING, TreeFeatures.OAK.value(), fruitDrop(Items.APPLE));
		addSapling(Items.SPRUCE_SAPLING, TreeFeatures.SPRUCE.value());
		//addSapling(Items.AZALEA, getAsTreeConfiguration(TreeFeatures.AZALEA_TREE));

		addFungus(Items.CRIMSON_FUNGUS, TreeFeatures.CRIMSON_FUNGUS.value(), new String[]{"crimson"});
		addFungus(Items.WARPED_FUNGUS, TreeFeatures.WARPED_FUNGUS.value(), new String[]{"warped"});

		addMushroom(Items.RED_MUSHROOM, TreeFeatures.HUGE_RED_MUSHROOM.value(), new String[]{"mycelium"});
		addMushroom(Items.BROWN_MUSHROOM, TreeFeatures.HUGE_BROWN_MUSHROOM.value(), new String[]{"mycelium"});

		addCoral(Items.BRAIN_CORAL, Items.DEAD_BRAIN_CORAL, Blocks.BRAIN_CORAL_BLOCK);
		addCoral(Items.BUBBLE_CORAL, Items.DEAD_BUBBLE_CORAL, Blocks.BUBBLE_CORAL_BLOCK);
		addCoral(Items.FIRE_CORAL, Items.DEAD_FIRE_CORAL, Blocks.FIRE_CORAL_BLOCK);
		addCoral(Items.HORN_CORAL, Items.DEAD_HORN_CORAL, Blocks.HORN_CORAL_BLOCK);
		addCoral(Items.TUBE_CORAL, Items.DEAD_TUBE_CORAL, Blocks.TUBE_CORAL_BLOCK);

		addChorus();
	}

	public JsonObject setTicks(JsonObject original, int ticks) {
		original.addProperty("ticks", ticks);
		return original;
	}

	public static SaplingDrop fruitDrop(Item item) {
		return new SaplingDrop(item, 0.05f, 1, false, true, Ingredient.EMPTY);
	}

	@SuppressWarnings("unchecked")
	public ConfiguredFeature<TreeConfiguration, ?> getAsTreeConfiguration(ConfiguredFeature<?, ?> feature) {
		return (ConfiguredFeature<TreeConfiguration, ?>) feature;
	}

	private JsonObject addChorus() {
		Item chorusItem = Items.CHORUS_FRUIT;
		Item enderPeal = Items.ENDER_PEARL;
		Item chorusFlower = Items.CHORUS_FLOWER;

		JsonObject root = new JsonObject();
		root.addProperty("type", "bonsaitrees3:sapling");
		root.addProperty("mod", "minecraft");

		JsonObject saplingObject = new JsonObject();
		saplingObject.addProperty("item", chorusItem.getRegistryName().toString());
		root.add("sapling", saplingObject);

		JsonArray drops = new JsonArray();
		addDrop(drops, chorusItem, 1, 0.2f);
		addDrop(drops, chorusFlower, 1, 0.1f);
		addDrop(drops, enderPeal, 1, 0.01f, true);
		root.add("drops", drops);

		JsonArray tags = new JsonArray();
		tags.add("end_stone");
		root.add("compatibleSoilTags", tags);

		setTicks(root, 20*300);

		add("recipes/sapling/minecraft/chorus", root);
		return root;
	}

	private JsonObject addCoral(Item coralItem, Item deadCoralItem, Block coralBlock) {
		JsonObject root = new JsonObject();
		root.addProperty("type", "bonsaitrees3:sapling");
		root.addProperty("mod", "minecraft");

		JsonObject saplingObject = new JsonObject();
		saplingObject.addProperty("item", coralItem.getRegistryName().toString());
		root.add("sapling", saplingObject);

		JsonArray drops = new JsonArray();
		addDrop(drops, deadCoralItem, 1, 0.05f);
		addDrop(drops, coralItem, 1, 0.05f, true);
		addDrop(drops, coralBlock, 1, 0.75f, true);

		root.add("drops", drops);

		JsonArray tags = new JsonArray();
		tags.add("water");
		root.add("compatibleSoilTags", tags);

		setTicks(root, 300);

		var coralLocation = coralItem.getRegistryName();
		add("recipes/sapling/" + coralLocation.getNamespace() + "/" + coralLocation.getPath(), root);
		return root;
	}

	private JsonObject addMushroom(Item mushroomItem, ConfiguredFeature<?, ?> mushroomFeature, String[] compatibleTags) {
		var mc = (HugeMushroomFeatureConfiguration) mushroomFeature.config();

		JsonObject root = new JsonObject();
		root.addProperty("type", "bonsaitrees3:sapling");
		root.addProperty("mod", "minecraft");

		JsonObject saplingObject = new JsonObject();
		saplingObject.addProperty("item", mushroomItem.getRegistryName().toString());
		root.add("sapling", saplingObject);

		JsonArray drops = new JsonArray();
		addDrop(drops, mushroomItem, 3, 0.20f);
		addDrop(drops, mc.stemProvider.getState(null, null).getBlock(), 1, 0.75f, true);
		addDrop(drops, mc.capProvider.getState(null, null).getBlock(), 2, 0.2f, true);

		root.add("drops", drops);

		if(compatibleTags.length > 0) {
			JsonArray tags = new JsonArray();
			Arrays.stream(compatibleTags).forEach(s -> tags.add(s));
			root.add("compatibleSoilTags", tags);
		}

		setTicks(root, 300);

		ResourceLocation mushroomLocation = BuiltinRegistries.CONFIGURED_FEATURE.getResourceKey(mushroomFeature).get().location();
		add("recipes/sapling/" + mushroomLocation.getNamespace() + "/" + mushroomLocation.getPath(), root);
		return root;
	}

	private JsonObject addFungus(Item fungusItem, ConfiguredFeature<HugeFungusConfiguration, ?> fungusFeature, String[] compatibleTags) {
		var fc = fungusFeature.config();

		JsonObject root = new JsonObject();
		root.addProperty("type", "bonsaitrees3:sapling");
		root.addProperty("mod", "minecraft");

		JsonObject saplingObject = new JsonObject();
		saplingObject.addProperty("item", fungusItem.getRegistryName().toString());
		root.add("sapling", saplingObject);

		JsonArray drops = new JsonArray();
		addDrop(drops, fungusItem, 3, 0.20f);
		addDrop(drops, fc.stemState.getBlock(), 1, 0.75f);
		addDrop(drops, fc.hatState.getBlock(), 2, 0.2f);
		addDrop(drops, fc.decorState.getBlock(), 1, 0.05f);

		root.add("drops", drops);

		if(compatibleTags.length > 0) {
			JsonArray tags = new JsonArray();
			Arrays.stream(compatibleTags).forEach(s -> tags.add(s));
			root.add("compatibleSoilTags", tags);
		}

		setTicks(root, 400);

		ResourceLocation fungusLocation = BuiltinRegistries.CONFIGURED_FEATURE.getResourceKey(fungusFeature).get().location();
		add("recipes/sapling/" + fungusLocation.getNamespace() + "/" + fungusLocation.getPath(), root);
		return root;
	}

	public JsonObject addSapling(Item saplingItem, ConfiguredFeature<TreeConfiguration, ?> treeFeature) {
		return addSapling(saplingItem, treeFeature, new String[]{"dirt", "grass"});
	}

	public JsonObject addSapling(Item saplingItem, ConfiguredFeature<TreeConfiguration, ?> treeFeature, SaplingDrop... extraDrops) {
		return addSapling(saplingItem, treeFeature, new String[]{"dirt", "grass"}, extraDrops);
	}

	public JsonObject addSapling(Item saplingItem, ConfiguredFeature<TreeConfiguration, ?> treeFeature, String[] compatibleTags) {
		return addSapling(saplingItem, treeFeature, compatibleTags, null);
	}

	public JsonObject addSapling(Item saplingItem, ConfiguredFeature<TreeConfiguration, ?> treeFeature, String[] compatibleTags, SaplingDrop... extraDrops) {
		var tc = treeFeature.config();

		JsonObject root = new JsonObject();
		root.addProperty("type", "bonsaitrees3:sapling");
		var mod = BuiltinRegistries.CONFIGURED_FEATURE.getResourceKey(treeFeature).get().location().getNamespace();
		root.addProperty("mod", mod);


		JsonObject saplingObject = new JsonObject();
		saplingObject.addProperty("item", saplingItem.getRegistryName().toString());
		root.add("sapling", saplingObject);

		JsonArray drops = new JsonArray();
		addDrop(drops, saplingItem, 1, 0.05f);


		for(var trunkState : getProviderBlockStates(tc.trunkProvider)) {
			var trunkItem = trunkState.getFirst().getBlock().asItem();
			var chance = trunkState.getSecond();

			addDrop(drops, trunkItem, 1, 0.75f * chance);
		}

		addDrop(drops, Items.STICK, 3, 0.2f);

		for(var foliageState : getProviderBlockStates(tc.foliageProvider)) {
			var foliageItem = foliageState.getFirst().getBlock().asItem();
			var chance = foliageState.getSecond();

			addDrop(drops, foliageItem, 2, 0.2f * chance, true);
		}

		if(extraDrops != null) {
			for(var drop : extraDrops) {
				addDrop(drops, drop);
			}
		}

		root.add("drops", drops);

		if(compatibleTags.length > 0) {
			JsonArray tags = new JsonArray();
			Arrays.stream(compatibleTags).forEach(tags::add);
			root.add("compatibleSoilTags", tags);
		}

		ResourceLocation treeLocation = BuiltinRegistries.CONFIGURED_FEATURE.getResourceKey(treeFeature).get().location();
		add("recipes/sapling/" + treeLocation.getNamespace() + "/" + treeLocation.getPath(), root);

		return root;
	}

	public void addDrop(JsonArray drops, SaplingDrop drop) {
		JsonObject root = new JsonObject();
		root.addProperty("rolls", drop.rolls);
		root.addProperty("chance", getRounded(drop.chance));
		root.add("result", new Ingredient.ItemValue(drop.resultStack).serialize());
		if(drop.requiresSilkTouch) {
			root.addProperty("requiresSilkTouch", true);
		}
		if(drop.requiresBees) {
			root.addProperty("requiresBees", true);
		}
		drops.add(root);
	}

	public void addDrop(JsonArray drops, Block drop, int rolls, double chance) {
		addDrop(drops, new ItemStack(drop), rolls, chance, false, false);
	}

	public void addDrop(JsonArray drops, Block drop, int rolls, double chance, boolean requiresSilkTouch) {
		addDrop(drops, new ItemStack(drop), rolls, chance, requiresSilkTouch, false);
	}

	public void addDrop(JsonArray drops, Block drop, int rolls, double chance, boolean requiresSilkTouch, boolean requiresBees) {
		addDrop(drops, new ItemStack(drop), rolls, chance, requiresSilkTouch, requiresBees);
	}

	public void addDrop(JsonArray drops, Item drop, int rolls, double chance) {
		addDrop(drops, new ItemStack(drop), rolls, chance, false, false);
	}

	public void addDrop(JsonArray drops, Item drop, int rolls, double chance, boolean requiresSilkTouch) {
		addDrop(drops, new ItemStack(drop), rolls, chance, requiresSilkTouch, false);
	}

	public void addDrop(JsonArray drops, Item drop, int rolls, double chance, boolean requiresSilkTouch, boolean requiresBees) {
		addDrop(drops, new ItemStack(drop), rolls, chance, requiresSilkTouch, requiresBees);
	}

	public void addDrop(JsonArray drops, ItemStack drop, int rolls, double chance, boolean requiresSilkTouch, boolean requiresBees) {
		if(drop.isEmpty()) {
			return;
		}

		JsonObject root = new JsonObject();
		root.addProperty("rolls", rolls);
		root.addProperty("chance", getRounded(chance));
		root.add("result", new Ingredient.ItemValue(drop).serialize());
		if(requiresSilkTouch) {
			root.addProperty("requiresSilkTouch", true);
		}
		if(requiresBees) {
			root.addProperty("requiresBees", true);
		}
		drops.add(root);
	}

	private List<Pair<BlockState, Double>> getProviderBlockStates(BlockStateProvider provider) {
		if(provider instanceof SimpleStateProvider ssp) {
			return List.of(Pair.of(ssp.getState(null, null), 1.0d));

		} else if(provider instanceof WeightedStateProvider wsp) {
			var weightedList = BlockStateProviderReflection.getWeightedListFromWeightedStateProvider(BlockState.class, wsp);

			var totalWeight = WeightedRandom.getTotalWeight(weightedList.unwrap());
			if(totalWeight <= 0) {
				return Collections.emptyList();
			}

			List<Pair<BlockState, Double>> result = new ArrayList<>();
			for(var entry : weightedList.unwrap()) {
				double chance = (double) entry.getWeight().asInt() / (double) totalWeight;
				result.add(Pair.of(entry.getData().getBlock().defaultBlockState(), chance));
			}

			return result;
		}

		LOGGER.error("Unknown block state provider {}. Skipping some blocks in recipe drops!", provider);
		return Collections.emptyList();
	}

	@Override
	public String getName() {
		return "Bonsai Trees Sapling Types";
	}
}