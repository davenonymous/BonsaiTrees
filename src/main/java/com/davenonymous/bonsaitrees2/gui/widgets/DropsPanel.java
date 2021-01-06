package com.davenonymous.bonsaitrees2.gui.widgets;

import com.davenonymous.bonsaitrees2.registry.sapling.SaplingDrop;
import com.davenonymous.libnonymous.gui.framework.widgets.WidgetPanelWithValue;
import com.davenonymous.libnonymous.gui.framework.widgets.WidgetTextBox;
import net.minecraft.block.Blocks;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DropsPanel extends WidgetPanelWithValue<List<SaplingDrop>> {
    private final List<DropRowPanel> dropRows = new ArrayList<>();

    public DropsPanel(int width, int height) {
        super();
        this.setSize(width, height);

        int yOffset = 0;
        WidgetTextBox dropLabel = new WidgetTextBox(I18n.format("bonsaitrees.gui.tree_creator.label.drop"), 0xC0000000);
        dropLabel.setDimensions(2, yOffset, 25, 9);
        this.add(dropLabel);

        WidgetTextBox rollsLabel = new WidgetTextBox(I18n.format("bonsaitrees.gui.tree_creator.label.rolls"), 0xC0000000);
        rollsLabel.setDimensions(30, yOffset, 35, 9);
        this.add(rollsLabel);

        WidgetTextBox chanceLabel = new WidgetTextBox(I18n.format("bonsaitrees.gui.tree_creator.label.chance"), 0xC0000000);
        chanceLabel.setDimensions(81, yOffset, 40, 9);
        this.add(chanceLabel);

        yOffset += 11;

        for(int i = 0; i < 6; i++) {
            DropRowPanel dropRowPanel = new DropRowPanel();
            dropRowPanel.setDimensions(4, yOffset, width, 12);
            this.add(dropRowPanel);
            this.dropRows.add(dropRowPanel);

            if(i == 0) {
                dropRowPanel.ghostSlot.setValue(new ItemStack(Items.STICK));
                dropRowPanel.dropChance.setValue(20);
                dropRowPanel.dropRolls.setValue(3);
            }

            if(i == 1) {
                dropRowPanel.ghostSlot.setValue(new ItemStack(Blocks.OAK_LOG));
                dropRowPanel.dropChance.setValue(75);
                dropRowPanel.dropRolls.setValue(1);
            }

            if(i == 2) {
                dropRowPanel.ghostSlot.setValue(new ItemStack(Blocks.OAK_LEAVES));
                dropRowPanel.dropChance.setValue(10);
                dropRowPanel.dropRolls.setValue(1);
            }

            if(i == 3) {
                dropRowPanel.ghostSlot.setValue(new ItemStack(Items.OAK_SAPLING));
                dropRowPanel.dropChance.setValue(5);
                dropRowPanel.dropRolls.setValue(1);
            }

            if(i == 4) {
                dropRowPanel.ghostSlot.setValue(new ItemStack(Items.APPLE));
                dropRowPanel.dropChance.setValue(20);
                dropRowPanel.dropRolls.setValue(1);
            }

            yOffset += 18;
        }
    }


    @Override
    public List<SaplingDrop> getValue() {
        return this.dropRows.stream().filter(DropRowPanel::hasItem).map(DropRowPanel::getValue).collect(Collectors.toList());
    }

    @Override
    public void setValue(List<SaplingDrop> value) {
        // NO OP
    }
}
