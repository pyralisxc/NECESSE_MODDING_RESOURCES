/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.swordToolItem;

import necesse.engine.sound.SoundSettings;
import necesse.gfx.GameResources;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.swordToolItem.SwordToolItem;
import necesse.inventory.lootTable.presets.CloseRangeWeaponsLootTable;

public class WoodSwordToolItem
extends SwordToolItem {
    public WoodSwordToolItem() {
        super(100, CloseRangeWeaponsLootTable.closeRangeWeapons);
        this.rarity = Item.Rarity.NORMAL;
        this.attackAnimTime.setBaseValue(300);
        this.attackDamage.setBaseValue(15.0f).setUpgradedValue(1.0f, 110.83337f);
        this.attackRange.setBaseValue(50);
        this.knockback.setBaseValue(75);
        this.canBeUsedForRaids = true;
    }

    @Override
    protected SoundSettings getAttackSound() {
        return new SoundSettings(GameResources.woodSword).volume(0.8f);
    }
}

