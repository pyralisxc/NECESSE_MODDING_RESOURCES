/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ShockWaveLevelEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.level.maps.LevelObjectHit;

public abstract class WeaponShockWaveLevelEvent
extends ShockWaveLevelEvent {
    protected boolean canDamageObjects;
    protected GameDamage damage;
    protected float resilienceGain;
    protected float knockback;

    public WeaponShockWaveLevelEvent(float angleExtent, float distancePerHit, float hitboxWidth) {
        super(angleExtent, 100.0f, 100.0f, distancePerHit, hitboxWidth);
    }

    public WeaponShockWaveLevelEvent(Mob owner, int x, int y, GameRandom uniqueIDRandom, float targetAngle, float angleExtent, float distancePerHit, float hitboxWidth, GameDamage damage, float resilienceGain, float velocity, float knockback, float range) {
        super(owner, x, y, uniqueIDRandom, targetAngle, angleExtent, velocity, range, distancePerHit, hitboxWidth);
        this.damage = damage;
        this.resilienceGain = resilienceGain;
        this.knockback = knockback;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(this.expandSpeed);
        writer.putNextFloat(this.maxDistance);
        this.damage.writePacket(writer);
        writer.putNextFloat(this.resilienceGain);
        writer.putNextFloat(this.knockback);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.expandSpeed = reader.getNextFloat();
        this.maxDistance = reader.getNextFloat();
        this.damage = GameDamage.fromReader(reader);
        this.resilienceGain = reader.getNextFloat();
        this.knockback = reader.getNextFloat();
    }

    @Override
    public void init() {
        super.init();
        this.canDamageObjects = this.owner != null && this.owner.isPlayer;
    }

    @Override
    public void damageTarget(Mob target) {
        target.isServerHit(this.damage, target.getX() - this.x, target.getY() - this.y, this.knockback, this.owner);
        if (target.canGiveResilience(this.owner) && this.resilienceGain != 0.0f) {
            this.owner.addResilience(this.resilienceGain);
            this.resilienceGain = 0.0f;
        }
    }

    @Override
    public void hitObject(LevelObjectHit hit) {
        if (this.canDamageObjects) {
            hit.getLevelObject().attackThrough(this.damage, this.owner);
        }
    }
}

