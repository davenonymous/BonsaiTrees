package com.davenonymous.bonsaitrees3.datagen.client;

import com.davenonymous.bonsaitrees3.BonsaiTrees3;
import com.davenonymous.bonsaitrees3.client.BonsaiPotScreen;
import com.davenonymous.bonsaitrees3.compat.jei.BonsaiTreesJEIPlugin;
import com.davenonymous.bonsaitrees3.setup.Registration;
import com.davenonymous.libnonymous.base.BaseLanguageProvider;
import net.minecraft.data.DataGenerator;

public class DatagenTranslations extends BaseLanguageProvider {
	public DatagenTranslations(DataGenerator gen, String locale) {
		super(gen, BonsaiTrees3.MODID, locale);
	}

	@Override
	protected void addTranslations() {

		add(Registration.BONSAI_POT.get(), "Bonsai Pot");
		add(Registration.BONSAI_POT_CONTAINER.get(), "Bonsai Pot");
		add(BonsaiPotScreen.CUT_BUTTON_TOOLTIP_OK, "Click to cut the tree");
		add(BonsaiPotScreen.CUT_BUTTON_TOOLTIP_WAIT, "The tree needs to fully grow before you can cut it");

		add("jei.bonsaitrees3.recipes.title", "Growing in Bonsai Pots");
		add("jei.bonsaitrees3.upgrades.title", "Upgrading Bonsai Pots");

		add("jei.bonsaitrees3.requiresSilkTouch", "Requires Silk Touch upgrade");
		add("jei.bonsaitrees3.requiresBees", "Requires a Bee Hive upgrade");
		add("jei.bonsaitrees3.requiresUpgrade", "Requires one of these upgrades:");

		add("jei.bonsaitrees3.growtime", "Time to grow: %s");
		add("jei.bonsaitrees3.soiltime", "Changes growtime to: %s");
		add("jei.bonsaitrees3.chance", "Chance: %d%%");
		
		add("config.jade.plugin_bonsaitrees3.bonsaipot", "Bonsai Pot");
		add("jade.bonsaitrees3.bonsaipot.growing", "Growing: %d%%");

		add(BonsaiTreesJEIPlugin.UPGRADE_TEXT_AUTOCUT, "Automatically cut trees");
		add(BonsaiTreesJEIPlugin.UPGRADE_TEXT_FORTUNE, "Increase drop rolls and chances");
		add(BonsaiTreesJEIPlugin.UPGRADE_TEXT_EFFICIENCY, "Reduce sapling grow time");
		add(BonsaiTreesJEIPlugin.UPGRADE_TEXT_HOPPING, "Export drops to the block below");
		add(BonsaiTreesJEIPlugin.UPGRADE_TEXT_SILKTOUCH, "Be careful and get leaf drops");
		add(BonsaiTreesJEIPlugin.UPGRADE_TEXT_BEES, "Pollinate and get fruit drops");
		add(BonsaiTreesJEIPlugin.UPGRADE_TEXT_ENERGY, "Insert batteries to boost growth rate");
	}
}