package org.dave.bonsaitrees.jei;

import mezz.jei.api.gui.ITooltipCallback;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.ForgeHooksClient;
import org.dave.bonsaitrees.misc.RenderTickCounter;
import org.dave.bonsaitrees.trees.*;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class BonsaiTreeRecipeWrapper implements IRecipeWrapper, ITooltipCallback<ItemStack> {
    public final TreeType type;
    public int[] slotChances;
    private IBlockAccess blockAccess;
    private TreeShape treeShape;

    public BonsaiTreeRecipeWrapper(TreeType type) {
        this.type = type;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInput(ItemStack.class, type.sapling);

        List<ItemStack> drops = new ArrayList<>();
        slotChances = new int[type.getDrops().size()];
        int slot = 0;
        for(TreeTypeDrop drop : type.getDrops()) {
            drops.add(drop.stack.copy());
            slotChances[slot] = drop.chance;
            slot++;
        }

        ingredients.setOutputs(ItemStack.class, drops);
    }

    @Override
    public void drawInfo(Minecraft mc, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0F, 0F, 216.5F);

        mc.fontRenderer.drawString(type.getGrowTimeHuman(), 20-mc.fontRenderer.getStringWidth(type.getGrowTimeHuman()), 21, 0x444444);

        GlStateManager.popMatrix();

        float angle = RenderTickCounter.renderTicks * 45.0f / 128.0f;

        if(treeShape == null) {
            String shapeName = TreeShapeRegistry.getRandomShapeForStack(type.sapling);
            if (shapeName == null) {
                return;
            }

            treeShape = TreeShapeRegistry.treeShapes.get(shapeName);
        }

        List<BlockPos> toRender = treeShape.getToRenderPositions();
        if(toRender.isEmpty()) {
            return;
        }

        blockAccess = new TreeBlockAccess(treeShape);
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

        GlStateManager.disableRescaleNormal();

        GlStateManager.translate(0F, 0F, 216.5F);


        GlStateManager.translate(50.0f, 20.0f, 0.0f);

        // Shift it a bit down so one can properly see 3d
        GlStateManager.rotate(-25.0f, 1.0f, 0.0f, 0.0f);

        // Rotate per our calculated time
        GlStateManager.rotate(angle, 0.0f, 1.0f, 0.0f);


        double progress = 4.0d;
        GlStateManager.scale(progress, progress, progress);

        GlStateManager.rotate(180.0f, 1.0f, 0.0f, 0.0f);

        GlStateManager.translate(
                (treeShape.getWidth() + 1) / -2.0f,
                (treeShape.getHeight() + 1) / -2.0f,
                (treeShape.getDepth() + 1) / -2.0f
        );

        //GlStateManager.translate(-rotateOffsetX, -rotateOffsetY, -rotateOffsetZ);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
        textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);

        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);

        GL11.glFrontFace(GL11.GL_CW);
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

        GL11.glFrontFace(GL11.GL_CCW);

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
    @Override
    public void onTooltip(int slotIndex, boolean input, ItemStack ingredient, List<String> tooltip) {
        if(input) {
            return;
        }

        tooltip.add(tooltip.size()-1, TextFormatting.YELLOW + I18n.format("bonsaitrees.jei.category.growing.chance", slotChances[slotIndex-1]));
    }
}
