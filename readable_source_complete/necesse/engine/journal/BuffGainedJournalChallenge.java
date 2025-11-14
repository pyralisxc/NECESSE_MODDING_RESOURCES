/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.journal;

import necesse.engine.journal.SimpleJournalChallenge;
import necesse.engine.journal.listeners.BuffGainedJournalChallengeListener;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;

public class BuffGainedJournalChallenge
extends SimpleJournalChallenge
implements BuffGainedJournalChallengeListener {
    protected String[] buffStringIDs;

    public BuffGainedJournalChallenge(String ... buffStringIDs) {
        this.buffStringIDs = buffStringIDs;
    }

    @Override
    public void onBuffGained(ServerClient serverClient, PlayerMob player, ActiveBuff activeBuff, boolean isOverride) {
        if (this.isCompleted(serverClient) || !this.isJournalEntryDiscovered(serverClient)) {
            return;
        }
        for (String buffStringID : this.buffStringIDs) {
            if (!activeBuff.buff.getStringID().equals(buffStringID)) continue;
            this.markCompleted(serverClient);
            serverClient.forceCombineNewStats();
            return;
        }
    }
}

