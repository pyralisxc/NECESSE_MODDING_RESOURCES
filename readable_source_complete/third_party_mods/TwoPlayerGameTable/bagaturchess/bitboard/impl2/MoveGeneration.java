/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl2;

import bagaturchess.bitboard.api.IInternalMoveList;
import bagaturchess.bitboard.impl2.Bitboard;
import bagaturchess.bitboard.impl2.CastlingUtil;
import bagaturchess.bitboard.impl2.ChessBoard;
import bagaturchess.bitboard.impl2.ChessConstants;
import bagaturchess.bitboard.impl2.MagicUtil;
import bagaturchess.bitboard.impl2.MoveUtil;

public class MoveGeneration {
    public static final void generateMoves(ChessBoard cb, IInternalMoveList list) {
        block0 : switch (Long.bitCount(cb.checking_pieces)) {
            case 0: {
                MoveGeneration.generateNotInCheckMoves(cb, list);
                break;
            }
            case 1: {
                switch (cb.piece_indexes[Long.numberOfTrailingZeros(cb.checking_pieces)]) {
                    case 1: 
                    case 2: {
                        MoveGeneration.addKingMoves(cb, list);
                        break block0;
                    }
                }
                MoveGeneration.generateOutOfSlidingCheckMoves(cb, list);
                break;
            }
            default: {
                MoveGeneration.addKingMoves(cb, list);
            }
        }
    }

    public static final void generateAttacks(ChessBoard cb, IInternalMoveList list) {
        switch (Long.bitCount(cb.checking_pieces)) {
            case 0: {
                MoveGeneration.generateNotInCheckAttacks(cb, list);
                break;
            }
            case 1: {
                MoveGeneration.generateOutOfCheckAttacks(cb, list);
                break;
            }
            default: {
                MoveGeneration.addKingAttacks(cb, list);
            }
        }
    }

    private static final void addMove(int move, IInternalMoveList list) {
        list.reserved_add(move);
    }

    private static void generateNotInCheckMoves(ChessBoard cb, IInternalMoveList list) {
        MoveGeneration.addKingMoves(cb, list);
        MoveGeneration.addQueenMoves(cb.getPiecesOfSideToMove(5) & (cb.pinned_pieces ^ 0xFFFFFFFFFFFFFFFFL), cb.all_pieces, cb.empty_spaces, list);
        MoveGeneration.addRookMoves(cb.getPiecesOfSideToMove(4) & (cb.pinned_pieces ^ 0xFFFFFFFFFFFFFFFFL), cb.all_pieces, cb.empty_spaces, list);
        MoveGeneration.addBishopMoves(cb.getPiecesOfSideToMove(3) & (cb.pinned_pieces ^ 0xFFFFFFFFFFFFFFFFL), cb.all_pieces, cb.empty_spaces, list);
        MoveGeneration.addNightMoves(cb.getPiecesOfSideToMove(2) & (cb.pinned_pieces ^ 0xFFFFFFFFFFFFFFFFL), cb.empty_spaces, list);
        MoveGeneration.addPawnMoves(cb.getPiecesOfSideToMove(1) & (cb.pinned_pieces ^ 0xFFFFFFFFFFFFFFFFL), cb, cb.empty_spaces, list);
        block6: for (long piece = cb.getPiecesOfSideToMove_All() & cb.pinned_pieces; piece != 0L; piece &= piece - 1L) {
            switch (cb.piece_indexes[Long.numberOfTrailingZeros(piece)]) {
                case 1: {
                    MoveGeneration.addPawnMoves(Long.lowestOneBit(piece), cb, cb.empty_spaces & ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][cb.getKingIndexOfSideToMove()], list);
                    continue block6;
                }
                case 3: {
                    MoveGeneration.addBishopMoves(Long.lowestOneBit(piece), cb.all_pieces, cb.empty_spaces & ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][cb.getKingIndexOfSideToMove()], list);
                    continue block6;
                }
                case 4: {
                    MoveGeneration.addRookMoves(Long.lowestOneBit(piece), cb.all_pieces, cb.empty_spaces & ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][cb.getKingIndexOfSideToMove()], list);
                    continue block6;
                }
                case 5: {
                    MoveGeneration.addQueenMoves(Long.lowestOneBit(piece), cb.all_pieces, cb.empty_spaces & ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][cb.getKingIndexOfSideToMove()], list);
                }
            }
        }
    }

    private static void generateOutOfSlidingCheckMoves(ChessBoard cb, IInternalMoveList list) {
        long inBetween = ChessConstants.IN_BETWEEN[cb.getKingIndexOfSideToMove()][Long.numberOfTrailingZeros(cb.checking_pieces)];
        if (inBetween != 0L) {
            MoveGeneration.addNightMoves(cb.getPiecesOfSideToMove(2) & (cb.pinned_pieces ^ 0xFFFFFFFFFFFFFFFFL), inBetween, list);
            MoveGeneration.addBishopMoves(cb.getPiecesOfSideToMove(3) & (cb.pinned_pieces ^ 0xFFFFFFFFFFFFFFFFL), cb.all_pieces, inBetween, list);
            MoveGeneration.addRookMoves(cb.getPiecesOfSideToMove(4) & (cb.pinned_pieces ^ 0xFFFFFFFFFFFFFFFFL), cb.all_pieces, inBetween, list);
            MoveGeneration.addQueenMoves(cb.getPiecesOfSideToMove(5) & (cb.pinned_pieces ^ 0xFFFFFFFFFFFFFFFFL), cb.all_pieces, inBetween, list);
            MoveGeneration.addPawnMoves(cb.getPiecesOfSideToMove(1) & (cb.pinned_pieces ^ 0xFFFFFFFFFFFFFFFFL), cb, inBetween, list);
        }
        MoveGeneration.addKingMoves(cb, list);
    }

    private static void generateNotInCheckAttacks(ChessBoard cb, IInternalMoveList list) {
        long enemies = cb.getPiecesOfSideNotToMove_All();
        MoveGeneration.addEpAttacks(cb, list);
        MoveGeneration.addPawnAttacksAndPromotions(cb.getPiecesOfSideToMove(1) & (cb.pinned_pieces ^ 0xFFFFFFFFFFFFFFFFL), cb, enemies, cb.empty_spaces, list);
        MoveGeneration.addNightAttacks(cb.getPiecesOfSideToMove(2) & (cb.pinned_pieces ^ 0xFFFFFFFFFFFFFFFFL), cb.piece_indexes, enemies, list);
        MoveGeneration.addRookAttacks(cb.getPiecesOfSideToMove(4) & (cb.pinned_pieces ^ 0xFFFFFFFFFFFFFFFFL), cb, enemies, list);
        MoveGeneration.addBishopAttacks(cb.getPiecesOfSideToMove(3) & (cb.pinned_pieces ^ 0xFFFFFFFFFFFFFFFFL), cb, enemies, list);
        MoveGeneration.addQueenAttacks(cb.getPiecesOfSideToMove(5) & (cb.pinned_pieces ^ 0xFFFFFFFFFFFFFFFFL), cb, enemies, list);
        MoveGeneration.addKingAttacks(cb, list);
        block6: for (long piece = cb.getPiecesOfSideToMove_All() & cb.pinned_pieces; piece != 0L; piece &= piece - 1L) {
            switch (cb.piece_indexes[Long.numberOfTrailingZeros(piece)]) {
                case 1: {
                    MoveGeneration.addPawnAttacksAndPromotions(Long.lowestOneBit(piece), cb, enemies & ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][cb.getKingIndexOfSideToMove()], 0L, list);
                    continue block6;
                }
                case 3: {
                    MoveGeneration.addBishopAttacks(Long.lowestOneBit(piece), cb, enemies & ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][cb.getKingIndexOfSideToMove()], list);
                    continue block6;
                }
                case 4: {
                    MoveGeneration.addRookAttacks(Long.lowestOneBit(piece), cb, enemies & ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][cb.getKingIndexOfSideToMove()], list);
                    continue block6;
                }
                case 5: {
                    MoveGeneration.addQueenAttacks(Long.lowestOneBit(piece), cb, enemies & ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][cb.getKingIndexOfSideToMove()], list);
                }
            }
        }
    }

    private static void generateOutOfCheckAttacks(ChessBoard cb, IInternalMoveList list) {
        MoveGeneration.addEpAttacks(cb, list);
        MoveGeneration.addPawnAttacksAndPromotions(cb.getPiecesOfSideToMove(1) & (cb.pinned_pieces ^ 0xFFFFFFFFFFFFFFFFL), cb, cb.checking_pieces, cb.empty_spaces, list);
        MoveGeneration.addNightAttacks(cb.getPiecesOfSideToMove(2) & (cb.pinned_pieces ^ 0xFFFFFFFFFFFFFFFFL), cb.piece_indexes, cb.checking_pieces, list);
        MoveGeneration.addBishopAttacks(cb.getPiecesOfSideToMove(3) & (cb.pinned_pieces ^ 0xFFFFFFFFFFFFFFFFL), cb, cb.checking_pieces, list);
        MoveGeneration.addRookAttacks(cb.getPiecesOfSideToMove(4) & (cb.pinned_pieces ^ 0xFFFFFFFFFFFFFFFFL), cb, cb.checking_pieces, list);
        MoveGeneration.addQueenAttacks(cb.getPiecesOfSideToMove(5) & (cb.pinned_pieces ^ 0xFFFFFFFFFFFFFFFFL), cb, cb.checking_pieces, list);
        MoveGeneration.addKingAttacks(cb, list);
    }

    private static void addPawnAttacksAndPromotions(long pawns, ChessBoard cb, long enemies, long emptySpaces, IInternalMoveList list) {
        if (pawns == 0L) {
            return;
        }
        if (cb.color_to_move == 0) {
            int fromIndex;
            long piece;
            for (piece = pawns & Bitboard.RANK_NON_PROMOTION[0] & Bitboard.getBlackPawnAttacks(enemies); piece != 0L; piece &= piece - 1L) {
                fromIndex = Long.numberOfTrailingZeros(piece);
                for (long moves = ChessConstants.PAWN_ATTACKS[0][fromIndex] & enemies; moves != 0L; moves &= moves - 1L) {
                    int toIndex = Long.numberOfTrailingZeros(moves);
                    MoveGeneration.addMove(MoveUtil.createAttackMove(fromIndex, toIndex, 1, cb.piece_indexes[toIndex]), list);
                }
            }
            for (piece = pawns & 0xFF000000000000L; piece != 0L; piece &= piece - 1L) {
                fromIndex = Long.numberOfTrailingZeros(piece);
                if ((Long.lowestOneBit(piece) << 8 & emptySpaces) != 0L) {
                    MoveGeneration.addPromotionMove(fromIndex, fromIndex + 8, list);
                }
                MoveGeneration.addPromotionAttacks(ChessConstants.PAWN_ATTACKS[0][fromIndex] & enemies, fromIndex, cb.piece_indexes, list);
            }
        } else {
            int fromIndex;
            long piece;
            for (piece = pawns & Bitboard.RANK_NON_PROMOTION[1] & Bitboard.getWhitePawnAttacks(enemies); piece != 0L; piece &= piece - 1L) {
                fromIndex = Long.numberOfTrailingZeros(piece);
                for (long moves = ChessConstants.PAWN_ATTACKS[1][fromIndex] & enemies; moves != 0L; moves &= moves - 1L) {
                    int toIndex = Long.numberOfTrailingZeros(moves);
                    MoveGeneration.addMove(MoveUtil.createAttackMove(fromIndex, toIndex, 1, cb.piece_indexes[toIndex]), list);
                }
            }
            for (piece = pawns & 0xFF00L; piece != 0L; piece &= piece - 1L) {
                fromIndex = Long.numberOfTrailingZeros(piece);
                if ((Long.lowestOneBit(piece) >>> 8 & emptySpaces) != 0L) {
                    MoveGeneration.addPromotionMove(fromIndex, fromIndex - 8, list);
                }
                MoveGeneration.addPromotionAttacks(ChessConstants.PAWN_ATTACKS[1][fromIndex] & enemies, fromIndex, cb.piece_indexes, list);
            }
        }
    }

    private static void addBishopAttacks(long piece, ChessBoard cb, long possiblePositions, IInternalMoveList list) {
        while (piece != 0L) {
            int fromIndex = Long.numberOfTrailingZeros(piece);
            for (long moves = MagicUtil.getBishopMoves(fromIndex, cb.all_pieces) & possiblePositions; moves != 0L; moves &= moves - 1L) {
                int toIndex = Long.numberOfTrailingZeros(moves);
                MoveGeneration.addMove(MoveUtil.createAttackMove(fromIndex, toIndex, 3, cb.piece_indexes[toIndex]), list);
            }
            piece &= piece - 1L;
        }
    }

    private static void addRookAttacks(long piece, ChessBoard cb, long possiblePositions, IInternalMoveList list) {
        while (piece != 0L) {
            int fromIndex = Long.numberOfTrailingZeros(piece);
            for (long moves = MagicUtil.getRookMoves(fromIndex, cb.all_pieces) & possiblePositions; moves != 0L; moves &= moves - 1L) {
                int toIndex = Long.numberOfTrailingZeros(moves);
                MoveGeneration.addMove(MoveUtil.createAttackMove(fromIndex, toIndex, 4, cb.piece_indexes[toIndex]), list);
            }
            piece &= piece - 1L;
        }
    }

    private static void addQueenAttacks(long piece, ChessBoard cb, long possiblePositions, IInternalMoveList list) {
        while (piece != 0L) {
            int fromIndex = Long.numberOfTrailingZeros(piece);
            for (long moves = MagicUtil.getQueenMoves(fromIndex, cb.all_pieces) & possiblePositions; moves != 0L; moves &= moves - 1L) {
                int toIndex = Long.numberOfTrailingZeros(moves);
                MoveGeneration.addMove(MoveUtil.createAttackMove(fromIndex, toIndex, 5, cb.piece_indexes[toIndex]), list);
            }
            piece &= piece - 1L;
        }
    }

    private static void addBishopMoves(long piece, long allPieces, long possiblePositions, IInternalMoveList list) {
        while (piece != 0L) {
            int fromIndex = Long.numberOfTrailingZeros(piece);
            for (long moves = MagicUtil.getBishopMoves(fromIndex, allPieces) & possiblePositions; moves != 0L; moves &= moves - 1L) {
                MoveGeneration.addMove(MoveUtil.createMove(fromIndex, Long.numberOfTrailingZeros(moves), 3), list);
            }
            piece &= piece - 1L;
        }
    }

    private static void addQueenMoves(long piece, long allPieces, long possiblePositions, IInternalMoveList list) {
        while (piece != 0L) {
            int fromIndex = Long.numberOfTrailingZeros(piece);
            for (long moves = MagicUtil.getQueenMoves(fromIndex, allPieces) & possiblePositions; moves != 0L; moves &= moves - 1L) {
                MoveGeneration.addMove(MoveUtil.createMove(fromIndex, Long.numberOfTrailingZeros(moves), 5), list);
            }
            piece &= piece - 1L;
        }
    }

    private static void addRookMoves(long piece, long allPieces, long possiblePositions, IInternalMoveList list) {
        while (piece != 0L) {
            int fromIndex = Long.numberOfTrailingZeros(piece);
            for (long moves = MagicUtil.getRookMoves(fromIndex, allPieces) & possiblePositions; moves != 0L; moves &= moves - 1L) {
                MoveGeneration.addMove(MoveUtil.createMove(fromIndex, Long.numberOfTrailingZeros(moves), 4), list);
            }
            piece &= piece - 1L;
        }
    }

    private static void addNightMoves(long piece, long possiblePositions, IInternalMoveList list) {
        while (piece != 0L) {
            int fromIndex = Long.numberOfTrailingZeros(piece);
            for (long moves = ChessConstants.KNIGHT_MOVES[fromIndex] & possiblePositions; moves != 0L; moves &= moves - 1L) {
                MoveGeneration.addMove(MoveUtil.createMove(fromIndex, Long.numberOfTrailingZeros(moves), 2), list);
            }
            piece &= piece - 1L;
        }
    }

    private static void addPawnMoves(long pawns, ChessBoard cb, long possiblePositions, IInternalMoveList list) {
        if (pawns == 0L) {
            return;
        }
        if (cb.color_to_move == 0) {
            long piece;
            for (piece = pawns & possiblePositions >>> 8 & 0xFFFFFFFFFF00L; piece != 0L; piece &= piece - 1L) {
                MoveGeneration.addMove(MoveUtil.createWhitePawnMove(Long.numberOfTrailingZeros(piece)), list);
            }
            for (piece = pawns & possiblePositions >>> 16 & 0xFF00L; piece != 0L; piece &= piece - 1L) {
                if ((cb.empty_spaces & Long.lowestOneBit(piece) << 8) == 0L) continue;
                MoveGeneration.addMove(MoveUtil.createWhitePawn2Move(Long.numberOfTrailingZeros(piece)), list);
            }
        } else {
            long piece;
            for (piece = pawns & possiblePositions << 8 & 0xFFFFFFFFFF0000L; piece != 0L; piece &= piece - 1L) {
                MoveGeneration.addMove(MoveUtil.createBlackPawnMove(Long.numberOfTrailingZeros(piece)), list);
            }
            for (piece = pawns & possiblePositions << 16 & 0xFF000000000000L; piece != 0L; piece &= piece - 1L) {
                if ((cb.empty_spaces & Long.lowestOneBit(piece) >>> 8) == 0L) continue;
                MoveGeneration.addMove(MoveUtil.createBlackPawn2Move(Long.numberOfTrailingZeros(piece)), list);
            }
        }
    }

    private static void addKingMoves(ChessBoard cb, IInternalMoveList list) {
        int fromIndex = cb.getKingIndexOfSideToMove();
        for (long moves = ChessConstants.KING_MOVES[fromIndex] & cb.empty_spaces; moves != 0L; moves &= moves - 1L) {
            MoveGeneration.addMove(MoveUtil.createMove(fromIndex, Long.numberOfTrailingZeros(moves), 6), list);
        }
        if (cb.checking_pieces == 0L) {
            for (long castlingIndexes = CastlingUtil.getCastlingIndexes(cb.color_to_move, cb.castling_rights, cb.castling_config); castlingIndexes != 0L; castlingIndexes &= castlingIndexes - 1L) {
                int toIndex_king = Long.numberOfTrailingZeros(castlingIndexes);
                if (!CastlingUtil.isValidCastlingMove(cb, fromIndex, toIndex_king)) continue;
                MoveGeneration.addMove(MoveUtil.createCastlingMove(fromIndex, toIndex_king), list);
            }
        }
    }

    private static void addKingAttacks(ChessBoard cb, IInternalMoveList list) {
        int fromIndex = cb.getKingIndexOfSideToMove();
        for (long moves = ChessConstants.KING_MOVES[fromIndex] & cb.getPiecesOfSideNotToMove_All(); moves != 0L; moves &= moves - 1L) {
            int toIndex = Long.numberOfTrailingZeros(moves);
            MoveGeneration.addMove(MoveUtil.createAttackMove(fromIndex, toIndex, 6, cb.piece_indexes[toIndex]), list);
        }
    }

    private static void addNightAttacks(long piece, int[] pieceIndexes, long possiblePositions, IInternalMoveList list) {
        while (piece != 0L) {
            int fromIndex = Long.numberOfTrailingZeros(piece);
            for (long moves = ChessConstants.KNIGHT_MOVES[fromIndex] & possiblePositions; moves != 0L; moves &= moves - 1L) {
                int toIndex = Long.numberOfTrailingZeros(moves);
                MoveGeneration.addMove(MoveUtil.createAttackMove(fromIndex, toIndex, 2, pieceIndexes[toIndex]), list);
            }
            piece &= piece - 1L;
        }
    }

    private static void addEpAttacks(ChessBoard cb, IInternalMoveList list) {
        if (cb.ep_index == 0) {
            return;
        }
        for (long piece = cb.getPiecesOfSideToMove(1) & ChessConstants.PAWN_ATTACKS[1 - cb.color_to_move][cb.ep_index]; piece != 0L; piece &= piece - 1L) {
            MoveGeneration.addMove(MoveUtil.createEPMove(Long.numberOfTrailingZeros(piece), cb.ep_index), list);
        }
    }

    private static void addPromotionMove(int fromIndex, int toIndex, IInternalMoveList list) {
        MoveGeneration.addMove(MoveUtil.createPromotionMove(5, fromIndex, toIndex), list);
        MoveGeneration.addMove(MoveUtil.createPromotionMove(2, fromIndex, toIndex), list);
        MoveGeneration.addMove(MoveUtil.createPromotionMove(3, fromIndex, toIndex), list);
        MoveGeneration.addMove(MoveUtil.createPromotionMove(4, fromIndex, toIndex), list);
    }

    private static void addPromotionAttacks(long moves, int fromIndex, int[] piece_indexes, IInternalMoveList list) {
        while (moves != 0L) {
            int toIndex = Long.numberOfTrailingZeros(moves);
            MoveGeneration.addMove(MoveUtil.createPromotionAttack(5, fromIndex, toIndex, piece_indexes[toIndex]), list);
            MoveGeneration.addMove(MoveUtil.createPromotionAttack(2, fromIndex, toIndex, piece_indexes[toIndex]), list);
            MoveGeneration.addMove(MoveUtil.createPromotionAttack(3, fromIndex, toIndex, piece_indexes[toIndex]), list);
            MoveGeneration.addMove(MoveUtil.createPromotionAttack(4, fromIndex, toIndex, piece_indexes[toIndex]), list);
            moves &= moves - 1L;
        }
    }
}

