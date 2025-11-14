/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.actions;

import java.util.function.BooleanSupplier;
import necesse.inventory.container.customAction.EventSubscribeEmptyCustomAction;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.inventory.container.settlement.events.SettlementSingleStorageEvent;
import necesse.inventory.container.settlement.events.SettlementSingleWorkstationsEvent;
import necesse.inventory.container.settlement.events.SettlementStorageEvent;
import necesse.inventory.container.settlement.events.SettlementWorkZoneChangedEvent;
import necesse.inventory.container.settlement.events.SettlementWorkZoneNameEvent;
import necesse.inventory.container.settlement.events.SettlementWorkZoneRemovedEvent;
import necesse.inventory.container.settlement.events.SettlementWorkZonesEvent;
import necesse.inventory.container.settlement.events.SettlementWorkstationsEvent;

public class SubscribeWorkContentCustomAction
extends EventSubscribeEmptyCustomAction {
    public final SettlementDependantContainer container;

    public SubscribeWorkContentCustomAction(SettlementDependantContainer container) {
        this.container = container;
    }

    @Override
    public void onSubscribed(BooleanSupplier isActive) {
        this.container.subscribeEvent(SettlementStorageEvent.class, e -> e.settlementUniqueID == this.container.getSettlementUniqueID(), isActive);
        this.container.subscribeEvent(SettlementSingleStorageEvent.class, e -> e.settlementUniqueID == this.container.getSettlementUniqueID(), isActive);
        this.container.subscribeEvent(SettlementWorkstationsEvent.class, e -> e.settlementUniqueID == this.container.getSettlementUniqueID(), isActive);
        this.container.subscribeEvent(SettlementSingleWorkstationsEvent.class, e -> e.settlementUniqueID == this.container.getSettlementUniqueID(), isActive);
        this.container.subscribeEvent(SettlementWorkZoneRemovedEvent.class, e -> e.settlementUniqueID == this.container.getSettlementUniqueID(), isActive);
        this.container.subscribeEvent(SettlementWorkZoneChangedEvent.class, e -> e.settlementUniqueID == this.container.getSettlementUniqueID(), isActive);
        this.container.subscribeEvent(SettlementWorkZoneNameEvent.class, e -> e.settlementUniqueID == this.container.getSettlementUniqueID(), isActive);
        if (this.container.client.isServer()) {
            new SettlementStorageEvent(this.container.getServerData()).applyAndSendToClient(this.container.client.getServerClient());
            new SettlementWorkstationsEvent(this.container.getServerData()).applyAndSendToClient(this.container.client.getServerClient());
            new SettlementWorkZonesEvent(this.container.getServerData()).applyAndSendToClient(this.container.client.getServerClient());
        }
    }
}

