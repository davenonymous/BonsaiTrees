package com.davenonymous.bonsaitrees3.client;

import com.davenonymous.bonsaitrees3.BonsaiTrees3;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;

import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class BonsaiPotModelLoader implements IGeometryLoader<BonsaiPotModelLoader.BonsaiPotModelGeometry> {
	public static final ResourceLocation BONSAIPOT_LOADER = new ResourceLocation(BonsaiTrees3.MODID, "bonsaipot_loader");

	public static final ResourceLocation BONSAIPOT_BRICKS_TEXTURE = new ResourceLocation(BonsaiTrees3.MODID, "block/bonsaipot");
	public static final Material BONSAIPOT_BRICKS_MATERIAL = ForgeHooksClient.getBlockMaterial(BONSAIPOT_BRICKS_TEXTURE);

	public static class BonsaiPotModelGeometry implements IUnbakedGeometry<BonsaiPotModelGeometry> {

		@Override
		public BakedModel bake(IGeometryBakingContext context, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
			return new BonsaiPotBakedModel(modelState, spriteGetter, overrides, context.getTransforms(), bakery.bake(new ResourceLocation(BonsaiTrees3.MODID, "block/bonsaipot_bricks"), modelState, spriteGetter));
		}

		@Override
		public Collection<Material> getMaterials(IGeometryBakingContext context, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
			return List.of(BONSAIPOT_BRICKS_MATERIAL);
		}
	}

	@Override
	public BonsaiPotModelGeometry read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
		return new BonsaiPotModelGeometry();
	}
}
