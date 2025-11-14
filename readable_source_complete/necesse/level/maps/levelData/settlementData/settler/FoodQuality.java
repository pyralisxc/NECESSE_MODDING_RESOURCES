/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.settler;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.friendly.human.HappinessModifier;
import necesse.inventory.item.ItemCategory;

public class FoodQuality {
    public final GameMessage displayName;
    public final int happinessIncrease;
    public final String[] masterCategoryTree;
    public final ItemCategory foodCategory;
    public final ItemCategory craftingCategory;
    public static HappinessModifier noFoodModifier = new HappinessModifier(0, new LocalMessage("settlement", "nofoodmood"));

    public FoodQuality(GameMessage displayName, int happinessIncrease, String categorySortString, String ... categoryTree) {
        this.displayName = displayName;
        this.happinessIncrease = happinessIncrease;
        this.masterCategoryTree = GameUtils.concat(new String[]{"consumable", "food"}, categoryTree);
        ItemCategory.createCategory(categorySortString, displayName, this.masterCategoryTree);
        this.foodCategory = ItemCategory.foodQualityManager.createCategory(categorySortString, displayName, categoryTree);
        this.craftingCategory = ItemCategory.craftingManager.createCategory(categorySortString, displayName, GameUtils.concat(new String[]{"consumable"}, categoryTree));
    }

    public HappinessModifier getModifier() {
        LocalMessage description = new LocalMessage("settlement", "foodmood").addReplacement("quality", this.displayName);
        return new HappinessModifier(this.happinessIncrease, description);
    }
}

