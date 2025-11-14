/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.vilagePresets.pirateVillagePresets;

import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.maps.presets.modularPresets.vilagePresets.pirateVillagePresets.PirateVillagePreset;

public class PirateVillageWalkway1HPreset
extends PirateVillagePreset {
    public PirateVillageWalkway1HPreset(GameRandom random, AtomicInteger chestRotation) {
        super(3, 3, true, random);
        this.applyScript("PRESET = {\n\twidth = 9,\n\theight = 9,\n\ttileIDs = [36, graveltile, 12, woodfloor],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 12, 12, 12, 12, 12, 12, 12, -1, -1, 12, 12, 12, 12, 12, 12, 12, -1, 36, 12, 12, 12, 12, 12, 12, 12, 36, 36, 12, 12, 12, 12, 12, 12, 12, 36, 36, 12, 12, 12, 12, 12, 12, 12, 36, -1, 12, 12, 12, 12, 12, 12, 12, -1, -1, 12, 12, 12, 12, 12, 12, 12, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 195, goldlamp, 40, workstationduo, 41, workstationduo2, 42, forge, 187, storagebox, 63, woodwall],\n\tobjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 63, 63, 63, 63, 63, 63, 63, 0, 0, 63, 0, 42, 187, 40, 41, 63, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 63, 0, 0, 195, 0, 0, 63, 0, 0, 63, 63, 63, 63, 63, 63, 63, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true\n}");
        this.applyRandomCoinStack(2, 2, random);
        this.applyRandomCoinStack(6, 6, random);
        this.addInventory(LootTablePresets.pirateChest, random, 4, 2, chestRotation);
        this.open(0, 1, 3);
        this.open(2, 1, 1);
    }
}

