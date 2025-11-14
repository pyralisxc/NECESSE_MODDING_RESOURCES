/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.actions;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementDataEvent;
import necesse.inventory.container.settlement.events.SettlementRemovedEvent;
import necesse.inventory.container.settlement.events.SettlementSettlersChangedEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class MoveSettlerRoomAction
extends ContainerCustomAction {
    public final SettlementContainer container;

    public MoveSettlerRoomAction(SettlementContainer container) {
        this.container = container;
    }

    public void runAndSend(int x, int y, int mobUniqueID) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(x);
        writer.putNextInt(y);
        writer.putNextInt(mobUniqueID);
        this.runAndSendAction(content);
    }

    @Override
    public void executePacket(PacketReader reader) {
        int x = reader.getNextInt();
        int y = reader.getNextInt();
        int mobUniqueID = reader.getNextInt();
        if (this.container.client.isServer()) {
            ServerClient client = this.container.client.getServerClient();
            ServerSettlementData serverData = this.container.getServerData();
            if (serverData != null) {
                if (!serverData.networkData.doesClientHaveAccess(client)) {
                    new SettlementDataEvent(serverData).applyAndSendToClient(client);
                    return;
                }
                if (serverData.moveSettler(mobUniqueID, x, y, client)) {
                    serverData.sendEvent(SettlementSettlersChangedEvent.class);
                }
            } else {
                new SettlementRemovedEvent(0).applyAndSendToClient(client);
            }
        }
    }
}

