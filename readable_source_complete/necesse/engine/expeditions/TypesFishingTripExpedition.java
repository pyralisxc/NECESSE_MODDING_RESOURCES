/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.expeditions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import necesse.engine.expeditions.FishingTripExpedition;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameLootUtils;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.inventory.InventoryItem;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class TypesFishingTripExpedition
extends FishingTripExpedition {
    public String questTierStringID;
    public int baseCost;
    public int valueGottenMin;
    public int valueGottenMax;
    public ArrayList<FishConfig> fish;

    public TypesFishingTripExpedition(String questTierStringID, int baseCost, int valueGottenMin, int valueGottenMax, List<FishConfig> fish) {
        this.questTierStringID = questTierStringID;
        this.baseCost = baseCost;
        this.valueGottenMin = valueGottenMin;
        this.valueGottenMax = valueGottenMax;
        this.fish = new ArrayList<FishConfig>(fish);
    }

    public TypesFishingTripExpedition(String questTierStringID, int baseCost, int valueGottenMin, int valueGottenMax, FishConfig ... fish) {
        this(questTierStringID, baseCost, valueGottenMin, valueGottenMax, Arrays.asList(fish));
    }

    public TypesFishingTripExpedition(String questTierStringID, int baseCost, int valueGottenMin, int valueGottenMax, String[] primaryFishStringIDs, String ... secondaryFishStringIDs) {
        this(questTierStringID, baseCost, valueGottenMin, valueGottenMax, TypesFishingTripExpedition.concat(TypesFishingTripExpedition.stringIDsToConfigs(400, primaryFishStringIDs), TypesFishingTripExpedition.stringIDsToConfigs(100, secondaryFishStringIDs)));
    }

    public TypesFishingTripExpedition(String questTierStringID, int baseCost, int valueGottenMin, int valueGottenMax, String primaryFishStringID, String ... secondaryFishStringIDs) {
        this(questTierStringID, baseCost, valueGottenMin, valueGottenMax, TypesFishingTripExpedition.concat(TypesFishingTripExpedition.stringIDsToConfigs(400, primaryFishStringID), TypesFishingTripExpedition.stringIDsToConfigs(100, secondaryFishStringIDs)));
    }

    private static FishConfig[] stringIDsToConfigs(int tickets, String ... stringIDs) {
        FishConfig[] out = new FishConfig[stringIDs.length];
        for (int i = 0; i < stringIDs.length; ++i) {
            out[i] = new FishConfig(stringIDs[i], tickets);
        }
        return out;
    }

    private static FishConfig[] concat(FishConfig[] ... configArrays) {
        int totalSize = 0;
        for (FishConfig[] configs : configArrays) {
            totalSize += configs.length;
        }
        FishConfig[] out = new FishConfig[totalSize];
        int currentIndex = 0;
        FishConfig[][] fishConfigArray = configArrays;
        int n = fishConfigArray.length;
        for (int i = 0; i < n; ++i) {
            FishConfig[] configArray;
            for (FishConfig fishConfig : configArray = fishConfigArray[i]) {
                out[currentIndex++] = fishConfig;
            }
        }
        return out;
    }

    @Override
    public void initDisplayName() {
        if (!this.fish.isEmpty()) {
            this.displayName = ItemRegistry.getItem(this.fish.get((int)0).itemStringID).getNewLocalization();
        } else {
            super.initDisplayName();
        }
    }

    @Override
    public GameMessage getUnavailableMessage() {
        return new LocalMessage("expedition", "completequests");
    }

    @Override
    public int getBaseCost(ServerSettlementData settlement) {
        return this.baseCost;
    }

    @Override
    public List<InventoryItem> getRewardItems(ServerSettlementData settlement, HumanMob mob) {
        TicketSystemList<InventoryItem> items = new TicketSystemList<InventoryItem>();
        for (FishConfig fish : this.fish) {
            items.addObject(fish.tickets, (Object)new InventoryItem(fish.itemStringID, fish.maxAmount));
        }
        int value = GameRandom.globalRandom.getIntBetween(this.valueGottenMin, this.valueGottenMax);
        ArrayList<InventoryItem> out = GameLootUtils.getItemsValuedAt(GameRandom.globalRandom, value, 0.8f, items);
        out.sort(Comparator.comparing(InventoryItem::getBrokerValue).reversed());
        return out;
    }

    @Override
    public float getSuccessChance(ServerSettlementData settlement) {
        if (this.questTierStringID == null) {
            return 1.0f;
        }
        return TypesFishingTripExpedition.questProgressSuccessChance(settlement, this.questTierStringID, 2);
    }

    @Override
    public List<InventoryItem> getItemIcons() {
        ArrayList<InventoryItem> items;
        block0: {
            items = new ArrayList<InventoryItem>();
            Iterator<FishConfig> iterator = this.fish.iterator();
            if (!iterator.hasNext()) break block0;
            FishConfig fishConfig = iterator.next();
            items.add(new InventoryItem(fishConfig.itemStringID));
        }
        return items;
    }

    public static class FishConfig {
        public String itemStringID;
        public float chance;
        public int tickets;
        public int maxAmount;

        public FishConfig(String itemStringID, float chance, int tickets, int maxAmount) {
            this.itemStringID = itemStringID;
            this.chance = chance;
            this.tickets = tickets;
            this.maxAmount = maxAmount;
        }

        public FishConfig(String itemStringID, float chance, int tickets) {
            this(itemStringID, chance, tickets, Integer.MAX_VALUE);
        }

        public FishConfig(String itemStringID, int tickets) {
            this(itemStringID, 1.0f, tickets);
        }
    }
}

