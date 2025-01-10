package com.davenonymous.bonsaitrees.lib.gui.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import javax.annotation.Nullable;

public record TranslatableTooltipComponent(TranslatableContents message) implements SerializableTooltipComponent<TranslatableTooltipComponent> {
	public static final StreamCodec<FriendlyByteBuf, TranslatableTooltipComponent> CODEC = StreamCodec.composite(
		ByteBufCodecs.fromCodec(TranslatableContents.CODEC.codec()), TranslatableTooltipComponent::message,
		TranslatableTooltipComponent::new
	);

	public TranslatableTooltipComponent(String key) {
		this(new TranslatableContents(key, "", new Object[0]));
	}

	public TranslatableTooltipComponent(String key, @Nullable String fallback) {
		this(new TranslatableContents(key, fallback, new Object[0]));
	}

	public TranslatableTooltipComponent(String key, @Nullable String fallback, Object[] args) {
		this(new TranslatableContents(key, fallback, args));
	}

	@Override
	public int getHeight() {
		return 12;
	}

	@Override
	public int getWidth(Font font) {
		String text = I18n.get(message.getKey(), message.getArgs());
		return font.width(text);
	}

	@Override
	public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
		String text = I18n.get(message.getKey(), message.getArgs());
		guiGraphics.drawString(font, text, x, y, 0xFFFFFF);
	}

	@Override
	public StreamCodec<FriendlyByteBuf, TranslatableTooltipComponent> getCodec() {
		return CODEC;
	}
}