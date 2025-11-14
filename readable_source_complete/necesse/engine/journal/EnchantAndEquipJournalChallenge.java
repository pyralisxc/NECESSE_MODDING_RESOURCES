/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.journal;

import java.util.ArrayList;
import necesse.engine.journal.JournalChallengeUtils;
import necesse.engine.journal.SimpleJournalChallenge;
import necesse.engine.journal.listeners.EquipmentChangedJournalChallengeListener;
import necesse.engine.journal.listeners.LevelChangedJournalChallengeListener;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerEquipmentInventory;
import necesse.inventory.PlayerEquipmentSetInventoryManager;
import necesse.inventory.enchants.Enchantable;
import necesse.level.maps.Level;

public class EnchantAndEquipJournalChallenge
extends SimpleJournalChallenge
implements EquipmentChangedJournalChallengeListener,
LevelChangedJournalChallengeListener {
    @Override
    public void onEquipmentChanged(ServerClient serverClient, PlayerEquipmentSetInventoryManager manager, ArrayList<PlayerEquipmentInventory> list, PlayerEquipmentInventory changedInventory, int changedSlot) {
        if (list != manager.armor) {
            return;
        }
        if (this.isCompleted(serverClient) || !this.isJournalEntryDiscovered(serverClient)) {
            return;
        }
        Level level = serverClient.playerMob.getLevel();
        if (!level.isCave || !JournalChallengeUtils.isSwampBiome(level.getBiome(serverClient.playerMob.getTileX(), serverClient.playerMob.getTileY()))) {
            return;
        }
        InventoryItem changedItem = changedInventory.getItem(changedSlot);
        if (changedItem != null) {
            this.checkEquipment(serverClient, changedInventory);
        }
    }

    @Override
    public void onLevelChanged(ServerClient serverClient, Level oldLevel, Level newLevel) {
        if (this.isCompleted(serverClient) || !this.isJournalEntryDiscovered(serverClient)) {
            return;
        }
        Level level = serverClient.playerMob.getLevel();
        if (!level.isCave || !JournalChallengeUtils.isSwampBiome(level.getBiome(serverClient.playerMob.getTileX(), serverClient.playerMob.getTileY()))) {
            return;
        }
        this.checkEquipment(serverClient, serverClient.playerMob.getInv().equipment.getSelectedArmorInventory());
    }

    public void checkEquipment(ServerClient serverClient, PlayerEquipmentInventory armorInventory) {
        for (int slot = 0; slot < armorInventory.getSize(); ++slot) {
            InventoryItem item = armorInventory.getCurrentUsedItem(slot);
            if (this.isValidItem(item)) continue;
            return;
        }
        this.markCompleted(serverClient);
        serverClient.forceCombineNewStats();
    }

    public boolean isValidItem(InventoryItem item) {
        return item != null && item.item instanceof Enchantable && ((Enchantable)((Object)item.item)).getEnchantmentID(item) > 0;
    }
}

