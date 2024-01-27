package com.davenonymous.bonsaitrees3.setup;

import com.davenonymous.bonsaitrees3.blocks.BonsaiPotBlock;
import com.davenonymous.bonsaitrees3.blocks.BonsaiPotBlockEntity;
import com.davenonymous.bonsaitrees3.blocks.BonsaiPotContainer;
import com.davenonymous.bonsaitrees3.registry.sapling.SaplingInfo;
import com.davenonymous.bonsaitrees3.registry.sapling.SaplingRecipeHelper;
import com.davenonymous.bonsaitrees3.registry.sapling.SaplingSerializer;
import com.davenonymous.bonsaitrees3.registry.soil.SoilInfo;
import com.davenonymous.bonsaitrees3.registry.soil.SoilRecipeHelper;
import com.davenonymous.bonsaitrees3.registry.soil.SoilSerializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.davenonymous.bonsaitrees3.BonsaiTrees3.MODID;

public class Registration {
	private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
	private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
	private static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);
	private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);
	private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, MODID);


	public static void init() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		BLOCKS.register(bus);
		ITEMS.register(bus);
		BLOCK_ENTITIES.register(bus);
		CONTAINERS.register(bus);
		RECIPE_TYPES.register(bus);
		RECIPE_SERIALIZERS.register(bus);
	}

	public static final ResourceKey<Level> GROWTOWN = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(MODID, "growtown"));

	public static final RegistryObject<RecipeType<SoilInfo>> RECIPE_TYPE_SOIL = RECIPE_TYPES.register("soil", () -> RecipeType.simple(new ResourceLocation(MODID, "soil")));
	public static final Lazy<SoilRecipeHelper> RECIPE_HELPER_SOIL = Lazy.of(SoilRecipeHelper::new);
	public static final RegistryObject<RecipeSerializer<?>> SOIL_SERIALIZER = RECIPE_SERIALIZERS.register("soil", SoilSerializer::new);

	public static final RegistryObject<RecipeType<SaplingInfo>> RECIPE_TYPE_SAPLING = RECIPE_TYPES.register("sapling", () -> RecipeType.simple(new ResourceLocation(MODID, "sapling")));
	public static final Lazy<SaplingRecipeHelper> RECIPE_HELPER_SAPLING = Lazy.of(SaplingRecipeHelper::new);
	public static final RegistryObject<RecipeSerializer<?>> SAPLING_SERIALIZER = RECIPE_SERIALIZERS.register("sapling", SaplingSerializer::new);


	public static final RegistryObject<Block> BONSAI_POT = BLOCKS.register("bonsaipot", BonsaiPotBlock::new);
	public static final RegistryObject<Item> BONSAI_POT_ITEM = fromBlock(BONSAI_POT);

	public static final RegistryObject<BlockEntityType<BonsaiPotBlockEntity>> BONSAI_POT_BLOCKENTITY = BLOCK_ENTITIES.register("bonsaipot", () -> BlockEntityType.Builder.of(BonsaiPotBlockEntity::new, BONSAI_POT.get())
			.build(null));

	public static final RegistryObject<MenuType<BonsaiPotContainer>> BONSAI_POT_CONTAINER = CONTAINERS.register("bonsaipot", () -> IForgeMenuType.create((windowId, inv, data) -> new BonsaiPotContainer(windowId, data.readBlockPos(), inv, inv.player)));

	// Convenience function: Take a RegistryObject<Block> and make a corresponding RegistryObject<Item> from it
	public static final Item.Properties ITEM_PROPERTIES = new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS);

	private static <B extends Block> RegistryObject<Item> fromBlock(RegistryObject<B> block) {
		return ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), ITEM_PROPERTIES));
	}
}