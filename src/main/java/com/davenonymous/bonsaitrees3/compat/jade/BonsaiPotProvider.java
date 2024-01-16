package com.davenonymous.bonsaitrees3.compat.jade;

import com.davenonymous.bonsaitrees3.BonsaiTrees3;
import com.davenonymous.bonsaitrees3.blocks.BonsaiPotBlockEntity;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.api.ui.IProgressStyle;

public class BonsaiPotProvider implements IBlockComponentProvider {
	public static final ResourceLocation ID = new ResourceLocation(BonsaiTrees3.MODID, "bonsaipot");
	public static final String LANG_GROWING = "jade.bonsaitrees3.bonsaipot.growing";

	@Override
	public ResourceLocation getUid() {
		return ID;
	}

	@Override
	public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
		if (accessor.getBlockEntity() instanceof BonsaiPotBlockEntity bonsaiPot) {
			IElementHelper helper = tooltip.getElementHelper();
			IElement progress = getProgress(bonsaiPot, helper);
			
			tooltip.add(progress);
		}
	}
	
	private static IElement getProgress(BonsaiPotBlockEntity bonsaiPot, IElementHelper helper) {
		Component text = Component.translatable(LANG_GROWING, Math.round(bonsaiPot.getProgress() * 100));
		IProgressStyle progressStyle = helper.progressStyle().color(0xFF00AA00);
		return helper.progress((float)bonsaiPot.getProgress(), text, progressStyle, BoxStyle.DEFAULT, false);
	}
}
