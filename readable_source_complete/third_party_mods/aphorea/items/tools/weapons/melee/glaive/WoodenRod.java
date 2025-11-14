/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.util.GameBlackboard
 *  necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item$Rarity
 *  necesse.inventory.item.toolItem.ToolItem
 *  necesse.level.maps.Level
 */
package aphorea.items.tools.weapons.melee.glaive;

import aphorea.items.vanillaitemtypes.weapons.AphGlaiveToolItem;
import aphorea.utils.magichealing.AphMagicHealing;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.ToolItem;
import necesse.level.maps.Level;

public class WoodenRod
extends AphGlaiveToolItem {
    int attackCount = 0;

    public WoodenRod() {
        super(200);
        this.rarity = Item.Rarity.NORMAL;
        this.attackAnimTime.setBaseValue(500);
        this.attackDamage.setBaseValue(5.0f).setUpgradedValue(1.0f, 60.0f);
        this.attackRange.setBaseValue(220);
        this.knockback.setBaseValue(40);
        this.attackXOffset = 86;
        this.attackYOffset = 86;
        this.width = 14.0f;
    }

    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"woodenrod", (String)"healing", (Object)AphMagicHealing.getMagicHealing((Mob)perspective, (Mob)perspective, 3)));
        return tooltips;
    }

    public void hitMob(InventoryItem item, ToolItemMobAbilityEvent event, Level level, Mob target, Mob attacker) {
        super.hitMob(item, event, level, target, attacker);
        ++this.attackCount;
        if (this.attackCount >= 10) {
            this.attackCount = 0;
            AphMagicHealing.healMob(attacker, attacker, 3, item, (ToolItem)this);
        }
    }
}

