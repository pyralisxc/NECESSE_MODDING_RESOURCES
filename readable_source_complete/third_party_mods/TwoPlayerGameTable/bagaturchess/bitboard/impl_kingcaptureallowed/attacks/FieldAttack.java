/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl_kingcaptureallowed.attacks;

import bagaturchess.bitboard.api.IPiecesLists;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.plies.CastlePlies;
import bagaturchess.bitboard.impl.plies.KingPlies;
import bagaturchess.bitboard.impl.plies.KnightPlies;
import bagaturchess.bitboard.impl.plies.OfficerPlies;

public class FieldAttack {
    public static final boolean isFieldAttacked(int fieldID, int attackingColour, int[] board, IPiecesLists plist) {
        boolean hasKnights;
        boolean hasBishopsOrQueens;
        int expectedPID;
        int targetPID;
        int toFieldID;
        boolean hasRooksOrQueens;
        boolean bl = attackingColour == 0 ? plist.getPieces(4).getDataSize() + plist.getPieces(5).getDataSize() > 0 : (hasRooksOrQueens = plist.getPieces(10).getDataSize() + plist.getPieces(11).getDataSize() > 0);
        if (hasRooksOrQueens) {
            int[] validDirIDs = CastlePlies.ALL_CASTLE_VALID_DIRS[fieldID];
            int[][] dirs_ids = CastlePlies.ALL_CASTLE_DIRS_WITH_FIELD_IDS[fieldID];
            block4: for (int dirID : validDirIDs) {
                int[] dirIDs = dirs_ids[dirID];
                for (int seq = 0; seq < dirIDs.length; ++seq) {
                    toFieldID = dirIDs[seq];
                    targetPID = board[toFieldID];
                    if (targetPID == 0) continue;
                    int n = expectedPID = attackingColour == 0 ? 5 : 11;
                    if (targetPID == expectedPID) {
                        return true;
                    }
                    int n2 = expectedPID = attackingColour == 0 ? 4 : 10;
                    if (targetPID != expectedPID) continue block4;
                    return true;
                }
            }
        }
        boolean bl2 = attackingColour == 0 ? plist.getPieces(3).getDataSize() + plist.getPieces(5).getDataSize() > 0 : (hasBishopsOrQueens = plist.getPieces(9).getDataSize() + plist.getPieces(11).getDataSize() > 0);
        if (hasBishopsOrQueens) {
            int[] validDirIDs = OfficerPlies.ALL_OFFICER_VALID_DIRS[fieldID];
            int[][] dirs_ids = OfficerPlies.ALL_OFFICER_DIRS_WITH_FIELD_IDS[fieldID];
            block6: for (int dirID : validDirIDs) {
                int[] dirIDs = dirs_ids[dirID];
                for (int seq = 0; seq < dirIDs.length; ++seq) {
                    int expectedPID2;
                    int toFieldID2 = dirIDs[seq];
                    int targetPID2 = board[toFieldID2];
                    if (targetPID2 == 0) continue;
                    int n = expectedPID2 = attackingColour == 0 ? 5 : 11;
                    if (targetPID2 == expectedPID2) {
                        return true;
                    }
                    int n3 = expectedPID2 = attackingColour == 0 ? 3 : 9;
                    if (targetPID2 != expectedPID2) continue block6;
                    return true;
                }
            }
        }
        boolean bl3 = attackingColour == 0 ? plist.getPieces(2).getDataSize() > 0 : (hasKnights = plist.getPieces(8).getDataSize() > 0);
        if (hasKnights) {
            int[] validDirIDs = KnightPlies.ALL_KNIGHT_VALID_DIRS[fieldID];
            int[][] dirs_ids = KnightPlies.ALL_KNIGHT_DIRS_WITH_FIELD_IDS[fieldID];
            for (int dirID : validDirIDs) {
                toFieldID = dirs_ids[dirID][0];
                targetPID = board[toFieldID];
                if (targetPID == 0) continue;
                int n = expectedPID = attackingColour == 0 ? 2 : 8;
                if (targetPID != expectedPID) continue;
                return true;
            }
        }
        int[] validDirIDs = KingPlies.ALL_KING_VALID_DIRS[fieldID];
        int[][] dirs_ids = KingPlies.ALL_KING_DIRS_WITH_FIELD_IDS[fieldID];
        for (int dirID : validDirIDs) {
            toFieldID = dirs_ids[dirID][0];
            targetPID = board[toFieldID];
            if (targetPID == 0) continue;
            int n = expectedPID = attackingColour == 0 ? 6 : 12;
            if (targetPID != expectedPID) continue;
            return true;
        }
        switch (attackingColour) {
            case 0: {
                int targetFieldID;
                int targetPID3;
                int targetFieldID2;
                int letter = Fields.LETTERS[fieldID];
                if (letter != 0 && (targetFieldID2 = fieldID - 9) >= 0 && (targetPID3 = board[targetFieldID2]) == 1) {
                    return true;
                }
                if (letter == 7 || (targetFieldID = fieldID - 7) < 0 || (targetPID3 = board[targetFieldID]) != 1) break;
                return true;
            }
            case 1: {
                int targetFieldID;
                int targetFieldID3;
                int targetPID3;
                int letter = Fields.LETTERS[fieldID];
                if (letter != 0 && (targetFieldID3 = fieldID + 7) <= 63 && (targetPID3 = board[targetFieldID3]) == 7) {
                    return true;
                }
                if (letter == 7 || (targetFieldID = fieldID + 9) > 63 || (targetPID3 = board[targetFieldID]) != 7) break;
                return true;
            }
        }
        return false;
    }
}

