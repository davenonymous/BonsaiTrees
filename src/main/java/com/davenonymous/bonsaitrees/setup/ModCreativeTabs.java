package com.davenonymous.bonsaitrees.setup;

import com.davenonymous.bonsaitrees.BonsaiTrees;
import com.davenonymous.bonsaitrees.datacomponents.SaplingDataComponent;
import com.davenonymous.bonsaitrees.datacomponents.SoilDataComponent;
import com.davenonymous.bonsaitrees.lib.util.Sorting;
import com.davenonymous.bonsaitrees.setup.cache.BonsaiCache;
import com.davenonymous.bonsaitrees.setup.data.BonsaiInfo;
import com.davenonymous.bonsaitrees.setup.data.SoilType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Optional;

public class ModCreativeTabs {
	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(
		Registries.CREATIVE_MODE_TAB,
		BonsaiTrees.MODID
	);

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BONSAITREES_TAB = CREATIVE_MODE_TABS.register(
		"bonsaitrees",
		() -> CreativeModeTab.builder()
			.title(Component.translatable("itemGroup.bonsaitrees4"))
			.withTabsBefore(CreativeModeTabs.COMBAT)
			.icon(() -> {
				ItemStack stack = new ItemStack(ModBlocks.BONSAI_POT.get());
				stack.set(ModDataComponents.SOIL_COMPONENT.get(), new SoilDataComponent(new ItemStack(Blocks.GRASS_BLOCK)));
				if(BonsaiCache.BONSAI_BY_ITEM.containsKey(Items.OAK_SAPLING)) {
					stack.set(
						ModDataComponents.SAPLING_COMPONENT.get(),
						new SaplingDataComponent(Items.OAK_SAPLING.builtInRegistryHolder().getKey().location(), Optional.of(1f))
					);
				}
				return stack;
			})
			.displayItems((parameters, output) -> {
				output.accept(ModItems.BONSAI_POT_ITEM.get());
				output.accept(ModItems.BONSAI_POT_SMALL_ITEM.get());
				Sorting.toSortedList(BonsaiCache.BONSAI_BY_ITEM.keySet()).forEach(sapling -> {
					ItemStack stack = new ItemStack(ModBlocks.BONSAI_POT.get());
					ItemStack soilStack = new ItemStack(Items.GRASS_BLOCK);
					stack.set(ModDataComponents.SOIL_COMPONENT.get(), new SoilDataComponent(soilStack));
					stack.set(
						ModDataComponents.SAPLING_COMPONENT.get(),
						new SaplingDataComponent(sapling.builtInRegistryHolder().getKey().location(), Optional.of(1F))
					);
					output.accept(stack);
				});
			})
			.build()
	);

}
