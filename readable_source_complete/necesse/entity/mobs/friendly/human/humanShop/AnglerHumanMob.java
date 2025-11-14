/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly.human.humanShop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import necesse.engine.expeditions.SettlerExpedition;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ExpeditionMissionRegistry;
import necesse.engine.util.GameLootUtils;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.ExpeditionList;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.friendly.human.humanShop.BuyingShopItem;
import necesse.entity.mobs.friendly.human.humanShop.HumanShop;
import necesse.entity.mobs.friendly.human.humanShop.SellingShopItem;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.CountOfTicketLootItems;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class AnglerHumanMob
extends HumanShop {
    public AnglerHumanMob() {
        super(500, 200, "angler");
        this.attackCooldown = 500;
        this.attackAnimTime = 500;
        this.setSwimSpeed(1.0f);
        this.jobTypeHandler.getPriority((String)"fishing").disabledBySettler = false;
        this.equipmentInventory.setItem(6, new InventoryItem("coppersword"));
        this.shop.addSellingItem("wormbait", new SellingShopItem(100, 10)).setStaticPriceBasedOnHappiness(8, 20, 4);
        this.shop.addSellingItem("anglersbait", new SellingShopItem(50, 5)).setStaticPriceBasedOnHappiness(15, 30, 6);
        this.shop.addSellingItem("rainhat", new SellingShopItem()).setStaticPriceBasedOnHappiness(75, 150, 20);
        this.shop.addSellingItem("raincoat", new SellingShopItem()).setStaticPriceBasedOnHappiness(75, 150, 20);
        this.shop.addSellingItem("rainboots", new SellingShopItem()).setStaticPriceBasedOnHappiness(75, 150, 20);
        this.shop.addBuyingItem("gobfish", new BuyingShopItem()).setPriceBasedOnHappiness(25, 16, 4);
        this.shop.addBuyingItem("halffish", new BuyingShopItem()).setPriceBasedOnHappiness(25, 16, 4);
        this.shop.addBuyingItem("furfish", new BuyingShopItem()).setPriceBasedOnHappiness(25, 16, 4);
        this.shop.addBuyingItem("icefish", new BuyingShopItem()).setPriceBasedOnHappiness(25, 16, 4);
        this.shop.addBuyingItem("swampfish", new BuyingShopItem()).setPriceBasedOnHappiness(25, 16, 4);
        this.shop.addBuyingItem("rockfish", new BuyingShopItem()).setPriceBasedOnHappiness(25, 16, 4);
        this.shop.addBuyingItem("terrorfish", new BuyingShopItem()).setPriceBasedOnHappiness(45, 30, 5);
    }

    @Override
    public LootTable getLootTable() {
        return super.getLootTable();
    }

    @Override
    public void setDefaultArmor(HumanDrawOptions drawOptions) {
        drawOptions.helmet(new InventoryItem("rainhat"));
        drawOptions.chestplate(new InventoryItem("raincoat"));
        drawOptions.boots(new InventoryItem("rainboots"));
    }

    @Override
    protected ArrayList<GameMessage> getMessages(ServerClient client) {
        ArrayList<GameMessage> out = this.getLocalMessages("anglertalk", 5);
        HumanMob m = this.getRandomHuman("stylist");
        if (m != null) {
            out.add(new LocalMessage("mobmsg", "anglerspecial", "stylist", m.getSettlerName()));
        }
        return out;
    }

    @Override
    public boolean canDoExpedition(SettlerExpedition expedition) {
        return ExpeditionMissionRegistry.fishingTripIDs.contains(expedition.getID());
    }

    @Override
    public List<ExpeditionList> getPossibleExpeditions() {
        ServerSettlementData data = this.getSettlerSettlementServerData();
        if (data != null && this.isSettlerWithinSettlement(data.networkData)) {
            ExpeditionList fishingTrips = new ExpeditionList(new LocalMessage("ui", "anglerfishingtrips"), new LocalMessage("ui", "anglerselecttrip"), new LocalMessage("ui", "anglertripcost"), new LocalMessage("ui", "anglershowmore"), data, this, ExpeditionMissionRegistry.fishingTripIDs.stream().map(ExpeditionMissionRegistry::getExpedition).collect(Collectors.toList()));
            return Collections.singletonList(fishingTrips);
        }
        return super.getPossibleExpeditions();
    }

    @Override
    public GameMessage getWorkInvMessage() {
        if (this.completedMission) {
            return new LocalMessage("ui", "anglertripcomplete");
        }
        return super.getWorkInvMessage();
    }

    @Override
    public List<InventoryItem> getRecruitItems(ServerClient client) {
        if (this.isTrapped()) {
            return Collections.emptyList();
        }
        GameRandom random = new GameRandom((long)this.getSettlerSeed() * 227L);
        if (this.isVisitor()) {
            return Collections.singletonList(new InventoryItem("coin", random.getIntBetween(250, 400)));
        }
        LootTable secondItems = new LootTable(new CountOfTicketLootItems(random.getIntBetween(1, 2), 100, new LootItem("gobfish", Integer.MAX_VALUE), 100, new LootItem("terrorfish", Integer.MAX_VALUE), 100, new LootItem("halffish", Integer.MAX_VALUE), 100, new LootItem("rockfish", Integer.MAX_VALUE), 100, new LootItem("furfish", Integer.MAX_VALUE), 100, new LootItem("icefish", Integer.MAX_VALUE), 100, new LootItem("swampfish", Integer.MAX_VALUE)));
        ArrayList<InventoryItem> out = GameLootUtils.getItemsValuedAt(random, random.getIntBetween(250, 400), 0.2f, new LootItem("coin", Integer.MAX_VALUE), new Object[0]);
        out.addAll(GameLootUtils.getItemsValuedAt(random, random.getIntBetween(75, 150), 0.2f, secondItems, new Object[0]));
        out.sort(Comparator.comparing(InventoryItem::getBrokerValue).reversed());
        return out;
    }
}

