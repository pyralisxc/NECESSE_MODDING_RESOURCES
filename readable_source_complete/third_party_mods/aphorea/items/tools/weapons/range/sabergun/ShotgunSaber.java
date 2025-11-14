/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.registries.ItemRegistry
 *  necesse.engine.util.GameBlackboard
 *  necesse.engine.util.GameRandom
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.itemAttacker.AmmoConsumed
 *  necesse.entity.mobs.itemAttacker.AmmoUserMob
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.entity.projectile.Projectile
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item$Rarity
 *  necesse.level.maps.Level
 */
package aphorea.items.tools.weapons.range.sabergun;

import aphorea.items.tools.weapons.range.sabergun.AphSaberGunToolItem;
import aphorea.projectiles.bullet.ShotgunBulletProjectile;
import necesse.engine.localization.Localization;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.AmmoConsumed;
import necesse.entity.mobs.itemAttacker.AmmoUserMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class ShotgunSaber
extends AphSaberGunToolItem {
    public ShotgunSaber() {
        super(1300);
        this.rarity = Item.Rarity.RARE;
        this.attackDamage.setBaseValue(14.0f).setUpgradedValue(1.0f, 32.0f);
        this.knockback.setBaseValue(20);
    }

    @Override
    public Projectile getProjectile(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        int spriteX;
        GameDamage damage;
        float spreadPercent = ShotgunSaber.spreadPercent(item.getGndData().getFloat("chargePercent"));
        GameDamage baseDamage = this.getAttackDamage(item);
        float projectileSpeed = this.getProjectileVelocity(item, (Mob)attackerMob);
        int range = this.getAttackRange(item);
        int knockback = this.getKnockback(item, (Attacker)attackerMob);
        if (spreadPercent <= 0.2f) {
            damage = baseDamage.setDamage(baseDamage.damage * 1.25f);
            spriteX = 1;
        } else {
            float statsMod = (1.0f - spreadPercent + 0.1f) * 0.4f + 0.6f;
            damage = baseDamage.setDamage(baseDamage.damage * statsMod);
            spriteX = 0;
            projectileSpeed *= statsMod;
            range = (int)((float)range * statsMod);
            knockback = (int)((float)knockback * statsMod);
        }
        return new ShotgunBulletProjectile(attackerMob.x, attackerMob.y, x, y, projectileSpeed, range, damage, this.getArmorPenPercent(level, attackerMob, item), knockback, (Mob)attackerMob, spriteX);
    }

    @Override
    public int getProjectilesNumber(InventoryItem item) {
        return 6;
    }

    @Override
    public float getProjectilesMaxSpread(InventoryItem item) {
        return 4.0f + 26.0f * ShotgunSaber.spreadPercent(item.getGndData().getFloat("chargePercent"));
    }

    @Override
    public float getDashDamageMultiplier(InventoryItem item) {
        return 5.0f;
    }

    @Override
    public void doAttack(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, int seed) {
        boolean shouldFire;
        if (attackerMob instanceof AmmoUserMob) {
            AmmoConsumed consumed = ((AmmoUserMob)attackerMob).removeAmmo(ItemRegistry.getItem((String)"simplebullet"), 1, "bulletammo");
            shouldFire = consumed.amount >= 1;
        } else {
            shouldFire = true;
        }
        if (shouldFire) {
            int projectilesNumber = this.getProjectilesNumber(item);
            float maxSpread = this.getProjectilesMaxSpread(item);
            GameRandom random = new GameRandom((long)seed);
            GameRandom spreadRandom = new GameRandom((long)(seed + 10));
            for (int i = 0; i < projectilesNumber; ++i) {
                Projectile projectile = this.getProjectile(level, x, y, attackerMob, item);
                projectile.height -= 2.0f;
                projectile.resetUniqueID(random);
                attackerMob.addAndSendAttackerProjectile(projectile, GameRandom.globalRandom.getIntBetween(10, 20), spreadRandom.getFloatOffset(0.0f, maxSpread));
            }
        }
    }

    @Override
    public void addLeftClickTooltips(ListGameTooltips tooltips, InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"shotgunsaber"));
    }

    @Override
    public float getBaseArmorPenPercent() {
        return 0.5f;
    }
}

