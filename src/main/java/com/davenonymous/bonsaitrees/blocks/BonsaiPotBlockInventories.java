package com.davenonymous.bonsaitrees.blocks;

import com.davenonymous.bonsaitrees.datacomponents.CamouflageDataComponent;
import com.davenonymous.bonsaitrees.datacomponents.SaplingDataComponent;
import com.davenonymous.bonsaitrees.datacomponents.SoilDataComponent;
import com.davenonymous.bonsaitrees.datacomponents.ToolDataComponent;
import com.davenonymous.bonsaitrees.setup.ModDataComponents;
import com.davenonymous.bonsaitrees.setup.cache.BonsaiCache;
import com.davenonymous.bonsaitrees.setup.cache.SoilCache;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

public class BonsaiPotBlockInventories implements INBTSerializable<CompoundTag> {
	public BonsaiPotBlockEntity potBlock;
	public ItemStackHandler saplingInventory;
	public ItemStackHandler soilInventory;
	public ItemStackHandler camouflageInventory;
	public ItemStackHandler toolInventory;
	public ItemStackHandler outputInventory;
	public CombinedInvWrapper accessibleInventories;
	public ItemEnchantments enchantments;
	private final BiConsumer<ItemStackHandler, Integer> onChangeHandler;

	public BonsaiPotBlockInventories(BonsaiPotBlockEntity potBlock, BiConsumer<ItemStackHandler, Integer> onChangeHandler) {
		this.potBlock = potBlock;
		this.saplingInventory = createSaplingInventory();
		this.soilInventory = createSoilInventory();
		this.camouflageInventory = createCamouflageInventory();
		this.toolInventory = createToolInventory();
		this.outputInventory = createOutputInventory();
		this.accessibleInventories = createCombinedInventory();
		this.onChangeHandler = onChangeHandler;
		this.enchantments = ItemEnchantments.EMPTY;
	}

	public BonsaiPotBlockInventories setSaplingStack(Block block) {
		return setSaplingStack(new ItemStack(block));
	}

	public BonsaiPotBlockInventories setSaplingStack(ItemStack stack) {
		saplingInventory.setStackInSlot(0, stack);
		return this;
	}

	public BonsaiPotBlockInventories setSoilStack(Block block) {
		return setSoilStack(new ItemStack(block));
	}

	public BonsaiPotBlockInventories setSoilStack(ItemStack stack) {
		soilInventory.setStackInSlot(0, stack);
		return this;
	}

	public BonsaiPotBlockInventories setCamouflageStack(Block block) {
		return setCamouflageStack(new ItemStack(block));
	}

	public BonsaiPotBlockInventories setCamouflageStack(ItemStack stack) {
		camouflageInventory.setStackInSlot(0, stack);
		return this;
	}

	public BonsaiPotBlockInventories setToolStack(ItemStack stack) {
		toolInventory.setStackInSlot(0, stack);
		return this;
	}

	public ItemStack getSaplingStack() {
		return saplingInventory.getStackInSlot(0);
	}

	public ItemStack getSoilStack() {
		return soilInventory.getStackInSlot(0);
	}

	public ItemStack getCamouflageStack() {
		return camouflageInventory.getStackInSlot(0);
	}

	public ItemStack getToolStack() {
		return toolInventory.getStackInSlot(0);
	}

	private CombinedInvWrapper createCombinedInventory() {
		return new CombinedInvWrapper(
			toolInventory,
			soilInventory,
			saplingInventory
		);
	}

	private ItemStackHandler createToolInventory() {
		final Set<ItemAbility> BONSAI_ACTIONS = Set.of(
			ItemAbilities.AXE_DIG,
			ItemAbilities.SHEARS_DIG,
			ItemAbilities.SHEARS_HARVEST,
			ItemAbilities.SWORD_DIG,
			ItemAbilities.SHOVEL_DIG,
			ItemAbilities.PICKAXE_DIG
		);
		return new ItemStackHandler(1) {
			@Override
			protected void onContentsChanged(int slot) {
				onChangeHandler.accept(this, slot);
			}

			@Override
			public int getSlotLimit(int slot) {
				return 1;
			}

			@Override
			public boolean isItemValid(int slot, ItemStack stack) {
				return stack.isDamageableItem() && BONSAI_ACTIONS.stream().anyMatch(stack::canPerformAction);
			}
		};
	}

	private ItemStackHandler createOutputInventory() {
		return new ItemStackHandler(6) {
			@Override
			protected void onContentsChanged(int slot) {
				onChangeHandler.accept(this, slot);
			}

			@Override
			public boolean isItemValid(int slot, ItemStack stack) {
				return true;
			}
		};
	}

	private ItemStackHandler createSaplingInventory() {
		return new ItemStackHandler(1) {
			@Override
			protected void onContentsChanged(int slot) {
				onChangeHandler.accept(this, slot);
			}

			@Override
			public int getSlotLimit(int slot) {
				return 1;
			}

			@Override
			protected int getStackLimit(int slot, ItemStack stack) {
				return 1;
			}

			@Override
			public boolean isItemValid(int slot, ItemStack stack) {
				return BonsaiCache.BONSAI_BY_ITEM.containsKey(stack.getItem());
			}
		};
	}

	private ItemStackHandler createSoilInventory() {
		return new ItemStackHandler(1) {
			@Override
			protected void onContentsChanged(int slot) {
				onChangeHandler.accept(this, slot);
			}

			@Override
			public int getSlotLimit(int slot) {
				return 1;
			}

			@Override
			protected int getStackLimit(int slot, ItemStack stack) {
				return 1;
			}

			@Override
			public boolean isItemValid(int slot, ItemStack stack) {
				return SoilCache.isSoil(stack);
			}
		};
	}

	private ItemStackHandler createCamouflageInventory() {
		return new ItemStackHandler(1) {
			@Override
			protected void onContentsChanged(int slot) {
				onChangeHandler.accept(this, slot);
			}

			@Override
			public int getSlotLimit(int slot) {
				return 1;
			}

			@Override
			protected int getStackLimit(int slot, ItemStack stack) {
				return 1;
			}

			@Override
			public boolean isItemValid(int slot, ItemStack stack) {
				if(stack.getItem() instanceof BlockItem item) {
					Block block = item.getBlock();
					BlockState state = block.defaultBlockState();
					boolean isSolid = state.isSolidRender(potBlock.getLevel(), potBlock.getBlockPos());
					boolean isFullCollision = state.isCollisionShapeFullBlock(potBlock.getLevel(), potBlock.getBlockPos());
					return isSolid && isFullCollision;
				}
				return false;
			}
		};
	}


	protected void applyImplicitComponents(BlockEntity.DataComponentInput componentInput) {
		if(componentInput.get(ModDataComponents.CAMOUFLAGE_COMPONENT) instanceof CamouflageDataComponent(ResourceLocation camouflage)) {
			Block camouflageBlock = BuiltInRegistries.BLOCK.get(camouflage);
			this.setCamouflageStack(camouflageBlock);
		}

		if(componentInput.get(ModDataComponents.SOIL_COMPONENT) instanceof SoilDataComponent(ItemStack soil)) {
			this.setSoilStack(soil);
		}

		if(componentInput.get(ModDataComponents.SAPLING_COMPONENT) instanceof SaplingDataComponent saplingDataComponent) {
			Block saplingBlock = BuiltInRegistries.BLOCK.get(saplingDataComponent.sapling());
			this.setSaplingStack(saplingBlock);
		}

		if(componentInput.get(ModDataComponents.TOOL_COMPONENT) instanceof ToolDataComponent(ItemStack tool)) {
			this.setToolStack(tool.copy());
		}

		if(componentInput.get(DataComponents.ENCHANTMENTS) instanceof ItemEnchantments itemEnchantments) {
			this.enchantments = itemEnchantments;
		}
	}

	protected void collectImplicitComponents(DataComponentMap.Builder components) {
		ItemStack soil = this.getSoilStack();
		if(!soil.isEmpty()) {
			components.set(ModDataComponents.SOIL_COMPONENT, new SoilDataComponent(soil));
		}

		ItemStack sapling = this.getSaplingStack();
		if(!sapling.isEmpty()) {
			components.set(ModDataComponents.SAPLING_COMPONENT, new SaplingDataComponent(sapling.getItem().builtInRegistryHolder().getKey().location(), Optional.of(1.0F)));
		}

		ItemStack camouflage = this.getCamouflageStack();
		if(!camouflage.isEmpty() && camouflage.getItem() instanceof BlockItem item) {
			components.set(ModDataComponents.CAMOUFLAGE_COMPONENT, new CamouflageDataComponent(item.getBlock().builtInRegistryHolder().getKey().location()));
		}

		ItemStack tool = this.getToolStack();
		if(!tool.isEmpty()) {
			components.set(ModDataComponents.TOOL_COMPONENT, new ToolDataComponent(tool.copy()));
		}

		if(!enchantments.isEmpty()) {
			components.set(DataComponents.ENCHANTMENTS, enchantments);
		}
	}


	@Override
	public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider registries) {
		CompoundTag tag = new CompoundTag();
		tag.put("sapling", saplingInventory.serializeNBT(registries));
		tag.put("soil", soilInventory.serializeNBT(registries));
		tag.put("camouflage", camouflageInventory.serializeNBT(registries));
		tag.put("tool", toolInventory.serializeNBT(registries));
		tag.put("output", outputInventory.serializeNBT(registries));

		if(!enchantments.isEmpty()) {
			CompoundTag enchantmentTag = new CompoundTag();
			enchantments.keySet().forEach(holder -> {
				int level = enchantments.getLevel(holder);
				var key = holder.getKey();
				enchantmentTag.putInt(key.location().toString(), level);
			});
			tag.put("enchantments", enchantmentTag);
		}

		return tag;
	}

	@Override
	public void deserializeNBT(HolderLookup.Provider registries, CompoundTag tag) {
		saplingInventory.deserializeNBT(registries, tag.getCompound("sapling"));
		soilInventory.deserializeNBT(registries, tag.getCompound("soil"));
		camouflageInventory.deserializeNBT(registries, tag.getCompound("camouflage"));
		toolInventory.deserializeNBT(registries, tag.getCompound("tool"));
		outputInventory.deserializeNBT(registries, tag.getCompound("output"));

		if(tag.contains("enchantments")) {
			CompoundTag enchantmentTag = tag.getCompound("enchantments");
			var registry = registries.lookupOrThrow(Registries.ENCHANTMENT);
			ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
			for(String key : enchantmentTag.getAllKeys()) {
				Optional<Holder.Reference<Enchantment>> enchantment = registry.get(ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.parse(key)));
				if(enchantment.isPresent()) {
					int level = enchantmentTag.getInt(key);
					enchantments.set(enchantment.get().getDelegate(), level);
				}
			}
			this.enchantments = enchantments.toImmutable();
		}
	}
}
