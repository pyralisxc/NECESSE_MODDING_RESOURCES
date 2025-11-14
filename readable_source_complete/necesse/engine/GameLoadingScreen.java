/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine;

import java.util.LinkedList;
import java.util.function.Supplier;
import necesse.engine.Settings;
import necesse.engine.ThreadFreezeMonitor;
import necesse.engine.platforms.Platform;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsStart;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameFont.GameFontHandler;
import necesse.gfx.gameFont.LoadGameFont;
import necesse.gfx.gameTexture.GameTexture;

public class GameLoadingScreen {
    public static GameFontHandler font;
    public static GameTexture logo;
    public static GameTexture keyArt;
    private static String main;
    private static String sub;
    private static final LinkedList<String> log;
    private static Runnable preDrawFunction;
    private static Runnable postDrawFunction;
    private static boolean isDone;
    private static final int maxFPS = 30;
    private static final double msPerFrame = 33.333333333333336;
    private static double drawValue;
    private static long lastDrawCallTime;
    private static int rejectedDrawCalls;
    private static int successfulDrawCalls;
    private static double missedDrawCalls;

    public static void initLoadingScreen(Runnable preDraw, Runnable postDraw) {
        if (preDrawFunction != null || postDrawFunction != null) {
            throw new IllegalStateException("Loading screen init cannot be done twice");
        }
        isDone = false;
        preDrawFunction = preDraw;
        postDrawFunction = postDraw;
        if (font == null) {
            font = new GameFontHandler();
            GameLoadingScreen.font.regularFonts.add(new LoadGameFont(), true);
        }
        lastDrawCallTime = System.currentTimeMillis();
        WindowManager.getWindow().setVSync(false);
    }

    public static void markDone() {
        preDrawFunction = null;
        postDrawFunction = null;
        isDone = true;
        WindowManager.getWindow().setVSync(Settings.vSyncEnabled);
    }

    public static void addLog(String string) {
        log.addFirst(string);
        GameLoadingScreen.draw(false);
    }

    public static void clearLog() {
        log.clear();
    }

    public static void drawLoadingString(String str) {
        main = str;
        sub = null;
        GameLoadingScreen.draw(true);
    }

    public static void drawLoadingSub(String str) {
        sub = str;
        GameLoadingScreen.draw(false);
    }

    public static void drawKeyArt(GameWindow window, float alpha) {
        if (keyArt != null) {
            TextureDrawOptionsStart drawOptions = keyArt.initDraw();
            drawOptions.shrinkHeight(window.getHudHeight(), false);
            if (drawOptions.getWidth() < window.getHudWidth()) {
                drawOptions.shrinkWidth(window.getHudWidth(), false);
            }
            drawOptions.alpha(alpha).posMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2).draw();
        }
    }

    public static boolean shouldDrawLogo(GameWindow window) {
        return window.getHudHeight() >= 630;
    }

    public static void drawLogo(GameWindow window, Supplier<Integer> getFormYPos) {
        if (logo == null) {
            return;
        }
        if (!GameLoadingScreen.shouldDrawLogo(window)) {
            return;
        }
        int preferredLogoY = window.getHudHeight() / 5;
        int logoWidth = 700;
        int formY = getFormYPos == null ? window.getHudHeight() : getFormYPos.get().intValue();
        TextureDrawOptionsEnd logoDrawOptions = logo.initDraw().shrinkWidth(logoWidth, false);
        preferredLogoY = Math.min(preferredLogoY, formY - logoDrawOptions.getHeight() / 2 + 50);
        preferredLogoY = Math.max(preferredLogoY, logoDrawOptions.getHeight() / 2 - 10);
        preferredLogoY = Math.min(preferredLogoY, formY - logoDrawOptions.getHeight() / 2 + 125);
        preferredLogoY = Math.max(preferredLogoY, logoDrawOptions.getHeight() / 2 - 40);
        logoDrawOptions.posMiddle(window.getHudWidth() / 2, preferredLogoY).draw();
    }

    public static void draw(boolean forceDraw) {
        if (isDone || Platform.getWindowManager() == null) {
            return;
        }
        ThreadFreezeMonitor.setLoading();
        long curMs = System.currentTimeMillis();
        drawValue += (double)(curMs - lastDrawCallTime) / 33.333333333333336;
        lastDrawCallTime = curMs;
        if (drawValue >= 1.0) {
            missedDrawCalls += (drawValue -= 1.0);
            drawValue = 0.0;
        } else if (!forceDraw) {
            ++rejectedDrawCalls;
            return;
        }
        preDrawFunction.run();
        GameWindow window = WindowManager.getWindow();
        GameLoadingScreen.drawKeyArt(window, 1.0f);
        GameLoadingScreen.drawLogo(window, () -> window.getHudWidth() / 2 - 20);
        int mainHeight = 0;
        if (main != null) {
            FontOptions options = new FontOptions(32).outline();
            mainHeight = font.getHeightCeil(main, options);
            GameLoadingScreen.drawMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2 - mainHeight / 2, main, options);
        }
        if (sub != null) {
            GameLoadingScreen.drawMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2 + mainHeight / 2 + 10, sub, new FontOptions(20).outline());
        }
        int logY = window.getHudHeight() - Math.min(5, log.size()) * 16;
        int i = 0;
        for (String logS : log) {
            font.drawString(5.0f, logY + i * 16, logS, new FontOptions(16).outline());
            if (++i <= 5) continue;
            break;
        }
        postDrawFunction.run();
        ++successfulDrawCalls;
    }

    private static void drawMiddle(int x, int y, String str, FontOptions options) {
        int width = font.getWidthCeil(str, options);
        font.drawString(x - width / 2, y, str, options);
    }

    static {
        log = new LinkedList();
        preDrawFunction = null;
        postDrawFunction = null;
    }
}

