package com.davenonymous.bonsaitrees2.registry.sapling;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;

import java.util.Random;

public class SaplingDrop {
    public ItemStack resultStack;
    public float chance;
    public int rolls;

    public SaplingDrop(ItemStack resultStack, float chance, int rolls) {
        this.resultStack = resultStack;
        this.chance = chance;
        this.rolls = rolls;
    }

    public SaplingDrop(JsonObject json) {
        this.resultStack = new ItemStack(JSONUtils.getItem(json.getAsJsonObject("result"), "item"), 1);
        this.chance = json.get("chance").getAsFloat();
        this.rolls = json.get("rolls").getAsInt();
    }

    public SaplingDrop(PacketBuffer buffer) {
        this.resultStack = buffer.readItemStack();
        this.chance = buffer.readFloat();
        this.rolls = buffer.readInt();
    }

    public void write(PacketBuffer buffer) {
        buffer.writeItemStack(this.resultStack);
        buffer.writeFloat(this.chance);
        buffer.writeInt(this.rolls);
    }

    public ItemStack getRandomDrop(Random rand) {
        int count = 0;
        for(int roll = 0; roll < this.rolls; roll++) {
            if(rand.nextFloat() <= this.chance) {
                count++;
            }
        }

        if(count <= 0) {
            return ItemStack.EMPTY;
        }
        if(count > 64) {
            count = 64;
        }

        ItemStack result = this.resultStack.copy();
        result.setCount(count);
        return result;
    }

    @Override
    public String toString() {
        return "SaplingDrop{" +
                "stack=" + resultStack +
                ", chance=" + chance +
                ", rolls=" + rolls +
                '}';
    }
}
