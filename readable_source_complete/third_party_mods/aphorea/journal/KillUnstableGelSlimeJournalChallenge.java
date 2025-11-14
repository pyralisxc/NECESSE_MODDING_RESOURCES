/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.journal.MobsKilledJournalChallenge
 *  necesse.engine.network.server.ServerClient
 *  necesse.entity.mobs.Mob
 */
package aphorea.journal;

import necesse.engine.journal.MobsKilledJournalChallenge;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;

public class KillUnstableGelSlimeJournalChallenge
extends MobsKilledJournalChallenge {
    public KillUnstableGelSlimeJournalChallenge() {
        super(1, new String[]{"unstablegelslime"});
    }

    public void onMobKilled(ServerClient serverClient, Mob mob) {
        super.onMobKilled(serverClient, mob);
    }
}

