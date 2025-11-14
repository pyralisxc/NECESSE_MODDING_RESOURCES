/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.input.controller;

import java.util.HashSet;
import java.util.List;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerHandle;
import necesse.engine.input.controller.ControllerState;

public class ControllerRefreshFocusState
extends ControllerState {
    private boolean submitNextEvent;

    public void updateState(HashSet<ControllerHandle> controllerHandles, List<ControllerEvent> events) {
        for (ControllerHandle controllerHandle : controllerHandles) {
            if (!this.submitNextEvent) continue;
            events.add(ControllerEvent.customEvent(controllerHandle, this));
        }
        this.submitNextEvent = false;
    }

    public void submitNextEvent() {
        this.submitNextEvent = true;
    }
}

