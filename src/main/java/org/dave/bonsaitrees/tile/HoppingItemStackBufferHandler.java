package org.dave.bonsaitrees.tile;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class HoppingItemStackBufferHandler extends ItemStackHandler {

    public HoppingItemStackBufferHandler() {
        super(16);
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return stack;
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        // nothing to do here
    }

    public void setStackInSlotInternal(int slot, @Nonnull ItemStack stack) {
        super.setStackInSlot(slot, stack);
    }

    public boolean isEmpty() {
        for(int slot = 0; slot < this.getSlots(); slot++) {
            ItemStack itemStack = this.getStackInSlot(slot);
            if(!itemStack.isEmpty()) {
                return false;
            }
        }

        return true;
    }
}
