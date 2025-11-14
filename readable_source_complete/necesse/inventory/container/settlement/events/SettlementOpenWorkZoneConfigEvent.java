/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.events;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.level.maps.levelData.settlementData.zones.SettlementWorkZone;

public class SettlementOpenWorkZoneConfigEvent
extends ContainerEvent {
    public final int uniqueID;
    public final Packet configPacket;

    public SettlementOpenWorkZoneConfigEvent(SettlementWorkZone zone) {
        this.uniqueID = zone.getUniqueID();
        this.configPacket = new Packet();
        zone.writeSettingsForm(new PacketWriter(this.configPacket));
    }

    public SettlementOpenWorkZoneConfigEvent(PacketReader reader) {
        super(reader);
        this.uniqueID = reader.getNextInt();
        this.configPacket = reader.getNextContentPacket();
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextInt(this.uniqueID);
        writer.putNextContentPacket(this.configPacket);
    }
}

