package com.davenonymous.bonsaitrees3.libnonymous.utils;

import net.minecraft.network.chat.TextComponent;

public class ComponentUtils {
	public static TextComponent format(String fmt, Object... data) {
		return new TextComponent(String.format(fmt, data));
	}
}