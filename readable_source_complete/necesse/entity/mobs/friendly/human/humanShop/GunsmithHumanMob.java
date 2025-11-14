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
import necesse.entity.mobs.friendly.human.humanShop.BlacksmithHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.HumanShop;
import necesse.entity.mobs.friendly.human.humanShop.SellingShopItem;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.CountOfTicketLootItems;
import necesse.inventory.lootTable.lootItem.LootItem;

public class GunsmithHumanMob
extends HumanShop {
    public GunsmithHumanMob() {
        super(500, 200, "gunsmith");
        this.attackCooldown = 600;
        this.attackAnimTime = 500;
        this.setSwimSpeed(1.0f);
        this.equipmentInventory.setItem(6, new InventoryItem("handgun"));
        BlacksmithHumanMob.addBarBuyingToShop(this.shop);
        this.shop.addSellingItem("simplebullet", new SellingShopItem()).setItem(new InventoryItem("simplebullet", 150)).setStaticPriceBasedOnHappiness(25, 55, 10);
        this.shop.addSellingItem("handgun", new SellingShopItem()).setStaticPriceBasedOnHappiness(200, 400, 50);
        this.shop.addSellingItem("machinegun", new SellingShopItem()).setStaticPriceBasedOnHappiness(500, 800, 100).addKilledMobRequirement("evilsprotector");
        this.shop.addSellingItem("shotgun", new SellingShopItem()).setStaticPriceBasedOnHappiness(600, 1000, 100).addKilledMobRequirement("voidwizard");
        this.shop.addSellingItem("sixshooter", new SellingShopItem()).setStaticPriceBasedOnHappiness(650, 1050, 100).addKilledMobRequirement("swampguardian");
        this.shop.addSellingItem("sniperrifle", new SellingShopItem()).setStaticPriceBasedOnHappiness(700, 1100, 100).addKilledMobRequirement("ancientvulture");
        this.shop.addSellingItem("deathripper", new SellingShopItem()).setStaticPriceBasedOnHappiness(1400, 2200, 200).addKilledMobRequirement("reaper");
        this.shop.addSellingItem("ammobox", new SellingShopItem()).setStaticPriceBasedOnHappiness(800, 1200, 100).addKilledMobRequirement("reaper");
        this.shop.addSellingItem("engineergoggles", new SellingShopItem()).setStaticPriceBasedOnHappiness(75, 150, 20);
        this.shop.addSellingItem("labapron", new SellingShopItem()).setStaticPriceBasedOnHappiness(75, 150, 20);
        this.shop.addSellingItem("labboots", new SellingShopItem()).setStaticPriceBasedOnHappiness(75, 150, 20);
    }

    @Override
    public LootTable getLootTable() {
        return super.getLootTable();
    }

    @Override
    public void setDefaultArmor(HumanDrawOptions drawOptions) {
        drawOptions.helmet(new InventoryItem("engineergoggles"));
        drawOptions.chestplate(new InventoryItem("labapron"));
        drawOptions.boots(new InventoryItem("labboots"));
    }

    @Override
    protected ArrayList<GameMessage> getMessages(ServerClient client) {
        HumanMob hh;
        ArrayList<GameMessage> out = this.getLocalMessages("gunsmithtalk", 4);
        HumanMob hb = this.getRandomHuman("blacksmith");
        if (hb != null) {
            out.add(new LocalMessage("mobmsg", "gunsmithspecial1", "blacksmith", hb.getSettlerName()));
        }
        if ((hh = this.getRandomHuman("hunter")) != null) {
            out.add(new LocalMessage("mobmsg", "gunsmithspecial2", "hunter", hh.getSettlerName()));
        }
        return out;
    }

    @Override
    public List<InventoryItem> getRecruitItems(ServerClient client) {
        if (this.isTrapped()) {
            return Collections.emptyList();
        }
        GameRandom random = new GameRandom((long)this.getSettlerSeed() * 709L);
        if (this.isVisitor()) {
            return Collections.singletonList(new InventoryItem("coin", random.getIntBetween(350, 500)));
        }
        LootTable secondItems = new LootTable(new CountOfTicketLootItems(random.getIntBetween(1, 2), 100, new LootItem("copperbar", Integer.MAX_VALUE), 100, new LootItem("ironbar", Integer.MAX_VALUE), 100, new LootItem("goldbar", Integer.MAX_VALUE)));
        ArrayList<InventoryItem> out = GameLootUtils.getItemsValuedAt(random, random.getIntBetween(350, 500), 0.2f, new LootItem("coin", Integer.MAX_VALUE), new Object[0]);
        out.addAll(GameLootUtils.getItemsValuedAt(random, random.getIntBetween(75, 150), 0.2f, secondItems, new Object[0]));
        out.sort(Comparator.comparing(InventoryItem::getBrokerValue).reversed());
        return out;
    }
}

