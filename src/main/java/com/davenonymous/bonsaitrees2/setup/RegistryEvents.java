package com.davenonymous.bonsaitrees2.setup;

import com.davenonymous.bonsaitrees2.BonsaiTrees2;
import com.davenonymous.bonsaitrees2.block.BonsaiPotBlock;
import com.davenonymous.bonsaitrees2.block.BonsaiPotTileEntity;
import com.davenonymous.bonsaitrees2.block.HoppingBonsaiPotTileEntity;
import com.davenonymous.bonsaitrees2.block.ModObjects;
import com.davenonymous.bonsaitrees2.gui.TreeCreatorContainer;
import com.davenonymous.bonsaitrees2.registry.sapling.SaplingRecipeHelper;
import com.davenonymous.bonsaitrees2.registry.sapling.SaplingSerializer;
import com.davenonymous.bonsaitrees2.registry.soil.SoilRecipeHelper;
import com.davenonymous.bonsaitrees2.registry.soil.SoilSerializer;
import com.davenonymous.libnonymous.utils.RecipeHelper;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

// You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
// Event bus for receiving Registry Events)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryEvents {

    @SubscribeEvent
    public static void onBlocksRegistry(final RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();
        registry.register(new BonsaiPotBlock(false).setRegistryName("bonsaipot"));
        registry.register(new BonsaiPotBlock(true).setRegistryName("hopping_bonsaipot"));
    }

    @SubscribeEvent
    public static void onItemsRegistry(final RegistryEvent.Register<Item> event) {
        Item.Properties properties = new Item.Properties().group(ItemGroup.DECORATIONS);
        IForgeRegistry<Item> registry = event.getRegistry();
        registry.register(new BlockItem(ModObjects.BONSAIPOT, properties).setRegistryName("bonsaipot"));
        registry.register(new BlockItem(ModObjects.HOPPING_BONSAIPOT, properties).setRegistryName("hopping_bonsaipot"));
    }

    @SubscribeEvent
    public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event) {
        IForgeRegistry<TileEntityType<?>> registry = event.getRegistry();
        registry.register(TileEntityType.Builder.create(BonsaiPotTileEntity::new, ModObjects.BONSAIPOT).build(null).setRegistryName("bonsaipot"));
        registry.register(TileEntityType.Builder.create(HoppingBonsaiPotTileEntity::new, ModObjects.HOPPING_BONSAIPOT).build(null).setRegistryName("hopping_bonsaipot"));
    }

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

    @SubscribeEvent
    public static void onContainerRegistry(final RegistryEvent.Register<ContainerType<?>> event) {
        IForgeRegistry<ContainerType<?>> registry = event.getRegistry();
        registry.register(IForgeContainerType.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            return new TreeCreatorContainer(windowId, inv, pos);
        }).setRegistryName("tree_creator"));
    }
}
