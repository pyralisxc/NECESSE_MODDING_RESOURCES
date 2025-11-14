/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameRandom;
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

public class RubyShieldFollowingMob
extends FlyingAttackingFollowingMob {
    public RubyShieldFollowingMob() {
        super(10);
        this.moveAccuracy = 5;
        this.setSpeed(200.0f);
        this.setFriction(2.0f);
        this.isStatic = false;
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
            this.remove(0.0f, 0.0f, null, true);
        }
    }

    @Override
    public void init() {
        super.init();
        this.collisionHitCooldowns.hitCooldown = 500;
        this.ai = new BehaviourTreeAI<RubyShieldFollowingMob>(this, new PlayerFlyingFollowerAINode(1024, 64), new FlyingAIMover());
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 14; ++i) {
            GameRandom random = GameRandom.globalRandom;
            this.getLevel().entityManager.addParticle(this.x, this.y, Particle.GType.COSMETIC).sprite(GameResources.rubyShardParticles.sprite(random.nextInt(4), 0, 18, 24)).movesConstantAngle(random.nextInt(360), random.getIntBetween(5, 20)).sizeFades(24, 12);
        }
    }

    @Override
    protected SoundSettings getDeathSound() {
        return new SoundSettings(GameResources.shatter1).volume(0.2f);
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(RubyShieldFollowingMob.getTileCoordinate(x), RubyShieldFollowingMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 16;
        int drawY = camera.getDrawY(y) - 10;
        TextureDrawOptionsEnd body = MobRegistry.Textures.rubyShield.initDraw().sprite(0, 0, 32).light(light).pos(drawX, drawY);
        topList.add(tm -> body.draw());
        TextureDrawOptionsEnd shadow = MobRegistry.Textures.rubyShield_shadow.initDraw().sprite(0, 0, 32).light(light).pos(drawX, drawY + 10);
        tileList.add(tm -> shadow.draw());
    }
}

