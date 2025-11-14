/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.actions;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.actions.SettlementAccessRequiredContainerCustomAction;
import necesse.inventory.container.settlement.events.SettlementSettlerDietChangedEvent;
import necesse.inventory.container.settlement.events.SettlementSettlersChangedEvent;
import necesse.inventory.itemFilter.ItemCategoriesFilterChange;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class SetSettlerDietAction
extends SettlementAccessRequiredContainerCustomAction {
    public SetSettlerDietAction(SettlementContainer container) {
        super(container);
    }

    public void runAndSendChange(int mobUniqueID, ItemCategoriesFilterChange change) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(mobUniqueID);
        change.write(writer);
        this.runAndSendAction(content);
    }

    @Override
    public void executePacket(PacketReader reader, ServerSettlementData data, ServerClient client) {
        int mobUniqueID = reader.getNextInt();
        LevelSettler settler = data.getSettler(mobUniqueID);
        if (settler != null) {
            ItemCategoriesFilterChange change = ItemCategoriesFilterChange.fromPacket(reader);
            if (change.applyTo(settler.dietFilter)) {
                new SettlementSettlerDietChangedEvent(data, mobUniqueID, change).applyAndSendToClientsAt(client);
            }
        } else {
            new SettlementSettlersChangedEvent(data).applyAndSendToClient(client);
        }
    }
}

