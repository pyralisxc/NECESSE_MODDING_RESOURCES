/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.attacks.fast;

import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.attacks.control.FieldsStateMachine;
import bagaturchess.bitboard.impl.plies.BlackPawnPlies;
import bagaturchess.bitboard.impl.plies.WhitePawnPlies;

public class Pawns
extends Fields {
    public static final long genAttacks(int pawnColour, int pawnFieldID, FieldsStateMachine fac, boolean add) {
        long attacks = 0L;
        int[] validDirIDs = pawnColour == 0 ? WhitePawnPlies.ALL_WHITE_PAWN_ATTACKS_VALID_DIRS[pawnFieldID] : BlackPawnPlies.ALL_BLACK_PAWN_ATTACKS_VALID_DIRS[pawnFieldID];
        long[][] dirs = pawnColour == 0 ? WhitePawnPlies.ALL_WHITE_PAWN_ATTACKS_DIRS_WITH_BITBOARDS[pawnFieldID] : BlackPawnPlies.ALL_BLACK_PAWN_ATTACKS_DIRS_WITH_BITBOARDS[pawnFieldID];
        int[][] dirFieldIDs = pawnColour == 0 ? WhitePawnPlies.ALL_WHITE_PAWN_ATTACKS_DIRS_WITH_FIELD_IDS[pawnFieldID] : BlackPawnPlies.ALL_BLACK_PAWN_ATTACKS_DIRS_WITH_FIELD_IDS[pawnFieldID];
        for (int dirID : validDirIDs) {
            long toBitboard = dirs[dirID][0];
            attacks |= toBitboard;
            if (add) {
                fac.addAttack(pawnColour, 1, dirFieldIDs[dirID][0], toBitboard);
                continue;
            }
            fac.removeAttack(pawnColour, 1, dirFieldIDs[dirID][0], toBitboard);
        }
        return attacks;
    }

    public static long genAttacks(int pawnColour, int pawnFieldID) {
        long attacks = 0L;
        int[] validDirIDs = pawnColour == 0 ? WhitePawnPlies.ALL_WHITE_PAWN_ATTACKS_VALID_DIRS[pawnFieldID] : BlackPawnPlies.ALL_BLACK_PAWN_ATTACKS_VALID_DIRS[pawnFieldID];
        long[][] dirs = pawnColour == 0 ? WhitePawnPlies.ALL_WHITE_PAWN_ATTACKS_DIRS_WITH_BITBOARDS[pawnFieldID] : BlackPawnPlies.ALL_BLACK_PAWN_ATTACKS_DIRS_WITH_BITBOARDS[pawnFieldID];
        for (int dirID : validDirIDs) {
            long toBitboard = dirs[dirID][0];
            attacks |= toBitboard;
        }
        return attacks;
    }
}

