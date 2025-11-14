/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.expeditions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import necesse.engine.expeditions.FishingTripExpedition;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.util.GameLootUtils;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.inventory.InventoryItem;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class CommonFishingTripExpedition
extends FishingTripExpedition {
    public static List<String> commonFishStringIDs = new ArrayList<String>();

    public CommonFishingTripExpedition() {
        commonFishStringIDs.add("carp");
        commonFishStringIDs.add("cod");
        commonFishStringIDs.add("herring");
        commonFishStringIDs.add("mackerel");
        commonFishStringIDs.add("salmon");
        commonFishStringIDs.add("trout");
        commonFishStringIDs.add("tuna");
    }

    @Override
    public GameMessage getUnavailableMessage() {
        return new LocalMessage("expedition", "completequests");
    }

    @Override
    public int getBaseCost(ServerSettlementData settlement) {
        return 300;
    }

    @Override
    public List<InventoryItem> getRewardItems(ServerSettlementData settlement, HumanMob mob) {
        TicketSystemList<InventoryItem> items = new TicketSystemList<InventoryItem>();
        for (String fishStringID : commonFishStringIDs) {
            items.addObject(100, (Object)new InventoryItem(fishStringID, Integer.MAX_VALUE));
        }
        int value = GameRandom.globalRandom.getIntBetween(400, 500);
        ArrayList<InventoryItem> out = GameLootUtils.getItemsValuedAt(GameRandom.globalRandom, value, 0.8f, items);
        out.sort(Comparator.comparing(InventoryItem::getBrokerValue).reversed());
        return out;
    }

    @Override
    public List<InventoryItem> getItemIcons() {
        ArrayList<InventoryItem> items = new ArrayList<InventoryItem>();
        for (String fishStringID : commonFishStringIDs) {
            items.add(new InventoryItem(fishStringID));
        }
        return items;
    }
}

