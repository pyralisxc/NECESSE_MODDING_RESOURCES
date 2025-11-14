/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.container;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.sound.SoundSettings;
import necesse.engine.sound.SoundSettingsRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootTable;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.ObjectHoverHitbox;
import necesse.level.gameObject.container.CraftingStationObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.SideMultiTile;

class LandscapingStation2Object
extends CraftingStationObject {
    protected ObjectDamagedTextureArray texture;
    protected int counterID;

    protected LandscapingStation2Object() {
        super(new Rectangle(32, 32));
        this.mapColor = new Color(150, 119, 70);
        this.toolType = ToolType.ALL;
        this.isLightTransparent = true;
    }

    @Override
    public int getCraftingCategoryDepth() {
        return 3;
    }

    @Override
    public MultiTile getMultiTile(int rotation) {
        return new SideMultiTile(0, 0, 1, 2, rotation, false, this.getID(), this.counterID);
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/landscapingstation");
    }

    @Override
    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        if (rotation == 0) {
            return new Rectangle(x * 32 + 5, y * 32 + 16, 22, 16);
        }
        if (rotation == 1) {
            return new Rectangle(x * 32, y * 32 + 6, 20, 20);
        }
        if (rotation == 2) {
            return new Rectangle(x * 32 + 5, y * 32, 22, 26);
        }
        return new Rectangle(x * 32 + 12, y * 32 + 6, 20, 20);
    }

    @Override
    protected ObjectHoverHitbox getHoverHitbox(Level level, int layerID, int tileX, int tileY) {
        byte rotation = level.getObjectRotation(layerID, tileX, tileY);
        if (rotation == 1 || rotation == 3) {
            return new ObjectHoverHitbox(layerID, tileX, tileY, 0, -16, 32, 48);
        }
        return super.getHoverHitbox(level, layerID, tileX, tileY);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        byte rotation = level.getObjectRotation(tileX, tileY);
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        final DrawOptionsList options = new DrawOptionsList();
        if (rotation == 0) {
            options.add(texture.initDraw().sprite(0, 0, 32).light(light).pos(drawX, drawY - 32));
            options.add(texture.initDraw().sprite(0, 1, 32).light(light).pos(drawX, drawY));
        } else if (rotation == 1) {
            options.add(texture.initDraw().sprite(1, 5, 32).light(light).pos(drawX, drawY - 32));
            options.add(texture.initDraw().sprite(1, 6, 32).light(light).pos(drawX, drawY));
        } else if (rotation == 2) {
            options.add(texture.initDraw().sprite(1, 2, 32).light(light).pos(drawX, drawY));
        } else {
            options.add(texture.initDraw().sprite(0, 3, 32).light(light).pos(drawX, drawY - 32));
            options.add(texture.initDraw().sprite(0, 4, 32).light(light).pos(drawX, drawY));
        }
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 16;
            }

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }

    @Override
    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        return new LootTable();
    }

    @Override
    protected SoundSettings getInteractSoundOpen() {
        return SoundSettingsRegistry.landscapingStationOpen;
    }
}

