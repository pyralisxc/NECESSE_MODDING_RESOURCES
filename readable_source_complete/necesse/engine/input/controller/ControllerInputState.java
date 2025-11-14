/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.input.controller;

import necesse.engine.input.controller.ControllerActionSet;
import necesse.engine.input.controller.ControllerState;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;

public abstract class ControllerInputState
extends ControllerState {
    public final String actionName;
    private final GameMessage displayName;
    private ControllerActionSet actionSet;

    public ControllerInputState(String actionName, GameMessage displayName) {
        this.actionName = actionName;
        this.displayName = displayName == null ? new StaticMessage("NULL:" + actionName) : displayName;
    }

    public GameMessage getDisplayName() {
        return this.displayName;
    }

    public ControllerActionSet getActionSet() {
        return this.actionSet;
    }

    public void setActionSet(ControllerActionSet controllerActionSet) {
        if (this.actionSet != null) {
            throw new RuntimeException("A state should only have its ActionSet set once.");
        }
        this.actionSet = controllerActionSet;
    }
}

