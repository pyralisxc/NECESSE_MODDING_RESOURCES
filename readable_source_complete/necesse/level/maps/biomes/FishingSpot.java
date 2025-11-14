/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.biomes;

import necesse.inventory.item.baitItem.BaitItem;
import necesse.inventory.item.placeableItem.fishingRodItem.FishingRodItem;
import necesse.level.maps.LevelTile;
import necesse.level.maps.biomes.Biome;

public class FishingSpot {
    public final LevelTile tile;
    public final FishingRodItem fishingRod;
    public final BaitItem bait;

    public FishingSpot(LevelTile tile, FishingRodItem fishingRod, BaitItem bait) {
        this.tile = tile;
        this.fishingRod = fishingRod;
        this.bait = bait;
    }

    public Biome getBiome() {
        return this.tile.level.getBiome(this.tile.tileX, this.tile.tileY);
    }
}

