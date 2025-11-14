/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.SapphireRevolverAttackHandler;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.SapphireRevolverProjectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.bulletItem.BulletItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.GunProjectileToolItem;
import necesse.inventory.lootTable.presets.GunWeaponsLootTable;
import necesse.level.maps.Level;
import necesse.level.maps.incursion.IncursionData;

public class SapphireRevolverProjectileToolItem
extends GunProjectileToolItem {
    public SapphireRevolverProjectileToolItem() {
        super(NORMAL_AMMO_TYPES, 350, GunWeaponsLootTable.gunWeapons);
        this.rarity = Item.Rarity.COMMON;
        this.attackAnimTime.setBaseValue(400);
        this.attackDamage.setBaseValue(40.0f).setUpgradedValue(1.0f, 198.33339f);
        this.attackXOffset = 8;
        this.attackYOffset = 12;
        this.attackRange.setBaseValue(800);
        this.velocity.setBaseValue(350);
        this.addGlobalIngredient("bulletuser");
        this.canBeUsedForRaids = true;
        this.minRaidTier = 1;
        this.maxRaidTier = IncursionData.ITEM_TIER_UPGRADE_CAP;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "sapphirerevolvertip"));
        return tooltips;
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        attackerMob.startAttackHandler(new SapphireRevolverAttackHandler(attackerMob, slot, item, this, seed, x, y));
        return item;
    }

    public InventoryItem superOnAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        return super.onAttack(level, x, y, attackerMob, attackHeight, item, slot, animAttack, seed, mapContent);
    }

    @Override
    protected void fireProjectiles(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, int seed, BulletItem bullet, boolean dropItem, GNDItemMap mapContent) {
        float distance = GameMath.diamondDistance(attackerMob.x, attackerMob.y, x, y);
        float t = 30.0f / distance;
        float projectileX = (1.0f - t) * attackerMob.x + t * (float)x;
        float projectileY = (1.0f - t) * attackerMob.y + t * (float)y;
        GameRandom random = new GameRandom(seed);
        Projectile projectile = this.getNormalProjectile(projectileX, projectileY, x, y, 2000.0f, 2000, this.getAttackDamage(item), this.getKnockback(item, attackerMob), attackerMob);
        projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
        projectile.dropItem = dropItem;
        projectile.getUniqueID(random);
        attackerMob.addAndSendAttackerProjectile(projectile, this.moveDist);
    }

    @Override
    public Projectile getNormalProjectile(float x, float y, float targetX, float targetY, float velocity, int range, GameDamage toolItemDamage, int knockback, ItemAttackerMob attackerMob) {
        return new SapphireRevolverProjectile(x, y, targetX, targetY, velocity, range, toolItemDamage, knockback, attackerMob);
    }

    @Override
    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        if (attackerMob.isClient() && item.getGndData().getBoolean("charged")) {
            SoundManager.playSound(GameResources.sniperrifle, (SoundEffect)SoundEffect.effect(attackerMob).volume(0.4f).pitch(GameRandom.globalRandom.getFloatBetween(0.4f, 0.6f)));
            SoundManager.playSound(GameResources.laserBlast1, (SoundEffect)SoundEffect.effect(attackerMob).volume(0.8f).pitch(GameRandom.globalRandom.getFloatBetween(0.65f, 0.85f)));
        }
    }
}

