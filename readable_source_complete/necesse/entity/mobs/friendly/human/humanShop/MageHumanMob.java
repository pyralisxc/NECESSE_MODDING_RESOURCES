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
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameLootUtils;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.humanShop.BuyingShopItem;
import necesse.entity.mobs.friendly.human.humanShop.HumanShop;
import necesse.entity.mobs.friendly.human.humanShop.SellingShopItem;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.mob.MageContainer;
import necesse.inventory.item.placeableItem.objectItem.WaystoneObjectItem;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.CountOfTicketLootItems;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class MageHumanMob
extends HumanShop {
    public int lookSeed;
    public InventoryItem helmet;
    public InventoryItem chest;
    public InventoryItem boots;

    public MageHumanMob() {
        super(500, 200, "mage");
        this.attackCooldown = 600;
        this.attackAnimTime = 500;
        this.setSwimSpeed(1.0f);
        this.getLookSeed();
        this.updateLook();
        this.equipmentInventory.setItem(6, new InventoryItem("voidmissile"));
        this.shop.addSellingItem("manapotion", new SellingShopItem(50, 10)).setStaticPriceBasedOnHappiness(5, 25, 5);
        this.shop.addSellingItem("book", new SellingShopItem()).setStaticPriceBasedOnHappiness(100, 200, 25);
        this.shop.addSellingItem("magicmanual", new SellingShopItem()).setStaticPriceBasedOnHappiness(500, 1000, 100);
        this.shop.addSellingItem("homestone", new SellingShopItem()).setItem((random, client, mob) -> {
            ServerSettlementData settlement = mob.getSettlerSettlementServerData();
            if (settlement != null) {
                return new InventoryItem("homestone");
            }
            return null;
        }).setStaticPriceBasedOnHappiness(1000, 1600, 100);
        this.shop.addSellingItem("waystone", new SellingShopItem()).setItem((random, client, mob) -> {
            ServerSettlementData settlement = mob.getSettlerSettlementServerData();
            if (settlement != null) {
                return WaystoneObjectItem.setupWaystoneItem(new InventoryItem("waystone"), settlement.uniqueID);
            }
            return null;
        }).setStaticPriceBasedOnHappiness(250, 550, 100);
        this.shop.addSellingItem("voidstaff", new SellingShopItem()).setStaticPriceBasedOnHappiness(400, 600, 100).addKilledMobRequirement("voidwizard");
        this.shop.addSellingItem("voidmissile", new SellingShopItem()).setStaticPriceBasedOnHappiness(500, 700, 100).addKilledMobRequirement("voidwizard");
        this.shop.addSellingItem("recallscroll", new SellingShopItem(25, 5)).setStaticPriceBasedOnHappiness(40, 80, 10);
        this.shop.addSellingItem("teleportationscroll", new SellingShopItem(25, 2)).setStaticPriceBasedOnHappiness(150, 200, 10).addKilledMobRequirement("voidwizard");
        this.shop.addSellingItem("magicstilts", new SellingShopItem()).setStaticPriceBasedOnHappiness(600, 1200, 200).addKilledMobRequirement("voidwizard");
        this.shop.addSellingItem("genielamp", new SellingShopItem()).setStaticPriceBasedOnHappiness(800, 1200, 100).addKilledMobRequirement("piratecaptain");
        this.shop.addBuyingItem("batwing", new BuyingShopItem()).setPriceBasedOnHappiness(22, 10, 4);
        this.shop.addBuyingItem("ectoplasm", new BuyingShopItem()).setPriceBasedOnHappiness(30, 12, 10);
        this.shop.addSellingItem("magehat", new SellingShopItem()).setStaticPriceBasedOnHappiness(75, 150, 20);
        this.shop.addSellingItem("magerobe", new SellingShopItem()).setStaticPriceBasedOnHappiness(75, 150, 20);
        this.shop.addSellingItem("mageshoes", new SellingShopItem()).setStaticPriceBasedOnHappiness(75, 150, 20);
        this.shop.addSellingItem("crimsonhat", new SellingShopItem()).setStaticPriceBasedOnHappiness(75, 150, 20);
        this.shop.addSellingItem("crimsonrobe", new SellingShopItem()).setStaticPriceBasedOnHappiness(75, 150, 20);
        this.shop.addSellingItem("crimsonshoes", new SellingShopItem()).setStaticPriceBasedOnHappiness(75, 150, 20);
    }

    @Override
    public void init() {
        super.init();
        this.updateLook();
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addInt("lookSeed", this.lookSeed);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.lookSeed = save.getInt("lookSeed", this.lookSeed);
        this.getLookSeed();
        this.updateLook();
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.lookSeed = reader.getNextInt();
        this.updateLook();
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.lookSeed);
    }

    public void getLookSeed() {
        if (this.lookSeed == 0) {
            this.lookSeed = GameRandom.globalRandom.nextInt();
        }
    }

    public void updateLook() {
        GameRandom random = new GameRandom(this.lookSeed);
        if (random.getChance(0.5f)) {
            this.helmet = new InventoryItem("crimsonhat");
            this.chest = new InventoryItem("crimsonrobe");
            this.boots = new InventoryItem("crimsonshoes");
        } else {
            this.helmet = new InventoryItem("magehat");
            this.chest = new InventoryItem("magerobe");
            this.boots = new InventoryItem("mageshoes");
        }
    }

    @Override
    public LootTable getLootTable() {
        return super.getLootTable();
    }

    @Override
    public void setDefaultArmor(HumanDrawOptions drawOptions) {
        drawOptions.helmet(this.helmet);
        drawOptions.chestplate(this.chest);
        drawOptions.boots(this.boots);
    }

    @Override
    public GameMessage getRecruitError(ServerClient client) {
        GameMessage superError = super.getRecruitError(client);
        if (superError != null) {
            return superError;
        }
        if (this.isTrapped()) {
            return null;
        }
        if (client.characterStats().mob_kills.getKills("voidwizard") > 0) {
            return null;
        }
        return new LocalMessage("ui", "settlernorecruit");
    }

    @Override
    public List<InventoryItem> getRecruitItems(ServerClient client) {
        if (this.isTrapped()) {
            return Collections.emptyList();
        }
        GameRandom random = new GameRandom((long)this.getSettlerSeed() * 557L);
        if (this.isVisitor()) {
            return Collections.singletonList(new InventoryItem("coin", random.getIntBetween(600, 800)));
        }
        LootTable items = new LootTable(new CountOfTicketLootItems(1, 100, new LootItem("teleportationscroll", Integer.MAX_VALUE), 100, new LootItem("recallscroll", Integer.MAX_VALUE)));
        ArrayList<InventoryItem> out = GameLootUtils.getItemsValuedAt(random, random.getIntBetween(600, 800), 0.2f, new LootItem("coin", Integer.MAX_VALUE), new Object[0]);
        out.addAll(GameLootUtils.getItemsValuedAt(random, random.getIntBetween(100, 200), 0.2f, items, new Object[0]));
        out.sort(Comparator.comparing(InventoryItem::getBrokerValue).reversed());
        return out;
    }

    @Override
    protected ArrayList<GameMessage> getMessages(ServerClient client) {
        return this.getLocalMessages("magetalk", 7);
    }

    @Override
    public PacketOpenContainer getOpenShopPacket(Server server, ServerClient client) {
        return MageContainer.getMageContainerContent(this, client).getPacket(ContainerRegistry.MAGE_CONTAINER, this);
    }
}

