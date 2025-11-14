/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.summon.summonFollowingMob.petFollowingMob;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.leaves.PlayerFollowerAINode;
import necesse.entity.mobs.summon.summonFollowingMob.petFollowingMob.PetFollowingMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class PetPugMob
extends PetFollowingMob {
    public long nextPetTime;

    public PetPugMob() {
        super(10);
        this.setSpeed(80.0f);
        this.setFriction(2.0f);
        this.moveAccuracy = 10;
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-12, -14, 24, 24);
        this.selectBox = new Rectangle(-18, -24, 36, 36);
        this.swimMaskMove = 14;
        this.swimMaskOffset = 0;
        this.swimSinkOffset = -6;
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<PetPugMob>(this, new PlayerFollowerAINode(480, 64));
    }

    @Override
    public void playDeathSound() {
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.pug, i, 8, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(PetPugMob.getTileCoordinate(x), PetPugMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 48 - 7;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        final MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        final TextureDrawOptionsEnd options = MobRegistry.Textures.pug.initDraw().sprite(sprite.x, sprite.y, 64).light(light).addMaskShader(swimMask).pos(drawX, drawY += this.getLevel().getTile(x / 16, y / 16).getMobSinkingAmount(this));
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                swimMask.use();
                options.draw();
                swimMask.stop();
            }
        });
        TextureDrawOptionsEnd shadow = MobRegistry.Textures.pug_shadow.initDraw().sprite(0, sprite.y, 64).light(light).pos(drawX, drawY);
        tileList.add(tm -> shadow.draw());
    }

    @Override
    public int getRockSpeed() {
        return 10;
    }

    @Override
    public void interact(PlayerMob player) {
        super.interact(player);
        if (!this.isServer()) {
            this.nextPetTime = this.getTime() + 2000L;
            for (int i = 0; i < 10; ++i) {
                float posX = this.x + (float)GameRandom.globalRandom.getIntBetween(-10, 10);
                float posY = this.y + (float)GameRandom.globalRandom.getIntBetween(-5, 5);
                int startHeight = 8 + GameRandom.globalRandom.nextInt(24);
                int lifeTime = GameRandom.globalRandom.getIntBetween(4500, 5500);
                final int swing = GameRandom.globalRandom.getIntBetween(3, 7);
                final float swingOffset = GameRandom.globalRandom.nextFloat();
                float moveX = GameRandom.globalRandom.floatGaussian() * 0.5f;
                float moveY = GameRandom.globalRandom.floatGaussian() * 1.5f;
                final ParticleOption.FrictionMover frictionMover = new ParticleOption.FrictionMover(moveX, moveY, 0.0f);
                this.getLevel().entityManager.addParticle(posX, posY, Particle.GType.COSMETIC).sprite(GameResources.heartParticle).dontRotate().color(new Color(255, 255, 255)).heightMoves(startHeight, startHeight + 70).moves(new ParticleOption.Mover(){

                    @Override
                    public void tick(Point2D.Float pos, float delta, int lifeTime, int timeAlive, float lifePercent) {
                        frictionMover.tick(pos, delta, lifeTime, timeAlive, lifePercent);
                        float angle = (lifePercent + swingOffset) * 500.0f;
                        pos.x += GameMath.sin(angle) * (float)swing * delta / 250.0f;
                    }
                }).lifeTime(lifeTime);
            }
        }
    }

    @Override
    public boolean canInteract(Mob mob) {
        return this.nextPetTime <= this.getTime();
    }

    @Override
    protected String getInteractTip(PlayerMob perspective, boolean debug) {
        return Localization.translate("controls", "pettip");
    }
}

