/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.input;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Function;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Input;
import necesse.engine.input.InputPosition;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.window.WindowManager;

public class InputEvent {
    private InputEvent parent;
    private LinkedList<InputEvent> children = new LinkedList();
    private int lastID = -1000;
    private int id;
    public final long tick;
    public final long frame;
    public final boolean state;
    private double mouseWheelX;
    private double mouseWheelY;
    public final InputPosition pos;
    private boolean isMoveUsed;
    private int scancode;
    private Object[] repeatCallers;
    private int repeatCounter;
    private int repeatDelay;
    private int repeatID;
    private boolean preventRepeatEvent = false;
    InputEvent downStateEvent;
    private String key;
    private int codepoint;
    private ControllerEvent controllerEvent;

    public static InputEvent ControllerButtonEvent(ControllerEvent event, TickManager tickManager) {
        InputEvent inputEvent = new InputEvent(-104, event.buttonState, InputPosition.dummyPos(), tickManager);
        inputEvent.controllerEvent = event;
        if (event.isUsed()) {
            inputEvent.use();
        } else {
            event.inputEvents.add(inputEvent);
        }
        return inputEvent;
    }

    public static InputEvent KeyboardEvent(int id, boolean state, int scancode, InputPosition pos, TickManager tickManager) {
        InputEvent event = new InputEvent(id, state, pos, tickManager);
        event.scancode = scancode;
        return event;
    }

    public static InputEvent CharacterEvent(int codepoint, String key, InputPosition pos, TickManager tickManager) {
        InputEvent event = new InputEvent(-1, true, pos, tickManager);
        event.codepoint = codepoint;
        event.key = key;
        return event;
    }

    public static InputEvent MouseButtonEvent(int mouseButton, boolean state, InputPosition pos, TickManager tickManager) {
        if (mouseButton < 0 || mouseButton > 50) {
            throw new IllegalArgumentException("Mouse button must in range [0-50]");
        }
        return new InputEvent(mouseButton - 100, state, pos, tickManager);
    }

    public static InputEvent RepeatEvent(Object[] callers, int repeatCounter, int repeatDelay, InputPosition pos, TickManager tickManager, int repeatID) {
        InputEvent e = new InputEvent(-105, true, pos, tickManager);
        e.repeatCallers = callers;
        e.repeatCounter = repeatCounter;
        e.repeatDelay = repeatDelay;
        e.repeatID = repeatID;
        return e;
    }

    public static InputEvent MouseScrollEvent(double mouseWheelX, double mouseWheelY, InputPosition pos, TickManager tickManager) {
        InputEvent event = new InputEvent(mouseWheelY < 0.0 ? -102 : -103, true, pos, tickManager);
        event.mouseWheelX = mouseWheelX;
        event.mouseWheelY = mouseWheelY;
        return event;
    }

    public static InputEvent MouseMoveEvent(InputPosition pos, TickManager tickManager) {
        return new InputEvent(-101, true, pos, tickManager);
    }

    public static InputEvent ReplacePosEvent(InputEvent event, InputPosition pos) {
        InputEvent out = new InputEvent(event.getID(), event.state, pos, event.tick, event.frame);
        out.parent = event.parent;
        event.parent.children.add(out);
        out.lastID = event.lastID;
        out.mouseWheelX = event.mouseWheelX;
        out.mouseWheelY = event.mouseWheelY;
        out.repeatCallers = event.repeatCallers;
        out.repeatCounter = event.repeatCounter;
        out.repeatDelay = event.repeatDelay;
        out.repeatID = event.repeatID;
        out.scancode = event.scancode;
        out.key = event.key;
        out.codepoint = event.codepoint;
        out.isMoveUsed = event.isMoveUsed;
        out.preventRepeatEvent = event.preventRepeatEvent;
        return out;
    }

    public static InputEvent OffsetHudEvent(Input input, InputEvent event, int xOffset, int yOffset) {
        return InputEvent.ReplacePosEvent(event, InputPosition.fromHudPos(input, event.pos.hudX == Integer.MIN_VALUE ? event.pos.hudX : event.pos.hudX + xOffset, event.pos.hudY == Integer.MIN_VALUE ? event.pos.hudY : event.pos.hudY + yOffset));
    }

    protected InputEvent(int id, boolean state, InputPosition pos, TickManager tickManager) {
        this(id, state, pos, tickManager == null ? 0L : tickManager.getTotalTicks(), tickManager == null ? 0L : tickManager.getTotalFrames());
    }

    protected InputEvent(int id, boolean state, InputPosition pos, long tick, long frame) {
        this.id = id;
        this.state = state;
        this.pos = pos;
        this.tick = tick;
        this.frame = frame;
        this.parent = this;
    }

    public int getID() {
        return this.id;
    }

    public int getLastID() {
        return this.lastID;
    }

    public double getMouseWheelX() {
        return this.mouseWheelX;
    }

    public double getMouseWheelY() {
        return this.mouseWheelY;
    }

    public boolean isKeyboardEvent() {
        return InputEvent.isKeyboardEvent(this.getID());
    }

    public boolean isMouseClickEvent() {
        return InputEvent.isMouseClickEvent(this.getID());
    }

    public boolean isCharacterEvent() {
        return InputEvent.isCharacterEvent(this.getID());
    }

    public boolean isMouseMoveEvent() {
        return InputEvent.isMouseMoveEvent(this.getID());
    }

    public boolean isMouseWheelEvent() {
        return InputEvent.isMouseWheelEvent(this.getID());
    }

    public boolean isControllerEvent() {
        return InputEvent.isControllerEvent(this.getID());
    }

    public ControllerEvent getControllerEvent() {
        return this.controllerEvent;
    }

    public boolean wasKeyboardEvent() {
        return InputEvent.isKeyboardEvent(this.getLastID());
    }

    public boolean wasMouseClickEvent() {
        return InputEvent.isMouseClickEvent(this.getLastID());
    }

    public boolean wasCharacterEvent() {
        return InputEvent.isCharacterEvent(this.getLastID());
    }

    public boolean wasMouseMoveEvent() {
        return InputEvent.isMouseMoveEvent(this.getLastID());
    }

    public boolean wasMouseWheelEvent() {
        return InputEvent.isMouseWheelEvent(this.getLastID());
    }

    public boolean wasControllerEvent() {
        return InputEvent.isControllerEvent(this.getLastID());
    }

    public boolean isRepeatEvent(Function<Object, Boolean> validCaller) {
        return this.isRepeatEvent(0, validCaller);
    }

    public boolean isRepeatEvent(int index, Function<Object, Boolean> validCaller) {
        if (this.getID() == -105) {
            if (this.repeatCallers != null && index < this.repeatCallers.length) {
                return validCaller.apply(this.repeatCallers[index]);
            }
            return validCaller.apply(null);
        }
        return false;
    }

    public boolean isRepeatEvent(Object ... callers) {
        if (this.getID() == -105) {
            return Arrays.equals(this.repeatCallers, callers);
        }
        return false;
    }

    public InputEvent getDownStateEvent() {
        return this.downStateEvent;
    }

    public boolean isSameFrame(TickManager tickManager) {
        return this.frame == tickManager.getTotalFrames();
    }

    public boolean isSameFrame(InputEvent other) {
        return other.frame == this.frame;
    }

    public boolean shouldSubmitSound() {
        if (this.controllerEvent != null) {
            return this.controllerEvent.shouldSubmitSound();
        }
        if (this.getID() != -105) {
            return true;
        }
        int maxMS = 11;
        if (this.repeatDelay >= maxMS) {
            return true;
        }
        return this.repeatCounter % (maxMS / this.repeatDelay) == 0;
    }

    public int getRepeatID() {
        return this.repeatID;
    }

    public boolean isRepeatEvent(Object caller) {
        return this.isRepeatEvent((Object o) -> Objects.equals(o, caller));
    }

    public boolean isRepeatEvent(int index, Object caller) {
        return this.isRepeatEvent(index, (Object o) -> Objects.equals(o, caller));
    }

    public String getChar() {
        return this.key;
    }

    public void useMove() {
        if (!this.isMoveUsed()) {
            this.isMoveUsed = true;
            this.children.stream().filter(e -> e != this).forEach(InputEvent::useMove);
            if (this.parent != this) {
                this.parent.useMove();
            }
        }
    }

    public boolean isMoveUsed() {
        return this.isMoveUsed;
    }

    public void use() {
        if (!this.isUsed()) {
            this.lastID = this.id;
            this.id = -1000;
            if (this.parent != this) {
                this.parent.use();
                this.parent.children.stream().filter(e -> e != this).forEach(InputEvent::use);
            }
            this.children.stream().filter(e -> e != this).forEach(InputEvent::use);
            if (this.controllerEvent != null) {
                this.controllerEvent.use();
            }
        }
    }

    public boolean isSameEvent(InputEvent event) {
        if (event == this) {
            return true;
        }
        if (this.parent != null && this.parent != this) {
            return this.parent.isSameEvent(event);
        }
        return this.children.contains(event);
    }

    public boolean isUsed() {
        return this.getID() == -1000;
    }

    public void preventStartOfRepeatEvent() {
        this.preventRepeatEvent = true;
    }

    public void startRepeatEvents(Object ... callers) {
        if (this.preventRepeatEvent) {
            return;
        }
        WindowManager.getWindow().getInput().startRepeatEvents(this, callers);
    }

    public static boolean isKeyboardEvent(int id) {
        return id >= 0;
    }

    public static boolean isMouseClickEvent(int id) {
        return id <= -50 && id >= -100;
    }

    public static boolean isCharacterEvent(int id) {
        return id == -1;
    }

    public static boolean isMouseMoveEvent(int id) {
        return id == -101;
    }

    public static boolean isMouseWheelEvent(int id) {
        return id == -102 || id == -103;
    }

    public static boolean isControllerEvent(int id) {
        return id == -104;
    }

    public static boolean isFromSameEvent(InputEvent e1, InputEvent e2) {
        if (e1 != null && e2 != null) {
            return Objects.equals(e1.parent, e2.parent);
        }
        return false;
    }
}

