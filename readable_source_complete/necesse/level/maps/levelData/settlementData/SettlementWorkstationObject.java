/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData;

import java.util.ArrayList;
import java.util.stream.Stream;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.inventory.recipe.Recipe;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementRequestOptions;

public interface SettlementWorkstationObject {
    public Stream<Recipe> streamSettlementRecipes(Level var1, int var2, int var3);

    default public boolean canCurrentlyCraft(Level level, int tileX, int tileY, Recipe recipe) {
        return true;
    }

    default public int getMaxCraftsAtOnce(Level level, int tileX, int tileY, Recipe recipe) {
        return 1;
    }

    default public void tickCrafting(Level level, int tileX, int tileY, Recipe recipe) {
    }

    default public void onCraftFinished(Level level, int tileX, int tileY, Recipe recipe) {
    }

    default public SettlementRequestOptions getFuelRequestOptions(Level level, int tileX, int tileY) {
        return null;
    }

    default public InventoryRange getFuelInventoryRange(Level level, int tileX, int tileY) {
        return null;
    }

    default public boolean isProcessingInventory(Level level, int tileX, int tileY) {
        return false;
    }

    default public InventoryRange getProcessingInputRange(Level level, int tileX, int tileY) {
        return null;
    }

    default public InventoryRange getProcessingOutputRange(Level level, int tileX, int tileY) {
        return null;
    }

    default public ArrayList<InventoryItem> getCurrentAndFutureProcessingOutputs(Level level, int tileX, int tileY) {
        return new ArrayList<InventoryItem>();
    }
}

