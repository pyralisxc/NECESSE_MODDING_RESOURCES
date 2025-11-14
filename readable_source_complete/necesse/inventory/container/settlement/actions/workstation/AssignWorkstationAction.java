/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.actions.workstation;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementDataEvent;
import necesse.inventory.container.settlement.events.SettlementRemovedEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class AssignWorkstationAction
extends ContainerCustomAction {
    public final SettlementContainer container;

    public AssignWorkstationAction(SettlementContainer container) {
        this.container = container;
    }

    public void runAndSend(int tileX, int tileY) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(tileX);
        writer.putNextInt(tileY);
        this.runAndSendAction(content);
    }

    @Override
    public void executePacket(PacketReader reader) {
        int x = reader.getNextInt();
        int y = reader.getNextInt();
        if (this.container.client.isServer()) {
            ServerClient client = this.container.client.getServerClient();
            ServerSettlementData serverData = this.container.getServerData();
            if (serverData != null) {
                if (!serverData.networkData.doesClientHaveAccess(client)) {
                    new SettlementDataEvent(serverData).applyAndSendToClient(client);
                    return;
                }
                serverData.storageManager.assignWorkstation(x, y, true);
            } else {
                new SettlementRemovedEvent(0).applyAndSendToClient(client);
            }
        }
    }
}

