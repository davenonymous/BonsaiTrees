package com.davenonymous.bonsaitrees2.setup;

import com.davenonymous.bonsaitrees2.block.Blockz;
import com.davenonymous.bonsaitrees2.block.BonsaiPotBlock;
import com.davenonymous.bonsaitrees2.block.BonsaiPotTileEntity;
import com.davenonymous.bonsaitrees2.block.HoppingBonsaiPotTileEntity;
import com.davenonymous.bonsaitrees2.registry.RecipeTypes;
import com.davenonymous.bonsaitrees2.registry.sapling.SaplingSerializer;
import com.davenonymous.bonsaitrees2.registry.soil.SoilSerializer;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tileentity.TileEntityType;
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
        registry.register(new BlockItem(Blockz.BONSAIPOT, properties).setRegistryName("bonsaipot"));
        registry.register(new BlockItem(Blockz.HOPPING_BONSAIPOT, properties).setRegistryName("hopping_bonsaipot"));
    }

    @SubscribeEvent
    public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event) {
        IForgeRegistry<TileEntityType<?>> registry = event.getRegistry();
        registry.register(TileEntityType.Builder.create(BonsaiPotTileEntity::new, Blockz.BONSAIPOT).build(null).setRegistryName("bonsaipot"));
        registry.register(TileEntityType.Builder.create(HoppingBonsaiPotTileEntity::new, Blockz.HOPPING_BONSAIPOT).build(null).setRegistryName("hopping_bonsaipot"));
    }

    @SubscribeEvent
    public static void onRecipeRegistry(final RegistryEvent.Register<IRecipeSerializer<?>> event) {
        IForgeRegistry<IRecipeSerializer<?>> registry = event.getRegistry();

        RecipeTypes.soilRecipeType = RecipeTypes.registerRecipeType("soil");
        RecipeTypes.soilRecipeSerializer = new SoilSerializer();
        registry.register(RecipeTypes.soilRecipeSerializer);

        RecipeTypes.saplingRecipeType = RecipeTypes.registerRecipeType("sapling");
        RecipeTypes.saplingRecipeSerializer = new SaplingSerializer();
        registry.register(RecipeTypes.saplingRecipeSerializer);
    }
}
