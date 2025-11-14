/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.WeaponChargeSmiteBeamProjectile;

public class WeaponChargeSmiteLevelEvent
extends MobAbilityLevelEvent {
    protected ItemAttackerMob attackerMob;
    protected float startX;
    protected float startY;

    public WeaponChargeSmiteLevelEvent() {
    }

    public WeaponChargeSmiteLevelEvent(Mob owner, GameRandom uniqueIDRandom, float startX, float startY) {
        super(owner, uniqueIDRandom);
        this.startX = startX;
        this.startY = startY;
    }

    @Override
    public boolean isNetworkImportant() {
        return true;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(this.startX);
        writer.putNextFloat(this.startY);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.startX = reader.getNextFloat();
        this.startY = reader.getNextFloat();
    }

    @Override
    public void init() {
        super.init();
        if (this.owner instanceof ItemAttackerMob) {
            this.attackerMob = (ItemAttackerMob)this.owner;
        }
        if (this.attackerMob != null) {
            WeaponChargeSmiteBeamProjectile smiteProjectile = new WeaponChargeSmiteBeamProjectile(this.level, this.attackerMob, this.startX, this.startY);
            smiteProjectile.resetUniqueID(new GameRandom(this.getUniqueID()).nextSeeded(2422));
            this.getLevel().entityManager.projectiles.addHidden(smiteProjectile);
        }
        this.over();
    }
}

