/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.biomes;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import necesse.engine.util.GameRandom;
import necesse.inventory.InventoryItem;
import necesse.level.maps.biomes.FishLootOptions;
import necesse.level.maps.biomes.FishingSpot;

public class FishingLootTable {
    private final LinkedList<FishingLootTable> includes = new LinkedList();
    private final LinkedList<FishLoot> table = new LinkedList();

    public FishingLootTable(FishingLootTable copy) {
        this.addAll(copy);
    }

    public FishingLootTable() {
    }

    public FishingLootTable addAll(FishingLootTable other) {
        this.includes.add(other);
        return this;
    }

    public FishingLootTable clear() {
        this.includes.clear();
        this.table.clear();
        return this;
    }

    public FishingLootTable add(int tickets, Predicate<FishingSpot> isValid, BiFunction<FishingSpot, GameRandom, InventoryItem> itemProducer) {
        this.table.add(new FishLoot(tickets, isValid, itemProducer));
        return this;
    }

    public FishLootOptions startCustom(int tickets) {
        return new FishLootOptions(this, tickets);
    }

    public FishingLootTable add(int tickets, Predicate<FishingSpot> isValid, String itemStringID) {
        return this.startCustom(tickets).filter(isValid).end(itemStringID);
    }

    public FishingLootTable addWater(int tickets, String itemStringID) {
        return this.startCustom(tickets).onlyWater().end(itemStringID);
    }

    public FishingLootTable addWater(int tickets, BiFunction<FishingSpot, GameRandom, InventoryItem> itemProducer) {
        return this.startCustom(tickets).onlyWater().end(itemProducer);
    }

    public FishingLootTable addFreshWater(int tickets, String itemStringID) {
        return this.startCustom(tickets).onlyFreshWater().end(itemStringID);
    }

    public FishingLootTable addSaltWater(int tickets, String itemStringID) {
        return this.startCustom(tickets).onlySaltWater().end(itemStringID);
    }

    public InventoryItem getRandomItem(FishingSpot spot, GameRandom random) {
        int maxTickets = 0;
        LinkedList<FishLoot> validLoot = new LinkedList<FishLoot>();
        if ((maxTickets = this.addValidLoot(validLoot, maxTickets, spot)) <= 0) {
            return null;
        }
        int ticket = random.nextInt(maxTickets);
        int ticketCounter = 0;
        for (FishLoot loot : validLoot) {
            if (ticket >= ticketCounter && ticket < ticketCounter + loot.tickets) {
                return loot.itemProducer.apply(spot, random);
            }
            ticketCounter += loot.tickets;
        }
        return null;
    }

    private int addValidLoot(LinkedList<FishLoot> validLoot, int maxTickets, FishingSpot spot) {
        for (FishingLootTable include : this.includes) {
            maxTickets = include.addValidLoot(validLoot, maxTickets, spot);
        }
        for (FishLoot loot : this.table) {
            if (!loot.isValid.test(spot)) continue;
            validLoot.add(loot);
            maxTickets += loot.tickets;
        }
        return maxTickets;
    }

    public static InventoryItem getRandomItem(FishingSpot spot, GameRandom random, List<FishingLootTable> tables) {
        FishingLootTable finalTable = new FishingLootTable();
        for (FishingLootTable table : tables) {
            finalTable.addAll(table);
        }
        return finalTable.getRandomItem(spot, random);
    }

    public static InventoryItem getRandomItem(FishingSpot spot, GameRandom random, FishingLootTable ... tables) {
        return FishingLootTable.getRandomItem(spot, random, Arrays.asList(tables));
    }

    private static class FishLoot {
        public final int tickets;
        public final Predicate<FishingSpot> isValid;
        public final BiFunction<FishingSpot, GameRandom, InventoryItem> itemProducer;

        public FishLoot(int tickets, Predicate<FishingSpot> isValid, BiFunction<FishingSpot, GameRandom, InventoryItem> itemProducer) {
            if (tickets <= 0) {
                throw new IllegalArgumentException("Tickets must be above 0");
            }
            this.tickets = tickets;
            this.isValid = isValid;
            this.itemProducer = itemProducer;
        }
    }
}

