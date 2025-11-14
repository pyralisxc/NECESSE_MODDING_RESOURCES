/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Rectangle;
import java.awt.Shape;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.GroundEffectEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.entity.mobs.hostile.GiantCaveSpiderMob;
import necesse.entity.particle.CaveSpiderSpitParticle;
import necesse.entity.particle.Particle;
import necesse.level.maps.LevelObjectHit;

public class CaveSpiderSpitEvent
extends GroundEffectEvent {
    public GiantCaveSpiderMob.Variant variant;
    public GameDamage damage;
    protected int tickCounter;
    protected int hitsRemaining;
    protected MobHitCooldowns hitCooldowns;
    protected CaveSpiderSpitParticle particle;

    public CaveSpiderSpitEvent() {
    }

    public CaveSpiderSpitEvent(Mob owner, int x, int y, GameRandom uniqueIDRandom, GiantCaveSpiderMob.Variant variant, GameDamage damage, int maxHits) {
        super(owner, x, y, uniqueIDRandom);
        this.variant = variant;
        this.damage = damage;
        this.hitsRemaining = Math.min(maxHits, 65535);
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextShortUnsigned(this.variant.ordinal());
        this.damage.writePacket(writer);
        writer.putNextShortUnsigned(this.hitsRemaining);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.variant = GiantCaveSpiderMob.Variant.values()[reader.getNextShortUnsigned()];
        this.damage = GameDamage.fromReader(reader);
        this.hitsRemaining = reader.getNextShortUnsigned();
    }

    @Override
    public void init() {
        super.init();
        this.tickCounter = 0;
        this.hitCooldowns = new MobHitCooldowns();
        if (this.isClient()) {
            this.particle = new CaveSpiderSpitParticle(this.level, this.x, this.y, 5000L, this.variant);
            this.level.entityManager.addParticle(this.particle, true, Particle.GType.CRITICAL);
        }
    }

    @Override
    public Shape getHitBox() {
        int width = 40;
        int height = 30;
        return new Rectangle(this.x - width / 2, this.y - height / 2, width, height);
    }

    @Override
    public void clientHit(Mob target) {
        target.startHitCooldown();
        this.hitCooldowns.startCooldown(target);
        --this.hitsRemaining;
        if (this.hitsRemaining <= 0) {
            this.over();
        }
    }

    @Override
    public void serverHit(Mob target, boolean clientSubmitted) {
        if (clientSubmitted || this.hitCooldowns.canHit(target)) {
            target.isServerHit(this.damage, 0.0f, 0.0f, 0.0f, this.owner);
            this.hitCooldowns.startCooldown(target);
            --this.hitsRemaining;
            if (this.hitsRemaining <= 0) {
                this.over();
            }
        }
    }

    @Override
    public void hitObject(LevelObjectHit hit) {
        hit.getLevelObject().attackThrough(this.damage, this.owner);
    }

    @Override
    public boolean canHit(Mob mob) {
        return super.canHit(mob) && this.hitCooldowns.canHit(mob);
    }

    @Override
    public void clientTick() {
        ++this.tickCounter;
        if (this.tickCounter > 100) {
            this.over();
        } else {
            super.clientTick();
        }
    }

    @Override
    public void serverTick() {
        ++this.tickCounter;
        if (this.tickCounter > 100) {
            this.over();
        } else {
            super.serverTick();
        }
    }

    @Override
    public void over() {
        super.over();
        if (this.particle != null) {
            this.particle.despawnNow();
        }
    }
}

