/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.object.missionBoard;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.level.maps.levelData.settlementData.SettlementMissionBoardManager;

public class MissionBoardSlotsUpdateEvent
extends ContainerEvent {
    public final int settlementUniqueID;
    public final int slots;
    public final int nextSlotCost;

    public MissionBoardSlotsUpdateEvent(SettlementMissionBoardManager manager) {
        this.settlementUniqueID = manager.data.uniqueID;
        this.slots = manager.missionBoardSlots;
        this.nextSlotCost = SettlementMissionBoardManager.getNextSlotCost(manager.missionBoardSlots);
    }

    public MissionBoardSlotsUpdateEvent(PacketReader reader) {
        super(reader);
        this.settlementUniqueID = reader.getNextInt();
        this.slots = reader.getNextShortUnsigned();
        this.nextSlotCost = reader.getNextInt();
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextInt(this.settlementUniqueID);
        writer.putNextShortUnsigned(this.slots);
        writer.putNextInt(this.nextSlotCost);
    }
}

