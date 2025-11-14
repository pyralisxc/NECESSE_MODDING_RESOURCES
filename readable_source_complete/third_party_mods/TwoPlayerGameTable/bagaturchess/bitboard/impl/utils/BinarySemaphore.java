/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.utils;

import bagaturchess.bitboard.api.IBinarySemaphore;

public class BinarySemaphore
implements IBinarySemaphore {
    private Object lock = new Object();
    private volatile boolean inUse = false;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void lock() {
        Object object = this.lock;
        synchronized (object) {
            while (this.inUse) {
                try {
                    this.lock.wait();
                }
                catch (InterruptedException e) {
                    throw new IllegalStateException("INTERRUPTED: " + String.valueOf(e));
                }
            }
            this.inUse = true;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unlock() {
        Object object = this.lock;
        synchronized (object) {
            if (!this.inUse) {
                throw new IllegalStateException();
            }
            this.inUse = false;
            this.lock.notifyAll();
        }
    }
}

