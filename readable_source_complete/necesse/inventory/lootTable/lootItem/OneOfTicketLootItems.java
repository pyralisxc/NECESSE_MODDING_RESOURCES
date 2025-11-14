/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.lootItem;

import java.util.List;
import necesse.engine.util.GameRandom;
import necesse.engine.util.ProtectedTicketSystemList;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootList;

public class OneOfTicketLootItems
extends ProtectedTicketSystemList<LootItemInterface>
implements LootItemInterface {
    public static final int defaultTickets = 100;

    public OneOfTicketLootItems(Object ... items) {
        this.addAll(items);
    }

    public OneOfTicketLootItems(OneOfTicketLootItems copy, Object ... items) {
        super(copy);
        this.addAll(items);
    }

    protected void addAll(Object ... items) {
        int nextTickets = 100;
        for (Object item : items) {
            if (item instanceof Integer) {
                nextTickets = (Integer)item;
                continue;
            }
            if (item instanceof LootItemInterface) {
                this.addLoot(nextTickets, (LootItemInterface)item);
                nextTickets = 100;
                continue;
            }
            throw new IllegalArgumentException("Unknown object  " + item + ". Must be either Integer or LootItemInterface.");
        }
    }

    @Override
    public void addPossibleLoot(LootList list, Object ... extra) {
        for (ProtectedTicketSystemList.TicketObject item : this.getTicketItems()) {
            ((LootItemInterface)item.object).addPossibleLoot(list, extra);
        }
    }

    @Override
    public void addItems(List<InventoryItem> list, GameRandom random, float lootMultiplier, Object ... extra) {
        ((LootItemInterface)this.getRandomObject(random)).addItems(list, random, lootMultiplier, extra);
    }

    public OneOfTicketLootItems addLoot(int tickets, LootItemInterface loot) {
        this.addObject(tickets, loot);
        return this;
    }

    public OneOfTicketLootItems addAll(OneOfTicketLootItems loot) {
        super.addAll(loot);
        return this;
    }
}

