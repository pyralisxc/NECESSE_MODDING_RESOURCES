/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.vilagePresets.pirateVillagePresets;

import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.maps.presets.modularPresets.vilagePresets.pirateVillagePresets.PirateVillagePreset;

public class PirateVillageWalkway1VPreset
extends PirateVillagePreset {
    public PirateVillageWalkway1VPreset(GameRandom random, AtomicInteger chestRotation) {
        super(3, 3, true, random);
        this.applyScript("PRESET = {\n\twidth = 9,\n\theight = 9,\n\ttileIDs = [36, graveltile, 12, woodfloor],\n\ttiles = [-1, -1, -1, 36, 36, 36, -1, -1, -1, -1, 12, 12, 12, 12, 12, 12, 12, -1, -1, 12, 12, 12, 12, 12, 12, 12, -1, -1, 12, 12, 12, 12, 12, 12, 12, -1, -1, 12, 12, 12, 12, 12, 12, 12, -1, -1, 12, 12, 12, 12, 12, 12, 12, -1, -1, 12, 12, 12, 12, 12, 12, 12, -1, -1, 12, 12, 12, 12, 12, 12, 12, -1, -1, -1, -1, 36, 36, 36, -1, -1, -1],\n\tobjectIDs = [0, air, 195, goldlamp, 40, workstationduo, 41, workstationduo2, 42, forge, 187, storagebox, 63, woodwall],\n\tobjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 63, 63, 0, 0, 0, 63, 63, 0, 0, 63, 41, 0, 0, 0, 0, 63, 0, 0, 63, 40, 0, 0, 0, 0, 63, 0, 0, 63, 187, 0, 0, 0, 195, 63, 0, 0, 63, 42, 0, 0, 0, 0, 63, 0, 0, 63, 0, 0, 0, 0, 0, 63, 0, 0, 63, 63, 0, 0, 0, 63, 63, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 2, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true\n}");
        this.applyRandomCoinStack(6, 2, random);
        this.applyRandomCoinStack(2, 6, random);
        this.addInventory(LootTablePresets.pirateChest, random, 2, 4, chestRotation);
        this.open(1, 0, 0);
        this.open(1, 2, 2);
    }
}

