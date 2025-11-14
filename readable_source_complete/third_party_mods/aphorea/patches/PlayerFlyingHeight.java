/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modLoader.annotations.ModMethodPatch
 *  necesse.entity.mobs.Mob
 *  net.bytebuddy.asm.Advice$OnMethodExit
 *  net.bytebuddy.asm.Advice$Return
 *  net.bytebuddy.asm.Advice$This
 */
package aphorea.patches;

import java.util.HashMap;
import java.util.Map;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.entity.mobs.Mob;
import net.bytebuddy.asm.Advice;

@ModMethodPatch(target=Mob.class, name="getFlyingHeight", arguments={})
public class PlayerFlyingHeight {
    public static Map<Integer, Integer> playersFlyingHeight = new HashMap<Integer, Integer>();

    @Advice.OnMethodExit
    static void onExit(@Advice.This Mob mob, @Advice.Return(readOnly=false) int flyingHeight) {
        if (flyingHeight == 0 && mob.isPlayer) {
            flyingHeight = playersFlyingHeight.getOrDefault(mob.getUniqueID(), 0);
        }
    }
}

