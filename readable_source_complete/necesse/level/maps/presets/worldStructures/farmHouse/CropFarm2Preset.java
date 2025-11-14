/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.worldStructures.farmHouse;

import necesse.engine.util.GameRandom;
import necesse.level.maps.presets.set.CropSet;
import necesse.level.maps.presets.worldStructures.LandStructurePreset;

public class CropFarm2Preset
extends LandStructurePreset {
    public CropFarm2Preset(GameRandom random, CropSet cropSet) {
        super(7, 6);
        this.applyScript("PRESET = {\n\twidth = 7,\n\theight = 6,\n\ttileIDs = [9, farmland, 13, woodpathtile],\n\ttiles = [13, 13, 13, 13, 13, 13, 13, -1, 9, 9, 9, -1, -1, -1, -1, 9, 9, 9, -1, -1, -1, -1, 9, 9, 9, -1, -1, -1, -1, 9, 9, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [144, woodfencegate, 0, air, 146, stonefence, 147, stonefencegate, 404, wheatseed, 43, woodwall, 143, woodfence],\n\tobjects = [43, 43, 144, 43, 43, 143, 43, 146, 404, 404, 404, 146, 0, 0, 146, 0, 404, 404, 146, 0, 0, 146, 404, 404, 404, 146, 0, 0, 146, 404, 404, 404, 146, 0, 0, 146, 146, 147, 146, 146, 0, 0],\n\trotations = [2, 3, 2, 3, 1, 1, 2, 0, 3, 1, 1, 0, 0, 0, 2, 0, 1, 1, 0, 1, 0, 2, 1, 3, 2, 0, 1, 0, 2, 3, 3, 3, 0, 1, 3, 2, 1, 3, 1, 1, 1, 3],\n\twire = [85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85]\n}");
        cropSet.replacePreset(CropSet.wheat, this, random);
    }
}

