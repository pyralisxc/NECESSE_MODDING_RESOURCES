/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.spearToolItem;

import necesse.engine.sound.SoundSettings;
import necesse.gfx.GameResources;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.spearToolItem.SpearToolItem;
import necesse.inventory.lootTable.presets.SpearWeaponsLootTable;

public class VulturesTalonToolItem
extends SpearToolItem {
    public VulturesTalonToolItem() {
        super(1050, SpearWeaponsLootTable.spearWeapons);
        this.rarity = Item.Rarity.RARE;
        this.attackAnimTime.setBaseValue(400);
        this.attackDamage.setBaseValue(40.0f).setUpgradedValue(1.0f, 70.000015f);
        this.attackRange.setBaseValue(150);
        this.knockback.setBaseValue(50);
        this.width = 12.0f;
        this.canBeUsedForRaids = true;
        this.raidTicketsModifier = 0.5f;
        this.useForRaidsOnlyIfObtained = true;
    }

    @Override
    protected SoundSettings getAttackSound() {
        return new SoundSettings(GameResources.vulturesTalon).volume(0.3f);
    }
}

