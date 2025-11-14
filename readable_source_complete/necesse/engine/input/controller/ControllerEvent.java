/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.input.controller;

import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Function;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerHandle;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.input.controller.ControllerState;

public class ControllerEvent {
    public final ControllerHandle controllerHandle;
    private ControllerState state;
    private ControllerState usedState;
    private ControllerState repeatState;
    public final boolean isButton;
    public final boolean isAnalog;
    public final boolean buttonState;
    public final float analogX;
    public final float analogY;
    private Object[] repeatCallers;
    private int repeatCounter;
    private int repeatDelay;
    public LinkedList<InputEvent> inputEvents = new LinkedList();

    private ControllerEvent(ControllerHandle controllerHandle, ControllerState state, boolean isButton, boolean isAnalog, boolean buttonState, float analogX, float analogY) {
        this.controllerHandle = controllerHandle;
        this.state = state;
        this.isButton = isButton;
        this.isAnalog = isAnalog;
        this.buttonState = buttonState;
        this.analogX = analogX;
        this.analogY = analogY;
    }

    public static ControllerEvent customEvent(ControllerHandle controllerHandle, ControllerState state) {
        return new ControllerEvent(controllerHandle, state, false, false, false, 0.0f, 0.0f);
    }

    public static ControllerEvent buttonEvent(ControllerHandle controllerHandle, ControllerState state, boolean buttonState) {
        return new ControllerEvent(controllerHandle, state, true, false, buttonState, 0.0f, 0.0f);
    }

    public static ControllerEvent repeatEvent(ControllerHandle controllerHandle, Object[] callers, int repeatCounter, int repeatDelay, ControllerState repeatState) {
        ControllerEvent event = new ControllerEvent(controllerHandle, ControllerInput.REPEAT_EVENT, true, false, true, 0.0f, 0.0f);
        event.repeatCallers = callers;
        event.repeatCounter = repeatCounter;
        event.repeatDelay = repeatDelay;
        event.repeatState = repeatState;
        return event;
    }

    public static ControllerEvent analogEvent(ControllerHandle controllerHandle, ControllerState state, float analogX, float analogY) {
        return new ControllerEvent(controllerHandle, state, false, true, false, analogX, analogY);
    }

    public boolean isRepeatEvent(Object caller) {
        return this.isRepeatEvent((Object o) -> Objects.equals(o, caller));
    }

    public boolean isRepeatEvent(int index, Object caller) {
        return this.isRepeatEvent(index, (Object o) -> Objects.equals(o, caller));
    }

    public boolean isRepeatEvent(Function<Object, Boolean> validCaller) {
        return this.isRepeatEvent(0, validCaller);
    }

    public boolean isRepeatEvent(int index, Function<Object, Boolean> validCaller) {
        if (this.state == ControllerInput.REPEAT_EVENT) {
            if (this.repeatCallers != null && index < this.repeatCallers.length) {
                return validCaller.apply(this.repeatCallers[index]);
            }
            return validCaller.apply(null);
        }
        return false;
    }

    public ControllerState getState() {
        return this.state;
    }

    public ControllerState getUsedState() {
        return this.usedState;
    }

    public ControllerState getRepeatState() {
        return this.repeatState;
    }

    public void startRepeatEvents(Object ... callers) {
        ControllerInput.startRepeatEvents(this, callers);
    }

    public boolean shouldSubmitSound() {
        if (this.state != ControllerInput.REPEAT_EVENT) {
            return true;
        }
        int maxMS = 11;
        if (this.repeatDelay >= maxMS) {
            return true;
        }
        return this.repeatCounter % (maxMS / this.repeatDelay) == 0;
    }

    public void use() {
        if (!this.isUsed()) {
            this.inputEvents.forEach(InputEvent::use);
            this.inputEvents.clear();
            this.usedState = this.state;
            this.state = null;
        }
    }

    public boolean isUsed() {
        return this.state == null;
    }
}

