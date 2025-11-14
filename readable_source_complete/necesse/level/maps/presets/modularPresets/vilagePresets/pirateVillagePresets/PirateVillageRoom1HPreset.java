/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.vilagePresets.pirateVillagePresets;

import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.maps.presets.modularPresets.vilagePresets.pirateVillagePresets.PirateVillagePreset;

public class PirateVillageRoom1HPreset
extends PirateVillagePreset {
    public PirateVillageRoom1HPreset(GameRandom random, AtomicInteger chestRotation) {
        super(4, 3, true, random);
        this.applyScript("PRESET = {\n\twidth = 12,\n\theight = 9,\n\ttileIDs = [12, woodfloor],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 12, 12, 12, 12, -1, -1, -1, -1, -1, -1, 12, 12, 12, 12, 12, 12, 12, 12, -1, -1, -1, -1, 12, 12, 12, 12, 12, 12, 12, 12, -1, -1, -1, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, -1, -1, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, -1, -1, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, -1, -1, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, -1, -1, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, -1],\n\tobjectIDs = [0, air, 322, sprucebookshelf, 195, goldlamp, 188, barrel, 460, leathercarpet, 317, sprucedesk, 63, woodwall],\n\tobjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 63, 63, 63, 63, 0, 0, 0, 0, 0, 0, 63, 63, 63, 322, 322, 63, 63, 63, 0, 0, 0, 0, 63, 317, 195, 0, 0, 188, 0, 63, 0, 0, 0, 63, 63, 0, 0, 0, 0, 0, 0, 63, 63, 0, 0, 63, 0, 0, 0, 460, 460, 0, 0, 0, 63, 0, 0, 63, 0, 0, 0, 460, 460, 0, 0, 0, 63, 0, 0, 63, 0, 0, 0, 0, 0, 0, 0, 0, 63, 0, 0, 63, 63, 0, 0, 0, 0, 0, 0, 63, 63, 0],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 1, 0, 0, 0],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true\n}");
        this.applyRandomCoinStack(2, 5, random);
        this.applyRandomCoinStack(8, 3, random);
        this.applyRandomCoinStack(9, 5, random);
        this.addInventory(LootTablePresets.pirateChest, random, 7, 3, chestRotation);
        this.open(1, 2, 2);
        this.open(2, 2, 2);
    }
}

