package com.davenonymous.bonsaitrees3.compat.jei;


import com.davenonymous.bonsaitrees3.client.TreeModels;
import com.davenonymous.bonsaitrees3.config.CommonConfig;
import com.davenonymous.libnonymous.render.MultiModelBlockRenderer;
import com.davenonymous.libnonymous.utils.TickTimeHelper;
import com.davenonymous.bonsaitrees3.registry.SoilCompatibility;
import com.davenonymous.bonsaitrees3.registry.sapling.SaplingDrop;
import com.davenonymous.bonsaitrees3.registry.sapling.SaplingInfo;
import com.davenonymous.bonsaitrees3.registry.soil.SoilInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import org.lwjgl.opengl.GL11;

import java.util.*;

public class BonsaiRecipeWrapper implements IRecipeCategoryExtension, IRecipeSlotTooltipCallback {
	SaplingInfo sapling;
	public Map<String, SaplingDrop> slotDrops;
	public Map<ResourceLocation, Float> tickModifiers;

	public static ITickTimer tickTimer = null;

	public BonsaiRecipeWrapper(SaplingInfo sapling) {
		this.sapling = sapling;
	}
	
	public void setRecipe(IRecipeLayoutBuilder builder, IFocusGroup focuses) {
		
		builder.addSlot(RecipeIngredientRole.INPUT, 1, 1)
			.setSlotName("sapling").addTooltipCallback(this)
			.addItemStacks(Arrays.stream(sapling.ingredient.getItems()).toList());

		tickModifiers = new HashMap<>();
		List<ItemStack> soilStacks = new ArrayList<>();
		for(SoilInfo soil : SoilCompatibility.INSTANCE.getValidSoilsForSapling(sapling)) {
			ItemStack representation = soil.ingredient.getItems()[0];
			tickModifiers.put(ForgeRegistries.ITEMS.getKey(representation.getItem()), soil.getTickModifier());
			soilStacks.add(representation);
		}
		
		builder.addSlot(RecipeIngredientRole.INPUT, 1, 20)
			.setSlotName("soil").addTooltipCallback(this)
			.addItemStacks(soilStacks);
		
		slotDrops = new HashMap<String, SaplingDrop>();

		int slot = 0;
		for(SaplingDrop drop : sapling.drops) {
			ItemStack dropStack = drop.resultStack.copy();
			dropStack.setCount(drop.rolls);
			slotDrops.put("output_" + slot, drop);
			
			builder.addSlot(RecipeIngredientRole.OUTPUT, 81 + 19 * (slot % 4), 1 + 19 * (slot / 4))
				.setSlotName("output_" + slot).addTooltipCallback(this)
				.addItemStack(dropStack);
			
			slot++;
		}
    }
	
	public void draw(IRecipeSlotsView view, PoseStack stack, double mouseX, double mouseY, IGuiHelper guiHelper) {
    	final IDrawableStatic slotDrawable = guiHelper.getSlotDrawable();
    	
    	slotDrawable.draw(stack, 0, 19 * 0);
		slotDrawable.draw(stack, 0, 19 * 1);

		slotDrawable.draw(stack, 80 + 19 * 0, 19 * 0);
		slotDrawable.draw(stack, 80 + 19 * 1, 19 * 0);
		slotDrawable.draw(stack, 80 + 19 * 2, 19 * 0);
		slotDrawable.draw(stack, 80 + 19 * 3, 19 * 0);

		slotDrawable.draw(stack, 80 + 19 * 0, 19 * 1);
		slotDrawable.draw(stack, 80 + 19 * 1, 19 * 1);
		slotDrawable.draw(stack, 80 + 19 * 2, 19 * 1);
		slotDrawable.draw(stack, 80 + 19 * 3, 19 * 1);
		
		drawInfo(0, 0, stack, mouseX, mouseY);
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

		var mc = Minecraft.getInstance();
		MultiModelBlockRenderer.renderMultiBlockModel(model, mc.level, buffer, pose, LightTexture.FULL_BRIGHT);

		bufferSource.endBatch();
		GL11.glFrontFace(GL11.GL_CCW);

		pose.popPose();
	}

	@Override
	public void onTooltip(IRecipeSlotView recipeSlotView, List<Component> tooltip) {
		if(recipeSlotView.isEmpty() || recipeSlotView.getSlotName().isEmpty()) {
			return;
		}
		var slotName = recipeSlotView.getSlotName().get();
		if(recipeSlotView.getRole() == RecipeIngredientRole.INPUT) {
			if(slotName.equals("sapling")) {
				// Sapling slot
				String timeToGrow = TickTimeHelper.getDuration(sapling.baseTicks);
				var toAdd = Component.translatable("jei.bonsaitrees3.growtime", timeToGrow).withStyle(ChatFormatting.YELLOW);
				if(!tooltip.contains(toAdd)) {
					tooltip.add(tooltip.size() - 1, toAdd);
				}
			}

			if(slotName.equals("soil")) {
				ResourceLocation rLoc = ForgeRegistries.ITEMS.getKey(recipeSlotView.getDisplayedItemStack().orElse(ItemStack.EMPTY).getItem());
				float tickModifier = tickModifiers.getOrDefault(rLoc, 1.0f);
				String timeToGrow = TickTimeHelper.getDuration((int) (sapling.baseTicks * tickModifier));
				var toAdd = Component.translatable("jei.bonsaitrees3.soiltime", timeToGrow).withStyle(ChatFormatting.YELLOW);
				if(!tooltip.contains(toAdd)) {
					tooltip.add(tooltip.size() - 1, toAdd);
				}
			}
		} else if (slotDrops.containsKey(slotName)) {
			// Some output slot
			var drop = slotDrops.get(slotName);
			if(CommonConfig.showChanceInJEI.get()) {
				var toAdd = Component.translatable("jei.bonsaitrees3.chance", (int) (drop.chance * 100)).withStyle(ChatFormatting.YELLOW);
				if(!tooltip.contains(toAdd)) {
					tooltip.add(tooltip.size() - 1, toAdd);
				}
			}

			if(drop.requiresSilkTouch) {
				var toAdd = Component.translatable("jei.bonsaitrees3.requiresSilkTouch").withStyle(ChatFormatting.RED);
				if(!tooltip.contains(toAdd)) {
					tooltip.add(tooltip.size() - 1, toAdd);
				}
			}

			if(drop.requiresBees) {
				var toAdd = Component.translatable("jei.bonsaitrees3.requiresBees").withStyle(ChatFormatting.RED);
				if(!tooltip.contains(toAdd)) {
					tooltip.add(tooltip.size() - 1, toAdd);
				}
			}

			if(!drop.requiredUpgrades.isEmpty()) {
				var toAdd = Component.translatable("jei.bonsaitrees3.requiresUpgrade").withStyle(ChatFormatting.RED);
				if(!tooltip.contains(toAdd)) {
					tooltip.add(tooltip.size() - 1, toAdd);

					var items = drop.requiredUpgrades.getItems();
					for(var item : items) {
						var name = item.getItem().getName(item);
						var itemLine = Component.literal("- " + name.getString()).withStyle(ChatFormatting.AQUA);
						tooltip.add(tooltip.size() - 1, itemLine);
					}
				}
			}
		}
	}
}