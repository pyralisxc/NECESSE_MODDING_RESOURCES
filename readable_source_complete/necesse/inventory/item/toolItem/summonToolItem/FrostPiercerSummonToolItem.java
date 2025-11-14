/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.summonToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.summonToolItem.SummonToolItem;
import necesse.inventory.lootTable.presets.SummonWeaponsLootTable;

public class FrostPiercerSummonToolItem
extends SummonToolItem {
    public FrostPiercerSummonToolItem() {
        super("frostpiercer", FollowPosition.FLYING_CIRCLE_FAST, 1.0f, 550, SummonWeaponsLootTable.summonWeapons);
        this.summonType = "summonedmobtemp";
        this.attackAnimTime.setBaseValue(400);
        this.rarity = Item.Rarity.RARE;
        this.attackDamage.setBaseValue(26.0f).setUpgradedValue(1.0f, 81.66669f);
        this.knockback.setBaseValue(0);
        this.attackXOffset = 5;
        this.attackYOffset = 24;
        this.drawMaxSummons = false;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "secondarysummon"));
        return tooltips;
    }

    @Override
    public int getMaxSummons(InventoryItem item, ItemAttackerMob attackerMob) {
        return 8;
    }

    @Override
    protected SoundSettings getAttackSound() {
        return new SoundSettings(GameResources.frostPiercer).volume(0.2f);
    }
}

