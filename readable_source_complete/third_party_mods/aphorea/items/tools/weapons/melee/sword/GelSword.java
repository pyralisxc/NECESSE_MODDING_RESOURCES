/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.util.GameBlackboard
 *  necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item$Rarity
 *  necesse.level.maps.Level
 */
package aphorea.items.tools.weapons.melee.sword;

import aphorea.items.vanillaitemtypes.weapons.AphSwordToolItem;
import aphorea.registry.AphBuffs;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class GelSword
extends AphSwordToolItem {
    public GelSword() {
        super(200);
        this.rarity = Item.Rarity.NORMAL;
        this.attackAnimTime.setBaseValue(300);
        this.attackDamage.setBaseValue(18.0f).setUpgradedValue(1.0f, 90.0f);
        this.attackRange.setBaseValue(55);
        this.knockback.setBaseValue(5);
        this.attackXOffset = 12;
        this.attackYOffset = 16;
    }

    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"stikybuff1"));
        return tooltips;
    }

    public void hitMob(InventoryItem item, ToolItemMobAbilityEvent event, Level level, Mob target, Mob attacker) {
        super.hitMob(item, event, level, target, attacker);
        target.addBuff(new ActiveBuff(AphBuffs.STICKY, target, 1000, (Attacker)attacker), true);
    }
}

