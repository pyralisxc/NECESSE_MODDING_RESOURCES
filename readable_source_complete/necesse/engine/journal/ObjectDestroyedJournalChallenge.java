/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.journal;

import necesse.engine.journal.SimpleJournalChallenge;
import necesse.engine.journal.listeners.ObjectDestroyedJournalChallengeListener;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Attacker;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;

public class ObjectDestroyedJournalChallenge
extends SimpleJournalChallenge
implements ObjectDestroyedJournalChallengeListener {
    protected String[] objectStringIDs;

    public ObjectDestroyedJournalChallenge(String ... objectStringIDs) {
        this.objectStringIDs = objectStringIDs;
    }

    @Override
    public void onObjectDestroyed(GameObject object, Level level, int layerID, int tileX, int tileY, int objectRotation, Attacker attacker, ServerClient client) {
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

