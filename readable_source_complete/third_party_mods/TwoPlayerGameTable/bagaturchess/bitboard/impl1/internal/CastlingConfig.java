/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl1.internal;

import bagaturchess.bitboard.impl1.internal.ChessConstants;
import bagaturchess.bitboard.impl1.internal.Util;

public class CastlingConfig {
    public static final int A1 = 7;
    public static final int C1 = 5;
    public static final int D1 = 4;
    public static final int E1 = 3;
    public static final int F1 = 2;
    public static final int G1 = 1;
    public static final int H1 = 0;
    public static final int A8 = 63;
    public static final int C8 = 61;
    public static final int D8 = 60;
    public static final int E8 = 59;
    public static final int F8 = 58;
    public static final int G8 = 57;
    public static final int H8 = 56;
    public static final CastlingConfig CLASSIC_CHESS = new CastlingConfig(3, 0, 7, 59, 56, 63);
    public int from_SquareID_king_w;
    public int from_SquareID_rook_kingside_w;
    public int from_SquareID_rook_queenside_w;
    public int from_SquareID_king_b;
    public int from_SquareID_rook_kingside_b;
    public int from_SquareID_rook_queenside_b;
    public long bb_inbetween_king_kingside_w;
    public long bb_inbetween_king_queenside_w;
    public long bb_inbetween_rook_kingside_w;
    public long bb_inbetween_rook_queenside_w;
    public long bb_inbetween_king_kingside_b;
    public long bb_inbetween_king_queenside_b;
    public long bb_inbetween_rook_kingside_b;
    public long bb_inbetween_rook_queenside_b;

    public CastlingConfig(int from_SquareID_king_w, int from_SquareID_rook_kingside_w, int from_SquareID_rook_queenside_w, int from_SquareID_king_b, int from_SquareID_rook_kingside_b, int from_SquareID_rook_queenside_b) {
        this.from_SquareID_king_w = from_SquareID_king_w;
        this.from_SquareID_rook_kingside_w = from_SquareID_rook_kingside_w;
        this.from_SquareID_rook_queenside_w = from_SquareID_rook_queenside_w;
        this.from_SquareID_king_b = from_SquareID_king_b;
        this.from_SquareID_rook_kingside_b = from_SquareID_rook_kingside_b;
        this.from_SquareID_rook_queenside_b = from_SquareID_rook_queenside_b;
        this.bb_inbetween_king_kingside_w = ChessConstants.IN_BETWEEN[from_SquareID_king_w][1] | Util.POWER_LOOKUP[1];
        this.bb_inbetween_king_queenside_w = ChessConstants.IN_BETWEEN[from_SquareID_king_w][5] | Util.POWER_LOOKUP[5];
        this.bb_inbetween_rook_kingside_w = ChessConstants.IN_BETWEEN[from_SquareID_rook_kingside_w][2] | Util.POWER_LOOKUP[2];
        this.bb_inbetween_rook_queenside_w = ChessConstants.IN_BETWEEN[from_SquareID_rook_queenside_w][4] | Util.POWER_LOOKUP[4];
        this.bb_inbetween_king_kingside_b = ChessConstants.IN_BETWEEN[from_SquareID_king_b][57] | Util.POWER_LOOKUP[57];
        this.bb_inbetween_king_queenside_b = ChessConstants.IN_BETWEEN[from_SquareID_king_b][61] | Util.POWER_LOOKUP[61];
        this.bb_inbetween_rook_kingside_b = ChessConstants.IN_BETWEEN[from_SquareID_rook_kingside_b][58] | Util.POWER_LOOKUP[58];
        this.bb_inbetween_rook_queenside_b = ChessConstants.IN_BETWEEN[from_SquareID_rook_queenside_b][60] | Util.POWER_LOOKUP[60];
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        CastlingConfig other = (CastlingConfig)o;
        return this.from_SquareID_king_w == other.from_SquareID_king_w && this.from_SquareID_rook_kingside_w == other.from_SquareID_rook_kingside_w && this.from_SquareID_rook_queenside_w == other.from_SquareID_rook_queenside_w && this.from_SquareID_king_b == other.from_SquareID_king_b && this.from_SquareID_rook_kingside_b == other.from_SquareID_rook_kingside_b && this.from_SquareID_rook_queenside_b == other.from_SquareID_rook_queenside_b;
    }

    public int hashCode() {
        return this.from_SquareID_king_w ^ this.from_SquareID_rook_kingside_w ^ this.from_SquareID_rook_queenside_w ^ this.from_SquareID_king_b ^ this.from_SquareID_rook_kingside_b ^ this.from_SquareID_rook_queenside_b;
    }

    public String toString() {
        Object msg = "";
        msg = (String)msg + "[";
        msg = (String)msg + "from_SquareID_king_w = " + this.from_SquareID_king_w + ", ";
        msg = (String)msg + "from_SquareID_rook_kingside_w = " + this.from_SquareID_rook_kingside_w + ", ";
        msg = (String)msg + "from_SquareID_rook_queenside_w = " + this.from_SquareID_rook_queenside_w + ", ";
        msg = (String)msg + "from_SquareID_king_b = " + this.from_SquareID_king_b + ", ";
        msg = (String)msg + "from_SquareID_rook_kingside_b = " + this.from_SquareID_rook_kingside_b + ", ";
        msg = (String)msg + "from_SquareID_rook_queenside_b = " + this.from_SquareID_rook_queenside_b;
        msg = (String)msg + "]";
        return msg;
    }
}

