/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.actions;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.actions.SettlementAccessRequiredContainerCustomAction;
import necesse.inventory.container.settlement.events.SettlementSettlerEquipmentFiltersEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class RequestSettlerEquipmentFiltersAction
extends SettlementAccessRequiredContainerCustomAction {
    public RequestSettlerEquipmentFiltersAction(SettlementContainer container) {
        super(container);
    }

    public void runAndSend() {
        this.runAndSendAction(new Packet());
    }

    @Override
    public void executePacket(PacketReader reader, ServerSettlementData data, ServerClient client) {
        new SettlementSettlerEquipmentFiltersEvent(data).applyAndSendToClient(client);
    }
}

