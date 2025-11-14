/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.api;

public interface IHistoryProvider {
    public int getScores(int var1, int var2);

    public int getCounter1(int var1, int var2);

    public int getCounter2(int var1, int var2);

    public int getKiller1(int var1, int var2);

    public int getKiller2(int var1, int var2);
}

