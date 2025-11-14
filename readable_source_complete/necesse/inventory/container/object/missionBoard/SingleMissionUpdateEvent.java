/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.object.missionBoard;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.container.object.missionBoard.NetworkMissionBoardMission;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.SettlementMissionBoardMission;

public class SingleMissionUpdateEvent
extends ContainerEvent {
    public final int settlementUniqueID;
    public final int slot;
    public final NetworkMissionBoardMission mission;

    public SingleMissionUpdateEvent(ServerSettlementData data, int slot, SettlementMissionBoardMission mission) {
        this.settlementUniqueID = data.uniqueID;
        this.slot = slot;
        this.mission = new NetworkMissionBoardMission(data, mission);
    }

    public SingleMissionUpdateEvent(PacketReader reader) {
        super(reader);
        this.settlementUniqueID = reader.getNextInt();
        this.slot = reader.getNextInt();
        this.mission = new NetworkMissionBoardMission(reader);
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextInt(this.settlementUniqueID);
        writer.putNextInt(this.slot);
        this.mission.writePacket(writer);
    }
}

