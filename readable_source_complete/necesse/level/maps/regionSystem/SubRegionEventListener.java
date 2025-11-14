/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.regionSystem;

import necesse.level.gameObject.DoorObject;

public abstract class SubRegionEventListener {
    public abstract void onInvalidated();

    public abstract void onDoorChanged(DoorObject var1, DoorObject var2, int var3, int var4);

    public abstract void submitHandlerInvalidated();
}

