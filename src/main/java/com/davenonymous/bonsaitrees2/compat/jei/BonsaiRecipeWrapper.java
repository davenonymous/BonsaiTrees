package com.davenonymous.bonsaitrees2.compat.jei;

import com.davenonymous.bonsaitrees2.registry.SoilCompatibility;
import com.davenonymous.bonsaitrees2.registry.sapling.SaplingDrop;
import com.davenonymous.bonsaitrees2.registry.sapling.SaplingInfo;
import com.davenonymous.bonsaitrees2.render.TreeModels;
import com.davenonymous.libnonymous.render.MultiblockBlockModel;
import com.davenonymous.libnonymous.render.MultiblockBlockModelRenderer;
import com.davenonymous.libnonymous.render.RenderTickCounter;
import com.mojang.blaze3d.platform.GlStateManager;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.ingredient.ITooltipCallback;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BonsaiRecipeWrapper implements IRecipeCategoryExtension, ITooltipCallback<ItemStack> {
    SaplingInfo sapling;
    public float[] slotChances;

    public BonsaiRecipeWrapper(SaplingInfo sapling) {
        this.sapling = sapling;
    }

    @Override
    public void drawInfo(int recipeWidth, int recipeHeight, double mouseX, double mouseY) {
        MultiblockBlockModel model = TreeModels.get(sapling.getId());
        if(model == null) {
            return;
        }

        float angle = RenderTickCounter.renderTicks * 45.0f / 128.0f;

        GlStateManager.pushMatrix();

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

        GlStateManager.disableRescaleNormal();

        GlStateManager.translatef(0F, 0F, 216.5F);


        GlStateManager.translatef(50.0f, 20.0f, 0.0f);

        // Shift it a bit down so one can properly see 3d
        GlStateManager.rotatef(-25.0f, 1.0f, 0.0f, 0.0f);

        // Rotate per our calculated time
        GlStateManager.rotatef(angle, 0.0f, 1.0f, 0.0f);

        double scale = model.getScaleRatio(true);
        GlStateManager.scaled(scale, scale, scale);

        double progress = 40.0d;
        GlStateManager.scaled(progress, progress, progress);



        GlStateManager.rotatef(180.0f, 1.0f, 0.0f, 0.0f);

        GlStateManager.translatef(
                (model.width + 1) / -2.0f,
                (model.height + 1) / -2.0f,
                (model.depth + 1) / -2.0f
        );

        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        textureManager.getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);

        GL11.glFrontFace(GL11.GL_CW);
        MultiblockBlockModelRenderer.renderModel(model);

        textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        textureManager.getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();

        GL11.glFrontFace(GL11.GL_CCW);

        GlStateManager.disableBlend();

        GlStateManager.popMatrix();
    }

    @Override
    public void onTooltip(int slot, boolean isInput, ItemStack stack, List<String> tooltip) {
        if(isInput) {
            return;
        }

        tooltip.add(tooltip.size()-1, TextFormatting.YELLOW + I18n.format("bonsaitrees.jei.category.growing.chance", (int)(slotChances[slot-2]*100)));
    }

    @Override
    public void setIngredients(IIngredients iIngredients) {
        //sapling.ingredient
        List<List<ItemStack>> inputs = new ArrayList<>();
        inputs.add(Collections.singletonList(sapling.ingredient.getMatchingStacks()[0]));
        inputs.add(SoilCompatibility.INSTANCE.getValidSoilsForSapling(sapling).stream().map(s -> s.ingredient.getMatchingStacks()[0]).collect(Collectors.toList()));

        iIngredients.setInputLists(VanillaTypes.ITEM, inputs);

        List<ItemStack> drops = new ArrayList<>();

        slotChances = new float[sapling.drops.size()];
        int slot = 0;
        for(SaplingDrop drop : sapling.drops) {
            ItemStack dropStack = drop.resultStack.copy();
            dropStack.setCount(drop.rolls);
            drops.add(dropStack);
            slotChances[slot] = drop.chance;
            slot++;
        }

        iIngredients.setOutputs(VanillaTypes.ITEM, drops);
    }
}
