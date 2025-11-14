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

public class SurfaceGrassObject
extends GrassObject {
    public SurfaceGrassObject() {
        super("grass", 2);
        this.mapColor = new Color(69, 105, 0);
    }

    @Override
    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        if (level.objectLayer.isPlayerPlaced(tileX, tileY)) {
            return super.getLootTable(level, layerID, tileX, tileY);
        }
        float baitChance = 35.0f;
        if (level.weatherLayer.isRaining()) {
            baitChance = 15.0f;
        }
        String seedItem = "grassseed";
        if (level.getTileID(tileX, tileY) == TileRegistry.overgrownGrassID) {
            seedItem = "overgrown" + seedItem;
        }
        return new LootTable(new ChanceLootItem(1.0f / baitChance, "wormbait"), new ChanceLootItem(0.01f, seedItem));
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
        if (tileID != TileRegistry.grassID && tileID != TileRegistry.overgrownGrassID) {
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
        return tileID == TileRegistry.grassID || tileID == TileRegistry.overgrownGrassID;
    }
}

