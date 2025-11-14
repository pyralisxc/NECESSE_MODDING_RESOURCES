/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem.food;

import necesse.engine.localization.Localization;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.Item;
import necesse.inventory.item.matItem.MatItem;

public class FoodMatItem
extends MatItem {
    protected String cropTextureName;

    public FoodMatItem(int stackSize, String ... globalIngredients) {
        super(stackSize, globalIngredients);
        this.setItemCategory("consumable", "rawfood");
        this.dropDecayTimeMillis = 1800000L;
    }

    public FoodMatItem(int stackSize, Item.Rarity rarity, String ... globalIngredients) {
        super(stackSize, rarity, globalIngredients);
        this.setItemCategory("consumable", "rawfood");
        this.dropDecayTimeMillis = 1800000L;
    }

    public FoodMatItem(int stackSize, Item.Rarity rarity, String tooltipKey) {
        super(stackSize, rarity, tooltipKey);
        this.dropDecayTimeMillis = 1800000L;
    }

    public FoodMatItem(int stackSize, Item.Rarity rarity, String tooltipKey, String ... globalIngredients) {
        super(stackSize, rarity, tooltipKey, globalIngredients);
        this.dropDecayTimeMillis = 1800000L;
    }

    public FoodMatItem cropTexture(String textureName) {
        this.cropTextureName = textureName;
        return this;
    }

    @Override
    protected void loadItemTextures() {
        if (this.cropTextureName != null) {
            this.itemTexture = new GameTexture(GameTexture.fromFile("objects/" + this.cropTextureName), 0, 0, 32);
        } else {
            super.loadItemTextures();
        }
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("itemcategory", "ingredient");
    }
}

