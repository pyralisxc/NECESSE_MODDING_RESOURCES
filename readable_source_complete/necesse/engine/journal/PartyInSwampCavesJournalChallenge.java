/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.journal;

import necesse.engine.journal.JournalChallengeUtils;
import necesse.engine.journal.SimpleJournalChallenge;
import necesse.engine.journal.listeners.AdventurePartyChangedJournalChallengeListener;
import necesse.engine.journal.listeners.LevelChangedJournalChallengeListener;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.level.maps.Level;

public class PartyInSwampCavesJournalChallenge
extends SimpleJournalChallenge
implements LevelChangedJournalChallengeListener,
AdventurePartyChangedJournalChallengeListener {
    @Override
    public void onLevelChanged(ServerClient serverClient, Level oldLevel, Level newLevel) {
        if (this.isCompleted(serverClient) || !this.isJournalEntryDiscovered(serverClient)) {
            return;
        }
        this.checkCompletion(serverClient, newLevel);
    }

    @Override
    public void onPartyMemberAdded(ServerClient serverClient, HumanMob mob) {
        if (this.isCompleted(serverClient) || !this.isJournalEntryDiscovered(serverClient)) {
            return;
        }
        this.checkCompletion(serverClient, serverClient.getLevel());
    }

    @Override
    public void onPartyMemberRemoved(ServerClient serverClient, HumanMob mob, boolean becauseInvalid, boolean mobRemove, boolean isDeath) {
    }

    protected void checkCompletion(ServerClient serverClient, Level level) {
        if (!level.isCave || !JournalChallengeUtils.isSwampBiome(level.getBiome(serverClient.playerMob.getTileX(), serverClient.playerMob.getTileY()))) {
            return;
        }
        if (serverClient.adventureParty.getSize() >= 2) {
            this.markCompleted(serverClient);
            serverClient.forceCombineNewStats();
        }
    }
}

