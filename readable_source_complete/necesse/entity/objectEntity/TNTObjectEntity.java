/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import necesse.entity.levelEvent.explosionEvent.TNTExplosionEvent;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.level.maps.Level;

public class TNTObjectEntity
extends ObjectEntity {
    public boolean exploded;

    public TNTObjectEntity(Level level, int x, int y) {
        super(level, "tnt", x, y);
        this.shouldSave = false;
        this.exploded = false;
    }

    @Override
    public boolean shouldRequestPacket() {
        return false;
    }

    public void explode() {
        if (this.exploded || this.isClient()) {
            return;
        }
        TNTExplosionEvent event = new TNTExplosionEvent(this.tileX, this.tileY);
        this.getLevel().entityManager.events.add(event);
        this.exploded = true;
    }
}

