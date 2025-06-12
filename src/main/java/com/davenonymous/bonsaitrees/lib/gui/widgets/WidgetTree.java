package com.davenonymous.bonsaitrees.lib.gui.widgets;

import com.davenonymous.bonsaitrees.lib.gui.event.ValueChangedEvent;
import com.davenonymous.bonsaitrees.lib.gui.event.WidgetEventResult;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class WidgetTree extends WidgetList {
	private List<WidgetTreeNode> rootNodes;
	private int indentPixels = 10;
	private Predicate<WidgetTreeNode> filter = null;

	public WidgetTree() {
		super();
		rootNodes = new ArrayList<>();
	}

	public WidgetTree setFilter(Predicate<WidgetTreeNode> filter) {
		this.filter = filter;
		return this;
	}

	public WidgetTree setIndentPixels(int indentPixels) {
		this.indentPixels = indentPixels;
		return this;
	}

	public WidgetTreeNode addRootNode(Widget widget) {
		WidgetTreeNode node = new WidgetTreeNode(widget);
		node.setIndentPixels(this.indentPixels);
		node.setExpanded(true);
		rootNodes.add(node);
		return node;
	}

	private void addNode(WidgetTreeNode node, int indent) {
		if(node.children.isEmpty() && filter != null && !filter.test(node)) {
			return;
		}

		WidgetTreeNode.TreeNodeEntryPanel indented = node.getIndented(indent, this.width - 15);
		indented.addListener(
			ValueChangedEvent.class, (event, widget) -> {
				buildListFromRootNodes();
				return WidgetEventResult.HANDLED;
			}
		);
		this.addListEntry(indented);
	}

	private void addToListFromNode(WidgetTreeNode node, int indent) {
		addNode(node, indent);
		if(node.isExpanded()) {
			for(WidgetTreeNode child : node.children) {
				addToListFromNode(child, indent + 1);
			}
		}
	}

	public void buildListFromRootNodes() {
		clear();
		for(WidgetTreeNode node : rootNodes) {
			addToListFromNode(node, 0);
		}
	}


}
