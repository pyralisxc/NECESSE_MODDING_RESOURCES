/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly.critters;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.critters.CritterMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SpiderCritterMob
extends CritterMob {
    public static LootTable lootTable = new LootTable();

    public SpiderCritterMob() {
        this.setSpeed(25.0f);
        this.setFriction(3.0f);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-12, -14, 24, 24);
        this.selectBox = new Rectangle(-16, -20, 32, 30);
        this.swimMaskMove = 6;
        this.swimMaskOffset = 30;
        this.swimSinkOffset = 0;
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.spider.body, 12, i, 16, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(SpiderCritterMob.getTileCoordinate(x), SpiderCritterMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 16;
        int drawY = camera.getDrawY(y) - 22;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        final MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        final TextureDrawOptionsEnd options = MobRegistry.Textures.spider.body.initDraw().sprite(sprite.x, sprite.y, 32).addMaskShader(swimMask).light(light).pos(drawX, drawY += level.getTile(SpiderCritterMob.getTileCoordinate(x), SpiderCritterMob.getTileCoordinate(y)).getMobSinkingAmount(this));
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                swimMask.use();
                options.draw();
                swimMask.stop();
            }
        });
        TextureDrawOptionsEnd shadow = MobRegistry.Textures.spider.shadow.initDraw().sprite(0, dir, 32).light(light).pos(drawX, drawY);
        tileList.add(tm -> shadow.draw());
    }

    @Override
    public int getRockSpeed() {
        return 5;
    }

    @Override
    protected SoundSettings getAmbientSound() {
        return new SoundSettings(GameResources.critterSpiderAmbient).volume(2.0f);
    }

    @Override
    protected SoundSettings getDeathSound() {
        return new SoundSettings(GameResources.critterSpiderDeath).volume(0.5f);
    }
}

