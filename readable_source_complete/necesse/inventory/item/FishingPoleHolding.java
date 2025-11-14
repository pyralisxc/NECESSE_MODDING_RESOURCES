/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item;

import java.awt.Color;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.fishingRodItem.FishingRodItem;

public class FishingPoleHolding
extends Item {
    public FishingPoleHolding() {
        super(1);
        this.attackAnimTime.setBaseValue(5000);
    }

    public static InventoryItem setGNDData(InventoryItem item, FishingRodItem fishingRod) {
        item.getGndData().setInt("fishingRod", fishingRod.getID());
        return item;
    }

    public FishingRodItem getFishingRodItem(InventoryItem item) {
        int fishingRodID = item.getGndData().getInt("fishingRod");
        Item fishingRodItem = ItemRegistry.getItem(fishingRodID);
        if (fishingRodItem instanceof FishingRodItem) {
            return (FishingRodItem)fishingRodItem;
        }
        return null;
    }

    @Override
    public ItemAttackDrawOptions setupItemSpriteAttackDrawOptions(ItemAttackDrawOptions options, InventoryItem item, PlayerMob player, int mobDir, float attackDirX, float attackDirY, float attackProgress, Color itemColor) {
        FishingRodItem fishingRod = this.getFishingRodItem(item);
        if (fishingRod != null) {
            ItemAttackDrawOptions.AttackItemSprite itemSprite = options.itemSprite(this.getAttackSprite(item, player));
            itemSprite.itemRotatePoint(fishingRod.attackXOffset, fishingRod.attackYOffset);
            if (itemColor != null) {
                itemSprite.itemColor(itemColor);
            }
            return itemSprite.itemEnd();
        }
        return super.setupItemSpriteAttackDrawOptions(options, item, player, mobDir, attackDirX, attackDirY, attackProgress, itemColor);
    }

    @Override
    public GameSprite getAttackSprite(InventoryItem item, PlayerMob player) {
        FishingRodItem fishingRod = this.getFishingRodItem(item);
        if (fishingRod != null) {
            return fishingRod.getAttackSprite(item, player);
        }
        return super.getAttackSprite(item, player);
    }

    @Override
    public GameMessage getNewLocalization() {
        return new StaticMessage(this.getStringID());
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add("NOT OBTAINABLE");
        return tooltips;
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        drawOptions.rotation(0.0f);
    }
}

