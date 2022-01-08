package com.davenonymous.bonsaitrees3.compat.jei;

import com.davenonymous.bonsaitrees3.BonsaiTrees3;
import com.davenonymous.bonsaitrees3.blocks.BonsaiPotContainer;
import com.davenonymous.bonsaitrees3.client.BonsaiPotScreen;
import com.davenonymous.bonsaitrees3.libnonymous.helper.EnchantmentHelper;
import com.davenonymous.bonsaitrees3.libnonymous.helper.Translatable;
import com.davenonymous.bonsaitrees3.registry.sapling.SaplingInfo;
import com.davenonymous.bonsaitrees3.setup.Registration;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@JeiPlugin
public class BonsaiTreesJEIPlugin implements IModPlugin {
	public static List<SaplingInfo> saplings;
	private static final ResourceLocation PLUGIN_ID = new ResourceLocation(BonsaiTrees3.MODID, "jei");

	public static final Translatable UPGRADE_TEXT_HOPPING = new Translatable(BonsaiTrees3.MODID, "jei.upgrade.hopper");
	public static final Translatable UPGRADE_TEXT_AUTOCUT = new Translatable(BonsaiTrees3.MODID, "jei.upgrade.autocut");
	public static final Translatable UPGRADE_TEXT_FORTUNE = new Translatable(BonsaiTrees3.MODID, "jei.upgrade.fortune");
	public static final Translatable UPGRADE_TEXT_EFFICIENCY = new Translatable(BonsaiTrees3.MODID, "jei.upgrade.efficiency");
	public static final Translatable UPGRADE_TEXT_SILKTOUCH = new Translatable(BonsaiTrees3.MODID, "jei.upgrade.silktouch");

	@Override
	public ResourceLocation getPluginUid() {
		return PLUGIN_ID;
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(new ItemStack(Registration.BONSAI_POT.get()), BonsaiRecipeCategory.ID);
		registration.addRecipeCatalyst(new ItemStack(Registration.BONSAI_POT.get()), BonsaiUpgradeCategory.ID);
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
		registration.addRecipeClickArea(BonsaiPotScreen.class, 29, 19, BonsaiPotContainer.WIDTH - (20 + 34 + 8 + 3 * 18), 18, BonsaiRecipeCategory.ID, BonsaiUpgradeCategory.ID);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		if(saplings == null) {
			return;
		}

		BonsaiTrees3.LOGGER.info("Registering {} saplings", saplings.size());
		registration.addRecipes(asRecipes(saplings, BonsaiRecipeWrapper::new), BonsaiRecipeCategory.ID);

		List<ItemStack> axeItems = new ArrayList<>();
		for(var item : ForgeRegistries.ITEMS.getValues()) {
			var stack = new ItemStack(item);
			if(item.canPerformAction(stack, ToolActions.AXE_DIG)) {
				axeItems.add(stack);
			}
		}

		List<ItemStack> fortuneItems = new ArrayList<>();
		fortuneItems.addAll(EnchantmentHelper.getEnchantmentBooks(Enchantments.BLOCK_FORTUNE));
		fortuneItems.addAll(axeItems.stream().map(ItemStack::copy).peek(stack -> stack.enchant(Enchantments.BLOCK_FORTUNE, 1)).toList());

		List<ItemStack> efficiencyItems = new ArrayList<>();
		efficiencyItems.addAll(EnchantmentHelper.getEnchantmentBooks(Enchantments.BLOCK_EFFICIENCY));
		efficiencyItems.addAll(axeItems.stream().map(ItemStack::copy).peek(stack -> stack.enchant(Enchantments.BLOCK_EFFICIENCY, 1)).toList());

		List<ItemStack> silkTouchItems = new ArrayList<>();
		silkTouchItems.addAll(EnchantmentHelper.getEnchantmentBooks(Enchantments.SILK_TOUCH));
		silkTouchItems.addAll(axeItems.stream().map(ItemStack::copy).peek(stack -> stack.enchant(Enchantments.SILK_TOUCH, 1)).toList());

		var upgradeRecipes = List.of(new BonsaiUpgradeWrapper(UPGRADE_TEXT_HOPPING, new ItemStack(Blocks.HOPPER)), new BonsaiUpgradeWrapper(UPGRADE_TEXT_AUTOCUT, axeItems), new BonsaiUpgradeWrapper(UPGRADE_TEXT_FORTUNE, fortuneItems), new BonsaiUpgradeWrapper(UPGRADE_TEXT_EFFICIENCY, efficiencyItems), new BonsaiUpgradeWrapper(UPGRADE_TEXT_SILKTOUCH, silkTouchItems));
		registration.addRecipes(upgradeRecipes, BonsaiUpgradeCategory.ID);
	}

	/*
		This has been copied from JustEnoughResources to make life in the lines above easier.
		https://github.com/way2muchnoise/JustEnoughResources/blob/d04c9a4a12dda05348a3397144cc885714ee89a4/src/main/java/jeresources/jei/JEIConfig.java#L92-L94

		See license here:
		https://github.com/way2muchnoise/JustEnoughResources/blob/d04c9a4a12/LICENSE.md
	 */
	private static <T, R> Collection<R> asRecipes(Collection<T> collection, Function<T, R> transformer) {
		return collection.stream().map(transformer).collect(Collectors.toList());
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		BonsaiRecipeWrapper.tickTimer = registration.getJeiHelpers().getGuiHelper().createTickTimer(360, 360, false);
		registration.addRecipeCategories(new BonsaiRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
		registration.addRecipeCategories(new BonsaiUpgradeCategory(registration.getJeiHelpers().getGuiHelper()));
	}
}