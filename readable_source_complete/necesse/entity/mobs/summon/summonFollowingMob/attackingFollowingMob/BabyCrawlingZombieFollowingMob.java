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
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerFollowerCollisionChaserAI;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class BabyCrawlingZombieFollowingMob
extends AttackingFollowingMob {
    public Mob customFocus;
    protected int deathTime = 4000;
    protected int lifeTime = 0;

    public BabyCrawlingZombieFollowingMob() {
        super(10);
        this.setSpeed(50.0f);
        this.setFriction(2.0f);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-12, -14, 24, 24);
        this.selectBox = new Rectangle(-16, -30, 32, 36);
        this.swimMaskMove = 12;
        this.swimMaskOffset = 0;
        this.swimSinkOffset = 0;
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<BabyCrawlingZombieFollowingMob>(this, new PlayerFollowerCollisionChaserAI<BabyCrawlingZombieFollowingMob>(576, this.summonDamage, 30, 500, 640, 64){

            @Override
            public Mob getCustomFocus() {
                return BabyCrawlingZombieFollowingMob.this.customFocus;
            }
        });
        if (this.isClient()) {
            this.spawnSummonParticles();
            this.ambientSoundCooldownMin = 2000;
            this.ambientSoundCooldownMax = 5000;
            this.ambientSoundLastTriggerTime = 0L;
        }
    }

    protected void spawnSummonParticles() {
        for (int i = 0; i < 14; ++i) {
            float xDir = GameRandom.globalRandom.getFloatBetween(-1.0f, 1.0f);
            float yDir = GameRandom.globalRandom.getFloatBetween(-1.0f, 1.0f);
            this.getLevel().entityManager.addParticle(this.x, this.y, Particle.GType.CRITICAL).movesConstant(xDir * 12.0f, yDir * 12.0f).color(GameRandom.globalRandom.getOneOf(new Color(103, 168, 121), new Color(74, 117, 72), new Color(103, 78, 42), new Color(64, 55, 104))).height(10.0f).sizeFades(14, 18).lifeTime(600);
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.lifeTime += 50;
        if (this.lifeTime >= this.deathTime) {
            this.remove(0.0f, 0.0f, null, true);
        }
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 3; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.babyCrawlingZombie.body, i, 8, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(BabyCrawlingZombieFollowingMob.getTileCoordinate(x), BabyCrawlingZombieFollowingMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 44 - 7;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        final TextureDrawOptionsEnd body = MobRegistry.Textures.babyCrawlingZombie.body.initDraw().sprite(sprite.x, sprite.y, 64).addMaskShader(swimMask).light(light).pos(drawX, drawY += level.getTile(BabyCrawlingZombieFollowingMob.getTileCoordinate(x), BabyCrawlingZombieFollowingMob.getTileCoordinate(y)).getMobSinkingAmount(this));
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                body.draw();
            }
        });
        TextureDrawOptionsEnd shadow = MobRegistry.Textures.babyCrawlingZombie.shadow.initDraw().sprite(sprite.x, sprite.y, 64).light(light).pos(drawX + swimMask.drawXOffset, drawY + swimMask.drawYOffset);
        tileList.add(tm -> shadow.draw());
    }

    @Override
    public int getRockSpeed() {
        return 10;
    }

    @Override
    protected SoundSettings getAmbientSound() {
        return new SoundSettings(GameResources.zombieGroans[GameRandom.globalRandom.getOneOf(9, 10, 11)]).volume(0.15f).basePitch(1.6f).pitchVariance(0.05f).fallOffDistance(1000);
    }
}

