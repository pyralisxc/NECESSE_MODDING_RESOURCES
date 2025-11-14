/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly.human.humanShop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameLootUtils;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.friendly.human.humanShop.BuyingShopItem;
import necesse.entity.mobs.friendly.human.humanShop.HumanShop;
import necesse.entity.mobs.friendly.human.humanShop.SellingShopItem;
import necesse.entity.mobs.friendly.human.humanShop.ShopManager;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.CountOfTicketLootItems;
import necesse.inventory.lootTable.lootItem.LootItem;

public class BlacksmithHumanMob
extends HumanShop {
    public BlacksmithHumanMob() {
        super(500, 200, "blacksmith");
        this.attackCooldown = 500;
        this.attackAnimTime = 500;
        this.setSwimSpeed(1.0f);
        this.equipmentInventory.setItem(6, new InventoryItem("ironsword"));
        BlacksmithHumanMob.addBarBuyingToShop(this.shop);
        this.shop.addSellingItem("ironpickaxe", new SellingShopItem()).setStaticPriceBasedOnHappiness(40, 120, 40);
        this.shop.addSellingItem("ironfishingrod", new SellingShopItem()).setStaticPriceBasedOnHappiness(200, 400, 50);
        this.shop.addSellingItem("hardhat", new SellingShopItem()).setStaticPriceBasedOnHappiness(75, 150, 20);
        this.shop.addSellingItem("smithingapron", new SellingShopItem()).setStaticPriceBasedOnHappiness(75, 150, 20);
        this.shop.addSellingItem("smithingshoes", new SellingShopItem()).setStaticPriceBasedOnHappiness(75, 150, 20);
    }

    @Override
    public LootTable getLootTable() {
        return super.getLootTable();
    }

    @Override
    public void setDefaultArmor(HumanDrawOptions drawOptions) {
        drawOptions.chestplate(new InventoryItem("smithingapron"));
        drawOptions.boots(new InventoryItem("smithingshoes"));
    }

    @Override
    protected ArrayList<GameMessage> getMessages(ServerClient client) {
        ArrayList<GameMessage> out = this.getLocalMessages("blacksmithtalk", 4);
        HumanMob m = this.getRandomHuman("gunsmith");
        if (m != null) {
            out.add(new LocalMessage("mobmsg", "blacksmithspecial", "gunsmith", m.getSettlerName()));
        }
        return out;
    }

    @Override
    protected GameMessage getTrappedMessage(ServerClient client) {
        if (client.getLevel().isCave) {
            return new LocalMessage("mobmsg", "blacksmithtrappedcave");
        }
        return super.getTrappedMessage(client);
    }

    @Override
    public List<InventoryItem> getRecruitItems(ServerClient client) {
        if (this.isTrapped()) {
            return Collections.emptyList();
        }
        GameRandom random = new GameRandom((long)this.getSettlerSeed() * 29L);
        if (this.isVisitor()) {
            return Collections.singletonList(new InventoryItem("coin", random.getIntBetween(200, 400)));
        }
        LootTable secondItems = new LootTable(new CountOfTicketLootItems(random.getIntBetween(1, 2), 100, new LootItem("copperbar", Integer.MAX_VALUE), 100, new LootItem("ironbar", Integer.MAX_VALUE), 100, new LootItem("goldbar", Integer.MAX_VALUE)));
        ArrayList<InventoryItem> out = GameLootUtils.getItemsValuedAt(random, random.getIntBetween(200, 400), 0.2f, new LootItem("coin", Integer.MAX_VALUE), new Object[0]);
        out.addAll(GameLootUtils.getItemsValuedAt(random, random.getIntBetween(75, 150), 0.2f, secondItems, new Object[0]));
        out.sort(Comparator.comparing(InventoryItem::getBrokerValue).reversed());
        return out;
    }

    public static void addBarBuyingToShop(ShopManager shop) {
        shop.addBuyingItem("copperbar", new BuyingShopItem()).setPriceBasedOnHappiness(10, 4, 2);
        shop.addBuyingItem("ironbar", new BuyingShopItem()).setPriceBasedOnHappiness(12, 6, 2);
        shop.addBuyingItem("goldbar", new BuyingShopItem()).setPriceBasedOnHappiness(18, 10, 2);
        shop.addBuyingItem("brokencoppertool", new BuyingShopItem()).setPriceBasedOnHappiness(60, 20, 10);
        shop.addBuyingItem("brokenirontool", new BuyingShopItem()).setPriceBasedOnHappiness(70, 30, 10);
        shop.addBuyingItem("demonicbar", new BuyingShopItem()).setPriceBasedOnHappiness(18, 10, 2).addKilledMobRequirement("evilsprotector");
        shop.addBuyingItem("ivybar", new BuyingShopItem()).setPriceBasedOnHappiness(22, 13, 3).addKilledMobRequirement("evilsprotector");
        shop.addBuyingItem("quartz", new BuyingShopItem()).setPriceBasedOnHappiness(25, 15, 3).addKilledMobRequirement("evilsprotector");
        shop.addBuyingItem("obsidian", new BuyingShopItem()).setPriceBasedOnHappiness(8, 2, 2).addKilledMobRequirement("piratecaptain");
        shop.addBuyingItem("lifequartz", new BuyingShopItem()).setPriceBasedOnHappiness(35, 20, 5).addKilledMobRequirement("piratecaptain");
        shop.addBuyingItem("tungstenbar", new BuyingShopItem()).setPriceBasedOnHappiness(35, 20, 5).addKilledMobRequirement("piratecaptain");
        shop.addBuyingItem("glacialbar", new BuyingShopItem()).setPriceBasedOnHappiness(35, 20, 5).addKilledMobRequirement("piratecaptain");
        shop.addBuyingItem("myceliumbar", new BuyingShopItem()).setPriceBasedOnHappiness(35, 20, 5).addKilledMobRequirement("piratecaptain");
        shop.addBuyingItem("ancientfossilbar", new BuyingShopItem()).setPriceBasedOnHappiness(35, 20, 5).addKilledMobRequirement("piratecaptain");
    }
}

