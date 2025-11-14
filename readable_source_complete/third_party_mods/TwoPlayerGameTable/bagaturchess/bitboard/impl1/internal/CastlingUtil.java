/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl1.internal;

import bagaturchess.bitboard.impl1.internal.CastlingConfig;
import bagaturchess.bitboard.impl1.internal.CheckUtil;
import bagaturchess.bitboard.impl1.internal.ChessBoard;
import bagaturchess.bitboard.impl1.internal.Util;

public final class CastlingUtil {
    public static long getCastlingIndexes(int colorToMove, int castlingRights, CastlingConfig castlingConfig) {
        if (castlingRights == 0) {
            return 0L;
        }
        if (colorToMove == 0) {
            switch (castlingRights) {
                case 0: 
                case 1: 
                case 2: 
                case 3: {
                    return 0L;
                }
                case 4: 
                case 5: 
                case 6: 
                case 7: {
                    return 32L;
                }
                case 8: 
                case 9: 
                case 10: 
                case 11: {
                    return 2L;
                }
                case 12: 
                case 13: 
                case 14: 
                case 15: {
                    return 34L;
                }
            }
            throw new IllegalStateException("castlingRights=" + castlingRights);
        }
        if (colorToMove == 1) {
            switch (castlingRights) {
                case 0: 
                case 4: 
                case 8: 
                case 12: {
                    return 0L;
                }
                case 1: 
                case 5: 
                case 9: 
                case 13: {
                    return 0x2000000000000000L;
                }
                case 2: 
                case 6: 
                case 10: 
                case 14: {
                    return 0x200000000000000L;
                }
                case 3: 
                case 7: 
                case 11: 
                case 15: {
                    return 0x2200000000000000L;
                }
            }
            throw new IllegalStateException("castlingRights=" + castlingRights);
        }
        throw new IllegalStateException("colorToMove=" + colorToMove);
    }

    public static int getRookMovedOrAttackedCastlingRights(int castlingRights, int rook_square_id, CastlingConfig castlingConfig) {
        if (rook_square_id == castlingConfig.from_SquareID_rook_kingside_w) {
            return castlingRights & 7;
        }
        if (rook_square_id == castlingConfig.from_SquareID_rook_queenside_w) {
            return castlingRights & 0xB;
        }
        if (rook_square_id == castlingConfig.from_SquareID_rook_kingside_b) {
            return castlingRights & 0xD;
        }
        if (rook_square_id == castlingConfig.from_SquareID_rook_queenside_b) {
            return castlingRights & 0xE;
        }
        return castlingRights;
    }

    public static int getKingMovedCastlingRights(int castlingRights, int color, CastlingConfig castlingConfig) {
        if (color == 0) {
            return castlingRights & 3;
        }
        if (color == 1) {
            return castlingRights & 0xC;
        }
        throw new RuntimeException("Incorrect color: " + color);
    }

    public static boolean isValidCastlingMove(ChessBoard cb, int fromIndex, int toIndex) {
        int king_color;
        long bb_all_pieces_no_king_no_rook;
        long bb_KingInBetween;
        long bb_RookInBetween;
        if (cb.checkingPieces != 0L) {
            return false;
        }
        if (toIndex == 1) {
            bb_RookInBetween = cb.castlingConfig.bb_inbetween_rook_kingside_w;
            bb_KingInBetween = cb.castlingConfig.bb_inbetween_king_kingside_w;
            bb_all_pieces_no_king_no_rook = cb.allPieces & ((Util.POWER_LOOKUP[cb.castlingConfig.from_SquareID_king_w] | Util.POWER_LOOKUP[cb.castlingConfig.from_SquareID_rook_kingside_w]) ^ 0xFFFFFFFFFFFFFFFFL);
            if ((Util.POWER_LOOKUP[cb.castlingConfig.from_SquareID_king_w] & cb.pieces[0][6]) == 0L) {
                throw new IllegalStateException();
            }
            if ((Util.POWER_LOOKUP[cb.castlingConfig.from_SquareID_rook_kingside_w] & cb.pieces[0][4]) == 0L) {
                throw new IllegalStateException();
            }
        } else if (toIndex == 5) {
            bb_RookInBetween = cb.castlingConfig.bb_inbetween_rook_queenside_w;
            bb_KingInBetween = cb.castlingConfig.bb_inbetween_king_queenside_w;
            bb_all_pieces_no_king_no_rook = cb.allPieces & ((Util.POWER_LOOKUP[cb.castlingConfig.from_SquareID_king_w] | Util.POWER_LOOKUP[cb.castlingConfig.from_SquareID_rook_queenside_w]) ^ 0xFFFFFFFFFFFFFFFFL);
            if ((Util.POWER_LOOKUP[cb.castlingConfig.from_SquareID_king_w] & cb.pieces[0][6]) == 0L) {
                throw new IllegalStateException();
            }
            if ((Util.POWER_LOOKUP[cb.castlingConfig.from_SquareID_rook_queenside_w] & cb.pieces[0][4]) == 0L) {
                throw new IllegalStateException();
            }
        } else if (toIndex == 57) {
            bb_RookInBetween = cb.castlingConfig.bb_inbetween_rook_kingside_b;
            bb_KingInBetween = cb.castlingConfig.bb_inbetween_king_kingside_b;
            bb_all_pieces_no_king_no_rook = cb.allPieces & ((Util.POWER_LOOKUP[cb.castlingConfig.from_SquareID_king_b] | Util.POWER_LOOKUP[cb.castlingConfig.from_SquareID_rook_kingside_b]) ^ 0xFFFFFFFFFFFFFFFFL);
            if ((Util.POWER_LOOKUP[cb.castlingConfig.from_SquareID_king_b] & cb.pieces[1][6]) == 0L) {
                throw new IllegalStateException();
            }
            if ((Util.POWER_LOOKUP[cb.castlingConfig.from_SquareID_rook_kingside_b] & cb.pieces[1][4]) == 0L) {
                throw new IllegalStateException();
            }
        } else if (toIndex == 61) {
            bb_RookInBetween = cb.castlingConfig.bb_inbetween_rook_queenside_b;
            bb_KingInBetween = cb.castlingConfig.bb_inbetween_king_queenside_b;
            bb_all_pieces_no_king_no_rook = cb.allPieces & ((Util.POWER_LOOKUP[cb.castlingConfig.from_SquareID_king_b] | Util.POWER_LOOKUP[cb.castlingConfig.from_SquareID_rook_queenside_b]) ^ 0xFFFFFFFFFFFFFFFFL);
            if ((Util.POWER_LOOKUP[cb.castlingConfig.from_SquareID_king_b] & cb.pieces[1][6]) == 0L) {
                throw new IllegalStateException();
            }
            if ((Util.POWER_LOOKUP[cb.castlingConfig.from_SquareID_rook_queenside_b] & cb.pieces[1][4]) == 0L) {
                throw new IllegalStateException();
            }
        } else {
            throw new RuntimeException("Incorrect castling-index: " + toIndex);
        }
        if ((bb_all_pieces_no_king_no_rook & bb_KingInBetween) != 0L || (bb_all_pieces_no_king_no_rook & bb_RookInBetween) != 0L) {
            return false;
        }
        int n = king_color = (Util.POWER_LOOKUP[fromIndex] & cb.pieces[0][6]) != 0L ? 0 : 1;
        while (bb_KingInBetween != 0L) {
            int intermediate_square_id = Long.numberOfTrailingZeros(bb_KingInBetween);
            if (CheckUtil.isInCheckIncludingKing(intermediate_square_id, king_color, cb.pieces[1 - king_color], bb_all_pieces_no_king_no_rook)) {
                return false;
            }
            bb_KingInBetween &= bb_KingInBetween - 1L;
        }
        return true;
    }

    public static final void getRookFromToSquareIDs(ChessBoard cb, int kingToIndex, int[] result) {
        int from;
        int to = switch (kingToIndex) {
            case 1 -> {
                from = cb.castlingConfig.from_SquareID_rook_kingside_w;
                yield 2;
            }
            case 5 -> {
                from = cb.castlingConfig.from_SquareID_rook_queenside_w;
                yield 4;
            }
            case 57 -> {
                from = cb.castlingConfig.from_SquareID_rook_kingside_b;
                yield 58;
            }
            case 61 -> {
                from = cb.castlingConfig.from_SquareID_rook_queenside_b;
                yield 60;
            }
            default -> throw new RuntimeException("Incorrect king castling to-index: " + kingToIndex);
        };
        result[0] = from;
        result[1] = to;
    }
}

