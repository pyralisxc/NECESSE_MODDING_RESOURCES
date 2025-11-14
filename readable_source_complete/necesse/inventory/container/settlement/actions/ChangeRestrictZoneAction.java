/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.actions;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.ZoningChange;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.actions.SettlementAccessRequiredContainerCustomAction;
import necesse.inventory.container.settlement.events.SettlementRestrictZoneChangedEvent;
import necesse.inventory.container.settlement.events.SettlementRestrictZonesFullEvent;
import necesse.level.maps.levelData.settlementData.RestrictZone;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class ChangeRestrictZoneAction
extends SettlementAccessRequiredContainerCustomAction {
    public ChangeRestrictZoneAction(SettlementContainer container) {
        super(container);
    }

    public void runAndSend(int zoneUniqueID, ZoningChange change) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(zoneUniqueID);
        change.write(writer);
        this.runAndSendAction(content);
    }

    @Override
    public void executePacket(PacketReader reader, ServerSettlementData data, ServerClient client) {
        int uniqueID = reader.getNextInt();
        ZoningChange change = ZoningChange.fromPacket(reader);
        RestrictZone restrictZone = data.getRestrictZone(uniqueID);
        if (restrictZone != null) {
            if (restrictZone.applyChange(change)) {
                new SettlementRestrictZoneChangedEvent(data, uniqueID, change).applyAndSendToClientsAtExcept(client);
            }
        } else {
            new SettlementRestrictZonesFullEvent(data).applyAndSendToClient(client);
        }
    }
}

