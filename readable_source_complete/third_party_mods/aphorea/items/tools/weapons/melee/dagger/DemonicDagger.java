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
package aphorea.items.tools.weapons.melee.dagger;

import aphorea.items.tools.weapons.melee.dagger.AphDaggerToolItem;
import aphorea.projectiles.toolitem.DaggerProjectile;
import aphorea.utils.AphColors;
import java.awt.Color;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class DemonicDagger
extends AphDaggerToolItem {
    public DemonicDagger() {
        super(400);
        this.rarity = Item.Rarity.COMMON;
        this.attackAnimTime.setBaseValue(400);
        this.attackDamage.setBaseValue(27.0f).setUpgradedValue(1.0f, 86.0f);
        this.attackRange.setBaseValue(45);
        this.knockback.setBaseValue(25);
    }

    @Override
    public Projectile getProjectile(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, float throwingVelocity, boolean shouldDrop) {
        return new DaggerProjectile.DemonicDaggerProjectile(level, (Mob)attackerMob, attackerMob.x, attackerMob.y, x, y, 200.0f * throwingVelocity, this.projectileRange(), this.getAttackDamage(item), this.getKnockback(item, (Attacker)attackerMob), shouldDrop, item.item.getStringID(), item.getGndData());
    }

    @Override
    public int projectileRange() {
        return 350;
    }

    @Override
    public Color getSecondaryAttackColor() {
        return AphColors.demonic;
    }
}

