/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets;

import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.set.ColumnSet;
import necesse.level.maps.presets.set.GroundSet;
import necesse.level.maps.presets.set.RockAndOreSet;
import necesse.level.maps.presets.set.TreeSet;

public class SmallForgottenShrinePreset
extends Preset {
    public SmallForgottenShrinePreset(Biome biome, LevelIdentifier levelIdentifier, GameRandom random, TreeSet trees, RockAndOreSet stones, ColumnSet column, GroundSet ground) {
        super("PRESET = {\n\twidth = 16,\n\theight = 12,\n\ttileIDs = [4, overgrowngrasstile, 22, stonefloor, 46, graveltile],\n\ttiles = [-1, -1, -1, -1, -1, 4, 4, 4, -1, 4, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 4, -1, 4, 4, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 46, -1, 4, 4, 46, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 4, -1, 46, 4, 46, -1, -1, 4, -1, 4, 4, -1, -1, -1, 46, -1, 46, 46, 46, 46, 46, 4, 4, 4, 4, 4, 4, -1, -1, -1, -1, -1, 4, 46, 46, 46, 4, 4, 46, 4, 4, 4, 4, -1, -1, -1, -1, -1, -1, 22, 46, 22, -1, 4, 4, 4, 4, -1, 4, -1, -1, -1, -1, -1, 46, 46, 22, 46, 46, 4, 46, 4, 4, -1, 4, -1, -1, -1, -1, -1, -1, 46, -1, 46, 46, 46, 4, -1, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 46, 46, 46, 46, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 46, -1, 46, 46, -1, -1, 46, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 46, -1, -1, 46, -1, -1],\n\tobjectIDs = [0, air, 2, oaktree, 3, oaktreestump, 5, sprucetree, 1253, cobweb, 325, candle, 1064, surfacerock, 296, sack, 1225, grass, 1065, surfacerockr, 1066, surfacerocksmall, 845, spilledgoldchalice, 47, blueberrybush, 368, mossymonkstatue, 848, rottenpigdish, 339, brazier, 851, brokenplate, 276, stonecolumn],\n\tobjects = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 1225, 0, 1225, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 1225, 0, 0, 0, 0, 1225, -1, -1, -1, -1, -1, -1, 5, 1225, 0, 2, 0, 5, 0, 1225, 2, 1225, 1225, -1, -1, -1, -1, 1225, 0, 1225, 1253, 1253, 0, 1253, 0, 1225, 0, 0, 0, -1, -1, 1225, 0, 47, 0, 2, 1066, 325, 368, 1253, 276, 5, 0, 1225, 1225, -1, -1, 1225, 5, 1225, 47, 1225, 0, 845, 848, 0, 1253, 1064, 1065, 2, 0, 0, -1, -1, 1225, 0, 1253, 1064, 1065, 0, 339, 0, 0, 1066, 0, 47, 1225, 0, -1, -1, 0, 5, 47, 0, 47, 1253, 0, 1253, 0, 1253, 2, 1225, 47, -1, -1, -1, -1, 0, 1225, 2, 296, 1253, 0, 0, 851, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, 1225, 0, 0, 3, 0, 0, 0, 0, 1225, 0, 1225, -1, -1, -1, -1, -1, -1, -1, 0, 0, 1225, 0, 0, 1225, 1225, 0, 0, 1225],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 2, 2, 0, 0, 0, 3, 1, 0, 2, 2, 3, 0, 0, 0, 0, 0, 2, 2, 0, 3, 0, 0, 0, 0, 0, 2, 0, 0, 2, 2, 0, 0, 0, 2, 1, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 1, 0, 0, 3, 3, 1, 0, 0, 2, 0, 0, 0, 0, 0, 2, 2, 1, 1, 0, 0, 2, 0, 2, 0, 0, 2, 0, 0, 0, 0, 2, 2, 1, 1, 1, 1, 0, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 2, 1, 1, 1, 1, 1, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true,\n\tclearOtherWires = false,\n\twire = [85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 87, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 87, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 87, 87, 95, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 93, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85],\n\tlogicGates = {\n\t\tgate = {\n\t\t\ttileX = 8,\n\t\t\ttileY = 8,\n\t\t\tstringID = sensorgate,\n\t\t\tdata = {\n\t\t\t\toutputs = 0100,\n\t\t\t\twireOutputs = 0100,\n\t\t\t\tplayers = true,\n\t\t\t\tpassiveMobs = false,\n\t\t\t\thostileMobs = false,\n\t\t\t\trange = 5\n\t\t\t}\n\t\t},\n\t\tgate = {\n\t\t\ttileX = 8,\n\t\t\ttileY = 7,\n\t\t\tstringID = norgate,\n\t\t\tdata = {\n\t\t\t\toutputs = 0000,\n\t\t\t\twireInputs = 0100,\n\t\t\t\twireOutputs = 1000\n\t\t\t}\n\t\t}\n\t},\n}");
        TreeSet.oak.replaceWith(trees, this);
        TreeSet.spruce.replaceWith(trees, this);
        RockAndOreSet.forest.replaceWith(stones, this);
        ColumnSet.stone.replaceWith(column, this);
        GroundSet.forest.replaceWith(ground, this);
        this.addMobs(10, 3, false, "stabbybush");
        this.addInventory(new LootTable(LootItem.between("coin", 21, 78), random.getOneOf(new LootItem("demonclaw"), new LootItem("demonicsword"), new LootItem("demonicspear"), new LootItem("demonicbow"))), random, 5, 9, new Object[0]);
        PresetUtils.addShoreTiles(this, -1, -1, this.width + 2, this.height + 2);
    }
}

