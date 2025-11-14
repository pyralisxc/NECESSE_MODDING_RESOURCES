/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.trinketItem;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.trinketItem.ShieldTrinketItem;
import necesse.inventory.lootTable.presets.TrinketsLootTable;

public class CactusShieldTrinketItem
extends ShieldTrinketItem {
    public CactusShieldTrinketItem(Item.Rarity rarity, int enchantCost) {
        super(rarity, 4, 0.5f, 8000, 0.25f, 40, 360.0f, enchantCost, TrinketsLootTable.trinkets);
    }

    @Override
    public ListGameTooltips getExtraShieldTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getExtraShieldTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "cactusshieldtip"));
        return tooltips;
    }

    @Override
    public void onShieldHit(InventoryItem item, Mob mob, MobWasHitEvent hitEvent) {
        super.onShieldHit(item, mob, hitEvent);
        if (mob.isServer() && !hitEvent.wasPrevented) {
            boolean hasOwnerInChain;
            Mob attackOwner = hitEvent.attacker != null ? hitEvent.attacker.getAttackOwner() : null;
            boolean bl = hasOwnerInChain = hitEvent.attacker != null && hitEvent.attacker.isInAttackOwnerChain(mob);
            if (attackOwner != null && !hasOwnerInChain) {
                float dx = attackOwner.x - mob.x;
                float dy = attackOwner.y - mob.y;
                float finalDamageMultiplier = this.getShieldFinalDamageMultiplier(item, mob);
                if (finalDamageMultiplier > 0.0f) {
                    float damage = (float)hitEvent.damage / finalDamageMultiplier;
                    if (attackOwner.isPlayer) {
                        damage /= 2.0f;
                    }
                    attackOwner.isServerHit(new GameDamage(damage, 0.0f), dx, dy, 0.0f, mob);
                }
            }
        }
    }
}

