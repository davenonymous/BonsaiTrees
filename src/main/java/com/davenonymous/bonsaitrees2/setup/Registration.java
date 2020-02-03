package com.davenonymous.bonsaitrees2.setup;

import com.davenonymous.bonsaitrees2.BonsaiTrees2;
import com.davenonymous.bonsaitrees2.block.BonsaiPotBlock;
import com.davenonymous.bonsaitrees2.block.BonsaiPotTileEntity;
import com.davenonymous.bonsaitrees2.block.HoppingBonsaiPotTileEntity;
import com.davenonymous.bonsaitrees2.gui.TreeCreatorContainer;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Registration {
    private static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, BonsaiTrees2.MODID);
    private static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, BonsaiTrees2.MODID);
    private static final DeferredRegister<TileEntityType<?>> TILES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, BonsaiTrees2.MODID);
    private static final DeferredRegister<ContainerType<?>> CONTAINERS = new DeferredRegister<>(ForgeRegistries.CONTAINERS, BonsaiTrees2.MODID);

    public static void init() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TILES.register(FMLJavaModLoadingContext.get().getModEventBus());
        CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<BonsaiPotBlock> BONSAIPOT = BLOCKS.register("bonsaipot", () -> new BonsaiPotBlock(false));
    public static final RegistryObject<BonsaiPotBlock> HOPPING_BONSAIPOT = BLOCKS.register("hopping_bonsaipot", () -> new BonsaiPotBlock(true));

    private static Item.Properties properties = new Item.Properties().group(ItemGroup.DECORATIONS);
    public static final RegistryObject<Item> BONSAIPOT_ITEM = ITEMS.register("bonsaipot", () -> new BlockItem(BONSAIPOT.get(), properties));
    public static final RegistryObject<Item> HOPPING_BONSAIPOT_ITEM = ITEMS.register("hopping_bonsaipot", () -> new BlockItem(HOPPING_BONSAIPOT.get(), properties));

    public static final RegistryObject<TileEntityType<BonsaiPotTileEntity>> BONSAIPOT_TILE = TILES.register("bonsaipot", () -> TileEntityType.Builder.create(BonsaiPotTileEntity::new, BONSAIPOT.get()).build(null));
    public static final RegistryObject<TileEntityType<HoppingBonsaiPotTileEntity>> HOPPING_BONSAIPOT_TILE = TILES.register("hopping_bonsaipot", () -> TileEntityType.Builder.create(HoppingBonsaiPotTileEntity::new, HOPPING_BONSAIPOT.get()).build(null));

    public static final RegistryObject<ContainerType<TreeCreatorContainer>> TREE_CREATOR_CONTAINER = CONTAINERS.register("tree_creator", () -> IForgeContainerType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        return new TreeCreatorContainer(windowId, inv, pos);
    }));
}
