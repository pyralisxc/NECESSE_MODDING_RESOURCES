/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.actions;

import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.customAction.BooleanCustomAction;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementDataEvent;
import necesse.inventory.container.settlement.events.SettlementRemovedEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class ChangePrivacyAction
extends BooleanCustomAction {
    public final SettlementContainer container;

    public ChangePrivacyAction(SettlementContainer container) {
        this.container = container;
    }

    @Override
    protected void run(boolean value) {
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
                serverData.networkData.setPrivate(value);
                if (serverData.networkData.getOwnerAuth() != client.authentication) {
                    serverData.networkData.setOwner(client);
                }
                serverData.sendEvent(SettlementDataEvent.class);
            } else {
                new SettlementRemovedEvent(0).applyAndSendToClient(client);
            }
        }
    }
}

