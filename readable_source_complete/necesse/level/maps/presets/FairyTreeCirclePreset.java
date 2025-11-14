/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets;

import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.objectEntity.FruitGrowerObjectEntity;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.set.RockAndOreSet;

public class FairyTreeCirclePreset
extends Preset {
    public FairyTreeCirclePreset(GameRandom random, RockAndOreSet stone, Biome biome) {
        super("PRESET = {\n\twidth = 13,\n\theight = 11,\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 1027, surfacerock, 1028, surfacerockr, 1029, surfacerocksmall, 39, appletree, 1193, redflowerpatch, 1194, purpleflowerpatch, 1195, blueflowerpatch, 1180, grass, 1196, whiteflowerpatch],\n\tobjects = [-1, -1, -1, -1, 0, 1180, 0, 0, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 1029, 1027, 1028, 0, 0, -1, -1, -1, -1, 0, 1180, 1180, 1029, 1196, 1193, 1194, 1029, 0, 0, -1, -1, -1, 0, 1027, 1028, 1195, 1029, 1029, 1029, 1193, 1029, 1180, 0, -1, 0, 1180, 1029, 1196, 1027, 1028, 1180, 1027, 1028, 1196, 1027, 1028, 0, 0, 1180, 1029, 1194, 1029, 1180, 39, 1180, 1029, 1193, 1029, 0, 0, 0, 0, 1029, 1196, 1027, 1028, 1180, 1180, 1029, 1196, 1029, 0, 0, -1, 0, 1027, 1028, 1193, 1029, 1029, 1029, 1195, 1027, 1028, 0, 0, -1, 0, 0, 1029, 1180, 1196, 1195, 1196, 1029, 0, 0, 0, -1, -1, -1, 0, 1027, 1028, 1029, 1029, 1029, 0, 0, 1180, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 0, -1, -1, -1, -1],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true,\n\tclearOtherWires = false\n}");
        RockAndOreSet.forest.replaceWith(stone, this);
        if (biome.equals(BiomeRegistry.SNOW)) {
            this.replaceObject(ObjectRegistry.getObjectID("redflowerpatch"), 0);
            this.replaceObject(ObjectRegistry.getObjectID("purpleflowerpatch"), ObjectRegistry.getObjectID("blueflowerpatch"));
            this.replaceObject(ObjectRegistry.getObjectID("whiteflowerpatch"), 0);
            this.replaceObject(ObjectRegistry.getObjectID("appletree"), ObjectRegistry.getObjectID("sprucetree"));
        }
        if (biome.equals(BiomeRegistry.SWAMP)) {
            this.replaceObject(ObjectRegistry.getObjectID("blueflowerpatch"), 0);
        }
        this.addCustomApply(6, 5, 0, (level, levelX, levelY, dir, blackboard) -> {
            FruitGrowerObjectEntity fruitTree = (FruitGrowerObjectEntity)level.entityManager.getObjectEntity(levelX, levelY);
            if (fruitTree != null) {
                fruitTree.setRandomStage(random);
            }
            return null;
        });
        PresetUtils.addShoreTiles(this, -1, -1, this.width + 2, this.height + 2);
    }
}

