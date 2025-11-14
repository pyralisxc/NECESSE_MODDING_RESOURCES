/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.incursionPerkTree;

import java.util.List;
import necesse.engine.incursionPerkTree.IncursionPerk;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootList;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.incursion.IncursionData;

public class EnchantScrollsRewardPerk
extends IncursionPerk {
    public EnchantScrollsRewardPerk(Integer tier, int perkCost, int xPositionOnPerkTree, IncursionPerk ... prerequisitePerkRequired) {
        super(tier, perkCost, xPositionOnPerkTree, false, prerequisitePerkRequired);
    }

    @Override
    public TicketSystemList<LootItemInterface> onGenerateTabletRewards(TicketSystemList<LootItemInterface> ticketedRewards, GameRandom seededRandom, int tier, IncursionData incursionData) {
        int tabletTierWeight = tier * 10;
        ticketedRewards.addObject(75 + tabletTierWeight, (Object)new LootItemInterface(){

            @Override
            public void addPossibleLoot(LootList list, Object ... extra) {
                list.add("enchantingscroll");
            }

            @Override
            public void addItems(List<InventoryItem> list, GameRandom random, float lootMultiplier, Object ... extra) {
                InventoryItem enchantingScroll = new InventoryItem("enchantingscroll");
                enchantingScroll.getGndData().setInt("incursionUpToAmount", 5);
                if (!LootTable.isExtraEquals(extra, 0, "displayOnly")) {
                    enchantingScroll.getGndData().setInt("generateScrollSeed", -1);
                }
                list.add(enchantingScroll);
            }
        });
        return ticketedRewards;
    }
}

