/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.entity.projectile.Projectile
 *  necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions
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
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class CopperDagger
extends AphDaggerToolItem {
    public CopperDagger() {
        super(200);
        this.rarity = Item.Rarity.NORMAL;
        this.attackAnimTime.setBaseValue(500);
        this.attackDamage.setBaseValue(20.0f).setUpgradedValue(1.0f, 90.0f);
        this.attackRange.setBaseValue(40);
        this.knockback.setBaseValue(25);
    }

    public ItemAttackDrawOptions setupItemSpriteAttackDrawOptions(ItemAttackDrawOptions options, InventoryItem item, PlayerMob player, int mobDir, float attackDirX, float attackDirY, float attackProgress, Color itemColor) {
        return super.setupItemSpriteAttackDrawOptions(options, item, player, mobDir, attackDirX, attackDirY, attackProgress, itemColor);
    }

    @Override
    public Projectile getProjectile(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, float throwingVelocity, boolean shouldDrop) {
        return new DaggerProjectile.CopperDaggerProjectile(level, (Mob)attackerMob, attackerMob.x, attackerMob.y, x, y, 100.0f * throwingVelocity, this.projectileRange(), this.getAttackDamage(item), this.getKnockback(item, (Attacker)attackerMob), shouldDrop, item.item.getStringID(), item.getGndData());
    }

    @Override
    public int projectileRange() {
        return 300;
    }

    @Override
    public Color getSecondaryAttackColor() {
        return AphColors.copper;
    }
}

