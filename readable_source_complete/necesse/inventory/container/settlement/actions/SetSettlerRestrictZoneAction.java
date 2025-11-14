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
import necesse.inventory.container.settlement.events.SettlementRestrictZonesFullEvent;
import necesse.inventory.container.settlement.events.SettlementSettlerRestrictZoneChangedEvent;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class SetSettlerRestrictZoneAction
extends SettlementAccessRequiredContainerCustomAction {
    public SetSettlerRestrictZoneAction(SettlementContainer container) {
        super(container);
    }

    public void runAndSend(int mobUniqueID, int restrictZoneUniqueID) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(mobUniqueID);
        writer.putNextInt(restrictZoneUniqueID);
        this.runAndSendAction(content);
    }

    @Override
    public void executePacket(PacketReader reader, ServerSettlementData data, ServerClient client) {
        int mobUniqueID = reader.getNextInt();
        int restrictZoneUniqueID = reader.getNextInt();
        LevelSettler settler = data.getSettler(mobUniqueID);
        if (settler != null) {
            settler.setRestrictZoneUniqueID(restrictZoneUniqueID);
            new SettlementSettlerRestrictZoneChangedEvent(data, settler).applyAndSendToClientsAtExcept(client);
        } else {
            new SettlementRestrictZonesFullEvent(data).applyAndSendToClient(client);
        }
    }
}

