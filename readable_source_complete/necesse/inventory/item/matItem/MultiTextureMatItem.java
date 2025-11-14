/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.matItem;

import java.util.List;
import java.util.Random;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTexture.GameSprite;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryAddConsumer;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.matItem.MatItem;
import necesse.level.maps.Level;

public class MultiTextureMatItem
extends MatItem {
    private int spriteCount;

    public static void setCurrentSprite(GNDItemMap gndData, int sprite) {
        gndData.setByte("currentSprite", (byte)sprite);
    }

    public static GNDItemMap getGNDData(int sprite) {
        GNDItemMap gndData = new GNDItemMap();
        MultiTextureMatItem.setCurrentSprite(gndData, sprite);
        return gndData;
    }

    public static InventoryItem generateItem(String itemStringID, int sprite) {
        InventoryItem inventoryItem = new InventoryItem(itemStringID);
        if (inventoryItem.item instanceof MultiTextureMatItem) {
            MultiTextureMatItem item = (MultiTextureMatItem)inventoryItem.item;
            item.setCurrentSprite(inventoryItem, sprite);
        }
        return inventoryItem;
    }

    public static InventoryItem generateRandomItem(String itemStringID, Random random) {
        return MultiTextureMatItem.generateItem(itemStringID, random.nextInt());
    }

    public MultiTextureMatItem(int spriteCount, int stackSize, String ... globalIngredients) {
        super(stackSize, globalIngredients);
        this.spriteCount = spriteCount;
    }

    public MultiTextureMatItem(int spriteCount, int stackSize, Item.Rarity rarity, String ... globalIngredients) {
        super(stackSize, rarity, globalIngredients);
        this.spriteCount = spriteCount;
    }

    public MultiTextureMatItem(int spriteCount, int stackSize, Item.Rarity rarity, String tooltipKey) {
        super(stackSize, rarity, tooltipKey);
        this.spriteCount = spriteCount;
    }

    public MultiTextureMatItem(int spriteCount, int stackSize, Item.Rarity rarity, String tooltipKey, String ... globalIngredients) {
        super(stackSize, rarity, tooltipKey, globalIngredients);
        this.spriteCount = spriteCount;
    }

    public void setCurrentSprite(InventoryItem item, int sprite) {
        MultiTextureMatItem.setCurrentSprite(item.getGndData(), sprite);
    }

    @Override
    public GameSprite getItemSprite(InventoryItem item, PlayerMob perspective) {
        int currentSprite = (item.getGndData().getByte("currentSprite") & 0xFF) % this.spriteCount;
        return new GameSprite(this.itemTexture, 0, currentSprite, 32);
    }

    @Override
    public boolean onCombine(Level level, PlayerMob player, Inventory myInventory, int mySlot, InventoryItem me, InventoryItem other, int maxStackSize, int amount, boolean combineIsNew, String purpose, InventoryAddConsumer addConsumer) {
        if (super.onCombine(level, player, myInventory, mySlot, me, other, maxStackSize, amount, combineIsNew, purpose, addConsumer)) {
            me.getGndData().setByte("currentSprite", other.getGndData().getByte("currentSprite"));
            return true;
        }
        return false;
    }

    @Override
    public void addDefaultItems(List<InventoryItem> list, PlayerMob player) {
        for (int i = 0; i < this.spriteCount; ++i) {
            InventoryItem item = this.getDefaultItem(player, 1);
            item.getGndData().setByte("currentSprite", (byte)i);
            list.add(item);
        }
    }
}

