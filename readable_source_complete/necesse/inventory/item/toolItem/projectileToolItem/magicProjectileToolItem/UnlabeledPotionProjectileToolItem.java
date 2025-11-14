/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem;

import java.awt.Color;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.UnlabeledPotionProjectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.FlaskProjectileToolItem;
import necesse.inventory.lootTable.presets.MagicWeaponsLootTable;
import necesse.level.maps.Level;

public class UnlabeledPotionProjectileToolItem
extends FlaskProjectileToolItem {
    public UnlabeledPotionProjectileToolItem() {
        super(1600, MagicWeaponsLootTable.magicWeapons);
        this.rarity = Item.Rarity.RARE;
        this.attackAnimTime.setBaseValue(500);
        this.attackDamage.setBaseValue(40.0f).setUpgradedValue(1.0f, 154.00006f);
        this.velocity.setBaseValue(800);
        this.attackXOffset = 8;
        this.attackYOffset = 10;
        this.attackCooldownTime.setBaseValue(500);
        this.attackRange.setBaseValue(300);
        this.manaCost.setBaseValue(1.25f).setUpgradedValue(1.0f, 1.25f);
        this.resilienceGain.setBaseValue(1.0f);
        this.itemAttackerProjectileCanHitWidth = 5.0f;
        this.canBeUsedForRaids = false;
    }

    @Override
    protected Projectile getProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, GameRandom random) {
        return new UnlabeledPotionProjectile(level, owner, x, y, targetX, targetY, speed, distance, damage, knockback, random);
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "unlabeledpotiontip1"), 400);
        String translatedString = Localization.translate("itemtooltip", "unlabeledpotiontip2");
        StringTooltips tooltip2 = new StringTooltips(translatedString, new Color(255, 189, 46), 400);
        tooltips.add(tooltip2);
        return tooltips;
    }
}

