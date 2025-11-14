/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.pickup;

import necesse.entity.pickup.ItemPickupEntity;
import necesse.entity.pickup.ItemPickupReservedAmount;

public class ItemPickupReservedCombinedEvent {
    public final ItemPickupReservedAmount reserved;
    public final ItemPickupEntity next;
    public final int combinedAmount;

    public ItemPickupReservedCombinedEvent(ItemPickupReservedAmount reserved, ItemPickupEntity next, int combinedAmount) {
        this.reserved = reserved;
        this.next = next;
        this.combinedAmount = combinedAmount;
    }
}

