/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.worldStructures;

import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.worldStructures.WitchCabinParentPreset;

public class WitchCabin3Preset
extends WitchCabinParentPreset {
    public WitchCabin3Preset(GameRandom random) {
        super(11, 8);
        this.applyScript("PRESET = {\n\twidth = 11,\n\theight = 8,\n\ttileIDs = [8, dungeonfloor, 10, woodfloor],\n\ttiles = [-1, -1, 10, 10, 10, 10, -1, -1, -1, -1, -1, -1, -1, 10, 8, 10, 8, 10, 10, -1, -1, -1, -1, 10, 10, 8, 8, 8, 8, 10, -1, -1, -1, -1, 10, 8, 10, 8, 8, 8, 8, 8, 8, -1, -1, 10, 8, 10, 8, 8, 10, 8, 8, 8, -1, 10, 10, 10, 8, 8, 8, 8, 8, 8, 8, -1, -1, -1, 10, 8, 8, 8, 10, 10, -1, -1, -1, -1, -1, -1, 10, 10, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 98, air, 146, stonefence, 147, stonefencegate, 371, swampstonearrowtrap, 68, swampstonewall, 36, alchemytable, 311, dungeoncandelabra, 297, dungeonchest, 58, stonewall, 506, cookingpot, 302, dungeonchair],\n\tobjects = [98, 0, 146, 58, 68, 68, 0, 0, 0, 0, 0, 0, 0, 58, 311, 297, 311, 68, 0, 0, 0, 0, 0, 58, 0, 0, 0, 0, 0, 58, 0, 0, 0, 0, 146, 0, 146, 147, 146, 146, 371, 146, 146, 0, 0, 58, 0, 146, 0, 0, 0, 0, 0, 0, 0, 0, 0, 58, 146, 302, 506, 36, 146, 146, 146, 0, 0, 0, 146, 68, 146, 68, 68, 146, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\trotations = [0, 0, 1, 3, 3, 3, 0, 0, 2, 1, 1, 0, 0, 2, 0, 2, 0, 0, 3, 0, 0, 3, 0, 2, 0, 2, 2, 2, 0, 3, 0, 0, 1, 0, 2, 0, 3, 3, 3, 3, 2, 3, 3, 2, 0, 2, 0, 3, 2, 3, 0, 2, 3, 3, 1, 0, 0, 2, 3, 1, 2, 1, 0, 0, 0, 0, 0, 0, 2, 2, 2, 1, 0, 1, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\twire = [85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 93, 85, 85, 85, 85, 85, 85, 85, 85, 85, 93, 93, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85],\n\tlogicGates = {\n\t\tgate = {\n\t\t\ttileX = 6,\n\t\t\ttileY = 4,\n\t\t\tstringID = sensorgate,\n\t\t\tdata = {\n\t\t\t\toutputs = 0000,\n\t\t\t\twireOutputs = 0100,\n\t\t\t\tplayers = true,\n\t\t\t\tpassiveMobs = false,\n\t\t\t\thostileMobs = false,\n\t\t\t\trange = 1\n\t\t\t}\n\t\t}\n\t}\n}");
        this.addInventory(LootTablePresets.witchCrate, random, 4, 1, new Object[0]);
        PresetUtils.addFuelToInventory(this, 5, 5, random, "oaklog", 5, 15, true);
        this.addMob("spider", 2, 3, false);
        this.addMob("spider", 5, 4, false);
        this.addMob("spider", 6, 4, false);
        this.addMob("enchantedcrawlingzombie", 2, 4, false);
        this.addMob("enchantedcrawlingzombie", 2, 3, false);
        this.addMob("enchantedcrawlingzombie", 4, 2, false);
        this.addMob("enchantedcrawlingzombie", 6, 2, false);
        this.spawnWitch(5, 4);
    }
}

