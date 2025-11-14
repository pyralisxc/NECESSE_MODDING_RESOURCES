/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.message.GameMessage
 *  necesse.engine.localization.message.StaticMessage
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.registries.BuffRegistry$Debuffs
 *  necesse.engine.registries.ObjectRegistry
 *  necesse.engine.sound.SoundEffect
 *  necesse.engine.sound.SoundManager
 *  necesse.engine.sound.gameSound.GameSound
 *  necesse.engine.util.GameRandom
 *  necesse.engine.util.GameUtils
 *  necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.DeathMessageTable
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.particle.Particle
 *  necesse.entity.particle.Particle$GType
 *  necesse.gfx.GameResources
 */
package aphorea.levelevents.babylon;

import aphorea.particles.BabylonTowerFallingCrystalParticle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;

public class BabylonTowerFallingCrystalAttackEvent
extends MobAbilityLevelEvent
implements Attacker {
    protected long spawnTime;
    protected int x;
    protected int y;
    protected GameDamage damage;
    protected boolean playedStartSound;
    protected boolean turnIntoObject;

    public BabylonTowerFallingCrystalAttackEvent() {
    }

    public BabylonTowerFallingCrystalAttackEvent(Mob owner, int x, int y, GameRandom uniqueIDRandom, GameDamage damage) {
        super(owner, uniqueIDRandom);
        this.spawnTime = owner.getWorldEntity().getTime();
        this.x = x;
        this.y = y;
        this.damage = damage;
        this.turnIntoObject = GameRandom.globalRandom.getChance(0.05f);
    }

    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.spawnTime = reader.getNextLong();
        this.x = reader.getNextInt();
        this.y = reader.getNextInt();
        this.turnIntoObject = reader.getNextBoolean();
    }

    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextLong(this.spawnTime);
        writer.putNextInt(this.x);
        writer.putNextInt(this.y);
        writer.putNextBoolean(this.turnIntoObject);
    }

    public void init() {
        super.init();
        if (this.isClient()) {
            this.level.entityManager.addParticle((Particle)new BabylonTowerFallingCrystalParticle(this.level, this.x, this.y, this.spawnTime, 1000L), Particle.GType.CRITICAL);
        }
    }

    public void clientTick() {
        if (!this.isOver()) {
            long eventTime = this.level.getWorldEntity().getTime() - this.spawnTime;
            if (eventTime > 1000L && !this.playedStartSound) {
                SoundManager.playSound((GameSound)GameResources.magicbolt2, (SoundEffect)SoundEffect.effect((float)this.x, (float)this.y));
                this.playedStartSound = true;
            }
            if (eventTime > 1200L) {
                SoundManager.playSound((GameSound)GameResources.firespell1, (SoundEffect)SoundEffect.effect((float)this.x, (float)this.y).volume(0.5f));
                this.over();
            }
        }
    }

    public void serverTick() {
        long eventTime;
        if (!this.isOver() && (eventTime = this.level.getWorldEntity().getTime() - this.spawnTime) > 1200L) {
            Ellipse2D.Float hitBox = new Ellipse2D.Float(this.x - 38, this.y - 30, 76.0f, 60.0f);
            GameUtils.streamTargets((Mob)this.owner, (Shape)GameUtils.rangeTileBounds((int)this.x, (int)this.y, (int)5)).filter(m -> m.canBeHit((Attacker)this.owner) && hitBox.intersects(m.getHitBox())).forEach(m -> {
                m.addBuff(new ActiveBuff(BuffRegistry.Debuffs.BROKEN_ARMOR, m, 5000, (Attacker)this), true);
                m.isServerHit(this.damage, (float)(m.getX() - this.x), (float)(m.getY() - this.y), 100.0f, (Attacker)this.owner);
            });
            this.over();
        }
    }

    public GameMessage getAttackerName() {
        return this.owner != null ? this.owner.getAttackerName() : new StaticMessage("BT_BOMB_ATTACK");
    }

    public DeathMessageTable getDeathMessages() {
        return this.owner != null ? this.owner.getDeathMessages() : DeathMessageTable.fromRange((String)"generic", (int)8);
    }

    public Mob getFirstAttackOwner() {
        return this.owner;
    }

    public void over() {
        int tileX = this.x / 32;
        int tileY = this.y / 32;
        if (this.turnIntoObject && (this.level.getObject(tileX, tileY).getID() == 0 || this.level.getObject((int)tileX, (int)tileY).isGrass)) {
            ObjectRegistry.getObject((String)"spinelclustersmall").placeObject(this.level, tileX, tileY, GameRandom.globalRandom.getIntBetween(0, 3), false);
        }
        super.over();
    }
}

