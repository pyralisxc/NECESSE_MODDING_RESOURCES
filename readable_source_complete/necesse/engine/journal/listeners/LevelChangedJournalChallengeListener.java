/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.journal.listeners;

import necesse.engine.network.server.ServerClient;
import necesse.level.maps.Level;

public interface LevelChangedJournalChallengeListener {
    public void onLevelChanged(ServerClient var1, Level var2, Level var3);
}

