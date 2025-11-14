/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.entity.projectile.Projectile
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item$Rarity
 *  necesse.level.maps.Level
 */
package aphorea.items.tools.weapons.range.sling;

import aphorea.items.tools.weapons.range.sling.AphSlingToolItem;
import aphorea.projectiles.toolitem.SlingStoneProjectile;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class Sling
extends AphSlingToolItem {
    public Sling() {
        super(100);
        this.rarity = Item.Rarity.NORMAL;
        this.attackAnimTime.setBaseValue(1200);
        this.attackDamage.setBaseValue(30.0f).setUpgradedValue(1.0f, 120.0f);
        this.attackRange.setBaseValue(500);
        this.velocity.setBaseValue(100);
        this.attackXOffset = 8;
        this.attackYOffset = 20;
    }

    @Override
    public Projectile getProjectile(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return new SlingStoneProjectile(level, (Mob)attackerMob, attackerMob.x, attackerMob.y, x, y, this.getProjectileVelocity(item, (Mob)attackerMob), this.getAttackRange(item), this.getAttackDamage(item), this.getKnockback(item, (Attacker)attackerMob));
    }
}

