/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.swordToolItem;

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
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.CausticExecutionerProjectile;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.swordToolItem.SwordToolItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.incursion.IncursionData;

public class CausticExecutionerToolItem
extends SwordToolItem {
    public CausticExecutionerToolItem(OneOfLootItems lootTableCategory) {
        super(1900, lootTableCategory);
        this.rarity = Item.Rarity.EPIC;
        this.attackAnimTime.setBaseValue(300);
        this.attackDamage.setBaseValue(55.0f).setUpgradedValue(1.0f, 64.16669f);
        this.attackRange.setBaseValue(70);
        this.knockback.setBaseValue(75);
        this.resilienceGain.setBaseValue(1.5f);
        this.attackXOffset = 16;
        this.attackYOffset = 16;
        this.canBeUsedForRaids = true;
        this.minRaidTier = 1;
        this.maxRaidTier = IncursionData.ITEM_TIER_UPGRADE_CAP;
        this.raidTicketsModifier = 0.5f;
        this.useForRaidsOnlyIfObtained = true;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "causticexecutionertip"));
        return tooltips;
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        item = super.onAttack(level, x, y, attackerMob, attackHeight, item, slot, animAttack, seed, mapContent);
        float rangeMod = 2.0f;
        GameDamage projectileDamage = this.getAttackDamage(item).modDamage(0.6f);
        CausticExecutionerProjectile projectile = new CausticExecutionerProjectile(level, attackerMob.x, attackerMob.y, x, y, (int)((float)this.getAttackRange(item) * rangeMod), projectileDamage, attackerMob);
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
        if (distance < (float)attackRange * 1.5f) {
            return !attackerMob.getLevel().collides((Shape)new LineHitbox(fromX, fromY, target.x, target.y, 45.0f), attackerMob.modifyChasingCollisionFilter(new CollisionFilter().projectileCollision(), target));
        }
        return false;
    }

    @Override
    protected SoundSettings getAttackSound() {
        return new SoundSettings(GameResources.causticExecutioner).volume(0.75f);
    }
}

