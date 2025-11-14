/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.worldStructures;

import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.objectEntity.ApiaryObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.FenceSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.WallSet;
import necesse.level.maps.presets.worldStructures.LandStructurePreset;

public class BeeKeeperPreset
extends LandStructurePreset {
    public BeeKeeperPreset(GameRandom random, WallSet wallSet, FenceSet fenceSet, FurnitureSet furnitureSet) {
        super(9, 10);
        this.applyScript("PRESET = {\n\twidth = 9,\n\theight = 10,\n\ttileIDs = [3, grasstile, 5, swampgrasstile, 11, farmland],\n\ttiles = [-1, -1, 3, 3, 3, 3, 3, -1, -1, -1, 3, 3, 5, 11, 11, 3, 3, -1, 3, 3, 5, 3, 3, 3, 11, 3, 3, 3, 11, 3, 3, 3, 3, 5, 5, 3, 3, 11, 3, 3, 3, 3, 3, 5, 3, 3, 5, 3, 3, 3, 3, 3, 11, 3, -1, 3, 11, 11, 3, 11, 11, 3, -1, -1, 3, 3, 3, 3, 3, 3, 3, -1, -1, -1, 3, -1, -1, -1, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 576, sunflowerseed4, 545, sunflowerseed2, 515, firemoneseed3, 295, oakdisplay, 520, iceblossomseed2, 555, sunflowerseed3, 587, firemoneseed2, 590, iceblossomseed3, 623, apiary, 49, woodwall, 594, iceblossomseed3, 149, woodfence, 759, grass, 151, woodfencegateopen, 540, firemoneseed3, 286, oakbench, 191, ironstreetlamp, 287, oakbench2],\n\tobjects = [0, 0, 149, 149, 149, 149, 149, 0, 0, 0, 49, 49, 623, 555, 587, 49, 49, 0, 49, 49, 623, 0, 0, 0, 590, 49, 49, 49, 520, 0, 0, 191, 0, 0, 623, 49, 49, 545, 0, 0, 295, 0, 0, 623, 49, 49, 623, 759, 0, 0, 0, 0, 576, 49, 149, 149, 587, 515, 0, 540, 594, 149, 149, 759, 149, 149, 149, 151, 149, 149, 149, 0, 0, 759, 286, 287, 0, 286, 287, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\trotations = [0, 0, 3, 3, 3, 3, 3, 0, 0, 0, 0, 1, 0, 3, 0, 3, 1, 0, 0, 0, 0, 0, 0, 1, 0, 2, 0, 0, 0, 0, 0, 1, 0, 0, 1, 2, 3, 1, 0, 3, 2, 0, 0, 1, 2, 0, 3, 0, 3, 0, 0, 0, 1, 2, 3, 3, 3, 0, 0, 2, 3, 1, 1, 0, 3, 3, 3, 2, 3, 3, 1, 0, 0, 0, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\twire = [85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85]\n}");
        WallSet.wood.replaceWith(wallSet, this);
        FenceSet.wood.replaceWith(fenceSet, this);
        FurnitureSet.oak.replaceWith(furnitureSet, this);
        this.giveApiaryQueenBee(1, 5);
        this.giveApiaryQueenBee(3, 1);
        this.giveApiaryQueenBee(7, 4);
        this.addInventory(new LootTable(new LootItem("queenbee")), random, 4, 4, new Object[0]);
    }

    private void giveApiaryQueenBee(int x, int y) {
        this.addCustomApply(x, y, 0, new Preset.CustomApplyFunction(){

            @Override
            public Preset.UndoLogic applyToLevel(Level level, int levelX, int levelY, int dir, GameBlackboard blackboard) {
                ObjectEntity objectEntity = level.entityManager.getObjectEntity(levelX, levelY);
                if (objectEntity instanceof ApiaryObjectEntity) {
                    ApiaryObjectEntity apiary = (ApiaryObjectEntity)objectEntity;
                    apiary.hasQueen = true;
                }
                return null;
            }
        });
    }
}

