/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl_kingcaptureallowed.movegen;

import bagaturchess.bitboard.api.IInternalMoveList;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.bitboard.impl.plies.checking.OfficerChecks;

public class OfficerMovesGen
extends OfficerChecks {
    static final int[][] validDirsIDs = ALL_OFFICER_VALID_DIRS;
    static final int[][][] dirsFieldIDs = ALL_OFFICER_DIRS_WITH_FIELD_IDS;

    public static final void genAllMoves(int pid, int fromFieldID, int[] figuresIDsPerFieldsIDs, IInternalMoveList list) {
        int[] validDirIDs = validDirsIDs[fromFieldID];
        int[][] dirs_ids = dirsFieldIDs[fromFieldID];
        block0: for (int dirID : validDirIDs) {
            int[] dirIDs = dirs_ids[dirID];
            for (int seq = 0; seq < dirIDs.length; ++seq) {
                int toFieldID = dirIDs[seq];
                int targetPID = figuresIDsPerFieldsIDs[toFieldID];
                if (targetPID != 0) {
                    if (Constants.hasSameColour(pid, targetPID)) continue block0;
                    list.reserved_add(MoveInt.createCapture(pid, fromFieldID, toFieldID, targetPID));
                    continue block0;
                }
                list.reserved_add(MoveInt.createNonCapture(pid, fromFieldID, toFieldID));
            }
        }
    }

    public static final void genCaptureMoves(int pid, int fromFieldID, int[] figuresIDsPerFieldsIDs, IInternalMoveList list) {
        int[] validDirIDs = validDirsIDs[fromFieldID];
        int[][] dirs_ids = dirsFieldIDs[fromFieldID];
        block0: for (int dirID : validDirIDs) {
            int[] dirIDs = dirs_ids[dirID];
            for (int seq = 0; seq < dirIDs.length; ++seq) {
                int toFieldID = dirIDs[seq];
                int targetPID = figuresIDsPerFieldsIDs[toFieldID];
                if (targetPID == 0) continue;
                if (Constants.hasSameColour(pid, targetPID)) continue block0;
                list.reserved_add(MoveInt.createCapture(pid, fromFieldID, toFieldID, targetPID));
                continue block0;
            }
        }
    }
}

