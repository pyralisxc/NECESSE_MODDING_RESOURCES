/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.actions;

import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.actions.SettlementAccessRequiredContainerCustomAction;
import necesse.inventory.container.settlement.events.SettlementRestrictZoneRenameEvent;
import necesse.inventory.container.settlement.events.SettlementRestrictZonesFullEvent;
import necesse.level.maps.levelData.settlementData.RestrictZone;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class RenameRestrictZoneAction
extends SettlementAccessRequiredContainerCustomAction {
    public RenameRestrictZoneAction(SettlementContainer container) {
        super(container);
    }

    public void runAndSend(int zoneUniqueID, String name) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(zoneUniqueID);
        writer.putNextString(name);
        this.runAndSendAction(content);
    }

    @Override
    public void executePacket(PacketReader reader, ServerSettlementData data, ServerClient client) {
        int uniqueID = reader.getNextInt();
        String name = reader.getNextString();
        if (name.isEmpty()) {
            return;
        }
        RestrictZone restrictZone = data.getRestrictZone(uniqueID);
        if (restrictZone != null) {
            if (name.length() > 30) {
                name = name.substring(0, 30);
            }
            restrictZone.name = new StaticMessage(name);
            new SettlementRestrictZoneRenameEvent(data, restrictZone).applyAndSendToClientsAtExcept(client);
        } else {
            new SettlementRestrictZonesFullEvent(data).applyAndSendToClient(client);
        }
    }
}

