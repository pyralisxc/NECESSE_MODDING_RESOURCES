/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.events;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationRecipe;

public class SettlementWorkstationRecipeUpdateEvent
extends ContainerEvent {
    public final int settlementUniqueID;
    public final int tileX;
    public final int tileY;
    public final int index;
    public final int recipeUniqueID;
    public final Packet recipeContent;

    public SettlementWorkstationRecipeUpdateEvent(ServerSettlementData data, int tileX, int tileY, int index, SettlementWorkstationRecipe recipe) {
        this.settlementUniqueID = data.uniqueID;
        this.tileX = tileX;
        this.tileY = tileY;
        this.index = index;
        this.recipeUniqueID = recipe.uniqueID;
        this.recipeContent = new Packet();
        recipe.writePacket(new PacketWriter(this.recipeContent));
    }

    public SettlementWorkstationRecipeUpdateEvent(ServerSettlementData data, int tileX, int tileY, SettlementWorkstationRecipe recipe) {
        this(data, tileX, tileY, -1, recipe);
    }

    public SettlementWorkstationRecipeUpdateEvent(PacketReader reader) {
        super(reader);
        this.settlementUniqueID = reader.getNextInt();
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
        this.index = reader.getNextShort();
        this.recipeUniqueID = reader.getNextInt();
        this.recipeContent = reader.getNextContentPacket();
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextInt(this.settlementUniqueID);
        writer.putNextInt(this.tileX);
        writer.putNextInt(this.tileY);
        writer.putNextShort((short)this.index);
        writer.putNextInt(this.recipeUniqueID);
        writer.putNextContentPacket(this.recipeContent);
    }
}

