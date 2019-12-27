package com.davenonymous.bonsaitrees2.block;

import com.davenonymous.bonsaitrees2.render.TreeModels;
import com.davenonymous.libnonymous.render.MultiblockBlockModel;
import com.davenonymous.libnonymous.render.MultiblockBlockModelRenderer;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.BlockRenderLayer;
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
    public static void clearGlLists() {
        BonsaiPotTileEntityRenderer.clearLists = true;
    }


    private void renderSoil(BonsaiPotTileEntity pot, double x, double y, double z) {
        if(pot.getSoilBlockState() == null) {
            return;
        }

        GlStateManager.pushMatrix();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

        GlStateManager.translated(x, y, z);
        GlStateManager.disableRescaleNormal();

        // Init GlStateManager
        GlStateManager.enableAlphaTest();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);

        GlStateManager.disableFog();
        GlStateManager.disableLighting();
        RenderHelper.disableStandardItemLighting();

        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
        } else {
            GlStateManager.shadeModel(GL11.GL_FLAT);
        }

        GlStateManager.scalef(1 / 16.0f, 1 / 16.0f, 1 / 16.0f);
        GlStateManager.translated(2.0d, 1.1d, 2.0d);
        GlStateManager.scalef(12.0f, 1.0f, 12.0f);

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

        GlStateManager.translatef(-pot.getPos().getX(), -pot.getPos().getY(), -pot.getPos().getZ());

        tessellator.draw();

        textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        textureManager.getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();

        GlStateManager.disableAlphaTest();
        GlStateManager.disableBlend();
        GlStateManager.disableCull();
        GlStateManager.enableLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.clearCurrentColor();

        GlStateManager.popMatrix();
    }

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
                GlStateManager.deleteLists(listId, 1);
            }

            glLists = new HashMap<>();
            clearLists = false;
        }

        if(glLists.containsKey(model)) {
            // We have a display list for this model
            GlStateManager.pushMatrix();

            GlStateManager.translated(x, y, z);
            GlStateManager.disableRescaleNormal();

            float translateOffsetX = (float)(model.width+1) / 2.0f;
            float translateOffsetY = 0.0f;
            float translateOffsetZ = (float)(model.depth+1) / 2.0f;

            GlStateManager.translatef(0.5f, 0.0f, 0.5f);

            // Translate up a bit, so we actually grow out of the bonsai pot, not through it
            GlStateManager.translated(0.0d, 0.10d, 0.0d);

            // Scale the whole tree to a single block width/depth
            double scale = model.getScaleRatio(false);
            GlStateManager.scaled(scale, scale, scale);

            // Scale it down even further so we get leave a bit of room on all sides
            float maxSize = 0.9f;
            GlStateManager.scalef(maxSize, maxSize, maxSize);

            double progress = pot.getProgress(partialTicks);
            GlStateManager.scaled(progress, progress, progress);

            double rotate = pot.modelRotation * 90.0d;
            GlStateManager.rotated(rotate, 0.0d, 1.0d, 0.0d);

            GlStateManager.translatef(-translateOffsetX, -translateOffsetY, -translateOffsetZ);


            // Init GlStateManager
            GlStateManager.enableAlphaTest();
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);

            GlStateManager.disableFog();
            GlStateManager.disableLighting();
            RenderHelper.disableStandardItemLighting();

            GlStateManager.enableBlend();
            GlStateManager.enableCull();
            GlStateManager.enableAlphaTest();

            if (Minecraft.isAmbientOcclusionEnabled()) {
                GlStateManager.shadeModel(GL11.GL_SMOOTH);
            } else {
                GlStateManager.shadeModel(GL11.GL_FLAT);
            }

            TextureManager textureManager = Minecraft.getInstance().getTextureManager();
            textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
            textureManager.getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);

            GlStateManager.callList(glLists.get(model));

            textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
            textureManager.getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();

            GlStateManager.disableBlend();
            GlStateManager.clearCurrentColor();

            GlStateManager.popMatrix();
        } else {
            // We need to create a display list for this model
            int listId = GLAllocation.generateDisplayLists(1);
            glLists.put(model, listId);

            GlStateManager.newList(listId, GL11.GL_COMPILE);

            MultiblockBlockModelRenderer.renderModel(model, pot.getWorld(), pot.getPos());

            GlStateManager.endList();
        }
    }

    @Override
    public void render(BonsaiPotTileEntity te, double x, double y, double z, float partialTicks, int destroyStage) {
        renderSoil(te, x, y, z);
        renderShape(te, x, y, z, partialTicks);
    }
}
