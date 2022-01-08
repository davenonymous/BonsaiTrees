package com.davenonymous.bonsaitrees3.client;

import com.davenonymous.bonsaitrees3.BonsaiTrees3;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class BonsaiPotModelLoader implements IModelLoader<BonsaiPotModelLoader.BonsaiPotModelGeometry> {
	public static final ResourceLocation BONSAIPOT_LOADER = new ResourceLocation(BonsaiTrees3.MODID, "bonsaipot_loader");

	public static final ResourceLocation BONSAIPOT_BRICKS_TEXTURE = new ResourceLocation(BonsaiTrees3.MODID, "block/bonsaipot");
	public static final Material BONSAIPOT_BRICKS_MATERIAL = ForgeHooksClient.getBlockMaterial(BONSAIPOT_BRICKS_TEXTURE);

	@Override
	public BonsaiPotModelGeometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
		return new BonsaiPotModelGeometry();
	}

	@Override
	public void onResourceManagerReload(ResourceManager manager) {
	}

	public static class BonsaiPotModelGeometry implements IModelGeometry<BonsaiPotModelGeometry> {

		@Override
		public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
			return new BonsaiPotBakedModel(modelTransform, spriteGetter, overrides, owner.getCameraTransforms(), bakery.bake(new ResourceLocation(BonsaiTrees3.MODID, "block/bonsaipot_bricks"), modelTransform, spriteGetter));
		}

		@Override
		public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
			return List.of(BONSAIPOT_BRICKS_MATERIAL);
		}
	}
}
