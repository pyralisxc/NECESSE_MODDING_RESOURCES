/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.events;

import java.util.ArrayList;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.container.settlement.data.SettlementLockedBedData;
import necesse.inventory.container.settlement.data.SettlementSettlerBasicData;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.SettlementBed;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;

public class SettlementSettlerBasicsEvent
extends ContainerEvent {
    public final int settlementUniqueID;
    public final ArrayList<SettlementSettlerBasicData> settlers;
    public final ArrayList<SettlementLockedBedData> lockedBeds;

    public SettlementSettlerBasicsEvent(ServerSettlementData data) {
        this.settlementUniqueID = data.uniqueID;
        this.settlers = new ArrayList();
        this.lockedBeds = new ArrayList();
        for (LevelSettler settler : data.settlers) {
            SettlerMob mob = settler.getMob();
            if (mob == null) continue;
            this.settlers.add(new SettlementSettlerBasicData(settler));
        }
        for (SettlementBed bed : data.getBeds()) {
            if (!bed.isLocked) continue;
            this.lockedBeds.add(new SettlementLockedBedData(bed.tileX, bed.tileY));
        }
    }

    public SettlementSettlerBasicsEvent(PacketReader reader) {
        super(reader);
        this.settlementUniqueID = reader.getNextInt();
        int settlersSize = reader.getNextShortUnsigned();
        this.settlers = new ArrayList(settlersSize);
        for (int i = 0; i < settlersSize; ++i) {
            this.settlers.add(new SettlementSettlerBasicData(reader));
        }
        int lockedRoomsSize = reader.getNextShortUnsigned();
        this.lockedBeds = new ArrayList(lockedRoomsSize);
        for (int i = 0; i < lockedRoomsSize; ++i) {
            this.lockedBeds.add(new SettlementLockedBedData(reader));
        }
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextInt(this.settlementUniqueID);
        writer.putNextShortUnsigned(this.settlers.size());
        for (SettlementSettlerBasicData settler : this.settlers) {
            settler.writeContentPacket(writer);
        }
        writer.putNextShortUnsigned(this.lockedBeds.size());
        for (SettlementLockedBedData lockedRoom : this.lockedBeds) {
            lockedRoom.writeContentPacket(writer);
        }
    }
}

