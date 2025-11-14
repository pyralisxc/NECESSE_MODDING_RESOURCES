/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine;

public interface GameEventInterface<T> {
    public void init(Runnable var1);

    public void onEvent(T var1);

    public boolean isDisposed();
}

