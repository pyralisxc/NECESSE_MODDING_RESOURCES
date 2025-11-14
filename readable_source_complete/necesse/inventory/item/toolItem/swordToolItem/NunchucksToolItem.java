/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.swordToolItem;

import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.swordToolItem.SwordToolItem;
import necesse.inventory.lootTable.presets.CloseRangeWeaponsLootTable;
import necesse.level.maps.Level;

public class NunchucksToolItem
extends SwordToolItem {
    public NunchucksToolItem() {
        super(300, CloseRangeWeaponsLootTable.closeRangeWeapons);
        this.rarity = Item.Rarity.COMMON;
        this.attackAnimTime.setBaseValue(300);
        this.attackDamage.setBaseValue(20.0f).setUpgradedValue(1.0f, 105.00003f);
        this.attackRange.setBaseValue(30);
        this.knockback.setBaseValue(150);
        this.canBeUsedForRaids = true;
        this.raidTicketsModifier = 0.5f;
    }

    @Override
    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        if (level.isClient()) {
            SoundManager.playSound(GameResources.nunchucks, (SoundEffect)SoundEffect.effect(attackerMob).volume(0.9f).pitch(GameRandom.globalRandom.getFloatBetween(0.95f, 1.05f)));
        }
    }
}

