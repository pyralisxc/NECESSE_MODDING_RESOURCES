/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly.human.humanShop;

import java.util.ArrayList;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.GameRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.engine.util.IntRange;
import necesse.engine.world.WorldEntity;
import necesse.entity.mobs.friendly.human.humanShop.BuyingShopItem;
import necesse.entity.mobs.friendly.human.humanShop.HumanShop;
import necesse.entity.mobs.friendly.human.humanShop.SellingShopItem;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.events.ShopWealthUpdateEvent;
import necesse.inventory.container.mob.NetworkBuyingShopItem;
import necesse.inventory.container.mob.NetworkSellingShopItem;
import necesse.level.maps.Level;

public class ShopManager {
    public int uniqueID = -1;
    public SellingShopRegistry sellingShop = new SellingShopRegistry();
    public BuyingShopRegistry buyingShop = new BuyingShopRegistry();
    public int lastDayStockUpdate = -1;
    public int shopWealth;
    public IntRange maxShopWealthRange;
    public IntRange shopWealthChangePerDay;
    protected boolean forceSaveWealth;

    public ShopManager() {
        this.setMaxShopWealth(1000, 100);
    }

    public void addSaveData(SaveData save) {
        if (this.sellingShop.getSize() > 0 || this.buyingShop.getSize() > 0 || this.forceSaveWealth) {
            if (this.lastDayStockUpdate >= 0) {
                save.addInt("lastDayStockUpdate", this.lastDayStockUpdate);
            }
            if (this.maxShopWealthRange != null) {
                save.addInt("shopWealth", this.shopWealth);
            }
        }
        SaveData itemsSave = new SaveData("ITEMS");
        for (SellingShopItem item : this.sellingShop.getItems()) {
            if (!item.shouldSave()) continue;
            SaveData itemSave = new SaveData("ITEM");
            itemSave.addSafeString("stringID", item.getStringID());
            item.addSaveData(itemSave);
            itemsSave.addSaveData(itemSave);
        }
        save.addSaveData(itemsSave);
    }

    public void applyLoadData(LoadData save) {
        this.lastDayStockUpdate = save.getInt("lastDayStockUpdate", this.lastDayStockUpdate, false);
        this.shopWealth = save.getInt("shopWealth", this.shopWealth, false);
        LoadData itemsSave = save.getFirstLoadDataByName("ITEMS");
        if (itemsSave != null) {
            for (LoadData itemSave : itemsSave.getLoadDataByName("ITEM")) {
                SellingShopItem item;
                String itemStringID = itemSave.getSafeString("stringID", null);
                if (itemStringID == null || (item = this.sellingShop.getItem(itemStringID)) == null) continue;
                item.applyLoadData(itemSave);
            }
        }
    }

    public void init(Level level, int uniqueSeed) {
        if (this.uniqueID == -1) {
            this.uniqueID = new GameRandom(uniqueSeed).nextSeeded().nextInt();
        }
        this.sellingShop.closeRegistry();
        this.buyingShop.closeRegistry();
        for (SellingShopItem sellingShopItem : this.sellingShop.getItems()) {
            sellingShopItem.init(level, uniqueSeed);
        }
        for (BuyingShopItem buyingShopItem : this.buyingShop.getItems()) {
            buyingShopItem.init(level, uniqueSeed);
        }
    }

    public void serverTick(WorldEntity worldEntity, Server server) {
        if (worldEntity == null) {
            return;
        }
        int currentDay = worldEntity.getDay();
        if (this.lastDayStockUpdate < 0) {
            this.lastDayStockUpdate = currentDay;
        } else if (this.lastDayStockUpdate < currentDay) {
            int daysPassed = currentDay - this.lastDayStockUpdate;
            this.lastDayStockUpdate = currentDay;
            for (SellingShopItem item : this.sellingShop.getItems()) {
                item.restock(server, daysPassed);
            }
            int lastShopWealth = this.shopWealth;
            if (this.maxShopWealthRange != null) {
                int thisDayMaxWealth = this.maxShopWealthRange.getRandomValueInRange(GameRandom.globalRandom);
                int wealthChange = this.shopWealthChangePerDay.getRandomValueInRange(GameRandom.globalRandom);
                if (this.shopWealth < thisDayMaxWealth) {
                    this.shopWealth = Math.min(this.shopWealth + wealthChange * daysPassed, thisDayMaxWealth);
                } else {
                    double multiplier = (double)this.shopWealth / (double)thisDayMaxWealth;
                    this.shopWealth = Math.max(this.shopWealth - (int)((double)(wealthChange * daysPassed) * multiplier), thisDayMaxWealth);
                }
            } else {
                this.shopWealth = -1;
            }
            if (lastShopWealth != this.shopWealth && server != null) {
                new ShopWealthUpdateEvent(this).applyAndSendToAllClients(server);
            }
        }
    }

    public void setMaxShopWealth(IntRange maxWealthRange, IntRange changePerDay) {
        if (this.sellingShop.isClosed()) {
            throw new IllegalStateException("Cannot set max shop wealth on closed registry");
        }
        this.maxShopWealthRange = maxWealthRange;
        this.shopWealth = maxWealthRange != null ? GameRandom.globalRandom.getIntBetween(maxWealthRange.min, maxWealthRange.max) : -1;
        this.shopWealthChangePerDay = changePerDay;
    }

    public void setMaxShopWealth(int maxWealth, int changePerDay) {
        int maxWealthRange = maxWealth / 5;
        int changePerDayRange = changePerDay / 5;
        this.setMaxShopWealth(new IntRange(maxWealth - maxWealthRange, maxWealth + maxWealthRange), new IntRange(changePerDay - changePerDayRange, changePerDay + changePerDayRange));
    }

    public SellingShopItem addSellingItem(String stringID, SellingShopItem item) {
        return this.sellingShop.addItem(stringID, item);
    }

    public BuyingShopItem addBuyingItem(String stringID, BuyingShopItem item) {
        return this.buyingShop.addItem(stringID, item);
    }

    public void forceSaveWealth() {
        this.forceSaveWealth = true;
    }

    public ArrayList<NetworkSellingShopItem> getSellingItemsList(ServerClient client, HumanShop mob, long shopSeed) {
        ArrayList<NetworkSellingShopItem> items = new ArrayList<NetworkSellingShopItem>();
        GameRandom random = new GameRandom(shopSeed + 5L);
        GameBlackboard blackboard = new GameBlackboard();
        for (SellingShopItem shopItem : this.sellingShop.getItems()) {
            IntRange price;
            InventoryItem item;
            GameRandom itemRandom;
            if (!shopItem.isAvailable(itemRandom = random.nextSeeded(), client, mob, blackboard) || (item = shopItem.generateItem(itemRandom, client, mob)) == null || (price = shopItem.generatePrice(itemRandom, client, mob)) == null) continue;
            items.add(new NetworkSellingShopItem(shopItem.getID(), item, price.min, price.max, shopItem.currentStock, shopItem.maxStock));
        }
        return items;
    }

    public ArrayList<NetworkBuyingShopItem> getBuyingItemsList(ServerClient client, HumanShop mob, long shopSeed) {
        ArrayList<NetworkBuyingShopItem> items = new ArrayList<NetworkBuyingShopItem>();
        GameRandom random = new GameRandom(shopSeed + 5L);
        for (BuyingShopItem shopItem : this.buyingShop.getItems()) {
            GameRandom itemRandom;
            if (!shopItem.isAvailable(itemRandom = random.nextSeeded(), client)) continue;
            int price = shopItem.generatePrice(itemRandom, client, mob);
            items.add(new NetworkBuyingShopItem(shopItem.item, price));
        }
        return items;
    }

    public class SellingShopRegistry
    extends GameRegistry<SellingShopItem> {
        public SellingShopRegistry() {
            super("SellingShopItem", Short.MAX_VALUE);
        }

        @Override
        public void registerCore() {
        }

        @Override
        protected void onRegister(SellingShopItem object, int id, String stringID, boolean isReplace) {
            object.onRegistered(ShopManager.this);
        }

        @Override
        protected void onRegistryClose() {
            for (SellingShopItem element : this.getElements()) {
                element.onRegistryClosed();
            }
        }

        public SellingShopItem addItem(String stringID, SellingShopItem item) {
            this.register(stringID, item);
            return item;
        }

        public int getSize() {
            return this.getTotalElements();
        }

        public Iterable<SellingShopItem> getItems() {
            return this.getElements();
        }

        public SellingShopItem getItem(String stringID) {
            return (SellingShopItem)this.getElement(stringID);
        }

        public SellingShopItem getItem(int id) {
            return (SellingShopItem)this.getElement(id);
        }
    }

    public class BuyingShopRegistry
    extends GameRegistry<BuyingShopItem> {
        public BuyingShopRegistry() {
            super("BuyingShopItem", Short.MAX_VALUE);
        }

        @Override
        public void registerCore() {
        }

        @Override
        protected void onRegister(BuyingShopItem object, int id, String stringID, boolean isReplace) {
            object.onRegistered(ShopManager.this);
        }

        @Override
        protected void onRegistryClose() {
            for (BuyingShopItem element : this.getElements()) {
                element.onRegistryClosed();
            }
        }

        public BuyingShopItem addItem(String stringID, BuyingShopItem item) {
            this.register(stringID, item);
            return item;
        }

        public int getSize() {
            return this.getTotalElements();
        }

        public Iterable<BuyingShopItem> getItems() {
            return this.getElements();
        }

        public BuyingShopItem getItem(String stringID) {
            return (BuyingShopItem)this.getElement(stringID);
        }

        public BuyingShopItem getItem(int id) {
            return (BuyingShopItem)this.getElement(id);
        }
    }
}

