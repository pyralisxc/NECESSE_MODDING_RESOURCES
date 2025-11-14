/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly.human.humanWorkSetting;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.mob.ShopContainer;
import necesse.inventory.container.settlement.events.SettlementSettlerEquipmentFilterChangedEvent;
import necesse.inventory.itemFilter.ItemCategoriesFilterChange;
import necesse.level.maps.levelData.settlementData.LevelSettler;

public class SetEquipmentFilterSettingAction
extends ContainerCustomAction {
    public final ShopContainer container;

    public SetEquipmentFilterSettingAction(ShopContainer container) {
        this.container = container;
    }

    public void runAndSendPreferArmorSets(boolean preferArmorSets) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextMaxValue(0, 1);
        writer.putNextBoolean(preferArmorSets);
        this.runAndSendAction(content);
    }

    public void runAndSendChange(ItemCategoriesFilterChange change) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextMaxValue(1, 1);
        change.write(writer);
        this.runAndSendAction(content);
    }

    @Override
    public void executePacket(PacketReader reader) {
        LevelSettler settler;
        if (this.container.client.isServer() && (settler = this.container.humanShop.levelSettler) != null) {
            int packetType = reader.getNextMaxValue(1);
            switch (packetType) {
                case 0: {
                    settler.preferArmorSets = reader.getNextBoolean();
                    new SettlementSettlerEquipmentFilterChangedEvent(settler.data, this.container.humanShop.getUniqueID(), settler.preferArmorSets, null).applyAndSendToClientsAt(this.container.client.getServerClient());
                    break;
                }
                case 1: {
                    ItemCategoriesFilterChange change = ItemCategoriesFilterChange.fromPacket(reader);
                    if (!change.applyTo(settler.equipmentFilter)) break;
                    new SettlementSettlerEquipmentFilterChangedEvent(settler.data, this.container.humanShop.getUniqueID(), settler.preferArmorSets, change).applyAndSendToClientsAt(this.container.client.getServerClient());
                }
            }
        }
    }
}

