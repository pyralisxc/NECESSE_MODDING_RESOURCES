/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import java.awt.Point;
import necesse.entity.levelEvent.FlameTrapEvent;
import necesse.entity.objectEntity.TrapObjectEntity;
import necesse.level.maps.Level;

public class FlameTrapObjectEntity
extends TrapObjectEntity {
    public FlameTrapObjectEntity(Level level, int x, int y) {
        super(level, x, y, 1000L);
        this.shouldSave = false;
    }

    @Override
    public void triggerTrap(int wireID, int dir) {
        if (this.isClient() || this.onCooldown()) {
            return;
        }
        if (this.otherWireActive(wireID)) {
            return;
        }
        Point position = this.getPos(this.tileX, this.tileY, dir);
        FlameTrapEvent e = new FlameTrapEvent(position.x, position.y, dir);
        this.getLevel().entityManager.events.add(e);
        this.startCooldown();
    }
}

