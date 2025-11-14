/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.events;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.friendly.human.humanShop.ShopManager;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.container.mob.NetworkSellingShopItem;

public class FullShopStockUpdateEvent
extends ContainerEvent {
    public int managerUniqueID;
    public int shopWealth;
    public LinkedHashMap<Integer, Integer> shopItemIDsStock;

    public FullShopStockUpdateEvent(ShopManager manager, Collection<NetworkSellingShopItem> shopItems) {
        this.managerUniqueID = manager.uniqueID;
        this.shopWealth = manager.shopWealth;
        this.shopItemIDsStock = new LinkedHashMap();
        for (NetworkSellingShopItem shopItem : shopItems) {
            this.shopItemIDsStock.put(shopItem.shopItemID, shopItem.currentStock);
        }
    }

    public FullShopStockUpdateEvent(PacketReader reader) {
        super(reader);
        this.shopWealth = reader.getNextInt();
        int size = reader.getNextShortUnsigned();
        this.shopItemIDsStock = new LinkedHashMap(size);
        for (int i = 0; i < size; ++i) {
            int id = reader.getNextShortUnsigned();
            int stock = reader.getNextInt();
            this.shopItemIDsStock.put(id, stock);
        }
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextInt(this.shopWealth);
        writer.putNextShortUnsigned(this.shopItemIDsStock.size());
        for (Map.Entry<Integer, Integer> entry : this.shopItemIDsStock.entrySet()) {
            writer.putNextShortUnsigned(entry.getKey());
            writer.putNextInt(entry.getValue());
        }
    }
}

