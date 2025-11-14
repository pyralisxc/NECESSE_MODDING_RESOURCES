/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.api;

public interface IFieldsAttacks {
    public static final int OP_ADD_ATTACK = 0;
    public static final int OP_REM_ATTACK = 1;
    public static final int OP_MAX = 2;
    public static final boolean MINOR_UNION = true;
    public static final int MAX_KNIGHT_STATES = 3;
    public static final int MAX_OFFICER_STATES = 3;
    public static final int MAX_MINOR_STATES = 4;
    public static final int MAX_ROOK_STATES = 4;
    public static final int MAX_QUEEN_STATES = 5;
    public static final int MAX_OTHER_STATES = 2;
    public static final int MAX_PAWN_STATES = 3;
    public static final int MAX_KING_STATES = 2;

    public int[] getControlArray(int var1);

    public long getControlBitboard(int var1);

    public int getScore_BeforeMove(int var1);

    public int getScore_AfterMove(int var1);

    public int getScore_ForEval(int var1);
}

