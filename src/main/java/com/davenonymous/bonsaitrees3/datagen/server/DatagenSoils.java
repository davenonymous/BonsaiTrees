package com.davenonymous.bonsaitrees3.datagen.server;

import com.davenonymous.bonsaitrees3.BonsaiTrees3;
import com.davenonymous.libnonymous.datagen.BaseDataProvider;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;

public class DatagenSoils extends BaseDataProvider {
	public DatagenSoils(DataGenerator generator) {
		super(generator, Type.DATA);
	}

	@Override
	public String getModId() {
		return BonsaiTrees3.MODID;
	}

	@Override
	public void addValues() {
		addSoil(Items.SAND, Blocks.SAND, 1.0, new String[]{"sand"});

		addSoil(Items.DIRT, Blocks.DIRT, 1.1, new String[]{"dirt"});
		addSoil(Items.GRASS_BLOCK, Blocks.GRASS_BLOCK, 1.0, new String[]{"grass"});
		addSoil(Items.MOSS_BLOCK, Blocks.MOSS_BLOCK, 1.0, new String[]{"moss"});
		addSoil(Items.MUD, Blocks.MUD, 1.0, new String[]{"mud"});

		addSoil(Items.END_STONE, Blocks.END_STONE, 1.0, new String[]{"end_stone"});

		addSoil(Items.NETHERRACK, Blocks.NETHERRACK, 0.9, new String[]{"netherrack"});
		addSoil(Items.NETHER_WART_BLOCK, Blocks.NETHER_WART_BLOCK, 1.0, new String[]{"netherrack"});

		addSoil(Items.CRIMSON_NYLIUM, Blocks.CRIMSON_NYLIUM, 1.0, new String[]{"crimson"});
		addSoil(Items.WARPED_NYLIUM, Blocks.WARPED_NYLIUM, 1.0, new String[]{"warped"});

		addSoil(Items.MYCELIUM, Blocks.MYCELIUM, 1.0, new String[]{"mycelium"});


		addSoil(Items.WATER_BUCKET, Fluids.WATER, 1.0, new String[]{"water"});
		addSoil(Items.LAVA_BUCKET, Fluids.LAVA, 1.0, new String[]{"lava"});
	}

	private JsonObject buildDefault(Item item, double tickModifier, String[] compatibleTags) {
		JsonObject root = new JsonObject();
		root.addProperty("type", "bonsaitrees3:soil");
		root.addProperty("mod", "minecraft");
		root.addProperty("tickModifier", tickModifier);

		JsonObject soilItem = new JsonObject();
		soilItem.addProperty("item", ForgeRegistries.ITEMS.getKey(item).toString());
		root.add("soil", soilItem);

		if(compatibleTags.length > 0) {
			JsonArray tags = new JsonArray();
			Arrays.stream(compatibleTags).forEach(s -> tags.add(s));
			root.add("compatibleSoilTags", tags);
		}

		return root;
	}

	private void addSoil(Item item, Fluid displayFluid, double tickModifier, String[] compatibleTags) {
		var root = buildDefault(item, tickModifier, compatibleTags);

		JsonObject displayObj = new JsonObject();
		displayObj.addProperty("fluid", ForgeRegistries.FLUIDS.getKey(displayFluid).toString());
		root.add("display", displayObj);

		var rLoc = ForgeRegistries.ITEMS.getKey(item);
		add("recipes/soil/" + rLoc.getNamespace() + "/" + rLoc.getPath(), root);
	}

	private void addSoil(Item item, Block displayBlock, double tickModifier, String[] compatibleTags) {
		var root = buildDefault(item, tickModifier, compatibleTags);

		JsonObject displayObj = new JsonObject();
		displayObj.addProperty("block", ForgeRegistries.BLOCKS.getKey(displayBlock).toString());
		root.add("display", displayObj);

		var rLoc = ForgeRegistries.ITEMS.getKey(item);
		add("recipes/soil/" + rLoc.getNamespace() + "/" + rLoc.getPath(), root);
	}


	@Override
	public String getName() {
		return "Bonsai Trees Soil Types";
	}
}