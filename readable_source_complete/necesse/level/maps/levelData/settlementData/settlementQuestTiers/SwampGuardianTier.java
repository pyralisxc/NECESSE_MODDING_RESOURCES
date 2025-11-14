/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.settlementQuestTiers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import necesse.engine.quest.DeliverItemsSettlementQuest;
import necesse.engine.quest.Quest;
import necesse.engine.util.GameRandom;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.objectItem.WaystoneObjectItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootList;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.OneOfTicketLootItems;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.settlementQuestTiers.SimpleBossSettlementQuestTier;

public class SwampGuardianTier
extends SimpleBossSettlementQuestTier {
    public static OneOfTicketLootItems uniqueQuestRewards = new OneOfTicketLootItems(new Object[0]);
    public static ArrayList<Function<ServerSettlementData, Quest>> tierQuests = new ArrayList();
    public static LootTable tierCompleteRewards;

    public SwampGuardianTier() {
        super("swampguardian", "swampguardian");
    }

    @Override
    public OneOfTicketLootItems getUniqueQuestRewards() {
        return uniqueQuestRewards;
    }

    @Override
    public ArrayList<Function<ServerSettlementData, Quest>> getTierQuests() {
        return tierQuests;
    }

    @Override
    public LootTable getTierCompleteRewards() {
        return tierCompleteRewards;
    }

    static {
        tierQuests.add(d -> new DeliverItemsSettlementQuest(d.getSettlementName(), null, "slimechunk", 1));
        tierQuests.add(d -> new DeliverItemsSettlementQuest(d.getSettlementName(), null, "swampeel", 1));
        tierQuests.add(d -> new DeliverItemsSettlementQuest(d.getSettlementName(), null, "slimylauncher", 1));
        tierCompleteRewards = new LootTable(new LootItemInterface(){

            @Override
            public void addPossibleLoot(LootList list, Object ... extra) {
                list.add("homestone");
                list.add("waystone");
            }

            @Override
            public void addItems(List<InventoryItem> list, GameRandom random, float lootMultiplier, Object ... extra) {
                ServerSettlementData data = LootTable.expectExtra(ServerSettlementData.class, extra, 0);
                if (data != null) {
                    list.add(new InventoryItem("homestone"));
                    InventoryItem waystone = WaystoneObjectItem.setupWaystoneItem(new InventoryItem("waystone", 2), data.uniqueID);
                    list.add(waystone);
                }
            }
        });
    }
}

