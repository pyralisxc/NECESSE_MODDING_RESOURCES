/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.journal;

import java.util.Arrays;
import java.util.HashSet;
import necesse.engine.journal.SimpleJournalChallenge;
import necesse.engine.journal.listeners.SettlerEquipmentChangedJournalChallengeListener;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class EquipSettlerWithQuartzJournalChallenge
extends SimpleJournalChallenge
implements SettlerEquipmentChangedJournalChallengeListener {
    public static HashSet<String> helmetStringIDs = new HashSet<String>(Arrays.asList("quartzhelmet", "quartzcrown"));
    public static String chestStringID = "quartzchestplate";
    public static String bootsStringID = "quartzboots";

    @Override
    public void onSettlerEquipmentChanged(ServerClient serverClient, ServerSettlementData settlement, LevelSettler settler, InventoryRange inventoryRange, int slot, boolean isCosmetic) {
        if (isCosmetic) {
            return;
        }
        if (this.isCompleted(serverClient) || !this.isJournalEntryDiscovered(serverClient)) {
            return;
        }
        boolean hasBoots = false;
        boolean hasChest = false;
        boolean hasHelmet = false;
        for (int i = inventoryRange.startSlot; i <= inventoryRange.endSlot; ++i) {
            InventoryItem item = inventoryRange.inventory.getItem(i);
            if (item == null) continue;
            if (!hasBoots && item.item.getStringID().equals(bootsStringID)) {
                hasBoots = true;
            }
            if (!hasChest && item.item.getStringID().equals(chestStringID)) {
                hasChest = true;
            }
            if (hasHelmet || !helmetStringIDs.contains(item.item.getStringID())) continue;
            hasHelmet = true;
        }
        if (hasBoots && hasChest && hasHelmet) {
            this.markCompleted(serverClient);
            serverClient.forceCombineNewStats();
        }
    }
}

