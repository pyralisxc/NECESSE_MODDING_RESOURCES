/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.object;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.PacketReader;
import necesse.entity.objectEntity.FueledRefrigeratorObjectEntity;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryRange;
import necesse.inventory.container.object.OEInventoryContainer;
import necesse.inventory.container.settlement.events.SettlementDataEvent;
import necesse.inventory.container.slots.OEInventoryContainerSlot;

public class FueledRefrigeratorInventoryContainer
extends OEInventoryContainer {
    public final FueledRefrigeratorObjectEntity refrigeratorObjectEntity;
    public int FUEL_START = -1;
    public int FUEL_END = -1;

    public FueledRefrigeratorInventoryContainer(NetworkClient client, int uniqueSeed, SettlementDataEvent settlement, FueledRefrigeratorObjectEntity refrigeratorObjectEntity, PacketReader reader) {
        super(client, uniqueSeed, settlement, refrigeratorObjectEntity, reader);
        this.refrigeratorObjectEntity = refrigeratorObjectEntity;
        for (int i = 0; i < refrigeratorObjectEntity.fuelSlots; ++i) {
            int index = this.addSlot(new OEInventoryContainerSlot(refrigeratorObjectEntity, i));
            if (this.FUEL_START == -1) {
                this.FUEL_START = index;
            }
            if (this.FUEL_END == -1) {
                this.FUEL_END = index;
            }
            this.FUEL_START = Math.min(this.FUEL_START, index);
            this.FUEL_END = Math.max(this.FUEL_END, index);
        }
        this.addInventoryQuickTransfer(this.FUEL_START, this.FUEL_END);
    }

    @Override
    public InventoryRange getOEInventoryRange() {
        Inventory inventory = this.oeInventory.getInventory();
        return new InventoryRange(inventory, ((FueledRefrigeratorObjectEntity)this.objectEntity).fuelSlots, inventory.getSize() - 1);
    }
}

