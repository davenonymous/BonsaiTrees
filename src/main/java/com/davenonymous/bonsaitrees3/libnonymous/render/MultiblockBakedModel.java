package com.davenonymous.bonsaitrees3.libnonymous.render;

import com.davenonymous.bonsaitrees3.client.BonsaiPotModelLoader;
import com.davenonymous.bonsaitrees3.libnonymous.serialization.MultiblockBlockModel;
import com.mojang.math.Matrix4f;
import com.mojang.math.Transformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.QuadTransformer;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public class MultiblockBakedModel implements IDynamicBakedModel {
	private final Function<Material, TextureAtlasSprite> spriteGetter;
	private final MultiblockBlockModel model;
	private final ItemOverrides overrides;
	private final ItemTransforms itemTransforms;
	private final List<BakedQuad> cache = new ArrayList<>();

	private static Map<MultiblockBlockModel, MultiblockBakedModel> bakeCache = new HashMap<>();

	public static MultiblockBakedModel of(MultiblockBlockModel model) {
		if(!bakeCache.containsKey(model)) {
			bakeCache.put(model, new MultiblockBakedModel(model));
		}

		return bakeCache.get(model);
	}

	private MultiblockBakedModel(MultiblockBlockModel model) {
		this.spriteGetter = material -> Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(material.atlasLocation());
		this.model = model;
		this.overrides = ItemOverrides.EMPTY;
		this.itemTransforms = ItemTransforms.NO_TRANSFORMS;
	}

	@NotNull
	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState inputBlockState, @Nullable Direction pSide, @NotNull Random rand, @NotNull IModelData extraData) {
		RenderType layer = MinecraftForgeClient.getRenderType();
		if(pSide != null || (layer != null && !layer.equals(RenderType.solid()))) {
			return Collections.emptyList();
		}

		if(cache.isEmpty()) {
			var shaper = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper();

			for(var pos : model.getRelevantPositions()) {
				var state = model.blocks.get(pos);

				BakedModel blockModel = shaper.getBlockModel(state);
				var sides = new ArrayList<>(List.of(Direction.values()));
				sides.add(null);
				for(var side : sides) {
					List<BakedQuad> modelQuads = blockModel.getQuads(state, side, rand, EmptyModelData.INSTANCE);

					Transformation translate = new Transformation(Matrix4f.createTranslateMatrix(pos.getX(), pos.getY(), pos.getZ()));
					QuadTransformer quadTransformer = new QuadTransformer(translate);

					var transformedQuads = quadTransformer.processMany(modelQuads);
					for(var quad : transformedQuads) {
						cache.add(TintedBakedQuad.of(quad, state, pos));
					}
				}
			}
		}

		return cache;
	}

	@Override
	public boolean useAmbientOcclusion() {
		return true;
	}

	@Override
	public boolean isGui3d() {
		return false;
	}

	@Override
	public boolean usesBlockLight() {
		return false;
	}

	@Override
	public boolean isCustomRenderer() {
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleIcon() {
		return spriteGetter.apply(BonsaiPotModelLoader.BONSAIPOT_BRICKS_MATERIAL);
	}

	@Override
	public ItemOverrides getOverrides() {
		return overrides;
	}

	@Override
	public ItemTransforms getTransforms() {
		return itemTransforms;
	}
}