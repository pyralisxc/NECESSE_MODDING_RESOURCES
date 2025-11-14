/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modLoader.annotations.ModMethodPatch
 *  necesse.engine.save.SaveData
 *  necesse.engine.world.WorldSettings
 *  net.bytebuddy.asm.Advice$OnMethodExit
 *  net.bytebuddy.asm.Advice$Return
 */
package increasedStackSize.patches;

import increasedStackSize.IncreasedStackSize;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.save.SaveData;
import necesse.engine.world.WorldSettings;
import net.bytebuddy.asm.Advice;

@ModMethodPatch(target=WorldSettings.class, name="getSaveScript", arguments={})
public class WorldSettingsGetSaveScriptPatch {
    @Advice.OnMethodExit
    static void onExit(@Advice.Return(readOnly=false) SaveData save) {
        save.addInt("stackSizeMultiplier", IncreasedStackSize.newStackSizeMultiplier);
    }
}

