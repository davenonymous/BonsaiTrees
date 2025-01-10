package com.davenonymous.bonsaitrees.lib;

import com.davenonymous.bonsaitrees.BonsaiTrees;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class SimpleRecipeBuilder implements RecipeBuilder {
	protected final ItemStack result;
	protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
	protected String group;


	protected SimpleRecipeBuilder(ItemStack result) {
		this.result = result;
		this.group = BonsaiTrees.MODID;
	}

	@Override
	public RecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
		this.criteria.put(name, criterion);
		return this;
	}

	@Override
	public RecipeBuilder group(@Nullable String group) {
		this.group = group;
		return this;
	}

	@Override
	public Item getResult() {
		return this.result.getItem();
	}

	protected AdvancementHolder getAdvancementOutput(RecipeOutput output, ResourceLocation id) {
		var builder = output.advancement()
			.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
			.rewards(AdvancementRewards.Builder.recipe(id))
			.requirements(AdvancementRequirements.Strategy.OR);
		this.criteria.forEach(builder::addCriterion);
		return builder.build(id.withPrefix("recipes/"));
	}
}