/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.journal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import necesse.engine.journal.SimpleJournalChallenge;
import necesse.engine.journal.listeners.EquipmentChangedJournalChallengeListener;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerEquipmentInventory;
import necesse.inventory.PlayerEquipmentSetInventoryManager;

public class EquippedArmorSetJournalChallenge
extends SimpleJournalChallenge
implements EquipmentChangedJournalChallengeListener {
    protected String chestStringID;
    protected String bootsStringID;
    protected HashSet<String> helmetStringIDs = new HashSet();

    public EquippedArmorSetJournalChallenge(String bootsStringID, String chestStringID, String ... helmetStringIDs) {
        this.bootsStringID = bootsStringID;
        this.chestStringID = chestStringID;
        Collections.addAll(this.helmetStringIDs, helmetStringIDs);
    }

    @Override
    public void onEquipmentChanged(ServerClient serverClient, PlayerEquipmentSetInventoryManager manager, ArrayList<PlayerEquipmentInventory> list, PlayerEquipmentInventory changedInventory, int changedSlot) {
        if (list != manager.armor) {
            return;
        }
        if (this.isCompleted(serverClient) || !this.isJournalEntryDiscovered(serverClient)) {
            return;
        }
        boolean hasBoots = false;
        boolean hasChest = false;
        boolean hasHelmet = false;
        for (int slot = 0; slot < changedInventory.getSize(); ++slot) {
            InventoryItem item = changedInventory.getCurrentUsedItem(slot);
            if (item == null) continue;
            if (!hasBoots && item.item.getStringID().equals(this.bootsStringID)) {
                hasBoots = true;
            }
            if (!hasChest && item.item.getStringID().equals(this.chestStringID)) {
                hasChest = true;
            }
            if (hasHelmet || !this.helmetStringIDs.contains(item.item.getStringID())) continue;
            hasHelmet = true;
        }
        if (hasBoots && hasChest && hasHelmet) {
            this.markCompleted(serverClient);
            serverClient.forceCombineNewStats();
        }
    }
}

