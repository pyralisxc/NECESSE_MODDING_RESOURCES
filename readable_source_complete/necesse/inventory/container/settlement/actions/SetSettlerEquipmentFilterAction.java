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
import necesse.inventory.container.settlement.events.SettlementSettlerEquipmentFilterChangedEvent;
import necesse.inventory.container.settlement.events.SettlementSettlersChangedEvent;
import necesse.inventory.itemFilter.ItemCategoriesFilterChange;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class SetSettlerEquipmentFilterAction
extends SettlementAccessRequiredContainerCustomAction {
    public SetSettlerEquipmentFilterAction(SettlementContainer container) {
        super(container);
    }

    public void runAndSendPreferArmorSets(int mobUniqueID, boolean preferArmorSets) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(mobUniqueID);
        writer.putNextMaxValue(0, 1);
        writer.putNextBoolean(preferArmorSets);
        this.runAndSendAction(content);
    }

    public void runAndSendChange(int mobUniqueID, ItemCategoriesFilterChange change) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(mobUniqueID);
        writer.putNextMaxValue(1, 1);
        change.write(writer);
        this.runAndSendAction(content);
    }

    @Override
    public void executePacket(PacketReader reader, ServerSettlementData data, ServerClient client) {
        int mobUniqueID = reader.getNextInt();
        LevelSettler settler = data.getSettler(mobUniqueID);
        if (settler != null) {
            int packetType = reader.getNextMaxValue(1);
            switch (packetType) {
                case 0: {
                    boolean preferArmorSets = reader.getNextBoolean();
                    if (settler.preferArmorSets == preferArmorSets) break;
                    settler.preferArmorSets = preferArmorSets;
                    new SettlementSettlerEquipmentFilterChangedEvent(data, mobUniqueID, settler.preferArmorSets, null).applyAndSendToClientsAt(client);
                    break;
                }
                case 1: {
                    ItemCategoriesFilterChange change = ItemCategoriesFilterChange.fromPacket(reader);
                    if (!change.applyTo(settler.equipmentFilter)) break;
                    new SettlementSettlerEquipmentFilterChangedEvent(data, mobUniqueID, settler.preferArmorSets, change).applyAndSendToClientsAt(client);
                }
            }
        } else {
            new SettlementSettlersChangedEvent(data).applyAndSendToClient(client);
        }
    }
}

