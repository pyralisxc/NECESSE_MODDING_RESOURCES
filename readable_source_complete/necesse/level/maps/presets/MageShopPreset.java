/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets;

import java.awt.Point;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.humanShop.MageHumanMob;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.presets.PaintingSelectionTable;
import necesse.level.maps.levelData.settlementData.Waystone;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.set.FurnitureSet;

public class MageShopPreset
extends Preset {
    public MageShopPreset(GameRandom random, FurnitureSet furnitureSet) {
        super("PRESET = {\n\twidth = 11,\n\theight = 18,\n\ttileIDs = [33, swampstonepathtile, 13, farmland],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 33, -1, 33, -1, 33, -1, -1, -1, -1, 33, 33, 33, 33, 33, 33, 33, 33, 33, -1, 33, 33, 33, 33, 33, 33, 33, 33, 33, 33, 33, -1, 33, 33, 33, 33, 33, 33, 33, 33, 33, -1, -1, 33, 33, 33, 33, 33, 33, 33, 33, 33, -1, 33, 33, 33, 33, 33, 33, 33, 33, 33, 33, 33, -1, 33, 33, 33, 33, 33, 33, 33, 33, 33, -1, -1, 33, 33, 33, 33, 33, 33, 33, 33, 33, -1, -1, 33, 33, 33, 33, 33, 33, 33, 33, 33, -1, -1, 33, -1, -1, -1, -1, -1, 33, -1, -1, -1, 33, 33, 33, 33, 33, 33, 33, 33, 33, 33, -1, 33, 33, 33, 33, 33, 33, 33, 33, 33, 33, -1, 33, 33, 33, 33, -1, 33, 33, 33, 33, 33, -1, -1, -1, -1, -1, 33, 33, 33, 33, 33, 33, -1, 13, 13, 13, -1, -1, 33, 33, 33, 33, 33, -1, 13, 13, 13, -1, -1, 33, 33, 33, 33, 33, -1, -1, -1, -1, -1, -1, -1, -1, 33, -1, -1, -1],\n\tobjectIDs = [0, air, 448, oakdinnertable2, 450, oakmodulartable, 835, steelgreycarpet, 452, oakbench, 133, swampstonewall, 453, oakbench2, 70, alchemytable, 454, oakbookshelf, 456, oakbed, 457, oakbed2, 329, paintingabstract, 138, swampstonewindow, 270, obsidiancolumn, 462, oakdresser, 782, blueandyellowbooks, 465, oakdisplay, 1170, amethystclustersmall, 723, spidercastlecandelabra, 787, quillandparchment, 86, wooddoor, 984, firemoneseed2, 348, paintinglargeabstract, 988, firemone, 1180, grass, 349, paintinglargeabstract2, 285, walltorch, 543, palmchair, 799, caveglowglassdisplay, 993, iceblossomseed, 802, voidflaskandtesttube, 995, iceblossom, 612, dungeonchair, 999, mushroom2, 489, sprucebathtub, 490, sprucebathtub2, 236, woodfence, 1011, cookingpot, 308, oillantern, 312, candle, 1338, vase, 829, bluecarpet, 446, oakchest, 447, oakdinnertable],\n\tobjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 133, 133, 133, 138, 133, 86, 133, 138, 133, 133, 133, 133, 70, 1338, 0, 270, 0, 270, 0, 465, 0, 133, 138, 0, 0, 0, 829, 829, 829, 0, 0, 0, 138, 133, 1011, 0, 465, 829, 829, 829, 465, 0, 349, 133, 133, 1170, 0, 723, 829, 829, 829, 723, 0, 348, 133, 138, 0, 0, 465, 829, 829, 829, 465, 0, 0, 138, 133, 0, 0, 0, 829, 829, 829, 0, 0, 0, 133, 133, 0, 450, 450, 450, 450, 450, 0, 0, 285, 133, 133, 0, 0, 0, 543, 308, 450, 0, 995, 1338, 133, 133, 86, 133, 133, 133, 133, 133, 133, 133, 133, 133, 988, 0, 452, 453, 133, 454, 454, 454, 454, 446, 133, 0, 0, 0, 0, 86, 0, 0, 835, 835, 1338, 133, 0, 0, 0, 0, 133, 612, 612, 835, 835, 0, 133, 1180, 1180, 1180, 308, 138, 447, 448, 835, 835, 995, 133, 993, 999, 984, 1180, 133, 308, 0, 835, 835, 462, 133, 993, 999, 984, 1180, 133, 489, 490, 0, 457, 456, 133, 236, 236, 236, 236, 133, 133, 133, 138, 133, 133, 133],\n\trotations = [2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 3, 1, 1, 2, 1, 0, 1, 2, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 3, 0, 0, 0, 2, 2, 2, 0, 0, 0, 2, 2, 0, 0, 0, 2, 2, 2, 0, 0, 3, 3, 2, 0, 0, 2, 2, 2, 2, 2, 0, 3, 2, 0, 0, 0, 0, 2, 2, 2, 0, 0, 0, 0, 3, 0, 0, 0, 2, 2, 2, 0, 0, 2, 2, 3, 0, 1, 3, 3, 3, 0, 0, 0, 3, 2, 2, 0, 0, 0, 0, 1, 3, 0, 2, 3, 2, 2, 2, 0, 3, 3, 3, 3, 1, 3, 3, 3, 2, 0, 1, 1, 1, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 0, 2, 0, 0, 0, 2, 0, 1, 1, 2, 2, 1, 2, 0, 0, 0, 0, 0, 2, 0, 2, 2, 3, 2, 0, 0, 0, 0, 0, 2, 2, 0, 3, 3, 2, 2, 3, 3, 3, 3, 3, 3, 2, 1, 1, 2],\n\ttileObjectsClear = true,\n\twallDecorObjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 329, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\twallDecorRotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttableDecorObjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 312, 782, 0, 0, 799, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 802, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 787, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttableDecorRotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\tclearOtherWires = false\n}");
        if (furnitureSet != null) {
            FurnitureSet.oak.replaceWith(furnitureSet, this);
        }
        PresetUtils.applyRandomPainting(this, 9, 5, 3, random, PaintingSelectionTable.largeRarePaintings);
        PresetUtils.applyRandomPainting(this, 9, 11, 2, random, PaintingSelectionTable.uncommonPaintings);
        HashMap<String, Integer> carpets = new HashMap<String, Integer>();
        carpets.put("leathercarpet", 100);
        carpets.put("bluecarpet", 100);
        carpets.put("goldgridcarpet", 100);
        carpets.put("greencarpet", 100);
        carpets.put("steelgreycarpet", 100);
        carpets.put("purplecarpet", 80);
        carpets.put("velourcarpet", 80);
        PresetUtils.applyRandomCarpetToSelection((Preset)this, 4, 3, 3, 5, 0, random, carpets);
        carpets = new HashMap<String, Integer>(carpets);
        carpets.put("brownbearcarpet", 100);
        carpets.put("heartcarpet", 80);
        carpets.put("woolcarpet", 100);
        PresetUtils.applyRandomCarpetToSelection((Preset)this, 7, 12, 2, 4, 0, random, carpets);
        this.addInventory(new LootTable(random.getOneOf(new LootItem("sparegemstones"), new LootItem("magicmanual"), new LootItem("dreamcatcher"))), random, 8, 2, new Object[0]);
        this.addInventory(new LootTable(new LootItem("enchantingscroll")), random, 3, 4, new Object[0]);
        this.addInventory(new LootTable(new LootItem("enchantingscroll")), random, 7, 4, new Object[0]);
        this.addInventory(new LootTable(new LootItem("enchantingscroll")), random, 3, 6, new Object[0]);
        this.addInventory(new LootTable(new LootItem("enchantingscroll")), random, 7, 6, new Object[0]);
        this.addInventory(new LootTable(LootItem.between("coin", 10, 50), ChanceLootItem.between(0.5f, "manapotion", 1, 3), ChanceLootItem.between(0.5f, "manaregenpotion", 1, 3)), random, 9, 11, new Object[0]);
        AtomicReference mageRef = new AtomicReference();
        this.addCustomApply(9, 15, 0, (level, levelX, levelY, dir, blackboard) -> {
            MageHumanMob mage = (MageHumanMob)MobRegistry.getMob("magehuman", level);
            mage.canDespawn = false;
            Point spawnLocation = Waystone.findTeleportLocation(level, levelX, levelY, mage);
            level.entityManager.addMob(mage, spawnLocation.x, spawnLocation.y);
            mage.setHome(levelX, levelY);
            mageRef.set(mage);
            return (level1, presetX, presetY) -> mage.remove();
        });
        this.addSign(() -> ((MageHumanMob)mageRef.get()).getSettlerName() + "'s Scroll Shop", 3, 0, 0);
        PresetUtils.addShoreTiles(this, -1, -1, this.width + 2, this.height + 2);
    }
}

