/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Color;
import java.awt.geom.Point2D;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.Ray;
import necesse.engine.util.RayLinkedList;
import necesse.entity.ParticleBeamHandler;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.LevelObjectHit;

public class CrystalDragonLaserLevelEvent
extends MobAbilityLevelEvent {
    protected float currentDistance;
    protected float endDistance;
    protected float expandSpeed = 150.0f;
    protected GameDamage damage;
    protected int knockback;
    protected int ticker;
    protected int aliveTime;
    protected float lastLaserAngle = Float.MIN_VALUE;
    protected MobHitCooldowns hitCooldowns = new MobHitCooldowns();
    private ParticleBeamHandler beamHandler;

    public CrystalDragonLaserLevelEvent() {
    }

    public CrystalDragonLaserLevelEvent(Mob owner, GameRandom uniqueIDRandom, float endDistance, GameDamage damage, int knockback, int aliveTime) {
        super(owner, uniqueIDRandom);
        this.endDistance = endDistance;
        this.damage = damage;
        this.knockback = knockback;
        this.aliveTime = aliveTime;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(this.endDistance);
        this.damage.writePacket(writer);
        writer.putNextInt(this.knockback);
        writer.putNextFloat(this.currentDistance);
        writer.putNextShortUnsigned(this.ticker);
        writer.putNextInt(this.aliveTime);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.endDistance = reader.getNextFloat();
        this.damage = GameDamage.fromReader(reader);
        this.knockback = reader.getNextInt();
        this.currentDistance = reader.getNextFloat();
        this.ticker = reader.getNextShortUnsigned();
        this.aliveTime = reader.getNextInt();
    }

    @Override
    public boolean isNetworkImportant() {
        return true;
    }

    @Override
    public void tickMovement(float delta) {
        super.tickMovement(delta);
        if (this.isOver()) {
            return;
        }
        if (this.currentDistance < this.endDistance) {
            this.currentDistance = Math.min(this.endDistance, this.currentDistance + this.expandSpeed * delta / 250.0f);
        }
        float nextLaserAngle = GameMath.getAngle(GameMath.normalize(this.owner.dx, this.owner.dy));
        if (this.lastLaserAngle == Float.MIN_VALUE) {
            this.lastLaserAngle = nextLaserAngle;
        }
        float angleDelta = GameMath.getAngleDifference(this.lastLaserAngle, nextLaserAngle);
        float circumference = (float)(Math.PI * (double)this.currentDistance);
        float distancePerHitDetection = 5.0f;
        float anglePerHitDetection = distancePerHitDetection / circumference * 360.0f;
        int hitDetectionIterations = Math.max((int)(Math.abs(angleDelta) / anglePerHitDetection), 1);
        for (int i = 1; i <= hitDetectionIterations; ++i) {
            float angle = GameMath.lerp((float)i / (float)hitDetectionIterations, this.lastLaserAngle, this.lastLaserAngle + angleDelta);
            Point2D.Float dir = GameMath.getAngleDir(angle);
            RayLinkedList<LevelObjectHit> rays = GameUtils.castRay(this.level, (double)this.owner.x, (double)this.owner.y, (double)dir.x, (double)dir.y, (double)this.currentDistance, 0, null);
            for (Ray ray : rays) {
                this.handleHits(ray, this::canHit, null);
            }
            if (!this.isClient() || i != hitDetectionIterations) continue;
            this.updateTrail(rays, this.level.tickManager().getDelta(), this.currentDistance);
        }
        this.lastLaserAngle = nextLaserAngle;
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.owner == null || this.owner.removed()) {
            this.over();
        }
        ++this.ticker;
        if (this.ticker * 50 >= this.aliveTime) {
            this.over();
        }
        if (this.ticker % 2 == 0) {
            SoundManager.playSound(GameResources.crystalHit2, (SoundEffect)SoundEffect.effect(this.owner.x, this.owner.y).volume(0.9f).falloffDistance(2000));
        }
    }

    @Override
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
        if (!mob.canBeHit(this)) {
            return false;
        }
        return this.hitCooldowns.canHit(mob);
    }

    @Override
    public void clientHit(Mob target, Packet content) {
        super.clientHit(target, content);
        this.hitCooldowns.startCooldown(target);
        target.startHitCooldown();
    }

    @Override
    public void serverHit(Mob target, Packet content, boolean clientSubmitted) {
        this.hitCooldowns.startCooldown(target);
        target.isServerHit(this.damage, this.owner.dx, this.owner.dy, this.knockback, this.owner);
    }

    private void updateTrail(RayLinkedList<LevelObjectHit> rays, float delta, float dist) {
        if (this.beamHandler == null) {
            this.beamHandler = new ParticleBeamHandler(this.level).color(new Color(200, 200, 255)).thickness(80, 40).speed(100.0f).sprite(new GameSprite(GameResources.chains, 7, 0, 32));
        }
        this.beamHandler.update(rays, delta);
    }

    @Override
    public void over() {
        if (this.beamHandler != null) {
            this.beamHandler.dispose();
        }
        super.over();
    }
}

