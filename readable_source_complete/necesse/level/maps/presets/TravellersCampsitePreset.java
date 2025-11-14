/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameRandom;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.SignObjectEntity;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.presets.BiomeOresLootTable;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.BushSet;
import necesse.level.maps.presets.set.FenceSet;
import necesse.level.maps.presets.set.TreeSet;

public class TravellersCampsitePreset
extends Preset {
    public TravellersCampsitePreset(GameRandom random, FenceSet fenceSet, TreeSet treeSet, BushSet bushSet) {
        super("PRESET = {\n\twidth = 12,\n\theight = 10,\n\ttileIDs = [1, dirttile, 42, graniterocktile, 46, graveltile],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 42, 42, 46, 42, -1, -1, -1, -1, -1, 42, 46, 42, 42, 42, 42, 42, 46, -1, -1, -1, -1, 42, 42, 46, 46, 42, 42, 42, 46, -1, -1, -1, -1, -1, 42, 42, 46, 42, 42, 46, 46, -1, -1, -1, -1, -1, -1, -1, 42, 42, 42, 46, -1, -1, -1, -1, -1, -1, -1, -1, 42, 1, 46, -1, -1, -1, -1, -1, -1, -1, -1, 46, 42, 46, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 2, oaktree, 1027, surfacerock, 1028, surfacerockr, 390, sign, 7, sprucelogbench, 8, sprucelogbench2, 278, barrel, 282, merchantsbackpack, 1180, grass, 734, decorativepot1, 735, decorativepot2, 39, appletree, 1192, yellowflowerpatch, 1193, redflowerpatch, 1196, whiteflowerpatch, 236, woodfence, 47, blueberrybush, 435, bigtent, 436, bigtent2, 308, oillantern, 1012, roastingstation, 437, bigtent3, 438, bigtent4, 763, dogplush, 1022, apiary],\n\tobjects = [1192, -1, -1, 1192, -1, 0, 1196, -1, -1, -1, -1, -1, -1, 2, 0, 1180, 236, 236, 236, 0, 0, 2, 0, -1, -1, 0, 1192, 1192, 2, 1027, 1028, 0, 236, 236, 236, -1, 1196, 735, 435, 436, 0, 278, 0, 0, 763, 1022, 236, -1, -1, 0, 437, 438, 308, 734, 0, 0, 0, 0, 0, -1, 0, 47, 0, 0, 0, 0, 0, 1012, 0, 0, 0, -1, 1193, 0, 0, 0, 0, 0, 282, 8, 7, 0, 0, -1, 0, 236, 236, 236, 236, 0, 0, 0, 0, 0, 39, -1, -1, 0, 2, 0, 390, 0, 0, 236, 236, 236, 236, -1, -1, -1, -1, 1193, 0, 0, 0, 1196, -1, -1, 1027, 1028],\n\trotations = [0, 3, 3, 0, 3, 0, 1, 0, 0, 0, 0, 0, 3, 0, 0, 2, 0, 0, 0, 0, 0, 0, 2, 0, 3, 1, 0, 1, 0, 2, 2, 0, 2, 2, 2, 0, 0, 1, 0, 0, 3, 2, 1, 0, 2, 0, 2, 0, 3, 1, 0, 0, 2, 2, 2, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 2, 0, 2, 2, 0, 0, 0, 1, 0, 0, 3, 3, 0, 2, 3, 3, 0, 0, 0, 0, 1, 2, 1, 2, 2, 0, 0, 0, 1, 2, 0, 3, 0, 3, 0, 2, 0, 2, 2, 2, 2, 2, 0, 3, 3, 3, 2, 0, 0, 0, 1, 3, 3, 3, 3],\n\ttileObjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 1196, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1196, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1196, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttileRotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true,\n\tclearOtherWires = false\n}");
        if (treeSet != null) {
            TreeSet.oak.replaceWith(treeSet, this);
        }
        if (fenceSet != null) {
            FenceSet.wood.replaceWith(fenceSet, this);
        }
        if (bushSet != null) {
            BushSet.blueberry.replaceWith(bushSet, this);
        }
        this.addInventory(new LootTable(LootItem.between("torch", 13, 23), LootItem.between("healthpotion", 3, 7), random.getOneOf(new LootItem("heavyhammer"), new LootItem("frostbow"), new LootItem("sprinkler")), random.getOneOf(LootItem.between("hardboiledegg", 2, 7), LootItem.between("roastedfrogleg", 2, 7), LootItem.between("candycane", 2, 7))), random, 6, 6, new Object[0]);
        this.addInventory(BiomeOresLootTable.defaultOres, random, 5, 3, new Object[0]);
        this.addCustomApply(4, 8, 0, (level, levelX, levelY, dir, blackboard) -> {
            try {
                ObjectEntity objEnt = level.entityManager.getObjectEntity(levelX, levelY);
                if (objEnt instanceof SignObjectEntity) {
                    ((SignObjectEntity)objEnt).setText(Localization.translate("biome", "travellerscampsitepreset"));
                } else if (level.isServer()) {
                    throw new NullPointerException("Could not find a sign objectEntity for preset at " + levelX + ", " + levelY);
                }
            }
            catch (Exception e) {
                System.err.println(e.getMessage());
            }
            return null;
        });
    }
}

