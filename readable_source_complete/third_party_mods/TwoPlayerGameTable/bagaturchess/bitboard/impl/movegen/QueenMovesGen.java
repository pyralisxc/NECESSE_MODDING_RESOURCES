/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.movegen;

import bagaturchess.bitboard.api.IInternalMoveList;
import bagaturchess.bitboard.impl.Board;
import bagaturchess.bitboard.impl.movegen.CastleMovesGen;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.bitboard.impl.movegen.OfficerMovesGen;
import bagaturchess.bitboard.impl.plies.CastlePlies;
import bagaturchess.bitboard.impl.plies.OfficerPlies;
import bagaturchess.bitboard.impl.plies.checking.QueenUniqueChecks;

public class QueenMovesGen
extends QueenUniqueChecks {
    public static final int genAllMoves(long excludedToFieldsIDs, boolean interuptAtFirstExclusionHit, int figureID, int fromFieldID, long freeBitboard, long allOpponentBitboard, int[] figuresIDsPerFieldsIDs, IInternalMoveList list, int maxCount) {
        int count = 0;
        if ((count += OfficerMovesGen.genAllMoves(excludedToFieldsIDs, interuptAtFirstExclusionHit, figureID, fromFieldID, freeBitboard, allOpponentBitboard, figuresIDsPerFieldsIDs, list, maxCount)) >= maxCount) {
            return count;
        }
        return count += CastleMovesGen.genAllMoves(excludedToFieldsIDs, interuptAtFirstExclusionHit, figureID, fromFieldID, freeBitboard, allOpponentBitboard, figuresIDsPerFieldsIDs, list, maxCount);
    }

    public static final int genCaptureMoves(Board bitboard, long excludedToFieldsIDs, int figureID, int fromFieldID, long freeBitboard, long allOpponentBitboard, int[] figuresIDsPerFieldsIDs, IInternalMoveList list, int maxCount) {
        int count = 0;
        long attacks = OfficerPlies.ALL_OFFICER_MOVES[fromFieldID];
        if ((allOpponentBitboard & attacks) != 0L && (count += OfficerMovesGen.genCaptureMoves(excludedToFieldsIDs, true, figureID, fromFieldID, freeBitboard, allOpponentBitboard, figuresIDsPerFieldsIDs, list, maxCount)) >= maxCount) {
            return count;
        }
        attacks = CastlePlies.ALL_CASTLE_MOVES[fromFieldID];
        if ((allOpponentBitboard & attacks) != 0L) {
            count += CastleMovesGen.genCaptureMoves(bitboard, excludedToFieldsIDs, true, figureID, fromFieldID, freeBitboard, allOpponentBitboard, figuresIDsPerFieldsIDs, list, maxCount);
        }
        return count;
    }

    public static final int genNonCaptureMoves(long excludedToFieldsIDs, int figureID, int fromFieldID, long freeBitboard, long allOpponentBitboard, IInternalMoveList list, int maxCount) {
        int count = 0;
        if ((count += OfficerMovesGen.genNonCaptureMoves(excludedToFieldsIDs, true, figureID, fromFieldID, freeBitboard, allOpponentBitboard, list, maxCount)) >= maxCount) {
            return count;
        }
        return count += CastleMovesGen.genNonCaptureMoves(excludedToFieldsIDs, true, figureID, fromFieldID, freeBitboard, allOpponentBitboard, list, maxCount);
    }

    public static final boolean isPossible(int move, int[] figuresIDsPerFieldsIDs, long free) {
        int figureDirType = MoveInt.getDirType(move);
        if (figureDirType == 4) {
            return CastleMovesGen.isPossible(move, figuresIDsPerFieldsIDs, free);
        }
        if (figureDirType == 3) {
            return OfficerMovesGen.isPossible(move, figuresIDsPerFieldsIDs, free);
        }
        throw new IllegalStateException();
    }

    public static final int genCheckMoves(long excludedToFieldsIDs, int figureID, int fromFieldID, long opponentKingBitboard, int opponentKingFieldID, long freeBitboard, long allMineBitboard, long allOpponentBitboard, int[] figuresIDsPerFieldsIDs, IInternalMoveList list, int maxCount) {
        int count = 0;
        if ((count += OfficerMovesGen.genCheckMoves(excludedToFieldsIDs, figureID, fromFieldID, opponentKingBitboard, opponentKingFieldID, freeBitboard, allMineBitboard, allOpponentBitboard, figuresIDsPerFieldsIDs, list, maxCount)) >= maxCount) {
            return count;
        }
        if ((count += CastleMovesGen.genCheckMoves(excludedToFieldsIDs, figureID, fromFieldID, opponentKingFieldID, freeBitboard, allMineBitboard, allOpponentBitboard, figuresIDsPerFieldsIDs, list, maxCount)) >= maxCount) {
            return count;
        }
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
        return count;
    }
}

