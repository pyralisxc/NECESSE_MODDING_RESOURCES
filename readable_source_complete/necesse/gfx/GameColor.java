/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx;

import java.awt.Color;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import necesse.engine.util.GameMath;

public enum GameColor {
    NO_COLOR('0', (Supplier<Color>)null),
    WHITE('1', new Color(250, 250, 250)),
    BLACK('2', new Color(0, 0, 0)),
    LIGHT_GRAY('3', new Color(200, 200, 200)),
    DARK_GRAY('4', new Color(50, 50, 50)),
    GRAY('5', new Color(150, 150, 150)),
    RED('6', new Color(200, 50, 50)),
    GREEN('7', new Color(50, 200, 50)),
    BLUE('8', new Color(50, 50, 200)),
    YELLOW('9', new Color(200, 200, 50)),
    CYAN('a', new Color(50, 200, 200)),
    PURPLE('b', new Color(200, 50, 200)),
    ITEM_NORMAL('c', new Color(250, 250, 250)),
    ITEM_COMMON('d', new Color(25, 230, 27)),
    ITEM_UNCOMMON('e', new Color(65, 96, 224)),
    ITEM_RARE('f', new Color(229, 230, 25)),
    ITEM_EPIC('g', new Color(216, 39, 211)),
    ITEM_LEGENDARY('h', new Color(220, 60, 60)),
    ITEM_QUEST('i', new Color(242, 128, 13)),
    ITEM_UNIQUE('j', () -> {
        float minHue = 0.8472222f;
        float maxHue = 0.9444444f;
        float timeF = (float)(System.currentTimeMillis() % 3000L) / 3000.0f;
        float timeSin = GameMath.sin(timeF * 360.0f);
        return new Color(Color.HSBtoRGB(minHue + (maxHue - minHue) * timeSin, 1.0f, 1.0f));
    }),
    CUSTOM('#', s -> {
        if (s == null) {
            return () -> Color.WHITE;
        }
        Pattern p = Pattern.compile("[0-9a-fA-F]{6}");
        Matcher m = p.matcher((CharSequence)s);
        if (m.find() && m.start() == 0) {
            return () -> new Color(Integer.valueOf(s.substring(0, 2), 16), Integer.valueOf(s.substring(2, 4), 16), Integer.valueOf(s.substring(4, 6), 16));
        }
        p = Pattern.compile("[0-9a-fA-F]{3}");
        m = p.matcher((CharSequence)s);
        if (m.find() && m.start() == 0) {
            return () -> new Color(Integer.valueOf(s.substring(0, 1), 16) * 17, Integer.valueOf(s.substring(1, 2), 16) * 17, Integer.valueOf(s.substring(2, 3), 16) * 17);
        }
        return null;
    }),
    RAINBOW('!', () -> {
        long time = System.currentTimeMillis() % 2500L;
        return new Color(Color.HSBtoRGB((float)time / 2500.0f, 1.0f, 1.0f));
    });

    public static final Supplier<Color> DEFAULT_COLOR;
    public static final char CODE_PREFIX = '\u00a7';
    public static final String CODE_REGEX = "\u00a7([!0-9a-z]|#([0-9a-fA-F]{6}|[0-9a-fA-F]{3}))";
    public final char codeChar;
    public final Supplier<Color> color;
    private final Function<String, Supplier<Color>> colorParser;

    private GameColor(char codeChar, Color color) {
        this.codeChar = codeChar;
        this.colorParser = s -> () -> color;
        this.color = () -> color;
    }

    private GameColor(char codeChar, Function<String, Supplier<Color>> colorParser) {
        this.codeChar = codeChar;
        this.colorParser = colorParser;
        this.color = () -> (Color)((Supplier)colorParser.apply(null)).get();
    }

    private GameColor(char codeChar, Supplier<Color> colorSupplier) {
        this.codeChar = codeChar;
        this.color = colorSupplier;
        this.colorParser = s -> colorSupplier;
    }

    public String getColorCode() {
        return "\u00a7" + this.codeChar;
    }

    public int getID() {
        return this.ordinal();
    }

    public static GameColor getGameColor(int id) {
        GameColor[] colors = GameColor.values();
        if (id < 0 || id >= colors.length) {
            return WHITE;
        }
        return colors[id];
    }

    public static Supplier<Color> parseColorSupplierString(String str) {
        if (str.length() < 2) {
            return null;
        }
        if (str.charAt(0) == '\u00a7') {
            GameColor[] colors;
            char code = str.charAt(1);
            for (GameColor gc : colors = GameColor.values()) {
                if (gc.codeChar != code) continue;
                return gc.colorParser.apply(str.substring(2));
            }
        }
        return null;
    }

    public static String getCustomColorCode(Color color) {
        if (color == null) {
            return NO_COLOR.getColorCode();
        }
        StringBuilder red = new StringBuilder(Integer.toHexString(color.getRed()));
        while (red.length() < 2) {
            red.insert(0, "0");
        }
        StringBuilder green = new StringBuilder(Integer.toHexString(color.getGreen()));
        while (green.length() < 2) {
            green.insert(0, "0");
        }
        StringBuilder blue = new StringBuilder(Integer.toHexString(color.getBlue()));
        while (blue.length() < 2) {
            blue.insert(0, "0");
        }
        return "\u00a7#" + red + green + blue;
    }

    public static Color parseColorString(String str) {
        Supplier<Color> colorSupplier = GameColor.parseColorSupplierString(str);
        if (colorSupplier == null) {
            return null;
        }
        return colorSupplier.get();
    }

    public static GameColor getGameColor(char codeChar) {
        GameColor[] colors;
        for (GameColor gc : colors = GameColor.values()) {
            if (gc == null || gc.codeChar != codeChar) continue;
            return gc;
        }
        return null;
    }

    public static String stripCodes(String codeString) {
        return codeString.replaceAll(CODE_REGEX, "");
    }

    static {
        DEFAULT_COLOR = () -> Color.WHITE;
    }
}

