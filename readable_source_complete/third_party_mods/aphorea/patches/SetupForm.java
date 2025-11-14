/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modLoader.annotations.ModMethodPatch
 *  necesse.gfx.forms.MainGameFormManager
 *  net.bytebuddy.asm.Advice$OnMethodExit
 *  net.bytebuddy.asm.Advice$This
 */
package aphorea.patches;

import aphorea.ui.AphCustomUI;
import aphorea.ui.AphCustomUIList;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.gfx.forms.MainGameFormManager;
import net.bytebuddy.asm.Advice;

@ModMethodPatch(target=MainGameFormManager.class, name="setup", arguments={})
public class SetupForm {
    @Advice.OnMethodExit
    static void onExit(@Advice.This MainGameFormManager mainGameFormManager) {
        for (AphCustomUI manager : AphCustomUIList.list.values()) {
            manager.mainGameFormManager = mainGameFormManager;
            manager.startForm();
            if (manager.form == null) continue;
            manager.setupForm();
        }
    }
}

