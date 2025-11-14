/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.input.controller;

import java.util.List;
import java.util.function.Supplier;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Control;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerHandle;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.input.controller.ControllerInputState;
import necesse.engine.input.controller.SameControlBindDetector;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;

public class ControllerButtonState
extends ControllerInputState {
    protected Supplier<Control> controlSupplier;
    protected Control control;
    protected boolean justPressed;
    protected boolean justReleased;
    protected boolean down;

    public ControllerButtonState(String actionName, Supplier<Control> controlSupplier, GameMessage displayName) {
        super(actionName, displayName);
        this.controlSupplier = controlSupplier;
    }

    public ControllerButtonState(String actionName, Supplier<Control> controlSupplier) {
        this(actionName, controlSupplier, controlSupplier == null ? new LocalMessage("controller", actionName) : null);
    }

    @Override
    public GameMessage getDisplayName() {
        if (this.controlSupplier == null) {
            return super.getDisplayName();
        }
        return this.controlSupplier.get().text;
    }

    @Override
    public void init() {
        super.init();
        if (this.controlSupplier != null) {
            this.control = this.controlSupplier.get();
            if (this.control != null) {
                this.control.controllerState = this;
            }
        }
    }

    public boolean isJustPressed() {
        return this.justPressed;
    }

    public boolean isDown() {
        return this.down;
    }

    public boolean isJustReleased() {
        return this.justReleased;
    }

    public void updateState(boolean isDown, ControllerHandle controllerHandle, Object inputUsedToActivate, List<ControllerEvent> events, TickManager tickManager) {
        this.justPressed = false;
        this.justReleased = false;
        if (this.down && !isDown) {
            this.justReleased = true;
            ControllerInput.repeatEvents.remove(this.getID());
            ControllerEvent event = ControllerEvent.buttonEvent(controllerHandle, this, false);
            events.add(event);
            if (this.control != null) {
                this.control.activate(InputEvent.ControllerButtonEvent(event, tickManager));
            }
        } else if (!this.down && isDown && !SameControlBindDetector.wasInputJustUsedWithDifferentAction(this, inputUsedToActivate)) {
            this.justPressed = true;
            ControllerEvent event = ControllerEvent.buttonEvent(controllerHandle, this, true);
            events.add(event);
            if (this.control != null) {
                this.control.activate(InputEvent.ControllerButtonEvent(event, tickManager));
            }
        }
        this.down = isDown;
    }
}

