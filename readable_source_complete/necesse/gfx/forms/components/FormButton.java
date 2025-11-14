/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import necesse.engine.MouseDraggingElement;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Input;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.Renderer;
import necesse.gfx.forms.FormClickHandler;
import necesse.gfx.forms.components.FormButtonToggle;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.events.FormDraggingEvent;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormEventsHandler;
import necesse.gfx.forms.events.FormInputEvent;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.ui.ButtonState;

public abstract class FormButton
extends FormComponent {
    private long onCooldown;
    private int cooldownTime;
    private FormClickHandler clickHandler;
    private boolean active;
    public boolean useHoverMoveEvents = true;
    private boolean isHovering;
    public boolean acceptMouseRepeatEvents = false;
    public boolean acceptRightClicks = false;
    public boolean handleClicksIfNoEventHandlers = false;
    public boolean submitControllerPressEvent = false;
    public double draggingStartDistance = 5.0;
    protected FormEventsHandler<FormInputEvent<FormButton>> clickedEvents = new FormEventsHandler();
    protected FormEventsHandler<FormInputEvent<FormButton>> changedHoverEvents;
    protected FormEventsHandler<FormDraggingEvent<FormButton>> dragStartedEvents;
    protected InputEvent dragStartDownEvent;

    public FormButton() {
        this.clickHandler = new FormClickHandler(e -> this.acceptsEvents() && !this.isOnCooldown() && this.isMouseOver((InputEvent)e), e -> e.getID() == -100 || e.getID() == -99 && this.acceptRightClicks, e -> {
            if (this.isPressDraggingThis()) {
                e.use();
                return;
            }
            FormInputEvent<FormButton> fEvent = new FormInputEvent<FormButton>(this, (InputEvent)e);
            this.clickedEvents.onEvent(fEvent);
            if (!fEvent.hasPreventedDefault()) {
                this.pressed((InputEvent)e);
                if (this.cooldownTime > 0) {
                    this.startCooldown();
                }
                e.use();
            }
        });
        this.changedHoverEvents = new FormEventsHandler();
        this.dragStartedEvents = new FormEventsHandler();
        this.setActive(true);
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        if (event.isMouseMoveEvent()) {
            boolean nextIsMouseOver;
            double distance;
            InputEvent downEvent;
            if (this.clickHandler.isDown() && (downEvent = this.clickHandler.getDownEvent()) != null && downEvent != this.dragStartDownEvent && (distance = GameMath.diagonalMoveDistance(downEvent.pos.windowX, downEvent.pos.windowY, event.pos.windowX, event.pos.windowY)) >= this.draggingStartDistance) {
                this.dragStartDownEvent = downEvent;
                this.dragStartedEvents.onEvent(new FormDraggingEvent<FormButton>(this, event, this.dragStartDownEvent));
            }
            if (this.isHovering != (nextIsMouseOver = this.isMouseOver(event))) {
                this.isHovering = nextIsMouseOver;
                FormInputEvent<FormButton> fEvent = new FormInputEvent<FormButton>(this, event);
                this.changedHoverEvents.onEvent(fEvent);
            }
            if (nextIsMouseOver && this.useHoverMoveEvents) {
                event.useMove();
            }
        }
        if (this.handleClicksIfNoEventHandlers || this.clickedEvents.hasListeners() || this.dragStartedEvents.hasListeners()) {
            if (this.acceptMouseRepeatEvents) {
                if (event.state && this.clickHandler.eventAccept.apply(event).booleanValue() && (event.getID() == -100 || event.isRepeatEvent((Object)this))) {
                    if (this.isPressDraggingThis()) {
                        event.use();
                        return;
                    }
                    event.startRepeatEvents(this);
                    FormInputEvent<FormButton> fEvent = new FormInputEvent<FormButton>(this, event);
                    this.clickedEvents.onEvent(fEvent);
                    if (!fEvent.hasPreventedDefault()) {
                        this.pressed(event);
                        if (this.cooldownTime > 0) {
                            this.startCooldown();
                        }
                        event.use();
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
        if (this.handleClicksIfNoEventHandlers || this.clickedEvents.hasListeners() || this.dragStartedEvents.hasListeners()) {
            ControllerFocus.add(list, area, this, this.getBoundingBox(), currentXOffset, currentYOffset, this.controllerInitialFocusPriority, customNavigationHandler);
        }
    }

    @Override
    public void drawControllerFocus(ControllerFocus current) {
        super.drawControllerFocus(current);
        GameTooltipManager.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
    }

    public FormButton onClicked(FormEventListener<FormInputEvent<FormButton>> listener) {
        this.clickedEvents.addListener(listener);
        return this;
    }

    public FormButton onChangedHover(FormEventListener<FormInputEvent<FormButton>> listener) {
        this.changedHoverEvents.addListener(listener);
        return this;
    }

    public FormButton onDragStarted(FormEventListener<FormDraggingEvent<FormButton>> listener) {
        this.dragStartedEvents.addListener(listener);
        return this;
    }

    public void setupDragPressOtherButtons(Object sameButtonObject, Consumer<FormInputEvent<FormButton>> onStartedDragging, Predicate<FormButton> isValid, Consumer<FormInputEvent<FormButton>> onPressed) {
        this.onDragStarted(e -> {
            final int id = e.draggingStartedEvent.isUsed() ? e.draggingStartedEvent.getLastID() : e.draggingStartedEvent.getID();
            FormButtonDraggingElement thisElement = new FormButtonDraggingElement(this, sameButtonObject){

                @Override
                public boolean isKeyDown(Input input) {
                    return input.isKeyDown(id);
                }
            };
            thisElement.pressedButtons.add(this);
            Renderer.setMouseDraggingElement(thisElement);
            FormInputEvent<FormButton> fEvent = new FormInputEvent<FormButton>(this, e.draggingStartedEvent);
            this.clickedEvents.onEvent(fEvent);
            if (!fEvent.hasPreventedDefault()) {
                this.pressed(e.event);
                if (this.cooldownTime > 0) {
                    this.startCooldown();
                }
                e.event.use();
            }
            if (onStartedDragging != null) {
                onStartedDragging.accept(fEvent);
            }
        });
        this.onChangedHover(e -> {
            MouseDraggingElement draggingElement;
            if (this.isHovering() && this.isActive() && !this.isOnCooldown() && (draggingElement = Renderer.getMouseDraggingElement()) instanceof FormButtonDraggingElement) {
                FormButtonDraggingElement thisElement = (FormButtonDraggingElement)draggingElement;
                if (thisElement.component != this && !thisElement.pressedButtons.contains(this) && (isValid == null || isValid.test(thisElement.component)) && Objects.equals(thisElement.sameObject, sameButtonObject)) {
                    thisElement.pressedButtons.add(this);
                    if (onPressed != null) {
                        onPressed.accept(new FormInputEvent<FormButton>(this, e.event));
                    } else {
                        FormInputEvent<FormButton> fEvent = new FormInputEvent<FormButton>(this, e.event);
                        this.clickedEvents.onEvent(fEvent);
                        if (!fEvent.hasPreventedDefault()) {
                            this.pressed(e.event);
                            if (this.cooldownTime > 0) {
                                this.startCooldown();
                            }
                            e.event.use();
                        }
                    }
                }
            }
        });
    }

    public void setupDragPressOtherButtons(Object sameButtonObject) {
        this.setupDragPressOtherButtons(sameButtonObject, null, null, null);
    }

    protected boolean isPressDraggingThis() {
        MouseDraggingElement draggingElement = Renderer.getMouseDraggingElement();
        if (draggingElement instanceof FormButtonToggle.FormButtonToggleDraggingElement) {
            FormButtonToggle.FormButtonToggleDraggingElement thisElement = (FormButtonToggle.FormButtonToggleDraggingElement)draggingElement;
            return thisElement.component == this;
        }
        return false;
    }

    protected void pressed(InputEvent event) {
        if (event.shouldSubmitSound()) {
            this.playTickSound();
        }
    }

    public void setCooldown(int milliSeconds) {
        this.cooldownTime = milliSeconds;
    }

    public void startCooldown(int milliSeconds) {
        this.onCooldown = System.currentTimeMillis() + (long)milliSeconds;
    }

    public void startCooldown() {
        this.onCooldown = System.currentTimeMillis() + (long)this.cooldownTime;
    }

    public void stopCooldown() {
        this.onCooldown = 0L;
    }

    public boolean isOnCooldown() {
        return this.onCooldown > System.currentTimeMillis();
    }

    protected boolean acceptsEvents() {
        return this.isActive();
    }

    public boolean isHovering() {
        return this.isHovering || this.isControllerFocus();
    }

    protected boolean isDown() {
        return this.clickHandler.isDown();
    }

    public Color getDrawColor() {
        return this.getButtonState().elementColorGetter.apply(this.getInterfaceStyle());
    }

    public ButtonState getButtonState() {
        if (!this.isActive() || this.isOnCooldown()) {
            return ButtonState.INACTIVE;
        }
        if (this.isHovering()) {
            return ButtonState.HIGHLIGHTED;
        }
        return ButtonState.ACTIVE;
    }

    public Color getTextColor() {
        return this.getButtonState().textColorGetter.apply(this.getInterfaceStyle());
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean shouldUseMouseEvents() {
        return this.useHoverMoveEvents;
    }

    public void drawDraggingElement(int mouseX, int mouseY) {
    }

    protected static class FormButtonDraggingElement
    implements MouseDraggingElement {
        public final FormButton component;
        public final Object sameObject;
        public final HashSet<FormButton> pressedButtons = new HashSet();

        public FormButtonDraggingElement(FormButton component, Object sameObject) {
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

