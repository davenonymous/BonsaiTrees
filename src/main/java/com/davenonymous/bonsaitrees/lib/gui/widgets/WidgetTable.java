package com.davenonymous.bonsaitrees.lib.gui.widgets;

import com.davenonymous.bonsaitrees.lib.gui.CellData;
import com.davenonymous.bonsaitrees.lib.gui.event.*;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.phys.Vec2;

import java.util.Map;

public class WidgetTable extends WidgetPanel {
	Table<Integer, Integer, CellData> table;
	int cellPadding = 0;
	public boolean alwaysShowFirstColumn = true;
	public boolean alwaysShowFirstRow = true;

	private int rowOffset = 0;
	private int colOffset = 0;

	private int visibleRows = 0;
	private int visibleColumns = 0;
	private int visibleHeight = 0;
	private int visibleWidth = 0;

	private int totalRows = 0;
	private int totalColumns = 0;
	private int scrollBarDimension = 8;

	Vec2 dragStart = new Vec2(0, 0);

	public WidgetTable() {
		table = TreeBasedTable.create();

		this.addListener(
			WidgetSizeChangeEvent.class, (event, widget) -> {
				this.repositionCells();
				return WidgetEventResult.HANDLED;
			}
		);

		this.addListener(
			MouseScrollEvent.class, (event, widget) -> {

				if(!this.areAllParentsVisible()) {
					return WidgetEventResult.CONTINUE_PROCESSING;
				}

				if(widget.isPosInside(event.mouseX, event.mouseY)) {
					var gui = getGUI();
					var horizontal = gui.isCtrlDown();
					var scrollValue = Math.abs((int) Math.ceil(event.rawScrollValue));
					if(event.up) {
						if(horizontal) {
							this.scrollLeft(scrollValue);
						} else {
							this.scrollUp(scrollValue);
						}
					} else {
						if(horizontal) {
							this.scrollRight(scrollValue);
						} else {
							this.scrollDown(scrollValue);
						}
					}

					return WidgetEventResult.HANDLED;
				}

				return WidgetEventResult.CONTINUE_PROCESSING;
			}
		);

		this.addListener(
			MouseReleasedEvent.class, (event, widget) -> {
				getGUI().setDragging(false);
				return WidgetEventResult.CONTINUE_PROCESSING;
			}
		);

		this.addListener(
			MouseDraggedEvent.class, (event, widget) -> {
				if(!getGUI().isDragging()) {
					getGUI().setDragging(true);
					dragStart = new Vec2((float) event.mouseX(), (float) event.mouseY());
				}

				double mouseX = event.mouseX();
				double mouseY = event.mouseY();


				if(!this.isPosInside(mouseX, mouseY)) {
					return WidgetEventResult.CONTINUE_PROCESSING;
				}

				int scrollBarY = this.getActualY();
				int scrollBarX = this.getActualX();
				if(alwaysShowFirstRow) {
					mouseY -= getRowHeight(0);
					scrollBarY -= getRowHeight(0);
				}
				if(alwaysShowFirstColumn) {
					mouseX -= getColumnWidth(0);
					scrollBarX -= getColumnWidth(0);
				}

				boolean needsUpdatedCells = false;

				int scrollBarHeight = this.height;
				float yRatio = (float) (mouseY - scrollBarY) / scrollBarHeight;

				int newYOffset = (int) (yRatio * totalRows);
				if(newYOffset != rowOffset) {
					rowOffset = Math.min(newYOffset, totalRows - visibleRows);
					needsUpdatedCells = true;
				}

				if(rowOffset < 0) {
					rowOffset = 0;
				}

				int scrollBarWidth = this.width;
				float xRatio = (float) (mouseX - scrollBarX) / scrollBarWidth;
				int newXOffset = (int) (xRatio * totalColumns);
				if(newXOffset != colOffset) {
					colOffset = Math.min(newXOffset, totalColumns - visibleColumns);
					needsUpdatedCells = true;
				}

				if(colOffset < 0) {
					colOffset = 0;
				}

				if(needsUpdatedCells) {
					repositionCells();
				}

				return WidgetEventResult.HANDLED;
			}
		);
	}


	private boolean needsVerticalScrollbar() {
		return this.visibleRows < this.totalRows;
	}

	private boolean needsHorizontalScrollbar() {
		return this.visibleColumns < this.totalColumns;
	}

	public WidgetTable setCellPadding(int cellPadding) {
		this.cellPadding = cellPadding;
		return this;
	}

	public CellData add(int column, int row, Widget widget) {
		return this.add(column, row, new CellData(widget));
	}

	public CellData add(int column, int row, CellData cell) {
		this.totalColumns = Math.max(this.totalColumns, column + 1);
		this.totalRows = Math.max(this.totalRows, row + 1);

		this.table.put(row, column, cell);
		this.add(cell.widget());
		return cell;
	}

	public int getCellHeight(int column, int row) {
		if(!table.contains(row, column)) {
			return 0;
		}

		Widget widget = table.get(row, column).widget();
		return widget.height;
	}

	public int getCellWidth(int column, int row) {
		if(!table.contains(row, column)) {
			return 0;
		}

		Widget widget = table.get(row, column).widget();
		return widget.width;
	}

	public int getColumnWidth(int column) {
		int maxWidth = 0;
		for(CellData cell : table.columnMap().get(column).values()) {
			Widget cellWidget = cell.widget();
			maxWidth = Math.max(maxWidth, cellWidget.width);
		}
		return maxWidth;
	}

	public int getRowHeight(int row) {
		int maxHeight = 0;
		for(CellData cell : table.rowMap().get(row).values()) {
			Widget cellWidget = cell.widget();
			maxHeight = Math.max(maxHeight, cellWidget.height);
		}
		return maxHeight;
	}

	public int getColumnCount() {
		return table.columnMap().size();
	}

	public int getRowCount() {
		return table.rowMap().size();
	}

	@Override
	public void draw(GuiGraphics guiGraphics, Screen screen) {
		if(needsVerticalScrollbar()) {
			int skipY = this.alwaysShowFirstRow ? getRowHeight(0) : 0;
			int scrollBarX = width - scrollBarDimension - 1;

			int scrollColor = 0xFF666666;
			int matrixHeight = height - skipY;
			if(matrixHeight < 0) {
				matrixHeight = 0;
			}

			int rows = this.totalRows - (this.alwaysShowFirstRow ? 1 : 0) - visibleRows;
			int pxPerRow = (int) Math.floor((float) matrixHeight / rows);

			int topOffset = rowOffset * pxPerRow;

			guiGraphics.fill(
				scrollBarX + 1, skipY + topOffset,
				scrollBarX + scrollBarDimension - 1, skipY + topOffset + pxPerRow, scrollColor
			);
		}

		if(needsHorizontalScrollbar()) {
			int skipX = this.alwaysShowFirstColumn ? getColumnWidth(0) : 0;
			int scrollBarY = height - scrollBarDimension - 1;

			int scrollColor = 0xFF666666;
			int matrixWidth = width - skipX;
			if(matrixWidth < 0) {
				matrixWidth = 0;
			}

			int columns = this.totalColumns - (this.alwaysShowFirstColumn ? 1 : 0);
			int pxPerColumn = (int) Math.ceil((float) matrixWidth / columns);

			int leftOffset = colOffset * pxPerColumn;


			guiGraphics.fill(
				skipX + leftOffset, scrollBarY + 1,
				skipX + leftOffset + pxPerColumn, scrollBarY + scrollBarDimension - 1, scrollColor
			);

		}

		super.draw(guiGraphics, screen);
	}

	public void repositionCells() {
		visibleWidth = 0;
		visibleRows = 0;
		visibleColumns = 0;
		boolean exceededWidth = false;

		int xOffset = 0;
		for(Map.Entry<Integer, Map<Integer, CellData>> columnData : table.columnMap().entrySet()) {
			int column = columnData.getKey();
			int columnWidth = getColumnWidth(column);

			if(visibleWidth + cellPadding + columnWidth + scrollBarDimension > this.width) {
				// Widget won't fit -> no more widgets from here on out
				exceededWidth = true;
			}

			boolean columnForcedVisible = this.alwaysShowFirstColumn && column == 0;
			boolean exceededHeight = false;
			int localVisibleHeight = 0;
			int yOffset = 0;
			for(Map.Entry<Integer, CellData> cellEntry : columnData.getValue().entrySet()) {
				int row = cellEntry.getKey();
				CellData cell = cellEntry.getValue();
				Widget cellWidget = cell.widget();

				boolean rowForcedVisible = this.alwaysShowFirstRow && row == 0;
				if(!columnForcedVisible && (column < colOffset || exceededWidth)) {
					// Widget is scrolled past -> hide
					cellWidget.setVisible(false);
					continue;
				}

				if(!rowForcedVisible && row < rowOffset) {
					// Widget is scrolled past -> hide
					cellWidget.setVisible(false);
					continue;
				}

				int rowHeight = getRowHeight(row);
				if(localVisibleHeight + cellPadding + rowHeight + scrollBarDimension > this.height) {
					// Widget won't fit -> no more widgets from here on out
					exceededHeight = true;
				}

				if(exceededHeight) {
					cellWidget.setVisible(false);
					continue;
				}

				visibleRows = Math.max(visibleRows, 1 + row - rowOffset);
				visibleColumns = Math.max(visibleColumns, 1 + column - colOffset);

				cellWidget.setVisible(true);
				localVisibleHeight += rowHeight + cellPadding;
				this.visibleHeight = Math.max(this.visibleHeight, localVisibleHeight);

				var alignmentOffset = cell.contentAlignment().getChildPosition(columnWidth, rowHeight, cellWidget);
				cellWidget.setX(xOffset + alignmentOffset.getX());
				cellWidget.setY(yOffset + alignmentOffset.getY());

				yOffset += rowHeight + cellPadding;
			}

			if(columnForcedVisible || !(column < colOffset || exceededWidth)) {
				visibleWidth += columnWidth + cellPadding;
				xOffset += columnWidth + cellPadding;
			}
		}
	}

	@Override
	public void renderExtraDebugInfo(GuiGraphics pGuiGraphics, Screen screen) {
		String visibleWidth = "Visible Width: " + this.visibleWidth;
		String visibleHeight = "Visible Height: " + this.visibleHeight;
		String visibleColumns = "Visible Columns: " + this.visibleColumns;
		String visibleRows = "Visible Rows: " + this.visibleRows;
		String colOffset = "Col Offset: " + this.colOffset;
		String rowOffset = "Row Offset: " + this.rowOffset;
		pGuiGraphics.drawString(screen.getMinecraft().font, visibleWidth, 0, 30, 0xFF8000);
		pGuiGraphics.drawString(screen.getMinecraft().font, visibleHeight, 0, 40, 0xFF8000);
		pGuiGraphics.drawString(screen.getMinecraft().font, visibleColumns, 0, 50, 0xFF8000);
		pGuiGraphics.drawString(screen.getMinecraft().font, visibleRows, 0, 60, 0xFF8000);
		pGuiGraphics.drawString(screen.getMinecraft().font, colOffset, 0, 70, 0xFF8000);
		pGuiGraphics.drawString(screen.getMinecraft().font, rowOffset, 0, 80, 0xFF8000);
	}

	public void scrollToTop() {
		this.rowOffset = 0;
	}

	public void scrollUp() {
		scrollUp(1);
	}

	public void scrollUp(int lines) {
		this.rowOffset = Math.max(0, this.rowOffset - lines);
		this.repositionCells();
	}

	public void scrollDown() {
		scrollDown(1);
	}

	public void scrollDown(int lines) {
		this.rowOffset += lines;
		if(this.rowOffset > totalRows - visibleRows) {
			this.rowOffset = totalRows - visibleRows;
		}
		this.repositionCells();
	}

	public void scrollToBottom() {
		this.rowOffset = Math.max(0, totalRows - visibleRows);
	}

	public void scrollToLeftSide() {
		this.colOffset = 0;
	}

	public void scrollToRightSide() {
		this.colOffset = Math.max(0, totalColumns - visibleColumns);
	}

	public void scrollLeft() {
		scrollLeft(1);
	}

	public void scrollLeft(int lines) {
		this.colOffset = Math.max(0, this.colOffset - lines);
		this.repositionCells();
	}

	public void scrollRight() {
		scrollRight(1);
	}

	public void scrollRight(int lines) {
		this.colOffset = Math.min(this.colOffset + lines, totalColumns - visibleColumns);
		this.repositionCells();
	}

}
