package com.davenonymous.bonsaitrees.compatibility.jei;

import com.davenonymous.bonsaitrees.BonsaiTrees;
import com.davenonymous.bonsaitrees.blocks.BonsaiPotScreen;
import com.davenonymous.bonsaitrees.setup.ModBlocks;
import com.davenonymous.bonsaitrees.setup.cache.SoilTypeCache;
import com.davenonymous.bonsaitrees.setup.config.ClientConfig;
import com.davenonymous.bonsaitrees.setup.config.DebugConfig;
import com.davenonymous.bonsaitrees.setup.data.SoilType;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class BonsaiJEIPlugin implements IModPlugin {
	private static final ResourceLocation PLUGIN_ID = BonsaiTrees.resource("jei");
	public static final RecipeType<BonsaiRecipe> BONSAIS = RecipeType.create(BonsaiTrees.MODID, "bonsais", BonsaiRecipe.class);
	public static final RecipeType<SoilType> SOIL_TYPES = RecipeType.create(BonsaiTrees.MODID, "soils", SoilType.class);

	@Override
	public ResourceLocation getPluginUid() {
		return PLUGIN_ID;
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		registration.addRecipeCategories(new BonsaiCategory(registration.getJeiHelpers()));
		registration.addRecipeCategories(new SoilTypeCategory(registration.getJeiHelpers()));
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		var soilTypeStream = SoilTypeCache.RESOURCE_BY_SOIL.keySet().stream();
		if(!DebugConfig.showUnusedSoilRecipesInJEI) {
			soilTypeStream = soilTypeStream.filter(SoilType::hasSoils).filter(SoilType::hasBonsais);
		}
		registration.addRecipes(SOIL_TYPES, soilTypeStream.toList());
	}

	@Override
	public void registerAdvanced(IAdvancedRegistration registration) {
		registration.addTypedRecipeManagerPlugin(BONSAIS, new BonsaiRecipeManager());
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(ModBlocks.BONSAI_POT.get(), BONSAIS);
		registration.addRecipeCatalyst(ModBlocks.BONSAI_POT_SMALL.get(), BONSAIS);
		registration.addRecipeCatalyst(ModBlocks.BONSAI_POT.get(), SOIL_TYPES);
		registration.addRecipeCatalyst(ModBlocks.BONSAI_POT_SMALL.get(), SOIL_TYPES);
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
		registration.addRecipeClickArea(BonsaiPotScreen.class, 86, 40, 22, 15, BONSAIS, SOIL_TYPES);
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration) {
		if(ClientConfig.showJEISubtypes) {
			registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModBlocks.BONSAI_POT.get().asItem(), new BonsaiSubtypeInterpreter());
		}
	}
}
