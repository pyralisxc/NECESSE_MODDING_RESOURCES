/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.InputPosition;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameMath;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.Renderer;
import necesse.gfx.fairType.FairCharacterGlyph;
import necesse.gfx.fairType.FairGlyph;
import necesse.gfx.fairType.FairItemGlyph;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.gfx.fairType.GlyphContainer;
import necesse.gfx.fairType.parsers.TypeParser;
import necesse.gfx.forms.components.FormTypingComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.events.FormCursorMoveEvent;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormEventsHandler;
import necesse.gfx.forms.events.FormInputEvent;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.ui.HUD;
import necesse.inventory.InventoryItem;

public abstract class FormFairTypeEdit
extends FormTypingComponent {
    private Caret caret;
    private FairType text;
    private FairTypeDrawOptions drawOptions;
    private FairTypeDrawOptions textBoxDrawOptions;
    private ArrayList<GlyphContainer> drawOptionsArrayList;
    private LinkedList<CaretPosition> possibleCaretPositions;
    public final FairType.TextAlign align;
    public final FontOptions fontOptions;
    public final Color textColor;
    private Pattern regexPattern;
    protected int maxLines;
    protected int maxWidth;
    protected int maxLength;
    public boolean allowTyping = true;
    public boolean allowCaretSelect = true;
    public boolean allowCaretSetTyping = true;
    public boolean allowCaretRemoveTyping = true;
    public boolean allowUsedMouseClickStopTyping = false;
    public boolean allowTextSelect = true;
    public boolean allowItemAppend = false;
    public Supplier<FormTypingComponent> tabTypingComponent;
    protected Rectangle textSelectEmptySpace = null;
    protected boolean isHovering;
    private CaretPosition currentMouseCaretPosition = null;
    private boolean currentlySelecting = false;
    private CaretPosition startSelection;
    private CaretPosition endSelection;
    private Point startSelectionMousePos;
    protected TypeParser[] parsers;
    protected InputEvent setTypingEvent;
    protected FormEventsHandler<FormCursorMoveEvent<FormFairTypeEdit>> caretMoveEvents = new FormEventsHandler();
    protected FormEventsHandler<FormInputEvent<FormFairTypeEdit>> onMouseChangedTyping = new FormEventsHandler();
    private static final Pattern nextNonWordChar = Pattern.compile("[^\\w\\d]");

    public FormFairTypeEdit(FontOptions fontOptions, FairType.TextAlign align, Color textColor, int maxWidth, int maxLines, int maxLength) {
        this.fontOptions = fontOptions;
        this.align = align;
        this.textColor = textColor;
        this.maxWidth = maxWidth;
        this.maxLines = maxLines;
        this.maxLength = maxLength;
        this.parsers = new TypeParser[0];
        this.setText("");
    }

    public FormFairTypeEdit onCaretMove(FormEventListener<FormCursorMoveEvent<FormFairTypeEdit>> listener) {
        this.caretMoveEvents.addListener(listener);
        return this;
    }

    public FormFairTypeEdit onMouseChangedTyping(FormEventListener<FormInputEvent<FormFairTypeEdit>> listener) {
        this.onMouseChangedTyping.addListener(listener);
        return this;
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        if (event.getID() != -100 || !this.currentlySelecting) {
            this.getDrawOptions().handleInputEvent(this.getTextX(), this.getTextY(), event);
        }
        if (this.isTyping()) {
            this.submitTypingEvent(event, true);
        }
        if (event.isUsed()) {
            return;
        }
        if (event.isMouseMoveEvent()) {
            if (!event.isMoveUsed()) {
                this.isHovering = this.isMouseOver(event);
                this.updateCurrentCaretPosition(event);
                if (this.currentlySelecting && this.currentMouseCaretPosition != null) {
                    this.caret.typeIndex = this.currentMouseCaretPosition.index;
                    this.caret.updateProps(this.currentMouseCaretPosition, true, new FormCursorMoveEvent<FormFairTypeEdit>(this, true));
                }
                if (this.isHovering) {
                    event.useMove();
                }
            } else {
                this.currentMouseCaretPosition = null;
            }
        } else if (this.allowCaretSelect && event.getID() == -100) {
            this.updateCurrentCaretPosition(event);
            if (this.currentMouseCaretPosition != null) {
                this.setTypingEvent = event;
                if (!this.isTyping()) {
                    this.setTyping(true);
                    this.onMouseChangedTyping.onEvent(new FormInputEvent<FormFairTypeEdit>(this, event));
                }
                if (this.allowTextSelect) {
                    if (event.state) {
                        this.startSelection = this.currentMouseCaretPosition;
                        InputPosition mousePos = WindowManager.getWindow().mousePos();
                        this.startSelectionMousePos = new Point(mousePos.hudX, mousePos.hudY);
                        this.currentlySelecting = true;
                        this.endSelection = null;
                    }
                    if (!event.state) {
                        this.endSelection = this.currentMouseCaretPosition;
                        this.startSelectionMousePos = null;
                        this.currentlySelecting = false;
                    }
                } else {
                    this.startSelection = null;
                    this.endSelection = null;
                }
                this.caret.typeIndex = this.currentMouseCaretPosition.index;
                this.caret.updateProps(this.currentMouseCaretPosition, true, new FormCursorMoveEvent<FormFairTypeEdit>(this, true));
                event.use();
            } else if (!(!this.allowCaretRemoveTyping || this.allowItemAppend && WindowManager.getWindow().isKeyDown(340))) {
                this.startSelection = null;
                this.endSelection = null;
                this.currentlySelecting = false;
                if (this.isTyping()) {
                    this.setTyping(false);
                    this.onMouseChangedTyping.onEvent(new FormInputEvent<FormFairTypeEdit>(this, event));
                }
            }
        }
        if (this.isTyping()) {
            FormTypingComponent nextTypingComponent;
            if (this.allowTextSelect && event.state && event.getID() == 340 && this.startSelection == null) {
                this.startSelection = this.caret.getCurrentPosition();
                this.endSelection = this.caret.getCurrentPosition();
            } else if (event.getID() == 256 && !event.state) {
                this.setTyping(false);
                event.use();
            } else if (event.state && this.tabTypingComponent != null && event.getID() == 258 && (nextTypingComponent = this.tabTypingComponent.get()) != null) {
                event.use();
                this.clearSelection();
                nextTypingComponent.setTyping(true);
            }
        }
    }

    @Override
    public boolean submitTypingEvent(InputEvent event, boolean allowNavigation) {
        if (this.handleCaretInput(event, allowNavigation)) {
            this.text.applyParsers(p -> true, p -> {
                if (this.caret.typeIndex >= p.start && this.caret.typeIndex < p.end) {
                    this.caret.typeIndex = Math.min(this.caret.typeIndex, p.start + p.newGlyphs.length - 1);
                } else if (this.caret.typeIndex >= p.end) {
                    this.caret.typeIndex += p.newGlyphs.length - p.oldGlyphs.length;
                }
                this.resetDrawOptions();
                this.caret.updateProps(null, true, new FormCursorMoveEvent<FormFairTypeEdit>(this, false));
            }, this.parsers);
            this.submitChangeEvent();
            return true;
        }
        return false;
    }

    @Override
    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        if (event.getState() == ControllerInput.MENU_SELECT) {
            if (this.isControllerFocus() && event.buttonState) {
                this.setTyping(true);
                event.use();
            }
        } else if ((event.getState() == ControllerInput.MENU_BACK || event.getState() == ControllerInput.MAIN_MENU) && this.isTyping() && event.buttonState) {
            this.setTyping(false);
            event.use();
        }
    }

    @Override
    public void onControllerUnfocused(ControllerFocus current) {
        super.onControllerUnfocused(current);
        this.setTyping(false);
    }

    @Override
    public boolean handleControllerNavigate(int dir, ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        if (this.isTyping()) {
            return true;
        }
        return super.handleControllerNavigate(dir, event, tickManager, perspective);
    }

    @Override
    public void addNextControllerFocus(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
        if (this.textSelectEmptySpace != null) {
            Rectangle emptySpace = new Rectangle(this.textSelectEmptySpace);
            emptySpace.x += this.getTextX();
            emptySpace.y += this.getTextY();
            ControllerFocus.add(list, area, this, emptySpace, currentXOffset - emptySpace.x, currentYOffset - emptySpace.y, this.controllerInitialFocusPriority, customNavigationHandler);
        } else {
            ControllerFocus.add(list, area, this, this.getBoundingBox(), currentXOffset, currentYOffset, this.controllerInitialFocusPriority, customNavigationHandler);
        }
    }

    @Override
    public void submitUsedInputEvent(InputEvent event) {
        if (this.isTyping() && !InputEvent.isFromSameEvent(this.setTypingEvent, event) && event.wasMouseClickEvent() && this.allowUsedMouseClickStopTyping) {
            this.setTyping(false);
            this.onMouseChangedTyping.onEvent(new FormInputEvent<FormFairTypeEdit>(this, event));
        }
    }

    private void updateCurrentCaretPosition(InputEvent event) {
        CaretPosition bestPosition = null;
        if (this.allowCaretSelect) {
            if (!this.isTyping() && !this.allowCaretSetTyping) {
                return;
            }
            Rectangle textBoundingBox = this.getTextBoundingBox();
            Rectangle selectBox = new Rectangle(textBoundingBox);
            if (this.textSelectEmptySpace != null) {
                Rectangle emptySpace = new Rectangle(this.textSelectEmptySpace);
                emptySpace.x += this.getTextX();
                emptySpace.y += this.getTextY();
                selectBox = selectBox.union(emptySpace);
            }
            if (selectBox.contains(event.pos.hudX, event.pos.hudY) || this.currentlySelecting && this.startSelectionMousePos != null) {
                InputEvent offsetEvent = InputEvent.OffsetHudEvent(WindowManager.getWindow().getInput(), event, -this.getTextX(), -this.getTextY());
                bestPosition = this.getClosestCaretPosition(offsetEvent.pos.hudX, offsetEvent.pos.hudY);
            }
        }
        this.currentMouseCaretPosition = bestPosition;
    }

    protected CaretPosition getClosestCaretPosition(int x, int y) {
        CaretPosition bestPosition = null;
        int bestDeltaX = Integer.MAX_VALUE;
        LinkedList<CaretPosition> caretPositions = this.getPossibleCaretPositions();
        int firstLine = caretPositions.getFirst().line;
        int lastLine = caretPositions.getLast().line;
        for (CaretPosition pos : caretPositions) {
            int deltaX;
            if (y > pos.y && pos.line != lastLine || y <= pos.y - pos.lineHeight && pos.line != firstLine || (deltaX = Math.abs(pos.x - x)) > bestDeltaX) continue;
            bestPosition = pos;
            bestDeltaX = deltaX;
        }
        if (bestPosition == null) {
            CaretPosition first = caretPositions.getFirst();
            bestPosition = y <= first.y ? first : caretPositions.getLast();
        }
        return bestPosition;
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.drawSelection();
        this.drawGlyphs();
        this.drawCaret();
    }

    public void drawSelection() {
        if (this.startSelection != null && (this.endSelection != null || this.currentMouseCaretPosition != null)) {
            CaretPosition end = this.endSelection == null ? this.currentMouseCaretPosition : this.endSelection;
            int lastDrawX = Integer.MIN_VALUE;
            int lastLine = 0;
            if (this.startSelection.index != end.index) {
                int min = Math.min(this.startSelection.index, end.index);
                int max = Math.max(this.startSelection.index, end.index);
                ArrayList<GlyphContainer> ar = this.getDrawOptionsArrayList();
                for (int i = min + 1; i <= max; ++i) {
                    GlyphContainer c = ar.get(i);
                    if (c.line > lastLine) {
                        lastLine = c.line;
                        lastDrawX = Integer.MIN_VALUE;
                    }
                    Dimension dim = c.glyph.getDimensions().toInt();
                    int x = this.getTextX() + (int)c.x;
                    int width = dim.width;
                    int drawX = Math.max(x, lastDrawX);
                    if (x < drawX) {
                        width -= drawX - x;
                    }
                    Renderer.initQuadDraw(width, (int)c.lineHeight).color(0.0f, 0.0f, 1.0f, 0.5f).draw(drawX, this.getTextY() + (int)(c.y - c.lineHeight));
                    lastDrawX = x + width;
                }
            }
        }
        if (this.currentMouseCaretPosition != null) {
            Renderer.setCursor(GameWindow.CURSOR.CARET);
        }
    }

    public void drawGlyphs() {
        this.getDrawOptions().draw(this.getTextX(), this.getTextY(), this.textColor);
    }

    public void drawCaret() {
        this.caret.draw(this.fontOptions.getOutline() ? this.fontOptions.getStrokeColorObj() : null);
    }

    @Override
    public void drawControllerFocus(ControllerFocus current) {
        if (this.isTyping()) {
            Rectangle box = current.boundingBox;
            int padding = 5;
            box = new Rectangle(box.x - padding, box.y - padding, box.width + padding * 2, box.height + padding * 2);
            HUD.selectBoundOptions(this.getInterfaceStyle().controllerFocusBoundsHighlightColor, true, box).draw();
        } else {
            super.drawControllerFocus(current);
            GameTooltipManager.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
        }
    }

    @Override
    public FairTypeDrawOptions getDrawOptions() {
        if (this.drawOptions == null) {
            this.drawOptions = this.text.getDrawOptions(this.align, this.maxWidth, true, false);
        }
        return this.drawOptions;
    }

    @Override
    public FairTypeDrawOptions getTextBoxDrawOptions() {
        if (this.textBoxDrawOptions == null) {
            this.textBoxDrawOptions = this.text.getTextBoxCopy().getDrawOptions(FairType.TextAlign.LEFT, 400, true, false);
        }
        return this.textBoxDrawOptions;
    }

    public void resetDrawOptions() {
        this.drawOptions = null;
        this.textBoxDrawOptions = null;
        this.drawOptionsArrayList = null;
        this.possibleCaretPositions = null;
        this.clearSelection();
    }

    public int getMaxWidth() {
        return this.maxWidth;
    }

    public void setMaxWidth(int maxWidth) {
        if (this.maxWidth != maxWidth) {
            this.maxWidth = maxWidth;
            this.resetDrawOptions();
        }
    }

    public int getMaxLines() {
        return this.maxLines;
    }

    public void setMaxLines(int maxLines) {
        if (this.maxLines != maxLines) {
            this.maxLines = maxLines;
            this.resetDrawOptions();
        }
    }

    public int getMaxLength() {
        return this.maxLines;
    }

    public void setMaxLength(int maxLength) {
        if (this.maxLength != maxLength) {
            this.maxLength = maxLength;
            this.resetDrawOptions();
        }
    }

    public int getTextLength() {
        return this.text.getLength();
    }

    public void clearSelection() {
        this.startSelectionMousePos = null;
        this.startSelection = null;
        this.endSelection = null;
        this.currentlySelecting = false;
    }

    public void selectAll() {
        LinkedList<CaretPosition> possibleCaretPositions = this.getPossibleCaretPositions();
        this.startSelection = possibleCaretPositions.getFirst();
        this.endSelection = possibleCaretPositions.getLast();
        this.currentMouseCaretPosition = possibleCaretPositions.getLast();
    }

    protected ArrayList<GlyphContainer> getDrawOptionsArrayList() {
        if (this.drawOptionsArrayList == null) {
            this.drawOptionsArrayList = new ArrayList<GlyphContainer>(this.getDrawOptions().getDrawList());
        }
        return this.drawOptionsArrayList;
    }

    protected LinkedList<CaretPosition> getPossibleCaretPositions() {
        if (this.possibleCaretPositions == null) {
            LinkedList<GlyphContainer> drawList = this.getDrawOptions().getDrawList();
            LinkedList<CaretPosition> positions = new LinkedList<CaretPosition>();
            if (drawList.isEmpty()) {
                positions.add(new CaretPosition(null, -1, 0, this.fontOptions.getSize(), 0, this.fontOptions.getSize()));
            }
            for (GlyphContainer container : drawList) {
                positions.add(new CaretPosition(container, container.index - 1, (int)container.x, (int)container.y));
                Dimension dim = container.glyph.getDimensions().toInt();
                positions.add(new CaretPosition(container, container.index, GameMath.ceil(container.x + (float)dim.width), GameMath.ceil(container.y)));
            }
            this.possibleCaretPositions = positions;
        }
        return this.possibleCaretPositions;
    }

    protected LinkedList<CaretPosition> getPossibleCaretPositions(int line) {
        LinkedList<CaretPosition> out = new LinkedList<CaretPosition>();
        for (CaretPosition pos : this.getPossibleCaretPositions()) {
            if (pos.line != line) continue;
            out.add(pos);
        }
        return out;
    }

    protected Rectangle getTextBoundingBox() {
        if (this.text.getLength() == 0) {
            return new Rectangle(this.getTextX(), this.getTextY(), 2, this.fontOptions.getSize());
        }
        return this.getDrawOptions().getBoundingBox(this.getTextX(), this.getTextY());
    }

    public void setText(String text, boolean submitChangeEvent) {
        FairType newText = new FairType();
        for (char c : text.toCharArray()) {
            newText.append(this.getCharacterGlyph(c));
        }
        this.setFairType(newText);
        if (submitChangeEvent) {
            this.submitChangeEvent();
        }
    }

    public final void setText(String text) {
        this.setText(text, true);
    }

    public String getText() {
        return this.text.getParseString();
    }

    public String getTextCharString() {
        return this.text.getCharString();
    }

    protected final FairType getFairType() {
        return this.text;
    }

    public void setParsers(TypeParser ... parsers) {
        this.parsers = parsers;
    }

    protected void setFairType(FairType type) {
        this.text = type;
        this.text.applyParsers(this.parsers);
        if (this.maxLength > 0) {
            while (this.text.getLength() > this.maxLength) {
                this.text.remove(this.text.getLength() - 1);
            }
        }
        this.resetDrawOptions();
        CaretPosition newCaretPos = this.caret == null ? null : this.getClosestCaretPosition(this.caret.x, this.caret.y);
        this.caret = new Caret();
        if (newCaretPos != null) {
            this.caret.typeIndex = newCaretPos.index;
            this.caret.updateProps(newCaretPos, true, null);
        } else {
            this.caret.typeIndex = this.text.getLength() - 1;
            this.caret.updateProps(null, true, null);
        }
    }

    @Override
    public void setCaretEnd() {
        LinkedList<CaretPosition> positions = this.getPossibleCaretPositions();
        if (!positions.isEmpty()) {
            CaretPosition last = positions.getLast();
            this.caret.typeIndex = last.index;
            this.caret.updateProps(last, true, null);
        }
    }

    public boolean appendAtCaret(String str) {
        if (str != null) {
            FairGlyph[] glyphs = new FairGlyph[str.length()];
            for (int i = 0; i < glyphs.length; ++i) {
                glyphs[i] = this.getCharacterGlyph(str.charAt(i));
            }
            return this.appendAtCaret(glyphs);
        }
        return this.appendAtCaret((FairGlyph[])null);
    }

    public boolean appendAtCaret(FairGlyph ... glyphs) {
        boolean change = false;
        if (this.deleteSelection()) {
            this.resetDrawOptions();
            this.caret.updateProps(null, true, new FormCursorMoveEvent<FormFairTypeEdit>(this, false));
            change = true;
        }
        if (glyphs != null) {
            for (FairGlyph glyph : glyphs) {
                if (!this.acceptInput(this.caret.typeIndex + 1, glyph)) break;
                ++this.caret.typeIndex;
                change = true;
            }
            this.resetDrawOptions();
            this.caret.updateProps(null, true, new FormCursorMoveEvent<FormFairTypeEdit>(this, false));
        }
        return change;
    }

    protected Caret getCaret() {
        return this.caret;
    }

    protected abstract int getTextX();

    protected abstract int getTextY();

    @Override
    public boolean appendItem(InventoryItem item) {
        if (this.appendAtCaret(new FairItemGlyph(this.fontOptions.getSize(), item))) {
            this.submitChangeEvent();
            return true;
        }
        return false;
    }

    @Override
    public boolean isValidAppendText(String text) {
        return this.validInput(text);
    }

    @Override
    public boolean appendText(String text) {
        if (this.validInput(text)) {
            if (this.appendAtCaret(text)) {
                this.submitChangeEvent();
                return true;
            }
            return false;
        }
        return this.appendAtCaret((String)null);
    }

    @Override
    public boolean submitBackspace() {
        this.handleBackspace();
        this.submitChangeEvent();
        return true;
    }

    public final int getCaretIndex() {
        return this.caret.typeIndex;
    }

    public Point getCaretPos() {
        return new Point(this.caret.x, this.caret.y);
    }

    public void setCaretIndex(int index) {
        this.caret.typeIndex = index;
        this.caret.updateProps(null, true, null);
    }

    public void setCaretPos(Point pos) {
        CaretPosition caretPos = this.getClosestCaretPosition(pos.x, pos.y);
        if (caretPos != null) {
            this.caret.typeIndex = caretPos.index;
            this.caret.updateProps(caretPos, true, null);
        }
    }

    public Rectangle getCaretBoundingBox() {
        Point pos = this.getCaretPos();
        return new Rectangle(this.getTextX() + pos.x, this.getTextY() + pos.y - this.caret.height, 2, this.caret.height);
    }

    protected boolean acceptInput(int index, FairGlyph glyph) {
        if (this.maxLength > 0 && this.text.getLength() >= this.maxLength) {
            return false;
        }
        if (this.maxLines > 0) {
            FairType oldType = new FairType(this.text);
            this.text.insert(index, glyph);
            this.resetDrawOptions();
            if (this.getDrawOptions().getLineCount() > this.maxLines) {
                this.text = oldType;
                return false;
            }
            return true;
        }
        this.text.insert(index, glyph);
        return true;
    }

    public void setRegexMatchFull(String regexMatch) {
        this.setRegexMatchFull(Pattern.compile(regexMatch));
    }

    public void setRegexMatchFull(Pattern pattern) {
        this.regexPattern = pattern;
    }

    protected boolean validInput(String input) {
        if (this.regexPattern == null) {
            return true;
        }
        return this.regexPattern.matcher(input).matches();
    }

    protected FairGlyph getCharacterGlyph(char character) {
        return new FairCharacterGlyph(this.fontOptions, character);
    }

    protected boolean deleteSelection() {
        if (this.startSelection != null && this.endSelection != null && this.startSelection.index != this.endSelection.index) {
            int min = Math.min(this.startSelection.index + 1, this.endSelection.index + 1);
            int max = Math.max(this.startSelection.index, this.endSelection.index);
            for (int i = min; i <= max; ++i) {
                this.text.remove(min);
            }
            this.caret.typeIndex = min - 1;
            this.startSelection = null;
            this.endSelection = null;
            this.startSelectionMousePos = null;
            this.currentlySelecting = false;
            return true;
        }
        return false;
    }

    protected boolean handleCaretInput(InputEvent event, boolean allowNavigation) {
        FormInputEvent<FormFairTypeEdit> inputEvent = new FormInputEvent<FormFairTypeEdit>(this, event);
        this.inputEvents.onEvent(inputEvent);
        GameWindow window = WindowManager.getWindow();
        if (inputEvent.hasPreventedDefault()) {
            return false;
        }
        if (this.allowTyping && event.isCharacterEvent() && !event.getChar().equals("")) {
            event.use();
            String inputStr = event.getChar();
            if (!this.validInput(inputStr)) {
                return this.appendAtCaret((String)null);
            }
            return this.appendAtCaret(inputStr);
        }
        if (event.state && event.isKeyboardEvent()) {
            if (this.allowTyping && (event.getID() == 257 || event.getID() == 335)) {
                event.use();
                if (!this.validInput("\n")) {
                    return this.appendAtCaret((String)null);
                }
                return this.appendAtCaret("\n");
            }
            if (allowNavigation && event.getID() == 263) {
                event.use();
                if (this.caret.typeIndex >= 0) {
                    if (window.isKeyDown(FormFairTypeEdit.getSystemShiftWordKey())) {
                        for (int i = this.caret.typeIndex - 1; i >= -1; --i) {
                            this.caret.typeIndex = i;
                            if (i < 0 || !nextNonWordChar.matcher(Character.toString(this.text.get(i).getCharacter())).matches()) {
                                continue;
                            }
                            break;
                        }
                    } else {
                        --this.caret.typeIndex;
                    }
                    this.caret.updateProps(null, true, new FormCursorMoveEvent<FormFairTypeEdit>(this, false));
                    if (!window.isKeyDown(340)) {
                        this.clearSelection();
                    } else if (this.startSelection != null) {
                        this.endSelection = this.caret.getCurrentPosition();
                    }
                } else if (!window.isKeyDown(340)) {
                    this.clearSelection();
                }
                return false;
            }
            if (allowNavigation && event.getID() == 262) {
                event.use();
                if (this.caret.typeIndex < this.text.getLength() - 1) {
                    if (window.isKeyDown(FormFairTypeEdit.getSystemShiftWordKey())) {
                        for (int i = this.caret.typeIndex + 1; i < this.text.getLength(); ++i) {
                            this.caret.typeIndex = i;
                            if (!nextNonWordChar.matcher(Character.toString(this.text.get(i).getCharacter())).matches()) {
                                continue;
                            }
                            break;
                        }
                    } else {
                        ++this.caret.typeIndex;
                    }
                    this.caret.updateProps(null, true, new FormCursorMoveEvent<FormFairTypeEdit>(this, false));
                    if (!window.isKeyDown(340)) {
                        this.clearSelection();
                    } else if (this.startSelection != null) {
                        this.endSelection = this.caret.getCurrentPosition();
                    }
                } else if (!window.isKeyDown(340)) {
                    this.clearSelection();
                }
                return false;
            }
            if (allowNavigation && event.getID() == 265) {
                event.use();
                int currentLine = this.caret.prevLine;
                if (currentLine > 0) {
                    CaretPosition bestPosition = null;
                    int bestDelta = Integer.MAX_VALUE;
                    for (CaretPosition pos : this.getPossibleCaretPositions(currentLine - 1)) {
                        int deltaX = Math.abs(pos.x - this.caret.prevX);
                        if (bestPosition != null && deltaX > bestDelta) continue;
                        bestPosition = pos;
                        bestDelta = deltaX;
                    }
                    if (bestPosition != null) {
                        this.caret.typeIndex = bestPosition.index;
                        this.caret.updateProps(bestPosition, false, new FormCursorMoveEvent<FormFairTypeEdit>(this, false));
                        if (!window.isKeyDown(340)) {
                            this.clearSelection();
                        } else if (this.startSelection != null) {
                            this.endSelection = this.caret.getCurrentPosition();
                        }
                    }
                } else if (!window.isKeyDown(340)) {
                    this.clearSelection();
                }
                return false;
            }
            if (allowNavigation && event.getID() == 264) {
                event.use();
                int currentLine = this.caret.prevLine;
                CaretPosition bestPosition = null;
                int bestDelta = Integer.MAX_VALUE;
                for (CaretPosition pos : this.getPossibleCaretPositions(currentLine + 1)) {
                    int deltaX = Math.abs(pos.x - this.caret.prevX);
                    if (bestPosition != null && deltaX > bestDelta) continue;
                    bestPosition = pos;
                    bestDelta = deltaX;
                }
                if (bestPosition != null) {
                    this.caret.typeIndex = bestPosition.index;
                    this.caret.updateProps(bestPosition, false, new FormCursorMoveEvent<FormFairTypeEdit>(this, false));
                    if (!window.isKeyDown(340)) {
                        this.clearSelection();
                    } else if (this.startSelection != null) {
                        this.endSelection = this.caret.getCurrentPosition();
                    }
                } else if (!window.isKeyDown(340)) {
                    this.clearSelection();
                }
                return false;
            }
            if (allowNavigation && event.getID() == 269) {
                event.use();
                int currentLine = this.caret.prevLine;
                CaretPosition bestPosition = null;
                for (CaretPosition pos : this.getPossibleCaretPositions(currentLine)) {
                    if (bestPosition != null && bestPosition.x > pos.x) continue;
                    bestPosition = pos;
                }
                if (bestPosition != null) {
                    this.caret.typeIndex = bestPosition.index;
                    this.caret.updateProps(bestPosition, true, new FormCursorMoveEvent<FormFairTypeEdit>(this, false));
                    if (!window.isKeyDown(340)) {
                        this.clearSelection();
                    } else if (this.startSelection != null) {
                        this.endSelection = this.caret.getCurrentPosition();
                    }
                } else if (!window.isKeyDown(340)) {
                    this.clearSelection();
                }
                return false;
            }
            if (allowNavigation && event.getID() == 268) {
                event.use();
                int currentLine = this.caret.prevLine;
                CaretPosition bestPosition = null;
                for (CaretPosition pos : this.getPossibleCaretPositions(currentLine)) {
                    if (bestPosition != null && bestPosition.x < pos.x) continue;
                    bestPosition = pos;
                }
                if (bestPosition != null) {
                    this.caret.typeIndex = bestPosition.index;
                    this.caret.updateProps(bestPosition, true, new FormCursorMoveEvent<FormFairTypeEdit>(this, false));
                    if (!window.isKeyDown(340)) {
                        this.clearSelection();
                    } else if (this.startSelection != null) {
                        this.endSelection = this.caret.getCurrentPosition();
                    }
                } else if (!window.isKeyDown(340)) {
                    this.clearSelection();
                }
                return false;
            }
            if (window.isKeyDown(FormFairTypeEdit.getSystemPasteKey()) && event.getID() == 67) {
                event.use();
                if (this.startSelection != null && this.endSelection != null && this.startSelection.index != this.endSelection.index) {
                    StringBuilder builder = new StringBuilder();
                    int min = Math.min(this.startSelection.index + 1, this.endSelection.index + 1);
                    int max = Math.max(this.startSelection.index, this.endSelection.index);
                    for (int i = min; i <= max; ++i) {
                        builder.append(this.text.get(i).getParseString());
                    }
                    window.putClipboard(builder.toString());
                }
                return false;
            }
            if (this.allowTyping && window.isKeyDown(FormFairTypeEdit.getSystemPasteKey()) && event.getID() == 86) {
                event.use();
                String clipboardStr = window.getClipboard();
                if (clipboardStr == null) {
                    return false;
                }
                if (!this.validInput(clipboardStr = clipboardStr.replace("\r", ""))) {
                    return this.appendAtCaret((String)null);
                }
                FairType clipboardType = new FairType().append(this.fontOptions, clipboardStr).applyParsers(this.parsers);
                return this.appendAtCaret(clipboardType.getGlyphsArray());
            }
            if (this.allowTyping && event.getID() == 259) {
                event.use();
                this.handleBackspace();
                return true;
            }
            if (this.allowTyping && event.getID() == 261) {
                event.use();
                this.handleDelete();
                return true;
            }
            if (allowNavigation && window.isKeyDown(341) && event.getID() == 65) {
                event.use();
                this.selectAll();
                return false;
            }
        }
        return false;
    }

    public void handleBackspace() {
        if (this.deleteSelection()) {
            this.resetDrawOptions();
            this.caret.updateProps(null, true, new FormCursorMoveEvent<FormFairTypeEdit>(this, false));
        } else if (this.caret.typeIndex >= 0) {
            if (WindowManager.getWindow().isKeyDown(FormFairTypeEdit.getSystemShiftWordKey())) {
                for (int i = this.caret.typeIndex - 1; i >= -1; --i) {
                    this.text.remove(this.caret.typeIndex--);
                    if (this.caret.typeIndex >= 0 && !nextNonWordChar.matcher(Character.toString(this.text.get(this.caret.typeIndex).getCharacter())).matches()) {
                        continue;
                    }
                    break;
                }
            } else {
                this.text.remove(this.caret.typeIndex--);
            }
            this.resetDrawOptions();
            this.caret.updateProps(null, true, new FormCursorMoveEvent<FormFairTypeEdit>(this, false));
        }
    }

    public void handleDelete() {
        if (this.deleteSelection()) {
            this.resetDrawOptions();
            this.caret.updateProps(null, true, new FormCursorMoveEvent<FormFairTypeEdit>(this, false));
        } else if (this.caret.typeIndex < this.text.getLength() - 1) {
            if (WindowManager.getWindow().isKeyDown(FormFairTypeEdit.getSystemPasteKey())) {
                int max = this.text.getLength() - 1;
                for (int i = this.caret.typeIndex; i < max; ++i) {
                    this.text.remove(this.caret.typeIndex + 1);
                    if (this.caret.typeIndex + 1 < this.text.getLength() && !nextNonWordChar.matcher(Character.toString(this.text.get(this.caret.typeIndex + 1).getCharacter())).matches()) {
                        continue;
                    }
                    break;
                }
            } else {
                this.text.remove(this.caret.typeIndex + 1);
            }
            this.resetDrawOptions();
            this.caret.updateProps(null, true, new FormCursorMoveEvent<FormFairTypeEdit>(this, false));
        }
    }

    protected class CaretPosition {
        public final GlyphContainer container;
        public final int index;
        public final int x;
        public final int y;
        public final int line;
        public final int lineHeight;

        public CaretPosition(GlyphContainer container, int index, int x, int y, int line, int lineHeight) {
            this.container = container;
            this.index = index;
            this.x = container == null ? x : (int)container.x;
            this.y = container == null ? y : (int)container.y;
            this.line = container == null ? line : container.line;
            this.lineHeight = container == null ? lineHeight : (int)container.lineHeight;
        }

        public CaretPosition(GlyphContainer container, int index, int x, int y) {
            this.container = container;
            this.index = index;
            this.x = x;
            this.y = y;
            this.line = container.line;
            this.lineHeight = (int)container.lineHeight;
        }
    }

    protected class Caret {
        public int typeIndex = 0;
        public int x;
        public int prevX;
        public int y;
        public CaretPosition prevPosition;
        public int prevLine = -1;
        public int height;
        public Supplier<Color> colorSupplier;

        protected Caret() {
        }

        public void updateProps(CaretPosition position, boolean updatePrevX, FormCursorMoveEvent<FormFairTypeEdit> moveEvent) {
            int oldX = this.x;
            int oldY = this.y;
            this.typeIndex = Math.min(Math.max(this.typeIndex, -1), FormFairTypeEdit.this.text.getLength() - 1);
            if (position == null) {
                LinkedList<GlyphContainer> drawList = FormFairTypeEdit.this.getDrawOptions().getDrawList();
                if (this.typeIndex == -1) {
                    GlyphContainer container = drawList.isEmpty() ? null : (GlyphContainer)drawList.get(0);
                    position = new CaretPosition(container, this.typeIndex, 0, FormFairTypeEdit.this.fontOptions.getSize(), 0, FormFairTypeEdit.this.fontOptions.getSize());
                } else {
                    GlyphContainer container = (GlyphContainer)drawList.get(this.typeIndex);
                    Dimension dim = container.glyph.getDimensions().toInt();
                    position = new CaretPosition(container, this.typeIndex, GameMath.ceil(container.x + (float)dim.width), GameMath.ceil(container.y));
                }
            }
            this.prevPosition = position;
            if (position.container != null) {
                this.colorSupplier = position.container.currentColor;
                if (this.colorSupplier == null) {
                    this.colorSupplier = () -> FormFairTypeEdit.this.textColor;
                }
                this.height = GameMath.ceil(position.container.glyph.getDimensions().height);
                if (this.height <= 0) {
                    this.height = FormFairTypeEdit.this.fontOptions.getSize();
                }
            } else {
                this.colorSupplier = () -> FormFairTypeEdit.this.textColor;
                this.height = FormFairTypeEdit.this.fontOptions.getSize();
            }
            this.x = position.x;
            this.y = position.y;
            this.prevLine = position.line;
            if (updatePrevX) {
                this.prevX = this.x;
            }
            if (moveEvent != null && (this.x != oldX || this.y != oldY)) {
                FormFairTypeEdit.this.caretMoveEvents.onEvent(moveEvent);
            }
        }

        public CaretPosition getCurrentPosition() {
            GlyphContainer container = this.typeIndex < 0 ? null : FormFairTypeEdit.this.getDrawOptions().getDrawList().get(this.typeIndex);
            return new CaretPosition(container, this.typeIndex, this.x, this.y, this.prevLine, this.height);
        }

        public void draw(Color outlineColor) {
            boolean draw;
            if (!FormFairTypeEdit.this.isTyping() || !FormFairTypeEdit.this.allowTyping) {
                return;
            }
            boolean bl = draw = System.currentTimeMillis() / 500L % 2L == 0L;
            if (draw) {
                int y = this.y;
                int height = this.height;
                if (outlineColor != null) {
                    Renderer.initQuadDraw(4, height).color(outlineColor).draw(FormFairTypeEdit.this.getTextX() + this.x - 1, FormFairTypeEdit.this.getTextY() + y - height);
                    --y;
                    height -= 2;
                }
                Renderer.initQuadDraw(2, height).color(this.colorSupplier.get()).draw(FormFairTypeEdit.this.getTextX() + this.x, FormFairTypeEdit.this.getTextY() + y - height);
            }
        }
    }
}

