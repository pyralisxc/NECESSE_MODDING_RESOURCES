/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Rectangle;
import necesse.engine.registries.ObjectLayerRegistry;
import necesse.inventory.lootTable.LootTable;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;

public class AirObject
extends GameObject {
    public AirObject() {
        super(new Rectangle(0, 0));
        this.isLightTransparent = true;
        for (int layer = 0; layer < ObjectLayerRegistry.getTotalLayers(); ++layer) {
            this.validObjectLayers.add(layer);
        }
    }

    @Override
    public boolean canBePlacedOn(Level level, int layerID, int x, int y, GameObject newObject, boolean ignoreOtherLayers) {
        return true;
    }

    @Override
    public void onPlacedOn(Level level, int layerID, int x, int y, GameObject newObject) {
    }

    @Override
    public String canPlace(Level level, int layerID, int x, int y, int rotation, boolean byPlayer, boolean ignoreOtherLayers) {
        return null;
    }

    @Override
    public boolean isValid(Level level, int layerID, int x, int y) {
        return true;
    }

    @Override
    public boolean shouldShowInItemList() {
        return false;
    }

    @Override
    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        return new LootTable();
    }
}

