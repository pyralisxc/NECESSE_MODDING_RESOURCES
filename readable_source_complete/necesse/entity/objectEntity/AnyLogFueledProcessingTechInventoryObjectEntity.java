/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import necesse.engine.registries.GlobalIngredientRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.entity.objectEntity.FueledProcessingTechInventoryObjectEntity;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.recipe.Tech;
import necesse.level.maps.Level;

public abstract class AnyLogFueledProcessingTechInventoryObjectEntity
extends FueledProcessingTechInventoryObjectEntity {
    public AnyLogFueledProcessingTechInventoryObjectEntity(Level level, String type, int x, int y, int inputSlots, int outputSlots, boolean fuelAlwaysOn, boolean fuelRunsOutWhenNotProcessing, boolean runningOutOfFuelResetsProcessingTime, Tech ... techs) {
        super(level, type, x, y, 2, inputSlots, outputSlots, fuelAlwaysOn, fuelRunsOutWhenNotProcessing, runningOutOfFuelResetsProcessingTime, techs);
    }

    @Override
    public boolean isValidFuelItem(InventoryItem item) {
        return item.item.isGlobalIngredient(GlobalIngredientRegistry.getGlobalIngredient("anylog"));
    }

    @Override
    public int getNextFuelBurnTime(boolean useFuel) {
        return this.itemToBurnTime(useFuel, item -> {
            if (item.item.isGlobalIngredient(GlobalIngredientRegistry.getGlobalIngredient("anylog"))) {
                return this.getFuelTime((InventoryItem)item);
            }
            return 0;
        });
    }

    public abstract int getFuelTime(InventoryItem var1);

    @Override
    public boolean shouldPlayAmbientSound() {
        return this.isFuelRunning();
    }

    @Override
    protected SoundSettings getAmbientSound() {
        return new SoundSettings(GameResources.campfireAmbient).volume(0.3f);
    }
}

