/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.worldStructures;

import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.worldStructures.WitchCabinParentPreset;

public class WitchCabin1Preset
extends WitchCabinParentPreset {
    public WitchCabin1Preset(GameRandom random) {
        super(11, 8);
        this.applyScript("PRESET = {\n\twidth = 11,\n\theight = 8,\n\ttileIDs = [5, swampgrasstile, 8, dungeonfloor, 10, woodfloor],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, 8, -1, -1, -1, 8, 5, 5, 5, 8, 10, 10, 8, 8, -1, -1, 5, 10, 8, 8, 5, 10, 10, 8, 8, -1, -1, 5, 10, 8, -1, 10, 10, -1, 10, 8, 8, 8, 5, 8, -1, -1, 8, 8, 8, 8, 8, -1, -1, 8, -1, 5, 8, 10, 10, -1, 8, -1, -1, -1, -1, 8, 8, 10, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 10, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 146, stonefence, 371, swampstonearrowtrap, 147, stonefencegate, 68, swampstonewall, 311, dungeoncandelabra, 359, air, 297, dungeonchest, 58, stonewall, 506, cookingpot, 302, dungeonchair],\n\tobjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 68, 68, 58, 0, 146, 58, 68, 146, 0, 0, 58, 311, 297, 311, 371, 146, 0, 0, 58, 0, 0, 68, 302, 0, 0, 0, 147, 0, 0, 58, 0, 0, 68, 359, 0, 0, 0, 146, 0, 0, 146, 0, 0, 146, 58, 68, 506, 0, 58, 146, 146, 146, 0, 0, 0, 0, 146, 146, 0, 146, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 3, 0, 0, 0, 1, 0, 0, 0, 3, 0, 2, 0, 2, 0, 0, 0, 1, 0, 0, 3, 1, 0, 0, 0, 3, 0, 0, 1, 0, 0, 3, 1, 0, 0, 2, 3, 3, 2, 2, 0, 0, 2, 3, 2, 3, 0, 0, 3, 3, 1, 0, 0, 0, 0, 3, 3, 3, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 0, 0],\n\twire = [85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 87, 85, 85, 85, 85, 85, 85, 85, 85, 117, 117, 127, 93, 85, 85, 85, 85, 85, 85, 117, 117, 85, 87, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85],\n\tlogicGates = {\n\t\tgate = {\n\t\t\ttileX = 5,\n\t\t\ttileY = 4,\n\t\t\tstringID = sensorgate,\n\t\t\tdata = {\n\t\t\t\toutputs = 0000,\n\t\t\t\twireOutputs = 1000,\n\t\t\t\tplayers = true,\n\t\t\t\tpassiveMobs = false,\n\t\t\t\thostileMobs = false,\n\t\t\t\trange = 1\n\t\t\t}\n\t\t},\n\t\tgate = {\n\t\t\ttileX = 5,\n\t\t\ttileY = 3,\n\t\t\tstringID = srlatchgate,\n\t\t\tdata = {\n\t\t\t\toutputs = 0000,\n\t\t\t\tactivateInputs = 1000,\n\t\t\t\tresetInputs = 0010,\n\t\t\t\twireOutputs = 0100,\n\t\t\t\tactive = false\n\t\t\t}\n\t\t}\n\t}\n}");
        this.addInventory(LootTablePresets.witchCrate, random, 3, 2, new Object[0]);
        PresetUtils.addFuelToInventory(this, 4, 6, random, "oaklog", 5, 15, true);
        this.addMob("spider", 1, 1, false);
        this.addMob("spider", 5, 1, false);
        this.addMob("spider", 6, 7, false);
        this.addMob("enchantedcrawlingzombie", 7, 2, false);
        this.addMob("enchantedcrawlingzombie", 7, 3, false);
        this.addMob("enchantedcrawlingzombie", 8, 3, false);
        this.spawnWitch(3, 3);
    }
}

