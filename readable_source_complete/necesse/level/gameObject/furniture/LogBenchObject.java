/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.furniture;

import java.awt.Color;
import necesse.engine.registries.ObjectRegistry;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.gameObject.furniture.Bench2Object;
import necesse.level.gameObject.furniture.BenchObject;
import necesse.level.maps.Level;

public class LogBenchObject
extends BenchObject {
    private final String logStringID;

    protected LogBenchObject(String textureName, String logStringID, ToolType toolType, Color mapColor) {
        super(textureName, toolType, mapColor, new String[0]);
        this.logStringID = logStringID;
        this.setItemCategory("objects", "landscaping", "plants");
    }

    @Override
    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        if (level.objectLayer.isPlayerPlaced(tileX, tileY)) {
            return super.getLootTable(level, layerID, tileX, tileY);
        }
        LootTable lootTable = new LootTable();
        if (this.logStringID != null) {
            lootTable.items.add(LootItem.between(this.logStringID, 4, 5).splitItems(5));
        }
        return lootTable;
    }

    public static int[] registerLogBench(String stringID, String textureName, String logStringID, ToolType toolType, Color mapColor, float brokerValue) {
        int bdi;
        LogBenchObject buo = new LogBenchObject(textureName, logStringID, toolType, mapColor);
        Bench2Object bdo = new Bench2Object(textureName, toolType, mapColor, new String[0]);
        int bui = ObjectRegistry.registerObject(stringID, buo, brokerValue, true);
        buo.counterID = bdi = ObjectRegistry.registerObject(stringID + "2", bdo, 0.0f, false);
        bdo.counterID = bui;
        return new int[]{bui, bdi};
    }
}

