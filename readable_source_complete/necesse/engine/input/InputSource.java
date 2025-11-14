/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.input;

import java.util.ArrayList;
import java.util.function.Consumer;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.controller.ControllerBind;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerHandle;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.input.controller.ControllerInputState;
import necesse.engine.input.controller.ControllerState;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.window.GameWindow;
import necesse.gfx.forms.components.lists.FormControlListPopulator;
import necesse.gfx.gameTexture.GameTexture;

public abstract class InputSource {
    public abstract void initialize(GameWindow var1);

    public abstract void dispose();

    public abstract void gatherInputs(TickManager var1);

    public ControllerHandle updateControllerState(ControllerInputState controllerState, ArrayList<ControllerEvent> outEventList, TickManager tickManager) {
        return null;
    }

    public GameTexture getControllerStateGlyph(ControllerInputState state, ControllerHandle controllerHandle) {
        ControllerBind bind = this.getControllerBindForState(state, controllerHandle);
        if (bind != null) {
            return bind.getGlyph(controllerHandle);
        }
        return null;
    }

    public abstract ControllerBind getControllerBindForState(ControllerInputState var1, ControllerHandle var2);

    public abstract void saveControllerStateBind(ControllerInputState var1, SaveData var2);

    public abstract void loadControllerStateBind(ControllerInputState var1, LoadData var2);

    public abstract ArrayList<ControlSettings> getControlSettings();

    public abstract ControllerBind getDefaultControllerBind(ControllerInputState var1, ControllerHandle var2);

    public void onActionSetsChanged() {
    }

    public void restoreAllControllerBinds(ControllerHandle controllerHandle) {
        ControllerInput.updateControllerOverrides();
    }

    public void restoreControllerBind(ControllerState state, ControllerHandle controllerHandle) {
        ControllerInput.updateControllerOverrides();
    }

    public void clearControllerBind(ControllerInputState state, ControllerHandle controllerHandle) {
        ControllerInput.updateControllerOverrides();
    }

    public void setStateBind(ControllerInputState state, ControllerBind bind, ControllerHandle controllerHandle) {
        ControllerInput.updateControllerOverrides();
    }

    public abstract void onNextAnalogInput(Consumer<ControllerBind> var1);

    public abstract void onNextButtonInput(Consumer<ControllerBind> var1);

    public class ControlSettings {
        public final GameMessage gameMessage;
        public final Runnable runWhenSettingsClicked;
        public final FormControlListPopulator controlList;

        public ControlSettings(GameMessage gameMessage, Runnable settingsClicked, FormControlListPopulator controlList) {
            this.gameMessage = gameMessage;
            this.runWhenSettingsClicked = settingsClicked;
            this.controlList = controlList;
        }
    }
}

