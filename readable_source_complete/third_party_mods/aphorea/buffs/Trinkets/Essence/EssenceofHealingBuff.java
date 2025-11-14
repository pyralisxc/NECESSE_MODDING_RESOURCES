/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.modifiers.ModifierContainer
 *  necesse.engine.modifiers.ModifierValue
 *  necesse.engine.registries.BuffRegistry
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.buffs.BuffEventSubscriber
 *  necesse.entity.mobs.buffs.BuffModifiers
 *  necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.ItemStatTip
 *  necesse.inventory.item.trinketItem.TrinketItem
 */
package aphorea.buffs.Trinkets.Essence;

import aphorea.registry.AphModifiers;
import java.util.LinkedList;
import necesse.engine.localization.Localization;
import necesse.engine.modifiers.ModifierContainer;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.trinketItem.TrinketItem;

public class EssenceofHealingBuff
extends TrinketBuff {
    int currentEssences = 0;

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        new ModifierValue(BuffModifiers.LIFE_ESSENCE_DURATION, (Object)Float.valueOf(-0.8f)).max((Object)Float.valueOf(-0.5f)).apply((ModifierContainer)buff);
    }

    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"essenceofhealing", (String)"percent", (Object)10));
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"essenceofhealing2", (String)"flat", (Object)1));
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"essenceofhealing3"));
        return tooltips;
    }

    public void addStatTooltips(LinkedList<ItemStatTip> list, ActiveBuff currentValues, ActiveBuff lastValues) {
        super.addStatTooltips(list, currentValues, lastValues);
    }

    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        this.updateBuff(buff);
    }

    public void serverTick(ActiveBuff buff) {
        super.serverTick(buff);
        this.updateBuff(buff);
    }

    public void updateBuff(ActiveBuff buff) {
        int lifeEssences;
        PlayerMob player = (PlayerMob)buff.owner;
        if (player.buffManager.hasBuff(BuffRegistry.LIFE_ESSENCE) && (lifeEssences = player.buffManager.getBuff(BuffRegistry.LIFE_ESSENCE).getStacks() / 15) != this.currentEssences) {
            buff.setModifier(AphModifiers.MAGIC_HEALING, (Object)Float.valueOf((float)lifeEssences * 0.1f));
            if (lifeEssences / 2 != this.currentEssences / 2) {
                buff.setModifier(AphModifiers.MAGIC_HEALING_FLAT, (Object)(lifeEssences / 2));
            }
            this.currentEssences = lifeEssences;
        }
    }
}

