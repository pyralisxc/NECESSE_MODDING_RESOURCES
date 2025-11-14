/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemInventoryItem;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.ItemRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class SwingSpriteAttackItem
extends Item {
    public SwingSpriteAttackItem() {
        super(1);
        this.attackAnimTime.setBaseValue(250);
    }

    @Override
    public GameMessage getNewLocalization() {
        return new StaticMessage("SWING_ATTACK");
    }

    @Override
    public GameSprite getAttackSprite(InventoryItem item, PlayerMob player) {
        Item spriteItem;
        GNDItem gndInvItem = item.getGndData().getItem("invItem");
        if (gndInvItem instanceof GNDItemInventoryItem) {
            InventoryItem swingItem = ((GNDItemInventoryItem)gndInvItem).invItem;
            return new GameSprite(swingItem.item.getItemSprite(swingItem, player), 24);
        }
        int itemID = item.getGndData().getInt("itemID");
        if (itemID != -1 && (spriteItem = ItemRegistry.getItem(itemID)) != null) {
            return new GameSprite(spriteItem.getItemSprite(item, player), 24);
        }
        return null;
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        boolean inverted = item.getGndData().getBoolean("inverted");
        if (inverted) {
            drawOptions.swingRotationInv(attackProgress);
        } else {
            drawOptions.swingRotation(attackProgress);
        }
    }

    public static InventoryItem setup(InventoryItem attackItem, InventoryItem swingItem, boolean inverted) {
        GNDItemMap gndData = attackItem.getGndData();
        if (swingItem == null) {
            gndData.setInt("itemID", -1);
        } else {
            gndData.setItem("invItem", (GNDItem)new GNDItemInventoryItem(swingItem));
        }
        gndData.setBoolean("inverted", inverted);
        return attackItem;
    }

    public static InventoryItem setup(InventoryItem attackItem, int itemID, boolean inverted) {
        GNDItemMap gndData = attackItem.getGndData();
        gndData.setInt("itemID", itemID);
        gndData.setBoolean("inverted", inverted);
        return attackItem;
    }

    public static InventoryItem setup(InventoryItem attackItem, Item swingItem, boolean inverted) {
        return SwingSpriteAttackItem.setup(attackItem, swingItem == null ? -1 : swingItem.getID(), inverted);
    }
}

