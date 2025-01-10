package com.davenonymous.bonsaitrees.mixins;

import com.davenonymous.bonsaitrees.setup.ModBlocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LootItemBlockStatePropertyCondition.class)
public class MixinLootItemBlockStatePropertyCondition {


	@Inject(at = @At("HEAD"), method = "test(Lnet/minecraft/world/level/storage/loot/LootContext;)Z", cancellable = true)
	private void test(LootContext context, CallbackInfoReturnable<Boolean> cir) {
		BlockState blockstate = context.getParamOrNull(LootContextParams.BLOCK_STATE);
		if(blockstate != null && blockstate.is(ModBlocks.BONSAI_POT.get())) {
			cir.setReturnValue(true);
			cir.cancel();
		}
	}
}