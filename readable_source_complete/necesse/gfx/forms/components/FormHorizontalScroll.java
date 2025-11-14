/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import java.util.Objects;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.MouseWheelBuffer;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.entity.mobs.PlayerMob;
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
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.HUD;
import necesse.gfx.ui.HoverStateTextures;

public class FormHorizontalScroll<T>
extends FormComponent
implements FormPositionContainer {
    private FormPosition position;
    private final int width;
    private boolean active;
    protected int scroll;
    protected ScrollElement<T>[] elements;
    protected ScrollElement<T> customElement;
    public DrawOption drawOption;
    private final MouseWheelBuffer wheelBuffer = new MouseWheelBuffer(false);
    private boolean isHovering;
    private boolean isHoveringLeft;
    private boolean isHoveringRight;
    private boolean isControllerSelected;
    public boolean allowScroll = false;
    private final FormEventsHandler<FormInputEvent<FormHorizontalScroll<T>>> changedEvents = new FormEventsHandler();

    @SafeVarargs
    public FormHorizontalScroll(int x, int y, int width, DrawOption drawOption, int startScroll, ScrollElement<T> ... elements) {
        this.position = new FormFixedPosition(x, y);
        this.width = width;
        this.drawOption = drawOption;
        this.scroll = startScroll;
        this.elements = elements;
        this.setActive(true);
    }

    public FormHorizontalScroll<T> onChanged(FormEventListener<FormInputEvent<FormHorizontalScroll<T>>> listener) {
        this.changedEvents.addListener(listener);
        return this;
    }

    public ScrollElement<T> getCurrent() {
        if (this.scroll == -1) {
            return this.customElement;
        }
        return this.elements[this.scroll];
    }

    public T getValue() {
        if (this.scroll == -1) {
            return this.customElement.value;
        }
        return this.elements[this.scroll].value;
    }

    public boolean setValue(T value) {
        for (int i = 0; i < this.elements.length; ++i) {
            if (!Objects.equals(this.elements[i].value, value)) continue;
            this.scroll = i;
            return true;
        }
        return false;
    }

    public void setElement(ScrollElement<T> element) {
        Objects.requireNonNull(element);
        if (!this.setValue(element.value)) {
            this.customElement = element;
            this.scroll = -1;
        }
    }

    public boolean hasValue(T value) {
        for (ScrollElement<T> element : this.elements) {
            if (!Objects.equals(element.value, value)) continue;
            return true;
        }
        return false;
    }

    public void setData(ScrollElement<T>[] elements) {
        this.elements = elements;
        if (this.scroll >= elements.length) {
            this.scroll = elements.length - 1;
        }
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        if (event.isMouseMoveEvent()) {
            this.isHovering = this.isMouseOver(event);
            this.isHoveringLeft = this.isMouseOverLeft(event);
            this.isHoveringRight = this.isMouseOverRight(event);
            if (this.isHovering || this.isHoveringLeft || this.isHoveringRight) {
                event.useMove();
            }
        }
        if (!this.isActive()) {
            return;
        }
        if (this.allowScroll && event.isMouseWheelEvent() && event.state && this.isMouseOver(event)) {
            this.wheelBuffer.add(event);
            this.wheelBuffer.useScrollY(isPositive -> {
                if (isPositive) {
                    this.increase(event);
                } else {
                    this.decrease(event);
                }
            });
        }
        if (!event.state || event.isKeyboardEvent()) {
            return;
        }
        if (event.getID() == -100) {
            if (this.isMouseOverLeft(event)) {
                this.decrease(event);
            }
            if (this.isMouseOverRight(event)) {
                this.increase(event);
            }
        }
    }

    @Override
    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        if (this.isActive()) {
            if (event.getState() == ControllerInput.MENU_SELECT) {
                if (this.isControllerFocus() && event.buttonState) {
                    this.isControllerSelected = true;
                    event.use();
                }
            } else if ((event.getState() == ControllerInput.MENU_BACK || event.getState() == ControllerInput.MAIN_MENU) && this.isControllerSelected && event.buttonState) {
                this.isControllerSelected = false;
                event.use();
            }
        }
    }

    @Override
    public void onControllerUnfocused(ControllerFocus current) {
        super.onControllerUnfocused(current);
        this.isControllerSelected = false;
    }

    @Override
    public boolean handleControllerNavigate(int dir, ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        if (this.isControllerSelected && this.isActive()) {
            switch (dir) {
                case 1: {
                    this.increase(InputEvent.ControllerButtonEvent(event, tickManager));
                    event.use();
                    break;
                }
                case 3: {
                    this.decrease(InputEvent.ControllerButtonEvent(event, tickManager));
                    event.use();
                }
            }
            return true;
        }
        return super.handleControllerNavigate(dir, event, tickManager, perspective);
    }

    @Override
    public void addNextControllerFocus(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
        ControllerFocus.add(list, area, this, this.getBoundingBox(), currentXOffset, currentYOffset, this.controllerInitialFocusPriority, customNavigationHandler);
    }

    public void increase(InputEvent event) {
        int scrollBefore = this.scroll++;
        FormInputEvent<FormHorizontalScroll> fEvent = new FormInputEvent<FormHorizontalScroll>(this, event);
        if (this.scroll >= this.elements.length) {
            this.scroll = 0;
        }
        this.changedEvents.onEvent(fEvent);
        if (fEvent.hasPreventedDefault()) {
            this.scroll = scrollBefore;
        } else {
            this.playTickSound();
        }
        if (event != null) {
            event.use();
        }
    }

    public void decrease(InputEvent event) {
        int scrollBefore = this.scroll--;
        FormInputEvent<FormHorizontalScroll> fEvent = new FormInputEvent<FormHorizontalScroll>(this, event);
        if (this.scroll < 0) {
            this.scroll = this.elements.length - 1;
        }
        this.changedEvents.onEvent(fEvent);
        if (fEvent.hasPreventedDefault()) {
            this.scroll = scrollBefore;
        } else {
            this.playTickSound();
        }
        if (event != null) {
            event.use();
        }
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        Color col = this.getInterfaceStyle().activeTextColor;
        if (!this.isActive()) {
            col = this.getInterfaceStyle().inactiveTextColor;
        }
        ScrollElement str = this.getDrawText();
        str.draw(this.getX() + this.width / 2, this.getY() + 1, col, this.isHovering || this.isControllerFocus() && this.isControllerSelected, this.drawOption);
        HoverStateTextures scrollButtons = this.getInterfaceStyle().button_navigate_horizontal;
        if (this.isActive()) {
            GameTexture leftTexture = this.isHoveringLeft || this.isControllerSelected ? scrollButtons.highlighted : scrollButtons.active;
            Color leftColor = this.isHoveringLeft || this.isControllerSelected ? this.getInterfaceStyle().highlightElementColor : this.getInterfaceStyle().activeElementColor;
            GameTexture rightTexture = this.isHoveringRight || this.isControllerSelected ? scrollButtons.highlighted : scrollButtons.active;
            Color rightColor = this.isHoveringRight || this.isControllerSelected ? this.getInterfaceStyle().highlightElementColor : this.getInterfaceStyle().activeElementColor;
            leftTexture.initDraw().color(leftColor).draw(this.getX(), this.getY() - 1);
            rightTexture.initDraw().color(rightColor).mirrorX().draw(this.getX() + this.width - rightTexture.getWidth() - 1, this.getY() - 1);
        } else {
            scrollButtons.active.initDraw().color(this.getInterfaceStyle().inactiveElementColor).draw(this.getX(), this.getY() - 1);
            scrollButtons.active.initDraw().mirrorX().color(this.getInterfaceStyle().inactiveElementColor).draw(this.getX() + this.width - scrollButtons.active.getWidth() - 1, this.getY() - 1);
        }
    }

    @Override
    public void drawControllerFocus(ControllerFocus current) {
        if (this.isControllerSelected) {
            Rectangle box = current.boundingBox;
            int padding = 5;
            box = new Rectangle(box.x - padding, box.y - padding, box.width + padding * 2, box.height + padding * 2);
            HUD.selectBoundOptions(this.getInterfaceStyle().controllerFocusBoundsHighlightColor, true, box).draw();
        } else {
            super.drawControllerFocus(current);
            GameTooltipManager.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
        }
    }

    public ScrollElement getDrawText() {
        if (this.scroll == -1) {
            return this.getCustomElement();
        }
        return this.elements[this.scroll];
    }

    public ScrollElement getCustomElement() {
        return this.customElement;
    }

    public boolean isMouseOverLeft(InputEvent event) {
        if (event.isMoveUsed()) {
            return false;
        }
        return new Rectangle(this.getX(), this.getY(), 10, 14).contains(event.pos.hudX, event.pos.hudY);
    }

    public boolean isMouseOverRight(InputEvent event) {
        if (event.isMoveUsed()) {
            return false;
        }
        return new Rectangle(this.getX() + this.width - 10, this.getY(), 10, 14).contains(event.pos.hudX, event.pos.hudY);
    }

    @Override
    public List<Rectangle> getHitboxes() {
        return FormHorizontalScroll.singleBox(new Rectangle(this.getX(), this.getY() - 2, this.width, 16));
    }

    public boolean isHovering() {
        return this.isHovering;
    }

    public boolean isHoveringLeft() {
        return this.isHoveringLeft;
    }

    public boolean isHoveringRight() {
        return this.isHoveringRight;
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

    public static enum DrawOption {
        string,
        value,
        valueOnHover;

    }

    public static class ScrollElement<T> {
        public T value;
        public FontOptions fontOptions;
        public GameMessage str;
        public GameTooltips tooltips;

        public ScrollElement(T value, GameMessage str, FontOptions fontOptions, GameTooltips tooltips) {
            this.value = value;
            this.str = str;
            this.fontOptions = fontOptions;
            this.tooltips = tooltips;
        }

        public ScrollElement(T value, String str, FontOptions fontOptions, GameTooltips tooltips) {
            this(value, new StaticMessage(str), fontOptions, tooltips);
        }

        public ScrollElement(T value, GameMessage str, FontOptions fontOptions) {
            this(value, str, fontOptions, null);
        }

        public ScrollElement(T value, String str, FontOptions fontOptions) {
            this(value, new StaticMessage(str), fontOptions);
        }

        public ScrollElement(T value, GameMessage str) {
            this(value, str, new FontOptions(12));
        }

        public ScrollElement(T value, String str) {
            this(value, new StaticMessage(str));
        }

        public void draw(int drawX, int drawY, Color defaultColor, boolean isMouseOver, DrawOption drawOption) {
            String str = "N/A";
            switch (drawOption) {
                case value: {
                    str = String.valueOf(this.value);
                    break;
                }
                case string: {
                    str = this.str == null ? "null" : this.str.translate();
                    break;
                }
                case valueOnHover: {
                    str = isMouseOver ? String.valueOf(this.value) : (this.str == null ? "null" : this.str.translate());
                }
            }
            FontOptions options = new FontOptions(this.fontOptions).defaultColor(defaultColor);
            FontManager.bit.drawString(drawX - FontManager.bit.getWidthCeil(str, options) / 2, drawY + 6 - FontManager.bit.getHeightCeil(str, options) / 2, str, options);
            if (isMouseOver && this.tooltips != null) {
                GameTooltipManager.addTooltip(this.tooltips, TooltipLocation.FORM_FOCUS);
            }
        }
    }
}

