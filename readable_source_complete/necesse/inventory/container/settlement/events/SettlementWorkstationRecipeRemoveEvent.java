/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.events;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class SettlementWorkstationRecipeRemoveEvent
extends ContainerEvent {
    public final int settlementUniqueID;
    public final int tileX;
    public final int tileY;
    public final int recipeUniqueID;

    public SettlementWorkstationRecipeRemoveEvent(ServerSettlementData data, int tileX, int tileY, int recipeUniqueID) {
        this.settlementUniqueID = data.uniqueID;
        this.tileX = tileX;
        this.tileY = tileY;
        this.recipeUniqueID = recipeUniqueID;
    }

    public SettlementWorkstationRecipeRemoveEvent(PacketReader reader) {
        super(reader);
        this.settlementUniqueID = reader.getNextInt();
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
        this.recipeUniqueID = reader.getNextInt();
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextInt(this.settlementUniqueID);
        writer.putNextInt(this.tileX);
        writer.putNextInt(this.tileY);
        writer.putNextInt(this.recipeUniqueID);
    }
}

