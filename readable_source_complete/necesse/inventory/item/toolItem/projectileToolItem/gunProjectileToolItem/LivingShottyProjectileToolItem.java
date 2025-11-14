/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem;

import java.awt.Point;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.LivingShottyLeafProjectile;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.bulletItem.BulletItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.GunProjectileToolItem;
import necesse.inventory.lootTable.presets.GunWeaponsLootTable;
import necesse.level.maps.Level;

public class LivingShottyProjectileToolItem
extends GunProjectileToolItem {
    public LivingShottyProjectileToolItem() {
        super(NORMAL_AMMO_TYPES, 1650, GunWeaponsLootTable.gunWeapons);
        this.rarity = Item.Rarity.EPIC;
        this.attackAnimTime.setBaseValue(600);
        this.attackDamage.setBaseValue(49.0f).setUpgradedValue(1.0f, 64.16669f);
        this.attackXOffset = 12;
        this.attackYOffset = 10;
        this.attackRange.setBaseValue(1000);
        this.velocity.setBaseValue(350);
        this.moveDist = 20;
        this.resilienceGain.setBaseValue(0.2f);
        this.addGlobalIngredient("bulletuser");
        this.canBeUsedForRaids = true;
        this.raidTicketsModifier = 0.5f;
        this.useForRaidsOnlyIfObtained = true;
    }

    @Override
    protected void fireProjectiles(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, int seed, BulletItem bullet, boolean dropItem, GNDItemMap mapContent) {
        int range;
        GameRandom random = new GameRandom(seed);
        GameRandom spreadRandom = random.nextSeeded(27);
        if (this.controlledRange) {
            Point newTarget = this.controlledRangePosition(spreadRandom, attackerMob, x, y, item, this.controlledMinRange, this.controlledInaccuracy);
            x = newTarget.x;
            y = newTarget.y;
            range = (int)attackerMob.getDistance(x, y);
        } else {
            range = this.getAttackRange(item);
        }
        for (int i = 0; i <= 5; ++i) {
            Projectile projectile;
            if (i == 2) {
                projectile = new LivingShottyLeafProjectile(level, attackerMob, attackerMob.x, attackerMob.y, x, y, this.getProjectileVelocity(item, attackerMob), range, this.getAttackDamage(item), this.getKnockback(item, attackerMob));
            } else {
                projectile = this.getProjectile(item, bullet, attackerMob.x, attackerMob.y, x, y, range, attackerMob);
                projectile.setDamage(this.getAttackDamage(item).modFinalMultiplier(0.4f));
            }
            projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
            projectile.dropItem = dropItem;
            projectile.getUniqueID(random);
            attackerMob.addAndSendAttackerProjectile(projectile, this.moveDist, (spreadRandom.nextFloat() - 0.5f) * 20.0f);
        }
    }

    @Override
    protected SoundSettings getAttackSound() {
        return new SoundSettings(GameResources.shotgun);
    }
}

