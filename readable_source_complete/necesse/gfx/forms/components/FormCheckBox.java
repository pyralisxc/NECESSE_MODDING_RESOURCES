/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import necesse.engine.MouseDraggingElement;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Input;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.Renderer;
import necesse.gfx.forms.FormClickHandler;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.events.FormDraggingEvent;
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
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonIcon;
import necesse.gfx.ui.ButtonState;
import necesse.gfx.ui.HUD;
import necesse.gfx.ui.HoverStateTextures;

public class FormCheckBox
extends FormComponent
implements FormPositionContainer {
    private boolean active;
    private boolean isHovering;
    private FormPosition position;
    private String text;
    private ArrayList<String> lines;
    private FontOptions fontOptions;
    public boolean checked;
    private int maxWidth;
    private int width;
    private FormClickHandler clickHandler;
    public boolean useHoverMoveEvents;
    public boolean acceptMouseRepeatEvents;
    public boolean acceptRightClicks;
    public boolean handleClicksIfNoEventHandlers;
    public boolean submitControllerPressEvent;
    public double draggingStartDistance;
    private boolean useButtonTexture;
    private ButtonColor buttonColor;
    private ButtonIcon buttonCheckedIcon;
    private ButtonIcon buttonUncheckedIcon;
    protected FormEventsHandler<FormInputEvent<FormCheckBox>> clickedEvents;
    protected FormEventsHandler<FormInputEvent<FormCheckBox>> changedHoverEvents;
    protected FormEventsHandler<FormDraggingEvent<FormCheckBox>> dragStartedEvents;
    protected InputEvent dragStartDownEvent;

    public FormCheckBox(String text, int x, int y) {
        this(text, x, y, false);
    }

    public FormCheckBox(String text, int x, int y, boolean checked) {
        this(text, x, y, -1, checked);
    }

    public FormCheckBox(String text, int x, int y, int maxWidth) {
        this(text, x, y, maxWidth, false);
    }

    public FormCheckBox(String text, int x, int y, int maxWidth, boolean checked) {
        this.fontOptions = new FontOptions(12).defaultColor(this.getInterfaceStyle().activeTextColor);
        this.useHoverMoveEvents = true;
        this.acceptMouseRepeatEvents = false;
        this.acceptRightClicks = false;
        this.handleClicksIfNoEventHandlers = false;
        this.submitControllerPressEvent = false;
        this.draggingStartDistance = 5.0;
        this.position = new FormFixedPosition(x, y);
        this.setText(text, maxWidth);
        this.checked = checked;
        this.clickedEvents = new FormEventsHandler();
        this.clickHandler = new FormClickHandler(e -> this.isActive() && this.isMouseOver((InputEvent)e), e -> e.getID() == -100 || e.getID() == -99 && this.acceptRightClicks, e -> {
            if (this.isDraggingThis()) {
                e.use();
                return;
            }
            boolean checkedBefore = this.checked;
            FormInputEvent<FormCheckBox> fEvent = new FormInputEvent<FormCheckBox>(this, (InputEvent)e);
            this.checked = !checkedBefore;
            this.clickedEvents.onEvent(fEvent);
            if (fEvent.hasPreventedDefault()) {
                this.checked = checkedBefore;
                e.use();
            } else if (e.shouldSubmitSound()) {
                this.playTickSound();
            }
        });
        this.changedHoverEvents = new FormEventsHandler();
        this.dragStartedEvents = new FormEventsHandler();
        this.setActive(true);
    }

    public FormCheckBox useButtonTexture(ButtonColor color, ButtonIcon buttonCheckedIcon, ButtonIcon buttonUncheckedIcon) {
        this.useButtonTexture = true;
        this.buttonColor = color;
        this.buttonCheckedIcon = buttonCheckedIcon;
        this.buttonUncheckedIcon = buttonUncheckedIcon;
        this.fontOptions = new FontOptions(16).defaultColor(this.getInterfaceStyle().activeTextColor);
        this.setText(this.text, this.maxWidth);
        return this;
    }

    public FormCheckBox useButtonTexture(ButtonColor color) {
        return this.useButtonTexture(color, this.getInterfaceStyle().button_checked_20, null);
    }

    public FormCheckBox useButtonTexture() {
        return this.useButtonTexture(ButtonColor.BASE);
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        if (event.isMouseMoveEvent()) {
            boolean nextIsMouseOver;
            double distance;
            InputEvent downEvent;
            if (this.clickHandler.isDown() && (downEvent = this.clickHandler.getDownEvent()) != null && downEvent != this.dragStartDownEvent && (distance = GameMath.diagonalMoveDistance(downEvent.pos.windowX, downEvent.pos.windowY, event.pos.windowX, event.pos.windowY)) >= this.draggingStartDistance) {
                this.dragStartDownEvent = downEvent;
                this.dragStartedEvents.onEvent(new FormDraggingEvent<FormCheckBox>(this, event, this.dragStartDownEvent));
            }
            if (this.isHovering != (nextIsMouseOver = this.isMouseOver(event))) {
                this.isHovering = nextIsMouseOver;
                FormInputEvent<FormCheckBox> fEvent = new FormInputEvent<FormCheckBox>(this, event);
                this.changedHoverEvents.onEvent(fEvent);
            }
            if (nextIsMouseOver && this.useHoverMoveEvents) {
                event.useMove();
            }
        }
        if (this.handleClicksIfNoEventHandlers || this.clickedEvents.hasListeners() || this.dragStartedEvents.hasListeners()) {
            if (this.acceptMouseRepeatEvents) {
                if (event.state && this.clickHandler.eventAccept.apply(event).booleanValue() && (event.getID() == -100 || event.isRepeatEvent((Object)this))) {
                    if (this.isDraggingThis()) {
                        event.use();
                        return;
                    }
                    event.startRepeatEvents(this);
                    boolean checkedBefore = this.checked;
                    FormInputEvent<FormCheckBox> fEvent = new FormInputEvent<FormCheckBox>(this, event);
                    this.checked = !checkedBefore;
                    this.clickedEvents.onEvent(fEvent);
                    if (fEvent.hasPreventedDefault()) {
                        this.checked = checkedBefore;
                        event.use();
                    } else if (event.shouldSubmitSound()) {
                        this.playTickSound();
                    }
                }
            } else {
                this.clickHandler.handleEvent(event);
            }
        }
    }

    @Override
    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        if (this.isActive()) {
            if (event.getState() == ControllerInput.MENU_SELECT || this.acceptRightClicks && event.getState() == ControllerInput.MENU_BACK) {
                if (this.isControllerFocus() && this.submitControllerPressEvent) {
                    if (event.buttonState) {
                        if (this.acceptMouseRepeatEvents) {
                            event.startRepeatEvents(this);
                        }
                        InputEvent inputEvent = InputEvent.ControllerButtonEvent(event, tickManager);
                        this.clickHandler.forceHandleEvent(inputEvent);
                        event.use();
                        this.clickHandler.forceHandleEvent(InputEvent.ControllerButtonEvent(ControllerEvent.buttonEvent(event.controllerHandle, event.getState(), false), tickManager));
                        event.use();
                    }
                } else if (this.isControllerFocus() && event.buttonState) {
                    if (this.acceptMouseRepeatEvents) {
                        event.startRepeatEvents(this);
                    }
                    InputEvent inputEvent = InputEvent.ControllerButtonEvent(event, tickManager);
                    this.clickHandler.forceHandleEvent(inputEvent);
                    event.use();
                } else if (!event.buttonState && this.clickHandler.isDown()) {
                    this.clickHandler.forceHandleEvent(InputEvent.ControllerButtonEvent(event, tickManager));
                    event.use();
                }
            } else if (this.acceptMouseRepeatEvents && event.isRepeatEvent(this) && (this.clickHandler.isDown() || this.submitControllerPressEvent)) {
                this.clickHandler.forceClick(InputEvent.ControllerButtonEvent(event, tickManager));
                event.use();
            }
        }
    }

    @Override
    public void onControllerUnfocused(ControllerFocus current) {
        this.clickHandler.reset();
    }

    @Override
    public void addNextControllerFocus(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
        ControllerFocus.add(list, area, this, this.getBoundingBox(), currentXOffset, currentYOffset, this.controllerInitialFocusPriority, customNavigationHandler);
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        GameTooltips tooltip;
        Color drawCol = this.getDrawColor();
        if (this.useButtonTexture) {
            GameTexture contentTexture;
            ButtonIcon checkedButton;
            FormInputSize size = FormInputSize.SIZE_20;
            int drawY = this.lines.size() * this.fontOptions.getSize() / 2 - 10;
            ButtonState state = !this.isActive() ? ButtonState.INACTIVE : (this.isHovering() ? ButtonState.HIGHLIGHTED : ButtonState.ACTIVE);
            size.getButtonDrawOptions(this.getInterfaceStyle(), this.buttonColor, ButtonState.ACTIVE, this.getX(), this.getY() + drawY, 20, drawCol).draw();
            ButtonIcon buttonIcon = checkedButton = this.checked ? this.buttonCheckedIcon : this.buttonUncheckedIcon;
            if (checkedButton != null && (contentTexture = checkedButton.texture) != null) {
                contentTexture.initDraw().color((Color)checkedButton.colorGetter.apply(state)).draw(this.getX() + 10 - contentTexture.getWidth() / 2, this.getY() + drawY + size.textureDrawOffset + 10 - contentTexture.getHeight() / 2);
            }
            size.getButtonEdgeDrawOptions(this.getInterfaceStyle(), this.buttonColor, ButtonState.ACTIVE, this.getX(), this.getY() + drawY, 20, drawCol).draw();
        } else {
            HoverStateTextures textures = this.checked ? this.getInterfaceStyle().checkbox_checked : this.getInterfaceStyle().checkbox;
            GameTexture texture = this.isHovering() ? textures.highlighted : textures.active;
            int drawY = this.lines.size() * this.fontOptions.getSize() / 2 - texture.getHeight() / 2;
            texture.initDraw().color(drawCol).draw(this.getX(), this.getY() + drawY);
        }
        for (int i = 0; i < this.lines.size(); ++i) {
            FontManager.bit.drawString(this.getX() + (this.useButtonTexture ? 24 : 18), this.getY() + this.fontOptions.getSize() * i, this.lines.get(i), this.fontOptions);
        }
        if (this.isHovering() && (tooltip = this.getTooltip()) != null) {
            GameTooltipManager.addTooltip(tooltip, TooltipLocation.FORM_FOCUS);
        }
    }

    @Override
    public void drawControllerFocus(ControllerFocus current) {
        Rectangle box = current.boundingBox;
        if (this.useButtonTexture) {
            box = new Rectangle(box.x, box.y + this.lines.size() * this.fontOptions.getSize() / 2 - 10 + 1, 20, 20);
        } else {
            HoverStateTextures textures = this.checked ? this.getInterfaceStyle().checkbox_checked : this.getInterfaceStyle().checkbox;
            GameTexture texture = this.isHovering() ? textures.highlighted : textures.active;
            box = new Rectangle(box.x, box.y + this.lines.size() * this.fontOptions.getSize() / 2 - texture.getHeight() / 2 + 1, texture.getWidth(), texture.getHeight());
        }
        int padding = 5;
        box = new Rectangle(box.x - padding, box.y - padding, box.width + padding * 2, box.height + padding * 2);
        HUD.selectBoundOptions(this.getInterfaceStyle().controllerFocusBoundsColor, true, box).draw();
        GameTooltipManager.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
    }

    public GameTooltips getTooltip() {
        return null;
    }

    public boolean isHovering() {
        return this.isHovering || this.isControllerFocus();
    }

    public Color getDrawColor() {
        if (!this.isActive()) {
            return this.getInterfaceStyle().inactiveElementColor;
        }
        if (this.isHovering()) {
            return this.getInterfaceStyle().highlightElementColor;
        }
        return this.getInterfaceStyle().activeElementColor;
    }

    @Override
    public List<Rectangle> getHitboxes() {
        return FormCheckBox.singleBox(new Rectangle(this.getX(), this.getY() - 1, (this.useButtonTexture ? 24 : 18) + this.width, this.lines.size() * this.fontOptions.getSize() + 2));
    }

    public FormCheckBox onClicked(FormEventListener<FormInputEvent<FormCheckBox>> listener) {
        this.clickedEvents.addListener(listener);
        return this;
    }

    public FormCheckBox onChangedHover(FormEventListener<FormInputEvent<FormCheckBox>> listener) {
        this.changedHoverEvents.addListener(listener);
        return this;
    }

    public FormCheckBox onDragStarted(FormEventListener<FormDraggingEvent<FormCheckBox>> listener) {
        this.dragStartedEvents.addListener(listener);
        return this;
    }

    public void setupDragToOtherCheckboxes(Object sameCheckboxObject, boolean submitOnToggleEvent) {
        this.onDragStarted(e -> {
            final int id = e.draggingStartedEvent.isUsed() ? e.draggingStartedEvent.getLastID() : e.draggingStartedEvent.getID();
            Renderer.setMouseDraggingElement(new FormCheckboxDraggingElement(this, sameCheckboxObject){

                @Override
                public boolean isKeyDown(Input input) {
                    return input.isKeyDown(id);
                }
            });
            boolean bl = this.checked = !this.checked;
            if (submitOnToggleEvent) {
                this.clickedEvents.onEvent(new FormInputEvent<FormCheckBox>(this, e.event));
                this.playTickSound();
            }
        });
        this.onChangedHover(e -> {
            MouseDraggingElement draggingElement;
            if (this.isHovering() && this.isActive() && (draggingElement = Renderer.getMouseDraggingElement()) instanceof FormCheckboxDraggingElement) {
                FormCheckboxDraggingElement thisElement = (FormCheckboxDraggingElement)draggingElement;
                if (thisElement.component != this && this.checked != thisElement.component.checked && Objects.equals(thisElement.sameObject, sameCheckboxObject)) {
                    this.checked = thisElement.component.checked;
                    if (submitOnToggleEvent) {
                        this.clickedEvents.onEvent(new FormInputEvent<FormCheckBox>(this, e.event));
                    }
                    this.playTickSound();
                }
            }
        });
    }

    public void setupDragToOtherCheckboxes(Object sameCheckboxObject) {
        this.setupDragToOtherCheckboxes(sameCheckboxObject, true);
    }

    protected boolean isDraggingThis() {
        MouseDraggingElement draggingElement = Renderer.getMouseDraggingElement();
        if (draggingElement instanceof FormCheckboxDraggingElement) {
            FormCheckboxDraggingElement thisElement = (FormCheckboxDraggingElement)draggingElement;
            return thisElement.component == this;
        }
        return false;
    }

    public void setText(String text, int maxWidth) {
        this.text = text;
        this.maxWidth = maxWidth;
        this.width = 0;
        this.lines = GameUtils.breakString(text, this.fontOptions, maxWidth > 0 ? Math.max(1, maxWidth - (this.useButtonTexture ? 24 : 18)) : Integer.MAX_VALUE);
        for (String str : this.lines) {
            int width = FontManager.bit.getWidthCeil(str, this.fontOptions);
            if (this.width >= width) continue;
            this.width = width;
        }
    }

    public void setText(String text) {
        this.setText(text, -1);
    }

    public String getText() {
        StringBuilder out = new StringBuilder();
        for (String str : this.lines) {
            out.append(str).append("\n");
        }
        return out.toString().trim();
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

    public void drawDraggingElement(int mouseX, int mouseY) {
    }

    protected static class FormCheckboxDraggingElement
    implements MouseDraggingElement {
        public final FormCheckBox component;
        public final Object sameObject;

        public FormCheckboxDraggingElement(FormCheckBox component, Object sameObject) {
            this.component = component;
            this.sameObject = sameObject;
        }

        @Override
        public boolean draw(int mouseX, int mouseY) {
            this.component.drawDraggingElement(mouseX, mouseY);
            return true;
        }
    }
}

