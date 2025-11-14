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
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.HumanAI;
import necesse.entity.mobs.ai.behaviourTree.util.AIMover;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.friendly.human.humanShop.HumanShop;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.CountOfTicketLootItems;
import necesse.inventory.lootTable.lootItem.LootItem;

public class GuardHumanMob
extends HumanShop {
    public GuardHumanMob() {
        super(2000, 700, "guard");
        this.attackCooldown = 500;
        this.attackAnimTime = 500;
        this.setSpeed(35.0f);
        this.setSwimSpeed(1.0f);
        this.canJoinAdventureParties = true;
        this.jobTypeHandler.getPriority((String)"crafting").disabledBySettler = true;
        this.jobTypeHandler.getPriority((String)"forestry").disabledBySettler = true;
        this.jobTypeHandler.getPriority((String)"farming").disabledBySettler = true;
        this.equipmentInventory.setItem(1, new InventoryItem("ironchestplate"));
        this.equipmentInventory.setItem(2, new InventoryItem("ironboots"));
        this.equipmentInventory.setItem(6, new InventoryItem("ironsword"));
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<GuardHumanMob>(this, new HumanAI(640, true, true, 25000), new AIMover(HumanMob.humanPathIterations));
    }

    @Override
    public float getRegenFlat() {
        if (this.adventureParty.isInAdventureParty() && !this.isSettlerWithinSettlement()) {
            return super.getRegenFlat();
        }
        return super.getRegenFlat() * 5.0f;
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
        GameRandom random = new GameRandom((long)this.getSettlerSeed() * 37L);
        if (this.isVisitor()) {
            return Collections.singletonList(new InventoryItem("coin", random.getIntBetween(200, 350)));
        }
        LootTable secondItems = new LootTable(new LootItem("healthpotion", Integer.MAX_VALUE), new CountOfTicketLootItems(1, 100, new LootItem("speedpotion", Integer.MAX_VALUE), 100, new LootItem("healthregenpotion", Integer.MAX_VALUE), 100, new LootItem("attackspeedpotion", Integer.MAX_VALUE), 100, new LootItem("battlepotion", Integer.MAX_VALUE), 100, new LootItem("resistancepotion", Integer.MAX_VALUE)));
        ArrayList<InventoryItem> out = GameLootUtils.getItemsValuedAt(random, random.getIntBetween(200, 400), 0.2f, new LootItem("coin", Integer.MAX_VALUE), new Object[0]);
        out.addAll(GameLootUtils.getItemsValuedAt(random, random.getIntBetween(50, 100), 0.2f, secondItems, new Object[0]));
        out.sort(Comparator.comparing(InventoryItem::getBrokerValue).reversed());
        return out;
    }
}

