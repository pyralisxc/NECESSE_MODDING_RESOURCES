/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.questItem;

import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.entity.pickup.QuestItemPickupEntity;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ObtainTip;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.FishingLootTable;
import necesse.level.maps.biomes.FishingSpot;
import necesse.level.maps.biomes.MobSpawnTable;

public class QuestItem
extends Item
implements ObtainTip {
    public GameMessage obtainTip;

    public QuestItem(int stackSize, GameMessage obtainTip) {
        super(stackSize);
        this.rarity = Item.Rarity.QUEST;
        this.keyWords.add("quest");
        this.dropsAsMatDeathPenalty = true;
        this.setItemCategory("misc", "questitems");
        this.obtainTip = obtainTip;
        this.worldDrawSize = 32;
        this.incinerationTimeMillis = 10000;
    }

    public QuestItem(GameMessage obtainTip) {
        this(1, obtainTip);
    }

    public QuestItem() {
        this(null);
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips out = super.getTooltips(item, perspective, blackboard);
        out.add(Localization.translate("itemtooltip", "questitem"));
        return out;
    }

    @Override
    public GameMessage getObtainTip() {
        return this.obtainTip;
    }

    @Override
    public ItemPickupEntity getPickupEntity(Level level, InventoryItem item, float x, float y, float dx, float dy) {
        return new QuestItemPickupEntity(level, item, x, y, dx, dy);
    }

    public MobSpawnTable getExtraCritterSpawnTable(ServerClient client, Level level) {
        return null;
    }

    public MobSpawnTable getExtraMobSpawnTable(ServerClient client, Level level) {
        return null;
    }

    public FishingLootTable getExtraFishingLoot(ServerClient client, FishingSpot spot) {
        return null;
    }

    public LootTable getExtraMobDrops(ServerClient client, Mob mob) {
        return null;
    }
}

