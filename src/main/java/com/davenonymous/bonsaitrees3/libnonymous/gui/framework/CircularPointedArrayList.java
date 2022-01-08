package com.davenonymous.bonsaitrees3.libnonymous.gui.framework;

import java.util.ArrayList;

public class CircularPointedArrayList<E> extends ArrayList<E> {
	int pointer = 0;
	public boolean wrap = true;

	public E getPointedElement() {
		return this.get(pointer);
	}

	public E next() {
		this.pointer++;
		if(pointer >= this.size()) {
			pointer = wrap ? 0 : this.size() - 1;
		}

		return getPointedElement();
	}

	public E prev() {
		this.pointer--;
		if(pointer < 0) {
			pointer = wrap ? this.size() - 1 : 0;
		}

		return getPointedElement();
	}

	public void setPointerTo(E element) {
		int position = this.indexOf(element);
		if(position == -1) {
			pointer = 0;
		} else {
			pointer = position;
		}
	}
}
