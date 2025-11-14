/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.object;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.PacketReader;
import necesse.entity.objectEntity.FueledIncineratorObjectEntity;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryRange;
import necesse.inventory.container.object.OEInventoryContainer;
import necesse.inventory.container.settlement.events.SettlementDataEvent;
import necesse.inventory.container.slots.OEInventoryContainerSlot;

public class FueledIncineratorInventoryContainer
extends OEInventoryContainer {
    public final FueledIncineratorObjectEntity incineratorObjectEntity;
    public int FUEL_START = -1;
    public int FUEL_END = -1;

    public FueledIncineratorInventoryContainer(NetworkClient client, int uniqueSeed, SettlementDataEvent settlement, FueledIncineratorObjectEntity incineratorObjectEntity, PacketReader reader) {
        super(client, uniqueSeed, settlement, incineratorObjectEntity, reader);
        this.incineratorObjectEntity = incineratorObjectEntity;
        for (int i = 0; i < incineratorObjectEntity.fuelSlots; ++i) {
            int index = this.addSlot(new OEInventoryContainerSlot(incineratorObjectEntity, i));
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
        return new InventoryRange(inventory, ((FueledIncineratorObjectEntity)this.objectEntity).fuelSlots, inventory.getSize() - 1);
    }
}

