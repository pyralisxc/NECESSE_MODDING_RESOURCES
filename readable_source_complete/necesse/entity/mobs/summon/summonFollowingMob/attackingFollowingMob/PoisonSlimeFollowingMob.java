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
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerFollowerCollisionChaserAI;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingJumpingMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class PoisonSlimeFollowingMob
extends AttackingFollowingJumpingMob {
    public int lifeTime = 8000;

    public PoisonSlimeFollowingMob() {
        super(10);
        this.setSpeed(60.0f);
        this.setFriction(2.0f);
        this.jumpStats.setJumpAnimationTime(250);
        this.jumpStats.setJumpStrength(150.0f);
        this.jumpStats.setJumpCooldown(50);
        this.jumpStats.jumpStrengthUseSpeedMod = false;
        this.collision = new Rectangle(-8, -6, 16, 12);
        this.hitBox = new Rectangle(-12, -14, 24, 24);
        this.selectBox = new Rectangle(-12, -16, 26, 24);
        this.swimMaskMove = 8;
        this.swimMaskOffset = 28;
        this.swimSinkOffset = 0;
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
        this.ai = new BehaviourTreeAI<PoisonSlimeFollowingMob>(this, new PlayerFollowerCollisionChaserAI(384, null, 30, 500, 640, 64));
        if (this.isClient()) {
            this.spawnSummonParticles();
        }
    }

    protected void spawnSummonParticles() {
        for (int i = 0; i < 5; ++i) {
            float xDir = GameRandom.globalRandom.getFloatBetween(-1.0f, 1.0f);
            float yDir = GameRandom.globalRandom.getFloatBetween(-1.0f, 1.0f);
            this.getLevel().entityManager.addParticle(this.x, this.y, Particle.GType.CRITICAL).movesConstant(xDir * 12.0f, yDir * 12.0f).color(GameRandom.globalRandom.getOneOfWeighted(Color.class, 5, new Color(52, 103, 66), 4, new Color(69, 149, 81), 1, new Color(11, 88, 33))).height(10.0f).sizeFades(12, 18).lifeTime(600);
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
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.poisonSlime, i, 2, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    protected SoundSettings getDeathSound() {
        return new SoundSettings(GameResources.slimeSplash2).volume(0.1f);
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(PoisonSlimeFollowingMob.getTileCoordinate(x), PoisonSlimeFollowingMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 16;
        int drawY = camera.getDrawY(y) - 26;
        boolean inLiquid = this.inLiquid(x, y);
        int spriteX = inLiquid ? GameUtils.getAnim(this.getWorldEntity().getTime(), 2, 1000) : this.getJumpAnimationFrame(6);
        drawY += this.getBobbing(x, y);
        final MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        final TextureDrawOptionsEnd options = MobRegistry.Textures.poisonSlime.initDraw().sprite(spriteX, inLiquid ? 1 : 0, 32).addMaskShader(swimMask).light(light).pos(drawX, drawY += level.getTile(PoisonSlimeFollowingMob.getTileCoordinate(x), PoisonSlimeFollowingMob.getTileCoordinate(y)).getMobSinkingAmount(this));
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                swimMask.use();
                options.draw();
                swimMask.stop();
            }
        });
        TextureDrawOptionsEnd shadow = MobRegistry.Textures.poisonSlime_shadow.initDraw().sprite(spriteX, 0, 32).light(light).pos(drawX, drawY);
        tileList.add(tm -> shadow.draw());
    }

    @Override
    public void onJump() {
        if (this.isClient()) {
            SoundManager.playSound(new SoundSettings(GameResources.slimeSplash4).volume(0.05f).basePitch(0.9f).pitchVariance(0.05f), this);
        }
    }
}

