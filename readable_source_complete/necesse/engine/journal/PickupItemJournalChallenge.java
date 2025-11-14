/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.journal;

import necesse.engine.journal.SimpleJournalChallenge;
import necesse.engine.journal.listeners.ItemPickedUpJournalChallengeListener;
import necesse.engine.network.server.ServerClient;
import necesse.entity.pickup.ItemPickupEntity;

public class PickupItemJournalChallenge
extends SimpleJournalChallenge
implements ItemPickedUpJournalChallengeListener {
    protected String[] itemStringIDs;
    protected boolean onlyByNonPlayerDropped;

    public PickupItemJournalChallenge(boolean onlyByNonPlayerDropped, String ... itemStringIDs) {
        this.onlyByNonPlayerDropped = onlyByNonPlayerDropped;
        this.itemStringIDs = itemStringIDs;
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
            this.markCompleted(serverClient);
            serverClient.forceCombineNewStats();
            return;
        }
    }
}

