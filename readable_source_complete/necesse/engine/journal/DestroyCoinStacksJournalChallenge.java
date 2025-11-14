/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.journal;

import necesse.engine.journal.ObjectsDestroyedJournalChallenge;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Attacker;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.OneWorldPirateVillageData;

public class DestroyCoinStacksJournalChallenge
extends ObjectsDestroyedJournalChallenge {
    public DestroyCoinStacksJournalChallenge() {
        super(5, true, "coin", "coinstack");
    }

    @Override
    public void onObjectDestroyed(GameObject object, Level level, int layerID, int tileX, int tileY, int objectRotation, Attacker attacker, ServerClient client) {
        OneWorldPirateVillageData pirateVillageData = OneWorldPirateVillageData.getPirateVillageData(level, false);
        if (pirateVillageData == null) {
            return;
        }
        if (!pirateVillageData.isPirateTile(tileX, tileY)) {
            return;
        }
        super.onObjectDestroyed(object, level, layerID, tileX, tileY, objectRotation, attacker, client);
    }
}

