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

public class CaveRuins9
extends CaveRuins {
    public CaveRuins9(GameRandom random, WallSet wallSet, FurnitureSet furnitureSet, String floorStringID, LootTable chestLootTable, AtomicInteger chestLootRotation) {
        super("PRESET = {\n\twidth = 7,\n\theight = 8,\n\ttileIDs = [11, rockfloor],\n\ttiles = [-1, 11, 11, 11, 11, 11, -1, -1, 11, 11, 11, 11, 11, -1, -1, 11, 11, 11, 11, 11, 11, -1, 11, 11, 11, 11, 11, 11, -1, -1, 11, 11, 11, 11, 11, -1, 11, 11, 11, 11, 11, -1, 11, 11, 11, 11, 11, -1, -1, 11, 11, 11, 11, -1, -1, -1],\n\tobjectIDs = [0, air, 96, oakdesk, 98, oakchair, 101, oakbookshelf, 102, oakcabinet, 103, oakbed, 23, alchemytable, 104, oakbed2, 26, woodwall, 93, oakchest],\n\tobjects = [-1, 26, 26, 26, 26, 26, -1, -1, 26, 103, 0, 102, 26, -1, -1, 26, 104, 0, 0, 26, 26, -1, 26, 26, 0, 0, 23, 26, -1, -1, 26, 0, 0, 93, 26, -1, 26, 26, 0, 0, 0, -1, 26, 26, 96, 98, 0, -1, -1, 26, 101, 0, 0, -1, -1, -1],\n\trotations = [0, 0, 0, 1, 1, 1, 0, 0, 0, 2, 0, 2, 2, 0, 0, 0, 2, 0, 0, 2, 2, 0, 0, 0, 0, 0, 3, 2, 0, 0, 0, 0, 0, 3, 2, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 3, 0, 0, 0, 1, 1, 3, 0, 0, 0, 0]\n}", random, wallSet, furnitureSet, floorStringID);
        this.addInventory(chestLootTable, random, 5, 4, chestLootRotation);
    }
}

