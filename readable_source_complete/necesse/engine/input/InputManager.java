/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.input;

import java.util.ArrayList;
import necesse.engine.GlobalData;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Control;
import necesse.engine.input.Input;
import necesse.engine.input.InputSource;
import necesse.engine.input.controller.ControllerHandle;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.gfx.forms.FormManager;
import necesse.gfx.forms.components.FormTypingComponent;

public abstract class InputManager {
    protected static ArrayList<InputSource> inputSources = new ArrayList();

    public abstract void initialize();

    public abstract void initializeInputSources(GameWindow var1);

    public void tick(TickManager tickManager) {
        for (InputSource inputSource : inputSources) {
            inputSource.gatherInputs(tickManager);
        }
        FormManager formManager = GlobalData.getCurrentState().getFormManager();
        boolean isTyping = formManager != null && (FormTypingComponent.isCurrentlyTyping() || formManager.isControllerKeyboardOpen());
        Input input = WindowManager.getWindow().getInput();
        input.tick(isTyping, tickManager);
        Control.tickControlInputs(input, isTyping, tickManager);
    }

    public abstract void dispose();

    public abstract boolean showControllerPanel(ControllerHandle var1);

    public abstract void triggerVibration(ControllerHandle var1, float var2, float var3);

    public static ArrayList<InputSource> getInputSources() {
        return inputSources;
    }

    public abstract void setCursorPosition(int var1, int var2);

    public abstract String getKeyName(int var1);
}

