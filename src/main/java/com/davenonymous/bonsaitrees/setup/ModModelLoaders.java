package com.davenonymous.bonsaitrees.setup;

import com.davenonymous.bonsaitrees.BonsaiTrees;
import com.davenonymous.bonsaitrees.client.PotModelLoader;
import com.davenonymous.bonsaitrees.client.multiblock.MultiBlockFakeLevel;
import com.davenonymous.bonsaitrees.client.multiblock.MultiBlockModel;
import com.davenonymous.bonsaitrees.client.multiblock.MultiBlockModelLoader;
import com.davenonymous.bonsaitrees.client.multiblock.MultiBlockRenderer;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.model.data.ModelData;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = BonsaiTrees.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModModelLoaders {
	public static final Map<ResourceLocation, VertexBuffer> MULTIBLOCK_VBOS = new HashMap<>();
	public static Map<ResourceLocation, ModelResourceLocation> MODEL_MAP = new HashMap<>();
	public static RenderType renderTypeForModels = RenderType.cutout();

	public static VertexBuffer getVbo(ResourceLocation modelId, MultiBlockModel multiBlockModel, MultiBlockFakeLevel level, BlockPos pos) {
		if(!ModModelLoaders.MULTIBLOCK_VBOS.containsKey(modelId)) {
			ByteBufferBuilder byteBufferBuilder = new ByteBufferBuilder(renderTypeForModels.bufferSize);
			BufferBuilder bufferBuilder = new BufferBuilder(byteBufferBuilder, VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
			MultiBlockRenderer renderer = new MultiBlockRenderer(Minecraft.getInstance().getBlockColors(), level, pos, true);

			PoseStack tempPoseStack = new PoseStack();
			renderer.tesselateWithAO(
				level,
				multiBlockModel, ModBlocks.BONSAI_POT.get().defaultBlockState(), pos,
				tempPoseStack, bufferBuilder, false,
				RandomSource.create(), 0,
				0xF000F0, ModelData.EMPTY, renderTypeForModels
			);

			MeshData meshData = bufferBuilder.build();
			if(meshData != null) {
				VertexBuffer modelVertexBuffer = new VertexBuffer(VertexBuffer.Usage.DYNAMIC);
				modelVertexBuffer.bind();
				modelVertexBuffer.upload(meshData);
				VertexBuffer.unbind();
				ModModelLoaders.MULTIBLOCK_VBOS.put(modelId, modelVertexBuffer);
			}
		}

		return MULTIBLOCK_VBOS.get(modelId);
	}

	@SubscribeEvent
	public static void registerGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
		event.register(MultiBlockModelLoader.ID, MultiBlockModelLoader.INSTANCE);
		event.register(PotModelLoader.ID, PotModelLoader.INSTANCE);
	}

	@SubscribeEvent
	public static void registerAdditional(ModelEvent.RegisterAdditional event) {
		var treeModels = Minecraft.getInstance().getResourceManager().listResources("models/multiblock", resource -> resource.getPath().endsWith(".json")).keySet();
		for(var treeModel : treeModels) {
			var modelId = ResourceLocation.fromNamespaceAndPath(treeModel.getNamespace(), treeModel.getPath().replace(".json", "").replace("models/", ""));
			String[] itemParts = modelId.getPath().replace("multiblock/", "").split("/", 2);
			if(itemParts.length != 2) {
				continue;
			}

			var itemId = ResourceLocation.fromNamespaceAndPath(itemParts[0], itemParts[1]);
			var modelResourceId = ModelResourceLocation.standalone(modelId);

			event.register(modelResourceId);
			MODEL_MAP.put(itemId, modelResourceId);
		}
	}

	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		// Some client setup code
		BonsaiTrees.CONTAINER.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
	}
}
