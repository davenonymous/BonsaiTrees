package org.dave.bonsaitrees.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.dave.bonsaitrees.BonsaiTrees;
import org.dave.bonsaitrees.misc.ConfigurationHandler;
import org.dave.bonsaitrees.tile.TileBonsaiPot;
import org.dave.bonsaitrees.trees.TreeBlockAccess;
import org.dave.bonsaitrees.trees.TreeShape;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class TESRBonsaiPot extends TileEntitySpecialRenderer<TileBonsaiPot> {
    private IBlockAccess blockAccess;
    private TreeShape treeShape;
    private static Map<TreeShape, Integer> glLists = new HashMap<>();
    private static boolean clearLists = false;

    public static void clearGlLists() {
        TESRBonsaiPot.clearLists = true;
    }

    private void renderSoil(TileBonsaiPot te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if(te.getSoilBlockState() == null) {
            return;
        }

        GlStateManager.pushMatrix();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

        GlStateManager.translate(x, y, z);
        GlStateManager.disableRescaleNormal();

        // Init GlStateManager
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

        GlStateManager.disableFog();
        GlStateManager.disableLighting();
        RenderHelper.disableStandardItemLighting();

        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
        } else {
            GlStateManager.shadeModel(GL11.GL_FLAT);
        }

        GlStateManager.scale(1 / 16.0f, 1 / 16.0f, 1 / 16.0f);
        GlStateManager.translate(2.0d, 1.1d, 2.0d);
        GlStateManager.scale(12.0f, 1.0f, 12.0f);


        TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
        textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);

        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);

        ForgeHooksClient.setRenderLayer(BlockRenderLayer.TRANSLUCENT);
        try {
            BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
            blockrendererdispatcher.renderBlock(te.getSoilBlockState(), te.getPos(), te.getWorld(), buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ForgeHooksClient.setRenderLayer(null);

        GlStateManager.translate(-te.getPos().getX(), -te.getPos().getY(), -te.getPos().getZ());

        tessellator.draw();

        textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();

        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableCull();
        GlStateManager.enableLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.resetColor();
        
        GlStateManager.popMatrix();
    }

    @Override
    public void render(TileBonsaiPot te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        //super.render(te, x, y, z, partialTicks, destroyStage, alpha);
        renderSoil(te, x, y, z, partialTicks, destroyStage, alpha);

        if(te.getBonsaiShapeName() == null) {
            return;
        }

        treeShape = te.getShapeFilename();
        if(treeShape == null) {
            return;
        }

        if(te.getTreeType() == null) {
            return;
        }

        List<BlockPos> toRender = treeShape.getToRenderPositions();
        if(toRender.isEmpty()) {
            return;
        }

        if(clearLists) {
            for(int listId : glLists.values()) {
                GlStateManager.glDeleteLists(listId, 1);
            }

            glLists = new HashMap<>();
            clearLists = false;
        }

        if(glLists.containsKey(treeShape)) {
            GlStateManager.pushMatrix();
            GlStateManager.pushAttrib();

            GlStateManager.translate(x, y, z);
            GlStateManager.disableRescaleNormal();

            float rotateOffsetX = (float)(treeShape.getWidth()+1) / 2.0f;
            float rotateOffsetY = 0.0f;
            float rotateOffsetZ = (float)(treeShape.getDepth()+1) / 2.0f;

            GlStateManager.translate(0.5f, 0.0f, 0.5f);

            // Translate up a bit, so we actually grow out of the bonsai pot, not through it
            GlStateManager.translate(0.0d, 0.10d, 0.0d);

            // Scale the whole tree to a single block width/depth
            double scale = treeShape.getScaleRatio(false);
            GlStateManager.scale(scale, scale, scale);

            // Scale it down even further so we get leave a bit of room on all sides
            float maxSize = ConfigurationHandler.ClientSettings.maxTreeScale;
            GlStateManager.scale(maxSize, maxSize, maxSize);

            double progress = te.getProgress() / (double)BonsaiTrees.instance.typeRegistry.getFinalGrowTime(te.getTreeType(), te.getBonsaiSoil());
            GlStateManager.scale(progress, progress, progress);

            GlStateManager.translate(-rotateOffsetX, -rotateOffsetY, -rotateOffsetZ);


            // Init GlStateManager
            GlStateManager.enableAlpha();
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

            GlStateManager.disableFog();
            GlStateManager.disableLighting();
            RenderHelper.disableStandardItemLighting();

            GlStateManager.enableBlend();
            GlStateManager.enableCull();
            GlStateManager.enableAlpha();

            if (Minecraft.isAmbientOcclusionEnabled()) {
                GlStateManager.shadeModel(GL11.GL_SMOOTH);
            } else {
                GlStateManager.shadeModel(GL11.GL_FLAT);
            }


            TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
            textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);

            GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);


            GlStateManager.callList(glLists.get(treeShape));

            textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();

            GlStateManager.disableBlend();
            GlStateManager.resetColor();

            GlStateManager.popAttrib();
            GlStateManager.popMatrix();
        } else {
            blockAccess = new TreeBlockAccess(treeShape, te.getWorld(), te.getPos());
            BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();

            int listId = GLAllocation.generateDisplayLists(1);
            glLists.put(treeShape, listId);

            GlStateManager.glNewList(listId, GL11.GL_COMPILE);

            GlStateManager.pushAttrib();
            GlStateManager.pushMatrix();

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();

            // Aaaand render
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
            GlStateManager.disableAlpha();
            this.renderLayer(blockrendererdispatcher, buffer, BlockRenderLayer.SOLID, toRender);
            GlStateManager.enableAlpha();
            this.renderLayer(blockrendererdispatcher, buffer, BlockRenderLayer.CUTOUT_MIPPED, toRender);
            this.renderLayer(blockrendererdispatcher, buffer, BlockRenderLayer.CUTOUT, toRender);
            GlStateManager.shadeModel(GL11.GL_FLAT);
            this.renderLayer(blockrendererdispatcher, buffer, BlockRenderLayer.TRANSLUCENT, toRender);

            tessellator.draw();

            GlStateManager.popMatrix();
            GlStateManager.popAttrib();

            GlStateManager.glEndList();
        }
    }

    public void renderLayer(BlockRendererDispatcher blockrendererdispatcher, BufferBuilder buffer, BlockRenderLayer renderLayer, List<BlockPos> toRender) {
        ForgeHooksClient.setRenderLayer(renderLayer);
        for (BlockPos pos : toRender) {
            IBlockState state = treeShape.getStateAtPos(pos);
            if (!state.getBlock().canRenderInLayer(state, renderLayer)) {
                continue;
            }

            try {
                //Logz.info("Rendering: %s", state);
                blockrendererdispatcher.renderBlock(state, pos, blockAccess, buffer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ForgeHooksClient.setRenderLayer(null);
    }
}
