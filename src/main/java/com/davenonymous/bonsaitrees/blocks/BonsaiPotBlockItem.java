package com.davenonymous.bonsaitrees.blocks;

import com.davenonymous.bonsaitrees.datacomponents.CamouflageDataComponent;
import com.davenonymous.bonsaitrees.datacomponents.RedstoneModeDataComponent;
import com.davenonymous.bonsaitrees.lib.gui.tooltip.ItemStackTooltipComponent;
import com.davenonymous.bonsaitrees.lib.gui.tooltip.RedstoneModeTooltipComponent;
import com.davenonymous.bonsaitrees.lib.gui.tooltip.VBoxTooltipComponent;
import com.davenonymous.bonsaitrees.setup.ModDataComponents;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;

import java.util.Optional;
import java.util.Set;

public class BonsaiPotBlockItem extends BlockItem {
	public static final Set<ResourceKey<Enchantment>> validEnchantments = Set.of(
		Enchantments.FORTUNE,
		Enchantments.SILK_TOUCH,
		Enchantments.UNBREAKING
	);

	public BonsaiPotBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public Component getName(ItemStack stack) {
		if(stack.has(ModDataComponents.CAMOUFLAGE_COMPONENT)) {
			return Component
				.translatable("bonsaitrees4.tooltip.painted")
				.append(Component.literal(" "))
				.append(super.getName(stack));
		}
		return super.getName(stack);
	}

	@Override
	public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
		return validEnchantments.contains(enchantment.getKey());
	}

	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
		return true;
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return true;
	}

	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
		VBoxTooltipComponent vbox = new VBoxTooltipComponent();
		if(stack.has(ModDataComponents.SAPLING_COMPONENT)) {
			ResourceLocation saplingId = stack.get(ModDataComponents.SAPLING_COMPONENT).sapling();
			Item saplingItem = BuiltInRegistries.ITEM.get(saplingId);
			vbox.add(new ItemStackTooltipComponent(new ItemStack(saplingItem)));
		}

		if(stack.has(ModDataComponents.SOIL_COMPONENT)) {
			vbox.add(new ItemStackTooltipComponent(stack.get(ModDataComponents.SOIL_COMPONENT).soil()));
		}

		if(stack.has(ModDataComponents.TOOL_COMPONENT)) {
			ItemStack toolStack = stack.get(ModDataComponents.TOOL_COMPONENT).tool();
			vbox.add(new ItemStackTooltipComponent(toolStack));
		}

		if(stack.has(ModDataComponents.CAMOUFLAGE_COMPONENT)) {
			CamouflageDataComponent camouData = stack.get(ModDataComponents.CAMOUFLAGE_COMPONENT);
			ResourceLocation blockId = camouData.camouflage();
			Block camouBlock = BuiltInRegistries.BLOCK.get(blockId);

			vbox.add(new ItemStackTooltipComponent(new ItemStack(camouBlock)));
		}

		if(stack.has(ModDataComponents.REDSTONEMODE_COMPONENT)) {
			RedstoneModeDataComponent redstoneModeData = stack.get(ModDataComponents.REDSTONEMODE_COMPONENT);
			vbox.add(new RedstoneModeTooltipComponent(redstoneModeData.mode()));
		}


		return Optional.of(vbox);
	}
}
