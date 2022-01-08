package com.davenonymous.bonsaitrees3.libnonymous.gui.framework.widgets;


import com.davenonymous.bonsaitrees3.libnonymous.gui.framework.ISelectable;
import com.davenonymous.bonsaitrees3.libnonymous.gui.framework.event.ListSelectionEvent;
import com.davenonymous.bonsaitrees3.libnonymous.gui.framework.event.MouseClickEvent;
import com.davenonymous.bonsaitrees3.libnonymous.gui.framework.event.MouseScrollEvent;
import com.davenonymous.bonsaitrees3.libnonymous.gui.framework.event.WidgetEventResult;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;


public class WidgetList extends WidgetPanel {
	public int padding = 2;
	public int scrollLines = 1;

	private int lineOffset = 0;
	private int lastVisibleLine = 0;
	private int visibleWidgets = 0;

	protected int selected = -1;

	boolean autoSelectFirstEntry = false;

	public WidgetList() {
		super();

		this.addListener(MouseScrollEvent.class, (event, widget) -> {
			if(widget.isPosInside(event.mouseX, event.mouseY)) {
				if(event.up) {
					this.scrollUp();
				} else {
					this.scrollDown();
				}
			}

			return WidgetEventResult.CONTINUE_PROCESSING;
		});
	}

	public Widget getScrollUpButton(int color) {
		WidgetTextBox box = new WidgetTextBox("<") {
			@Override
			public void draw(PoseStack pPoseStack, Screen screen) {
				if(lineOffset == 0) {
					return;
				}

                /*
                RenderSystem.pushMatrix();
                RenderSystem.translatef(7.0f, 0.0f, 0.0f);
                RenderSystem.rotatef(90.0f, 0.0f, 0.0f, 1.0f);
                super.draw(screen);
                RenderSystem.popMatrix();
                 */
			}
		};
		box.setTextColor(color);
		box.setDimensions(0, 0, 7, 6);
		box.addListener(MouseClickEvent.class, (event, widget) -> {
			this.scrollUp();
			return WidgetEventResult.HANDLED;
		});
		return box;
	}

	public Widget getScrollDownButton(int color) {
		WidgetTextBox box = new WidgetTextBox(">") {
			@Override
			public void draw(PoseStack pPoseStack, Screen screen) {
				if(lastVisibleLine == getTotalLines() - 1) {
					return;
				}

                /*
                RenderSystem.pushMatrix();
                RenderSystem.translatef(7.0f, 0.0f, 0.0f);
                RenderSystem.rotatef(90.0f, 0.0f, 0.0f, 1.0f);
                super.draw(screen);
                RenderSystem.popMatrix();
                 */
			}
		};
		box.setTextColor(color);
		box.setDimensions(0, 0, 7, 6);
		box.addListener(MouseClickEvent.class, (event, widget) -> {
			this.scrollDown();
			return WidgetEventResult.HANDLED;
		});
		return box;
	}

	@Override
	public void clear() {
		super.clear();
		this.selected = -1;
		this.fireEvent(new ListSelectionEvent(this.selected));
	}

	public void scrollToTop() {
		this.lineOffset = 0;
	}

	public void scrollUp() {
		this.lineOffset = Math.max(0, this.lineOffset - this.scrollLines);
		this.updateWidgets();
	}

	public void scrollDown() {
		if(lastVisibleLine == getTotalLines() - 1) {
			return;
		}

		this.lineOffset += this.scrollLines;
		this.updateWidgets();
	}

	private ISelectable getSelectedWidget() {
		if(this.selected == -1) {
			return null;
		}
		return (ISelectable) this.children.get(this.selected);
	}

	public void deselect() {
		if(this.selected == -1) {
			return;
		}

		this.getSelectedWidget().setSelected(false);
		this.selected = -1;
		this.fireEvent(new ListSelectionEvent(this.selected));
	}

	public void select(int index) {
		if(index == -1) {
			this.deselect();
			return;
		}

		if(this.selected != -1) {
			this.getSelectedWidget().setSelected(false);
		}
		this.selected = index;
		this.getSelectedWidget().setSelected(true);
		this.fireEvent(new ListSelectionEvent(this.selected));
	}

	public int getTotalLines() {
		return this.children.size();
	}

	public int getLineHeight(int line) {
		if(line >= this.children.size()) {
			return 0;
		}

		return this.children.get(line).height;
	}

	@Override
	public void draw(PoseStack pPoseStack, Screen screen) {
		int backgroundColor = 0xFF333333;
		int borderColor = 0xFF000000;
		int selectedBackgroundColor = 0xFF555555;


/*
        // Draw background
        boolean drawScrollbar = visibleWidgets < getTotalLines();
        int scrollbarWidth = drawScrollbar ? 8 : 0;

        int listWidth = width-scrollbarWidth;
        GuiUtils.drawGradientRect(0, 0, 0, listWidth, height, borderColor, borderColor);
        GuiUtils.drawGradientRect(0, 1, 1, listWidth-1, height-1, backgroundColor, backgroundColor);

        // Draw scrollbars
        if(drawScrollbar) {
            int scrollBarX = listWidth + 1;
            GuiUtils.drawGradientRect(0, scrollBarX, 0, listWidth + scrollbarWidth, height, backgroundColor, backgroundColor);

            int linesBefore = lineOffset;
            int linesAfter = getTotalLines() - lastVisibleLine - 1;

            int scrollColor = 0xFF666666;

            float ratioBefore = (float)linesBefore / getTotalLines();
            float ratioSize = (float)visibleWidgets / getTotalLines();

            int topOffset = (int) (height * ratioBefore);
            int paddleHeight = (int) (height * ratioSize);

            if(topOffset == 0) {
                topOffset = 1;
            }
            GuiUtils.drawGradientRect(0, scrollBarX+1, topOffset, listWidth + scrollbarWidth -1, topOffset+paddleHeight, scrollColor, scrollColor);
        }

        //Logz.info("Rendering lines %d to %d", lineOffset, lastVisibleLine);

        if(selected >= lineOffset && selected <= lastVisibleLine) {
            // We need to high-light a specific line
            int yOffset = 0;
            for(int line = lineOffset; line < selected; line++) {
                Widget widget = this.children.get(line);
                yOffset += widget.height;
            }

            Widget selectedWidget = this.children.get(selected);

            GuiUtils.drawGradientRect(0, 1, yOffset+1, listWidth-1, yOffset+1+selectedWidget.height-1, selectedBackgroundColor, selectedBackgroundColor);
        }

*/
		super.draw(pPoseStack, screen);
	}

	public <T extends Widget & ISelectable> void addListEntry(T widget) {
		if(widget.height <= 0) {
			// Logz.warn("Heightless widget [%s] added to list. This will cause problems.", widget);
		}
		if(widget.height > this.height) {
			// Logz.warn("List has an entry larger than the list itself. This will cause problems.", widget);
		}

		widget.addListener(MouseClickEvent.class, (event, clickedWidget) -> {
			if(this.selected == this.children.indexOf(widget)) {
				this.selected = -1;
				widget.setSelected(false);
			} else {
				if(this.selected != -1 && this.selected < this.children.size()) {
					Widget oldSelection = this.children.get(selected);
					if(oldSelection instanceof ISelectable) {
						((ISelectable) oldSelection).setSelected(false);
					}
				}

				this.selected = this.children.indexOf(widget);
				widget.setSelected(true);
			}

			this.fireEvent(new ListSelectionEvent(this.selected));

			return WidgetEventResult.CONTINUE_PROCESSING;
		});

		super.add(widget);

		updateWidgets();
	}

	@Override
	public void remove(Widget widget) {
		super.remove(widget);
		this.updateWidgets();
	}

	public void updateWidgets() {
		int visibleHeight = padding;
		boolean exceededListHeight = false;
		visibleWidgets = 0;
		for(int line = 0; line < this.children.size(); line++) {
			Widget widget = this.children.get(line);

			if(line < lineOffset) {
				// Widget is scrolled past -> hide
				widget.setVisible(false);
				continue;
			}

			if(visibleHeight + widget.height > this.height - padding) {
				// Widget won't fit -> no more widgets from here on out
				exceededListHeight = true;
			}

			if(exceededListHeight) {
				widget.setVisible(false);
				continue;
			}

			if(line == this.selected && widget instanceof ISelectable) {
				((ISelectable) widget).setSelected(true);
			}

			widget.setVisible(true);
			widget.setY(visibleHeight);
			widget.setX(padding);
			visibleHeight += widget.height;
			lastVisibleLine = line;
			visibleWidgets++;
		}
	}
}
