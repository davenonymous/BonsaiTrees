package com.davenonymous.bonsaitrees3.client;

import com.davenonymous.bonsaitrees3.blocks.BonsaiPotBlockEntity;
import com.davenonymous.bonsaitrees3.libnonymous.render.QuadBaker;
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
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.QuadTransformer;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public class BonsaiPotBakedModel implements IDynamicBakedModel {
	private final Function<Material, TextureAtlasSprite> spriteGetter;
	private final ModelState modelState;
	private final ItemOverrides overrides;
	private final ItemTransforms itemTransforms;
	private final Map<BlockState, List<BakedQuad>> quadCache = new HashMap<>();
	private final Map<FluidState, List<BakedQuad>> fluidQuadCache = new HashMap<>();
	private final BakedModel potModel;

	public BonsaiPotBakedModel(ModelState modelState, Function<Material, TextureAtlasSprite> spriteGetter, ItemOverrides overrides, ItemTransforms itemTransforms, BakedModel potModel) {
		this.modelState = modelState;
		this.spriteGetter = spriteGetter;
		this.overrides = overrides;
		this.itemTransforms = itemTransforms;
		this.potModel = potModel;
	}

	@Nonnull
	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
		RenderType layer = MinecraftForgeClient.getRenderType();
		if(side != null || (layer != null && !layer.equals(RenderType.solid()))) {
			return Collections.emptyList();
		}

		BlockState soilBlock = extraData.getData(BonsaiPotBlockEntity.SOIL_BLOCK);
		FluidState fluidBlock = extraData.getData(BonsaiPotBlockEntity.FLUID_BLOCK);

		boolean hasBlock = soilBlock != null && !soilBlock.isAir();
		boolean hasFluid = fluidBlock != null && !fluidBlock.isEmpty();

		if(!hasFluid && !hasBlock) {
			return this.potModel.getQuads(state, side, rand, extraData);
		}

		if(hasBlock) {
			if(!quadCache.containsKey(soilBlock)) {
				List<BakedQuad> potQuads = new ArrayList<>(this.potModel.getQuads(state, side, rand, extraData));

				Transformation transformation = new Transformation(Matrix4f.createScaleMatrix(1 / 16.0f, 1 / 16.0f, 1 / 16.0f));
				transformation = transformation.compose(new Transformation(Matrix4f.createTranslateMatrix(2.0f, 1.1f, 2.0f)));
				transformation = transformation.compose(new Transformation(Matrix4f.createScaleMatrix(12.0f, 1.0f, 12.0f)));
				QuadTransformer quadTransformer = new QuadTransformer(transformation);

				BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getBlockModel(soilBlock);
				model.getQuads(soilBlock, Direction.UP, rand, EmptyModelData.INSTANCE).stream().map(quadTransformer::processOne).forEach(potQuads::add);
				quadCache.put(soilBlock, potQuads);
			}

			return quadCache.get(soilBlock);
		}

		if(hasFluid) {
			if(!fluidQuadCache.containsKey(fluidBlock)) {
				List<BakedQuad> potQuads = new ArrayList<>(this.potModel.getQuads(state, side, rand, extraData));
				var textureLocation = fluidBlock.getType().getAttributes().getStillTexture();
				var sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(textureLocation);
				int i = fluidBlock.getType().getAttributes().getColor();
				float alpha = (float) (i >> 24 & 255) / 255.0F;
				float r = (float) (i >> 16 & 255) / 255.0F;
				float g = (float) (i >> 8 & 255) / 255.0F;
				float b = (float) (i & 255) / 255.0F;

				var height = 0.125f;
				var quad = QuadBaker.createQuad(QuadBaker.v(0.1f, height, 0.1f), QuadBaker.v(0.1f, height, 0.9f), QuadBaker.v(0.9f, height, 0.9f), QuadBaker.v(0.9f, height, 0.1f), Transformation.identity(), sprite, r, g, b, alpha);

				potQuads.add(quad);
				fluidQuadCache.put(fluidBlock, potQuads);
			}

			return fluidQuadCache.get(fluidBlock);
		}

		return Collections.emptyList();
	}

	private BakedQuad untintQuad(BakedQuad input) {
		return new BakedQuad(input.getVertices(), -1, input.getDirection(), input.getSprite(), input.isShade());
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