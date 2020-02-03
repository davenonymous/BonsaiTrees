package com.davenonymous.bonsaitrees2.setup;

import com.davenonymous.bonsaitrees2.BonsaiTrees2;
import com.davenonymous.bonsaitrees2.block.ModObjects;
import com.davenonymous.bonsaitrees2.registry.sapling.SaplingRecipeHelper;
import com.davenonymous.bonsaitrees2.registry.sapling.SaplingSerializer;
import com.davenonymous.bonsaitrees2.registry.soil.SoilRecipeHelper;
import com.davenonymous.bonsaitrees2.registry.soil.SoilSerializer;
import com.davenonymous.libnonymous.utils.RecipeHelper;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

// You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
// Event bus for receiving Registry Events)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryEvents {

    @SubscribeEvent
    public static void onRecipeRegistry(final RegistryEvent.Register<IRecipeSerializer<?>> event) {
        IForgeRegistry<IRecipeSerializer<?>> registry = event.getRegistry();

        ModObjects.saplingRecipeType = RecipeHelper.registerRecipeType(new ResourceLocation(BonsaiTrees2.MODID, "sapling"));
        ModObjects.saplingRecipeSerializer = new SaplingSerializer();
        ModObjects.saplingRecipeHelper = new SaplingRecipeHelper();
        registry.register(ModObjects.saplingRecipeSerializer);

        ModObjects.soilRecipeType = RecipeHelper.registerRecipeType(new ResourceLocation(BonsaiTrees2.MODID, "soil"));
        ModObjects.soilRecipeSerializer = new SoilSerializer();
        ModObjects.soilRecipeHelper = new SoilRecipeHelper();
        registry.register(ModObjects.soilRecipeSerializer);
    }
}
