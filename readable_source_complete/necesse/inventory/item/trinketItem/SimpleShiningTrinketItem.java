/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.trinketItem;

import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.trinketItem.SimpleTrinketItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SimpleShiningTrinketItem
extends SimpleTrinketItem {
    protected float hue;
    protected boolean whileOnGround;
    protected boolean whileHolding;

    public SimpleShiningTrinketItem(Item.Rarity rarity, String[] buffStringIDs, int enchantCost, OneOfLootItems lootTableCategory) {
        super(rarity, buffStringIDs, enchantCost, lootTableCategory);
        this.setDefaults();
    }

    public SimpleShiningTrinketItem(Item.Rarity rarity, String buffStringID, int enchantCost, OneOfLootItems lootTableCategory) {
        super(rarity, buffStringID, enchantCost, lootTableCategory);
        this.setDefaults();
    }

    public SimpleShiningTrinketItem setLightLevel(float lightLevel) {
        this.worldDrawLight = new GameLight(lightLevel);
        return this;
    }

    public SimpleShiningTrinketItem setHue(float hue) {
        this.hue = hue;
        return this;
    }

    public SimpleShiningTrinketItem setEnableWhile(boolean whileOnGround, boolean whileHolding) {
        this.whileOnGround = whileOnGround;
        this.whileHolding = whileHolding;
        return this;
    }

    public void setDefaults() {
        this.hue = 50.0f;
        this.whileOnGround = true;
        this.whileHolding = true;
        this.worldDrawLight = new GameLight(120.0f);
    }

    @Override
    public void refreshLight(Level level, float x, float y, InventoryItem item, boolean isHolding) {
        if (!this.whileHolding && isHolding) {
            return;
        }
        if (!this.whileOnGround && !isHolding) {
            return;
        }
        level.lightManager.refreshParticleLightFloat(x, y, this.hue, 0.4f, 100);
    }
}

