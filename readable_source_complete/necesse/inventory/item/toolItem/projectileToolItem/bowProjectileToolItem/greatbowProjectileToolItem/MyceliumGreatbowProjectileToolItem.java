/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem;

import java.awt.Color;
import necesse.engine.sound.SoundSettings;
import necesse.gfx.GameResources;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem.GreatbowProjectileToolItem;
import necesse.inventory.lootTable.presets.GreatbowWeaponsLootTable;
import necesse.level.maps.incursion.IncursionData;

public class MyceliumGreatbowProjectileToolItem
extends GreatbowProjectileToolItem {
    public MyceliumGreatbowProjectileToolItem() {
        super(1600, GreatbowWeaponsLootTable.greatbowWeapons);
        this.rarity = Item.Rarity.UNCOMMON;
        this.attackAnimTime.setBaseValue(600);
        this.attackDamage.setBaseValue(160.0f).setUpgradedValue(1.0f, 210.00006f);
        this.attackRange.setBaseValue(1400);
        this.velocity.setBaseValue(425);
        this.attackXOffset = 10;
        this.attackYOffset = 36;
        this.particleColor = new Color(230, 108, 14);
        this.canBeUsedForRaids = true;
        this.maxRaidTier = IncursionData.ITEM_TIER_UPGRADE_CAP;
    }

    @Override
    protected SoundSettings getGreatbowShootSoundWeak() {
        return new SoundSettings(GameResources.myceliumGreatBowWeak);
    }

    @Override
    protected SoundSettings getGreatbowShootSoundStrong() {
        return new SoundSettings(GameResources.myceliumGreatBowStrong);
    }
}

