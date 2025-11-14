/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.actions;

import java.util.function.BooleanSupplier;
import necesse.inventory.container.customAction.EventSubscribeEmptyCustomAction;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementNewSettlerRestrictZoneChangedEvent;
import necesse.inventory.container.settlement.events.SettlementRestrictZoneChangedEvent;
import necesse.inventory.container.settlement.events.SettlementRestrictZoneRecolorEvent;
import necesse.inventory.container.settlement.events.SettlementRestrictZoneRenameEvent;
import necesse.inventory.container.settlement.events.SettlementRestrictZonesFullEvent;
import necesse.inventory.container.settlement.events.SettlementSettlerRestrictZoneChangedEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class SubscribeRestrictAction
extends EventSubscribeEmptyCustomAction {
    public final SettlementContainer container;

    public SubscribeRestrictAction(SettlementContainer container) {
        this.container = container;
    }

    @Override
    public void onSubscribed(BooleanSupplier isActive) {
        ServerSettlementData serverData;
        this.container.subscribeEvent(SettlementRestrictZonesFullEvent.class, e -> e.settlementUniqueID == this.container.getSettlementUniqueID(), isActive);
        this.container.subscribeEvent(SettlementNewSettlerRestrictZoneChangedEvent.class, e -> e.settlementUniqueID == this.container.getSettlementUniqueID(), isActive);
        this.container.subscribeEvent(SettlementSettlerRestrictZoneChangedEvent.class, e -> e.settlementUniqueID == this.container.getSettlementUniqueID(), isActive);
        this.container.subscribeEvent(SettlementRestrictZoneChangedEvent.class, e -> e.settlementUniqueID == this.container.getSettlementUniqueID(), isActive);
        this.container.subscribeEvent(SettlementRestrictZoneRenameEvent.class, e -> e.settlementUniqueID == this.container.getSettlementUniqueID(), isActive);
        this.container.subscribeEvent(SettlementRestrictZoneRecolorEvent.class, e -> e.settlementUniqueID == this.container.getSettlementUniqueID(), isActive);
        if (this.container.client.isServer() && (serverData = this.container.getServerData()) != null) {
            new SettlementRestrictZonesFullEvent(serverData).applyAndSendToClient(this.container.client.getServerClient());
            new SettlementNewSettlerRestrictZoneChangedEvent(serverData).applyAndSendToClient(this.container.client.getServerClient());
        }
    }
}

