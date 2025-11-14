/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.movegen;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IInternalMoveList;
import bagaturchess.bitboard.impl.Board;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.bitboard.impl.plies.checking.WhitePawnsChecks;
import bagaturchess.bitboard.impl.plies.specials.Enpassanting;

public class WhitePawnMovesGen
extends WhitePawnsChecks {
    private static long[][] middleField = new long[64][64];
    static final int figureType = 1;
    static final int[][] attacksValidDirs;
    static final int[][] nonattacksValidDirs;
    static final int[][][] attacksFieldIDs;
    static final int[][][] nonattacksFieldIDs;
    static final long[][][] attacksBitboards;
    static final long[][][] nonattacksBitboards;

    public static final int genAllMoves(IBitBoard board, long excludedToFieldsIDs, boolean interuptAtFirstExclusionHit, int figureID, int fromFieldID, long freeBitboard, long allOpponentBitboard, int[] figuresIDsPerFieldsIDs, boolean hasEnpassant, long enpassantPawnBitboard, IInternalMoveList list, int maxCount) {
        int toFieldID;
        long toBitboard;
        int count = 0;
        int[] validDirIDs = attacksValidDirs[fromFieldID];
        int[][] dirs_ids = attacksFieldIDs[fromFieldID];
        long[][] dirs = attacksBitboards[fromFieldID];
        for (int dirID : validDirIDs) {
            int enpassCount;
            toBitboard = dirs[dirID][0];
            if ((excludedToFieldsIDs & toBitboard) != 0L && (!hasEnpassant || (toBitboard & allOpponentBitboard) != 0L || (excludedToFieldsIDs & enpassantPawnBitboard) != 0L)) continue;
            if ((toBitboard & allOpponentBitboard) != 0L) {
                toFieldID = dirs_ids[dirID][0];
                if ((toBitboard & 0xFFL) != 0L) {
                    if (list != null) {
                        int cap_pid = figuresIDsPerFieldsIDs[toFieldID];
                        list.reserved_add(MoveInt.createCapturePromotion(fromFieldID, toFieldID, cap_pid, 5));
                        list.reserved_add(MoveInt.createCapturePromotion(fromFieldID, toFieldID, cap_pid, 4));
                        list.reserved_add(MoveInt.createCapturePromotion(fromFieldID, toFieldID, cap_pid, 3));
                        list.reserved_add(MoveInt.createCapturePromotion(fromFieldID, toFieldID, cap_pid, 2));
                    }
                    if ((count += 4) < maxCount) continue;
                    return maxCount;
                }
                if (list != null) {
                    int capturedFigureID = figuresIDsPerFieldsIDs[toFieldID];
                    list.reserved_add(MoveInt.createCapture(figureID, fromFieldID, toFieldID, capturedFigureID));
                }
                if (++count < maxCount) continue;
                return count;
            }
            if (!hasEnpassant || (enpassCount = WhitePawnMovesGen.genEnpassantMove(board, excludedToFieldsIDs, figureID, fromFieldID, allOpponentBitboard, figuresIDsPerFieldsIDs, hasEnpassant, enpassantPawnBitboard, list, maxCount)) <= 0) continue;
            hasEnpassant = false;
            if ((count += enpassCount) < maxCount) continue;
            return count;
        }
        validDirIDs = nonattacksValidDirs[fromFieldID];
        dirs_ids = nonattacksFieldIDs[fromFieldID];
        dirs = nonattacksBitboards[fromFieldID];
        for (int dirID : validDirIDs) {
            toBitboard = dirs[dirID][0];
            if ((excludedToFieldsIDs & toBitboard) != 0L) {
                if ((toBitboard & freeBitboard) != 0L && !interuptAtFirstExclusionHit) continue;
                break;
            }
            if ((toBitboard & freeBitboard) == 0L) break;
            toFieldID = dirs_ids[dirID][0];
            if ((toBitboard & 0xFFL) != 0L) {
                if (list != null) {
                    list.reserved_add(MoveInt.createPromotion(fromFieldID, toFieldID, 5));
                    list.reserved_add(MoveInt.createPromotion(fromFieldID, toFieldID, 4));
                    list.reserved_add(MoveInt.createPromotion(fromFieldID, toFieldID, 3));
                    list.reserved_add(MoveInt.createPromotion(fromFieldID, toFieldID, 2));
                }
                if ((count += 4) < maxCount) continue;
                return maxCount;
            }
            if (list != null) {
                list.reserved_add(MoveInt.createNonCapture(figureID, fromFieldID, toFieldID));
            }
            if (++count < maxCount) continue;
            return count;
        }
        return count;
    }

    public static final int genAllNonSpecialMoves(long excludedToFieldsIDs, boolean interuptAtFirstExclusionHit, int figureID, int fromFieldID, long freeBitboard, long allOpponentBitboard, int[] figuresIDsPerFieldsIDs, IInternalMoveList list, int maxCount) {
        int toFieldID;
        long toBitboard;
        int count = 0;
        int[] validDirIDs = attacksValidDirs[fromFieldID];
        int[][] dirs_ids = attacksFieldIDs[fromFieldID];
        long[][] dirs = attacksBitboards[fromFieldID];
        for (int dirID : validDirIDs) {
            toBitboard = dirs[dirID][0];
            if ((excludedToFieldsIDs & toBitboard) != 0L || (toBitboard & allOpponentBitboard) == 0L || (toBitboard & 0xFFL) != 0L) continue;
            if (list != null) {
                toFieldID = dirs_ids[dirID][0];
                int capturedFigureID = figuresIDsPerFieldsIDs[toFieldID];
                list.reserved_add(MoveInt.createCapture(figureID, fromFieldID, toFieldID, capturedFigureID));
            }
            if (++count < maxCount) continue;
            return count;
        }
        validDirIDs = nonattacksValidDirs[fromFieldID];
        dirs_ids = nonattacksFieldIDs[fromFieldID];
        dirs = nonattacksBitboards[fromFieldID];
        for (int dirID : validDirIDs) {
            toBitboard = dirs[dirID][0];
            if ((excludedToFieldsIDs & toBitboard) != 0L) {
                if ((toBitboard & freeBitboard) != 0L && !interuptAtFirstExclusionHit) continue;
                break;
            }
            if ((toBitboard & freeBitboard) == 0L) break;
            if ((toBitboard & 0xFFL) != 0L) continue;
            if (list != null) {
                toFieldID = dirs_ids[dirID][0];
                list.reserved_add(MoveInt.createNonCapture(figureID, fromFieldID, toFieldID));
            }
            if (++count < maxCount) continue;
            return count;
        }
        return count;
    }

    public static final int genEnpassantMove(IBitBoard board, long excludedToFieldsIDs, int figureID, int fromFieldID, long allOpponentBitboard, int[] figuresIDsPerFieldsIDs, boolean hasEnpassant, long enpassantPawnBitboard, IInternalMoveList list, int maxCount) {
        if (!hasEnpassant) {
            throw new IllegalStateException();
        }
        int count = 0;
        int[] validDirIDs = attacksValidDirs[fromFieldID];
        int[][] dirs_ids = attacksFieldIDs[fromFieldID];
        long[][] dirs = attacksBitboards[fromFieldID];
        for (int dirID : validDirIDs) {
            int figureColour;
            long opponentPawnBitboard;
            long toBitboard = dirs[dirID][0];
            if ((excludedToFieldsIDs & toBitboard) != 0L && (!hasEnpassant || (toBitboard & allOpponentBitboard) != 0L || (excludedToFieldsIDs & enpassantPawnBitboard) != 0L) || !hasEnpassant || (enpassantPawnBitboard & (opponentPawnBitboard = Enpassanting.ADJOINING_FILE_BITBOARD_AT_CAPTURE[figureColour = Figures.getFigureColour(figureID)][fromFieldID][dirID])) == 0L) continue;
            int toFieldID = dirs_ids[dirID][0];
            int capturedFieldID = Enpassanting.ADJOINING_FILE_FIELD_ID_AT_CAPTURE[figureColour][fromFieldID][dirID];
            int capturedPawnID = figuresIDsPerFieldsIDs[capturedFieldID];
            int enpassMove = MoveInt.createEnpassant(figureID, fromFieldID, toFieldID, dirID, capturedPawnID);
            ((Board)board).makeMoveForward(enpassMove, false);
            boolean legal = !board.isInCheck(figureColour);
            ((Board)board).makeMoveBackward(enpassMove, false);
            if (!legal) continue;
            ++count;
            if (list != null) {
                list.reserved_add(enpassMove);
            }
            if (count < maxCount) break;
            return count;
        }
        return count;
    }

    public static final int genCapturePromotionEnpassantMoves(IBitBoard board, long excludedToFieldsIDs, boolean interuptAtFirstExclusionHit, int figureID, int fromFieldID, long freeBitboard, long allOpponentBitboard, int[] figuresIDsPerFieldsIDs, boolean hasEnpassant, long enpassantPawnBitboard, IInternalMoveList list, int maxCount) {
        int toFieldID;
        long toBitboard;
        int dirID2;
        int count = 0;
        int[] validDirIDs = attacksValidDirs[fromFieldID];
        int[][] dirs_ids = attacksFieldIDs[fromFieldID];
        long[][] dirs = attacksBitboards[fromFieldID];
        for (int dirID2 : validDirIDs) {
            int enpassCount;
            toBitboard = dirs[dirID2][0];
            if ((excludedToFieldsIDs & toBitboard) != 0L && (!hasEnpassant || (toBitboard & allOpponentBitboard) != 0L || (excludedToFieldsIDs & enpassantPawnBitboard) != 0L)) continue;
            if ((toBitboard & allOpponentBitboard) != 0L) {
                toFieldID = dirs_ids[dirID2][0];
                if ((toBitboard & 0xFFL) != 0L) {
                    if (list != null) {
                        int cap_pid = figuresIDsPerFieldsIDs[toFieldID];
                        list.reserved_add(MoveInt.createCapturePromotion(fromFieldID, toFieldID, cap_pid, 5));
                        list.reserved_add(MoveInt.createCapturePromotion(fromFieldID, toFieldID, cap_pid, 4));
                        list.reserved_add(MoveInt.createCapturePromotion(fromFieldID, toFieldID, cap_pid, 3));
                        list.reserved_add(MoveInt.createCapturePromotion(fromFieldID, toFieldID, cap_pid, 2));
                    }
                    if ((count += 4) < maxCount) continue;
                    return maxCount;
                }
                if (list != null) {
                    int capturedFigureID = figuresIDsPerFieldsIDs[toFieldID];
                    list.reserved_add(MoveInt.createCapture(figureID, fromFieldID, toFieldID, capturedFigureID));
                }
                if (++count < maxCount) continue;
                return count;
            }
            if (!hasEnpassant || (enpassCount = WhitePawnMovesGen.genEnpassantMove(board, excludedToFieldsIDs, figureID, fromFieldID, allOpponentBitboard, figuresIDsPerFieldsIDs, hasEnpassant, enpassantPawnBitboard, list, maxCount)) <= 0) continue;
            hasEnpassant = false;
            if ((count += enpassCount) < maxCount) continue;
            return count;
        }
        validDirIDs = nonattacksValidDirs[fromFieldID];
        dirs_ids = nonattacksFieldIDs[fromFieldID];
        dirs = nonattacksBitboards[fromFieldID];
        int size = validDirIDs.length;
        for (int i = 0; i < size && ((toBitboard = dirs[dirID2 = validDirIDs[i]][0]) & freeBitboard) != 0L; ++i) {
            if ((excludedToFieldsIDs & toBitboard) != 0L) {
                if ((toBitboard & freeBitboard) != 0L && !interuptAtFirstExclusionHit) continue;
                break;
            }
            if ((toBitboard & 0xFFL) == 0L) continue;
            if (list != null) {
                toFieldID = dirs_ids[dirID2][0];
                list.reserved_add(MoveInt.createPromotion(fromFieldID, toFieldID, 5));
                list.reserved_add(MoveInt.createPromotion(fromFieldID, toFieldID, 4));
                list.reserved_add(MoveInt.createPromotion(fromFieldID, toFieldID, 3));
                list.reserved_add(MoveInt.createPromotion(fromFieldID, toFieldID, 2));
            }
            if ((count += 4) < maxCount) continue;
            return maxCount;
        }
        return count;
    }

    public static final int genPromotionMoves(long excludedToFieldsIDs, boolean interuptAtFirstExclusionHit, long fromBitboard, int fromFieldID, long freeBitboard, long allOpponentBitboard, int[] figuresIDsPerFieldsIDs, IInternalMoveList list, int maxCount) {
        int count = 0;
        if ((fromBitboard & 0xFF00L) != 0L) {
            int toFieldID;
            long toBitboard;
            int[] validDirIDs = attacksValidDirs[fromFieldID];
            int[][] dirs_ids = attacksFieldIDs[fromFieldID];
            long[][] dirs = attacksBitboards[fromFieldID];
            for (int dirID : validDirIDs) {
                toBitboard = dirs[dirID][0];
                if ((excludedToFieldsIDs & toBitboard) != 0L || (toBitboard & allOpponentBitboard) == 0L) continue;
                if ((toBitboard & 0xFFL) == 0L) {
                    throw new IllegalStateException();
                }
                if (list != null) {
                    toFieldID = dirs_ids[dirID][0];
                    int cap_pid = figuresIDsPerFieldsIDs[toFieldID];
                    list.reserved_add(MoveInt.createCapturePromotion(fromFieldID, toFieldID, cap_pid, 5));
                    list.reserved_add(MoveInt.createCapturePromotion(fromFieldID, toFieldID, cap_pid, 4));
                    list.reserved_add(MoveInt.createCapturePromotion(fromFieldID, toFieldID, cap_pid, 3));
                    list.reserved_add(MoveInt.createCapturePromotion(fromFieldID, toFieldID, cap_pid, 2));
                }
                if ((count += 4) < maxCount) continue;
                return maxCount;
            }
            validDirIDs = nonattacksValidDirs[fromFieldID];
            dirs_ids = nonattacksFieldIDs[fromFieldID];
            dirs = nonattacksBitboards[fromFieldID];
            for (int dirID : validDirIDs) {
                toBitboard = dirs[dirID][0];
                if ((excludedToFieldsIDs & toBitboard) != 0L) {
                    if ((toBitboard & freeBitboard) != 0L && !interuptAtFirstExclusionHit) continue;
                    break;
                }
                if ((toBitboard & freeBitboard) == 0L) continue;
                if (list != null) {
                    toFieldID = dirs_ids[dirID][0];
                    list.reserved_add(MoveInt.createPromotion(fromFieldID, toFieldID, 5));
                    list.reserved_add(MoveInt.createPromotion(fromFieldID, toFieldID, 4));
                    list.reserved_add(MoveInt.createPromotion(fromFieldID, toFieldID, 3));
                    list.reserved_add(MoveInt.createPromotion(fromFieldID, toFieldID, 2));
                }
                if ((count += 4) < maxCount) continue;
                return maxCount;
            }
        }
        return count;
    }

    public static final int genNonCaptureMoves(long excludedToFieldsIDs, boolean interuptAtFirstExclusionHit, int figureID, int fromFieldID, long freeBitboard, IInternalMoveList list, int maxCount) {
        int dirID;
        long toBitboard;
        int count = 0;
        int[] validDirIDs = nonattacksValidDirs[fromFieldID];
        int[][] dirs_ids = nonattacksFieldIDs[fromFieldID];
        long[][] dirs = nonattacksBitboards[fromFieldID];
        int size = validDirIDs.length;
        for (int i = 0; i < size && ((toBitboard = dirs[dirID = validDirIDs[i]][0]) & freeBitboard) != 0L; ++i) {
            if ((excludedToFieldsIDs & toBitboard) != 0L) {
                if ((toBitboard & freeBitboard) != 0L && !interuptAtFirstExclusionHit) continue;
                break;
            }
            if ((toBitboard & 0xFFL) != 0L) continue;
            if (list != null) {
                int toFieldID = dirs_ids[dirID][0];
                list.reserved_add(MoveInt.createNonCapture(figureID, fromFieldID, toFieldID));
            }
            if (++count < maxCount) continue;
            return count;
        }
        return count;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static final boolean isPossible(int move, int[] figuresIDsPerFieldsIDs, long free, boolean hasEnpassant, long enpassantPawnBitboard) {
        int figureID = MoveInt.getFigurePID(move);
        int fromFieldID = MoveInt.getFromFieldID(move);
        if (figuresIDsPerFieldsIDs[fromFieldID] != figureID) {
            return false;
        }
        int toFieldID = MoveInt.getToFieldID(move);
        if (MoveInt.isCapture(move)) {
            if (MoveInt.isEnpassant(move)) {
                if (!hasEnpassant) return false;
                long opponentPawnBitboard = Fields.ALL_ORDERED_A1H1[MoveInt.getEnpassantCapturedFieldID(move)];
                if (enpassantPawnBitboard == opponentPawnBitboard) return true;
                return false;
            }
            int capturedFigureID = MoveInt.getCapturedFigurePID(move);
            if (figuresIDsPerFieldsIDs[toFieldID] == capturedFigureID) return true;
            return false;
        }
        if (figuresIDsPerFieldsIDs[toFieldID] != 0) {
            return false;
        }
        if (middleField[fromFieldID][toFieldID] == 0L || (middleField[fromFieldID][toFieldID] & free) != 0L) return true;
        return false;
    }

    public static final int genCheckMoves(long excludedToFieldsIDs, int figureID, int fromFieldID, int opponentKingFieldID, long freeBitboard, long allOpponentBitboard, int[] figuresIDsPerFieldsIDs, IInternalMoveList list, int maxCount) {
        int toFieldID;
        long middleFieldBitboard;
        int i;
        int size;
        int count = 0;
        int[] fields = CHECK_NONATTACK_MIDDLE_FIELDS_IDS[fromFieldID][opponentKingFieldID];
        long[] fieldBoards = CHECK_NONATTACK_MIDDLE_FIELDS_BITBOARDS[fromFieldID][opponentKingFieldID];
        if (fields != null) {
            size = fields.length;
            for (i = 0; i < size; ++i) {
                middleFieldBitboard = fieldBoards[i];
                if ((excludedToFieldsIDs & middleFieldBitboard) != 0L || middleField[fromFieldID][toFieldID = fields[i]] != 0L && (middleField[fromFieldID][toFieldID] & freeBitboard) == 0L) continue;
                if ((middleFieldBitboard & freeBitboard) == 0L) break;
                if ((middleFieldBitboard & 0xFFL) != 0L) {
                    throw new IllegalStateException();
                }
                if (list != null) {
                    list.reserved_add(MoveInt.createNonCapture(figureID, fromFieldID, toFieldID));
                }
                if (++count < maxCount) continue;
                return count;
            }
        }
        fields = CHECK_ATTACK_MIDDLE_FIELDS_IDS[fromFieldID][opponentKingFieldID];
        fieldBoards = CHECK_ATTACK_MIDDLE_FIELDS_BITBOARDS[fromFieldID][opponentKingFieldID];
        if (fields != null) {
            size = fields.length;
            for (i = 0; i < size; ++i) {
                middleFieldBitboard = fieldBoards[i];
                if ((excludedToFieldsIDs & middleFieldBitboard) != 0L || (middleFieldBitboard & allOpponentBitboard) == 0L) continue;
                if ((middleFieldBitboard & 0xFFL) != 0L) {
                    throw new IllegalStateException();
                }
                if (list != null) {
                    toFieldID = fields[i];
                    int capturedFigureID = figuresIDsPerFieldsIDs[toFieldID];
                    list.reserved_add(MoveInt.createCapture(figureID, fromFieldID, toFieldID, capturedFigureID));
                }
                if (++count < maxCount) continue;
                return count;
            }
        }
        return count;
    }

    static {
        WhitePawnMovesGen.middleField[WhitePawnMovesGen.get67IDByBitboard((long)0x80000000000000L)][WhitePawnMovesGen.get67IDByBitboard((long)0x8000000000L)] = 0x800000000000L;
        WhitePawnMovesGen.middleField[WhitePawnMovesGen.get67IDByBitboard((long)0x40000000000000L)][WhitePawnMovesGen.get67IDByBitboard((long)0x4000000000L)] = 0x400000000000L;
        WhitePawnMovesGen.middleField[WhitePawnMovesGen.get67IDByBitboard((long)0x20000000000000L)][WhitePawnMovesGen.get67IDByBitboard((long)0x2000000000L)] = 0x200000000000L;
        WhitePawnMovesGen.middleField[WhitePawnMovesGen.get67IDByBitboard((long)0x10000000000000L)][WhitePawnMovesGen.get67IDByBitboard((long)0x1000000000L)] = 0x100000000000L;
        WhitePawnMovesGen.middleField[WhitePawnMovesGen.get67IDByBitboard((long)0x8000000000000L)][WhitePawnMovesGen.get67IDByBitboard((long)0x800000000L)] = 0x80000000000L;
        WhitePawnMovesGen.middleField[WhitePawnMovesGen.get67IDByBitboard((long)0x4000000000000L)][WhitePawnMovesGen.get67IDByBitboard((long)0x400000000L)] = 0x40000000000L;
        WhitePawnMovesGen.middleField[WhitePawnMovesGen.get67IDByBitboard((long)0x2000000000000L)][WhitePawnMovesGen.get67IDByBitboard((long)0x200000000L)] = 0x20000000000L;
        WhitePawnMovesGen.middleField[WhitePawnMovesGen.get67IDByBitboard((long)0x1000000000000L)][WhitePawnMovesGen.get67IDByBitboard((long)0x100000000L)] = 0x10000000000L;
        attacksValidDirs = ALL_WHITE_PAWN_ATTACKS_VALID_DIRS;
        nonattacksValidDirs = ALL_WHITE_PAWN_NONATTACKS_VALID_DIRS;
        attacksFieldIDs = ALL_WHITE_PAWN_ATTACKS_DIRS_WITH_FIELD_IDS;
        nonattacksFieldIDs = ALL_WHITE_PAWN_NONATTACKS_DIRS_WITH_FIELD_IDS;
        attacksBitboards = ALL_WHITE_PAWN_ATTACKS_DIRS_WITH_BITBOARDS;
        nonattacksBitboards = ALL_WHITE_PAWN_NONATTACKS_DIRS_WITH_BITBOARDS;
    }
}

