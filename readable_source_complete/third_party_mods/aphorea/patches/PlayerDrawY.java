/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modLoader.annotations.ModMethodPatch
 *  necesse.entity.mobs.Mob
 *  necesse.level.gameTile.GameTile
 *  net.bytebuddy.asm.Advice$Argument
 *  net.bytebuddy.asm.Advice$OnMethodExit
 *  net.bytebuddy.asm.Advice$Return
 *  net.bytebuddy.asm.Advice$This
 */
package aphorea.patches;

import aphorea.patches.PlayerFlyingHeight;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.entity.mobs.Mob;
import necesse.level.gameTile.GameTile;
import net.bytebuddy.asm.Advice;

@ModMethodPatch(target=GameTile.class, name="getMobSinkingAmount", arguments={Mob.class})
public class PlayerDrawY {
    @Advice.OnMethodExit
    static void onExit(@Advice.This GameTile gameTile, @Advice.Argument(value=0) Mob mob, @Advice.Return(readOnly=false) int y) {
        int flyingHeight = PlayerFlyingHeight.playersFlyingHeight.getOrDefault(mob.getUniqueID(), 0);
        y -= flyingHeight;
    }
}

