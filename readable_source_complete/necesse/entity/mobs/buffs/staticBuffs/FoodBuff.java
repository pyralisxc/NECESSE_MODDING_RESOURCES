/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import java.util.LinkedList;
import necesse.engine.localization.Localization;
import necesse.engine.modifiers.ModifierTooltip;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemGameItem;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.consumableItem.food.FoodConsumableItem;

public class FoodBuff
extends Buff {
    public FoodBuff(boolean canCancel) {
        this.canCancel = canCancel;
    }

    @Override
    public String getLocalizationKey() {
        return this.canCancel ? "foodbuff" : "fooddebuff";
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        FoodConsumableItem foodItem = FoodBuff.getFoodItem(buff);
        if (foodItem != null) {
            for (ModifierValue<?> modifier : foodItem.modifiers) {
                modifier.apply(buff);
            }
        }
    }

    public static void setFoodItem(ActiveBuff buff, FoodConsumableItem item) {
        buff.getGndData().setItem("foodItem", (GNDItem)new GNDItemGameItem(item));
    }

    public static FoodConsumableItem getFoodItem(ActiveBuff buff) {
        int itemID = GNDItemGameItem.getItemID(buff.getGndData(), "foodItem");
        Item item = ItemRegistry.getItem(itemID);
        if (item != null && item.isFoodItem()) {
            return (FoodConsumableItem)item;
        }
        return null;
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltip(ab, blackboard);
        LinkedList<ModifierTooltip> modifierTooltips = ab.getModifierTooltips();
        if (modifierTooltips.isEmpty()) {
            tooltips.add(Localization.translate("bufftooltip", "nomodifiers"));
        } else {
            ab.getModifierTooltips().stream().map(mft -> mft.toTooltip(true)).forEach(tooltips::add);
        }
        return tooltips;
    }

    @Override
    public void drawIcon(int x, int y, ActiveBuff buff) {
        FoodConsumableItem foodItem = FoodBuff.getFoodItem(buff);
        if (foodItem != null) {
            this.iconTexture = foodItem.buffTexture;
        }
        super.drawIcon(x, y, buff);
    }
}

