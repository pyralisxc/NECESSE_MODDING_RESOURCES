/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerFlyingFollowerCollisionChaserAI;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.FlyingAttackingFollowingMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class DuskMoonDiscFollowingMob
extends FlyingAttackingFollowingMob {
    public int lifeTime = 5000;
    private int currentRotation;
    private float toMove;
    private float particleBuffer;

    public DuskMoonDiscFollowingMob() {
        super(10);
        this.moveAccuracy = 15;
        this.setSpeed(160.0f);
        this.setFriction(2.0f);
        this.collision = new Rectangle(-16, -16, 36, 36);
        this.hitBox = new Rectangle(-16, -16, 36, 36);
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
    public void serverTick() {
        super.serverTick();
        this.lifeTime -= 50;
        if (this.lifeTime <= 0) {
            this.remove(0.0f, 0.0f, null, true);
        }
    }

    @Override
    public void tickMovement(float delta) {
        this.toMove += delta;
        while (this.toMove > 4.0f) {
            super.tickMovement(4.0f);
            this.toMove -= 4.0f;
            this.currentRotation -= 4;
            if (!this.isClient()) continue;
            this.spawnTrailParticles();
        }
    }

    private void spawnTrailParticles() {
        if (this.particleBuffer > 10.0f) {
            GameRandom random = GameRandom.globalRandom;
            this.getLevel().entityManager.addParticle(this.x + (float)random.getIntBetween(-8, 8), this.y + (float)random.getIntBetween(-8, 8), Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.magicSparkParticles.sprite(random.nextInt(3), 0, 22)).sizeFades(10, 20).movesConstant(-this.dx / (float)random.getIntBetween(5, 10), -this.dy / (float)random.getIntBetween(5, 10)).height(10.0f).color(new Color(220, 212, 255)).givesLight(0.0f, 0.0f).lifeTime(2000);
            this.particleBuffer = 0.0f;
        }
        this.particleBuffer += 1.0f;
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        super.spawnDeathParticles(knockbackX, knockbackY);
        for (int i = 0; i < 30; ++i) {
            float dirX = GameRandom.globalRandom.getFloatBetween(-30.0f, 30.0f);
            float dirY = GameRandom.globalRandom.getFloatBetween(-30.0f, 30.0f);
            this.getLevel().entityManager.addParticle(this.x, this.y, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.magicSparkParticles.sprite(GameRandom.globalRandom.nextInt(3), 0, 22)).sizeFades(10, 20).movesConstant(dirX, dirY).height(10.0f).givesLight(0.0f, 0.0f).lifeTime(2000);
        }
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<DuskMoonDiscFollowingMob>(this, new PlayerFlyingFollowerCollisionChaserAI(576, null, 15, 500, 640, 64), new FlyingAIMover());
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(DuskMoonDiscFollowingMob.getTileCoordinate(x), DuskMoonDiscFollowingMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 16;
        int drawY = camera.getDrawY(y) - 20;
        TextureDrawOptionsEnd body = MobRegistry.Textures.duskMoonDisc.initDraw().light(light).rotate(this.currentRotation, 16, 16).pos(drawX, drawY);
        topList.add(tm -> body.draw());
    }
}

