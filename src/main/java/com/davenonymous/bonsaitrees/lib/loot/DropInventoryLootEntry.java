package com.davenonymous.bonsaitrees.lib.loot;

import com.davenonymous.bonsaitrees.setup.ModLootPools;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class DropInventoryLootEntry extends LootPoolSingletonContainer {
	private final Optional<Direction> side;

	public static final MapCodec<DropInventoryLootEntry> CODEC = RecordCodecBuilder.mapCodec(inst ->
		// Add our own fields.
		inst.group(
				Direction.CODEC.optionalFieldOf("side").forGetter(e -> e.side)
			)
			.and(singletonFields(inst))
			.apply(inst, DropInventoryLootEntry::new)
	);

	protected DropInventoryLootEntry(Optional<Direction> side, int weight, int quality, List<LootItemCondition> conditions, List<LootItemFunction> functions) {
		super(weight, quality, conditions, functions);
		this.side = side;
	}


	public static LootPoolSingletonContainer.Builder<?> dropInventory(Direction side) {
		return simpleBuilder((weight, quality, conditions, functions) -> new DropInventoryLootEntry(Optional.ofNullable(side), weight, quality, conditions, functions));
	}

	@Override
	public void createItemStack(Consumer<ItemStack> consumer, LootContext lootContext) {
		if(!lootContext.hasParam(LootContextParams.BLOCK_ENTITY)) {
			return;
		}

		BlockEntity blockEntity = lootContext.getParam(LootContextParams.BLOCK_ENTITY);
		IItemHandler handler = Capabilities.ItemHandler.BLOCK.getCapability(
			blockEntity.getLevel(), blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity, side.orElse(null));
		if(handler == null) {
			return;
		}

		for(int i = 0; i < handler.getSlots(); i++) {
			consumer.accept(handler.getStackInSlot(i));
		}
	}

	@Override
	public LootPoolEntryType getType() {
		return ModLootPools.DROP_INVENTORY_LOOT.get();
	}
}
