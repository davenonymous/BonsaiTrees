package com.davenonymous.bonsaitrees3.libnonymous.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.Motive;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;

public class MCJsonUtils {
	public static <T extends ForgeRegistryEntry<T>> T getRegistryEntry(JsonObject json, String memberName, IForgeRegistry<T> registry) {

		if(json.has(memberName)) {

			return getRegistryEntry(json.get(memberName), memberName, registry);
		} else {

			throw new JsonSyntaxException("Missing required value " + memberName);
		}
	}

	public static <T extends ForgeRegistryEntry<T>> T getRegistryEntry(JsonElement json, String memberName, IForgeRegistry<T> registry) {

		if(json == null) {

			throw new JsonSyntaxException("The property " + memberName + " is missing.");
		}

		if(json.isJsonPrimitive()) {

			final String rawId = json.getAsString();
			final ResourceLocation registryId = ResourceLocation.tryParse(rawId);

			if(registryId != null) {

				final T registryEntry = registry.getValue(registryId);

				if(registryEntry != null) {

					return registryEntry;
				} else {

					throw new JsonSyntaxException("No entry found for id " + rawId);
				}
			} else {

				throw new JsonSyntaxException("Registry id " + rawId + " for property " + memberName + " was not a valid format.");
			}
		} else {

			throw new JsonSyntaxException("Expected " + memberName + " to be a JSON primitive. was " + json.getClass());
		}
	}

	public static Block getBlock(JsonObject json, String memberName) {
		return getRegistryEntry(json.get(memberName), memberName, ForgeRegistries.BLOCKS);
	}

	public static Fluid getFluid(JsonObject json, String memberName) {
		return getRegistryEntry(json.get(memberName), memberName, ForgeRegistries.FLUIDS);
	}

	public static Item getItem(JsonObject json, String memberName) {
		return getRegistryEntry(json.get(memberName), memberName, ForgeRegistries.ITEMS);
	}

	public static Potion getPotion(JsonObject json, String memberName) {
		return getRegistryEntry(json.get(memberName), memberName, ForgeRegistries.POTIONS);
	}

	public static Biome getBiome(JsonObject json, String memberName) {
		return getRegistryEntry(json.get(memberName), memberName, ForgeRegistries.BIOMES);
	}

	public static SoundEvent getSound(JsonObject json, String memberName) {
		return getRegistryEntry(json.get(memberName), memberName, ForgeRegistries.SOUND_EVENTS);
	}

	public static Enchantment getEnchantment(JsonObject json, String memberName) {
		return getRegistryEntry(json.get(memberName), memberName, ForgeRegistries.ENCHANTMENTS);
	}

	public static EntityType<?> getEntity(JsonObject json, String memberName) {
		return getRegistryEntry(json.get(memberName), memberName, ForgeRegistries.ENTITIES);
	}

	public static BlockEntityType<?> getBlockEntity(JsonObject json, String memberName) {
		return getRegistryEntry(json.get(memberName), memberName, ForgeRegistries.BLOCK_ENTITIES);
	}

	public static ParticleType<?> getParticleType(JsonObject json, String memberName) {
		return getRegistryEntry(json.get(memberName), memberName, ForgeRegistries.PARTICLE_TYPES);
	}

	public static Motive getPainting(JsonObject json, String memberName) {
		return getRegistryEntry(json.get(memberName), memberName, ForgeRegistries.PAINTING_TYPES);
	}
}
