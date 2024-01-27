package com.davenonymous.bonsaitrees3.compat.jade;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.davenonymous.bonsaitrees3.BonsaiTrees3;
import com.davenonymous.bonsaitrees3.blocks.BonsaiPotBlockEntity;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.api.ui.IProgressStyle;
import snownee.jade.api.view.IServerExtensionProvider;
import snownee.jade.api.view.ViewGroup;

public class BonsaiPotProvider 
implements IBlockComponentProvider, IServerExtensionProvider<BonsaiPotBlockEntity, ItemStack>/*, IClientExtensionProvider<ItemStack, ItemView>*/ {
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
			ItemStack sapling = bonsaiPot.getSapling();
			ItemStack soil = bonsaiPot.getSoilItemStacks().getStackInSlot(0);
			
			IElement progress = getProgress(bonsaiPot, helper);
			
			if (bonsaiPot.hasSapling()) {
				tooltip.add(helper.item(sapling, 0.5f).size(new Vec2(10, 10)).translate(new Vec2(-1, -1)));
				tooltip.append(sapling.getHoverName());
			}
			
			if (bonsaiPot.hasSoil()) {
				tooltip.add(helper.item(soil, 0.5f).size(new Vec2(10, 10)).translate(new Vec2(-1, -1)));
				tooltip.append(soil.getHoverName());
			}

			if (bonsaiPot.hasSoil() && bonsaiPot.hasSapling())
				tooltip.add(progress);
		}
	}

	@Override
	public @Nullable List<ViewGroup<ItemStack>> getGroups(ServerPlayer player, ServerLevel level, BonsaiPotBlockEntity bonsaiPot, boolean arg3) {
		return null;
	}
	
	private static IElement getProgress(BonsaiPotBlockEntity bonsaiPot, IElementHelper helper) {
		Component text = Component.translatable(LANG_GROWING, Math.round(bonsaiPot.getProgress() * 100));
		IProgressStyle progressStyle = helper.progressStyle().color(0xFF00AA00);
		return helper.progress((float)bonsaiPot.getProgress(), text, progressStyle, BoxStyle.DEFAULT, false);
	}
}
