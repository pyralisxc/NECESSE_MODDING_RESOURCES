/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl1.internal;

import bagaturchess.bitboard.impl1.internal.StaticMoves;
import bagaturchess.bitboard.impl1.internal.Util;

public class ChessConstants {
    public static final int CACHE_MISS = Integer.MIN_VALUE;
    public static final String[] FEN_WHITE_PIECES;
    public static final String[] FEN_BLACK_PIECES;
    public static final String FEN_START = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    public static final int EMPTY = 0;
    public static final int PAWN = 1;
    public static final int NIGHT = 2;
    public static final int BISHOP = 3;
    public static final int ROOK = 4;
    public static final int QUEEN = 5;
    public static final int KING = 6;
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    public static final int SCORE_NOT_RUNNING = 7777;
    public static final int[] COLOR_FACTOR;
    public static final int[] COLOR_FACTOR_8;
    public static final long[][] KING_AREA;
    public static final long[][] IN_BETWEEN;
    public static final long[][] PINNED_MOVEMENT;

    static {
        int i;
        int i2;
        int to;
        int from;
        FEN_WHITE_PIECES = new String[]{"1", "P", "N", "B", "R", "Q", "K"};
        FEN_BLACK_PIECES = new String[]{"1", "p", "n", "b", "r", "q", "k"};
        COLOR_FACTOR = new int[]{1, -1};
        COLOR_FACTOR_8 = new int[]{8, -8};
        KING_AREA = new long[2][64];
        IN_BETWEEN = new long[64][64];
        PINNED_MOVEMENT = new long[64][64];
        for (from = 0; from < 64; ++from) {
            for (to = from + 1; to < 64; ++to) {
                if (from / 8 == to / 8) {
                    for (i2 = to - 1; i2 > from; --i2) {
                        long[] lArray = IN_BETWEEN[from];
                        int n = to;
                        lArray[n] = lArray[n] | Util.POWER_LOOKUP[i2];
                    }
                }
                if (from % 8 != to % 8) continue;
                for (i2 = to - 8; i2 > from; i2 -= 8) {
                    long[] lArray = IN_BETWEEN[from];
                    int n = to;
                    lArray[n] = lArray[n] | Util.POWER_LOOKUP[i2];
                }
            }
        }
        for (from = 0; from < 64; ++from) {
            for (to = 0; to < from; ++to) {
                ChessConstants.IN_BETWEEN[from][to] = IN_BETWEEN[to][from];
            }
        }
        for (from = 0; from < 64; ++from) {
            for (to = from + 1; to < 64; ++to) {
                if ((to - from) % 9 == 0 && to % 8 > from % 8) {
                    for (i2 = to - 9; i2 > from; i2 -= 9) {
                        long[] lArray = IN_BETWEEN[from];
                        int n = to;
                        lArray[n] = lArray[n] | Util.POWER_LOOKUP[i2];
                    }
                }
                if ((to - from) % 7 != 0 || to % 8 >= from % 8) continue;
                for (i2 = to - 7; i2 > from; i2 -= 7) {
                    long[] lArray = IN_BETWEEN[from];
                    int n = to;
                    lArray[n] = lArray[n] | Util.POWER_LOOKUP[i2];
                }
            }
        }
        for (from = 0; from < 64; ++from) {
            for (to = 0; to < from; ++to) {
                ChessConstants.IN_BETWEEN[from][to] = IN_BETWEEN[to][from];
            }
        }
        int[] DIRECTION = new int[]{-1, -7, -8, -9, 1, 7, 8, 9};
        for (int pinnedPieceIndex = 0; pinnedPieceIndex < 64; ++pinnedPieceIndex) {
            for (int kingIndex = 0; kingIndex < 64; ++kingIndex) {
                int correctDirection = 0;
                block14: for (int direction : DIRECTION) {
                    if (correctDirection != 0) break;
                    for (int xray = kingIndex + direction; xray >= 0 && xray < 64 && (direction != -1 && direction != -9 && direction != 7 || (xray & 7) != 7) && (direction != 1 && direction != 9 && direction != -7 || (xray & 7) != 0); xray += direction) {
                        if (xray != pinnedPieceIndex) continue;
                        correctDirection = direction;
                        continue block14;
                    }
                }
                if (correctDirection == 0) continue;
                for (int xray = kingIndex + correctDirection; xray >= 0 && xray < 64 && (correctDirection != -1 && correctDirection != -9 && correctDirection != 7 || (xray & 7) != 7) && (correctDirection != 1 && correctDirection != 9 && correctDirection != -7 || (xray & 7) != 0); xray += correctDirection) {
                    long[] lArray = PINNED_MOVEMENT[pinnedPieceIndex];
                    int n = kingIndex;
                    lArray[n] = lArray[n] | Util.POWER_LOOKUP[xray];
                }
            }
        }
        for (i = 0; i < 64; ++i) {
            long[] lArray = KING_AREA[0];
            int n = i;
            lArray[n] = lArray[n] | (StaticMoves.KING_MOVES[i] | Util.POWER_LOOKUP[i]);
            long[] lArray2 = KING_AREA[1];
            int n2 = i;
            lArray2[n2] = lArray2[n2] | (StaticMoves.KING_MOVES[i] | Util.POWER_LOOKUP[i]);
            if (i > 15) {
                long[] lArray3 = KING_AREA[1];
                int n3 = i;
                lArray3[n3] = lArray3[n3] | StaticMoves.KING_MOVES[i] >>> 8;
            }
            if (i >= 48) continue;
            long[] lArray4 = KING_AREA[0];
            int n4 = i;
            lArray4[n4] = lArray4[n4] | StaticMoves.KING_MOVES[i] << 8;
        }
        for (i = 0; i < 64; ++i) {
            for (int color = 0; color < 2; ++color) {
                if (i % 8 == 0) {
                    long[] lArray = KING_AREA[color];
                    int n = i;
                    lArray[n] = lArray[n] | KING_AREA[color][i + 1];
                    continue;
                }
                if (i % 8 != 7) continue;
                long[] lArray = KING_AREA[color];
                int n = i;
                lArray[n] = lArray[n] | KING_AREA[color][i - 1];
            }
        }
        for (i = 0; i < 64; ++i) {
            if (i < 8) {
                ChessConstants.KING_AREA[0][i] = KING_AREA[0][i + 8];
                continue;
            }
            if (i <= 47) continue;
            ChessConstants.KING_AREA[0][i] = i > 55 ? KING_AREA[0][i - 16] : KING_AREA[0][i - 8];
        }
        for (i = 0; i < 64; ++i) {
            if (i > 55) {
                ChessConstants.KING_AREA[1][i] = KING_AREA[1][i - 8];
                continue;
            }
            if (i >= 16) continue;
            ChessConstants.KING_AREA[1][i] = i < 8 ? KING_AREA[1][i + 16] : KING_AREA[1][i + 8];
        }
    }

    public static enum ScoreType {
        EXACT(" "),
        UPPER(" upperbound "),
        LOWER(" lowerbound ");

        private String uci;

        private ScoreType(String uci) {
            this.uci = uci;
        }

        public String toString() {
            return this.uci;
        }
    }
}

