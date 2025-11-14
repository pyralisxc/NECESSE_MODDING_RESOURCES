/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.set.FlowerPatchSet;

public class TrainingDummyPicnicPreset
extends Preset {
    public TrainingDummyPicnicPreset(Biome biome, LevelIdentifier levelIdentifier, GameRandom random, FlowerPatchSet[] flowers) {
        super("PRESET = {\n\twidth = 6,\n\theight = 9,\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 390, sign, 393, trainingdummy, 1196, whiteflowerpatch, 435, bigtent, 436, bigtent2, 437, bigtent3, 438, bigtent4, 439, picnicblanket, 440, picnicblanket2, 312, candle, 441, picnicblanket3, 282, merchantsbackpack, 1180, grass],\n\tobjects = [1196, -1, 1196, -1, -1, -1, -1, 1196, 1196, 1180, 1196, 1196, -1, 1196, 435, 436, 1196, -1, 1196, 282, 437, 438, 390, 1196, 0, 1180, 1180, 1196, 1196, 0, 0, 1196, 393, 0, 393, 1196, 1196, 1180, 441, 440, 439, 1180, 1196, 1196, 312, 1180, 1180, 1196, -1, 0, 1196, 1196, 1196, -1],\n\trotations = [1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 2, 0, 3, 2, 2, 0, 0, 1, 2, 2, 2, 2, 1, 3, 0, 0, 1, 0, 1, 3, 1, 2, 0, 2, 1, 1, 0, 2, 2, 2, 0, 1, 1, 3, 0, 0, 1, 0, 3, 0, 0, 2, 0],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true,\n\tclearOtherWires = false\n}");
        FlowerPatchSet.white.replaceWithRandomly(random, flowers, this);
        this.addInventory(new LootTable(LootTablePresets.deadMerchantsLoot), random, 1, 3, new Object[0]);
        String text = Localization.translate("biome", "trainingdummypresetpoem");
        this.addSign(text, 4, 3);
        PresetUtils.addShoreTiles(this, -1, -1, this.width + 2, this.height + 2);
    }
}

