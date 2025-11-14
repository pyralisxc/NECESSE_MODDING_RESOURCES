/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.worldStructures;

import necesse.engine.util.GameRandom;
import necesse.level.maps.presets.set.FenceSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.WallSet;
import necesse.level.maps.presets.worldStructures.LandStructurePreset;

public class Carpenter2Preset
extends LandStructurePreset {
    public Carpenter2Preset(GameRandom random, WallSet wallSet, FenceSet fenceSet, FurnitureSet furnitureSet) {
        super(6, 10);
        this.applyScript("PRESET = {\n\twidth = 6,\n\theight = 10,\n\ttileIDs = [12, woodfloor, 15, woodpathtile],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 12, 12, 12, -1, -1, 12, 12, 12, 12, 12, 12, 12, 12, 15, 15, 12, 12, 12, 12, 15, 15, 12, 15, 12, 12, 15, 15, 12, 15, 12, 12, 15, 15, 12, 12, 12, 12, 12, 12, 12, 12, -1, 12, 12, 12, -1, -1],\n\tobjectIDs = [0, air, 448, goldgridcarpet, 34, workstation, 290, oakbed, 291, oakbed2, 36, carpentersbench, 37, carpentersbench2, 206, wallcandle, 49, woodwall, 50, wooddoor, 178, torch, 281, oakdinnertable, 282, oakdinnertable2, 285, oakchair, 447, bluecarpet],\n\tobjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 49, 50, 49, 49, 49, 0, 49, 448, 0, 206, 49, 49, 49, 178, 0, 447, 34, 49, 49, 282, 0, 447, 36, 49, 49, 281, 285, 447, 37, 49, 49, 206, 0, 447, 34, 49, 49, 49, 291, 290, 49, 49, 0, 49, 49, 49, 49, 0],\n\trotations = [0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 3, 0, 3, 1, 0, 1, 1, 0, 3, 3, 2, 1, 0, 0, 3, 2, 2, 0, 2, 2, 2, 2, 2, 0, 3, 2, 2, 2, 2, 2, 0, 2, 3, 2, 2, 1, 3, 3, 3, 3, 0, 1, 1, 1, 1, 1],\n\twire = [85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85]\n}");
        WallSet.wood.replaceWith(wallSet, this);
        FenceSet.wood.replaceWith(fenceSet, this);
        FurnitureSet.oak.replaceWith(furnitureSet, this);
    }
}

