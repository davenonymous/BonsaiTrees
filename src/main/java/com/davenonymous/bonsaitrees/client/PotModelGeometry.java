package com.davenonymous.bonsaitrees.client;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.ElementsModel;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;

import java.util.List;
import java.util.function.Function;

public class PotModelGeometry implements IUnbakedGeometry<PotModelGeometry> {
	private final BlockModel potModel;
	private final Vec3 treeOffset;
	private final float treeScale;
	private final List<SoilElement> soilElements;

	public PotModelGeometry(BlockModel potModel, Vec3 treeOffset, float treeScale, List<SoilElement> soilElements) {
		this.potModel = potModel;
		this.treeOffset = treeOffset;
		this.treeScale = treeScale;
		this.soilElements = soilElements;
	}

	@Override
	public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides) {
		BakedModel bakedBase = new ElementsModel(potModel.getElements()).bake(context, baker, spriteGetter, modelState, overrides);
		return new PotModel(bakedBase, treeOffset, treeScale, potModel.getElements(), soilElements);
	}

	@Override
	public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context) {
		potModel.resolveParents(modelGetter);
	}

	public record SoilElement(Vec3 from, Vec3 to) {

	}
}