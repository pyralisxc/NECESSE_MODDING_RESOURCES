/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.swordToolItem;

import necesse.engine.sound.SoundSettings;
import necesse.gfx.GameResources;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.swordToolItem.SwordToolItem;
import necesse.inventory.lootTable.presets.CloseRangeWeaponsLootTable;

public class SpiderClawSwordToolItem
extends SwordToolItem {
    public SpiderClawSwordToolItem() {
        super(550, CloseRangeWeaponsLootTable.closeRangeWeapons);
        this.rarity = Item.Rarity.UNCOMMON;
        this.attackAnimTime.setBaseValue(100);
        this.attackDamage.setBaseValue(16.0f).setUpgradedValue(1.0f, 64.16669f);
        this.attackRange.setBaseValue(45);
        this.knockback.setBaseValue(30);
        this.resilienceGain.setBaseValue(0.5f);
        this.canBeUsedForRaids = true;
        this.raidTicketsModifier = 0.5f;
        this.useForRaidsOnlyIfObtained = true;
    }

    @Override
    protected SoundSettings getAttackSound() {
        return new SoundSettings(GameResources.spiderclaw).volume(0.3f);
    }
}

