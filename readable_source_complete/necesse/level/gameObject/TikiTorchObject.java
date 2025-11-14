/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.ParticleOption;
import necesse.entity.projectile.BombProjectile;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class TikiTorchObject
extends GameObject {
    public float flameHue = ParticleOption.defaultFlameHue;
    public float smokeHue = ParticleOption.defaultSmokeHue;
    public GameTexture texture;
    public GameTexture texture_off;
    public int particleStartHeight = 32;

    public TikiTorchObject() {
        super(new Rectangle(9, 9, 14, 14));
        this.mapColor = new Color(255, 255, 152);
        this.displayMapTooltip = true;
        this.lightLevel = 150;
        this.objectHealth = 1;
        this.stackSize = 500;
        this.toolType = ToolType.ALL;
        this.isLightTransparent = true;
        this.setItemCategory("objects", "lighting");
        this.setCraftingCategory("objects", "lighting");
        this.roomProperties.add("lights");
        this.canPlaceOnShore = true;
        this.replaceCategories.add("torch");
        this.canReplaceCategories.add("torch");
        this.canReplaceCategories.add("furniture");
        this.canReplaceCategories.add("column");
        this.replaceRotations = false;
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = GameTexture.fromFile("objects/" + this.getStringID());
        this.texture_off = GameTexture.fromFile("objects/" + this.getStringID() + "_off");
    }

    @Override
    public void tickEffect(Level level, int layerID, int tileX, int tileY) {
        if (GameRandom.globalRandom.getEveryXthChance(40) && this.isActive(level, tileX, tileY)) {
            int startHeight = this.particleStartHeight + (int)(GameRandom.globalRandom.nextGaussian() * 2.0);
            BombProjectile.spawnFuseParticle(level, tileX * 32 + 16, tileY * 32 + 16 + 2, startHeight, this.flameHue, this.smokeHue);
        }
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        Performance.record((PerformanceTimerManager)tickManager, "tikitorchSetup", () -> {
            GameLight light = level.getLightLevel(tileX, tileY);
            int drawX = camera.getTileDrawX(tileX);
            int drawY = camera.getTileDrawY(tileY);
            int rotation = level.getObjectRotation(tileX, tileY) % 4;
            GameTexture texture = this.isActive(level, tileX, tileY) ? this.texture : this.texture_off;
            final TextureDrawOptionsEnd options = texture.initDraw().sprite(rotation % 4, 0, 32, texture.getHeight()).light(light).pos(drawX, drawY - texture.getHeight() + 32);
            list.add(new LevelSortedDrawable(this, tileX, tileY){

                @Override
                public int getSortY() {
                    return 16;
                }

                @Override
                public void draw(TickManager tickManager) {
                    Performance.record((PerformanceTimerManager)tickManager, "tikitorchDraw", options::draw);
                }
            });
        });
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        this.texture.initDraw().sprite(rotation, 0, 32, this.texture.getHeight()).alpha(alpha).draw(drawX, drawY - this.texture.getHeight() + 32);
    }

    @Override
    public int getLightLevel(Level level, int layerID, int tileX, int tileY) {
        return this.isActive(level, tileX, tileY) ? this.lightLevel : 0;
    }

    public boolean isActive(Level level, int x, int y) {
        byte rotation = level.getObjectRotation(x, y);
        return this.getMultiTile(rotation).streamIDs(x, y).noneMatch(c -> level.wireManager.isWireActiveAny(c.tileX, c.tileY));
    }

    @Override
    public void onWireUpdate(Level level, int layerID, int tileX, int tileY, int wireID, boolean active) {
        byte rotation = level.getObjectRotation(tileX, tileY);
        Rectangle rect = this.getMultiTile(rotation).getTileRectangle(tileX, tileY);
        level.lightManager.updateStaticLight(rect.x, rect.y, rect.x + rect.width - 1, rect.y + rect.height - 1, true);
    }
}

