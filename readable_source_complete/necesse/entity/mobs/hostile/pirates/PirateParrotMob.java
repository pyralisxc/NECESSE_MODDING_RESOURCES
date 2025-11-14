/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.pirates;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.HumanTexture;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.CollisionPlayerChaserAI;
import necesse.entity.mobs.hostile.pirates.PirateMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class PirateParrotMob
extends PirateMob {
    public static MaxHealthGetter MAX_HEALTH = new MaxHealthGetter(50, 80, 100, 125, 170);
    public static MaxHealthGetter SUMMONED_MAX_HEALTH = new MaxHealthGetter(25, 40, 50, 60, 85);

    public PirateParrotMob() {
        super(100);
        this.difficultyChanges.setMaxHealth(MAX_HEALTH);
        this.setSpeed(60.0f);
        this.setFriction(1.0f);
        this.setSwimSpeed(1.0f);
        this.meleeDamage = 50;
        this.moveAccuracy = 20;
        this.collision = new Rectangle(-8, -6, 16, 12);
        this.hitBox = new Rectangle(-12, -14, 24, 24);
        this.selectBox = new Rectangle(-20, -45, 44, 45);
        this.shouldSave = false;
    }

    @Override
    public void setSummoned() {
        super.setSummoned();
        this.difficultyChanges.setMaxHealth(SUMMONED_MAX_HEALTH);
    }

    @Override
    public void setupAI() {
        if (this.baseTile == null || this.baseTile.x == 0 && this.baseTile.y == 0) {
            this.baseTile = new Point(this.getTileX(), this.getTileY());
        }
        this.ai = new BehaviourTreeAI<PirateParrotMob>(this, new CollisionPlayerChaserAI(1280, new GameDamage(this.meleeDamage), 100));
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.parrot, i, 8, 64, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.superAddDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(PirateParrotMob.getTileCoordinate(x), PirateParrotMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 63;
        int drawY = camera.getDrawY(y) - 90;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        final TextureDrawOptionsEnd options = MobRegistry.Textures.parrot.initDraw().sprite(sprite.x, sprite.y, 128).light(light).pos(drawX, drawY += level.getTile(PirateParrotMob.getTileCoordinate(x), PirateParrotMob.getTileCoordinate(y)).getMobSinkingAmount(this));
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }

    @Override
    protected HumanTexture getPirateTexture() {
        return null;
    }

    @Override
    public Point getAnimSprite(int x, int y, int dir) {
        return new Point(GameUtils.getAnim(this.getWorldEntity().getTime(), 4, 400), dir);
    }

    @Override
    protected TextureDrawOptions getShadowDrawOptions(Level level, int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = MobRegistry.Textures.human_baby_shadow;
        int res = shadowTexture.getHeight();
        int drawX = camera.getDrawX(x) - res / 2;
        int drawY = camera.getDrawY(y) - res / 2;
        return shadowTexture.initDraw().sprite(this.getDir(), 0, res).light(light).pos(drawX, drawY);
    }

    @Override
    public int getBobbing(int x, int y) {
        return 0;
    }

    @Override
    public void attack(int x, int y, boolean showAllDirections) {
    }
}

