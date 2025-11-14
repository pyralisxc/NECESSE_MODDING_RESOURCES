/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.object;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.container.object.HomestoneContainer;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class HomestoneUpdateEvent
extends ContainerEvent {
    public final Packet content;

    public HomestoneUpdateEvent(ServerSettlementData data) {
        this.content = HomestoneContainer.getContainerContent(data);
    }

    public HomestoneUpdateEvent(PacketReader reader) {
        super(reader);
        this.content = reader.getNextContentPacket();
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextContentPacket(this.content);
    }
}

