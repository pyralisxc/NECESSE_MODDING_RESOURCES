/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.settlementQuestTiers;

import java.util.ArrayList;
import java.util.function.Function;
import necesse.engine.quest.DeliverItemsSettlementQuest;
import necesse.engine.quest.Quest;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.OneOfTicketLootItems;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.settlementQuestTiers.SimpleBossSettlementQuestTier;

public class ReaperTier
extends SimpleBossSettlementQuestTier {
    public static OneOfTicketLootItems uniqueQuestRewards = new OneOfTicketLootItems(new Object[0]);
    public static ArrayList<Function<ServerSettlementData, Quest>> tierQuests = new ArrayList();
    public static LootTable tierCompleteRewards;

    public ReaperTier() {
        super("reaper", "reaper");
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
        tierQuests.add(d -> new DeliverItemsSettlementQuest(d.getSettlementName(), null, "deepspiritswab", 1));
        tierQuests.add(d -> new DeliverItemsSettlementQuest(d.getSettlementName(), null, "brokenlimb", 1));
        tierCompleteRewards = new LootTable(new LootItem("hoverboard"));
    }
}

