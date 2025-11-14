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
import necesse.entity.mobs.friendly.human.humanShop.BuyingShopItem;
import necesse.entity.mobs.friendly.human.humanShop.HumanShop;
import necesse.entity.mobs.friendly.human.humanShop.SellingShopItem;
import necesse.gfx.HumanGender;
import necesse.gfx.HumanLook;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.CountOfTicketLootItems;
import necesse.inventory.lootTable.lootItem.LootItem;

public class PirateHumanMob
extends HumanShop {
    public PirateHumanMob() {
        super(500, 200, "pirate");
        this.attackCooldown = 500;
        this.attackAnimTime = 500;
        this.setSwimSpeed(1.0f);
        this.equipmentInventory.setItem(6, new InventoryItem("cutlass"));
        this.shop.addSellingItem("goldbar", new SellingShopItem(50, 5)).setStaticPriceBasedOnHappiness(20, 40, 8);
        this.shop.addBuyingItem("goldbar", new BuyingShopItem()).setPriceBasedOnHappiness(18, 10, 2);
        this.shop.addSellingItem("handcannon", new SellingShopItem()).setStaticPriceBasedOnHappiness(600, 1000, 100);
        this.shop.addSellingItem("cutlass", new SellingShopItem()).setStaticPriceBasedOnHappiness(600, 1000, 100);
        this.shop.addSellingItem("flintlock", new SellingShopItem()).setStaticPriceBasedOnHappiness(600, 1000, 100);
        this.shop.addSellingItem("genielamp", new SellingShopItem()).setStaticPriceBasedOnHappiness(700, 1100, 100);
        this.shop.addSellingItem("lifeline", new SellingShopItem()).setStaticPriceBasedOnHappiness(500, 900, 100);
        this.shop.addSellingItem("piratetelescope", new SellingShopItem()).setStaticPriceBasedOnHappiness(600, 1000, 100);
        this.shop.addSellingItem("spareboatparts", new SellingShopItem()).setStaticPriceBasedOnHappiness(400, 800, 100);
        this.shop.addSellingItem("goldfishingrod", new SellingShopItem()).setStaticPriceBasedOnHappiness(350, 650, 100);
        this.shop.addSellingItem("mapfragment", new SellingShopItem()).setStaticPriceBasedOnHappiness(100, 200, 25);
        this.shop.addSellingItem("goldchair", new SellingShopItem()).setStaticPriceBasedOnHappiness(40, 80, 10);
        this.shop.addSellingItem("golddinnertable", new SellingShopItem()).setStaticPriceBasedOnHappiness(80, 120, 10);
        this.shop.addSellingItem("siegevinyl", new SellingShopItem()).setStaticPriceBasedOnHappiness(100, 150, 10);
        this.shop.addSellingItem("ironbomb", new SellingShopItem(100, 10)).setStaticPriceBasedOnHappiness(40, 80, 10);
        this.shop.addSellingItem("dynamitestick", new SellingShopItem(50, 5)).setStaticPriceBasedOnHappiness(100, 160, 10);
    }

    @Override
    public void randomizeLook(HumanLook look, HumanGender gender, GameRandom random) {
        this.gender = HumanGender.MALE;
        super.randomizeLook(look, this.gender, random);
        look.setFacialFeature(random.getOneOf(1, 3, 4));
        this.settlerName = this.getRandomName(new GameRandom(this.settlerSeed));
    }

    @Override
    public LootTable getLootTable() {
        return super.getLootTable();
    }

    @Override
    public void setDefaultArmor(HumanDrawOptions drawOptions) {
        drawOptions.helmet(new InventoryItem("captainshat"));
        drawOptions.chestplate(new InventoryItem("captainsshirt"));
        drawOptions.boots(new InventoryItem("captainsboots"));
    }

    @Override
    public List<InventoryItem> getRecruitItems(ServerClient client) {
        if (this.isTrapped()) {
            return Collections.emptyList();
        }
        GameRandom random = new GameRandom((long)this.getSettlerSeed() * 317L);
        if (this.isVisitor()) {
            return Collections.singletonList(new InventoryItem("coin", random.getIntBetween(800, 1000)));
        }
        LootTable items = new LootTable(new CountOfTicketLootItems(1, 100, new LootItem("goldbar", Integer.MAX_VALUE), 100, new LootItem("mapfragment", Integer.MAX_VALUE)));
        ArrayList<InventoryItem> out = GameLootUtils.getItemsValuedAt(random, random.getIntBetween(800, 1000), 0.2f, new LootItem("coin", Integer.MAX_VALUE), new Object[0]);
        out.addAll(GameLootUtils.getItemsValuedAt(random, random.getIntBetween(100, 200), 0.2f, items, new Object[0]));
        out.sort(Comparator.comparing(InventoryItem::getBrokerValue).reversed());
        return out;
    }

    @Override
    protected GameMessage getTrappedMessage(ServerClient client) {
        return new LocalMessage("ui", "pirategiveup");
    }

    @Override
    protected ArrayList<GameMessage> getMessages(ServerClient client) {
        ArrayList<GameMessage> out = this.getLocalMessages("piratetalk", 6);
        if (client.characterStats().mob_kills.getKills("piratecaptain") > 0) {
            out.addAll(this.getLocalMessages("piratespecial", 2));
        }
        return out;
    }
}

