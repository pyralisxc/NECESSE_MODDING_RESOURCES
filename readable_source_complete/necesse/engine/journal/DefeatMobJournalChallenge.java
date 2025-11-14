/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.journal;

import necesse.engine.journal.SimpleJournalChallenge;
import necesse.engine.journal.listeners.MobKilledJournalChallengeListener;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;

public class DefeatMobJournalChallenge
extends SimpleJournalChallenge
implements MobKilledJournalChallengeListener {
    protected String[] mobStringIDs;

    public DefeatMobJournalChallenge(String ... mobStringID) {
        this.mobStringIDs = mobStringID;
    }

    @Override
    public void onMobKilled(ServerClient serverClient, Mob mob) {
        if (this.isCompleted(serverClient) || !this.isJournalEntryDiscovered(serverClient)) {
            return;
        }
        for (String mobStringID : this.mobStringIDs) {
            if (!mob.getStringID().equals(mobStringID)) continue;
            this.markCompleted(serverClient);
        }
    }
}

