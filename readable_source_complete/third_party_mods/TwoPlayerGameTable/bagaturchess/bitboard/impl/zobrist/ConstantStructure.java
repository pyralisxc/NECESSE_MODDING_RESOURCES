/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.zobrist;

import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.zobrist.Randoms;

public class ConstantStructure
extends Figures {
    public static final long[][] MOVES_KEYS = new long[13][64];
    public static final int[][][][] MOVES_INDEXES = new int[64][8][7][7];
    public static final int MOVES_INDEXES_MAX = 25088;
    public static final long[] FIGURE_TYPE = new long[7];
    public static final long WHITE_TO_MOVE;
    public static final long HAS_ENPASSANT;
    public static final long WHITE_CASTLE_KING_SIDE;
    public static final long WHITE_CASTLE_QUEEN_SIDE;
    public static final long BLACK_CASTLE_KING_SIDE;
    public static final long BLACK_CASTLE_QUEEN_SIDE;
    public static final long[] CASTLE_KING_SIDE_BY_COLOUR;
    public static final long[] CASTLE_QUEEN_SIDE_BY_COLOUR;

    public static final long getMoveHash(int pid, int fromFieldID, int toFieldID) {
        return MOVES_KEYS[pid][fromFieldID] ^ MOVES_KEYS[pid][toFieldID];
    }

    public static final long getMoveHash(int pid, int promotionFigureType, int fromFieldID, int toFieldID) {
        return MOVES_KEYS[pid][fromFieldID] ^ MOVES_KEYS[pid][toFieldID] ^ FIGURE_TYPE[promotionFigureType];
    }

    public static final int getMoveIndex(int from, int dir, int seq) {
        return MOVES_INDEXES[from][dir][seq][0];
    }

    public static final int getMoveIndex(int from, int dir, int seq, int dirType) {
        return MOVES_INDEXES[from][dir][seq][dirType];
    }

    public static void main(String[] args) {
        long k = MOVES_KEYS[0][0];
    }

    static {
        CASTLE_KING_SIDE_BY_COLOUR = new long[COLOUR_MAX];
        CASTLE_QUEEN_SIDE_BY_COLOUR = new long[COLOUR_MAX];
        long[] primes = Randoms.NUMBERS;
        int count = 0;
        for (int pid = 1; pid < 13; ++pid) {
            for (int field = 0; field < 64; ++field) {
                int field_idx = Fields.IDX_ORDERED_2_A1H1[field];
                ConstantStructure.MOVES_KEYS[pid][field_idx] = primes[count++];
            }
        }
        for (int i = 0; i < FIGURE_TYPE.length; ++i) {
            ConstantStructure.FIGURE_TYPE[i] = primes[count++];
        }
        WHITE_TO_MOVE = primes[count++];
        HAS_ENPASSANT = primes[count++];
        WHITE_CASTLE_KING_SIDE = primes[count++];
        WHITE_CASTLE_QUEEN_SIDE = primes[count++];
        BLACK_CASTLE_KING_SIDE = primes[count++];
        BLACK_CASTLE_QUEEN_SIDE = primes[count++];
        ConstantStructure.CASTLE_KING_SIDE_BY_COLOUR[0] = WHITE_CASTLE_KING_SIDE;
        ConstantStructure.CASTLE_KING_SIDE_BY_COLOUR[1] = BLACK_CASTLE_KING_SIDE;
        ConstantStructure.CASTLE_QUEEN_SIDE_BY_COLOUR[0] = WHITE_CASTLE_QUEEN_SIDE;
        ConstantStructure.CASTLE_QUEEN_SIDE_BY_COLOUR[1] = BLACK_CASTLE_QUEEN_SIDE;
        int index = 1;
        for (int i1 = 0; i1 < 64; ++i1) {
            for (int i2 = 0; i2 < 8; ++i2) {
                for (int i3 = 0; i3 < 7; ++i3) {
                    for (int i4 = 0; i4 < 7; ++i4) {
                        ConstantStructure.MOVES_INDEXES[i1][i2][i3][i4] = index++;
                    }
                }
            }
        }
    }
}

