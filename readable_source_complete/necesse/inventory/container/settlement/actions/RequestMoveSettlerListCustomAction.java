/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.actions;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.inventory.container.settlement.actions.SettlementAccessRequiredContainerCustomAction;
import necesse.inventory.container.settlement.events.SettlementOpenSettlementListEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class RequestMoveSettlerListCustomAction
extends SettlementAccessRequiredContainerCustomAction {
    public RequestMoveSettlerListCustomAction(SettlementDependantContainer container) {
        super(container);
    }

    public void runAndSend(int mobUniqueID) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(mobUniqueID);
        this.runAndSendAction(content);
    }

    @Override
    public void executePacket(PacketReader reader, ServerSettlementData data, ServerClient client) {
        int mobUniqueID = reader.getNextInt();
        new SettlementOpenSettlementListEvent(data, client, data.uniqueID, mobUniqueID).applyAndSendToClient(client);
    }
}

