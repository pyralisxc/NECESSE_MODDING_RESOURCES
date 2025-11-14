/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory;

public abstract class InventoryUpdateListener {
    private Runnable disposeLogic;

    public void init(Runnable disposeLogic) {
        this.disposeLogic = disposeLogic;
    }

    public abstract void onSlotUpdate(int var1);

    public abstract boolean isDisposed();

    public void dispose() {
        if (this.disposeLogic != null) {
            this.disposeLogic.run();
        }
    }
}

