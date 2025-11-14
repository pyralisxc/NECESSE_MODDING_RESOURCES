/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine;

import java.io.Serializable;
import necesse.engine.platforms.PlatformManager;

public class GameAuth
implements Serializable {
    private static long tempAuth = 0L;
    private static long platformAuthID = 0L;

    public static void setTempAuth(long authentication) {
        tempAuth = authentication;
    }

    public static long getAuthentication() {
        if (tempAuth != 0L) {
            return tempAuth;
        }
        return platformAuthID;
    }

    public static void loadAuth() {
        platformAuthID = PlatformManager.getPlatform().getUniqueUserID();
    }
}

