/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets;

import java.awt.Point;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.localization.Localization;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.humanShop.BlacksmithHumanMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.SignObjectEntity;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.presets.PaintingSelectionTable;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.levelData.settlementData.Waystone;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.set.WallSet;

public class UndergroundBlacksmithShopPreset
extends Preset {
    /*
     * WARNING - void declaration
     */
    public UndergroundBlacksmithShopPreset(GameRandom random, WallSet wallSet, Biome biome) {
        super("PRESET = {\n\twidth = 21,\n\theight = 21,\n\ttileIDs = [24, stonetiledfloor, 41, snowstonepathtile, 60, basaltpathtile, 14, woodfloor],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, -1, -1, -1, -1, -1, -1, 24, 41, 41, 41, 41, 41, 24, 24, 24, 24, 24, 24, 24, 24, 24, -1, -1, -1, -1, -1, -1, 24, 41, 41, 41, 41, 41, 24, 24, 24, 24, 24, 24, 24, 24, 24, -1, -1, -1, -1, -1, -1, 24, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 24, 24, -1, -1, -1, -1, -1, -1, 14, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 14, -1, -1, -1, -1, -1, -1, 14, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 14, -1, -1, -1, -1, -1, -1, 14, 60, 14, 14, 14, 14, 60, 60, 60, 60, 60, 60, 60, 60, 14, -1, -1, -1, -1, -1, -1, 14, 60, 60, 60, 60, 14, 60, 60, 60, 60, 14, 14, 14, 60, 14, -1, -1, -1, -1, -1, -1, 14, 60, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 60, 14, -1, -1, -1, -1, -1, -1, 14, 60, 60, 60, 60, 60, 60, 14, 60, 60, 60, 60, 60, 60, 14, -1, -1, -1, -1, -1, -1, 14, 60, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 60, 14, -1, -1, -1, -1, -1, -1, 14, 60, 60, 14, 60, 60, 60, 14, 60, 60, 60, 14, 60, 60, 14, -1, -1, -1, -1, -1, -1, 14, 60, 14, 14, 60, 14, 14, 14, 14, 14, 60, 14, 14, 60, 14, -1, -1, -1, -1, -1, -1, 14, 60, 60, 60, 60, 60, -1, -1, -1, 60, 60, 60, 60, 60, 14, -1, -1, -1, -1, -1, -1, 14, 14, 14, 14, 14, 14, -1, -1, -1, 14, 14, 14, 14, 14, 14, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 836, velourcarpet, 581, maplebathtub, 325, paintingbroken, 582, maplebathtub2, 390, sign, 583, mapletoilet, 519, willowmodulartable, 520, willowchair, 392, armorstand, 523, willowbookshelf, 332, paintingdagger, 76, tungstenworkstation, 525, willowbed, 77, tungstenworkstation2, 526, willowbed2, 78, tungstenanvil, 719, deadwooddisplay, 783, yellowbook, 658, dryadchair, 724, spidercastlewallcandle, 278, barrel, 284, torch, 285, walltorch, 868, graniteflametrap, 103, willowwall, 104, willowdoor, 108, willowwindow, 492, pinechest, 813, oldsoup, 496, pinemodulartable, 752, pottedplant5, 434, bloodstains, 116, bamboodoor, 309, lantern, 245, cryptfence, 246, cryptfencegate, 56, forge, 312, candle, 763, dogplush],\n\tobjects = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, -1, -1, -1, -1, -1, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, 103, 103, 103, 108, 103, 103, 103, 103, 103, 103, 103, 108, 103, 103, 103, -1, -1, -1, -1, -1, -1, 103, 309, 583, 0, 0, 104, 285, 332, 0, 523, 0, 0, 0, 285, 103, -1, -1, -1, -1, -1, -1, 103, 581, 582, 0, 519, 103, 525, 526, 0, 0, 520, 519, 103, 0, 103, -1, -1, -1, -1, -1, -1, 103, 103, 103, 103, 103, 103, 103, 103, 868, 868, 868, 103, 103, 0, 103, -1, -1, -1, -1, -1, -1, 103, 278, 0, 719, 0, 325, 0, 245, 492, 56, 76, 77, 0, 0, 103, -1, -1, -1, -1, -1, -1, 103, 0, 434, 0, 0, 0, 0, 246, 0, 0, 0, 0, 0, 658, 103, -1, -1, -1, -1, -1, -1, 103, 719, 0, 0, 434, 0, 0, 245, 245, 245, 496, 496, 496, 78, 103, -1, -1, -1, -1, -1, -1, 103, 724, 0, 434, 0, 0, 752, 392, 392, 392, 836, 836, 836, 752, 103, -1, -1, -1, -1, 0, 0, 103, 719, 0, 0, 0, 0, 0, 0, 0, 0, 836, 836, 836, 719, 103, 0, -1, -1, -1, -1, 0, 108, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 108, 0, 0, -1, -1, -1, -1, 103, 719, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 719, 103, 0, 0, -1, -1, -1, -1, 103, 724, 0, 0, 0, 0, 0, 0, 0, 284, 0, 0, 0, 724, 103, 0, -1, -1, -1, -1, -1, 103, 719, 0, 0, 0, 103, 103, 116, 103, 103, 0, 0, 0, 719, 103, -1, -1, -1, -1, -1, -1, 103, 0, 0, 0, 0, 103, 752, 0, 390, 103, 0, 0, 0, 0, 103, -1, -1, -1, -1, -1, -1, 103, 103, 108, 108, 103, 103, 0, 0, 0, 103, 103, 108, 108, 103, 103, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 724, 0, 0, 0, 724, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, -1, -1, 0, 0, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, -1, -1],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3, 0, 3, 3, 3, 3, 3, 3, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 3, 0, 1, 0, 0, 1, 2, 2, 0, 2, 1, 0, 0, 2, 0, 1, 1, 1, 0, 0, 0, 3, 0, 0, 0, 1, 0, 1, 1, 3, 0, 1, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 2, 2, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 2, 0, 0, 2, 0, 0, 0, 0, 1, 2, 0, 3, 0, 1, 1, 1, 0, 0, 0, 0, 0, 1, 3, 2, 3, 3, 2, 3, 3, 1, 1, 1, 3, 0, 1, 1, 1, 1, 1, 1, 0, 1, 3, 2, 0, 3, 2, 2, 2, 2, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 3, 0, 3, 3, 0, 3, 2, 1, 1, 1, 2, 0, 0, 1, 1, 1, 1, 0, 0, 0, 3, 0, 0, 1, 0, 0, 0, 3, 3, 2, 3, 1, 0, 0, 0, 1, 1, 1, 1, 0, 0, 2, 2, 1, 3, 3, 0, 0, 3, 1, 2, 2, 2, 0, 0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 3, 0, 0, 1, 1, 1, 0, 0, 0, 2, 0, 0, 3, 1, 1, 2, 1, 1, 2, 0, 0, 2, 0, 1, 1, 1, 1, 0, 0, 0, 3, 3, 3, 2, 1, 2, 1, 2, 1, 2, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 2, 0, 1, 1, 0, 0, 2, 2, 0, 0, 0, 1, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 3, 0, 0, 0, 0, 0, 1, 1, 1, 2, 1, 0, 0, 0, 0, 0, 3, 3, 3, 3, 3, 3, 2, 2, 2, 0, 0, 0, 0, 2, 2],\n\ttileObjectsClear = true,\n\twallDecorObjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 724, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\twallDecorRotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttableDecorObjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 783, 0, 0, 0, 0, 0, 0, 813, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 284, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 763, 0, 312, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttableDecorRotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\tclearOtherWires = false\n}");
        WallSet.willow.replaceWith(wallSet, this);
        PresetUtils.applyRandomCarpetToSelection(this, 13, 10, 3, 2, 0, random);
        PresetUtils.applyRandomPainting(this, 2, 3, 2, random, PaintingSelectionTable.uncommonPaintings);
        ArrayList<Point> pedestalLocations = new ArrayList<Point>();
        pedestalLocations.add(new Point(6, 7));
        pedestalLocations.add(new Point(4, 9));
        pedestalLocations.add(new Point(4, 11));
        pedestalLocations.add(new Point(4, 13));
        pedestalLocations.add(new Point(4, 15));
        pedestalLocations.add(new Point(16, 11));
        pedestalLocations.add(new Point(16, 13));
        pedestalLocations.add(new Point(16, 15));
        for (Point point : pedestalLocations) {
            this.addInventory(new LootTable(LootTablePresets.rollingPinDisplayStand), random, point.x, point.y, new Object[0]);
        }
        ArrayList<Point> armorStandLocations = new ArrayList<Point>();
        armorStandLocations.add(new Point(10, 10));
        armorStandLocations.add(new Point(11, 10));
        armorStandLocations.add(new Point(12, 10));
        for (Point armorStandLocation : armorStandLocations) {
            this.addInventory(new LootTable(new LootItem("farmerhat"), new LootItem("farmershirt"), new LootItem("farmershoes")), random, armorStandLocation.x, armorStandLocation.y, new Object[0]);
        }
        this.addInventory(new LootTable(LootTablePresets.carpenterChest), random, 11, 7, new Object[0]);
        if (biome.equals(BiomeRegistry.SWAMP) || biome.equals(BiomeRegistry.DESERT)) {
            String string = "ancientskeleton";
        } else {
            String string = "skeleton";
        }
        ArrayList<Point> enemySpawnLocations = new ArrayList<Point>();
        enemySpawnLocations.add(new Point(8, 8));
        enemySpawnLocations.add(new Point(8, 11));
        enemySpawnLocations.add(new Point(14, 11));
        enemySpawnLocations.add(new Point(14, 15));
        enemySpawnLocations.add(new Point(5, 14));
        enemySpawnLocations.add(new Point(6, 4));
        for (Point enemySpawnLocation : enemySpawnLocations) {
            void var6_10;
            this.addCustomApply(enemySpawnLocation.x, enemySpawnLocation.y, 0, (arg_0, arg_1, arg_2, arg_3, arg_4) -> UndergroundBlacksmithShopPreset.lambda$new$1((String)var6_10, arg_0, arg_1, arg_2, arg_3, arg_4));
        }
        AtomicReference blacksmithRef = new AtomicReference();
        this.addCustomApply(6, 9, 0, (level, levelX, levelY, dir, blackboard) -> {
            BlacksmithHumanMob trappedBlacksmith = (BlacksmithHumanMob)MobRegistry.getMob("blacksmithhuman", level);
            trappedBlacksmith.setHome(levelX, levelY);
            trappedBlacksmith.setTrapped();
            trappedBlacksmith.canDespawn = false;
            Point spawnLocation = Waystone.findTeleportLocation(level, levelX, levelY, trappedBlacksmith);
            level.entityManager.addMob(trappedBlacksmith, spawnLocation.x, spawnLocation.y);
            blacksmithRef.set(trappedBlacksmith);
            return (level1, presetX, presetY) -> trappedBlacksmith.remove();
        });
        this.addCustomApply(11, 16, 0, (level, levelX, levelY, dir, blackboard) -> {
            try {
                ObjectEntity objEnt = level.entityManager.getObjectEntity(levelX, levelY);
                if (objEnt instanceof SignObjectEntity) {
                    BlacksmithHumanMob blacksmithMob = (BlacksmithHumanMob)blacksmithRef.get();
                    if (blacksmithMob != null) {
                        String signText = Localization.translate("biome", "undergroundblacksmithsignpreset", "settlername", blacksmithMob.getSettlerName());
                        ((SignObjectEntity)objEnt).setText(signText);
                    }
                } else if (level.isServer()) {
                    throw new NullPointerException("Could not find a sign objectEntity for preset at " + levelX + ", " + levelY);
                }
            }
            catch (Exception e) {
                System.err.println(e.getMessage());
            }
            blacksmithRef.set(null);
            return null;
        });
    }
}

