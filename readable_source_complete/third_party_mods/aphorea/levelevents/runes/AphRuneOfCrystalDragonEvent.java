/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.Packet
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.registries.DamageTypeRegistry
 *  necesse.engine.util.GameMath
 *  necesse.engine.util.GameRandom
 *  necesse.engine.util.GameUtils
 *  necesse.engine.util.Ray
 *  necesse.engine.util.RayLinkedList
 *  necesse.entity.ParticleBeamHandler
 *  necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.MobHitCooldowns
 *  necesse.gfx.GameResources
 *  necesse.gfx.gameTexture.GameSprite
 *  necesse.level.maps.Level
 *  necesse.level.maps.LevelObjectHit
 */
package aphorea.levelevents.runes;

import aphorea.utils.AphColors;
import java.awt.Shape;
import java.awt.geom.Point2D;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.Ray;
import necesse.engine.util.RayLinkedList;
import necesse.entity.ParticleBeamHandler;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;

public class AphRuneOfCrystalDragonEvent
extends MobAbilityLevelEvent {
    protected float endDistance;
    protected float effectNumber;
    protected int knockback;
    protected int aliveTime;
    protected float laserAngle;
    protected float currentDistance;
    protected int ticker;
    protected float expandSpeed = 150.0f;
    protected MobHitCooldowns hitCooldowns = new MobHitCooldowns();
    private ParticleBeamHandler beamHandler;

    public AphRuneOfCrystalDragonEvent() {
    }

    public AphRuneOfCrystalDragonEvent(Mob owner, GameRandom uniqueIDRandom, float endDistance, float effectNumber, int knockback, int aliveTime, float laserAngle) {
        super(owner, uniqueIDRandom);
        this.endDistance = endDistance;
        this.effectNumber = effectNumber;
        this.knockback = knockback;
        this.aliveTime = aliveTime;
        this.laserAngle = laserAngle;
    }

    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(this.endDistance);
        writer.putNextFloat(this.effectNumber);
        writer.putNextInt(this.knockback);
        writer.putNextFloat(this.currentDistance);
        writer.putNextShortUnsigned(this.ticker);
        writer.putNextInt(this.aliveTime);
        writer.putNextFloat(this.laserAngle);
    }

    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.endDistance = reader.getNextFloat();
        this.effectNumber = reader.getNextFloat();
        this.knockback = reader.getNextInt();
        this.currentDistance = reader.getNextFloat();
        this.ticker = reader.getNextShortUnsigned();
        this.aliveTime = reader.getNextInt();
        this.laserAngle = reader.getNextFloat();
    }

    public boolean isNetworkImportant() {
        return true;
    }

    public void tickMovement(float delta) {
        super.tickMovement(delta);
        if (!this.isOver() && this.owner.isPlayer) {
            if (this.currentDistance < this.endDistance) {
                this.currentDistance = Math.min(this.endDistance, this.currentDistance + this.expandSpeed * delta / 250.0f);
            }
            Point2D.Float dir = GameMath.getAngleDir((float)this.laserAngle);
            RayLinkedList rays = GameUtils.castRay((Level)this.level, (double)this.owner.x, (double)this.owner.y, (double)dir.x, (double)dir.y, (double)this.currentDistance, (int)0, null);
            for (Ray levelObjectHitRay : rays) {
                this.handleHits((Shape)levelObjectHitRay, this::canHit, null);
            }
            if (this.isClient()) {
                this.updateTrail((RayLinkedList<LevelObjectHit>)rays, this.level.tickManager().getDelta());
            }
        }
    }

    public void clientTick() {
        super.clientTick();
        if (this.owner == null || this.owner.removed()) {
            this.over();
        }
        ++this.ticker;
        if (this.ticker * 50 >= this.aliveTime) {
            this.over();
        }
    }

    public void serverTick() {
        super.serverTick();
        if (this.owner == null || this.owner.removed()) {
            this.over();
        }
        ++this.ticker;
        if (this.ticker * 50 >= this.aliveTime) {
            this.over();
        }
    }

    public boolean canHit(Mob mob) {
        return mob.canBeHit((Attacker)this) && this.hitCooldowns.canHit(mob);
    }

    public void clientHit(Mob target, Packet content) {
        super.clientHit(target, content);
        this.hitCooldowns.startCooldown(target);
        target.startHitCooldown();
    }

    public void serverHit(Mob target, Packet content, boolean clientSubmitted) {
        this.hitCooldowns.startCooldown(target);
        float modifier = target.getKnockbackModifier();
        if (modifier != 0.0f) {
            float damagePercent = this.effectNumber;
            if (target.isBoss()) {
                damagePercent /= 50.0f;
            } else if (target.isPlayer || target.isHuman) {
                damagePercent /= 5.0f;
            }
            target.isServerHit(new GameDamage(DamageTypeRegistry.TRUE, (float)target.getMaxHealth() * damagePercent), target.x - this.owner.x, target.y - this.owner.y, (float)this.knockback, (Attacker)this.owner);
        }
    }

    private void updateTrail(RayLinkedList<LevelObjectHit> rays, float delta) {
        if (this.beamHandler == null) {
            this.beamHandler = new ParticleBeamHandler(this.level).color(AphColors.diamond).thickness(160, 80).speed(100.0f).sprite(new GameSprite(GameResources.chains, 7, 0, 32));
        }
        this.beamHandler.update(rays, delta);
    }

    public void over() {
        if (this.beamHandler != null) {
            this.beamHandler.dispose();
        }
        super.over();
    }
}

