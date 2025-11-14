/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.events;

import java.util.ArrayList;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.container.settlement.data.SettlementSettlerEquipmentFilterData;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;

public class SettlementSettlerEquipmentFiltersEvent
extends ContainerEvent {
    public final int settlementUniqueID;
    public final ArrayList<SettlementSettlerEquipmentFilterData> settlers;

    public SettlementSettlerEquipmentFiltersEvent(ServerSettlementData data) {
        this.settlementUniqueID = data.uniqueID;
        this.settlers = new ArrayList();
        for (LevelSettler settler : data.settlers) {
            SettlerMob mob = settler.getMob();
            if (mob == null) continue;
            this.settlers.add(new SettlementSettlerEquipmentFilterData(settler));
        }
    }

    public SettlementSettlerEquipmentFiltersEvent(PacketReader reader) {
        super(reader);
        this.settlementUniqueID = reader.getNextInt();
        int settlersSize = reader.getNextShortUnsigned();
        this.settlers = new ArrayList(settlersSize);
        for (int i = 0; i < settlersSize; ++i) {
            this.settlers.add(new SettlementSettlerEquipmentFilterData(reader));
        }
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextInt(this.settlementUniqueID);
        writer.putNextShortUnsigned(this.settlers.size());
        for (SettlementSettlerEquipmentFilterData settler : this.settlers) {
            settler.writeContentPacket(writer);
        }
    }
}

