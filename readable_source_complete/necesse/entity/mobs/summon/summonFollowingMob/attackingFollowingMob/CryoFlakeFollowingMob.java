/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.leaves.CooldownAttackTargetAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerFlyingFollowerShooterChaserAI;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.FlyingAttackingFollowingMob;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.CryoMissileProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class CryoFlakeFollowingMob
extends FlyingAttackingFollowingMob {
    public CryoFlakeFollowingMob() {
        super(10);
        this.accelerationMod = 1.0f;
        this.moveAccuracy = 10;
        this.setSpeed(70.0f);
        this.setFriction(1.0f);
        this.collision = new Rectangle(-18, -15, 36, 30);
        this.hitBox = new Rectangle(-18, -15, 36, 36);
        this.selectBox = new Rectangle(-20, -18, 40, 36);
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<CryoFlakeFollowingMob>(this, new PlayerFlyingFollowerShooterChaserAI<CryoFlakeFollowingMob>(576, CooldownAttackTargetAINode.CooldownTimer.TICK, 800, 480, 640, 64){

            @Override
            protected boolean shootAtTarget(CryoFlakeFollowingMob mob, Mob target) {
                CryoMissileProjectile entity = new CryoMissileProjectile(mob.getLevel(), mob, mob.x, mob.y, target.x, target.y, 150.0f, 800, CryoFlakeFollowingMob.this.summonDamage, 10);
                entity.setTargetPrediction(target);
                mob.getLevel().entityManager.projectiles.add(entity);
                return true;
            }
        }, new FlyingAIMover());
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 30; ++i) {
            this.getLevel().entityManager.addParticle(this.x, this.y, Particle.GType.IMPORTANT_COSMETIC).movesConstant(GameRandom.globalRandom.getIntBetween(5, 20) * (GameRandom.globalRandom.nextBoolean() ? -1 : 1), GameRandom.globalRandom.getIntBetween(5, 20) * (GameRandom.globalRandom.nextBoolean() ? -1 : 1)).color(new Color(88, 105, 218));
        }
    }

    @Override
    public void playDeathSound() {
        this.playHitSound();
    }

    @Override
    public void playHitSound() {
        float pitch = GameRandom.globalRandom.getOneOf(Float.valueOf(0.95f), Float.valueOf(1.0f), Float.valueOf(1.05f)).floatValue();
        SoundManager.playSound(GameResources.jinglehit, (SoundEffect)SoundEffect.effect(this).pitch(pitch));
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(CryoFlakeFollowingMob.getTileCoordinate(x), CryoFlakeFollowingMob.getTileCoordinate(y));
        Point p = new Point(MobRegistry.Textures.cryoFlakePet.getWidth() / 2, MobRegistry.Textures.cryoFlakePet.getHeight() / 2);
        int drawX = camera.getDrawX(x) - p.x;
        int drawY = camera.getDrawY(y) - p.y;
        float rotation = GameUtils.getTimeRotation(level.getWorldEntity().getTime(), 4);
        TextureDrawOptionsEnd body = MobRegistry.Textures.cryoFlakePet.initDraw().rotate(rotation, p.x, p.y).light(light).pos(drawX, drawY);
        topList.add(tm -> body.draw());
    }
}

