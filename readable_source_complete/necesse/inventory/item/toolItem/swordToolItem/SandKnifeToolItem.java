/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.swordToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameBlackboard;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.swordToolItem.SwordToolItem;
import necesse.inventory.lootTable.presets.CloseRangeWeaponsLootTable;
import necesse.level.maps.Level;

public class SandKnifeToolItem
extends SwordToolItem {
    public SandKnifeToolItem() {
        super(1750, CloseRangeWeaponsLootTable.closeRangeWeapons);
        this.rarity = Item.Rarity.UNCOMMON;
        this.attackAnimTime.setBaseValue(200);
        this.attackDamage.setBaseValue(5.0f).setUpgradedValue(1.0f, 11.66667f);
        this.attackRange.setBaseValue(40);
        this.knockback.setBaseValue(25);
        this.attackXOffset = 10;
        this.attackYOffset = 10;
        this.canBeUsedForRaids = false;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "sandknifetip"));
        return tooltips;
    }

    @Override
    public void hitMob(InventoryItem item, ToolItemMobAbilityEvent event, Level level, Mob target, Mob attacker) {
        super.hitMob(item, event, level, target, attacker);
        target.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.SAND_KNIFE_WOUND_BUFF, target, 5000, (Attacker)attacker), true);
    }

    @Override
    protected SoundSettings getAttackSound() {
        return new SoundSettings(GameResources.sandKnife).volume(0.5f);
    }
}

