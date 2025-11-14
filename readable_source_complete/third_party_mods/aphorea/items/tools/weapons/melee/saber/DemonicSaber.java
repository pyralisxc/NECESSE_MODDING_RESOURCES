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
package aphorea.items.tools.weapons.melee.saber;

import aphorea.items.tools.weapons.melee.saber.AphSaberToolItem;
import aphorea.projectiles.toolitem.AircutProjectile;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class DemonicSaber
extends AphSaberToolItem {
    public DemonicSaber() {
        super(400);
        this.rarity = Item.Rarity.COMMON;
        this.attackDamage.setBaseValue(30.0f).setUpgradedValue(1.0f, 85.0f);
        this.knockback.setBaseValue(80);
        this.attackRange.setBaseValue(60);
    }

    @Override
    public Projectile getProjectile(Level level, int x, int y, int targetX, int targetY, ItemAttackerMob attackerMob, InventoryItem item, float powerPercent, int seed) {
        return new AircutProjectile.DemonicAircutProjectile(level, (Mob)attackerMob, x, y, targetX, targetY, 200.0f * powerPercent, (int)(400.0f * powerPercent), this.getAttackDamage(item).modDamage(powerPercent), (int)((float)this.getKnockback(item, (Attacker)attackerMob) * powerPercent));
    }
}

