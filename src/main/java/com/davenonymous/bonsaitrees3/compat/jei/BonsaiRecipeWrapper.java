package com.davenonymous.bonsaitrees3.compat.jei;


import com.davenonymous.bonsaitrees3.client.TreeModels;
import com.davenonymous.bonsaitrees3.config.CommonConfig;
import com.davenonymous.bonsaitrees3.libnonymous.render.MultiModelBlockRenderer;
import com.davenonymous.bonsaitrees3.libnonymous.utils.TickTimeHelper;
import com.davenonymous.bonsaitrees3.registry.SoilCompatibility;
import com.davenonymous.bonsaitrees3.registry.sapling.SaplingDrop;
import com.davenonymous.bonsaitrees3.registry.sapling.SaplingInfo;
import com.davenonymous.bonsaitrees3.registry.soil.SoilInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.ingredient.ITooltipCallback;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.util.*;

public class BonsaiRecipeWrapper implements IRecipeCategoryExtension, ITooltipCallback<ItemStack> {
	SaplingInfo sapling;
	public float[] slotChances;
	public SaplingDrop[] slotDrop;
	public Map<ResourceLocation, Float> tickModifiers;

	public static ITickTimer tickTimer = null;

	public BonsaiRecipeWrapper(SaplingInfo sapling) {
		this.sapling = sapling;
	}

	@Override
	public void drawInfo(int recipeWidth, int recipeHeight, PoseStack pose, double mouseX, double mouseY) {
		var model = TreeModels.get(sapling.getId());
		if(model == null) {
			return;
		}

		pose.pushPose();
		pose.translate(50f, 20f, 100.0f);
		pose.scale(36f, 36f, 36f);

		float scale = (float) model.getScaleRatio(true);
		pose.scale(scale, scale, scale);

		pose.mulPose(Quaternion.fromXYZDegrees(new Vector3f(-25.0f + 180.0f, 1.0f, 0.0f)));

		if(tickTimer != null) {
			pose.mulPose(Quaternion.fromXYZDegrees(new Vector3f(0, tickTimer.getValue(), 0.0f)));
		}

		pose.translate((model.width + 1) / -2.0f, (model.height + 1) / -2.0f, (model.depth + 1) / -2.0f);

		MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
		var buffer = bufferSource.getBuffer(RenderType.cutout());

		GL11.glFrontFace(GL11.GL_CW);

		MultiModelBlockRenderer.renderMultiBlockModel(model, Minecraft.getInstance().level, buffer, pose, LightTexture.FULL_BRIGHT);

		bufferSource.endBatch();
		GL11.glFrontFace(GL11.GL_CCW);

		pose.popPose();
	}

	@Override
	public void onTooltip(int slot, boolean isInput, ItemStack stack, List<Component> tooltip) {
		if(stack.isEmpty()) {
			return;
		}

		if(isInput) {
			if(slot == 0) {
				// Sapling slot
				String timeToGrow = TickTimeHelper.getDuration(sapling.baseTicks);
				tooltip.add(tooltip.size() - 1, new TextComponent(ChatFormatting.YELLOW + I18n.get("jei.bonsaitrees3.growtime", timeToGrow)));
			}

			if(slot == 1) {
				float tickModifier = tickModifiers.getOrDefault(stack.getItem().getRegistryName(), 1.0f);
				String timeToGrow = TickTimeHelper.getDuration((int) (sapling.baseTicks * tickModifier));
				tooltip.add(tooltip.size() - 1, new TextComponent(ChatFormatting.YELLOW + I18n.get("jei.bonsaitrees3.soiltime", timeToGrow)));
			}
		} else {
			// Some output slot
			if(CommonConfig.showChanceInJEI.get()) {
				tooltip.add(tooltip.size() - 1, new TextComponent(ChatFormatting.YELLOW + I18n.get("jei.bonsaitrees3.chance", (int) (slotDrop[slot - 2].chance * 100))));
			}

			if(slotDrop[slot - 2].requiresSilkTouch) {
				tooltip.add(tooltip.size() - 1, new TextComponent(ChatFormatting.RED + I18n.get("jei.bonsaitrees3.requiresSilkTouch")));
			}

			if(slotDrop[slot - 2].requiresBees) {
				tooltip.add(tooltip.size() - 1, new TextComponent(ChatFormatting.RED + I18n.get("jei.bonsaitrees3.requiresBees")));
			}
		}
	}

	@Override
	public void setIngredients(IIngredients iIngredients) {
		List<List<ItemStack>> inputs = new ArrayList<>();
		inputs.add(Collections.singletonList(sapling.ingredient.getItems()[0]));

		tickModifiers = new HashMap<>();
		List<ItemStack> soilStacks = new ArrayList<>();
		for(SoilInfo soil : SoilCompatibility.INSTANCE.getValidSoilsForSapling(sapling)) {
			ItemStack representation = soil.ingredient.getItems()[0];
			tickModifiers.put(representation.getItem().getRegistryName(), soil.getTickModifier());
			soilStacks.add(representation);
		}
		inputs.add(soilStacks);

		iIngredients.setInputLists(VanillaTypes.ITEM, inputs);

		List<ItemStack> drops = new ArrayList<>();

		slotChances = new float[sapling.drops.size()];
		slotDrop = new SaplingDrop[sapling.drops.size()];

		int slot = 0;
		for(SaplingDrop drop : sapling.drops) {
			ItemStack dropStack = drop.resultStack.copy();
			dropStack.setCount(drop.rolls);
			drops.add(dropStack);
			slotChances[slot] = drop.chance;
			slotDrop[slot] = drop;
			slot++;
		}

		iIngredients.setOutputs(VanillaTypes.ITEM, drops);
	}
}