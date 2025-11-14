/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.journal.listeners;

import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;

public interface MobKilledJournalChallengeListener {
    public void onMobKilled(ServerClient var1, Mob var2);
}

