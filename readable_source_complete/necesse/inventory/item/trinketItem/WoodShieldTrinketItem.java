/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.trinketItem;

import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.trinketItem.ShieldTrinketItem;
import necesse.inventory.lootTable.presets.TrinketsLootTable;

public class WoodShieldTrinketItem
extends ShieldTrinketItem {
    public WoodShieldTrinketItem(Item.Rarity rarity, int armorValue, float minSlowModifier, int msToDepleteStamina, float staminaUsageOnBlock, int damageTakenPercent, float angleCoverage, int enchantCost) {
        super(rarity, armorValue, minSlowModifier, msToDepleteStamina, staminaUsageOnBlock, damageTakenPercent, angleCoverage, enchantCost, TrinketsLootTable.trinkets);
    }

    @Override
    public void playHitSound(InventoryItem item, Mob mob, MobWasHitEvent hitEvent) {
        SoundManager.playSound(GameResources.tap2, (SoundEffect)SoundEffect.effect(mob).volume(0.8f));
    }
}

