/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl1;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.common.MoveListener;
import bagaturchess.bitboard.impl.utils.VarStatistic;

public class NNUE_Input
implements MoveListener {
    public static final int INPUT_SIZE = 768;
    public static final int SHIFT_KING = 0;
    public static final int SHIFT_PAWNS = 64;
    public static final int SHIFT_KNIGHTS = 128;
    public static final int SHIFT_BISHOP = 192;
    public static final int SHIFT_ROOK = 256;
    public static final int SHIFT_QUEEN = 320;
    private static final boolean CHECK_CONSISTENCY = true;
    private float[] inputs = new float[768];
    private IBitBoard board;

    public NNUE_Input(IBitBoard _board) {
        this.board = _board;
    }

    public final float[] getInputs() {
        return this.inputs;
    }

    @Override
    public final void preForwardMove(int color, int move) {
        this.move(move, color);
    }

    @Override
    public final void postForwardMove(int color, int move) {
    }

    @Override
    public final void preBackwardMove(int color, int move) {
    }

    @Override
    public final void postBackwardMove(int color, int move) {
        this.unmove(move, color);
    }

    @Override
    public final void addPiece_Special(int color, int type) {
    }

    @Override
    public final void initially_addPiece(int color, int type, long bb_pieces) {
        while (bb_pieces != 0L) {
            int square_id = Long.numberOfTrailingZeros(bb_pieces);
            this.inputs[NNUE_Input.getInputIndex((int)color, (int)type, (int)square_id)] = 1.0f;
            bb_pieces &= bb_pieces - 1L;
        }
    }

    public final void move(int move, int color) {
        block11: {
            int toFieldID;
            block12: {
                block10: {
                    int pieceType = this.board.getMoveOps().getFigureType(move);
                    int fromFieldID = this.board.getMoveOps().getFromFieldID(move);
                    toFieldID = this.board.getMoveOps().getToFieldID(move);
                    if (!this.board.getMoveOps().isCastling(move) || fromFieldID != toFieldID) {
                        this.setInputAt(color, pieceType, fromFieldID, 0.0f);
                        if (!this.board.getMoveOps().isPromotion(move)) {
                            this.setInputAt(color, pieceType, toFieldID, 1.0f);
                        }
                    }
                    if (!this.board.getMoveOps().isEnpassant(move)) break block10;
                    int ep_index = this.board.getEnpassantSquareID();
                    int captured_pawn_index = ep_index + (1 - color == 0 ? 8 : -8);
                    this.setInputAt(1 - color, 1, captured_pawn_index, 0.0f);
                    break block11;
                }
                if (!this.board.getMoveOps().isCastling(move)) break block12;
                switch (toFieldID) {
                    case 1: {
                        this.setInputAt(color, 4, this.board.getCastlingConfig().from_SquareID_rook_kingside_w, 0.0f);
                        this.setInputAt(color, 4, 2, 1.0f);
                        break block11;
                    }
                    case 5: {
                        this.setInputAt(color, 4, this.board.getCastlingConfig().from_SquareID_rook_queenside_w, 0.0f);
                        this.setInputAt(color, 4, 4, 1.0f);
                        break block11;
                    }
                    case 57: {
                        this.setInputAt(color, 4, this.board.getCastlingConfig().from_SquareID_rook_kingside_b, 0.0f);
                        this.setInputAt(color, 4, 58, 1.0f);
                        break block11;
                    }
                    case 61: {
                        this.setInputAt(color, 4, this.board.getCastlingConfig().from_SquareID_rook_queenside_b, 0.0f);
                        this.setInputAt(color, 4, 60, 1.0f);
                        break block11;
                    }
                    default: {
                        throw new RuntimeException("Incorrect king index: " + toFieldID);
                    }
                }
            }
            if (this.board.getMoveOps().isCapture(move)) {
                int capType = this.board.getMoveOps().getCapturedFigureType(move);
                this.setInputAt(1 - color, capType, toFieldID, 0.0f);
            }
            if (this.board.getMoveOps().isPromotion(move)) {
                int promType = this.board.getMoveOps().getPromotionFigureType(move);
                this.setInputAt(color, promType, toFieldID, 1.0f);
            }
        }
    }

    public final void unmove(int move, int color) {
        block11: {
            int toFieldID;
            block12: {
                block10: {
                    int pieceType = this.board.getMoveOps().getFigureType(move);
                    int fromFieldID = this.board.getMoveOps().getFromFieldID(move);
                    toFieldID = this.board.getMoveOps().getToFieldID(move);
                    if (!this.board.getMoveOps().isCastling(move) || fromFieldID != toFieldID) {
                        this.setInputAt(color, pieceType, fromFieldID, 1.0f);
                        if (!this.board.getMoveOps().isPromotion(move)) {
                            this.setInputAt(color, pieceType, toFieldID, 0.0f);
                        }
                    }
                    if (!this.board.getMoveOps().isEnpassant(move)) break block10;
                    int ep_index = this.board.getEnpassantSquareID();
                    int captured_pawn_index = ep_index + (1 - color == 0 ? 8 : -8);
                    this.setInputAt(1 - color, 1, captured_pawn_index, 1.0f);
                    break block11;
                }
                if (!this.board.getMoveOps().isCastling(move)) break block12;
                switch (toFieldID) {
                    case 1: {
                        this.setInputAt(color, 4, 2, 0.0f);
                        this.setInputAt(color, 4, this.board.getCastlingConfig().from_SquareID_rook_kingside_w, 1.0f);
                        break block11;
                    }
                    case 5: {
                        this.setInputAt(color, 4, 4, 0.0f);
                        this.setInputAt(color, 4, this.board.getCastlingConfig().from_SquareID_rook_queenside_w, 1.0f);
                        break block11;
                    }
                    case 57: {
                        this.setInputAt(color, 4, 58, 0.0f);
                        this.setInputAt(color, 4, this.board.getCastlingConfig().from_SquareID_rook_kingside_b, 1.0f);
                        break block11;
                    }
                    case 61: {
                        this.setInputAt(color, 4, 60, 0.0f);
                        this.setInputAt(color, 4, this.board.getCastlingConfig().from_SquareID_rook_queenside_b, 1.0f);
                        break block11;
                    }
                    default: {
                        throw new RuntimeException("Incorrect king castling to-index: " + toFieldID);
                    }
                }
            }
            if (this.board.getMoveOps().isCapture(move)) {
                int capType = this.board.getMoveOps().getCapturedFigureType(move);
                this.setInputAt(1 - color, capType, toFieldID, 1.0f);
            }
            if (this.board.getMoveOps().isPromotion(move)) {
                int promType = this.board.getMoveOps().getPromotionFigureType(move);
                this.setInputAt(color, promType, toFieldID, 0.0f);
            }
        }
    }

    public static final int getInputIndex(int color, int type, int square_id) {
        int index = color == 0 ? 0 : 384;
        switch (type) {
            case 1: {
                return index + 64 + square_id;
            }
            case 2: {
                return index + 128 + square_id;
            }
            case 3: {
                return index + 192 + square_id;
            }
            case 4: {
                return index + 256 + square_id;
            }
            case 5: {
                return index + 320 + square_id;
            }
            case 6: {
                return index + 0 + square_id;
            }
        }
        throw new IllegalStateException("type=" + type);
    }

    private final void setInputAt(int color, int piece_type, int square_id, float signal) {
        int index = NNUE_Input.getInputIndex(color, piece_type, square_id);
        if (signal == 0.0f) {
            if (this.inputs[index] != 1.0f) {
                throw new IllegalStateException("signal=" + signal + ", color=" + color + ", piece_type=" + piece_type + ", square_id=" + square_id);
            }
        } else if (signal == 1.0f) {
            if (this.inputs[index] != 0.0f) {
                throw new IllegalStateException("signal=" + signal + ", color=" + color + ", piece_type=" + piece_type + ", square_id=" + square_id);
            }
        } else {
            throw new IllegalStateException("signal=" + signal + ", color=" + color + ", piece_type=" + piece_type + ", square_id=" + square_id);
        }
        this.inputs[index] = signal;
    }

    public static final void printWeights(Double[] nnue_weights) {
        System.out.println("nnue_weights=" + nnue_weights.length);
        for (int color = 0; color < 2; ++color) {
            for (int piece_type = 1; piece_type <= 6; ++piece_type) {
                System.out.println("******************************************************************************************************************************************");
                System.out.println("COLOR: " + color + ", TYPE: " + piece_type);
                VarStatistic stats = new VarStatistic();
                for (int rank = 7; rank >= 0; --rank) {
                    Object board_line = "";
                    for (int file = 0; file < 8; ++file) {
                        int square_id = 8 * rank + file;
                        int nnue_index = NNUE_Input.getInputIndex(color, piece_type, square_id);
                        double nnue_weight = nnue_weights[nnue_index];
                        stats.addValue(nnue_weight);
                        board_line = (String)board_line + nnue_weight + ", ";
                    }
                    System.out.println((String)board_line);
                }
                System.out.println("STATS: " + String.valueOf(stats));
                System.out.println("******************************************************************************************************************************************");
            }
        }
        System.exit(0);
    }
}

