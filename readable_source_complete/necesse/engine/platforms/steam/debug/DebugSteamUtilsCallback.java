/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.codedisaster.steamworks.SteamUtilsCallback
 */
package necesse.engine.platforms.steam.debug;

import com.codedisaster.steamworks.SteamUtilsCallback;
import necesse.engine.platforms.steam.debug.DebugSteamCallback;

public class DebugSteamUtilsCallback
extends DebugSteamCallback
implements SteamUtilsCallback {
    public void onSteamShutdown() {
        this.print("onSteamShutdown", new Object[0]);
    }
}

