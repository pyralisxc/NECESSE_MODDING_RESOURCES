/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.events;

import java.util.ArrayList;
import java.util.HashMap;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.container.settlement.data.SettlementRestrictZoneData;
import necesse.inventory.container.settlement.data.SettlementSettlerRestrictZoneData;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.RestrictZone;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;

public class SettlementRestrictZonesFullEvent
extends ContainerEvent {
    public final int settlementUniqueID;
    public final ArrayList<SettlementSettlerRestrictZoneData> settlers;
    public final HashMap<Integer, SettlementRestrictZoneData> zones;
    public final int newSettlerRestrictZoneUniqueID;

    public SettlementRestrictZonesFullEvent(ServerSettlementData data) {
        this.settlementUniqueID = data.uniqueID;
        this.settlers = new ArrayList();
        for (LevelSettler settler : data.settlers) {
            SettlerMob mob = settler.getMob();
            if (mob == null) continue;
            this.settlers.add(new SettlementSettlerRestrictZoneData(settler));
        }
        this.zones = new HashMap();
        for (RestrictZone restrictZone : data.getRestrictZones()) {
            this.zones.put(restrictZone.uniqueID, new SettlementRestrictZoneData(restrictZone));
        }
        this.newSettlerRestrictZoneUniqueID = data.getNewSettlerRestrictZoneUniqueID();
    }

    public SettlementRestrictZonesFullEvent(PacketReader reader) {
        super(reader);
        this.settlementUniqueID = reader.getNextInt();
        int settlersSize = reader.getNextShortUnsigned();
        this.settlers = new ArrayList(settlersSize);
        for (int i = 0; i < settlersSize; ++i) {
            this.settlers.add(new SettlementSettlerRestrictZoneData(reader));
        }
        int zonesSize = reader.getNextShortUnsigned();
        this.zones = new HashMap(zonesSize);
        for (int i = 0; i < zonesSize; ++i) {
            SettlementRestrictZoneData zone = new SettlementRestrictZoneData(reader);
            this.zones.put(zone.uniqueID, zone);
        }
        this.newSettlerRestrictZoneUniqueID = reader.getNextInt();
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextInt(this.settlementUniqueID);
        writer.putNextShortUnsigned(this.settlers.size());
        for (SettlementSettlerRestrictZoneData settler : this.settlers) {
            settler.writeContentPacket(writer);
        }
        writer.putNextShortUnsigned(this.zones.size());
        for (SettlementRestrictZoneData zone : this.zones.values()) {
            zone.writeContentPacket(writer);
        }
        writer.putNextInt(this.newSettlerRestrictZoneUniqueID);
    }
}

