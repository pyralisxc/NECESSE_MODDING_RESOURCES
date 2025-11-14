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
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.leaves.PlayerFlyingFollowerAINode;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.FlyingAttackingFollowingMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class OrbOfSlimesFollowingMob
extends FlyingAttackingFollowingMob {
    public OrbOfSlimesFollowingMob() {
        super(10);
        this.moveAccuracy = 5;
        this.setSpeed(200.0f);
        this.setFriction(2.0f);
        this.collision = new Rectangle(-30, -30, 60, 60);
        this.hitBox = new Rectangle(-30, -30, 60, 60);
        this.selectBox = new Rectangle();
    }

    @Override
    public GameDamage getCollisionDamage(Mob target, boolean fromPacket, ServerClient packetSubmitter) {
        return this.summonDamage;
    }

    @Override
    public int getCollisionKnockback(Mob target) {
        return 105;
    }

    @Override
    public void handleCollisionHit(Mob target, GameDamage damage, int knockback) {
        Mob owner = this.getAttackOwner();
        if (owner != null && target != null) {
            target.isServerHit(damage, target.x - owner.x, target.y - owner.y, knockback, this);
            this.collisionHitCooldowns.startCooldown(target);
            SoundManager.playSound(GameResources.slimeSplash2, (SoundEffect)SoundEffect.effect(this).volume(0.3f));
        }
    }

    @Override
    public void init() {
        super.init();
        this.collisionHitCooldowns.hitCooldown = 500;
        this.ai = new BehaviourTreeAI<OrbOfSlimesFollowingMob>(this, new PlayerFlyingFollowerAINode(1024, 64), new FlyingAIMover());
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.getLevel().entityManager.addParticle(this.x + (float)(GameRandom.globalRandom.nextGaussian() * 4.0), this.y + (float)(GameRandom.globalRandom.nextGaussian() * 4.0), Particle.GType.IMPORTANT_COSMETIC).lifeTime(1000).sprite(GameResources.bubbleParticle.sprite(0, 0, 12)).movesConstant(this.dx / 10.0f, this.dy / 10.0f).color(new Color(70, 178, 170));
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 30; ++i) {
            this.getLevel().entityManager.addParticle(this.x, this.y, Particle.GType.COSMETIC).sprite(GameResources.bubbleParticle.sprite(0, 0, 12)).movesConstantAngle(GameRandom.globalRandom.nextInt(360), GameRandom.globalRandom.getIntBetween(5, 20)).color(new Color(70, 178, 170));
        }
    }

    @Override
    protected SoundSettings getDeathSound() {
        return new SoundSettings(GameResources.slimeSplash4).volume(0.15f);
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(OrbOfSlimesFollowingMob.getTileCoordinate(x), OrbOfSlimesFollowingMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 16;
        int drawY = camera.getDrawY(y) - 10;
        int anim = GameUtils.getAnim(this.getTime(), 6, 600);
        TextureDrawOptionsEnd body = MobRegistry.Textures.orbOfSlimesSlime.body.initDraw().sprite(anim, 0, 32, 32).light(light).pos(drawX, drawY);
        topList.add(tm -> body.draw());
        TextureDrawOptionsEnd shadow = MobRegistry.Textures.orbOfSlimesSlime.shadow.initDraw().sprite(anim, 0, 32, 32).light(light).pos(drawX, drawY + 10);
        tileList.add(tm -> shadow.draw());
    }
}

