/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.MouseWheelBuffer;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameUtils;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormEventsHandler;
import necesse.gfx.forms.events.FormGrabbedEvent;
import necesse.gfx.forms.events.FormInputEvent;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.HUD;

public class FormSlider
extends FormComponent
implements FormPositionContainer {
    private FormPosition position;
    private final int width;
    protected String text;
    public boolean drawValue;
    public boolean drawValueInPercent;
    public boolean allowScroll = true;
    private int value;
    private float percentage;
    private int minValue;
    private int maxValue;
    private boolean mouseDown;
    private boolean isControllerSelected;
    private int mouseDownGlobalX;
    private int mouseDownBarX;
    private final FontOptions fontOptions;
    private final MouseWheelBuffer wheelBuffer = new MouseWheelBuffer(false);
    private boolean isHoveringSlider;
    private boolean isHoveringBar;
    private boolean isHoveringText;
    private final FormEventsHandler<FormInputEvent<FormSlider>> changedEvents = new FormEventsHandler();
    private final FormEventsHandler<FormGrabbedEvent<FormSlider>> grabEvents = new FormEventsHandler();
    private final FormEventsHandler<FormInputEvent<FormSlider>> scrollEvents = new FormEventsHandler();

    public FormSlider(String text, int x, int y, int startValue, int minValue, int maxValue, int width, FontOptions fontOptions) {
        this.text = text;
        this.position = new FormFixedPosition(x, y);
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.width = width;
        this.fontOptions = fontOptions.defaultColor(this.getInterfaceStyle().activeTextColor);
        this.setValue(startValue);
        this.drawValue = true;
        this.drawValueInPercent = true;
    }

    public FormSlider(String text, int x, int y, int startValue, int minValue, int maxValue, int width) {
        this(text, x, y, startValue, minValue, maxValue, width, new FontOptions(16));
    }

    public FormSlider setValue(int value) {
        this.value = Math.max(this.minValue, Math.min(this.maxValue, value));
        this.percentage = (float)(this.value - this.minValue) / (float)(this.maxValue - this.minValue);
        return this;
    }

    public int getValue() {
        return this.value;
    }

    public int getMaxValue() {
        return this.maxValue;
    }

    public int getMinValue() {
        return this.minValue;
    }

    public FormSlider setPercentage(float percentage) {
        this.percentage = Math.max(0.0f, Math.min(percentage, 1.0f));
        this.value = (int)(this.percentage * (float)(this.maxValue - this.minValue)) + this.minValue;
        return this;
    }

    public float getPercentage() {
        return this.percentage;
    }

    public FormSlider setRange(int minValue, int maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.setValue(this.getValue());
        return this;
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        FormGrabbedEvent<FormSlider> ev;
        if (this.allowScroll && event.state && event.isMouseWheelEvent() && this.isMouseOverBar(event)) {
            this.wheelBuffer.add(event);
            this.wheelBuffer.useScrollY(isPositive -> {
                if (isPositive) {
                    if (this.getValue() < this.maxValue) {
                        FormInputEvent<FormSlider> ev = new FormInputEvent<FormSlider>(this, event);
                        this.setValue(this.getValue() + 1);
                        this.changedEvents.onEvent(ev);
                        this.scrollEvents.onEvent(ev);
                        if (!ev.hasPreventedDefault()) {
                            this.playTickSound();
                        }
                    }
                } else if (this.getValue() > this.minValue) {
                    FormInputEvent<FormSlider> ev = new FormInputEvent<FormSlider>(this, event);
                    this.setValue(this.getValue() - 1);
                    this.changedEvents.onEvent(ev);
                    this.scrollEvents.onEvent(ev);
                    if (!ev.hasPreventedDefault()) {
                        this.playTickSound();
                    }
                }
            });
            event.use();
        }
        if (event.isKeyboardEvent()) {
            return;
        }
        if (event.state) {
            if (event.getID() == -100 & this.isMouseOverBar(event)) {
                ev = new FormGrabbedEvent<FormSlider>(this, event, true);
                this.mouseDown = true;
                this.mouseDownGlobalX = WindowManager.getWindow().mousePos().hudX;
                this.mouseDownBarX = event.pos.hudX - this.getX() - 5;
                this.grabEvents.onEvent(ev);
                if (!ev.hasPreventedDefault()) {
                    this.playTickSound();
                }
                FormInputEvent<FormSlider> changeEvent = new FormInputEvent<FormSlider>(this, event);
                this.setPercentage((float)this.mouseDownBarX / ((float)this.width - 10.0f));
                this.changedEvents.onEvent(changeEvent);
                event.use();
            }
        } else if (event.getID() == -100 & this.mouseDown) {
            ev = new FormGrabbedEvent<FormSlider>(this, event, false);
            this.mouseDown = false;
            this.grabEvents.onEvent(ev);
            event.use();
        }
        if (event.isMouseMoveEvent()) {
            if (this.mouseDown) {
                int lastValue = this.getValue();
                int mouseX = WindowManager.getWindow().mousePos().hudX - this.mouseDownGlobalX;
                FormInputEvent<FormSlider> ev2 = new FormInputEvent<FormSlider>(this, event);
                this.setPercentage((float)(this.mouseDownBarX + mouseX) / ((float)this.width - 10.0f));
                int newValue = this.getValue();
                if (newValue != lastValue) {
                    this.changedEvents.onEvent(ev2);
                }
            }
            this.isHoveringBar = this.isMouseOverBar(event);
            this.isHoveringSlider = this.isMouseOverSlider(event);
            this.isHoveringText = this.isMouseOverText(event);
            if (this.isHoveringBar || this.isHoveringSlider || this.isHoveringText) {
                event.useMove();
            }
        }
    }

    @Override
    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        if (event.getState() == ControllerInput.MENU_SELECT) {
            if (this.isControllerFocus() && event.buttonState) {
                FormGrabbedEvent<FormSlider> ev = new FormGrabbedEvent<FormSlider>(this, InputEvent.ControllerButtonEvent(event, tickManager), true);
                this.isControllerSelected = true;
                this.grabEvents.onEvent(ev);
                if (!ev.hasPreventedDefault()) {
                    this.playTickSound();
                }
                event.use();
            }
        } else if ((event.getState() == ControllerInput.MENU_BACK || event.getState() == ControllerInput.MAIN_MENU) && this.isControllerSelected && event.buttonState) {
            FormGrabbedEvent<FormSlider> ev = new FormGrabbedEvent<FormSlider>(this, InputEvent.ControllerButtonEvent(event, tickManager), false);
            this.isControllerSelected = false;
            this.grabEvents.onEvent(ev);
            if (!ev.hasPreventedDefault()) {
                this.playTickSound();
            }
            event.use();
        }
    }

    @Override
    public void onControllerUnfocused(ControllerFocus current) {
        super.onControllerUnfocused(current);
        this.isControllerSelected = false;
    }

    @Override
    public boolean handleControllerNavigate(int dir, ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        if (this.isControllerSelected) {
            switch (dir) {
                case 1: {
                    if (this.getValue() < this.maxValue) {
                        FormInputEvent<FormSlider> ev = new FormInputEvent<FormSlider>(this, InputEvent.ControllerButtonEvent(event, tickManager));
                        this.setValue(this.getValue() + 1);
                        this.changedEvents.onEvent(ev);
                        this.scrollEvents.onEvent(ev);
                        if (!ev.hasPreventedDefault() && event.shouldSubmitSound()) {
                            this.playTickSound();
                        }
                    }
                    event.use();
                    break;
                }
                case 3: {
                    if (this.getValue() > this.minValue) {
                        FormInputEvent<FormSlider> ev = new FormInputEvent<FormSlider>(this, InputEvent.ControllerButtonEvent(event, tickManager));
                        this.setValue(this.getValue() - 1);
                        this.changedEvents.onEvent(ev);
                        this.scrollEvents.onEvent(ev);
                        if (!ev.hasPreventedDefault() && event.shouldSubmitSound()) {
                            this.playTickSound();
                        }
                    }
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

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        Color color = this.getInterfaceStyle().activeElementColor;
        GameTexture texture = this.getInterfaceStyle().slider.active;
        if (this.isHoveringSlider || this.isGrabbed()) {
            color = this.getInterfaceStyle().highlightElementColor;
            texture = this.getInterfaceStyle().slider.highlighted;
        }
        String valueText = this.getValueText();
        int valueTextWidth = FontManager.bit.getWidthCeil(valueText, this.fontOptions);
        FontManager.bit.drawString(this.getX() + this.width - valueTextWidth, this.getY(), valueText, this.fontOptions);
        int maxTextWidth = this.width - valueTextWidth;
        String maxString = GameUtils.maxString(this.text, this.fontOptions, maxTextWidth - 10);
        if (!maxString.equals(this.text)) {
            maxString = maxString + "...";
            if (this.isHoveringText || this.isControllerSelected || this.isControllerFocus(this)) {
                GameTooltipManager.addTooltip(new StringTooltips(this.text), TooltipLocation.FORM_FOCUS);
            }
        }
        FontManager.bit.drawString(this.getX(), this.getY(), maxString, this.fontOptions);
        int textHeight = this.getTextHeight();
        FormSlider.drawWidthComponent(new GameSprite(texture, 0, 0, texture.getHeight()), new GameSprite(texture, 1, 0, texture.getHeight()), this.getX(), this.getY() + textHeight, this.width);
        texture.initDraw().section(texture.getHeight() * 2, texture.getWidth(), 0, texture.getHeight()).color(color).draw(this.getX() + this.getSliderPixelProgress(texture), this.getY() + this.getTextHeight());
    }

    @Override
    public void drawControllerFocus(ControllerFocus current) {
        if (this.isControllerSelected) {
            Rectangle box = current.boundingBox;
            int textHeight = this.getTextHeight();
            GameTexture texture = this.getInterfaceStyle().slider.active;
            int sliderProgress = this.getSliderPixelProgress(texture);
            box = new Rectangle(box.x + sliderProgress, box.y + textHeight, texture.getWidth() - texture.getHeight() * 2 + 1, texture.getHeight() + 1);
            int padding = 5;
            box = new Rectangle(box.x - padding, box.y - padding, box.width + padding * 2, box.height + padding * 2);
            HUD.selectBoundOptions(this.getInterfaceStyle().controllerFocusBoundsHighlightColor, true, box).draw();
        } else {
            super.drawControllerFocus(current);
            GameTooltipManager.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
        }
    }

    public boolean isGrabbed() {
        return this.mouseDown || this.isControllerSelected;
    }

    public FormSlider onChanged(FormEventListener<FormInputEvent<FormSlider>> listener) {
        this.changedEvents.addListener(listener);
        return this;
    }

    public FormSlider onGrab(FormEventListener<FormGrabbedEvent<FormSlider>> listener) {
        this.grabEvents.addListener(listener);
        return this;
    }

    public FormSlider onScroll(FormEventListener<FormInputEvent<FormSlider>> listener) {
        this.scrollEvents.addListener(listener);
        return this;
    }

    public String getValueText() {
        if (!this.drawValue) {
            return "";
        }
        if (this.drawValueInPercent) {
            return (int)(this.getPercentage() * 100.0f) + "%";
        }
        return String.valueOf(this.getValue());
    }

    protected int getTextHeight() {
        if (this.drawValue || !this.text.isEmpty()) {
            return this.fontOptions.getSize() + 4;
        }
        return 0;
    }

    public int getTotalHeight() {
        return this.getTextHeight() + this.getInterfaceStyle().slider.active.getHeight();
    }

    @Override
    public List<Rectangle> getHitboxes() {
        int textHeight = this.getTextHeight();
        return FormSlider.singleBox(new Rectangle(this.getX(), this.getY(), this.width, textHeight + this.getInterfaceStyle().slider.active.getHeight()));
    }

    public int getSliderPixelProgress(GameTexture texture) {
        int selectorWidth = texture.getWidth() - texture.getHeight() * 2;
        return (int)(this.getPercentage() * (float)(this.width - selectorWidth));
    }

    public boolean isMouseOverSlider(InputEvent event) {
        GameTexture texture = this.getInterfaceStyle().slider.active;
        int selectorWidth = texture.getWidth() - texture.getHeight() * 2;
        return new Rectangle(this.getX() + this.getSliderPixelProgress(texture), this.getY() + this.getTextHeight(), selectorWidth, texture.getHeight()).contains(event.pos.hudX, event.pos.hudY);
    }

    public boolean isMouseOverBar(InputEvent event) {
        return new Rectangle(this.getX(), this.getY() + this.getTextHeight(), this.width, 13).contains(event.pos.hudX, event.pos.hudY);
    }

    public boolean isMouseOverText(InputEvent event) {
        int textHeight = this.getTextHeight();
        if (textHeight > 0) {
            return new Rectangle(this.getX(), this.getY(), this.width, textHeight).contains(event.pos.hudX, event.pos.hudY);
        }
        return false;
    }

    @Override
    public FormPosition getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(FormPosition position) {
        this.position = position;
    }
}

