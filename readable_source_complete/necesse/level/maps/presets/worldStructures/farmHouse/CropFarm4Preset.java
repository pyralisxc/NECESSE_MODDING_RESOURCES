/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.worldStructures.farmHouse;

import necesse.engine.util.GameRandom;
import necesse.level.maps.presets.set.CropSet;
import necesse.level.maps.presets.worldStructures.LandStructurePreset;

public class CropFarm4Preset
extends LandStructurePreset {
    public CropFarm4Preset(GameRandom random, CropSet cropSet) {
        super(7, 6);
        this.applyScript("PRESET = {\n\twidth = 7,\n\theight = 6,\n\ttileIDs = [9, farmland, 13, woodpathtile],\n\ttiles = [13, 9, 13, 13, 13, 13, 13, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, -1, -1, 9, 9, 9, 9, 9, -1, -1, 9, 9, 9, 9, 9],\n\tobjectIDs = [144, woodfencegate, 0, air, 404, wheatseed, 43, woodwall, 143, woodfence],\n\tobjects = [43, 43, 144, 43, 43, 143, 43, 143, 404, 0, 0, 0, 0, 143, 143, 0, 0, 404, 404, 0, 143, 143, 143, 143, 0, 404, 404, 143, 0, 0, 143, 404, 0, 0, 143, 0, 0, 143, 143, 144, 143, 143],\n\trotations = [2, 3, 1, 3, 2, 1, 3, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2, 0, 0, 2, 0, 0, 0, 1, 2, 0, 2, 0, 0, 0, 2, 0, 0, 0, 0, 2, 1, 3, 3, 3, 3, 3],\n\twire = [85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85]\n}");
        cropSet.replacePreset(CropSet.wheat, this, random);
    }
}

