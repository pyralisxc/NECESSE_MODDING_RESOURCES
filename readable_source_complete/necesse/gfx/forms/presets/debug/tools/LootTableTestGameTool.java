/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.debug.tools;

import java.util.ArrayList;
import necesse.engine.gameTool.GameToolManager;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameRandom;
import necesse.gfx.forms.floatMenu.SelectionFloatMenu;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.forms.presets.debug.tools.MouseDebugGameTool;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.levelBuffManager.LevelModifiers;

public class LootTableTestGameTool
extends MouseDebugGameTool {
    public static ArrayList<LabeledLootTable> lootTables = new ArrayList();
    private final LabeledLootTable lootTable;

    public static SelectionFloatMenu getSelectionMenu(DebugForm parent) {
        SelectionFloatMenu menu = new SelectionFloatMenu(parent);
        for (LabeledLootTable lootTable : lootTables) {
            menu.add(lootTable.name, () -> {
                LootTableTestGameTool tool = new LootTableTestGameTool(parent, lootTable);
                GameToolManager.clearGameTools(parent);
                GameToolManager.setGameTool(tool, parent);
                menu.remove();
            });
        }
        return menu;
    }

    public LootTableTestGameTool(DebugForm parent, LabeledLootTable lootTable) {
        super(parent, "Test " + lootTable.name);
        this.lootTable = lootTable;
    }

    @Override
    public void init() {
        this.onLeftClick(e -> {
            Level sLevel;
            GameObject chestObject = ObjectRegistry.getObject(ObjectRegistry.getObjectID("storagebox"));
            GameRandom random = new GameRandom();
            Level cLevel = this.getLevel();
            int tileX = this.getMouseTileX();
            int tileY = this.getMouseTileY();
            if (cLevel != null) {
                if (cLevel.getObjectID(tileX, tileY) != chestObject.getID()) {
                    chestObject.placeObject(cLevel, tileX, tileY, 2, false);
                }
                this.lootTable.table.applyToLevel(random, cLevel.buffManager.getModifier(LevelModifiers.LOOT).floatValue(), cLevel, tileX, tileY, new Object[0]);
            }
            if ((sLevel = this.getServerLevel()) != null) {
                if (sLevel.getObjectID(tileX, tileY) != chestObject.getID()) {
                    chestObject.placeObject(sLevel, tileX, tileY, 2, false);
                }
                this.lootTable.table.applyToLevel(random, sLevel.buffManager.getModifier(LevelModifiers.LOOT).floatValue(), sLevel, tileX, tileY, new Object[0]);
            }
            return true;
        }, "Place chest");
        this.onRightClick(e -> {
            Level sLevel;
            GameObject airObject = ObjectRegistry.getObject(ObjectRegistry.getObjectID("air"));
            Level cLevel = this.getLevel();
            if (cLevel != null) {
                airObject.placeObject(cLevel, this.getMouseTileX(), this.getMouseTileY(), 0, false);
            }
            if ((sLevel = this.getServerLevel()) != null) {
                airObject.placeObject(sLevel, this.getMouseTileX(), this.getMouseTileY(), 0, false);
            }
            return true;
        }, "Remove object");
    }

    static {
        lootTables.add(new LabeledLootTable("Start chest", LootTablePresets.startChest));
        lootTables.add(new LabeledLootTable("Ruins chest", LootTablePresets.surfaceRuinsChest));
        lootTables.add(new LabeledLootTable("Crate", LootTablePresets.basicCrate));
        lootTables.add(new LabeledLootTable("Swamp Crate", LootTablePresets.swampCrate));
        lootTables.add(new LabeledLootTable("Desert Crate", LootTablePresets.desertCrate));
        lootTables.add(new LabeledLootTable("Plains Crate", LootTablePresets.plainsCrate));
        lootTables.add(new LabeledLootTable("Deep crate", LootTablePresets.basicDeepCrate));
        lootTables.add(new LabeledLootTable("Snow Deep crate", LootTablePresets.snowDeepCrate));
        lootTables.add(new LabeledLootTable("Desert Deep crate", LootTablePresets.desertDeepCrate));
        lootTables.add(new LabeledLootTable("Basic cave chest", LootTablePresets.basicCaveChest));
        lootTables.add(new LabeledLootTable("Snow cave chest", LootTablePresets.snowCaveChest));
        lootTables.add(new LabeledLootTable("Swamp cave chest", LootTablePresets.swampCaveChest));
        lootTables.add(new LabeledLootTable("Desert cave chest", LootTablePresets.desertCaveChest));
        lootTables.add(new LabeledLootTable("Plains cave chest", LootTablePresets.plainsCaveChest));
        lootTables.add(new LabeledLootTable("Basic cave ruins chest", LootTablePresets.basicCaveRuinsChest));
        lootTables.add(new LabeledLootTable("Snow cave ruins chest", LootTablePresets.snowCaveRuinsChest));
        lootTables.add(new LabeledLootTable("Swamp cave ruins chest", LootTablePresets.swampCaveRuinsChest));
        lootTables.add(new LabeledLootTable("Desert cave ruins chest", LootTablePresets.desertCaveRuinsChest));
        lootTables.add(new LabeledLootTable("Dungeon chest", LootTablePresets.dungeonChest));
        lootTables.add(new LabeledLootTable("Pirate chest", LootTablePresets.pirateChest));
        lootTables.add(new LabeledLootTable("Pirate display stand", LootTablePresets.pirateDisplayStand));
        lootTables.add(new LabeledLootTable("Deep cave chest", LootTablePresets.deepCaveChest));
        lootTables.add(new LabeledLootTable("Abandoned mine chest", LootTablePresets.abandonedMineChest));
    }

    public static class LabeledLootTable {
        public final String name;
        public final LootTable table;

        public LabeledLootTable(String name, LootTable table) {
            this.name = name;
            this.table = table;
        }
    }
}

