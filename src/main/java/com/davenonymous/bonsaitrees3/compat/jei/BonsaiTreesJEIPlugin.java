package com.davenonymous.bonsaitrees3.compat.jei;

import com.davenonymous.bonsaitrees3.BonsaiTrees3;
import com.davenonymous.bonsaitrees3.blocks.BonsaiPotContainer;
import com.davenonymous.bonsaitrees3.client.BonsaiPotScreen;
import com.davenonymous.bonsaitrees3.config.CommonConfig;
import com.davenonymous.libnonymous.helper.EnchantmentHelper;
import com.davenonymous.libnonymous.helper.Translatable;
import com.davenonymous.bonsaitrees3.registry.sapling.SaplingInfo;
import com.davenonymous.bonsaitrees3.setup.Registration;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
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
	
    public static final RecipeType<BonsaiUpgradeWrapper> UPGRADES = RecipeType.create(BonsaiTrees3.MODID, "upgrades", BonsaiUpgradeWrapper.class);
    public static final RecipeType<BonsaiRecipeWrapper> BONSAIS = RecipeType.create(BonsaiTrees3.MODID, "bonsais", BonsaiRecipeWrapper.class);

	public static final Translatable UPGRADE_TEXT_HOPPING = new Translatable(BonsaiTrees3.MODID, "jei.upgrade.hopper");
	public static final Translatable UPGRADE_TEXT_AUTOCUT = new Translatable(BonsaiTrees3.MODID, "jei.upgrade.autocut");
	public static final Translatable UPGRADE_TEXT_FORTUNE = new Translatable(BonsaiTrees3.MODID, "jei.upgrade.fortune");
	public static final Translatable UPGRADE_TEXT_EFFICIENCY = new Translatable(BonsaiTrees3.MODID, "jei.upgrade.efficiency");
	public static final Translatable UPGRADE_TEXT_SILKTOUCH = new Translatable(BonsaiTrees3.MODID, "jei.upgrade.silktouch");
	public static final Translatable UPGRADE_TEXT_BEES = new Translatable(BonsaiTrees3.MODID, "jei.upgrade.bees");
	public static final Translatable UPGRADE_TEXT_ENERGY = new Translatable(BonsaiTrees3.MODID, "jei.upgrade.energy");

	@Override
	public ResourceLocation getPluginUid() {
		return PLUGIN_ID;
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
        final IGuiHelper gui = registration.getJeiHelpers().getGuiHelper();
		BonsaiRecipeWrapper.tickTimer = gui.createTickTimer(360, 360, false);

		registration.addRecipeCategories(new BonsaiRecipeCategory(gui, BONSAIS));
		registration.addRecipeCategories(new BonsaiUpgradeCategory(gui, UPGRADES));
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(new ItemStack(Registration.BONSAI_POT.get()), BONSAIS);
		registration.addRecipeCatalyst(new ItemStack(Registration.BONSAI_POT.get()), UPGRADES);
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
		registration.addRecipeClickArea(BonsaiPotScreen.class, 29, 19, BonsaiPotContainer.WIDTH - (20 + 34 + 8 + 3 * 18), 18, BONSAIS, UPGRADES);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		if(saplings == null) {
			return;
		}

		BonsaiTrees3.LOGGER.info("Registering {} saplings", saplings.size());
		registration.addRecipes(BONSAIS, asRecipes(saplings, BonsaiRecipeWrapper::new));

		List<BonsaiUpgradeWrapper> upgradeRecipes = new ArrayList<>();

		if(CommonConfig.enableAutoCuttingUpgrade.get()) {
			List<ItemStack> axeItems = new ArrayList<>();
			for(var item : ForgeRegistries.ITEMS.getValues()) {
				var stack = new ItemStack(item);
				if(item.canPerformAction(stack, ToolActions.AXE_DIG)) {
					axeItems.add(stack);
				}
			}

			upgradeRecipes.add(new BonsaiUpgradeWrapper(UPGRADE_TEXT_AUTOCUT, axeItems));
		}

		if(CommonConfig.enableFortuneUpgrade.get()) {
			List<ItemStack> fortuneItems = new ArrayList<>(EnchantmentHelper.getEnchantmentBooks(Enchantments.BLOCK_FORTUNE));
			if(!fortuneItems.isEmpty()) {
				upgradeRecipes.add(new BonsaiUpgradeWrapper(UPGRADE_TEXT_FORTUNE, fortuneItems));
			}
		}

		if(CommonConfig.enableEfficiencyUpgrade.get()) {
			List<ItemStack> efficiencyItems = new ArrayList<>(EnchantmentHelper.getEnchantmentBooks(Enchantments.BLOCK_EFFICIENCY));
			if(!efficiencyItems.isEmpty()) {
				upgradeRecipes.add(new BonsaiUpgradeWrapper(UPGRADE_TEXT_EFFICIENCY, efficiencyItems));
			}
		}

		if(CommonConfig.enableForgeEnergyUpgrade.get()) {
			var batteryItems = ForgeRegistries.ITEMS.getValues().stream().map(ItemStack::new).filter(item -> {
				var cap = item.getCapability(ForgeCapabilities.ENERGY).resolve();
				if(cap.isEmpty()) {
					return false;
				}

				var storage = cap.get();
				if(storage.canExtract()) {
					return true;
				}

				if(storage.canReceive()) {
					storage.receiveEnergy(storage.getMaxEnergyStored(), false);
					if(storage.canExtract()) {
						return true;
					}
				}

				return false;
			}).toList();

			if(!batteryItems.isEmpty()) {
				upgradeRecipes.add(new BonsaiUpgradeWrapper(UPGRADE_TEXT_ENERGY, batteryItems));
			}
		}

		if(CommonConfig.enableHoppingUpgrade.get()) {
			upgradeRecipes.add(new BonsaiUpgradeWrapper(UPGRADE_TEXT_HOPPING, new ItemStack(Blocks.HOPPER)));
		}

		List<ItemStack> silkTouchItems = new ArrayList<>(EnchantmentHelper.getEnchantmentBooks(Enchantments.SILK_TOUCH));
		upgradeRecipes.add(new BonsaiUpgradeWrapper(UPGRADE_TEXT_SILKTOUCH, silkTouchItems));

		List<ItemStack> beeItems = new ArrayList<>();
		beeItems.add(new ItemStack(Blocks.BEEHIVE));
		beeItems.add(new ItemStack(Blocks.BEE_NEST));
		upgradeRecipes.add(new BonsaiUpgradeWrapper(UPGRADE_TEXT_BEES, beeItems));

		registration.addRecipes(UPGRADES, upgradeRecipes);
	}

	/*
		This has been copied from JustEnoughResources to make life in the lines above easier.
		https://github.com/way2muchnoise/JustEnoughResources/blob/d04c9a4a12dda05348a3397144cc885714ee89a4/src/main/java/jeresources/jei/JEIConfig.java#L92-L94

		See license here:
		https://github.com/way2muchnoise/JustEnoughResources/blob/d04c9a4a12/LICENSE.md
	 */
	private static <T, R> List<R> asRecipes(Collection<T> collection, Function<T, R> transformer) {
		return collection.stream().map(transformer).collect(Collectors.toList());
	}
}