/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.IncursionPerkModifiers;

import java.awt.Point;
import java.util.ArrayList;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.manager.MobLootTableDropsListenerEntityComponent;
import necesse.entity.mobs.Mob;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.level.maps.levelBuffManager.LevelModifiers;

public class MobsDropUpgradeShardsPerkLevelEvent
extends LevelEvent
implements MobLootTableDropsListenerEntityComponent {
    LootTable altarDustLootTable = new LootTable(ChanceLootItem.between(0.1f, "upgradeshard", 2, 4));

    public MobsDropUpgradeShardsPerkLevelEvent() {
        super(true);
        this.shouldSave = true;
    }

    @Override
    public boolean isNetworkImportant() {
        return true;
    }

    @Override
    public void onLevelMobDropsLoot(Mob mob, Point dropPosition, ArrayList<InventoryItem> drops) {
        if (mob.isHostile && !mob.isSummoned) {
            this.altarDustLootTable.addItems(drops, GameRandom.globalRandom, this.getLevel().buffManager.getModifier(LevelModifiers.LOOT).floatValue(), mob);
        }
    }
}

