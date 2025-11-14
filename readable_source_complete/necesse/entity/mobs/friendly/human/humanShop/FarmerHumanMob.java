/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly.human.humanShop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameLootUtils;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.humanShop.BuyingShopItem;
import necesse.entity.mobs.friendly.human.humanShop.HumanShop;
import necesse.entity.mobs.friendly.human.humanShop.SellingShopItem;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.CountOfTicketLootItems;
import necesse.inventory.lootTable.lootItem.LootItem;

public class FarmerHumanMob
extends HumanShop {
    public FarmerHumanMob() {
        super(500, 200, "farmer");
        this.setSwimSpeed(1.0f);
        this.jobTypeHandler.getPriority((String)"fertilize").disabledBySettler = false;
        this.equipmentInventory.setItem(6, new InventoryItem("copperpitchfork"));
        this.shop.addSellingItem("rope", new SellingShopItem(10, 4)).setStaticPriceBasedOnHappiness(100, 190, 30);
        this.shop.addSellingItem("farmerhat", new SellingShopItem()).setStaticPriceBasedOnHappiness(85, 160, 30);
        this.shop.addSellingItem("farmershirt", new SellingShopItem()).setStaticPriceBasedOnHappiness(100, 200, 30);
        this.shop.addSellingItem("farmershoes", new SellingShopItem()).setStaticPriceBasedOnHappiness(70, 150, 30);
        this.shop.addSellingItem("farmingscythe", new SellingShopItem()).setStaticPriceBasedOnHappiness(650, 1000, 100).addKilledMobRequirement("voidwizard");
        this.shop.addSellingItem("fertilizer", new SellingShopItem(500, 50)).setStockPriceBasedOnHappiness(4, 40, 12, 60, 3);
        this.shop.addSellingItem("wheatseed", new SellingShopItem(20, 4)).setStaticPriceBasedOnHappiness(20, 50, 10);
        this.shop.addSellingItem("cornseed", new SellingShopItem(20, 4)).setStaticPriceBasedOnHappiness(20, 50, 10);
        this.shop.addSellingItem("tomatoseed", new SellingShopItem(20, 4)).setStaticPriceBasedOnHappiness(20, 50, 10);
        this.shop.addSellingItem("cabbageseed", new SellingShopItem(20, 4)).setStaticPriceBasedOnHappiness(20, 50, 10);
        this.shop.addSellingItem("beetseed", new SellingShopItem(20, 4)).setStaticPriceBasedOnHappiness(20, 50, 10);
        this.shop.addSellingItem("chilipepperseed", new SellingShopItem(20, 4)).setStaticPriceBasedOnHappiness(30, 60, 10).addKilledMobRequirement("queenspider");
        this.shop.addSellingItem("sugarbeetseed", new SellingShopItem(20, 4)).setStaticPriceBasedOnHappiness(30, 60, 10).addKilledMobRequirement("queenspider");
        this.shop.addSellingItem("eggplantseed", new SellingShopItem(20, 4)).setStaticPriceBasedOnHappiness(50, 100, 15).addKilledMobRequirement("swampguardian");
        this.shop.addSellingItem("potatoseed", new SellingShopItem(20, 4)).setStaticPriceBasedOnHappiness(50, 100, 15).addKilledMobRequirement("swampguardian");
        this.shop.addSellingItem("riceseed", new SellingShopItem(20, 4)).setStaticPriceBasedOnHappiness(60, 120, 20).addKilledMobRequirement("ancientvulture");
        this.shop.addSellingItem("carrotseed", new SellingShopItem(20, 4)).setStaticPriceBasedOnHappiness(60, 120, 20).addKilledMobRequirement("ancientvulture");
        this.shop.addSellingItem("blueberrysapling", new SellingShopItem(5, 1)).setStaticPriceBasedOnHappiness(250, 500, 50).addKilledMobRequirement("piratecaptain");
        this.shop.addSellingItem("raspberrysapling", new SellingShopItem(5, 1)).setStaticPriceBasedOnHappiness(250, 500, 50).addKilledMobRequirement("piratecaptain");
        this.shop.addSellingItem("blackberrysapling", new SellingShopItem(5, 1)).setStaticPriceBasedOnHappiness(250, 500, 50).addKilledMobRequirement("piratecaptain");
        this.shop.addSellingItem("applesapling", new SellingShopItem(3, 1)).setStaticPriceBasedOnHappiness(500, 1000, 100).addKilledMobRequirement("piratecaptain");
        this.shop.addSellingItem("coconutsapling", new SellingShopItem(3, 1)).setStaticPriceBasedOnHappiness(500, 1000, 100).addKilledMobRequirement("piratecaptain");
        this.shop.addSellingItem("onionseed", new SellingShopItem(20, 4)).setStaticPriceBasedOnHappiness(80, 160, 20).addKilledMobRequirement("reaper");
        this.shop.addSellingItem("pumpkinseed", new SellingShopItem(20, 4)).setStaticPriceBasedOnHappiness(80, 160, 20).addKilledMobRequirement("reaper");
        this.shop.addSellingItem("strawberryseed", new SellingShopItem(20, 4)).setStaticPriceBasedOnHappiness(100, 200, 25).addKilledMobRequirement("cryoqueen");
        this.shop.addSellingItem("coffeebeans", new SellingShopItem(20, 4)).setStaticPriceBasedOnHappiness(140, 280, 30).addKilledMobRequirement("sageandgrit");
        this.shop.addSellingItem("lemonsapling", new SellingShopItem(3, 1)).setStaticPriceBasedOnHappiness(1000, 1800, 150).addKilledMobRequirement("sageandgrit");
        this.shop.addSellingItem("bananasapling", new SellingShopItem(3, 1)).setStaticPriceBasedOnHappiness(1000, 1800, 150).addKilledMobRequirement("sageandgrit");
        this.shop.addBuyingItem("wheat", new BuyingShopItem()).setPriceBasedOnHappiness(10, 2, 2);
        this.shop.addBuyingItem("sunflower", new BuyingShopItem()).setPriceBasedOnHappiness(12, 3, 3);
    }

    @Override
    public LootTable getLootTable() {
        return super.getLootTable();
    }

    @Override
    public void setDefaultArmor(HumanDrawOptions drawOptions) {
        drawOptions.helmet(new InventoryItem("farmerhat"));
        drawOptions.chestplate(new InventoryItem("farmershirt"));
        drawOptions.boots(new InventoryItem("farmershoes"));
        drawOptions.holdItem(new InventoryItem("farmerpitchfork"));
    }

    @Override
    protected ArrayList<GameMessage> getMessages(ServerClient client) {
        return this.getLocalMessages("farmertalk", 4);
    }

    @Override
    public List<InventoryItem> getRecruitItems(ServerClient client) {
        if (this.isTrapped()) {
            return Collections.emptyList();
        }
        GameRandom random = new GameRandom((long)this.getSettlerSeed() * 83L);
        if (this.isVisitor()) {
            return Collections.singletonList(new InventoryItem("coin", random.getIntBetween(250, 400)));
        }
        CountOfTicketLootItems items = new CountOfTicketLootItems(random.getIntBetween(2, 3), 100, new LootItem("wheat", Integer.MAX_VALUE), 100, new LootItem("sunflower", Integer.MAX_VALUE), 100, new LootItem("fertilizer", Integer.MAX_VALUE), 500, new LootItem("coin", Integer.MAX_VALUE));
        int value = random.getIntBetween(300, 500);
        ArrayList<InventoryItem> out = GameLootUtils.getItemsValuedAt(random, value, 0.2f, items, new Object[0]);
        out.sort(Comparator.comparing(InventoryItem::getBrokerValue).reversed());
        return out;
    }
}

