/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import necesse.engine.registries.ObjectLayerRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.GrassObject;
import necesse.level.maps.Level;

public class SwampGrassObject
extends GrassObject {
    public SwampGrassObject() {
        super("swampgrass", 4);
        this.mapColor = new Color(62, 88, 47);
    }

    @Override
    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        if (level.objectLayer.isPlayerPlaced(tileX, tileY)) {
            return super.getLootTable(level, layerID, tileX, tileY);
        }
        float baitChance = 30.0f;
        if (level.weatherLayer.isRaining()) {
            baitChance = 10.0f;
        }
        if (level.isCave) {
            baitChance = 25.0f;
        }
        String seedItem = "swampgrassseed";
        if (level.getTileID(tileX, tileY) == TileRegistry.overgrownSwampGrassID) {
            seedItem = "overgrown" + seedItem;
        }
        return new LootTable(new ChanceLootItem(1.0f / baitChance, "swamplarva"), new ChanceLootItem(0.01f, seedItem));
    }

    @Override
    public boolean canPlaceOn(Level level, int layerID, int x, int y, GameObject other) {
        return other.getID() == 0 || !other.getValidObjectLayers().contains(ObjectLayerRegistry.TILE_LAYER);
    }

    @Override
    public String canPlace(Level level, int layerID, int x, int y, int rotation, boolean byPlayer, boolean ignoreOtherLayers) {
        String error = super.canPlace(level, layerID, x, y, rotation, byPlayer, ignoreOtherLayers);
        if (error != null) {
            return error;
        }
        if (level.getObjectID(ObjectLayerRegistry.TILE_LAYER, x, y) != 0) {
            return "occupied";
        }
        if (byPlayer && level.getTile((int)x, (int)y).isOrganic) {
            return null;
        }
        int tileID = level.getTileID(x, y);
        if (tileID != TileRegistry.swampRockID && tileID != TileRegistry.swampGrassID && tileID != TileRegistry.overgrownSwampGrassID) {
            return "wrongtile";
        }
        return null;
    }

    @Override
    public boolean isValid(Level level, int layerID, int x, int y) {
        if (!super.isValid(level, layerID, x, y)) {
            return false;
        }
        if (level.getObjectID(ObjectLayerRegistry.TILE_LAYER, x, y) != 0) {
            return false;
        }
        if (level.objectLayer.isPlayerPlaced(layerID, x, y) && level.getTile((int)x, (int)y).isOrganic) {
            return true;
        }
        int tileID = level.getTileID(x, y);
        return tileID == TileRegistry.swampRockID || tileID == TileRegistry.swampGrassID || tileID == TileRegistry.overgrownSwampGrassID;
    }
}

