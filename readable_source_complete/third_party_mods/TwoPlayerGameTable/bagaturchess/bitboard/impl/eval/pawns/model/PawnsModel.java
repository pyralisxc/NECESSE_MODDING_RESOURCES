/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.eval.pawns.model;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IPiecesLists;
import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.eval.pawns.model.Pawn;
import bagaturchess.bitboard.impl.state.PiecesList;

public class PawnsModel
extends Fields {
    private static boolean GEN_PST = false;
    private int w_count;
    private int b_count;
    private int w_passed_count;
    private int b_passed_count;
    private int w_max_passed_rank;
    private int b_max_passed_rank;
    private Pawn[] w_pawns = new Pawn[8];
    private Pawn[] b_pawns = new Pawn[8];
    private Pawn[] w_passed = new Pawn[8];
    private Pawn[] b_passed = new Pawn[8];
    private int w_islands_count;
    private int b_islands_count;
    private long w_king_verticals;
    private long b_king_verticals;
    private long opened_files;
    private long w_half_opened_files;
    private long b_half_opened_files;
    private long w_weak_fields;
    private long b_weak_fields;
    private int w_king_opened_files_count;
    private int w_king_semi_opened_files_count_own;
    private int w_king_semi_opened_files_count_op;
    private int b_king_opened_files_count;
    private int b_king_semi_opened_files_count_own;
    private int b_king_semi_opened_files_count_op;
    private long w_attacks;
    private long b_attacks;
    private int w_unstoppablePasser_rank;
    private int b_unstoppablePasser_rank;
    int wKingFieldID;
    int bKingFieldID;
    private long w_space;
    private long b_space;
    private int[] w_pstKnight;
    private int[] b_pstKnight;
    private int[] w_pstBishop;
    private int[] b_pstBishop;
    private int[] w_pstRooks;
    private int[] b_pstRooks;

    public PawnsModel() {
        for (int i = 0; i < 8; ++i) {
            this.w_pawns[i] = new Pawn();
            this.b_pawns[i] = new Pawn();
        }
        if (GEN_PST) {
            this.w_pstKnight = new int[64];
            this.b_pstKnight = new int[64];
            this.w_pstBishop = new int[64];
            this.b_pstBishop = new int[64];
            this.w_pstRooks = new int[64];
            this.b_pstRooks = new int[64];
        }
        this.reinit();
    }

    private void reinit() {
        this.w_count = 0;
        this.b_count = 0;
        this.w_passed_count = 0;
        this.b_passed_count = 0;
        this.w_max_passed_rank = 0;
        this.b_max_passed_rank = 0;
        this.w_islands_count = 0;
        this.b_islands_count = 0;
        this.w_king_verticals = 0L;
        this.b_king_verticals = 0L;
        this.opened_files = -1L;
        this.w_half_opened_files = -1L;
        this.b_half_opened_files = -1L;
        this.w_weak_fields = 0xFFFF00000000L;
        this.b_weak_fields = 0xFFFF0000L;
        this.w_king_opened_files_count = 0;
        this.w_king_semi_opened_files_count_own = 0;
        this.w_king_semi_opened_files_count_op = 0;
        this.b_king_opened_files_count = 0;
        this.b_king_semi_opened_files_count_own = 0;
        this.b_king_semi_opened_files_count_op = 0;
        this.w_attacks = 0L;
        this.b_attacks = 0L;
        this.w_unstoppablePasser_rank = 0;
        this.b_unstoppablePasser_rank = 0;
        this.wKingFieldID = 0;
        this.bKingFieldID = 0;
    }

    public void rebuild(IBitBoard _bitboard) {
        this.reinit();
        long w_pawns_board = _bitboard.getFiguresBitboardByPID(1);
        long b_pawns_board = _bitboard.getFiguresBitboardByPID(7);
        IPiecesLists lists = _bitboard.getPiecesLists();
        this.wKingFieldID = lists.getPieces(6).getData()[0];
        this.bKingFieldID = lists.getPieces(12).getData()[0];
        PiecesList w_pawns = lists.getPieces(1);
        PiecesList b_pawns = lists.getPieces(7);
        int w_pawns_count = w_pawns.getDataSize();
        int[] w_pawns_arr = w_pawns.getData();
        for (int i = 0; i < w_pawns_count; ++i) {
            int w_pawnFieldID = w_pawns_arr[i];
            Pawn cur = this.getForFilling(0);
            cur.initialize(0, _bitboard.getColourToMove(), w_pawnFieldID, w_pawns_board, b_pawns_board, this.wKingFieldID, this.bKingFieldID);
            if (cur.isPassed()) {
                this.w_passed[this.w_passed_count] = cur;
                ++this.w_passed_count;
                if (cur.rank > this.w_max_passed_rank) {
                    this.w_max_passed_rank = cur.rank;
                }
                if (cur.isPassedUnstoppable() && cur.rank > this.w_unstoppablePasser_rank) {
                    this.w_unstoppablePasser_rank = cur.rank;
                }
            }
            this.w_half_opened_files &= cur.vertical ^ 0xFFFFFFFFFFFFFFFFL;
            this.opened_files &= cur.vertical ^ 0xFFFFFFFFFFFFFFFFL;
            this.w_weak_fields &= cur.front_neighbour ^ 0xFFFFFFFFFFFFFFFFL;
            this.w_attacks |= cur.attacks;
        }
        int b_pawns_count = b_pawns.getDataSize();
        int[] b_pawns_arr = b_pawns.getData();
        for (int i = 0; i < b_pawns_count; ++i) {
            int b_pawnFieldID = b_pawns_arr[i];
            Pawn cur = this.getForFilling(1);
            cur.initialize(1, _bitboard.getColourToMove(), b_pawnFieldID, b_pawns_board, w_pawns_board, this.bKingFieldID, this.wKingFieldID);
            if (cur.isPassed()) {
                this.b_passed[this.b_passed_count] = cur;
                ++this.b_passed_count;
                if (cur.rank > this.b_max_passed_rank) {
                    this.b_max_passed_rank = cur.rank;
                }
                if (cur.isPassedUnstoppable() && cur.rank > this.b_unstoppablePasser_rank) {
                    this.b_unstoppablePasser_rank = cur.rank;
                }
            }
            this.b_half_opened_files &= cur.vertical ^ 0xFFFFFFFFFFFFFFFFL;
            this.opened_files &= cur.vertical ^ 0xFFFFFFFFFFFFFFFFL;
            this.b_weak_fields &= cur.front_neighbour ^ 0xFFFFFFFFFFFFFFFFL;
            this.b_attacks |= cur.attacks;
        }
        this.w_islands_count = PawnsModel.fillIslands(w_pawns_board);
        this.b_islands_count = PawnsModel.fillIslands(b_pawns_board);
        this.fillKingOpenedAndSemiOpened(this.wKingFieldID, this.bKingFieldID);
        if (GEN_PST) {
            // empty if block
        }
        long w_space_noownpawns_and_notattacked = 0x3C3C3C00000000L & (w_pawns_board ^ 0xFFFFFFFFFFFFFFFFL) & (this.b_attacks ^ 0xFFFFFFFFFFFFFFFFL);
        long w_behindFriendlyPawns = w_pawns_board;
        w_behindFriendlyPawns |= w_behindFriendlyPawns << 8;
        w_behindFriendlyPawns |= w_behindFriendlyPawns << 16;
        this.w_space = w_space_noownpawns_and_notattacked & (w_behindFriendlyPawns | this.w_half_opened_files | this.opened_files);
        long b_space_noownpawns_and_notattacked = 0x3C3C3C00L & (b_pawns_board ^ 0xFFFFFFFFFFFFFFFFL) & (this.w_attacks ^ 0xFFFFFFFFFFFFFFFFL);
        long b_behindFriendlyPawns = b_pawns_board;
        b_behindFriendlyPawns |= b_behindFriendlyPawns >> 8;
        b_behindFriendlyPawns |= b_behindFriendlyPawns >> 16;
        this.b_space = b_space_noownpawns_and_notattacked & (b_behindFriendlyPawns | this.b_half_opened_files | this.opened_files);
    }

    private void fillKingOpenedAndSemiOpened(int wKingFieldID, int bKingFieldID) {
        this.w_king_verticals = LETTERS_NEIGHBOURS_BY_FIELD_ID[wKingFieldID] | LETTERS_BY_FIELD_ID[wKingFieldID];
        this.b_king_verticals = LETTERS_NEIGHBOURS_BY_FIELD_ID[bKingFieldID] | LETTERS_BY_FIELD_ID[bKingFieldID];
        if ((this.w_king_verticals & this.opened_files) != 0L) {
            if ((this.opened_files & LETTERS_LEFT_BY_FIELD_ID[wKingFieldID]) != 0L) {
                ++this.w_king_opened_files_count;
            }
            if ((this.opened_files & LETTERS_BY_FIELD_ID[wKingFieldID]) != 0L) {
                ++this.w_king_opened_files_count;
            }
            if ((this.opened_files & LETTERS_RIGHT_BY_FIELD_ID[wKingFieldID]) != 0L) {
                ++this.w_king_opened_files_count;
            }
        }
        if ((this.w_king_verticals & this.w_half_opened_files) != 0L) {
            if ((this.w_half_opened_files & LETTERS_LEFT_BY_FIELD_ID[wKingFieldID]) != 0L && (this.opened_files & LETTERS_LEFT_BY_FIELD_ID[wKingFieldID]) == 0L) {
                ++this.w_king_semi_opened_files_count_own;
            }
            if ((this.w_half_opened_files & LETTERS_BY_FIELD_ID[wKingFieldID]) != 0L && (this.opened_files & LETTERS_BY_FIELD_ID[wKingFieldID]) == 0L) {
                ++this.w_king_semi_opened_files_count_own;
            }
            if ((this.w_half_opened_files & LETTERS_RIGHT_BY_FIELD_ID[wKingFieldID]) != 0L && (this.opened_files & LETTERS_RIGHT_BY_FIELD_ID[wKingFieldID]) == 0L) {
                ++this.w_king_semi_opened_files_count_own;
            }
        }
        if ((this.w_king_verticals & this.b_half_opened_files) != 0L) {
            if ((this.b_half_opened_files & LETTERS_LEFT_BY_FIELD_ID[wKingFieldID]) != 0L && (this.opened_files & LETTERS_LEFT_BY_FIELD_ID[wKingFieldID]) == 0L) {
                ++this.w_king_semi_opened_files_count_op;
            }
            if ((this.b_half_opened_files & LETTERS_BY_FIELD_ID[wKingFieldID]) != 0L && (this.opened_files & LETTERS_BY_FIELD_ID[wKingFieldID]) == 0L) {
                ++this.w_king_semi_opened_files_count_op;
            }
            if ((this.b_half_opened_files & LETTERS_RIGHT_BY_FIELD_ID[wKingFieldID]) != 0L && (this.opened_files & LETTERS_RIGHT_BY_FIELD_ID[wKingFieldID]) == 0L) {
                ++this.w_king_semi_opened_files_count_op;
            }
        }
        if ((this.b_king_verticals & this.opened_files) != 0L) {
            if ((this.opened_files & LETTERS_LEFT_BY_FIELD_ID[bKingFieldID]) != 0L) {
                ++this.b_king_opened_files_count;
            }
            if ((this.opened_files & LETTERS_BY_FIELD_ID[bKingFieldID]) != 0L) {
                ++this.b_king_opened_files_count;
            }
            if ((this.opened_files & LETTERS_RIGHT_BY_FIELD_ID[bKingFieldID]) != 0L) {
                ++this.b_king_opened_files_count;
            }
        }
        if ((this.b_king_verticals & this.b_half_opened_files) != 0L) {
            if ((this.b_half_opened_files & LETTERS_LEFT_BY_FIELD_ID[bKingFieldID]) != 0L && (this.opened_files & LETTERS_LEFT_BY_FIELD_ID[bKingFieldID]) == 0L) {
                ++this.b_king_semi_opened_files_count_own;
            }
            if ((this.b_half_opened_files & LETTERS_BY_FIELD_ID[bKingFieldID]) != 0L && (this.opened_files & LETTERS_BY_FIELD_ID[bKingFieldID]) == 0L) {
                ++this.b_king_semi_opened_files_count_own;
            }
            if ((this.b_half_opened_files & LETTERS_RIGHT_BY_FIELD_ID[bKingFieldID]) != 0L && (this.opened_files & LETTERS_RIGHT_BY_FIELD_ID[bKingFieldID]) == 0L) {
                ++this.b_king_semi_opened_files_count_own;
            }
        }
        if ((this.b_king_verticals & this.w_half_opened_files) != 0L) {
            if ((this.w_half_opened_files & LETTERS_LEFT_BY_FIELD_ID[bKingFieldID]) != 0L && (this.opened_files & LETTERS_LEFT_BY_FIELD_ID[bKingFieldID]) == 0L) {
                ++this.b_king_semi_opened_files_count_op;
            }
            if ((this.w_half_opened_files & LETTERS_BY_FIELD_ID[bKingFieldID]) != 0L && (this.opened_files & LETTERS_BY_FIELD_ID[bKingFieldID]) == 0L) {
                ++this.b_king_semi_opened_files_count_op;
            }
            if ((this.w_half_opened_files & LETTERS_RIGHT_BY_FIELD_ID[bKingFieldID]) != 0L && (this.opened_files & LETTERS_RIGHT_BY_FIELD_ID[bKingFieldID]) == 0L) {
                ++this.b_king_semi_opened_files_count_op;
            }
        }
    }

    private Pawn getForFilling(int colour) {
        if (colour == 0) {
            return this.w_pawns[this.w_count++];
        }
        return this.b_pawns[this.b_count++];
    }

    private static int fillIslands(long pawns) {
        boolean hasPawn;
        int switchesCount = 0;
        boolean hasPawn_backup = false;
        boolean bl = hasPawn = (pawns & 0x8080808080808080L) != 0L;
        if (hasPawn && !hasPawn_backup) {
            ++switchesCount;
        }
        hasPawn_backup = hasPawn;
        boolean bl2 = hasPawn = (pawns & 0x4040404040404040L) != 0L;
        if (hasPawn && !hasPawn_backup) {
            ++switchesCount;
        }
        hasPawn_backup = hasPawn;
        boolean bl3 = hasPawn = (pawns & 0x2020202020202020L) != 0L;
        if (hasPawn && !hasPawn_backup) {
            ++switchesCount;
        }
        hasPawn_backup = hasPawn;
        boolean bl4 = hasPawn = (pawns & 0x1010101010101010L) != 0L;
        if (hasPawn && !hasPawn_backup) {
            ++switchesCount;
        }
        hasPawn_backup = hasPawn;
        boolean bl5 = hasPawn = (pawns & 0x808080808080808L) != 0L;
        if (hasPawn && !hasPawn_backup) {
            ++switchesCount;
        }
        hasPawn_backup = hasPawn;
        boolean bl6 = hasPawn = (pawns & 0x404040404040404L) != 0L;
        if (hasPawn && !hasPawn_backup) {
            ++switchesCount;
        }
        hasPawn_backup = hasPawn;
        boolean bl7 = hasPawn = (pawns & 0x202020202020202L) != 0L;
        if (hasPawn && !hasPawn_backup) {
            ++switchesCount;
        }
        hasPawn_backup = hasPawn;
        boolean bl8 = hasPawn = (pawns & 0x101010101010101L) != 0L;
        if (hasPawn && !hasPawn_backup) {
            ++switchesCount;
        }
        int result = switchesCount;
        return result;
    }

    public int getBCount() {
        return this.b_count;
    }

    public Pawn[] getBPawns() {
        return this.b_pawns;
    }

    public int getWCount() {
        return this.w_count;
    }

    public Pawn[] getWPawns() {
        return this.w_pawns;
    }

    public int getBIslandsCount() {
        return this.b_islands_count;
    }

    public int getWIslandsCount() {
        return this.w_islands_count;
    }

    public long getOpenedFiles() {
        return this.opened_files;
    }

    public long getWHalfOpenedFiles() {
        return this.w_half_opened_files;
    }

    public long getWKingVerticals() {
        return this.w_king_verticals;
    }

    public long getBHalfOpenedFiles() {
        return this.b_half_opened_files;
    }

    public long getBKingVerticals() {
        return this.b_king_verticals;
    }

    public int getWKingOpenedFiles() {
        return this.w_king_opened_files_count;
    }

    public int getBKingOpenedFiles() {
        return this.b_king_opened_files_count;
    }

    public int getWKingSemiOwnOpenedFiles() {
        return this.w_king_semi_opened_files_count_own;
    }

    public int getBKingSemiOwnOpenedFiles() {
        return this.b_king_semi_opened_files_count_own;
    }

    public int getWKingSemiOpOpenedFiles() {
        return this.w_king_semi_opened_files_count_op;
    }

    public int getBKingSemiOpOpenedFiles() {
        return this.b_king_semi_opened_files_count_op;
    }

    public int getBWeakFields() {
        return Utils.countBits(this.b_weak_fields);
    }

    public int getWWeakFields() {
        return Utils.countBits(this.w_weak_fields);
    }

    public long getBattacks() {
        return this.b_attacks;
    }

    public long getWattacks() {
        return this.w_attacks;
    }

    public int[] getBpstKnight() {
        return this.b_pstKnight;
    }

    public int[] getWpstKnight() {
        return this.w_pstKnight;
    }

    public int[] getBpstBishop() {
        return this.b_pstBishop;
    }

    public int[] getWpstBishop() {
        return this.w_pstBishop;
    }

    public int[] getBpstRook() {
        return this.b_pstRooks;
    }

    public int[] getWpstRook() {
        return this.w_pstRooks;
    }

    public Pawn[] getBPassed() {
        return this.b_passed;
    }

    public int getBPassedCount() {
        return this.b_passed_count;
    }

    public Pawn[] getWPassed() {
        return this.w_passed;
    }

    public int getWPassedCount() {
        return this.w_passed_count;
    }

    public int getBUnstoppablePasserRank() {
        return this.b_unstoppablePasser_rank;
    }

    public int getWUnstoppablePasserRank() {
        return this.w_unstoppablePasser_rank;
    }

    public int getBKingFieldID() {
        return this.bKingFieldID;
    }

    public int getWKingFieldID() {
        return this.wKingFieldID;
    }

    public int getWMaxPassedRank() {
        return this.w_max_passed_rank;
    }

    public int getBMaxPassedRank() {
        return this.b_max_passed_rank;
    }

    public long getWspace() {
        return this.w_space;
    }

    public long getBspace() {
        return this.b_space;
    }
}

