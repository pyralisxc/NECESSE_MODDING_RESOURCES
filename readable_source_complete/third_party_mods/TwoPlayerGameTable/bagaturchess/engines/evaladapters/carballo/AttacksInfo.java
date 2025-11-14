/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.engines.evaladapters.carballo;

import bagaturchess.engines.evaladapters.carballo.BitboardAttacks;
import bagaturchess.engines.evaladapters.carballo.BitboardUtils;
import bagaturchess.engines.evaladapters.carballo.IBoard;

public class AttacksInfo {
    public static final int W = 0;
    public static final int B = 1;
    BitboardAttacks bbAttacks;
    public long[] attackedSquaresAlsoPinned = new long[]{0L, 0L};
    public long[] attackedSquares = new long[]{0L, 0L};
    public long[] attacksFromSquare = new long[64];
    public long[] pawnAttacks = new long[]{0L, 0L};
    public long[] knightAttacks = new long[]{0L, 0L};
    public long[] bishopAttacks = new long[]{0L, 0L};
    public long[] rookAttacks = new long[]{0L, 0L};
    public long[] queenAttacks = new long[]{0L, 0L};
    public long[] kingAttacks = new long[]{0L, 0L};
    public int[] kingIndex = new int[]{0, 0};
    public long[] pinnedMobility = new long[64];
    public long[] bishopAttacksKing = new long[]{0L, 0L};
    public long[] rookAttacksKing = new long[]{0L, 0L};
    public long[] mayPin = new long[]{0L, 0L};
    public long piecesGivingCheck;
    public long interposeCheckSquares;
    public long pinnedPieces;

    public AttacksInfo() {
        this.bbAttacks = BitboardAttacks.getInstance();
    }

    private void checkPinnerRay(long ray, long mines, long attackerSlider) {
        long pinner = ray & attackerSlider;
        if (pinner != 0L) {
            long pinned = ray & mines;
            this.pinnedPieces |= pinned;
            this.pinnedMobility[BitboardUtils.square2Index((long)pinned)] = ray;
        }
    }

    private void checkPinnerBishop(int kingIndex, long bishopSliderAttacks, long all, long mines, long otherBishopsOrQueens) {
        if ((bishopSliderAttacks & mines) == 0L || (this.bbAttacks.bishop[kingIndex] & otherBishopsOrQueens) == 0L) {
            return;
        }
        long xray = this.bbAttacks.getBishopAttacks(kingIndex, all & (mines & bishopSliderAttacks ^ 0xFFFFFFFFFFFFFFFFL));
        if ((xray & (bishopSliderAttacks ^ 0xFFFFFFFFFFFFFFFFL) & otherBishopsOrQueens) != 0L) {
            int rank = kingIndex >> 3;
            int file = 7 - kingIndex & 7;
            this.checkPinnerRay(xray & BitboardUtils.RANKS_UPWARDS[rank] & BitboardUtils.FILES_LEFT[file], mines, otherBishopsOrQueens);
            this.checkPinnerRay(xray & BitboardUtils.RANKS_UPWARDS[rank] & BitboardUtils.FILES_RIGHT[file], mines, otherBishopsOrQueens);
            this.checkPinnerRay(xray & BitboardUtils.RANKS_DOWNWARDS[rank] & BitboardUtils.FILES_LEFT[file], mines, otherBishopsOrQueens);
            this.checkPinnerRay(xray & BitboardUtils.RANKS_DOWNWARDS[rank] & BitboardUtils.FILES_RIGHT[file], mines, otherBishopsOrQueens);
        }
    }

    private void checkPinnerRook(int kingIndex, long rookSliderAttacks, long all, long mines, long otherRooksOrQueens) {
        if ((rookSliderAttacks & mines) == 0L || (this.bbAttacks.rook[kingIndex] & otherRooksOrQueens) == 0L) {
            return;
        }
        long xray = this.bbAttacks.getRookAttacks(kingIndex, all & (mines & rookSliderAttacks ^ 0xFFFFFFFFFFFFFFFFL));
        if ((xray & (rookSliderAttacks ^ 0xFFFFFFFFFFFFFFFFL) & otherRooksOrQueens) != 0L) {
            int rank = kingIndex >> 3;
            int file = 7 - kingIndex & 7;
            this.checkPinnerRay(xray & BitboardUtils.RANKS_UPWARDS[rank], mines, otherRooksOrQueens);
            this.checkPinnerRay(xray & BitboardUtils.FILES_LEFT[file], mines, otherRooksOrQueens);
            this.checkPinnerRay(xray & BitboardUtils.RANKS_DOWNWARDS[rank], mines, otherRooksOrQueens);
            this.checkPinnerRay(xray & BitboardUtils.FILES_RIGHT[file], mines, otherRooksOrQueens);
        }
    }

    public void build(IBoard board) {
        long all = board.getAll();
        long mines = board.getColourToMove() == 0 ? board.getWhites() : board.getBlacks();
        long myKing = board.getKings() & mines;
        int us = board.getColourToMove() == 0 ? 0 : 1;
        this.attackedSquaresAlsoPinned[0] = 0L;
        this.attackedSquaresAlsoPinned[1] = 0L;
        this.pawnAttacks[0] = 0L;
        this.pawnAttacks[1] = 0L;
        this.knightAttacks[0] = 0L;
        this.knightAttacks[1] = 0L;
        this.bishopAttacks[0] = 0L;
        this.bishopAttacks[1] = 0L;
        this.rookAttacks[0] = 0L;
        this.rookAttacks[1] = 0L;
        this.queenAttacks[0] = 0L;
        this.queenAttacks[1] = 0L;
        this.kingAttacks[0] = 0L;
        this.kingAttacks[1] = 0L;
        this.mayPin[0] = 0L;
        this.mayPin[1] = 0L;
        this.pinnedPieces = 0L;
        this.piecesGivingCheck = 0L;
        this.interposeCheckSquares = 0L;
        this.kingIndex[0] = BitboardUtils.square2Index(board.getKings() & board.getWhites());
        this.kingIndex[1] = BitboardUtils.square2Index(board.getKings() & board.getBlacks());
        this.bishopAttacksKing[0] = this.bbAttacks.getBishopAttacks(this.kingIndex[0], all);
        this.checkPinnerBishop(this.kingIndex[0], this.bishopAttacksKing[0], all, board.getWhites(), (board.getBishops() | board.getQueens()) & board.getBlacks());
        this.bishopAttacksKing[1] = this.bbAttacks.getBishopAttacks(this.kingIndex[1], all);
        this.checkPinnerBishop(this.kingIndex[1], this.bishopAttacksKing[1], all, board.getBlacks(), (board.getBishops() | board.getQueens()) & board.getWhites());
        this.rookAttacksKing[0] = this.bbAttacks.getRookAttacks(this.kingIndex[0], all);
        this.checkPinnerRook(this.kingIndex[0], this.rookAttacksKing[0], all, board.getWhites(), (board.getRooks() | board.getQueens()) & board.getBlacks());
        this.rookAttacksKing[1] = this.bbAttacks.getRookAttacks(this.kingIndex[1], all);
        this.checkPinnerRook(this.kingIndex[1], this.rookAttacksKing[1], all, board.getBlacks(), (board.getRooks() | board.getQueens()) & board.getWhites());
        long square = 1L;
        for (int index = 0; index < 64; ++index) {
            if ((square & all) != 0L) {
                int color = (board.getWhites() & square) != 0L ? 0 : 1;
                long pinnedSquares = (square & this.pinnedPieces) != 0L ? this.pinnedMobility[index] : -1L;
                long pieceAttacks = 0L;
                if ((square & board.getPawns()) != 0L) {
                    pieceAttacks = this.bbAttacks.pawn[color][index];
                    if ((square & mines) == 0L && (pieceAttacks & myKing) != 0L) {
                        this.piecesGivingCheck |= square;
                    }
                    int n = color;
                    this.pawnAttacks[n] = this.pawnAttacks[n] | pieceAttacks & pinnedSquares;
                } else if ((square & board.getKnights()) != 0L) {
                    pieceAttacks = this.bbAttacks.knight[index];
                    if ((square & mines) == 0L && (pieceAttacks & myKing) != 0L) {
                        this.piecesGivingCheck |= square;
                    }
                    int n = color;
                    this.knightAttacks[n] = this.knightAttacks[n] | pieceAttacks & pinnedSquares;
                } else if ((square & board.getBishops()) != 0L) {
                    pieceAttacks = this.bbAttacks.getBishopAttacks(index, all);
                    if ((square & mines) == 0L && (pieceAttacks & myKing) != 0L) {
                        this.piecesGivingCheck |= square;
                        this.interposeCheckSquares |= pieceAttacks & this.bishopAttacksKing[us];
                    }
                    int n = color;
                    this.bishopAttacks[n] = this.bishopAttacks[n] | pieceAttacks & pinnedSquares;
                    int n2 = color;
                    this.mayPin[n2] = this.mayPin[n2] | all & pieceAttacks;
                } else if ((square & board.getRooks()) != 0L) {
                    pieceAttacks = this.bbAttacks.getRookAttacks(index, all);
                    if ((square & mines) == 0L && (pieceAttacks & myKing) != 0L) {
                        this.piecesGivingCheck |= square;
                        this.interposeCheckSquares |= pieceAttacks & this.rookAttacksKing[us];
                    }
                    int n = color;
                    this.rookAttacks[n] = this.rookAttacks[n] | pieceAttacks & pinnedSquares;
                    int n3 = color;
                    this.mayPin[n3] = this.mayPin[n3] | all & pieceAttacks;
                } else if ((square & board.getQueens()) != 0L) {
                    long bishopSliderAttacks = this.bbAttacks.getBishopAttacks(index, all);
                    if ((square & mines) == 0L && (bishopSliderAttacks & myKing) != 0L) {
                        this.piecesGivingCheck |= square;
                        this.interposeCheckSquares |= bishopSliderAttacks & this.bishopAttacksKing[us];
                    }
                    long rookSliderAttacks = this.bbAttacks.getRookAttacks(index, all);
                    if ((square & mines) == 0L && (rookSliderAttacks & myKing) != 0L) {
                        this.piecesGivingCheck |= square;
                        this.interposeCheckSquares |= rookSliderAttacks & this.rookAttacksKing[us];
                    }
                    pieceAttacks = rookSliderAttacks | bishopSliderAttacks;
                    int n = color;
                    this.queenAttacks[n] = this.queenAttacks[n] | pieceAttacks & pinnedSquares;
                    int n4 = color;
                    this.mayPin[n4] = this.mayPin[n4] | all & pieceAttacks;
                } else if ((square & board.getKings()) != 0L) {
                    pieceAttacks = this.bbAttacks.king[index];
                    int n = color;
                    this.kingAttacks[n] = this.kingAttacks[n] | pieceAttacks;
                }
                int n = color;
                this.attackedSquaresAlsoPinned[n] = this.attackedSquaresAlsoPinned[n] | pieceAttacks;
                this.attacksFromSquare[index] = pieceAttacks & pinnedSquares;
            } else {
                this.attacksFromSquare[index] = 0L;
            }
            square <<= 1;
        }
        this.attackedSquares[0] = this.pawnAttacks[0] | this.knightAttacks[0] | this.bishopAttacks[0] | this.rookAttacks[0] | this.queenAttacks[0] | this.kingAttacks[0];
        this.attackedSquares[1] = this.pawnAttacks[1] | this.knightAttacks[1] | this.bishopAttacks[1] | this.rookAttacks[1] | this.queenAttacks[1] | this.kingAttacks[1];
    }
}

