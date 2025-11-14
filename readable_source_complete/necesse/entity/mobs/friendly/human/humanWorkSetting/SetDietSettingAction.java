/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly.human.humanWorkSetting;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.mob.ShopContainer;
import necesse.inventory.container.settlement.events.SettlementSettlerDietChangedEvent;
import necesse.inventory.itemFilter.ItemCategoriesFilterChange;
import necesse.level.maps.levelData.settlementData.LevelSettler;

public class SetDietSettingAction
extends ContainerCustomAction {
    public final ShopContainer container;

    public SetDietSettingAction(ShopContainer container) {
        this.container = container;
    }

    public void runAndSendChange(ItemCategoriesFilterChange change) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        change.write(writer);
        this.runAndSendAction(content);
    }

    @Override
    public void executePacket(PacketReader reader) {
        ItemCategoriesFilterChange change;
        LevelSettler settler;
        if (this.container.client.isServer() && (settler = this.container.humanShop.levelSettler) != null && (change = ItemCategoriesFilterChange.fromPacket(reader)).applyTo(settler.dietFilter)) {
            new SettlementSettlerDietChangedEvent(settler.data, this.container.humanShop.getUniqueID(), change).applyAndSendToClientsAt(this.container.client.getServerClient());
        }
    }
}

