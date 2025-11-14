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

public class CaveRuins5
extends CaveRuins {
    public CaveRuins5(GameRandom random, WallSet wallSet, FurnitureSet furnitureSet, String floorStringID, LootTable chestLootTable, AtomicInteger chestLootRotation) {
        super("PRESET = {\n\twidth = 5,\n\theight = 5,\n\ttileIDs = [17, stonefloor],\n\ttiles = [17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, -1, 17, 17, 17, 17],\n\tobjectIDs = [0, air, 295, oakchest, 40, workstationduo, 41, workstationduo2, 42, forge, 301, oakbench, 302, oakbench2, 63, woodwall],\n\tobjects = [63, 63, 63, 63, 63, 63, 41, 295, 42, 63, 63, 40, 0, 0, 0, 63, 63, 302, 301, 0, -1, 63, 63, 63, 63],\n\trotations = [0, 0, 1, 1, 2, 0, 0, 2, 2, 2, 1, 0, 0, 2, 2, 1, 1, 3, 3, 1, 3, 1, 1, 1, 1],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true\n}", random, wallSet, furnitureSet, floorStringID);
        this.addInventory(chestLootTable, random, 2, 1, chestLootRotation);
    }
}

