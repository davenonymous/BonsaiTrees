package com.davenonymous.bonsaitrees.lib.gui;

import com.davenonymous.bonsaitrees.lib.gui.widgets.Widget;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.core.Vec3i;

public enum ContentAlignment {
	TOP_LEFT(true, false, false, true, false, false),
	TOP_CENTER(true, false, false, false, true, false),
	TOP_RIGHT(true, false, false, false, false, true),

	MIDDLE_LEFT(false, true, false, true, false, false),
	MIDDLE_CENTER(false, true, false, false, true, false),
	MIDDLE_RIGHT(false, true, false, false, false, true),

	BOTTOM_LEFT(false, false, true, true, false, false),
	BOTTOM_CENTER(false, false, true, false, true, false),
	BOTTOM_RIGHT(false, false, true, false, false, true),
	;

	public final boolean isTop;
	public final boolean isMiddle;
	public final boolean isBottom;

	public final boolean isLeft;
	public final boolean isCenter;
	public final boolean isRight;

	ContentAlignment(boolean isTop, boolean isMiddle, boolean isBottom, boolean isLeft, boolean isCenter, boolean isRight) {
		this.isTop = isTop;
		this.isMiddle = isMiddle;
		this.isBottom = isBottom;
		this.isLeft = isLeft;
		this.isCenter = isCenter;
		this.isRight = isRight;
	}

	public Vec3i getChildPosition(Window window, int childWidth, int childHeight) {
		return getChildPosition(window.getGuiScaledWidth(), window.getGuiScaledHeight(), childWidth, childHeight);
	}

	public Vec3i getChildPosition(Widget parent, Widget child) {
		return getChildPosition(parent.width, parent.height, child.width, child.height);
	}

	public Vec3i getChildPosition(Widget parent, int childWidth, int childHeight) {
		return getChildPosition(parent.width, parent.height, childWidth, childHeight);
	}

	public Vec3i getChildPosition(int parentWidth, int parentHeight, Widget child) {
		return getChildPosition(parentWidth, parentHeight, child.width, child.height);
	}

	public Vec3i getChildPosition(int parentWidth, int parentHeight, int childWidth, int childHeight) {
		int x = 0;
		int y = 0;

		if(isTop || childHeight >= parentHeight) {
			y = 0;
		} else if(isMiddle) {
			y = (parentHeight - childHeight) / 2;
		} else if(isBottom) {
			y = parentHeight - childHeight;
		}

		if(isLeft || childWidth >= parentWidth) {
			x = 0;
		} else if(isCenter) {
			x = (parentWidth - childWidth) / 2;
		} else if(isRight) {
			x = parentWidth - childWidth;
		}

		return new Vec3i(x, y, 0);
	}
}
