/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.util.GameBlackboard
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.entity.projectile.Projectile
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item$Rarity
 *  necesse.level.maps.Level
 */
package aphorea.items.tools.weapons.range.sling;

import aphorea.items.tools.weapons.range.sling.AphSlingToolItem;
import aphorea.projectiles.toolitem.FireSlingStoneProjectile;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class FireSling
extends AphSlingToolItem {
    public FireSling() {
        super(200);
        this.rarity = Item.Rarity.NORMAL;
        this.attackAnimTime.setBaseValue(1200);
        this.attackDamage.setBaseValue(35.0f).setUpgradedValue(1.0f, 116.0f);
        this.attackRange.setBaseValue(500);
        this.velocity.setBaseValue(100);
        this.attackXOffset = 8;
        this.attackYOffset = 20;
    }

    @Override
    public Projectile getProjectile(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return new FireSlingStoneProjectile(level, (Mob)attackerMob, attackerMob.x, attackerMob.y, x, y, this.getProjectileVelocity(item, (Mob)attackerMob), this.getAttackRange(item), this.getAttackDamage(item), this.getKnockback(item, (Attacker)attackerMob));
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"firesling"));
        return tooltips;
    }
}

