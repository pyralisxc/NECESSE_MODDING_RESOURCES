/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.actions;

import java.util.function.BooleanSupplier;
import necesse.inventory.container.customAction.EventSubscribeEmptyCustomAction;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementNewSettlerDietChangedEvent;
import necesse.inventory.container.settlement.events.SettlementSettlerDietChangedEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class SubscribeDietsAction
extends EventSubscribeEmptyCustomAction {
    public final SettlementContainer container;

    public SubscribeDietsAction(SettlementContainer container) {
        this.container = container;
    }

    @Override
    public void onSubscribed(BooleanSupplier isActive) {
        ServerSettlementData serverData;
        this.container.subscribeEvent(SettlementNewSettlerDietChangedEvent.class, e -> e.settlementUniqueID == this.container.getSettlementUniqueID(), isActive);
        this.container.subscribeEvent(SettlementSettlerDietChangedEvent.class, e -> e.settlementUniqueID == this.container.getSettlementUniqueID(), isActive);
        if (this.container.client.isServer() && (serverData = this.container.getServerData()) != null) {
            new SettlementNewSettlerDietChangedEvent(serverData).applyAndSendToClient(this.container.client.getServerClient());
        }
    }
}

