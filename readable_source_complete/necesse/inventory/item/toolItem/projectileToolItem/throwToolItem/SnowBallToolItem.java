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
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.SettlerIgnoredThrowToolItem;
import necesse.level.maps.Level;

public class SnowBallToolItem
extends SettlerIgnoredThrowToolItem {
    public SnowBallToolItem() {
        this.attackAnimTime.setBaseValue(250);
        this.attackDamage.setBaseValue(0.0f);
        this.velocity.setBaseValue(100);
        this.attackRange.setBaseValue(800);
        this.rarity = Item.Rarity.COMMON;
        this.attackXOffset = -25;
        this.attackYOffset = 2;
        this.resilienceGain.setBaseValue(0.0f);
        this.incinerationTimeMillis = 3000;
    }

    @Override
    public void addAttackSpeedTip(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, ItemAttackerMob attackerMob) {
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        Projectile projectile = ProjectileRegistry.getProjectile("playersnowball", level, attackerMob.x, attackerMob.y, (float)x, (float)y, (float)this.getThrowingVelocity(item, attackerMob), this.getAttackRange(item), this.getAttackDamage(item), this.getKnockback(item, attackerMob), (Mob)attackerMob);
        projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
        projectile.resetUniqueID(new GameRandom(seed));
        attackerMob.addAndSendAttackerProjectile(projectile);
        item.setAmount(item.getAmount() - 1);
        return item;
    }

    @Override
    protected SoundSettings getAttackSound() {
        return new SoundSettings(GameResources.snowBall).volume(0.4f).basePitch(1.3f);
    }
}

