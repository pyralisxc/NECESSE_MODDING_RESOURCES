/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.journal.listeners;

import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;

public interface BuffGainedJournalChallengeListener {
    public void onBuffGained(ServerClient var1, PlayerMob var2, ActiveBuff var3, boolean var4);
}

