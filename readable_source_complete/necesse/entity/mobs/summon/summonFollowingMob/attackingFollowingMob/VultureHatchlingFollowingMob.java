/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob;

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
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class VultureHatchlingFollowingMob
extends FlyingAttackingFollowingMob {
    public VultureHatchlingFollowingMob() {
        super(10);
        this.accelerationMod = 1.0f;
        this.moveAccuracy = 10;
        this.setSpeed(60.0f);
        this.setFriction(1.0f);
        this.collision = new Rectangle(-18, -15, 36, 30);
        this.hitBox = new Rectangle(-18, -15, 36, 36);
        this.selectBox = new Rectangle(-20, -34, 40, 36);
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
        }
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<VultureHatchlingFollowingMob>(this, new PlayerFlyingFollowerCollisionChaserAI(576, null, 15, 500, 640, 64), new FlyingAIMover());
    }

    @Override
    public void setFacingDir(float deltaX, float deltaY) {
        if (deltaX < 0.0f) {
            this.setDir(0);
        } else if (deltaX > 0.0f) {
            this.setDir(1);
        }
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 5; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.vultureHatchling, GameRandom.globalRandom.nextInt(4), 2, 32, this.x, this.y, 10.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(VultureHatchlingFollowingMob.getTileCoordinate(x), VultureHatchlingFollowingMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 32 - 16;
        int dir = this.getDir();
        long time = level.getWorldEntity().getTime() % 350L;
        int sprite = time < 100L ? 0 : (time < 200L ? 1 : (time < 300L ? 2 : 3));
        float rotate = this.dx / 10.0f;
        TextureDrawOptionsEnd options = MobRegistry.Textures.vultureHatchling.initDraw().sprite(sprite, 0, 64).light(light).mirror(dir == 0, false).rotate(rotate, 32, 32).pos(drawX, drawY);
        topList.add(tm -> options.draw());
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }

    @Override
    protected TextureDrawOptions getShadowDrawOptions(Level level, int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = MobRegistry.Textures.vultureHatchling_shadow;
        int drawX = camera.getDrawX(x) - shadowTexture.getWidth() / 2;
        int drawY = camera.getDrawY(y) - shadowTexture.getHeight() / 2 + 13;
        return shadowTexture.initDraw().light(light).pos(drawX, drawY);
    }
}

