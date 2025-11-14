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
import necesse.entity.particle.Particle;
import necesse.entity.particle.RuneSpiritPoolParticle;
import necesse.level.maps.LevelObjectHit;

public class RuneSpiritPoolEvent
extends GroundEffectEvent {
    public GameDamage damage;
    public float lingerTimeSeconds;
    protected int tickCounter;
    protected MobHitCooldowns hitCooldowns;
    public static Rectangle hitBox = new Rectangle(-15, -10, 30, 20);

    public RuneSpiritPoolEvent() {
    }

    public RuneSpiritPoolEvent(Mob owner, int x, int y, GameRandom uniqueIDRandom, GameDamage damage, float lingerTimeSeconds) {
        super(owner, x, y, uniqueIDRandom);
        this.damage = damage;
        this.lingerTimeSeconds = lingerTimeSeconds;
    }

    @Override
    public boolean isNetworkImportant() {
        return true;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        this.damage.writePacket(writer);
        writer.putNextFloat(this.lingerTimeSeconds);
        writer.putNextInt(this.tickCounter);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.damage = GameDamage.fromReader(reader);
        this.lingerTimeSeconds = reader.getNextFloat();
        this.tickCounter = reader.getNextInt();
    }

    @Override
    public void init() {
        super.init();
        this.hitCooldowns = new MobHitCooldowns();
        if (this.isClient()) {
            long msLeft = (long)(this.lingerTimeSeconds * 1000.0f) - (long)(this.tickCounter * 50);
            this.level.entityManager.addParticle(new RuneSpiritPoolParticle(this.level, this.x, this.y, msLeft), true, Particle.GType.CRITICAL);
        }
    }

    @Override
    public Shape getHitBox() {
        return new Rectangle(this.x + RuneSpiritPoolEvent.hitBox.x, this.y + RuneSpiritPoolEvent.hitBox.y, RuneSpiritPoolEvent.hitBox.width, RuneSpiritPoolEvent.hitBox.height);
    }

    @Override
    public void clientHit(Mob target) {
        target.startHitCooldown();
        this.hitCooldowns.startCooldown(target);
    }

    @Override
    public void serverHit(Mob target, boolean clientSubmitted) {
        if (clientSubmitted || this.hitCooldowns.canHit(target)) {
            target.isServerHit(this.damage, 0.0f, 0.0f, 0.0f, this.owner);
            this.hitCooldowns.startCooldown(target);
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
        if ((float)this.tickCounter > 20.0f * this.lingerTimeSeconds) {
            this.over();
        } else {
            super.clientTick();
        }
    }

    @Override
    public void serverTick() {
        ++this.tickCounter;
        if ((float)this.tickCounter > 20.0f * this.lingerTimeSeconds) {
            this.over();
        } else {
            super.serverTick();
        }
    }
}

