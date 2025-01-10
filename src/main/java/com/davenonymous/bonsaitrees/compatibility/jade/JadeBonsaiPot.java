package com.davenonymous.bonsaitrees.compatibility.jade;

import com.davenonymous.bonsaitrees.blocks.BonsaiPotBlockEntity;
import com.davenonymous.bonsaitrees.blocks.BonsaiPotBlockInventories;
import com.davenonymous.bonsaitrees.setup.ModBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;
import snownee.jade.addon.core.ModNameProvider;
import snownee.jade.addon.harvest.HarvestToolProvider;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;

public class JadeBonsaiPot implements IBlockComponentProvider {
	@Override
	public @Nullable IElement getIcon(BlockAccessor accessor, IPluginConfig config, IElement currentIcon) {
		return IElementHelper.get().spacer(1, 1);
	}

	@Override
	public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
		BonsaiPotBlockEntity pot = (BonsaiPotBlockEntity) blockAccessor.getBlockEntity();
		BonsaiPotBlockInventories inventories = pot.inventories;

		iTooltip.clear();

		StringBuilder blockName = new StringBuilder("block.bonsaitrees4.bonsaipot");
		if(blockAccessor.getBlock() == ModBlocks.BONSAI_POT_SMALL.get()) {
			blockName.append("_small");
		}

		ChatFormatting titleColor = ChatFormatting.WHITE;
		if(!inventories.enchantments.isEmpty()) {
			blockName.append(".enchanted");
			titleColor = ChatFormatting.AQUA;
		}

		var title = IElementHelper.get().text(Component.translatable(blockName.toString()).withStyle(titleColor));
		iTooltip.append(title);
		HarvestToolProvider.INSTANCE.appendTooltip(iTooltip, blockAccessor, iPluginConfig);

		appendItemLine(iTooltip, inventories.getSaplingStack());
		appendItemLine(iTooltip, inventories.getSoilStack());
		appendItemLine(iTooltip, inventories.getToolStack());

		appendVerticalSpacer(iTooltip, 1);

		ModNameProvider.getBlock().appendTooltip(iTooltip, blockAccessor, iPluginConfig);
	}

	private void appendVerticalSpacer(ITooltip iTooltip, int height) {
		iTooltip.add(IElementHelper.get().spacer(0, height));
	}

	private void appendItemLine(ITooltip iTooltip, ItemStack stack) {
		if(stack.isEmpty()) {
			return;
		}

		iTooltip.add(IElementHelper.get()
			.item(stack)
			.size(new Vec2(16, 16))
		);

		iTooltip.append(
			IElementHelper.get().text(stack.getHoverName()).translate(new Vec2(5, 6))
		);
		iTooltip.append(IElementHelper.get().spacer(5, 0));
	}

	@Override
	public int getDefaultPriority() {
		return 90000;
	}

	@Override
	public ResourceLocation getUid() {
		return JadePlugin.BONSAI_POT;
	}
}
