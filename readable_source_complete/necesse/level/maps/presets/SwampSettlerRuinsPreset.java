/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets;

import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.set.CarpetSet;
import necesse.level.maps.presets.set.ColumnSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.RockAndOreSet;
import necesse.level.maps.presets.set.WallSet;

public class SwampSettlerRuinsPreset
extends Preset {
    public SwampSettlerRuinsPreset(GameRandom random, LevelIdentifier levelIdentifier, CarpetSet carpet, FurnitureSet furniture, WallSet wall, RockAndOreSet rock, FurnitureSet chest, ColumnSet column) {
        super("PRESET = {\n\twidth = 11,\n\theight = 7,\n\ttileIDs = [32, swampstonebrickfloor, 1, dirttile, 56, deepswampstonefloor, 46, graveltile],\n\ttiles = [-1, -1, 46, 46, 46, 46, 46, -1, -1, -1, -1, 46, 46, 46, 46, 56, 56, 46, 32, 56, -1, -1, 46, 56, 32, 32, 32, 32, 32, 56, -1, -1, -1, 56, 56, 32, 56, 32, 56, 56, -1, 46, 46, -1, 56, 32, 32, 32, 32, 32, 56, -1, 46, 46, 46, -1, -1, 56, -1, 46, 1, 1, -1, 46, 46, 46, -1, -1, -1, 46, 1, 46, 46, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 772, dirtyplate, 133, swampstonewall, 1061, swampsurfacerocksmall, 135, swampstonedooropen, 520, willowchair, 521, willowbench, 522, willowbench2, 268, swampstonecolumn, 1011, cookingpot, 54, workstationduo, 758, mosscoveredskull, 55, workstationduo2, 283, sack, 798, brokenherbglassdisplay, 734, decorativepot1, 831, greencarpet, 607, dungeonchest],\n\tobjects = [-1, 0, 0, 133, 0, 0, 133, -1, -1, -1, -1, 0, 0, 133, 0, 831, 0, 283, 268, 0, 133, -1, 0, 55, 0, 0, 0, 831, 831, 607, 133, 133, -1, 133, 54, 0, 0, 1011, 0, 758, 0, 521, 522, 0, 133, 798, 0, 0, 772, 0, 520, 133, 0, 0, 734, -1, -1, 268, 0, 0, 0, 133, 0, 0, 1061, 0, -1, -1, -1, 133, 135, 0, 0, 0, 0, -1, -1],\n\trotations = [0, 0, 0, 3, 0, 0, 3, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 3, 0, 0, 0, 0, 3, 0, 0, 0, 3, 3, 3, 0, 3, 0, 0, 0, 3, 0, 0, 0, 1, 1, 0, 3, 3, 0, 0, 3, 0, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 3, 3, 0, 0, 3, 3, 0, 0],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true,\n\tclearOtherWires = false\n}");
        LootTable chestLoot;
        String weaponDrop;
        CarpetSet.green.replaceWith(carpet, this);
        FurnitureSet.willow.replaceWith(furniture, this);
        WallSet.swampStone.replaceWith(wall, this);
        RockAndOreSet.swamp.replaceWith(rock, this);
        FurnitureSet.dungeon.replaceWith(chest, this);
        ColumnSet.swampstone.replaceWith(column, this);
        LootTable sackLoot = new LootTable();
        if (levelIdentifier.equals(LevelIdentifier.CAVE_IDENTIFIER)) {
            this.addMobs(3, 3, false, "swampzombie");
            weaponDrop = random.getOneOf("ivyspear", "ivysword", "ivygreatsword", "ivybow", "ivypickaxe", "ivyaxe");
            chestLoot = LootTablePresets.swampCaveChest;
        } else if (levelIdentifier.equals(LevelIdentifier.DEEP_CAVE_IDENTIFIER)) {
            this.addMobs(3, 3, false, "swampskeleton");
            weaponDrop = random.getOneOf("myceliumpickaxe", "myceliumaxe", "myceliumgreatbow");
            chestLoot = LootTablePresets.deepSwampCaveChest;
            sackLoot.items.add(new ChanceLootItem(0.4f, "decayingleaf"));
        } else {
            weaponDrop = random.getOneOf("woodstaff", "venomstaff", "sapphirestaff");
            chestLoot = LootTablePresets.basicCaveChest;
        }
        sackLoot.items.add(new LootItem(weaponDrop));
        sackLoot.items.add(LootItem.between("coin", 10, 125));
        this.addInventory(new LootTable(LootItem.between("willowlog", 2, 5)), random, 4, 3, new Object[0]);
        this.addInventory(sackLoot, random, 6, 1, new Object[0]);
        this.addInventory(chestLoot, random, 7, 2, new Object[0]);
        PresetUtils.addShoreTiles(this, -1, -1, this.width + 2, this.height + 2);
    }
}

