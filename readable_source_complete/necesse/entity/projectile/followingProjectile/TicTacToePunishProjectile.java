/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile.followingProjectile;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.stream.Stream;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.followingProjectile.FollowingProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;

public class TicTacToePunishProjectile
extends FollowingProjectile {
    private boolean isXProjectile;
    private float startVelocity;
    private float maxVelocity;

    public TicTacToePunishProjectile() {
    }

    public TicTacToePunishProjectile(Level level, float x, float y, Mob target, int seed, float speed, GameDamage damage, int knockback, boolean isXProjectile) {
        this();
        this.setLevel(level);
        this.x = x;
        this.y = y;
        Point2D.Float dir = GameMath.normalize((float)target.getX() - x, (float)target.getY() - y);
        GameRandom random = new GameRandom(seed);
        this.setTarget(x - dir.x * 100.0f + random.getFloatBetween(-5.0f, 5.0f), y - dir.y * 100.0f + random.getFloatBetween(-5.0f, 5.0f));
        this.target = target;
        this.startVelocity = speed / 15.0f;
        this.maxVelocity = speed;
        this.speed = this.startVelocity;
        this.setDamage(damage);
        this.setDistance(4000);
        this.knockback = knockback;
        this.isXProjectile = isXProjectile;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(this.startVelocity);
        writer.putNextFloat(this.maxVelocity);
        writer.putNextBoolean(this.isXProjectile);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.startVelocity = reader.getNextFloat();
        this.maxVelocity = reader.getNextFloat();
        this.isXProjectile = reader.getNextBoolean();
    }

    @Override
    public void init() {
        super.init();
        this.turnSpeed = 0.75f;
        this.isSolid = false;
        this.givesLight = true;
        this.height = 16.0f;
        this.piercing = 0;
        this.trailOffset = 0.0f;
        this.particleDirOffset = -24.0f;
        this.setWidth(8.0f);
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (this.target == null) {
            this.remove();
        }
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.target == null) {
            this.remove();
        }
    }

    @Override
    public void onMoveTick(Point2D.Float startPos, double movedDist) {
        super.onMoveTick(startPos, movedDist);
        float progress = GameMath.limit(this.traveledDistance / 300.0f, 0.0f, 1.0f);
        this.speed = GameMath.lerp(progress, this.startVelocity, this.maxVelocity);
    }

    @Override
    public float getTurnSpeed(int targetX, int targetY, float delta) {
        return this.getTurnSpeed(delta) * this.dynamicTurnSpeedMod(targetX, targetY, this.getTurnRadius());
    }

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (mob != null && this.isClient()) {
            SoundManager.playSound(GameResources.explosionLight, (SoundEffect)SoundEffect.effect(this).falloffDistance(1400).volume(mob.isPlayer ? 0.6f : 1.5f));
            SoundManager.playSound(GameResources.electricExplosion, (SoundEffect)SoundEffect.effect(this).falloffDistance(1400).volume(mob.isPlayer ? 0.6f : 1.5f));
            for (int i = 0; i < 30; ++i) {
                int lifeTime = GameRandom.globalRandom.getIntBetween(500, 3000);
                float lifePerc = (float)lifeTime / 3000.0f;
                float startHeight = 10.0f;
                float height = startHeight + (float)GameRandom.globalRandom.getIntBetween(50, 100) * lifePerc;
                this.getLevel().entityManager.addTopParticle(x + GameRandom.globalRandom.getFloatBetween(-10.0f, 10.0f), y + GameRandom.globalRandom.getFloatBetween(-5.0f, 5.0f), Particle.GType.IMPORTANT_COSMETIC).sizeFades(20, 30).movesFriction(GameRandom.globalRandom.getFloatBetween(-40.0f, 40.0f) + this.dx * this.speed / 4.0f, GameRandom.globalRandom.getFloatBetween(-20.0f, 20.0f) + this.dy * this.speed / 4.0f, 0.5f).heightMoves(startHeight, height).colorRandom(this.getParticleColor(), 10.0f, 0.1f, 0.1f).lifeTime(lifeTime);
            }
        }
    }

    @Override
    protected int getExtraSpinningParticles() {
        return 5;
    }

    @Override
    public Color getParticleColor() {
        if (this.isXProjectile) {
            return new Color(255, 125, 131);
        }
        return new Color(148, 225, 255);
    }

    @Override
    public Trail getTrail() {
        Trail trail = new Trail(this, this.getLevel(), this.getParticleColor(), 12.0f, 500, 18.0f);
        trail.drawOnTop = true;
        return trail;
    }

    @Override
    public boolean canHit(Mob mob) {
        return mob == this.target;
    }

    @Override
    protected Stream<Mob> streamTargets(Mob owner, Shape hitBounds) {
        if (this.target instanceof Mob) {
            return Stream.of((Mob)this.target);
        }
        return super.streamTargets(owner, hitBounds);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
    }
}

