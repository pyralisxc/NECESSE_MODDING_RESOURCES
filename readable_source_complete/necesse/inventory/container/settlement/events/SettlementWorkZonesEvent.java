/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.events;

import java.util.HashMap;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.zones.SettlementWorkZone;
import necesse.level.maps.levelData.settlementData.zones.SettlementWorkZoneRegistry;

public class SettlementWorkZonesEvent
extends ContainerEvent {
    public final int settlementUniqueID;
    public final HashMap<Integer, SettlementWorkZone> zones;

    public SettlementWorkZonesEvent(ServerSettlementData data) {
        this.settlementUniqueID = data.uniqueID;
        this.zones = new HashMap<Integer, SettlementWorkZone>(data.getWorkZones().getZones());
    }

    public SettlementWorkZonesEvent(PacketReader reader) {
        super(reader);
        this.settlementUniqueID = reader.getNextInt();
        int size = reader.getNextShortUnsigned();
        this.zones = new HashMap();
        for (int i = 0; i < size; ++i) {
            int id = reader.getNextShortUnsigned();
            int uniqueID = reader.getNextInt();
            SettlementWorkZone zone = SettlementWorkZoneRegistry.getNewZone(id);
            zone.setUniqueID(uniqueID);
            zone.applyPacket(reader);
            this.zones.put(uniqueID, zone);
        }
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextInt(this.settlementUniqueID);
        writer.putNextShortUnsigned(this.zones.size());
        for (SettlementWorkZone zone : this.zones.values()) {
            writer.putNextShortUnsigned(zone.getID());
            writer.putNextInt(zone.getUniqueID());
            zone.writePacket(writer);
        }
    }
}

