/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.settlementQuestTiers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.quest.Quest;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.entity.mobs.Mob;
import necesse.inventory.lootTable.LootList;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.OneOfTicketLootItems;
import necesse.level.maps.levelData.settlementData.CachedSettlementData;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.SettlementClientQuests;
import necesse.level.maps.levelData.settlementData.settlementQuestTiers.SettlementQuestTier;

public abstract class SimpleSettlementQuestTier
extends SettlementQuestTier {
    public SimpleSettlementQuestTier(String stringID) {
        super(stringID);
    }

    public abstract int getTotalTierQuests();

    public abstract int getTotalBasicQuests();

    public abstract OneOfTicketLootItems getUniqueQuestRewards();

    public abstract ArrayList<Function<ServerSettlementData, Quest>> getTierQuests();

    public abstract LootTable getTierCompleteRewards();

    public abstract Quest getTierCompleteQuest(ServerSettlementData var1, Mob var2);

    @Override
    public void addUniqueRewards(OneOfTicketLootItems list) {
        list.addAll(this.getUniqueQuestRewards());
    }

    @Override
    public void addBasicQuests(ArrayList<Function<ServerSettlementData, Quest>> list, int tierQuestsCompleted) {
        list.addAll(this.getTierQuests());
    }

    @Override
    public LootTable rewardsLootTable(ServerSettlementData data, OneOfTicketLootItems uniqueRewards, int tierQuestsCompleted) {
        return new LootTable(uniqueRewards, addedQuestRewards);
    }

    @Override
    public ArrayList<Function<ServerSettlementData, Quest>> nextQuests(ArrayList<Function<ServerSettlementData, Quest>> basicQuests, int tierQuestsCompleted) {
        if (tierQuestsCompleted < this.getTotalTierQuests()) {
            ArrayList<Function<ServerSettlementData, Quest>> tierQuests = this.getTierQuests();
            if (tierQuests.isEmpty()) {
                return basicQuests;
            }
            return tierQuests;
        }
        ArrayList<Function<ServerSettlementData, Quest>> out = new ArrayList<Function<ServerSettlementData, Quest>>();
        out.addAll(this.getTierQuests());
        out.addAll(basicQuests);
        return out;
    }

    @Override
    public Quest getTierCompleteQuest(ServerSettlementData data, int tierQuestsCompleted) {
        if (tierQuestsCompleted >= this.getTotalTierQuests() + this.getTotalBasicQuests()) {
            return this.getTierCompleteQuest(data, null);
        }
        return null;
    }

    @Override
    public void onBossKilled(Mob mob, ServerClient client) {
        if (this.bossKillTriggersTierQuest(mob)) {
            Server server = client.getServer();
            SettlementsWorldData worldData = SettlementsWorldData.getSettlementsData(server);
            List<CachedSettlementData> settlements = worldData.collectCachedSettlements(data -> data.getOwnerAuth() == client.authentication || client.isSameTeam(data.getTeamID()));
            for (CachedSettlementData cache : settlements) {
                SettlementClientQuests clientsQuests;
                ServerSettlementData settlement = worldData.getServerData(cache.uniqueID);
                if (settlement == null || (clientsQuests = settlement.getClientsQuests(client)).hasCompletedQuestTier(this.stringID)) continue;
                Quest quest = this.getTierCompleteQuest(settlement, mob);
                clientsQuests.addTierQuest(client, this, quest);
            }
        }
    }

    public abstract boolean bossKillTriggersTierQuest(Mob var1);

    @Override
    public GameMessage getTierCompleteQuestError(ServerSettlementData data, int tierQuestsCompleted) {
        if (tierQuestsCompleted < this.getTotalTierQuests() + this.getTotalBasicQuests()) {
            return new LocalMessage("ui", "eldertiercompleteerr", "count", this.getTotalTierQuests() + this.getTotalBasicQuests() - tierQuestsCompleted);
        }
        return null;
    }

    @Override
    public LootList getTierRewardsDisplayList() {
        LootList out = new LootList();
        LootTable rewards = this.getTierCompleteRewards();
        rewards.addPossibleLoot(out, new Object[0]);
        return out;
    }

    @Override
    public LootTable tierRewardsLootTable(ServerSettlementData data, int tierQuestsCompleted) {
        return new LootTable(this.getTierCompleteRewards(), addedQuestRewards);
    }
}

