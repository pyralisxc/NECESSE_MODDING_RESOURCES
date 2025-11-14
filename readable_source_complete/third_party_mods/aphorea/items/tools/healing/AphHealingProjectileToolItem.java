/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.gameNetworkData.GNDItemMap
 *  necesse.engine.util.GameRandom
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.ai.behaviourTree.AINode
 *  necesse.entity.mobs.ai.behaviourTree.trees.ItemAttackerChaserAINode
 *  necesse.entity.mobs.buffs.BuffModifiers
 *  necesse.entity.mobs.itemAttacker.ItemAttackSlot
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.entity.projectile.Projectile
 *  necesse.entity.projectile.modifiers.ProjectileModifier
 *  necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier
 *  necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.enchants.ToolItemModifiers
 *  necesse.inventory.item.ItemStatTipList
 *  necesse.inventory.item.upgradeUtils.IntUpgradeValue
 *  necesse.level.maps.Level
 */
package aphorea.items.tools.healing;

import aphorea.items.tools.healing.AphMagicHealingToolItem;
import aphorea.utils.magichealing.AphMagicHealing;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.trees.ItemAttackerChaserAINode;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ProjectileModifier;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.inventory.InventoryItem;
import necesse.inventory.enchants.ToolItemModifiers;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;
import necesse.level.maps.Level;

public abstract class AphHealingProjectileToolItem
extends AphMagicHealingToolItem {
    protected IntUpgradeValue velocity = new IntUpgradeValue(50, 0.0f);
    public int moveDist;

    public AphHealingProjectileToolItem(int enchantCost) {
        super(enchantCost);
    }

    protected abstract Projectile[] getProjectiles(Level var1, int var2, int var3, ItemAttackerMob var4, InventoryItem var5);

    public int getFlatVelocity(InventoryItem item) {
        GNDItemMap gndData = item.getGndData();
        return gndData.hasKey("velocity") ? gndData.getInt("velocity") : this.velocity.getValue(this.getUpgradeTier(item)).intValue();
    }

    public int getProjectileVelocity(InventoryItem item, Mob mob) {
        int velocity = this.getFlatVelocity(item);
        return Math.round(((Float)this.getEnchantment(item).applyModifierLimited(ToolItemModifiers.VELOCITY, (Object)((Float)ToolItemModifiers.VELOCITY.defaultBuffManagerValue))).floatValue() * (float)velocity * ((Float)mob.buffManager.getModifier(BuffModifiers.PROJECTILE_VELOCITY)).floatValue());
    }

    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        drawOptions.pointRotation(attackDirX, attackDirY);
    }

    public boolean animDrawBehindHand(InventoryItem item) {
        return true;
    }

    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
    }

    public void addStatTooltips(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, ItemAttackerMob perspective, boolean forceAdd) {
        AphMagicHealing.addMagicHealingTip(this, list, currentItem, lastItem, (Mob)perspective);
        this.addAttackSpeedTip(list, currentItem, lastItem, perspective);
        this.addManaCostTip(list, currentItem, lastItem, (Mob)perspective);
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        this.onHealingToolItemUsed((Mob)attackerMob, item);
        if (this.getManaCost(item) > 0.0f) {
            this.consumeMana(attackerMob, item);
        }
        this.fireProjectiles(level, x, y, attackerMob, item, seed, mapContent);
        return item;
    }

    protected void fireProjectiles(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, int seed, GNDItemMap mapContent) {
        Projectile[] projectiles;
        for (Projectile projectile : projectiles = this.getProjectiles(level, x, y, attackerMob, item)) {
            projectile.setModifier((ProjectileModifier)new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
            projectile.getUniqueID(new GameRandom((long)seed));
            attackerMob.addAndSendAttackerProjectile(projectile, this.moveDist);
        }
    }

    public AINode<ItemAttackerMob> getItemAttackerWeaponChaserAI(ItemAttackerChaserAINode<? extends ItemAttackerMob> node, ItemAttackerMob mob, InventoryItem item, ItemAttackSlot slot) {
        return super.getItemAttackerWeaponChaserAI(node, mob, item, slot);
    }
}

