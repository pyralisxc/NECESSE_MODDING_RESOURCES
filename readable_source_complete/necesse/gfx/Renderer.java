/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.BufferUtils
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL20
 *  org.lwjgl.stb.STBImageWrite
 *  org.lwjgl.system.Platform
 */
package necesse.gfx;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.io.File;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import necesse.engine.GameInfo;
import necesse.engine.GameLoadingScreen;
import necesse.engine.GameLog;
import necesse.engine.GlobalData;
import necesse.engine.MouseDraggingElement;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerAverage;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Input;
import necesse.engine.input.InputPosition;
import necesse.engine.input.controller.ControllerGlyphTip;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.network.client.Client;
import necesse.engine.platforms.PlatformManager;
import necesse.engine.screenHudManager.ScreenHudManager;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.engine.util.HashMapSet;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameColor;
import necesse.gfx.GameResources;
import necesse.gfx.TableContentDraw;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.fairType.FairCharacterGlyph;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.components.chat.ChatMessage;
import necesse.gfx.forms.components.chat.ChatMessageList;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameFrameBuffer;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.shader.GameShader;
import necesse.level.maps.Level;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.system.Platform;

public class Renderer {
    private static LinkedList<GameShader> shaderHistory;
    private static final AtomicBoolean screenshotCooldown;
    private static long screenshotTime;
    private static final long screenshotEffectTime = 350L;
    private static MouseDraggingElement mouseDraggingElement;
    public static ScreenHudManager hudManager;
    private static final HashMapSet<String, Integer> glErrorPrints;
    public static int cursorSizeOffset;
    public static float[] cursorSizes;
    private static boolean changedCursor;
    private static boolean forceCursorChange;
    private static GameWindow.CURSOR currentCursor;
    private static int currentCursorSize;
    private static GameWindow.CURSOR nextCursor;

    public static float getCursorSizeZoom(int size) {
        return cursorSizes[GameMath.limit(size + cursorSizeOffset, 0, cursorSizes.length - 1)];
    }

    public static void initialize() {
        shaderHistory = new LinkedList();
    }

    public static void preGameTick(TickManager tickManager) {
        hudManager.preGameTick(tickManager);
    }

    public static void drawTick(TickManager tickManager) {
        Performance.recordConstant((PerformanceTimerManager)tickManager, "drawTime", () -> {
            GameTexture cursorTexture;
            GameWindow.CURSOR cursor;
            boolean drawUI = !Settings.reduceUIFramerate || tickManager.isGameTick();
            GameWindow window = WindowManager.getWindow();
            if (tickManager.isBehind() && TickManager.skipDrawIfBehind) {
                return;
            }
            Renderer.clearShaderHistory();
            GL11.glClear((int)16640);
            Performance.record((PerformanceTimerManager)tickManager, "scene", () -> {
                try {
                    window.startSceneDraw();
                    GlobalData.getCurrentState().drawScene(tickManager, window.wasResizedThisFrame());
                }
                finally {
                    window.endSceneDraw();
                }
            });
            Performance.record((PerformanceTimerManager)tickManager, "sceneOverlay", () -> {
                try {
                    window.startSceneOverlayDraw();
                    GlobalData.getCurrentState().drawSceneOverlay(tickManager);
                    GameTooltipManager.drawControllerInputTooltips();
                }
                finally {
                    window.endSceneOverlayDraw();
                }
            });
            Performance.record((PerformanceTimerManager)tickManager, "postProcess", () -> window.renderScene(null));
            Performance.record((PerformanceTimerManager)tickManager, "hudDraw", () -> {
                if (drawUI) {
                    try {
                        window.startHudDraw();
                        Renderer.drawDebug(tickManager);
                        GlobalData.getCurrentState().drawHud(tickManager);
                        boolean drewDraggingElement = false;
                        MouseDraggingElement draggingElement = mouseDraggingElement;
                        if (draggingElement != null) {
                            if (!draggingElement.isKeyDown(window.getInput())) {
                                mouseDraggingElement = null;
                            } else {
                                drewDraggingElement = draggingElement.draw(window.mousePos().hudX, window.mousePos().hudY);
                            }
                        }
                        hudManager.cleanUp();
                        if (!Settings.hideUI) {
                            if (!drewDraggingElement && !Settings.hideCursor) {
                                GameTooltipManager.drawHudTooltips();
                            }
                            hudManager.draw(tickManager);
                        }
                        Renderer.drawScreenshotEffect();
                    }
                    finally {
                        window.endHudDraw();
                    }
                }
            });
            window.renderHud(hudManager.getHudAlpha());
            if (drawUI) {
                if (!changedCursor) {
                    Renderer.changeCursor(GameWindow.CURSOR.DEFAULT, window);
                } else {
                    Renderer.changeCursor(nextCursor, window);
                }
                changedCursor = false;
            }
            if (Settings.drawCursorManually && PlatformManager.getPlatform().isSteamDeck() && ControllerInput.isCursorVisible() && (cursor = window.getCursor()) != null && (cursorTexture = cursor.getTexture()) != null) {
                window.getInput().updateNextMousePos();
                InputPosition position = window.mousePos();
                float size = Renderer.getCursorSizeZoom(Settings.cursorSize);
                float xOffset = (float)(cursor.xOffset - 1) * size;
                float yOffset = (float)(cursor.yOffset - 1) * size;
                cursorTexture.initDraw().size((int)(size * 20.0f), (int)(size * 20.0f)).draw((int)((float)position.windowX + xOffset), (int)((float)position.windowY + yOffset));
            }
            window.update();
        });
    }

    private static void drawDebug(TickManager tickManager) {
        GameWindow window = WindowManager.getWindow();
        int rightDrawY = window.getHudHeight() - 2 - (Input.lastInputIsController && Settings.showControlTips ? ControllerGlyphTip.getHeight() + 4 : 0);
        if (!Settings.hideUI && (Settings.showDebugInfo || GlobalData.debugActive())) {
            FontManager.bit.drawString(10.0f, 10.0f, "FPS, TPS: " + tickManager.getFPS() + " (" + GameMath.toDecimals(tickManager.getFullDelta(), 2) + "), " + tickManager.getTPS() + " (" + tickManager.getTick() + ")", new FontOptions(16));
            PerformanceTimerAverage previousAverage = tickManager.getPreviousAverage();
            int leftDrawY = window.getHudHeight() - 10;
            if (previousAverage != null) {
                Collection values = previousAverage.getChildren().values();
                TableContentDraw tableDraw = new TableContentDraw();
                FontOptions timerFontOptions = new FontOptions(16);
                for (PerformanceTimerAverage t : values) {
                    tableDraw.newRow().addTextColumn(t.name, timerFontOptions, 10, 0).addTextColumn(GameMath.toDecimals(t.getAverageTimePercent(), 3) + "%", timerFontOptions, 20, 0).addTextColumn(GameUtils.getTimeStringNano(t.getAverageTime()), timerFontOptions, 20, 0);
                }
                tableDraw.newRow().addTextColumn("Loop", timerFontOptions).addTextColumn(GameUtils.getTimeStringNano(previousAverage.getAverageTime()), timerFontOptions);
                tableDraw.draw(10, leftDrawY -= tableDraw.getHeight());
            }
            FontOptions subFontOptions = new FontOptions(12);
            String infoString = Platform.get().getName() + ", build " + PlatformManager.getPlatform().getPlatformAppBuild();
            FontManager.bit.drawString(window.getHudWidth() - 4 - FontManager.bit.getWidthCeil(infoString, subFontOptions), rightDrawY -= 12, infoString, subFontOptions);
            if (ModLoader.getEnabledMods().size() > 0) {
                String modsString = ModLoader.getEnabledMods().size() + " mod(s) loaded";
                FontManager.bit.drawString(window.getHudWidth() - 4 - FontManager.bit.getWidthCeil(modsString, subFontOptions), rightDrawY -= 12, modsString, subFontOptions);
            }
            String versionString = "v. 1.0.1";
            FontOptions versionFontOptions = new FontOptions(20);
            FontManager.bit.drawString(window.getHudWidth() - 4 - FontManager.bit.getWidthCeil(versionString, versionFontOptions), rightDrawY -= 20, versionString, versionFontOptions);
        }
        if (GameInfo.bottomRightInfo != null) {
            FontOptions infoFontOptions = new FontOptions(20).color(new Color(255, 255, 255, 150));
            FontManager.bit.drawString(window.getHudWidth() - 12 - FontManager.bit.getWidthCeil(GameInfo.bottomRightInfo, infoFontOptions), rightDrawY -= 28, GameInfo.bottomRightInfo, infoFontOptions);
        }
    }

    public static void setCursor(GameWindow.CURSOR cursor) {
        nextCursor = cursor;
        changedCursor = true;
    }

    public static void reapplyCursor(GameWindow window) {
        forceCursorChange = true;
        if (currentCursor != null) {
            Renderer.changeCursor(currentCursor, window);
        }
    }

    private static void changeCursor(GameWindow.CURSOR cursor, GameWindow window) {
        if (!forceCursorChange && currentCursor == cursor && currentCursorSize == Settings.cursorSize) {
            return;
        }
        currentCursor = cursor;
        currentCursorSize = Settings.cursorSize;
        window.setCursor(cursor);
    }

    public static void setCursorColor(Color color) {
        Settings.cursorColor = color;
        GameResources.loadCursors();
        Renderer.setCursor(currentCursor);
        forceCursorChange = true;
    }

    public static void setCursorSize(int size) {
        Settings.cursorSize = size;
        GameResources.loadCursors();
        Renderer.setCursor(currentCursor);
        forceCursorChange = true;
    }

    public static GameTexture getQuadTexture() {
        return GameResources.empty;
    }

    public static TextureDrawOptionsEnd initQuadDraw(int width, int height) {
        return Renderer.getQuadTexture().initDraw().size(width, height);
    }

    private static void drawScreenshotEffect() {
        long delta = System.currentTimeMillis() - screenshotTime;
        if (delta < 350L) {
            float progress = (float)delta / 350.0f;
            float floatHeight = progress > 0.5f ? Math.abs(progress - 1.0f) : progress;
            GameWindow window = WindowManager.getWindow();
            int effectHeight = (int)((float)window.getHudHeight() * Math.abs(floatHeight - 0.5f));
            Renderer.initQuadDraw(window.getHudWidth(), window.getHudHeight() / 2).color(Color.BLACK).draw(0, -effectHeight);
            Renderer.initQuadDraw(window.getHudWidth(), window.getHudHeight() / 2).color(Color.BLACK).draw(0, window.getHudHeight() / 2 + effectHeight);
        }
    }

    public static void drawLineAdv(int x1, int y1, int x2, int y2, float[] advCol) {
        GameResources.empty.bindTexture();
        GL11.glLoadIdentity();
        GL11.glBegin((int)1);
        GL11.glColor4f((float)advCol[0], (float)advCol[1], (float)advCol[2], (float)advCol[3]);
        GL11.glVertex2f((float)x1, (float)y1);
        GL11.glColor4f((float)advCol[4], (float)advCol[5], (float)advCol[6], (float)advCol[7]);
        GL11.glVertex2f((float)x2, (float)y2);
        GL11.glEnd();
    }

    public static void drawRectangleLines(Rectangle rectangle, float xOffset, float yOffset, float red, float green, float blue, float alpha) {
        GameResources.empty.bindTexture();
        GL11.glLoadIdentity();
        GL11.glBegin((int)2);
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)alpha);
        GL11.glVertex2f((float)(xOffset + (float)rectangle.x), (float)(yOffset + (float)rectangle.y));
        GL11.glVertex2f((float)(xOffset + (float)rectangle.x + (float)rectangle.width), (float)(yOffset + (float)rectangle.y));
        GL11.glVertex2f((float)(xOffset + (float)rectangle.x + (float)rectangle.width), (float)(yOffset + (float)rectangle.y + (float)rectangle.height));
        GL11.glVertex2f((float)(xOffset + (float)rectangle.x), (float)(yOffset + (float)rectangle.y + (float)rectangle.height));
        GL11.glEnd();
    }

    public static void drawRectangleLines(Rectangle rectangle, GameCamera camera, float red, float green, float blue, float alpha) {
        Renderer.drawRectangleLines(rectangle, -camera.getX(), -camera.getY(), red, green, blue, alpha);
    }

    public static void drawRectangleLines(Rectangle rectangle, float red, float green, float blue, float alpha) {
        Renderer.drawRectangleLines(rectangle, 0.0f, 0.0f, red, green, blue, alpha);
    }

    public static void drawShapeLines(Shape shape, float xOffset, float yOffset, float red, float green, float blue, float alpha) {
        int SG;
        float[] coords = new float[6];
        LinkedList<Runnable> runnables = new LinkedList<Runnable>();
        PathIterator it = shape.getPathIterator(null);
        while (!it.isDone() && (SG = it.currentSegment(coords)) != 4) {
            if (SG == 1) {
                Point2D.Float point = new Point2D.Float(coords[0] + xOffset, coords[1] + yOffset);
                runnables.add(() -> GL11.glVertex2f((float)point.x, (float)point.y));
            } else if (SG == 0) {
                runnables.add(() -> {
                    GL11.glEnd();
                    GL11.glBegin((int)3);
                });
            }
            it.next();
        }
        GameResources.empty.bindTexture();
        GL11.glLoadIdentity();
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)alpha);
        GL11.glBegin((int)3);
        for (Runnable runnable : runnables) {
            runnable.run();
        }
        GL11.glEnd();
    }

    public static void drawShape(Shape shape, float xOffset, float yOffset, boolean filled, float red, float green, float blue, float alpha) {
        int SG;
        float[] coords = new float[6];
        LinkedList<Point2D.Float> points = new LinkedList<Point2D.Float>();
        PathIterator it = shape.getPathIterator(null);
        while (!it.isDone() && (SG = it.currentSegment(coords)) != 4) {
            if (SG == 0 || SG == 1) {
                points.add(new Point2D.Float(coords[0] + xOffset, coords[1] + yOffset));
            }
            it.next();
        }
        GameResources.empty.bindTexture();
        GL11.glLoadIdentity();
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)alpha);
        if (points.size() <= 2) {
            GL11.glBegin((int)1);
        } else {
            GL11.glBegin((int)(filled ? 9 : 2));
        }
        for (Point2D.Float point : points) {
            GL11.glVertex2f((float)point.x, (float)point.y);
        }
        GL11.glEnd();
    }

    public static void drawShape(Shape shape, boolean filled, float red, float green, float blue, float alpha) {
        Renderer.drawShape(shape, 0.0f, 0.0f, filled, red, green, blue, alpha);
    }

    public static void drawShape(Shape shape, GameCamera camera, boolean filled, float red, float green, float blue, float alpha) {
        Renderer.drawShape(shape, -camera.getX(), -camera.getY(), filled, red, green, blue, alpha);
    }

    public static void drawCircle(int centerX, int centerY, int radius, int segments, Color color, boolean filled) {
        Renderer.drawCircle(centerX, centerY, radius, segments, (float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, (float)color.getAlpha() / 255.0f, filled);
    }

    public static void drawCircle(int centerX, int centerY, int radius, int segments, float red, float green, float blue, float alpha, boolean filled) {
        GameResources.empty.bindTexture();
        GL11.glLoadIdentity();
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)alpha);
        GL11.glBegin((int)(filled ? 9 : 2));
        for (int i = 0; i < segments; ++i) {
            double theta = Math.PI * 2 * (double)i / (double)segments;
            double x = (double)radius * Math.cos(theta);
            double y = (double)radius * Math.sin(theta);
            GL11.glVertex2d((double)(x + (double)centerX), (double)(y + (double)centerY));
        }
        GL11.glEnd();
    }

    public static void drawLine(int x1, int y1, int x2, int y2, Color color) {
        Renderer.drawLineRGBA(x1, y1, x2, y2, (float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, (float)color.getAlpha() / 255.0f);
    }

    public static void drawLineRGBA(int x1, int y1, int x2, int y2, float red, float green, float blue, float alpha) {
        GameResources.empty.bindTexture();
        GL11.glLoadIdentity();
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)alpha);
        GL11.glBegin((int)1);
        GL11.glVertex2f((float)x1, (float)y1);
        GL11.glVertex2f((float)x2, (float)y2);
        GL11.glEnd();
    }

    private static boolean isDrawingScreenshotEffect() {
        return System.currentTimeMillis() - screenshotTime < 350L;
    }

    private static void clearShaderHistory() {
        while (!shaderHistory.isEmpty()) {
            Renderer.stopShader(shaderHistory.getLast());
        }
    }

    public static void stopShader(GameShader shader) {
        GameShader lastShader;
        boolean fail = false;
        if (!shaderHistory.isEmpty()) {
            lastShader = shaderHistory.getLast();
            if (lastShader == shader) {
                GameShader nextShader;
                shaderHistory.removeLast();
                GameShader gameShader = nextShader = shaderHistory.isEmpty() ? null : shaderHistory.getLast();
                if (nextShader != null) {
                    nextShader._ScreenUse();
                } else {
                    GL20.glUseProgram((int)0);
                }
            } else {
                fail = true;
            }
        } else {
            fail = true;
        }
        if (fail) {
            System.err.println("Stopped wrong shader. Remember to stop shader again before stopping another.");
            lastShader = shaderHistory.isEmpty() ? null : shaderHistory.getLast();
            System.err.println("Current shader in use is " + lastShader);
        }
    }

    public static void useShader(GameShader shader) {
        shaderHistory.add(shader);
        if (shader != null) {
            shader._ScreenUse();
        } else {
            GL20.glUseProgram((int)0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void takeSceneAndHudShot(TickManager tickManager, ChatMessageList chat) {
        GameWindow window;
        block33: {
            if (screenshotCooldown.get()) {
                if (chat != null) {
                    chat.addMessage("Cannot take scene and hudshot at this time.");
                }
                return;
            }
            window = WindowManager.getWindow();
            screenshotCooldown.set(true);
            try {
                int frameHeight;
                int frameWidth;
                int bpp;
                block31: {
                    bpp = 4;
                    frameWidth = window.getFrameWidth();
                    frameHeight = window.getFrameHeight();
                    GameFrameBuffer sceneBuffer = window.getNewFrameBuffer(frameWidth, frameHeight);
                    try {
                        Renderer.clearShaderHistory();
                        GL11.glClear((int)16640);
                        if (sceneBuffer.isComplete()) {
                            try {
                                window.startSceneDraw();
                                GlobalData.getCurrentState().drawScene(tickManager, true);
                            }
                            finally {
                                window.endSceneDraw();
                            }
                            try {
                                window.startSceneOverlayDraw();
                                GlobalData.getCurrentState().drawSceneOverlay(tickManager);
                            }
                            finally {
                                window.endSceneOverlayDraw();
                            }
                            sceneBuffer.bindFrameBuffer();
                            GL11.glClearColor((float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f);
                            GL11.glClear((int)16384);
                            window.renderScene(sceneBuffer);
                            ByteBuffer buffer = Renderer.readColorBufferFromFrameBuffer(sceneBuffer, false);
                            String filePath = GlobalData.appDataPath() + "screenshots/scene " + new SimpleDateFormat("yyyy-MM-dd HH'h'mm'm'ss's'").format(new Date());
                            new Thread(new ShotSave(filePath, buffer, frameWidth, frameHeight, bpp, "sceneshotsave", chat)).start();
                            break block31;
                        }
                        GameLog.err.println("An error occurred creating sceneshot frame buffer.");
                        if (chat != null) {
                            chat.addMessage("There was an error taking sceneshot");
                        }
                    }
                    finally {
                        sceneBuffer.unbindFrameBuffer();
                        sceneBuffer.dispose();
                    }
                }
                GL20.glUseProgram((int)0);
                GameFrameBuffer hudBuffer = window.getNewFrameBuffer(frameWidth, frameHeight);
                try {
                    if (hudBuffer.isComplete()) {
                        try {
                            window.startHudDraw();
                            GlobalData.getCurrentState().drawHud(tickManager);
                            hudManager.cleanUp();
                            GameTooltipManager.drawHudTooltips();
                        }
                        finally {
                            window.endHudDraw();
                        }
                        hudBuffer.bindFrameBuffer();
                        GL11.glClearColor((float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f);
                        GL11.glClear((int)16384);
                        window.renderHud(1.0f);
                        ByteBuffer buffer = Renderer.readColorBufferFromFrameBuffer(hudBuffer, true);
                        String filePath = GlobalData.appDataPath() + "screenshots/hud " + new SimpleDateFormat("yyyy-MM-dd HH'h'mm'm'ss's'").format(new Date());
                        new Thread(new ShotSave(filePath, buffer, frameWidth, frameHeight, bpp, "hudshotsave", chat)).start();
                        break block33;
                    }
                    GameLog.err.println("An error occurred creating hudshot frame buffer.");
                    if (chat != null) {
                        chat.addMessage("There was an error taking hudshot");
                    }
                }
                finally {
                    hudBuffer.unbindFrameBuffer();
                    hudBuffer.dispose();
                }
            }
            catch (Exception e) {
                screenshotCooldown.set(false);
                e.printStackTrace();
            }
            finally {
                if (!shaderHistory.isEmpty()) {
                    GameShader lastShader = shaderHistory.getLast();
                    if (lastShader != null) {
                        lastShader._ScreenUse();
                    } else {
                        GL20.glUseProgram((int)0);
                    }
                }
            }
        }
        window.makeCurrent();
        window.getInput().clearInput();
        window.updateResize();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void takeMapshot(Level level, Client client, GameCamera camera) {
        if (screenshotCooldown.get()) {
            if (client != null) {
                client.chat.addMessage("Cannot take mapshot at this time.");
            }
            return;
        }
        GameWindow window = WindowManager.getWindow();
        screenshotCooldown.set(true);
        try {
            GameLoadingScreen.clearLog();
            GameLoadingScreen.drawLoadingString(Localization.translate("loading", "mapshot"));
            int bpp = 3;
            GL20.glUseProgram((int)0);
            GameFrameBuffer frameBuffer = window.getNewFrameBuffer(camera.getWidth(), camera.getHeight());
            try {
                frameBuffer.bindFrameBuffer();
                if (frameBuffer.isComplete()) {
                    boolean lastHideUI = Settings.hideUI;
                    Settings.hideUI = true;
                    level.runGLContextRunnables();
                    level.drawUtils.draw(camera, client == null ? null : client.getPlayer(), null, true);
                    window.update();
                    Settings.hideUI = lastHideUI;
                    ByteBuffer buffer = Renderer.readColorBufferFromFrameBuffer(frameBuffer, false);
                    if (client != null) {
                        client.chat.addMessage(Localization.translate("misc", "mapshottip"));
                    }
                    String filePath = GlobalData.appDataPath() + "screenshots/" + new SimpleDateFormat("yyyy-MM-dd HH'h'mm'm'ss's'").format(new Date());
                    new Thread(new ShotSave(filePath, buffer, camera.getWidth(), camera.getHeight(), bpp, "mapshotsave", client == null ? null : client.chat)).start();
                } else {
                    GameLog.err.println("An error occurred creating mapshot frame buffer.");
                    if (client != null) {
                        client.chat.addMessage("There was an error taking mapshot");
                    }
                }
            }
            finally {
                frameBuffer.unbindFrameBuffer();
                frameBuffer.dispose();
                if (!shaderHistory.isEmpty()) {
                    GameShader lastShader = shaderHistory.getLast();
                    if (lastShader != null) {
                        lastShader._ScreenUse();
                    } else {
                        GL20.glUseProgram((int)0);
                    }
                }
            }
        }
        catch (Exception e) {
            screenshotCooldown.set(false);
            e.printStackTrace();
        }
        window.makeCurrent();
        window.getInput().clearInput();
        window.tickWindowResize(true);
    }

    public static void takeScreenshot(ChatMessageList chat) {
        if (screenshotCooldown.get() || Renderer.isDrawingScreenshotEffect()) {
            if (chat != null) {
                chat.addMessage("Cannot take screenshot at this time.");
            }
            return;
        }
        GameWindow window = WindowManager.getWindow();
        SoundManager.playSound(GameResources.cameraShutter, SoundEffect.ui());
        screenshotCooldown.set(true);
        screenshotTime = System.currentTimeMillis();
        try {
            GL11.glReadBuffer((int)1028);
            int width = window.getWidth();
            int height = window.getHeight();
            int bpp = 4;
            ByteBuffer buffer = BufferUtils.createByteBuffer((int)(width * height * bpp));
            GL11.glReadPixels((int)0, (int)0, (int)width, (int)height, (int)6408, (int)5121, (ByteBuffer)buffer);
            String path = GlobalData.appDataPath() + "screenshots/" + new SimpleDateFormat("yyyy-MM-dd HH'h'mm'm'ss's'").format(new Date());
            new Thread(new ShotSave(path, buffer, width, height, bpp, "screenshotsave", chat)).start();
        }
        catch (Exception e) {
            screenshotCooldown.set(false);
            e.printStackTrace();
        }
        window.getInput().clearInput();
    }

    public static GameFrameBuffer getNewFrameBuffer(int width, int height) {
        GameWindow window = WindowManager.getWindow();
        return window.getNewFrameBuffer(width, height);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void renderLevelToBuffer(GameFrameBuffer frameBuffer, Level level, PlayerMob perspective, GameCamera camera, TickManager tickManager) {
        GameWindow window = WindowManager.getWindow();
        try {
            GL20.glUseProgram((int)0);
            try {
                frameBuffer.bindFrameBuffer();
                frameBuffer.clearColor();
                frameBuffer.clearDepth();
                if (frameBuffer.isComplete()) {
                    boolean lastHideUI = Settings.hideUI;
                    Settings.hideUI = true;
                    level.drawUtils.draw(camera, perspective, tickManager, true);
                    Settings.hideUI = lastHideUI;
                } else {
                    GameLog.err.println("An error occurred creating level shot frame buffer.");
                }
            }
            finally {
                frameBuffer.unbindFrameBuffer();
                if (!shaderHistory.isEmpty()) {
                    GameShader lastShader = shaderHistory.getLast();
                    if (lastShader != null) {
                        lastShader._ScreenUse();
                    } else {
                        GL20.glUseProgram((int)0);
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        window.makeCurrent();
    }

    public static ByteBuffer readColorBufferFromFrameBuffer(GameFrameBuffer frameBuffer, boolean includeAlpha) {
        int bpp = includeAlpha ? 4 : 3;
        ByteBuffer buffer = BufferUtils.createByteBuffer((int)(frameBuffer.getWidth() * frameBuffer.getHeight() * bpp));
        GL11.glBindTexture((int)3553, (int)frameBuffer.getColorBufferTextureID());
        int oldPackAlignment = GL11.glGetInteger((int)3333);
        GL11.glPixelStorei((int)3333, (int)1);
        GL11.glGetTexImage((int)3553, (int)0, (int)(includeAlpha ? 6408 : 6407), (int)5121, (ByteBuffer)buffer);
        GL11.glPixelStorei((int)3333, (int)oldPackAlignment);
        return buffer;
    }

    public static void setMouseDraggingElement(MouseDraggingElement e) {
        mouseDraggingElement = e;
    }

    public static MouseDraggingElement getMouseDraggingElement() {
        return mouseDraggingElement;
    }

    public static int queryGLError(String uniqueID) {
        int error = GL11.glGetError();
        if (uniqueID != null) {
            Renderer.printGLError(uniqueID, error);
        }
        return error;
    }

    public static void printGLError(String uniqueID, int error) {
        if (error == 0) {
            return;
        }
        switch (error) {
            case 1280: {
                if (uniqueID == null || ((HashSet)glErrorPrints.get(uniqueID)).contains(error)) break;
                System.err.println("GLError." + uniqueID + ": GL_INVALID_ENUM");
                new Throwable().printStackTrace(System.err);
                glErrorPrints.add(uniqueID, error);
                break;
            }
            case 1281: {
                if (uniqueID == null || ((HashSet)glErrorPrints.get(uniqueID)).contains(error)) break;
                System.err.println("GLError." + uniqueID + ": GL_INVALID_VALUE");
                new Throwable().printStackTrace(System.err);
                glErrorPrints.add(uniqueID, error);
                break;
            }
            case 1282: {
                if (uniqueID == null || ((HashSet)glErrorPrints.get(uniqueID)).contains(error)) break;
                System.err.println("GLError." + uniqueID + ": GL_INVALID_OPERATION");
                new Throwable().printStackTrace(System.err);
                glErrorPrints.add(uniqueID, error);
                break;
            }
            case 1283: {
                if (uniqueID == null || ((HashSet)glErrorPrints.get(uniqueID)).contains(error)) break;
                System.err.println("GLError." + uniqueID + ": GL_STACK_OVERFLOW");
                new Throwable().printStackTrace(System.err);
                glErrorPrints.add(uniqueID, error);
                break;
            }
            case 1284: {
                if (uniqueID == null || ((HashSet)glErrorPrints.get(uniqueID)).contains(error)) break;
                System.err.println("GLError." + uniqueID + ": GL_STACK_UNDERFLOW");
                new Throwable().printStackTrace(System.err);
                glErrorPrints.add(uniqueID, error);
                break;
            }
            case 1285: {
                if (uniqueID == null || ((HashSet)glErrorPrints.get(uniqueID)).contains(error)) break;
                System.err.println("GLError." + uniqueID + ": GL_OUT_OF_MEMORY");
                new Throwable().printStackTrace(System.err);
                glErrorPrints.add(uniqueID, error);
            }
        }
    }

    static {
        screenshotCooldown = new AtomicBoolean(false);
        hudManager = new ScreenHudManager();
        glErrorPrints = new HashMapSet();
        cursorSizeOffset = 1;
        cursorSizes = new float[]{0.75f, 1.0f, 1.25f, 1.5f, 1.75f};
        currentCursor = null;
        nextCursor = null;
    }

    private static class ShotSave
    implements Runnable {
        private final ByteBuffer buffer;
        private final String filePath;
        private final String translateKey;
        private final int imageWidth;
        private final int imageHeight;
        private final int imageBPP;
        private final ChatMessageList chat;

        public ShotSave(String filePath, ByteBuffer buffer, int imageWidth, int imageHeight, int imageBPP, String translateKey, ChatMessageList chat) {
            this.filePath = filePath;
            this.buffer = buffer;
            this.imageWidth = imageWidth;
            this.imageHeight = imageHeight;
            this.imageBPP = imageBPP;
            this.translateKey = translateKey;
            this.chat = chat;
        }

        @Override
        public void run() {
            try {
                int size = this.imageWidth * this.imageHeight * this.imageBPP;
                if (this.buffer.capacity() != size) {
                    throw new IllegalArgumentException("Buffer incorrect size: " + this.buffer.capacity() + ", expected: " + size);
                }
                File file = new File(this.filePath + ".png");
                GameUtils.mkDirs(file);
                ByteBuffer flippedBuffer = BufferUtils.createByteBuffer((int)size);
                for (int y = this.imageHeight - 1; y >= 0; --y) {
                    int index = this.imageWidth * y * this.imageBPP;
                    byte[] row = new byte[this.imageWidth * this.imageBPP];
                    this.buffer.position(index);
                    this.buffer.get(row, 0, row.length);
                    flippedBuffer.put(row, 0, row.length);
                }
                flippedBuffer.position(0);
                STBImageWrite.stbi_write_png((CharSequence)file.getAbsolutePath(), (int)this.imageWidth, (int)this.imageHeight, (int)this.imageBPP, (ByteBuffer)flippedBuffer, (int)0);
                if (this.chat != null) {
                    FairType type = new FairType();
                    type.append(FairCharacterGlyph.fromStringToOpenFile(ChatMessage.fontOptions, Localization.translate("misc", this.translateKey, "path", file.getAbsolutePath()), file));
                    this.chat.addMessage(type);
                }
            }
            catch (Exception e) {
                this.chat.addMessage(GameColor.RED.getColorCode() + "Error saving image: " + e.getMessage());
                e.printStackTrace();
                screenshotCooldown.set(false);
            }
            screenshotCooldown.set(false);
        }
    }
}

