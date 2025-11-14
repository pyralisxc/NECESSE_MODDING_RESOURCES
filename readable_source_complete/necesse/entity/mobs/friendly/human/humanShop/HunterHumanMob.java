/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly.human.humanShop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameLootUtils;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.HumanAI;
import necesse.entity.mobs.ai.behaviourTree.util.AIMover;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.friendly.human.humanShop.BuyingShopItem;
import necesse.entity.mobs.friendly.human.humanShop.HumanShop;
import necesse.entity.mobs.friendly.human.humanShop.SellingShopItem;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.CountOfTicketLootItems;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class HunterHumanMob
extends HumanShop {
    protected boolean isLost;

    public HunterHumanMob() {
        super(500, 200, "hunter");
        this.attackCooldown = 600;
        this.attackAnimTime = 500;
        this.setSwimSpeed(1.0f);
        this.jobTypeHandler.getPriority((String)"hunting").disabledBySettler = false;
        this.equipmentInventory.setItem(6, new InventoryItem("copperbow"));
        this.shop.addSellingItem("ironbow", new SellingShopItem()).setStaticPriceBasedOnHappiness(60, 120, 20);
        this.shop.addSellingItem("stonearrow", new SellingShopItem()).setItem(new InventoryItem("stonearrow", 5)).setStaticPriceBasedOnHappiness(5, 10, 2);
        this.shop.addSellingItem("firearrow", new SellingShopItem()).setItem(new InventoryItem("firearrow", 5)).setStaticPriceBasedOnHappiness(8, 14, 3);
        this.shop.addSellingItem("ironarrow", new SellingShopItem()).setItem(new InventoryItem("ironarrow", 5)).setStaticPriceBasedOnHappiness(12, 18, 3);
        this.shop.addSellingItem("ninjastar", new SellingShopItem()).setItem(new InventoryItem("ninjastar", 5)).setStaticPriceBasedOnHappiness(16, 22, 3);
        this.shop.addSellingItem("icejavelin", new SellingShopItem()).setItem(new InventoryItem("icejavelin", 5)).setStaticPriceBasedOnHappiness(25, 45, 10).addKilledMobRequirement("evilsprotector");
        this.shop.addSellingItem("hunterhood", new SellingShopItem()).setStaticPriceBasedOnHappiness(75, 150, 20);
        this.shop.addSellingItem("huntershirt", new SellingShopItem()).setStaticPriceBasedOnHappiness(75, 150, 20);
        this.shop.addSellingItem("hunterboots", new SellingShopItem()).setStaticPriceBasedOnHappiness(75, 150, 20);
        this.shop.addSellingItem("magicalquiver", new SellingShopItem()).setStaticPriceBasedOnHappiness(600, 1000, 100).addKilledMobRequirement("ancientvulture");
        this.shop.addBuyingItem("wool", new BuyingShopItem()).setPriceBasedOnHappiness(13, 7, 2);
        this.shop.addBuyingItem("leather", new BuyingShopItem()).setPriceBasedOnHappiness(16, 8, 2);
    }

    @Override
    public void setupMovementPacket(PacketWriter writer) {
        super.setupMovementPacket(writer);
        writer.putNextBoolean(this.isLost);
    }

    @Override
    public void applyMovementPacket(PacketReader reader, boolean isDirect) {
        super.applyMovementPacket(reader, isDirect);
        this.isLost = reader.getNextBoolean();
    }

    @Override
    public void init() {
        super.init();
        this.updateAI();
    }

    public void updateAI() {
        this.ai = new BehaviourTreeAI<HunterHumanMob>(this, new HumanAI(320, true, false, this.isLost ? 5000 : 25000), new AIMover(HumanMob.humanPathIterations));
    }

    @Override
    public LootTable getLootTable() {
        return super.getLootTable();
    }

    @Override
    public boolean shouldSave() {
        if (this.isLost) {
            return false;
        }
        return super.shouldSave();
    }

    public void setLost(boolean isLost) {
        if (this.isLost == isLost) {
            return;
        }
        this.isLost = isLost;
        this.updateTeam();
        this.updateAI();
    }

    @Override
    public void updateTeam() {
        if (this.getLevel() == null || this.isClient()) {
            return;
        }
        if (this.isLost) {
            this.team.set(-1);
            this.owner.set(-1L);
            return;
        }
        super.updateTeam();
    }

    @Override
    public void makeSettler(ServerSettlementData data, LevelSettler settler) {
        if (this.isLost) {
            this.setLost(false);
            this.updateAI();
        }
        super.makeSettler(data, settler);
    }

    @Override
    public Predicate<Mob> filterHumanTargets() {
        return super.filterHumanTargets();
    }

    @Override
    public void setDefaultArmor(HumanDrawOptions drawOptions) {
        drawOptions.helmet(new InventoryItem("hunterhood"));
        drawOptions.chestplate(new InventoryItem("huntershirt"));
        drawOptions.boots(new InventoryItem("hunterboots"));
    }

    @Override
    protected ArrayList<GameMessage> getMessages(ServerClient client) {
        ArrayList<GameMessage> out = this.getLocalMessages("huntertalk", 5);
        HumanMob m = this.getRandomHuman("gunsmith");
        if (m != null) {
            out.add(new LocalMessage("mobmsg", "huntertalkspecial", "gunsmith", m.getSettlerName()));
        }
        return out;
    }

    @Override
    public int getRecruitedToSettlementUniqueID(ServerClient client) {
        if (this.isLost) {
            return 0;
        }
        return super.getRecruitedToSettlementUniqueID(client);
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
        if (this.isLost) {
            return null;
        }
        if (client.characterStats().mob_kills.getKills("ancientvulture") > 0) {
            return null;
        }
        return new LocalMessage("ui", "settlernorecruit");
    }

    @Override
    public List<InventoryItem> getRecruitItems(ServerClient client) {
        if (this.isTrapped()) {
            return Collections.emptyList();
        }
        GameRandom random = new GameRandom((long)this.getSettlerSeed() * 193L);
        if (this.isVisitor()) {
            return Collections.singletonList(new InventoryItem("coin", random.getIntBetween(250, 450)));
        }
        LootTable items = new LootTable(new LootItem("coin", Integer.MAX_VALUE), new CountOfTicketLootItems(1, 100, new LootItem("wool", Integer.MAX_VALUE), 100, new LootItem("leather", Integer.MAX_VALUE)));
        int value = random.getIntBetween(300, 500);
        ArrayList<InventoryItem> out = GameLootUtils.getItemsValuedAt(random, value, 0.2f, items, new Object[0]);
        out.sort(Comparator.comparing(InventoryItem::getBrokerValue).reversed());
        return out;
    }
}

