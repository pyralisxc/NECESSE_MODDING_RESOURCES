/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets;

import necesse.engine.registries.BiomeRegistry;
import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.PathSet;
import necesse.level.maps.presets.set.RockAndOreSet;
import necesse.level.maps.presets.set.WallSet;

public class CircularTrapRoomDeepPreset
extends Preset {
    public CircularTrapRoomDeepPreset(GameRandom random, Biome biome, WallSet walls, RockAndOreSet rocktiles, PathSet floortiles, FurnitureSet chest) {
        super("PRESET = {\n\twidth = 15,\n\theight = 15,\n\ttileIDs = [84, ancientruinfloor, 58, basaltrocktile, 78, darkfullmoonpath],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 84, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 58, 58, 84, 58, 58, -1, -1, -1, -1, -1, -1, -1, -1, -1, 58, 78, 78, 84, 78, 78, 58, -1, -1, -1, -1, -1, -1, -1, 58, 78, 78, 78, 84, 78, 78, 78, 58, -1, -1, -1, -1, -1, -1, 58, 78, 78, 58, 84, 58, 78, 78, 58, -1, -1, -1, -1, -1, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, 84, -1, -1, -1, -1, -1, 58, 78, 78, 58, 84, 58, 78, 78, 58, -1, -1, -1, -1, -1, -1, 58, 78, 78, 78, 84, 78, 78, 78, 58, -1, -1, -1, -1, -1, -1, -1, 58, 78, 78, 84, 78, 78, 58, -1, -1, -1, -1, -1, -1, -1, -1, -1, 58, 58, 84, 58, 58, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 84, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 176, obsidiandoor, 898, glyphtrapbounce, 844, dungeonpressureplate, 878, obsidianflametrap, 734, decorativepot1, 175, obsidianwall, 879, obsidianarrowtrap, 735, decorativepot2, 607, dungeonchest],\n\tobjects = [-1, -1, -1, -1, -1, -1, -1, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 175, 175, 175, 176, 878, 175, 175, -1, -1, -1, -1, -1, -1, -1, 175, 879, 0, 844, 0, 844, 0, 175, 175, -1, -1, -1, -1, -1, 175, 175, 844, 0, 0, 0, 0, 0, 844, 878, 175, -1, -1, -1, -1, 175, 0, 0, 0, 0, 898, 734, 0, 0, 0, 879, -1, -1, -1, -1, 878, 844, 735, 0, 844, 0, 844, 0, 0, 844, 175, -1, -1, 0, 0, 176, 0, 0, 898, 0, 607, 0, 898, 0, 0, 176, 0, 0, -1, -1, 879, 844, 0, 0, 844, 0, 844, 734, 0, 844, 878, -1, -1, -1, -1, 175, 0, 0, 0, 0, 898, 0, 0, 735, 0, 175, -1, -1, -1, -1, 175, 175, 844, 0, 734, 0, 0, 0, 844, 175, 175, -1, -1, -1, -1, -1, 175, 878, 0, 844, 0, 844, 0, 879, 175, -1, -1, -1, -1, -1, -1, -1, 175, 175, 175, 176, 175, 175, 175, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1, -1, -1, -1, -1, -1, -1],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 2, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, -1, 1, 3, 3, 3, 3, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 2, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 2, 2, 0, 1, 3, 0, 0, 0, 0, 1, 2, 2, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 3, 1, 0, 1, 1, 2, 0, 3, 0, 1, 0, 0, 0, 0, 0, 1, 2, 0, 0, 0, 0, 0, 2, 2, 0, 3, 0, 0, 0, 0, 3, 0, 0, 3, 0, 1, 0, 0, 2, 1, 2, 0, 0, 0, 0, 3, 3, 0, 0, 2, 0, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 3, 0, 0, 1, 1, 1, 0, 3, 2, 0, 0, 0, 0, 0, 0, 0, 3, 3, 3, 0, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true,\n\tclearOtherWires = false,\n\twire = [85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 87, 87, 87, 85, 87, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 87, 85, 87, 85, 87, 85, 87, 87, 85, 85, 85, 85, 85, 85, 85, 85, 85, 87, 85, 87, 85, 85, 87, 87, 85, 85, 85, 85, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 87, 85, 85, 85, 85, 85, 85, 85, 85, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 85, 85, 85, 85, 85, 85, 85, 85, 87, 85, 87, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 87, 85, 87, 85, 87, 85, 87, 85, 85, 85, 85, 85, 85, 85, 85, 87, 87, 87, 85, 87, 87, 87, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85]\n}");
        WallSet.obsidian.replaceWith(walls, this);
        RockAndOreSet.deepplainscave.replaceWith(rocktiles, this);
        PathSet.darkFullMoon.replaceWith(floortiles, this);
        FurnitureSet.dungeon.replaceWith(chest, this);
        LootTable loot = new LootTable();
        if (biome == BiomeRegistry.FOREST) {
            loot = LootTablePresets.basicDeepCaveRuinsChest;
        } else if (biome == BiomeRegistry.SNOW) {
            loot = LootTablePresets.snowDeepCaveRuinsChest;
        } else if (biome == BiomeRegistry.PLAINS) {
            loot = LootTablePresets.plainsDeepCaveRuinsChest;
        } else if (biome == BiomeRegistry.SWAMP) {
            loot = LootTablePresets.swampDeepCaveRuinsChest;
        } else if (biome == BiomeRegistry.DESERT) {
            loot = LootTablePresets.desertDeepCaveRuinsChest;
        }
        this.addInventory(loot, random, 7, 7, new Object[0]);
        PresetUtils.addShoreTiles(this, -1, -1, this.width + 2, this.height + 2);
    }
}

