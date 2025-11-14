/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Stream;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Input;
import necesse.engine.input.InputEvent;
import necesse.engine.input.InputManager;
import necesse.engine.input.MouseWheelBuffer;
import necesse.engine.input.controller.ControllerButtonState;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.platforms.Platform;
import necesse.engine.window.WindowManager;
import necesse.gfx.gameFont.CustomGameFont;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;

public class Control {
    private static boolean controlsLoaded;
    private static final ArrayList<Control> controls;
    private static final HashMap<String, Integer> controlsIDs;
    private static final HashSet<ControlGroup> controlGroups;
    private static final HashMap<String, ControlGroup> modGroups;
    private int key;
    public final int defaultKey;
    private boolean isPressed;
    private boolean isReleased;
    public final GameMessage text;
    public GameMessage tooltip;
    public final String id;
    public final LoadedMod mod;
    private ControlGroup group;
    private final int overlapGroup;
    private InputEvent activateEvent;
    public ControllerButtonState controllerState;
    private final MouseWheelBuffer wheelBuffer = new MouseWheelBuffer(false);
    private static InputManager inputManager;
    public static Control MOUSE1;
    public static Control MOUSE2;
    public static Control MOVE_UP;
    public static Control MOVE_DOWN;
    public static Control MOVE_LEFT;
    public static Control MOVE_RIGHT;
    public static Control MOVE_TO_MOUSE;
    public static Control INVENTORY;
    public static Control HEALTH_POT;
    public static Control MANA_POT;
    public static Control BUFF_POTS;
    public static Control EAT_FOOD;
    public static Control USE_MOUNT;
    public static Control SET_ABILITY;
    public static Control TRINKET_ABILITY;
    public static Control PLACE_TORCH;
    public static Control EXPRESSION_WHEEL;
    public static Control OPEN_ADVENTURE_PARTY;
    public static Control OPEN_ADVENTURE_JOURNAL;
    public static Control OPEN_SETTLEMENT;
    public static Control SMART_MINING;
    public static Control LOOT_ALL;
    public static Control SORT_INVENTORY;
    public static Control QUICK_STACK;
    public static Control NEXT_HOTBAR;
    public static Control PREV_HOTBAR;
    public static Control HOTBAR_SLOT_0;
    public static Control HOTBAR_SLOT_1;
    public static Control HOTBAR_SLOT_2;
    public static Control HOTBAR_SLOT_3;
    public static Control HOTBAR_SLOT_4;
    public static Control HOTBAR_SLOT_5;
    public static Control HOTBAR_SLOT_6;
    public static Control HOTBAR_SLOT_7;
    public static Control HOTBAR_SLOT_8;
    public static Control HOTBAR_SLOT_9;
    public static Control ITEM_SET_0;
    public static Control ITEM_SET_1;
    public static Control ITEM_SET_2;
    public static Control ITEM_SET_3;
    public static Control SHOW_MAP;
    public static Control SCOREBOARD;
    public static Control HIDE_CURSOR;
    public static Control HIDE_UI;
    public static Control SCREENSHOT;
    public static Control ZOOM_IN;
    public static Control ZOOM_OUT;
    public static Control PIPETTE;
    public static Control ERASER;
    public static Control DEBUG_INFO;
    public static Control DEBUG_HUD;
    public static Control INV_LOCK;
    public static Control INV_QUICK_MOVE;
    public static Control INV_QUICK_TRASH;
    public static Control INV_QUICK_DROP;
    public static Control CRAFT_10;
    public static Control CRAFT_ALL;
    public static Control[] HOTBAR_SLOTS;
    public static Control[] ITEM_SETS;

    public Control(int defaultKey, String id) {
        this(defaultKey, id, new LocalMessage("controls", id), "default");
    }

    public Control(int defaultKey, String id, String overlapGroup) {
        this(defaultKey, id, new LocalMessage("controls", id), overlapGroup);
    }

    public Control(int defaultKey, String id, GameMessage text) {
        this(defaultKey, id, text, "default");
    }

    public Control(int defaultKey, String id, GameMessage text, String overlapGroup) {
        this.key = defaultKey;
        this.defaultKey = defaultKey;
        this.id = id;
        this.text = text;
        this.overlapGroup = overlapGroup.hashCode();
        this.mod = LoadedMod.getRunningMod();
    }

    public Control setTooltip(GameMessage message) {
        this.tooltip = message;
        return this;
    }

    public void changeKey(int key) {
        this.key = key;
    }

    public void activate(InputEvent event) {
        if (event.getID() == -103) {
            this.wheelBuffer.add(event);
            if (this.wheelBuffer.useAllScrollY() > 0) {
                this.activateEvent = event;
                this.isPressed = true;
            } else {
                this.activateEvent = event;
                this.isReleased = true;
            }
        } else if (event.getID() == -102) {
            this.wheelBuffer.add(event);
            if (this.wheelBuffer.useAllScrollY() < 0) {
                this.activateEvent = event;
                this.isPressed = true;
            } else {
                this.activateEvent = event;
                this.isReleased = true;
            }
        } else {
            this.activateEvent = event;
            if (event.state) {
                this.isPressed = true;
            } else {
                this.isReleased = true;
            }
        }
    }

    public void reset() {
        this.isPressed = false;
        this.isReleased = false;
    }

    public void restoreDefaultKey() {
        this.changeKey(this.defaultKey);
    }

    public boolean isDown() {
        if (!WindowManager.getWindow().isFocused()) {
            return false;
        }
        if (this.activateEvent == null || this.activateEvent.isUsed()) {
            return false;
        }
        if (this.activateEvent.isMouseWheelEvent()) {
            return this.isPressed;
        }
        return this.activateEvent.state;
    }

    public boolean isPressed() {
        if (this.activateEvent == null || this.activateEvent.isUsed()) {
            return false;
        }
        return this.isPressed;
    }

    public boolean isReleased() {
        if (this.activateEvent == null || this.activateEvent.isUsed()) {
            return false;
        }
        return this.isReleased;
    }

    public boolean isDefaultBind() {
        return this.key == this.defaultKey;
    }

    public InputEvent getEvent() {
        return this.activateEvent;
    }

    public boolean isMouseWheel() {
        return InputEvent.isMouseWheelEvent(this.getKey());
    }

    public int getKey() {
        return this.key;
    }

    public String getKeyName() {
        return Input.getName(this.getKey());
    }

    public String getDefaultKeyName() {
        return Input.getName(this.defaultKey);
    }

    public boolean overlaps(Control other) {
        return other != this && this.getKey() != -1 && other.overlapGroup == this.overlapGroup && this.getKey() == other.getKey();
    }

    public static void loadControls() {
        if (controlsLoaded) {
            return;
        }
        ControlGroup GROUP_GENERAL = new ControlGroup(100, new LocalMessage("controls", "groupgeneral"));
        ControlGroup GROUP_MOVEMENT = new ControlGroup(200, new LocalMessage("controls", "groupmovement"));
        ControlGroup GROUP_MISC = new ControlGroup(300, new LocalMessage("controls", "groupmisc"));
        ControlGroup GROUP_HOTBAR = new ControlGroup(400, new LocalMessage("controls", "grouphotbar"));
        ControlGroup GROUP_ITEM_SETS = new ControlGroup(500, new LocalMessage("controls", "groupitemsets"));
        ControlGroup GROUP_KEYMODS = new ControlGroup(600, new LocalMessage("controls", "groupkeymods"));
        ControlGroup GROUP_DEBUG = new ControlGroup(700, new LocalMessage("controls", "groupdebug"));
        MOUSE1 = Control.addControl(new Control(-100, "mouse1"), GROUP_GENERAL);
        MOUSE2 = Control.addControl(new Control(-99, "mouse2"), GROUP_GENERAL);
        MOVE_UP = Control.addControl(new Control(87, "moveup"), GROUP_MOVEMENT);
        MOVE_LEFT = Control.addControl(new Control(65, "moveleft"), GROUP_MOVEMENT);
        MOVE_DOWN = Control.addControl(new Control(83, "movedown"), GROUP_MOVEMENT);
        MOVE_RIGHT = Control.addControl(new Control(68, "moveright"), GROUP_MOVEMENT);
        MOVE_TO_MOUSE = Control.addControl(new Control(-1, "movetomouse"), GROUP_MOVEMENT);
        INVENTORY = Control.addControl(new Control(69, "inventory"), GROUP_GENERAL);
        HEALTH_POT = Control.addControl(new Control(81, "healthpotion"), GROUP_GENERAL);
        MANA_POT = Control.addControl(new Control(90, "manapotion"), GROUP_GENERAL);
        EAT_FOOD = Control.addControl(new Control(-1, "eatfood"), GROUP_GENERAL);
        PIPETTE = Control.addControl(new Control(-98, "pipette").setTooltip(new LocalMessage("controls", "pipettetip")), GROUP_MISC);
        ERASER = Control.addControl(new Control(71, "eraser").setTooltip(new LocalMessage("controls", "erasertip")), GROUP_MISC);
        SCOREBOARD = Control.addControl(new Control(258, "scoreboard"), GROUP_MISC);
        SHOW_MAP = Control.addControl(new Control(77, "showmap", new LocalMessage("controls", "showworldmap")), GROUP_MISC);
        ZOOM_IN = Control.addControl(new Control(334, "zoomin"), GROUP_MISC);
        ZOOM_OUT = Control.addControl(new Control(333, "zoomout"), GROUP_MISC);
        HIDE_CURSOR = Control.addControl(new Control(292, "hidecursor"), GROUP_MISC);
        HIDE_UI = Control.addControl(new Control(293, "hideui"), GROUP_MISC);
        SCREENSHOT = Control.addControl(new Control(294, "screenshot"), GROUP_MISC);
        DEBUG_INFO = Control.addControl(new Control(290, "debuginfo"), GROUP_DEBUG);
        DEBUG_HUD = Control.addControl(new Control(291, "debughud"), GROUP_DEBUG);
        BUFF_POTS = Control.addControl(new Control(66, "buffpotions"), GROUP_GENERAL);
        PLACE_TORCH = Control.addControl(new Control(82, "placetorch"), GROUP_GENERAL);
        EXPRESSION_WHEEL = Control.addControl(new Control(84, "expressionwheel"), GROUP_GENERAL);
        USE_MOUNT = Control.addControl(new Control(70, "usemount"), GROUP_GENERAL);
        SET_ABILITY = Control.addControl(new Control(86, "setability"), GROUP_GENERAL);
        TRINKET_ABILITY = Control.addControl(new Control(32, "trinketability"), GROUP_GENERAL);
        OPEN_ADVENTURE_PARTY = Control.addControl(new Control(88, "openadventureparty"), GROUP_GENERAL);
        OPEN_ADVENTURE_JOURNAL = Control.addControl(new Control(74, "openadventurejournal"), GROUP_GENERAL);
        OPEN_SETTLEMENT = Control.addControl(new Control(67, "opensettlement"), GROUP_GENERAL);
        SMART_MINING = Control.addControl(new Control(341, "smartmining"), GROUP_GENERAL);
        LOOT_ALL = Control.addControl(new Control(-1, "lootall"), GROUP_GENERAL);
        SORT_INVENTORY = Control.addControl(new Control(-1, "sortinventory"), GROUP_GENERAL);
        QUICK_STACK = Control.addControl(new Control(-1, "quickstack"), GROUP_GENERAL);
        NEXT_HOTBAR = Control.addControl(new Control(-102, "nexthotbar"), GROUP_HOTBAR);
        PREV_HOTBAR = Control.addControl(new Control(-103, "prevhotbar"), GROUP_HOTBAR);
        HOTBAR_SLOT_0 = Control.addControl(new Control(49, "hotbarslot0", new LocalMessage("controls", "hotbarslot").addReplacement("number", "1")), GROUP_HOTBAR);
        HOTBAR_SLOT_1 = Control.addControl(new Control(50, "hotbarslot1", new LocalMessage("controls", "hotbarslot").addReplacement("number", "2")), GROUP_HOTBAR);
        HOTBAR_SLOT_2 = Control.addControl(new Control(51, "hotbarslot2", new LocalMessage("controls", "hotbarslot").addReplacement("number", "3")), GROUP_HOTBAR);
        HOTBAR_SLOT_3 = Control.addControl(new Control(52, "hotbarslot3", new LocalMessage("controls", "hotbarslot").addReplacement("number", "4")), GROUP_HOTBAR);
        HOTBAR_SLOT_4 = Control.addControl(new Control(53, "hotbarslot4", new LocalMessage("controls", "hotbarslot").addReplacement("number", "5")), GROUP_HOTBAR);
        HOTBAR_SLOT_5 = Control.addControl(new Control(54, "hotbarslot5", new LocalMessage("controls", "hotbarslot").addReplacement("number", "6")), GROUP_HOTBAR);
        HOTBAR_SLOT_6 = Control.addControl(new Control(55, "hotbarslot6", new LocalMessage("controls", "hotbarslot").addReplacement("number", "7")), GROUP_HOTBAR);
        HOTBAR_SLOT_7 = Control.addControl(new Control(56, "hotbarslot7", new LocalMessage("controls", "hotbarslot").addReplacement("number", "8")), GROUP_HOTBAR);
        HOTBAR_SLOT_8 = Control.addControl(new Control(57, "hotbarslot8", new LocalMessage("controls", "hotbarslot").addReplacement("number", "9")), GROUP_HOTBAR);
        HOTBAR_SLOT_9 = Control.addControl(new Control(48, "hotbarslot9", new LocalMessage("controls", "hotbarslot").addReplacement("number", "10")), GROUP_HOTBAR);
        HOTBAR_SLOTS = new Control[]{HOTBAR_SLOT_0, HOTBAR_SLOT_1, HOTBAR_SLOT_2, HOTBAR_SLOT_3, HOTBAR_SLOT_4, HOTBAR_SLOT_5, HOTBAR_SLOT_6, HOTBAR_SLOT_7, HOTBAR_SLOT_8, HOTBAR_SLOT_9};
        ITEM_SET_0 = Control.addControl(new Control(-1, "itemset0", new LocalMessage("controls", "itemset").addReplacement("number", "1")), GROUP_ITEM_SETS);
        ITEM_SET_1 = Control.addControl(new Control(-1, "itemset1", new LocalMessage("controls", "itemset").addReplacement("number", "2")), GROUP_ITEM_SETS);
        ITEM_SET_2 = Control.addControl(new Control(-1, "itemset2", new LocalMessage("controls", "itemset").addReplacement("number", "3")), GROUP_ITEM_SETS);
        ITEM_SET_3 = Control.addControl(new Control(-1, "itemset3", new LocalMessage("controls", "itemset").addReplacement("number", "4")), GROUP_ITEM_SETS);
        ITEM_SETS = new Control[]{ITEM_SET_0, ITEM_SET_1, ITEM_SET_2, ITEM_SET_3};
        INV_LOCK = Control.addControl(new Control(342, "invlock", "invmods"), GROUP_KEYMODS);
        INV_QUICK_MOVE = Control.addControl(new Control(340, "invquickmove", "invmods"), GROUP_KEYMODS);
        INV_QUICK_TRASH = Control.addControl(new Control(341, "invtrash", "invmods"), GROUP_KEYMODS);
        INV_QUICK_DROP = Control.addControl(new Control(-1, "invdrop", "invmods"), GROUP_KEYMODS);
        CRAFT_10 = Control.addControl(new Control(340, "craft10", "craftmods"), GROUP_KEYMODS);
        CRAFT_ALL = Control.addControl(new Control(-1, "craftall", "craftmods"), GROUP_KEYMODS);
        inputManager = Platform.getInputManager();
        ControllerInput.init(inputManager);
        controlsLoaded = true;
    }

    public static void tickControlInputs(Input input, boolean isTyping, TickManager tickManager) {
        for (Control control : controls) {
            if (control == null) continue;
            boolean reset = true;
            if (!(control.getKey() == -1 || InputEvent.isKeyboardEvent(control.key) && isTyping)) {
                for (InputEvent event : input.getEvents()) {
                    if (event.getID() != control.getKey()) continue;
                    control.activate(event);
                    reset = false;
                }
            }
            if (!reset) continue;
            control.reset();
        }
        ControllerInput.tick(tickManager);
    }

    public static void resetControls() {
        for (Control control : controls) {
            control.reset();
            control.activateEvent = null;
        }
    }

    public static Iterable<Control> getControls() {
        return controls;
    }

    public static Stream<Control> streamControls() {
        return controls.stream();
    }

    public static Iterable<ControlGroup> getGroups() {
        return controlGroups;
    }

    public static Stream<ControlGroup> streamGroups() {
        return controlGroups.stream();
    }

    public static boolean isControlsLoaded() {
        return controlsLoaded;
    }

    private static <T extends Control> T addControl(T control, ControlGroup group) {
        if (controlsIDs.containsKey(control.id)) {
            throw new NullPointerException("ERROR: Conflicted control name: Could not add " + control.id);
        }
        controlsIDs.put(control.id, controls.size());
        controls.add(control);
        controlGroups.add(group);
        group.controls.add(control);
        return control;
    }

    public static <T extends Control> T addModControl(T control) {
        LoadedMod mod = LoadedMod.getRunningMod();
        if (mod == null) {
            throw new IllegalStateException("Cannot add controls when outside mod loading");
        }
        ControlGroup group = modGroups.compute(mod.id, (s, last) -> {
            if (last == null) {
                last = new ControlGroup(Integer.MAX_VALUE, new StaticMessage(mod.name));
            }
            return last;
        });
        return Control.addControl(control, group);
    }

    public static Control getControl(String id) {
        if (controlsIDs.containsKey(id)) {
            return controls.get(controlsIDs.get(id));
        }
        return null;
    }

    public static int getControlIconWidth(FontOptions fontOptions, String modifierKey, Control control, String keyName, String tooltip) {
        return Control.getDrawControlLogic((FontOptions)fontOptions, (int)0, (int)0, (String)modifierKey, (Control)control, (String)keyName, (String)tooltip).width;
    }

    public static void drawControlIcon(FontOptions fontOptions, int x, int y, String modifierKey, Control control, String keyName, String tooltip) {
        Control.getDrawControlLogic(fontOptions, x, y, modifierKey, control, keyName, tooltip).draw();
    }

    public static int getControlIconWidth(FontOptions fontOptions, String modifierKey, int inputKey, String keyName, String tooltip) {
        return Control.getDrawControlLogic((FontOptions)fontOptions, (int)0, (int)0, (String)modifierKey, (int)inputKey, (String)keyName, (String)tooltip).width;
    }

    public static void drawControlIcon(FontOptions fontOptions, int x, int y, String modifierKey, int inputKey, String keyName, String tooltip) {
        Control.getDrawControlLogic(fontOptions, x, y, modifierKey, inputKey, keyName, tooltip).draw();
    }

    public static DrawFlow getDrawControlLogic(FontOptions fontOptions, int x, int y, String modifierKey, Control control, String keyName, String tooltip) {
        return Control.getDrawControlLogic(fontOptions, x, y, modifierKey, control == null ? -1 : control.getKey(), keyName, tooltip);
    }

    public static DrawFlow getDrawControlLogic(FontOptions fontOptions, int x, int y, String modifierKey, int inputKey, String keyName, String tooltip) {
        int widthFlow = 0;
        ArrayList<Runnable> draw = new ArrayList<Runnable>();
        String tipPrefix = "";
        int iconDrawY = y + fontOptions.getSize() / 2 - 8;
        if (modifierKey != null && !modifierKey.isEmpty()) {
            widthFlow += Control.drawKey(x + widthFlow, iconDrawY, draw, modifierKey, fontOptions.getAlpha(), fontOptions.isPixelFont());
            int plusX = x + widthFlow;
            draw.add(() -> FontManager.bit.drawString(plusX, y + 2, "+", fontOptions));
            widthFlow += FontManager.bit.getWidthCeil("+", fontOptions) + 2;
        }
        if (inputKey < -1 && inputKey >= -100) {
            switch (inputKey) {
                case -100: {
                    widthFlow += Control.drawMouse(x + widthFlow, iconDrawY, draw, 0, fontOptions.getAlpha());
                    break;
                }
                case -99: {
                    widthFlow += Control.drawMouse(x + widthFlow, iconDrawY, draw, 1, fontOptions.getAlpha());
                    break;
                }
                case -98: {
                    widthFlow += Control.drawMouse(x + widthFlow, iconDrawY, draw, 2, fontOptions.getAlpha());
                    break;
                }
                default: {
                    widthFlow += Control.drawMouse(x + widthFlow, iconDrawY, draw, -1, fontOptions.getAlpha());
                    tipPrefix = " - ";
                    int keyX = x + widthFlow;
                    draw.add(() -> FontManager.bit.drawString(keyX, y + 2, keyName, fontOptions));
                    widthFlow += FontManager.bit.getWidthCeil(keyName, fontOptions);
                    break;
                }
            }
        } else {
            widthFlow += Control.drawKey(x + widthFlow, iconDrawY, draw, keyName, fontOptions.getAlpha(), fontOptions.isPixelFont());
        }
        if (tooltip != null && tooltip.length() > 0) {
            String tip = tipPrefix + tooltip;
            int tipX = x + widthFlow;
            draw.add(() -> FontManager.bit.drawString(tipX + 2, y + 2, tip, fontOptions));
            widthFlow += FontManager.bit.getWidthCeil(tip, fontOptions) + 4;
        }
        return new DrawFlow(widthFlow, draw);
    }

    private static int drawMouse(int x, int y, ArrayList<Runnable> draw, int mouse, float alpha) {
        GameTexture texture = Settings.UI.input;
        int drawX = x + 2;
        int drawY = y + 2;
        draw.add(() -> texture.initDraw().sprite(0, 0, 16).alpha(alpha).draw(drawX, drawY));
        if (mouse == 0) {
            draw.add(() -> texture.initDraw().sprite(1, 0, 16).color(1.0f, 0.2f, 0.2f, alpha).draw(drawX, drawY));
        } else {
            draw.add(() -> texture.initDraw().sprite(1, 0, 16).alpha(alpha).draw(drawX, drawY));
        }
        if (mouse == 1) {
            draw.add(() -> texture.initDraw().sprite(2, 0, 16).color(1.0f, 0.2f, 0.2f, alpha).draw(drawX, drawY));
        } else {
            draw.add(() -> texture.initDraw().sprite(2, 0, 16).alpha(alpha).draw(drawX, drawY));
        }
        if (mouse == 2) {
            draw.add(() -> texture.initDraw().sprite(3, 0, 16).color(1.0f, 0.2f, 0.2f, alpha).draw(drawX, drawY));
        } else {
            draw.add(() -> texture.initDraw().sprite(3, 0, 16).alpha(alpha).draw(drawX, drawY));
        }
        return 16;
    }

    private static int drawKey(int x, int y, ArrayList<Runnable> draw, String keyName, float alpha, boolean pixelFont) {
        int fontOffset;
        int fontSize;
        int drawX = x + 2;
        int drawY = y + 2;
        if (!pixelFont || keyName.chars().anyMatch(ch -> !CustomGameFont.fontTextureContains((char)ch))) {
            fontSize = 15;
            fontOffset = 1;
        } else {
            fontSize = 16;
            fontOffset = 0;
        }
        FontOptions fontOptions = new FontOptions(fontSize).colorf(0.15f, 0.15f, 0.15f, alpha);
        if (pixelFont) {
            fontOptions.forcePixelFont();
        } else {
            fontOptions.forceNonPixelFont();
        }
        int keyFontWidth = FontManager.bit.getWidthCeil(keyName, fontOptions);
        int keyWidth = keyName.length() <= 1 ? Math.max(keyFontWidth, 12) : keyFontWidth;
        GameTexture texture = Settings.UI.input;
        for (int i = 0; i < keyWidth; i += 16) {
            int finalI = i;
            if (i > keyWidth - 16) {
                draw.add(() -> texture.initDraw().spriteSection(1, 1, 16, 0, finalI == 0 ? keyWidth : keyWidth % finalI, 0, 16).color(1.0f, 1.0f, 1.0f, alpha).draw(drawX + finalI, drawY));
                continue;
            }
            draw.add(() -> texture.initDraw().sprite(1, 1, 16).color(1.0f, 1.0f, 1.0f, alpha).draw(drawX + finalI, drawY));
        }
        draw.add(() -> texture.initDraw().sprite(0, 1, 16).color(1.0f, 1.0f, 1.0f, alpha).draw(drawX - 16, drawY));
        draw.add(() -> texture.initDraw().sprite(2, 1, 16).color(1.0f, 1.0f, 1.0f, alpha).draw(drawX + keyWidth, drawY));
        int stringDrawX = drawX + keyWidth / 2 - keyFontWidth / 2;
        draw.add(() -> FontManager.bit.drawString(stringDrawX, drawY + fontOffset, keyName, fontOptions));
        return keyWidth + 8;
    }

    static {
        controls = new ArrayList();
        controlsIDs = new HashMap();
        controlGroups = new HashSet();
        modGroups = new HashMap();
    }

    public static class ControlGroup {
        public final int sort;
        public final GameMessage displayName;
        private final ArrayList<Control> controls = new ArrayList();

        public ControlGroup(int sort, GameMessage displayName) {
            this.sort = sort;
            this.displayName = displayName;
        }

        public Iterable<Control> getControls() {
            return this.controls;
        }
    }

    public static class DrawFlow {
        public final int width;
        public final ArrayList<Runnable> drawLogic;

        public DrawFlow(int width, ArrayList<Runnable> drawLogic) {
            this.width = width;
            this.drawLogic = drawLogic;
        }

        public void draw() {
            this.drawLogic.forEach(Runnable::run);
        }
    }
}

