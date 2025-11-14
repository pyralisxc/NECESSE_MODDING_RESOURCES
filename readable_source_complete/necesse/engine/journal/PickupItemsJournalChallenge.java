/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.journal;

import necesse.engine.journal.IntJournalChallenge;
import necesse.engine.journal.listeners.ItemPickedUpJournalChallengeListener;
import necesse.engine.network.server.ServerClient;
import necesse.engine.playerStats.PlayerStats;
import necesse.entity.pickup.ItemPickupEntity;

public class PickupItemsJournalChallenge
extends IntJournalChallenge
implements ItemPickedUpJournalChallengeListener {
    protected String[] itemStringIDs;
    protected boolean onlyByNonPlayerDropped;

    public PickupItemsJournalChallenge(int total, boolean onlyByNonPlayerDropped, String ... itemStringIDs) {
        super(total);
        this.onlyByNonPlayerDropped = onlyByNonPlayerDropped;
        this.itemStringIDs = itemStringIDs;
    }

    @Override
    public void markCompleted(ServerClient serverClient) {
        super.markCompleted(serverClient);
        serverClient.newStats.challenges_data.clearKey(this.getStringID() + "Pickups");
    }

    @Override
    protected int getProgress(PlayerStats stats) {
        return stats.challenges_data.getData().getInt(this.getStringID() + "Pickups");
    }

    @Override
    public void onItemPickedUp(ServerClient serverClient, ItemPickupEntity entity, int amount, boolean addedToNonPlayerInventory) {
        if (this.isCompleted(serverClient) || !this.isJournalEntryDiscovered(serverClient)) {
            return;
        }
        if (this.onlyByNonPlayerDropped && entity.droppedByPlayer) {
            return;
        }
        for (String itemStringID : this.itemStringIDs) {
            if (!entity.item.item.getStringID().equals(itemStringID)) continue;
            int next = this.getProgress(serverClient.characterStats()) + amount;
            serverClient.newStats.challenges_data.getData().setInt(this.getStringID() + "Pickups", next);
            if (next >= this.max) {
                this.markCompleted(serverClient);
            }
            serverClient.forceCombineNewStats();
            return;
        }
    }
}

