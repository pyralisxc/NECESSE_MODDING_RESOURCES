/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets;

import necesse.engine.registries.BiomeRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.set.ColumnSet;
import necesse.level.maps.presets.set.FloorSet;
import necesse.level.maps.presets.set.GroundSet;
import necesse.level.maps.presets.set.RockAndOreSet;
import necesse.level.maps.presets.set.WallSet;

public class SmallMineEntrancePreset
extends Preset {
    public SmallMineEntrancePreset(Biome biome, LevelIdentifier levelIdentifier, GameRandom random, RockAndOreSet rockwalls, WallSet wall, FloorSet cavefloor, ColumnSet columns, GroundSet gravel) {
        super("PRESET = {\n\twidth = 17,\n\theight = 10,\n\ttileIDs = [22, stonefloor, 46, graveltile],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 22, -1, -1, -1, -1, 46, 46, -1, 46, 46, 46, 46, 46, 46, -1, -1, 22, 22, 22, 22, -1, -1, -1, 46, 46, 46, 46, 46, 46, 46, 46, 22, 46, 22, 22, 22, 22, -1, -1, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, 46, 22, 22, 22, 22, 22, -1, 46, 46, 46, 46, -1, 46, 46, 46, 46, -1, -1, -1, 22, 22, 22, -1, -1, 46, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 22, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 1026, rock, 1029, surfacerocksmall, 263, woodcolumn, 264, stonecolumn, 1033, ironorerock, 308, oillantern, 85, woodwall, 405, minecarttrack, 469, sprucechest, 1334, crate, 121, stonewall, 1211, ladderdown],\n\tobjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1026, 1026, 1026, 1026, 1026, 1026, 0, 0, 0, 0, 0, 0, 85, 85, 85, 121, 121, 1026, 1026, 264, 1026, 1026, 1026, 1026, 0, 0, 0, 0, 0, 263, 0, 1334, 264, 121, 121, 1026, 1029, 308, 1029, 1033, 1026, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1211, 1033, 1026, 0, 0, 0, 0, 405, 405, 405, 0, 405, 405, 405, 405, 405, 405, 0, 1026, 1026, 0, 0, 0, 0, 0, 263, 1334, 1334, 264, 121, 121, 1026, 469, 0, 264, 1026, 1026, 0, 0, 0, 0, 0, 85, 85, 85, 85, 121, 1026, 1026, 1026, 1026, 1026, 1026, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1026, 1026, 1026, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 3, 3, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 3, 2, 3, 1, 1, 1, 1, 3, 1, 3, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 1, 1, 0, 0, 0, 0, 2, 3, 3, 0, 1, 1, 1, 1, 1, 0, 3, 3, 1, 0, 0, 0, 0, 0, 3, 2, 2, 1, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 3, 3, 3, 1, 1, 1, 1, 3, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true,\n\tclearOtherWires = false\n}");
        RockAndOreSet.forest.replaceWith(rockwalls, this);
        WallSet.stone.replaceWith(wall, this);
        FloorSet.stone.replaceWith(cavefloor, this);
        ColumnSet.stone.replaceWith(columns, this);
        GroundSet.forest.replaceWith(gravel, this);
        LootItem pickaxe = new LootItem("ironpickaxe");
        if (biome == BiomeRegistry.FOREST) {
            pickaxe = new LootItem("goldpickaxe");
        } else if (biome == BiomeRegistry.SNOW) {
            pickaxe = new LootItem("frostpickaxe");
        } else if (biome == BiomeRegistry.PLAINS) {
            pickaxe = new LootItem("demonicpickaxe");
        } else if (biome == BiomeRegistry.SWAMP) {
            pickaxe = new LootItem("demonicpickaxe");
        } else if (biome == BiomeRegistry.DESERT) {
            pickaxe = new LootItem("demonicpickaxe");
        }
        this.addInventory(new LootTable(pickaxe, LootItem.between("torch", 15, 45), LootItem.between("ironbomb", 3, 8)), random, 12, 6, new Object[0]);
        PresetUtils.addShoreTiles(this, -1, -1, this.width + 2, this.height + 2);
    }
}

