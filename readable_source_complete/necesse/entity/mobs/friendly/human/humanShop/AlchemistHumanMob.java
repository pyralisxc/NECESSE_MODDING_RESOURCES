/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly.human.humanShop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.util.GameLootUtils;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.humanShop.BuyingShopItem;
import necesse.entity.mobs.friendly.human.humanShop.HumanShop;
import necesse.entity.mobs.friendly.human.humanShop.SellingShopItem;
import necesse.gfx.HumanLook;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.CountOfTicketLootItems;
import necesse.inventory.lootTable.lootItem.LootItem;

public class AlchemistHumanMob
extends HumanShop {
    public AlchemistHumanMob() {
        super(500, 200, "alchemist");
        this.look = new HumanLook();
        this.attackCooldown = 500;
        this.attackAnimTime = 500;
        this.setSwimSpeed(1.0f);
        this.equipmentInventory.setItem(6, new InventoryItem("coppersword"));
        this.shop.addSellingItem("healthpotion", new SellingShopItem(50, 10)).setStaticPriceBasedOnHappiness(5, 25, 5);
        this.shop.addSellingItem("manapotion", new SellingShopItem(50, 10)).setStaticPriceBasedOnHappiness(5, 25, 5);
        this.shop.addSellingItem("speedpotion", new SellingShopItem(25, 5)).setStaticPriceBasedOnHappiness(20, 60, 10);
        this.shop.addSellingItem("healthregenpotion", new SellingShopItem(25, 5)).setStaticPriceBasedOnHappiness(20, 60, 10);
        this.shop.addSellingItem("attackspeedpotion", new SellingShopItem(25, 5)).setStaticPriceBasedOnHappiness(20, 60, 10);
        this.shop.addSellingItem("manaregenpotion", new SellingShopItem(25, 5)).setStaticPriceBasedOnHappiness(20, 60, 10);
        this.shop.addSellingItem("alchemistglasses", new SellingShopItem()).setStaticPriceBasedOnHappiness(75, 150, 20);
        this.shop.addSellingItem("labcoat", new SellingShopItem()).setStaticPriceBasedOnHappiness(75, 150, 20);
        this.shop.addSellingItem("labboots", new SellingShopItem()).setStaticPriceBasedOnHappiness(75, 150, 20);
        this.shop.addBuyingItem("firemone", new BuyingShopItem()).setPriceBasedOnHappiness(12, 3, 3);
        this.shop.addBuyingItem("sunflower", new BuyingShopItem()).setPriceBasedOnHappiness(12, 3, 3);
        this.shop.addBuyingItem("iceblossom", new BuyingShopItem()).setPriceBasedOnHappiness(12, 3, 3);
        this.shop.addBuyingItem("caveglow", new BuyingShopItem()).setPriceBasedOnHappiness(20, 5, 3);
    }

    @Override
    public LootTable getLootTable() {
        return super.getLootTable();
    }

    @Override
    public void setDefaultArmor(HumanDrawOptions drawOptions) {
        drawOptions.helmet(new InventoryItem("alchemistglasses"));
        drawOptions.chestplate(new InventoryItem("labcoat"));
        drawOptions.boots(new InventoryItem("labboots"));
    }

    @Override
    protected ArrayList<GameMessage> getMessages(ServerClient client) {
        return this.getLocalMessages("alchemisttalk", 5);
    }

    @Override
    public PacketOpenContainer getOpenShopPacket(Server server, ServerClient client) {
        return this.getShopContainerData(client).getPacket(ContainerRegistry.ALCHEMIST_CONTAINER, this);
    }

    @Override
    public List<InventoryItem> getRecruitItems(ServerClient client) {
        if (this.isTrapped()) {
            return Collections.emptyList();
        }
        GameRandom random = new GameRandom((long)this.getSettlerSeed() * 89L);
        if (this.isVisitor()) {
            return Collections.singletonList(new InventoryItem("coin", random.getIntBetween(200, 400)));
        }
        LootTable secondItems = new LootTable(new CountOfTicketLootItems(2, 100, new LootItem("sunflower", Integer.MAX_VALUE), 100, new LootItem("firemone", Integer.MAX_VALUE), 100, new LootItem("iceblossom", Integer.MAX_VALUE), 100, new LootItem("mushroom", Integer.MAX_VALUE)));
        ArrayList<InventoryItem> out = GameLootUtils.getItemsValuedAt(random, random.getIntBetween(200, 400), 0.2f, new LootItem("coin", Integer.MAX_VALUE), new Object[0]);
        out.addAll(GameLootUtils.getItemsValuedAt(random, random.getIntBetween(75, 100), 0.2f, secondItems, new Object[0]));
        out.sort(Comparator.comparing(InventoryItem::getBrokerValue).reversed());
        return out;
    }
}

