/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modLoader.annotations.ModMethodPatch
 *  necesse.engine.network.client.Client
 *  net.bytebuddy.asm.Advice$OnMethodExit
 *  net.bytebuddy.asm.Advice$This
 */
package medievalsim.patches;

import medievalsim.buildmode.BuildModeManager;
import medievalsim.commandcenter.worldclick.WorldClickIntegration;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.client.Client;
import net.bytebuddy.asm.Advice;

public class ClientTickPatch {

    @ModMethodPatch(target=Client.class, name="tick", arguments={})
    public static class Tick {
        @Advice.OnMethodExit
        static void onExit(@Advice.This Client client) {
            if (BuildModeManager.hasInstance()) {
                BuildModeManager.getInstance().tick();
            }
            WorldClickIntegration.updateHoverPosition();
        }
    }
}

