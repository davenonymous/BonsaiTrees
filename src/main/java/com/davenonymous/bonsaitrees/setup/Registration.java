package com.davenonymous.bonsaitrees.setup;

import net.neoforged.bus.api.IEventBus;

public class Registration {

	public static void register(IEventBus modbus) {
		ModBlocks.BLOCKS.register(modbus);
		ModBlocks.BLOCK_ENTITIES.register(modbus);
		ModItems.ITEMS.register(modbus);
		ModRecipes.RECIPE_TYPES.register(modbus);
		ModRecipes.RECIPE_SERIALIZERS.register(modbus);
		ModContainers.CONTAINERS.register(modbus);
		ModCreativeTabs.CREATIVE_MODE_TABS.register(modbus);
		ModCommands.ARGUMENT_TYPES.register(modbus);
		ModDataComponents.DATA_COMPONENTS.register(modbus);
		ModLootPools.LOOT_POOL_ENTRY_TYPES.register(modbus);
	}

}
