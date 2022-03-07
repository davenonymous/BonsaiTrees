package com.davenonymous.bonsaitrees3.datagen.server;

import com.davenonymous.bonsaitrees3.BonsaiTrees3;
import com.davenonymous.bonsaitrees3.setup.Registration;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class DatagenRecipes extends RecipeProvider {
	public DatagenRecipes(DataGenerator generator) {
		super(generator);
	}

	@Override
	protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
		ShapedRecipeBuilder.shaped(Registration.BONSAI_POT.get())
				.pattern("b b")
				.pattern("bbb")
				.define('b', Tags.Items.INGOTS_BRICK)
				.group(BonsaiTrees3.MODID)
				.unlockedBy("bonsaipot", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BRICK)).save(consumer);
	}
}