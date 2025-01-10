package com.davenonymous.bonsaitrees.setup;

import com.davenonymous.bonsaitrees.BonsaiTrees;
import com.davenonymous.bonsaitrees.lib.loot.DropInventoryLootEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModLootPools {
	public static final DeferredRegister<LootPoolEntryType> LOOT_POOL_ENTRY_TYPES =
		DeferredRegister.create(Registries.LOOT_POOL_ENTRY_TYPE, BonsaiTrees.MODID);

	public static final Supplier<LootPoolEntryType> DROP_INVENTORY_LOOT =
		LOOT_POOL_ENTRY_TYPES.register("drop_inventory", () -> new LootPoolEntryType(DropInventoryLootEntry.CODEC));
}
