/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.object.missionBoard;

import java.util.LinkedHashSet;
import necesse.engine.expeditions.SettlerExpedition;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.ExpeditionMissionRegistry;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.SettlementMissionBoardMission;
import necesse.level.maps.levelData.settlementData.jobCondition.JobCondition;

public class NetworkMissionBoardMission {
    public final int uniqueID;
    public final SettlerExpedition expedition;
    public final int basePrice;
    public final float successChance;
    public final boolean allSettlersAssigned;
    public final LinkedHashSet<Integer> assignedSettlers;
    public JobCondition condition;

    public NetworkMissionBoardMission(ServerSettlementData serverData, SettlementMissionBoardMission mission) {
        this.uniqueID = mission.uniqueID;
        this.expedition = mission.expedition;
        this.basePrice = mission.expedition.getBaseCost(serverData);
        this.successChance = mission.expedition.getSuccessChance(serverData);
        this.allSettlersAssigned = mission.allSettlersAssigned;
        this.assignedSettlers = !this.allSettlersAssigned ? new LinkedHashSet<Integer>(mission.assignedSettlers) : new LinkedHashSet();
        this.condition = mission.condition;
    }

    public NetworkMissionBoardMission(PacketReader reader) {
        this.uniqueID = reader.getNextInt();
        int expeditionID = reader.getNextShortUnsigned();
        this.expedition = ExpeditionMissionRegistry.getExpedition(expeditionID);
        this.basePrice = reader.getNextInt();
        this.successChance = reader.getNextFloat();
        this.allSettlersAssigned = reader.getNextBoolean();
        this.assignedSettlers = !this.allSettlersAssigned ? reader.getNextCollection(size -> new LinkedHashSet(), reader::getNextInt) : new LinkedHashSet();
        this.condition = JobCondition.fromContentPacket(reader);
    }

    public void writePacket(PacketWriter writer) {
        writer.putNextInt(this.uniqueID);
        writer.putNextShortUnsigned(this.expedition.getID());
        writer.putNextInt(this.basePrice);
        writer.putNextFloat(this.successChance);
        writer.putNextBoolean(this.allSettlersAssigned);
        if (!this.allSettlersAssigned) {
            writer.putNextCollection(this.assignedSettlers, writer::putNextInt);
        }
        JobCondition.writeContentPacket(this.condition, writer);
    }
}

