package com.davenonymous.bonsaitrees.blocks;

import com.davenonymous.bonsaitrees.setup.cache.BonsaiCache;
import com.davenonymous.bonsaitrees.setup.cache.SoilCache;
import com.davenonymous.bonsaitrees.setup.config.GameplayConfig;
import com.davenonymous.bonsaitrees.setup.data.BonsaiInfo;
import com.davenonymous.bonsaitrees.setup.data.SoilInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.UnknownNullability;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BonsaiPotBlockProduction implements INBTSerializable<CompoundTag> {
	protected BonsaiPotBlockEntity potBlock;

	protected boolean canGrow;

	public int cutCooldown;
	public int growTicks;

	public ItemStack saplingStack = ItemStack.EMPTY;

	public BonsaiPotBlockProduction(BonsaiPotBlockEntity potBlock) {
		this.potBlock = potBlock;
	}

	public void init() {
		this.canGrow = canGrow();
		this.saplingStack = potBlock.inventories.getSaplingStack().copy();
	}

	public void tick() {
		if(!canGrow) {
			// BonsaiTrees.LOGGER.debug("Bonsai can not grow, skipping tick");
			return;
		}

		if(getRequiredGrowTicks() == 0) {
			// BonsaiTrees.LOGGER.debug("Sapling requires 0 grow ticks, skipping tick");
			return;
		}

		if(cutCooldown > 0) {
			// BonsaiTrees.LOGGER.debug("Cooldown to cutting the tree: " + cutCooldown);
			cutCooldown--;
			return;
		}

		if(growTicks < getRequiredGrowTicks()) {
			growTicks++;
			return;
		}

		if(!potBlock.getRedstoneMode().resolve(potBlock.getLevel(), potBlock.getBlockPos())) {
			return;
		}

		onProductionFinished();
	}

	private boolean canInsertAllIntoOutput(List<ItemStack> stacks) {
		for(ItemStack stack : stacks) {
			ItemStack simulatedStack = ItemHandlerHelper.insertItemStacked(potBlock.inventories.outputInventory, stack, true);
			if(ItemStack.matches(stack, simulatedStack)) {
				return false;
			}
		}
		return true;
	}

	private void insertAllIntoOutput(List<ItemStack> stacks) {
		for(ItemStack stack : stacks) {
			ItemHandlerHelper.insertItemStacked(potBlock.inventories.outputInventory, stack, false);
		}
	}

	public void onProductionFinished() {
		if(potBlock.getLevel().isClientSide()) {
			return;
		}

		if(potBlock.inventories.getToolStack().isEmpty()) {
			return;
		}

		if(potBlock.getLevel() instanceof ServerLevel serverLevel) {
			ItemStack originalToolStack = potBlock.inventories.getToolStack();
			ItemStack toolStack = originalToolStack.copy();
			LootParams.Builder lootParams = new LootParams.Builder(serverLevel)
				.withParameter(LootContextParams.BLOCK_ENTITY, potBlock)
				.withParameter(LootContextParams.BLOCK_STATE, potBlock.getBlockState())
				.withParameter(LootContextParams.ORIGIN, potBlock.getBlockPos().getCenter());

			ItemEnchantments enchantments = potBlock.inventories.enchantments;
			float extraLuck = 0.0f;
			for(var enchantment : enchantments.keySet()) {
				if(enchantment.is(Enchantments.FORTUNE)) {
					extraLuck = enchantments.getLevel(enchantment);
				} else if(!enchantment.is(Enchantments.EFFICIENCY)) {
					toolStack.enchant(enchantment, enchantments.getLevel(enchantment));
				}
			}

			if(extraLuck > 0) {
				lootParams.withLuck(extraLuck);
			}
			lootParams.withParameter(LootContextParams.TOOL, toolStack);

			ResourceKey<LootTable> lootTableId = getBonsaiInfo().get().lootTable();
			LootTable lootTable = serverLevel.getServer().reloadableRegistries().getLootTable(lootTableId);

			int extraRools = 0;
			ItemStack soil = potBlock.inventories.getSoilStack();
			if(SoilCache.isSoil(soil)) {
				SoilInfo soilInfo = SoilCache.getSoilInfo(soil).get();
				if(soilInfo.extraRolls().isPresent()) {
					extraRools = soilInfo.extraRolls().get();
				}
			}

			LootContext lootContext = new LootContext.Builder(lootParams.create(LootContextParamSets.BLOCK)).create(lootTable.randomSequence);
			NumberProvider originalRolls[] = new NumberProvider[lootTable.pools.size()];
			int index = 0;
			for(var pool : lootTable.pools) {
				originalRolls[index] = pool.getRolls();
				pool.setRolls(ConstantValue.exactly(pool.getRolls().getInt(lootContext) + extraRools));
				index++;
			}

			List<ItemStack> newRolledItems = new ArrayList<>();
			lootTable.getRandomItems(
				lootContext, stack -> {
					if(stack.getItem() instanceof BlockItem blockItem) {
						Block block = blockItem.getBlock();
						BlockState state = block.defaultBlockState();
						if(state.requiresCorrectToolForDrops() && !toolStack.isCorrectToolForDrops(state)) {
							return;
						}
					}

					newRolledItems.add(stack);
				}
			);

			index = 0;
			for(var pool : lootTable.pools) {
				pool.setRolls(originalRolls[index]);
				index++;
			}

			int toolRemainingDurability = originalToolStack.getMaxDamage() - originalToolStack.getDamageValue();
			boolean toolHasEnoughDurability =
				GameplayConfig.toolDamageChance == 0 ||
					toolRemainingDurability >= GameplayConfig.toolDamagePerCut;
			if(!toolHasEnoughDurability) {
				originalToolStack.hurtAndBreak(
					GameplayConfig.toolDamagePerCut, serverLevel, null, item -> {
					}
				);

				this.potBlock.setChanged();
				this.potBlock.notifyClients(false);
				return;
			}

			boolean canInsert = canInsertAllIntoOutput(newRolledItems);
			if(!canInsert) {
				this.cutCooldown = GameplayConfig.cutCooldown;
			} else {
				if(GameplayConfig.toolDamageChance > serverLevel.random.nextDouble() && GameplayConfig.toolDamagePerCut > 0) {
					originalToolStack.hurtAndBreak(
						GameplayConfig.toolDamagePerCut, serverLevel, null, item -> {
						}
					);
				}

				insertAllIntoOutput(newRolledItems);
				this.cutCooldown = 0;
				this.growTicks = 0;
			}

			this.potBlock.setChanged();
			this.potBlock.notifyClients(false);
		}
	}

	public void onInventoryChange(ItemStackHandler handler, int slot) {
		if(handler == potBlock.inventories.saplingInventory) {
			ItemStack newSaplingStack = potBlock.inventories.getSaplingStack();
			if(!ItemStack.matches(newSaplingStack, saplingStack)) {
				this.saplingStack = newSaplingStack.copy();
				this.growTicks = 0;
			}
			this.canGrow = canGrow();
		} else if(handler == potBlock.inventories.soilInventory) {
			this.canGrow = canGrow();
		}

		if(!this.canGrow) {
			this.growTicks = 0;
		}
	}

	public boolean canGrow() {
		if(getBonsaiInfo().isEmpty() || getSoilInfo().isEmpty()) {
			return false;
		}

		BonsaiInfo bonsaiInfo = getBonsaiInfo().get();
		SoilInfo soilInfo = getSoilInfo().get();
		return bonsaiInfo.canGrowOnSoil(soilInfo.soilType());
	}

	public Optional<SoilInfo> getSoilInfo() {
		ItemStack soil = potBlock.inventories.getSoilStack();
		if(soil.isEmpty()) {
			return Optional.empty();
		}

		return SoilCache.getSoilInfo(soil);
	}

	public Optional<BonsaiInfo> getBonsaiInfo() {
		ItemStack sapling = potBlock.inventories.getSaplingStack();
		if(sapling.isEmpty()) {
			return Optional.empty();
		}

		return Optional.ofNullable(BonsaiCache.BONSAI_BY_ITEM.get(sapling.getItem()));
	}

	public int getRequiredGrowTicks() {
		if(getBonsaiInfo().isEmpty()) {
			return 0;
		}

		return getBonsaiInfo().get().baseTicks();
	}

	public float getTreeGrowthProgress(float partialTicks) {
		if(getRequiredGrowTicks() == 0) {
			return 0;
		}

		float progress = (partialTicks + growTicks) / (float) getRequiredGrowTicks();
		return Math.max(Math.min(progress, 1.0f), 0.0f);
	}


	@Override
	public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.putInt("growTicks", growTicks);
		return compoundTag;
	}

	@Override
	public void deserializeNBT(HolderLookup.Provider provider, CompoundTag compoundTag) {
		growTicks = compoundTag.getInt("growTicks");
	}


}
