/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.summon.summonFollowingMob.mountFollowingMob;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.leaves.PlayerFollowerAINode;
import necesse.entity.mobs.summon.summonFollowingMob.mountFollowingMob.MountFollowingJumpingMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SeahorseMob
extends MountFollowingJumpingMob {
    private double waterSoundTimer;

    public SeahorseMob() {
        super(50);
        this.setSpeed(20.0f);
        this.setFriction(3.0f);
        this.setSwimSpeed(5.0f);
        this.setJumpStrength(100.0f);
        this.setJumpCooldown(100);
        this.setJumpAnimationTime(400);
        this.moveAccuracy = 10;
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-16, -12, 32, 24);
        this.selectBox = new Rectangle(-18, -83, 36, 90);
        this.swimMaskMove = 32;
        this.swimMaskOffset = -40;
        this.swimSinkOffset = -6;
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<SeahorseMob>(this, new PlayerFollowerAINode(480, 64));
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.seahorse_front, GameRandom.globalRandom.nextInt(5), 12, 32, this.x, this.y, 10.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(SeahorseMob.getTileCoordinate(x), SeahorseMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 22 - 10;
        int drawY = camera.getDrawY(y) - 44 - 32 - 11;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        int jumpHeight = this.getJumpHeight();
        final MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        final TextureDrawOptionsEnd back = MobRegistry.Textures.seahorse_back.initDraw().sprite(sprite.x, sprite.y, 64, 96).addMaskShader(swimMask).light(light).pos(drawX, (drawY += level.getTile(SeahorseMob.getTileCoordinate(x), SeahorseMob.getTileCoordinate(y)).getMobSinkingAmount(this)) - jumpHeight);
        final TextureDrawOptionsEnd front = MobRegistry.Textures.seahorse_front.initDraw().sprite(sprite.x, sprite.y, 64, 96).addMaskShader(swimMask).light(light).pos(drawX, drawY - jumpHeight);
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                swimMask.use();
                front.draw();
                swimMask.stop();
            }

            @Override
            public void drawBehindRider(TickManager tickManager) {
                swimMask.use();
                back.draw();
                swimMask.stop();
            }
        });
        TextureDrawOptionsEnd shadow = MobRegistry.Textures.seahorse_shadow.initDraw().sprite(sprite.x, sprite.y, 64, 96).light(light).pos(drawX, drawY);
        tileList.add(tm -> shadow.draw());
    }

    public int getJumpHeight() {
        int jumpFrames = 5;
        int jumpFrame = this.getJumpAnimationFrame(jumpFrames);
        double sin = GameMath.sin((float)jumpFrame / (float)jumpFrames * 180.0f);
        return (int)(sin * 20.0);
    }

    @Override
    public int getRockSpeed() {
        return 20;
    }

    @Override
    public Point getAnimSprite(int x, int y, int dir) {
        Point p = new Point(0, dir);
        p.x = this.dx == 0.0f & this.dy == 0.0f ? 0 : (int)(this.getDistanceRan() / (double)this.getRockSpeed()) % 4 + 1;
        return p;
    }

    @Override
    public Point getSpriteOffset(int spriteX, int spriteY) {
        Point p = new Point(0, 0);
        if (spriteX == 2) {
            p.y = 2;
        }
        p.x += this.getRiderDrawXOffset();
        p.y += this.getRiderDrawYOffset();
        if (spriteY == 0) {
            p.y += 5;
        }
        return p;
    }

    @Override
    public int getRiderDrawYOffset() {
        return this.getSwimMaskShaderOptions((float)this.inLiquidFloat((int)this.getDrawX(), (int)this.getDrawY())).drawYOffset - 38 - this.getJumpHeight();
    }

    @Override
    public int getRiderDrawXOffset() {
        return super.getRiderDrawXOffset();
    }

    @Override
    public GameTexture getRiderMask() {
        return MobRegistry.Textures.mountmask;
    }

    @Override
    public int getRiderMaskYOffset() {
        return -8;
    }

    @Override
    public int getSwimMaskOffset() {
        return super.getSwimMaskOffset() - this.getJumpHeight();
    }

    @Override
    public float getSwimSpeedModifier() {
        if (this.inLiquid()) {
            return this.getSwimSpeed();
        }
        return 1.0f;
    }

    @Override
    protected void doMountedLogic() {
        if (this.isServer()) {
            return;
        }
        int particleCount = 40;
        for (int i = 0; i < particleCount; ++i) {
            this.getLevel().entityManager.addParticle(this.x + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), this.y + 16.0f + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.smokePuff.sprite(0, 0, 32)).lifeTime(750).fadesAlphaTime(100, 250).movesFriction(16.0f * (float)GameRandom.globalRandom.nextGaussian(), 5.0f * (float)GameRandom.globalRandom.nextGaussian(), 1.0f).sizeFades(14, 18).heightMoves(20.0f, 64.0f);
        }
    }

    @Override
    public void tickMovement(float delta) {
        super.tickMovement(delta);
        this.waterSoundTimer += (double)(delta * Math.max(0.2f, this.getCurrentSpeed() / 30.0f));
        if (this.waterSoundTimer >= 600.0 && this.getCurrentSpeed() >= 5.0f) {
            this.waterSoundTimer = 0.0;
            if (this.inLiquid()) {
                SoundManager.playSound(new SoundSettings(GameResources.waterblob).volume(0.1f), this);
            }
        }
    }

    @Override
    public void onJump() {
        if (this.isClient()) {
            return;
        }
        SoundManager.playSound(new SoundSettings(GameResources.slimeSplash1).volume(0.2f), this);
        if (this.inLiquidFloat() > 0.0f) {
            SoundManager.playSound(new SoundSettings(GameResources.watersplash).volume(0.1f), this);
        }
    }

    @Override
    protected SoundSettings getAmbientSound() {
        return new SoundSettings(GameResources.seahorseAmbient).volume(0.4f);
    }
}

