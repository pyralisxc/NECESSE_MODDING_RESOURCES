/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.ObjectLayerRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.GrassObject;
import necesse.level.maps.Level;

public class DeepSwampGrassObject
extends GrassObject {
    public DeepSwampGrassObject() {
        super("deepswampgrass", 4);
        this.mapColor = new Color(47, 73, 44);
    }

    @Override
    public GameMessage getNewLocalization() {
        return new LocalMessage("object", "swampgrass");
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
        return new LootTable(new ChanceLootItem(1.0f / baitChance, "swamplarva"));
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
        if (tileID != TileRegistry.deepSwampRockID) {
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
        return level.getTileID(x, y) == TileRegistry.deepSwampRockID;
    }
}

