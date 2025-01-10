package com.davenonymous.bonsaitrees.setup;

import com.davenonymous.bonsaitrees.blocks.BonsaiPotBlockItem;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;
import java.util.stream.Collectors;

public class BonsaiHooks {
	public static final Set<String> validEnchantments = BonsaiPotBlockItem.validEnchantments.stream().map(enchantmentResourceKey -> {
		String id = enchantmentResourceKey.location().getPath();
		return "enchantment.minecraft." + id;
	}).collect(Collectors.toSet());

	public static void canEnchantHook(ItemStack stack, Component description, CallbackInfoReturnable<Boolean> cir) {
		if(!stack.is(ModItems.BONSAI_POT_ITEM.get()) && !stack.is(ModItems.BONSAI_POT_SMALL_ITEM.get())) {
			return;
		}

		if(description.getContents() instanceof TranslatableContents content) {
			String enchantmentKey = content.getKey();
			cir.setReturnValue(validEnchantments.contains(enchantmentKey));
		}
	}
}
