/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.save.levelData;

import java.util.ArrayList;
import java.util.List;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.save.SaveSyntaxException;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.RecipeData;

public class RecipeSave {
    public static Recipe loadSave(LoadData save) {
        try {
            RecipeData recipeData = new RecipeData(save);
            try {
                return recipeData.validate();
            }
            catch (Exception e) {
                String eMsg = e.getMessage();
                System.err.println("Could not load recipe for '" + recipeData.resultID + "'" + (eMsg == null || eMsg.length() == 0 ? "" : " - " + eMsg));
            }
        }
        catch (SaveSyntaxException e) {
            System.err.println("Syntax error on recipe load:");
            System.err.println(e.getMessage());
        }
        catch (Exception e) {
            System.err.println("Unknown error in loading recipe");
            e.printStackTrace();
        }
        return null;
    }

    public static SaveData getSave(Recipe r) {
        SaveData save = new SaveData("");
        new RecipeData(r).addSaveData(save);
        return save;
    }

    public static void putRecipeSave(SaveData save, Iterable<Recipe> recipes) {
        for (Recipe r : recipes) {
            save.addSaveData(RecipeSave.getSave(r));
        }
    }

    public static List<Recipe> loadRecipesSave(LoadData save) {
        ArrayList<Recipe> recipes = new ArrayList<Recipe>();
        for (int i = 0; i < save.getLoadData().size(); ++i) {
            LoadData recipeSave = save.getLoadData().get(i);
            Recipe r = RecipeSave.loadSave(recipeSave);
            if (r == null) continue;
            recipes.add(r);
        }
        return recipes;
    }
}

