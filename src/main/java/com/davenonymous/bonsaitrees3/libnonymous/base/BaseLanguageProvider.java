package com.davenonymous.bonsaitrees3.libnonymous.base;

import com.davenonymous.bonsaitrees3.libnonymous.helper.Translatable;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.data.LanguageProvider;

public abstract class BaseLanguageProvider extends LanguageProvider {
	private final String modid;

	public BaseLanguageProvider(DataGenerator gen, String modid, String locale) {
		super(gen, modid, locale);
		this.modid = modid;
	}

	public void add(MenuType container, String translation) {
		add(getContainerLanguageKey(container), translation);
	}

	public static String getContainerLanguageKey(MenuType container) {
		return "container." + container.getRegistryName().getNamespace() + "." + container.getRegistryName().getPath();
	}


	public void add(Translatable label, String translation) {
		add(getTranslatableLanguageKey(label), translation);
	}

	public static String getTranslatableLanguageKey(Translatable label) {
		return "gui." + label.getNamespace() + ".label." + label.getPath();
	}
}
