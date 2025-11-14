/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import necesse.engine.registries.MobRegistry;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.level.maps.Level;

public class BeetCaveCropplerObjectEntity
extends ObjectEntity {
    public BeetCaveCropplerObjectEntity(Level level, int x, int y) {
        super(level, "beetcavecroppler", x, y);
    }

    public void spawnContainedMob(Level level) {
        level.entityManager.addMob(MobRegistry.getMob("beetcavecroppler", level), this.tileX * 32 + 16, this.tileY * 32 + 16);
    }
}

