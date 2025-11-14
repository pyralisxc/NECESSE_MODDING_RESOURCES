/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.recipe;

import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemByte;
import necesse.engine.network.gameNetworkData.GNDItemDouble;
import necesse.engine.network.gameNetworkData.GNDItemInt;
import necesse.engine.network.gameNetworkData.GNDItemLong;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.gameNetworkData.GNDItemShort;
import necesse.engine.network.gameNetworkData.GNDItemString;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveComponent;
import necesse.engine.save.SaveData;
import necesse.engine.save.SaveSyntaxException;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.IngredientData;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Tech;

public class RecipeData {
    public final String resultID;
    public final int resultAmount;
    public final IngredientData[] ingredients;
    public final String tech;
    public final boolean isHidden;
    public final GNDItemMap gndData;
    protected String sortResultID;
    protected boolean sortBefore;

    public RecipeData(String resultID, int resultAmount, IngredientData[] ingredients, String tech, boolean isHidden, GNDItemMap gndData) {
        this.resultID = resultID;
        this.resultAmount = resultAmount;
        this.ingredients = ingredients;
        this.tech = tech;
        this.isHidden = isHidden;
        this.gndData = gndData;
    }

    public RecipeData(Recipe recipe) {
        this.resultID = recipe.resultStringID;
        this.resultAmount = recipe.resultAmount;
        this.ingredients = new IngredientData[recipe.ingredients.length];
        for (int i = 0; i < this.ingredients.length; ++i) {
            this.ingredients[i] = new IngredientData(recipe.ingredients[i]);
        }
        this.tech = recipe.tech.getStringID();
        this.isHidden = recipe.isHidden;
        this.gndData = recipe.getGndData().copy();
        this.sortResultID = recipe.sortResultID;
        this.sortBefore = recipe.sortBefore;
    }

    public RecipeData(LoadData save) throws SaveSyntaxException {
        LoadData data;
        LoadData data2;
        ListIterator<LoadData> it = save.getLoadData().listIterator();
        if (it.hasNext()) {
            data2 = it.next();
            if (!data2.isData()) {
                throw new SaveSyntaxException("Recipe resultName must be a data component");
            }
        } else {
            throw new SaveSyntaxException("Missing recipe resultName");
        }
        this.resultID = LoadData.getUnsafeString(data2);
        if (it.hasNext()) {
            data2 = it.next();
            if (!data2.isData()) {
                throw new SaveSyntaxException("Recipe for '" + this.resultID + "' resultAmount must be a data component");
            }
            try {
                this.resultAmount = LoadData.getInt(data2);
            }
            catch (NumberFormatException e) {
                throw new SaveSyntaxException("Recipe resultAmount must be a number");
            }
        } else {
            throw new SaveSyntaxException("Missing recipe resultAmount for '" + this.resultID + "'");
        }
        if (it.hasNext()) {
            data2 = it.next();
            if (!data2.isData()) {
                throw new SaveSyntaxException("Recipe for '" + this.resultID + "' tech must be a data component");
            }
        } else {
            throw new SaveSyntaxException("Missing recipe tech for '" + this.resultID + "'");
        }
        this.tech = LoadData.getUnsafeString(data2);
        if (it.hasNext()) {
            data2 = it.next();
            if (!data2.isArray()) {
                throw new SaveSyntaxException("Recipe for '" + this.resultID + "' ingredients must be an array component");
            }
            List<LoadData> ingredientsSave = data2.getLoadData();
            this.ingredients = new IngredientData[ingredientsSave.size()];
            for (int i = 0; i < this.ingredients.length; ++i) {
                this.ingredients[i] = new IngredientData(ingredientsSave.get(i), this);
            }
        } else {
            throw new SaveSyntaxException("Missing recipe ingredients for '" + this.resultID + "'");
        }
        LoadData prev = null;
        if (it.hasNext()) {
            data = it.next();
            prev = null;
            if (data.isData()) {
                String s = LoadData.getUnsafeString(data);
                if (s.equals("true")) {
                    this.isHidden = true;
                } else if (s.equals("false")) {
                    this.isHidden = false;
                } else {
                    this.isHidden = false;
                    prev = data;
                }
            } else {
                this.isHidden = false;
                prev = data;
            }
        } else {
            this.isHidden = false;
        }
        if (prev != null || it.hasNext()) {
            data = prev != null ? prev : it.next();
            prev = null;
            if (data.isData()) {
                String s = LoadData.getUnsafeString(data);
                if (s.startsWith("before:")) {
                    this.sortResultID = s.substring("before:".length());
                    this.sortBefore = true;
                } else if (s.startsWith("after:")) {
                    this.sortResultID = s.substring("after:".length());
                    this.sortBefore = false;
                } else {
                    this.sortResultID = null;
                    prev = data;
                }
            }
        } else {
            this.sortResultID = null;
        }
        if (prev != null || it.hasNext()) {
            data = prev != null ? prev : it.next();
            prev = null;
            if (!data.isArray()) {
                throw new SaveSyntaxException("Recipe for '" + this.resultID + "' gndData must be a array component");
            }
            this.gndData = RecipeData.getRecipeGNDData(this.resultID, data);
        } else {
            this.gndData = new GNDItemMap();
        }
    }

    private static GNDItemMap getRecipeGNDData(String resultID, LoadData save) throws SaveSyntaxException {
        GNDItemMap gndMap = new GNDItemMap();
        for (LoadData data : save.getLoadData()) {
            String dataKey = data.getName();
            if (data.isData()) {
                String dataString = LoadData.getUnsafeString(data);
                if (dataString.startsWith("\"")) {
                    gndMap.setItem(dataKey, (GNDItem)new GNDItemString(SaveComponent.fromSafeData(dataString)));
                    continue;
                }
                if (dataString.contains(".")) {
                    gndMap.setItem(dataKey, (GNDItem)new GNDItemDouble(Double.parseDouble(dataString)));
                    continue;
                }
                try {
                    long longVal = LoadData.getLong(data);
                    GNDItem.GNDPrimitive gndVal = new GNDItemLong(longVal);
                    if (longVal >= -128L && longVal <= 127L) {
                        gndVal = new GNDItemByte((byte)longVal);
                    } else if (longVal >= -32768L && longVal <= 32767L) {
                        gndVal = new GNDItemShort((short)longVal);
                    } else if (longVal >= Integer.MIN_VALUE && longVal <= Integer.MAX_VALUE) {
                        gndVal = new GNDItemInt((int)longVal);
                    }
                    gndMap.setItem(dataKey, (GNDItem)gndVal);
                }
                catch (NumberFormatException e) {
                    gndMap.setString(dataKey, dataString);
                }
                continue;
            }
            if (data.isArray()) {
                gndMap.setItem(dataKey, (GNDItem)RecipeData.getRecipeGNDData(resultID, data));
                continue;
            }
            throw new SaveSyntaxException("Recipe for " + resultID + " gnd had unknown data type");
        }
        return gndMap;
    }

    public void addSaveData(SaveData save) {
        save.addUnsafeString("", this.resultID);
        save.addInt("", this.resultAmount);
        save.addUnsafeString("", this.tech);
        SaveData ingredientsData = new SaveData("");
        for (IngredientData in : this.ingredients) {
            SaveData inSave = new SaveData("");
            in.addSaveData(inSave);
            ingredientsData.addSaveData(inSave);
        }
        save.addSaveData(ingredientsData);
        if (this.isHidden) {
            save.addUnsafeString("", "true");
        }
        if (this.sortResultID != null) {
            if (this.sortBefore) {
                save.addUnsafeString("", "before:" + this.sortResultID);
            } else {
                save.addUnsafeString("", "after:" + this.sortResultID);
            }
        }
        if (this.gndData.getMapSize() != 0) {
            SaveData gndSave = new SaveData("GND");
            for (String key : this.gndData.getKeyStringSet()) {
                GNDItem data = this.gndData.getItem(key);
                if (data instanceof GNDItemString) {
                    gndSave.addSafeString(key, data.toString());
                    continue;
                }
                gndSave.addUnsafeString(key, data.toString());
            }
            save.addSaveData(gndSave);
        }
    }

    public Recipe validate() {
        Ingredient[] ingredients = new Ingredient[this.ingredients.length];
        for (int i = 0; i < ingredients.length; ++i) {
            ingredients[i] = this.ingredients[i].validate();
        }
        try {
            Tech tech = RecipeTechRegistry.getTech(this.tech);
            Recipe recipe = new Recipe(this.resultID, this.resultAmount, tech, ingredients, this.isHidden, this.gndData);
            if (this.sortResultID != null) {
                if (this.sortBefore) {
                    recipe.showBefore(this.sortResultID);
                } else {
                    recipe.showAfter(this.sortResultID);
                }
            }
            return recipe;
        }
        catch (NoSuchElementException e) {
            throw new SaveSyntaxException("Could not find tech with name " + this.tech);
        }
    }
}

