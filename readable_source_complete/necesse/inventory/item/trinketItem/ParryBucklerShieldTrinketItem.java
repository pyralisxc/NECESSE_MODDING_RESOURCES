/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.trinketItem;

import necesse.engine.localization.Localization;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.trinketItem.ShieldTrinketItem;
import necesse.inventory.lootTable.presets.TrinketsLootTable;

public class ParryBucklerShieldTrinketItem
extends ShieldTrinketItem {
    public ParryBucklerShieldTrinketItem(Item.Rarity rarity, int enchantCost) {
        super(rarity, 3, 0.25f, 5000, 0.35f, 65, 360.0f, enchantCost, TrinketsLootTable.trinkets);
        this.isPerfectBlocker = true;
    }

    @Override
    public ListGameTooltips getExtraShieldTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getExtraShieldTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "parrybucklertip"), 400);
        return tooltips;
    }

    @Override
    public float getShieldFinalDamageMultiplier(InventoryItem item, Mob mob) {
        if (this.canPerfectBlock(mob)) {
            return 0.0f;
        }
        return super.getShieldFinalDamageMultiplier(item, mob);
    }

    @Override
    public float getShieldStaminaUsageOnBlock(InventoryItem item, Mob mob) {
        if (this.canPerfectBlock(mob)) {
            return 0.1f;
        }
        return super.getShieldStaminaUsageOnBlock(item, mob);
    }

    @Override
    public int getKnockback(Mob mob) {
        if (this.canPerfectBlock(mob)) {
            return 200;
        }
        return super.getKnockback(mob);
    }

    @Override
    public void playHitSound(InventoryItem item, Mob mob, MobWasHitEvent hitEvent) {
        SoundManager.playSound(GameResources.cling, (SoundEffect)SoundEffect.effect(mob).pitch(1.5f).volume(0.8f));
    }

    @Override
    public void onPerfectBlock(Mob mob) {
        super.onPerfectBlock(mob);
        mob.buffManager.addBuff(new ActiveBuff(BuffRegistry.PARRY_BUCKLER_PARRIED, mob, 5000, null), false);
    }
}

