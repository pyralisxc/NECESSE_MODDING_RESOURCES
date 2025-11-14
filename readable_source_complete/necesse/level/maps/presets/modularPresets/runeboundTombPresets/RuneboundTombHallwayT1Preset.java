/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.runeboundTombPresets;

import necesse.engine.util.GameRandom;
import necesse.level.maps.presets.modularPresets.runeboundTombPresets.RuneboundTombPreset;

public class RuneboundTombHallwayT1Preset
extends RuneboundTombPreset {
    public RuneboundTombHallwayT1Preset(GameRandom random) {
        super(1, 1, random);
        this.applyScript("PRESET = {\n\twidth = 10,\n\theight = 10,\n\ttileIDs = [57, basaltrocktile, 58, basaltfloor, 59, basaltpathtile],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 58, -1, -1, 57, -1, -1, -1, 57, -1, 58, 59, 57, 59, 59, 57, 58, 58, 57, 59, 59, 57, 58, 57, 57, 59, 59, 59, 59, 59, 57, 59, 57, 58, 59, 59, 57, 59, 58, -1, 58, 59, 57, 59, 59, 58, 57, 57, -1, -1, -1, 58, 59, 59, 58, -1, -1, -1, -1, -1, -1, -1, 59, 57, -1, -1, -1, -1, -1, -1, -1, 57, 57, 59, 58, -1, -1, -1],\n\tobjectIDs = [0, air, 160, basaltdoor, 978, cobweb, 282, wallbasalttorch, 159, basaltwall, 1023, crate],\n\tobjects = [159, 159, 159, 0, 159, 159, 0, 159, 159, 159, 159, 159, 159, 159, 159, 159, 159, 159, 159, 159, 159, 159, 159, 282, 159, 159, 282, 159, 159, 159, 978, 159, 1023, 0, 0, 1023, 0, 978, 978, 0, 0, 160, 0, 0, 0, 0, 0, 0, 0, 0, 0, 160, 0, 0, 0, 0, 0, 0, 1023, 0, 0, 159, 978, 0, 0, 0, 0, 1023, 1023, 0, 159, 159, 159, 282, 0, 0, 282, 159, 159, 159, 159, 159, 159, 159, 160, 160, 159, 159, 159, 159, 159, 159, 159, 1023, 0, 0, 0, 159, 159, 159],\n\trotations = [2, 2, 2, 0, 3, 3, 0, 2, 2, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 2, 2, 2, 2, 0, 3, 3, 3, 3, 3, 0, 0, 1, 0, 3, 1, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 3, 0, 0, 3, 3, 0, 0, 0, 0, 3, 3, 2, 2, 2, 3, 0, 0, 0, 0, 3, 2, 0, 2, 2, 2, 3, 2, 2, 3, 2, 2, 2, 2, 2, 2, 1, 0, 0, 2, 2, 2, 2],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true,\n\twire = [85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 117, 117, 117, 117, 85, 85, 85, 85, 85, 85, 85, 85, 117, 85, 85, 85, 85, 85, 85, 85, 85, 87, 119, 85, 85, 85, 85, 85, 85, 85, 85, 85, 119, 85, 85, 85, 85, 85, 85, 85, 85, 85, 117, 85, 85, 85, 85, 85, 85, 85, 117, 117, 117, 117, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85],\n\tlogicGates = {\n\t\tgate = {\n\t\t\ttileX = 4,\n\t\t\ttileY = 4,\n\t\t\tstringID = sensorgate,\n\t\t\tdata = {\n\t\t\t\toutputs = 0000,\n\t\t\t\twireOutputs = 1000,\n\t\t\t\tplayers = true,\n\t\t\t\tpassiveMobs = false,\n\t\t\t\thostileMobs = false,\n\t\t\t\trange = 4\n\t\t\t}\n\t\t},\n\t\tgate = {\n\t\t\ttileX = 5,\n\t\t\ttileY = 4,\n\t\t\tstringID = norgate,\n\t\t\tdata = {\n\t\t\t\toutputs = 0010,\n\t\t\t\twireInputs = 1000,\n\t\t\t\twireOutputs = 0010\n\t\t\t}\n\t\t},\n\t\tgate = {\n\t\t\ttileX = 5,\n\t\t\ttileY = 5,\n\t\t\tstringID = sensorgate,\n\t\t\tdata = {\n\t\t\t\toutputs = 0000,\n\t\t\t\twireOutputs = 1000,\n\t\t\t\tplayers = true,\n\t\t\t\tpassiveMobs = false,\n\t\t\t\thostileMobs = false,\n\t\t\t\trange = 4\n\t\t\t}\n\t\t}\n\t}\n}");
        this.open(0, 0, 1);
        this.open(0, 0, 2);
        this.open(0, 0, 3);
    }
}

