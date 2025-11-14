/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.furniture;

import java.awt.Rectangle;
import necesse.entity.objectEntity.BedObjectEntity;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;

public interface SettlerBedObject {
    default public BedObjectEntity getBedObjectEntity(Level level, int tileX, int tileY) {
        LevelObject levelObject = this.getSettlerBedMasterLevelObject(level, tileX, tileY);
        return levelObject.object.getCurrentObjectEntity(level, tileX, tileY, BedObjectEntity.class);
    }

    public boolean isMasterBedObject(Level var1, int var2, int var3);

    public LevelObject getSettlerBedMasterLevelObject(Level var1, int var2, int var3);

    public Rectangle getSettlerBedTileRectangle(Level var1, int var2, int var3);
}

