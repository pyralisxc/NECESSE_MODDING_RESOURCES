/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.codedisaster.steamworks.SteamController
 *  org.lwjgl.glfw.GLFW
 */
package necesse.engine.platforms.steam.input;

import com.codedisaster.steamworks.SteamController;
import java.util.ArrayList;
import java.util.Arrays;
import necesse.engine.input.InputManager;
import necesse.engine.input.InputSource;
import necesse.engine.input.controller.ControllerHandle;
import necesse.engine.platforms.sharedOnPC.input.GLFWInputSource;
import necesse.engine.platforms.sharedOnPC.window.GLFWGameWindow;
import necesse.engine.platforms.steam.input.SteamControllerInputSource;
import necesse.engine.platforms.steam.input.SteamGameControllerHandle;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import org.lwjgl.glfw.GLFW;

public class SteamInputManager
extends InputManager {
    private SteamController steamController;

    @Override
    public void initialize() {
        this.steamController = new SteamController();
        this.steamController.init();
        InputManager.inputSources = new ArrayList<InputSource>(Arrays.asList(new GLFWInputSource(false), new SteamControllerInputSource(this.steamController)));
    }

    @Override
    public void initializeInputSources(GameWindow window) {
        for (InputSource inputSource : InputManager.inputSources) {
            inputSource.initialize(window);
        }
    }

    @Override
    public void dispose() {
        if (InputManager.inputSources != null) {
            for (InputSource inputSource : InputManager.inputSources) {
                inputSource.dispose();
            }
        }
        if (this.steamController != null) {
            this.steamController.shutdown();
        }
    }

    @Override
    public boolean showControllerPanel(ControllerHandle controllerHandle) {
        if (controllerHandle != null) {
            this.steamController.showBindingPanel(((SteamGameControllerHandle)controllerHandle).steamNativeControllerHandle);
            return true;
        }
        return false;
    }

    @Override
    public void triggerVibration(ControllerHandle controllerHandle, float leftIntensity, float rightIntensity) {
        short left = (short)(leftIntensity * 65535.0f);
        short right = (short)(rightIntensity * 65535.0f);
        this.steamController.triggerVibration(((SteamGameControllerHandle)controllerHandle).steamNativeControllerHandle, left, right);
    }

    @Override
    public void setCursorPosition(int mouseWindowX, int mouseWindowY) {
        GLFW.glfwSetCursorPos((long)((GLFWGameWindow)WindowManager.getWindow()).getGlfwWindow(), (double)mouseWindowX, (double)mouseWindowY);
    }

    @Override
    public String getKeyName(int key) {
        return GLFW.glfwGetKeyName((int)key, (int)0).toUpperCase();
    }
}

