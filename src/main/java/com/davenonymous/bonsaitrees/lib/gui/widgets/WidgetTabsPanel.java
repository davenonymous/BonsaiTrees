package com.davenonymous.bonsaitrees.lib.gui.widgets;

import com.davenonymous.bonsaitrees.lib.gui.GUIHelper;
import com.davenonymous.bonsaitrees.lib.gui.event.TabChangedEvent;
import com.davenonymous.bonsaitrees.lib.gui.event.WidgetEventResult;
import com.davenonymous.bonsaitrees.lib.gui.event.WidgetSizeChangeEvent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WidgetTabsPanel extends WidgetPanel {
	protected final List<WidgetPanel> pages = new ArrayList<>();
	protected final Map<WidgetPanel, Widget> pageButtonWidget = new HashMap<>();
	protected final Map<WidgetPanel, List<Component>> pageTooltips = new HashMap<>();
	protected TabDockEdge edge = TabDockEdge.WEST;

	protected WidgetPanel activePanel = null;
	protected WidgetPanel buttonsPanel = null;

	public WidgetTabsPanel() {
		super();

		this.buttonsPanel = new WidgetPanel();
		this.buttonsPanel.setSize(200, 32);
		this.add(buttonsPanel);

		this.addListener(
			WidgetSizeChangeEvent.class, (event, widget) -> {
				this.buttonsPanel.setDimensions(0, 0, event.newWidth(), 32);
				for(var page : pages) {
					page.setWidth(this.width);
					page.setHeight(this.height - 32);
				}
				return WidgetEventResult.CONTINUE_PROCESSING;
			}
		);
	}

	public WidgetTabsPanel setEdge(TabDockEdge edge) {
		this.edge = edge;
		return this;
	}

	@Override
	public void clear() {
		super.clear();
		this.pages.clear();
		this.pageButtonWidget.clear();
		this.pageTooltips.clear();
		this.children.clear();

		this.add(buttonsPanel);
	}

	public void addPage(WidgetPanel panel, Widget buttonStack) {
		this.addPage(panel, buttonStack, null);
	}

	public void addPage(WidgetPanel panel, Widget buttonStack, List<Component> tooltip) {
		panel.setWidth(this.width);
		panel.setHeight(this.height - 32);
		panel.setY(32);
		panel.parent = this;

		pages.add(panel);
		pageButtonWidget.put(panel, buttonStack);

		if(activePanel == null) {
			activePanel = panel;
			activePanel.setVisible(true);
		} else {
			panel.setVisible(false);
		}

		if(tooltip != null) {
			pageTooltips.put(panel, tooltip);
		}

		this.add(panel);

		updateButtonsPanel();
	}

	public void setActivePage(WidgetPanel page) {
		if(!pages.contains(page)) {
			return;
		}

		setActivePage(pages.indexOf(page));
	}

	public void setActivePage(int page) {
		if(page < 0 || page >= pages.size()) {
			return;
		}

		var oldPage = activePanel;
		var newPage = pages.get(page);
		activePanel.setVisible(false);
		newPage.setVisible(true);
		activePanel = newPage;

		this.fireEvent(new TabChangedEvent(oldPage, newPage));
	}

	public void updateButtonsPanel() {
		this.buttonsPanel.clear();

		int y = 0;
		int x = edge == TabDockEdge.NORTH ? 4 : 0;
		for(WidgetPanel page : pages) {
			WidgetTabsButton button = new WidgetTabsButton(this, page, pageButtonWidget.get(page), edge);
			button.setPosition(x, y);
			switch(edge) {
				default:
				case WEST:
					button.setSize(32, 28);
					y += 28;
					break;
				case NORTH:
					button.setSize(31, 32);
					x += 31;
					break;
			}
			buttonsPanel.add(button);

			if(pageTooltips.containsKey(page)) {
				button.addTooltipLine(pageTooltips.get(page));
			}
		}
	}

	@Override
	public void draw(GuiGraphics guiGraphics, Screen screen) {
		GUIHelper.drawWindow(guiGraphics, this.width, this.height - 28, false, this.x - 9, this.y + 19);
		super.draw(guiGraphics, screen);
	}

	public enum TabDockEdge {
		WEST, NORTH
	}

}
