/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl;

public class Constants {
    public static final String INITIAL_BOARD = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    public static final int COLOUR_WHITE = 0;
    public static final int COLOUR_BLACK = 1;
    public static final int[] COLOUR_OP = new int[2];
    public static final int TYPE_NONE = 0;
    public static final int TYPE_PAWN = 1;
    public static final int TYPE_KNIGHT = 2;
    public static final int TYPE_BISHOP = 3;
    public static final int TYPE_ROOK = 4;
    public static final int TYPE_QUEEN = 5;
    public static final int TYPE_KING = 6;
    public static final int TYPE_ALL = 7;
    public static final int PID_NONE = 0;
    public static final int PID_W_PAWN = 1;
    public static final int PID_W_KNIGHT = 2;
    public static final int PID_W_BISHOP = 3;
    public static final int PID_W_ROOK = 4;
    public static final int PID_W_QUEEN = 5;
    public static final int PID_W_KING = 6;
    public static final int PID_B_PAWN = 7;
    public static final int PID_B_KNIGHT = 8;
    public static final int PID_B_BISHOP = 9;
    public static final int PID_B_ROOK = 10;
    public static final int PID_B_QUEEN = 11;
    public static final int PID_B_KING = 12;
    public static final int PID_MAX = 13;
    public static final int[] PIECE_IDENTITY_2_TYPE;
    public static final int[][] COLOUR_AND_TYPE_2_PIECE_IDENTITY;
    public static final String[] PIECE_IDENTITY_2_SIGN;

    public static final boolean isWhite(int pid) {
        return pid > 0 && pid < 7;
    }

    public static final boolean isBlack(int pid) {
        return pid >= 7;
    }

    public static final boolean hasSameColour(int pid1, int pid2) {
        if (pid1 == 0 || pid2 == 0) {
            throw new IllegalStateException();
        }
        return Constants.isWhite(pid1) == Constants.isWhite(pid2);
    }

    public static final boolean hasDiffColour(int pid1, int pid2) {
        if (pid1 == 0 || pid2 == 0) {
            throw new IllegalStateException();
        }
        return Constants.isWhite(pid1) == Constants.isBlack(pid2) || Constants.isWhite(pid2) == Constants.isBlack(pid1);
    }

    public static final int getColourByPieceIdentity(int id) {
        if (id >= 7) {
            return 1;
        }
        if (id >= 1) {
            return 0;
        }
        throw new IllegalStateException("piece id " + id);
    }

    public static final String getPieceIDString(int pieceID) {
        switch (pieceID) {
            case 1: {
                return "P";
            }
            case 2: {
                return "N";
            }
            case 3: {
                return "B";
            }
            case 4: {
                return "R";
            }
            case 5: {
                return "Q";
            }
            case 6: {
                return "K";
            }
            case 7: {
                return "p";
            }
            case 8: {
                return "n";
            }
            case 9: {
                return "b";
            }
            case 10: {
                return "r";
            }
            case 11: {
                return "q";
            }
            case 12: {
                return "k";
            }
        }
        return "_";
    }

    public static final String colourToString(int colour) {
        if (colour == 0) {
            return "White";
        }
        if (colour == 1) {
            return "Black";
        }
        throw new IllegalStateException("colour=" + colour);
    }

    static {
        Constants.COLOUR_OP[0] = 1;
        Constants.COLOUR_OP[1] = 0;
        PIECE_IDENTITY_2_TYPE = new int[13];
        Constants.PIECE_IDENTITY_2_TYPE[0] = 0;
        Constants.PIECE_IDENTITY_2_TYPE[1] = 1;
        Constants.PIECE_IDENTITY_2_TYPE[2] = 2;
        Constants.PIECE_IDENTITY_2_TYPE[3] = 3;
        Constants.PIECE_IDENTITY_2_TYPE[4] = 4;
        Constants.PIECE_IDENTITY_2_TYPE[5] = 5;
        Constants.PIECE_IDENTITY_2_TYPE[6] = 6;
        Constants.PIECE_IDENTITY_2_TYPE[7] = 1;
        Constants.PIECE_IDENTITY_2_TYPE[8] = 2;
        Constants.PIECE_IDENTITY_2_TYPE[9] = 3;
        Constants.PIECE_IDENTITY_2_TYPE[10] = 4;
        Constants.PIECE_IDENTITY_2_TYPE[11] = 5;
        Constants.PIECE_IDENTITY_2_TYPE[12] = 6;
        COLOUR_AND_TYPE_2_PIECE_IDENTITY = new int[3][7];
        Constants.COLOUR_AND_TYPE_2_PIECE_IDENTITY[0][1] = -1;
        Constants.COLOUR_AND_TYPE_2_PIECE_IDENTITY[0][2] = -1;
        Constants.COLOUR_AND_TYPE_2_PIECE_IDENTITY[0][3] = -1;
        Constants.COLOUR_AND_TYPE_2_PIECE_IDENTITY[0][4] = -1;
        Constants.COLOUR_AND_TYPE_2_PIECE_IDENTITY[0][5] = -1;
        Constants.COLOUR_AND_TYPE_2_PIECE_IDENTITY[0][6] = -1;
        Constants.COLOUR_AND_TYPE_2_PIECE_IDENTITY[0][0] = -1;
        Constants.COLOUR_AND_TYPE_2_PIECE_IDENTITY[0][1] = 1;
        Constants.COLOUR_AND_TYPE_2_PIECE_IDENTITY[0][2] = 2;
        Constants.COLOUR_AND_TYPE_2_PIECE_IDENTITY[0][3] = 3;
        Constants.COLOUR_AND_TYPE_2_PIECE_IDENTITY[0][4] = 4;
        Constants.COLOUR_AND_TYPE_2_PIECE_IDENTITY[0][5] = 5;
        Constants.COLOUR_AND_TYPE_2_PIECE_IDENTITY[0][6] = 6;
        Constants.COLOUR_AND_TYPE_2_PIECE_IDENTITY[1][0] = -1;
        Constants.COLOUR_AND_TYPE_2_PIECE_IDENTITY[1][1] = 7;
        Constants.COLOUR_AND_TYPE_2_PIECE_IDENTITY[1][2] = 8;
        Constants.COLOUR_AND_TYPE_2_PIECE_IDENTITY[1][3] = 9;
        Constants.COLOUR_AND_TYPE_2_PIECE_IDENTITY[1][4] = 10;
        Constants.COLOUR_AND_TYPE_2_PIECE_IDENTITY[1][5] = 11;
        Constants.COLOUR_AND_TYPE_2_PIECE_IDENTITY[1][6] = 12;
        PIECE_IDENTITY_2_SIGN = new String[13];
        Constants.PIECE_IDENTITY_2_SIGN[0] = "X";
        Constants.PIECE_IDENTITY_2_SIGN[1] = "P";
        Constants.PIECE_IDENTITY_2_SIGN[2] = "N";
        Constants.PIECE_IDENTITY_2_SIGN[3] = "B";
        Constants.PIECE_IDENTITY_2_SIGN[4] = "R";
        Constants.PIECE_IDENTITY_2_SIGN[5] = "Q";
        Constants.PIECE_IDENTITY_2_SIGN[6] = "K";
        Constants.PIECE_IDENTITY_2_SIGN[7] = "p";
        Constants.PIECE_IDENTITY_2_SIGN[8] = "n";
        Constants.PIECE_IDENTITY_2_SIGN[9] = "b";
        Constants.PIECE_IDENTITY_2_SIGN[10] = "r";
        Constants.PIECE_IDENTITY_2_SIGN[11] = "q";
        Constants.PIECE_IDENTITY_2_SIGN[12] = "k";
    }
}

