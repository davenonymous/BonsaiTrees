package com.davenonymous.bonsaitrees.lib.gui.widgets;

import com.davenonymous.bonsaitrees.lib.gui.ISelectable;
import com.davenonymous.bonsaitrees.lib.gui.event.*;
import com.davenonymous.bonsaitrees.lib.gui.tooltip.WrappedStringTooltipComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class WidgetTreeNode {
	Widget widget;
	private boolean expanded;
	List<WidgetTreeNode> children;
	private TreeNodeEntryPanel indentedBox = null;
	private Widget expansionButton = null;
	private int indentPixels = 10;

	public WidgetTreeNode(Widget widget) {
		this.widget = widget;
		this.expanded = false;
		this.children = new ArrayList<>();
	}

	public WidgetTreeNode setIndentPixels(int indentPixels) {
		this.indentPixels = indentPixels;
		return this;
	}

	public Widget widget() {
		return widget;
	}

	public WidgetTreeNode addChild(Widget widget) {
		WidgetTreeNode node = new WidgetTreeNode(widget);
		node.setIndentPixels(this.indentPixels);
		children.add(node);
		return node;
	}

	public WidgetTreeNode setExpanded(boolean expanded) {
		this.expanded = expanded;
		if(this.expansionButton != null) {
			this.expansionButton.fireEvent(new ValueChangedEvent<Boolean>(!expanded, expanded));
		}
		return this;
	}

	public Widget expansionButton() {
		return expansionButton;
	}

	private Supplier<Widget> buttonProvider = null;

	public WidgetTreeNode setExtensionButtonProvider(Supplier<Widget> buttonProvider) {
		this.buttonProvider = buttonProvider;
		return this;
	}

	public TreeNodeEntryPanel getIndented(int indentLevel, int maxWidth) {
		TreeNodeEntryPanel box = new TreeNodeEntryPanel(this);
		box.setWidth(maxWidth);
		box.setHeight(widget.height);

		widget.setWidth(maxWidth - indentPixels * (indentLevel + 1));
		if(widget instanceof WidgetTextBox tb) {
			tb.autoWidth();
			if(tb.width > maxWidth - indentPixels * (indentLevel + 1)) {
				widget.setWidth(maxWidth - indentPixels * (indentLevel + 1));
			}
		}


		boolean canExpand = !children.isEmpty();
		int expandButtonWidth = 0;
		if(canExpand) {
			Widget expander;
			if(buttonProvider != null) {
				expander = buttonProvider.get();
			} else {
				WidgetTextBox textButton = new WidgetTextBox(expanded ? "-" : "+");
				textButton.setStyle(style -> style.withColor(ChatFormatting.DARK_GRAY));
				textButton.setSize(8, 10);
				textButton.addListener(
					ValueChangedEvent.class, (event, widget1) -> {
						if(event.newValue instanceof Boolean newValue && newValue) {
							textButton.setText("-");
						} else {
							textButton.setText("+");
						}
						return WidgetEventResult.HANDLED;
					}
				);
				expander = textButton;
			}
			expandButtonWidth = expander.width;

			int expanderYOffset = (widget.height + 4 - expander.height) / 2;
			expander.setPosition(indentPixels * indentLevel, expanderYOffset);
			box.add(expander);
			if(count() <= 256) {
				expander.addListener(
					MouseClickEvent.class, (event, widget1) -> {
						setExpanded(!expanded);
						box.fireEvent(new ValueChangedEvent<>(!expanded, expanded));
						return WidgetEventResult.HANDLED;
					}
				);
			} else {
				expander.setTooltipElements(
					WrappedStringTooltipComponent.yellow(I18n.get("bonsaitrees.gui.files.overflow.label")),
					WrappedStringTooltipComponent.gray(I18n.get("bonsaitrees.gui.files.overflow.tooltip", count()))
				);
			}
			box.add(expander);
			this.expansionButton = expander;
		}

		widget.setPosition(expandButtonWidth + (indentPixels * indentLevel), 2);
		box.add(widget);

		indentedBox = box;

		return indentedBox;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public int count() {
		return children.size();
	}

	public static class TreeNodeEntryPanel extends WidgetPanel implements ISelectable {
		private boolean selected = false;
		private WidgetTreeNode node;

		public TreeNodeEntryPanel(WidgetTreeNode node) {
			super();
			this.node = node;
			this.addListener(
				WidgetSizeChangeEvent.class, (event, widget) -> {
					node.widget.setWidth(event.newWidth());
					return WidgetEventResult.CONTINUE_PROCESSING;
				}
			);
		}

		@Override
		public boolean isSelected() {
			return selected;
		}

		@Override
		public void setSelected(boolean state) {
			selected = state;
			node.widget().fireEvent(new ListEntrySelectionEvent(state));
		}

		public WidgetTreeNode node() {
			return node;
		}
	}
}
