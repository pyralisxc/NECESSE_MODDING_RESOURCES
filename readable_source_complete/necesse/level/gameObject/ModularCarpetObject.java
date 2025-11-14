/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Point;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.ObjectLayerRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.gfx.gameTexture.MergeFunction;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.furniture.RoomFurniture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class ModularCarpetObject
extends GameObject
implements RoomFurniture {
    protected String textureName;
    protected int textureTileWidth;
    protected int textureTileHeight;
    protected ObjectDamagedTextureArray[][] masked;

    public ModularCarpetObject(String textureName, Color mapColor) {
        this.textureName = textureName;
        this.mapColor = mapColor;
        this.objectHealth = 50;
        this.toolType = ToolType.ALL;
        this.isLightTransparent = true;
        this.validObjectLayers.add(ObjectLayerRegistry.TILE_LAYER);
        this.hoverHitboxSortY = -16;
        this.setItemCategory("objects", "decorations", "carpets");
        this.setCraftingCategory("objects", "decorations", "carpets");
        this.replaceRotations = false;
        this.replaceCategories.add("carpet");
        this.canReplaceCategories.add("carpet");
    }

    @Override
    public void loadTextures() {
        GameTexture texture = GameTexture.fromFile("objects/carpets/" + this.textureName, true);
        this.textureTileWidth = texture.getWidth() / 32;
        this.textureTileHeight = texture.getHeight() / 32;
        GameTexture mask = GameTexture.fromFile("objects/carpets/" + this.textureName + "mask", true);
        int maskWidth = mask.getWidth() / 32 + 1;
        int maskHeight = mask.getHeight() / 32;
        this.masked = new ObjectDamagedTextureArray[this.textureTileWidth][this.textureTileHeight];
        for (int x = 0; x < this.textureTileWidth; ++x) {
            for (int y = 0; y < this.textureTileHeight; ++y) {
                GameTexture part = new GameTexture("objects/carpets/" + this.textureName + " part" + x + "x" + y, maskWidth * 32, maskHeight * 32);
                for (int mX = 0; mX < maskWidth; ++mX) {
                    for (int mY = 0; mY < maskHeight; ++mY) {
                        part.mergeSprite(texture, x, y, 32, mX * 32, mY * 32);
                    }
                }
                part.merge(mask, 0, 0, MergeFunction.MULTIPLY);
                part.makeFinal();
                this.masked[x][y] = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, part);
            }
        }
        texture.makeFinal();
        mask.makeFinal();
    }

    public GameTextureSection getTopLeft(GameTextureSection mask, boolean[] adj) {
        int topLeft = 0;
        int top = 1;
        int left = 3;
        if (adj[left]) {
            if (adj[top]) {
                if (adj[topLeft]) {
                    return mask.sprite(4, 0, 16);
                }
                return mask.sprite(2, 2, 16);
            }
            return mask.sprite(2, 0, 16);
        }
        if (adj[top]) {
            return mask.sprite(0, 2, 16);
        }
        return mask.sprite(0, 0, 16);
    }

    public GameTextureSection getTopRight(GameTextureSection mask, boolean[] adj) {
        int topRight = 2;
        int top = 1;
        int right = 4;
        if (adj[right]) {
            if (adj[top]) {
                if (adj[topRight]) {
                    return mask.sprite(5, 0, 16);
                }
                return mask.sprite(3, 2, 16);
            }
            return mask.sprite(3, 0, 16);
        }
        if (adj[top]) {
            return mask.sprite(1, 2, 16);
        }
        return mask.sprite(1, 0, 16);
    }

    public GameTextureSection getBotLeft(GameTextureSection mask, boolean[] adj) {
        int left = 3;
        int botLeft = 5;
        int bot = 6;
        if (adj[left]) {
            if (adj[bot]) {
                if (adj[botLeft]) {
                    return mask.sprite(4, 1, 16);
                }
                return mask.sprite(2, 3, 16);
            }
            return mask.sprite(2, 1, 16);
        }
        if (adj[bot]) {
            return mask.sprite(0, 3, 16);
        }
        return mask.sprite(0, 1, 16);
    }

    public GameTextureSection getBotRight(GameTextureSection mask, boolean[] adj) {
        int right = 4;
        int bot = 6;
        int botRight = 7;
        if (adj[right]) {
            if (adj[bot]) {
                if (adj[botRight]) {
                    return mask.sprite(5, 1, 16);
                }
                return mask.sprite(3, 3, 16);
            }
            return mask.sprite(3, 1, 16);
        }
        if (adj[bot]) {
            return mask.sprite(1, 3, 16);
        }
        return mask.sprite(1, 1, 16);
    }

    @Override
    public void addLayerDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int layerID, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        boolean[] isSame = new boolean[Level.adjacentGetters.length];
        boolean allIsSame = true;
        LinkedHashSet<Integer> layers = this.getValidObjectLayers();
        for (int i = 0; i < Level.adjacentGetters.length; ++i) {
            Point p = Level.adjacentGetters[i];
            boolean same = false;
            Iterator iterator = layers.iterator();
            while (iterator.hasNext()) {
                int layer = (Integer)iterator.next();
                int adjID = level.getObjectID(layer, tileX + p.x, tileY + p.y);
                if (adjID != this.getID()) continue;
                same = true;
                break;
            }
            isSame[i] = same;
            if (same) continue;
            allIsSame = false;
        }
        int spriteX = Math.floorMod(tileX, this.textureTileWidth);
        int spriteY = Math.floorMod(tileY, this.textureTileHeight);
        GameTexture texture = this.masked[spriteX][spriteY].getDamagedTexture(this, level, layerID, tileX, tileY);
        GameTextureSection mask = new GameTextureSection(texture);
        SharedTextureDrawOptions options = new SharedTextureDrawOptions(mask.getTexture());
        if (allIsSame) {
            options.add(mask.sprite(2, 0, 32)).light(light).pos(drawX, drawY);
        } else {
            options.add(this.getTopLeft(mask, isSame)).light(light).pos(drawX, drawY);
            options.add(this.getTopRight(mask, isSame)).light(light).pos(drawX + 16, drawY);
            options.add(this.getBotLeft(mask, isSame)).light(light).pos(drawX, drawY + 16);
            options.add(this.getBotRight(mask, isSame)).light(light).pos(drawX + 16, drawY + 16);
        }
        tileList.add(-10000, tm -> options.draw());
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        Integer[] adj = level.getAdjacentObjectsInt(tileX, tileY);
        boolean[] isSame = new boolean[adj.length];
        boolean allIsSame = true;
        int id = this.getID();
        for (int i = 0; i < adj.length; ++i) {
            boolean same;
            isSame[i] = same = adj[i] == id;
            if (same) continue;
            allIsSame = false;
        }
        int spriteX = Math.floorMod(tileX, this.textureTileWidth);
        int spriteY = Math.floorMod(tileY, this.textureTileHeight);
        GameTexture texture = this.masked[spriteX][spriteY].getDamagedTexture(0.0f);
        GameTextureSection mask = new GameTextureSection(texture);
        SharedTextureDrawOptions options = new SharedTextureDrawOptions(mask.getTexture());
        if (allIsSame) {
            options.add(mask.sprite(4, 0, 32)).alpha(alpha).pos(drawX, drawY);
        } else {
            options.add(this.getTopLeft(mask, isSame)).alpha(alpha).pos(drawX, drawY);
            options.add(this.getTopRight(mask, isSame)).alpha(alpha).pos(drawX + 16, drawY);
            options.add(this.getBotLeft(mask, isSame)).alpha(alpha).pos(drawX, drawY + 16);
            options.add(this.getBotRight(mask, isSame)).alpha(alpha).pos(drawX + 16, drawY + 16);
        }
        options.draw();
    }

    @Override
    public String getFurnitureType() {
        return "carpet";
    }

    @Override
    public String canPlace(Level level, int layerID, int x, int y, int rotation, boolean byPlayer, boolean ignoreOtherLayers) {
        String superError = super.canPlace(level, layerID, x, y, rotation, byPlayer, ignoreOtherLayers);
        if (superError != null) {
            return superError;
        }
        if (layerID != 0) {
            GameObject object = level.getObject(0, x, y);
            if (object.isWall && !object.isDoor || object.isRock) {
                return "tilecovered";
            }
        }
        return null;
    }

    @Override
    public boolean isValid(Level level, int layerID, int x, int y) {
        if (!super.isValid(level, layerID, x, y)) {
            return false;
        }
        if (layerID != 0) {
            GameObject object = level.getObject(0, x, y);
            return (!object.isWall || object.isDoor) && !object.isRock;
        }
        return true;
    }
}

