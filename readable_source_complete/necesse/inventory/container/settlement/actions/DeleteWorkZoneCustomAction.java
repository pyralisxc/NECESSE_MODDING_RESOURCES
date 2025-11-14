/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.actions;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.inventory.container.settlement.events.SettlementWorkZoneRemovedEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.zones.SettlementWorkZone;

public class DeleteWorkZoneCustomAction
extends ContainerCustomAction {
    public final SettlementDependantContainer container;

    public DeleteWorkZoneCustomAction(SettlementDependantContainer container) {
        this.container = container;
    }

    public void runAndSend(int zoneUniqueID) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(zoneUniqueID);
        this.runAndSendAction(content);
    }

    @Override
    public void executePacket(PacketReader reader) {
        ServerSettlementData serverData;
        int zoneUniqueID = reader.getNextInt();
        if (this.container.client.isServer() && (serverData = this.container.getServerData()) != null) {
            SettlementWorkZone zone = serverData.getWorkZones().getZone(zoneUniqueID);
            if (zone == null) {
                new SettlementWorkZoneRemovedEvent(serverData, zoneUniqueID).applyAndSendToClient(this.container.client.getServerClient());
            } else {
                serverData.getWorkZones().removeZone(zoneUniqueID);
            }
        }
    }
}

