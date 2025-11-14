/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl_kingcaptureallowed.movegen;

import bagaturchess.bitboard.api.IInternalMoveList;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.bitboard.impl.plies.checking.BlackPawnsChecks;
import bagaturchess.bitboard.impl_kingcaptureallowed.plies.Enpassanting;

public class BlackPawnMovesGen
extends BlackPawnsChecks {
    static final int[][] attacksValidDirs = ALL_BLACK_PAWN_ATTACKS_VALID_DIRS;
    static final int[][] nonattacksValidDirs = ALL_BLACK_PAWN_NONATTACKS_VALID_DIRS;
    static final int[][][] attacksFieldIDs = ALL_BLACK_PAWN_ATTACKS_DIRS_WITH_FIELD_IDS;
    static final int[][][] nonattacksFieldIDs = ALL_BLACK_PAWN_NONATTACKS_DIRS_WITH_FIELD_IDS;
    static final long[][][] attacksBitboards = ALL_BLACK_PAWN_ATTACKS_DIRS_WITH_BITBOARDS;
    static final long[][][] nonattacksBitboards = ALL_BLACK_PAWN_NONATTACKS_DIRS_WITH_BITBOARDS;

    public static final void genAllMoves(int fromFieldID, int[] figuresIDsPerFieldsIDs, int enpassantEnemyPawnFieldID, IInternalMoveList list) {
        int targetPID;
        int toFieldID;
        int dirID2;
        int[] validDirIDs = attacksValidDirs[fromFieldID];
        int[][] dirs_ids = attacksFieldIDs[fromFieldID];
        for (int dirID2 : validDirIDs) {
            toFieldID = dirs_ids[dirID2][0];
            targetPID = figuresIDsPerFieldsIDs[toFieldID];
            if (targetPID == 0) {
                int enemyFieldID;
                if (enpassantEnemyPawnFieldID == -1 || (enemyFieldID = Enpassanting.ADJOINING_FILE_FIELD_ID_AT_CAPTURE[1][fromFieldID][dirID2]) != enpassantEnemyPawnFieldID) continue;
                list.reserved_add(MoveInt.createEnpassant(7, fromFieldID, toFieldID, dirID2, 1));
                continue;
            }
            if (Constants.hasSameColour(7, targetPID)) continue;
            long toBitboard = Fields.ALL_A1H1[toFieldID];
            if ((toBitboard & 0xFF00000000000000L) != 0L) {
                int cap_pid = figuresIDsPerFieldsIDs[toFieldID];
                list.reserved_add(MoveInt.createCapturePromotion(fromFieldID, toFieldID, cap_pid, 11));
                list.reserved_add(MoveInt.createCapturePromotion(fromFieldID, toFieldID, cap_pid, 10));
                list.reserved_add(MoveInt.createCapturePromotion(fromFieldID, toFieldID, cap_pid, 9));
                list.reserved_add(MoveInt.createCapturePromotion(fromFieldID, toFieldID, cap_pid, 8));
                continue;
            }
            list.reserved_add(MoveInt.createCapture(7, fromFieldID, toFieldID, targetPID));
        }
        validDirIDs = nonattacksValidDirs[fromFieldID];
        dirs_ids = nonattacksFieldIDs[fromFieldID];
        int size = validDirIDs.length;
        for (int i = 0; i < size && (targetPID = figuresIDsPerFieldsIDs[toFieldID = dirs_ids[dirID2 = validDirIDs[i]][0]]) == 0; ++i) {
            long toBitboard = Fields.ALL_A1H1[toFieldID];
            if ((toBitboard & 0xFF00000000000000L) != 0L) {
                list.reserved_add(MoveInt.createPromotion(fromFieldID, toFieldID, 11));
                list.reserved_add(MoveInt.createPromotion(fromFieldID, toFieldID, 10));
                list.reserved_add(MoveInt.createPromotion(fromFieldID, toFieldID, 9));
                list.reserved_add(MoveInt.createPromotion(fromFieldID, toFieldID, 8));
                continue;
            }
            list.reserved_add(MoveInt.createNonCapture(7, fromFieldID, toFieldID));
        }
    }

    public static final void genCapturePromotionMoves(int fromFieldID, int[] figuresIDsPerFieldsIDs, IInternalMoveList list) {
        long toBitboard;
        int targetPID;
        int toFieldID;
        int dirID2;
        int[] validDirIDs = attacksValidDirs[fromFieldID];
        int[][] dirs_ids = attacksFieldIDs[fromFieldID];
        for (int dirID2 : validDirIDs) {
            toFieldID = dirs_ids[dirID2][0];
            targetPID = figuresIDsPerFieldsIDs[toFieldID];
            if (targetPID == 0 || Constants.hasSameColour(7, targetPID)) continue;
            toBitboard = Fields.ALL_A1H1[toFieldID];
            if ((toBitboard & 0xFF00000000000000L) != 0L) {
                int cap_pid = figuresIDsPerFieldsIDs[toFieldID];
                list.reserved_add(MoveInt.createCapturePromotion(fromFieldID, toFieldID, cap_pid, 11));
                list.reserved_add(MoveInt.createCapturePromotion(fromFieldID, toFieldID, cap_pid, 10));
                list.reserved_add(MoveInt.createCapturePromotion(fromFieldID, toFieldID, cap_pid, 9));
                list.reserved_add(MoveInt.createCapturePromotion(fromFieldID, toFieldID, cap_pid, 8));
                continue;
            }
            list.reserved_add(MoveInt.createCapture(7, fromFieldID, toFieldID, targetPID));
        }
        validDirIDs = nonattacksValidDirs[fromFieldID];
        dirs_ids = nonattacksFieldIDs[fromFieldID];
        int size = validDirIDs.length;
        for (int i = 0; i < size && (targetPID = figuresIDsPerFieldsIDs[toFieldID = dirs_ids[dirID2 = validDirIDs[i]][0]]) == 0; ++i) {
            toBitboard = Fields.ALL_A1H1[toFieldID];
            if ((toBitboard & 0xFF00000000000000L) == 0L) continue;
            list.reserved_add(MoveInt.createPromotion(fromFieldID, toFieldID, 11));
            list.reserved_add(MoveInt.createPromotion(fromFieldID, toFieldID, 10));
            list.reserved_add(MoveInt.createPromotion(fromFieldID, toFieldID, 9));
            list.reserved_add(MoveInt.createPromotion(fromFieldID, toFieldID, 8));
        }
    }
}

