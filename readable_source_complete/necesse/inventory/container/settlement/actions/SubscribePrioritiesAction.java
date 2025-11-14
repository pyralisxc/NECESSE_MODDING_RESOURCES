/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.actions;

import java.util.function.BooleanSupplier;
import necesse.inventory.container.customAction.EventSubscribeEmptyCustomAction;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementSettlerPrioritiesChangedEvent;

public class SubscribePrioritiesAction
extends EventSubscribeEmptyCustomAction {
    public final SettlementContainer container;

    public SubscribePrioritiesAction(SettlementContainer container) {
        this.container = container;
    }

    @Override
    public void onSubscribed(BooleanSupplier isActive) {
        this.container.subscribeEvent(SettlementSettlerPrioritiesChangedEvent.class, e -> e.settlementUniqueID == this.container.getSettlementUniqueID(), isActive);
    }
}

