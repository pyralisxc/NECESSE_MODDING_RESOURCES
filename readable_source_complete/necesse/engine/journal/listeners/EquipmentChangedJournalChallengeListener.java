/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.journal.listeners;

import java.util.ArrayList;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.PlayerEquipmentInventory;
import necesse.inventory.PlayerEquipmentSetInventoryManager;

public interface EquipmentChangedJournalChallengeListener {
    public void onEquipmentChanged(ServerClient var1, PlayerEquipmentSetInventoryManager var2, ArrayList<PlayerEquipmentInventory> var3, PlayerEquipmentInventory var4, int var5);
}

