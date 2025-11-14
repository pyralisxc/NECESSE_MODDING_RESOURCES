/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.movegen;

import bagaturchess.bitboard.api.IInternalMoveList;
import bagaturchess.bitboard.impl.Board;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.bitboard.impl.plies.KingPlies;
import bagaturchess.bitboard.impl.plies.checking.Checking;
import bagaturchess.bitboard.impl.plies.specials.Castling;

public class KingMovesGen
extends KingPlies {
    static final int figureType = 6;
    static final int[][] validDirsIDs = ALL_KING_VALID_DIRS;
    static final int[][][] dirsFieldIDs = ALL_KING_DIRS_WITH_FIELD_IDS;
    static final long[][][] dirsBitBoards = ALL_KING_DIRS_WITH_BITBOARDS;

    public static final int genAllMoves(boolean checkAware, Board bitboard, long excludedToFieldsIDs, int figureID, int figureColour, int opponentColour, long fromBitboard, int fromFieldID, long freeBitboard, long allMineBitboard, long allOpponentBitboard, int[] figuresIDsPerFieldsIDs, boolean kingSidePossible, boolean queenSidePossible, long opponentKing, int opponentKingFieldID, IInternalMoveList list, int maxCount) {
        int count = 0;
        long opponentKingMoves = KingPlies.ALL_KING_MOVES[opponentKingFieldID];
        int[] validDirIDs = validDirsIDs[fromFieldID];
        int[][] dirs_ids = dirsFieldIDs[fromFieldID];
        long[][] dirs = dirsBitBoards[fromFieldID];
        for (int dirID : validDirIDs) {
            long toBitboard = dirs[dirID][0];
            if ((excludedToFieldsIDs & toBitboard) != 0L || (toBitboard & allMineBitboard) != 0L || (toBitboard & opponentKing) != 0L || checkAware && (!checkAware || (toBitboard & opponentKingMoves) != 0L)) continue;
            int toFieldID = dirs_ids[dirID][0];
            if (checkAware && Checking.isFieldAttacked(bitboard, opponentColour, figureColour, toBitboard, toFieldID, freeBitboard | fromBitboard, true)) continue;
            if (list != null) {
                if ((toBitboard & allOpponentBitboard) != 0L) {
                    int capturedFigureID = figuresIDsPerFieldsIDs[toFieldID];
                    list.reserved_add(MoveInt.createCapture(figureID, fromFieldID, toFieldID, capturedFigureID));
                } else {
                    list.reserved_add(MoveInt.createNonCapture(figureID, fromFieldID, toFieldID));
                }
            }
            if (++count < maxCount) continue;
            return count;
        }
        if (kingSidePossible) {
            if (list != null) {
                list.reserved_add(MoveInt.createKingSide(Castling.KINGS_PIDS_BY_COLOUR[figureColour], Castling.KING_FROM_FIELD_ID_BY_COLOUR[figureColour], Castling.KING_TO_FIELD_ID_ON_KING_SIDE_BY_COLOUR[figureColour]));
            }
            if (++count >= maxCount) {
                return count;
            }
        }
        if (queenSidePossible) {
            if (list != null) {
                list.reserved_add(MoveInt.createQueenSide(Castling.KINGS_PIDS_BY_COLOUR[figureColour], Castling.KING_FROM_FIELD_ID_BY_COLOUR[figureColour], Castling.KING_TO_FIELD_ID_ON_QUEEN_SIDE_BY_COLOUR[figureColour]));
            }
            if (++count >= maxCount) {
                return count;
            }
        }
        return count;
    }

    public static final int genCaptureMoves(Board bitboard, long excludedToFieldsIDs, int figureID, int figureColour, int opponentColour, long fromBitboard, int fromFieldID, long freeBitboard, long allMineBitboard, long allOpponentBitboard, int[] figuresIDsPerFieldsIDs, long opponentKing, int opponentKingFieldID, IInternalMoveList list, int maxCount) {
        int count = 0;
        long opponentKingMoves = KingPlies.ALL_KING_MOVES[opponentKingFieldID];
        int[] validDirIDs = validDirsIDs[fromFieldID];
        int[][] dirs_ids = dirsFieldIDs[fromFieldID];
        long[][] dirs = dirsBitBoards[fromFieldID];
        for (int dirID : validDirIDs) {
            int toFieldID;
            long toBitboard = dirs[dirID][0];
            if ((excludedToFieldsIDs & toBitboard) != 0L || (toBitboard & allMineBitboard) != 0L || (toBitboard & opponentKing) != 0L || (toBitboard & opponentKingMoves) != 0L || (toBitboard & allOpponentBitboard) == 0L || Checking.isFieldAttacked(bitboard, opponentColour, figureColour, toBitboard, toFieldID = dirs_ids[dirID][0], freeBitboard | fromBitboard, true)) continue;
            if (list != null) {
                int capturedFigureID = figuresIDsPerFieldsIDs[toFieldID];
                list.reserved_add(MoveInt.createCapture(figureID, fromFieldID, toFieldID, capturedFigureID));
            }
            if (++count < maxCount) continue;
            return count;
        }
        return count;
    }

    public static final int genNonCaptureMoves(Board bitboard, long excludedToFieldsIDs, int figureID, int figureColour, int opponentColour, long fromBitboard, int fromFieldID, long freeBitboard, long allMineBitboard, long allOpponentBitboard, boolean kingSidePossible, boolean queenSidePossible, long opponentKing, int opponentKingFieldID, IInternalMoveList list, int maxCount) {
        int count = 0;
        long opponentKingMoves = KingPlies.ALL_KING_MOVES[opponentKingFieldID];
        int[] validDirIDs = validDirsIDs[fromFieldID];
        int[][] dirs_ids = dirsFieldIDs[fromFieldID];
        long[][] dirs = dirsBitBoards[fromFieldID];
        for (int dirID : validDirIDs) {
            int toFieldID;
            long toBitboard = dirs[dirID][0];
            if ((excludedToFieldsIDs & toBitboard) != 0L || (toBitboard & allMineBitboard) != 0L || (toBitboard & opponentKing) != 0L || (toBitboard & opponentKingMoves) != 0L || (toBitboard & allOpponentBitboard) != 0L || Checking.isFieldAttacked(bitboard, opponentColour, figureColour, toBitboard, toFieldID = dirs_ids[dirID][0], freeBitboard | fromBitboard, true)) continue;
            if (list != null) {
                list.reserved_add(MoveInt.createNonCapture(figureID, fromFieldID, toFieldID));
            }
            if (++count < maxCount) continue;
            return count;
        }
        if (kingSidePossible) {
            if (list != null) {
                list.reserved_add(MoveInt.createKingSide(Castling.KINGS_PIDS_BY_COLOUR[figureColour], Castling.KING_FROM_FIELD_ID_BY_COLOUR[figureColour], Castling.KING_TO_FIELD_ID_ON_KING_SIDE_BY_COLOUR[figureColour]));
            }
            if (++count >= maxCount) {
                return count;
            }
        }
        if (queenSidePossible) {
            if (list != null) {
                list.reserved_add(MoveInt.createQueenSide(Castling.KINGS_PIDS_BY_COLOUR[figureColour], Castling.KING_FROM_FIELD_ID_BY_COLOUR[figureColour], Castling.KING_TO_FIELD_ID_ON_QUEEN_SIDE_BY_COLOUR[figureColour]));
            }
            if (++count >= maxCount) {
                return count;
            }
        }
        return count;
    }

    public static final int genCastleSides(int figureColour, boolean kingSidePossible, boolean queenSidePossible, IInternalMoveList list, int maxCount) {
        int count = 0;
        if (kingSidePossible) {
            if (list != null && list != null) {
                list.reserved_add(MoveInt.createKingSide(Castling.KINGS_PIDS_BY_COLOUR[figureColour], Castling.KING_FROM_FIELD_ID_BY_COLOUR[figureColour], Castling.KING_TO_FIELD_ID_ON_KING_SIDE_BY_COLOUR[figureColour]));
            }
            if (++count >= maxCount) {
                return count;
            }
        }
        if (queenSidePossible) {
            if (list != null) {
                list.reserved_add(MoveInt.createQueenSide(Castling.KINGS_PIDS_BY_COLOUR[figureColour], Castling.KING_FROM_FIELD_ID_BY_COLOUR[figureColour], Castling.KING_TO_FIELD_ID_ON_QUEEN_SIDE_BY_COLOUR[figureColour]));
            }
            if (++count >= maxCount) {
                return count;
            }
        }
        return count;
    }

    public static final boolean isPossible(Board board, int move, int[] figuresIDsPerFieldsIDs, boolean kingSidePossible, boolean queenSidePossible, long opponentKing, int opponentKingFieldID, long free) {
        int capturedFigureID;
        int figureID = MoveInt.getFigurePID(move);
        int colour = MoveInt.getColour(move);
        int fromFieldID = MoveInt.getFromFieldID(move);
        long fromBitboard = Fields.ALL_ORDERED_A1H1[fromFieldID];
        byte opponentColour = Figures.OPPONENT_COLOUR[colour];
        if (figuresIDsPerFieldsIDs[fromFieldID] != figureID) {
            return false;
        }
        int toFieldID = MoveInt.getToFieldID(move);
        long toBitboard = Fields.ALL_ORDERED_A1H1[toFieldID];
        if (MoveInt.isCapture(move) ? figuresIDsPerFieldsIDs[toFieldID] != (capturedFigureID = MoveInt.getCapturedFigurePID(move)) : figuresIDsPerFieldsIDs[toFieldID] != 0) {
            return false;
        }
        long opponentKingMoves = KingPlies.ALL_KING_MOVES[opponentKingFieldID];
        if ((toBitboard & opponentKing) != 0L || (toBitboard & opponentKingMoves) != 0L) {
            return false;
        }
        if (MoveInt.isCastleKingSide(move)) {
            return kingSidePossible;
        }
        if (MoveInt.isCastleQueenSide(move)) {
            return queenSidePossible;
        }
        return !Checking.isFieldAttacked(board, opponentColour, colour, toBitboard, toFieldID, free | fromBitboard, true);
    }
}

