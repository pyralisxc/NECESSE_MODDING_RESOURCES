/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.actions.workstation;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.inventory.container.settlement.actions.workstation.ConfigureWorkstationAction;
import necesse.inventory.container.settlement.events.SettlementWorkstationEvent;
import necesse.inventory.container.settlement.events.SettlementWorkstationRecipeRemoveEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.SettlementWorkstation;

public class RemoveWorkstationRecipeAction
extends ConfigureWorkstationAction {
    public RemoveWorkstationRecipeAction(SettlementDependantContainer container) {
        super(container);
    }

    public void runAndSend(int tileX, int tileY, int uniqueID) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(uniqueID);
        this.runAndSend(tileX, tileY, content);
    }

    @Override
    public void handleAction(PacketReader reader, ServerSettlementData serverData, SettlementWorkstation workstation) {
        int uniqueID = reader.getNextInt();
        if (workstation.recipes.removeIf(r -> r.uniqueID == uniqueID)) {
            new SettlementWorkstationRecipeRemoveEvent(serverData, workstation.tileX, workstation.tileY, uniqueID).applyAndSendToClientsAt(serverData.getLevel());
        } else {
            new SettlementWorkstationEvent(serverData, workstation).applyAndSendToClient(this.container.client.getServerClient());
        }
    }
}

