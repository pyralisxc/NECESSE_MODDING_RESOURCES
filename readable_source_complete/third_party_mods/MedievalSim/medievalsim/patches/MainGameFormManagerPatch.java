/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modLoader.annotations.ModMethodPatch
 *  necesse.engine.network.client.Client
 *  necesse.gfx.forms.MainGameFormManager
 *  net.bytebuddy.asm.Advice$FieldValue
 *  net.bytebuddy.asm.Advice$OnMethodExit
 *  net.bytebuddy.asm.Advice$This
 */
package medievalsim.patches;

import medievalsim.admintools.AdminToolsManager;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.client.Client;
import necesse.gfx.forms.MainGameFormManager;
import net.bytebuddy.asm.Advice;

@ModMethodPatch(target=MainGameFormManager.class, name="setup", arguments={})
public class MainGameFormManagerPatch {
    @Advice.OnMethodExit
    static void onExit(@Advice.This MainGameFormManager mainGameFormManager, @Advice.FieldValue(value="client") Client client) {
        AdminToolsManager.setupAdminButton(mainGameFormManager, client);
    }
}

