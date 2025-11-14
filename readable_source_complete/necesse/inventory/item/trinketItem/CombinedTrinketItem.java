/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.trinketItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.trinketItem.TrinketItem;
import necesse.inventory.lootTable.presets.TrinketsLootTable;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class CombinedTrinketItem
extends TrinketItem {
    public ArrayList<String> trinketStringIDs;

    public CombinedTrinketItem(Item.Rarity rarity, int enchantCost, String ... trinketStringIDs) {
        super(rarity, enchantCost, TrinketsLootTable.trinkets);
        this.trinketStringIDs = new ArrayList<String>(Arrays.asList(trinketStringIDs));
    }

    @Override
    public void onItemRegistryClosed() {
        super.onItemRegistryClosed();
        if (this.getWorldDrawLight() == null) {
            GameLight out = null;
            ArrayList array = this.streamCombinedTrinkets().collect(Collectors.toCollection(ArrayList::new));
            for (int i = 0; i < array.size(); ++i) {
                if (out == null && ((TrinketItem)array.get(i)).getWorldDrawLight() != null) {
                    out = ((TrinketItem)array.get(i)).getWorldDrawLight();
                    continue;
                }
                if (((TrinketItem)array.get(i)).getWorldDrawLight() == null) continue;
                out.combine(((TrinketItem)array.get(i)).getWorldDrawLight());
            }
            if (out != null) {
                this.worldDrawLight = out;
            }
        }
    }

    @Override
    public TrinketBuff[] getBuffs(InventoryItem item) {
        TrinketBuff[] out = new TrinketBuff[]{};
        for (TrinketItem trinketItem : (TrinketItem[])this.streamCombinedTrinkets().toArray(TrinketItem[]::new)) {
            out = GameUtils.concat(out, trinketItem.getBuffs(item));
        }
        return out;
    }

    @Override
    public void refreshLight(Level level, float x, float y, InventoryItem item, boolean isHolding) {
        super.refreshLight(level, x, y, item, isHolding);
        this.streamCombinedTrinkets().forEach(tI -> tI.refreshLight(level, x, y, item, isHolding));
    }

    @Override
    public GameLight getWorldDrawLight() {
        return this.worldDrawLight;
    }

    @Override
    public boolean disabledBy(InventoryItem item) {
        if (super.disabledBy(item)) {
            return true;
        }
        return this.streamCombinedTrinkets().anyMatch(i -> i.disabledBy(item));
    }

    @Override
    public boolean disables(InventoryItem item) {
        if (super.disables(item)) {
            return true;
        }
        if (this.trinketStringIDs.stream().anyMatch(s -> s.equals(item.item.getStringID()))) {
            return true;
        }
        return this.streamCombinedTrinkets().anyMatch(i -> i.disables(item));
    }

    public Stream<TrinketItem> streamCombinedTrinkets() {
        return this.trinketStringIDs.stream().map(s -> (TrinketItem)ItemRegistry.getItem(s));
    }
}

