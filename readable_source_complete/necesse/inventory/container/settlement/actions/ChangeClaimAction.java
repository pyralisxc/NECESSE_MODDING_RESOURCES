/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.actions;

import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.inventory.container.customAction.BooleanCustomAction;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementDataEvent;
import necesse.inventory.container.settlement.events.SettlementRemovedEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class ChangeClaimAction
extends BooleanCustomAction {
    public final SettlementContainer container;

    public ChangeClaimAction(SettlementContainer container) {
        this.container = container;
    }

    @Override
    protected void run(boolean value) {
        if (this.container.client.isServer()) {
            ServerClient client = this.container.client.getServerClient();
            ServerSettlementData serverData = this.container.getServerData();
            if (serverData != null) {
                SettlementsWorldData settlementsData;
                long current;
                int max;
                if (serverData.networkData.getDisbandTime() != 0L) {
                    new SettlementDataEvent(serverData).applyAndSendToClient(client);
                    return;
                }
                if (value && serverData.networkData.getOwnerAuth() != -1L) {
                    new SettlementDataEvent(serverData).applyAndSendToClient(client);
                    return;
                }
                if (value && (max = client.getServer().world.settings.maxSettlementsPerPlayer) > 0 && (current = (settlementsData = SettlementsWorldData.getSettlementsData(client.getServer())).streamSettlements().filter(e -> e.getOwnerAuth() == client.authentication).count()) >= (long)max) {
                    client.sendChatMessage(new LocalMessage("misc", "maxsettlementsreached", "count", max));
                    return;
                }
                serverData.networkData.setOwner(value ? client : null);
                serverData.sendEvent(SettlementDataEvent.class);
            } else {
                new SettlementRemovedEvent(0).applyAndSendToClient(client);
            }
        }
    }
}

