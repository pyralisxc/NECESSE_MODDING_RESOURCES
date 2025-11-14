/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.engines.evaladapters.carballo;

public interface IBoard {
    public int getColourToMove();

    public long getPawns();

    public long getKnights();

    public long getBishops();

    public long getRooks();

    public long getQueens();

    public long getKings();

    public long getWhites();

    public long getBlacks();

    public long getAll();
}

