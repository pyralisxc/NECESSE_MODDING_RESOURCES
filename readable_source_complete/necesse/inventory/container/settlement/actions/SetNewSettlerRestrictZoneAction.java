/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.actions;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.actions.SettlementAccessRequiredContainerCustomAction;
import necesse.inventory.container.settlement.events.SettlementNewSettlerRestrictZoneChangedEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class SetNewSettlerRestrictZoneAction
extends SettlementAccessRequiredContainerCustomAction {
    public SetNewSettlerRestrictZoneAction(SettlementContainer container) {
        super(container);
    }

    public void runAndSend(int restrictZoneUniqueID) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(restrictZoneUniqueID);
        this.runAndSendAction(content);
    }

    @Override
    public void executePacket(PacketReader reader, ServerSettlementData data, ServerClient client) {
        int restrictZoneUniqueID = reader.getNextInt();
        data.setNewSettlerRestrictZone(restrictZoneUniqueID);
        data.sendEvent(SettlementNewSettlerRestrictZoneChangedEvent.class);
    }
}

