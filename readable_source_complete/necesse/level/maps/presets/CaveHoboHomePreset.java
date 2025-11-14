/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets;

import java.awt.Point;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.presets.BiomeOresLootTable;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.levelData.settlementData.Waystone;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.set.CrystalSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.RockAndOreSet;
import necesse.level.maps.presets.set.WallSet;

public class CaveHoboHomePreset
extends Preset {
    public CaveHoboHomePreset(Biome biome, LevelIdentifier levelIdentifier, GameRandom random, RockAndOreSet walls, FurnitureSet furniture, WallSet doors) {
        super("PRESET = {\n\twidth = 25,\n\theight = 16,\n\ttileIDs = [34, snowrocktile, 83, rubygravel, 72, spidercobbletile, 58, basaltrocktile],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 58, 58, 58, 58, 58, 58, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 58, 58, 58, 34, 34, 34, 34, 34, 34, 58, 58, 58, 58, 58, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 58, 58, 58, 34, 34, 34, 34, 34, 34, 34, 58, 83, 83, 83, 58, 58, 58, 58, 72, -1, -1, -1, -1, -1, 58, 58, 34, 34, 34, 34, 34, 34, 83, 83, 83, 83, 83, 83, 83, 83, 83, 58, 72, 72, 72, -1, -1, -1, 58, 58, 58, 34, 34, 34, 34, 34, 83, 83, 83, 83, 83, 83, 83, 83, 83, 83, 72, 72, 72, 72, -1, -1, -1, 58, 58, 58, 34, 34, 34, 34, 72, 72, 72, 72, 83, 83, 83, 83, 83, 72, 72, 72, 72, 58, 72, -1, -1, -1, 58, 58, 58, 72, 72, 72, 72, 72, 34, 34, 72, 72, 72, 83, 83, 72, 72, 72, 72, 34, 72, 72, -1, -1, -1, 58, 58, 72, 72, 72, 72, 58, 34, 34, 34, 72, 34, 72, 72, 72, 72, 72, 72, 72, 58, 58, -1, -1, -1, -1, 58, 72, 72, 72, 72, 58, 58, 34, 34, 34, 34, 34, 72, 72, 72, 72, 72, 72, 34, 58, 58, -1, -1, -1, -1, 72, 72, 58, 58, 58, 58, 58, 34, 34, 34, 34, 34, 34, 72, 72, 72, 72, 72, 58, 58, -1, -1, -1, -1, -1, 58, 58, 58, 58, 58, 58, 58, 58, 34, 34, 34, 34, 34, 34, 34, 72, 72, 58, 58, -1, -1, -1, -1, -1, -1, -1, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 34, 34, 34, 34, 34, 58, 58, 58, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 58, 58, 58, 58, 58, 58, 58, 34, 34, 34, 34, 58, 58, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 58, 58, 58, 58, 58, 34, 34, 34, 34, 58, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 58, 58, 58, 58, 58, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 704, deadwoodchair, 705, deadwoodbench, 706, deadwoodbench2, 707, deadwoodbookshelf, 708, deadwoodcabinet, 836, velourcarpet, 711, deadwooddoublebed, 712, deadwooddoublebedfoot1, 713, deadwooddoublebed2, 714, deadwooddoublebedfoot2, 715, deadwooddresser, 716, deadwoodclock, 1101, basaltrock, 1166, rubycluster, 719, deadwooddisplay, 1167, rubyclusterr, 724, spidercastlewallcandle, 1173, rubyclustersmall, 735, decorativepot2, 995, iceblossom, 751, pottedplant4, 1009, caveglow, 182, deepsnowstonedoor, 56, forge, 826, woolcarpet, 315, snowcandlepedestal, 699, deadwoodchest, 318, tikitorch, 703, deadwoodmodulartable],\n\tobjects = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1101, 1101, 1101, 1101, 1101, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1101, 1101, 1101, 1101, 1101, 1101, 1101, 1101, -1, -1, -1, -1, -1, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, 1101, 1101, 1101, 1101, 1101, 1101, 1101, 1101, 1101, 1101, 1101, 1101, 1101, -1, -1, 0, 0, 0, -1, -1, -1, -1, -1, -1, 1101, 1101, 1101, 1101, 1101, 719, 703, 703, 315, 1101, 1101, 1101, 1101, 1101, 1101, 1101, 1101, 182, 1101, -1, -1, -1, -1, -1, 1101, 1101, 1101, 1101, 1166, 1167, 0, 0, 0, 0, 1173, 1166, 1167, 1101, 1101, 1101, 1101, 1101, 0, 1101, -1, -1, -1, -1, 1101, 1101, 1101, 715, 716, 0, 0, 0, 0, 0, 0, 0, 0, 0, 315, 1166, 1167, 707, 724, 0, 1101, -1, -1, -1, 1101, 1101, 1101, 1101, 0, 0, 0, 0, 318, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1101, 1101, -1, -1, -1, 1101, 1101, 1101, 724, 0, 0, 0, 1173, 1101, 1101, 1101, 1173, 724, 1101, 1101, 1101, 724, 0, 0, 719, 1101, 1101, -1, -1, -1, 1101, 1101, 0, 0, 0, 1173, 1101, 1101, 1101, 711, 713, 826, 0, 708, 705, 706, 0, 704, 703, 1101, 1101, -1, -1, 0, 0, 1101, 0, 0, 0, 0, 1101, 1101, 1101, 751, 712, 714, 826, 0, 836, 836, 836, 0, 0, 703, 1101, 1101, -1, 0, 0, 0, 182, 0, 1101, 1101, 1101, 1101, 1101, 1101, 1101, 1166, 1167, 0, 0, 0, 0, 0, 0, 56, 1101, 1101, -1, -1, -1, 0, 0, 1101, 1101, 1101, 1101, 1101, 1101, 1101, 1101, 1101, 1101, 1101, 1101, 699, 0, 0, 0, 318, 1101, 1101, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1101, 1101, 1101, 1101, 1101, 1101, 1101, 1101, 1101, 0, 0, 735, 1101, 1101, 1101, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1101, 1101, 1101, 1101, 1101, 1101, 1101, 1009, 995, 1101, 1101, 1101, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1101, 1101, 1101, 1101, 1101, 1101, 1101, 1101, 1101, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1101, 1101, 1101, 1101, -1, -1, -1, -1, -1, -1],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 3, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 3, 0, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 0, 3, 0, 0, 0, 0, 0, 3, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 3, 3, 3, 0, 0, 0, 0, 3, 3, 3, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 2, 2, 0, 3, 0, 0, 0, 3, 3, 3, 3, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 3, 3, 3, 0, 0, 0, 3, 3, 3, 2, 1, 0, 0, 0, 3, 3, 3, 0, 3, 3, 3, 3, 1, 0, 0, 2, 3, 3, 0, 0, 0, 3, 3, 0, 0, 0, 0, 3, 3, 3, 2, 2, 2, 3, 2, 1, 1, 0, 1, 0, 3, 3, 0, 0, 0, 0, 3, 0, 0, 0, 0, 3, 3, 3, 3, 2, 2, 2, 3, 2, 2, 2, 0, 0, 3, 3, 3, 0, 0, 0, 0, 3, 2, 3, 3, 3, 3, 3, 3, 3, 0, 0, 2, 0, 3, 2, 2, 2, 3, 3, 3, 0, 0, 0, 0, 0, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1, 0, 0, 0, 0, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1, 0, 0, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 3, 3, 3, 2, 2, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 3, 3, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 3, 3, 0, 0, 0, 0, 0, 0],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true,\n\tclearOtherWires = false\n}");
        RockAndOreSet.deepplainscave.replaceWith(walls, this);
        FurnitureSet.deadwood.replaceWith(furniture, this);
        WallSet.deepSnowStone.replaceWith(doors, this);
        this.replaceNonEmptyObjects(ObjectRegistry.getObjectID("ironfence"), 0);
        String crystal = "sapphire";
        if (levelIdentifier.equals(LevelIdentifier.DEEP_CAVE_IDENTIFIER)) {
            crystal = "ruby";
            this.replaceNonEmptyTiles(TileRegistry.getTileID("deeprocktile"), TileRegistry.getTileID("puddlecobble"));
            this.addInventory(new LootTable(new LootItem("glacialpickaxe", 1), BiomeOresLootTable.deepSnowOres), random, 15, 11, new Object[0]);
        } else {
            CrystalSet.ruby.replaceWith(CrystalSet.sapphire, this);
            this.replaceNonEmptyTiles(TileRegistry.getTileID("snowrocktile"), TileRegistry.getTileID("puddlecobble"));
            this.replaceNonEmptyTiles(TileRegistry.getTileID("deeprocktile"), TileRegistry.getTileID("snowrocktile"));
            this.addInventory(new LootTable(new LootItem("frostpickaxe", 1), BiomeOresLootTable.snowOres), random, 15, 11, new Object[0]);
        }
        this.addInventory(new LootTable(LootItem.between(crystal, 2, 5)), random, 22, 7, new Object[0]);
        this.addInventory(new LootTable(LootItem.between(crystal, 2, 5)), random, 11, 3, new Object[0]);
        PresetUtils.addShoreTiles(this, -1, -1, this.width + 2, this.height + 2);
        String randomSettlerID = random.getOneOf("blacksmithhuman", "minerhuman");
        this.addCustomApply(14, 9, 0, (level, levelX, levelY, dir, blackboard) -> {
            HumanMob settler = (HumanMob)MobRegistry.getMob(randomSettlerID, level);
            settler.setHome(levelX, levelY);
            settler.canDespawn = false;
            Point spawnLocation = Waystone.findTeleportLocation(level, levelX, levelY, settler);
            level.entityManager.addMob(settler, spawnLocation.x, spawnLocation.y);
            return (level1, presetX, presetY) -> settler.remove();
        });
    }
}

