/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.util;

import java.awt.Point;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;

public class TargetFinderDistance<T extends Mob> {
    public int searchDistance;
    public int targetLostAddedDistance;

    public TargetFinderDistance(int searchDistance) {
        this.searchDistance = searchDistance;
        this.targetLostAddedDistance = 64;
    }

    public float getSearchDistanceMod(T mob, Mob target) {
        float ownerMod = ((Mob)mob).buffManager.getModifier(BuffModifiers.CHASER_RANGE).floatValue();
        float targetMod = target == null ? 1.0f : target.buffManager.getModifier(BuffModifiers.TARGET_RANGE).floatValue();
        return ((Entity)mob).getLevel().entityManager.getChaserDistanceMod() * ownerMod * targetMod;
    }

    protected int getSearchDistanceFlat(T mob, Mob target) {
        return this.searchDistance;
    }

    protected int getTargetLostDistanceFlat(T mob, Mob target) {
        return this.getSearchDistanceFlat(mob, target) + this.targetLostAddedDistance;
    }

    public final float getDistance(Point base, Mob mob) {
        return this.getDistance(base, mob.x, mob.y);
    }

    public float getDistance(Point base, float x, float y) {
        return (float)base.distance(x, y);
    }

    public int getSearchDistance(T mob, Mob target) {
        return (int)((float)this.getSearchDistanceFlat(mob, target) * this.getSearchDistanceMod(mob, target));
    }

    public int getTargetLostDistance(T mob, Mob target) {
        return (int)((float)this.getTargetLostDistanceFlat(mob, target) * this.getSearchDistanceMod(mob, target));
    }

    public GameAreaStream<Mob> streamMobsAndPlayersInRange(Point base, T mob) {
        int distance = (int)((float)this.getSearchDistance(mob, null) * BuffModifiers.MAX_TARGET_RANGE_MODIFIER);
        return ((Entity)mob).getLevel().entityManager.streamAreaMobsAndPlayers(base.x, base.y, distance);
    }

    public GameAreaStream<PlayerMob> streamPlayersInRange(Point base, T mob) {
        int distance = (int)((float)this.getSearchDistance(mob, null) * BuffModifiers.MAX_TARGET_RANGE_MODIFIER);
        return ((Entity)mob).getLevel().entityManager.players.streamArea(base.x, base.y, distance);
    }

    public GameAreaStream<Mob> streamMobsInRange(Point base, T mob) {
        int distance = (int)((float)this.getSearchDistance(mob, null) * BuffModifiers.MAX_TARGET_RANGE_MODIFIER);
        return ((Entity)mob).getLevel().entityManager.mobs.streamArea(base.x, base.y, distance);
    }
}

