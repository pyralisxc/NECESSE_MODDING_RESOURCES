/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.GreatswordToolItem;
import necesse.inventory.lootTable.presets.IncursionGreatswordWeaponsLootTable;
import necesse.level.maps.incursion.IncursionData;

public class RavenwingGreatswordToolItem
extends GreatswordToolItem {
    public RavenwingGreatswordToolItem() {
        super(1900, IncursionGreatswordWeaponsLootTable.incursionGreatswordWeapons, RavenwingGreatswordToolItem.getThreeChargeLevels(150, 300, 450));
        this.rarity = Item.Rarity.EPIC;
        this.attackDamage.setBaseValue(150.0f).setUpgradedValue(1.0f, 186.66672f);
        this.attackRange.setBaseValue(100);
        this.knockback.setBaseValue(150);
        this.canBeUsedForRaids = true;
        this.minRaidTier = 1;
        this.maxRaidTier = IncursionData.ITEM_TIER_UPGRADE_CAP;
        this.raidTicketsModifier = 0.25f;
        this.useForRaidsOnlyIfObtained = true;
        this.defaultLootTier = 1.0f;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "ravenwinggreatswordtip"), 400);
        return tooltips;
    }

    @Override
    public float getFinalAttackMovementMod(InventoryItem item, ItemAttackerMob attackerMob) {
        return 1.0f;
    }

    @Override
    protected SoundSettings getGreatswordSwingSound1() {
        return new SoundSettings(GameResources.ravenwingGreatsword1).volume(0.4f);
    }

    @Override
    protected SoundSettings getGreatswordSwingSound2() {
        return new SoundSettings(GameResources.ravenwingGreatsword2).volume(0.4f);
    }

    @Override
    protected SoundSettings getGreatswordSwingSound3() {
        return new SoundSettings(GameResources.ravenwingGreatsword3).volume(0.7f);
    }
}

