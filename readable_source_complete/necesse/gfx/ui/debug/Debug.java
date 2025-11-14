/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.ui.debug;

import java.awt.Color;
import java.util.ArrayList;
import necesse.engine.GlobalData;
import necesse.engine.input.InputEvent;
import necesse.engine.network.client.Client;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.debug.DebugInfo;
import necesse.gfx.ui.debug.DebugNetwork;
import necesse.gfx.ui.debug.DebugPerformanceHistory;
import necesse.gfx.ui.debug.DebugRegionPathCache;
import necesse.level.maps.Level;

public abstract class Debug {
    private static int active = 0;
    private static ArrayList<Debug> states;
    private int currentY = 30;
    protected static final FontOptions smallFontOptions;
    protected static final FontOptions bigFontOptions;

    private static void setup() {
        states = new ArrayList();
        states.add(new Debug(){

            @Override
            protected void drawDebug(Client client) {
            }
        });
        states.add(new DebugInfo());
        states.add(new DebugNetwork());
        states.add(new DebugPerformanceHistory());
        if (GlobalData.isDevMode()) {
            states.add(new DebugRegionPathCache());
        }
    }

    public static void submitChange() {
        if (states == null) {
            Debug.setup();
        }
        active = (active + 1) % states.size();
    }

    public static void reset() {
        if (states == null) {
            Debug.setup();
        }
        active = 0;
        for (Debug state : states) {
            state.onReset();
        }
    }

    public static boolean isActive() {
        return active != 0;
    }

    private static Debug getActiveDebug() {
        if (states == null) {
            Debug.setup();
        }
        return states.get(active);
    }

    public static void draw(Client client) {
        Debug.getActiveDebug().drawDebugPrivate(client);
    }

    public static void drawHUD(Level level, GameCamera camera, PlayerMob perspective) {
        Debug.getActiveDebug().drawHUDPrivate(level, camera, perspective);
    }

    public static void submitInputEvent(InputEvent event, Client client) {
        Debug.getActiveDebug().submitDebugInputEvent(event, client);
    }

    private void drawDebugPrivate(Client client) {
        this.initDraw();
        this.drawDebug(client);
    }

    private void drawHUDPrivate(Level level, GameCamera camera, PlayerMob perspective) {
        this.initDraw();
        this.drawDebugHUD(level, camera, perspective);
    }

    protected abstract void drawDebug(Client var1);

    protected void drawDebugHUD(Level level, GameCamera camera, PlayerMob perspective) {
    }

    protected void submitDebugInputEvent(InputEvent event, Client client) {
    }

    protected void onReset() {
    }

    private void initDraw() {
        this.currentY = 30;
    }

    protected final int skipY(int y) {
        int lastY = this.currentY;
        this.currentY += y;
        return lastY;
    }

    protected final void drawString(String str) {
        FontManager.bit.drawString(10.0f, this.currentY, str, smallFontOptions);
        this.skipY(15);
    }

    protected final void drawStringBig(String str) {
        FontManager.bit.drawString(10.0f, this.currentY, str, bigFontOptions);
        this.skipY(20);
    }

    static {
        smallFontOptions = new FontOptions(12).color(Color.WHITE);
        bigFontOptions = new FontOptions(16).color(Color.WHITE);
    }
}

