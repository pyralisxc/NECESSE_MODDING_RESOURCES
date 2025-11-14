/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.actions;

import java.util.function.BooleanSupplier;
import necesse.inventory.container.customAction.EventSubscribeEmptyCustomAction;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementNewSettlerEquipmentFilterChangedEvent;
import necesse.inventory.container.settlement.events.SettlementSettlerEquipmentFilterChangedEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class SubscribeEquipmentAction
extends EventSubscribeEmptyCustomAction {
    public final SettlementContainer container;

    public SubscribeEquipmentAction(SettlementContainer container) {
        this.container = container;
    }

    @Override
    public void onSubscribed(BooleanSupplier isActive) {
        ServerSettlementData serverData;
        this.container.subscribeEvent(SettlementNewSettlerEquipmentFilterChangedEvent.class, e -> e.settlementUniqueID == this.container.getSettlementUniqueID(), isActive);
        this.container.subscribeEvent(SettlementSettlerEquipmentFilterChangedEvent.class, e -> e.settlementUniqueID == this.container.getSettlementUniqueID(), isActive);
        if (this.container.client.isServer() && (serverData = this.container.getServerData()) != null) {
            new SettlementNewSettlerEquipmentFilterChangedEvent(serverData).applyAndSendToClient(this.container.client.getServerClient());
        }
    }
}

