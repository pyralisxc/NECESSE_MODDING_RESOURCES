/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.system.Callback
 *  org.lwjgl.system.MemoryStack
 */
package necesse.engine.platforms.sharedOnPC.input;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import necesse.engine.GameLog;
import necesse.engine.GlobalData;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Input;
import necesse.engine.input.InputEvent;
import necesse.engine.input.InputPosition;
import necesse.engine.input.InputSource;
import necesse.engine.input.controller.ControllerAnalogState;
import necesse.engine.input.controller.ControllerBind;
import necesse.engine.input.controller.ControllerButtonState;
import necesse.engine.input.controller.ControllerDefaultBinds;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerHandle;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.input.controller.ControllerInputState;
import necesse.engine.input.controller.ControllerState;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.platforms.sharedOnPC.forms.FormControllerControlListPopulator;
import necesse.engine.platforms.sharedOnPC.forms.FormKeyboardAndMouseControlListPopulator;
import necesse.engine.platforms.sharedOnPC.input.GLFWControllerBind;
import necesse.engine.platforms.sharedOnPC.input.controller.GLFWControllerHandle;
import necesse.engine.platforms.sharedOnPC.window.GLFWGameWindow;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.engine.util.ObjectValue;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryStack;

public class GLFWInputSource
extends InputSource {
    private final boolean provideControllerInput;
    private final GLFWControllerHandle[] controllerHandles = new GLFWControllerHandle[16];
    private final HashMap<String, HashMap<ControllerState, GLFWControllerBind>> controlBinds = new HashMap();
    private final LinkedList<Consumer<ControllerBind>> waitingForNextAnalogInput = new LinkedList();
    private final LinkedList<Consumer<ControllerBind>> waitingForNextButtonInput = new LinkedList();
    private ObjectValue<GLFWControllerHandle, GLFWControllerBind> waitingForNextButtonInputToBeReleased = null;
    private Callback keyCallback;
    private Callback charCallback;
    private Callback cursorCallback;
    private Callback mouseCallback;
    private Callback scrollCallback;
    private Callback joystickConnectionCallback;
    private int connectedControllerCount;

    public GLFWInputSource(boolean provideControllerInput) {
        this.provideControllerInput = provideControllerInput;
    }

    @Override
    public void initialize(GameWindow window) {
        this.freeCallbacks();
        long glfwWindow = ((GLFWGameWindow)WindowManager.getWindow()).getGlfwWindow();
        this.keyCallback = GLFW.glfwSetKeyCallback((long)glfwWindow, (win, key, scancode, action, mods) -> {
            if (key == -1) {
                return;
            }
            if (key < 0) {
                GameLog.warn.println("Registered invalid keyboard event with key " + key);
                return;
            }
            Input input = WindowManager.getWindow().getInput();
            if (!Input.isTyping && action == 2) {
                return;
            }
            if (Input.isTyping && !Input.isFunctionKey(key) && !input.isKeyDown(Input.getSystemControlKey())) {
                return;
            }
            InputEvent e = InputEvent.KeyboardEvent(key, action == 1 || action == 2, scancode, Input.mousePos, GlobalData.getCurrentGameLoop());
            input.submitButtonInputEvent(e);
            Input.lastKeyboardInputTime = System.currentTimeMillis();
        });
        this.charCallback = GLFW.glfwSetCharCallback((long)glfwWindow, (win, codepoint) -> {
            Input input = WindowManager.getWindow().getInput();
            if (input.isKeyDown(Input.getSystemControlKey())) {
                return;
            }
            input.submitNonButtonInputEvent(InputEvent.CharacterEvent(codepoint, new String(Character.toChars(codepoint)), input.mousePos(), GlobalData.getCurrentGameLoop()));
            Input.lastKeyboardInputTime = System.currentTimeMillis();
        });
        this.cursorCallback = GLFW.glfwSetCursorPosCallback((long)glfwWindow, (win, xPos, yPos) -> {
            Input input = WindowManager.getWindow().getInput();
            Input.mousePos = InputPosition.fromWindowPos(WindowManager.getWindow(), (int)xPos, (int)yPos);
            input.submitNonButtonInputEvent(InputEvent.MouseMoveEvent(Input.mousePos, GlobalData.getCurrentGameLoop()));
            if (Input.setCursorPos == null || GameMath.squareDistance(Input.setCursorPos.x, Input.setCursorPos.y, (int)xPos, (int)yPos) > 10.0f) {
                Input.lastCursorInputTime = System.currentTimeMillis();
            }
            Input.setCursorPos = null;
        });
        this.mouseCallback = GLFW.glfwSetMouseButtonCallback((long)glfwWindow, (win, button, action, mods) -> {
            Input input = WindowManager.getWindow().getInput();
            InputEvent e = InputEvent.MouseButtonEvent(button, action == 1, Input.mousePos, GlobalData.getCurrentGameLoop());
            input.submitButtonInputEvent(e);
            Input.lastKeyboardInputTime = System.currentTimeMillis();
        });
        this.scrollCallback = GLFW.glfwSetScrollCallback((long)glfwWindow, (win, xOffset, yOffset) -> {
            Input input = WindowManager.getWindow().getInput();
            input.submitNonButtonInputEvent(InputEvent.MouseScrollEvent(xOffset, yOffset, Input.mousePos, GlobalData.getCurrentGameLoop()));
            Input.lastKeyboardInputTime = System.currentTimeMillis();
        });
        if (this.provideControllerInput) {
            this.joystickConnectionCallback = GLFW.glfwSetJoystickCallback((id, event) -> {
                switch (event) {
                    case 262145: {
                        this.controllerConnected(id);
                        break;
                    }
                    case 262146: {
                        this.controllerDisconnected(id);
                        break;
                    }
                    default: {
                        throw new RuntimeException("Unexpected controller event: " + event);
                    }
                }
            });
            for (int i = this.connectedControllerCount; i <= 15; ++i) {
                if (!GLFW.glfwJoystickPresent((int)i)) continue;
                this.controllerConnected(i);
            }
        }
    }

    private void freeCallbacks() {
        if (this.keyCallback != null) {
            this.keyCallback.free();
        }
        this.keyCallback = null;
        if (this.charCallback != null) {
            this.charCallback.free();
        }
        this.charCallback = null;
        if (this.cursorCallback != null) {
            this.cursorCallback.free();
        }
        this.cursorCallback = null;
        if (this.mouseCallback != null) {
            this.mouseCallback.free();
        }
        this.mouseCallback = null;
        if (this.scrollCallback != null) {
            this.scrollCallback.free();
        }
        this.scrollCallback = null;
        if (this.joystickConnectionCallback != null) {
            this.joystickConnectionCallback.free();
        }
        this.joystickConnectionCallback = null;
    }

    private void controllerConnected(int id) {
        GLFWControllerHandle controllerHandle;
        if (this.isIgnoredController(id)) {
            return;
        }
        this.controllerHandles[id] = controllerHandle = new GLFWControllerHandle((InputSource)this, id);
        ++this.connectedControllerCount;
        this.controlBinds.computeIfAbsent(controllerHandle.guid, value -> new HashMap());
        ControllerInput.registerController(controllerHandle);
    }

    private void controllerDisconnected(int id) {
        if (Arrays.stream(this.controllerHandles).noneMatch(x -> x != null && x.nativeControllerHandle == id)) {
            return;
        }
        --this.connectedControllerCount;
        ControllerInput.deregisterController(this.controllerHandles[id]);
        this.controllerHandles[id] = null;
    }

    private boolean isIgnoredController(int id) {
        String joystickName = GLFW.glfwGetJoystickName((int)id);
        String gamepadName = GLFW.glfwGetGamepadName((int)id);
        return Objects.equals(gamepadName, "Steam Virtual Gamepad") || Objects.equals(gamepadName, "Nintendo Switch Pro Controller") || Objects.equals(joystickName, "Pro Controller") || joystickName != null && joystickName.toLowerCase().contains("steam");
    }

    @Override
    public void dispose() {
        this.freeCallbacks();
    }

    @Override
    public void gatherInputs(TickManager tickManager) {
        GLFW.glfwPollEvents();
        GameWindow window = WindowManager.getWindow();
        long glfwWindow = ((GLFWGameWindow)WindowManager.getWindow()).getGlfwWindow();
        if (Input.updateNextMousePos) {
            try (MemoryStack stack = MemoryStack.stackPush();){
                DoubleBuffer x = stack.mallocDouble(1);
                DoubleBuffer y = stack.mallocDouble(1);
                GLFW.glfwGetCursorPos((long)glfwWindow, (DoubleBuffer)x, (DoubleBuffer)y);
                int windowX = (int)x.get();
                int windowY = (int)y.get();
                Input.mousePos = InputPosition.fromWindowPos(window, windowX, windowY);
            }
            Input.updateNextMousePos = false;
        }
        if (this.provideControllerInput) {
            for (GLFWControllerHandle controllerHandle : this.controllerHandles) {
                if (controllerHandle == null) continue;
                controllerHandle.updateStates();
                this.provideInputBindsForWaitingConsumers(controllerHandle);
            }
        }
    }

    private void provideInputBindsForWaitingConsumers(GLFWControllerHandle controllerHandle) {
        int i;
        FloatBuffer axis;
        int i2;
        GLFWControllerBind newBind;
        if (!this.waitingForNextAnalogInput.isEmpty()) {
            newBind = null;
            if (controllerHandle.getGamepadState() != null && controllerHandle.isSupportedAsGamepad()) {
                FloatBuffer gamepadAxis = controllerHandle.getGamepadState().axes();
                for (i2 = 0; i2 < gamepadAxis.capacity(); ++i2) {
                    if (!((double)Math.abs(gamepadAxis.get(i2)) >= 0.1)) continue;
                    if (i2 == 0 || i2 == 1) {
                        newBind = new GLFWControllerBind(GLFWControllerBind.BindType.GamepadAxis, new int[]{106});
                        continue;
                    }
                    if (i2 != 2 && i2 != 3) continue;
                    newBind = new GLFWControllerBind(GLFWControllerBind.BindType.GamepadAxis, new int[]{107});
                }
            }
            axis = controllerHandle.getAxisStates();
            if (newBind == null && axis != null) {
                int lastAxis = -1;
                for (i = 0; i < axis.capacity(); ++i) {
                    if (!((double)Math.abs(axis.get(i)) > 0.25) || !((double)Math.abs(axis.get(i)) < 0.9)) continue;
                    if (lastAxis >= 0) {
                        newBind = new GLFWControllerBind(GLFWControllerBind.BindType.Axis, new int[]{lastAxis, i});
                        continue;
                    }
                    lastAxis = i;
                }
            }
            if (newBind != null && ControllerInput.getLatestControllerHandle() != controllerHandle) {
                ControllerInput.forceSetLatestControllerHandle(controllerHandle);
            }
            while (newBind != null && !this.waitingForNextAnalogInput.isEmpty()) {
                this.waitingForNextAnalogInput.removeFirst().accept(newBind);
            }
        }
        if (!this.waitingForNextButtonInput.isEmpty()) {
            newBind = null;
            if (this.waitingForNextButtonInputToBeReleased != null) {
                if (Arrays.stream(this.controllerHandles).noneMatch(x -> x == this.waitingForNextButtonInputToBeReleased.object)) {
                    this.waitingForNextButtonInputToBeReleased = null;
                    return;
                }
                GLFWControllerBind controllerBind = (GLFWControllerBind)this.waitingForNextButtonInputToBeReleased.value;
                controllerHandle = (GLFWControllerHandle)this.waitingForNextButtonInputToBeReleased.object;
                int bind = controllerBind.getBinds()[0];
                switch (controllerBind.getBindType()) {
                    case Button: 
                    case GamepadButton: {
                        ByteBuffer buttonStates;
                        ByteBuffer byteBuffer = buttonStates = controllerBind.getBindType() == GLFWControllerBind.BindType.Button ? controllerHandle.getButtonStates() : controllerHandle.getGamepadState().buttons();
                        if (buttonStates == null || buttonStates.get(bind) != 0) break;
                        newBind = controllerBind;
                        this.waitingForNextButtonInputToBeReleased = null;
                        break;
                    }
                    case GamepadAxis: 
                    case Axis: {
                        FloatBuffer axisStates;
                        FloatBuffer floatBuffer = axisStates = controllerBind.getBindType() == GLFWControllerBind.BindType.Axis ? controllerHandle.getAxisStates() : controllerHandle.getGamepadState().axes();
                        if (axisStates == null || (double)Math.abs(axisStates.get(bind)) > 0.4 && (double)Math.abs(axisStates.get(bind)) < 0.6) break;
                        newBind = controllerBind;
                        this.waitingForNextButtonInputToBeReleased = null;
                    }
                }
            } else {
                if (controllerHandle.getGamepadState() != null && controllerHandle.isSupportedAsGamepad()) {
                    ByteBuffer gamepadButtons = controllerHandle.getGamepadState().buttons();
                    for (i2 = 0; i2 < gamepadButtons.capacity(); ++i2) {
                        if (gamepadButtons.get(i2) != 1) continue;
                        this.waitingForNextButtonInputToBeReleased = new ObjectValue<GLFWControllerHandle, GLFWControllerBind>(controllerHandle, new GLFWControllerBind(GLFWControllerBind.BindType.GamepadButton, new int[]{i2}));
                    }
                    if (this.waitingForNextButtonInputToBeReleased == null) {
                        FloatBuffer axis2 = controllerHandle.getGamepadState().axes();
                        for (i = 0; i < axis2.capacity(); ++i) {
                            if (!((double)Math.abs(axis2.get(i)) > 0.4) || !((double)Math.abs(axis2.get(i)) < 0.6)) continue;
                            this.waitingForNextButtonInputToBeReleased = new ObjectValue<GLFWControllerHandle, GLFWControllerBind>(controllerHandle, new GLFWControllerBind(GLFWControllerBind.BindType.GamepadAxis, new int[]{i}));
                        }
                    }
                }
                axis = controllerHandle.getAxisStates();
                if (this.waitingForNextButtonInputToBeReleased == null && axis != null) {
                    for (int i3 = 0; i3 < axis.capacity(); ++i3) {
                        if (!((double)Math.abs(axis.get(i3)) > 0.4) || !((double)Math.abs(axis.get(i3)) < 0.6)) continue;
                        this.waitingForNextButtonInputToBeReleased = new ObjectValue<GLFWControllerHandle, GLFWControllerBind>(controllerHandle, new GLFWControllerBind(GLFWControllerBind.BindType.Axis, new int[]{i3}));
                    }
                }
                ByteBuffer buttons = controllerHandle.getButtonStates();
                if (this.waitingForNextButtonInputToBeReleased == null && buttons != null) {
                    for (i = 0; i < buttons.capacity(); ++i) {
                        if (buttons.get(i) != 1) continue;
                        this.waitingForNextButtonInputToBeReleased = new ObjectValue<GLFWControllerHandle, GLFWControllerBind>(controllerHandle, new GLFWControllerBind(GLFWControllerBind.BindType.Button, new int[]{i}));
                    }
                }
            }
            if (newBind != null && ControllerInput.getLatestControllerHandle() != controllerHandle) {
                ControllerInput.forceSetLatestControllerHandle(controllerHandle);
            }
            while (newBind != null && !this.waitingForNextButtonInput.isEmpty()) {
                this.waitingForNextButtonInput.removeFirst().accept(newBind);
            }
        }
    }

    @Override
    public ControllerHandle updateControllerState(ControllerInputState state, ArrayList<ControllerEvent> outEventList, TickManager tickManager) {
        if (!this.provideControllerInput) {
            return null;
        }
        GLFWControllerHandle controllerUsed = null;
        for (GLFWControllerHandle controllerHandle : this.controllerHandles) {
            GLFWControllerBind bind;
            if (controllerHandle == null || !(bind = (GLFWControllerBind)this.getControllerBindForState(state, controllerHandle)).updateStateIfInput(state, controllerHandle, outEventList, tickManager)) continue;
            controllerUsed = controllerHandle;
            break;
        }
        return controllerUsed;
    }

    @Override
    public ControllerBind getControllerBindForState(ControllerInputState state, ControllerHandle controllerHandle) {
        if (!this.provideControllerInput || !(controllerHandle instanceof GLFWControllerHandle)) {
            return null;
        }
        return this.controlBinds.get(((GLFWControllerHandle)controllerHandle).guid).computeIfAbsent(state, value -> (GLFWControllerBind)this.getDefaultControllerBind(state, controllerHandle));
    }

    @Override
    public void saveControllerStateBind(ControllerInputState state, SaveData saveData) {
        if (!this.provideControllerInput) {
            return;
        }
        for (String guid : this.controlBinds.keySet()) {
            SaveData guidSaveData = new SaveData(guid);
            GLFWControllerBind controllerBind = this.controlBinds.get(guid).get(state);
            if (controllerBind == null) continue;
            controllerBind.saveBind(guidSaveData);
            saveData.addSaveData(guidSaveData);
        }
    }

    @Override
    public void loadControllerStateBind(ControllerInputState state, LoadData loadData) {
        if (!this.provideControllerInput) {
            return;
        }
        List<LoadData> guids = loadData.getLoadData();
        for (LoadData guid : guids) {
            GLFWControllerBind controllerBind = (GLFWControllerBind)this.getDefaultControllerBind(state, ControllerInput.getLatestControllerHandle());
            controllerBind.loadBind(guid);
            this.controlBinds.computeIfAbsent(guid.getName(), value -> new HashMap()).put(state, controllerBind);
        }
    }

    @Override
    public ArrayList<InputSource.ControlSettings> getControlSettings() {
        ArrayList<InputSource.ControlSettings> controlSettings = new ArrayList<InputSource.ControlSettings>();
        controlSettings.add(new InputSource.ControlSettings(new LocalMessage("settingsui", "mouseandkeyboard"), null, new FormKeyboardAndMouseControlListPopulator()));
        if (this.provideControllerInput && this.connectedControllerCount > 0) {
            controlSettings.add(new InputSource.ControlSettings(new LocalMessage("settingsui", "controller"), null, new FormControllerControlListPopulator(this)));
        }
        return controlSettings;
    }

    @Override
    public ControllerBind getDefaultControllerBind(ControllerInputState state, ControllerHandle controllerHandle) {
        if (!this.provideControllerInput) {
            return null;
        }
        ObjectValue<ControllerDefaultBinds.BindType, Integer> defaultBind = null;
        if (controllerHandle instanceof GLFWControllerHandle && ((GLFWControllerHandle)controllerHandle).isSupportedAsGamepad()) {
            defaultBind = ControllerDefaultBinds.GAMEPAD.get(state);
        }
        if (controllerHandle != null) {
            switch (controllerHandle.type) {
                case PlayStation4: {
                    defaultBind = ControllerDefaultBinds.PLAYSTATION4.get(state);
                    break;
                }
                case PlayStation5: {
                    defaultBind = ControllerDefaultBinds.PLAYSTATION5.get(state);
                    break;
                }
                case Xbox: {
                    defaultBind = ControllerDefaultBinds.XBOX.get(state);
                    break;
                }
                case XboxGeneric: {
                    defaultBind = ControllerDefaultBinds.XBOXGeneric.get(state);
                    break;
                }
                case XboxLinuxWireless: {
                    defaultBind = ControllerDefaultBinds.XBOXLinuxWireless.get(state);
                    break;
                }
                case XboxLinuxWired: {
                    defaultBind = ControllerDefaultBinds.XBOXLinuxWired.get(state);
                }
            }
        }
        if (defaultBind == null) {
            return new GLFWControllerBind(GLFWControllerBind.BindType.NotSet, new int[0]);
        }
        if (defaultBind.object == ControllerDefaultBinds.BindType.Gamepad && (Integer)defaultBind.value >= 100) {
            switch ((Integer)defaultBind.value) {
                case 100: {
                    return new GLFWControllerBind(GLFWControllerBind.BindType.GamepadAxis, new int[]{0});
                }
                case 101: {
                    return new GLFWControllerBind(GLFWControllerBind.BindType.GamepadAxis, new int[]{1});
                }
                case 102: {
                    return new GLFWControllerBind(GLFWControllerBind.BindType.GamepadAxis, new int[]{2});
                }
                case 103: {
                    return new GLFWControllerBind(GLFWControllerBind.BindType.GamepadAxis, new int[]{3});
                }
                case 104: {
                    return new GLFWControllerBind(GLFWControllerBind.BindType.GamepadAxis, new int[]{4});
                }
                case 105: {
                    return new GLFWControllerBind(GLFWControllerBind.BindType.GamepadAxis, new int[]{5});
                }
                case 106: {
                    return new GLFWControllerBind(GLFWControllerBind.BindType.GamepadAxis, new int[]{106});
                }
                case 107: {
                    return new GLFWControllerBind(GLFWControllerBind.BindType.GamepadAxis, new int[]{107});
                }
            }
            return new GLFWControllerBind(GLFWControllerBind.BindType.NotSet, new int[0]);
        }
        if (state instanceof ControllerAnalogState) {
            return new GLFWControllerBind(defaultBind.object == ControllerDefaultBinds.BindType.Gamepad ? GLFWControllerBind.BindType.GamepadAxis : GLFWControllerBind.BindType.Axis, new int[]{(Integer)defaultBind.value, (Integer)defaultBind.value + 1});
        }
        if (defaultBind.object == ControllerDefaultBinds.BindType.Gamepad) {
            return new GLFWControllerBind(GLFWControllerBind.BindType.GamepadButton, new int[]{(Integer)defaultBind.value});
        }
        return new GLFWControllerBind(defaultBind.object == ControllerDefaultBinds.BindType.Button ? GLFWControllerBind.BindType.Button : GLFWControllerBind.BindType.Axis, new int[]{(Integer)defaultBind.value});
    }

    @Override
    public void restoreAllControllerBinds(ControllerHandle controllerHandle) {
        if (controllerHandle instanceof GLFWControllerHandle) {
            this.controlBinds.get(((GLFWControllerHandle)controllerHandle).guid).clear();
        }
        super.restoreAllControllerBinds(controllerHandle);
    }

    @Override
    public void restoreControllerBind(ControllerState state, ControllerHandle controllerHandle) {
        if (controllerHandle instanceof GLFWControllerHandle) {
            this.controlBinds.get(((GLFWControllerHandle)controllerHandle).guid).remove(state);
        }
        super.restoreControllerBind(state, controllerHandle);
    }

    @Override
    public void clearControllerBind(ControllerInputState state, ControllerHandle controllerHandle) {
        if (!(controllerHandle instanceof GLFWControllerHandle)) {
            return;
        }
        this.controlBinds.get(((GLFWControllerHandle)controllerHandle).guid).put(state, new GLFWControllerBind(GLFWControllerBind.BindType.NotSet, new int[0]));
        super.clearControllerBind(state, controllerHandle);
    }

    @Override
    public void setStateBind(ControllerInputState state, ControllerBind bind, ControllerHandle controllerHandle) {
        if (!bind.isBound()) {
            GameLog.warn.println("Could not set controller bind for state: " + state.actionName + " because it's not bound.");
            return;
        }
        GLFWControllerHandle glfwControllerHandle = (GLFWControllerHandle)controllerHandle;
        GLFWControllerBind glfwBind = (GLFWControllerBind)bind;
        GLFWControllerBind.BindType bindType = glfwBind.getBindType();
        int[] binds = glfwBind.getBinds();
        if (state instanceof ControllerAnalogState) {
            if (bindType == GLFWControllerBind.BindType.GamepadAxis || bindType == GLFWControllerBind.BindType.Axis && binds.length == 2) {
                this.controlBinds.get(glfwControllerHandle.guid).put(state, glfwBind);
            } else if (bindType == GLFWControllerBind.BindType.Hat) {
                this.controlBinds.get(glfwControllerHandle.guid).put(state, glfwBind);
            } else {
                GameLog.warn.println("Tried setting unsupported bind for state: " + state.actionName);
            }
        } else if (state instanceof ControllerButtonState && binds.length == 1) {
            if (bindType == GLFWControllerBind.BindType.GamepadButton || bindType == GLFWControllerBind.BindType.Button || bindType == GLFWControllerBind.BindType.Axis || bindType == GLFWControllerBind.BindType.GamepadAxis) {
                this.controlBinds.get(glfwControllerHandle.guid).put(state, glfwBind);
            } else {
                GameLog.warn.println("Tried setting unsupported bind for state: " + state.actionName);
            }
        } else {
            GameLog.warn.println("Tried setting unsupported bind for state: " + state.actionName);
        }
        super.setStateBind(state, bind, controllerHandle);
    }

    @Override
    public void onNextAnalogInput(Consumer<ControllerBind> onInputReceived) {
        this.waitingForNextAnalogInput.add(onInputReceived);
    }

    @Override
    public void onNextButtonInput(Consumer<ControllerBind> onInputReceived) {
        this.waitingForNextButtonInput.add(onInputReceived);
    }
}

