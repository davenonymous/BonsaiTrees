package com.davenonymous.bonsaitrees.mixins;

import com.davenonymous.bonsaitrees.setup.BonsaiHooks;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class MixinJeiEnchantmentFix {
	@Shadow
	Component description;

	@Inject(at = @At("HEAD"), method = "canEnchant(Lnet/minecraft/world/item/ItemStack;)Z", cancellable = true)
	private void canEnchant(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		BonsaiHooks.canEnchantHook(stack, description, cir);
	}


}
