package com.davenonymous.bonsaitrees.client.multiblock;

import com.davenonymous.bonsaitrees.multiblock.MultiBlockGeometryBase;
import com.davenonymous.bonsaitrees.setup.config.ClientConfig;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import net.neoforged.neoforge.client.model.QuadTransformers;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiBlockModel implements IDynamicBakedModel {
	private static final Material MISSING_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, MissingTextureAtlasSprite.getLocation());
	private final Map<Direction, List<BakedQuad>> cache = new HashMap<>();

	private final int scaleToBlocks;
	private final float scale;

	public final MultiBlockGeometry geometry;
	public final Map<BlockPos, MultiBlockGeometry.Voxel> blocks;

	public MultiBlockModel(MultiBlockGeometry geometry) {
		this.geometry = geometry;
		this.blocks = geometry.voxels();
		this.scaleToBlocks = geometry.scaleToBlocks();

		int width = 0;
		int height = 0;
		int depth = 0;
		for(BlockPos pos : blocks.keySet()) {
			if(pos.getX() > width) {
				width = pos.getX();
			}
			if(pos.getY() > height) {
				height = pos.getY();
			}
			if(pos.getZ() > depth) {
				depth = pos.getZ();
			}
		}

		int dim = Math.max(height, Math.max(width, depth));

		++dim;
		if(height > 6 || dim <= 4) {
			dim = Math.max(6, dim);
		}

		this.scale = this.scaleToBlocks / (float) dim;
	}


	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, RandomSource randomSource, ModelData modelData, @Nullable RenderType renderType) {
		if(cache.isEmpty() || !cache.containsKey(direction) || ClientConfig.disableModelCache) {
			cache.put(direction, new ArrayList<>());

			var centerOffset = new Vector3f(geometry.trunkPos.getX() + 0.5f, 0, geometry.trunkPos.getZ() + 0.5f);
			for(MultiBlockGeometry.Voxel voxel : blocks.values()) {
				if(ClientConfig.minimalQuads && direction != null && blocks.containsKey(voxel.pos().relative(direction))) {
					MultiBlockGeometryBase.Voxel relativeState = blocks.get(voxel.pos().relative(direction));
					if(relativeState != null && relativeState.state().getBlock() == voxel.state().getBlock()) {
						continue;
					}
				}

				BakedModel model = voxel.model();
				List<BakedQuad> modelQuads = voxel.model().getQuads(voxel.state(), direction, randomSource, modelData, null);
				Transformation translate = new Transformation(new Matrix4f().translate(voxel.pos().getX(), voxel.pos().getY(), voxel.pos().getZ()));
				Transformation translateCenter = new Transformation(new Matrix4f().translate(-centerOffset.x, 0, -centerOffset.z));
				Transformation scale = new Transformation(new Matrix4f().scale(this.scale));

				IQuadTransformer translator = QuadTransformers.applying(translate);
				IQuadTransformer centerer = QuadTransformers.applying(translateCenter);
				IQuadTransformer scaler = QuadTransformers.applying(scale);

				var transformedQuads = translator.andThen(centerer).andThen(scaler).process(modelQuads);
				for(var quad : transformedQuads) {
					cache.get(direction).add(MultiBlockBakedQuad.of(quad, voxel.state(), voxel.pos()));
				}
			}
		}

		return cache.get(direction);
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
		return MISSING_TEXTURE.sprite();
	}

	@Override
	public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data) {
		return ChunkRenderTypeSet.of(RenderType.TRANSLUCENT);
	}

	@Override
	public ItemOverrides getOverrides() {
		return ItemOverrides.EMPTY;
	}

}
