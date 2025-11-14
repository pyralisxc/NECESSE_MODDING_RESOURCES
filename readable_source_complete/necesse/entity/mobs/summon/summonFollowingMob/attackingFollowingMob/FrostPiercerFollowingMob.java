/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerFlyingFollowerValidTargetCollisionChaserAI;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.FlyingAttackingFollowingMob;
import necesse.entity.particle.Particle;
import necesse.entity.trails.Trail;
import necesse.entity.trails.TrailVector;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class FrostPiercerFollowingMob
extends FlyingAttackingFollowingMob {
    public Trail trail;
    public float moveAngle;
    private float toMove;

    public FrostPiercerFollowingMob() {
        super(10);
        this.moveAccuracy = 15;
        this.setSpeed(160.0f);
        this.setFriction(2.0f);
        this.collision = new Rectangle(-18, -15, 36, 30);
        this.hitBox = new Rectangle(-18, -15, 36, 36);
        this.selectBox = new Rectangle();
    }

    @Override
    public GameDamage getCollisionDamage(Mob target, boolean fromPacket, ServerClient packetSubmitter) {
        return this.summonDamage;
    }

    @Override
    public int getCollisionKnockback(Mob target) {
        return 15;
    }

    @Override
    public void handleCollisionHit(Mob target, GameDamage damage, int knockback) {
        Mob owner = this.getAttackOwner();
        if (owner != null && target != null) {
            target.isServerHit(damage, target.x - owner.x, target.y - owner.y, knockback, this);
            this.collisionHitCooldowns.startCooldown(target);
            this.remove(0.0f, 0.0f, null, true);
        }
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<FrostPiercerFollowingMob>(this, new PlayerFlyingFollowerValidTargetCollisionChaserAI<FrostPiercerFollowingMob>(448, null, 15, 500, 640, 64){

            @Override
            public boolean isValidTarget(FrostPiercerFollowingMob mob, ItemAttackerMob owner, Mob target) {
                if (owner == null) {
                    return false;
                }
                Object result = GameUtils.castRayFirstHit(new Line2D.Float(owner.x, owner.y, target.x, target.y), 100.0, line -> {
                    CollisionFilter collisionFilter = mob.modifyChasingCollisionFilter(new CollisionFilter().projectileCollision(), target);
                    if (mob.getLevel().collides((Line2D)line, collisionFilter)) {
                        return new Object();
                    }
                    return null;
                });
                return result == null;
            }
        }, new FlyingAIMover());
        if (this.isClient()) {
            this.trail = new Trail(this, this.getLevel(), new Color(48, 123, 158), 16.0f, 200, 0.0f);
            this.trail.drawOnTop = true;
            this.trail.removeOnFadeOut = false;
            this.getLevel().entityManager.addTrail(this.trail);
        }
    }

    @Override
    public void tickMovement(float delta) {
        this.toMove += delta;
        while (this.toMove > 4.0f) {
            float oldX = this.x;
            float oldY = this.y;
            super.tickMovement(4.0f);
            this.toMove -= 4.0f;
            Point2D.Float d = GameMath.normalize(oldX - this.x, oldY - this.y);
            this.moveAngle = (float)Math.toDegrees(Math.atan2(d.y, d.x)) - 90.0f;
            if (this.trail == null) continue;
            float trailOffset = 5.0f;
            this.trail.addPoint(new TrailVector(this.x + d.x * trailOffset, this.y + d.y * trailOffset, -d.x, -d.y, this.trail.thickness, 0.0f));
        }
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.getLevel().entityManager.addParticle(this.x + (float)(GameRandom.globalRandom.nextGaussian() * 4.0), this.y + (float)(GameRandom.globalRandom.nextGaussian() * 4.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(this.dx / 10.0f, this.dy / 10.0f).color(new Color(48, 123, 158));
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 30; ++i) {
            this.getLevel().entityManager.addParticle(this.x, this.y, Particle.GType.COSMETIC).movesConstantAngle(GameRandom.globalRandom.nextInt(360), GameRandom.globalRandom.getIntBetween(5, 20)).color(new Color(48, 123, 158));
        }
    }

    @Override
    protected SoundSettings getDeathSound() {
        return new SoundSettings(GameResources.frostPiercerHit).volume(0.6f);
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(FrostPiercerFollowingMob.getTileCoordinate(x), FrostPiercerFollowingMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 16;
        int drawY = camera.getDrawY(y) - 20;
        TextureDrawOptionsEnd body = MobRegistry.Textures.frostPiercer.initDraw().light(light).rotate(this.moveAngle, 16, 20).pos(drawX, drawY);
        topList.add(tm -> body.draw());
    }

    @Override
    public void dispose() {
        super.dispose();
        if (this.trail != null) {
            this.trail.removeOnFadeOut = true;
        }
    }
}

