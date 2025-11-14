/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.glfw.GLFWGamepadState
 */
package necesse.engine.platforms.sharedOnPC.input.controller;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Objects;
import necesse.engine.input.InputSource;
import necesse.engine.input.controller.ControllerHandle;
import necesse.engine.platforms.Platform;
import necesse.engine.platforms.PlatformManager;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWGamepadState;

public class GLFWControllerHandle
extends ControllerHandle {
    public final int nativeControllerHandle;
    public final String name;
    public final String guid;
    private final GLFWGamepadState gamepadState = GLFWGamepadState.create();
    private ByteBuffer buttonStates;
    private FloatBuffer axisStates;
    private ByteBuffer hatStates;

    public GLFWControllerHandle(InputSource inputSource, int nativeControllerHandle) {
        super(inputSource, GLFWControllerHandle.getControllerType(nativeControllerHandle));
        this.nativeControllerHandle = nativeControllerHandle;
        this.name = GLFW.glfwJoystickIsGamepad((int)nativeControllerHandle) ? GLFW.glfwGetGamepadName((int)nativeControllerHandle) : GLFW.glfwGetJoystickName((int)nativeControllerHandle);
        this.guid = GLFW.glfwGetJoystickGUID((int)nativeControllerHandle);
    }

    private static ControllerHandle.ControllerType getControllerType(int nativeControllerHandle) {
        String gamepadName = GLFW.glfwGetGamepadName((int)nativeControllerHandle);
        String joystickName = GLFW.glfwGetJoystickName((int)nativeControllerHandle);
        if (gamepadName != null && gamepadName.toLowerCase().contains("xinput")) {
            return ControllerHandle.ControllerType.Xbox;
        }
        if (Objects.equals(gamepadName, "PS4 Controller")) {
            return ControllerHandle.ControllerType.PlayStation4;
        }
        if (Objects.equals(gamepadName, "PS5 Controller") || Objects.equals(gamepadName, "Sony DualSense")) {
            return ControllerHandle.ControllerType.PlayStation5;
        }
        if (joystickName != null && joystickName.toLowerCase().contains("xbox")) {
            if (PlatformManager.getPlatform().getOperatingSystemFamily() == Platform.OperatingSystemFamily.Linux) {
                if (joystickName.contains("Wireless")) {
                    return ControllerHandle.ControllerType.XboxLinuxWireless;
                }
                return ControllerHandle.ControllerType.XboxLinuxWired;
            }
            return ControllerHandle.ControllerType.XboxGeneric;
        }
        return ControllerHandle.ControllerType.Unknown;
    }

    public void updateStates() {
        GLFW.glfwGetGamepadState((int)this.nativeControllerHandle, (GLFWGamepadState)this.gamepadState);
        this.buttonStates = GLFW.glfwGetJoystickButtons((int)this.nativeControllerHandle);
        this.axisStates = GLFW.glfwGetJoystickAxes((int)this.nativeControllerHandle);
        this.hatStates = GLFW.glfwGetJoystickHats((int)this.nativeControllerHandle);
    }

    public boolean isSupportedAsGamepad() {
        switch (this.type) {
            case XboxGeneric: 
            case Unknown: 
            case XboxLinuxWireless: 
            case XboxLinuxWired: {
                return false;
            }
            case Xbox: 
            case PlayStation4: 
            case PlayStation5: {
                return true;
            }
        }
        return false;
    }

    public GLFWGamepadState getGamepadState() {
        return this.gamepadState;
    }

    public ByteBuffer getButtonStates() {
        return this.buttonStates;
    }

    public FloatBuffer getAxisStates() {
        return this.axisStates;
    }

    public ByteBuffer getHatStates() {
        return this.hatStates;
    }

    public static enum controllerType {
        Unknown,
        PlayStation,
        Xbox,
        NintendoSwitch,
        SteamDeck;

    }
}

