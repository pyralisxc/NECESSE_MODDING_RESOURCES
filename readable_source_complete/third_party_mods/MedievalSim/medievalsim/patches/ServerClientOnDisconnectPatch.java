/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modLoader.annotations.ModMethodPatch
 *  necesse.engine.network.server.ServerClient
 *  net.bytebuddy.asm.Advice$OnMethodExit
 *  net.bytebuddy.asm.Advice$This
 */
package medievalsim.patches;

import medievalsim.zones.PvPZoneTracker;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.server.ServerClient;
import net.bytebuddy.asm.Advice;

public class ServerClientOnDisconnectPatch {

    @ModMethodPatch(target=ServerClient.class, name="onDisconnect", arguments={})
    public static class OnDisconnect {
        @Advice.OnMethodExit
        static void onExit(@Advice.This ServerClient client) {
            PvPZoneTracker.cleanupPlayerState(client);
        }
    }
}

