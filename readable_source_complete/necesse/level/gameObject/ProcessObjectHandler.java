/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import necesse.engine.util.GameObjectReservable;

public abstract class ProcessObjectHandler {
    public int tileX;
    public int tileY;
    public GameObjectReservable reservable;

    public ProcessObjectHandler(int tileX, int tileY, GameObjectReservable reservable) {
        this.tileX = tileX;
        this.tileY = tileY;
        this.reservable = reservable;
    }

    public abstract boolean canProcess();

    public abstract void process();

    public abstract boolean isValid();

    public abstract int getTimeItTakesInMilliseconds();

    public boolean isOnTargetTile() {
        return true;
    }
}

