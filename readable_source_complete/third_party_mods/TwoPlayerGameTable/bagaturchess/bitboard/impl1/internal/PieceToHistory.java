/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl1.internal;

public class PieceToHistory {
    long[][] array = new long[16][64];

    PieceToHistory() {
        this.clear();
    }

    void clear() {
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 64; ++j) {
                this.array[i][j] = 1L;
            }
        }
    }
}

