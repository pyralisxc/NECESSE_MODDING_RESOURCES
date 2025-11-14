/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem;

import java.awt.Color;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.DruidsGreatBowPetalProjectile;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.arrowItem.ArrowItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem.GreatbowProjectileToolItem;
import necesse.inventory.lootTable.presets.GreatbowWeaponsLootTable;
import necesse.level.maps.Level;

public class DruidsGreatBowProjectileToolItem
extends GreatbowProjectileToolItem {
    public DruidsGreatBowProjectileToolItem() {
        super(1650, GreatbowWeaponsLootTable.greatbowWeapons);
        this.attackAnimTime.setBaseValue(600);
        this.rarity = Item.Rarity.RARE;
        this.attackDamage.setBaseValue(80.0f).setUpgradedValue(1.0f, 110.83337f);
        this.velocity.setBaseValue(300);
        this.attackRange.setBaseValue(800);
        this.attackXOffset = 12;
        this.attackYOffset = 38;
        this.particleColor = new Color(46, 71, 50);
        this.canBeUsedForRaids = true;
        this.raidTicketsModifier = 0.25f;
        this.useForRaidsOnlyIfObtained = true;
    }

    @Override
    protected void addExtraBowTooltips(ListGameTooltips tooltips, InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        super.addExtraBowTooltips(tooltips, item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "druidsgreatbowtip"));
    }

    @Override
    public Projectile getProjectile(Level level, int x, int y, ItemAttackerMob owner, InventoryItem item, int seed, ArrowItem arrow, boolean consumeAmmo, float velocity, int range, GameDamage damage, int knockback, float resilienceGain, GNDItemMap mapContent) {
        return new DruidsGreatBowPetalProjectile(level, owner, owner.x, owner.y, x, y, velocity, range, damage, knockback);
    }

    @Override
    protected SoundSettings getGreatbowShootSoundWeak() {
        return new SoundSettings(GameResources.druidsGreatBowWeak).volume(0.8f);
    }

    @Override
    protected SoundSettings getGreatbowShootSoundStrong() {
        return new SoundSettings(GameResources.druidsGreatBowStrong).volume(0.55f);
    }
}

