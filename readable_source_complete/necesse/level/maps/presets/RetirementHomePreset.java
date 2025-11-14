/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets;

import java.util.concurrent.atomic.AtomicBoolean;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.HumanGender;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.set.CarpetSet;
import necesse.level.maps.presets.set.FloorSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.LargelPaintingSet;
import necesse.level.maps.presets.set.SmallPaintingSet;
import necesse.level.maps.presets.set.WallSet;

public class RetirementHomePreset
extends Preset {
    public RetirementHomePreset(GameRandom random, WallSet wall, WallSet door, CarpetSet carpet, FloorSet mainFloor, FloorSet bathroomFloor, FurnitureSet furniture, SmallPaintingSet kitchenPainting, SmallPaintingSet entrancePainting, SmallPaintingSet deskPainting, SmallPaintingSet chestPainting, LargelPaintingSet largePainting) {
        super("PRESET = {\n\twidth = 18,\n\theight = 10,\n\ttileIDs = [70, cryptpath, 71, spidercastlefloor, 73, spidercastlecarpet, 59, basaltfloor, 12, dungeonfloor],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 71, 71, 71, 71, 71, 71, 71, 71, 71, 71, 71, 71, 71, 71, -1, -1, -1, -1, 71, 71, 71, 71, 71, 71, 73, 73, 71, 71, 71, 71, 71, 71, -1, -1, -1, -1, 71, 71, 71, 71, 71, 71, 73, 73, 71, 71, 71, 71, 71, 71, -1, -1, -1, -1, 71, 71, 71, 71, 71, 71, 71, 71, 71, 71, 71, 12, 71, 71, -1, -1, -1, -1, 71, 71, 71, 71, 71, 71, 71, 71, 71, 71, 59, 59, 59, 59, -1, -1, -1, -1, 71, 71, 71, 71, 71, 71, 71, 71, 71, 59, 59, 59, 59, 59, -1, -1, -1, -1, 71, 71, 71, 71, 71, 71, 71, 71, 71, 59, 59, 59, 59, 59, -1, -1, -1, 71, 71, 71, 71, 71, 71, 71, 71, 71, 71, 59, 59, 59, 59, 59, 59, -1, -1, -1, -1, -1, 70, 70, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 833, purplecarpet, 451, oakchair, 772, dirtyplate, 775, cuttingboard, 200, deepsandstonedoor, 330, paintingrainsun, 588, birchmodulartable, 334, paintingmouse, 465, oakdisplay, 338, paintingstonecaveling, 723, spidercastlecandelabra, 340, paintingswampcaveling, 344, paintinglargeship, 345, paintinglargeship2, 988, firemone, 734, decorativepot1, 607, dungeonchest, 735, decorativepot2, 610, dungeondesk, 611, dungeonmodulartable, 612, dungeonchair, 613, dungeonbench, 614, dungeonbench2, 615, dungeonbookshelf, 616, dungeoncabinet, 169, deepstonewall, 619, dungeondoublebed, 620, dungeondoublebedfoot1, 621, dungeondoublebed2, 174, deepstonewindow, 622, dungeondoublebedfoot2, 623, dungeondresser, 1011, cookingpot, 627, dungeonbathtub, 308, oillantern, 628, dungeonbathtub2, 629, dungeontoilet, 565, maplemodulartable, 310, walllantern, 1336, swampcrate, 312, candle, 827, leathercarpet, 188, basaltdoor],\n\tobjects = [-1, 169, 169, 169, 169, 169, 169, 169, 169, 169, 169, 169, 169, 169, 169, 169, 169, -1, -1, 169, 623, 615, 615, 610, 169, 169, 588, 588, 169, 169, 616, 619, 621, 623, 169, -1, 0, 174, 833, 833, 833, 451, 723, 465, 0, 0, 607, 723, 0, 620, 622, 0, 174, 0, -1, 169, 833, 833, 833, 833, 0, 612, 0, 0, 612, 0, 0, 827, 827, 0, 169, -1, -1, 169, 169, 0, 0, 169, 0, 0, 614, 613, 0, 0, 169, 169, 200, 169, 169, -1, -1, 169, 988, 0, 0, 169, 169, 0, 0, 0, 0, 169, 169, 169, 0, 629, 169, -1, 0, 174, 614, 0, 0, 0, 338, 0, 0, 0, 0, 340, 1011, 169, 0, 0, 174, 0, -1, 169, 613, 310, 0, 0, 310, 612, 611, 611, 612, 308, 565, 169, 627, 628, 169, -1, -1, 169, 174, 169, 188, 188, 169, 174, 169, 169, 174, 169, 169, 169, 169, 169, 169, -1, -1, 1336, 0, 310, 0, 0, 310, 0, 735, 734, 0, -1, -1, -1, -1, -1, -1, -1],\n\trotations = [0, 2, 2, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 1, 3, 3, 3, 1, 0, 2, 2, 2, 2, 0, 0, 0, 2, 2, 2, 2, 0, 0, 1, 0, 0, 0, 2, 2, 2, 2, 0, 1, 0, 0, 2, 3, 1, 2, 0, 0, 1, 3, 3, 3, 0, 2, 2, 2, 3, 0, 0, 0, 2, 3, 0, 0, 2, 2, 1, 3, 3, 3, 1, 1, 0, 0, 0, 0, 0, 0, 2, 1, 0, 0, 2, 2, 1, 3, 3, 3, 2, 2, 3, 1, 3, 1, 0, 0, 3, 0, 0, 0, 2, 2, 3, 0, 3, 3, 2, 2, 3, 2, 2, 0, 0, 0, 2, 0, 0, 2, 0, 0, 1, 1, 1, 3, 3, 3, 3, 0, 0, 1, 0, 0, 3, 3, 3, 2, 2, 3, 3, 0, 0, 1, 2, 3, 3, 2, 2, 1, 0, 0, 3, 0, 2, 2, 2, 2, 0, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttileObjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 833, 833, 833, 833, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 833, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttileRotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\twallDecorObjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 330, 0, 0, 345, 344, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 334, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 310, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\twallDecorRotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 2, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttableDecorObjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 312, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 312, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 772, 0, 0, 775, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttableDecorRotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\tclearOtherWires = false\n}");
        WallSet.deepStone.replaceWith(wall, this);
        WallSet.basalt.replaceWith(door, this);
        CarpetSet.purple.replaceWith(carpet, this);
        FloorSet.spiderCastle.replaceWith(mainFloor, this);
        FloorSet.basalt.replaceWith(bathroomFloor, this);
        FurnitureSet.dungeon.replaceWith(furniture, this);
        SmallPaintingSet.rareSwampcaveling.replaceWith(kitchenPainting, this);
        SmallPaintingSet.rareStonecaveling.replaceWith(entrancePainting, this);
        SmallPaintingSet.commonRainsun.replaceWith(deskPainting, this);
        SmallPaintingSet.uncommonMouse.replaceWith(chestPainting, this);
        LargelPaintingSet.rareShip.replaceWith(largePainting, this);
        AtomicBoolean firstIsFemale = new AtomicBoolean(true);
        this.addHuman("human", 13, 1, human -> firstIsFemale.set(human.gender == HumanGender.FEMALE), random);
        this.addHuman("human", 14, 1, human -> {
            if (random.getChance(0.75)) {
                human.gender = firstIsFemale.get() ? HumanGender.MALE : HumanGender.FEMALE;
                human.customLook = true;
                human.setSettlerName(HumanMob.getRandomName(random, human.gender));
                human.randomizeLook(human.look, human.gender, random);
            }
        }, random);
        this.addInventory(new LootTable(random.getOneOf(LootItem.between("sapphire", 2, 5), LootItem.between("amethyst", 2, 5))), random, 7, 2, new Object[0]);
        this.addInventory(LootTablePresets.surfaceRuinsChest, random, 10, 2, new Object[0]);
        PresetUtils.addShoreTiles(this, 0, 0, this.width, this.height);
    }
}

