/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.events;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.item.Item;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.level.maps.levelData.settlementData.SettlementInventory;

public class SettlementOpenStorageConfigEvent
extends ContainerEvent {
    public final int tileX;
    public final int tileY;
    public final Packet filterContent;
    public final int priority;

    public SettlementOpenStorageConfigEvent(SettlementInventory inventory) {
        this.tileX = inventory.tileX;
        this.tileY = inventory.tileY;
        if (inventory.filter == null) {
            this.filterContent = null;
        } else {
            this.filterContent = new Packet();
            inventory.filter.writePacket(new PacketWriter(this.filterContent));
        }
        this.priority = inventory.priority;
    }

    public SettlementOpenStorageConfigEvent(PacketReader reader) {
        super(reader);
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
        boolean valid = reader.getNextBoolean();
        if (valid) {
            this.priority = reader.getNextInt();
            this.filterContent = reader.getNextContentPacket();
        } else {
            this.filterContent = null;
            this.priority = 0;
        }
    }

    public ItemCategoriesFilter getFilter(final OEInventory oeInventory) {
        ItemCategoriesFilter filter = new ItemCategoriesFilter(false){

            @Override
            public boolean isItemDisabled(Item item) {
                if (super.isItemDisabled(item)) {
                    return true;
                }
                return oeInventory != null && oeInventory.isSettlementStorageItemDisabled(item);
            }
        };
        filter.readPacket(new PacketReader(this.filterContent));
        return filter;
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextInt(this.tileX);
        writer.putNextInt(this.tileY);
        writer.putNextBoolean(this.filterContent != null);
        if (this.filterContent != null) {
            writer.putNextInt(this.priority);
            writer.putNextContentPacket(this.filterContent);
        }
    }
}

