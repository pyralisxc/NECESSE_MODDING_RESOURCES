/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.events;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class SettlementStorageFullUpdateEvent
extends ContainerEvent {
    public final int settlementUniqueID;
    public final int tileX;
    public final int tileY;
    public final int priority;
    public final Packet filterContent;

    public SettlementStorageFullUpdateEvent(ServerSettlementData data, int tileX, int tileY, ItemCategoriesFilter filter, int priority) {
        this.settlementUniqueID = data.uniqueID;
        this.tileX = tileX;
        this.tileY = tileY;
        this.priority = priority;
        this.filterContent = new Packet();
        filter.writePacket(new PacketWriter(this.filterContent));
    }

    public SettlementStorageFullUpdateEvent(PacketReader reader) {
        super(reader);
        this.settlementUniqueID = reader.getNextInt();
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
        this.priority = reader.getNextInt();
        this.filterContent = reader.getNextContentPacket();
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextInt(this.settlementUniqueID);
        writer.putNextInt(this.tileX);
        writer.putNextInt(this.tileY);
        writer.putNextInt(this.priority);
        writer.putNextContentPacket(this.filterContent);
    }
}

