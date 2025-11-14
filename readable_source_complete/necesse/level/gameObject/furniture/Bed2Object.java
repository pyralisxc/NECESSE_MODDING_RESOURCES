/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.furniture;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.sound.SoundSettings;
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
import necesse.level.gameObject.furniture.SettlerBedObject;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;

class Bed2Object
extends FurnitureObject
implements SettlerBedObject {
    protected String textureName;
    public ObjectDamagedTextureArray texture;
    protected int counterID;

    public Bed2Object(String textureName, ToolType toolType, Color mapColor, String ... category) {
        super(new Rectangle(32, 32));
        this.textureName = textureName;
        this.toolType = toolType;
        this.mapColor = mapColor;
        this.objectHealth = 50;
        this.isLightTransparent = true;
        this.hoverHitbox = new Rectangle(0, -16, 32, 48);
        if (category.length > 0) {
            this.setItemCategory(category);
            this.setCraftingCategory(category);
        } else {
            this.setItemCategory("objects", "furniture");
            this.setCraftingCategory("objects", "furniture");
        }
    }

    @Override
    public MultiTile getMultiTile(int rotation) {
        return new MultiTile(0, 0, 1, 2, rotation, false, this.getID(), this.counterID);
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/" + this.textureName);
    }

    @Override
    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        if (rotation == 0) {
            return new Rectangle(x * 32 + 2, y * 32 + 6, 28, 26);
        }
        if (rotation == 1) {
            return new Rectangle(x * 32, y * 32 + 6, 30, 24);
        }
        if (rotation == 2) {
            return new Rectangle(x * 32 + 2, y * 32, 28, 30);
        }
        return new Rectangle(x * 32 + 2, y * 32 + 6, 30, 24);
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
            options.add(texture.initDraw().sprite(3, 1, 32).light(light).pos(drawX, drawY - 32));
            options.add(texture.initDraw().sprite(3, 2, 32).light(light).pos(drawX, drawY));
        } else if (rotation == 1) {
            options.add(texture.initDraw().sprite(1, 1, 32, 64).light(light).pos(drawX, drawY - 32));
        } else if (rotation == 2) {
            options.add(texture.initDraw().sprite(2, 3, 32).light(light).pos(drawX, drawY));
        } else {
            options.add(texture.initDraw().sprite(0, 0, 32, 64).light(light).pos(drawX, drawY - 32));
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
        return Localization.translate("controls", "usetip");
    }

    @Override
    public void interact(Level level, int x, int y, PlayerMob player) {
        super.interact(level, x, y, player);
        this.getMultiTile(level.getObjectRotation(x, y)).getMasterLevelObject(level, 0, x, y).ifPresent(e -> e.interact(player));
    }

    @Override
    protected SoundSettings getInteractSoundOpen() {
        return null;
    }

    @Override
    public boolean isMasterBedObject(Level level, int tileX, int tileY) {
        return false;
    }

    @Override
    public LevelObject getSettlerBedMasterLevelObject(Level level, int tileX, int tileY) {
        return this.getMultiTile(level, 0, tileX, tileY).getMasterLevelObject(level, 0, tileX, tileY).orElse(null);
    }

    @Override
    public Rectangle getSettlerBedTileRectangle(Level level, int tileX, int tileY) {
        return this.getMultiTile(level, 0, tileX, tileY).getTileRectangle(tileX, tileY);
    }
}

