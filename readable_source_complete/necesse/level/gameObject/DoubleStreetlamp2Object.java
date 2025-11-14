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
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.furniture.FurnitureObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.StaticMultiTile;

class DoubleStreetlamp2Object
extends FurnitureObject {
    protected String textureName;
    public ObjectDamagedTextureArray texture;
    protected int counterID;

    protected DoubleStreetlamp2Object(String textureName, ToolType toolType, Color mapColor) {
        super(new Rectangle(32, 32));
        this.textureName = textureName;
        this.toolType = toolType;
        this.mapColor = mapColor;
        this.displayMapTooltip = true;
        this.lightLevel = 200;
        this.objectHealth = 50;
        this.canPlaceOnShore = true;
    }

    @Override
    public MultiTile getMultiTile(int rotation) {
        return new StaticMultiTile(1, 0, 2, 1, rotation, false, this.counterID, this.getID());
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/" + this.textureName);
    }

    @Override
    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        if (rotation == 0) {
            int width = 12;
            int height = 16;
            return new Rectangle(x * 32, y * 32 + (32 - height) / 2, width, height);
        }
        if (rotation == 1) {
            int width = 16;
            int height = 12;
            return new Rectangle(x * 32 + (32 - width) / 2, y * 32, width, height);
        }
        if (rotation == 2) {
            int width = 12;
            int height = 16;
            return new Rectangle(x * 32 + 32 - width, y * 32 + (32 - height) / 2, width, height);
        }
        int width = 16;
        int height = 12;
        return new Rectangle(x * 32 + (32 - width) / 2, y * 32 + 32 - height, width, height);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        Performance.record((PerformanceTimerManager)tickManager, "doublestreetlampSetup", () -> {
            GameLight light = level.getLightLevel(tileX, tileY);
            int drawX = camera.getTileDrawX(tileX);
            int drawY = camera.getTileDrawY(tileY);
            boolean active = this.isActive(level, tileX, tileY);
            int rotation = level.getObjectRotation(tileX, tileY) % 4;
            GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
            int textureHeight = texture.getHeight() / 2;
            final DrawOptionsList options = new DrawOptionsList();
            if (rotation == 0) {
                options.add(texture.initDraw().sprite(1, active ? 0 : 1, 32, textureHeight).light(light).pos(drawX, drawY - textureHeight + 32));
            } else if (rotation == 1) {
                int heightSprites = textureHeight / 32;
                options.add(texture.initDraw().sprite(2, active ? heightSprites - 1 : heightSprites * 2 - 1, 32, 32).light(light).pos(drawX, drawY));
            } else if (rotation == 2) {
                options.add(texture.initDraw().sprite(0, active ? 0 : 1, 32, textureHeight).light(light).pos(drawX, drawY - textureHeight + 32));
            } else {
                int heightSprites = textureHeight / 32;
                for (int i = 0; i < heightSprites; ++i) {
                    options.add(texture.initDraw().sprite(2, active ? i : i + heightSprites, 32, 32).light(light).pos(drawX, drawY - textureHeight + 64 + i * 32));
                }
            }
            list.add(new LevelSortedDrawable(this, tileX, tileY){

                @Override
                public int getSortY() {
                    return 16;
                }

                @Override
                public void draw(TickManager tickManager) {
                    Performance.record((PerformanceTimerManager)tickManager, "doublestreetlampDraw", options::draw);
                }
            });
        });
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

