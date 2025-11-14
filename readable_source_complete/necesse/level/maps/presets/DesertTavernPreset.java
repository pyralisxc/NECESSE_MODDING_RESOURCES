/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets;

import java.awt.Point;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.SignObjectEntity;
import necesse.gfx.HumanGender;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.inventory.lootTable.presets.PaintingSelectionTable;
import necesse.level.maps.levelData.settlementData.Waystone;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.WallSet;

public class DesertTavernPreset
extends Preset {
    public DesertTavernPreset(GameRandom random, FurnitureSet furnitureSet, WallSet wallSet) {
        super("PRESET = {\n\twidth = 18,\n\theight = 14,\n\ttileIDs = [16, palmfloor, 5, sandtile, 63, sandgraveltile],\n\ttiles = [-1, -1, -1, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, -1, -1, -1, 5, 16, 16, 16, 5, 16, 16, 16, 5, 16, 16, 16, 16, 16, 5, -1, -1, -1, 5, 16, 16, 16, 5, 16, 16, 16, 5, 16, 16, 16, 16, 16, 5, -1, -1, -1, 5, 16, 16, 16, 5, 16, 16, 16, 5, 16, 16, 16, 16, 16, 5, -1, -1, -1, 5, 5, 16, 5, 5, 16, 5, 5, 5, 16, 5, 5, 5, 5, 5, -1, -1, -1, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, -1, -1, -1, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, -1, -1, -1, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, -1, -1, 63, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 63, 63, 63, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, -1, -1, 63, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, -1, -1, -1, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, -1, -1, -1, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, -1, -1, -1, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5],\n\tobjectIDs = [0, air, 832, heartcarpet, 128, sandstonedoor, 771, reddiningset, 132, sandstonewindow, 773, dirtydishes, 390, sign, 774, stewpot, 327, paintingavocado, 778, papertowel, 786, stackedbooks, 787, quillandparchment, 539, palmdinnertable, 348, paintinglargeabstract, 540, palmdinnertable2, 349, paintinglargeabstract2, 542, palmmodulartable, 543, palmchair, 547, palmcabinet, 548, palmbed, 741, pottedcactus4, 549, palmbed2, 550, palmdoublebed, 551, palmdoublebedfoot1, 743, pottedflower2, 552, palmdoublebed2, 553, palmdoublebedfoot2, 809, goldchalice, 554, palmdresser, 556, palmcandelabra, 813, oldsoup, 816, oldchalices, 822, roastedduck, 759, bloodgoblet, 823, roastedduck2, 828, brownbearcarpet, 765, dinoplush, 318, tikitorch, 127, sandstonewall],\n\tobjects = [-1, -1, -1, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, -1, -1, -1, 127, 741, 554, 548, 127, 556, 554, 548, 127, 556, 554, 550, 552, 547, 127, -1, -1, -1, 132, 0, 0, 549, 127, 0, 0, 549, 127, 0, 832, 551, 553, 832, 132, -1, -1, -1, 127, 556, 0, 0, 127, 0, 0, 0, 127, 0, 832, 832, 832, 832, 127, -1, -1, -1, 127, 127, 128, 127, 127, 128, 127, 127, 127, 128, 127, 127, 127, 127, 127, -1, -1, -1, 127, 556, 828, 349, 348, 828, 0, 327, 0, 828, 0, 542, 0, 556, 127, -1, -1, -1, 127, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 542, 0, 542, 127, -1, -1, -1, 132, 0, 543, 539, 543, 0, 543, 540, 543, 0, 0, 542, 0, 542, 127, 318, 0, 390, 127, 0, 543, 540, 543, 0, 543, 539, 543, 0, 0, 542, 0, 0, 132, 0, 0, 0, 128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 542, 0, 0, 127, 318, 0, 0, 127, 0, 543, 540, 543, 0, 543, 540, 543, 0, 0, 542, 542, 0, 127, -1, -1, -1, 127, 0, 543, 539, 543, 0, 543, 539, 543, 0, 0, 0, 0, 0, 128, -1, -1, -1, 127, 556, 0, 0, 0, 0, 0, 556, 0, 0, 0, 0, 0, 556, 127, -1, -1, -1, 127, 127, 127, 132, 127, 132, 127, 127, 127, 132, 127, 132, 127, 127, 127],\n\trotations = [0, 0, 0, 3, 3, 3, 3, 2, 3, 1, 1, 1, 1, 1, 1, 1, 1, 2, 0, 0, 0, 2, 3, 2, 2, 0, 2, 2, 2, 0, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 2, 0, 3, 3, 2, 0, 0, 3, 2, 2, 3, 0, 0, 0, 0, 2, 0, 0, 0, 0, 3, 3, 0, 1, 0, 3, 3, 3, 3, 2, 0, 0, 0, 2, 0, 2, 1, 1, 2, 1, 1, 1, 2, 1, 1, 1, 3, 2, 0, 0, 0, 2, 2, 2, 2, 2, 2, 0, 2, 0, 1, 0, 3, 0, 3, 2, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 2, 0, 0, 0, 2, 0, 1, 2, 3, 0, 1, 0, 3, 0, 0, 2, 0, 0, 3, 2, 0, 3, 1, 0, 1, 2, 3, 0, 1, 0, 3, 0, 0, 2, 0, 0, 1, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 1, 3, 0, 0, 0, 0, 1, 0, 3, 0, 1, 0, 3, 0, 0, 2, 2, 0, 2, 2, 3, 3, 0, 0, 1, 0, 3, 0, 1, 0, 3, 0, 0, 0, 0, 0, 1, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 2, 2, 2, 2, 1, 2, 1, 2, 1, 1, 1, 2, 1, 2, 1, 1, 1],\n\ttileObjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 832, 832, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttileRotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\twallDecorObjectsClear = true,\n\ttableDecorObjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 765, 0, 0, 0, 743, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 786, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 759, 0, 774, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 778, 0, 0, 0, 0, 0, 773, 0, 0, 0, 0, 0, 0, 0, 813, 0, 0, 0, 0, 0, 0, 0, 787, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 771, 0, 0, 0, 0, 0, 0, 0, 822, 823, 0, 0, 0, 0, 0, 0, 0, 0, 809, 0, 0, 0, 816, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttableDecorRotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\tclearOtherWires = false\n}");
        FurnitureSet.palm.replaceWith(furnitureSet, this);
        WallSet.sandstone.replaceWith(wallSet, this);
        PresetUtils.applyRandomPainting(this, 10, 5, 2, random, PaintingSelectionTable.commonPaintings);
        PresetUtils.applyRandomPainting(this, 7, 5, 2, random, PaintingSelectionTable.largeRarePaintings);
        this.addInventory(new LootTable(LootTablePresets.rollingPinDisplayStand), random, 16, 1, new Object[0]);
        AtomicReference firstSettlerRef = new AtomicReference();
        AtomicReference secondSettlerRef = new AtomicReference();
        boolean farmerIsMale = random.getChance(0.5f);
        this.addCustomApply(14, 1, 0, (level, levelX, levelY, dir, blackboard) -> {
            HumanMob firstSettler = (HumanMob)MobRegistry.getMob("human", level);
            if (farmerIsMale) {
                firstSettler.gender = HumanGender.MALE;
                firstSettler.setSettlerName(HumanMob.getRandomName(random, HumanMob.maleNames));
            } else {
                firstSettler.gender = HumanGender.FEMALE;
                firstSettler.setSettlerName(HumanMob.getRandomName(random, HumanMob.femaleNames));
            }
            firstSettler.customLook = true;
            firstSettler.randomizeLook(firstSettler.look, firstSettler.gender, random);
            firstSettler.setHome(levelX, levelY);
            Point spawnLocation = Waystone.findTeleportLocation(level, levelX, levelY, firstSettler);
            level.entityManager.addMob(firstSettler, spawnLocation.x, spawnLocation.y);
            firstSettlerRef.set(firstSettler);
            return (level1, presetX, presetY) -> firstSettler.remove();
        });
        this.addCustomApply(15, 1, 0, (level, levelX, levelY, dir, blackboard) -> {
            HumanMob secondSettler = (HumanMob)MobRegistry.getMob("human", level);
            if (farmerIsMale) {
                secondSettler.gender = HumanGender.FEMALE;
                secondSettler.setSettlerName(HumanMob.getRandomName(random, HumanMob.femaleNames));
            } else {
                secondSettler.gender = HumanGender.MALE;
                secondSettler.setSettlerName(HumanMob.getRandomName(random, HumanMob.maleNames));
            }
            secondSettler.customLook = true;
            secondSettler.randomizeLook(secondSettler.look, secondSettler.gender, random);
            secondSettler.setHome(levelX, levelY);
            Point spawnLocation = Waystone.findTeleportLocation(level, levelX, levelY, secondSettler);
            level.entityManager.addMob(secondSettler, spawnLocation.x, spawnLocation.y);
            secondSettlerRef.set(secondSettler);
            return (level1, presetX, presetY) -> secondSettler.remove();
        });
        this.addCustomApply(2, 8, 3, (level, levelX, levelY, dir, blackboard) -> {
            try {
                ObjectEntity objEnt = level.entityManager.getObjectEntity(levelX, levelY);
                if (objEnt instanceof SignObjectEntity) {
                    HumanMob firstSettler = (HumanMob)firstSettlerRef.get();
                    HumanMob secondSettler = (HumanMob)secondSettlerRef.get();
                    if (firstSettler != null && secondSettler != null) {
                        ((SignObjectEntity)objEnt).setText(firstSettler.getSettlerName() + "'s & " + secondSettler.getSettlerName() + "'s Oasis Inn");
                    }
                } else if (level.isServer()) {
                    throw new NullPointerException("Could not find a sign objectEntity for preset at " + levelX + ", " + levelY);
                }
            }
            catch (Exception e) {
                System.err.println(e.getMessage());
            }
            firstSettlerRef.set(null);
            secondSettlerRef.set(null);
            return null;
        });
    }
}

