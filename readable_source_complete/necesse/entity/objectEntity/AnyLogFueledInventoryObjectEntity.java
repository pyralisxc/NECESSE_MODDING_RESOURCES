/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import necesse.engine.registries.GlobalIngredientRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.entity.objectEntity.FueledInventoryObjectEntity;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public class AnyLogFueledInventoryObjectEntity
extends FueledInventoryObjectEntity {
    public AnyLogFueledInventoryObjectEntity(Level level, String type, int x, int y, boolean alwaysOn) {
        super(level, type, x, y, 2, alwaysOn);
    }

    @Override
    public boolean isValidFuelItem(int slot, InventoryItem item) {
        return item.item.isGlobalIngredient(GlobalIngredientRegistry.getGlobalIngredient("anylog"));
    }

    @Override
    public int getNextFuelBurnTime(boolean useFuel) {
        return this.itemToBurnTime(useFuel, item -> {
            if (item.item.isGlobalIngredient(GlobalIngredientRegistry.getGlobalIngredient("anylog"))) {
                return 120000;
            }
            return 0;
        });
    }

    @Override
    public boolean shouldPlayAmbientSound() {
        return this.isFueled();
    }

    @Override
    protected SoundSettings getAmbientSound() {
        return new SoundSettings(GameResources.campfireAmbient).volume(0.3f).pitchVariance(0.0f);
    }
}

