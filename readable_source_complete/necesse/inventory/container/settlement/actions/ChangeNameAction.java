/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.actions;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementDataEvent;
import necesse.inventory.container.settlement.events.SettlementRemovedEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class ChangeNameAction
extends ContainerCustomAction {
    public final SettlementContainer container;

    public ChangeNameAction(SettlementContainer container) {
        this.container = container;
    }

    public void runAndSend(GameMessage name) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextContentPacket(name.getContentPacket());
        this.runAndSendAction(content);
    }

    @Override
    public void executePacket(PacketReader reader) {
        GameMessage name = GameMessage.fromContentPacket(reader.getNextContentPacket());
        if (this.container.client.isServer()) {
            ServerClient client = this.container.client.getServerClient();
            ServerSettlementData serverData = this.container.getServerData();
            if (serverData != null) {
                boolean isOwner;
                boolean bl = isOwner = serverData.networkData.getOwnerAuth() == client.authentication;
                if (!isOwner) {
                    new SettlementDataEvent(serverData).applyAndSendToClient(client);
                    return;
                }
                serverData.networkData.setName(name);
                serverData.sendEvent(SettlementDataEvent.class);
            } else {
                new SettlementRemovedEvent(0).applyAndSendToClient(client);
            }
        }
    }
}

