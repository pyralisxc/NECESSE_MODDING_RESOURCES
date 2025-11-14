/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem;

import java.awt.Color;
import java.awt.geom.Line2D;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.consumableItem.ConsumableItem;
import necesse.level.maps.Level;

public class StinkFlaskItem
extends ConsumableItem {
    public StinkFlaskItem() {
        super(1, false);
        this.attackAnimTime.setBaseValue(300);
        this.rarity = Item.Rarity.RARE;
        this.itemCooldownTime.setBaseValue(2000);
        this.worldDrawSize = 32;
    }

    @Override
    public String canPlace(Level level, int x, int y, PlayerMob player, Line2D playerPositionLine, InventoryItem item, GNDItemMap mapContent) {
        return null;
    }

    @Override
    public boolean shouldSendToOtherClients(Level level, int x, int y, PlayerMob player, InventoryItem item, String error, GNDItemMap mapContent) {
        return error == null;
    }

    @Override
    public void onOtherPlayerPlace(Level level, int x, int y, PlayerMob player, InventoryItem item, GNDItemMap mapContent) {
        SoundManager.playSound(GameResources.drink, (SoundEffect)SoundEffect.effect(player));
    }

    @Override
    public InventoryItem onPlace(Level level, int x, int y, PlayerMob player, int seed, InventoryItem item, GNDItemMap mapContent) {
        if (this.isSingleUse(player)) {
            item.setAmount(item.getAmount() - 1);
        }
        if (level.isServer()) {
            player.buffManager.addBuff(new ActiveBuff(BuffRegistry.Potions.STINKFLASK, (Mob)player, 180.0f, null), true);
        } else if (level.isClient()) {
            SoundManager.playSound(GameResources.drink, (SoundEffect)SoundEffect.effect(player));
        }
        return item;
    }

    @Override
    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        super.showAttack(level, x, y, attackerMob, attackHeight, item, animAttack, seed, mapContent);
        for (int i = 0; i < 20; ++i) {
            level.entityManager.addParticle(attackerMob.x + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), attackerMob.y + 2.0f + (float)(GameRandom.globalRandom.nextGaussian() * 4.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(attackerMob.dx / 2.0f, attackerMob.dy / 2.0f).color(new Color(91, 130, 36)).heightMoves(36.0f, 4.0f).lifeTime(750);
        }
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "stinkflasktip"));
        tooltips.add(Localization.translate("itemtooltip", "infiniteuse"));
        return tooltips;
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("item", "flask");
    }
}

