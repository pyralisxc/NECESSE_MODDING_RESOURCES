/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.NecroticFlaskProjectile;
import necesse.entity.projectile.Projectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.FlaskProjectileToolItem;
import necesse.inventory.lootTable.presets.MagicWeaponsLootTable;
import necesse.level.maps.Level;

public class NecroticFlaskProjectileToolItem
extends FlaskProjectileToolItem {
    public NecroticFlaskProjectileToolItem() {
        super(850, MagicWeaponsLootTable.magicWeapons);
        this.rarity = Item.Rarity.RARE;
        this.attackAnimTime.setBaseValue(500);
        this.attackDamage.setBaseValue(75.0f).setUpgradedValue(1.0f, 161.00005f);
        this.velocity.setBaseValue(800);
        this.attackXOffset = 8;
        this.attackYOffset = 10;
        this.attackCooldownTime.setBaseValue(500);
        this.attackRange.setBaseValue(300);
        this.manaCost.setBaseValue(1.25f).setUpgradedValue(1.0f, 2.0f);
        this.resilienceGain.setBaseValue(1.0f);
        this.itemAttackerProjectileCanHitWidth = 5.0f;
        this.canBeUsedForRaids = false;
    }

    @Override
    protected Projectile getProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, GameRandom random) {
        return new NecroticFlaskProjectile(level, owner, x, y, targetX, targetY, speed, distance, damage, knockback);
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "necroticflasktip"), 400);
        return tooltips;
    }
}

