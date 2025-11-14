/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.MobSpawnLocation;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.level.maps.levelBuffManager.LevelModifiers;

public abstract class HostileItemAttackerMob
extends ItemAttackerMob {
    public HostileItemAttackerMob(int health) {
        super(health);
        this.isHostile = true;
        this.setTeam(-2);
        this.canDespawn = true;
    }

    @Override
    public boolean shouldSave() {
        return this.shouldSave && !this.canDespawn();
    }

    @Override
    public boolean isValidSpawnLocation(Server server, ServerClient client, int targetX, int targetY) {
        return new MobSpawnLocation(this, targetX, targetY).checkLightThreshold(client).checkMobSpawnLocation().validAndApply();
    }

    @Override
    public float getOutgoingDamageModifier() {
        float modifier = super.getOutgoingDamageModifier();
        if (this.getLevel() != null) {
            modifier *= this.getLevel().buffManager.getModifier(LevelModifiers.ENEMY_DAMAGE).floatValue();
        }
        return modifier;
    }

    @Override
    public float getSpeedModifier() {
        float modifier = super.getSpeedModifier();
        if (this.getLevel() != null) {
            modifier *= this.getLevel().buffManager.getModifier(LevelModifiers.ENEMY_SPEED).floatValue();
        }
        return modifier;
    }

    @Override
    public float getMaxHealthModifier() {
        float modifier = super.getMaxHealthModifier();
        if (this.getLevel() != null) {
            modifier *= this.getLevel().buffManager.getModifier(LevelModifiers.ENEMY_MAX_HEALTH).floatValue();
        }
        return modifier;
    }
}

