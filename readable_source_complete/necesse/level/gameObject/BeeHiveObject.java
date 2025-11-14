/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.AbstractBeeHiveObjectEntity;
import necesse.entity.objectEntity.BeeHiveObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class BeeHiveObject
extends GameObject {
    public ObjectDamagedTextureArray texture;

    public BeeHiveObject() {
        super(new Rectangle(2, 8, 28, 22));
        this.mapColor = new Color(209, 170, 57);
        this.displayMapTooltip = true;
        this.toolType = ToolType.ALL;
        this.objectHealth = 50;
        this.isLightTransparent = true;
        this.hoverHitbox = new Rectangle(0, -10, 32, 42);
        this.setItemCategory("objects", "misc");
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/beehive");
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        AbstractBeeHiveObjectEntity apiary = this.getApiaryObjectEntity(level, tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        int spriteX = 0;
        if (apiary != null) {
            int beeAmount = Math.max(apiary.getMaxBees() - 1, 1);
            int maxBees = BeeHiveObjectEntity.maxBees;
            float beePercent = (float)beeAmount / (float)maxBees;
            int maxStages = texture.getWidth() / 32;
            spriteX = Math.min(Math.round(beePercent * (float)maxStages), maxStages - 1);
        }
        final TextureDrawOptionsEnd drawOptions = texture.initDraw().sprite(spriteX, 0, 32, texture.getHeight()).light(light).pos(drawX, drawY - texture.getHeight() + 32);
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 16;
            }

            @Override
            public void draw(TickManager tickManager) {
                drawOptions.draw();
            }
        });
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        GameLight light = level.getLightLevel(tileX, tileY);
        AbstractBeeHiveObjectEntity apiary = this.getApiaryObjectEntity(level, tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.texture.getDamagedTexture(0.0f);
        int spriteX = 0;
        if (apiary != null) {
            int beeAmount = Math.max(apiary.getMaxBees() - 1, 1);
            int maxBees = BeeHiveObjectEntity.maxBees;
            float beePercent = (float)beeAmount / (float)maxBees;
            int maxStages = texture.getWidth() / 32;
            spriteX = Math.min(Math.round(beePercent * (float)maxStages), maxStages - 1);
        }
        texture.initDraw().sprite(spriteX, 0, 32, texture.getHeight()).light(light).alpha(alpha).draw(drawX, drawY - texture.getHeight() + 32);
    }

    public AbstractBeeHiveObjectEntity getApiaryObjectEntity(Level level, int tileX, int tileY) {
        ObjectEntity objectEntity = level.entityManager.getObjectEntity(tileX, tileY);
        if (objectEntity instanceof AbstractBeeHiveObjectEntity) {
            return (AbstractBeeHiveObjectEntity)objectEntity;
        }
        return null;
    }

    @Override
    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        AbstractBeeHiveObjectEntity apiary = this.getApiaryObjectEntity(level, x, y);
        if (apiary != null) {
            return apiary.getInteractTip(perspective);
        }
        return null;
    }

    @Override
    public void interact(Level level, int x, int y, PlayerMob player) {
        AbstractBeeHiveObjectEntity apiary = this.getApiaryObjectEntity(level, x, y);
        if (apiary != null) {
            apiary.interact(player);
        }
    }

    @Override
    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return true;
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new BeeHiveObjectEntity(level, x, y);
    }
}

