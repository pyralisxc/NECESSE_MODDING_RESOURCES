/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem.BoomerangToolItem;
import necesse.inventory.lootTable.presets.ThrowWeaponsLootTable;
import necesse.level.maps.Level;

public class RazorBladeBoomerangToolItem
extends BoomerangToolItem {
    public RazorBladeBoomerangToolItem() {
        super(900, ThrowWeaponsLootTable.throwWeapons, "razorbladeboomerang");
        this.rarity = Item.Rarity.RARE;
        this.attackAnimTime.setBaseValue(180);
        this.attackDamage.setBaseValue(26.0f).setUpgradedValue(1.0f, 46.66668f);
        this.attackRange.setBaseValue(250);
        this.velocity.setBaseValue(125);
        this.resilienceGain.setBaseValue(0.2f);
        this.knockback.setBaseValue(50);
        this.itemAttackerProjectileCanHitWidth = 10.0f;
        this.canBeUsedForRaids = true;
        this.raidTicketsModifier = 0.5f;
        this.useForRaidsOnlyIfObtained = true;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "razorbladeboomerangtip"));
        return tooltips;
    }

    @Override
    public String canAttack(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return attackerMob.getBoomerangsUsage() < 5 ? null : "";
    }
}

