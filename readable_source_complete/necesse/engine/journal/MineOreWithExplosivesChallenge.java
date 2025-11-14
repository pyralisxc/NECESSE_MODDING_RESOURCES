/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.journal;

import necesse.engine.journal.JournalChallengeUtils;
import necesse.engine.journal.SimpleJournalChallenge;
import necesse.engine.journal.listeners.ObjectDestroyedJournalChallengeListener;
import necesse.engine.network.server.ServerClient;
import necesse.entity.levelEvent.explosionEvent.ExplosionEvent;
import necesse.entity.mobs.Attacker;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;

public class MineOreWithExplosivesChallenge
extends SimpleJournalChallenge
implements ObjectDestroyedJournalChallengeListener {
    @Override
    public void onObjectDestroyed(GameObject object, Level level, int layerID, int tileX, int tileY, int objectRotation, Attacker attacker, ServerClient client) {
        if (this.isCompleted(client) || !this.isJournalEntryDiscovered(client)) {
            return;
        }
        if (!level.isCave || !object.isOre) {
            return;
        }
        if (!JournalChallengeUtils.isForestBiome(level.getBiome(tileX, tileY))) {
            return;
        }
        if (!(attacker instanceof ExplosionEvent)) {
            return;
        }
        this.markCompleted(client);
        client.forceCombineNewStats();
    }
}

