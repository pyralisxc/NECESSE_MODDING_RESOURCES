/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.runeboundTombPresets;

import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.maps.presets.modularPresets.runeboundTombPresets.RuneboundTombPreset;

public class RuneboundTombDeadEnd1Preset
extends RuneboundTombPreset {
    public RuneboundTombDeadEnd1Preset(GameRandom random) {
        super(1, 1, random);
        this.applyScript("PRESET = {\n\twidth = 10,\n\theight = 10,\n\ttileIDs = [57, basaltrocktile, 58, basaltfloor, 59, basaltpathtile],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 57, 58, 58, 58, -1, -1, -1, -1, -1, 58, 58, 59, 59, 57, 58, -1, -1, -1, -1, 58, 59, 59, 59, 59, 58, -1, -1, -1, -1, 57, 57, 59, 57, 59, 58, -1, -1, -1, -1, 58, 58, 59, 59, 57, 58, -1, -1, -1, -1, -1, 57, 57, 59, 58, -1, -1, -1, -1, -1, -1, -1, 59, 59, -1, -1, -1, -1, -1, -1, -1, 58, 59, 57, 58, -1, -1, -1],\n\tobjectIDs = [0, air, 160, basaltdoor, 978, cobweb, 282, wallbasalttorch, 318, basaltcoffin, 159, basaltwall, 319, basaltcoffin2, 1023, crate],\n\tobjects = [159, 159, 159, 0, 0, 0, 0, 159, 159, 159, 159, 159, 159, 159, 159, 159, 159, 159, 159, 159, 159, 159, 159, 282, 318, 319, 282, 159, 159, 159, 0, 159, 319, 978, 0, 1023, 1023, 318, 159, 0, 0, 159, 318, 0, 0, 0, 0, 319, 159, 0, 0, 159, 319, 978, 0, 0, 0, 318, 159, 0, 0, 159, 318, 0, 0, 0, 978, 319, 159, 0, 159, 159, 159, 1023, 0, 0, 978, 159, 159, 159, 159, 159, 159, 159, 160, 160, 159, 159, 159, 159, 159, 159, 159, 978, 0, 0, 1023, 159, 159, 159],\n\trotations = [3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 3, 3, 3, 0, 0, 2, 0, 2, 1, 1, 2, 0, 0, 0, 0, 2, 0, 1, 0, 2, 0, 2, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0, 2, 0, 1, 0, 0, 0, 2, 0, 0, 0, 2, 0, 2, 0, 0, 1, 2, 0, 0, 0, 2, 0, 3, 0, 0, 3, 1, 1, 1, 0, 0, 0, 1, 2, 2, 1, 1, 0, 0, 1, 0, 1, 0, 0, 0, 2, 1, 0, 1],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true,\n\twire = [85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 117, 117, 117, 117, 85, 85, 85, 85, 85, 85, 85, 119, 85, 85, 85, 85, 85, 85, 85, 85, 85, 87, 87, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85],\n\tlogicGates = {\n\t\tgate = {\n\t\t\ttileX = 4,\n\t\t\ttileY = 4,\n\t\t\tstringID = sensorgate,\n\t\t\tdata = {\n\t\t\t\toutputs = 0000,\n\t\t\t\twireOutputs = 1000,\n\t\t\t\tplayers = true,\n\t\t\t\tpassiveMobs = false,\n\t\t\t\thostileMobs = false,\n\t\t\t\trange = 3\n\t\t\t}\n\t\t},\n\t\tgate = {\n\t\t\ttileX = 5,\n\t\t\ttileY = 4,\n\t\t\tstringID = sensorgate,\n\t\t\tdata = {\n\t\t\t\toutputs = 0000,\n\t\t\t\twireOutputs = 1000,\n\t\t\t\tplayers = true,\n\t\t\t\tpassiveMobs = false,\n\t\t\t\thostileMobs = false,\n\t\t\t\trange = 3\n\t\t\t}\n\t\t},\n\t\tgate = {\n\t\t\ttileX = 4,\n\t\t\ttileY = 3,\n\t\t\tstringID = norgate,\n\t\t\tdata = {\n\t\t\t\toutputs = 0010,\n\t\t\t\twireInputs = 1000,\n\t\t\t\twireOutputs = 0010\n\t\t\t}\n\t\t}\n\t}\n}");
        this.addInventory(LootTablePresets.runeboundTombCoffin, random, 2, 4, new Object[0]);
        this.addInventory(LootTablePresets.runeboundTombCoffin, random, 2, 6, new Object[0]);
        this.addInventory(LootTablePresets.runeboundTombCoffin, random, 4, 2, new Object[0]);
        this.addInventory(LootTablePresets.runeboundTombCoffin, random, 7, 3, new Object[0]);
        this.addInventory(LootTablePresets.runeboundTombCoffin, random, 7, 5, new Object[0]);
        this.open(0, 0, 2);
    }
}

