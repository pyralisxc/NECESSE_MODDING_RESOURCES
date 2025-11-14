/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.common;

public class BackupInfo {
    public long enpassantPawnBitboard = 0L;
    public int enpassantPawnFieldID = -1;
    public boolean w_kingSideAvailable = false;
    public boolean w_queenSideAvailable = false;
    public boolean b_kingSideAvailable = false;
    public boolean b_queenSideAvailable = false;
    public int lastCaptureOrPawnMoveBefore = 0;
    public int lastCaptureFieldID = -1;
    public long hashkey;
    public long pawnshash;

    public long getHashkey() {
        return this.hashkey;
    }
}

