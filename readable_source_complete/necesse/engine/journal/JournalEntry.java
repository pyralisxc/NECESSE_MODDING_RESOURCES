/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.journal;

import java.util.ArrayList;
import java.util.Objects;
import necesse.engine.journal.JournalChallenge;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.ServerClient;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.registries.IDData;
import necesse.engine.registries.IDDataContainer;
import necesse.engine.registries.JournalChallengeRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameUtils;
import necesse.engine.util.LevelIdentifier;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.critters.caveling.CavelingMob;
import necesse.entity.mobs.hostile.bosses.FlyingSpiritsHead;
import necesse.inventory.lootTable.LootList;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.incursion.IncursionBiome;

public class JournalEntry
implements IDDataContainer {
    public final IDData idData = new IDData();
    public ArrayList<MobJournalData> mobsData = new ArrayList();
    public LootList biomeLoot = new LootList();
    public LootList treasuresData;
    public ArrayList<Integer> entryChallengeIDs = new ArrayList();
    public final Biome biome;
    public final LevelIdentifier levelIdentifier;
    public final IncursionBiome incursionBiome;
    public boolean toggleIsHidden = true;

    public JournalEntry(Biome biome) {
        this.biome = biome;
        this.levelIdentifier = null;
        this.incursionBiome = null;
    }

    public JournalEntry(Biome biome, LevelIdentifier levelIdentifier) {
        this.biome = biome;
        this.levelIdentifier = levelIdentifier;
        this.incursionBiome = null;
    }

    public JournalEntry(Biome biome, IncursionBiome incursionBiome) {
        this.biome = biome;
        this.levelIdentifier = null;
        this.incursionBiome = incursionBiome;
    }

    @Override
    public IDData getIDData() {
        return this.idData;
    }

    @Override
    public String getStringID() {
        return this.idData.getStringID();
    }

    @Override
    public int getID() {
        return this.idData.getID();
    }

    public void addBiomeLootEntry(String ... lootIDs) {
        for (String lootID : lootIDs) {
            this.biomeLoot.add(lootID);
        }
    }

    public void addMobEntries(String ... mobIDs) {
        for (String mobID : mobIDs) {
            Mob mob;
            if (!MobRegistry.mobExists(mobID) || (mob = MobRegistry.getMob(mobID, null)) == null) continue;
            MobJournalData mobData = new MobJournalData();
            mobData.mob = mob;
            mobData.itemDrops = new LootList();
            if (this.incursionBiome == null || !Objects.equals(mob.getStringID(), "ninja")) {
                LootTable lootTable = mob.getLootTable();
                lootTable.addPossibleLoot(mobData.itemDrops, new Object[0]);
            }
            LootTable privateLootTable = this.getPrivateLootTableExcludingDeepCaveIncursionBosses(mob);
            privateLootTable.addPossibleLoot(mobData.itemDrops, new Object[0]);
            LootTable additionalJournalLoot = mob.showAdditionalLootTableInJournal();
            additionalJournalLoot.addPossibleLoot(mobData.itemDrops, new Object[0]);
            if (this.biome != null && !mob.isCritter) {
                LootTable extraBiomeDrops = this.biome.getExtraBiomeMobDrops(this.levelIdentifier);
                extraBiomeDrops.addPossibleLoot(mobData.itemDrops, new Object[0]);
            } else if (this.incursionBiome != null) {
                LootTable extraIncursionDrops = this.incursionBiome.getExtraIncursionDrops(mob);
                extraIncursionDrops.addPossibleLoot(mobData.itemDrops, new Object[0]);
            }
            if (mob instanceof CavelingMob) {
                LootTable cavelingDropsAsLootTable = ((CavelingMob)mob).getCavelingDropsAsLootTable();
                cavelingDropsAsLootTable.addPossibleLoot(mobData.itemDrops, new Object[0]);
            }
            this.mobsData.add(mobData);
        }
    }

    public LootTable getPrivateLootTableExcludingDeepCaveIncursionBosses(Mob mob) {
        if (this.incursionBiome != null && (mob.getStringID().equals("reaper") || mob.getStringID().equals("cryoqueen") || mob.getStringID().equals("pestwarden") || mob.getStringID().equals("sageandgrit"))) {
            return new LootTable();
        }
        if (mob.getStringID().equals("sageandgrit")) {
            return FlyingSpiritsHead.privateLootTable;
        }
        return mob.getPrivateLootTable();
    }

    public void addTreasureEntry(LootTable ... lootTable) {
        if (this.treasuresData == null) {
            this.treasuresData = new LootList();
        }
        for (LootTable table : lootTable) {
            table.addPossibleLoot(this.treasuresData, new Object[0]);
        }
    }

    public void addTreasureEntry(String ... itemIDs) {
        if (this.treasuresData == null) {
            this.treasuresData = new LootList();
        }
        for (String itemID : itemIDs) {
            this.treasuresData.add(itemID);
        }
    }

    public void addEntryChallenges(String ... challengeStringIDs) {
        Integer[] challengeIDs = GameUtils.mapArray(challengeStringIDs, new Integer[0], JournalChallengeRegistry::getChallengeID);
        this.addEntryChallenges(challengeIDs);
    }

    public void addEntryChallenges(Integer ... challengeIDs) {
        Integer[] integerArray = challengeIDs;
        int n = integerArray.length;
        for (int i = 0; i < n; ++i) {
            int challengeID = integerArray[i];
            JournalChallengeRegistry.getChallenge(challengeID).setAttachedJournal(this);
            this.entryChallengeIDs.add(challengeID);
        }
    }

    public Iterable<JournalChallenge> getChallenges() {
        return GameUtils.mapIterable(this.entryChallengeIDs.iterator(), JournalChallengeRegistry::getChallenge);
    }

    public boolean checkRewardAvailable(ServerClient serverClient) {
        for (JournalChallenge challenge : this.getChallenges()) {
            if (challenge.isClaimed(serverClient) || !challenge.isCompleted(serverClient) || !challenge.hasReward(serverClient)) continue;
            return true;
        }
        return false;
    }

    public boolean canDiscoverWithLevelIdentifier(LevelIdentifier levelIdentifier) {
        if (this.levelIdentifier == null) {
            return true;
        }
        if (levelIdentifier == null) {
            return false;
        }
        return this.levelIdentifier.equals(levelIdentifier);
    }

    public boolean isDiscovered(PlayerStats stats) {
        return stats.discovered_journal_entries.isJournalDiscovered(this.getStringID());
    }

    public boolean isDiscovered(Client client) {
        return this.isDiscovered(client.characterStats);
    }

    public boolean isDiscovered(ServerClient client) {
        return this.isDiscovered(client.characterStats());
    }

    public GameMessage getLocalization() {
        return new LocalMessage("journal", this.getStringID());
    }

    public static class MobJournalData {
        public Mob mob;
        public LootList itemDrops;
    }
}

