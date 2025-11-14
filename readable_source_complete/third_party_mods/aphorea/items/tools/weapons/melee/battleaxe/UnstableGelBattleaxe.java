/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.util.GameBlackboard
 *  necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item$Rarity
 *  necesse.level.maps.Level
 */
package aphorea.items.tools.weapons.melee.battleaxe;

import aphorea.items.tools.weapons.melee.battleaxe.AphBattleaxeToolItem;
import aphorea.registry.AphBuffs;
import aphorea.utils.AphColors;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class UnstableGelBattleaxe
extends AphBattleaxeToolItem {
    public UnstableGelBattleaxe() {
        super(400, UnstableGelBattleaxe.getChargeLevel(2000, AphColors.unstableGel));
        this.rarity = Item.Rarity.COMMON;
        this.attackDamage.setBaseValue(120.0f).setUpgradedValue(1.0f, 340.0f);
        this.attackRange.setBaseValue(90);
        this.knockback.setBaseValue(150);
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"stikybuff3"));
        return tooltips;
    }

    @Override
    public void hitMob(InventoryItem item, ToolItemMobAbilityEvent event, Level level, Mob target, Mob attacker) {
        super.hitMob(item, event, level, target, attacker);
        target.addBuff(new ActiveBuff(AphBuffs.STICKY, target, 3000, null), true);
    }
}

