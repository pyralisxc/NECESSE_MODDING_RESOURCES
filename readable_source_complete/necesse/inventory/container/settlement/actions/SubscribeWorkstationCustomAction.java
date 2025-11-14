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
import necesse.inventory.container.settlement.events.SettlementWorkstationEvent;
import necesse.inventory.container.settlement.events.SettlementWorkstationRecipeRemoveEvent;
import necesse.inventory.container.settlement.events.SettlementWorkstationRecipeUpdateEvent;

public class SubscribeWorkstationCustomAction
extends EventSubscribeCustomAction<Point> {
    public final SettlementDependantContainer container;

    public SubscribeWorkstationCustomAction(SettlementDependantContainer container) {
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
        this.container.subscribeEvent(SettlementWorkstationRecipeUpdateEvent.class, e -> e.settlementUniqueID == this.container.getSettlementUniqueID() && e.tileX == tile.x && e.tileY == tile.y, isActive);
        this.container.subscribeEvent(SettlementWorkstationRecipeRemoveEvent.class, e -> e.settlementUniqueID == this.container.getSettlementUniqueID() && e.tileX == tile.x && e.tileY == tile.y, isActive);
        this.container.subscribeEvent(SettlementWorkstationEvent.class, e -> e.settlementUniqueID == this.container.getSettlementUniqueID() && e.tileX == tile.x && e.tileY == tile.y, isActive);
    }
}

