package com.davenonymous.bonsaitrees.client;

import com.davenonymous.bonsaitrees.blocks.BonsaiPotBlock;
import com.davenonymous.bonsaitrees.setup.cache.SoilCache;
import com.davenonymous.bonsaitrees.setup.data.SoilInfoWithTexture;
import com.mojang.math.Transformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.SimpleModelState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.util.TriState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.davenonymous.bonsaitrees.lib.BakedModelHelper.*;

public class PotModel extends BakedModelWrapper<BakedModel> {
	public final Vec3 treeOffset;
	public final float treeScale;
	public final List<BlockElement> elements;
	public final List<PotModelGeometry.SoilElement> soilElements;
	public final Map<Block, List<BakedQuad>> camouflageQuads = new HashMap<>();
	public final Map<Block, List<BakedQuad>> soilQuads = new HashMap<>();
	public final Map<Fluid, List<BakedQuad>> fluidSoilQuads = new HashMap<>();
	public final Map<Item, List<BakedQuad>> itemSoilQuads = new HashMap<>();

	public PotModel(BakedModel originalModel, Vec3 treeOffset, float treeScale, List<BlockElement> elements, List<PotModelGeometry.SoilElement> soilElements) {
		super(originalModel);
		this.treeOffset = treeOffset;
		this.treeScale = treeScale;
		this.elements = elements;
		this.soilElements = soilElements;
	}

	@Override
	public TextureAtlasSprite getParticleIcon(ModelData data) {
		return super.getParticleIcon(data);
	}

	private List<BakedQuad> getSoilQuadsWithTexture(TextureAtlasSprite sprite, int tintIndex, int tintColor) {
		float alpha = (float) (tintColor >> 24 & 255) / 255.0F;
		float r = (float) (tintColor >> 16 & 255) / 255.0F;
		float g = (float) (tintColor >> 8 & 255) / 255.0F;
		float b = (float) (tintColor & 255) / 255.0F;

		List<BakedQuad> soilQuadsResult = new ArrayList<>();
		for(PotModelGeometry.SoilElement soilElement : this.soilElements) {
			Vec3 from = soilElement.from();
			Vec3 to = soilElement.to();
			double minX = Math.min(from.x, to.x) / 16d;
			double maxX = Math.max(from.x, to.x) / 16d;
			double minY = Math.min(from.y, to.y) / 16d;
			double maxY = Math.max(from.y, to.y) / 16d;
			double minZ = Math.min(from.z, to.z) / 16d;
			double maxZ = Math.max(from.z, to.z) / 16d;

			var baked = quad(
				v(minX, maxY, minZ), uv(minX, minZ),
				v(minX, maxY, maxZ), uv(minX, maxZ),
				v(maxX, maxY, maxZ), uv(maxX, maxZ),
				v(maxX, maxY, minZ), uv(maxX, minZ),
				r, g, b, 1.0f, tintIndex, sprite
			);
			soilQuadsResult.add(baked);
		}

		return soilQuadsResult;
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {
		List<BakedQuad> quads = new ArrayList<>(originalModel.getQuads(state, side, rand, extraData, renderType));
		if(quads.isEmpty()) {
			return quads;
		}

		List<BakedQuad> finalQuads = new ArrayList<>();
		if(extraData.has(BonsaiPotBlock.SOIL)) {
			Block soilBlock = extraData.get(BonsaiPotBlock.SOIL);
			if(!soilQuads.containsKey(soilBlock)) {
				BlockState soilState = soilBlock.defaultBlockState();
				BakedModel soilModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(soilState);
				List<BakedQuad> originalQuads = soilModel.getQuads(soilState, Direction.UP, rand, extraData, renderType);
				List<BakedQuad> soilQuadsResult = new ArrayList<>();
				if(!originalQuads.isEmpty()) {
					BakedQuad soilQuad = originalQuads.getFirst();
					TextureAtlasSprite soilTexture = soilQuad.getSprite();
					int soilTint = 0; //soilQuad.getTintIndex();
					int tintColor = Minecraft.getInstance().getBlockColors().getColor(soilState, null, null, soilTint);

					soilQuadsResult.addAll(getSoilQuadsWithTexture(soilTexture, soilTint, tintColor));
				}
				soilQuads.put(soilBlock, soilQuadsResult);
			}
			finalQuads.addAll(soilQuads.get(soilBlock));
		}

		if(extraData.has(BonsaiPotBlock.FLUID_SOIL)) {
			Fluid fluid = extraData.get(BonsaiPotBlock.FLUID_SOIL);
			IClientFluidTypeExtensions fluidStyle = IClientFluidTypeExtensions.of(fluid);
			TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidStyle.getStillTexture());
			int tintColor = fluidStyle.getTintColor();
			if(!fluidSoilQuads.containsKey(fluid)) {
				fluidSoilQuads.put(fluid, getSoilQuadsWithTexture(sprite, 0, tintColor));
			}
			finalQuads.addAll(fluidSoilQuads.get(fluid));
		}

		if(extraData.has(BonsaiPotBlock.ITEM_SOIL)) {
			Item item = extraData.get(BonsaiPotBlock.ITEM_SOIL);
			if(SoilCache.SOIL_BY_ITEM.containsKey(item)) {
				if(!itemSoilQuads.containsKey(item)) {
					SoilInfoWithTexture itemDetails = SoilCache.SOIL_BY_ITEM.get(item);

					TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(itemDetails.texture());
					int tintColor = 0xFFFFFFFF;

					itemSoilQuads.put(item, getSoilQuadsWithTexture(sprite, 0, tintColor));
				}

				finalQuads.addAll(itemSoilQuads.get(item));
			}
		}


		if(extraData.has(BonsaiPotBlock.CAMOUFLAGE)) {
			Block camouflageBlock = extraData.get(BonsaiPotBlock.CAMOUFLAGE);
			BlockState camouflageState = camouflageBlock.defaultBlockState();
			if(!camouflageQuads.containsKey(camouflageBlock)) {
				BakedModel camouflageModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(camouflageState);
				List<BakedQuad> camouflageQuadsResult = new ArrayList<>();
				for(BlockElement element : this.elements) {
					for(Direction direction : element.faces.keySet()) {
						List<BakedQuad> camouflageQuads = camouflageModel.getQuads(camouflageState, direction, rand, extraData, renderType);
						if(camouflageQuads.isEmpty()) {
							continue;
						}

						BakedQuad firstCamouflageQuad = camouflageQuads.getFirst();
						TextureAtlasSprite faceTexture = firstCamouflageQuad.getSprite();
						BlockElementFace face = element.faces.get(direction);

						BakedQuad quad = BlockModel.bakeFace(element, face, faceTexture, direction, new SimpleModelState(new Transformation(null)));
						camouflageQuadsResult.add(quad);
					}
				}
				camouflageQuads.put(camouflageBlock, camouflageQuadsResult);
			}
			finalQuads.addAll(camouflageQuads.get(camouflageBlock));
		} else {
			finalQuads.addAll(quads);
		}

		return finalQuads;
	}

	@Override
	public ItemTransforms getTransforms() {
		return originalModel.getTransforms();
	}

	@Override
	public boolean useAmbientOcclusion() {
		return true;
	}

	@Override
	public boolean usesBlockLight() {
		return false;
	}

	@Override
	public boolean isGui3d() {
		return false;
	}

	@Override
	public TriState useAmbientOcclusion(BlockState state, ModelData data, RenderType renderType) {
		return TriState.TRUE;
	}

	@Override
	public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data) {
		return ChunkRenderTypeSet.of(RenderType.solid());
	}

}
