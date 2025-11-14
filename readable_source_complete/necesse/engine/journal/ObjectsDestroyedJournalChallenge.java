/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.journal;

import necesse.engine.journal.IntJournalChallenge;
import necesse.engine.journal.listeners.ObjectDestroyedJournalChallengeListener;
import necesse.engine.network.server.ServerClient;
import necesse.engine.playerStats.PlayerStats;
import necesse.entity.mobs.Attacker;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;

public class ObjectsDestroyedJournalChallenge
extends IntJournalChallenge
implements ObjectDestroyedJournalChallengeListener {
    protected String[] objectStringIDs;
    protected boolean onlyNonPlayerPlaced;

    public ObjectsDestroyedJournalChallenge(int total, boolean onlyNonPlayerPlaced, String ... objectStringIDs) {
        super(total);
        this.objectStringIDs = objectStringIDs;
        this.onlyNonPlayerPlaced = onlyNonPlayerPlaced;
    }

    public ObjectsDestroyedJournalChallenge(int total, String ... objectStringIDs) {
        this(total, false, objectStringIDs);
    }

    @Override
    public void markCompleted(ServerClient serverClient) {
        super.markCompleted(serverClient);
        serverClient.newStats.challenges_data.clearKey(this.getStringID() + "Destroys");
    }

    @Override
    protected int getProgress(PlayerStats stats) {
        return stats.challenges_data.getData().getInt(this.getStringID() + "Destroys");
    }

    @Override
    public void onObjectDestroyed(GameObject object, Level level, int layerID, int tileX, int tileY, int objectRotation, Attacker attacker, ServerClient client) {
        if (this.isCompleted(client) || !this.isJournalEntryDiscovered(client)) {
            return;
        }
        for (String objectStringID : this.objectStringIDs) {
            if (this.onlyNonPlayerPlaced && level.objectLayer.isPlayerPlaced(layerID, tileX, tileY) || !object.getStringID().equals(objectStringID)) continue;
            int next = this.getProgress(client.characterStats()) + 1;
            client.newStats.challenges_data.getData().setInt(this.getStringID() + "Destroys", next);
            if (next >= this.max) {
                this.markCompleted(client);
            }
            client.forceCombineNewStats();
            return;
        }
    }
}

