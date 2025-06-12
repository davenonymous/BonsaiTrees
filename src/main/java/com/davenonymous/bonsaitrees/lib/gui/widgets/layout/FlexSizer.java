package com.davenonymous.bonsaitrees.lib.gui.widgets.layout;

import com.davenonymous.bonsaitrees.lib.gui.event.VisibilityChangedEvent;
import com.davenonymous.bonsaitrees.lib.gui.event.WidgetEventResult;
import com.davenonymous.bonsaitrees.lib.gui.event.WidgetSizeChangeEvent;
import com.davenonymous.bonsaitrees.lib.gui.widgets.Widget;
import com.davenonymous.bonsaitrees.lib.gui.widgets.WidgetPanel;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public abstract class FlexSizer extends WidgetPanel {
	public enum FlexMode {
		FIXED,
		FLEX,
		CONTENT
	}

	public enum FlexAlign {
		START,
		CENTER,
		END
	}

	public enum FlexDirection {
		X,
		Y
	}

	Map<Integer, Integer> flexValues;
	Map<Integer, FlexMode> flexModes;
	Map<Integer, Widget> flexWidgets;

	// TODO: These should contain the calculated values
	Map<Integer, Integer> realBoxOffsets;
	Map<Integer, Integer> realBoxSize;

	FlexAlign flexAlign = FlexAlign.START;
	FlexDirection flexDirection = FlexDirection.X;
	int padding = 0;

	public FlexSizer() {
		this.flexWidgets = new HashMap<>();
		this.flexValues = new HashMap<>();
		this.flexModes = new HashMap<>();
		this.realBoxOffsets = new HashMap<>();
		this.realBoxSize = new HashMap<>();

		this.addListener(
			WidgetSizeChangeEvent.class, ((event, widget) -> {
				update(this);
				return WidgetEventResult.CONTINUE_PROCESSING;
			})
		);
	}

	public boolean isHorizontal() {
		return flexDirection == FlexDirection.X;
	}

	public boolean isVertical() {
		return flexDirection == FlexDirection.Y;
	}

	public FlexSizer setFlexDirection(FlexDirection direction) {
		this.flexDirection = direction;
		return this;
	}

	public FlexSizer setFlexAlign(FlexAlign alignment) {
		this.flexAlign = alignment;
		return this;
	}

	public FlexSizer setPadding(int padding) {
		this.padding = padding;
		return this;
	}

	public FlexSizer addFixedBox(Widget box, int size) {
		return addBox(box, FlexMode.FIXED, size);
	}

	public FlexSizer addFlexBox(Widget box, int flexWeight) {
		return addBox(box, FlexMode.FLEX, flexWeight);
	}

	public FlexSizer addContentBox(Widget box) {
		box.addListener(
			WidgetSizeChangeEvent.class, ((event, widget) -> {
				if(event.xChanged() && isHorizontal()) {
					update(widget);
				} else if(event.yChanged() && isVertical()) {
					update(widget);
				}
				return WidgetEventResult.CONTINUE_PROCESSING;
			})
		);

		return addBox(box, FlexMode.CONTENT, 0);
	}

	private FlexSizer addBox(Widget box, FlexMode mode, int value) {
		flexValues.put(flexValues.size(), value);
		flexModes.put(flexModes.size(), mode);
		flexWidgets.put(flexWidgets.size(), box);

		update(null);
		if(isHorizontal()) {
			box.y = 0;
			box.height = this.height;
		} else {
			box.x = 0;
			box.width = this.width;
		}
		add(box);
		update(null);
		box.addListener(
			VisibilityChangedEvent.class, ((event, widget) -> {
				update(null);
				return WidgetEventResult.CONTINUE_PROCESSING;
			})
		);
		return this;
	}

	public FlexSizer setFlex(int boxNum, int value) {
		flexValues.put(boxNum, value);
		return this;
	}

	public FlexSizer setFlexMode(int boxNum, FlexMode mode) {
		flexModes.put(boxNum, mode);
		return this;
	}


	public FlexSizer update(@Nullable Widget trigger) {
		int totalFlexWeight = 0;
		int totalFixedPx = 0;
		int totalContentPx = 0;
		int totalPadding = (flexWidgets.size() - 1) * padding;

		for(var entry : flexModes.entrySet()) {
			int columnIndex = entry.getKey();
			if(!flexWidgets.get(columnIndex).isVisible()) {
				continue;
			}
			int value = flexValues.get(columnIndex);
			switch(entry.getValue()) {
				case FIXED:
					totalFixedPx += value;
					break;
				case FLEX:
					totalFlexWeight += value;
					break;
				case CONTENT:
					var box = flexWidgets.get(columnIndex);
					totalContentPx += isHorizontal() ? box.width : box.height;
					break;
			}
		}

		int thisSize = isHorizontal() ? this.width : this.height;
		int remainingPx = thisSize - totalFixedPx - totalContentPx - totalPadding;
		int pxPerFlex = totalFlexWeight > 0 ? remainingPx / totalFlexWeight : 0;

		int offset = 0;
		for(var entry : flexValues.entrySet()) {
			int columnIndex = entry.getKey();
			int value = entry.getValue();
			Widget box = flexWidgets.get(columnIndex);
			if(!box.isVisible()) {
				continue;
			}

			switch(flexModes.get(columnIndex)) {
				case FIXED:
					realBoxSize.put(columnIndex, value);
					realBoxOffsets.put(columnIndex, offset);
					offset += value + padding;
					break;
				case FLEX:
					int flexPx = value * pxPerFlex;
					realBoxSize.put(columnIndex, flexPx);
					realBoxOffsets.put(columnIndex, offset);
					offset += flexPx + padding;
					break;
				case CONTENT:
					int contentPx = isHorizontal() ? box.width : box.height;
					realBoxSize.put(columnIndex, contentPx);
					realBoxOffsets.put(columnIndex, offset);
					offset += contentPx + padding;
					break;
			}

			if(trigger == null || trigger != box) {
				if(flexDirection == FlexDirection.X) {
					box.x = realBoxOffsets.get(columnIndex);
					box.width = realBoxSize.get(columnIndex);
					box.height = this.height;
				} else {
					box.y = realBoxOffsets.get(columnIndex);
					box.height = realBoxSize.get(columnIndex);
					box.width = this.width;
				}
			}
		}

		return this;
	}
}
