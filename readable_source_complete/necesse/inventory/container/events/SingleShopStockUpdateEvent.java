/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.events;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.friendly.human.humanShop.SellingShopItem;
import necesse.entity.mobs.friendly.human.humanShop.ShopManager;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.container.mob.NetworkSellingShopItem;

public class SingleShopStockUpdateEvent
extends ContainerEvent {
    public int managerUniqueID;
    public int shopItemID;
    public int currentStock;
    public int shopWealth;

    public SingleShopStockUpdateEvent(ShopManager manager, SellingShopItem shopItem) {
        this.managerUniqueID = manager.uniqueID;
        this.shopItemID = shopItem.getID();
        this.currentStock = shopItem.currentStock;
        this.shopWealth = -1;
    }

    public SingleShopStockUpdateEvent(ShopManager manager, NetworkSellingShopItem shopItem) {
        this.managerUniqueID = manager.uniqueID;
        this.shopItemID = shopItem.shopItemID;
        this.currentStock = shopItem.currentStock;
        this.shopWealth = manager.shopWealth;
    }

    public SingleShopStockUpdateEvent(PacketReader reader) {
        super(reader);
        this.shopItemID = reader.getNextShortUnsigned();
        this.currentStock = reader.getNextInt();
        if (reader.getNextBoolean()) {
            this.shopWealth = reader.getNextInt();
        }
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextShortUnsigned(this.shopItemID);
        writer.putNextInt(this.currentStock);
        if (this.shopWealth >= 0) {
            writer.putNextBoolean(true);
            writer.putNextInt(this.shopWealth);
        } else {
            writer.putNextBoolean(false);
        }
    }
}

