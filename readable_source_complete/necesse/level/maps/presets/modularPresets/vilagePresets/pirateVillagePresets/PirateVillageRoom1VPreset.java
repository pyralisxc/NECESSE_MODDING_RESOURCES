/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.vilagePresets.pirateVillagePresets;

import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.maps.presets.modularPresets.vilagePresets.pirateVillagePresets.PirateVillagePreset;

public class PirateVillageRoom1VPreset
extends PirateVillagePreset {
    public PirateVillageRoom1VPreset(GameRandom random, AtomicInteger chestRotation) {
        super(3, 4, true, random);
        this.applyScript("PRESET = {\n\twidth = 9,\n\theight = 12,\n\ttileIDs = [12, woodfloor],\n\ttiles = [-1, -1, -1, -1, -1, -1, -1, -1, -1, 12, 12, 12, 12, 12, -1, -1, -1, -1, 12, 12, 12, 12, 12, 12, 12, -1, -1, 12, 12, 12, 12, 12, 12, 12, -1, -1, 12, 12, 12, 12, 12, 12, 12, 12, -1, 12, 12, 12, 12, 12, 12, 12, 12, -1, 12, 12, 12, 12, 12, 12, 12, 12, -1, 12, 12, 12, 12, 12, 12, 12, 12, -1, 12, 12, 12, 12, 12, 12, 12, -1, -1, 12, 12, 12, 12, 12, 12, 12, -1, -1, 12, 12, 12, 12, 12, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1],\n\tobjectIDs = [0, air, 322, sprucebookshelf, 195, goldlamp, 460, leathercarpet, 188, barrel, 317, sprucedesk, 63, woodwall],\n\tobjects = [0, 0, 0, 0, 0, 0, 0, 0, 0, 63, 63, 63, 63, 63, 0, 0, 0, 0, 63, 322, 322, 322, 63, 63, 63, 0, 0, 0, 0, 0, 0, 0, 317, 63, 0, 0, 0, 0, 0, 0, 0, 195, 63, 63, 0, 0, 0, 0, 460, 460, 0, 0, 63, 0, 0, 0, 0, 460, 460, 0, 0, 63, 0, 0, 0, 0, 0, 0, 188, 63, 63, 0, 0, 0, 0, 0, 0, 0, 63, 0, 0, 63, 322, 322, 322, 63, 63, 63, 0, 0, 63, 63, 63, 63, 63, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\trotations = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 3, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],\n\ttileObjectsClear = true,\n\twallDecorObjectsClear = true,\n\ttableDecorObjectsClear = true\n}");
        this.applyRandomCoinStack(5, 8, random);
        this.applyRandomCoinStack(3, 3, random);
        this.applyRandomCoinStack(2, 7, random);
        this.addInventory(LootTablePresets.pirateChest, random, 5, 7, chestRotation);
        this.open(0, 1, 3);
        this.open(0, 2, 3);
    }
}

