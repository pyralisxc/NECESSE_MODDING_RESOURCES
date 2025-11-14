/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.journal.listeners;

import necesse.engine.network.server.ServerClient;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;

public interface ObjectPlacedJournalChallengeListener {
    public void onObjectPlaced(GameObject var1, Level var2, int var3, int var4, int var5, int var6, ServerClient var7);
}

