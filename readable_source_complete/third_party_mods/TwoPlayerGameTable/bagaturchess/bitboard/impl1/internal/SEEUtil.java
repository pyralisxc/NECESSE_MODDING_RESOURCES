/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl1.internal;

import bagaturchess.bitboard.impl1.internal.Bitboard;
import bagaturchess.bitboard.impl1.internal.ChessBoard;
import bagaturchess.bitboard.impl1.internal.EvalConstants;
import bagaturchess.bitboard.impl1.internal.MagicUtil;
import bagaturchess.bitboard.impl1.internal.MoveUtil;
import bagaturchess.bitboard.impl1.internal.StaticMoves;
import bagaturchess.bitboard.impl1.internal.Util;

public class SEEUtil {
    private static int getSmallestAttackSeeMove(long[] pieces, int colorToMove, int toIndex, long allPieces, long slidingMask) {
        long attackMove = StaticMoves.PAWN_ATTACKS[1 - colorToMove][toIndex] & pieces[1] & allPieces & Bitboard.RANK_NON_PROMOTION[colorToMove];
        if (attackMove != 0L) {
            return MoveUtil.createSeeAttackMove(attackMove, 1);
        }
        attackMove = pieces[2] & StaticMoves.KNIGHT_MOVES[toIndex] & allPieces;
        if (attackMove != 0L) {
            return MoveUtil.createSeeAttackMove(attackMove, 2);
        }
        if ((pieces[3] & slidingMask) != 0L && (attackMove = pieces[3] & MagicUtil.getBishopMoves(toIndex, allPieces) & allPieces) != 0L) {
            return MoveUtil.createSeeAttackMove(attackMove, 3);
        }
        if ((pieces[4] & slidingMask) != 0L && (attackMove = pieces[4] & MagicUtil.getRookMoves(toIndex, allPieces) & allPieces) != 0L) {
            return MoveUtil.createSeeAttackMove(attackMove, 4);
        }
        if ((pieces[5] & slidingMask) != 0L && (attackMove = pieces[5] & MagicUtil.getQueenMoves(toIndex, allPieces) & allPieces) != 0L) {
            return MoveUtil.createSeeAttackMove(attackMove, 5);
        }
        if ((pieces[1] & Bitboard.RANK_PROMOTION[colorToMove]) != 0L && (attackMove = StaticMoves.PAWN_ATTACKS[1 - colorToMove][toIndex] & pieces[1] & allPieces & Bitboard.RANK_PROMOTION[colorToMove]) != 0L) {
            return MoveUtil.createPromotionAttack(5, Long.numberOfTrailingZeros(attackMove), toIndex, 0);
        }
        attackMove = pieces[6] & StaticMoves.KING_MOVES[toIndex];
        if (attackMove != 0L) {
            return MoveUtil.createSeeAttackMove(attackMove, 6);
        }
        return 0;
    }

    private static int getSeeScore(ChessBoard cb, int colorToMove, int toIndex, int attackedPieceIndex, long allPieces, long slidingMask) {
        int move = SEEUtil.getSmallestAttackSeeMove(cb.pieces[colorToMove], colorToMove, toIndex, allPieces, slidingMask);
        if (move == 0) {
            return 0;
        }
        if (attackedPieceIndex == 6) {
            return 3000;
        }
        slidingMask &= (allPieces ^= Util.POWER_LOOKUP[MoveUtil.getFromIndex(move)]);
        if (MoveUtil.isPromotion(move)) {
            return Math.max(0, EvalConstants.PROMOTION_SCORE_SEE[5] + EvalConstants.MATERIAL_SEE[attackedPieceIndex] - SEEUtil.getSeeScore(cb, 1 - colorToMove, toIndex, 5, allPieces, slidingMask));
        }
        return Math.max(0, EvalConstants.MATERIAL_SEE[attackedPieceIndex] - SEEUtil.getSeeScore(cb, 1 - colorToMove, toIndex, MoveUtil.getSourcePieceIndex(move), allPieces, slidingMask));
    }

    public static int getSeeCaptureScore(ChessBoard cb, int move) {
        int index = MoveUtil.getToIndex(move);
        long allPieces = cb.allPieces & (Util.POWER_LOOKUP[MoveUtil.getFromIndex(move)] ^ 0xFFFFFFFFFFFFFFFFL);
        long slidingMask = MagicUtil.getQueenMovesEmptyBoard(index) & allPieces;
        if (MoveUtil.isPromotion(move)) {
            return EvalConstants.PROMOTION_SCORE_SEE[MoveUtil.getMoveType(move)] + EvalConstants.MATERIAL_SEE[MoveUtil.getAttackedPieceIndex(move)] - SEEUtil.getSeeScore(cb, cb.colorToMoveInverse, index, MoveUtil.getMoveType(move), allPieces, slidingMask);
        }
        return EvalConstants.MATERIAL_SEE[MoveUtil.getAttackedPieceIndex(move)] - SEEUtil.getSeeScore(cb, cb.colorToMoveInverse, index, MoveUtil.getSourcePieceIndex(move), allPieces, slidingMask);
    }

    public static int getSeeFieldScore(ChessBoard cb, int squareID) {
        long allPieces = cb.allPieces & (Util.POWER_LOOKUP[squareID] ^ 0xFFFFFFFFFFFFFFFFL);
        long slidingMask = MagicUtil.getQueenMovesEmptyBoard(squareID) & allPieces;
        return -SEEUtil.getSeeScore(cb, cb.colorToMoveInverse, squareID, cb.pieceIndexes[squareID], allPieces, slidingMask);
    }
}

