/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.actions;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.inventory.container.settlement.actions.SettlementAccessRequiredContainerCustomAction;
import necesse.inventory.container.settlement.events.SettlementSettlerListEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class RequestSettlerListAction
extends SettlementAccessRequiredContainerCustomAction {
    public RequestSettlerListAction(SettlementDependantContainer container) {
        super(container);
    }

    public void runAndSend() {
        this.runAndSendAction(new Packet());
    }

    @Override
    public void executePacket(PacketReader reader, ServerSettlementData data, ServerClient client) {
        new SettlementSettlerListEvent(data).applyAndSendToClient(client);
    }
}

