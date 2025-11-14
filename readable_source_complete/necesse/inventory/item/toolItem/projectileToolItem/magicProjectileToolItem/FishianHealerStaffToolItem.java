/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.MagicProjectileToolItem;
import necesse.level.maps.Level;

public class FishianHealerStaffToolItem
extends MagicProjectileToolItem {
    public FishianHealerStaffToolItem() {
        super(0, null);
        this.rarity = Item.Rarity.EPIC;
        this.attackAnimTime.setBaseValue(1000);
        this.attackDamage.setBaseValue(1.0f).setUpgradedValue(1.0f, 1.0f);
        this.attackXOffset = 30;
        this.attackYOffset = 30;
        this.attackRange.setBaseValue(1000);
        this.manaCost.setBaseValue(10.0f);
        this.resilienceGain.setBaseValue(0.0f);
    }

    @Override
    public GameMessage getNewLocalization() {
        return new StaticMessage("NOT_OBTAINABLE: Fishian Healer Staff");
    }

    @Override
    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        if (level.isClient()) {
            SoundManager.playSound(GameResources.magicbolt1, (SoundEffect)SoundEffect.effect(attackerMob).volume(0.3f).pitch(GameRandom.globalRandom.getFloatBetween(1.5f, 1.6f)));
        }
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        return item;
    }
}

