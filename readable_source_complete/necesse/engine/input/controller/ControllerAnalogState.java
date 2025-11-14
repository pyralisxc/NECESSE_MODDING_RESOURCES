/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.input.controller;

import java.util.List;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerHandle;
import necesse.engine.input.controller.ControllerInputState;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;

public class ControllerAnalogState
extends ControllerInputState {
    public final boolean absoluteMouse;
    protected float x;
    protected float y;
    protected boolean changed;

    public ControllerAnalogState(String actionName, boolean absoluteMouse, GameMessage displayName) {
        super(actionName, displayName);
        this.absoluteMouse = absoluteMouse;
    }

    public ControllerAnalogState(String actionName, boolean absoluteMouse) {
        this(actionName, absoluteMouse, new LocalMessage("controller", actionName));
    }

    public void updateState(float x, float y, ControllerHandle controllerHandle, List<ControllerEvent> events) {
        this.changed = false;
        float oldX = this.x;
        float oldY = this.y;
        this.x = x;
        this.y = y;
        if (x != oldX || y != oldY) {
            this.changed = true;
            events.add(ControllerEvent.analogEvent(controllerHandle, this, x, y));
        }
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public boolean hasChanged() {
        return this.changed;
    }
}

