/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.journal;

import necesse.engine.journal.SimpleJournalChallenge;
import necesse.engine.journal.listeners.ObjectPlacedJournalChallengeListener;
import necesse.engine.network.server.ServerClient;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;

public class ObjectPlacedJournalChallenge
extends SimpleJournalChallenge
implements ObjectPlacedJournalChallengeListener {
    protected String[] objectStringIDs;

    public ObjectPlacedJournalChallenge(String ... objectStringIDs) {
        this.objectStringIDs = objectStringIDs;
    }

    @Override
    public void onObjectPlaced(GameObject object, Level level, int layerID, int tileX, int tileY, int objectRotation, ServerClient client) {
        if (this.isCompleted(client) || !this.isJournalEntryDiscovered(client)) {
            return;
        }
        for (String objectStringID : this.objectStringIDs) {
            if (!object.getStringID().equals(objectStringID)) continue;
            this.markCompleted(client);
            client.forceCombineNewStats();
            return;
        }
    }
}

