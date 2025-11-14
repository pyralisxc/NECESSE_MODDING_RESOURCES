/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.actions;

import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.customAction.IntCustomAction;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementDataEvent;
import necesse.inventory.container.settlement.events.SettlementRemovedEvent;
import necesse.inventory.container.settlement.events.SettlementSettlersChangedEvent;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class BanishSettlerAction
extends IntCustomAction {
    public final SettlementContainer container;

    public BanishSettlerAction(SettlementContainer container) {
        this.container = container;
    }

    @Override
    protected void run(int value) {
        if (this.container.client.isServer()) {
            ServerClient client = this.container.client.getServerClient();
            ServerSettlementData serverData = this.container.getServerData();
            if (serverData != null) {
                if (!serverData.networkData.doesClientHaveAccess(client)) {
                    new SettlementDataEvent(serverData).applyAndSendToClient(client);
                    return;
                }
                LevelSettler settler = serverData.getSettler(value);
                if (settler != null && settler.canBanish()) {
                    if (serverData.removeSettler(value, client)) {
                        serverData.sendEvent(SettlementSettlersChangedEvent.class);
                    }
                } else {
                    new SettlementSettlersChangedEvent(serverData).applyAndSendToClient(client);
                }
            } else {
                new SettlementRemovedEvent(0).applyAndSendToClient(client);
            }
        }
    }
}

