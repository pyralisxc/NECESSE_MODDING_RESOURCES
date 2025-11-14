/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import java.util.function.BiFunction;
import java.util.function.Predicate;
import necesse.engine.modifiers.Modifier;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;

public class MobSpawnLocation {
    public final Mob mob;
    public final int x;
    public final int y;
    private boolean valid;

    public MobSpawnLocation(Mob mob, int x, int y) {
        this.mob = mob;
        this.x = x;
        this.y = y;
        this.valid = true;
    }

    public void apply() {
        this.mob.setPos(this.x, this.y, true);
    }

    public boolean valid() {
        return this.valid;
    }

    public boolean validAndApply() {
        if (this.valid()) {
            this.apply();
            return true;
        }
        return false;
    }

    public MobSpawnLocation checkMobSpawnLocation() {
        return this.mob.checkSpawnLocation(this);
    }

    public MobSpawnLocation checkMaxHostilesAround(int maxMobs, int tileRange, ServerClient client) {
        return this.checkMaxMobsAround(maxMobs, tileRange, m -> m.isHostile, client);
    }

    public MobSpawnLocation checkMaxMobsAround(int maxMobs, int tileRange, Predicate<Mob> filter, ServerClient client) {
        if (this.valid) {
            long count = this.mob.getLevel().entityManager.mobs.streamInRegionsInTileRange(this.x, this.y, tileRange).filter(m -> GameMath.squareDistance(this.x, this.y, m.x, m.y) <= (float)(tileRange * 32)).filter(filter).count();
            float multiplier = 1.0f;
            if (client != null) {
                multiplier = client.playerMob.buffManager.getModifier(BuffModifiers.MOB_SPAWN_CAP).floatValue();
            }
            this.valid = (float)count < (float)maxMobs * multiplier;
        }
        return this;
    }

    public MobSpawnLocation checkLocation(BiFunction<Integer, Integer, Boolean> logic) {
        this.valid = this.valid && logic.apply(this.x, this.y) != false;
        return this;
    }

    public MobSpawnLocation checkTile(BiFunction<Integer, Integer, Boolean> logic) {
        this.valid = this.valid && logic.apply(GameMath.getTileCoordinate(this.x), GameMath.getTileCoordinate(this.y)) != false;
        return this;
    }

    public MobSpawnLocation checkLightThreshold(ServerClient client) {
        Modifier<Integer> modifier = BuffModifiers.MOB_SPAWN_LIGHT_THRESHOLD;
        int threshold = client != null ? client.playerMob.buffManager.getAndApplyModifiers(modifier, this.mob.spawnLightThreshold).intValue() : this.mob.spawnLightThreshold.limits.applyModifierLimits(modifier, modifier.finalLimit(modifier.appendManager((Integer)modifier.defaultBuffManagerValue, (Integer)this.mob.spawnLightThreshold.value))).intValue();
        return this.checkMaxLightThreshold(threshold);
    }

    public MobSpawnLocation checkStaticLightThreshold(ServerClient client) {
        Modifier<Integer> modifier = BuffModifiers.MOB_SPAWN_LIGHT_THRESHOLD;
        int threshold = client != null ? client.playerMob.buffManager.getAndApplyModifiers(modifier, this.mob.spawnLightThreshold).intValue() : this.mob.spawnLightThreshold.limits.applyModifierLimits(modifier, modifier.finalLimit(modifier.appendManager((Integer)modifier.defaultBuffManagerValue, (Integer)this.mob.spawnLightThreshold.value))).intValue();
        return this.checkMaxStaticLightThreshold(threshold);
    }

    public MobSpawnLocation checkMaxLightThreshold(int maxLight) {
        return this.checkTile((tileX, tileY) -> this.mob.getLevel().lightManager.getAmbientAndStaticLightLevelFloat((int)tileX, (int)tileY) <= (float)maxLight);
    }

    public MobSpawnLocation checkMinLightThreshold(int minLight) {
        return this.checkTile((tileX, tileY) -> this.mob.getLevel().lightManager.getAmbientAndStaticLightLevelFloat((int)tileX, (int)tileY) >= (float)minLight);
    }

    public MobSpawnLocation checkMaxStaticLightThreshold(int maxLight) {
        return this.checkTile((tileX, tileY) -> this.mob.getLevel().lightManager.getStaticLight((int)tileX, (int)tileY).getLevel() <= (float)maxLight);
    }

    public MobSpawnLocation checkMinStaticLightThreshold(int minLight) {
        return this.checkTile((tileX, tileY) -> this.mob.getLevel().lightManager.getStaticLight((int)tileX, (int)tileY).getLevel() >= (float)minLight);
    }

    public MobSpawnLocation checkNotLevelCollides() {
        return this.checkLocation((x, y) -> !this.mob.collidesWith(this.mob.getLevel(), (int)x, (int)y));
    }

    public MobSpawnLocation checkNotOnSurfaceInsideOnFloor() {
        return this.checkTile((x, y) -> this.mob.getLevel().isCave || this.mob.getLevel().isOutside((int)x, (int)y) || !this.mob.getLevel().getTile((int)x.intValue(), (int)y.intValue()).isFloor);
    }

    public MobSpawnLocation checkNotInsideOnFloor() {
        return this.checkTile((x, y) -> this.mob.getLevel().isOutside((int)x, (int)y) || !this.mob.getLevel().getTile((int)x.intValue(), (int)y.intValue()).isFloor);
    }

    public MobSpawnLocation checkNotSolidTile() {
        return this.checkTile((x, y) -> !this.mob.getLevel().isSolidTile((int)x, (int)y));
    }

    public MobSpawnLocation checkNotInLiquid() {
        return this.checkTile((tileX, tileY) -> !this.mob.getLevel().getTile((int)tileX.intValue(), (int)tileY.intValue()).isLiquid);
    }

    public MobSpawnLocation checkInLiquid() {
        return this.checkTile((tileX, tileY) -> this.mob.getLevel().getTile((int)tileX.intValue(), (int)tileY.intValue()).isLiquid);
    }
}

