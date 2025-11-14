/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.network.gameNetworkData.GNDItemMap
 *  necesse.engine.util.GameRandom
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.entity.projectile.Projectile
 *  necesse.entity.projectile.modifiers.ProjectileModifier
 *  necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item$Rarity
 *  necesse.inventory.item.bulletItem.BulletItem
 *  necesse.level.maps.Level
 */
package aphorea.items.tools.weapons.range.gun;

import aphorea.items.vanillaitemtypes.weapons.AphGunProjectileToolItem;
import java.awt.Point;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ProjectileModifier;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.bulletItem.BulletItem;
import necesse.level.maps.Level;

public class TheSpammer
extends AphGunProjectileToolItem {
    public TheSpammer() {
        super("spambullet", 1300);
        this.rarity = Item.Rarity.RARE;
        this.attackAnimTime.setBaseValue(300);
        this.attackDamage.setBaseValue(30.0f).setUpgradedValue(1.0f, 45.0f);
        this.attackXOffset = 14;
        this.attackYOffset = 14;
        this.attackRange.setBaseValue(1000);
        this.velocity.setBaseValue(150);
        this.knockback.setBaseValue(25);
        this.resilienceGain.setBaseValue(0.5f);
        this.addGlobalIngredient(new String[]{"bulletuser"});
    }

    protected void addAmmoTooltips(ListGameTooltips tooltips, InventoryItem item) {
        super.addAmmoTooltips(tooltips, item);
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"thespammer"));
    }

    protected void fireProjectiles(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, int seed, BulletItem bullet, boolean dropItem, GNDItemMap mapContent) {
        int range;
        GameRandom spreadRandom = new GameRandom((long)(seed + 10));
        if (this.controlledRange) {
            Point newTarget = this.controlledRangePosition(new GameRandom((long)(seed + 10)), (Mob)attackerMob, x, y, item, this.controlledMinRange, this.controlledInaccuracy);
            x = newTarget.x;
            y = newTarget.y;
            range = (int)attackerMob.getDistance((float)x, (float)y);
        } else {
            range = this.getAttackRange(item);
        }
        Projectile projectile = this.getProjectile(item, bullet, attackerMob.x, attackerMob.y, x, y, range, attackerMob);
        projectile.setModifier((ProjectileModifier)new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
        projectile.dropItem = dropItem;
        projectile.getUniqueID(new GameRandom((long)seed));
        projectile.setAngle((float)Math.toDegrees(Math.atan2((float)y - attackerMob.y, (float)x - attackerMob.x)) + spreadRandom.getFloatOffset(0.0f, 6.0f) + 90.0f);
        attackerMob.addAndSendAttackerProjectile(projectile, GameRandom.globalRandom.getIntBetween(10, 20));
    }
}

