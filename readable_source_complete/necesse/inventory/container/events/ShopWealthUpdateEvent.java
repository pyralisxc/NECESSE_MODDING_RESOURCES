/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.events;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.friendly.human.humanShop.ShopManager;
import necesse.inventory.container.events.ContainerEvent;

public class ShopWealthUpdateEvent
extends ContainerEvent {
    public int managerUniqueID;
    public int shopWealth;

    public ShopWealthUpdateEvent(ShopManager manager) {
        this.managerUniqueID = manager.uniqueID;
        this.shopWealth = manager.shopWealth;
    }

    public ShopWealthUpdateEvent(PacketReader reader) {
        super(reader);
        this.shopWealth = reader.getNextInt();
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextInt(this.shopWealth);
    }
}

