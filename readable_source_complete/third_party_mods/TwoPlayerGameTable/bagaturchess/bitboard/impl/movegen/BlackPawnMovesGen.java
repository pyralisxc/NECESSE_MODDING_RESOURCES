/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.movegen;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IInternalMoveList;
import bagaturchess.bitboard.impl.Board;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.bitboard.impl.plies.checking.BlackPawnsChecks;
import bagaturchess.bitboard.impl.plies.specials.Enpassanting;

public class BlackPawnMovesGen
extends BlackPawnsChecks {
    private static long[][] middleField = new long[64][64];
    static final int figureType = 1;
    static final int[][] attacksValidDirs;
    static final int[][] nonattacksValidDirs;
    static final int[][][] attacksFieldIDs;
    static final int[][][] nonattacksFieldIDs;
    static final long[][][] attacksBitboards;
    static final long[][][] nonattacksBitboards;

    public static final int genAllMoves(IBitBoard board, long excludedToFieldsIDs, boolean interuptAtFirstExclusionHit, int figureID, int fromFieldID, long freeBitboard, int[] figuresIDsPerFieldsIDs, boolean hasEnpassant, long enpassantPawnBitboard, IInternalMoveList list, int maxCount) {
        int toFieldID;
        long toBitboard;
        int count = 0;
        int[] validDirIDs = attacksValidDirs[fromFieldID];
        int[][] dirs_ids = attacksFieldIDs[fromFieldID];
        long[][] dirs = attacksBitboards[fromFieldID];
        for (int dirID : validDirIDs) {
            int enpassCount;
            toBitboard = dirs[dirID][0];
            toFieldID = dirs_ids[dirID][0];
            if ((excludedToFieldsIDs & toBitboard) != 0L && (!hasEnpassant || Constants.isWhite(figuresIDsPerFieldsIDs[toFieldID]) || (excludedToFieldsIDs & enpassantPawnBitboard) != 0L)) continue;
            if (Constants.isWhite(figuresIDsPerFieldsIDs[toFieldID])) {
                if ((toBitboard & 0xFF00000000000000L) != 0L) {
                    if (list != null) {
                        int cap_pid = figuresIDsPerFieldsIDs[toFieldID];
                        list.reserved_add(MoveInt.createCapturePromotion(fromFieldID, toFieldID, cap_pid, 11));
                        list.reserved_add(MoveInt.createCapturePromotion(fromFieldID, toFieldID, cap_pid, 10));
                        list.reserved_add(MoveInt.createCapturePromotion(fromFieldID, toFieldID, cap_pid, 9));
                        list.reserved_add(MoveInt.createCapturePromotion(fromFieldID, toFieldID, cap_pid, 8));
                    }
                    if ((count += 4) < maxCount) continue;
                    return count;
                }
                if (list != null) {
                    int capturedFigureID = figuresIDsPerFieldsIDs[toFieldID];
                    list.reserved_add(MoveInt.createCapture(figureID, fromFieldID, toFieldID, capturedFigureID));
                }
                if (++count < maxCount) continue;
                return count;
            }
            if (!hasEnpassant || (enpassCount = BlackPawnMovesGen.genEnpassantMove(board, excludedToFieldsIDs, figureID, fromFieldID, 0L, figuresIDsPerFieldsIDs, hasEnpassant, enpassantPawnBitboard, list, maxCount)) <= 0) continue;
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
            if ((toBitboard & 0xFF00000000000000L) != 0L) {
                if (list != null) {
                    list.reserved_add(MoveInt.createPromotion(fromFieldID, toFieldID, 11));
                    list.reserved_add(MoveInt.createPromotion(fromFieldID, toFieldID, 10));
                    list.reserved_add(MoveInt.createPromotion(fromFieldID, toFieldID, 9));
                    list.reserved_add(MoveInt.createPromotion(fromFieldID, toFieldID, 8));
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
            if ((excludedToFieldsIDs & toBitboard) != 0L || (toBitboard & allOpponentBitboard) == 0L || (toBitboard & 0xFF00000000000000L) != 0L) continue;
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
            if ((toBitboard & 0xFF00000000000000L) != 0L) continue;
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
        int count = 0;
        int[] validDirIDs = attacksValidDirs[fromFieldID];
        int[][] dirs_ids = attacksFieldIDs[fromFieldID];
        long[][] dirs = attacksBitboards[fromFieldID];
        for (int dirID : validDirIDs) {
            int figureColour;
            long opponentPawnBitboard;
            long toBitboard = dirs[dirID][0];
            int toFieldID = dirs_ids[dirID][0];
            if ((excludedToFieldsIDs & toBitboard) != 0L && (!hasEnpassant || Constants.isWhite(figuresIDsPerFieldsIDs[toFieldID]) || (excludedToFieldsIDs & enpassantPawnBitboard) != 0L) || !hasEnpassant || (enpassantPawnBitboard & (opponentPawnBitboard = Enpassanting.ADJOINING_FILE_BITBOARD_AT_CAPTURE[figureColour = Figures.getFigureColour(figureID)][fromFieldID][dirID])) == 0L) continue;
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
                if ((toBitboard & 0xFF00000000000000L) != 0L) {
                    if (list != null) {
                        int cap_pid = figuresIDsPerFieldsIDs[toFieldID];
                        list.reserved_add(MoveInt.createCapturePromotion(fromFieldID, toFieldID, cap_pid, 11));
                        list.reserved_add(MoveInt.createCapturePromotion(fromFieldID, toFieldID, cap_pid, 10));
                        list.reserved_add(MoveInt.createCapturePromotion(fromFieldID, toFieldID, cap_pid, 9));
                        list.reserved_add(MoveInt.createCapturePromotion(fromFieldID, toFieldID, cap_pid, 8));
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
            if (!hasEnpassant || (enpassCount = BlackPawnMovesGen.genEnpassantMove(board, excludedToFieldsIDs, figureID, fromFieldID, allOpponentBitboard, figuresIDsPerFieldsIDs, hasEnpassant, enpassantPawnBitboard, list, maxCount)) <= 0) continue;
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
            if ((toBitboard & 0xFF00000000000000L) == 0L) continue;
            if (list != null) {
                list.reserved_add(MoveInt.createPromotion(fromFieldID, toFieldID, 11));
                list.reserved_add(MoveInt.createPromotion(fromFieldID, toFieldID, 10));
                list.reserved_add(MoveInt.createPromotion(fromFieldID, toFieldID, 9));
                list.reserved_add(MoveInt.createPromotion(fromFieldID, toFieldID, 8));
            }
            if ((count += 4) < maxCount) continue;
            return maxCount;
        }
        return count;
    }

    public static final int genPromotionMoves(long excludedToFieldsIDs, boolean interuptAtFirstExclusionHit, long fromBitboard, int fromFieldID, long freeBitboard, long allOpponentBitboard, int[] figuresIDsPerFieldsIDs, IInternalMoveList list, int maxCount) {
        int count = 0;
        if ((fromBitboard & 0xFF000000000000L) != 0L) {
            int toFieldID;
            long toBitboard;
            int[] validDirIDs = attacksValidDirs[fromFieldID];
            int[][] dirs_ids = attacksFieldIDs[fromFieldID];
            long[][] dirs = attacksBitboards[fromFieldID];
            for (int dirID : validDirIDs) {
                toBitboard = dirs[dirID][0];
                if ((excludedToFieldsIDs & toBitboard) != 0L || (toBitboard & allOpponentBitboard) == 0L) continue;
                toFieldID = dirs_ids[dirID][0];
                if ((toBitboard & 0xFF00000000000000L) == 0L) {
                    throw new IllegalStateException();
                }
                if (list != null) {
                    int cap_pid = figuresIDsPerFieldsIDs[toFieldID];
                    list.reserved_add(MoveInt.createCapturePromotion(fromFieldID, toFieldID, cap_pid, 11));
                    list.reserved_add(MoveInt.createCapturePromotion(fromFieldID, toFieldID, cap_pid, 10));
                    list.reserved_add(MoveInt.createCapturePromotion(fromFieldID, toFieldID, cap_pid, 9));
                    list.reserved_add(MoveInt.createCapturePromotion(fromFieldID, toFieldID, cap_pid, 8));
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
                if ((toBitboard & 0xFF00000000000000L) == 0L) {
                    throw new IllegalStateException();
                }
                if (list != null) {
                    toFieldID = dirs_ids[dirID][0];
                    list.reserved_add(MoveInt.createPromotion(fromFieldID, toFieldID, 11));
                    list.reserved_add(MoveInt.createPromotion(fromFieldID, toFieldID, 10));
                    list.reserved_add(MoveInt.createPromotion(fromFieldID, toFieldID, 9));
                    list.reserved_add(MoveInt.createPromotion(fromFieldID, toFieldID, 8));
                }
                if ((count += 4) < maxCount) continue;
                return maxCount;
            }
        }
        return count;
    }

    public static final int genNonCaptureMoves(long excludedToFieldsIDs, boolean interuptAtFirstExclusionHit, int figureID, int fromFieldID, long freeBitboard, IInternalMoveList list, int maxCount) {
        int count = 0;
        int[] validDirIDs = nonattacksValidDirs[fromFieldID];
        int[][] dirs_ids = nonattacksFieldIDs[fromFieldID];
        long[][] dirs = nonattacksBitboards[fromFieldID];
        for (int dirID : validDirIDs) {
            long toBitboard = dirs[dirID][0];
            if ((excludedToFieldsIDs & toBitboard) != 0L) {
                if ((toBitboard & freeBitboard) != 0L && !interuptAtFirstExclusionHit) continue;
                break;
            }
            if ((toBitboard & freeBitboard) == 0L) break;
            if ((toBitboard & 0xFF00000000000000L) != 0L) continue;
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
                if ((middleFieldBitboard & 0xFF00000000000000L) != 0L) {
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
                if ((middleFieldBitboard & 0xFF00000000000000L) != 0L) {
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
        BlackPawnMovesGen.middleField[BlackPawnMovesGen.get67IDByBitboard((long)32768L)][BlackPawnMovesGen.get67IDByBitboard((long)0x80000000L)] = 0x800000L;
        BlackPawnMovesGen.middleField[BlackPawnMovesGen.get67IDByBitboard((long)16384L)][BlackPawnMovesGen.get67IDByBitboard((long)0x40000000L)] = 0x400000L;
        BlackPawnMovesGen.middleField[BlackPawnMovesGen.get67IDByBitboard((long)8192L)][BlackPawnMovesGen.get67IDByBitboard((long)0x20000000L)] = 0x200000L;
        BlackPawnMovesGen.middleField[BlackPawnMovesGen.get67IDByBitboard((long)4096L)][BlackPawnMovesGen.get67IDByBitboard((long)0x10000000L)] = 0x100000L;
        BlackPawnMovesGen.middleField[BlackPawnMovesGen.get67IDByBitboard((long)2048L)][BlackPawnMovesGen.get67IDByBitboard((long)0x8000000L)] = 524288L;
        BlackPawnMovesGen.middleField[BlackPawnMovesGen.get67IDByBitboard((long)1024L)][BlackPawnMovesGen.get67IDByBitboard((long)0x4000000L)] = 262144L;
        BlackPawnMovesGen.middleField[BlackPawnMovesGen.get67IDByBitboard((long)512L)][BlackPawnMovesGen.get67IDByBitboard((long)0x2000000L)] = 131072L;
        BlackPawnMovesGen.middleField[BlackPawnMovesGen.get67IDByBitboard((long)256L)][BlackPawnMovesGen.get67IDByBitboard((long)0x1000000L)] = 65536L;
        attacksValidDirs = ALL_BLACK_PAWN_ATTACKS_VALID_DIRS;
        nonattacksValidDirs = ALL_BLACK_PAWN_NONATTACKS_VALID_DIRS;
        attacksFieldIDs = ALL_BLACK_PAWN_ATTACKS_DIRS_WITH_FIELD_IDS;
        nonattacksFieldIDs = ALL_BLACK_PAWN_NONATTACKS_DIRS_WITH_FIELD_IDS;
        attacksBitboards = ALL_BLACK_PAWN_ATTACKS_DIRS_WITH_BITBOARDS;
        nonattacksBitboards = ALL_BLACK_PAWN_NONATTACKS_DIRS_WITH_BITBOARDS;
    }
}

