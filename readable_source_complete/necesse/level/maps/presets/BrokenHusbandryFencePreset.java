/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets;

import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.FenceSet;

public class BrokenHusbandryFencePreset
extends Preset {
    private static final LootTable barrelLootTable = new LootTable(LootItem.between("woodfence", 4, 8), LootItem.between("sprucelog", 8, 12), new LootItem("rope", 2), LootItem.between("wheat", 4, 8), new LootItem("eggnest"));

    public BrokenHusbandryFencePreset(GameRandom random, FenceSet fenceSet) {
        super(9, 9);
        this.applyScript("PRESET = {\n\twidth = 9,\n\theight = 9,\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 721, feedingtrough, 722, feedingtrough2, 215, barrel, 40, workstationduo, 41, workstationduo2, 190, woodfence, 191, woodfencegate],\n\tobjects = [190, 190, 190, -1, -1, 190, -1, 190, 190, 190, -1, -1, -1, -1, -1, -1, -1, 190, -1, -1, -1, -1, -1, -1, -1, -1, 190, 190, 722, -1, -1, -1, -1, -1, -1, 190, -1, 721, -1, -1, -1, -1, -1, -1, -1, 190, -1, -1, -1, -1, -1, -1, -1, 190, 190, -1, -1, -1, -1, -1, -1, -1, 190, 190, 190, -1, 190, 191, -1, 190, 190, 190, -1, 215, -1, -1, -1, -1, 40, 41, -1],\n\trotations = [3, 3, 3, 1, 1, 3, 1, 3, 3, 3, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 3, 3, 0, 1, 1, 1, 1, 1, 1, 3, 1, 0, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 3, 3, 3, 1, 3, 2, 1, 3, 0, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true\n}");
        FenceSet.wood.replaceWith(fenceSet, this);
        this.addInventory(barrelLootTable, random, 1, 8, new Object[0]);
        this.addCustomPreApplyRectEach(0, 0, this.width, this.height, 0, new Preset.CustomApplyFunction(){

            @Override
            public Preset.UndoLogic applyToLevel(Level level, int levelX, int levelY, int dir, GameBlackboard blackboard) {
                if (!level.getObject((int)levelX, (int)levelY).isGrass) {
                    level.setObject(levelX, levelY, 0);
                }
                return null;
            }
        });
        this.addCanApplyRectPredicate(-4, -4, this.width + 8, this.height + 8, 0, (level, levelStartX, levelStartY, levelEndX, levelEndY, dir) -> {
            for (int x = levelStartX; x <= levelEndX; ++x) {
                for (int y = levelStartY; y <= levelEndY; ++y) {
                    if (!level.isLiquidTile(x, y)) continue;
                    return false;
                }
            }
            return true;
        });
        this.addCanApplyRectPredicate(-10, -10, this.width + 10, this.height + 10, 0, (level, levelStartX, levelStartY, levelEndX, levelEndY, dir) -> {
            for (int tileY = levelStartY; tileY < levelEndY; ++tileY) {
                for (int tileX = levelStartX; tileX < levelEndX; ++tileX) {
                    if (!level.isLiquidTile(tileX, tileY)) continue;
                    return true;
                }
            }
            return false;
        });
    }
}

