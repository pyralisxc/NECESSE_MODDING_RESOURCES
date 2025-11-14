/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.movegen;

import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.plies.CastlePlies;
import bagaturchess.bitboard.impl.plies.specials.Enpassanting;

public class MoveInt {
    private static int PROM_FLAG_SHIFT = 27;
    private static int CAP_FLAG_SHIFT = 30;
    private static int ENP_FLAG_SHIFT = 29;
    private static int CAST_FLAG_SHIFT = 28;
    private static int PID1_SHIFT = 23;
    private static int PID2_SHIFT = 19;
    private static int SEQ_SHIFT = 16;
    private static int DIR_SHIFT = 12;
    private static int FROM_SHIFT = 6;
    private static int PID_MASK = 15;
    private static int FIGTYPE_MASK = 7;
    private static int FIELD_MASK = 63;
    private static int INIT_CAP = 1 << CAP_FLAG_SHIFT;
    private static int INIT_PROM = 1 << PROM_FLAG_SHIFT;
    private static int INIT_CAP_PROM = INIT_CAP | INIT_PROM;
    private static int INIT_ENPAS = INIT_CAP | 1 << ENP_FLAG_SHIFT | 1 << SEQ_SHIFT;
    private static int INIT_CAST = 1 << CAST_FLAG_SHIFT;
    private static final int ENPAS_CHECK = 1 << ENP_FLAG_SHIFT;
    private static int ORDERING_SHIFT = 32;

    public static int createCapturePromotion(int from, int to, int cap_pid, int prom_pid) {
        return INIT_CAP_PROM | from << FROM_SHIFT | to | cap_pid << PID1_SHIFT | prom_pid << PID2_SHIFT;
    }

    public static int createPromotion(int from, int to, int prom_pid) {
        return INIT_PROM | from << FROM_SHIFT | to | prom_pid << PID2_SHIFT;
    }

    public static int createCapture(int pid, int from, int to, int cap_pid) {
        return INIT_CAP | from << FROM_SHIFT | to | pid << PID2_SHIFT | cap_pid << PID1_SHIFT;
    }

    public static int createNonCapture(int pid, int from, int to) {
        return from << FROM_SHIFT | to | pid << PID2_SHIFT;
    }

    public static int createEnpassant(int pid, int from, int to, int dir, int cap_pid) {
        return INIT_ENPAS | from << FROM_SHIFT | to | dir << DIR_SHIFT | pid << PID2_SHIFT | cap_pid << PID1_SHIFT;
    }

    public static int createKingSide(int kingPID, int from, int to) {
        return INIT_CAST | from << FROM_SHIFT | to | kingPID << PID2_SHIFT;
    }

    public static int createQueenSide(int kingPID, int from, int to) {
        return INIT_CAST | from << FROM_SHIFT | to | kingPID << PID2_SHIFT;
    }

    public static long addOrderingValue(int move, long ord_val) {
        return ord_val << ORDERING_SHIFT | (long)move;
    }

    public static int getOrderingValue(long move) {
        return (int)(move >> ORDERING_SHIFT);
    }

    public static final boolean isPromotion(int move) {
        return (INIT_PROM & move) != 0;
    }

    public static final boolean isCapture(int move) {
        return (INIT_CAP & move) != 0;
    }

    public static final boolean isCastling(int move) {
        return (INIT_CAST & move) != 0;
    }

    public static final boolean isEnpassant(int move) {
        return (ENPAS_CHECK & move) != 0;
    }

    public static final int getDir(int move) {
        return move >> DIR_SHIFT & FIGTYPE_MASK;
    }

    public static final int getSeq(int move) {
        return move >> SEQ_SHIFT & FIGTYPE_MASK;
    }

    public static final int getCapturedFigurePID(int move) {
        return move >> PID1_SHIFT & PID_MASK;
    }

    public static final int getPromotionFigurePID(int move) {
        if (MoveInt.isPromotion(move)) {
            return move >> PID2_SHIFT & PID_MASK;
        }
        return 0;
    }

    public static final int getFromFieldID(int move) {
        return move >> FROM_SHIFT & FIELD_MASK;
    }

    public static final int getToFieldID(int move) {
        return move & FIELD_MASK;
    }

    public static final int getFigurePID(int move) {
        if (MoveInt.isPromotion(move)) {
            return (Fields.ALL_ORDERED_A1H1[MoveInt.getToFieldID(move)] & 0xFFL) != 0L ? 1 : 7;
        }
        return move >> PID2_SHIFT & PID_MASK;
    }

    public static final int getColour(int move) {
        return Constants.getColourByPieceIdentity(MoveInt.getFigurePID(move));
    }

    public static final int getFigureType(int move) {
        return Constants.PIECE_IDENTITY_2_TYPE[MoveInt.getFigurePID(move)];
    }

    public static final boolean isWhite(int move) {
        return MoveInt.getColour(move) == 0;
    }

    public static final int getEnpassantCapturedFieldID(int move) {
        return Enpassanting.ADJOINING_FILE_FIELD_ID_AT_CAPTURE[MoveInt.getColour(move)][MoveInt.getFromFieldID(move)][MoveInt.getDir(move)];
    }

    public static final int getCapturedFigureType(int move) {
        return Constants.PIECE_IDENTITY_2_TYPE[MoveInt.getCapturedFigurePID(move)];
    }

    public static final boolean isCastleKingSide(int move) {
        int toFieldID;
        return MoveInt.isCastling(move) && ((toFieldID = MoveInt.getToFieldID(move)) == 6 || toFieldID == 62);
    }

    public static final boolean isCastleQueenSide(int move) {
        int toFieldID;
        return MoveInt.isCastling(move) && ((toFieldID = MoveInt.getToFieldID(move)) == 2 || toFieldID == 58);
    }

    public static final boolean isQueen(int move) {
        return MoveInt.getFigureType(move) == 5;
    }

    public static final boolean isPawnCapture(int move) {
        return MoveInt.getCapturedFigureType(move) == 1;
    }

    public static final int getDirType(int move) {
        int to;
        if (!MoveInt.isQueen(move)) {
            return MoveInt.getFigureType(move);
        }
        int from = MoveInt.getFromFieldID(move);
        if ((CastlePlies.ALL_CASTLE_MOVES[from] & Fields.ALL_ORDERED_A1H1[to = MoveInt.getToFieldID(move)]) != 0L) {
            return 4;
        }
        return 3;
    }

    public static final int getPromotionFigureType(int move) {
        return Constants.PIECE_IDENTITY_2_TYPE[MoveInt.getPromotionFigurePID(move)];
    }

    public static final boolean isPawn(int move) {
        int pid = MoveInt.getFigurePID(move);
        return pid == 1 || pid == 7;
    }

    public static final boolean isCaptureOrPromotion(int move) {
        return MoveInt.isCapture(move) || MoveInt.isPromotion(move);
    }

    public static final int getOpponentColour(int move) {
        return Figures.OPPONENT_COLOUR[MoveInt.getColour(move)];
    }

    public static final long getToFieldBitboard(int move) {
        return Fields.ALL_ORDERED_A1H1[MoveInt.getToFieldID(move)];
    }

    public static final long getFromFieldBitboard(int move) {
        return Fields.ALL_ORDERED_A1H1[MoveInt.getFromFieldID(move)];
    }

    public static final int getCastlingRookPID(int move) {
        return MoveInt.getColour(move) == 0 ? 4 : 10;
    }

    public static final int getCastlingRookFromID(int move) {
        int toFieldID = MoveInt.getToFieldID(move);
        if (MoveInt.getColour(move) == 0) {
            if (toFieldID == 2) {
                return 0;
            }
            if (toFieldID == 6) {
                return 7;
            }
            throw new IllegalStateException();
        }
        if (toFieldID == 58) {
            return 56;
        }
        if (toFieldID == 62) {
            return 63;
        }
        throw new IllegalStateException();
    }

    public static final int getCastlingRookToID(int move) {
        int toFieldID = MoveInt.getToFieldID(move);
        if (MoveInt.getColour(move) == 0) {
            if (toFieldID == 2) {
                return 3;
            }
            if (toFieldID == 6) {
                return 5;
            }
            throw new IllegalStateException();
        }
        if (toFieldID == 58) {
            return 59;
        }
        if (toFieldID == 62) {
            return 61;
        }
        throw new IllegalStateException();
    }

    public static boolean isEquals(int move1, int move2) {
        return move1 == move2;
    }
}

