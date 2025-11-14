/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.biomes;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameRandom;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.baitItem.BaitItem;
import necesse.inventory.item.placeableItem.fishingRodItem.FishingRodItem;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.FishingLootTable;
import necesse.level.maps.biomes.FishingSpot;

public class FishLootOptions {
    private final FishingLootTable lootTable;
    private final int tickets;
    private Predicate<FishingSpot> isValid = spot -> true;

    public FishLootOptions(FishingLootTable lootTable, int tickets) {
        this.lootTable = lootTable;
        this.tickets = tickets;
    }

    public FishLootOptions filter(Predicate<FishingSpot> filter) {
        this.isValid = this.isValid.and(filter);
        return this;
    }

    public FishLootOptions onlyTile(String tileStringID) {
        this.isValid = this.isValid.and(spot -> spot.tile.tile.getStringID().equals(tileStringID));
        return this;
    }

    public FishLootOptions onlyWater() {
        return this.onlyTile("watertile");
    }

    public FishLootOptions onlyLava() {
        return this.onlyTile("lavatile");
    }

    @SafeVarargs
    public final FishLootOptions onlyBiomes(Class<? extends Biome> ... biomeClasses) {
        return this.filter(spot -> Arrays.stream(biomeClasses).anyMatch(c -> c.isInstance(spot.getBiome())));
    }

    public FishLootOptions filterBait(Predicate<BaitItem> filter) {
        return this.filter(spot -> filter.test(spot.bait));
    }

    public FishLootOptions filterFishingRod(Predicate<FishingRodItem> filter) {
        return this.filter(spot -> filter.test(spot.fishingRod));
    }

    public FishLootOptions onlySaltWater() {
        return this.onlyWater().filter(spot -> spot.tile.level.liquidManager.isSaltWater(spot.tile.tileX, spot.tile.tileY));
    }

    public FishLootOptions onlyFreshWater() {
        return this.onlyWater().filter(spot -> spot.tile.level.liquidManager.isFreshWater(spot.tile.tileX, spot.tile.tileY));
    }

    public FishLootOptions minDepth(int depth) {
        this.isValid = this.isValid.and(spot -> spot.tile.level.liquidManager.getHeight(spot.tile.tileX, spot.tile.tileY) <= -depth);
        return this;
    }

    public FishLootOptions maxDepth(int depth) {
        this.isValid = this.isValid.and(spot -> spot.tile.level.liquidManager.getHeight(spot.tile.tileX, spot.tile.tileY) >= -depth);
        return this;
    }

    public FishingLootTable end(BiFunction<FishingSpot, GameRandom, InventoryItem> itemProducer) {
        return this.lootTable.add(this.tickets, this.isValid, itemProducer);
    }

    public FishingLootTable end(String itemStringID) {
        return this.end((FishingSpot spot, GameRandom random) -> ItemRegistry.getItem(itemStringID).getDefaultLootItem((GameRandom)random, 1));
    }
}

