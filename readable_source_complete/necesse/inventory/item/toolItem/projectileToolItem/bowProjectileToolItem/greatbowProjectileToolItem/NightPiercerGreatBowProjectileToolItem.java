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
import necesse.entity.projectile.NightPiercerArrowProjectile;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.arrowItem.ArrowItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem.GreatbowProjectileToolItem;
import necesse.inventory.lootTable.presets.IncursionGreatbowWeaponsLootTable;
import necesse.level.maps.Level;
import necesse.level.maps.incursion.IncursionData;

public class NightPiercerGreatBowProjectileToolItem
extends GreatbowProjectileToolItem {
    public NightPiercerGreatBowProjectileToolItem() {
        super(1900, IncursionGreatbowWeaponsLootTable.incursionGreatbowWeapons);
        this.attackAnimTime.setBaseValue(500);
        this.rarity = Item.Rarity.RARE;
        this.attackDamage.setBaseValue(124.0f).setUpgradedValue(1.0f, 157.50005f);
        this.velocity.setBaseValue(500);
        this.attackRange.setBaseValue(1600);
        this.attackXOffset = 12;
        this.attackYOffset = 38;
        this.particleColor = new Color(108, 37, 92);
        this.canBeUsedForRaids = true;
        this.minRaidTier = 1;
        this.maxRaidTier = IncursionData.ITEM_TIER_UPGRADE_CAP;
        this.raidTicketsModifier = 0.2f;
        this.useForRaidsOnlyIfObtained = true;
    }

    @Override
    protected void addExtraBowTooltips(ListGameTooltips tooltips, InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        super.addExtraBowTooltips(tooltips, item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "nightpiercertip"));
    }

    @Override
    public Projectile getProjectile(Level level, int x, int y, ItemAttackerMob owner, InventoryItem item, int seed, ArrowItem arrow, boolean consumeAmmo, float velocity, int range, GameDamage damage, int knockback, float resilienceGain, GNDItemMap mapContent) {
        return new NightPiercerArrowProjectile(level, owner, owner.x, owner.y, x, y, velocity, range, damage, knockback);
    }

    @Override
    protected SoundSettings getGreatbowShootSoundWeak() {
        return new SoundSettings(GameResources.nightPiercerGreatBowWeak);
    }

    @Override
    protected SoundSettings getGreatbowShootSoundStrong() {
        return new SoundSettings(GameResources.nightPiercerGreatBowStrong);
    }
}

