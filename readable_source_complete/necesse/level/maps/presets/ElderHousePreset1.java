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

public class ElderHousePreset1
extends Preset {
    public ElderHousePreset1(GameRandom random) {
        super(17, 14);
        this.applyScript("PRESET = {\n\twidth = 17,\n\theight = 14,\n\ttileIDs = [17, stonefloor, 3, grasstile, 36, graveltile, 11, farmland, 12, woodfloor],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 3, 3, 3, 3, 3, 3, 11, 11, -1, -1, -1, 36, 36, 3, 3, 3, 3, 3, 12, 12, 12, 12, 3, 11, 11, -1, 36, 36, 36, 36, 3, 17, 17, 17, 3, 12, 12, 12, 12, 3, 11, 11, -1, 36, 36, 36, 36, 3, 17, 17, 17, 12, 12, 12, 12, 12, 3, 11, 11, -1, 36, 36, 36, 36, 3, 17, 17, 17, 3, 12, 12, 12, 12, 3, 3, 3, -1, -1, 36, 36, 36, 3, 3, 3, 3, 3, 12, 12, 12, 12, 12, 12, 3, -1, -1, -1, -1, -1, 3, 3, 3, 3, 12, 12, 12, 12, 12, 12, 12, 3, -1, -1, -1, -1, -1, 3, 3, 3, 12, 12, 12, 12, 12, 12, 12, 12, 3, -1, -1, -1, -1, -1, 3, 3, 3, 3, 12, 12, 12, 12, 12, 3, 3, 3, -1, -1, -1, -1, -1, 3, 3, 3, 3, 3, 3, 12, 3, 3, 3, 11, 11, -1, -1, -1, -1, -1, -1, -1, -1, 3, 3, 3, 3, 3, 3, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 3, 3, 3, 3, 3, 3, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 320, sprucebed2, 769, grass, 323, sprucecandelabra, 35, workstationduo, 36, workstationduo2, 324, sprucedisplay, 586, sunflowerseed4, 623, roastingstation, 786, ladderdown, 182, storagebox, 599, firemone, 216, wallcandle, 312, sprucedesk, 58, woodwall, 59, wooddoor, 187, torch, 158, woodfence, 319, sprucebed, 159, woodfencegate],\n\tobjects = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 58, 58, 58, 58, 58, 58, 586, 586, -1, -1, 0, 0, 0, 58, 58, 58, 58, 58, 323, 35, 36, 182, 58, 586, 586, -1, 0, 0, 0, 0, 58, 0, 216, 0, 58, 0, 0, 0, 0, 58, 586, 586, -1, 0, 0, 623, 0, 58, 0, 786, 0, 59, 0, 0, 0, 0, 58, 586, 586, -1, 0, 0, 0, 0, 58, 0, 0, 0, 58, 0, 0, 0, 0, 58, 58, 58, -1, -1, 0, 0, 0, 58, 58, 58, 58, 58, 0, 0, 0, 0, 320, 319, 58, -1, -1, -1, -1, -1, 158, 0, 599, 58, 0, 0, 0, 0, 0, 0, 312, 58, -1, -1, -1, -1, -1, 159, 0, 0, 59, 0, 0, 0, 0, 0, 0, 0, 58, -1, -1, -1, -1, -1, 158, 0, 0, 58, 324, 0, 0, 0, 323, 58, 58, 58, -1, -1, -1, -1, -1, 158, 158, 158, 58, 58, 58, 59, 58, 58, 58, 586, 586, -1, -1, -1, -1, -1, -1, -1, -1, 158, 0, 0, 0, 0, 0, 158, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 158, 158, 769, 0, 0, 158, 158, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\trotations = [3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 0, 0, 1, 3, 0, 0, 0, 1, 1, 1, 1, 1, 2, 1, 1, 2, 3, 0, 0, 1, 0, 0, 0, 0, 3, 0, 2, 0, 1, 0, 0, 0, 1, 3, 0, 0, 1, 0, 0, 2, 0, 3, 0, 3, 0, 1, 0, 0, 0, 0, 3, 0, 0, 1, 0, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 0, 0, 3, 0, 0, 1, 2, 0, 0, 0, 3, 3, 3, 3, 3, 1, 0, 1, 1, 3, 3, 0, 1, 2, 2, 2, 2, 3, 0, 2, 3, 1, 1, 1, 1, 1, 0, 3, 0, 1, 2, 2, 2, 2, 3, 0, 0, 3, 3, 1, 0, 1, 1, 0, 1, 0, 1, 2, 2, 2, 2, 3, 0, 0, 3, 3, 1, 0, 1, 2, 3, 0, 0, 1, 2, 2, 2, 2, 3, 2, 2, 3, 3, 3, 2, 3, 3, 3, 0, 0, 1, 2, 2, 2, 2, 1, 1, 1, 3, 0, 0, 0, 0, 0, 3, 1, 1, 1, 2, 2, 2, 2, 1, 1, 1, 3, 3, 0, 0, 0, 3, 3, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 187, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 187, 0, 0, 0, 187, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttableDecorRotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]\n}");
        this.replaceTile(TileRegistry.grassID, -1);
        this.addInventory(LootTablePresets.startChest, random, 12, 2, new Object[0]);
        this.addInventory(LootTablePresets.startDisplayStand, random, 8, 9, new Object[0]);
        PresetUtils.applyRandomPainting(this, 8, 7, 2, random, PaintingSelectionTable.commonPaintings);
        this.fillObject(14, 1, 2, 4, ObjectRegistry.getObjectID("sunflowerseed"));
        this.fillObject(14, 10, 2, 1, ObjectRegistry.getObjectID("sunflowerseed"));
        this.addCustomPreApplyRectEach(0, 0, this.width, this.height, 0, (level, levelX, levelY, dir, blackboard) -> {
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
        this.addCustomApply(2, 4, 0, (level, levelX, levelY, dir, blackboard) -> {
            level.setObject(levelX, levelY, ObjectRegistry.getObjectID("roastingstation"), dir);
            return null;
        });
        this.addCustomPreApplyRectEach(1, 3, 3, 3, 0, (level, levelX, levelY, dir, blackboard) -> {
            if (level.getTile((int)levelX, (int)levelY).isLiquid) {
                level.setTile(levelX, levelY, TileRegistry.sandID);
                level.setObject(levelX, levelY, 0);
            }
            return null;
        });
        this.addCustomApply(14, 6, 3, (level, levelX, levelY, dir, blackboard) -> {
            HumanMob mob = (HumanMob)MobRegistry.getMob("elderhuman", level);
            mob.setSettlerSeed(random.nextInt(), true);
            ElderHousePreset.createAndAddElder(mob, level, levelX, levelY);
            return (level1, presetX, presetY) -> mob.remove();
        });
    }
}

