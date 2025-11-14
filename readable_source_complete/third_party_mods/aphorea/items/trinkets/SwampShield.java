/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.util.GameBlackboard
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item$Rarity
 */
package aphorea.items.trinkets;

import aphorea.items.vanillaitemtypes.AphShieldTrinketItem;
import aphorea.utils.magichealing.AphMagicHealing;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class SwampShield
extends AphShieldTrinketItem {
    public SwampShield() {
        super(Item.Rarity.COMMON, 2, 0.5f, 6000, 0.2f, 50, 240.0f, 400, true);
        this.isPerfectBlocker = true;
    }

    public ListGameTooltips getExtraShieldTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getExtraShieldTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"swampshield"));
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"swampshield2", (String)"healing", (String)AphMagicHealing.getMagicHealingToolTipPercent((Mob)perspective, (Mob)perspective, 0.05f)));
        return tooltips;
    }

    public void onPerfectBlock(Mob mob) {
        super.onPerfectBlock(mob);
        if (mob.isServer()) {
            float healing = (float)mob.getMaxHealth() * 0.05f * AphMagicHealing.getMagicHealingMod(mob, mob, null, null);
            AphMagicHealing.healMobExecute(mob, mob, (int)healing);
        }
    }
}

