/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.NecroticGreatswordAttackHandler;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.GreatswordToolItem;
import necesse.inventory.lootTable.presets.GreatswordWeaponsLootTable;
import necesse.level.maps.Level;

public class NecroticGreatswordToolItem
extends GreatswordToolItem {
    public NecroticGreatswordToolItem() {
        super(850, GreatswordWeaponsLootTable.greatswordWeapons, NecroticGreatswordToolItem.getThreeChargeLevels(200, 400, 600));
        this.rarity = Item.Rarity.RARE;
        this.attackDamage.setBaseValue(90.0f).setUpgradedValue(1.0f, 151.6667f);
        this.attackRange.setBaseValue(130);
        this.knockback.setBaseValue(150);
        this.canBeUsedForRaids = true;
        this.raidTicketsModifier = 0.2f;
        this.useForRaidsOnlyIfObtained = true;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "necroticgreatswordtip"));
        return tooltips;
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        attackerMob.startAttackHandler(new NecroticGreatswordAttackHandler(attackerMob, slot, item, this, seed, x, y, this.chargeLevels));
        return item;
    }

    @Override
    protected SoundSettings getGreatswordSwingSound1() {
        return new SoundSettings(GameResources.necroticGreatsword1).volume(0.4f);
    }

    @Override
    protected SoundSettings getGreatswordSwingSound2() {
        return new SoundSettings(GameResources.necroticGreatsword2).volume(0.7f);
    }

    @Override
    protected SoundSettings getGreatswordSwingSound3() {
        return new SoundSettings(GameResources.necroticGreatsword3).volume(0.8f);
    }
}

