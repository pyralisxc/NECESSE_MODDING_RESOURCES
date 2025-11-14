/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.movegen;

import bagaturchess.bitboard.api.IInternalMoveList;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.bitboard.impl.plies.checking.OfficerChecks;

public class OfficerMovesGen
extends OfficerChecks {
    static final int[][] validDirsIDs = ALL_OFFICER_VALID_DIRS;
    static final int[][][] dirsFieldIDs = ALL_OFFICER_DIRS_WITH_FIELD_IDS;
    static final long[][][] dirsBitBoards = ALL_OFFICER_DIRS_WITH_BITBOARDS;
    static final long[][] wholeDirsBitboards = ALL_OFFICER_DIR_MOVES;

    public static final int genAllMoves(long excludedToFieldsIDs, boolean interuptAtFirstExclusionHit, int figureID, int fromFieldID, long freeBitboard, long allOpponentBitboard, int[] figuresIDsPerFieldsIDs, IInternalMoveList list, int maxCount) {
        int count = 0;
        int[] validDirIDs = validDirsIDs[fromFieldID];
        int[][] dirs_ids = dirsFieldIDs[fromFieldID];
        long[][] dirs = dirsBitBoards[fromFieldID];
        block0: for (int dirID : validDirIDs) {
            long[] dirBitboards = dirs[dirID];
            for (int seq = 0; seq < dirBitboards.length; ++seq) {
                int toFieldID;
                long toBitboard = dirs[dirID][seq];
                if ((excludedToFieldsIDs & toBitboard) != 0L) {
                    if ((toBitboard & freeBitboard) != 0L && !interuptAtFirstExclusionHit) continue;
                    continue block0;
                }
                if ((toBitboard & freeBitboard) != 0L) {
                    if (list != null) {
                        toFieldID = dirs_ids[dirID][seq];
                        list.reserved_add(MoveInt.createNonCapture(figureID, fromFieldID, toFieldID));
                    }
                    if (++count < maxCount) continue;
                    return count;
                }
                if ((toBitboard & allOpponentBitboard) == 0L) continue block0;
                if (list != null) {
                    toFieldID = dirs_ids[dirID][seq];
                    int capturedFigureID = figuresIDsPerFieldsIDs[toFieldID];
                    list.reserved_add(MoveInt.createCapture(figureID, fromFieldID, toFieldID, capturedFigureID));
                }
                if (++count < maxCount) continue block0;
                return count;
            }
        }
        return count;
    }

    public static final int genCaptureMoves(long excludedToFieldsIDs, boolean interuptAtFirstExclusionHit, int figureID, int fromFieldID, long freeBitboard, long allOpponentBitboard, int[] figuresIDsPerFieldsIDs, IInternalMoveList list, int maxCount) {
        int count = 0;
        int[] validDirIDs = validDirsIDs[fromFieldID];
        int[][] dirs_ids = dirsFieldIDs[fromFieldID];
        long[][] dirs = dirsBitBoards[fromFieldID];
        block0: for (int dirID : validDirIDs) {
            long allInDir = wholeDirsBitboards[dirID][fromFieldID];
            if ((allInDir & allOpponentBitboard) == 0L) continue;
            long[] dirBitboards = dirs[dirID];
            for (int seq = 0; seq < dirBitboards.length; ++seq) {
                long toBitboard = dirs[dirID][seq];
                if ((excludedToFieldsIDs & toBitboard) != 0L) {
                    if ((toBitboard & freeBitboard) != 0L && !interuptAtFirstExclusionHit) continue;
                    continue block0;
                }
                if ((toBitboard & freeBitboard) != 0L) continue;
                if ((toBitboard & allOpponentBitboard) == 0L) continue block0;
                if (list != null) {
                    int toFieldID = dirs_ids[dirID][seq];
                    int capturedFigureID = figuresIDsPerFieldsIDs[toFieldID];
                    list.reserved_add(MoveInt.createCapture(figureID, fromFieldID, toFieldID, capturedFigureID));
                }
                if (++count < maxCount) continue block0;
                return count;
            }
        }
        return count;
    }

    public static final int genNonCaptureMoves(long excludedToFieldsIDs, boolean interuptAtFirstExclusionHit, int figureID, int fromFieldID, long freeBitboard, long allOpponentBitboard, IInternalMoveList list, int maxCount) {
        int count = 0;
        int[] validDirIDs = validDirsIDs[fromFieldID];
        int[][] dirs_ids = dirsFieldIDs[fromFieldID];
        long[][] dirs = dirsBitBoards[fromFieldID];
        block0: for (int dirID : validDirIDs) {
            long[] dirBitboards = dirs[dirID];
            for (int seq = 0; seq < dirBitboards.length; ++seq) {
                long toBitboard = dirs[dirID][seq];
                if ((excludedToFieldsIDs & toBitboard) != 0L) {
                    if ((toBitboard & freeBitboard) != 0L && !interuptAtFirstExclusionHit) continue;
                    continue block0;
                }
                if ((toBitboard & freeBitboard) != 0L) {
                    if (list != null) {
                        int toFieldID = dirs_ids[dirID][seq];
                        list.reserved_add(MoveInt.createNonCapture(figureID, fromFieldID, toFieldID));
                    }
                    if (++count < maxCount) continue;
                    return count;
                }
                if ((toBitboard & allOpponentBitboard) == 0L) continue block0;
                continue block0;
            }
        }
        return count;
    }

    public static final boolean isPossible(int move, int[] figuresIDsPerFieldsIDs, long free) {
        int capturedFigureID;
        int figureID = MoveInt.getFigurePID(move);
        int fromFieldID = MoveInt.getFromFieldID(move);
        if (figuresIDsPerFieldsIDs[fromFieldID] != figureID) {
            return false;
        }
        int toFieldID = MoveInt.getToFieldID(move);
        if (MoveInt.isCapture(move) ? figuresIDsPerFieldsIDs[toFieldID] != (capturedFigureID = MoveInt.getCapturedFigurePID(move)) : figuresIDsPerFieldsIDs[toFieldID] != 0) {
            return false;
        }
        long path = PATHS[fromFieldID][toFieldID];
        if (path == -1L) {
            throw new IllegalStateException("Path none");
        }
        return (path & free) == path;
    }

    public static final int genCheckMoves(long excludedToFieldsIDs, int figureID, int fromFieldID, long opponentKingBitboard, int opponentKingFieldID, long freeBitboard, long allMineBitboard, long allOpponentBitboard, int[] figuresIDsPerFieldsIDs, IInternalMoveList list, int maxCount) {
        int count = 0;
        if ((Fields.ALL_OFFICERS_FIELDS[fromFieldID] & opponentKingBitboard) != 0L) {
            int[] fields = CHECK_MIDDLE_FIELDS_IDS[fromFieldID][opponentKingFieldID];
            long[] fieldBoards = CHECK_MIDDLE_FIELDS_BITBOARDS[fromFieldID][opponentKingFieldID];
            long[] path = FIELDS_WHOLE_PATH[fromFieldID][opponentKingFieldID];
            if (fields != null) {
                int size = fields.length;
                for (int i = 0; i < size; ++i) {
                    int toFieldID = fields[i];
                    long middleFieldBitboard = fieldBoards[i];
                    long curPath = path[i];
                    if ((excludedToFieldsIDs & middleFieldBitboard) != 0L || (allMineBitboard & curPath) != 0L || (allOpponentBitboard & curPath) != 0L) continue;
                    if ((middleFieldBitboard & freeBitboard) != 0L) {
                        if (list != null) {
                            list.reserved_add(MoveInt.createNonCapture(figureID, fromFieldID, toFieldID));
                        }
                        if (++count < maxCount) continue;
                        return count;
                    }
                    if ((middleFieldBitboard & allOpponentBitboard) == 0L) continue;
                    if (list != null) {
                        int capturedFigureID = figuresIDsPerFieldsIDs[toFieldID];
                        list.reserved_add(MoveInt.createCapture(figureID, fromFieldID, toFieldID, capturedFigureID));
                    }
                    if (++count < maxCount) continue;
                    return count;
                }
            }
        }
        return count;
    }
}

