/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.container;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.FeedingTroughObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootTable;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;

class FeedingTrough2Object
extends GameObject {
    public ObjectDamagedTextureArray texture;
    protected int counterID;

    protected FeedingTrough2Object() {
        super(new Rectangle(32, 32));
        this.mapColor = new Color(109, 68, 29);
        this.toolType = ToolType.ALL;
        this.objectHealth = 50;
        this.isLightTransparent = true;
    }

    @Override
    public MultiTile getMultiTile(int rotation) {
        return new MultiTile(0, 0, 1, 2, rotation, false, this.getID(), this.counterID);
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/feedingtrough");
    }

    @Override
    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        return new LootTable();
    }

    @Override
    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        if (rotation == 0) {
            return new Rectangle(x * 32 + 5, y * 32 + 6, 22, 26);
        }
        if (rotation == 1) {
            return new Rectangle(x * 32, y * 32 + 6, 26, 22);
        }
        if (rotation == 2) {
            return new Rectangle(x * 32 + 5, y * 32, 22, 26);
        }
        return new Rectangle(x * 32 + 6, y * 32 + 6, 26, 22);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        ObjectEntity objectEntity;
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        byte rotation = level.getObjectRotation(tileX, tileY);
        boolean hasFeed = false;
        LevelObject master = this.getMultiTile(rotation).getMasterLevelObject(level, 0, tileX, tileY).orElse(null);
        if (master != null && (objectEntity = level.entityManager.getObjectEntity(master.tileX, master.tileY)) instanceof FeedingTroughObjectEntity) {
            hasFeed = ((FeedingTroughObjectEntity)objectEntity).hasFeed();
        }
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        final SharedTextureDrawOptions options = new SharedTextureDrawOptions(texture);
        if (rotation == 0) {
            options.addSprite(2, 1, 32).light(light).pos(drawX, drawY);
            if (hasFeed) {
                options.addSprite(3, 1, 32).light(light).pos(drawX, drawY);
                options.addSprite(3, 0, 32).light(light).pos(drawX, drawY - 32);
            }
        } else if (rotation == 1) {
            options.addSprite(1, 2, 32).light(light).pos(drawX, drawY);
            if (hasFeed) {
                options.addSprite(1, 0, 32).light(light).pos(drawX, drawY - 32);
                options.addSprite(1, 1, 32).light(light).pos(drawX, drawY);
            }
        } else if (rotation == 2) {
            options.addSprite(2, 2, 32).light(light).pos(drawX, drawY);
            if (hasFeed) {
                options.addSprite(3, 2, 32).light(light).pos(drawX, drawY);
            }
        } else {
            options.addSprite(0, 2, 32).light(light).pos(drawX, drawY);
            if (hasFeed) {
                options.addSprite(0, 0, 32).light(light).pos(drawX, drawY - 32);
                options.addSprite(0, 1, 32).light(light).pos(drawX, drawY);
            }
        }
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 20;
            }

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }

    @Override
    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return true;
    }

    @Override
    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        return Localization.translate("controls", "opentip");
    }

    @Override
    public void interact(Level level, int x, int y, PlayerMob player) {
        this.getMultiTile(level.getObjectRotation(x, y)).getMasterLevelObject(level, 0, x, y).ifPresent(e -> e.interact(player));
    }
}

