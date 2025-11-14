/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.registries.ObjectLayerRegistry
 *  necesse.engine.registries.TileRegistry
 *  necesse.inventory.lootTable.LootItemInterface
 *  necesse.inventory.lootTable.LootTable
 *  necesse.inventory.lootTable.lootItem.ChanceLootItem
 *  necesse.level.gameObject.GrassObject
 *  necesse.level.maps.Level
 */
package aphorea.objects;

import aphorea.utils.AphColors;
import necesse.engine.registries.ObjectLayerRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.level.gameObject.GrassObject;
import necesse.level.maps.Level;

public class InfectedGrassObject
extends GrassObject {
    public InfectedGrassObject() {
        super("infectedgrass", 1);
        this.mapColor = AphColors.infected_dark;
    }

    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        if (level.objectLayer.isPlayerPlaced(tileX, tileY)) {
            return super.getLootTable(level, layerID, tileX, tileY);
        }
        return new LootTable(new LootItemInterface[]{new ChanceLootItem(0.01f, "infectedgrassseed")});
    }

    public String canPlace(Level level, int layerID, int x, int y, int rotation, boolean byPlayer, boolean ignoreOtherLayers) {
        String error = super.canPlace(level, layerID, x, y, rotation, byPlayer, ignoreOtherLayers);
        if (error != null) {
            return error;
        }
        if (byPlayer && level.getTile((int)x, (int)y).isOrganic) {
            return null;
        }
        return level.getTileID(x, y) != TileRegistry.getTileID((String)"infectedgrasstile") ? "wrongtile" : null;
    }

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
        return tileID == TileRegistry.getTileID((String)"infectedgrasstile");
    }
}

