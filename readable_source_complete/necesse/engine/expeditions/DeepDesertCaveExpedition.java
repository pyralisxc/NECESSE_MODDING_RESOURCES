/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.expeditions;

import java.util.List;
import necesse.engine.expeditions.SettlerExpedition;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootList;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.presets.DeepCaveChestLootTable;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class DeepDesertCaveExpedition
extends SettlerExpedition {
    public static LootTable ores = new LootTable(LootItem.between("ancientfossilore", 16, 26), LootItem.between("tungstenore", 12, 22), LootItem.between("lifequartz", 8, 18));
    public static LootTable extra = new LootTable(LootItem.between("bone", 4, 12));
    public static LootTable rewards = new LootTable(LootTablePresets.deepDesertCaveChest, ores, extra);

    @Override
    public GameMessage getUnavailableMessage() {
        return new LocalMessage("expedition", "completequests");
    }

    @Override
    public float getSuccessChance(ServerSettlementData settlement) {
        return DeepDesertCaveExpedition.questProgressSuccessChance(settlement, "sageandgrit");
    }

    @Override
    public int getBaseCost(ServerSettlementData settlement) {
        return 1300;
    }

    @Override
    public List<InventoryItem> getRewardItems(ServerSettlementData settlement, HumanMob mob) {
        return rewards.getNewList(GameRandom.globalRandom, 1.0f, new Object[0]);
    }

    @Override
    public List<InventoryItem> getItemIcons() {
        LootList list = new LootList();
        DeepCaveChestLootTable.desertMainItems.addPossibleLoot(list, new Object[0]);
        return list.getCombinedItemsAndCustomItems();
    }
}

