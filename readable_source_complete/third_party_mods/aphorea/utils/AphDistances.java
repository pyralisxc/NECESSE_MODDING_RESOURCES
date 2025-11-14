/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.entity.mobs.Mob
 *  necesse.level.maps.Level
 *  org.jetbrains.annotations.NotNull
 */
package aphorea.utils;

import java.util.ArrayList;
import java.util.function.Predicate;
import necesse.entity.mobs.Mob;
import necesse.level.maps.Level;
import org.jetbrains.annotations.NotNull;

public class AphDistances {
    public static Mob findClosestMob(@NotNull Mob mob, int distance) {
        return AphDistances.findClosestMob(mob.getLevel(), mob.x, mob.y, distance);
    }

    public static Mob findClosestMob(@NotNull Mob mob, int distance, Predicate<Mob> filter) {
        return AphDistances.findClosestMob(mob.getLevel(), mob.x, mob.y, distance, filter);
    }

    public static Mob findClosestMob(Level level, float x, float y, int distance) {
        return AphDistances.findClosestMob(level, x, y, distance, m -> true);
    }

    public static Mob findClosestMob(@NotNull Level level, float x, float y, int distance, Predicate<Mob> filter) {
        ArrayList mobs = new ArrayList();
        level.entityManager.streamAreaMobsAndPlayers(x, y, distance).filter(filter).forEach(mobs::add);
        mobs.sort((m1, m2) -> {
            float d1 = m1.getDistance(x, y);
            float d2 = m2.getDistance(x, y);
            return Float.compare(d1, d2);
        });
        return mobs.stream().findFirst().orElse(null);
    }
}

