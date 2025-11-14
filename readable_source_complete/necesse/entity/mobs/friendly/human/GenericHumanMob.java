/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly.human;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameLootUtils;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.humanShop.HumanShop;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.CountOfTicketLootItems;
import necesse.inventory.lootTable.lootItem.LootItem;

public class GenericHumanMob
extends HumanShop {
    public GenericHumanMob() {
        super(500, 200, "generic");
        this.attackCooldown = 500;
        this.attackAnimTime = 500;
        this.setSwimSpeed(1.0f);
        this.equipmentInventory.setItem(6, new InventoryItem("coppersword"));
    }

    @Override
    public LootTable getLootTable() {
        return super.getLootTable();
    }

    @Override
    public List<InventoryItem> getRecruitItems(ServerClient client) {
        if (this.isTrapped()) {
            return Collections.emptyList();
        }
        GameRandom random = new GameRandom((long)this.getSettlerSeed() * 127L);
        if (this.isVisitor()) {
            return Collections.singletonList(new InventoryItem("coin", random.getIntBetween(100, 250)));
        }
        LootTable items = new LootTable(new LootItem("coin", Integer.MAX_VALUE), new CountOfTicketLootItems(1, 100, new LootItem("wheat", Integer.MAX_VALUE), 100, new LootItem("wool", Integer.MAX_VALUE), 100, new LootItem("leather", Integer.MAX_VALUE)));
        int value = random.getIntBetween(100, 250);
        ArrayList<InventoryItem> out = GameLootUtils.getItemsValuedAt(random, value, 0.2f, items, new Object[0]);
        out.sort(Comparator.comparing(InventoryItem::getBrokerValue).reversed());
        return out;
    }
}

