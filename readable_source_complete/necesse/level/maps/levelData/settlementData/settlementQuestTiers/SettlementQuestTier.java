/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.settlementQuestTiers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Function;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.quest.DeliverItemsSettlementQuest;
import necesse.engine.quest.Quest;
import necesse.entity.mobs.Mob;
import necesse.inventory.lootTable.LootList;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.LootItemList;
import necesse.inventory.lootTable.lootItem.OneOfTicketLootItems;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.settlementQuestTiers.AncientVultureTier;
import necesse.level.maps.levelData.settlementData.settlementQuestTiers.ChieftainTier;
import necesse.level.maps.levelData.settlementData.settlementQuestTiers.CryoQueenTier;
import necesse.level.maps.levelData.settlementData.settlementQuestTiers.EvilsProtectorTier;
import necesse.level.maps.levelData.settlementData.settlementQuestTiers.FallenWizardTier;
import necesse.level.maps.levelData.settlementData.settlementQuestTiers.PestWardenTier;
import necesse.level.maps.levelData.settlementData.settlementQuestTiers.PirateCaptainTier;
import necesse.level.maps.levelData.settlementData.settlementQuestTiers.QueenSpiderTier;
import necesse.level.maps.levelData.settlementData.settlementQuestTiers.ReaperTier;
import necesse.level.maps.levelData.settlementData.settlementQuestTiers.SageAndGritTier;
import necesse.level.maps.levelData.settlementData.settlementQuestTiers.SwampGuardianTier;
import necesse.level.maps.levelData.settlementData.settlementQuestTiers.TheCursedCroneTier;
import necesse.level.maps.levelData.settlementData.settlementQuestTiers.VoidWizardTier;

public abstract class SettlementQuestTier {
    public static OneOfTicketLootItems basicUniqueQuestRewards = new OneOfTicketLootItems(400, LootItem.between("recallscroll", 2, 5), 400, LootItem.between("teleportationscroll", 1, 3), 1800, new OneOfTicketLootItems(100, LootItem.between("healthregenpotion", 3, 6), 100, LootItem.between("attackspeedpotion", 3, 6), 100, LootItem.between("fishingpotion", 3, 6), 100, LootItem.between("battlepotion", 3, 6), 100, LootItem.between("resistancepotion", 3, 6), 100, LootItem.between("thornspotion", 3, 6), 100, LootItem.between("accuracypotion", 3, 6), 100, LootItem.between("minionpotion", 3, 6), 100, LootItem.between("knockbackpotion", 3, 6), 100, LootItem.between("rapidpotion", 3, 6), 100, LootItem.between("spelunkerpotion", 3, 6), 100, LootItem.between("treasurepotion", 3, 6), 100, LootItem.between("passivepotion", 3, 6)), 200, new LootItem("constructionhammer"), 200, new LootItem("telescopicladder"), 200, new LootItem("toolextender"), 200, new LootItem("itemattractor"), 200, new LootItem("boxingglovegun"), 100, new LootItem("infinitewaterbucket"), 100, new LootItem("infiniterope"), 50, new LootItemList(new LootItem("horsemask"), new LootItem("horsecostumeshirt"), new LootItem("horsecostumeboots")), 50, new LootItemList(new LootItem("chickenmask"), new LootItem("chickencostumeshirt"), new LootItem("chickencostumeboots")), 50, new LootItemList(new LootItem("frogmask"), new LootItem("frogcostumeshirt"), new LootItem("frogcostumeboots")), 50, new LootItemList(new LootItem("alienmask"), new LootItem("aliencostumeshirt"), new LootItem("aliencostumeboots")));
    public static LootItemList addedQuestRewards = new LootItemList(LootItem.between("coin", 20, 50));
    public static ArrayList<Function<ServerSettlementData, Quest>> basicQuests = new ArrayList();
    public static ArrayList<SettlementQuestTier> questTiers;
    public final String stringID;

    public static int getTierIndex(String questTierStringID) {
        for (int i = 0; i < questTiers.size(); ++i) {
            if (!SettlementQuestTier.questTiers.get((int)i).stringID.equals(questTierStringID)) continue;
            return i;
        }
        return 0;
    }

    public static SettlementQuestTier getTier(String questTierStringID) {
        return questTiers.get(SettlementQuestTier.getTierIndex(questTierStringID));
    }

    public static SettlementQuestTier getTier(int questTier) {
        if (questTier < questTiers.size()) {
            return questTiers.get(questTier);
        }
        return null;
    }

    public static ArrayList<Function<ServerSettlementData, Quest>> getBasicQuests(HashSet<String> questTierStringIDs, int tierQuestsCompleted) {
        ArrayList<Function<ServerSettlementData, Quest>> list = new ArrayList<Function<ServerSettlementData, Quest>>(basicQuests);
        for (SettlementQuestTier questTier : questTiers) {
            if (!questTierStringIDs.contains(questTier.stringID)) continue;
            questTier.addBasicQuests(list, tierQuestsCompleted);
        }
        return list;
    }

    public static OneOfTicketLootItems getUniqueRewards(HashSet<String> questTierStringIDs) {
        OneOfTicketLootItems uniqueRewards = new OneOfTicketLootItems(basicUniqueQuestRewards, new Object[0]);
        for (SettlementQuestTier questTier : questTiers) {
            if (!questTierStringIDs.contains(questTier.stringID)) continue;
            questTier.addUniqueRewards(uniqueRewards);
        }
        return uniqueRewards;
    }

    public SettlementQuestTier(String stringID) {
        this.stringID = stringID;
    }

    public abstract void addUniqueRewards(OneOfTicketLootItems var1);

    public abstract void addBasicQuests(ArrayList<Function<ServerSettlementData, Quest>> var1, int var2);

    public abstract LootTable rewardsLootTable(ServerSettlementData var1, OneOfTicketLootItems var2, int var3);

    public abstract ArrayList<Function<ServerSettlementData, Quest>> nextQuests(ArrayList<Function<ServerSettlementData, Quest>> var1, int var2);

    public abstract Quest getTierCompleteQuest(ServerSettlementData var1, int var2);

    public abstract GameMessage getTierCompleteQuestError(ServerSettlementData var1, int var2);

    public abstract void onBossKilled(Mob var1, ServerClient var2);

    public abstract LootTable tierRewardsLootTable(ServerSettlementData var1, int var2);

    public abstract LootList getTierRewardsDisplayList();

    public float getExpectedSettlersIncrease() {
        return 1.34f;
    }

    static {
        basicQuests.add(d -> new DeliverItemsSettlementQuest(d.getSettlementName(), null, "babyshark", 1));
        basicQuests.add(d -> new DeliverItemsSettlementQuest(d.getSettlementName(), null, "crabclaw", 1));
        basicQuests.add(d -> new DeliverItemsSettlementQuest(d.getSettlementName(), null, "sandray", 1));
        basicQuests.add(d -> new DeliverItemsSettlementQuest(d.getSettlementName(), null, "babyswordfish", 1));
        questTiers = new ArrayList();
        questTiers.add(new EvilsProtectorTier());
        questTiers.add(new QueenSpiderTier());
        questTiers.add(new VoidWizardTier());
        questTiers.add(new ChieftainTier());
        questTiers.add(new SwampGuardianTier());
        questTiers.add(new AncientVultureTier());
        questTiers.add(new PirateCaptainTier());
        questTiers.add(new ReaperTier());
        questTiers.add(new CryoQueenTier());
        questTiers.add(new TheCursedCroneTier());
        questTiers.add(new PestWardenTier());
        questTiers.add(new SageAndGritTier());
        questTiers.add(new FallenWizardTier());
    }
}

