/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.worldStructures;

import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.critters.SpiderCritterMob;
import necesse.entity.objectEntity.CampfireObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.worldStructures.LandStructurePreset;

public class AbandonedWitchCabinPreset
extends LandStructurePreset {
    public AbandonedWitchCabinPreset(GameRandom random) {
        super(10, 14);
        this.applyScript("PRESET = {\n\twidth = 7,\n\theight = 8,\n\ttileIDs = [1, dirttile, 8, dungeonfloor, 10, woodfloor],\n\ttiles = [1, 1, 1, 1, 1, 1, -1, -1, 10, 8, 8, -1, -1, -1, 1, 10, 8, 1, 10, -1, -1, -1, 8, 1, 1, 8, 1, 1, -1, -1, 1, 8, 1, 1, -1, 1, 1, -1, 1, -1, 1, 1, -1, -1, 1, 1, -1, -1, -1, -1, -1, 1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 146, stonefence, 371, swampstonearrowtrap, 68, swampstonewall, 311, dungeoncandelabra, 297, dungeonchest, 58, stonewall, 506, cookingpot, 302, dungeonchair, 350, woodpressureplate],\n\tobjects = [0, 68, 68, 58, 0, 0, 0, 58, 311, 297, 311, 371, 146, 0, 68, 302, 0, 0, 0, 146, 0, 68, 0, 0, 0, 350, 146, 0, 146, 58, 68, 506, 0, 0, 0, 0, 0, 146, 146, 146, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\trotations = [0, 3, 3, 3, 0, 3, 0, 3, 0, 2, 0, 2, 0, 0, 3, 1, 0, 0, 0, 0, 0, 3, 3, 0, 1, 2, 0, 0, 2, 3, 2, 3, 2, 2, 3, 0, 0, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\twire = [85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 87, 85, 85, 85, 85, 85, 85, 87, 85, 85, 85, 85, 85, 85, 87, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85]\n}");
        this.addInventory(LootTablePresets.witchCrate, random, 2, 1, new Object[0]);
        this.addInventory(new LootTable(LootItem.between("oaklog", 5, 14)), random, 3, 4, new Object[0]);
        this.addCustomApply(3, 4, 0, new Preset.CustomApplyFunction(){

            @Override
            public Preset.UndoLogic applyToLevel(Level level, int levelX, int levelY, int dir, GameBlackboard blackboard) {
                ObjectEntity objectEntity = level.entityManager.getObjectEntity(levelX, levelY);
                if (objectEntity instanceof CampfireObjectEntity) {
                    CampfireObjectEntity cf = (CampfireObjectEntity)objectEntity;
                    cf.keepRunning = true;
                }
                return null;
            }
        });
        this.addMob("spider", 0, 0, SpiderCritterMob.class, spider -> spider.setRunning(false));
        this.addMob("spider", 4, 0, SpiderCritterMob.class, spider -> spider.setRunning(false));
        this.addMob("spider", 5, 6, SpiderCritterMob.class, spider -> spider.setRunning(false));
    }
}

