/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile.modifiers;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ProjectileModifier;
import necesse.level.maps.LevelObjectHit;

public class ResilienceOnHitProjectileModifier
extends ProjectileModifier {
    private float resilienceGain;
    private boolean hasGained = false;

    public ResilienceOnHitProjectileModifier() {
    }

    public ResilienceOnHitProjectileModifier(float resilienceGain) {
        this.resilienceGain = resilienceGain;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(this.resilienceGain);
        writer.putNextBoolean(this.hasGained);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.resilienceGain = reader.getNextFloat();
        this.hasGained = reader.getNextBoolean();
    }

    @Override
    public void initChildProjectile(Projectile projectile, float childStrength, int childCount) {
        super.initChildProjectile(projectile, childStrength, childCount);
        projectile.setModifier(new ResilienceOnHitProjectileModifier(this.resilienceGain / (float)childCount));
    }

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (!this.hasGained) {
            Mob owner = this.projectile.getOwner();
            if (mob != null && owner != null && mob.canGiveResilience(owner)) {
                owner.addResilience(this.resilienceGain);
                this.hasGained = true;
            }
        }
    }
}

