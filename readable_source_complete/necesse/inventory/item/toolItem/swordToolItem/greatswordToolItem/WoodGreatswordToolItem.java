/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.SoundSettings;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.GreatswordToolItem;
import necesse.level.maps.Level;

public class WoodGreatswordToolItem
extends GreatswordToolItem {
    public WoodGreatswordToolItem() {
        super(0, null, WoodGreatswordToolItem.getThreeChargeLevels(500, 600, 700));
        this.rarity = Item.Rarity.COMMON;
        this.attackDamage.setBaseValue(35.0f).setUpgradedValue(1.0f, 192.50005f);
        this.attackRange.setBaseValue(70);
        this.knockback.setBaseValue(150);
        this.attackXOffset = 12;
        this.attackYOffset = 12;
    }

    @Override
    public GameMessage getNewLocalization() {
        return new StaticMessage("NOT_OBTAINABLE: Wood Greatsword");
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        return item;
    }

    @Override
    protected SoundSettings getGreatswordSwingSound1() {
        return new SoundSettings(GameResources.woodGreatsword1);
    }

    @Override
    protected SoundSettings getGreatswordSwingSound2() {
        return new SoundSettings(GameResources.woodGreatsword2);
    }

    @Override
    protected SoundSettings getGreatswordSwingSound3() {
        return new SoundSettings(GameResources.woodGreatsword3);
    }
}

