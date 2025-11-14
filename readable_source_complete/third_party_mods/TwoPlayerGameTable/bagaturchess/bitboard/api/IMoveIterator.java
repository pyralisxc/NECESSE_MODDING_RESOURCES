/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.api;

public interface IMoveIterator {
    public boolean hasNext();

    public int next();

    public void reset();

    public void finish();
}

