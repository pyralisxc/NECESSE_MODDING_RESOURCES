/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.input.controller;

import java.util.ArrayList;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.controller.ControllerActionSet;
import necesse.engine.input.controller.ControllerAnalogState;
import necesse.engine.input.controller.ControllerButtonState;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.input.controller.ControllerState;
import necesse.engine.localization.message.GameMessage;

public class ControllerActionSetLayer
extends ControllerActionSet {
    public ControllerActionSetLayer(String name, GameMessage displayName) {
        super(name, displayName);
    }

    public void resetStates(ArrayList<ControllerEvent> outEvents, TickManager tickManager) {
        for (ControllerState state : this.states) {
            if (state instanceof ControllerButtonState) {
                ((ControllerButtonState)state).updateState(false, ControllerInput.getLatestControllerHandle(), null, outEvents, tickManager);
                continue;
            }
            if (!(state instanceof ControllerAnalogState)) continue;
            ControllerAnalogState controllerAnalogState = (ControllerAnalogState)state;
            controllerAnalogState.updateState(0.0f, 0.0f, ControllerInput.getLatestControllerHandle(), outEvents);
        }
    }
}

