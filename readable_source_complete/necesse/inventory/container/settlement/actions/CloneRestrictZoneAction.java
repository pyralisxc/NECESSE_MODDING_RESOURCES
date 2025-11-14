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
import necesse.level.maps.levelData.settlementData.RestrictZone;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class CloneRestrictZoneAction
extends SettlementAccessRequiredContainerCustomAction {
    public CloneRestrictZoneAction(SettlementContainer container) {
        super(container);
    }

    public void runAndSend(int zoneUniqueID) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(zoneUniqueID);
        this.runAndSendAction(content);
    }

    @Override
    public void executePacket(PacketReader reader, ServerSettlementData data, ServerClient client) {
        int uniqueID = reader.getNextInt();
        if (data.getRestrictZones().size() < ServerSettlementData.MAX_RESTRICT_ZONES) {
            RestrictZone oldZone = data.getRestrictZone(uniqueID);
            if (oldZone != null) {
                RestrictZone newZone = data.addNewRestrictZone();
                newZone.copyZoneFrom(oldZone);
                new SettlementRestrictZonesFullEvent(data).applyAndSendToClientsAt(client);
            } else {
                new SettlementRestrictZonesFullEvent(data).applyAndSendToClient(client);
            }
        }
    }
}

