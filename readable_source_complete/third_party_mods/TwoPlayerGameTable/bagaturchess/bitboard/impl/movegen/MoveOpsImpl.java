/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.movegen;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IMoveOps;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.bitboard.impl.movelist.BaseMoveList;

public class MoveOpsImpl
implements IMoveOps {
    private IBitBoard board;

    public MoveOpsImpl(IBitBoard _board) {
        this.board = _board;
    }

    @Override
    public boolean isCapture(int move) {
        return MoveInt.isCapture(move);
    }

    @Override
    public boolean isPromotion(int move) {
        return MoveInt.isPromotion(move);
    }

    @Override
    public boolean isCaptureOrPromotion(int move) {
        return this.isCapture(move) || this.isPromotion(move);
    }

    @Override
    public boolean isEnpassant(int move) {
        return MoveInt.isEnpassant(move);
    }

    @Override
    public boolean isCastling(int move) {
        return MoveInt.isCastling(move);
    }

    @Override
    public int getFigurePID(int move) {
        return MoveInt.getFigurePID(move);
    }

    @Override
    public int getToFieldID(int move) {
        return MoveInt.getToFieldID(move);
    }

    @Override
    public int getFigureType(int move) {
        return MoveInt.getFigureType(move);
    }

    @Override
    public boolean isCastlingKingSide(int move) {
        return MoveInt.isCastleKingSide(move);
    }

    @Override
    public boolean isCastlingQueenSide(int move) {
        return MoveInt.isCastleQueenSide(move);
    }

    @Override
    public int getFromFieldID(int move) {
        return MoveInt.getFromFieldID(move);
    }

    @Override
    public int getPromotionFigureType(int move) {
        return MoveInt.getPromotionFigureType(move);
    }

    @Override
    public int getCapturedFigureType(int cur_move) {
        return MoveInt.getCapturedFigureType(cur_move);
    }

    @Override
    public String moveToString(int move) {
        StringBuilder result = new StringBuilder();
        this.moveToString(move, result);
        return result.toString();
    }

    @Override
    public int stringToMove(String move) {
        return this.uciStrToMove(this.board, move);
    }

    @Override
    public int getToField_File(int move) {
        return Fields.LETTERS[this.getToFieldID(move)];
    }

    @Override
    public int getToField_Rank(int move) {
        return Fields.DIGITS[this.getToFieldID(move)];
    }

    @Override
    public int getFromField_File(int move) {
        return Fields.LETTERS[this.getFromFieldID(move)];
    }

    @Override
    public int getFromField_Rank(int move) {
        return Fields.DIGITS[this.getFromFieldID(move)];
    }

    private int uciStrToMove(IBitBoard bitboard, String moveStr) {
        int fromFieldID = Fields.getFieldID(moveStr.substring(0, 2));
        int toFieldID = Fields.getFieldID(moveStr.substring(2, 4));
        BaseMoveList mlist = new BaseMoveList();
        if (bitboard.isInCheck()) {
            bitboard.genKingEscapes(mlist);
        } else {
            bitboard.genAllMoves(mlist);
        }
        int cur_move = 0;
        while ((cur_move = mlist.next()) != 0) {
            if (fromFieldID != MoveInt.getFromFieldID(cur_move) || toFieldID != MoveInt.getToFieldID(cur_move)) continue;
            if (MoveInt.isPromotion(cur_move)) {
                if (moveStr.endsWith("q")) {
                    if (MoveInt.getPromotionFigureType(cur_move) != 5) continue;
                    return cur_move;
                }
                if (moveStr.endsWith("r")) {
                    if (MoveInt.getPromotionFigureType(cur_move) != 4) continue;
                    return cur_move;
                }
                if (moveStr.endsWith("b")) {
                    if (MoveInt.getPromotionFigureType(cur_move) != 3) continue;
                    return cur_move;
                }
                if (moveStr.endsWith("n")) {
                    if (MoveInt.getPromotionFigureType(cur_move) != 2) continue;
                    return cur_move;
                }
                throw new IllegalStateException(moveStr);
            }
            return cur_move;
        }
        throw new IllegalStateException(String.valueOf(bitboard) + "\r\n moveStr=" + moveStr);
    }

    @Override
    public final void moveToString(int move, StringBuilder result) {
        if (move == -1) {
            throw new IllegalStateException("move=" + move);
        }
        if (move == 0) {
            result.append("OOOO");
            return;
        }
        result.append(Fields.ALL_ORDERED_NAMES[Fields.IDX_2_ORDERED_A1H1[this.getFromFieldID(move)]]);
        result.append(Fields.ALL_ORDERED_NAMES[Fields.IDX_2_ORDERED_A1H1[this.getToFieldID(move)]]);
        if (this.isPromotion(move)) {
            int promotionFigureType = this.getPromotionFigureType(move);
            result.append(Figures.TYPES_SIGN[promotionFigureType].toLowerCase());
        }
    }
}

