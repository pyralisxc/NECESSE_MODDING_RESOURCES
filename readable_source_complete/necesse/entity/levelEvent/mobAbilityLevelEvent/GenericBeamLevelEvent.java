/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Collections;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.sound.LinesSoundEmitter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.LineHitbox;
import necesse.engine.util.Ray;
import necesse.engine.util.RayLinkedList;
import necesse.entity.ParticleBeamHandler;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.LevelObjectHit;

public abstract class GenericBeamLevelEvent
extends MobAbilityLevelEvent
implements LinesSoundEmitter {
    public int seed;
    public long startTime;
    public int sweepTime;
    public float startAngle;
    public float endAngle;
    protected int bounces;
    protected float distance;
    protected GameDamage damage;
    protected int knockback;
    protected MobHitCooldowns hitCooldowns;
    private RayLinkedList<LevelObjectHit> lastRays;
    private ParticleBeamHandler beamHandler;
    private SoundPlayer sound;

    public GenericBeamLevelEvent() {
    }

    public GenericBeamLevelEvent(Mob owner, float startAngle, float endAngle, long startTime, int sweepTime, int seed, float distance, GameDamage damage, int knockback, int hitCooldown, int bounces) {
        super(owner, new GameRandom(seed));
        this.startAngle = startAngle;
        this.endAngle = endAngle;
        this.startTime = startTime;
        this.sweepTime = sweepTime;
        this.seed = seed;
        this.distance = distance;
        this.damage = damage;
        this.knockback = knockback;
        this.hitCooldowns = new MobHitCooldowns(hitCooldown);
        this.bounces = Math.min(bounces, 100);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.seed = reader.getNextShortUnsigned();
        this.startAngle = reader.getNextFloat();
        this.endAngle = reader.getNextFloat();
        this.startTime = reader.getNextLong();
        this.sweepTime = reader.getNextInt();
        this.distance = reader.getNextFloat();
        this.bounces = reader.getNextByteUnsigned();
        this.hitCooldowns = new MobHitCooldowns(reader.getNextInt());
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextShortUnsigned(this.seed);
        writer.putNextFloat(this.startAngle);
        writer.putNextFloat(this.endAngle);
        writer.putNextLong(this.startTime);
        writer.putNextInt(this.sweepTime);
        writer.putNextFloat(this.distance);
        writer.putNextByteUnsigned(this.bounces);
        writer.putNextInt(this.hitCooldowns.hitCooldown);
    }

    @Override
    public boolean isNetworkImportant() {
        return true;
    }

    public int getTimeSinceStart() {
        return (int)(this.level.getTime() - this.startTime);
    }

    public int getTimeLeft() {
        return Math.max(0, this.sweepTime - this.getTimeSinceStart());
    }

    public float getCurrentAngle() {
        long timeSinceStart = this.getTimeSinceStart();
        if (timeSinceStart >= (long)this.sweepTime) {
            this.over();
            return this.endAngle;
        }
        float progress = this.easeProgress(Math.max(0.0f, (float)timeSinceStart / (float)this.sweepTime));
        float angleDelta = this.endAngle - this.startAngle;
        return this.startAngle + angleDelta * progress;
    }

    public float easeProgress(float progress) {
        return progress;
    }

    @Override
    public void tickMovement(float delta) {
        RayLinkedList<LevelObjectHit> rays;
        super.tickMovement(delta);
        if (this.owner.removed()) {
            this.over();
        }
        if ((rays = this.checkRayHitbox()) != null && this.isClient()) {
            this.updateTrail(rays, this.level.tickManager().getDelta());
        }
    }

    public SoundSettings getSound() {
        return new SoundSettings(GameResources.laserBeam1).fallOffDistance(750).volume(0.8f);
    }

    @Override
    public void clientTick() {
        SoundSettings newSound;
        super.clientTick();
        if (this.isOver()) {
            return;
        }
        boolean soundCreated = false;
        if ((this.sound == null || this.sound.isDone()) && (newSound = this.getSound()) != null) {
            this.sound = SoundManager.playSound(newSound, SoundEffect.effect(this), sp -> sp.fadeIn(1.0f));
            soundCreated = true;
        }
        if (this.sound != null) {
            long currentTime = this.level.getTime();
            long timeRemaining = this.startTime + (long)this.sweepTime - currentTime;
            if (timeRemaining >= 500L) {
                this.sound.refreshLooping(1.0f);
            }
            if (soundCreated) {
                this.sound.refreshLooping((float)timeRemaining / 1000.0f);
            }
        }
    }

    public Point2D.Float getBeamStartPosition(float currentAngle, Point2D.Float dir) {
        return new Point2D.Float(this.owner.x, this.owner.y);
    }

    private RayLinkedList<LevelObjectHit> checkRayHitbox() {
        float currentAngle = this.getCurrentAngle();
        if (this.isOver()) {
            return null;
        }
        Point2D.Float dir = GameMath.getAngleDir(currentAngle);
        Point2D.Float position = this.getBeamStartPosition(currentAngle, dir);
        this.lastRays = GameUtils.castRay(this.level, (double)position.x, (double)position.y, (double)dir.x, (double)dir.y, (double)this.distance, this.bounces, this.getCollisionFilter());
        int getHitboxWidth = this.getHitboxWidth();
        if (getHitboxWidth > 0) {
            for (Ray ray : this.lastRays) {
                this.handleHits(new LineHitbox(ray, getHitboxWidth), this::canHit, null);
            }
        }
        return this.lastRays;
    }

    public CollisionFilter getCollisionFilter() {
        return new CollisionFilter().projectileCollision();
    }

    public int getHitboxWidth() {
        return 10;
    }

    @Override
    public int getHitCooldown(LevelObjectHit hit) {
        return this.hitCooldowns.hitCooldown;
    }

    @Override
    public void hit(LevelObjectHit hit) {
        super.hit(hit);
        hit.getLevelObject().attackThrough(this.damage, this.owner);
    }

    public boolean canHit(Mob mob) {
        return mob.canBeHit(this) && this.hitCooldowns.canHit(mob);
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
        target.isServerHit(this.damage, target.x - this.owner.x, target.y - this.owner.y, this.knockback, this.owner);
    }

    private void updateTrail(RayLinkedList<LevelObjectHit> rays, float delta) {
        if (this.beamHandler == null) {
            this.beamHandler = new ParticleBeamHandler(this.level).sprite(new GameSprite(GameResources.chains, 7, 0, 32)).speed(this.distance / 6.0f).thickness(40, 5);
            this.modifyTrail(this.beamHandler);
        }
        this.beamHandler.update(rays, delta);
    }

    public abstract void modifyTrail(ParticleBeamHandler var1);

    @Override
    public void over() {
        if (this.beamHandler != null) {
            this.beamHandler.dispose();
        }
        super.over();
    }

    @Override
    public Iterable<Line2D> getSoundLines() {
        if (this.lastRays == null) {
            return Collections.emptyList();
        }
        return GameUtils.mapIterable(this.lastRays.iterator(), ray -> ray);
    }
}

