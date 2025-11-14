/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world;

import necesse.engine.world.GameClock;
import necesse.engine.world.WorldEntity;

public interface WorldEntityGameClock
extends GameClock {
    public WorldEntity getWorldEntity();

    @Override
    default public long getTime() {
        WorldEntity worldEntity = this.getWorldEntity();
        return worldEntity == null ? 0L : worldEntity.getTime();
    }

    @Override
    default public long getWorldTime() {
        WorldEntity worldEntity = this.getWorldEntity();
        return worldEntity == null ? 0L : worldEntity.getWorldTime();
    }

    @Override
    default public long getLocalTime() {
        WorldEntity worldEntity = this.getWorldEntity();
        return worldEntity == null ? 0L : worldEntity.getLocalTime();
    }
}

