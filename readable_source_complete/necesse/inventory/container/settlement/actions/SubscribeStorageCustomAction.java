/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.actions;

import java.awt.Point;
import java.util.function.BooleanSupplier;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.customAction.EventSubscribeCustomAction;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.inventory.container.settlement.events.SettlementStorageChangeAllowedEvent;
import necesse.inventory.container.settlement.events.SettlementStorageFullUpdateEvent;
import necesse.inventory.container.settlement.events.SettlementStorageLimitsEvent;
import necesse.inventory.container.settlement.events.SettlementStoragePriorityLimitEvent;

public class SubscribeStorageCustomAction
extends EventSubscribeCustomAction<Point> {
    public final SettlementDependantContainer container;

    public SubscribeStorageCustomAction(SettlementDependantContainer container) {
        this.container = container;
    }

    @Override
    public void writeData(PacketWriter writer, Point tile) {
        writer.putNextInt(tile.x);
        writer.putNextInt(tile.y);
    }

    @Override
    public Point readData(PacketReader reader) {
        int x = reader.getNextInt();
        int y = reader.getNextInt();
        return new Point(x, y);
    }

    @Override
    public void onSubscribed(BooleanSupplier isActive, Point tile) {
        this.container.subscribeEvent(SettlementStorageChangeAllowedEvent.class, e -> e.settlementUniqueID == this.container.getSettlementUniqueID() && e.tileX == tile.x && e.tileY == tile.y, isActive);
        this.container.subscribeEvent(SettlementStorageLimitsEvent.class, e -> e.settlementUniqueID == this.container.getSettlementUniqueID() && e.tileX == tile.x && e.tileY == tile.y, isActive);
        this.container.subscribeEvent(SettlementStoragePriorityLimitEvent.class, e -> e.settlementUniqueID == this.container.getSettlementUniqueID() && e.tileX == tile.x && e.tileY == tile.y, isActive);
        this.container.subscribeEvent(SettlementStorageFullUpdateEvent.class, e -> e.settlementUniqueID == this.container.getSettlementUniqueID() && e.tileX == tile.x && e.tileY == tile.y, isActive);
    }
}

