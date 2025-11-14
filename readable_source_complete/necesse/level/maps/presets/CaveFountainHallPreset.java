/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets;

import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.set.ColumnSet;
import necesse.level.maps.presets.set.FloorSet;
import necesse.level.maps.presets.set.WallSet;

public class CaveFountainHallPreset
extends Preset {
    public CaveFountainHallPreset(Biome biome, LevelIdentifier levelIdentifier, GameRandom random, FloorSet floors, WallSet walls, FloorSet centerFloor, ColumnSet columns) {
        super("PRESET = {\n\twidth = 22,\n\theight = 20,\n\ttileIDs = [50, deepstonebrickfloor, 51, deepstonetiledfloor],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 50, 50, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 50, 50, 50, 50, 50, 50, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, -1, -1, -1, -1, -1, -1, -1, -1, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, -1, -1, -1, -1, -1, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, -1, -1, -1, -1, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, -1, -1, -1, -1, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, -1, -1, -1, 50, 50, 50, 50, 50, 50, 50, 50, 51, 51, 51, 51, 50, 50, 50, 50, 50, 50, 50, 50, -1, -1, 50, 50, 50, 50, 50, 50, 50, 51, 51, 51, 51, 51, 51, 50, 50, 50, 50, 50, 50, 50, -1, 50, 50, 50, 50, 50, 50, 50, 51, 51, 51, 51, 51, 51, 51, 51, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 51, 51, 51, 51, 51, 51, 51, 51, 50, 50, 50, 50, 50, 50, 50, -1, 50, 50, 50, 50, 50, 50, 50, 51, 51, 51, 51, 51, 51, 50, 50, 50, 50, 50, 50, 50, -1, -1, 50, 50, 50, 50, 50, 50, 50, 50, 51, 51, 51, 51, 50, 50, 50, 50, 50, 50, 50, 50, -1, -1, -1, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, -1, -1, -1, -1, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, -1, -1, -1, -1, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, -1, -1, -1, -1, -1, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, -1, -1, -1, -1, -1, -1, -1, -1, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 50, 50, 50, 50, 50, 50, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 50, 50, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 736, deadwoodcandles, 1379, crate, 741, spidercastlecandelabra, 1384, vase, 169, deepstonewall, 434, bloodfountain, 435, bloodfountain2, 436, bloodfountain3, 437, bloodfountain4, 438, bloodfountain5, 439, bloodfountain6, 440, bloodfountain7, 281, deepstonecolumn, 441, bloodfountain8],\n\tobjects = [-1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 169, 169, 0, 0, 0, 0, 169, 169, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 169, 169, 169, 169, 169, 0, 1384, 0, 0, 0, 0, 169, 169, 169, 169, 169, -1, -1, -1, -1, -1, 169, 169, 741, 0, 281, 1379, 1379, 0, 0, 0, 0, 0, 0, 281, 0, 741, 169, 169, -1, -1, -1, -1, 169, 0, 1384, 1379, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1379, 1384, 0, 169, -1, -1, -1, -1, 169, 281, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 281, 169, -1, -1, -1, 169, 169, 1379, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1384, 169, 169, -1, -1, 169, 0, 0, 0, 0, 0, 0, 1379, 0, 0, 1379, 1384, 1384, 0, 0, 0, 0, 0, 0, 169, -1, 0, 0, 1379, 0, 1384, 0, 0, 0, 1384, 0, 0, 0, 736, 0, 0, 0, 0, 0, 0, 1379, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 736, 434, 435, 436, 437, 1384, 1379, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1384, 0, 438, 439, 440, 441, 1379, 0, 0, 1379, 0, 0, 0, 0, 0, 0, 0, 1384, 0, 0, 0, 0, 0, 0, 1384, 0, 0, 736, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 169, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 169, -1, -1, 169, 169, 1384, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1384, 169, 169, -1, -1, -1, 169, 281, 1379, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 281, 169, -1, -1, -1, -1, 169, 0, 1379, 1384, 0, 0, 1384, 0, 0, 0, 0, 0, 1379, 0, 1379, 1384, 0, 169, -1, -1, -1, -1, 169, 169, 741, 0, 281, 0, 0, 0, 0, 0, 1384, 0, 1379, 281, 0, 741, 169, 169, -1, -1, -1, -1, -1, 169, 169, 169, 169, 169, 0, 0, 0, 0, 1379, 0, 169, 169, 169, 169, 169, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 169, 169, 0, 0, 0, 0, 169, 169, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\trotations = [2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 1, 0, 2, 2, 0, 1, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 1, 1, 0, 0, 1, 3, 0, 0, 1, 3, 1, 0, 0, 2, 2, 2, 2, 2, 0, 3, 2, 0, 0, 0, 0, 2, 2, 2, 2, 2, 1, 1, 0, 2, 1, 0, 2, 2, 2, 2, 3, 0, 3, 0, 0, 0, 2, 0, 0, 0, 0, 3, 0, 1, 0, 1, 0, 0, 2, 2, 2, 2, 3, 1, 0, 3, 3, 3, 2, 0, 1, 3, 1, 3, 1, 1, 1, 0, 1, 1, 2, 2, 2, 0, 0, 0, 0, 1, 3, 1, 1, 0, 0, 0, 0, 1, 1, 1, 3, 0, 1, 0, 0, 2, 2, 0, 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, 0, 0, 3, 1, 1, 1, 3, 0, 0, 2, 0, 0, 0, 3, 3, 1, 0, 1, 3, 2, 0, 1, 2, 1, 1, 1, 1, 1, 1, 1, 0, 2, 0, 2, 0, 1, 3, 1, 1, 3, 2, 2, 2, 2, 2, 0, 0, 0, 3, 1, 0, 0, 2, 0, 0, 0, 0, 1, 0, 2, 1, 0, 0, 2, 2, 2, 2, 1, 1, 1, 1, 2, 0, 0, 0, 0, 2, 0, 3, 1, 0, 2, 0, 1, 2, 3, 2, 2, 2, 1, 1, 0, 1, 2, 0, 0, 0, 0, 2, 0, 0, 1, 0, 2, 1, 2, 0, 3, 3, 3, 2, 1, 1, 1, 1, 2, 3, 0, 2, 2, 2, 0, 3, 3, 1, 1, 3, 1, 1, 3, 3, 3, 1, 1, 1, 1, 1, 0, 1, 1, 0, 2, 2, 2, 0, 1, 3, 2, 0, 2, 1, 2, 3, 0, 0, 1, 2, 1, 1, 1, 1, 2, 2, 2, 2, 2, 0, 0, 3, 2, 3, 3, 2, 1, 1, 3, 1, 1, 1, 2, 2, 0, 0, 2, 2, 2, 2, 2, 0, 3, 2, 0, 1, 0, 0, 3, 0, 0, 2, 0, 1, 1, 0, 2, 1, 0, 2, 2, 2, 2, 2, 0, 0, 3, 1, 1, 0, 0, 0, 0, 2, 0, 1, 1, 2, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 2, 0, 1, 3, 0, 1, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true,\n\tclearOtherWires = false\n}");
        FloorSet.deepStoneBrick.replaceWith(floors, this);
        WallSet.deepStone.replaceWith(walls, this);
        FloorSet.deepStoneTiled.replaceWith(centerFloor, this);
        ColumnSet.deepstone.replaceWith(columns, this);
        String chosenMobStringID = this.getRandomHostileMobNameForBiomeLevelExcept(biome, levelIdentifier, random, "crawl", "worm");
        this.addMob(chosenMobStringID, 11, 3, false);
        this.addMob(chosenMobStringID, 3, 9, false);
        this.addMob(chosenMobStringID, 17, 10, false);
        this.addMob(chosenMobStringID, 10, 13, false);
        if (biome.equals(BiomeRegistry.FOREST) || biome.equals(BiomeRegistry.PLAINS)) {
            this.replaceFountain("fountain");
        } else if (biome.equals(BiomeRegistry.SWAMP)) {
            this.replaceFountain("overgrownfountain");
        }
        PresetUtils.addShoreTiles(this, -1, -1, this.width + 2, this.height + 2);
    }

    protected void replaceFountain(String newFountainObjectStringID) {
        this.replaceNonEmptyObjects(ObjectRegistry.getObjectID("bloodfountain"), ObjectRegistry.getObjectID(newFountainObjectStringID));
        this.replaceNonEmptyObjects(ObjectRegistry.getObjectID("bloodfountain2"), ObjectRegistry.getObjectID(newFountainObjectStringID + "2"));
        this.replaceNonEmptyObjects(ObjectRegistry.getObjectID("bloodfountain3"), ObjectRegistry.getObjectID(newFountainObjectStringID + "3"));
        this.replaceNonEmptyObjects(ObjectRegistry.getObjectID("bloodfountain4"), ObjectRegistry.getObjectID(newFountainObjectStringID + "4"));
        this.replaceNonEmptyObjects(ObjectRegistry.getObjectID("bloodfountain5"), ObjectRegistry.getObjectID(newFountainObjectStringID + "5"));
        this.replaceNonEmptyObjects(ObjectRegistry.getObjectID("bloodfountain6"), ObjectRegistry.getObjectID(newFountainObjectStringID + "6"));
        this.replaceNonEmptyObjects(ObjectRegistry.getObjectID("bloodfountain7"), ObjectRegistry.getObjectID(newFountainObjectStringID + "7"));
        this.replaceNonEmptyObjects(ObjectRegistry.getObjectID("bloodfountain8"), ObjectRegistry.getObjectID(newFountainObjectStringID + "8"));
    }
}

