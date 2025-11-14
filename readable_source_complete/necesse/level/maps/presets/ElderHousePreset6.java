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

public class ElderHousePreset6
extends Preset {
    public ElderHousePreset6(GameRandom random) {
        super(14, 17);
        this.applyScript("PRESET = {\n\twidth = 14,\n\theight = 17,\n\ttileIDs = [17, stonefloor, 3, grasstile, 36, graveltile, 11, farmland, 12, woodfloor],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 11, -1, -1, -1, 3, 12, 12, 12, 12, 3, 17, 17, 17, 3, 11, -1, -1, -1, 3, 12, 12, 12, 12, 3, 17, 17, 17, 3, 11, -1, -1, -1, 3, 12, 12, 12, 12, 3, 17, 17, 17, 3, 11, -1, -1, -1, 3, 12, 12, 3, 3, 3, 3, 12, 3, 3, 11, -1, -1, -1, 3, 12, 12, 3, 12, 12, 12, 12, 12, 3, 11, -1, -1, -1, 3, 12, 12, 3, 12, 12, 12, 12, 12, 3, 11, -1, -1, -1, 3, 12, 12, 12, 12, 12, 12, 12, 12, 3, 11, -1, -1, -1, 3, 12, 12, 3, 12, 12, 3, 12, 12, 3, 11, -1, -1, -1, 3, 3, 3, 3, 12, 12, 3, 3, 3, 3, 11, -1, -1, 36, 36, 36, 36, 36, -1, -1, -1, -1, -1, -1, -1, -1, -1, 36, 36, 36, 36, 36, -1, -1, -1, -1, -1, -1, -1, -1, -1, 36, 36, 36, 36, 36, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 36, 36, 36, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 320, sprucebed2, 323, sprucecandelabra, 35, workstationduo, 324, sprucedisplay, 36, workstationduo2, 586, sunflowerseed4, 623, roastingstation, 786, ladderdown, 182, storagebox, 599, firemone, 216, wallcandle, 313, sprucemodulartable, 58, woodwall, 314, sprucechair, 59, wooddoor, 319, sprucebed],\n\tobjects = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 586, -1, -1, -1, 58, 599, 0, 0, 323, 58, 0, 216, 0, 58, 586, -1, -1, -1, 58, 0, 0, 0, 319, 58, 0, 786, 0, 58, 586, -1, -1, -1, 58, 314, 0, 0, 320, 58, 0, 0, 0, 58, 586, -1, -1, -1, 58, 313, 0, 58, 58, 58, 58, 59, 58, 58, 586, -1, -1, -1, 58, 313, 0, 58, 323, 0, 324, 0, 35, 58, 586, -1, -1, -1, 58, 314, 0, 58, 0, 0, 0, 0, 36, 58, 586, -1, -1, -1, 58, 216, 0, 59, 0, 0, 0, 0, 216, 58, 586, -1, -1, -1, 58, 0, 0, 58, 0, 0, 58, 182, 0, 58, 586, -1, -1, -1, 58, 58, 58, 58, 59, 59, 58, 58, 58, 58, 586, -1, -1, 0, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 623, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\trotations = [0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1, 0, 0, 3, 3, 1, 3, 3, 1, 1, 1, 1, 1, 1, 1, 0, 0, 3, 2, 0, 3, 2, 3, 2, 2, 2, 1, 1, 2, 0, 0, 1, 3, 3, 3, 2, 3, 3, 1, 3, 1, 1, 2, 0, 0, 2, 2, 3, 0, 2, 3, 3, 2, 0, 3, 1, 2, 0, 0, 2, 3, 3, 3, 3, 3, 3, 2, 3, 2, 1, 2, 0, 0, 3, 0, 0, 3, 2, 2, 1, 2, 2, 2, 1, 2, 0, 0, 3, 0, 2, 3, 3, 0, 3, 3, 2, 3, 1, 2, 0, 0, 1, 1, 2, 1, 3, 3, 3, 2, 3, 1, 1, 2, 0, 0, 2, 2, 2, 3, 3, 0, 3, 0, 2, 2, 1, 2, 0, 0, 2, 2, 1, 2, 2, 2, 2, 1, 2, 2, 1, 2, 0, 0, 3, 2, 2, 0, 1, 1, 3, 3, 3, 3, 3, 2, 0, 0, 3, 3, 3, 2, 1, 1, 3, 3, 3, 3, 3, 2, 0, 2, 3, 2, 2, 2, 1, 1, 3, 3, 3, 3, 3, 2, 3, 3, 3, 3, 3, 2, 1, 1, 3, 3, 3, 3, 3, 2, 3, 3, 3, 3, 1, 1, 1, 1, 3, 3, 3, 3, 3, 2],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true\n}");
        this.replaceTile(TileRegistry.grassID, -1);
        this.addInventory(LootTablePresets.startChest, random, 9, 10, new Object[0]);
        this.addInventory(LootTablePresets.startDisplayStand, random, 8, 7, new Object[0]);
        PresetUtils.applyRandomPainting(this, 4, 3, 2, random, PaintingSelectionTable.largeRarePaintings);
        this.fillObject(12, 2, 1, 10, ObjectRegistry.getObjectID("sunflowerseed"));
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
        this.addCustomApply(3, 13, 0, (level, levelX, levelY, dir, blackboard) -> {
            level.setObject(levelX, levelY, ObjectRegistry.getObjectID("roastingstation"), dir);
            return null;
        });
        this.addCustomPreApplyRectEach(2, 12, 3, 3, 0, (level, levelX, levelY, dir, blackboard) -> {
            if (level.getTile((int)levelX, (int)levelY).isLiquid) {
                level.setTile(levelX, levelY, TileRegistry.sandID);
                level.setObject(levelX, levelY, 0);
            }
            return null;
        });
        this.addCustomApply(6, 4, 2, (level, levelX, levelY, dir, blackboard) -> {
            HumanMob mob = (HumanMob)MobRegistry.getMob("elderhuman", level);
            mob.setSettlerSeed(random.nextInt(), true);
            ElderHousePreset.createAndAddElder(mob, level, levelX, levelY);
            return (level1, presetX, presetY) -> mob.remove();
        });
    }
}

