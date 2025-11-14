/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.gameFont;

import java.io.IOException;
import java.util.function.Consumer;
import necesse.engine.GameLaunch;
import necesse.engine.GameLoadingScreen;
import necesse.engine.GameLog;
import necesse.engine.localization.Language;
import necesse.engine.localization.Localization;
import necesse.gfx.Renderer;
import necesse.gfx.gameFont.CustomGameFont;
import necesse.gfx.gameFont.GameFont;
import necesse.gfx.gameFont.GameFontHandler;
import necesse.gfx.gameFont.TrueTypeGameFont;
import necesse.gfx.gameFont.TrueTypeGameFontInfo;
import necesse.gfx.gameFont.TrueTypeGameFontOld;
import necesse.gfx.gameTexture.GameTexture;

public class FontManager {
    public static String additionalFontCharacters = "\u221e";
    public static GameFontHandler bit;
    private static TrueTypeGameFontInfo[] fontInfo;

    public static void loadFonts() {
        GameLoadingScreen.drawLoadingString("Loading fonts");
        FontManager.deleteFonts();
        bit = new GameFontHandler();
        try {
            fontInfo = new TrueTypeGameFontInfo[]{new TrueTypeGameFontInfo("base"), new TrueTypeGameFontInfo("japanese"), new TrueTypeGameFontInfo("korean"), new TrueTypeGameFontInfo("chinese"), new TrueTypeGameFontInfo("backup")};
            FontManager.bit.outlineFonts.add(FontManager.loadTrueTypeFont(12, 1, 3, -1, "bitoutline12", 8, 12, fontInfo), true);
            FontManager.bit.outlineFonts.add(FontManager.loadTrueTypeFont(16, 2, 2, -2, "bitoutline16", 11, 16, fontInfo), true);
            FontManager.bit.outlineFonts.add(FontManager.loadTrueTypeFont(20, 2, 1, -3, "bitoutline20", 13, 20, fontInfo), true);
            FontManager.bit.outlineFonts.add(new TrueTypeGameFont(32, 2, 0, -3, null, fontInfo), false);
            FontManager.bit.regularFonts.add(FontManager.loadTrueTypeFont(12, 0, 2, -1, "bit12", 8, 12, fontInfo), true);
            FontManager.bit.regularFonts.add(FontManager.loadTrueTypeFont(16, 0, 2, -2, "bit16", 11, 16, fontInfo), true);
            FontManager.bit.regularFonts.add(FontManager.loadTrueTypeFont(20, 0, 1, -3, "bit20", 13, 20, fontInfo), true);
            FontManager.bit.regularFonts.add(new TrueTypeGameFont(32, 0, 0, -3, null, fontInfo), false);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        Consumer<GameFont> charOffsets = font -> {
            if (font instanceof TrueTypeGameFont) {
                TrueTypeGameFont trueTypeFont = (TrueTypeGameFont)font;
                trueTypeFont.addCharOffset('_', 0.0f, (float)(-trueTypeFont.fontSize) / 8.0f);
                trueTypeFont.addCharOffset('-', 0.0f, (float)(-trueTypeFont.fontSize) / 16.0f);
                trueTypeFont.addCharOffset("+=<>~", 0.0f, (float)(-trueTypeFont.fontSize) / 10.0f);
                trueTypeFont.addCharOffset("({[]})|", 0.0f, (float)(-trueTypeFont.fontSize) / 12.0f);
            }
        };
        FontManager.bit.outlineFonts.forEachFont(charOffsets);
        FontManager.bit.regularFonts.forEachFont(charOffsets);
        long time = System.currentTimeMillis();
        if (FontManager.updateFont(Localization.getCurrentLang())) {
            GameLog.debug.println("Initial font update took " + (System.currentTimeMillis() - time) + " ms");
        }
        GameLoadingScreen.font = bit;
    }

    public static boolean updateFont(Language currentLang) {
        StringBuilder fontUpdate = new StringBuilder();
        if (currentLang != Localization.defaultLang) {
            fontUpdate.append(Localization.getCurrentLang().getCharactersUsed());
        }
        for (Language language : Localization.getLanguages()) {
            if (language.isDebugOnly() || language == Localization.defaultLang) continue;
            fontUpdate.append(language.localDisplayName);
            fontUpdate.append(language.credits);
        }
        fontUpdate.append(additionalFontCharacters);
        String updateString = fontUpdate.toString();
        if (!updateString.isEmpty()) {
            bit.updateFont(updateString);
            return true;
        }
        return false;
    }

    public static void deleteFonts() {
        if (bit != null) {
            bit.deleteFonts();
        }
        if (fontInfo != null) {
            for (TrueTypeGameFontInfo info : fontInfo) {
                info.dispose();
            }
        }
        bit = null;
    }

    public static boolean isLoaded() {
        return bit != null;
    }

    private static TrueTypeGameFontOld loadTrueTypeFont(String ttfPath, int fontSize, boolean antiAlias, boolean addOutline, int addedSize, int drawOffset, String customFontTexturePath, int customFontWidth, int customFontHeight) {
        CustomGameFont.CharArray customChars = null;
        if (!GameLaunch.launchOptions.containsKey("altfont")) {
            customChars = new CustomGameFont.CharArray(GameTexture.fromFile("fonts/" + customFontTexturePath), customFontWidth, customFontHeight);
        }
        return new TrueTypeGameFontOld(ttfPath, fontSize, antiAlias, addOutline, addedSize, drawOffset, customChars);
    }

    private static TrueTypeGameFont loadTrueTypeFont(int fontSize, int strokeSize, int addedFontSize, int yDrawOffset, String customFontTexturePath, int customFontWidth, int customFontHeight, TrueTypeGameFontInfo ... fonts) {
        GameTexture texture = GameTexture.fromFile("fonts/" + customFontTexturePath);
        Renderer.queryGLError("postFontTexture");
        CustomGameFont.CharArray customChars = new CustomGameFont.CharArray(texture, customFontWidth, customFontHeight);
        return new TrueTypeGameFont(fontSize, strokeSize, addedFontSize, yDrawOffset, customChars, fonts);
    }
}

