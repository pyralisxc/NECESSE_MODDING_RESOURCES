/*
 * Decompiled with CFR 0.152.
 */
package medievalsim.util;

public final class Constants {
    public static final String MOD_ID = "medieval.sim";
    public static final String MOD_NAME = "Medieval Sim";
    public static final String LOG_PREFIX = "MedievalSim: ";
    public static final String LOG_ERROR_PREFIX = "MedievalSim: ERROR - ";
    public static final String LOG_WARNING_PREFIX = "MedievalSim: WARNING - ";
    public static final String LOG_INFO_PREFIX = "MedievalSim: INFO - ";

    private Constants() {
    }

    public static final class CommandCenter {
        public static final int MIN_WIDTH = 400;
        public static final int MIN_HEIGHT = 300;
        public static final int DEFAULT_WIDTH = 600;
        public static final int DEFAULT_HEIGHT = 700;
        public static final int MAX_WIDTH = 800;
        public static final int MAX_HEIGHT = 900;
        public static final int RESIZE_EDGE_THRESHOLD = 5;
        public static final int RESIZE_CORNER_SIZE = 15;
        public static final int TAB_BAR_HEIGHT = 35;
        public static final int TAB_BUTTON_WIDTH = 150;
        public static final int TAB_SPACING = 5;
        public static final int HEADER_HEIGHT = 70;
        public static final int FAVORITES_HEIGHT = 40;
        public static final int COMMAND_INFO_HEIGHT = 60;
        public static final int ACTION_BAR_HEIGHT = 40;
        public static final int SCROLL_BAR_WIDTH = 12;
        public static final int MAX_FAVORITES = 10;
        public static final int FAVORITE_BUTTON_WIDTH = 55;
        public static final int FAVORITE_BUTTON_HEIGHT = 28;
        public static final int DROPDOWN_MAX_VISIBLE_ITEMS = 12;
        public static final int DROPDOWN_ITEM_HEIGHT = 20;
        public static final int PARAM_LABEL_WIDTH = 120;
        public static final int PARAM_WIDGET_WIDTH = 250;
        public static final int PARAM_ROW_HEIGHT = 35;
        public static final int PARAM_PADDING = 8;

        private CommandCenter() {
        }

        public static enum Tab {
            CONSOLE_COMMANDS,
            MOD_SETTINGS,
            COMMAND_HISTORY;

        }
    }

    public static final class UI {
        public static final int UNIT_SIZE = 8;
        public static final int MARGIN = 16;
        public static final int PADDING = 8;
        public static final int SECTION_SPACING = 24;
        public static final int ELEMENT_SPACING = 8;
        public static final int MIN_BUTTON_WIDTH = 160;
        public static final int STANDARD_BUTTON_WIDTH = 240;
        public static final int WIDE_BUTTON_WIDTH = 320;
        public static final int BUTTON_HEIGHT = 32;
        public static final int SMALL_BUTTON_HEIGHT = 24;
        public static final int SMALL_ICON_SIZE = 16;
        public static final int MEDIUM_ICON_SIZE = 20;
        public static final int STANDARD_ICON_SIZE = 24;
        public static final int LARGE_ICON_SIZE = 32;
        public static final int LABEL_HEIGHT = 24;
        public static final int INPUT_HEIGHT = 32;
        public static final int TEXT_PADDING = 8;
        public static final int DIALOG_MIN_WIDTH = 320;
        public static final int CONTENT_MAX_WIDTH = 480;

        private UI() {
        }
    }

    public static final class AdminTools {
        public static final int DEFAULT_SELECTION_RADIUS = 5;

        private AdminTools() {
        }
    }

    public static final class Network {
        public static final int DEFAULT_PACKET_TIMEOUT_MS = 5000;

        private Network() {
        }
    }

    public static final class Zones {
        public static final long DEFAULT_PVP_REENTRY_COOLDOWN_MS = 6000L;
        public static final float DEFAULT_PVP_SPAWN_IMMUNITY_SECONDS = 5.0f;
        public static final float DEFAULT_PVP_DAMAGE_MULTIPLIER = 0.05f;
        public static final int DEFAULT_COMBAT_LOCK_SECONDS = 10;
        public static final int DEFAULT_MAX_BARRIER_TILES = 10000;
        public static final int DEFAULT_BARRIER_BATCH_SIZE = 500;
        public static final int DEFAULT_BARRIER_MAX_TILES_PER_TICK = 1000;
        public static final int MAX_ZONE_NAME_LENGTH = 50;
        public static final int MIN_FORCE_CLEAN_RADIUS = 10;
        public static final int MAX_FORCE_CLEAN_RADIUS = 500;
        public static final int DEFAULT_FORCE_CLEAN_RADIUS = 50;

        private Zones() {
        }
    }

    public static final class BuildMode {
        public static final String GND_BUILD_MODE = "medievalsim_buildmode";
        public static final String GND_SHAPE = "medievalsim_shape";
        public static final String GND_HOLLOW = "medievalsim_isHollow";
        public static final String GND_LINE_LENGTH = "medievalsim_lineLength";
        public static final String GND_SQUARE_SIZE = "medievalsim_squareSize";
        public static final String GND_CIRCLE_RADIUS = "medievalsim_circleRadius";
        public static final String GND_SPACING = "medievalsim_spacing";
        public static final String GND_DIRECTION = "medievalsim_direction";
        public static final int MAIN_MENU_WIDTH = 300;
        public static final int MAIN_MENU_HEIGHT = 150;
        public static final int BUILD_TOOLS_WIDTH = 460;
        public static final int BUILD_TOOLS_HEIGHT = 480;
        public static final int UI_PADDING = 5;
        public static final int BUTTON_HEIGHT = 30;
        public static final int LABEL_HEIGHT = 20;
        public static final int SLIDER_HEIGHT = 30;
        public static final int MIN_LINE_LENGTH = 1;
        public static final int MAX_LINE_LENGTH = 50;
        public static final int DEFAULT_LINE_LENGTH = 5;
        public static final int MIN_SQUARE_SIZE = 1;
        public static final int MAX_SQUARE_SIZE = 25;
        public static final int DEFAULT_SQUARE_SIZE = 5;
        public static final int MIN_CIRCLE_RADIUS = 1;
        public static final int MAX_CIRCLE_RADIUS = 25;
        public static final int DEFAULT_CIRCLE_RADIUS = 5;
        public static final int MIN_SPACING = 1;
        public static final int MAX_SPACING = 10;
        public static final int DEFAULT_SPACING = 1;
        public static final int MAX_BLOCKS_PER_PLACEMENT = 1500;
        public static final int BUILD_MODE_PLACEMENT_RANGE = -1;
        public static final float PREVIEW_ALPHA = 0.5f;
        public static final int PREVIEW_PRIORITY = -100000;
        public static final int TOOLTIP_PRIORITY = Integer.MAX_VALUE;
        public static final int TOOLTIP_OFFSET_X = 20;
        public static final int TOOLTIP_OFFSET_Y = -10;
        public static final int TOOLTIP_PADDING = 4;
        public static final int TOOLTIP_FONT_SIZE = 16;
        public static final int TOOLTIP_BG_ALPHA = 180;
        public static final String LOC_CATEGORY_UI = "ui";
        public static final String LOC_BUILD_MODE_BLOCK_COST = "buildmodeblockcost";

        private BuildMode() {
        }
    }
}

