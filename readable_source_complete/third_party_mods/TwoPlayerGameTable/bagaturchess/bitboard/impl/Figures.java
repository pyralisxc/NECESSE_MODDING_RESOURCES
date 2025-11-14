/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl;

import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;

public class Figures
extends Fields {
    public static final byte COLOUR_WHITE = 0;
    public static final byte COLOUR_BLACK = 1;
    public static final byte COLOUR_UNSPECIFIED = -1;
    public static final byte COLOUR_MAX = (byte)(Math.max(0, 1) + 1);
    public static final byte[] OPPONENT_COLOUR = new byte[3];
    public static final int TYPE_UNDEFINED = 0;
    public static final int TYPE_PAWN = 1;
    public static final int TYPE_KNIGHT = 2;
    public static final int TYPE_OFFICER = 3;
    public static final int TYPE_CASTLE = 4;
    public static final int TYPE_QUEEN = 5;
    public static final int TYPE_KING = 6;
    public static final int TYPE_MAX = 7;
    public static final int[] TYPES;
    public static final String[] TYPES_SIGN;
    public static final String[] COLOURS_SIGN;
    public static final int ID_MAX = 64;
    public static final int DIR_MAX = 8;
    public static final int SEQ_MAX = 7;
    public static final long DUMMY_FIGURE = -1L;
    public static final long[] FIGURES;
    public static final int[] IDX_FIGURE_ID_2_ORDERED_FIGURE_ID;

    public static final int getFigureID(long figureBitboard) {
        return Figures.get67IDByBitboard(figureBitboard);
    }

    public static final long getFigureBitboard(int figureID) {
        throw new IllegalStateException();
    }

    public static void main(String[] args) {
        System.out.println("Yo");
    }

    public static int getFigureColour(int pid) {
        return Constants.getColourByPieceIdentity(pid);
    }

    public static int getFigureType(int pid) {
        return Constants.PIECE_IDENTITY_2_TYPE[pid];
    }

    public static boolean isMajorOrMinor(int figurePID) {
        boolean result = false;
        int type = Figures.getFigureType(figurePID);
        if (type == 3 || type == 2 || type == 4 || type == 5) {
            result = true;
        }
        return result;
    }

    public static int nextType(int type) {
        switch (type) {
            case 0: {
                return 1;
            }
            case 1: {
                return 2;
            }
            case 2: {
                return 3;
            }
            case 3: {
                return 4;
            }
            case 4: {
                return 5;
            }
            case 5: {
                return 6;
            }
            case 6: {
                return 7;
            }
        }
        throw new IllegalArgumentException("Figure type " + type + " is undefined!");
    }

    public static boolean isPawn(int figureID) {
        boolean result = false;
        int type = Figures.getFigureType(figureID);
        if (type == 1) {
            result = true;
        }
        return result;
    }

    public static boolean isTypeGreaterOrEqual(int type1, int type2) {
        return type1 >= type2;
    }

    public static boolean isTypeGreater(int type1, int type2) {
        return type1 > type2;
    }

    public static final int getPidByColourAndType(int colour, int type) {
        if (colour == 0) {
            switch (type) {
                case 1: {
                    return 1;
                }
                case 2: {
                    return 2;
                }
                case 6: {
                    return 6;
                }
                case 3: {
                    return 3;
                }
                case 4: {
                    return 4;
                }
                case 5: {
                    return 5;
                }
            }
            throw new IllegalStateException();
        }
        if (colour == 1) {
            switch (type) {
                case 1: {
                    return 7;
                }
                case 2: {
                    return 8;
                }
                case 6: {
                    return 12;
                }
                case 3: {
                    return 9;
                }
                case 4: {
                    return 10;
                }
                case 5: {
                    return 11;
                }
            }
            throw new IllegalStateException();
        }
        throw new IllegalStateException();
    }

    public static final int getTypeByPid(int pid) {
        switch (pid) {
            case 1: {
                return 1;
            }
            case 2: {
                return 2;
            }
            case 3: {
                return 3;
            }
            case 4: {
                return 4;
            }
            case 5: {
                return 5;
            }
            case 6: {
                return 6;
            }
            case 7: {
                return 1;
            }
            case 8: {
                return 2;
            }
            case 9: {
                return 3;
            }
            case 10: {
                return 4;
            }
            case 11: {
                return 5;
            }
            case 12: {
                return 6;
            }
        }
        return -1;
    }

    static {
        Figures.OPPONENT_COLOUR[0] = -1;
        Figures.OPPONENT_COLOUR[0] = 1;
        Figures.OPPONENT_COLOUR[1] = 0;
        TYPES = new int[]{6, 1, 2, 3, 4, 5};
        TYPES_SIGN = new String[7];
        Figures.TYPES_SIGN[6] = "K";
        Figures.TYPES_SIGN[1] = "P";
        Figures.TYPES_SIGN[2] = "N";
        Figures.TYPES_SIGN[3] = "B";
        Figures.TYPES_SIGN[4] = "R";
        Figures.TYPES_SIGN[5] = "Q";
        COLOURS_SIGN = new String[COLOUR_MAX];
        Figures.COLOURS_SIGN[0] = "W";
        Figures.COLOURS_SIGN[1] = "B";
        FIGURES = new long[64];
        IDX_FIGURE_ID_2_ORDERED_FIGURE_ID = new int[64];
    }
}

