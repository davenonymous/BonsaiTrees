package com.davenonymous.bonsaitrees.compatibility.jei;

import com.davenonymous.bonsaitrees.client.multiblock.MultiBlockFakeLevel;
import com.davenonymous.bonsaitrees.client.multiblock.MultiBlockModel;
import com.davenonymous.bonsaitrees.datacomponents.CamouflageDataComponent;
import com.davenonymous.bonsaitrees.datacomponents.SaplingDataComponent;
import com.davenonymous.bonsaitrees.datacomponents.SoilDataComponent;
import com.davenonymous.bonsaitrees.lib.gui.tooltip.StringTooltipComponent;
import com.davenonymous.bonsaitrees.lib.gui.tooltip.TranslatableTooltipComponent;
import com.davenonymous.bonsaitrees.lib.util.LootHelper;
import com.davenonymous.bonsaitrees.lib.util.Sorting;
import com.davenonymous.bonsaitrees.setup.ModBlocks;
import com.davenonymous.bonsaitrees.setup.ModDataComponents;
import com.davenonymous.bonsaitrees.setup.ModModelLoaders;
import com.davenonymous.bonsaitrees.setup.cache.BonsaiCache;
import com.davenonymous.bonsaitrees.setup.cache.LootCache;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.opengl.GL11;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class BonsaiCategory implements IRecipeCategory<BonsaiRecipe> {
	private final IJeiHelpers jeiHelpers;
	private final ITickTimer rotationTickTimer;

	public BonsaiCategory(IJeiHelpers jeiHelpers) {
		this.jeiHelpers = jeiHelpers;
		this.rotationTickTimer = this.jeiHelpers.getGuiHelper().createTickTimer(360, 360, false);
	}

	@Override
	public int getWidth() {
		return 155;
	}

	@Override
	public int getHeight() {
		return 40;
	}

	@Override
	public RecipeType<BonsaiRecipe> getRecipeType() {
		return BonsaiJEIPlugin.BONSAIS;
	}

	@Override
	public Component getTitle() {
		return Component.translatable("jei.bonsaitrees4.bonsais.title");
	}

	@Override
	public @Nullable IDrawable getIcon() {
		ItemStack stack = new ItemStack(ModBlocks.BONSAI_POT.get());
		stack.set(
			ModDataComponents.CAMOUFLAGE_COMPONENT.get(),
			new CamouflageDataComponent(BuiltInRegistries.BLOCK.getKey(Blocks.LIGHT_GRAY_CONCRETE_POWDER))
		);

		List<Item> saplingItems = Sorting.toSortedList(BonsaiCache.BONSAI_BY_ITEM.keySet());
		if(!saplingItems.isEmpty()) {
			int index = ((int) Minecraft.getInstance().level.getGameTime() >> 4) % saplingItems.size();
			Item saplingToShow = saplingItems.get(index);
			stack.set(
				ModDataComponents.SAPLING_COMPONENT.get(),
				new SaplingDataComponent(saplingToShow.builtInRegistryHolder().getKey().location(), Optional.of(1f))
			);
		}
		stack.set(ModDataComponents.SOIL_COMPONENT.get(), new SoilDataComponent(new ItemStack(Blocks.GRASS_BLOCK)));
		return jeiHelpers.getGuiHelper().createDrawableIngredient(VanillaTypes.ITEM_STACK, stack);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, BonsaiRecipe recipe, IFocusGroup focuses) {
		builder.addInputSlot(1, 1).setSlotName("sapling").addItemStacks(List.of(recipe.sapling().getDefaultInstance()));
		builder.addInputSlot(1, 20).setSlotName("soil").addItemStacks(recipe.info().validSoilItems(Minecraft.getInstance().level.registryAccess()).stream().map(
			ItemStack::new).toList());

		ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(recipe.sapling());
		List<LootHelper.LootTableDrop> drops = LootCache.DROPS_BY_BONSAI.get(itemId);
		if(drops == null) {
			//BonsaiTrees.LOGGER.info("No drops found for sapling: " + itemId);
			return;
		}

		int targetSlot = 0;
		for(int slot = 0; slot < drops.size() && targetSlot < 8; slot++) {
			LootHelper.LootTableDrop lootDrop = drops.get(slot);
			ItemStack drop = lootDrop.stack().copy();

			builder.addOutputSlot(81 + 19 * (targetSlot % 4), 1 + 19 * (targetSlot / 4))
				.setSlotName("output_" + targetSlot)
				.addItemStack(drop)
				.addRichTooltipCallback((recipeSlotView, tooltip) -> {
					this.richTooltipCallback(recipeSlotView, tooltip, lootDrop);
				});

			targetSlot++;
		}
	}

	private void richTooltipCallback(IRecipeSlotView recipeSlotView, ITooltipBuilder tooltip, LootHelper.LootTableDrop lootDrop) {
		if(lootDrop.conditions().isEmpty()) {
			return;
		}

		List<TooltipComponent> conditionTooltips = new LinkedList<>();
		lootDrop.conditions().forEach(condition -> {
			TooltipComponent conditionTooltip = LootHelper.interpretCondition(condition);
			if(conditionTooltip == null) {
				return;
			}

			conditionTooltips.add(conditionTooltip);
		});

		if(conditionTooltips.isEmpty()) {
			return;
		}

		tooltip.add(StringTooltipComponent.white(""));
		tooltip.add(new TranslatableTooltipComponent("jei.bonsaitrees4.recipes.requires"));
		conditionTooltips.forEach(tooltip::add);
	}

	@Override
	public void draw(BonsaiRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
		IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);

		final IDrawableStatic slotDrawable = jeiHelpers.getGuiHelper().getSlotDrawable();
		slotDrawable.draw(guiGraphics, 0, 0);
		slotDrawable.draw(guiGraphics, 0, 19);

		for(int i = 0; i < 8; i++) {
			slotDrawable.draw(guiGraphics, 80 + 19 * (i % 4), 19 * (i / 4));
		}


		ResourceLocation modelId = BonsaiCache.BONSAI_BY_ITEM.get(recipe.sapling()).model();
		ModelResourceLocation treeModelId = ModModelLoaders.MODEL_MAP.get(modelId);
		MultiBlockModel multiBlockModel = (MultiBlockModel) Minecraft.getInstance().getModelManager().getModel(treeModelId);

		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(50f, 38f, 100.0f);

		guiGraphics.pose().scale(32f, 32f, 32f);

		guiGraphics.pose().mulPose(new Quaternionf(new AxisAngle4f((float) Math.toRadians(-25.0f + 180.0f), 1, 0, 0)));

		if(rotationTickTimer != null) {
			guiGraphics.pose().mulPose(new Quaternionf(new AxisAngle4f((float) Math.toRadians(rotationTickTimer.getValue()), 0, 1, 0)));
		}

		MultiBlockFakeLevel fakeLevel = new MultiBlockFakeLevel(multiBlockModel, Minecraft.getInstance().level, BlockPos.ZERO);
		VertexBuffer treeVbo = ModModelLoaders.getVbo(modelId, multiBlockModel, fakeLevel, BlockPos.ZERO);
		if(treeVbo != null && !treeVbo.isInvalid()) {
			Matrix4f viewMatrix = new Matrix4f(RenderSystem.getModelViewMatrix());
			Matrix4f projectionMatrix = RenderSystem.getProjectionMatrix();
			viewMatrix.mul(guiGraphics.pose().last().pose());

			ModModelLoaders.renderTypeForModels.setupRenderState();
			ShaderInstance shader = RenderSystem.getShader();
			shader.setDefaultUniforms(VertexFormat.Mode.QUADS, viewMatrix, projectionMatrix, Minecraft.getInstance().getWindow());
			RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0F);
			RenderSystem.setupShaderLights(shader);
			RenderSystem.enableDepthTest();
			RenderSystem.disableBlend();
			RenderSystem.depthMask(true);

			treeVbo.bind();
			GL11.glFrontFace(GL11.GL_CW);
			treeVbo.drawWithShader(viewMatrix, projectionMatrix, shader);
			GL11.glFrontFace(GL11.GL_CCW);
			VertexBuffer.unbind();
		}

		guiGraphics.pose().popPose();
	}
}
