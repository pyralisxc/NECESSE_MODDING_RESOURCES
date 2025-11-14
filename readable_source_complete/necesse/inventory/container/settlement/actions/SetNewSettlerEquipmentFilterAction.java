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
import necesse.inventory.container.settlement.events.SettlementNewSettlerEquipmentFilterChangedEvent;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.inventory.itemFilter.ItemCategoriesFilterChange;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class SetNewSettlerEquipmentFilterAction
extends SettlementAccessRequiredContainerCustomAction {
    public SetNewSettlerEquipmentFilterAction(SettlementContainer container) {
        super(container);
    }

    public void runAndSendSelfManageEquipment(boolean selfManageEquipment) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextMaxValue(0, 2);
        writer.putNextBoolean(selfManageEquipment);
        this.runAndSendAction(content);
    }

    public void runAndSendPreferArmorSets(boolean preferArmorSets) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextMaxValue(1, 2);
        writer.putNextBoolean(preferArmorSets);
        this.runAndSendAction(content);
    }

    public void runAndSendChange(ItemCategoriesFilterChange change) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextMaxValue(2, 2);
        change.write(writer);
        this.runAndSendAction(content);
    }

    @Override
    public void executePacket(PacketReader reader, ServerSettlementData data, ServerClient client) {
        int packetType = reader.getNextMaxValue(2);
        switch (packetType) {
            case 0: {
                boolean selfManageEquipment = reader.getNextBoolean();
                if (selfManageEquipment == data.newSettlerSelfManageEquipment) break;
                data.newSettlerSelfManageEquipment = selfManageEquipment;
                new SettlementNewSettlerEquipmentFilterChangedEvent(data, data.newSettlerSelfManageEquipment, data.newSettlerEquipmentPreferArmorSets, null).applyAndSendToClientsAt(client);
                break;
            }
            case 1: {
                boolean preferArmorSets = reader.getNextBoolean();
                if (preferArmorSets == data.newSettlerEquipmentPreferArmorSets) break;
                data.newSettlerEquipmentPreferArmorSets = preferArmorSets;
                new SettlementNewSettlerEquipmentFilterChangedEvent(data, data.newSettlerSelfManageEquipment, data.newSettlerEquipmentPreferArmorSets, null).applyAndSendToClientsAt(client);
                break;
            }
            case 2: {
                ItemCategoriesFilterChange change = ItemCategoriesFilterChange.fromPacket(reader);
                ItemCategoriesFilter filter = data.getNewSettlerEquipmentFilter();
                if (!change.applyTo(filter)) break;
                new SettlementNewSettlerEquipmentFilterChangedEvent(data, data.newSettlerSelfManageEquipment, data.newSettlerEquipmentPreferArmorSets, change).applyAndSendToClientsAt(client);
            }
        }
    }
}

