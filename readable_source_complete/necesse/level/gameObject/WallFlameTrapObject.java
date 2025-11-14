/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import necesse.entity.objectEntity.FlameTrapObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.WallObject;
import necesse.level.gameObject.WallTrapObject;
import necesse.level.maps.Level;

public class WallFlameTrapObject
extends WallTrapObject {
    public WallFlameTrapObject(WallObject wallObject) {
        super(wallObject, "flametrap");
    }

    public WallFlameTrapObject(WallObject wallObject, float toolTier, ToolType toolType) {
        super(wallObject, "flametrap", toolTier, toolType);
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new FlameTrapObjectEntity(level, x, y);
    }
}

