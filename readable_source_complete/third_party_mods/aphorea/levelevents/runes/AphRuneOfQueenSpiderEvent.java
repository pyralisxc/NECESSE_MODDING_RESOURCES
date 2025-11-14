/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.registries.BuffRegistry$Debuffs
 *  necesse.engine.util.GameRandom
 *  necesse.entity.levelEvent.mobAbilityLevelEvent.GroundEffectEvent
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.MobHitCooldowns
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.particle.Particle
 *  necesse.entity.particle.Particle$GType
 *  necesse.entity.particle.WebWeaverWebParticle
 *  necesse.level.maps.LevelObjectHit
 */
package aphorea.levelevents.runes;

import java.awt.Rectangle;
import java.awt.Shape;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.GroundEffectEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.Particle;
import necesse.entity.particle.WebWeaverWebParticle;
import necesse.level.maps.LevelObjectHit;

public class AphRuneOfQueenSpiderEvent
extends GroundEffectEvent {
    protected int tickCounter;
    protected MobHitCooldowns hitCooldowns;
    protected WebWeaverWebParticle particle;
    protected int duration;

    public AphRuneOfQueenSpiderEvent() {
    }

    public AphRuneOfQueenSpiderEvent(Mob owner, int x, int y, int duration, GameRandom uniqueIDRandom) {
        super(owner, x, y, uniqueIDRandom);
        this.duration = duration;
    }

    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.duration);
    }

    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.duration = reader.getNextInt();
    }

    public void init() {
        super.init();
        this.tickCounter = 0;
        this.hitCooldowns = new MobHitCooldowns();
        if (this.isClient()) {
            this.particle = new WebWeaverWebParticle(this.level, (float)this.x, (float)this.y, (long)this.duration, 0L);
            this.level.entityManager.addParticle((Particle)this.particle, true, Particle.GType.CRITICAL);
        }
    }

    public Shape getHitBox() {
        int width = 180;
        int height = 136;
        return new Rectangle(this.x - width / 2, this.y - height / 2, width, height);
    }

    public void clientHit(Mob mob) {
    }

    public void serverHit(Mob target, boolean clientSubmitted) {
        if (clientSubmitted || !target.buffManager.hasBuff(BuffRegistry.Debuffs.SPIDER_WEB_SLOW)) {
            target.addBuff(new ActiveBuff(BuffRegistry.Debuffs.SPIDER_WEB_SLOW, target, 1000, (Attacker)this), true);
        }
    }

    public void hitObject(LevelObjectHit hit) {
    }

    public void clientTick() {
        ++this.tickCounter;
        if (this.tickCounter > this.duration / 50) {
            this.over();
        } else {
            super.clientTick();
        }
    }

    public void serverTick() {
        ++this.tickCounter;
        if (this.tickCounter > this.duration / 50) {
            this.over();
        } else {
            super.serverTick();
        }
    }

    public void over() {
        super.over();
        if (this.particle != null) {
            this.particle.despawnNow();
        }
    }
}

