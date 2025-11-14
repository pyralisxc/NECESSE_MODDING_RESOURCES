/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import necesse.entity.levelEvent.SpikeTrapEvent;
import necesse.entity.objectEntity.TrapObjectEntity;
import necesse.level.maps.Level;

public class SpikeTrapObjectEntity
extends TrapObjectEntity {
    public SpikeTrapObjectEntity(Level level, int x, int y, int animationDuration) {
        super(level, x, y, (long)animationDuration);
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
        SpikeTrapEvent e = new SpikeTrapEvent(this.tileX, this.tileY);
        this.getLevel().entityManager.events.add(e);
        this.sendClientTriggerPacket();
        this.startCooldown();
    }

    @Override
    public void onClientTrigger() {
        super.onClientTrigger();
        this.startCooldown();
    }
}

