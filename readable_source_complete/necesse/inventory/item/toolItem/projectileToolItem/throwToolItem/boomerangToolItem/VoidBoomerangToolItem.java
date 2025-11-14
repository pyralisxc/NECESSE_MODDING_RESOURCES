/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem;

import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem.BoomerangToolItem;
import necesse.inventory.lootTable.presets.ThrowWeaponsLootTable;

public class VoidBoomerangToolItem
extends BoomerangToolItem {
    public VoidBoomerangToolItem() {
        super(650, ThrowWeaponsLootTable.throwWeapons, "voidboomerang");
        this.rarity = Item.Rarity.RARE;
        this.attackAnimTime.setBaseValue(300);
        this.attackDamage.setBaseValue(33.0f).setUpgradedValue(1.0f, 75.83335f);
        this.attackRange.setBaseValue(400);
        this.velocity.setBaseValue(150);
        this.stackSize = 3;
        this.resilienceGain.setBaseValue(0.5f);
        this.itemAttackerProjectileCanHitWidth = 10.0f;
        this.canBeUsedForRaids = true;
        this.raidTicketsModifier = 0.5f;
        this.useForRaidsOnlyIfObtained = true;
    }
}

