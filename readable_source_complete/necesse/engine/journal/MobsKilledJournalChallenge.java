/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.journal;

import necesse.engine.journal.IntJournalChallenge;
import necesse.engine.journal.listeners.MobKilledJournalChallengeListener;
import necesse.engine.network.server.ServerClient;
import necesse.engine.playerStats.PlayerStats;
import necesse.entity.mobs.Mob;

public class MobsKilledJournalChallenge
extends IntJournalChallenge
implements MobKilledJournalChallengeListener {
    protected String[] mobStringIDs;

    public MobsKilledJournalChallenge(int kills, String ... mobStringIDs) {
        super(kills);
        this.mobStringIDs = mobStringIDs;
    }

    @Override
    public void markCompleted(ServerClient serverClient) {
        super.markCompleted(serverClient);
        serverClient.newStats.challenges_data.clearKey(this.getStringID() + "Kills");
    }

    @Override
    protected int getProgress(PlayerStats stats) {
        return stats.challenges_data.getData().getInt(this.getStringID() + "Kills");
    }

    @Override
    public void onMobKilled(ServerClient serverClient, Mob mob) {
        if (this.isCompleted(serverClient) || !this.isJournalEntryDiscovered(serverClient)) {
            return;
        }
        for (String mobStringID : this.mobStringIDs) {
            if (!mob.getStringID().equals(mobStringID)) continue;
            this.addKill(serverClient);
            return;
        }
    }

    protected void addKill(ServerClient serverClient) {
        int next = this.getProgress(serverClient.characterStats()) + 1;
        serverClient.newStats.challenges_data.getData().setInt(this.getStringID() + "Kills", next);
        if (next >= this.max) {
            this.markCompleted(serverClient);
        }
        serverClient.forceCombineNewStats();
    }
}

