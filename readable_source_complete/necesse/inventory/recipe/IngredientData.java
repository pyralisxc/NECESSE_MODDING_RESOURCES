/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.recipe;

import java.util.Iterator;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.save.SaveSyntaxException;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.RecipeData;

public class IngredientData {
    public final String ingredientStringID;
    public final int itemAmount;
    public final boolean requiredToShow;

    public IngredientData(String ingredientStringID, int itemAmount, boolean requiredToShow) {
        this.ingredientStringID = ingredientStringID;
        this.itemAmount = itemAmount;
        this.requiredToShow = requiredToShow;
    }

    public IngredientData(Ingredient ingredient) {
        this.ingredientStringID = ingredient.ingredientStringID;
        this.itemAmount = ingredient.getIngredientAmount();
        this.requiredToShow = ingredient.requiredToShow();
    }

    public IngredientData(LoadData save, RecipeData recipeData) throws SaveSyntaxException {
        LoadData data;
        Iterator<LoadData> it = save.getLoadData().iterator();
        if (it.hasNext()) {
            data = it.next();
            if (!data.isData()) {
                throw new SaveSyntaxException("Recipe for '" + (recipeData == null ? "null" : recipeData.resultID) + "' ingredient must only have data components");
            }
        } else {
            throw new SaveSyntaxException("Recipe for '" + (recipeData == null ? "null" : recipeData.resultID) + "' ingredient needs at least one data component");
        }
        this.ingredientStringID = LoadData.getUnsafeString(data);
        if (it.hasNext()) {
            data = it.next();
            if (!data.isData()) {
                throw new SaveSyntaxException("Recipe for '" + (recipeData == null ? "null" : recipeData.resultID) + "' ingredient '" + this.ingredientStringID + "' must only have data components");
            }
            try {
                this.itemAmount = LoadData.getInt(data);
            }
            catch (NumberFormatException e) {
                throw new SaveSyntaxException("Recipe for '" + (recipeData == null ? "null" : recipeData.resultID) + "' ingredient '" + this.ingredientStringID + "' amount must be a number");
            }
        } else {
            this.itemAmount = 1;
        }
        if (it.hasNext()) {
            data = it.next();
            if (!data.isData()) {
                throw new SaveSyntaxException("Recipe for '" + (recipeData == null ? "null" : recipeData.resultID) + "' ingredient '" + this.ingredientStringID + "' must only have data components");
            }
            this.requiredToShow = LoadData.getBoolean(data);
        } else {
            this.requiredToShow = false;
        }
    }

    public void addSaveData(SaveData save) {
        save.addUnsafeString("", this.ingredientStringID);
        if (this.itemAmount != 1 || this.requiredToShow) {
            save.addInt("", this.itemAmount);
        }
        if (this.requiredToShow) {
            save.addBoolean("", true);
        }
    }

    public Ingredient validate() {
        return new Ingredient(this.ingredientStringID, this.itemAmount, this.requiredToShow);
    }
}

