/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.actions;

import necesse.engine.GameLog;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketDisconnect;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementDataEvent;
import necesse.inventory.container.settlement.events.SettlementRemovedEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class RenameSettlerNameAction
extends ContainerCustomAction {
    public final SettlementContainer container;

    public RenameSettlerNameAction(SettlementContainer container) {
        this.container = container;
    }

    public void runAndSend(int mobUniqueID, String name) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(mobUniqueID);
        writer.putNextString(name);
        this.runAndSendAction(content);
    }

    @Override
    public void executePacket(PacketReader reader) {
        int mobUniqueID = reader.getNextInt();
        String name = reader.getNextString();
        if (this.container.client.isServer()) {
            ServerClient client = this.container.client.getServerClient();
            ServerSettlementData serverData = this.container.getServerData();
            if (serverData != null) {
                if (!serverData.networkData.doesClientHaveAccess(client)) {
                    new SettlementDataEvent(serverData).applyAndSendToClient(client);
                    return;
                }
                if (!name.isEmpty() && name.length() < 50) {
                    serverData.renameSettler(mobUniqueID, name);
                } else {
                    GameLog.warn.println("Kicking player " + client.getName() + " because they attempted to rename settler with invalid name");
                    client.getServer().disconnectClient(client, PacketDisconnect.Code.STATE_DESYNC);
                }
            } else {
                new SettlementRemovedEvent(0).applyAndSendToClient(client);
            }
        }
    }
}

