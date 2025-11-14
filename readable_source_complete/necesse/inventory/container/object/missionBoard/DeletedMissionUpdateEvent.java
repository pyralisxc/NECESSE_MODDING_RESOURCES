/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.object.missionBoard;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class DeletedMissionUpdateEvent
extends ContainerEvent {
    public final int settlementUniqueID;
    public final int missionUniqueID;

    public DeletedMissionUpdateEvent(ServerSettlementData data, int missionUniqueID) {
        this.settlementUniqueID = data.uniqueID;
        this.missionUniqueID = missionUniqueID;
    }

    public DeletedMissionUpdateEvent(PacketReader reader) {
        super(reader);
        this.settlementUniqueID = reader.getNextInt();
        this.missionUniqueID = reader.getNextInt();
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextInt(this.settlementUniqueID);
        writer.putNextInt(this.missionUniqueID);
    }
}

