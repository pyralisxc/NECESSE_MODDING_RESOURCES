/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.expeditions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import necesse.engine.expeditions.SettlerExpedition;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameLootUtils;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.inventory.InventoryItem;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class MiningTripExpedition
extends SettlerExpedition {
    public String questTierStringID;
    public int baseCost;
    public int valueGottenMin;
    public int valueGottenMax;
    public String[] stoneStringIDs;
    public ArrayList<OreConfig> ores;

    public MiningTripExpedition(String questTierStringID, int baseCost, int valueGottenMin, int valueGottenMax, String[] stoneStringIDs, List<OreConfig> ores) {
        this.questTierStringID = questTierStringID;
        this.baseCost = baseCost;
        this.valueGottenMin = valueGottenMin;
        this.valueGottenMax = valueGottenMax;
        this.stoneStringIDs = stoneStringIDs;
        this.ores = new ArrayList<OreConfig>(ores);
    }

    public MiningTripExpedition(String questTierStringID, int baseCost, int valueGottenMin, int valueGottenMax, String[] stoneStringIDs, OreConfig ... ores) {
        this(questTierStringID, baseCost, valueGottenMin, valueGottenMax, stoneStringIDs, Arrays.asList(ores));
    }

    public MiningTripExpedition(String questTierStringID, int baseCost, int valueGottenMin, int valueGottenMax, String stoneStringID, OreConfig ... ores) {
        String[] stringArray;
        if (stoneStringID == null) {
            stringArray = null;
        } else {
            String[] stringArray2 = new String[1];
            stringArray = stringArray2;
            stringArray2[0] = stoneStringID;
        }
        this(questTierStringID, baseCost, valueGottenMin, valueGottenMax, stringArray, Arrays.asList(ores));
    }

    public MiningTripExpedition(String questTierStringID, int baseCost, int valueGottenMin, int valueGottenMax, String[] stoneStringIDs, String[] primaryOreStringIDs, String ... secondaryOreStringIDs) {
        this(questTierStringID, baseCost, valueGottenMin, valueGottenMax, stoneStringIDs, MiningTripExpedition.concat(MiningTripExpedition.stringIDsToConfigs(400, primaryOreStringIDs), MiningTripExpedition.stringIDsToConfigs(100, secondaryOreStringIDs)));
    }

    public MiningTripExpedition(String questTierStringID, int baseCost, int valueGottenMin, int valueGottenMax, String stoneStringID, String primaryOreStringID, String ... secondaryOreStringIDs) {
        String[] stringArray;
        if (stoneStringID == null) {
            stringArray = null;
        } else {
            String[] stringArray2 = new String[1];
            stringArray = stringArray2;
            stringArray2[0] = stoneStringID;
        }
        this(questTierStringID, baseCost, valueGottenMin, valueGottenMax, stringArray, MiningTripExpedition.concat(MiningTripExpedition.stringIDsToConfigs(400, primaryOreStringID), MiningTripExpedition.stringIDsToConfigs(100, secondaryOreStringIDs)));
    }

    private static OreConfig[] stringIDsToConfigs(int tickets, String ... stringIDs) {
        OreConfig[] out = new OreConfig[stringIDs.length];
        for (int i = 0; i < stringIDs.length; ++i) {
            out[i] = new OreConfig(stringIDs[i], tickets);
        }
        return out;
    }

    private static OreConfig[] concat(OreConfig[] ... configArrays) {
        int totalSize = 0;
        for (OreConfig[] configs : configArrays) {
            totalSize += configs.length;
        }
        OreConfig[] out = new OreConfig[totalSize];
        int currentIndex = 0;
        OreConfig[][] oreConfigArray = configArrays;
        int n = oreConfigArray.length;
        for (int i = 0; i < n; ++i) {
            OreConfig[] configArray;
            for (OreConfig oreConfig : configArray = oreConfigArray[i]) {
                out[currentIndex++] = oreConfig;
            }
        }
        return out;
    }

    @Override
    public void initDisplayName() {
        if (!this.ores.isEmpty()) {
            this.displayName = ItemRegistry.getItem(this.ores.get((int)0).itemStringID).getNewLocalization();
        } else {
            super.initDisplayName();
        }
    }

    @Override
    public float getSuccessChance(ServerSettlementData settlement) {
        if (this.questTierStringID == null) {
            return 1.0f;
        }
        if ((double)MiningTripExpedition.questProgressSuccessChance(settlement, this.questTierStringID) > 0.5) {
            return 1.0f;
        }
        return 0.0f;
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
        String stoneStringID;
        TicketSystemList<InventoryItem> oreItems = new TicketSystemList<InventoryItem>();
        for (OreConfig ore : this.ores) {
            if (!GameRandom.globalRandom.getChance(ore.chance)) continue;
            oreItems.addObject(ore.tickets, (Object)new InventoryItem(ore.itemStringID, ore.maxAmount));
        }
        int value = GameRandom.globalRandom.getIntBetween(this.valueGottenMin, this.valueGottenMax);
        ArrayList<InventoryItem> out = GameLootUtils.getItemsValuedAt(GameRandom.globalRandom, value, 0.8f, oreItems);
        out.sort(Comparator.comparing(InventoryItem::getBrokerValue).reversed());
        if (this.stoneStringIDs != null && (stoneStringID = GameRandom.globalRandom.getOneOf(this.stoneStringIDs)) != null) {
            int totalOres = out.stream().mapToInt(InventoryItem::getAmount).sum();
            int stoneAmount = (int)((float)totalOres * GameRandom.globalRandom.getFloatBetween(2.0f, 3.5f));
            out.add(new InventoryItem(stoneStringID, stoneAmount));
        }
        return out;
    }

    @Override
    public List<InventoryItem> getItemIcons() {
        ArrayList<InventoryItem> items;
        block2: {
            block1: {
                items = new ArrayList<InventoryItem>();
                if (!this.ores.isEmpty()) break block1;
                if (this.stoneStringIDs == null) break block2;
                for (String stoneStringID : this.stoneStringIDs) {
                    items.add(new InventoryItem(stoneStringID));
                }
                break block2;
            }
            Iterator<OreConfig> iterator = this.ores.iterator();
            if (!iterator.hasNext()) break block2;
            OreConfig ore = iterator.next();
            items.add(new InventoryItem(ore.itemStringID));
        }
        return items;
    }

    public static class OreConfig {
        public String itemStringID;
        public float chance;
        public int tickets;
        public int maxAmount;

        public OreConfig(String itemStringID, float chance, int tickets, int maxAmount) {
            this.itemStringID = itemStringID;
            this.chance = chance;
            this.tickets = tickets;
            this.maxAmount = maxAmount;
        }

        public OreConfig(String itemStringID, float chance, int tickets) {
            this(itemStringID, chance, tickets, Integer.MAX_VALUE);
        }

        public OreConfig(String itemStringID, int tickets) {
            this(itemStringID, 1.0f, tickets);
        }
    }
}

