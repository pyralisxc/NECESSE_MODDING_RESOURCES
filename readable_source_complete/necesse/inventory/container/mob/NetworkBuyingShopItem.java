/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.mob;

import java.util.Collection;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.ItemRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.Item;

public class NetworkBuyingShopItem {
    public final int shopItemID;
    public Item item;
    public int price;

    public NetworkBuyingShopItem(Item item, int price) {
        this.shopItemID = item.getID();
        this.item = item;
        this.price = price;
    }

    public NetworkBuyingShopItem(PacketReader reader) {
        this.shopItemID = reader.getNextShortUnsigned();
        this.item = ItemRegistry.getItem(this.shopItemID);
        this.price = reader.getNextInt();
    }

    public void writePacket(PacketWriter writer) {
        writer.putNextShortUnsigned(this.shopItemID);
        writer.putNextInt(this.price);
    }

    public boolean canCompleteTrade(Container container, Collection<Inventory> inventories, int shopWealth) {
        if (!this.canAffordCost(container.client, inventories, shopWealth)) {
            return false;
        }
        return this.canFitCoinsInDraggingSlot(container);
    }

    public boolean canFitCoinsInDraggingSlot(Container container) {
        ContainerSlot draggingSlot = container.getClientDraggingSlot();
        if (!draggingSlot.isClear()) {
            InventoryItem item = new InventoryItem("coin");
            if (draggingSlot.getItemAmount() + item.getAmount() > draggingSlot.getItemStackLimit(draggingSlot.getItem())) {
                return false;
            }
            PlayerMob player = container.client.playerMob;
            return draggingSlot.getItem().canCombine(player.getLevel(), player, item.copy(), "sell");
        }
        return true;
    }

    public boolean canAffordCost(NetworkClient client, Collection<Inventory> inventories, int shopWealth) {
        if (shopWealth >= 0 && shopWealth < this.price) {
            return false;
        }
        int ingredientsHave = 0;
        for (Inventory inv : inventories) {
            if (!inv.canBeUsedForCrafting() || (ingredientsHave += inv.getAmount(client.playerMob.getLevel(), client.playerMob, this.item, "sell")) < 1) continue;
            return true;
        }
        return false;
    }

    public int completeTrade(Container container, Collection<Inventory> inventories) {
        ContainerSlot draggingSlot = container.getClientDraggingSlot();
        NetworkClient client = container.client;
        InventoryItem item = new InventoryItem("coin", this.price);
        if (draggingSlot.isClear()) {
            draggingSlot.setItem(item);
        } else {
            draggingSlot.getItem().combine(client.playerMob.getLevel(), client.playerMob, draggingSlot.getInventory(), draggingSlot.getInventorySlot(), item, "sell", null);
        }
        this.consumeCost(client, inventories);
        if (client.isServer()) {
            client.getServerClient().newStats.items_sold.increment(1);
            client.getServerClient().newStats.money_earned.increment(this.price);
        }
        return this.price;
    }

    protected void consumeCost(NetworkClient client, Collection<Inventory> inventories) {
        int remainingToRemove = 1;
        for (Inventory inv : inventories) {
            if (inv.canBeUsedForCrafting() && (remainingToRemove -= inv.removeItems(client.playerMob.getLevel(), client.playerMob, this.item, remainingToRemove, "sell")) <= 0) break;
        }
    }
}

