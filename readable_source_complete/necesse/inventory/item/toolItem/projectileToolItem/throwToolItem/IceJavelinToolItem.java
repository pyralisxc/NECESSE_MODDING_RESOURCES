/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.throwToolItem;

import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.ProjectileRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.SettlerIgnoredThrowToolItem;
import necesse.level.maps.Level;

public class IceJavelinToolItem
extends SettlerIgnoredThrowToolItem {
    public IceJavelinToolItem() {
        this.rarity = Item.Rarity.UNCOMMON;
        this.attackAnimTime.setBaseValue(250);
        this.attackDamage.setBaseValue(30.0f);
        this.velocity.setBaseValue(200);
        this.attackRange.setBaseValue(800);
        this.incinerationTimeMillis = 3000;
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        Projectile projectile = ProjectileRegistry.getProjectile("icejavelin", level, attackerMob.x, attackerMob.y, (float)x, (float)y, (float)this.getThrowingVelocity(item, attackerMob), this.getAttackRange(item), this.getAttackDamage(item), this.getKnockback(item, attackerMob), (Mob)attackerMob);
        projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
        projectile.resetUniqueID(new GameRandom(seed));
        attackerMob.addAndSendAttackerProjectile(projectile, 30);
        item.setAmount(item.getAmount() - 1);
        return item;
    }

    @Override
    protected SoundSettings getSwingSound() {
        return null;
    }
}

