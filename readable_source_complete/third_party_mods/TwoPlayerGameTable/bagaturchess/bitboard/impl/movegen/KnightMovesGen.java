/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.movegen;

import bagaturchess.bitboard.api.IInternalMoveList;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.bitboard.impl.plies.checking.KnightChecks;

public class KnightMovesGen
extends KnightChecks {
    static final int figureType = 2;
    static final int[][] validDirsIDs = ALL_KNIGHT_VALID_DIRS;
    static final int[][][] dirsFieldIDs = ALL_KNIGHT_DIRS_WITH_FIELD_IDS;
    static final long[][][] dirsBitBoards = ALL_KNIGHT_DIRS_WITH_BITBOARDS;

    public static final int genAllMoves(long excludedToFieldsIDs, int figureID, int fromFieldID, long allMineBitboard, long allOpponentBitboard, int[] figuresIDsPerFieldsIDs, IInternalMoveList list, int maxCount) {
        int count = 0;
        int[] validDirIDs = validDirsIDs[fromFieldID];
        int[][] dirs_ids = dirsFieldIDs[fromFieldID];
        long[][] dirs = dirsBitBoards[fromFieldID];
        for (int dirID : validDirIDs) {
            long toBitboard = dirs[dirID][0];
            if ((excludedToFieldsIDs & toBitboard) != 0L || (toBitboard & allMineBitboard) != 0L) continue;
            if (list != null) {
                int toFieldID = dirs_ids[dirID][0];
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
        return count;
    }

    public static final int genCaptureMoves(long excludedToFieldsIDs, int figureID, int fromFieldID, long allMineBitboard, long allOpponentBitboard, int[] figuresIDsPerFieldsIDs, IInternalMoveList list, int maxCount) {
        int count = 0;
        int[] validDirIDs = validDirsIDs[fromFieldID];
        int[][] dirs_ids = dirsFieldIDs[fromFieldID];
        long[][] dirs = dirsBitBoards[fromFieldID];
        for (int dirID : validDirIDs) {
            long toBitboard = dirs[dirID][0];
            if ((excludedToFieldsIDs & toBitboard) != 0L || (toBitboard & allMineBitboard) != 0L || (toBitboard & allOpponentBitboard) == 0L) continue;
            if (list != null) {
                int toFieldID = dirs_ids[dirID][0];
                int capturedFigureID = figuresIDsPerFieldsIDs[toFieldID];
                list.reserved_add(MoveInt.createCapture(figureID, fromFieldID, toFieldID, capturedFigureID));
            }
            if (++count < maxCount) continue;
            return count;
        }
        return count;
    }

    public static final int genNonCaptureMoves(long excludedToFieldsIDs, int figureID, int fromFieldID, long allMineBitboard, long allOpponentBitboard, IInternalMoveList list, int maxCount) {
        int count = 0;
        int[] validDirIDs = validDirsIDs[fromFieldID];
        int[][] dirs_ids = dirsFieldIDs[fromFieldID];
        long[][] dirs = dirsBitBoards[fromFieldID];
        for (int dirID : validDirIDs) {
            long toBitboard = dirs[dirID][0];
            if ((excludedToFieldsIDs & toBitboard) != 0L || (toBitboard & allMineBitboard) != 0L || (toBitboard & allOpponentBitboard) != 0L) continue;
            if (list != null) {
                int toFieldID = dirs_ids[dirID][0];
                list.reserved_add(MoveInt.createNonCapture(figureID, fromFieldID, toFieldID));
            }
            if (++count < maxCount) continue;
            return count;
        }
        return count;
    }

    public static final boolean isPossible(int move, int[] figuresIDsPerFieldsIDs) {
        int capturedFigureID;
        int figureID = MoveInt.getFigurePID(move);
        int fromFieldID = MoveInt.getFromFieldID(move);
        if (figuresIDsPerFieldsIDs[fromFieldID] != figureID) {
            return false;
        }
        int toFieldID = MoveInt.getToFieldID(move);
        return !(MoveInt.isCapture(move) ? figuresIDsPerFieldsIDs[toFieldID] != (capturedFigureID = MoveInt.getCapturedFigurePID(move)) : figuresIDsPerFieldsIDs[toFieldID] != 0);
    }

    public static final int genCheckMoves(long excludedToFieldsIDs, int figureID, int fromFieldID, int opponentKingFieldID, long allMineBitboard, long allOpponentBitboard, int[] figuresIDsPerFieldsIDs, IInternalMoveList list, int maxCount) {
        int count = 0;
        int[] fields = CHECK_MIDDLE_FIELDS_IDS[fromFieldID][opponentKingFieldID];
        long[] fieldBoards = CHECK_MIDDLE_FIELDS_BITBOARDS[fromFieldID][opponentKingFieldID];
        if (fields != null) {
            int size = fields.length;
            for (int i = 0; i < size; ++i) {
                long middleFieldBitboard = fieldBoards[i];
                if ((excludedToFieldsIDs & middleFieldBitboard) != 0L || (middleFieldBitboard & allMineBitboard) != 0L) continue;
                if (list != null) {
                    int toFieldID = fields[i];
                    if ((middleFieldBitboard & allOpponentBitboard) != 0L) {
                        int capturedFigureID = figuresIDsPerFieldsIDs[toFieldID];
                        list.reserved_add(MoveInt.createCapture(figureID, fromFieldID, toFieldID, capturedFigureID));
                    } else {
                        list.reserved_add(MoveInt.createNonCapture(figureID, fromFieldID, toFieldID));
                    }
                }
                if (++count < maxCount) continue;
                return count;
            }
        }
        return count;
    }
}

