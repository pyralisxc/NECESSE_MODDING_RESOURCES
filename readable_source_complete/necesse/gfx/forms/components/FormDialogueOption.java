/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.FormClickHandler;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormEventsHandler;
import necesse.gfx.forms.events.FormInputEvent;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameFont.GameFontHandler;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public class FormDialogueOption
extends FormComponent
implements FormPositionContainer {
    private static final int[] toolbarHotkeys = new int[]{49, 50, 51, 52, 53, 54, 55, 56, 57, 48};
    private GameFontHandler font = FontManager.bit;
    private int optionNumber;
    private FormPosition position;
    private GameMessage text;
    private int numberWidth;
    private int maxWidth;
    private FontOptions fontOptions;
    private ArrayList<Line> lines = new ArrayList();
    private int linesWidth;
    private boolean isHovering;
    private FormClickHandler clickHandler;
    private boolean active = true;
    public boolean mouseOverCoversEntireWidth = true;
    protected FormEventsHandler<FormInputEvent<FormDialogueOption>> clickedEvents;
    public Supplier<GameTooltips> tooltipsSupplier;

    public FormDialogueOption(int optionNumber, GameMessage text, FontOptions fontOptions, int x, int y, int maxWidth) {
        this.optionNumber = optionNumber;
        this.fontOptions = fontOptions.defaultColor(this.getInterfaceStyle().activeTextColor);
        this.position = new FormFixedPosition(x, y);
        this.numberWidth = optionNumber > 0 ? this.font.getWidthCeil(optionNumber + ". ", fontOptions) : 0;
        this.setText(text, maxWidth);
        this.clickedEvents = new FormEventsHandler();
        this.clickHandler = new FormClickHandler(e -> this.acceptsEvents() && this.isMouseOver((InputEvent)e), -100, e -> {
            FormInputEvent<FormDialogueOption> fEvent = new FormInputEvent<FormDialogueOption>(this, (InputEvent)e);
            this.clickedEvents.onEvent(fEvent);
            if (!fEvent.hasPreventedDefault()) {
                this.pressed((InputEvent)e);
                e.use();
            }
        });
    }

    public FormDialogueOption onClicked(FormEventListener<FormInputEvent<FormDialogueOption>> listener) {
        this.clickedEvents.addListener(listener);
        return this;
    }

    protected void pressed(InputEvent event) {
        this.playTickSound();
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        if (event.isMouseMoveEvent()) {
            this.isHovering = this.isMouseOver(event);
            if (this.isHovering) {
                event.useMove();
            }
        }
        this.clickHandler.handleEvent(event);
        if (this.isActive() && event.isKeyboardEvent() && event.state && this.optionNumber >= 1 && this.optionNumber <= 10 && event.getID() == toolbarHotkeys[this.optionNumber - 1]) {
            this.clickHandler.forceClick(event);
        }
    }

    @Override
    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        if (this.isActive() && event.getState() == ControllerInput.MENU_SELECT) {
            if (this.isControllerFocus() && event.buttonState) {
                InputEvent inputEvent = InputEvent.ControllerButtonEvent(event, tickManager);
                this.clickHandler.forceHandleEvent(inputEvent);
                event.use();
            } else if (!event.buttonState && this.clickHandler.isDown()) {
                this.clickHandler.forceHandleEvent(InputEvent.ControllerButtonEvent(event, tickManager));
                event.use();
            }
        }
    }

    @Override
    public void addNextControllerFocus(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
        ControllerFocus.add(list, area, this, this.getBoundingBox(), currentXOffset, currentYOffset, this.controllerInitialFocusPriority, customNavigationHandler);
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        GameTooltips tooltips;
        if (this.text.hasUpdated()) {
            this.updateLines();
        }
        int x = this.getX();
        int y = this.getY();
        FontOptions fontOptions = new FontOptions(this.fontOptions).color(this.getDrawColor());
        if (this.optionNumber > 0) {
            this.font.drawString(x, y, this.optionNumber + ". ", fontOptions);
        }
        for (int i = 0; i < this.lines.size(); ++i) {
            this.font.drawString(x + this.numberWidth, y + i * fontOptions.getSize(), this.lines.get((int)i).str, fontOptions);
        }
        if (this.isHovering() && this.tooltipsSupplier != null && (tooltips = this.tooltipsSupplier.get()) != null) {
            GameTooltipManager.addTooltip(tooltips, TooltipLocation.FORM_FOCUS);
        }
    }

    @Override
    public void drawControllerFocus(ControllerFocus current) {
        super.drawControllerFocus(current);
        GameTooltipManager.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
    }

    @Override
    public List<Rectangle> getHitboxes() {
        return FormDialogueOption.singleBox(new Rectangle(this.getX(), this.getY(), this.numberWidth + this.linesWidth + 2, this.lines.size() * this.fontOptions.getSize()));
    }

    @Override
    public boolean isMouseOver(InputEvent event) {
        if (event.isMoveUsed()) {
            return false;
        }
        int x = this.getX();
        int y = this.getY();
        if (this.mouseOverCoversEntireWidth) {
            return new Rectangle(x, y, this.maxWidth, this.lines.size() * this.fontOptions.getSize()).contains(event.pos.hudX, event.pos.hudY);
        }
        if (new Rectangle(x, y, this.numberWidth, this.fontOptions.getSize()).contains(event.pos.hudX, event.pos.hudY)) {
            return true;
        }
        for (int i = 0; i < this.lines.size(); ++i) {
            if (!new Rectangle(x, y + i * this.fontOptions.getSize(), this.lines.get((int)i).width + this.numberWidth, this.fontOptions.getSize()).contains(event.pos.hudX, event.pos.hudY)) continue;
            return true;
        }
        return false;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
        this.updateLines();
    }

    public void setText(GameMessage text) {
        this.text = text;
        this.updateLines();
    }

    public void setText(GameMessage text, int maxWidth) {
        this.text = text;
        this.maxWidth = maxWidth;
        this.updateLines();
    }

    public GameMessage getText() {
        return this.text;
    }

    protected void updateLines() {
        String str = this.text.translate();
        ArrayList<String> strLines = GameUtils.breakString(str, this.fontOptions, this.maxWidth - this.numberWidth);
        this.lines = new ArrayList(strLines.size());
        this.linesWidth = 0;
        for (String strLine : strLines) {
            int width = this.font.getWidthCeil(strLine, this.fontOptions);
            this.lines.add(new Line(strLine, width));
            this.linesWidth = Math.max(this.linesWidth, width);
        }
    }

    protected boolean acceptsEvents() {
        return this.isActive();
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public FormPosition getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(FormPosition position) {
        this.position = position;
    }

    protected boolean isHovering() {
        return this.isHovering;
    }

    public Color getDrawColor() {
        if (!this.isActive()) {
            return this.getInterfaceStyle().inactiveTextColor;
        }
        if (this.isHovering()) {
            return this.getInterfaceStyle().highlightTextColor;
        }
        return this.getInterfaceStyle().activeTextColor;
    }

    protected static class Line {
        public final String str;
        public final int width;

        public Line(String str, int width) {
            this.str = str;
            this.width = width;
        }
    }
}

