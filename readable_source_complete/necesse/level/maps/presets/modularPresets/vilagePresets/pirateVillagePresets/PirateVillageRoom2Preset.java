/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.vilagePresets.pirateVillagePresets;

import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.modularPresets.vilagePresets.pirateVillagePresets.PirateVillagePreset;

public class PirateVillageRoom2Preset
extends PirateVillagePreset {
    public PirateVillageRoom2Preset(GameRandom random, AtomicInteger chestRotation) {
        super(4, 4, false, random);
        this.applyScript("PRESET = {\n\twidth = 12,\n\theight = 12,\n\ttileIDs = [36, graveltile, 12, woodfloor],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 12, 12, 12, 12, 12, 12, 12, -1, -1, -1, -1, -1, 12, 12, 12, 12, 12, 12, 12, -1, -1, -1, -1, -1, 12, 12, 12, 12, 12, 12, 12, -1, -1, -1, -1, 36, 12, 12, 12, 12, 12, 12, 12, -1, -1, -1, -1, -1, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, -1, -1, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, -1, -1, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, -1, -1, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, -1, -1, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, -1, -1, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, -1, -1, -1, -1, -1, -1, -1, -1, 36, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 64, wooddoor, 257, armorstand, 322, sprucebookshelf, 195, goldlamp, 840, coinstack, 456, golddinnertable, 457, golddinnertable2, 458, goldchair, 460, leathercarpet, 47, ironanvil, 48, alchemytable, 597, sunflower, 187, storagebox, 317, sprucedesk, 63, woodwall, 319, sprucechair],\n\tobjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 63, 63, 63, 63, 63, 63, 63, 0, 0, 0, 0, 0, 63, 195, 257, 187, 47, 317, 63, 0, 0, 0, 0, 0, 63, 0, 460, 460, 0, 319, 63, 0, 0, 0, 0, 0, 64, 0, 460, 460, 0, 0, 63, 0, 0, 0, 0, 0, 63, 0, 840, 0, 0, 0, 63, 63, 63, 63, 0, 0, 63, 0, 0, 0, 0, 840, 458, 458, 597, 63, 0, 0, 63, 0, 0, 0, 0, 0, 456, 457, 0, 63, 0, 0, 63, 0, 0, 0, 0, 0, 458, 458, 0, 63, 0, 0, 63, 322, 322, 195, 48, 0, 0, 0, 0, 63, 0, 0, 63, 63, 63, 63, 63, 63, 64, 63, 63, 63, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true\n}");
        this.applyRandomCoinStack(2, 4, random);
        this.applyRandomCoinStack(6, 6, random);
        this.applyRandomCoinStack(9, 9, random);
        PresetUtils.applyRandomFlower(this, 9, 6, random);
        this.addInventory(LootTablePresets.pirateChest, random, 4, 2, chestRotation);
        this.open(0, 1, 3);
        this.open(2, 3, 2);
    }
}

