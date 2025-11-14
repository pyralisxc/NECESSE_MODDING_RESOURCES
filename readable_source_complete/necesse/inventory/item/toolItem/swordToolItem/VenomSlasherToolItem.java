/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.swordToolItem;

import java.awt.Point;
import java.awt.Shape;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LineHitbox;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.VenomSlasherWaveProjectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.enchants.ToolItemModifiers;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.swordToolItem.SwordToolItem;
import necesse.inventory.lootTable.presets.CloseRangeWeaponsLootTable;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;

public class VenomSlasherToolItem
extends SwordToolItem {
    public VenomSlasherToolItem() {
        super(1650, CloseRangeWeaponsLootTable.closeRangeWeapons);
        this.rarity = Item.Rarity.EPIC;
        this.attackAnimTime.setBaseValue(300);
        this.attackDamage.setBaseValue(65.0f).setUpgradedValue(1.0f, 87.50002f);
        this.attackRange.setBaseValue(70);
        this.knockback.setBaseValue(75);
        this.resilienceGain.setBaseValue(1.0f);
        this.canBeUsedForRaids = true;
        this.raidTicketsModifier = 0.5f;
        this.useForRaidsOnlyIfObtained = true;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "venomslasherprojtip"), 400);
        return tooltips;
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        item = super.onAttack(level, x, y, attackerMob, attackHeight, item, slot, animAttack, seed, mapContent);
        float rangeMod = 7.0f;
        float velocity = 140.0f;
        float finalVelocity = Math.round(this.getEnchantment(item).applyModifierLimited(ToolItemModifiers.VELOCITY, (Float)ToolItemModifiers.VELOCITY.defaultBuffManagerValue).floatValue() * velocity * attackerMob.buffManager.getModifier(BuffModifiers.PROJECTILE_VELOCITY).floatValue());
        VenomSlasherWaveProjectile projectile = new VenomSlasherWaveProjectile(level, attackerMob.x, attackerMob.y, x, y, finalVelocity, (int)((float)this.getAttackRange(item) * rangeMod), new GameDamage(this.getAttackDamage((InventoryItem)item).damage * 0.5f), attackerMob);
        projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item) / 2.0f));
        projectile.resetUniqueID(new GameRandom(seed));
        attackerMob.addAndSendAttackerProjectile((Projectile)projectile, 20);
        return item;
    }

    @Override
    public int getItemAttackerAttackRange(ItemAttackerMob mob, InventoryItem item) {
        return this.getAttackRange(item) * 5;
    }

    @Override
    public boolean canItemAttackerHitTarget(ItemAttackerMob attackerMob, float fromX, float fromY, Mob target, InventoryItem item) {
        int attackRange;
        float distance = attackerMob.getDistance(target);
        if (distance < (float)(attackRange = this.getAttackRange(item))) {
            return super.canItemAttackerHitTarget(attackerMob, fromX, fromY, target, item);
        }
        if (distance < (float)(attackRange * 5)) {
            return !attackerMob.getLevel().collides((Shape)new LineHitbox(fromX, fromY, target.x, target.y, 45.0f), attackerMob.modifyChasingCollisionFilter(new CollisionFilter().projectileCollision(), target));
        }
        return false;
    }

    @Override
    public Point getItemAttackerAttackPosition(Level level, ItemAttackerMob attackerMob, Mob target, int seed, InventoryItem item) {
        float velocity = 140.0f;
        float finalVelocity = Math.round(this.getEnchantment(item).applyModifierLimited(ToolItemModifiers.VELOCITY, (Float)ToolItemModifiers.VELOCITY.defaultBuffManagerValue).floatValue() * velocity * attackerMob.buffManager.getModifier(BuffModifiers.PROJECTILE_VELOCITY).floatValue());
        return this.applyInaccuracy(attackerMob, item, this.getPredictedItemAttackerAttackPosition(attackerMob, target, finalVelocity, -20.0f));
    }

    @Override
    protected SoundSettings getAttackSound() {
        return new SoundSettings(GameResources.venomSlasher).volume(0.2f);
    }
}

