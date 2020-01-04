package com.davenonymous.bonsaitrees2.gui.widgets;

import com.davenonymous.bonsaitrees2.gui.TreeCreatorScreen;
import com.davenonymous.libnonymous.gui.framework.event.ValueChangedEvent;
import com.davenonymous.libnonymous.gui.framework.event.WidgetEventResult;
import com.davenonymous.libnonymous.gui.framework.widgets.*;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class PropertiesPanel extends WidgetPanel {
    public PropertiesPanel(int width, int height) {
        super();
        this.setSize(width, height);

        int yOffset = 0;
        WidgetTextBox modelPanelLabel = new WidgetTextBox(I18n.format("bonsaitrees.gui.tree_creator.label.properties"), 0xC0000000);
        modelPanelLabel.setDimensions(2, yOffset, width-10, 9);
        this.add(modelPanelLabel);
        yOffset += 9;

        yOffset += 5;
        WidgetTextBox saplingLabel = new WidgetTextBox(I18n.format("bonsaitrees.gui.tree_creator.label.sapling"), 0x90000000);
        saplingLabel.setDimensions(2, yOffset, 80, 9);
        this.add(saplingLabel);
        yOffset += 9;

        yOffset += 1;
        WidgetGhostSlot stack = new WidgetGhostSlot(ItemStack.EMPTY);
        stack.setId(TreeCreatorScreen.STATE_SAPLING);
        stack.setPosition(85, yOffset-14);
        this.add(stack);
        yOffset += 8;

        yOffset += 3;
        WidgetTextBox idLabel = new WidgetTextBox(I18n.format("bonsaitrees.gui.tree_creator.label.treeid"), 0x90000000);
        idLabel.setDimensions(2, yOffset, width-10, 9);
        this.add(idLabel);
        yOffset += 9;

        yOffset += 1;
        WidgetInputField idInput = new WidgetInputField();
        idInput.setId(TreeCreatorScreen.STATE_TREE_ID);
        idInput.setDimensions(3, yOffset, width-14, 12);
        this.add(idInput);
        yOffset += 12;

        stack.addListener(ValueChangedEvent.class, (event, widget) -> {
            ItemStack newValue = (ItemStack) event.newValue;
            if(newValue.isEmpty()) {
                idInput.setText("");
                return WidgetEventResult.CONTINUE_PROCESSING;
            }

            ResourceLocation id = newValue.getItem().getRegistryName();
            String suggestedTreeId = id.toString().replaceAll("_sapling$", "");
            idInput.setText(suggestedTreeId);
            return WidgetEventResult.CONTINUE_PROCESSING;
        });

        yOffset += 6;
        WidgetTextBox growLabel = new WidgetTextBox(I18n.format("bonsaitrees.gui.tree_creator.label.growtime"), 0x90000000);
        growLabel.setDimensions(2, yOffset, width-10, 9);
        this.add(growLabel);
        yOffset += 9;

        yOffset += 1;
        WidgetIntegerSelect ticksInput = new WidgetIntegerSelect(0, Integer.MAX_VALUE, 300);
        ticksInput.setId(TreeCreatorScreen.STATE_BASETICKS);
        ticksInput.setDimensions(3, yOffset, width-14, 12);
        this.add(ticksInput);
        yOffset += 12;

        yOffset += 6;
        WidgetTextBox tagsLabel = new WidgetTextBox(I18n.format("bonsaitrees.gui.tree_creator.label.tags"), 0x90000000);
        tagsLabel.setDimensions(2, yOffset, width-10, 9);
        this.add(tagsLabel);
        yOffset += 9;

        yOffset += 1;
        WidgetInputField tagsInput = new WidgetInputField();
        tagsInput.setId(TreeCreatorScreen.STATE_TAGS);
        tagsInput.setText("dirt,grass");
        tagsInput.setDimensions(3, yOffset, width-14, 12);
        this.add(tagsInput);
        yOffset += 12;

    }
}
