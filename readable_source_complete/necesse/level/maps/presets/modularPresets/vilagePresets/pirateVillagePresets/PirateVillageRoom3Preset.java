/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.vilagePresets.pirateVillagePresets;

import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.modularPresets.vilagePresets.pirateVillagePresets.PirateVillagePreset;

public class PirateVillageRoom3Preset
extends PirateVillagePreset {
    public PirateVillageRoom3Preset(GameRandom random, AtomicInteger displayStandRotation) {
        super(5, 4, false, random);
        this.applyScript("PRESET = {\n\twidth = 15,\n\theight = 12,\n\ttileIDs = [3, grasstile, 36, graveltile, 12, woodfloor],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, 36, -1, -1, -1, -1, -1, -1, -1, -1, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, -1, -1, -1, -1, -1, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, -1, -1, -1, -1, -1, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, -1, -1, -1, -1, -1, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, -1, -1, -1, -1, -1, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, -1, -1, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, -1, -1, 3, 3, 3, 3, 3, 12, 12, 12, 12, 12, 12, 12, 12, -1, -1, 3, 3, 3, 3, 3, 12, 12, 12, 12, 12, 12, 12, 12, -1, -1, 3, 3, 3, 3, 3, 12, 12, 12, 12, 12, 12, 12, 12, -1, -1, 3, 3, 3, 3, 3, 12, 12, 12, 12, 12, 12, 12, 12, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 36, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 64, wooddoor, 322, sprucebookshelf, 195, goldlamp, 40, workstationduo, 840, coinstack, 41, workstationduo2, 329, sprucedisplay, 42, forge, 43, carpentersbench, 459, woolcarpet, 11, pinesapling, 44, carpentersbench2, 597, sunflower, 63, woodwall],\n\tobjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 63, 63, 63, 63, 63, 63, 64, 63, 63, 63, 0, 0, 0, 0, 0, 63, 597, 43, 44, 0, 0, 0, 195, 0, 63, 0, 0, 0, 0, 0, 63, 0, 459, 459, 0, 0, 0, 0, 0, 63, 0, 0, 0, 0, 0, 63, 0, 459, 459, 0, 0, 0, 0, 0, 63, 0, 0, 0, 0, 0, 63, 195, 41, 40, 42, 840, 0, 0, 0, 63, 63, 63, 63, 0, 0, 63, 63, 63, 63, 63, 63, 0, 0, 0, 0, 329, 840, 63, 0, 0, 0, 0, 0, 0, 0, 63, 0, 0, 0, 0, 0, 0, 63, 0, 0, 0, 0, 0, 11, 0, 63, 0, 0, 0, 0, 0, 0, 63, 0, 0, 0, 11, 0, 0, 0, 63, 322, 322, 195, 0, 0, 597, 63, 0, 0, 0, 0, 0, 0, 0, 63, 63, 63, 63, 64, 63, 63, 63, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true\n}");
        this.applyRandomCoinStack(5, 2, random);
        this.applyRandomCoinStack(12, 6, random);
        this.applyRandomCoinStack(6, 5, random);
        PresetUtils.applyRandomFlower(this, 2, 2, random);
        PresetUtils.applyRandomFlower(this, 12, 9, random);
        int pineSapling = ObjectRegistry.getObjectID("pinesapling");
        this.setObject(4, 8, pineSapling);
        this.setObject(2, 9, pineSapling);
        this.addInventory(LootTablePresets.pirateDisplayStand, random, 11, 6, displayStandRotation);
        this.open(2, 0, 0);
        this.open(3, 3, 2);
    }
}

