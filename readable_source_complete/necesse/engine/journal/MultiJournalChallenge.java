/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.journal;

import necesse.engine.journal.JournalChallenge;
import necesse.engine.journal.JournalEntry;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.ServerClient;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.registries.JournalChallengeRegistry;
import necesse.engine.util.GameUtils;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormFlow;
import necesse.inventory.lootTable.LootTable;

public class MultiJournalChallenge
extends JournalChallenge {
    public final JournalChallenge[] challenges;
    public LootTable reward;

    public MultiJournalChallenge(JournalChallenge ... challenges) {
        this.challenges = challenges;
    }

    public MultiJournalChallenge(Integer ... challengeIDs) {
        this(GameUtils.mapArray(challengeIDs, new JournalChallenge[0], JournalChallengeRegistry::getChallenge));
    }

    public MultiJournalChallenge setReward(LootTable reward) {
        this.reward = reward;
        return this;
    }

    @Override
    public GameMessage getName() {
        return null;
    }

    @Override
    public void setAttachedJournal(JournalEntry journalEntry) {
        super.setAttachedJournal(journalEntry);
        for (JournalChallenge challenge : this.challenges) {
            challenge.setAttachedJournal(journalEntry);
        }
    }

    @Override
    protected boolean isCompleted(PlayerStats stats) {
        for (JournalChallenge challenge : this.challenges) {
            if (challenge.isCompleted(stats)) continue;
            return false;
        }
        return true;
    }

    @Override
    public void markCompleted(ServerClient serverClient) {
        super.markCompleted(serverClient);
        for (JournalChallenge challenge : this.challenges) {
            challenge.markCompleted(serverClient);
        }
    }

    @Override
    public void markClaimed(ServerClient serverClient) {
        super.markClaimed(serverClient);
        for (JournalChallenge challenge : this.challenges) {
            challenge.markClaimed(serverClient);
        }
    }

    @Override
    public boolean hasReward(NetworkClient client) {
        if (this.reward != null && !this.reward.items.isEmpty()) {
            return true;
        }
        for (JournalChallenge challenge : this.challenges) {
            if (!challenge.hasReward(client)) continue;
            return true;
        }
        return false;
    }

    @Override
    public LootTable getReward() {
        LootTable lootTable = this.reward != null ? this.reward : new LootTable();
        for (JournalChallenge challenge : this.challenges) {
            LootTable reward = challenge.getReward();
            lootTable.items.add(reward);
        }
        return lootTable;
    }

    @Override
    public void addJournalFormContent(Client client, FormContentBox contentBox, FormFlow flow) {
        for (JournalChallenge challenge : this.challenges) {
            challenge.addJournalFormContent(client, contentBox, flow);
        }
    }
}

