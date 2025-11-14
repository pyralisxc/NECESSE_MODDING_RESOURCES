/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl_kingcaptureallowed.movegen;

import bagaturchess.bitboard.api.IInternalMoveList;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.bitboard.impl.plies.checking.KnightChecks;

public class KnightMovesGen
extends KnightChecks {
    static final int[][] validDirsIDs = ALL_KNIGHT_VALID_DIRS;
    static final int[][][] dirsFieldIDs = ALL_KNIGHT_DIRS_WITH_FIELD_IDS;

    public static final void genAllMoves(int pid, int fromFieldID, int[] figuresIDsPerFieldsIDs, IInternalMoveList list) {
        int[] validDirIDs = validDirsIDs[fromFieldID];
        int[][] dirs_ids = dirsFieldIDs[fromFieldID];
        for (int dirID : validDirIDs) {
            int toFieldID = dirs_ids[dirID][0];
            int targetPID = figuresIDsPerFieldsIDs[toFieldID];
            if (targetPID == 0) {
                list.reserved_add(MoveInt.createNonCapture(pid, fromFieldID, toFieldID));
                continue;
            }
            if (Constants.hasSameColour(pid, targetPID)) continue;
            list.reserved_add(MoveInt.createCapture(pid, fromFieldID, toFieldID, targetPID));
        }
    }

    public static final void genCaptureMoves(int pid, int fromFieldID, int[] figuresIDsPerFieldsIDs, IInternalMoveList list) {
        int[] validDirIDs = validDirsIDs[fromFieldID];
        int[][] dirs_ids = dirsFieldIDs[fromFieldID];
        for (int dirID : validDirIDs) {
            int toFieldID = dirs_ids[dirID][0];
            int targetPID = figuresIDsPerFieldsIDs[toFieldID];
            if (targetPID == 0 || Constants.hasSameColour(pid, targetPID)) continue;
            list.reserved_add(MoveInt.createCapture(pid, fromFieldID, toFieldID, targetPID));
        }
    }
}

