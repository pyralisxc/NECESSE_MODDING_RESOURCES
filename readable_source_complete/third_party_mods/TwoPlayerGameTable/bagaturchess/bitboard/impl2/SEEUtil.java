/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl2;

import bagaturchess.bitboard.impl2.Bitboard;
import bagaturchess.bitboard.impl2.ChessBoard;
import bagaturchess.bitboard.impl2.ChessConstants;
import bagaturchess.bitboard.impl2.MagicUtil;
import bagaturchess.bitboard.impl2.MoveUtil;

public class SEEUtil {
    private static int getSmallestAttackSeeMove(ChessBoard cb, int colorToMove, int toIndex, long allPieces, long slidingMask) {
        long attackMove = ChessConstants.PAWN_ATTACKS[1 - colorToMove][toIndex] & cb.getPieces(colorToMove, 1) & allPieces & Bitboard.RANK_NON_PROMOTION[colorToMove];
        if (attackMove != 0L) {
            return MoveUtil.createSeeAttackMove(attackMove, 1);
        }
        attackMove = cb.getPieces(colorToMove, 2) & ChessConstants.KNIGHT_MOVES[toIndex] & allPieces;
        if (attackMove != 0L) {
            return MoveUtil.createSeeAttackMove(attackMove, 2);
        }
        if ((cb.getPieces(colorToMove, 3) & slidingMask) != 0L && (attackMove = cb.getPieces(colorToMove, 3) & MagicUtil.getBishopMoves(toIndex, allPieces) & allPieces) != 0L) {
            return MoveUtil.createSeeAttackMove(attackMove, 3);
        }
        if ((cb.getPieces(colorToMove, 4) & slidingMask) != 0L && (attackMove = cb.getPieces(colorToMove, 4) & MagicUtil.getRookMoves(toIndex, allPieces) & allPieces) != 0L) {
            return MoveUtil.createSeeAttackMove(attackMove, 4);
        }
        if ((cb.getPieces(colorToMove, 5) & slidingMask) != 0L && (attackMove = cb.getPieces(colorToMove, 5) & MagicUtil.getQueenMoves(toIndex, allPieces) & allPieces) != 0L) {
            return MoveUtil.createSeeAttackMove(attackMove, 5);
        }
        if ((cb.getPieces(colorToMove, 1) & Bitboard.RANK_PROMOTION[colorToMove]) != 0L && (attackMove = ChessConstants.PAWN_ATTACKS[1 - colorToMove][toIndex] & cb.getPieces(colorToMove, 1) & allPieces & Bitboard.RANK_PROMOTION[colorToMove]) != 0L) {
            return MoveUtil.createPromotionAttack(5, Long.numberOfTrailingZeros(attackMove), toIndex, 0);
        }
        attackMove = cb.getPieces(colorToMove, 6) & ChessConstants.KING_MOVES[toIndex];
        if (attackMove != 0L) {
            return MoveUtil.createSeeAttackMove(attackMove, 6);
        }
        return 0;
    }

    private static int getSeeScore(ChessBoard cb, int colorToMove, int toIndex, int attackedPieceIndex, long allPieces, long slidingMask) {
        int move = SEEUtil.getSmallestAttackSeeMove(cb, colorToMove, toIndex, allPieces, slidingMask);
        if (move == 0) {
            return 0;
        }
        if (attackedPieceIndex == 6) {
            return 3000;
        }
        slidingMask &= (allPieces ^= ChessConstants.POWER_LOOKUP[MoveUtil.getFromIndex(move)]);
        if (MoveUtil.isPromotion(move)) {
            return Math.max(0, ChessConstants.PROMOTION_SCORE_SEE[5] + ChessConstants.MATERIAL_SEE[attackedPieceIndex] - SEEUtil.getSeeScore(cb, 1 - colorToMove, toIndex, 5, allPieces, slidingMask));
        }
        return Math.max(0, ChessConstants.MATERIAL_SEE[attackedPieceIndex] - SEEUtil.getSeeScore(cb, 1 - colorToMove, toIndex, MoveUtil.getSourcePieceIndex(move), allPieces, slidingMask));
    }

    public static int getSeeCaptureScore(ChessBoard cb, int move) {
        int index = MoveUtil.getToIndex(move);
        long allPieces = cb.all_pieces & (ChessConstants.POWER_LOOKUP[MoveUtil.getFromIndex(move)] ^ 0xFFFFFFFFFFFFFFFFL);
        long slidingMask = MagicUtil.getQueenMovesEmptyBoard(index) & allPieces;
        if (MoveUtil.isPromotion(move)) {
            return ChessConstants.PROMOTION_SCORE_SEE[MoveUtil.getMoveType(move)] + ChessConstants.MATERIAL_SEE[MoveUtil.getAttackedPieceIndex(move)] - SEEUtil.getSeeScore(cb, 1 - cb.color_to_move, index, MoveUtil.getMoveType(move), allPieces, slidingMask);
        }
        return ChessConstants.MATERIAL_SEE[MoveUtil.getAttackedPieceIndex(move)] - SEEUtil.getSeeScore(cb, 1 - cb.color_to_move, index, MoveUtil.getSourcePieceIndex(move), allPieces, slidingMask);
    }

    public static int getSeeFieldScore(ChessBoard cb, int squareID) {
        long allPieces = cb.all_pieces & (ChessConstants.POWER_LOOKUP[squareID] ^ 0xFFFFFFFFFFFFFFFFL);
        long slidingMask = MagicUtil.getQueenMovesEmptyBoard(squareID) & allPieces;
        return -SEEUtil.getSeeScore(cb, 1 - cb.color_to_move, squareID, cb.piece_indexes[squareID], allPieces, slidingMask);
    }
}

