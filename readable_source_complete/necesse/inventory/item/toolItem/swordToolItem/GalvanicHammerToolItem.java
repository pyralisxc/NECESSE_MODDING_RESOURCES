/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.swordToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameBlackboard;
import necesse.entity.levelEvent.mobAbilityLevelEvent.GalvanicTrailEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.swordToolItem.SwordToolItem;
import necesse.inventory.lootTable.presets.CloseRangeWeaponsLootTable;
import necesse.level.maps.Level;

public class GalvanicHammerToolItem
extends SwordToolItem {
    public GalvanicHammerToolItem() {
        super(650, CloseRangeWeaponsLootTable.closeRangeWeapons);
        this.rarity = Item.Rarity.UNCOMMON;
        this.attackAnimTime.setBaseValue(450);
        this.attackDamage.setBaseValue(18.0f).setUpgradedValue(1.0f, 26.83334f);
        this.attackRange.setBaseValue(75);
        this.knockback.setBaseValue(75);
        this.resilienceGain.setBaseValue(0.33f);
        this.canBeUsedForRaids = true;
        this.raidTicketsModifier = 0.5f;
        this.useForRaidsOnlyIfObtained = true;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "galvanichammertip"));
        return tooltips;
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        for (int i = 1; i < 6; ++i) {
            GalvanicTrailEvent event = new GalvanicTrailEvent(attackerMob, this.getAttackDamage(item), this.getResilienceGain(item), attackerMob.getX(), attackerMob.getY(), x, y + attackHeight, seed * i);
            attackerMob.addAndSendAttackerLevelEvent(event);
        }
        return item;
    }

    @Override
    protected SoundSettings getAttackSound() {
        return new SoundSettings(GameResources.lightningHammer).volume(0.35f);
    }
}

