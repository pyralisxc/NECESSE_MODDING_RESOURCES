/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import necesse.entity.objectEntity.InventoryObjectEntity;
import necesse.level.maps.Level;

public class DisplayStandObjectEntity
extends InventoryObjectEntity {
    public DisplayStandObjectEntity(Level level, int x, int y) {
        super(level, x, y, 1);
    }

    @Override
    public boolean canQuickStackInventory() {
        return false;
    }

    @Override
    public boolean canRestockInventory() {
        return false;
    }

    @Override
    public boolean canSortInventory() {
        return false;
    }

    @Override
    public boolean canUseForNearbyCrafting() {
        return false;
    }
}

