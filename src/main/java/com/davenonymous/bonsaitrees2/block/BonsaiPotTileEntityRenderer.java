package com.davenonymous.bonsaitrees2.block;

import com.davenonymous.bonsaitrees2.render.TreeModels;
import com.davenonymous.libnonymous.render.MultiblockBlockModel;
import com.davenonymous.libnonymous.render.MultiblockBlockModelRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.EmptyModelData;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BonsaiPotTileEntityRenderer extends TileEntityRenderer<BonsaiPotTileEntity> {
    private static boolean clearLists = false;
    private static final Random rand = new Random();

    private static Map<MultiblockBlockModel, Integer> glLists = new HashMap<>();

    public BonsaiPotTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    public static void clearGlLists() {
        BonsaiPotTileEntityRenderer.clearLists = true;
    }


    /*
    private void renderSoil(BonsaiPotTileEntity pot, double x, double y, double z) {
        if(pot.getSoilBlockState() == null) {
            return;
        }

        RenderSystem.pushMatrix();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

        RenderSystem.translated(x, y, z);
        RenderSystem.disableRescaleNormal();

        // Init RenderSystem
        RenderSystem.enableAlphaTest();
        RenderSystem.alphaFunc(GL11.GL_GREATER, 0.1f);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(RenderSystem.SourceFactor.SRC_ALPHA, RenderSystem.DestFactor.ONE_MINUS_SRC_ALPHA);

        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);

        RenderSystem.disableFog();
        RenderSystem.disableLighting();
        RenderHelper.disableStandardItemLighting();

        if (Minecraft.isAmbientOcclusionEnabled()) {
            RenderSystem.shadeModel(GL11.GL_SMOOTH);
        } else {
            RenderSystem.shadeModel(GL11.GL_FLAT);
        }

        RenderSystem.scalef(1 / 16.0f, 1 / 16.0f, 1 / 16.0f);
        RenderSystem.translated(2.0d, 1.1d, 2.0d);
        RenderSystem.scalef(12.0f, 1.0f, 12.0f);

        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        textureManager.getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);

        ForgeHooksClient.setRenderLayer(BlockRenderLayer.TRANSLUCENT);
        try {
            BlockRendererDispatcher brd = Minecraft.getInstance().getBlockRendererDispatcher();
            IBakedModel model = brd.getModelForState(pot.getSoilBlockState());
            brd.getBlockModelRenderer().renderModel(pot.getWorld(), model, pot.getSoilBlockState(), pot.getPos(), buffer, false, rand, 0, EmptyModelData.INSTANCE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ForgeHooksClient.setRenderLayer(null);

        RenderSystem.translatef(-pot.getPos().getX(), -pot.getPos().getY(), -pot.getPos().getZ());

        tessellator.draw();

        textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        textureManager.getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();

        RenderSystem.disableAlphaTest();
        RenderSystem.disableBlend();
        RenderSystem.disableCull();
        RenderSystem.enableLighting();
        RenderSystem.enableRescaleNormal();
        RenderSystem.clearCurrentColor();

        RenderSystem.popMatrix();
    }
    */
 /*
    private void renderShape(BonsaiPotTileEntity pot, double x, double y, double z, float partialTicks) {
        if(!pot.hasSapling()) {
            return;
        }

        MultiblockBlockModel model = TreeModels.get(pot.getTreeId());
        if(model == null) {
            return;
        }

        if(model.getBlockCount() == 0) {
            return;
        }

        if(clearLists) {
            for(int listId : glLists.values()) {
                RenderSystem.deleteLists(listId, 1);
            }

            glLists = new HashMap<>();
            clearLists = false;
        }

        if(glLists.containsKey(model)) {
            // We have a display list for this model
            RenderSystem.pushMatrix();

            RenderSystem.translated(x, y, z);
            RenderSystem.disableRescaleNormal();

            float translateOffsetX = (float)(model.width+1) / 2.0f;
            float translateOffsetY = 0.0f;
            float translateOffsetZ = (float)(model.depth+1) / 2.0f;

            RenderSystem.translatef(0.5f, 0.0f, 0.5f);

            // Translate up a bit, so we actually grow out of the bonsai pot, not through it
            RenderSystem.translated(0.0d, 0.10d, 0.0d);

            // Scale the whole tree to a single block width/depth
            double scale = model.getScaleRatio(false);
            RenderSystem.scaled(scale, scale, scale);

            // Scale it down even further so we get leave a bit of room on all sides
            float maxSize = 0.9f;
            RenderSystem.scalef(maxSize, maxSize, maxSize);

            double progress = pot.getProgress(partialTicks);
            RenderSystem.scaled(progress, progress, progress);

            double rotate = pot.modelRotation * 90.0d;
            RenderSystem.rotated(rotate, 0.0d, 1.0d, 0.0d);

            RenderSystem.translatef(-translateOffsetX, -translateOffsetY, -translateOffsetZ);


            // Init RenderSystem
            RenderSystem.enableAlphaTest();
            RenderSystem.alphaFunc(GL11.GL_GREATER, 0.1f);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(RenderSystem.SourceFactor.SRC_ALPHA, RenderSystem.DestFactor.ONE_MINUS_SRC_ALPHA);

            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);

            RenderSystem.disableFog();
            RenderSystem.disableLighting();
            RenderHelper.disableStandardItemLighting();

            RenderSystem.enableBlend();
            RenderSystem.enableCull();
            RenderSystem.enableAlphaTest();

            if (Minecraft.isAmbientOcclusionEnabled()) {
                RenderSystem.shadeModel(GL11.GL_SMOOTH);
            } else {
                RenderSystem.shadeModel(GL11.GL_FLAT);
            }

            TextureManager textureManager = Minecraft.getInstance().getTextureManager();
            textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
            textureManager.getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);

            RenderSystem.callList(glLists.get(model));

            textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
            textureManager.getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();

            RenderSystem.disableBlend();
            RenderSystem.clearCurrentColor();

            RenderSystem.popMatrix();
        } else {
            // We need to create a display list for this model
            int listId = GLAllocation.generateDisplayLists(1);
            glLists.put(model, listId);

            RenderSystem.newList(listId, GL11.GL_COMPILE);

            MultiblockBlockModelRenderer.renderModel(model, pot.getWorld(), pot.getPos());

            RenderSystem.endList();
        }
    }
    */

    @Override
    public void render(BonsaiPotTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        renderSoil(tileEntityIn, partialTicks, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
        renderShape(tileEntityIn, partialTicks, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
    }

    private void renderShape(BonsaiPotTileEntity tile, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        // TODO: VBOs or DisplayLists

        if(!tile.hasSapling()) {
            return;
        }

        MultiblockBlockModel model = TreeModels.get(tile.getTreeId());
        if(model == null) {
            return;
        }

        if(model.getBlockCount() == 0) {
            return;
        }

        matrix.push();
        matrix.translate(0.5f, 0.0f, 0.5f);

        // Translate up a bit, so we actually grow out of the bonsai pot, not through it
        matrix.translate(0.0d, 0.10d, 0.0d);

        // Scale the whole tree to a single block width/depth
        float scale = (float)model.getScaleRatio(false);
        matrix.scale(scale, scale, scale);

        // Scale it down even further so we get leave a bit of room on all sides
        float maxSize = 0.9f;
        matrix.scale(maxSize, maxSize, maxSize);

        float progress = (float)tile.getProgress(partialTicks);
        matrix.scale(progress, progress, progress);

        float rotate = tile.modelRotation * 90.0f;
        matrix.rotate(Vector3f.YP.rotationDegrees(rotate));

        float translateOffsetX = (float)(model.width+1) / 2.0f;
        float translateOffsetY = 0.0f;
        float translateOffsetZ = (float)(model.depth+1) / 2.0f;
        matrix.translate(-translateOffsetX, -translateOffsetY, -translateOffsetZ);

        MultiblockBlockModelRenderer.renderModel(model, matrix, buffer, combinedLightIn, combinedOverlayIn, tile.getWorld(), tile.getPos());

        matrix.pop();
    }

    private void renderSoil(BonsaiPotTileEntity tile, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
        if(tile.getSoilBlockState() == null) {
            return;
        }

        BlockRendererDispatcher brd = Minecraft.getInstance().getBlockRendererDispatcher();

        matrix.push();
        matrix.scale(1 / 16.0f, 1 / 16.0f, 1 / 16.0f);
        matrix.translate(2.0d, 1.1d, 2.0d);
        matrix.scale(12.0f, 1.0f, 12.0f);

        brd.renderModel(tile.getSoilBlockState(), tile.getPos(), tile.getWorld(), matrix, buffer.getBuffer(RenderType.cutoutMipped()), false, tile.getWorld().rand, EmptyModelData.INSTANCE);

        matrix.pop();
    }
}
