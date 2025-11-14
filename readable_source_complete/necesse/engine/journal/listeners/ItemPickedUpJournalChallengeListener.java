/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.journal.listeners;

import necesse.engine.network.server.ServerClient;
import necesse.entity.pickup.ItemPickupEntity;

public interface ItemPickedUpJournalChallengeListener {
    public void onItemPickedUp(ServerClient var1, ItemPickupEntity var2, int var3, boolean var4);
}

