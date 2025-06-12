package com.davenonymous.bonsaitrees.lib.gui.widgets;

import com.davenonymous.bonsaitrees.lib.gui.event.*;
import net.minecraft.resources.ResourceLocation;

public class WidgetCollapsable extends WidgetPanel {
	public enum CollapseAxis {
		X,
		Y
	}

	private CollapseAxis collapseAxis;
	private Widget button;
	private Widget content;
	private int padding = 2;

	private boolean collapsed = false;

	private static final ResourceLocation BACKWARD = ResourceLocation.withDefaultNamespace("textures/gui/sprites/recipe_book/page_backward.png");
	private static final ResourceLocation FORWARD = ResourceLocation.withDefaultNamespace("textures/gui/sprites/recipe_book/page_forward.png");
	private static final ResourceLocation BACKWARD_HIGH = ResourceLocation.withDefaultNamespace("textures/gui/sprites/recipe_book/page_backward_highlighted.png");
	private static final ResourceLocation FORWARD_HIGH = ResourceLocation.withDefaultNamespace("textures/gui/sprites/recipe_book/page_forward_highlighted.png");

	public static WidgetCollapsable standard(Widget content, CollapseAxis collapseAxis) {
		var standardButton = new WidgetImage(BACKWARD);
		standardButton.setSize(6, 9);
		standardButton.setTextureSize(12, 17);
		standardButton.addListener(
			WidgetCollapseEvent.class, (event, widget) -> {
				standardButton.setImage(event.collapsed() ? FORWARD : BACKWARD);
				return WidgetEventResult.HANDLED;
			}
		);
		standardButton.addListener(
			MouseEnterEvent.class, (event, widget) -> {
				if(standardButton.image == BACKWARD) {
					standardButton.setImage(BACKWARD_HIGH);
				} else if(standardButton.image == FORWARD) {
					standardButton.setImage(FORWARD_HIGH);
				}
				return WidgetEventResult.HANDLED;
			}
		);
		standardButton.addListener(
			MouseExitEvent.class, (event, widget) -> {
				if(standardButton.image == BACKWARD_HIGH) {
					standardButton.setImage(BACKWARD);
				} else if(standardButton.image == FORWARD_HIGH) {
					standardButton.setImage(FORWARD);
				}
				return WidgetEventResult.HANDLED;
			}
		);


		return new WidgetCollapsable(standardButton, content, collapseAxis);
	}

	public WidgetCollapsable(Widget button, Widget content, CollapseAxis collapseAxis) {
		super();
		this.button = button;
		this.content = content;
		this.collapseAxis = collapseAxis;

		this.add(button);
		this.add(content);

		this.updateContent();

		this.button.addListener(
			MouseClickEvent.class, (event, widget) -> {
				this.collapsed = !this.collapsed;
				this.updateContent();
				this.button.fireEvent(new WidgetCollapseEvent(this.collapsed));
				return WidgetEventResult.CONTINUE_PROCESSING;
			}
		);
	}


	private void updateContent() {
		if(collapseAxis == CollapseAxis.X) {
			this.button.setHeight(this.content.height);
		} else {
			this.button.setWidth(this.content.width);
		}

		if(this.collapsed) {
			this.content.setVisible(false);
			if(collapseAxis == CollapseAxis.X) {
				this.setWidth(this.button.width + 2 * padding);
				this.setHeight(this.content.height);
			} else {
				this.setHeight(this.button.height + 2 * padding);
				this.setWidth(this.content.width);
			}
		} else {
			this.content.setVisible(true);
			if(collapseAxis == CollapseAxis.X) {
				this.content.setX(this.button.width + 2);
				this.setWidth(this.button.width + this.content.width + 3 * padding);
				this.setHeight(this.content.height);
			} else {
				this.content.setY(this.button.height + 2);
				this.setHeight(this.button.height + this.content.height + 3 * padding);
				this.setWidth(this.content.width);
			}
		}
	}


	public Widget getButton() {
		return button;
	}

	public WidgetCollapsable setButton(Widget button) {
		this.button = button;
		return this;
	}

	public CollapseAxis getCollapseAxis() {
		return collapseAxis;
	}

	public WidgetCollapsable setCollapseAxis(CollapseAxis collapseAxis) {
		this.collapseAxis = collapseAxis;
		return this;
	}

	public Widget getContent() {
		return content;
	}

	public WidgetCollapsable setContent(Widget content) {
		this.content = content;
		return this;
	}

	public int getPadding() {
		return padding;
	}

	public WidgetCollapsable setPadding(int padding) {
		this.padding = padding;
		return this;
	}
}
