package com.davenonymous.bonsaitrees3.libnonymous.gui.framework.widgets;


import com.davenonymous.bonsaitrees3.libnonymous.gui.framework.event.CharTypedEvent;
import com.davenonymous.bonsaitrees3.libnonymous.gui.framework.event.KeyPressedEvent;
import com.davenonymous.bonsaitrees3.libnonymous.gui.framework.event.MouseClickEvent;
import com.davenonymous.bonsaitrees3.libnonymous.gui.framework.event.WidgetEventResult;
import com.davenonymous.bonsaitrees3.libnonymous.helper.MathHelper;
import com.google.common.base.Predicates;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;

import java.util.function.BiFunction;
import java.util.function.Predicate;

public class WidgetInputField extends WidgetWithValue<String> {
	private boolean hasShiftKeyDown;
	private int cursorPosition;
	private int lineScrollOffset;
	private int selectionEnd;
	private int enabledColor = 14737632;
	private int disabledColor = 7368816;
	private int maxStringLength = 128;
	private String suggestion;
	private int cursorCounter;
	private Predicate<String> validator = Predicates.alwaysTrue();
	private final Font fontRenderer;
	private boolean enableBackgroundDrawing = true;
	private boolean canLoseFocus = true;
	private BiFunction<String, Integer, String> textFormatter = (p_195610_0_, p_195610_1_) -> {
		return p_195610_0_;
	};

	public WidgetInputField() {
		fontRenderer = Minecraft.getInstance().font;
		this.lineScrollOffset = 0;
		this.value = "";

		this.addListener(KeyPressedEvent.class, (event, widget) -> {
			boolean result = this.onKeyPressed(event.keyCode, event.scanCode, event.modifiers);
			return !result ? WidgetEventResult.CONTINUE_PROCESSING : WidgetEventResult.HANDLED;
		});

		this.addListener(MouseClickEvent.class, ((event, widget) -> {
			boolean result = this.mouseClicked(event.x, event.y, event.button);
			return !result ? WidgetEventResult.CONTINUE_PROCESSING : WidgetEventResult.HANDLED;
		}));

		this.addListener(CharTypedEvent.class, (event, widget) -> {
			boolean result = this.charTyped(event.chr, event.scanCode);
			return !result ? WidgetEventResult.CONTINUE_PROCESSING : WidgetEventResult.HANDLED;
		});
	}

	public WidgetInputField setValidator(Predicate<String> validator) {
		this.validator = validator;
		return this;
	}

	public WidgetInputField setMaxStringLength(int maxStringLength) {
		this.maxStringLength = maxStringLength;
		return this;
	}

	public boolean charTyped(char chr, int scanCode) {
		if(!this.isFocused()) {
			return false;
		} else if(SharedConstants.isAllowedChatCharacter(chr)) {
			if(this.enabled) {
				this.writeText(Character.toString(chr));
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public void draw(PoseStack pPoseStack, Screen screen) {
		super.draw(pPoseStack, screen);

		int renderX = 0;
		int renderY = 0;

        /*
        if (this.isVisible()) {
            if (this.enableBackgroundDrawing) {
                fill(renderX - 1, renderY - 1, renderX + this.width + 1, renderY + this.height + 1, -6250336);
                fill(renderX, renderY, renderX + this.width, renderY + this.height, -16777216);
            }

            int i = this.enabled ? this.enabledColor : this.disabledColor;
            int j = this.cursorPosition - this.lineScrollOffset;
            int k = this.selectionEnd - this.lineScrollOffset;
            String s = this.fontRenderer.trimStringToWidth(this.value.substring(this.lineScrollOffset), this.getAdjustedWidth());
            boolean flag = j >= 0 && j <= s.length();
            boolean flag1 = this.isFocused() && this.cursorCounter / 6 % 2 == 0 && flag;
            int l = this.enableBackgroundDrawing ? renderX + 4 : renderX;
            int i1 = this.enableBackgroundDrawing ? renderY + (this.height - 8) / 2 : renderY;
            int j1 = l;
            if (k > s.length()) {
                k = s.length();
            }

            if (!s.isEmpty()) {
                String s1 = flag ? s.substring(0, j) : s;
                j1 = this.fontRenderer.drawStringWithShadow(this.textFormatter.apply(s1, this.lineScrollOffset), (float)l, (float)i1, i);
            }

            boolean flag2 = this.cursorPosition < this.value.length() || this.value.length() >= this.maxStringLength;
            int k1 = j1;
            if (!flag) {
                k1 = j > 0 ? l + this.width : l;
            } else if (flag2) {
                k1 = j1 - 1;
                --j1;
            }

            if (!s.isEmpty() && flag && j < s.length()) {
                this.fontRenderer.drawStringWithShadow(this.textFormatter.apply(s.substring(j), this.cursorPosition), (float)j1, (float)i1, i);
            }

            if (!flag2 && this.suggestion != null) {
                this.fontRenderer.drawStringWithShadow(this.suggestion, (float)(k1 - 1), (float)i1, -8355712);
            }

            if (flag1) {
                if (flag2) {
                    fill(k1, i1 - 1, k1 + 1, i1 + 1 + 9, -3092272);
                } else {
                    this.fontRenderer.drawStringWithShadow("_", (float)k1, (float)i1, i);
                }
            }

            if (k != j) {
                int l1 = l + this.fontRenderer.getStringWidth(s.substring(0, k));
                this.drawSelectionBox(k1, i1 - 1, l1 - 1, i1 + 1 + 9);
            }

            RenderSystem.enableAlphaTest();
            RenderSystem.enableBlend();
        }

         */
	}


	/**
	 * Draws the blue selection box.
	 */
	private void drawSelectionBox(PoseStack pPoseStack, int startX, int startY, int endX, int endY) {
		if(startX < endX) {
			int i = startX;
			startX = endX;
			endX = i;
		}

		if(startY < endY) {
			int j = startY;
			startY = endY;
			endY = j;
		}

		if(endX > this.x + this.width) {
			endX = this.x + this.width;
		}

		if(startX > this.x + this.width) {
			startX = this.x + this.width;
		}

        /*
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        RenderSystem.color4f(0.0F, 0.0F, 255.0F, 255.0F);
        RenderSystem.disableTexture();
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos((double)startX, (double)endY, 0.0D).endVertex();
        bufferbuilder.pos((double)endX, (double)endY, 0.0D).endVertex();
        bufferbuilder.pos((double)endX, (double)startY, 0.0D).endVertex();
        bufferbuilder.pos((double)startX, (double)startY, 0.0D).endVertex();
        tessellator.draw();
        RenderSystem.disableColorLogicOp();
        RenderSystem.enableTexture();
         */
	}

	public WidgetInputField setSuggestion(String suggestion) {
		this.suggestion = suggestion;
		return this;
	}

	/**
	 * Sets the text of the textbox, and moves the cursor to the end.
	 */
	public void setText(String textIn) {
		if(this.validator.test(textIn)) {
			if(textIn.length() > this.maxStringLength) {
				this.setValue(textIn.substring(0, this.maxStringLength));
			} else {
				this.setValue(textIn);
			}

			this.setCursorPositionEnd();
			this.setSelectionPos(this.cursorPosition);
			this.lineScrollOffset = 0;
		}
	}

	/**
	 * Returns the contents of the textbox
	 */
	public String getText() {
		return this.value;
	}

	/**
	 * returns the text between the cursor and selectionEnd
	 */
	public String getSelectedText() {
		int i = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
		int j = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
		return this.value.substring(i, j);
	}

	/**
	 * Moves the cursor to the very end of this text box.
	 */
	public void setCursorPositionEnd() {
		this.setCursorPosition(this.value.length());
	}


	/**
	 * Moves the text cursor by a specified number of characters and clears the selection
	 */
	public void moveCursorBy(int num) {
		this.setCursorPosition(this.cursorPosition + num);
	}

	/**
	 * Sets the current position of the cursor.
	 */
	public void setCursorPosition(int pos) {
		this.setClampedCursorPosition(pos);
		if(!this.hasShiftKeyDown) {
			this.setSelectionPos(this.cursorPosition);
		}
	}

	/**
	 * Moves the cursor to the very start of this text box.
	 */
	public void setCursorPositionZero() {
		this.setCursorPosition(0);
	}

	public void setClampedCursorPosition(int pos) {
		this.cursorPosition = MathHelper.clamp(pos, 0, this.value.length());
	}

	private boolean isFocused() {
		return this.isVisible() && this.enabled && this.focused;
	}

	private void delete(int p_212950_1_) {
		if(Screen.hasControlDown()) {
			this.deleteWords(p_212950_1_);
		} else {
			this.deleteFromCursor(p_212950_1_);
		}

	}

	/**
	 * Deletes the given number of words from the current cursor's position, unless there is currently a selection, in
	 * which case the selection is deleted instead.
	 */
	public void deleteWords(int num) {
		if(!this.value.isEmpty()) {
			if(this.selectionEnd != this.cursorPosition) {
				this.writeText("");
			} else {
				this.deleteFromCursor(this.getNthWordFromCursor(num) - this.cursorPosition);
			}
		}
	}

	/**
	 * Deletes the given number of characters from the current cursor's position, unless there is currently a selection,
	 * in which case the selection is deleted instead.
	 */
	public void deleteFromCursor(int num) {
		if(!this.value.isEmpty()) {
			if(this.selectionEnd != this.cursorPosition) {
				this.writeText("");
			} else {
				boolean flag = num < 0;
				int i = flag ? this.cursorPosition + num : this.cursorPosition;
				int j = flag ? this.cursorPosition : this.cursorPosition + num;
				String s = "";
				if(i >= 0) {
					s = this.value.substring(0, i);
				}

				if(j < this.value.length()) {
					s = s + this.value.substring(j);
				}

				if(this.validator.test(s)) {
					this.setValue(s);
					if(flag) {
						this.moveCursorBy(num);
					}
				}
			}
		}
	}

	/**
	 * Gets the starting index of the word at the specified number of words away from the cursor position.
	 */
	public int getNthWordFromCursor(int numWords) {
		return this.getNthWordFromPos(numWords, this.cursorPosition);
	}

	/**
	 * Gets the starting index of the word at a distance of the specified number of words away from the given position.
	 */
	private int getNthWordFromPos(int n, int pos) {
		return this.getNthWordFromPosWS(n, pos, true);
	}

	/**
	 * Like getNthWordFromPos (which wraps this), but adds option for skipping consecutive spaces
	 */
	private int getNthWordFromPosWS(int n, int pos, boolean skipWs) {
		int i = pos;
		boolean flag = n < 0;
		int j = Math.abs(n);

		for(int k = 0; k < j; ++k) {
			if(!flag) {
				int l = this.value.length();
				i = this.value.indexOf(32, i);
				if(i == -1) {
					i = l;
				} else {
					while(skipWs && i < l && this.value.charAt(i) == ' ') {
						++i;
					}
				}
			} else {
				while(skipWs && i > 0 && this.value.charAt(i - 1) == ' ') {
					--i;
				}

				while(i > 0 && this.value.charAt(i - 1) != ' ') {
					--i;
				}
			}
		}

		return i;
	}

	/**
	 * Adds the given text after the cursor, or replaces the currently selected text if there is a selection.
	 */
	public void writeText(String textToWrite) {
		String s = "";
		String s1 = SharedConstants.filterText(textToWrite);
		int i = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
		int j = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
		int k = this.maxStringLength - this.value.length() - (i - j);
		if(!this.value.isEmpty()) {
			s = s + this.value.substring(0, i);
		}

		int l;
		if(k < s1.length()) {
			s = s + s1.substring(0, k);
			l = k;
		} else {
			s = s + s1;
			l = s1.length();
		}

		if(!this.value.isEmpty() && j < this.value.length()) {
			s = s + this.value.substring(j);
		}

		if(this.validator.test(s)) {
			this.setValue(s);
			this.setClampedCursorPosition(i + l);
			this.setSelectionPos(this.cursorPosition);
		}
	}

	private boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
		if(!this.isFocused()) {
			return false;
		} else {
			if(keyCode == 69) {
				return true;
			}

			this.hasShiftKeyDown = Screen.hasShiftDown();
			if(Screen.isSelectAll(keyCode)) {
				this.setCursorPositionEnd();
				this.setSelectionPos(0);
				return true;
			} else if(Screen.isCopy(keyCode)) {
				Minecraft.getInstance().keyboardHandler.setClipboard(this.getSelectedText());
				return true;
			} else if(Screen.isPaste(keyCode)) {
				if(this.enabled) {
					this.writeText(Minecraft.getInstance().keyboardHandler.getClipboard());
				}

				return true;
			} else if(Screen.isCut(keyCode)) {
				Minecraft.getInstance().keyboardHandler.setClipboard(this.getSelectedText());
				if(this.enabled) {
					this.writeText("");
				}

				return true;
			} else {
				switch(keyCode) {
					case 259:
						if(this.enabled) {
							this.hasShiftKeyDown = false;
							this.delete(-1);
							this.hasShiftKeyDown = Screen.hasShiftDown();
						}

						return true;
					case 260:
					case 264:
					case 265:
					case 266:
					case 267:
					default:
						return false;
					case 261:
						if(this.enabled) {
							this.hasShiftKeyDown = false;
							this.delete(1);
							this.hasShiftKeyDown = Screen.hasShiftDown();
						}

						return true;
					case 262:
						if(Screen.hasControlDown()) {
							this.setCursorPosition(this.getNthWordFromCursor(1));
						} else {
							this.moveCursorBy(1);
						}

						return true;
					case 263:
						if(Screen.hasControlDown()) {
							this.setCursorPosition(this.getNthWordFromCursor(-1));
						} else {
							this.moveCursorBy(-1);
						}

						return true;
					case 268:
						this.setCursorPositionZero();
						return true;
					case 269:
						this.setCursorPositionEnd();
						return true;
				}
			}
		}
	}

	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		int thisX = this.getActualX();
		int thisY = this.getActualY();

		if(!this.isFocused()) {
			return false;
		} else {
			boolean flag = mouseX >= (double) thisX && mouseX < (double) (thisX + this.width) && mouseY >= (double) thisY && mouseY < (double) (thisY + this.height);
			if(this.canLoseFocus) {
				this.focused = flag;
			}

			if(this.isFocused() && flag && mouseButton == 0) {
				int i = (int) Math.floor(mouseX) - this.x;
				if(this.enableBackgroundDrawing) {
					i -= 4;
				}

				String s = this.fontRenderer.plainSubstrByWidth(this.value.substring(this.lineScrollOffset), this.getAdjustedWidth());
				this.setCursorPosition(this.fontRenderer.plainSubstrByWidth(s, i).length() + this.lineScrollOffset);
				return true;
			} else {
				return false;
			}
		}
	}


	/**
	 * returns the width of the textbox depending on if background drawing is enabled
	 */
	public int getAdjustedWidth() {
		return this.enableBackgroundDrawing ? this.width - 8 : this.width;
	}


	/**
	 * Sets the position of the selection anchor (the selection anchor and the cursor position mark the edges of the
	 * selection). If the anchor is set beyond the bounds of the current text, it will be put back inside.
	 */
	public void setSelectionPos(int position) {
		int i = this.value.length();
		this.selectionEnd = MathHelper.clamp(position, 0, i);
		if(this.fontRenderer != null) {
			if(this.lineScrollOffset > i) {
				this.lineScrollOffset = i;
			}

			int j = this.getAdjustedWidth();
			String s = this.fontRenderer.plainSubstrByWidth(this.value.substring(this.lineScrollOffset), j);
			int k = s.length() + this.lineScrollOffset;
			if(this.selectionEnd == this.lineScrollOffset) {
				this.lineScrollOffset -= this.fontRenderer.plainSubstrByWidth(this.value, j, true).length();
			}

			if(this.selectionEnd > k) {
				this.lineScrollOffset += this.selectionEnd - k;
			} else if(this.selectionEnd <= this.lineScrollOffset) {
				this.lineScrollOffset -= this.lineScrollOffset - this.selectionEnd;
			}

			this.lineScrollOffset = MathHelper.clamp(this.lineScrollOffset, 0, i);
		}

	}

}