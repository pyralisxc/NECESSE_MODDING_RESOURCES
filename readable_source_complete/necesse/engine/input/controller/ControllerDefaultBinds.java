/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.input.controller;

import java.util.HashMap;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.input.controller.ControllerInputState;
import necesse.engine.util.ObjectValue;

public class ControllerDefaultBinds {
    public static final HashMap<ControllerInputState, ObjectValue<BindType, Integer>> GAMEPAD = new HashMap();
    public static final HashMap<ControllerInputState, ObjectValue<BindType, Integer>> PLAYSTATION5 = new HashMap();
    public static final HashMap<ControllerInputState, ObjectValue<BindType, Integer>> PLAYSTATION4 = new HashMap();
    public static final HashMap<ControllerInputState, ObjectValue<BindType, Integer>> XBOX = new HashMap();
    public static final HashMap<ControllerInputState, ObjectValue<BindType, Integer>> XBOXGeneric = new HashMap();
    public static final HashMap<ControllerInputState, ObjectValue<BindType, Integer>> XBOXLinuxWireless = new HashMap();
    public static final HashMap<ControllerInputState, ObjectValue<BindType, Integer>> XBOXLinuxWired = new HashMap();

    static {
        GAMEPAD.put(ControllerInput.MOVE, new ObjectValue<BindType, Integer>(BindType.Gamepad, 106));
        GAMEPAD.put(ControllerInput.AIM, new ObjectValue<BindType, Integer>(BindType.Gamepad, 107));
        GAMEPAD.put(ControllerInput.ATTACK, new ObjectValue<BindType, Integer>(BindType.Gamepad, 105));
        GAMEPAD.put(ControllerInput.INTERACT, new ObjectValue<BindType, Integer>(BindType.Gamepad, 104));
        GAMEPAD.put(ControllerInput.INVENTORY, new ObjectValue<BindType, Integer>(BindType.Gamepad, 3));
        GAMEPAD.put(ControllerInput.HEALTH_POTION, new ObjectValue<BindType, Integer>(BindType.Gamepad, 1));
        GAMEPAD.put(ControllerInput.MANA_POTION, new ObjectValue<BindType, Integer>(BindType.Gamepad, 11));
        GAMEPAD.put(ControllerInput.USE_MOUNT, new ObjectValue<BindType, Integer>(BindType.Gamepad, 13));
        GAMEPAD.put(ControllerInput.SET_ABILITY, new ObjectValue<BindType, Integer>(BindType.Gamepad, 12));
        GAMEPAD.put(ControllerInput.TRINKET_ABILITY, new ObjectValue<BindType, Integer>(BindType.Gamepad, 2));
        GAMEPAD.put(ControllerInput.PLACE_TORCH, new ObjectValue<BindType, Integer>(BindType.Gamepad, 0));
        GAMEPAD.put(ControllerInput.OPEN_SETTLEMENT, new ObjectValue<BindType, Integer>(BindType.Gamepad, 14));
        GAMEPAD.put(ControllerInput.MAIN_MENU, new ObjectValue<BindType, Integer>(BindType.Gamepad, 7));
        GAMEPAD.put(ControllerInput.SHOW_MAP, new ObjectValue<BindType, Integer>(BindType.Gamepad, 6));
        GAMEPAD.put(ControllerInput.NEXT_HOTBAR, new ObjectValue<BindType, Integer>(BindType.Gamepad, 5));
        GAMEPAD.put(ControllerInput.PREV_HOTBAR, new ObjectValue<BindType, Integer>(BindType.Gamepad, 4));
        GAMEPAD.put(ControllerInput.SMART_MINING, new ObjectValue<BindType, Integer>(BindType.Gamepad, 9));
        GAMEPAD.put(ControllerInput.TOGGLE_AIM, new ObjectValue<BindType, Integer>(BindType.Gamepad, 10));
        GAMEPAD.put(ControllerInput.MENU_UP, new ObjectValue<BindType, Integer>(BindType.Gamepad, 11));
        GAMEPAD.put(ControllerInput.MENU_RIGHT, new ObjectValue<BindType, Integer>(BindType.Gamepad, 12));
        GAMEPAD.put(ControllerInput.MENU_DOWN, new ObjectValue<BindType, Integer>(BindType.Gamepad, 13));
        GAMEPAD.put(ControllerInput.MENU_LEFT, new ObjectValue<BindType, Integer>(BindType.Gamepad, 14));
        GAMEPAD.put(ControllerInput.MENU_SELECT, new ObjectValue<BindType, Integer>(BindType.Gamepad, 0));
        GAMEPAD.put(ControllerInput.MENU_BACK, new ObjectValue<BindType, Integer>(BindType.Gamepad, 1));
        GAMEPAD.put(ControllerInput.MENU_NEXT, new ObjectValue<BindType, Integer>(BindType.Gamepad, 5));
        GAMEPAD.put(ControllerInput.MENU_PREV, new ObjectValue<BindType, Integer>(BindType.Gamepad, 4));
        GAMEPAD.put(ControllerInput.MENU_INTERACT_ITEM, new ObjectValue<BindType, Integer>(BindType.Gamepad, 6));
        GAMEPAD.put(ControllerInput.MENU_ITEM_ACTIONS_MENU, new ObjectValue<BindType, Integer>(BindType.Gamepad, 2));
        for (ControllerInputState state : GAMEPAD.keySet()) {
            ObjectValue<BindType, Integer> value = GAMEPAD.get(state);
            PLAYSTATION5.put(state, value);
            PLAYSTATION4.put(state, value);
            XBOX.put(state, value);
        }
        XBOXGeneric.put(ControllerInput.MOVE, new ObjectValue<BindType, Integer>(BindType.Axis, 0));
        XBOXGeneric.put(ControllerInput.AIM, new ObjectValue<BindType, Integer>(BindType.Axis, 2));
        XBOXGeneric.put(ControllerInput.ATTACK, new ObjectValue<BindType, Integer>(BindType.Axis, 4));
        XBOXGeneric.put(ControllerInput.INTERACT, new ObjectValue<BindType, Integer>(BindType.Axis, 5));
        XBOXGeneric.put(ControllerInput.INVENTORY, new ObjectValue<BindType, Integer>(BindType.Button, 4));
        XBOXGeneric.put(ControllerInput.HEALTH_POTION, new ObjectValue<BindType, Integer>(BindType.Button, 1));
        XBOXGeneric.put(ControllerInput.MANA_POTION, new ObjectValue<BindType, Integer>(BindType.Button, 16));
        XBOXGeneric.put(ControllerInput.USE_MOUNT, new ObjectValue<BindType, Integer>(BindType.Button, 18));
        XBOXGeneric.put(ControllerInput.SET_ABILITY, new ObjectValue<BindType, Integer>(BindType.Button, 17));
        XBOXGeneric.put(ControllerInput.TRINKET_ABILITY, new ObjectValue<BindType, Integer>(BindType.Button, 3));
        XBOXGeneric.put(ControllerInput.PLACE_TORCH, new ObjectValue<BindType, Integer>(BindType.Button, 0));
        XBOXGeneric.put(ControllerInput.OPEN_SETTLEMENT, new ObjectValue<BindType, Integer>(BindType.Button, 19));
        XBOXGeneric.put(ControllerInput.MAIN_MENU, new ObjectValue<BindType, Integer>(BindType.Button, 11));
        XBOXGeneric.put(ControllerInput.SHOW_MAP, new ObjectValue<BindType, Integer>(BindType.Button, 10));
        XBOXGeneric.put(ControllerInput.NEXT_HOTBAR, new ObjectValue<BindType, Integer>(BindType.Button, 7));
        XBOXGeneric.put(ControllerInput.PREV_HOTBAR, new ObjectValue<BindType, Integer>(BindType.Button, 6));
        XBOXGeneric.put(ControllerInput.SMART_MINING, new ObjectValue<BindType, Integer>(BindType.Button, 13));
        XBOXGeneric.put(ControllerInput.TOGGLE_AIM, new ObjectValue<BindType, Integer>(BindType.Button, 14));
        XBOXGeneric.put(ControllerInput.MENU_UP, new ObjectValue<BindType, Integer>(BindType.Button, 16));
        XBOXGeneric.put(ControllerInput.MENU_RIGHT, new ObjectValue<BindType, Integer>(BindType.Button, 17));
        XBOXGeneric.put(ControllerInput.MENU_DOWN, new ObjectValue<BindType, Integer>(BindType.Button, 18));
        XBOXGeneric.put(ControllerInput.MENU_LEFT, new ObjectValue<BindType, Integer>(BindType.Button, 19));
        XBOXGeneric.put(ControllerInput.MENU_SELECT, new ObjectValue<BindType, Integer>(BindType.Button, 0));
        XBOXGeneric.put(ControllerInput.MENU_BACK, new ObjectValue<BindType, Integer>(BindType.Button, 1));
        XBOXGeneric.put(ControllerInput.MENU_NEXT, new ObjectValue<BindType, Integer>(BindType.Button, 7));
        XBOXGeneric.put(ControllerInput.MENU_PREV, new ObjectValue<BindType, Integer>(BindType.Button, 6));
        XBOXGeneric.put(ControllerInput.MENU_INTERACT_ITEM, new ObjectValue<BindType, Integer>(BindType.Button, 10));
        XBOXGeneric.put(ControllerInput.MENU_ITEM_ACTIONS_MENU, new ObjectValue<BindType, Integer>(BindType.Button, 3));
        XBOXLinuxWireless.putAll(XBOXGeneric);
        XBOXLinuxWireless.put(ControllerInput.MANA_POTION, new ObjectValue<BindType, Integer>(BindType.Button, 15));
        XBOXLinuxWireless.put(ControllerInput.USE_MOUNT, new ObjectValue<BindType, Integer>(BindType.Button, 17));
        XBOXLinuxWireless.put(ControllerInput.SET_ABILITY, new ObjectValue<BindType, Integer>(BindType.Button, 16));
        XBOXLinuxWireless.put(ControllerInput.OPEN_SETTLEMENT, new ObjectValue<BindType, Integer>(BindType.Button, 18));
        XBOXLinuxWireless.put(ControllerInput.MENU_UP, new ObjectValue<BindType, Integer>(BindType.Button, 15));
        XBOXLinuxWireless.put(ControllerInput.MENU_RIGHT, new ObjectValue<BindType, Integer>(BindType.Button, 16));
        XBOXLinuxWireless.put(ControllerInput.MENU_DOWN, new ObjectValue<BindType, Integer>(BindType.Button, 17));
        XBOXLinuxWireless.put(ControllerInput.MENU_LEFT, new ObjectValue<BindType, Integer>(BindType.Button, 18));
        XBOXLinuxWired.putAll(XBOXGeneric);
        XBOXLinuxWired.put(ControllerInput.AIM, new ObjectValue<BindType, Integer>(BindType.Axis, 3));
        XBOXLinuxWired.put(ControllerInput.ATTACK, new ObjectValue<BindType, Integer>(BindType.Axis, 5));
        XBOXLinuxWired.put(ControllerInput.INTERACT, new ObjectValue<BindType, Integer>(BindType.Axis, 2));
        XBOXLinuxWired.put(ControllerInput.INVENTORY, new ObjectValue<BindType, Integer>(BindType.Button, 3));
        XBOXLinuxWired.put(ControllerInput.MANA_POTION, new ObjectValue<BindType, Integer>(BindType.Button, 11));
        XBOXLinuxWired.put(ControllerInput.USE_MOUNT, new ObjectValue<BindType, Integer>(BindType.Button, 13));
        XBOXLinuxWired.put(ControllerInput.SET_ABILITY, new ObjectValue<BindType, Integer>(BindType.Button, 12));
        XBOXLinuxWired.put(ControllerInput.TRINKET_ABILITY, new ObjectValue<BindType, Integer>(BindType.Button, 2));
        XBOXLinuxWired.put(ControllerInput.OPEN_SETTLEMENT, new ObjectValue<BindType, Integer>(BindType.Button, 14));
        XBOXLinuxWired.put(ControllerInput.MAIN_MENU, new ObjectValue<BindType, Integer>(BindType.Button, 7));
        XBOXLinuxWired.put(ControllerInput.SHOW_MAP, new ObjectValue<BindType, Integer>(BindType.Button, 6));
        XBOXLinuxWired.put(ControllerInput.NEXT_HOTBAR, new ObjectValue<BindType, Integer>(BindType.Button, 5));
        XBOXLinuxWired.put(ControllerInput.PREV_HOTBAR, new ObjectValue<BindType, Integer>(BindType.Button, 4));
        XBOXLinuxWired.put(ControllerInput.SMART_MINING, new ObjectValue<BindType, Integer>(BindType.Button, 9));
        XBOXLinuxWired.put(ControllerInput.TOGGLE_AIM, new ObjectValue<BindType, Integer>(BindType.Button, 10));
        XBOXLinuxWired.put(ControllerInput.MENU_UP, new ObjectValue<BindType, Integer>(BindType.Button, 11));
        XBOXLinuxWired.put(ControllerInput.MENU_RIGHT, new ObjectValue<BindType, Integer>(BindType.Button, 12));
        XBOXLinuxWired.put(ControllerInput.MENU_DOWN, new ObjectValue<BindType, Integer>(BindType.Button, 13));
        XBOXLinuxWired.put(ControllerInput.MENU_LEFT, new ObjectValue<BindType, Integer>(BindType.Button, 14));
        XBOXLinuxWired.put(ControllerInput.MENU_NEXT, new ObjectValue<BindType, Integer>(BindType.Button, 5));
        XBOXLinuxWired.put(ControllerInput.MENU_PREV, new ObjectValue<BindType, Integer>(BindType.Button, 4));
        XBOXLinuxWired.put(ControllerInput.MENU_INTERACT_ITEM, new ObjectValue<BindType, Integer>(BindType.Button, 6));
        XBOXLinuxWired.put(ControllerInput.MENU_ITEM_ACTIONS_MENU, new ObjectValue<BindType, Integer>(BindType.Button, 2));
    }

    public static enum BindType {
        Gamepad,
        Button,
        Axis;

    }
}

