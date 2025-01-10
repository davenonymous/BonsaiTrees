package com.davenonymous.bonsaitrees.lib.util;

import com.davenonymous.bonsaitrees.BonsaiTrees;
import com.davenonymous.bonsaitrees.lib.gui.GUI;
import com.davenonymous.bonsaitrees.lib.gui.tooltip.*;
import com.davenonymous.bonsaitrees.setup.cache.ItemAbilityCache;
import com.davenonymous.bonsaitrees.setup.config.DebugConfig;
import com.mojang.datafixers.util.Either;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemEnchantmentsPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.CompositeEntryBase;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.predicates.*;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.loot.CanItemPerformAbility;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

public class LootHelper {

	public static List<LootTableDrop> getLootTableDrops(Either<ResourceKey<LootTable>, LootTable> lootTable, ServerLevel level, LootContext lootContext) {
		return getLootTableDrops(lootTable, new LinkedList<>(), level, lootContext);
	}

	public static List<LootTableDrop> getLootTableDrops(Either<ResourceKey<LootTable>, LootTable> lootTable, List<LootItemCondition> conditions, ServerLevel level, LootContext lootContext) {
		if(lootTable.left().isPresent()) {
			return getLootTableDrops(lootTable.left().get(), conditions, level, lootContext);
		} else if(lootTable.right().isPresent()) {
			return getLootTableDrops(lootTable.right().get(), conditions, level, lootContext);
		}

		return new LinkedList<>();
	}

	public static List<LootTableDrop> getLootTableDrops(ResourceKey<LootTable> lootTableId, ServerLevel level, LootContext lootContext) {
		return getLootTableDrops(lootTableId, new LinkedList<>(), level, lootContext);
	}

	public static List<LootTableDrop> getLootTableDrops(ResourceKey<LootTable> lootTableId, List<LootItemCondition> conditions, ServerLevel level, LootContext lootContext) {
		LootTable lootTable = level.getServer().reloadableRegistries().getLootTable(lootTableId);
		return getLootTableDrops(lootTable, conditions, level, lootContext);
	}

	public static List<LootTableDrop> getLootTableDrops(LootTable lootTable, List<LootItemCondition> inheritedConditions, ServerLevel level, LootContext lootContext) {
		List<LootTableDrop> drops = new LinkedList<>();
		for(LootPool pool : lootTable.pools) {
			List<LootItemCondition> conditionsForPool = new LinkedList<>(pool.conditions);
			Queue<LootPoolEntryContainer> entryQueue = new LinkedList<>(pool.entries);
			while(!entryQueue.isEmpty()) {
				LootPoolEntryContainer entry = entryQueue.poll();
				List<LootItemCondition> conditionsForEntry = new LinkedList<>(inheritedConditions);
				conditionsForEntry.addAll(entry.conditions);
				conditionsForEntry.addAll(conditionsForPool);

				if(entry instanceof NestedLootTable nestedEntry) {
					Either<ResourceKey<LootTable>, LootTable> nestedTable = nestedEntry.contents;
					var nestedDrops = getLootTableDrops(nestedTable, conditionsForEntry, level, lootContext);
					for(LootTableDrop nestedDrop : nestedDrops) {
						if(drops.stream().anyMatch(drop -> ItemStack.isSameItem(nestedDrop.stack(), drop.stack()))) {
							continue;
						}
						if(nestedDrop.stack().isEmpty()) {
							continue;
						}

						drops.add(nestedDrop);
					}
				} else if(entry instanceof CompositeEntryBase compositeEntry) {
					entryQueue.addAll(compositeEntry.children);
				} else if(entry instanceof LootPoolSingletonContainer container) {
					container.createItemStack(
						itemStack -> {
							if(itemStack.isEmpty()) {
								return;
							}

							drops.add(new LootTableDrop(itemStack, conditionsForEntry));
						}, lootContext
					);
				} else {
					BonsaiTrees.LOGGER.warn("Unknown loot pool entry: {}", entry);
				}
			}
		}


		return drops;
	}

	public static Optional<LootItemRandomChanceCondition> getLootCondition(LootPool pool) {
		for(LootItemCondition condition : pool.conditions) {
			if(condition instanceof LootItemRandomChanceCondition chanceCondition) {
				return Optional.of(chanceCondition);
			}
		}
		return Optional.empty();
	}

	public static TooltipComponent interpretCondition(LootItemCondition pCondition) {
		if(pCondition instanceof InvertedLootItemCondition condition) {
			var nested = interpretCondition(condition.term());
			if(nested == null) {
				return null;
			}
			var sprite = new SpriteTooltipComponent(GUI.tabIcons, 14, 11, 118, 0);
			return new HBoxTooltipComponent(sprite, nested).setAlignment(BoxAlignment.CENTER);
		} else if(pCondition instanceof LootItemRandomChanceCondition condition) {
			if(DebugConfig.showChances) {
				NumberProvider chanceProvider = condition.chance();
				if(chanceProvider instanceof ConstantValue constantValue) {
					float chance = constantValue.value();
					return StringTooltipComponent.gray("Chance: " + chance);
				}
				return StringTooltipComponent.gray("UnknownChance: " + chanceProvider.getClass().getSimpleName());
			}
			return null;
		} else if(pCondition instanceof AnyOfCondition condition) {
			VBoxTooltipComponent conditionList = new VBoxTooltipComponent();
			for(LootItemCondition term : condition.terms) {
				TooltipComponent component = interpretCondition(term);
				if(component != null) {
					conditionList.add(component);
				}
			}
			return conditionList;
		} else if(pCondition instanceof CanItemPerformAbility condition) {
			try {
				var abilityField = CanItemPerformAbility.class.getDeclaredField("ability");
				abilityField.setAccessible(true);
				ItemAbility ability = (ItemAbility) abilityField.get(condition);
				Ingredient items = ItemAbilityCache.getIngredientForAbility(ability);
				return new IngredientTooltipComponent(items);
			} catch (NoSuchFieldException e) {
			} catch (IllegalAccessException e) {
			}
			return null;
		} else if(pCondition instanceof ExplosionCondition condition) {
			return null;
		} else if(pCondition instanceof LootItemBlockStatePropertyCondition condition) {
			return null;
		} else if(pCondition instanceof MatchTool condition) {
			if(condition.predicate().isEmpty()) {
				return null;
			}
			ItemPredicate predicate = condition.predicate().get();

			VBoxTooltipComponent conditionList = new VBoxTooltipComponent();
			if(predicate.items().isPresent()) {
				HolderSet<Item> items = predicate.items().get();
				var ingredient = Ingredient.of(items.stream().map(ItemStack::new));
				conditionList.add(new IngredientTooltipComponent(ingredient));
			}

			for(ItemSubPredicate subPredicate : predicate.subPredicates().values()) {
				if(subPredicate instanceof ItemEnchantmentsPredicate enchantmentsPredicate) {
					for(EnchantmentPredicate enchantmentPredicate : enchantmentsPredicate.enchantments) {
						if(enchantmentPredicate.enchantments().isEmpty()) {
							continue;
						}
						HolderSet<Enchantment> enchantments = enchantmentPredicate.enchantments().get();
						for(Holder<Enchantment> enchantment : enchantments) {
							var book = new ItemStackTooltipComponent(new ItemStack(Items.ENCHANTED_BOOK)).setShowLabel(false);
							var label = new TranslatableTooltipComponent(enchantment.value().description().getString());
							conditionList.add(new HBoxTooltipComponent(book, label).setAlignment(BoxAlignment.END));
						}
					}
				} else {
					BonsaiTrees.LOGGER.warn("Unknown SubPredicate: {}", subPredicate.getClass().getSimpleName());
				}
			}

			//tooltip.add(conditionList);
			if(conditionList.isEmpty()) {
				return null;
			}

			return conditionList;
		} else if(pCondition instanceof BonusLevelTableCondition condition) {
			if(condition.values().isEmpty()) {
				return null;
			}

			Holder<Enchantment> enchantment = condition.enchantment();
			float baseChance = condition.values().getFirst();
			if(baseChance == 0.0f) {
				var book = new ItemStackTooltipComponent(new ItemStack(Items.ENCHANTED_BOOK)).setShowLabel(false);
				var label = new TranslatableTooltipComponent(enchantment.value().description().getString());
				return new HBoxTooltipComponent(book, label).setAlignment(BoxAlignment.END);
			}

			if(DebugConfig.showChances) {
				return StringTooltipComponent.gray("Chance: " + baseChance * 100 + "%");
			}

			return null;
		}

		if(DebugConfig.showUnknownLootConditions) {
			return new HBoxTooltipComponent(
				new TranslatableTooltipComponent("jei.bonsaitrees4.recipes.unknown_condition"),
				StringTooltipComponent.gray(pCondition.getClass().getSimpleName())
			);
		}
		return null;
	}

	public static class LootTableDrop {
		private ItemStack stack;
		private List<LootItemCondition> conditions;

		public ItemStack stack() {
			return stack;
		}

		public LootTableDrop stack(ItemStack stack) {
			this.stack = stack;
			return this;
		}

		public List<LootItemCondition> conditions() {
			return conditions;
		}

		public LootTableDrop addConditions(List<LootItemCondition> conditions) {
			this.conditions.addAll(conditions);
			return this;
		}

		public LootTableDrop conditions(List<LootItemCondition> conditions) {
			this.conditions = conditions;
			return this;
		}

		public static final StreamCodec<RegistryFriendlyByteBuf, LootTableDrop> STREAM_CODEC = StreamCodec.composite(
			ItemStack.STREAM_CODEC, LootTableDrop::stack,
			ByteBufCodecs.fromCodecWithRegistries(LootItemCondition.DIRECT_CODEC).apply(ByteBufCodecs.list()), LootTableDrop::conditions,
			LootTableDrop::new
		);

		public LootTableDrop(ItemStack stack, List<LootItemCondition> conditions) {
			this.stack = stack;
			this.conditions = conditions;
		}
	}
}
