/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.SawTrapObjectEntity;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.WallObject;
import necesse.level.gameObject.WallTrapObject;
import necesse.level.maps.Level;

public class WallSawTrapObject
extends WallTrapObject {
    public WallSawTrapObject(WallObject wallObject) {
        super(wallObject, "sawtrap");
    }

    public WallSawTrapObject(WallObject wallObject, float toolTier, ToolType toolType) {
        super(wallObject, "sawtrap", toolTier, toolType);
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new SawTrapObjectEntity(level, x, y);
    }
}

