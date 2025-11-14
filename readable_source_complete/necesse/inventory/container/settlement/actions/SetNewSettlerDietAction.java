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
import necesse.inventory.container.settlement.events.SettlementNewSettlerDietChangedEvent;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.inventory.itemFilter.ItemCategoriesFilterChange;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class SetNewSettlerDietAction
extends SettlementAccessRequiredContainerCustomAction {
    public SetNewSettlerDietAction(SettlementContainer container) {
        super(container);
    }

    public void runAndSendChange(ItemCategoriesFilterChange change) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        change.write(writer);
        this.runAndSendAction(content);
    }

    @Override
    public void executePacket(PacketReader reader, ServerSettlementData data, ServerClient client) {
        ItemCategoriesFilter diet;
        ItemCategoriesFilterChange change = ItemCategoriesFilterChange.fromPacket(reader);
        if (change.applyTo(diet = data.getNewSettlerDiet())) {
            new SettlementNewSettlerDietChangedEvent(data, change).applyAndSendToClientsAt(client);
        }
    }
}

