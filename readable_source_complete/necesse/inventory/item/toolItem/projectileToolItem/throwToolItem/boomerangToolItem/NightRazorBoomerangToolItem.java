/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem.BoomerangToolItem;
import necesse.inventory.lootTable.presets.IncursionThrowWeaponsLootTable;

public class NightRazorBoomerangToolItem
extends BoomerangToolItem {
    public NightRazorBoomerangToolItem() {
        super(1900, IncursionThrowWeaponsLootTable.incursionThrowWeapons, "nightrazorboomerang");
        this.rarity = Item.Rarity.RARE;
        this.attackAnimTime.setBaseValue(300);
        this.attackDamage.setBaseValue(70.0f).setUpgradedValue(1.0f, 81.66669f);
        this.attackRange.setBaseValue(600);
        this.velocity.setBaseValue(220);
        this.stackSize = 4;
        this.resilienceGain.setBaseValue(0.5f);
        this.knockback.setBaseValue(50);
        this.itemAttackerProjectileCanHitWidth = 18.0f;
        this.canBeUsedForRaids = true;
        this.raidTicketsModifier = 0.5f;
        this.useForRaidsOnlyIfObtained = true;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "nightrazorboomerangtip"));
        return tooltips;
    }
}

