/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.job;

import java.util.ListIterator;
import java.util.stream.Stream;
import necesse.inventory.InventoryItem;

public interface WorkInventory {
    public ListIterator<InventoryItem> listIterator();

    public Iterable<InventoryItem> items();

    public Stream<InventoryItem> stream();

    public void markDirty();

    public void add(InventoryItem var1);

    public int getCanAddAmount(InventoryItem var1);

    public boolean isFull();

    public int getTotalItemStacks();

    public boolean isEmpty();
}

