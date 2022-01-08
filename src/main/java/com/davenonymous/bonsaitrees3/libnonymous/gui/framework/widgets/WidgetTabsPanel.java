package com.davenonymous.bonsaitrees3.libnonymous.gui.framework.widgets;

import com.davenonymous.bonsaitrees3.libnonymous.gui.framework.event.MouseClickEvent;
import com.davenonymous.bonsaitrees3.libnonymous.gui.framework.event.TabChangedEvent;
import com.davenonymous.bonsaitrees3.libnonymous.gui.framework.event.WidgetEventResult;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WidgetTabsPanel extends WidgetPanel {
	private List<WidgetPanel> pages = new ArrayList<>();
	private Map<WidgetPanel, ItemStack> pageStacks = new HashMap<>();
	private Map<WidgetPanel, List<Component>> pageTooltips = new HashMap<>();
	private TabDockEdge edge = TabDockEdge.WEST;

	private WidgetPanel activePanel = null;

	public WidgetTabsPanel() {
		super();
	}

	public WidgetTabsPanel setEdge(TabDockEdge edge) {
		this.edge = edge;
		return this;
	}

	public void addPage(WidgetPanel panel, ItemStack buttonStack) {
		this.addPage(panel, buttonStack, null);
	}

	public void addPage(WidgetPanel panel, ItemStack buttonStack, List<Component> tooltip) {
		panel.setWidth(this.width);
		panel.setHeight(this.height);

		pages.add(panel);
		pageStacks.put(panel, buttonStack);

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
	}

	public void setActivePage(int page) {
		if(page < 0 || page >= pages.size()) {
			return;
		}

		activePanel.setVisible(false);
		pages.get(page).setVisible(true);

		WidgetPanel tmpOld = activePanel;
		activePanel = pages.get(page);

		this.fireEvent(new TabChangedEvent(tmpOld, pages.get(page)));
	}

	public WidgetPanel getButtonsPanel() {
		WidgetPanel result = new WidgetPanel();
		int y = 0;
		int x = edge == TabDockEdge.NORTH ? 4 : 0;
		for(WidgetPanel page : pages) {
			WidgetTabsButton button = new WidgetTabsButton(this, page, pageStacks.get(page), edge);
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
			result.add(button);

			if(pageTooltips.containsKey(page)) {
				button.addTooltipLine(pageTooltips.get(page));
			}
		}

		return result;
	}

	public enum TabDockEdge {
		WEST, NORTH
	}

	private static class WidgetTabsButton extends Widget {
		WidgetTabsPanel parent;
		WidgetPanel page;
		ItemStack pageStack;
		TabDockEdge edge;

		public WidgetTabsButton(WidgetTabsPanel parent, WidgetPanel page, ItemStack pageStack, TabDockEdge edge) {
			this.parent = parent;
			this.page = page;
			this.pageStack = pageStack;
			this.edge = edge;

			this.addListener(MouseClickEvent.class, (event, widget) -> {
				setActive(true);
				return WidgetEventResult.HANDLED;
			});
		}

		public void setActive(boolean fireEvent) {
			parent.activePanel.setVisible(false);
			page.setVisible(true);
			WidgetPanel tmpOld = parent.activePanel;
			parent.activePanel = page;

			if(fireEvent) {
				this.parent.fireEvent(new TabChangedEvent(tmpOld, page));
			}
		}

		private boolean isActive() {
			return this.parent.activePanel == this.page;
		}

		private boolean isFirst() {
			return this.parent.pages.indexOf(this.page) == 0;
		}

		@Override
		public void draw(PoseStack pPoseStack, Screen screen) {
            /*
            RenderSystem.pushMatrix();

            screen.getMinecraft().getTextureManager().bindTexture(GUI.tabIcons);

            RenderSystem.disableLighting();
            RenderSystem.color3f(1F, 1F, 1F); //Forge: Reset color in case Items change it.
            RenderSystem.enableBlend(); //Forge: Make sure blend is enabled else tabs show a white border.


            // Defaults are for the West edge
            int buttonWidth = 32;
            if(!isActive()) {
                buttonWidth = 28;
            }

            int buttonHeight = 28;

            int textureY = isFirst() ? 28 : 28*2;
            int textureX = isActive() ? 32 : 0;

            int x = 0;
            int y = 0;

            int iconX = 9;
            int iconY = 5;

            if(edge == TabDockEdge.NORTH) {
                buttonHeight = 31;
                buttonWidth = 31;

                if(isActive()) {
                    textureY = 104;
                    textureX = 0;
                } else {
                    textureY = 104;
                    textureX = 31;
                }

                iconX = 7;
                iconY = 7;
            }

            GuiUtils.drawTexturedModalRect(x, y, textureX, textureY, buttonWidth, buttonHeight, 0.0f);

            screen.getMinecraft().getItemRenderer().renderItemAndEffectIntoGUI(pageStack, iconX, iconY);
            RenderHelper.enableStandardItemLighting();

            RenderSystem.popMatrix();

             */
		}
	}
}
