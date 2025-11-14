/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.geom.Ellipse2D;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.particle.EvilsProtectorBombParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;

public class EvilsProtectorBombAttackEvent
extends MobAbilityLevelEvent
implements Attacker {
    protected long spawnTime;
    protected int x;
    protected int y;
    protected GameDamage damage;
    protected boolean playedStartSound;
    private final int warningTime = 1000;

    public EvilsProtectorBombAttackEvent() {
    }

    public EvilsProtectorBombAttackEvent(Mob owner, int x, int y, GameRandom uniqueIDRandom, GameDamage damage) {
        super(owner, uniqueIDRandom);
        this.spawnTime = owner.getWorldEntity().getTime();
        this.x = x;
        this.y = y;
        this.damage = damage;
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.spawnTime = reader.getNextLong();
        this.x = reader.getNextInt();
        this.y = reader.getNextInt();
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextLong(this.spawnTime);
        writer.putNextInt(this.x);
        writer.putNextInt(this.y);
    }

    @Override
    public void init() {
        super.init();
        if (this.isClient()) {
            this.level.entityManager.addParticle(new EvilsProtectorBombParticle(this.level, this.x, this.y, this.spawnTime, 1000L), Particle.GType.CRITICAL);
        }
    }

    @Override
    public void clientTick() {
        if (this.isOver()) {
            return;
        }
        long eventTime = this.level.getWorldEntity().getTime() - this.spawnTime;
        if (eventTime > 1000L && !this.playedStartSound) {
            SoundManager.playSound(GameResources.magicbolt2, (SoundEffect)SoundEffect.effect(this.x, this.y));
            this.playedStartSound = true;
        }
        if (eventTime > 1200L) {
            SoundManager.playSound(GameResources.firespell1, (SoundEffect)SoundEffect.effect(this.x, this.y).volume(0.5f));
            this.over();
        }
    }

    @Override
    public void serverTick() {
        if (this.isOver()) {
            return;
        }
        long eventTime = this.level.getWorldEntity().getTime() - this.spawnTime;
        if (eventTime > 1200L) {
            Ellipse2D.Float hitBox = new Ellipse2D.Float(this.x - 38, this.y - 30, 76.0f, 60.0f);
            GameUtils.streamTargets(this.owner, GameUtils.rangeTileBounds(this.x, this.y, 5)).filter(m -> m.canBeHit(this.owner) && hitBox.intersects(m.getHitBox())).forEach(m -> m.isServerHit(this.damage, m.getX() - this.x, m.getY() - this.y, 100.0f, this.owner));
            this.over();
        }
    }
}

