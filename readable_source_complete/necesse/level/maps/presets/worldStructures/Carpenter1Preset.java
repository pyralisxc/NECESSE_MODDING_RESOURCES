/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.worldStructures;

import necesse.engine.util.GameRandom;
import necesse.level.maps.presets.set.FenceSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.WallSet;
import necesse.level.maps.presets.worldStructures.LandStructurePreset;

public class Carpenter1Preset
extends LandStructurePreset {
    public Carpenter1Preset(GameRandom random, WallSet wallSet, FenceSet fenceSet, FurnitureSet furnitureSet) {
        super(8, 12);
        this.applyScript("PRESET = {\n\twidth = 8,\n\theight = 12,\n\ttileIDs = [12, woodfloor, 15, woodpathtile],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, 12, -1, 12, -1, 12, 12, -1, -1, 12, 12, 12, 12, -1, 12, -1, -1, 12, 12, 15, 15, 12, 12, -1, -1, 12, 12, 15, 15, 12, 12, -1, -1, 12, 12, 12, 12, 12, 12, -1, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 15, 15, 15, 15, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, -1, 12, 12, 12, 12, 12, 12, 12, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 448, goldgridcarpet, 288, oakbookshelf, 290, oakbed, 291, oakbed2, 36, carpentersbench, 452, redyarncarpet, 37, carpentersbench2, 294, oakcandelabra, 206, wallcandle, 49, woodwall, 50, wooddoor, 281, oakdinnertable, 282, oakdinnertable2, 285, oakchair, 447, bluecarpet],\n\tobjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 49, 49, 49, 49, 49, 49, 0, 0, 49, 447, 447, 448, 448, 49, 0, 0, 49, 447, 294, 294, 448, 49, 0, 0, 49, 448, 36, 37, 447, 49, 0, 49, 49, 448, 448, 447, 447, 49, 49, 49, 206, 0, 0, 0, 0, 288, 49, 49, 282, 452, 452, 452, 452, 291, 49, 49, 281, 285, 0, 0, 206, 290, 49, 49, 49, 49, 50, 50, 49, 49, 49, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 0, 0, 0, 2, 1, 1, 3, 3, 0, 0, 0, 2, 1, 0, 2, 3, 2, 0, 0, 2, 2, 1, 1, 2, 2, 0, 2, 2, 2, 2, 2, 2, 2, 0, 3, 3, 0, 0, 0, 3, 2, 0, 2, 0, 3, 3, 3, 3, 0, 0, 2, 0, 3, 0, 0, 0, 0, 0, 3, 3, 1, 2, 2, 1, 1, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\twire = [85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85]\n}");
        WallSet.wood.replaceWith(wallSet, this);
        FenceSet.wood.replaceWith(fenceSet, this);
        FurnitureSet.oak.replaceWith(furnitureSet, this);
    }
}

