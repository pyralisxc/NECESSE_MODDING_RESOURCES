/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.debug.tools;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import necesse.engine.GlobalData;
import necesse.engine.gameTool.GameTool;
import necesse.engine.input.Input;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.input.controller.ControllerInputState;
import necesse.engine.input.controller.ControllerState;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.window.WindowManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.InputTooltip;
import necesse.gfx.gameTooltips.ListGameTooltips;

public abstract class ModularGameTool
implements GameTool {
    protected Function<InputEvent, Boolean> scroll;
    protected Function<InputEvent, Boolean> mouseMove;
    protected GameMessage scrollUsage;
    protected HashMap<Integer, Function<InputEvent, Boolean>> keyEvents = new HashMap();
    protected HashMap<ControllerState, Function<InputEvent, Boolean>> controllerEvents = new HashMap();
    protected LinkedHashMap<Integer, GameMessage> keyUsages = new LinkedHashMap();
    protected LinkedHashMap<ControllerInputState, GameMessage> controllerUsages = new LinkedHashMap();

    public void setKeyUsage(int key, ControllerInputState controllerInput, GameMessage usage) {
        this.setControllerUsage(controllerInput, usage);
        if (usage == null) {
            this.keyUsages.remove(key);
            return;
        }
        this.keyUsages.put(key, usage);
    }

    public void setControllerUsage(ControllerInputState controllerInput, GameMessage usage) {
        if (usage == null) {
            this.controllerUsages.remove(controllerInput);
            return;
        }
        this.controllerUsages.put(controllerInput, usage);
    }

    public void onKeyEvent(int key, ControllerInputState controllerInput, Function<InputEvent, Boolean> event, GameMessage usage) {
        this.keyEvents.put(key, event);
        if (controllerInput != null) {
            this.controllerEvents.put(controllerInput, event);
        }
        this.setKeyUsage(key, controllerInput, usage);
    }

    public void onKeyClick(int key, ControllerInputState controllerInput, Function<InputEvent, Boolean> event, GameMessage usage) {
        this.onKeyEvent(key, controllerInput, e -> {
            if (e.state) {
                return true;
            }
            return (Boolean)event.apply((InputEvent)e);
        }, usage);
    }

    public void setLeftUsage(GameMessage usage) {
        this.setKeyUsage(-100, ControllerInput.MENU_NEXT, usage);
    }

    public void onLeftEvent(Function<InputEvent, Boolean> event, GameMessage usage) {
        this.onKeyEvent(-100, ControllerInput.MENU_NEXT, event, usage);
    }

    public void onLeftClick(Function<InputEvent, Boolean> event, GameMessage usage) {
        this.onKeyClick(-100, ControllerInput.MENU_NEXT, event, usage);
    }

    public void setRightUsage(GameMessage usage) {
        this.setKeyUsage(-99, ControllerInput.MENU_PREV, usage);
    }

    public void onRightEvent(Function<InputEvent, Boolean> event, GameMessage usage) {
        this.onKeyEvent(-99, ControllerInput.MENU_PREV, event, usage);
    }

    public void onRightClick(Function<InputEvent, Boolean> event, GameMessage usage) {
        this.onKeyClick(-99, ControllerInput.MENU_PREV, event, usage);
    }

    public void setScrollUsage(GameMessage usage) {
        this.scrollUsage = usage;
    }

    public void onScroll(Function<InputEvent, Boolean> event, GameMessage usage) {
        this.scroll = event;
        this.setScrollUsage(usage);
    }

    public void onMouseMove(Function<InputEvent, Boolean> event) {
        this.mouseMove = event;
    }

    @Override
    public boolean inputEvent(InputEvent event) {
        if (GlobalData.getCurrentState().getFormManager().isMouseOver(event)) {
            return false;
        }
        if (event.isMouseMoveEvent()) {
            if (this.mouseMove != null) {
                return this.mouseMove.apply(event);
            }
        } else if (event.isMouseWheelEvent()) {
            if (this.scroll != null) {
                return !event.state || this.scroll.apply(event) != false;
            }
        } else {
            Function<InputEvent, Boolean> listener = this.keyEvents.get(event.getID());
            if (listener != null) {
                return listener.apply(event);
            }
        }
        return false;
    }

    @Override
    public boolean controllerEvent(ControllerEvent event) {
        if (GlobalData.getCurrentState().getFormManager().isMouseOver(WindowManager.getWindow().mousePos())) {
            return false;
        }
        Function<InputEvent, Boolean> listener = this.controllerEvents.get(event.getState());
        if (listener != null) {
            return listener.apply(InputEvent.ControllerButtonEvent(event, null));
        }
        return false;
    }

    @Override
    public GameTooltips getTooltips() {
        ListGameTooltips list = new ListGameTooltips();
        if (Input.lastInputIsController) {
            for (Map.Entry<ControllerInputState, GameMessage> entry : this.controllerUsages.entrySet()) {
                list.add(new InputTooltip(entry.getKey(), entry.getValue().translate()));
            }
        } else {
            if (this.scrollUsage != null) {
                list.add(new InputTooltip(-98, this.scrollUsage.translate()));
            }
            for (Map.Entry<Integer, GameMessage> entry : this.keyUsages.entrySet()) {
                list.add(new InputTooltip(entry.getKey(), entry.getValue().translate()));
            }
        }
        return list;
    }
}

