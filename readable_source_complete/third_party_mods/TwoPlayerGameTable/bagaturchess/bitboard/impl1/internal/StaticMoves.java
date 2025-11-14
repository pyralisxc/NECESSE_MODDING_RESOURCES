/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl1.internal;

import bagaturchess.bitboard.impl1.internal.Util;

public class StaticMoves {
    public static final long[] KNIGHT_MOVES;
    public static final long[] KING_MOVES;
    public static final long[][] PAWN_ATTACKS;

    private static boolean isKnightMove(int currentPosition, int newPosition) {
        if (currentPosition / 8 - newPosition / 8 == 1) {
            return currentPosition - 10 == newPosition || currentPosition - 6 == newPosition;
        }
        if (newPosition / 8 - currentPosition / 8 == 1) {
            return currentPosition + 10 == newPosition || currentPosition + 6 == newPosition;
        }
        if (currentPosition / 8 - newPosition / 8 == 2) {
            return currentPosition - 17 == newPosition || currentPosition - 15 == newPosition;
        }
        if (newPosition / 8 - currentPosition / 8 == 2) {
            return currentPosition + 17 == newPosition || currentPosition + 15 == newPosition;
        }
        return false;
    }

    private static boolean isKingMove(int currentPosition, int newPosition) {
        if (currentPosition / 8 - newPosition / 8 == 0) {
            return currentPosition - newPosition == -1 || currentPosition - newPosition == 1;
        }
        if (currentPosition / 8 - newPosition / 8 == 1) {
            return currentPosition - newPosition == 7 || currentPosition - newPosition == 8 || currentPosition - newPosition == 9;
        }
        if (currentPosition / 8 - newPosition / 8 == -1) {
            return currentPosition - newPosition == -7 || currentPosition - newPosition == -8 || currentPosition - newPosition == -9;
        }
        return false;
    }

    static {
        int newPosition;
        int currentPosition;
        KNIGHT_MOVES = new long[64];
        KING_MOVES = new long[64];
        PAWN_ATTACKS = new long[2][64];
        for (currentPosition = 0; currentPosition < 64; ++currentPosition) {
            for (newPosition = 0; newPosition < 64; ++newPosition) {
                if (newPosition == currentPosition + 7 && newPosition % 8 != 7) {
                    long[] lArray = PAWN_ATTACKS[0];
                    int n = currentPosition;
                    lArray[n] = lArray[n] | Util.POWER_LOOKUP[newPosition];
                }
                if (newPosition == currentPosition + 9 && newPosition % 8 != 0) {
                    long[] lArray = PAWN_ATTACKS[0];
                    int n = currentPosition;
                    lArray[n] = lArray[n] | Util.POWER_LOOKUP[newPosition];
                }
                if (newPosition == currentPosition - 7 && newPosition % 8 != 0) {
                    long[] lArray = PAWN_ATTACKS[1];
                    int n = currentPosition;
                    lArray[n] = lArray[n] | Util.POWER_LOOKUP[newPosition];
                }
                if (newPosition != currentPosition - 9 || newPosition % 8 == 7) continue;
                long[] lArray = PAWN_ATTACKS[1];
                int n = currentPosition;
                lArray[n] = lArray[n] | Util.POWER_LOOKUP[newPosition];
            }
        }
        for (currentPosition = 0; currentPosition < 64; ++currentPosition) {
            for (newPosition = 0; newPosition < 64; ++newPosition) {
                if (!StaticMoves.isKnightMove(currentPosition, newPosition)) continue;
                int n = currentPosition;
                KNIGHT_MOVES[n] = KNIGHT_MOVES[n] | Util.POWER_LOOKUP[newPosition];
            }
        }
        for (currentPosition = 0; currentPosition < 64; ++currentPosition) {
            for (newPosition = 0; newPosition < 64; ++newPosition) {
                if (!StaticMoves.isKingMove(currentPosition, newPosition)) continue;
                int n = currentPosition;
                KING_MOVES[n] = KING_MOVES[n] | Util.POWER_LOOKUP[newPosition];
            }
        }
    }
}

