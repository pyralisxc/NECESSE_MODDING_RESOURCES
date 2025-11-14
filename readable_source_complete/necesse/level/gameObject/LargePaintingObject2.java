/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.ObjectLayerRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.ObjectHoverHitbox;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.SidedRotationMultiTile;

class LargePaintingObject2
extends GameObject {
    public ObjectDamagedTextureArray texture;
    protected int counterID;
    final String texturePath;

    protected LargePaintingObject2(String texturePath, Item.Rarity rarity) {
        this.texturePath = texturePath;
        this.drawDamage = false;
        this.objectHealth = 1;
        this.toolType = ToolType.ALL;
        this.rarity = rarity;
        this.validObjectLayers.add(ObjectLayerRegistry.WALL_DECOR);
        this.replaceCategories.add("painting");
        this.canReplaceCategories.add("painting");
    }

    @Override
    public void loadTextures() {
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/paintings/" + this.texturePath);
    }

    @Override
    public MultiTile getMultiTile(int rotation) {
        return new SidedRotationMultiTile(1, 0, 2, 1, rotation, false, this.counterID, this.getID());
    }

    @Override
    public void addLayerDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int layerID, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        int sortY;
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        byte rotation = level.getObjectRotation(layerID, tileX, tileY);
        GameTexture texture = this.texture.getDamagedTexture(this, level, layerID, tileX, tileY);
        final DrawOptionsList options = new DrawOptionsList();
        if (rotation == 0) {
            sortY = 32;
            options.add(texture.initDraw().sprite(1, 2, 32, 64).light(light).pos(drawX, drawY - 16));
        } else if (rotation == 1) {
            sortY = 24;
            options.add(texture.initDraw().sprite(0, 7, 32, 32).light(light).pos(drawX, drawY - 16));
        } else if (rotation == 2) {
            sortY = 0;
            options.add(texture.initDraw().sprite(0, 0, 32, 64).light(light).pos(drawX, drawY - 64));
        } else {
            sortY = 24;
            options.add(texture.initDraw().sprite(1, 2, 32, 32).light(light).pos(drawX, drawY - 16));
        }
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return sortY;
            }

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.texture.getDamagedTexture(0.0f);
        if (rotation == 0) {
            texture.initDraw().sprite(1, 2, 32, 64).alpha(alpha).draw(drawX, drawY - 16);
        } else if (rotation == 1) {
            texture.initDraw().sprite(0, 7, 32, 32).alpha(alpha).draw(drawX, drawY - 16);
        } else if (rotation == 2) {
            texture.initDraw().sprite(0, 0, 32, 64).alpha(alpha).draw(drawX, drawY - 64);
        } else {
            texture.initDraw().sprite(1, 2, 32, 32).alpha(alpha).draw(drawX, drawY - 16);
        }
    }

    @Override
    protected ObjectHoverHitbox getHoverHitbox(Level level, int layerID, int tileX, int tileY) {
        byte rotation = level.getObjectRotation(layerID, tileX, tileY);
        if (rotation == 0) {
            return new ObjectHoverHitbox(layerID, tileX, tileY, 0, 0, 32, 32, 32);
        }
        if (rotation == 1 || rotation == 3) {
            return new ObjectHoverHitbox(layerID, tileX, tileY, 0, -16, 32, 48, 32);
        }
        return new ObjectHoverHitbox(layerID, tileX, tileY, 0, -32, 32, 64, 0);
    }

    @Override
    public boolean canReplace(Level level, int layerID, int tileX, int tileY, int rotation) {
        return super.canReplace(level, layerID, tileX, tileY, rotation) && this.attachesToObject(level, tileX, tileY, rotation);
    }

    public boolean attachesToObject(Level level, int tileX, int tileY) {
        return level.getObject((int)tileX, (int)tileY).isWall && !level.getObject((int)tileX, (int)tileY).isDoor;
    }

    public boolean attachesToObject(Level level, int tileX, int tileY, int rotation) {
        if (rotation == 0 && this.attachesToObject(level, tileX, tileY + 1)) {
            return true;
        }
        if (rotation == 1 && this.attachesToObject(level, tileX - 1, tileY)) {
            return true;
        }
        if (rotation == 2 && this.attachesToObject(level, tileX, tileY - 1)) {
            return true;
        }
        return rotation == 3 && this.attachesToObject(level, tileX + 1, tileY);
    }

    @Override
    public String canPlace(Level level, int layerID, int x, int y, int rotation, boolean byPlayer, boolean ignoreOtherLayers) {
        if (this.isTilePlaceOccupied(level, layerID, x, y, ignoreOtherLayers)) {
            return "occupied";
        }
        if (layerID != 0) {
            GameObject object = level.getObject(0, x, y);
            if (object.isWall && !object.isDoor || object.isRock) {
                return "tilecovered";
            }
        }
        return this.attachesToObject(level, x, y, rotation) ? null : "nowall";
    }

    @Override
    public boolean isValid(Level level, int layerID, int x, int y) {
        byte rotation = level.getObjectRotation(layerID, x, y);
        if (layerID != 0) {
            GameObject object = level.getObject(0, x, y);
            if (object.isWall && !object.isDoor || object.isRock) {
                return false;
            }
        }
        if (!this.attachesToObject(level, x, y, rotation)) {
            return false;
        }
        return this.getMultiTile(rotation).streamOtherIDs(x, y).allMatch(e -> level.getObjectID(layerID, e.tileX, e.tileY) == ((Integer)e.value).intValue());
    }
}

