/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.actions.workstation;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.inventory.container.settlement.events.SettlementDataEvent;
import necesse.inventory.container.settlement.events.SettlementRemovedEvent;
import necesse.inventory.container.settlement.events.SettlementSingleWorkstationsEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.SettlementWorkstation;

public abstract class ConfigureWorkstationAction
extends ContainerCustomAction {
    public final SettlementDependantContainer container;

    public ConfigureWorkstationAction(SettlementDependantContainer container) {
        this.container = container;
    }

    protected void runAndSend(int tileX, int tileY, Packet actionContent) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(tileX);
        writer.putNextInt(tileY);
        writer.putNextContentPacket(actionContent);
        this.runAndSendAction(content);
    }

    @Override
    public void executePacket(PacketReader reader) {
        int x = reader.getNextInt();
        int y = reader.getNextInt();
        Packet actionContent = reader.getNextContentPacket();
        if (this.container.client.isServer()) {
            ServerClient client = this.container.client.getServerClient();
            ServerSettlementData serverData = this.container.getServerData();
            if (serverData != null) {
                if (!serverData.networkData.doesClientHaveAccess(client)) {
                    new SettlementDataEvent(serverData).applyAndSendToClient(client);
                    return;
                }
                SettlementWorkstation workstation = serverData.storageManager.getWorkstation(x, y);
                if (workstation != null) {
                    this.handleAction(new PacketReader(actionContent), serverData, workstation);
                } else {
                    new SettlementSingleWorkstationsEvent(serverData, x, y).applyAndSendToClient(client);
                }
            } else {
                new SettlementRemovedEvent(0).applyAndSendToClient(client);
            }
        }
    }

    public abstract void handleAction(PacketReader var1, ServerSettlementData var2, SettlementWorkstation var3);
}

