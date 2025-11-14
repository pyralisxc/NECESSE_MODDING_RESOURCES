/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.codedisaster.steamworks.SteamController
 *  com.codedisaster.steamworks.SteamControllerHandle
 *  com.codedisaster.steamworks.SteamNativeHandle
 */
package necesse.engine.platforms.steam.input;

import com.codedisaster.steamworks.SteamController;
import com.codedisaster.steamworks.SteamControllerHandle;
import com.codedisaster.steamworks.SteamNativeHandle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.function.Consumer;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputSource;
import necesse.engine.input.controller.ControllerActionSetLayer;
import necesse.engine.input.controller.ControllerAnalogState;
import necesse.engine.input.controller.ControllerBind;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerHandle;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.input.controller.ControllerInputState;
import necesse.engine.input.controller.ControllerState;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.platforms.steam.input.SteamControllerBind;
import necesse.engine.platforms.steam.input.SteamGameControllerHandle;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.window.GameWindow;

public class SteamControllerInputSource
extends InputSource {
    private final SteamController steamController;
    private final SteamGameControllerHandle[] controllerHandles;
    private final SteamControllerHandle[] nativeControllerHandles;
    private final HashMap<ControllerInputState, SteamControllerBind> controllerState_controllerBind = new HashMap();
    private int currentControllerCount;
    private long nextControllerDetectionTime;
    private SteamGameControllerHandle lastControllerUsed;
    public final SteamGameControllerHandle STEAM_CONTROLLER_HANDLE_ALL_CONTROLLERS;

    public SteamControllerInputSource(SteamController steamController) {
        this.steamController = steamController;
        this.controllerHandles = new SteamGameControllerHandle[16];
        this.nativeControllerHandles = new SteamControllerHandle[16];
        this.STEAM_CONTROLLER_HANDLE_ALL_CONTROLLERS = new SteamGameControllerHandle(new SteamControllerHandle(-1L), this);
    }

    @Override
    public void initialize(GameWindow window) {
    }

    @Override
    public void dispose() {
    }

    @Override
    public void gatherInputs(TickManager tickManager) {
        this.steamController.runFrame();
        if (this.nextControllerDetectionTime < System.currentTimeMillis()) {
            int i;
            this.nextControllerDetectionTime = System.currentTimeMillis() + 1000L;
            this.steamController.getConnectedControllers(this.nativeControllerHandles);
            for (i = 0; i < this.nativeControllerHandles.length; ++i) {
                if (this.nativeControllerHandles[i] == null && this.controllerHandles[i] != null) {
                    ControllerInput.deregisterController(this.controllerHandles[i]);
                    this.controllerHandles[i] = null;
                    --this.currentControllerCount;
                } else if (this.nativeControllerHandles[i] != null && this.controllerHandles[i] == null) {
                    this.controllerHandles[i] = new SteamGameControllerHandle(this.nativeControllerHandles[i], this);
                    ControllerInput.registerController(this.controllerHandles[i]);
                    this.updateControllerActionSets(this.controllerHandles[i]);
                    if (this.currentControllerCount == 0) {
                        this.controllerState_controllerBind.clear();
                        ControllerInput.updateControllerOverrides();
                    }
                    ++this.currentControllerCount;
                } else if (this.controllerHandles[i] != null && !this.controllerHandles[i].steamNativeControllerHandle.equals((Object)this.nativeControllerHandles[i])) {
                    ControllerInput.deregisterController(this.controllerHandles[i]);
                    this.controllerHandles[i] = new SteamGameControllerHandle(this.nativeControllerHandles[i], this);
                    ControllerInput.registerController(this.controllerHandles[i]);
                    this.updateControllerActionSets(this.controllerHandles[i]);
                }
                this.nativeControllerHandles[i] = null;
            }
            for (i = 0; i < this.controllerHandles.length; ++i) {
                if (this.controllerHandles[i] == null) continue;
                this.updateControllerActionSets(this.controllerHandles[i]);
            }
        }
    }

    @Override
    public ControllerHandle updateControllerState(ControllerInputState state, ArrayList<ControllerEvent> outEventList, TickManager tickManager) {
        SteamGameControllerHandle controllerUsed = null;
        SteamControllerBind controllerBind = (SteamControllerBind)this.getControllerBindForState(state, null);
        for (SteamGameControllerHandle controllerHandle : this.controllerHandles) {
            if (controllerHandle == null || !controllerBind.updateStateIfInput(state, controllerHandle, this.steamController, outEventList, tickManager)) continue;
            controllerUsed = controllerHandle;
            break;
        }
        if (controllerUsed != null && this.lastControllerUsed != controllerUsed) {
            this.lastControllerUsed = controllerUsed;
            for (ControllerInputState controllerState : this.controllerState_controllerBind.keySet()) {
                this.controllerState_controllerBind.get(controllerState).updateLastUsedInputToNewController(controllerState, controllerUsed, this.steamController);
            }
            ControllerInput.updateControllerOverrides();
        }
        return controllerUsed;
    }

    @Override
    public ControllerBind getControllerBindForState(ControllerInputState state, ControllerHandle controllerHandle) {
        SteamControllerBind controllerBind = this.controllerState_controllerBind.get(state);
        if (controllerBind != null) {
            return controllerBind;
        }
        controllerBind = state instanceof ControllerAnalogState ? new SteamControllerBind(this.steamController.getActionSetHandle(state.getActionSet().name), (SteamNativeHandle)this.steamController.getAnalogActionHandle(state.actionName)) : new SteamControllerBind(this.steamController.getActionSetHandle(state.getActionSet().name), (SteamNativeHandle)this.steamController.getDigitalActionHandle(state.actionName));
        this.controllerState_controllerBind.put(state, controllerBind);
        return controllerBind;
    }

    @Override
    public void saveControllerStateBind(ControllerInputState state, SaveData saveData) {
    }

    @Override
    public void loadControllerStateBind(ControllerInputState state, LoadData loadData) {
    }

    @Override
    public ArrayList<InputSource.ControlSettings> getControlSettings() {
        if (this.currentControllerCount > 0) {
            return new ArrayList<InputSource.ControlSettings>(Collections.singletonList(new InputSource.ControlSettings(new LocalMessage("settingsui", "controller"), ControllerInput::showControllerPanel, null)));
        }
        return new ArrayList<InputSource.ControlSettings>();
    }

    @Override
    public ControllerBind getDefaultControllerBind(ControllerInputState state, ControllerHandle controllerHandle) {
        return this.getControllerBindForState(state, controllerHandle);
    }

    @Override
    public void onActionSetsChanged() {
        if (this.currentControllerCount == 0) {
            this.updateControllerActionSets(this.STEAM_CONTROLLER_HANDLE_ALL_CONTROLLERS);
        } else {
            for (int i = 0; i < this.currentControllerCount; ++i) {
                if (this.controllerHandles[i] == null) continue;
                SteamGameControllerHandle controllerHandle = this.controllerHandles[i];
                this.updateControllerActionSets(controllerHandle);
            }
        }
    }

    @Override
    public void restoreAllControllerBinds(ControllerHandle controllerHandle) {
    }

    @Override
    public void restoreControllerBind(ControllerState state, ControllerHandle controllerHandle) {
    }

    @Override
    public void setStateBind(ControllerInputState state, ControllerBind bind, ControllerHandle controllerHandle) {
    }

    @Override
    public void onNextAnalogInput(Consumer<ControllerBind> onInputReceived) {
    }

    @Override
    public void onNextButtonInput(Consumer<ControllerBind> onInputReceived) {
    }

    private void updateControllerActionSets(SteamGameControllerHandle controllerHandle) {
        this.steamController.activateActionSet(controllerHandle.steamNativeControllerHandle, this.steamController.getActionSetHandle(ControllerInput.getActiveActionSet().name));
        this.steamController.deactivateAllActionSetLayers(controllerHandle.steamNativeControllerHandle);
        for (ControllerActionSetLayer layer : ControllerInput.getActiveActionSetLayers()) {
            this.steamController.activateActionSetLayer(controllerHandle.steamNativeControllerHandle, this.steamController.getActionSetHandle(layer.name));
        }
    }
}

