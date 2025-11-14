/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.incursionModifiers;

import java.awt.Point;
import java.util.ArrayList;
import necesse.engine.incursionPerkTree.AscendedShardsCanDropPerk;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.manager.MobLootTableDropsListenerEntityComponent;
import necesse.entity.mobs.Mob;
import necesse.inventory.InventoryItem;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.incursion.IncursionData;

public class AscendedShardsBossDropLevelEvent
extends LevelEvent
implements MobLootTableDropsListenerEntityComponent {
    public AscendedShardsBossDropLevelEvent() {
        super(true);
        this.shouldSave = true;
    }

    @Override
    public boolean isNetworkImportant() {
        return true;
    }

    @Override
    public void onLevelMobDropsLoot(Mob mob, Point dropPosition, ArrayList<InventoryItem> drops) {
        IncursionData incursionData;
        Level level;
        if (mob.isBoss() && (level = mob.getLevel()) instanceof IncursionLevel && (incursionData = ((IncursionLevel)level).incursionData) != null) {
            int minTier = AscendedShardsCanDropPerk.FRAGMENTS_DROP_AT_TIER;
            int delta = incursionData.getTabletTier() - minTier;
            if (delta >= 0) {
                float chance = AscendedShardsCanDropPerk.DROP_CHANCE + (float)delta * AscendedShardsCanDropPerk.ADDITIONAL_DROP_CHANCE_PER_TIER;
                int totalDrops = 0;
                while (GameRandom.globalRandom.getChance(chance)) {
                    chance -= 1.0f;
                    ++totalDrops;
                }
                for (int i = 0; i < totalDrops; ++i) {
                    drops.add(new InventoryItem("ascendedshard"));
                }
            }
        }
    }
}

