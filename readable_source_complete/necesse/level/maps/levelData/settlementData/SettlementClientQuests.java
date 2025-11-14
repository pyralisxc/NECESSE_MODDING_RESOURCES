/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.quest.Quest;
import necesse.engine.quest.QuestManager;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameRandom;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.lootItem.OneOfTicketLootItems;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.settlementQuestTiers.SettlementQuestTier;

public class SettlementClientQuests {
    public final long clientAuth;
    public final ServerSettlementData data;
    private HashSet<String> questTiersCompleted = new HashSet();
    private HashMap<String, Integer> tierQuestsUniqueIDs = new HashMap();
    private int highestTierCompleted;
    private int tierQuestsCompleted;
    private int totalCompletedQuests;
    private int lastRandomQuest;
    private int questUniqueID;
    private long questGeneratedWorldTime;

    public SettlementClientQuests(ServerSettlementData data, long clientAuth) {
        this.data = data;
        this.clientAuth = clientAuth;
    }

    public SettlementClientQuests(ServerSettlementData data, LoadData save) {
        int tierQuestUniqueID;
        this.data = data;
        this.clientAuth = save.getLong("clientAuth");
        this.questTiersCompleted = save.getStringHashSet("questTiersCompleted", this.questTiersCompleted, false);
        this.questTiersCompleted.removeIf(stringID -> SettlementQuestTier.questTiers.stream().noneMatch(tier -> tier.stringID.equals(stringID)));
        String questTier = save.getUnsafeString("questTier", null, false);
        if (questTier != null) {
            boolean hasCompletedQuestTier = save.getBoolean("hasCompletedQuestTier", false, false);
            HashSet<String> completedTiers = new HashSet<String>();
            boolean foundTier = false;
            for (SettlementQuestTier tier : SettlementQuestTier.questTiers) {
                if (tier.stringID.equals(questTier)) {
                    if (hasCompletedQuestTier) {
                        completedTiers.add(tier.stringID);
                    }
                    foundTier = true;
                    break;
                }
                completedTiers.add(tier.stringID);
            }
            if (foundTier) {
                this.questTiersCompleted.addAll(completedTiers);
            }
        }
        this.calculateHighestTierCompleted();
        this.tierQuestsCompleted = save.getInt("tierQuestsCompleted", 0);
        this.totalCompletedQuests = save.getInt("totalCompletedQuests", 0);
        this.lastRandomQuest = save.getInt("lastRandomQuest", 0);
        this.questUniqueID = save.getInt("questUniqueID", 0);
        this.questGeneratedWorldTime = save.getLong("questGeneratedWorldTime", 0L);
        this.tierQuestsUniqueIDs = new HashMap();
        LoadData tierQuestUniqueIDsSave = save.getFirstLoadDataByName("tierQuestUniqueIDs");
        if (tierQuestUniqueIDsSave != null) {
            for (LoadData tierQuest : tierQuestUniqueIDsSave.getLoadData()) {
                String tierStringID = tierQuest.getSafeString("tierStringID", null, false);
                int questUniqueID = tierQuest.getInt("questUniqueID", 0, false);
                if (tierStringID == null || questUniqueID == 0) continue;
                this.tierQuestsUniqueIDs.put(tierStringID, questUniqueID);
            }
        }
        if ((tierQuestUniqueID = save.getInt("tierQuestUniqueID", 0, false)) != 0) {
            this.getQuestManager().removeQuest(tierQuestUniqueID);
        }
    }

    public void addSaveData(SaveData save) {
        save.addLong("clientAuth", this.clientAuth);
        save.addStringHashSet("questTiersCompleted", this.questTiersCompleted);
        save.addInt("tierQuestsCompleted", this.tierQuestsCompleted);
        save.addInt("totalCompletedQuests", this.totalCompletedQuests);
        save.addInt("lastRandomQuest", this.lastRandomQuest);
        save.addInt("questUniqueID", this.questUniqueID);
        save.addLong("questGeneratedWorldTime", this.questGeneratedWorldTime);
        SaveData tierQuestUniqueIDsSave = new SaveData("tierQuestUniqueIDs");
        for (Map.Entry<String, Integer> entry : this.tierQuestsUniqueIDs.entrySet()) {
            SaveData entrySave = new SaveData("");
            entrySave.addSafeString("tierStringID", entry.getKey());
            entrySave.addInt("questUniqueID", entry.getValue());
            tierQuestUniqueIDsSave.addSaveData(entrySave);
        }
        if (!tierQuestUniqueIDsSave.isEmpty()) {
            save.addSaveData(tierQuestUniqueIDsSave);
        }
    }

    public QuestManager getQuestManager() {
        return this.data.getLevel().getServer().world.getQuests();
    }

    public ServerClient getClient() {
        return this.data.getLevel().getServer().getClientByAuth(this.clientAuth);
    }

    protected void calculateHighestTierCompleted() {
        this.highestTierCompleted = 0;
        for (String stringID : this.questTiersCompleted) {
            this.highestTierCompleted = Math.max(SettlementQuestTier.getTierIndex(stringID), this.highestTierCompleted);
        }
    }

    public void setAndCompletePreviousTiers(SettlementQuestTier tier, boolean hasCompletedTier) {
        this.questTiersCompleted = new HashSet();
        int tierIndex = SettlementQuestTier.getTierIndex(tier.stringID) - 1;
        if (hasCompletedTier) {
            ++tierIndex;
        }
        for (int i = 0; i < tierIndex; ++i) {
            this.questTiersCompleted.add(SettlementQuestTier.questTiers.get((int)i).stringID);
        }
        this.highestTierCompleted = tierIndex;
    }

    public void setCompletedTier(SettlementQuestTier tier) {
        int tierQuestUniqueID;
        this.questTiersCompleted.add(tier.stringID);
        int tierIndex = SettlementQuestTier.getTierIndex(tier.stringID);
        if (tierIndex > this.highestTierCompleted) {
            this.highestTierCompleted = tierIndex;
            if (this.questUniqueID != 0) {
                this.getQuestManager().removeQuest(this.questUniqueID);
                this.questUniqueID = 0;
            }
        }
        if ((tierQuestUniqueID = this.tierQuestsUniqueIDs.getOrDefault(tier.stringID, 0).intValue()) != 0) {
            this.getQuestManager().removeQuest(tierQuestUniqueID);
        }
    }

    public void resetCompletedTier(SettlementQuestTier tier) {
        int lastHighestTier = this.highestTierCompleted;
        this.questTiersCompleted.remove(tier.stringID);
        this.calculateHighestTierCompleted();
        if (lastHighestTier < this.highestTierCompleted && this.questUniqueID != 0) {
            this.getQuestManager().removeQuest(this.questUniqueID);
            this.questUniqueID = 0;
        }
    }

    public Quest getQuest() {
        return null;
    }

    public ArrayList<Quest> getTierQuests() {
        Quest quest;
        ArrayList<Quest> out = new ArrayList<Quest>();
        Iterator<Object> iterator = this.tierQuestsUniqueIDs.values().iterator();
        while (iterator.hasNext()) {
            int questUniqueID = iterator.next();
            quest = this.getQuestManager().getQuest(questUniqueID);
            if (quest == null) continue;
            out.add(quest);
        }
        if (out.isEmpty()) {
            for (SettlementQuestTier questTier : SettlementQuestTier.questTiers) {
                if (this.questTiersCompleted.contains(questTier.stringID)) continue;
                quest = questTier.getTierCompleteQuest(this.data, this.tierQuestsCompleted);
                if (quest == null) break;
                this.getQuestManager().addQuest(quest, true);
                this.tierQuestsUniqueIDs.put(questTier.stringID, quest.getUniqueID());
                out.add(quest);
                break;
            }
        }
        return out;
    }

    public void addTierQuest(ServerClient client, SettlementQuestTier tier, Quest quest) {
        if (this.tierQuestsUniqueIDs.containsKey(tier.stringID)) {
            return;
        }
        this.getQuestManager().addQuest(quest, true);
        quest.makeActiveFor(client.getServer(), client);
        this.tierQuestsUniqueIDs.put(tier.stringID, quest.getUniqueID());
    }

    public GameMessage getTierQuestError() {
        SettlementQuestTier lowestTier = this.getLowestIncompleteQuestTier();
        if (lowestTier != null) {
            return lowestTier.getTierCompleteQuestError(this.data, this.tierQuestsCompleted);
        }
        return null;
    }

    public int getHighestQuestTierCompleted() {
        return this.highestTierCompleted;
    }

    public SettlementQuestTier getLowestIncompleteQuestTier() {
        if (this.questTiersCompleted.size() >= SettlementQuestTier.questTiers.size()) {
            return null;
        }
        for (SettlementQuestTier questTier : SettlementQuestTier.questTiers) {
            if (this.questTiersCompleted.contains(questTier.stringID)) continue;
            return questTier;
        }
        return null;
    }

    public boolean hasCompletedQuestTier(String tierStringID) {
        return this.questTiersCompleted.contains(tierStringID);
    }

    public int getQuestsCompletedInCurrentTier() {
        return this.tierQuestsCompleted;
    }

    public int getTotalCompletedQuests() {
        return this.totalCompletedQuests;
    }

    protected Quest getNewQuest() {
        SettlementQuestTier lowestTier = this.getLowestIncompleteQuestTier();
        ArrayList<Function<ServerSettlementData, Quest>> basicQuests = SettlementQuestTier.getBasicQuests(this.questTiersCompleted, this.tierQuestsCompleted);
        ArrayList<Function<ServerSettlementData, Quest>> nextQuests = lowestTier != null ? lowestTier.nextQuests(basicQuests, this.tierQuestsCompleted) : basicQuests;
        if (nextQuests.size() <= 1) {
            this.lastRandomQuest = 0;
            return nextQuests.get(0).apply(this.data);
        }
        int nextRandomQuest = GameRandom.globalRandom.nextInt(nextQuests.size());
        if (nextRandomQuest == this.lastRandomQuest) {
            nextRandomQuest = (nextRandomQuest + 1) % nextQuests.size();
        }
        this.lastRandomQuest = nextRandomQuest;
        return nextQuests.get(nextRandomQuest).apply(this.data);
    }

    public GameMessage canSkipQuest() {
        long currentTime;
        int secondsInDay = this.data.getLevel().getWorldEntity().getDayTimeMax();
        if (this.questGeneratedWorldTime + (long)secondsInDay * 1000L > (currentTime = this.data.getLevel().getWorldEntity().getWorldTime())) {
            return new LocalMessage("ui", "elderskipquesttime");
        }
        return null;
    }

    public void removeCurrentQuest() {
        Quest quest = this.getQuest();
        if (quest != null) {
            quest.remove();
        }
        this.questUniqueID = 0;
    }

    public void removeTierQuests() {
        for (int questUniqueID : this.tierQuestsUniqueIDs.values()) {
            this.getQuestManager().removeQuest(questUniqueID);
        }
        this.tierQuestsUniqueIDs.clear();
    }

    public List<InventoryItem> completeQuestAndGetReward() {
        Quest quest = this.getQuest();
        if (quest != null) {
            quest.remove();
        }
        this.questUniqueID = 0;
        ArrayList<InventoryItem> rewards = new ArrayList<InventoryItem>();
        SettlementQuestTier currentTier = this.getLowestIncompleteQuestTier();
        OneOfTicketLootItems uniqueRewards = SettlementQuestTier.getUniqueRewards(this.questTiersCompleted);
        if (currentTier != null) {
            currentTier.rewardsLootTable(this.data, uniqueRewards, this.tierQuestsCompleted).addItems(rewards, GameRandom.globalRandom, 1.0f, this, this.totalCompletedQuests);
        } else {
            uniqueRewards.addItems(rewards, GameRandom.globalRandom, 1.0f, this, this.totalCompletedQuests);
        }
        ++this.tierQuestsCompleted;
        ++this.totalCompletedQuests;
        return rewards;
    }

    public List<InventoryItem> completeTierQuestAndGetReward(Quest quest) {
        String tierStringID = quest == null ? null : (String)this.tierQuestsUniqueIDs.entrySet().stream().filter(e -> ((Integer)e.getValue()).intValue() == quest.getUniqueID()).map(Map.Entry::getKey).findFirst().orElse(null);
        SettlementQuestTier questTier = null;
        if (tierStringID != null) {
            questTier = SettlementQuestTier.getTier(tierStringID);
            this.tierQuestsUniqueIDs.remove(tierStringID);
            this.questTiersCompleted.add(tierStringID);
        }
        if (questTier == null) {
            questTier = this.getLowestIncompleteQuestTier();
        }
        if (quest != null) {
            quest.remove();
        }
        ArrayList<InventoryItem> rewards = new ArrayList<InventoryItem>();
        if (questTier != null) {
            questTier.tierRewardsLootTable(this.data, this.tierQuestsCompleted).addItems(rewards, GameRandom.globalRandom, 1.0f, this.data, this, this.totalCompletedQuests);
        }
        this.tierQuestsCompleted = 0;
        if (questTier != null) {
            this.data.onCompletedQuestTier(questTier);
        }
        ++this.totalCompletedQuests;
        return rewards;
    }
}

