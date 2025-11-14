/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem;

import java.util.ArrayList;
import java.util.Comparator;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.ComparableSequence;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.SlotPriority;
import necesse.level.maps.Level;

public interface AdventurePartyConsumableItem {
    default public boolean canAddToPartyInventory(InventoryItem item, NetworkClient client, Inventory inventory, int slot) {
        return true;
    }

    public boolean canAndShouldPartyConsume(Level var1, HumanMob var2, ServerClient var3, InventoryItem var4, String var5);

    public InventoryItem onPartyConsume(Level var1, HumanMob var2, ServerClient var3, InventoryItem var4, String var5);

    default public boolean shouldPreventHit(Level level, HumanMob mob, ServerClient partyClient, InventoryItem item) {
        return false;
    }

    default public ComparableSequence<Integer> getPartyPriority(Level level, HumanMob mob, ServerClient partyClient, Inventory inventory, int inventorySlot, InventoryItem item, String purpose) {
        return new ComparableSequence<Integer>(inventorySlot);
    }

    public static ArrayList<SlotPriority> getPartyPriorityList(Level level, HumanMob mob, ServerClient partyClient, Inventory inventory, String purpose) {
        ArrayList<SlotPriority> priorityList = new ArrayList<SlotPriority>();
        for (int i2 = 0; i2 <= inventory.getSize(); ++i2) {
            AdventurePartyConsumableItem partyItem;
            InventoryItem invItem = inventory.getItem(i2);
            if (invItem == null || !(invItem.item instanceof AdventurePartyConsumableItem) || !(partyItem = (AdventurePartyConsumableItem)((Object)invItem.item)).canAndShouldPartyConsume(level, mob, partyClient, invItem, purpose)) continue;
            priorityList.add(new SlotPriority(i2, partyItem.getPartyPriority(level, mob, partyClient, inventory, i2, invItem, purpose)));
        }
        Comparator<SlotPriority> comparator = Comparator.comparing(i -> i.comparable);
        priorityList.sort(comparator);
        return priorityList;
    }
}

