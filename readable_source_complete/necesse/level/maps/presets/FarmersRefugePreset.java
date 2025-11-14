/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets;

import java.awt.Point;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.friendly.human.humanShop.FarmerHumanMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.SignObjectEntity;
import necesse.gfx.HumanGender;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.presets.PaintingSelectionTable;
import necesse.level.maps.levelData.settlementData.Waystone;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.set.BushSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.TreeSet;
import necesse.level.maps.presets.set.WallSet;

public class FarmersRefugePreset
extends Preset {
    public FarmersRefugePreset(GameRandom random, FurnitureSet furnitureSet, WallSet wallSet, TreeSet treeSet, BushSet bushSet) {
        super("PRESET = {\n\twidth = 25,\n\theight = 20,\n\ttileIDs = [20, stonetiledfloor, 37, graveltile, 23, sandstonefloor, 11, farmland, 12, woodfloor, 15, woodpathtile],\n\ttiles = [-1, -1, -1, -1, -1, 12, 12, 15, 12, 15, 12, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 20, 20, 20, 12, 12, 15, 12, 15, 12, 15, 15, 15, 15, 15, 15, 15, -1, -1, -1, -1, -1, -1, -1, -1, 20, 20, 20, 20, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, -1, -1, 20, 20, 20, 20, -1, -1, 20, 20, 20, 20, 15, 12, 15, 12, 15, 12, 15, 12, 15, 12, 15, 12, 12, -1, -1, 20, 20, 20, 20, -1, -1, 20, 20, 20, 20, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 37, -1, 20, 20, 20, 20, 20, -1, -1, 20, 20, 20, 15, 15, 15, 12, 15, 12, 15, 12, 15, 12, 15, 12, 12, 37, -1, 20, 20, 20, 20, 20, -1, -1, -1, -1, -1, -1, 12, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 12, 37, 37, 20, 20, 20, 20, 20, -1, -1, -1, -1, -1, -1, -1, -1, 23, -1, 12, 15, 12, 15, -1, 15, 12, -1, 37, 37, -1, 37, 37, 37, -1, -1, -1, -1, -1, -1, -1, -1, -1, 23, -1, -1, -1, -1, -1, -1, -1, -1, 37, 37, 37, 37, 37, 37, -1, -1, -1, -1, -1, 37, 37, -1, -1, -1, -1, 23, -1, -1, -1, -1, -1, -1, 37, 37, 37, 37, 37, 37, 37, -1, -1, -1, -1, -1, 37, 37, -1, -1, -1, -1, -1, 23, -1, -1, -1, 37, 37, 37, 37, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 37, -1, -1, -1, -1, -1, -1, 23, 37, -1, 37, 37, 37, 37, -1, 11, 11, 11, 11, 11, 11, -1, -1, -1, -1, 37, -1, -1, -1, -1, -1, -1, 23, 37, 37, 37, -1, -1, -1, -1, 11, 11, 11, 11, 11, 11, -1, -1, -1, -1, 37, -1, -1, -1, -1, -1, -1, -1, 23, 37, 37, -1, -1, -1, -1, 11, 11, 11, 11, 11, 11, -1, -1, -1, -1, 37, 37, 37, -1, -1, -1, -1, -1, 23, 37, -1, -1, -1, -1, -1, 11, 11, 11, 11, 11, 11, -1, -1, -1, -1, -1, -1, 37, 37, 37, 37, 37, -1, 23, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 37, 37, 23, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 23, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 23, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 2, oaktree, 518, decorativepot1, 273, paintinglargeship, 274, paintinglargeship2, 282, sign, 541, woolcarpet, 33, blueberrybush, 292, gravestone2, 43, carpentersbench, 44, carpentersbench2, 63, woodwall, 64, wooddoor, 193, stonefence, 194, stonefencegate, 322, oakchest, 195, stonefencegateopen, 68, woodwindow, 325, oakdesk, 326, oakmodulartable, 711, cookingstation, 327, oakchair, 712, cookingstation2, 328, oakbench, 713, compostbin, 329, oakbench2, 714, grainmill, 331, oakcabinet, 715, grainmill2, 716, grainmill3, 717, grainmill4, 334, oakdoublebed, 335, oakdoublebedfoot1, 336, oakdoublebed2, 337, oakdoublebedfoot2, 338, oakdresser, 339, oakclock, 340, oakcandelabra, 341, oakdisplay, 342, oakbathtub, 343, oakbathtub2, 344, oaktoilet, 218, incinerator, 603, wheatseed4, 220, walltorch, 875, purpleflowerpatch, 876, blueflowerpatch, 877, whiteflowerpatch, 245, walllantern],\n\tobjects = [-1, -1, -1, -1, -1, 63, 63, 68, 63, 63, 63, 63, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 63, 68, 63, 63, 711, 712, 326, 326, 331, 63, 63, 63, 63, 63, 63, 63, -1, -1, -1, -1, -1, -1, -1, -1, 63, 63, 325, 245, 63, 0, 0, 0, 0, 0, 341, 63, 326, 334, 336, 326, 63, -1, -1, 63, 63, 63, 63, -1, -1, 68, 344, 541, 541, 64, 0, 0, 0, 0, 0, 0, 63, 0, 335, 337, 0, 63, 193, 194, 63, 43, 44, 63, 63, -1, 63, 63, 342, 343, 63, 340, 0, 0, 0, 0, 0, 339, 0, 0, 0, 0, 68, 713, 0, 68, 0, 0, 331, 68, -1, -1, 63, 68, 63, 63, 63, 0, 0, 0, 0, 0, 0, 0, 0, 0, 338, 63, 0, 877, 63, 218, 0, 327, 63, -1, -1, -1, -1, -1, 292, 63, 63, 64, 63, 63, 0, 327, 326, 327, 340, 63, 63, 0, 0, 63, 63, 64, 63, 63, -1, -1, 193, 193, 193, 63, 63, 0, 0, 0, 63, 63, 68, 63, 68, 63, 63, 0, 0, 0, 716, 714, 0, 0, 193, -1, -1, 193, 2, 875, 875, 0, 0, 0, 0, 0, 0, 877, 0, 0, 0, 322, 0, 0, 0, 717, 715, 0, 876, 193, 193, 193, 193, 328, 329, 875, 2, 875, 0, 0, 0, 877, 0, 0, 2, 877, 0, 0, 0, 0, 0, 0, 0, 876, 193, 193, 2, 875, 0, 0, 0, 33, 0, 875, 0, 0, 0, 0, 877, 877, 877, 0, 0, 0, 0, 0, 0, 0, 0, 193, 193, 0, 875, 0, 0, 0, 0, 33, 875, 0, 0, 0, 0, 0, 0, 0, 0, 876, 603, 603, 603, 603, 603, 603, 193, 193, 33, 0, 0, 0, 0, 33, 875, 0, 875, 0, 0, 877, 0, 0, 0, 0, 0, 603, 603, 603, 603, 603, 603, 193, 193, 0, 33, 0, 0, 2, 875, 0, 875, 875, 0, 0, 0, 0, 0, 876, 0, 0, 603, 603, 603, 603, 603, 603, 193, 193, 0, 0, 0, 0, 0, 875, 875, 0, 2, 0, 0, 0, 0, 876, 0, 876, 0, 603, 603, 603, 603, 603, 603, 193, 193, 2, 875, 0, 0, 0, 0, 0, 0, 0, 875, 0, 0, 876, 0, 0, 0, 33, 193, 193, 193, 193, 193, 193, 193, 193, 875, 875, 875, 2, 875, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 33, 193, -1, -1, -1, -1, -1, -1, 193, 193, 193, 193, 193, 193, 193, 0, 0, 0, 875, 0, 0, 876, 0, 0, 193, 193, 193, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 193, 2, 0, 875, 0, 0, 0, 282, 876, 2, 193, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 193, 193, 193, 193, 193, 195, 193, 193, 193, 193, 193, -1, -1, -1, -1, -1, -1, -1, -1],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 3, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 3, 1, 3, 3, 0, 1, 1, 2, 2, 2, 1, 0, 0, 3, 3, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 0, 0, 3, 3, 0, 0, 2, 0, 2, 2, 2, 2, 1, 0, 1, 1, 1, 1, 1, 0, 0, 3, 1, 3, 3, 3, 0, 0, 3, 3, 3, 0, 0, 0, 2, 2, 1, 1, 1, 0, 0, 1, 1, 1, 1, 0, 1, 1, 0, 0, 0, 2, 0, 2, 2, 2, 0, 2, 0, 0, 0, 0, 1, 2, 0, 0, 0, 0, 2, 0, 0, 0, 3, 2, 3, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 3, 2, 0, 0, 2, 3, 0, 3, 0, 0, 0, 3, 3, 3, 3, 3, 3, 0, 3, 1, 0, 1, 2, 3, 2, 1, 1, 0, 0, 2, 2, 0, 2, 0, 0, 0, 3, 3, 3, 3, 3, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 0, 0, 3, 0, 0, 3, 3, 2, 2, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 3, 1, 1, 2, 3, 0, 0, 0, 0, 2, 0, 0, 3, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 2, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 1, 0, 0, 0, 1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 0, 2, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 2, 2, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 1, 0, 0, 0, 3, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 2, 0, 0, 0, 2, 2, 1, 1, 1, 1, 1, 1, 3, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 1, 1, 1, 1, 1, 1, 3, 3, 2, 2, 3, 3, 3, 0, 0, 0, 2, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 3, 2, 0, 2, 0, 0, 0, 2, 2, 2, 0, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 3, 3, 3, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1],\n\ttileObjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 877, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttileRotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\twallDecorObjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 274, 273, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 220, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\twallDecorRotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttableDecorObjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 518, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttableDecorRotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 2, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0]\n}");
        FurnitureSet.oak.replaceWith(furnitureSet, this);
        WallSet.wood.replaceWith(wallSet, this);
        TreeSet.oak.replaceWith(treeSet, this);
        BushSet.blueberry.replaceWith(bushSet, this);
        PresetUtils.applyRandomCarpetToSelection(this, 8, 3, 3, 2, 0, random);
        PresetUtils.applyRandomCarpetToSelection(this, 13, 2, 4, 2, 0, random);
        PresetUtils.applyRandomPainting(this, 2, 3, 2, random, PaintingSelectionTable.uncommonPaintings);
        long flowerSeed = random.nextLong();
        PresetUtils.applyRandomPot(this, 13, 2, new GameRandom(flowerSeed));
        PresetUtils.applyRandomPot(this, 16, 2, new GameRandom(flowerSeed));
        PresetUtils.applyRandomPot(this, 9, 1, random);
        PresetUtils.applyRandomPot(this, 7, 5, random);
        this.addInventory(new LootTable(LootTablePresets.rollingPinDisplayStand), random, 11, 2, new Object[0]);
        this.addInventory(new LootTable(LootTablePresets.farmerChest), random, 16, 8, new Object[0]);
        this.addInventory(new LootTable(new LootItem("farmerhat"), new LootItem("farmershirt"), new LootItem("farmershoes")), random, 16, 5, new Object[0]);
        this.addInventory(new LootTable(new LootItem("copperaxe"), LootTablePresets.carpenterChest), random, 23, 4, new Object[0]);
        this.addInventory(new LootTable(new LootItem("sickle"), LootItem.offset("coin", 20, 12)), random, 10, 1, new Object[0]);
        this.addInventory(new LootTable(LootItem.offset("wheat", 40, 8)), random, 21, 7, new Object[0]);
        AtomicReference farmerRef = new AtomicReference();
        AtomicReference humanRef = new AtomicReference();
        boolean farmerIsMale = random.getChance(0.5f);
        this.addCustomApply(14, 2, 0, (level, levelX, levelY, dir, blackboard) -> {
            FarmerHumanMob farmer = (FarmerHumanMob)MobRegistry.getMob("farmerhuman", level);
            if (farmerIsMale) {
                farmer.gender = HumanGender.MALE;
                farmer.setSettlerName(HumanMob.getRandomName(random, HumanMob.maleNames));
            } else {
                farmer.gender = HumanGender.FEMALE;
                farmer.setSettlerName(HumanMob.getRandomName(random, HumanMob.femaleNames));
            }
            farmer.customLook = true;
            farmer.randomizeLook(farmer.look, farmer.gender, random);
            farmer.setHome(levelX, levelY);
            Point spawnLocation = Waystone.findTeleportLocation(level, levelX, levelY, farmer);
            level.entityManager.addMob(farmer, spawnLocation.x, spawnLocation.y);
            farmerRef.set(farmer);
            return (level1, presetX, presetY) -> farmer.remove();
        });
        this.addCustomApply(15, 2, 0, (level, levelX, levelY, dir, blackboard) -> {
            HumanMob human = (HumanMob)MobRegistry.getMob("human", level);
            if (farmerIsMale) {
                human.gender = HumanGender.FEMALE;
                human.setSettlerName(HumanMob.getRandomName(random, HumanMob.femaleNames));
            } else {
                human.gender = HumanGender.MALE;
                human.setSettlerName(HumanMob.getRandomName(random, HumanMob.maleNames));
            }
            human.customLook = true;
            human.randomizeLook(human.look, human.gender, random);
            human.setHome(levelX, levelY);
            Point spawnLocation = Waystone.findTeleportLocation(level, levelX, levelY, human);
            level.entityManager.addMob(human, spawnLocation.x, spawnLocation.y);
            humanRef.set(human);
            return (level1, presetX, presetY) -> human.remove();
        });
        this.addCustomApply(13, 18, 0, (level, levelX, levelY, dir, blackboard) -> {
            try {
                ObjectEntity objEnt = level.entityManager.getObjectEntity(levelX, levelY);
                if (objEnt instanceof SignObjectEntity) {
                    FarmerHumanMob farmerHumanMob = (FarmerHumanMob)farmerRef.get();
                    HumanMob humanMob = (HumanMob)humanRef.get();
                    if (farmerHumanMob != null && humanMob != null) {
                        ((SignObjectEntity)objEnt).setText(farmerHumanMob.getSettlerName() + " & " + humanMob.getSettlerName() + "'s Cottage");
                    }
                } else if (level.isServer()) {
                    throw new NullPointerException("Could not find a sign objectEntity for preset at " + levelX + ", " + levelY);
                }
            }
            catch (Exception e) {
                System.err.println(e.getMessage());
            }
            farmerRef.set(null);
            humanRef.set(null);
            return null;
        });
        this.addCanApplyRectPredicate(-1, -1, this.width + 2, this.height + 2, 0, (level, levelStartX, levelStartY, levelEndX, levelEndY, dir) -> {
            for (int x = levelStartX; x <= levelEndX; ++x) {
                for (int y = levelStartY; y <= levelEndY; ++y) {
                    if (!level.isLiquidTile(x, y)) continue;
                    return false;
                }
            }
            return true;
        });
        PresetUtils.addShoreTiles(this, -1, -1, this.width + 2, this.height + 2);
    }
}

