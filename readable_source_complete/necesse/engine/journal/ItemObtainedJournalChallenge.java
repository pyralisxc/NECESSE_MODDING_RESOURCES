/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.journal;

import java.util.ArrayList;
import necesse.engine.journal.SimpleJournalChallenge;
import necesse.engine.journal.listeners.ItemObtainedJournalChallengeListener;
import necesse.engine.network.server.ServerClient;

public class ItemObtainedJournalChallenge
extends SimpleJournalChallenge
implements ItemObtainedJournalChallengeListener {
    protected String[] itemStringIDs;

    public ItemObtainedJournalChallenge(String ... itemStringID) {
        this.itemStringIDs = itemStringID;
    }

    public ItemObtainedJournalChallenge(ArrayList<String> itemStringID) {
        this(itemStringID.toArray(new String[0]));
    }

    @Override
    public void onNewItemObtained(ServerClient serverClient, String stringID) {
        if (this.isCompleted(serverClient) || !this.isJournalEntryDiscovered(serverClient)) {
            return;
        }
        for (String itemStringID : this.itemStringIDs) {
            if (!stringID.equals(itemStringID)) continue;
            this.markCompleted(serverClient);
            serverClient.forceCombineNewStats();
            return;
        }
    }
}

