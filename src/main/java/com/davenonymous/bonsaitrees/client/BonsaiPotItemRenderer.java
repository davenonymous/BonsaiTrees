package com.davenonymous.bonsaitrees.client;

import com.davenonymous.bonsaitrees.blocks.BonsaiPotBlock;
import com.davenonymous.bonsaitrees.client.multiblock.MultiBlockFakeLevel;
import com.davenonymous.bonsaitrees.client.multiblock.MultiBlockModel;
import com.davenonymous.bonsaitrees.datacomponents.SaplingDataComponent;
import com.davenonymous.bonsaitrees.setup.ModBlocks;
import com.davenonymous.bonsaitrees.setup.ModDataComponents;
import com.davenonymous.bonsaitrees.setup.ModModelLoaders;
import com.davenonymous.bonsaitrees.setup.cache.BonsaiCache;
import com.davenonymous.bonsaitrees.setup.cache.SoilCache;
import com.davenonymous.bonsaitrees.setup.data.BonsaiInfo;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;

public class BonsaiPotItemRenderer extends BlockEntityWithoutLevelRenderer {
	public static final Map<ResourceLocation, VertexBuffer> vboMap = new HashMap<>();

	public BonsaiPotItemRenderer() {
		super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
	}

	@Override
	public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource pBufferSource, int packedLight, int packedOverlay) {
		ModelManager modelManager = Minecraft.getInstance().getModelManager();
		BlockState potState = ModBlocks.BONSAI_POT.get().defaultBlockState();
		if(stack.is(ModBlocks.BONSAI_POT_SMALL.get().asItem())) {
			potState = ModBlocks.BONSAI_POT_SMALL.get().defaultBlockState();
		}
		BakedModel potModel = modelManager.getBlockModelShaper().getBlockModel(potState);
		Vec3 treeOffset = new Vec3(0, 0, 0);
		float treeMaxScale = 1.0f;
		if(potModel instanceof PotModel pot) {
			treeOffset = pot.treeOffset;
			treeMaxScale = pot.treeScale;
		}

		BlockState soilState = null;
		Fluid soilFluid = null;
		ModelData.Builder modelData = ModelData.builder();
		if(stack.has(ModDataComponents.SOIL_COMPONENT.get())) {
			ItemStack soilStack = stack.get(ModDataComponents.SOIL_COMPONENT.get()).soil();
			if(!soilStack.isEmpty() && soilStack.getItem() instanceof BlockItem item) {
				modelData.with(BonsaiPotBlock.SOIL, item.getBlock());
				soilState = item.getBlock().defaultBlockState();
			}

			if(!soilStack.isEmpty() && soilStack.getItem() instanceof BucketItem bucket) {
				modelData.with(BonsaiPotBlock.FLUID_SOIL, bucket.content);
				soilFluid = bucket.content;
			}

			if(!soilStack.isEmpty() && SoilCache.SOIL_BY_ITEM.containsKey(soilStack.getItem())) {
				modelData.with(BonsaiPotBlock.ITEM_SOIL, soilStack.getItem());
			}
		}

		if(stack.has(ModDataComponents.CAMOUFLAGE_COMPONENT.get())) {
			var camouflageId = stack.get(ModDataComponents.CAMOUFLAGE_COMPONENT.get()).camouflage();
			Block block = BuiltInRegistries.BLOCK.get(camouflageId);
			if(block != null) {
				modelData.with(BonsaiPotBlock.CAMOUFLAGE, block);
			}
		}

		ModelData data = modelData.build();

		poseStack.pushPose();

		// Center the model before applying item transforms
		poseStack.translate(0.5, 0.5, 0.5);
		if(potModel.getTransforms().hasTransform(displayContext)) {
			ItemTransform transform = potModel.getTransforms().getTransform(displayContext);
			transform.apply(false, poseStack);
		}
		poseStack.translate(-0.5, -0.5, -0.5);

		// Render the pot with soil and camouflage
		int tintColor = 0xFFFFFF;
		if(soilState != null) {
			tintColor = Minecraft.getInstance().getBlockColors().getColor(soilState, null, null, 0);
		}
		if(soilFluid != null) {
			tintColor = IClientFluidTypeExtensions.of(soilFluid).getTintColor();
		}
		float r = (float) (tintColor >> 16 & 0xFF) / 255.0F;
		float g = (float) (tintColor >> 8 & 0xFF) / 255.0F;
		float b = (float) (tintColor & 0xFF) / 255.0F;
		Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
			poseStack.last(), pBufferSource.getBuffer(RenderType.solid()), potState, potModel, r, g, b, packedLight, packedOverlay, data, RenderType.translucent()
		);

		// Render the tree
		if(stack.has(ModDataComponents.SAPLING_COMPONENT.get())) {
			SaplingDataComponent saplingData = stack.get(ModDataComponents.SAPLING_COMPONENT.get());

			BonsaiInfo info = BonsaiCache.BONSAI_BY_RESOURCE.get(saplingData.sapling());
			ResourceLocation modelId = info.model();
			if(modelId != null && ModModelLoaders.MODEL_MAP.containsKey(modelId)) {
				ModelResourceLocation model = ModModelLoaders.MODEL_MAP.get(modelId);
				if(Minecraft.getInstance().getModelManager().getModel(model) instanceof MultiBlockModel multiBlockModel) {
					MultiBlockFakeLevel level = new MultiBlockFakeLevel(
						multiBlockModel,
						Minecraft.getInstance().level,
						Minecraft.getInstance().player.blockPosition()
					);

					VertexBuffer vertexBuffer = ModModelLoaders.getVbo(modelId, multiBlockModel, level, Minecraft.getInstance().player.blockPosition());
					if(vertexBuffer != null && !vertexBuffer.isInvalid()) {
						vertexBuffer.bind();
						poseStack.pushPose();

						poseStack.translate(treeOffset.x / 16.0, treeOffset.y / 16.0, treeOffset.z / 16.0);
						poseStack.scale(treeMaxScale, treeMaxScale, treeMaxScale);
						Matrix4f modelMatrix = poseStack.last().pose();

						// Combine the camera transformation with the poseStack transformation
						Matrix4f viewMatrix = new Matrix4f(RenderSystem.getModelViewMatrix());
						viewMatrix.mul(modelMatrix);

						ModModelLoaders.renderTypeForModels.setupRenderState();
						ShaderInstance shader = RenderSystem.getShader();
						shader.setDefaultUniforms(VertexFormat.Mode.QUADS, viewMatrix, RenderSystem.getProjectionMatrix(), Minecraft.getInstance().getWindow());

						RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0F);
						RenderSystem.setupShaderLights(shader);
						RenderSystem.enableDepthTest();
						RenderSystem.disableBlend();
						RenderSystem.depthMask(true);
						shader.apply();

						Matrix4f projectionMatrix = RenderSystem.getProjectionMatrix();
						vertexBuffer.drawWithShader(viewMatrix, projectionMatrix, shader);

						RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
						shader.clear();
						VertexBuffer.unbind();
						poseStack.popPose();
					}
				}
			}
		}

		poseStack.popPose();
	}
}
