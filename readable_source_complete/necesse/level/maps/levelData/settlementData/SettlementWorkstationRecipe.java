/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData;

import java.util.Arrays;
import java.util.function.Function;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.settlement.events.SettlementWorkstationRecipeUpdateEvent;
import necesse.inventory.item.Item;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;
import necesse.level.maps.levelData.settlementData.SettlementWorkstation;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationLevelObject;

public class SettlementWorkstationRecipe {
    public static int INITIAL_COUNT_DIVISOR = 10;
    public static int[] INITIAL_COUNTS = new int[]{10, 50, 100};
    public final int uniqueID;
    public String name;
    public Recipe recipe;
    public ItemCategoriesFilter ingredientFilter;
    public Mode mode;
    public int modeCount;

    public static int getClosestInitialCount(int count) {
        for (int i = INITIAL_COUNTS.length - 1; i >= 0; --i) {
            int nextCount = INITIAL_COUNTS[i];
            if (count < nextCount) continue;
            return nextCount;
        }
        return INITIAL_COUNTS[0];
    }

    public SettlementWorkstationRecipe(int uniqueID, Recipe recipe) {
        this.uniqueID = uniqueID;
        this.name = null;
        this.recipe = recipe;
        this.refreshIngredientFilter();
        this.mode = Mode.DO_UNTIL;
        this.modeCount = recipe.resultItem.item.getInitialSettlementRecipeCount();
        if (this.modeCount == -1) {
            int stackSize = recipe.resultItem.itemStackSize();
            this.modeCount = Math.min(SettlementWorkstationRecipe.getClosestInitialCount(stackSize / INITIAL_COUNT_DIVISOR), stackSize);
        }
    }

    private void refreshIngredientFilter() {
        this.ingredientFilter = new ItemCategoriesFilter(true){

            @Override
            public boolean isItemDisabled(Item item) {
                if (super.isItemDisabled(item)) {
                    return true;
                }
                Recipe r = SettlementWorkstationRecipe.this.recipe;
                for (Ingredient ingredient : r.ingredients) {
                    if (!ingredient.isGlobalIngredient() || !ingredient.matchesItem(item)) continue;
                    return false;
                }
                return true;
            }
        };
    }

    public void addSaveData(SaveData save, boolean includeUniqueID) {
        if (includeUniqueID) {
            save.addInt("uniqueID", this.uniqueID);
        }
        if (this.name != null) {
            save.addSafeString("name", this.name);
        }
        save.addInt("recipeHash", this.recipe.getRecipeHash());
        if (!this.ingredientFilter.master.isAllAllowed()) {
            SaveData filterData = new SaveData("ingredientFilter");
            this.ingredientFilter.addSaveData(filterData);
            save.addSaveData(filterData);
        }
        save.addEnum("mode", this.mode);
        save.addInt("modeCount", this.modeCount);
    }

    public SettlementWorkstationRecipe(LoadData save, boolean includeUniqueID) throws LoadDataException {
        if (includeUniqueID) {
            this.uniqueID = save.getInt("uniqueID", -1);
            if (this.uniqueID == -1) {
                throw new LoadDataException("Missing recipe uniqueID");
            }
        } else {
            this.uniqueID = -1;
        }
        this.name = save.getSafeString("name", null, false);
        int recipeHash = save.getInt("recipeHash", -1);
        if (recipeHash == -1) {
            throw new LoadDataException("Missing recipe hash");
        }
        this.recipe = this.findRecipe(recipeHash);
        if (this.recipe == null) {
            throw new LoadDataException("Could not find recipe with hash " + recipeHash);
        }
        this.refreshIngredientFilter();
        LoadData filterData = save.getFirstLoadDataByName("ingredientFilter");
        if (filterData != null && filterData.isArray()) {
            this.ingredientFilter.applyLoadData(filterData);
        }
        this.mode = save.getEnum(Mode.class, "mode", Mode.DO_COUNT, false);
        this.modeCount = save.getInt("modeCount", 1, 0, 65535, false);
    }

    public void writePacket(PacketWriter writer) {
        writer.putNextBoolean(this.name != null);
        if (this.name != null) {
            writer.putNextString(this.name);
        }
        writer.putNextInt(this.recipe.getRecipeHash());
        this.ingredientFilter.writePacket(writer);
        writer.putNextByteUnsigned(this.mode.ordinal());
        writer.putNextShortUnsigned(this.modeCount);
    }

    public SettlementWorkstationRecipe(int uniqueID, PacketReader reader) throws LoadDataException {
        this.uniqueID = uniqueID;
        this.applyPacket(reader);
    }

    public void applyPacket(PacketReader reader) throws LoadDataException {
        this.name = reader.getNextBoolean() ? reader.getNextString() : null;
        int recipeHash = reader.getNextInt();
        this.recipe = this.findRecipe(recipeHash);
        if (this.recipe == null) {
            throw new LoadDataException("Could not find recipe with hash " + recipeHash);
        }
        this.refreshIngredientFilter();
        this.ingredientFilter.readPacket(reader);
        this.mode = Mode.values()[reader.getNextByteUnsigned()];
        this.modeCount = reader.getNextShortUnsigned();
    }

    private Recipe findRecipe(int recipeHash) {
        for (Recipe recipe : Recipes.getRecipes()) {
            if (recipe.getRecipeHash() != recipeHash) continue;
            return recipe;
        }
        return null;
    }

    public boolean canConfigureIngredientFilter() {
        return Arrays.stream(this.recipe.ingredients).anyMatch(Ingredient::isGlobalIngredient);
    }

    public boolean canUseItem(Ingredient ingredient, InventoryItem item) {
        if (!ingredient.matchesItem(item.item)) {
            return false;
        }
        if (ingredient.isGlobalIngredient()) {
            return this.ingredientFilter.isItemAllowed(item.item);
        }
        return true;
    }

    public void onCrafted(SettlementWorkstation workstation, int count) {
        SettlementWorkstationLevelObject workstationObject;
        if (this.mode == Mode.DO_COUNT) {
            this.modeCount = Math.max(0, this.modeCount - count);
        }
        if ((workstationObject = workstation.getWorkstationObject()) != null) {
            workstationObject.onCraftFinished(this.recipe);
        }
        new SettlementWorkstationRecipeUpdateEvent(workstation.data, workstation.tileX, workstation.tileY, this).applyAndSendToClientsAt(workstation.data.getLevel());
    }

    public static enum Mode {
        DO_COUNT(count -> new LocalMessage("ui", "conditiondocount", "count", (String)count)),
        DO_UNTIL(count -> new LocalMessage("ui", "conditiondountil", "count", (String)count)),
        DO_FOREVER(count -> new LocalMessage("ui", "conditiondoforever", "count", (String)count));

        public final Function<String, GameMessage> countMessageFunction;

        private Mode(Function<String, GameMessage> countMessageFunction) {
            this.countMessageFunction = countMessageFunction;
        }
    }
}

