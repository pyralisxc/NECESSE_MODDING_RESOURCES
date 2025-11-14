/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets;

import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.set.RockAndOreSet;
import necesse.level.maps.presets.set.WallSet;

public class CaveBoomPrankPreset
extends Preset {
    public CaveBoomPrankPreset(Biome biome, LevelIdentifier levelIdentifier, GameRandom random, RockAndOreSet rockwalls, WallSet wallpillars) {
        super("PRESET = {\n\twidth = 13,\n\theight = 7,\n\ttileIDs = [1, dirttile],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, -1, -1, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 1026, rock, 1029, surfacerocksmall, 309, lantern, 839, stonepressureplate, 856, stonearrowtrap, 121, stonewall, 1033, ironorerock, 1034, copperorerock, 1035, goldorerock, 894, tnt],\n\tobjects = [1026, 1026, 1026, 1026, 1026, 1026, 1026, 1026, 1026, 1026, 1026, 0, -1, 1026, 1026, 1026, 1026, 1026, 1026, 1035, 1035, 1026, 1026, 121, 1029, 0, 1026, 1026, 1026, 1026, 1026, 1026, 1035, 1034, 1034, 1029, 309, 0, 0, 1026, 894, 1026, 1026, 1026, 1026, 1026, 1034, 1033, 839, 0, 0, 0, 1026, 1026, 1026, 1026, 1026, 1026, 1035, 1034, 1033, 856, 1029, 0, -1, 1026, 1026, 1026, 1026, 1026, 1026, 1033, 1033, 1033, 1026, 0, 0, -1, 1026, 1026, 1026, 1026, 1026, 1026, 1026, 1026, 1026, 1026, 0, -1, -1],\n\trotations = [2, 2, 0, 0, 2, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 1, 1, 0, 1, 0, 0, 0, 2, 2, 0, 0, 1, 1, 0, 3, 0, 0, 0, 2, 3, 0, 2, 3, 0, 3, 3, 3, 0, 0, 0, 0, 1, 0, 2, 2, 0, 3, 3, 3, 1, 2, 0, 0, 0, 0, 0, 2, 0, 0, 3, 3, 3, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true,\n\tclearOtherWires = false,\n\twire = [85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 87, 87, 87, 87, 87, 87, 87, 87, 87, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85]\n}");
        RockAndOreSet.forest.replaceWith(rockwalls, this);
        WallSet.stone.replaceWith(wallpillars, this);
        this.iteratePreset((tileX, tileY) -> {
            int objectID = this.getObject((int)tileX, (int)tileY);
            if (objectID != -1 && ObjectRegistry.getObject(objectID).getStringID().contains("ore") && random.getEveryXthChance(3)) {
                this.setObject((int)tileX, (int)tileY, rockwalls.rock);
            }
        });
        PresetUtils.addShoreTiles(this, -1, -1, this.width + 2, this.height + 2);
    }
}

