/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.input.controller;

import java.util.HashMap;
import necesse.gfx.gameTexture.GameTexture;

public class ControllerGlyphCollections {
    public static final GlyphCollection GAMEPAD_BUTTON_A = new GlyphCollection("switch_button_a", "playstation_button_color_cross", "playstation_button_color_cross", "steamdeck_button_a", "xbox_button_color_a");
    public static final GlyphCollection GAMEPAD_BUTTON_B = new GlyphCollection("switch_button_b", "playstation_button_color_circle", "playstation_button_color_circle", "steamdeck_button_b", "xbox_button_color_b");
    public static final GlyphCollection GAMEPAD_BUTTON_X = new GlyphCollection("switch_button_x", "playstation_button_color_square", "playstation_button_color_square", "steamdeck_button_x", "xbox_button_color_x");
    public static final GlyphCollection GAMEPAD_BUTTON_Y = new GlyphCollection("switch_button_y", "playstation_button_color_triangle", "playstation_button_color_triangle", "steamdeck_button_y", "xbox_button_color_y");
    public static final GlyphCollection GAMEPAD_BUTTON_LEFT_BUMPER = new GlyphCollection("switch_button_l", "playstation_trigger_l1_alternative", "playstation_trigger_l1_alternative", "steamdeck_button_l1", "xbox_lb");
    public static final GlyphCollection GAMEPAD_BUTTON_RIGHT_BUMPER = new GlyphCollection("switch_button_r", "playstation_trigger_r1_alternative", "playstation_trigger_r1_alternative", "steamdeck_button_l2", "xbox_rb");
    public static final GlyphCollection GAMEPAD_BUTTON_BACK = new GlyphCollection("", "playstation4_button_share", "playstation5_button_create", "", "xbox_button_view");
    public static final GlyphCollection GAMEPAD_BUTTON_START = new GlyphCollection("", "playstation4_button_options", "playstation5_button_options", "", "xbox_button_menu");
    public static final GlyphCollection GAMEPAD_BUTTON_GUIDE = new GlyphCollection("switch_button_home", "keyboard_home", "keyboard_home", "steamdeck_button_guide", "xbox_guide");
    public static final GlyphCollection GAMEPAD_BUTTON_LEFT_THUMB = new GlyphCollection("switch_stick_l_press", "playstation_stick_l_press", "playstation_stick_l_press", "steamdeck_stick_l_press", "xbox_stick_l_press");
    public static final GlyphCollection GAMEPAD_BUTTON_RIGHT_THUMB = new GlyphCollection("switch_stick_r_press", "playstation_stick_r_press", "playstation_stick_r_press", "steamdeck_stick_r_press", "xbox_stick_r_press");
    public static final GlyphCollection GAMEPAD_BUTTON_DPAD_UP = new GlyphCollection("switch_dpad_up", "playstation_dpad_up", "playstation_dpad_up", "steamdeck_dpad_up", "xbox_dpad_round_up");
    public static final GlyphCollection GAMEPAD_BUTTON_DPAD_RIGHT = new GlyphCollection("switch_dpad_right", "playstation_dpad_right", "playstation_dpad_right", "steamdeck_dpad_right", "xbox_dpad_round_right");
    public static final GlyphCollection GAMEPAD_BUTTON_DPAD_DOWN = new GlyphCollection("switch_dpad_down", "playstation_dpad_down", "playstation_dpad_down", "steamdeck_dpad_down", "xbox_dpad_round_down");
    public static final GlyphCollection GAMEPAD_BUTTON_DPAD_LEFT = new GlyphCollection("switch_dpad_left", "playstation_dpad_left", "playstation_dpad_left", "steamdeck_dpad_left", "xbox_dpad_round_left");
    public static final GlyphCollection GAMEPAD_AXIS_LEFT_X = new GlyphCollection("switch_stick_l_horizontal", "playstation_stick_l_horizontal", "playstation_stick_l_horizontal", "steamdeck_stick_l_horizontal", "xbox_stick_l_horizontal");
    public static final GlyphCollection GAMEPAD_AXIS_LEFT_Y = new GlyphCollection("switch_stick_l_vertical", "playstation_stick_l_vertical", "playstation_stick_l_vertical", "steamdeck_stick_l_vertical", "xbox_stick_l_vertical");
    public static final GlyphCollection GAMEPAD_AXIS_RIGHT_X = new GlyphCollection("switch_stick_r_horizontal", "playstation_stick_r_horizontal", "playstation_stick_r_horizontal", "steamdeck_stick_r_horizontal", "xbox_stick_r_horizontal");
    public static final GlyphCollection GAMEPAD_AXIS_RIGHT_Y = new GlyphCollection("switch_stick_r_vertical", "playstation_stick_r_vertical", "playstation_stick_r_vertical", "steamdeck_stick_r_vertical", "xbox_stick_r_vertical");
    public static final GlyphCollection GAMEPAD_AXIS_LEFT_TRIGGER = new GlyphCollection("switch_button_zl", "playstation_trigger_l2", "playstation_trigger_l2", "steamdeck_button_l2", "xbox_lt");
    public static final GlyphCollection GAMEPAD_AXIS_RIGHT_TRIGGER = new GlyphCollection("switch_button_zr", "playstation_trigger_r2", "playstation_trigger_r2", "steamdeck_button_r2", "xbox_rt");
    public static final GlyphCollection GAMEPAD_AXIS_LEFT = new GlyphCollection("switch_stick_l", "playstation_stick_l", "playstation_stick_l", "steamdeck_stick_l", "xbox_stick_l");
    public static final GlyphCollection GAMEPAD_AXIS_RIGHT = new GlyphCollection("switch_stick_r", "playstation_stick_r", "playstation_stick_r", "steamdeck_stick_r", "xbox_stick_r");
    public static final Flair FLAIR_0 = new Flair("ui/input/Flairs/flair_number_0_outline");
    public static final Flair FLAIR_1 = new Flair("ui/input/Flairs/flair_number_1_outline");
    public static final Flair FLAIR_2 = new Flair("ui/input/Flairs/flair_number_2_outline");
    public static final Flair FLAIR_3 = new Flair("ui/input/Flairs/flair_number_3_outline");
    public static final Flair FLAIR_4 = new Flair("ui/input/Flairs/flair_number_4_outline");
    public static final Flair FLAIR_5 = new Flair("ui/input/Flairs/flair_number_5_outline");
    public static final Flair FLAIR_6 = new Flair("ui/input/Flairs/flair_number_6_outline");
    public static final Flair FLAIR_7 = new Flair("ui/input/Flairs/flair_number_7_outline");
    public static final Flair FLAIR_8 = new Flair("ui/input/Flairs/flair_number_8_outline");
    public static final Flair FLAIR_9 = new Flair("ui/input/Flairs/flair_number_9_outline");
    public static final Flair FLAIR_CONTROLLER_DISCONNECTED = new Flair("ui/input/Flairs/controller_disconnected");
    public static final HashMap<Integer, Glyph> PLAYSTATION4_NON_GAMEPAD_GLYPHS = new HashMap();
    public static final HashMap<Integer, Glyph> PLAYSTATION5_NON_GAMEPAD_GLYPHS = new HashMap();
    public static final HashMap<Integer, Glyph> XBOXGENERIC = new HashMap();
    public static final HashMap<Integer, Glyph> XBOXLINUXWIRELESS = new HashMap();
    public static final HashMap<Integer, Glyph> XBOXLINUXWIRED = new HashMap();

    static {
        PLAYSTATION4_NON_GAMEPAD_GLYPHS.put(6, new Glyph("ui/input/PlayStation Series/playstation_trigger_l2"));
        PLAYSTATION4_NON_GAMEPAD_GLYPHS.put(7, new Glyph("ui/input/PlayStation Series/playstation_trigger_r2"));
        PLAYSTATION4_NON_GAMEPAD_GLYPHS.put(13, new Glyph("ui/input/PlayStation Series/playstation4_touchpad"));
        PLAYSTATION5_NON_GAMEPAD_GLYPHS.put(6, new Glyph("ui/input/PlayStation Series/playstation_trigger_l2"));
        PLAYSTATION5_NON_GAMEPAD_GLYPHS.put(7, new Glyph("ui/input/PlayStation Series/playstation_trigger_r2"));
        PLAYSTATION5_NON_GAMEPAD_GLYPHS.put(13, new Glyph("ui/input/PlayStation Series/playstation5_touchpad"));
        PLAYSTATION5_NON_GAMEPAD_GLYPHS.put(14, new Glyph("ui/input/PlayStation Series/playstation5_button_mute"));
        XBOXGENERIC.put(100, new Glyph("ui/input/Xbox Series/xbox_stick_l"));
        XBOXGENERIC.put(102, new Glyph("ui/input/Xbox Series/xbox_stick_r"));
        XBOXGENERIC.put(104, new Glyph("ui/input/Xbox Series/xbox_rt"));
        XBOXGENERIC.put(105, new Glyph("ui/input/Xbox Series/xbox_lt"));
        XBOXGENERIC.put(4, new Glyph("ui/input/Xbox Series/xbox_button_color_y"));
        XBOXGENERIC.put(1, new Glyph("ui/input/Xbox Series/xbox_button_color_b"));
        XBOXGENERIC.put(16, new Glyph("ui/input/Xbox Series/xbox_dpad_round_up"));
        XBOXGENERIC.put(18, new Glyph("ui/input/Xbox Series/xbox_dpad_round_down"));
        XBOXGENERIC.put(17, new Glyph("ui/input/Xbox Series/xbox_dpad_round_right"));
        XBOXGENERIC.put(3, new Glyph("ui/input/Xbox Series/xbox_button_color_x"));
        XBOXGENERIC.put(0, new Glyph("ui/input/Xbox Series/xbox_button_color_a"));
        XBOXGENERIC.put(19, new Glyph("ui/input/Xbox Series/xbox_dpad_round_left"));
        XBOXGENERIC.put(11, new Glyph("ui/input/Xbox Series/xbox_button_menu"));
        XBOXGENERIC.put(10, new Glyph("ui/input/Xbox Series/xbox_button_view"));
        XBOXGENERIC.put(7, new Glyph("ui/input/Xbox Series/xbox_rb"));
        XBOXGENERIC.put(6, new Glyph("ui/input/Xbox Series/xbox_lb"));
        XBOXGENERIC.put(13, new Glyph("ui/input/Xbox Series/xbox_stick_l_press"));
        XBOXGENERIC.put(14, new Glyph("ui/input/Xbox Series/xbox_stick_r_press"));
        XBOXGENERIC.put(16, new Glyph("ui/input/Xbox Series/xbox_dpad_round_up"));
        XBOXGENERIC.put(17, new Glyph("ui/input/Xbox Series/xbox_dpad_round_right"));
        XBOXGENERIC.put(18, new Glyph("ui/input/Xbox Series/xbox_dpad_round_down"));
        XBOXGENERIC.put(19, new Glyph("ui/input/Xbox Series/xbox_dpad_round_left"));
        XBOXGENERIC.put(0, new Glyph("ui/input/Xbox Series/xbox_button_color_a"));
        XBOXGENERIC.put(1, new Glyph("ui/input/Xbox Series/xbox_button_color_b"));
        XBOXGENERIC.put(7, new Glyph("ui/input/Xbox Series/xbox_rb"));
        XBOXGENERIC.put(6, new Glyph("ui/input/Xbox Series/xbox_lb"));
        XBOXGENERIC.put(10, new Glyph("ui/input/Xbox Series/xbox_button_view"));
        XBOXGENERIC.put(3, new Glyph("ui/input/Xbox Series/xbox_button_color_x"));
        XBOXLINUXWIRELESS.putAll(XBOXGENERIC);
        XBOXLINUXWIRELESS.put(15, new Glyph("ui/input/Xbox Series/xbox_dpad_round_up"));
        XBOXLINUXWIRELESS.put(17, new Glyph("ui/input/Xbox Series/xbox_dpad_round_down"));
        XBOXLINUXWIRELESS.put(16, new Glyph("ui/input/Xbox Series/xbox_dpad_round_right"));
        XBOXLINUXWIRELESS.put(18, new Glyph("ui/input/Xbox Series/xbox_dpad_round_left"));
        XBOXLINUXWIRELESS.put(15, new Glyph("ui/input/Xbox Series/xbox_dpad_round_up"));
        XBOXLINUXWIRELESS.put(16, new Glyph("ui/input/Xbox Series/xbox_dpad_round_right"));
        XBOXLINUXWIRELESS.put(17, new Glyph("ui/input/Xbox Series/xbox_dpad_round_down"));
        XBOXLINUXWIRELESS.put(18, new Glyph("ui/input/Xbox Series/xbox_dpad_round_left"));
        XBOXLINUXWIRED.putAll(XBOXGENERIC);
        XBOXLINUXWIRED.put(103, new Glyph("ui/input/Xbox Series/xbox_stick_r"));
        XBOXLINUXWIRED.put(105, new Glyph("ui/input/Xbox Series/xbox_rt"));
        XBOXLINUXWIRED.put(102, new Glyph("ui/input/Xbox Series/xbox_lt"));
        XBOXLINUXWIRED.put(3, new Glyph("ui/input/Xbox Series/xbox_button_color_y"));
        XBOXLINUXWIRED.put(11, new Glyph("ui/input/Xbox Series/xbox_dpad_round_up"));
        XBOXLINUXWIRED.put(13, new Glyph("ui/input/Xbox Series/xbox_dpad_round_down"));
        XBOXLINUXWIRED.put(12, new Glyph("ui/input/Xbox Series/xbox_dpad_round_right"));
        XBOXLINUXWIRED.put(2, new Glyph("ui/input/Xbox Series/xbox_button_color_x"));
        XBOXLINUXWIRED.put(14, new Glyph("ui/input/Xbox Series/xbox_dpad_round_left"));
        XBOXLINUXWIRED.put(7, new Glyph("ui/input/Xbox Series/xbox_button_menu"));
        XBOXLINUXWIRED.put(6, new Glyph("ui/input/Xbox Series/xbox_button_view"));
        XBOXLINUXWIRED.put(5, new Glyph("ui/input/Xbox Series/xbox_rb"));
        XBOXLINUXWIRED.put(4, new Glyph("ui/input/Xbox Series/xbox_lb"));
        XBOXLINUXWIRED.put(9, new Glyph("ui/input/Xbox Series/xbox_stick_l_press"));
        XBOXLINUXWIRED.put(10, new Glyph("ui/input/Xbox Series/xbox_stick_r_press"));
        XBOXLINUXWIRED.put(11, new Glyph("ui/input/Xbox Series/xbox_dpad_round_up"));
        XBOXLINUXWIRED.put(12, new Glyph("ui/input/Xbox Series/xbox_dpad_round_right"));
        XBOXLINUXWIRED.put(13, new Glyph("ui/input/Xbox Series/xbox_dpad_round_down"));
        XBOXLINUXWIRED.put(14, new Glyph("ui/input/Xbox Series/xbox_dpad_round_left"));
        XBOXLINUXWIRED.put(5, new Glyph("ui/input/Xbox Series/xbox_rb"));
        XBOXLINUXWIRED.put(4, new Glyph("ui/input/Xbox Series/xbox_lb"));
        XBOXLINUXWIRED.put(6, new Glyph("ui/input/Xbox Series/xbox_button_view"));
        XBOXLINUXWIRED.put(2, new Glyph("ui/input/Xbox Series/xbox_button_color_x"));
    }

    public static class GlyphCollection {
        public final Glyph nintendoSwitch;
        public final Glyph playStation4;
        public final Glyph playStation5;
        public final Glyph steamDeck;
        public final Glyph xbox;

        private GlyphCollection(String nintendoSwitch, String playStation4, String playStation5, String steamDeck, String xbox) {
            this.nintendoSwitch = new Glyph("ui/input/Nintendo Switch/" + nintendoSwitch);
            this.playStation4 = new Glyph("ui/input/PlayStation Series/" + playStation4);
            this.playStation5 = new Glyph("ui/input/PlayStation Series/" + playStation5);
            this.steamDeck = new Glyph("ui/input/Steam Deck/" + steamDeck);
            this.xbox = new Glyph("ui/input/Xbox Series/" + xbox);
        }
    }

    public static class Flair {
        private final String path;
        private GameTexture texture = null;

        public Flair(String path) {
            this.path = path;
        }

        public GameTexture getTexture() {
            if (this.texture != null) {
                return this.texture;
            }
            this.texture = GameTexture.fromFile(this.path).croppedToNonTransparent(false);
            return this.texture;
        }
    }

    public static class Glyph {
        private final String path;
        private GameTexture texture = null;

        public Glyph(String path) {
            this.path = path;
        }

        public GameTexture getTexture() {
            if (this.texture != null) {
                return this.texture;
            }
            this.texture = GameTexture.fromFile(this.path).croppedToNonTransparent(true);
            return this.texture;
        }
    }
}

