/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly.critters;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobSpawnLocation;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.critters.CritterMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SwampSlugMob
extends CritterMob {
    public static LootTable lootTable = new LootTable(new LootItem("swamplarva"));

    public SwampSlugMob() {
        this.setSpeed(4.0f);
        this.setFriction(3.0f);
        this.collision = new Rectangle(-7, -5, 16, 14);
        this.hitBox = new Rectangle(-9, -10, 20, 20);
        this.selectBox = new Rectangle(-11, -11, 24, 24);
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 2; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.swampSlug, 12, i, 16, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(SwampSlugMob.getTileCoordinate(x), SwampSlugMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 16;
        int drawY = camera.getDrawY(y) - 18;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        final TextureDrawOptionsEnd options = MobRegistry.Textures.swampSlug.initDraw().sprite(sprite.x, sprite.y, 32).light(light).pos(drawX, drawY += level.getTile(SwampSlugMob.getTileCoordinate(x), SwampSlugMob.getTileCoordinate(y)).getMobSinkingAmount(this));
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }

    @Override
    public float getFullInLiquidAtPercent(int x, int y) {
        return 0.1f;
    }

    @Override
    public int getRockSpeed() {
        return 3;
    }

    @Override
    public MobSpawnLocation checkSpawnLocation(MobSpawnLocation location) {
        return super.checkSpawnLocation(location).checkTile((tileX, tileY) -> {
            int tileID = this.getLevel().getTileID((int)tileX, (int)tileY);
            return tileID == TileRegistry.swampGrassID || tileID == TileRegistry.overgrownSwampGrassID || tileID == TileRegistry.mudID;
        });
    }

    @Override
    protected SoundSettings getAmbientSound() {
        return new SoundSettings(GameResources.swampSlugAmbient).volume(2.0f);
    }

    @Override
    protected SoundSettings getDeathSound() {
        return new SoundSettings(GameResources.swampSlugDeath).volume(0.6f);
    }
}

