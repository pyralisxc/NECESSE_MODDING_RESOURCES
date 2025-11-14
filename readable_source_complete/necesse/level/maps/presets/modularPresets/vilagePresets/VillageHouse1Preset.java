/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.vilagePresets;

import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.presets.PaintingSelectionTable;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.modularPresets.vilagePresets.VillagePreset;

public class VillageHouse1Preset
extends VillagePreset {
    public VillageHouse1Preset(GameRandom random) {
        super(4, 3, false, random);
        this.applyScript("PRESET = {\n\twidth = 12,\n\theight = 9,\n\ttileIDs = [20, stonepathtile, 12, woodfloor],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, -1, -1, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, -1, -1, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, -1, -1, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, -1, -1, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, -1, -1, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, -1, -1, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, -1, -1, -1, -1, -1, -1, -1, 20, 20, 20, -1, -1, -1],\n\tobjectIDs = [0, air, 257, paintingmouse, 357, sprucecandelabra, 359, sprucebathtub, 360, sprucebathtub2, 680, firemone, 361, sprucetoilet, 524, pottedflower5, 81, stonewall, 242, wallcandle, 82, stonedoor, 340, sprucedinnertable, 341, sprucedinnertable2, 86, stonewindow, 344, sprucechair, 536, leathercarpet, 250, paintingavocado, 347, sprucebookshelf],\n\tobjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 0, 0, 81, 357, 250, 344, 344, 81, 361, 359, 360, 81, 0, 0, 81, 680, 0, 340, 341, 81, 242, 536, 536, 81, 0, 0, 86, 0, 0, 344, 344, 81, 81, 82, 81, 81, 0, 0, 81, 0, 0, 0, 0, 0, 0, 0, 357, 81, 0, 0, 81, 347, 347, 524, 0, 0, 0, 257, 0, 81, 0, 0, 81, 81, 81, 81, 86, 81, 82, 81, 81, 81, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 2, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 1, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true\n}");
        PresetUtils.applyRandomFlower(this, 2, 3, random);
        PresetUtils.applyRandomPot(this, 6, 4, random);
        PresetUtils.applyRandomPainting(this, 3, 2, 2, random, PaintingSelectionTable.commonPaintings);
        PresetUtils.applyRandomPainting(this, 8, 6, 0, random, PaintingSelectionTable.commonPaintings);
        PresetUtils.applyRandomCarpetToSelection(this, 8, 3, 2, 1, 0, random);
        this.open(2, 2, 2);
        this.addHumanMob(5, 5, "traderhuman");
        this.addHumanMob(7, 6, "guardhuman");
    }
}

