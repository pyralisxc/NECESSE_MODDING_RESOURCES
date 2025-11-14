/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import necesse.entity.objectEntity.ArrowTrapObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.WallObject;
import necesse.level.gameObject.WallTrapObject;
import necesse.level.maps.Level;

public class WallArrowTrapObject
extends WallTrapObject {
    public WallArrowTrapObject(WallObject wallObject) {
        super(wallObject, "arrowtrap");
    }

    public WallArrowTrapObject(WallObject wallObject, float toolTier, ToolType toolType) {
        super(wallObject, "arrowtrap", toolTier, toolType);
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new ArrowTrapObjectEntity(level, x, y);
    }
}

