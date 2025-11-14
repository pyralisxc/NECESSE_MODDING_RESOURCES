/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets;

import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.inventory.lootTable.presets.PaintingSelectionTable;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.presets.ElderHousePreset;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetUtils;

public class ElderHousePreset7
extends Preset {
    public ElderHousePreset7(GameRandom random) {
        super(17, 14);
        this.applyScript("PRESET = {\n\twidth = 17,\n\theight = 14,\n\ttileIDs = [17, stonefloor, 3, grasstile, 36, graveltile, 11, farmland, 12, woodfloor],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 11, 11, 11, 11, 11, 3, 3, 3, 3, 3, 3, 3, -1, -1, -1, -1, -1, 11, 11, 11, 11, 11, 3, 12, 12, 12, 12, 12, 3, -1, -1, -1, -1, -1, 3, 3, 3, 3, 3, 3, 12, 12, 12, 12, 12, 3, -1, -1, -1, -1, -1, 3, 12, 12, 12, 12, 3, 12, 12, 12, 12, 12, 3, -1, -1, -1, -1, -1, 3, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 3, -1, -1, -1, -1, -1, 3, 12, 12, 12, 12, 3, 12, 12, 12, 12, 12, 3, -1, -1, -1, -1, -1, 3, 12, 12, 12, 12, 3, 3, 3, 12, 3, 3, 3, -1, -1, -1, 36, 36, 3, 3, 3, 12, 3, 3, 3, 3, 3, 3, 3, -1, -1, -1, 36, 36, 36, 36, 3, 17, 17, 17, 3, 3, 3, 3, 3, 3, -1, -1, -1, 36, 36, 36, 36, 3, 17, 17, 17, 12, 3, 3, 3, 3, 3, -1, -1, -1, 36, 36, 36, 36, 3, 17, 17, 17, 3, 3, 3, 3, 3, 3, -1, -1, -1, -1, 36, 36, 36, 3, 3, 3, 3, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 320, sprucebed2, 323, sprucecandelabra, 35, workstationduo, 324, sprucedisplay, 36, workstationduo2, 296, oakbench, 457, bluecarpet, 297, oakbench2, 586, sunflowerseed4, 623, roastingstation, 786, ladderdown, 182, storagebox, 599, firemone, 216, wallcandle, 58, woodwall, 59, wooddoor, 187, torch, 158, woodfence, 319, sprucebed],\n\tobjects = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 586, 586, 586, 586, 586, 58, 58, 58, 58, 58, 58, 58, -1, -1, -1, -1, -1, 586, 586, 586, 586, 586, 58, 324, 0, 0, 0, 323, 58, -1, -1, -1, -1, -1, 58, 58, 58, 58, 58, 58, 0, 457, 457, 457, 0, 58, -1, -1, -1, -1, -1, 58, 319, 0, 182, 0, 58, 216, 457, 457, 457, 0, 58, -1, -1, -1, -1, -1, 58, 320, 0, 0, 0, 59, 0, 0, 0, 0, 0, 58, -1, -1, -1, -1, -1, 58, 0, 0, 0, 0, 58, 36, 35, 0, 0, 0, 58, -1, -1, -1, -1, -1, 58, 0, 0, 0, 323, 58, 58, 58, 59, 58, 58, 58, -1, -1, 158, 158, 158, 58, 58, 58, 59, 58, 58, 599, 216, 0, 296, 297, 158, -1, -1, 158, 0, 0, 0, 58, 0, 0, 0, 58, 0, 0, 0, 0, 0, 158, -1, -1, 0, 0, 623, 0, 58, 216, 786, 0, 59, 0, 0, 0, 0, 0, 158, -1, -1, 0, 0, 0, 0, 58, 0, 0, 0, 58, 0, 0, 0, 0, 0, 158, -1, -1, -1, 0, 0, 158, 58, 58, 58, 58, 58, 158, 158, -1, 158, 158, 158, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\trotations = [2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 2, 1, 2, 1, 2, 1, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 0, 2, 3, 3, 3, 1, 1, 2, 2, 2, 2, 2, 3, 2, 3, 2, 3, 3, 1, 3, 2, 3, 0, 1, 2, 2, 2, 2, 2, 3, 2, 1, 3, 3, 1, 2, 3, 3, 3, 0, 1, 2, 2, 2, 2, 2, 3, 1, 1, 3, 3, 3, 3, 3, 1, 0, 3, 1, 2, 2, 2, 2, 2, 3, 3, 3, 3, 2, 3, 2, 2, 2, 1, 1, 1, 2, 2, 0, 2, 2, 3, 3, 3, 2, 3, 3, 3, 2, 0, 1, 1, 0, 2, 2, 0, 3, 0, 2, 3, 0, 3, 2, 3, 0, 0, 0, 0, 0, 0, 2, 2, 1, 3, 3, 3, 3, 1, 1, 3, 1, 0, 1, 0, 3, 1, 1, 2, 2, 0, 3, 3, 3, 3, 3, 3, 3, 3, 0, 0, 0, 0, 0, 0, 2, 2, 2, 1, 3, 1, 3, 3, 3, 3, 3, 0, 0, 2, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 187, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 187, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttableDecorRotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]\n}");
        this.replaceTile(TileRegistry.grassID, -1);
        this.addInventory(LootTablePresets.startChest, random, 7, 4, new Object[0]);
        this.addInventory(LootTablePresets.startDisplayStand, random, 10, 2, new Object[0]);
        PresetUtils.applyRandomPainting(this, 12, 2, 2, random, PaintingSelectionTable.commonPaintings);
        this.fillObject(4, 1, 5, 2, ObjectRegistry.getObjectID("sunflowerseed"));
        this.addCustomPreApplyRectEach(-1, -1, this.width + 2, this.height + 5, 0, (level, levelX, levelY, dir, blackboard) -> {
            if (level.getTile((int)levelX, (int)levelY).isLiquid) {
                level.setTile(levelX, levelY, TileRegistry.sandID);
                level.setObject(levelX, levelY, 0);
            }
            return null;
        });
        this.addCustomPreApplyRectEach(0, 0, this.width, this.height + 5, 0, (level, levelX, levelY, dir, blackboard) -> {
            GameObject object = level.getObject(levelX, levelY);
            if (!object.isGrass) {
                level.setObject(levelX, levelY, 0);
            }
            return null;
        });
        this.addCustomApply(3, 10, 0, (level, levelX, levelY, dir, blackboard) -> {
            level.setObject(levelX, levelY, ObjectRegistry.getObjectID("roastingstation"), dir);
            return null;
        });
        this.addCustomPreApplyRectEach(2, 9, 3, 3, 0, (level, levelX, levelY, dir, blackboard) -> {
            if (level.getTile((int)levelX, (int)levelY).isLiquid) {
                level.setTile(levelX, levelY, TileRegistry.sandID);
                level.setObject(levelX, levelY, 0);
            }
            return null;
        });
        this.addCustomApply(5, 4, 2, (level, levelX, levelY, dir, blackboard) -> {
            HumanMob mob = (HumanMob)MobRegistry.getMob("elderhuman", level);
            mob.setSettlerSeed(random.nextInt(), true);
            ElderHousePreset.createAndAddElder(mob, level, levelX, levelY);
            return (level1, presetX, presetY) -> mob.remove();
        });
    }
}

