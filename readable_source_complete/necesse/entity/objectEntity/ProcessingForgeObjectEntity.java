/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import necesse.engine.registries.RecipeTechRegistry;
import necesse.entity.objectEntity.AnyLogFueledProcessingTechInventoryObjectEntity;
import necesse.inventory.InventoryItem;
import necesse.inventory.recipe.Recipe;
import necesse.level.maps.Level;

public class ProcessingForgeObjectEntity
extends AnyLogFueledProcessingTechInventoryObjectEntity {
    public static int logFuelTime = 40000;
    public static int recipeProcessTime = 8000;

    public ProcessingForgeObjectEntity(Level level, int x, int y) {
        super(level, "forge", x, y, 2, 2, false, false, true, RecipeTechRegistry.FORGE);
    }

    @Override
    public int getFuelTime(InventoryItem item) {
        return logFuelTime;
    }

    @Override
    public int getProcessTime(Recipe recipe) {
        return recipeProcessTime;
    }

    @Override
    public boolean shouldBeAbleToChangeKeepFuelRunning() {
        return false;
    }
}

