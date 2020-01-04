package com.davenonymous.bonsaitrees2.gui.widgets;

import com.davenonymous.bonsaitrees2.registry.sapling.SaplingDrop;
import com.davenonymous.libnonymous.gui.framework.widgets.WidgetGhostSlot;
import com.davenonymous.libnonymous.gui.framework.widgets.WidgetIntegerSelect;
import com.davenonymous.libnonymous.gui.framework.widgets.WidgetPanelWithValue;
import net.minecraft.item.ItemStack;

public class DropRowPanel extends WidgetPanelWithValue<SaplingDrop> {
    WidgetGhostSlot ghostSlot;
    WidgetIntegerSelect dropRolls;
    WidgetIntegerSelect dropChance;

    public DropRowPanel() {
        ghostSlot = new WidgetGhostSlot(ItemStack.EMPTY);
        ghostSlot.setPosition(4, 0);
        this.add(ghostSlot);

        dropRolls = new WidgetIntegerSelect(1, 64, 1);
        dropRolls.setDimensions(31, 2, 45, 12);
        this.add(dropRolls);

        dropChance = new WidgetIntegerSelect(1, 100, 20);
        dropChance.setDimensions(82, 2, 50, 12);
        this.add(dropChance);

    }

    public boolean hasItem() {
        return !ghostSlot.getValue().isEmpty();
    }

    @Override
    public SaplingDrop getValue() {
        return new SaplingDrop(ghostSlot.getValue(), dropChance.getValue(), dropRolls.getValue());
    }

    @Override
    public void setValue(SaplingDrop value) {
        this.ghostSlot.setValue(value.resultStack);
        this.dropRolls.setValue(value.rolls);
        this.dropChance.setValue((int)value.chance*100);
    }
}
