/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.actions;

import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementDataEvent;
import necesse.inventory.container.settlement.events.SettlementRemovedEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class DisbandSettlementAction
extends EmptyCustomAction {
    public final SettlementContainer container;

    public DisbandSettlementAction(SettlementContainer container) {
        this.container = container;
    }

    @Override
    protected void run() {
        if (this.container.client.isServer()) {
            ServerClient client = this.container.client.getServerClient();
            ServerSettlementData serverData = this.container.getServerData();
            if (serverData != null) {
                if (serverData.networkData.getDisbandTime() == 0L && serverData.networkData.getOwnerAuth() != client.authentication) {
                    new SettlementDataEvent(serverData).applyAndSendToClient(client);
                    return;
                }
                serverData.networkData.disband();
                new SettlementRemovedEvent(serverData.uniqueID).applyAndSendToClientsAt(serverData.getLevel());
            } else {
                new SettlementRemovedEvent(0).applyAndSendToClient(client);
            }
        }
    }
}

