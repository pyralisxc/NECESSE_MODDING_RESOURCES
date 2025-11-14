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

public class CaveRuins10
extends CaveRuins {
    public CaveRuins10(GameRandom random, WallSet wallSet, FurnitureSet furnitureSet, String floorStringID, LootTable chestLootTable, AtomicInteger chestLootRotation) {
        super("PRESET = {\n\twidth = 8,\n\theight = 7,\n\ttileIDs = [11, rockfloor],\n\ttiles = [-1, -1, -1, 11, 11, 11, 11, -1, -1, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, -1, -1, -1, 11, 11, 11, 11, 11, -1, -1, -1],\n\tobjectIDs = [0, air, 97, oakmodulartable, 99, oakbench, 19, carpentersbench, 100, oakbench2, 20, carpentersbench2, 101, oakbookshelf, 102, oakcabinet, 26, woodwall, 108, oakdisplay, 93, oakchest],\n\tobjects = [-1, -1, -1, 26, 26, 26, 26, -1, -1, 26, 26, 26, 99, 100, 26, 26, 26, 26, 108, 93, 0, 0, 19, 26, 26, 97, 0, 0, 0, 0, 20, 26, 26, 0, 0, 0, 26, 0, 0, 26, 26, 102, 101, 101, 26, -1, -1, -1, 26, 26, 26, 26, 26, -1, -1, -1],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 1, 1, 1, 1, 2, 1, 3, 3, 2, 2, 0, 0, 2, 2, 3, 1, 0, 0, 0, 0, 2, 2, 2, 0, 0, 0, 3, 0, 0, 1, 2, 0, 0, 0, 3, 0, 0, 0, 2, 2, 1, 1, 3, 0, 0, 0]\n}", random, wallSet, furnitureSet, floorStringID);
        this.addInventory(chestLootTable, random, 3, 2, chestLootRotation);
    }
}

