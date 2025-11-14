/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.bulletProjectile.NecroticBoltProjectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.arrowItem.ArrowItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.BowProjectileToolItem;
import necesse.inventory.lootTable.presets.BowWeaponsLootTable;
import necesse.level.maps.Level;

public class NecroticBowProjectileToolItem
extends BowProjectileToolItem {
    public NecroticBowProjectileToolItem() {
        super(850, BowWeaponsLootTable.bowWeapons);
        this.damageType = DamageTypeRegistry.SUMMON;
        this.rarity = Item.Rarity.COMMON;
        this.attackAnimTime.setBaseValue(750);
        this.attackDamage.setBaseValue(50.0f).setUpgradedValue(1.0f, 87.50002f);
        this.attackRange.setBaseValue(600);
        this.velocity.setBaseValue(140);
        this.attackXOffset = 8;
        this.attackYOffset = 26;
        this.setItemCategory("equipment", "weapons", "summonweapons");
        this.setItemCategory(ItemCategory.equipmentManager, "weapons", "summonweapons");
        this.setItemCategory(ItemCategory.craftingManager, "equipment", "weapons", "summonweapons");
        this.keyWords.add("summon");
    }

    @Override
    protected void addExtraBowTooltips(ListGameTooltips tooltips, InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        tooltips.add(Localization.translate("itemtooltip", "necroticbowtip"), 400);
    }

    @Override
    public Projectile getProjectile(Level level, int x, int y, ItemAttackerMob owner, InventoryItem item, int seed, ArrowItem arrow, boolean consumeAmmo, float velocity, int range, GameDamage damage, int knockback, float resilienceGain, GNDItemMap mapContent) {
        return new NecroticBoltProjectile(level, owner, owner.x, owner.y, x, y, velocity, range, damage);
    }

    @Override
    protected SoundSettings getAttackSound() {
        return super.getAttackSound();
    }
}

