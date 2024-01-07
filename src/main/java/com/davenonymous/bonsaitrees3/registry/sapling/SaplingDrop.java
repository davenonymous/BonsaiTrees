package com.davenonymous.bonsaitrees3.registry.sapling;

import com.davenonymous.bonsaitrees3.config.CommonConfig;
import com.davenonymous.libnonymous.json.MCJsonUtils;
import com.davenonymous.libnonymous.serialization.JsonHelpers;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class SaplingDrop {
	public ItemStack resultStack;
	public float chance;
	public int rolls;
	public boolean requiresSilkTouch;
	public boolean requiresBees;
	public Ingredient requiredUpgrades;

	public SaplingDrop(Item item, float chance, int rolls, boolean requiresSilkTouch, boolean requiresBees, Ingredient requiredUpgrades) {
		this.resultStack = new ItemStack(item);
		this.chance = chance;
		this.rolls = rolls;
		this.requiresSilkTouch = requiresSilkTouch;
		this.requiresBees = requiresBees;
		this.requiredUpgrades = requiredUpgrades;
	}

	public SaplingDrop(JsonObject json) {
		this.resultStack = new ItemStack(MCJsonUtils.getItem(json.getAsJsonObject("result"), "item"), 1);
		this.chance = json.get("chance").getAsFloat();
		this.rolls = json.get("rolls").getAsInt();
		this.requiresSilkTouch = json.has("requiresSilkTouch") && json.get("requiresSilkTouch").getAsBoolean();
		this.requiresBees = json.has("requiresBees") && json.get("requiresBees").getAsBoolean();
		if(json.has("requiredUpgrades")) {
			this.requiredUpgrades = JsonHelpers.getIngredientFromArrayOrSingle(json.get("requiredUpgrades"));
		} else {
			this.requiredUpgrades = Ingredient.EMPTY;
		}
	}

	public SaplingDrop(FriendlyByteBuf buffer) {
		this.resultStack = buffer.readItem();
		this.chance = buffer.readFloat();
		this.rolls = buffer.readInt();
		this.requiresSilkTouch = buffer.readBoolean();
		this.requiresBees = buffer.readBoolean();
		this.requiredUpgrades = Ingredient.fromNetwork(buffer);
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeItemStack(this.resultStack, true);
		buffer.writeFloat(this.chance);
		buffer.writeInt(this.rolls);
		buffer.writeBoolean(this.requiresSilkTouch);
		buffer.writeBoolean(this.requiresBees);
		this.requiredUpgrades.toNetwork(buffer);
	}

	public ItemStack getRandomDrop(RandomSource rand, int fortuneLevel) {
		var extraRolls = CommonConfig.extraRollsPerFortuneLevel.get();
		var extraChance = CommonConfig.extraChancePerFortuneLevel.get();

		int count = 0;
		for(int roll = 0; roll < this.rolls + (fortuneLevel * extraRolls); roll++) {
			if(rand.nextFloat() <= this.chance + (fortuneLevel * extraChance)) {
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
		return "SaplingDrop{" + "stack=" + resultStack + ", chance=" + chance + ", rolls=" + rolls + ", silky=" + requiresSilkTouch + ", pollinated=" + requiresBees + "}";
	}
}