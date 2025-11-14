/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.platforms;

import necesse.engine.GameLog;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.platforms.Platform;

public class PlatformManager {
    private static Platform platform;

    public static boolean initialize(Platform platform) throws Exception {
        PlatformManager.platform = platform;
        GameLog.debug.println("Initializing " + platform.getClass().getSimpleName());
        return platform.initialize();
    }

    public static void tick(TickManager tickManager) {
        platform.tick(tickManager);
    }

    public static void dispose() {
        if (platform != null) {
            platform.dispose();
        }
    }

    public static Platform getPlatform() {
        return platform;
    }
}

