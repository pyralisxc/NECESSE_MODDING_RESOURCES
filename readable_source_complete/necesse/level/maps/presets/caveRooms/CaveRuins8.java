/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.caveRooms;

import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.presets.caveRooms.CaveRuins;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.WallSet;

public class CaveRuins8
extends CaveRuins {
    public CaveRuins8(GameRandom random, WallSet wallSet, FurnitureSet furnitureSet, String floorStringID, LootTable chestLootTable, AtomicInteger chestLootRotation) {
        super("PRESET = {\n\twidth = 5,\n\theight = 7,\n\ttileIDs = [11, rockfloor],\n\ttiles = [11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, -1, 11, 11, -1, -1, -1],\n\tobjectIDs = [0, air, 96, oakdesk, 98, oakchair, 21, ironanvil, 102, oakcabinet, 26, woodwall, 93, oakchest],\n\tobjects = [26, 26, 26, 26, 26, 26, 102, 93, 21, 26, 26, 0, 0, 0, 26, 26, 26, 26, 0, 26, 26, 96, 98, 0, 26, 26, 0, 0, 0, -1, 26, 26, -1, -1, -1],\n\trotations = [0, 0, 0, 0, 2, 1, 2, 2, 2, 2, 0, 0, 0, 0, 2, 0, 0, 1, 0, 2, 0, 1, 3, 0, 1, 1, 0, 0, 0, 0, 3, 3, 0, 0, 0]\n}", random, wallSet, furnitureSet, floorStringID);
        this.addInventory(chestLootTable, random, 2, 1, chestLootRotation);
    }
}

