/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import necesse.engine.world.WorldEntity;
import necesse.entity.Entity;

public class GameObjectReservable {
    private long reserveTick;
    private Object reserveWorker;

    public void reserve(Object worker, WorldEntity worldEntity) {
        this.reserveWorker = worker;
        this.reserveTick = worldEntity.getGameTicks();
    }

    public void reserve(Entity entity) {
        this.reserve(entity, entity.getWorldEntity());
    }

    public boolean isAvailable(Object worker, WorldEntity worldEntity) {
        return this.reserveWorker == worker || this.reserveTick < worldEntity.getGameTicks() - 2L;
    }

    public final boolean isAvailable(Entity entity) {
        return this.isAvailable(entity, entity.getWorldEntity());
    }

    public void printReserveWorker() {
        System.out.println(this.reserveWorker);
    }
}

