package com.davenonymous.bonsaitrees2.loot.function;

import com.davenonymous.bonsaitrees2.BonsaiTrees2;
import com.davenonymous.libnonymous.misc.ColorProperty;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;

public class CopyColor extends LootFunction {
    protected CopyColor(ILootCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected ItemStack doApply(ItemStack stack, LootContext context) {
        BlockState state = context.get(LootParameters.BLOCK_STATE);
        if(state.hasProperty(ColorProperty.COLOR)) {
            int color = state.get(ColorProperty.COLOR);
            stack.getOrCreateTag().putInt("bonsaitrees2:color", color);
        }
        return stack;
    }

    @Override
    public LootFunctionType getFunctionType() {
        return null;
    }

    public static class Serializer extends LootFunction.Serializer<CopyColor> {

        public Serializer() {
            super();//new ResourceLocation(BonsaiTrees2.MODID, "copy_color"), CopyColor.class);
        }

        @Override
        public CopyColor deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn) {
            return new CopyColor(conditionsIn);
        }
    }
}
