/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl_kingcaptureallowed.movegen;

import bagaturchess.bitboard.api.IInternalMoveList;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.bitboard.impl.plies.KingPlies;
import bagaturchess.bitboard.impl_kingcaptureallowed.plies.Castling;

public class KingMovesGen
extends KingPlies {
    static final int[][] validDirsIDs = ALL_KING_VALID_DIRS;
    static final int[][][] dirsFieldIDs = ALL_KING_DIRS_WITH_FIELD_IDS;
    static final long[][][] dirsBitBoards = ALL_KING_DIRS_WITH_BITBOARDS;

    public static final void genAllMoves(int pid, int fromFieldID, int[] figuresIDsPerFieldsIDs, int opponentKingFieldID, boolean kingSidePossible, boolean queenSidePossible, IInternalMoveList list) {
        int figureColour;
        long opponentKing = KingPlies.ALL_KING_MOVES[opponentKingFieldID] | Fields.ALL_A1H1[opponentKingFieldID];
        int[] validDirIDs = validDirsIDs[fromFieldID];
        int[][] dirs_ids = dirsFieldIDs[fromFieldID];
        for (int dirID : validDirIDs) {
            int toFieldID = dirs_ids[dirID][0];
            long toBitboard = Fields.ALL_A1H1[toFieldID];
            int targetPID = figuresIDsPerFieldsIDs[toFieldID];
            if ((toBitboard & opponentKing) != 0L) continue;
            if (targetPID == 0) {
                list.reserved_add(MoveInt.createNonCapture(pid, fromFieldID, toFieldID));
                continue;
            }
            if (Constants.hasSameColour(pid, targetPID)) continue;
            list.reserved_add(MoveInt.createCapture(pid, fromFieldID, toFieldID, targetPID));
        }
        if (kingSidePossible) {
            figureColour = Constants.getColourByPieceIdentity(pid);
            list.reserved_add(MoveInt.createKingSide(Castling.KINGS_PIDS_BY_COLOUR[figureColour], Castling.KING_FROM_FIELD_ID_BY_COLOUR[figureColour], Castling.KING_TO_FIELD_ID_ON_KING_SIDE_BY_COLOUR[figureColour]));
        }
        if (queenSidePossible) {
            figureColour = Constants.getColourByPieceIdentity(pid);
            list.reserved_add(MoveInt.createQueenSide(Castling.KINGS_PIDS_BY_COLOUR[figureColour], Castling.KING_FROM_FIELD_ID_BY_COLOUR[figureColour], Castling.KING_TO_FIELD_ID_ON_QUEEN_SIDE_BY_COLOUR[figureColour]));
        }
    }

    public static final void genCaptureMoves(int pid, int fromFieldID, int[] figuresIDsPerFieldsIDs, int opponentKingFieldID, IInternalMoveList list) {
        long opponentKing = KingPlies.ALL_KING_MOVES[opponentKingFieldID] | Fields.ALL_A1H1[opponentKingFieldID];
        int[] validDirIDs = validDirsIDs[fromFieldID];
        int[][] dirs_ids = dirsFieldIDs[fromFieldID];
        for (int dirID : validDirIDs) {
            int toFieldID = dirs_ids[dirID][0];
            long toBitboard = Fields.ALL_A1H1[toFieldID];
            int targetPID = figuresIDsPerFieldsIDs[toFieldID];
            if ((toBitboard & opponentKing) != 0L || targetPID == 0 || Constants.hasSameColour(pid, targetPID)) continue;
            list.reserved_add(MoveInt.createCapture(pid, fromFieldID, toFieldID, targetPID));
        }
    }
}

