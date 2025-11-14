/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.actions;

import java.util.function.BooleanSupplier;
import necesse.engine.GameLog;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.customAction.EventSubscribeCustomAction;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.zones.SettlementWorkZone;

public class SubscribeWorkZoneConfigCustomAction
extends EventSubscribeCustomAction<SettlementWorkZone> {
    public final SettlementContainer container;

    public SubscribeWorkZoneConfigCustomAction(SettlementContainer container) {
        this.container = container;
    }

    @Override
    public void writeData(PacketWriter writer, SettlementWorkZone zone) {
        writer.putNextInt(zone.getUniqueID());
    }

    @Override
    public SettlementWorkZone readData(PacketReader reader) {
        int zoneUniqueID = reader.getNextInt();
        if (this.container.client.isServer()) {
            ServerSettlementData serverData = this.container.getServerData();
            return serverData.getWorkZones().getZone(zoneUniqueID);
        }
        return null;
    }

    @Override
    public void onSubscribed(BooleanSupplier isActive, SettlementWorkZone zone) {
        if (zone != null) {
            zone.subscribeConfigEvents(this.container, isActive);
        } else if (this.container.client.isServer()) {
            GameLog.warn.println(this.container.client.getName() + " subscribed to unknown work zone config events");
        }
    }
}

