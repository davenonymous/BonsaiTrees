package com.davenonymous.bonsaitrees.lib.gui.tooltip;

import com.davenonymous.bonsaitrees.lib.gui.GUIHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record WrappedStringTooltipComponent(String message, int color, int maxWidth) implements SerializableTooltipComponent<WrappedStringTooltipComponent> {

	public static final StreamCodec<FriendlyByteBuf, WrappedStringTooltipComponent> CODEC = StreamCodec.composite(
		ByteBufCodecs.STRING_UTF8, WrappedStringTooltipComponent::message,
		ByteBufCodecs.INT, WrappedStringTooltipComponent::color,
		ByteBufCodecs.INT, WrappedStringTooltipComponent::maxWidth,
		WrappedStringTooltipComponent::new
	);

	public static WrappedStringTooltipComponent white(String message) {
		return white(message, Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2);
	}

	public static WrappedStringTooltipComponent white(String message, int maxWidth) {
		return new WrappedStringTooltipComponent(message, ChatFormatting.WHITE.getColor(), maxWidth);
	}

	public static WrappedStringTooltipComponent gray(String message) {
		return gray(message, Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2);
	}

	public static WrappedStringTooltipComponent gray(String message, int maxWidth) {
		return new WrappedStringTooltipComponent(message, ChatFormatting.GRAY.getColor(), maxWidth);
	}

	public static WrappedStringTooltipComponent yellow(String message) {
		return yellow(message, Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2);
	}

	public static WrappedStringTooltipComponent yellow(String message, int maxWidth) {
		return new WrappedStringTooltipComponent(message, ChatFormatting.YELLOW.getColor(), maxWidth);
	}

	@Override
	public int getHeight() {
		return GUIHelper.wordWrapHeight(Minecraft.getInstance().font, FormattedText.of(message), maxWidth);
	}

	@Override
	public int getWidth(Font font) {
		return GUIHelper.longestWrappedLine(font, FormattedText.of(message), maxWidth);
	}

	@Override
	public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
		GUIHelper.drawWordWrap(guiGraphics, font, FormattedText.of(message), x, y, maxWidth, color);
	}

	@Override
	public StreamCodec<FriendlyByteBuf, WrappedStringTooltipComponent> getCodec() {
		return CODEC;
	}
}
