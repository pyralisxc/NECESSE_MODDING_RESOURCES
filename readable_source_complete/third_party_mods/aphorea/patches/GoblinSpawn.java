/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modLoader.annotations.ModMethodPatch
 *  necesse.engine.registries.MobRegistry
 *  necesse.engine.util.GameRandom
 *  necesse.entity.mobs.hostile.GoblinMob
 *  necesse.level.maps.Level
 *  net.bytebuddy.asm.Advice$OnMethodEnter
 *  net.bytebuddy.asm.Advice$OnNonDefaultValue
 *  net.bytebuddy.asm.Advice$This
 */
package aphorea.patches;

import aphorea.mobs.hostile.DaggerGoblin;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.hostile.GoblinMob;
import necesse.level.maps.Level;
import net.bytebuddy.asm.Advice;

@ModMethodPatch(target=GoblinMob.class, name="init", arguments={})
public class GoblinSpawn {
    @Advice.OnMethodEnter(skipOn=Advice.OnNonDefaultValue.class)
    static boolean onEnter(@Advice.This GoblinMob goblinMob) {
        if (goblinMob instanceof DaggerGoblin) {
            return false;
        }
        boolean daggerGoblin = GameRandom.globalRandom.getChance(0.2f);
        if (daggerGoblin) {
            float randomDagger = GameRandom.globalRandom.getFloatBetween(0.0f, 1.0f);
            String daggerType = (double)randomDagger < 0.55 ? "copper" : ((double)randomDagger < 0.85 ? "iron" : "gold");
            goblinMob.getLevel().entityManager.addMob(MobRegistry.getMob((String)(daggerType + "daggergoblin"), (Level)goblinMob.getLevel()), goblinMob.x, goblinMob.y);
            goblinMob.remove();
        }
        return daggerGoblin;
    }
}

