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

public class RubyShieldsToolItem
extends SummonToolItem {
    public RubyShieldsToolItem() {
        super("rubyshield", FollowPosition.FLYING_CIRCLE, 1.0f, 1300, SummonWeaponsLootTable.summonWeapons);
        this.summonType = "summonedmobtemp";
        this.rarity = Item.Rarity.EPIC;
        this.attackDamage.setBaseValue(50.0f).setUpgradedValue(1.0f, 87.50002f);
        this.attackXOffset = 15;
        this.attackYOffset = 10;
    }

    @Override
    public int getItemAttackerStoppingDistance(ItemAttackerMob mob, InventoryItem item, int attackRange) {
        return 128;
    }

    @Override
    public int getItemAttackerRunAwayDistance(ItemAttackerMob attackerMob, InventoryItem item) {
        return 96;
    }

    @Override
    public int getMaxSummons(InventoryItem item, ItemAttackerMob attackerMob) {
        return 6;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "rubyshieldstip"), 400);
        return tooltips;
    }

    @Override
    protected SoundSettings getAttackSound() {
        return new SoundSettings(GameResources.rubyShields).volume(0.1f).pitchVariance(0.01f);
    }
}

