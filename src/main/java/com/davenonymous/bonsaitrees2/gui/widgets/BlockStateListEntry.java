package com.davenonymous.bonsaitrees2.gui.widgets;

import com.davenonymous.libnonymous.gui.framework.ColorHelper;
import com.davenonymous.libnonymous.gui.framework.util.FontAwesomeIcons;
import com.davenonymous.libnonymous.gui.framework.widgets.WidgetFontAwesome;
import com.davenonymous.libnonymous.gui.framework.widgets.WidgetListEntry;
import com.davenonymous.libnonymous.gui.framework.widgets.WidgetTextBox;
import net.minecraft.block.BlockState;
import net.minecraft.state.IProperty;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.List;

public class BlockStateListEntry extends WidgetListEntry {
    public BlockState state;
    public int count;
    public WidgetFontAwesome deleteIcon;

    public BlockStateListEntry(BlockState state, int count, int columnWidth) {
        this.state = state;
        this.count = count;

        this.setSize(columnWidth, 12);

        String fullText = String.format("%dx %s", count, state.getBlock().getNameTextComponent().getFormattedText());
        List<ITextComponent> tooltipLines = new ArrayList<>();
        for(IProperty property : state.getProperties()) {
            tooltipLines.add(new StringTextComponent(String.format("%s: %s", property.getName(), state.get(property).toString())));
        }

        WidgetTextBox labelWidget = new WidgetTextBox(fullText, 0xA0FFFFFF);
        labelWidget.setTooltipLines(tooltipLines);
        labelWidget.setDimensions(0, 1, columnWidth, 9);
        this.add(labelWidget);

        deleteIcon = new WidgetFontAwesome(FontAwesomeIcons.SOLID_Trash, WidgetFontAwesome.IconSize.TINY);
        deleteIcon.setColor(ColorHelper.COLOR_DISABLED);
        deleteIcon.setPosition(columnWidth-20, 0);
        this.add(deleteIcon);
    }
}
