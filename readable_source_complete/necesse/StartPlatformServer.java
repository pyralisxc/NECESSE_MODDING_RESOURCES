/*
 * Decompiled with CFR 0.152.
 */
package necesse;

import necesse.engine.GameCrashLog;
import necesse.engine.ThreadFreezeMonitor;
import necesse.engine.loading.ServerLoader;
import necesse.engine.platforms.Platform;

public class StartPlatformServer {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void start(String[] args, Platform platform) {
        new ThreadFreezeMonitor(true, Thread.currentThread()).start();
        ServerLoader loader = new ServerLoader();
        try {
            if (loader.loadGame(args, platform)) {
                loader.startGame();
            }
        }
        catch (Error | Exception e) {
            System.err.println("Crash outside ticking, closing application.");
            e.printStackTrace();
            GameCrashLog.printCrashLog(e, null, null, "Start server crash", false);
        }
        finally {
            loader.unloadGame();
        }
    }
}

