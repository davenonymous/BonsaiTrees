package com.davenonymous.bonsaitrees3.libnonymous.helper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantmentHelper {
	private Map<ResourceLocation, Integer> enchantments;

	private void buildEnchantmentMap(ListTag enchantments) {
		this.enchantments = new HashMap<>();
		if(enchantments != null) {
			for(var enchantment : enchantments) {
				if(enchantment instanceof CompoundTag enchantmentTag && enchantmentTag.contains("id")) {
					var id = new ResourceLocation(enchantmentTag.getString("id"));
					var levelValue = enchantmentTag.getShort("lvl");
					this.enchantments.put(id, (int) levelValue);
				}
			}
		}
	}

	public EnchantmentHelper(ListTag enchantments) {
		buildEnchantmentMap(enchantments);
	}

	public EnchantmentHelper(ItemStack stack) {
		if(stack.getItem() instanceof EnchantedBookItem) {
			buildEnchantmentMap(EnchantedBookItem.getEnchantments(stack));
			return;
		}

		if(stack.isEnchanted()) {
			buildEnchantmentMap(stack.getEnchantmentTags());
			return;
		}

		buildEnchantmentMap(null);
	}

	public boolean hasAny(Enchantment... enchantments) {
		for(var enchantment : enchantments) {
			if(has(enchantment)) {
				return true;
			}
		}

		return false;
	}

	public boolean has(Enchantment enchantment) {
		return this.has(enchantment.getRegistryName());
	}

	public boolean has(ResourceLocation enchantment) {
		return enchantments.containsKey(enchantment);
	}

	public int getLevel(Enchantment enchantment) {
		return this.getLevel(enchantment.getRegistryName());
	}

	public int getLevel(ResourceLocation registryName) {
		return has(registryName) ? enchantments.get(registryName) : 0;
	}


	public static List<ItemStack> getEnchantmentBooks(Enchantment enchantment) {
		List<ItemStack> result = new ArrayList<>();
		for(int level = enchantment.getMinLevel(); level <= enchantment.getMaxLevel(); level++) {
			result.add(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, level)));
		}
		return result;
	}

	public static boolean hasAny(@Nonnull ListTag enchantments, @Nonnull Enchantment... wanted) {
		Map<ResourceLocation, Boolean> enchantmentHash = new HashMap<>();
		for(var enchantment : enchantments) {
			if(enchantment instanceof CompoundTag enchantmentTag && enchantmentTag.contains("id")) {
				var id = new ResourceLocation(enchantmentTag.getString("id"));
				enchantmentHash.put(id, true);
			}
		}

		for(var enchantment : wanted) {
			if(enchantmentHash.containsKey(enchantment.getRegistryName())) {
				return true;
			}
		}

		return false;
	}

	public static boolean has(@Nonnull ListTag enchantments, @Nonnull Enchantment enchantment) {
		return has(enchantments, enchantment.getRegistryName());
	}

	public static boolean has(@Nonnull ListTag enchantments, @Nonnull ResourceLocation wanted) {
		for(var enchantment : enchantments) {
			if(enchantment instanceof CompoundTag enchantmentTag) {
				var id = enchantmentTag.getString("id");
				if(id.equals(wanted.toString())) {
					return true;
				}
			}
		}

		return false;
	}


	public static int getLevel(@Nonnull ListTag enchantments, @Nonnull Enchantment enchantment) {
		return getLevel(enchantments, enchantment.getRegistryName());
	}

	public static int getLevel(@Nonnull ListTag enchantments, @Nonnull ResourceLocation enchantmentId) {
		int level = 0;
		for(var enchantment : enchantments) {
			if(enchantment instanceof CompoundTag enchantmentTag) {
				var id = enchantmentTag.getString("id");
				var levelValue = enchantmentTag.getShort("lvl");
				if(id.equals(enchantmentId.toString())) {
					level += levelValue;
				}
			}
		}

		return level;
	}
}
