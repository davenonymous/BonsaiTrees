package com.davenonymous.bonsaitrees2.gui.widgets;

import com.davenonymous.libnonymous.gui.framework.ColorHelper;
import com.davenonymous.libnonymous.gui.framework.event.MouseClickEvent;
import com.davenonymous.libnonymous.gui.framework.event.WidgetEventResult;
import com.davenonymous.libnonymous.gui.framework.util.FontAwesomeIcons;
import com.davenonymous.libnonymous.gui.framework.widgets.*;
import com.davenonymous.libnonymous.render.MultiblockBlockModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ModelPanel extends WidgetPanel {
    WidgetFontAwesome warningIcon;

    public ModelPanel(int width, int height, MultiblockBlockModel model) {
        super();
        this.setSize(width, height);

        WidgetTextBox modelPanelLabel = new WidgetTextBox(I18n.format("bonsaitrees.gui.tree_creator.label.preview", model.getBlockCount()), 0xFF808080);
        modelPanelLabel.setDimensions(0, 0, width-10, 9);
        this.add(modelPanelLabel);

        WidgetMultiBlockModel modelRenderWidget = new WidgetMultiBlockModel(model);
        modelRenderWidget.setDimensions(32, 20, 74, 74);
        this.add(modelRenderWidget);

        WidgetTextBox blockTypesLabel = new WidgetTextBox(I18n.format("bonsaitrees.gui.tree_creator.label.blocktypes", model.refMap.size()), 0xFF808080);
        blockTypesLabel.setDimensions(0, 120, width-40, 9);
        this.add(blockTypesLabel);

        WidgetList blockTypesList = new WidgetList();
        blockTypesList.setDimensions(0, 130, width-10, 70);
        this.add(blockTypesList);

        warningIcon = new WidgetFontAwesome(FontAwesomeIcons.SOLID_ExclamationTriangle, WidgetFontAwesome.IconSize.MEDIUM);
        warningIcon.setColor(ColorHelper.COLOR_ERRORED);
        warningIcon.setPosition(width-30, 110);
        this.updateWarningIcon(model);
        this.add(warningIcon);

        ArrayList<BlockStateListEntry> listEntries = new ArrayList<>();
        for(Map.Entry<BlockState, List<BlockPos>> entry : model.reverseBlocks.entrySet()) {
            BlockState state = entry.getKey();
            int count = entry.getValue().size();

            BlockStateListEntry listEntry = new BlockStateListEntry(state, count, width-12);
            listEntry.deleteIcon.addListener(MouseClickEvent.class, (event, widget) -> {
                model.removeBlockState(state);
                modelPanelLabel.setText(I18n.format("bonsaitrees.gui.tree_creator.label.preview", model.getBlockCount()));
                blockTypesLabel.setText(I18n.format("bonsaitrees.gui.tree_creator.label.blocktypes", model.refMap.size()));
                blockTypesList.remove(listEntry);
                blockTypesList.scrollUp();
                this.updateWarningIcon(model);
                return WidgetEventResult.HANDLED;
            });
            listEntries.add(listEntry);
        }
        listEntries.sort(Comparator.comparingInt(o -> -o.count));
        listEntries.forEach(blockTypesList::addListEntry);
    }

    private void updateWarningIcon(MultiblockBlockModel model) {
        boolean tooManyTypes = model.refMap.size() > 26;
        boolean tooManyBlocks = model.blocks.size() > 512;

        List<ITextComponent> tooltipLines = new ArrayList<>();
        if(tooManyBlocks) {
            tooltipLines.add(new TranslationTextComponent("bonsaitrees.gui.tree_creator.warning.too_many_blocks"));
        }

        if(tooManyTypes) {
            tooltipLines.add(new TranslationTextComponent("bonsaitrees.gui.tree_creator.warning.too_many_types"));
        }

        if(tooManyBlocks || tooManyTypes) {
            tooltipLines.add(new TranslationTextComponent("bonsaitrees.gui.tree_creator.warning.isolate"));
            this.warningIcon.setTooltipLines(tooltipLines);
            this.warningIcon.setVisible(true);
        } else {
            this.warningIcon.setVisible(false);
        }

    }
}
