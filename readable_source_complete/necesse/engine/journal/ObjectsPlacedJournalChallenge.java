/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.journal;

import necesse.engine.journal.IntJournalChallenge;
import necesse.engine.journal.listeners.ObjectPlacedJournalChallengeListener;
import necesse.engine.network.server.ServerClient;
import necesse.engine.playerStats.PlayerStats;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;

public class ObjectsPlacedJournalChallenge
extends IntJournalChallenge
implements ObjectPlacedJournalChallengeListener {
    protected String[] objectStringIDs;

    public ObjectsPlacedJournalChallenge(int total, String ... objectStringIDs) {
        super(total);
        this.objectStringIDs = objectStringIDs;
    }

    @Override
    public void markCompleted(ServerClient serverClient) {
        super.markCompleted(serverClient);
        serverClient.newStats.challenges_data.clearKey(this.getStringID() + "Places");
    }

    @Override
    protected int getProgress(PlayerStats stats) {
        return stats.challenges_data.getData().getInt(this.getStringID() + "Places");
    }

    @Override
    public void onObjectPlaced(GameObject object, Level level, int layerID, int tileX, int tileY, int objectRotation, ServerClient client) {
        if (this.isCompleted(client) || !this.isJournalEntryDiscovered(client)) {
            return;
        }
        for (String objectStringID : this.objectStringIDs) {
            if (!object.getStringID().equals(objectStringID)) continue;
            int next = this.getProgress(client.characterStats()) + 1;
            client.newStats.challenges_data.getData().setInt(this.getStringID() + "Places", next);
            if (next >= this.max) {
                this.markCompleted(client);
            }
            client.forceCombineNewStats();
            return;
        }
    }
}

