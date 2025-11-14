/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.mob;

import java.util.Collection;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.Item;

public class NetworkSellingShopItem {
    public final int shopItemID;
    public InventoryItem item;
    public int noStockPrice;
    public int fullStockPrice;
    public int currentStock;
    public int maxStock;

    public NetworkSellingShopItem(int shopItemID, InventoryItem item, int noStockPrice, int fullStockPrice, int currentStock, int maxStock) {
        this.shopItemID = shopItemID;
        this.item = item;
        this.noStockPrice = noStockPrice;
        this.fullStockPrice = fullStockPrice;
        this.currentStock = currentStock;
        this.maxStock = maxStock;
    }

    public NetworkSellingShopItem(PacketReader reader) {
        this.shopItemID = reader.getNextInt();
        this.item = InventoryItem.fromContentPacket(reader);
        this.noStockPrice = reader.getNextInt();
        this.fullStockPrice = reader.getNextInt();
        this.currentStock = reader.getNextInt();
        this.maxStock = reader.getNextInt();
    }

    public void writePacket(PacketWriter writer) {
        writer.putNextInt(this.shopItemID);
        InventoryItem.addPacketContent(this.item, writer);
        writer.putNextInt(this.noStockPrice);
        writer.putNextInt(this.fullStockPrice);
        writer.putNextInt(this.currentStock);
        writer.putNextInt(this.maxStock);
    }

    public int getPriceAtStock(int remainingStock) {
        if (this.maxStock <= 0) {
            return this.noStockPrice;
        }
        float stockPercent = (float)remainingStock / (float)this.maxStock;
        return GameMath.lerp(stockPercent, this.noStockPrice, this.fullStockPrice);
    }

    public int getCurrentPrice() {
        return this.getPriceAtStock(this.currentStock);
    }

    public boolean canCompleteTrade(Container container, Collection<Inventory> inventories) {
        if (this.maxStock >= 0 && this.currentStock <= 0) {
            return false;
        }
        if (!this.canAffordCost(container.client, inventories)) {
            return false;
        }
        return this.canFitInDraggingSlot(container);
    }

    public boolean canFitInDraggingSlot(Container container) {
        ContainerSlot draggingSlot = container.getClientDraggingSlot();
        if (!draggingSlot.isClear()) {
            if (draggingSlot.getItemAmount() + this.item.getAmount() > draggingSlot.getItemStackLimit(draggingSlot.getItem())) {
                return false;
            }
            PlayerMob player = container.client.playerMob;
            return draggingSlot.getItem().canCombine(player.getLevel(), player, this.item.copy(), "buy");
        }
        return true;
    }

    public boolean canAffordCost(NetworkClient client, Collection<Inventory> inventories) {
        int currentPrice = this.getCurrentPrice();
        int ingredientsHave = 0;
        Item ingredientItem = ItemRegistry.getItem("coin");
        for (Inventory inv : inventories) {
            if (!inv.canBeUsedForCrafting() || (ingredientsHave += inv.getAmount(client.playerMob.getLevel(), client.playerMob, ingredientItem, "buy")) < currentPrice) continue;
            return true;
        }
        return false;
    }

    public int completeTrade(Container container, Collection<Inventory> inventories) {
        int price = this.getCurrentPrice();
        ContainerSlot draggingSlot = container.getClientDraggingSlot();
        NetworkClient client = container.client;
        if (draggingSlot.isClear()) {
            draggingSlot.setItem(this.item.copy());
        } else {
            draggingSlot.getItem().combine(client.playerMob.getLevel(), client.playerMob, draggingSlot.getInventory(), draggingSlot.getInventorySlot(), this.item.copy(), "buy", null);
        }
        this.consumeCost(client, inventories, price);
        if (client.isServer()) {
            client.getServerClient().newStats.items_bought.increment(this.item.getAmount());
            client.getServerClient().newStats.money_spent.increment(price);
        }
        if (this.maxStock > 0) {
            --this.currentStock;
        }
        return price;
    }

    protected void consumeCost(NetworkClient client, Collection<Inventory> inventories, int price) {
        int remainingToRemove = price;
        Item ingredientItem = ItemRegistry.getItem("coin");
        for (Inventory inv : inventories) {
            if (inv.canBeUsedForCrafting() && (remainingToRemove -= inv.removeItems(client.playerMob.getLevel(), client.playerMob, ingredientItem, remainingToRemove, "buy")) <= 0) break;
        }
    }
}

