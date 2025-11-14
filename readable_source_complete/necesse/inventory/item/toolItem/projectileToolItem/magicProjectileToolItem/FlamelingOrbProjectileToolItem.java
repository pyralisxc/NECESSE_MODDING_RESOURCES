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

public class FlamelingOrbProjectileToolItem
extends MagicProjectileToolItem {
    public FlamelingOrbProjectileToolItem() {
        super(0, null);
        this.rarity = Item.Rarity.RARE;
        this.attackAnimTime.setBaseValue(0);
        this.attackDamage.setBaseValue(0.0f).setUpgradedValue(1.0f, 0.0f);
        this.velocity.setBaseValue(0);
        this.attackXOffset = 20;
        this.attackYOffset = 20;
        this.attackRange.setBaseValue(0);
        this.knockback.setBaseValue(0);
        this.manaCost.setBaseValue(0.0f).setUpgradedValue(1.0f, 0.0f);
        this.itemAttackerProjectileCanHitWidth = 0.0f;
    }

    @Override
    public GameMessage getNewLocalization() {
        return new StaticMessage("NOT_OBTAINABLE: Flameling Orb");
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        return item;
    }

    @Override
    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        if (level.isClient()) {
            SoundManager.playSound(GameResources.magicbolt2, (SoundEffect)SoundEffect.effect(attackerMob).volume(0.4f).pitch(GameRandom.globalRandom.getFloatBetween(0.8f, 0.9f)));
        }
    }
}

