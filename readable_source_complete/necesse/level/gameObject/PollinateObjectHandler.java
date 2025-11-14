/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import necesse.engine.util.GameObjectReservable;

public abstract class PollinateObjectHandler {
    public int tileX;
    public int tileY;
    public GameObjectReservable reservable;

    public PollinateObjectHandler(int tileX, int tileY, GameObjectReservable reservable) {
        this.tileX = tileX;
        this.tileY = tileY;
        this.reservable = reservable;
    }

    public abstract boolean canPollinate();

    public abstract void pollinate();

    public abstract boolean isValid();
}

