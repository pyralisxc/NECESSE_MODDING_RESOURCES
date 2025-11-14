/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.object.missionBoard;

import java.util.ArrayList;
import java.util.Collection;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.container.object.missionBoard.NetworkMissionBoardMission;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.SettlementMissionBoardMission;

public class AllMissionsUpdateEvent
extends ContainerEvent {
    public final int settlementUniqueID;
    public final ArrayList<NetworkMissionBoardMission> missions;

    public AllMissionsUpdateEvent(ServerSettlementData data) {
        this.settlementUniqueID = data.uniqueID;
        Collection<SettlementMissionBoardMission> settlementMissions = data.missionBoardManager.getMissions();
        this.missions = new ArrayList(settlementMissions.size());
        for (SettlementMissionBoardMission mission : settlementMissions) {
            this.missions.add(new NetworkMissionBoardMission(data, mission));
        }
    }

    public AllMissionsUpdateEvent(PacketReader reader) {
        super(reader);
        this.settlementUniqueID = reader.getNextInt();
        this.missions = reader.getNextCollection(ArrayList::new, () -> new NetworkMissionBoardMission(reader));
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextInt(this.settlementUniqueID);
        writer.putNextCollection(this.missions, e -> e.writePacket(writer));
    }
}

