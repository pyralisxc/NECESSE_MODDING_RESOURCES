/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.SmiteBeamProjectile;

public class SmiteLevelEvent
extends MobAbilityLevelEvent {
    public GameDamage damage;
    protected Mob targetMob;
    protected Mob owner;
    protected float targetX;
    protected float targetY;

    public SmiteLevelEvent() {
    }

    protected SmiteLevelEvent(Mob owner, GameRandom uniqueIDRandom, GameDamage damage) {
        super(owner, uniqueIDRandom);
        this.damage = damage;
        this.owner = owner;
    }

    public SmiteLevelEvent(Mob owner, GameRandom uniqueIDRandom, Mob targetMob, GameDamage damage) {
        this(owner, uniqueIDRandom, damage);
        this.targetMob = targetMob;
    }

    public SmiteLevelEvent(Mob owner, GameRandom uniqueIDRandom, float targetX, float targetY, GameDamage damage) {
        this(owner, uniqueIDRandom, damage);
        this.targetX = targetX;
        this.targetY = targetY;
    }

    @Override
    public boolean isNetworkImportant() {
        return true;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(this.targetX);
        writer.putNextFloat(this.targetY);
        writer.putNextInt(this.targetMob.getUniqueID());
        writer.putNextInt(this.owner.getUniqueID());
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.targetX = reader.getNextFloat();
        this.targetY = reader.getNextFloat();
        int targetMobUniqueID = reader.getNextInt();
        this.targetMob = GameUtils.getLevelMob(targetMobUniqueID, this.getLevel());
        int ownerID = reader.getNextInt();
        this.owner = GameUtils.getLevelMob(ownerID, this.getLevel());
    }

    @Override
    public void init() {
        super.init();
        if (this.isServer() && this.owner != null) {
            SmiteBeamProjectile smiteProjectile = this.targetMob != null ? new SmiteBeamProjectile(this.level, this.owner, this.targetMob, this.damage) : new SmiteBeamProjectile(this.level, this.owner, this.targetX, this.targetY, this.damage);
            smiteProjectile.resetUniqueID(new GameRandom(this.getUniqueID()).nextSeeded(2222));
            this.getLevel().entityManager.projectiles.add(smiteProjectile);
        }
        this.over();
    }
}

