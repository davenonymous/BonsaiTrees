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
import org.dave.bonsaitrees.misc.ConfigurationHandler;
import org.dave.bonsaitrees.tile.TileBonsaiPot;
import org.dave.bonsaitrees.trees.TreeBlockAccess;
import org.dave.bonsaitrees.trees.TreeShape;
import org.lwjgl.opengl.GL11;

import java.util.List;

@SideOnly(Side.CLIENT)
public class TESRBonsaiPot extends TileEntitySpecialRenderer<TileBonsaiPot> {
    private IBlockAccess blockAccess;
    private TreeShape treeShape;

    @Override
    public void render(TileBonsaiPot te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        //super.render(te, x, y, z, partialTicks, destroyStage, alpha);
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

        blockAccess = new TreeBlockAccess(treeShape, te.getWorld(), te.getPos());
        BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();

        GlStateManager.pushAttrib();
        GlStateManager.pushMatrix();

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

        double progress = te.getProgress() / (double)te.getTreeType().getGrowTime();
        GlStateManager.scale(progress, progress, progress);


        GlStateManager.translate(-rotateOffsetX, -rotateOffsetY, -rotateOffsetZ);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
        textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);

        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);

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

        GlStateManager.disableBlend();

        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    public void renderLayer(BlockRendererDispatcher blockrendererdispatcher, BufferBuilder buffer, BlockRenderLayer renderLayer, List<BlockPos> toRender) {
        for (BlockPos pos : toRender) {
            IBlockState state = treeShape.getStateAtPos(pos);
            if (!state.getBlock().canRenderInLayer(state, renderLayer)) {
                continue;
            }

            ForgeHooksClient.setRenderLayer(renderLayer);
            try {
                //Logz.info("Rendering: %s", state);
                blockrendererdispatcher.renderBlock(state, pos, blockAccess, buffer);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ForgeHooksClient.setRenderLayer(null);
        }
    }
}
