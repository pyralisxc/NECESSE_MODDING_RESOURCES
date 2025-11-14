/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile.pathProjectile;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ElectricOrbEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.entity.projectile.pathProjectile.PathProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;

public class StaticJellyfishProjectile
extends PathProjectile {
    protected float startX;
    protected float startY;
    protected float endX;
    protected float endY;
    protected float frequency;
    protected float amplitude;
    protected SoundPlayer playingSound;

    public StaticJellyfishProjectile() {
    }

    public StaticJellyfishProjectile(float startX, float startY, float endX, float endY, float maxFrequency, float amplitude, float speed, GameDamage damage, Mob owner) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        float distance = GameMath.getExactDistance(startX, startY, endX, endY);
        int periods = Math.max(1, (int)(distance / (maxFrequency * 2.0f)));
        this.frequency = distance / (float)periods;
        this.amplitude = amplitude;
        this.speed = speed;
        this.setDistance((int)distance);
        this.setDamage(damage);
        this.setOwner(owner);
    }

    @Override
    public void init() {
        super.init();
        this.height = 0.0f;
        this.piercing = 1000;
        this.isSolid = false;
        this.canHitMobs = false;
        this.spinningTypeSwitcher = new ParticleTypeSwitcher(Particle.GType.IMPORTANT_COSMETIC, Particle.GType.CRITICAL);
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(this.startX);
        writer.putNextFloat(this.startY);
        writer.putNextFloat(this.endX);
        writer.putNextFloat(this.endY);
        writer.putNextFloat(this.frequency);
        writer.putNextFloat(this.amplitude);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.startX = reader.getNextFloat();
        this.startY = reader.getNextFloat();
        this.endX = reader.getNextFloat();
        this.endY = reader.getNextFloat();
        this.frequency = reader.getNextFloat();
        this.amplitude = reader.getNextFloat();
    }

    @Override
    public Point2D.Float getPosition(double dist) {
        float currentAmplitude;
        double currentDist = (double)this.traveledDistance + dist;
        double period = currentDist / (double)this.frequency;
        Point2D.Float dir = GameMath.normalize(this.endX - this.startX, this.endY - this.startY);
        Point2D.Float perpDir = GameMath.getPerpendicularDir(dir.x, dir.y);
        float currentCenterX = (float)((double)this.startX + (double)dir.x * currentDist);
        float currentCenterY = (float)((double)this.startY + (double)dir.y * currentDist);
        float periodProgress = (float)(period % 1.0);
        if (periodProgress < 0.25f) {
            float progress = GameMath.clamp(periodProgress, 0.0f, 0.25f);
            currentAmplitude = progress * this.amplitude;
        } else if (periodProgress < 0.75f) {
            float progress = GameMath.clamp(periodProgress, 0.25f, 0.5f) - 1.0f;
            currentAmplitude = progress * -this.amplitude;
        } else {
            float progress = GameMath.clamp(periodProgress, 1.0f, 0.75f);
            currentAmplitude = progress * -this.amplitude;
        }
        return new Point2D.Float(currentCenterX + perpDir.x * currentAmplitude, currentCenterY + perpDir.y * currentAmplitude);
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.playingSound == null || this.playingSound.isDone()) {
            this.playingSound = SoundManager.playSound(GameResources.electricLoop, (SoundEffect)SoundEffect.effect(this).pitch(1.3f));
        }
        if (this.playingSound != null) {
            this.playingSound.refreshLooping(1.0f);
        }
    }

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        ElectricOrbEvent event = new ElectricOrbEvent(this.getOwner(), (int)x, (int)y, GameRandom.globalRandom, this.getDamage());
        this.getLevel().entityManager.events.add(event);
    }

    @Override
    protected void modifySpinningParticle(ParticleOption particle) {
        super.modifySpinningParticle(particle);
        particle.sprite(GameResources.magicSparkParticles.sprite(GameRandom.globalRandom.nextInt(4), 0, 22)).sizeFades(15, 20);
    }

    @Override
    protected int getExtraSpinningParticles() {
        return 2;
    }

    @Override
    public Color getParticleColor() {
        return new Color(95, 205, 228);
    }

    @Override
    public Trail getTrail() {
        return null;
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
    }
}

