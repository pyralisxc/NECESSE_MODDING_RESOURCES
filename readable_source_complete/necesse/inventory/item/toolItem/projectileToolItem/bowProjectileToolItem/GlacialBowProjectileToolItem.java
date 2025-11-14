/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.GlacialBowProjectile;
import necesse.entity.projectile.Projectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.arrowItem.ArrowItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.BowProjectileToolItem;
import necesse.inventory.lootTable.presets.BowWeaponsLootTable;
import necesse.level.maps.Level;

public class GlacialBowProjectileToolItem
extends BowProjectileToolItem {
    public GlacialBowProjectileToolItem() {
        super(1450, BowWeaponsLootTable.bowWeapons);
        this.attackAnimTime.setBaseValue(500);
        this.rarity = Item.Rarity.RARE;
        this.attackDamage.setBaseValue(60.0f).setUpgradedValue(1.0f, 99.166695f);
        this.velocity.setBaseValue(200);
        this.attackRange.setBaseValue(800);
        this.attackXOffset = 12;
        this.attackYOffset = 28;
        this.resilienceGain.setBaseValue(1.0f);
        this.canBeUsedForRaids = true;
        this.raidTicketsModifier = 0.25f;
    }

    @Override
    protected void addExtraBowTooltips(ListGameTooltips tooltips, InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        tooltips.add(Localization.translate("itemtooltip", "glacialbowtip"));
    }

    @Override
    public Projectile getProjectile(Level level, int x, int y, ItemAttackerMob owner, InventoryItem item, int seed, ArrowItem arrow, boolean consumeAmmo, float velocity, int range, GameDamage damage, int knockback, float resilienceGain, GNDItemMap mapContent) {
        return new GlacialBowProjectile(owner, owner.x, owner.y, x, y, velocity, range, damage, knockback);
    }

    @Override
    protected SoundSettings getAttackSound() {
        return super.getAttackSound();
    }
}

