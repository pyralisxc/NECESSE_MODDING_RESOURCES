/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.journal.listeners;

import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.friendly.human.HumanMob;

public interface AdventurePartyChangedJournalChallengeListener {
    public void onPartyMemberAdded(ServerClient var1, HumanMob var2);

    public void onPartyMemberRemoved(ServerClient var1, HumanMob var2, boolean var3, boolean var4, boolean var5);
}

