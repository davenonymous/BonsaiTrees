package com.davenonymous.bonsaitrees3.libnonymous.gui.framework;

import com.davenonymous.bonsaitrees3.BonsaiTrees3;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WidgetContainer extends AbstractContainerMenu {
	public static ResourceLocation SLOTGROUP_PLAYER = new ResourceLocation(BonsaiTrees3.MODID, "player_slots");

	private IItemHandler playerInventory;
	private CircularPointedArrayList<ResourceLocation> slotGroups;
	private Map<ResourceLocation, List<Integer>> slotGroupMap;
	private Map<ResourceLocation, List<ResourceLocation>> allowedSlotGroupQuickMoving;

	private int nextSlotId = 0;

	protected WidgetContainer(@Nullable MenuType<?> type, int id, Inventory inv) {
		super(type, id);

		this.playerInventory = new InvWrapper(inv);
		this.slotGroups = new CircularPointedArrayList<>();
		this.slotGroupMap = new HashMap<>();
		this.allowedSlotGroupQuickMoving = new HashMap<>();
	}

	@Override
	protected Slot addSlot(Slot slotIn) {
		if(!(slotIn instanceof WidgetSlot)) {
			throw new RuntimeException("Only WidgetSlots are allowed in a WidgetContainer!");
		}

		ResourceLocation slotGroupId = ((WidgetSlot) slotIn).getGroupId();
		if(!this.slotGroups.contains(slotGroupId)) {
			this.slotGroups.add(slotGroupId);
		}

		if(!this.slotGroupMap.containsKey(slotGroupId)) {
			this.slotGroupMap.put(slotGroupId, new ArrayList<>());
		}

		this.slotGroupMap.get(slotGroupId).add(nextSlotId++);
		return super.addSlot(slotIn);
	}

	protected void allowSlotGroupMovement(ResourceLocation from, ResourceLocation to, boolean bidirectional) {
		allowSlotGroupMovement(from, to);
		if(bidirectional) {
			allowSlotGroupMovement(to, from);
		}
	}

	protected void allowSlotGroupMovement(ResourceLocation from, ResourceLocation to) {
		if(!this.allowedSlotGroupQuickMoving.containsKey(from)) {
			this.allowedSlotGroupQuickMoving.put(from, new ArrayList<>());
		}

		this.allowedSlotGroupQuickMoving.get(from).add(to);
	}

	protected void lockSlot(int index) {
		Slot slot = this.slots.get(index);
		if(slot instanceof WidgetSlot) {
			((WidgetSlot) slot).setLocked(true);
			this.slots.set(index, slot);
		}
	}

	protected int addSlotRange(ResourceLocation id, IItemHandler handler, int index, int x, int y, int amount, int dx) {
		for(int i = 0; i < amount; i++) {
			this.addSlot(new WidgetSlot(id, handler, index, x, y));
			x += dx;
			index++;
		}
		return index;
	}

	protected int addSlotBox(ResourceLocation id, IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
		for(int j = 0; j < verAmount; j++) {
			index = this.addSlotRange(id, handler, index, x, y, horAmount, dx);
			y += dy;
		}
		return index;
	}

	protected void layoutPlayerInventorySlots(int leftCol, int topRow) {
		// Player inventory
		this.addSlotBox(SLOTGROUP_PLAYER, playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);

		// Hotbar
		topRow += 58;
		this.addSlotRange(SLOTGROUP_PLAYER, playerInventory, 0, leftCol, topRow, 9, 18);
	}

	private ArrayList<Integer> getTransferTargetSlots(WidgetSlot pSlot) {
		ItemStack stack = pSlot.getItem();
		ArrayList<Integer> result = new ArrayList<>();

		for(int groupIndex = 0; groupIndex < this.slotGroups.size(); groupIndex++) {
			ResourceLocation targetGroup = this.slotGroups.next();
			if(allowedSlotGroupQuickMoving.containsKey(pSlot.getGroupId())) {
				var allowedGroups = allowedSlotGroupQuickMoving.get(pSlot.getGroupId());
				if(allowedGroups.size() > 0 && !allowedGroups.contains(targetGroup)) {
					continue;
				}
			}

			List<Integer> slotsForThisGroup = this.slotGroupMap.get(targetGroup);
			for(int slotIndex : slotsForThisGroup) {
				WidgetSlot testSlot = (WidgetSlot) this.slots.get(slotIndex);
				if(!testSlot.isEnabled() || testSlot.isLocked()) {
					continue;
				}

				if(!testSlot.mayPlace(stack)) {
					continue;
				}

				if(testSlot.hasItem()) {
					if(!stack.isStackable()) {
						continue;
					}

					ItemStack existingStack = testSlot.getItem();
					if(!existingStack.isStackable()) {
						continue;
					}

					if(!existingStack.is(stack.getItem())) {
						continue;
					}

					if(existingStack.getCount() >= existingStack.getMaxStackSize()) {
						continue;
					}

					if(existingStack.getCount() >= testSlot.getMaxStackSize()) {
						continue;
					}

					if(existingStack.getCount() >= testSlot.getMaxStackSize(existingStack)) {
						continue;
					}

					if(!stack.areShareTagsEqual(existingStack)) {
						continue;
					}
				}

				result.add(slotIndex);
			}
		}

		return result;
	}

	private int getSlotStackLimit(WidgetSlot slot, ItemStack stack) {
		int limit = Integer.MAX_VALUE;
		limit = Math.min(limit, slot.getMaxStackSize(stack));
		limit = Math.min(limit, slot.getMaxStackSize());
		limit = Math.min(limit, stack.getMaxStackSize());
		return limit;
	}

	// This method assumes that the widget slot already fulfills all required conditions.
	// See the getTransferTargetSlots method above.
	private ItemStack insertStackIntoSlot(WidgetSlot slot, ItemStack stack) {
		ItemStack existingStack = slot.getItem();
		int fitSize = getSlotStackLimit(slot, stack);
		int remainingSpace = fitSize - existingStack.getCount();
		int toAddSize = stack.getCount();
		int remaining = Math.max(0, toAddSize - remainingSpace);
		int inserted = toAddSize - remaining;

		ItemStack toInsert = stack.copy();
		toInsert.setCount(inserted + existingStack.getCount());
		slot.set(toInsert);

		ItemStack remainingStack = stack.copy();
		remainingStack.setCount(remaining);
		return remainingStack;
	}


	// We are relying on the client to tell the server which slots are currently enabled,
	// see MessageEnabledSlots.
	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		Slot uncastSlot = this.slots.get(index);
		if(uncastSlot == null || !uncastSlot.hasItem() || !(uncastSlot instanceof WidgetSlot)) {
			return ItemStack.EMPTY;
		}
		WidgetSlot slot = (WidgetSlot) uncastSlot;

		ItemStack stackToMove = uncastSlot.getItem().copy();
		//BonsaiTrees3.LOGGER.info("Clicked slot: {} -> {} [{}]", index, stackToMove, slot.getGroupId());
		if(stackToMove.isEmpty()) {
			return ItemStack.EMPTY;
		}

		this.slotGroups.setPointerTo(slot.getGroupId());
		List<Integer> availableSlotsInOrderOfPriority = getTransferTargetSlots(slot);
		//BonsaiTrees3.LOGGER.info("Target slots: {}", availableSlotsInOrderOfPriority.toArray());
		for(int targetSlotId : availableSlotsInOrderOfPriority) {
			if(targetSlotId == index) {
				// Skip own slot
				continue;
			}

			WidgetSlot targetSlot = (WidgetSlot) this.slots.get(targetSlotId);
			stackToMove = insertStackIntoSlot(targetSlot, stackToMove);
			if(stackToMove.isEmpty()) {
				break;
			}
		}

		slot.set(stackToMove);
		return ItemStack.EMPTY;
	}

	@Override
	public boolean stillValid(Player pPlayer) {
		return true;
	}
}
