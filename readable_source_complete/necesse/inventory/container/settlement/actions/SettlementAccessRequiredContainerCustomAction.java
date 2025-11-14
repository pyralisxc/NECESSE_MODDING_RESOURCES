/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.actions;

import necesse.engine.network.PacketReader;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.inventory.container.settlement.events.SettlementDataEvent;
import necesse.inventory.container.settlement.events.SettlementRemovedEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public abstract class SettlementAccessRequiredContainerCustomAction
extends ContainerCustomAction {
    public final SettlementDependantContainer container;

    public SettlementAccessRequiredContainerCustomAction(SettlementDependantContainer container) {
        this.container = container;
    }

    @Override
    public final void executePacket(PacketReader reader) {
        if (this.container.client.isServer()) {
            ServerClient client = this.container.client.getServerClient();
            ServerSettlementData serverData = this.container.getServerData();
            if (serverData != null) {
                if (!serverData.networkData.doesClientHaveAccess(client)) {
                    new SettlementDataEvent(serverData).applyAndSendToClient(client);
                    return;
                }
                this.executePacket(reader, serverData, client);
            } else {
                new SettlementRemovedEvent(0).applyAndSendToClient(client);
            }
        }
    }

    public abstract void executePacket(PacketReader var1, ServerSettlementData var2, ServerClient var3);
}

