/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl1.internal;

import bagaturchess.bitboard.impl1.internal.ChessBoard;
import bagaturchess.bitboard.impl1.internal.MagicUtil;
import bagaturchess.bitboard.impl1.internal.StaticMoves;

public final class CheckUtil {
    public static boolean isInCheck(ChessBoard cb, int color) {
        int colorInverse = 1 - color;
        int kingIndex = cb.kingIndex[color];
        return (cb.pieces[colorInverse][2] & StaticMoves.KNIGHT_MOVES[kingIndex] | (cb.pieces[colorInverse][4] | cb.pieces[colorInverse][5]) & MagicUtil.getRookMoves(kingIndex, cb.allPieces) | (cb.pieces[colorInverse][3] | cb.pieces[colorInverse][5]) & MagicUtil.getBishopMoves(kingIndex, cb.allPieces) | cb.pieces[colorInverse][1] & StaticMoves.PAWN_ATTACKS[color][kingIndex]) != 0L;
    }

    public static long getCheckingPieces(ChessBoard cb) {
        int kingIndex = cb.kingIndex[cb.colorToMove];
        return cb.pieces[cb.colorToMoveInverse][2] & StaticMoves.KNIGHT_MOVES[kingIndex] | (cb.pieces[cb.colorToMoveInverse][4] | cb.pieces[cb.colorToMoveInverse][5]) & MagicUtil.getRookMoves(kingIndex, cb.allPieces) | (cb.pieces[cb.colorToMoveInverse][3] | cb.pieces[cb.colorToMoveInverse][5]) & MagicUtil.getBishopMoves(kingIndex, cb.allPieces) | cb.pieces[cb.colorToMoveInverse][1] & StaticMoves.PAWN_ATTACKS[cb.colorToMove][kingIndex];
    }

    public static long getCheckingPieces(ChessBoard cb, int sourcePieceIndex) {
        switch (sourcePieceIndex) {
            case 1: {
                return cb.pieces[cb.colorToMoveInverse][1] & StaticMoves.PAWN_ATTACKS[cb.colorToMove][cb.kingIndex[cb.colorToMove]];
            }
            case 2: {
                return cb.pieces[cb.colorToMoveInverse][2] & StaticMoves.KNIGHT_MOVES[cb.kingIndex[cb.colorToMove]];
            }
            case 3: {
                return cb.pieces[cb.colorToMoveInverse][3] & MagicUtil.getBishopMoves(cb.kingIndex[cb.colorToMove], cb.allPieces);
            }
            case 4: {
                return cb.pieces[cb.colorToMoveInverse][4] & MagicUtil.getRookMoves(cb.kingIndex[cb.colorToMove], cb.allPieces);
            }
            case 5: {
                return cb.pieces[cb.colorToMoveInverse][5] & MagicUtil.getQueenMoves(cb.kingIndex[cb.colorToMove], cb.allPieces);
            }
        }
        return 0L;
    }

    public static boolean isInCheck(int kingIndex, int colorToMove, long[] enemyPieces, long allPieces) {
        return (enemyPieces[2] & StaticMoves.KNIGHT_MOVES[kingIndex] | (enemyPieces[4] | enemyPieces[5]) & MagicUtil.getRookMoves(kingIndex, allPieces) | (enemyPieces[3] | enemyPieces[5]) & MagicUtil.getBishopMoves(kingIndex, allPieces) | enemyPieces[1] & StaticMoves.PAWN_ATTACKS[colorToMove][kingIndex]) != 0L;
    }

    public static boolean isInCheckIncludingKing(int kingIndex, int colorToMove, long[] enemyPieces, long allPieces, int enemyMajorPieces) {
        if (enemyMajorPieces == 0) {
            return (enemyPieces[1] & StaticMoves.PAWN_ATTACKS[colorToMove][kingIndex] | enemyPieces[6] & StaticMoves.KING_MOVES[kingIndex]) != 0L;
        }
        return (enemyPieces[2] & StaticMoves.KNIGHT_MOVES[kingIndex] | (enemyPieces[4] | enemyPieces[5]) & MagicUtil.getRookMoves(kingIndex, allPieces) | (enemyPieces[3] | enemyPieces[5]) & MagicUtil.getBishopMoves(kingIndex, allPieces) | enemyPieces[1] & StaticMoves.PAWN_ATTACKS[colorToMove][kingIndex] | enemyPieces[6] & StaticMoves.KING_MOVES[kingIndex]) != 0L;
    }

    public static boolean isInCheckIncludingKing(int kingIndex, int colorToMove, long[] enemyPieces, long allPieces) {
        return (enemyPieces[2] & StaticMoves.KNIGHT_MOVES[kingIndex] | (enemyPieces[4] | enemyPieces[5]) & MagicUtil.getRookMoves(kingIndex, allPieces) | (enemyPieces[3] | enemyPieces[5]) & MagicUtil.getBishopMoves(kingIndex, allPieces) | enemyPieces[1] & StaticMoves.PAWN_ATTACKS[colorToMove][kingIndex] | enemyPieces[6] & StaticMoves.KING_MOVES[kingIndex]) != 0L;
    }
}

