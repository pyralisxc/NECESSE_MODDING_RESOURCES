/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.regionSystem;

import java.awt.Point;
import necesse.entity.levelEvent.LevelEvent;
import necesse.level.maps.regionSystem.AllRegionPositionsTracker;

public class LevelEventRegionPositionsTracker
extends AllRegionPositionsTracker<LevelEvent> {
    public LevelEventRegionPositionsTracker(LevelEvent event) {
        super(event);
    }

    @Override
    public boolean isDisposed() {
        return ((LevelEvent)this.regionPositionGetter).isOver();
    }

    @Override
    public Point getSaveToRegionPos() {
        if (!((LevelEvent)this.regionPositionGetter).shouldSave()) {
            return null;
        }
        return ((LevelEvent)this.regionPositionGetter).getSaveToRegionPos();
    }
}

