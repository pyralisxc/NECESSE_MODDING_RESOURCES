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

public class SlimeCanisterSummonToolItem
extends SummonToolItem {
    public SlimeCanisterSummonToolItem() {
        super("playerpoisonslime", FollowPosition.WALK_CLOSE, 1.0f, 850, SummonWeaponsLootTable.summonWeapons);
        this.summonType = "playerpoisonslime";
        this.rarity = Item.Rarity.RARE;
        this.attackAnimTime.setBaseValue(400);
        this.attackDamage.setBaseValue(40.0f).setUpgradedValue(1.0f, 58.33335f);
        this.knockback.setBaseValue(0);
        this.attackXOffset = 10;
        this.attackYOffset = 16;
        this.drawMaxSummons = false;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "slimecanistertip"));
        tooltips.add(Localization.translate("itemtooltip", "secondarysummon"));
        return tooltips;
    }

    @Override
    public int getMaxSummons(InventoryItem item, ItemAttackerMob attackerMob) {
        return 10;
    }

    @Override
    protected SoundSettings getAttackSound() {
        return new SoundSettings(GameResources.slimeSplash3).volume(0.1f).basePitch(1.1f).pitchVariance(0.2f);
    }
}

