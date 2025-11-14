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
import necesse.inventory.container.settlement.events.SettlementRestrictZoneRecolorEvent;
import necesse.inventory.container.settlement.events.SettlementRestrictZonesFullEvent;
import necesse.level.maps.levelData.settlementData.RestrictZone;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class RecolorRestrictZoneAction
extends SettlementAccessRequiredContainerCustomAction {
    public RecolorRestrictZoneAction(SettlementContainer container) {
        super(container);
    }

    public void runAndSend(int zoneUniqueID, int hue) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(zoneUniqueID);
        writer.putNextMaxValue(hue, 360);
        this.runAndSendAction(content);
    }

    @Override
    public void executePacket(PacketReader reader, ServerSettlementData data, ServerClient client) {
        int uniqueID = reader.getNextInt();
        int hue = reader.getNextMaxValue(360);
        RestrictZone restrictZone = data.getRestrictZone(uniqueID);
        if (restrictZone != null) {
            restrictZone.colorHue = hue;
            new SettlementRestrictZoneRecolorEvent(data, restrictZone).applyAndSendToClientsAtExcept(client);
        } else {
            new SettlementRestrictZonesFullEvent(data).applyAndSendToClient(client);
        }
    }
}

