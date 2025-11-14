/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.actions.workstation;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.inventory.container.settlement.actions.workstation.ConfigureWorkstationAction;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.SettlementWorkstation;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationRecipe;

public class UpdateWorkstationRecipeAction
extends ConfigureWorkstationAction {
    public UpdateWorkstationRecipeAction(SettlementDependantContainer container) {
        super(container);
    }

    public void runAndSend(int tileX, int tileY, int index, SettlementWorkstationRecipe recipe) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextShortUnsigned(index);
        writer.putNextInt(recipe.uniqueID);
        recipe.writePacket(writer);
        this.runAndSend(tileX, tileY, content);
    }

    @Override
    public void handleAction(PacketReader reader, ServerSettlementData serverData, SettlementWorkstation workstation) {
        int index = reader.getNextShortUnsigned();
        int uniqueID = reader.getNextInt();
        workstation.updateRecipe(index, uniqueID, reader);
    }
}

