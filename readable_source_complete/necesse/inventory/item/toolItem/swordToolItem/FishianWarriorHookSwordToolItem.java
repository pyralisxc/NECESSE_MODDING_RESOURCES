/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.swordToolItem;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.SoundSettings;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.swordToolItem.SwordToolItem;
import necesse.level.maps.Level;

public class FishianWarriorHookSwordToolItem
extends SwordToolItem {
    public FishianWarriorHookSwordToolItem() {
        super(0, null);
        this.rarity = Item.Rarity.NORMAL;
        this.attackAnimTime.setBaseValue(300);
        this.attackDamage.setBaseValue(17.0f).setUpgradedValue(1.0f, 93.0f);
        this.attackRange.setBaseValue(50);
        this.knockback.setBaseValue(75);
        this.attackXOffset = 16;
        this.attackYOffset = 16;
    }

    @Override
    public GameMessage getNewLocalization() {
        return new StaticMessage("NOT_OBTAINABLE: Fishian Warrior Hook");
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        return item;
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        super.setDrawAttackRotation(item, drawOptions, attackDirX, attackDirY, drawOptions.dir == 0 || drawOptions.dir == 2 ? 0.2f : 0.4f);
    }

    @Override
    protected SoundSettings getAttackSound() {
        return new SoundSettings(GameResources.fishianWarriorHook);
    }
}

