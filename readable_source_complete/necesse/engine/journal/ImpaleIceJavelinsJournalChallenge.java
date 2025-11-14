/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.journal;

import necesse.engine.journal.JournalChallengeUtils;
import necesse.engine.journal.SimpleJournalChallenge;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemIntArrayList;
import necesse.engine.network.gameNetworkData.GNDItemLongArrayList;
import necesse.engine.network.server.ServerClient;
import necesse.engine.playerStats.PlayerStats;
import necesse.entity.mobs.Mob;
import necesse.level.maps.Level;

public class ImpaleIceJavelinsJournalChallenge
extends SimpleJournalChallenge {
    public static int TIME_SINCE_FIRST_JAVELIN = 5000;
    public static int JAVELIN_HIT_COUNT = 5;

    protected String getMobsKey() {
        return this.getStringID() + "Mobs";
    }

    protected String getHitsKey(int mobUniqueID) {
        return this.getStringID() + "Hits" + mobUniqueID;
    }

    protected GNDItemIntArrayList getMobsHit(PlayerStats stats) {
        GNDItem mobs = stats.challenges_data.getData().getItem(this.getMobsKey());
        if (mobs instanceof GNDItemIntArrayList) {
            return (GNDItemIntArrayList)mobs;
        }
        return new GNDItemIntArrayList();
    }

    protected GNDItemLongArrayList getHitTimes(PlayerStats stats, int mobUniqueID) {
        GNDItem mobs = stats.challenges_data.getData().getItem(this.getHitsKey(mobUniqueID));
        if (mobs instanceof GNDItemLongArrayList) {
            return (GNDItemLongArrayList)mobs;
        }
        return new GNDItemLongArrayList();
    }

    public void clearData(ServerClient client) {
        GNDItemIntArrayList mobs = this.getMobsHit(client.characterStats());
        for (Integer mobUniqueID : mobs) {
            client.newStats.challenges_data.clearKey(this.getHitsKey(mobUniqueID));
        }
        client.newStats.challenges_data.clearKey(this.getMobsKey());
    }

    public void submitIceJavelinImpale(ServerClient client, Mob mob) {
        if (this.isCompleted(client) || !this.isJournalEntryDiscovered(client)) {
            return;
        }
        Level level = mob.getLevel();
        if (!level.isCave || !JournalChallengeUtils.isSnowBiome(level.getBiome(mob.getTileX(), mob.getTileY()))) {
            return;
        }
        long currentTime = mob.getTime();
        PlayerStats stats = client.characterStats();
        GNDItemIntArrayList mobs = this.getMobsHit(stats);
        boolean foundCurrentMob = false;
        boolean shouldUpdateMobs = false;
        boolean shouldForceUpdate = false;
        for (int i = 0; i < mobs.size(); ++i) {
            int mobUniqueID = mobs.get(i);
            GNDItemLongArrayList hitTimes = this.getHitTimes(stats, mobUniqueID);
            int validCount = 0;
            boolean shouldUpdateHitTimes = false;
            if (mobUniqueID == mob.getUniqueID()) {
                foundCurrentMob = true;
                shouldUpdateHitTimes = true;
                hitTimes = (GNDItemLongArrayList)hitTimes.copy();
                hitTimes.add(currentTime);
            }
            for (int j = 0; j < hitTimes.size(); ++j) {
                long timeSince = currentTime - hitTimes.get(j);
                if (timeSince > (long)TIME_SINCE_FIRST_JAVELIN) {
                    shouldUpdateHitTimes = true;
                    hitTimes = (GNDItemLongArrayList)hitTimes.copy();
                    hitTimes.remove(j);
                    --j;
                    continue;
                }
                if (++validCount >= JAVELIN_HIT_COUNT) break;
            }
            if (validCount >= JAVELIN_HIT_COUNT) {
                this.clearData(client);
                this.markCompleted(client);
                client.forceCombineNewStats();
                return;
            }
            if (!shouldUpdateHitTimes) continue;
            shouldForceUpdate = true;
            if (hitTimes.isEmpty()) {
                shouldUpdateMobs = true;
                mobs = (GNDItemIntArrayList)mobs.copy();
                mobs.remove(i);
                --i;
                client.newStats.challenges_data.clearKey(this.getHitsKey(mobUniqueID));
                continue;
            }
            client.newStats.challenges_data.getData().setItem(this.getHitsKey(mobUniqueID), (GNDItem)hitTimes);
        }
        if (!foundCurrentMob) {
            shouldUpdateMobs = true;
            mobs = (GNDItemIntArrayList)mobs.copy();
            mobs.add(mob.getUniqueID());
            client.newStats.challenges_data.getData().setItem(this.getHitsKey(mob.getUniqueID()), (GNDItem)new GNDItemLongArrayList(currentTime));
        }
        if (shouldUpdateMobs) {
            shouldForceUpdate = true;
            if (mobs.isEmpty()) {
                client.newStats.challenges_data.clearKey(this.getMobsKey());
            } else {
                client.newStats.challenges_data.getData().setItem(this.getMobsKey(), (GNDItem)mobs);
            }
        }
        if (shouldForceUpdate) {
            client.forceCombineNewStats();
        }
    }
}

