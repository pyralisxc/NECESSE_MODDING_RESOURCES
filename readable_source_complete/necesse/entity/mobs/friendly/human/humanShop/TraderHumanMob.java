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
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.util.GameLootUtils;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.humanShop.HumanShop;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.mob.TraderHumanContainer;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;

public class TraderHumanMob
extends HumanShop {
    public TraderHumanMob() {
        super(500, 200, "trader");
        this.attackCooldown = 500;
        this.attackAnimTime = 500;
        this.setSwimSpeed(1.0f);
        this.jobTypeHandler.getPriority((String)"tradingmission").disabledBySettler = false;
        this.equipmentInventory.setItem(6, new InventoryItem("ironsword"));
    }

    @Override
    public LootTable getLootTable() {
        return super.getLootTable();
    }

    @Override
    public void setDefaultArmor(HumanDrawOptions drawOptions) {
        drawOptions.helmet(null);
        drawOptions.chestplate(new InventoryItem("merchantshirt"));
        drawOptions.boots(new InventoryItem("merchantboots"));
    }

    @Override
    protected ArrayList<GameMessage> getMessages(ServerClient client) {
        return this.getLocalMessages("tradertalk", 7);
    }

    @Override
    public PacketOpenContainer getOpenShopPacket(Server server, ServerClient client) {
        return TraderHumanContainer.getMerchantContainerContent(this, client).getPacket(ContainerRegistry.TRAVELING_MERCHANT_CONTAINER, this);
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
        if (client.characterStats().items_obtained.isItemObtained("shippingchest")) {
            return null;
        }
        return new LocalMessage("ui", "tradershippingmissing");
    }

    @Override
    public List<InventoryItem> getRecruitItems(ServerClient client) {
        if (this.isTrapped()) {
            return Collections.emptyList();
        }
        GameRandom random = new GameRandom((long)this.getSettlerSeed() * 53L);
        LootTable items = new LootTable(new LootItem("coin", Integer.MAX_VALUE));
        int value = random.getIntBetween(800, 1200);
        ArrayList<InventoryItem> out = GameLootUtils.getItemsValuedAt(random, value, 0.2f, items, new Object[0]);
        out.sort(Comparator.comparing(InventoryItem::getBrokerValue).reversed());
        return out;
    }
}

