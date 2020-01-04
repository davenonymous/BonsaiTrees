package com.davenonymous.bonsaitrees2.gui.widgets;

import com.davenonymous.libnonymous.gui.framework.widgets.WidgetMultiBlockModel;
import com.davenonymous.libnonymous.gui.framework.widgets.WidgetPanel;
import com.davenonymous.libnonymous.gui.framework.widgets.WidgetTextBox;
import com.davenonymous.libnonymous.render.MultiblockBlockModel;
import net.minecraft.client.resources.I18n;

public class ModelPanel extends WidgetPanel {
    public ModelPanel(int width, int height, MultiblockBlockModel model) {
        super();
        this.setSize(width, height);

        WidgetMultiBlockModel modelRenderWidget = new WidgetMultiBlockModel(model);
        modelRenderWidget.setDimensions(32, 20, 74, 74);
        this.add(modelRenderWidget);

        WidgetTextBox modelPanelLabel = new WidgetTextBox(I18n.format("bonsaitrees.gui.tree_creator.label.preview", model.getBlockCount()), 0xFF808080);
        modelPanelLabel.setDimensions(0, 0, width-10, 9);
        this.add(modelPanelLabel);
    }
}
