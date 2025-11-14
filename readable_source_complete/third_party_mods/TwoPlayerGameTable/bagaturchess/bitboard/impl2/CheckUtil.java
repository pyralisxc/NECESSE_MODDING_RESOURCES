/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl2;

import bagaturchess.bitboard.impl2.ChessBoard;
import bagaturchess.bitboard.impl2.ChessConstants;
import bagaturchess.bitboard.impl2.MagicUtil;

public final class CheckUtil {
    public static boolean isInCheck(ChessBoard cb, int color) {
        int kingIndex = cb.getKingIndex(color);
        return (cb.getPieces(1 - color, 2) & ChessConstants.KNIGHT_MOVES[kingIndex] | (cb.getPieces(1 - color, 4) | cb.getPieces(1 - color, 5)) & MagicUtil.getRookMoves(kingIndex, cb.all_pieces) | (cb.getPieces(1 - color, 3) | cb.getPieces(1 - color, 5)) & MagicUtil.getBishopMoves(kingIndex, cb.all_pieces) | cb.getPieces(1 - color, 1) & ChessConstants.PAWN_ATTACKS[color][kingIndex]) != 0L;
    }

    public static long getCheckingPieces(ChessBoard cb) {
        int kingIndex = cb.getKingIndexOfSideToMove();
        return cb.getPiecesOfSideNotToMove(2) & ChessConstants.KNIGHT_MOVES[kingIndex] | (cb.getPiecesOfSideNotToMove(4) | cb.getPiecesOfSideNotToMove(5)) & MagicUtil.getRookMoves(kingIndex, cb.all_pieces) | (cb.getPiecesOfSideNotToMove(3) | cb.getPiecesOfSideNotToMove(5)) & MagicUtil.getBishopMoves(kingIndex, cb.all_pieces) | cb.getPiecesOfSideNotToMove(1) & ChessConstants.PAWN_ATTACKS[cb.color_to_move][kingIndex];
    }

    public static long getCheckingPieces(ChessBoard cb, int sourcePieceIndex) {
        switch (sourcePieceIndex) {
            case 1: {
                return cb.getPiecesOfSideNotToMove(1) & ChessConstants.PAWN_ATTACKS[cb.color_to_move][cb.getKingIndexOfSideToMove()];
            }
            case 2: {
                return cb.getPiecesOfSideNotToMove(2) & ChessConstants.KNIGHT_MOVES[cb.getKingIndexOfSideToMove()];
            }
            case 3: {
                return cb.getPiecesOfSideNotToMove(3) & MagicUtil.getBishopMoves(cb.getKingIndexOfSideToMove(), cb.all_pieces);
            }
            case 4: {
                return cb.getPiecesOfSideNotToMove(4) & MagicUtil.getRookMoves(cb.getKingIndexOfSideToMove(), cb.all_pieces);
            }
            case 5: {
                return cb.getPiecesOfSideNotToMove(5) & MagicUtil.getQueenMoves(cb.getKingIndexOfSideToMove(), cb.all_pieces);
            }
        }
        return 0L;
    }

    public static boolean isInCheck(ChessBoard cb, int kingIndex, int colorToMove, long allPieces) {
        return (cb.getPiecesOfSideNotToMove(2) & ChessConstants.KNIGHT_MOVES[kingIndex] | (cb.getPiecesOfSideNotToMove(4) | cb.getPiecesOfSideNotToMove(5)) & MagicUtil.getRookMoves(kingIndex, allPieces) | (cb.getPiecesOfSideNotToMove(3) | cb.getPiecesOfSideNotToMove(5)) & MagicUtil.getBishopMoves(kingIndex, allPieces) | cb.getPiecesOfSideNotToMove(1) & ChessConstants.PAWN_ATTACKS[colorToMove][kingIndex]) != 0L;
    }

    public static boolean isInCheckIncludingKing(ChessBoard cb, int kingIndex, int colorToMove, long allPieces) {
        return (cb.getPiecesOfSideNotToMove(2) & ChessConstants.KNIGHT_MOVES[kingIndex] | (cb.getPiecesOfSideNotToMove(4) | cb.getPiecesOfSideNotToMove(5)) & MagicUtil.getRookMoves(kingIndex, allPieces) | (cb.getPiecesOfSideNotToMove(3) | cb.getPiecesOfSideNotToMove(5)) & MagicUtil.getBishopMoves(kingIndex, allPieces) | cb.getPiecesOfSideNotToMove(1) & ChessConstants.PAWN_ATTACKS[colorToMove][kingIndex] | cb.getPiecesOfSideNotToMove(6) & ChessConstants.KING_MOVES[kingIndex]) != 0L;
    }
}

