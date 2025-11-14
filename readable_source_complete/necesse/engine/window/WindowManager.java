/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.window;

import java.awt.Dimension;
import necesse.engine.Settings;
import necesse.engine.gameLoop.GameLoop;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.platforms.Platform;
import necesse.engine.window.DisplayMode;
import necesse.engine.window.GameWindow;
import necesse.gfx.Renderer;

public abstract class WindowManager {
    protected static GameWindow currentWindow;
    private boolean updateDisplayModeInPostTick;

    public static GameWindow getWindow() {
        return currentWindow;
    }

    public static long getMonitor(int index) {
        long[] monitors = Platform.getWindowManager().getMonitors();
        if (monitors.length == 0) {
            return 0L;
        }
        if (index < 0 || index >= monitors.length) {
            index = 0;
        }
        return monitors[index];
    }

    public abstract long[] getMonitors();

    public boolean initialize() {
        return true;
    }

    public void preGameTick(TickManager tickManager) {
        if (currentWindow.isCloseRequested() && tickManager instanceof GameLoop) {
            ((GameLoop)tickManager).stopMainGameLoop();
        }
        currentWindow.preGameTick(tickManager);
    }

    public void postGameTick(TickManager tickManager) {
        if (currentWindow.getInput().isPressed(300)) {
            Settings.displayMode = Settings.displayMode == DisplayMode.Borderless || Settings.displayMode == DisplayMode.Fullscreen ? DisplayMode.Windowed : DisplayMode.Borderless;
            this.updateDisplayModeNow();
            WindowManager.currentWindow.hasResized = true;
            Settings.saveClientSettings();
        }
        if (this.updateDisplayModeInPostTick) {
            this.updateDisplayModeNow();
            WindowManager.currentWindow.hasResized = true;
        }
    }

    public void updateDisplayModeNow() {
        this.updateDisplayModeInPostTick = false;
        GameWindow oldWindow = currentWindow;
        GameWindow window = this.createNewWindow(true);
        window.createWindow(oldWindow, false);
        oldWindow.destroy();
        window.updateSizeAndShow();
        window.setupPostProcessing();
        Renderer.reapplyCursor(window);
        window.show();
        window.tickWindowResize(true);
    }

    public void updateDisplayModeAfterTick() {
        this.updateDisplayModeInPostTick = true;
    }

    public abstract GameWindow createNewWindow(boolean var1);

    public abstract void dispose();

    public abstract Dimension[] getVideoModes(long var1);

    public abstract Dimension getVideoMode(long var1);

    public abstract GameMessage getBorderlessDisplayMessage();

    public abstract boolean doesMonitorNeedHDRHack();
}

