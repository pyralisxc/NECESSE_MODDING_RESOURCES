/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.journal;

import java.util.ArrayList;
import necesse.engine.journal.ItemObtainedJournalChallenge;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootList;
import necesse.inventory.lootTable.presets.DeepCaveChestLootTable;

public class ObtainDeepDesertTrinketJournalChallenge
extends ItemObtainedJournalChallenge {
    public ObtainDeepDesertTrinketJournalChallenge() {
        super(new String[0]);
    }

    @Override
    public void onChallengeRegistryClosed() {
        super.onChallengeRegistryClosed();
        ArrayList<String> itemStringIDs = new ArrayList<String>();
        LootList list = new LootList();
        DeepCaveChestLootTable.desertMainItems.addPossibleLoot(list, new Object[0]);
        for (InventoryItem invItem : list.getCombinedItemsAndCustomItems()) {
            if (!invItem.item.isTrinketItem()) continue;
            itemStringIDs.add(invItem.item.getStringID());
        }
        this.itemStringIDs = itemStringIDs.toArray(new String[0]);
    }
}

