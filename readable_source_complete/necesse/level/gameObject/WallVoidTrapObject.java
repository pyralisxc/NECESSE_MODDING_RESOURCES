/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.VoidTrapObjectEntity;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.WallObject;
import necesse.level.gameObject.WallTrapObject;
import necesse.level.maps.Level;

public class WallVoidTrapObject
extends WallTrapObject {
    public WallVoidTrapObject(WallObject wallObject) {
        super(wallObject, "voidtrap");
    }

    public WallVoidTrapObject(WallObject wallObject, float toolTier, ToolType toolType) {
        super(wallObject, "voidtrap", toolTier, toolType);
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new VoidTrapObjectEntity(level, x, y);
    }
}

