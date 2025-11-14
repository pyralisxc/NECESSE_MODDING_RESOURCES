/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.attacks.fast;

import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.attacks.control.FieldsStateMachine;
import bagaturchess.bitboard.impl.plies.KingPlies;

public class King
extends Fields {
    public static final long genAttacks(int colour, int fromFieldID, FieldsStateMachine fac, boolean add) {
        long attacks = 0L;
        int[] validDirIDs = KingPlies.ALL_KING_VALID_DIRS[fromFieldID];
        int[][] dirFieldIDs = KingPlies.ALL_KING_DIRS_WITH_FIELD_IDS[fromFieldID];
        long[][] dirs = KingPlies.ALL_KING_DIRS_WITH_BITBOARDS[fromFieldID];
        for (int dirID : validDirIDs) {
            long toBitboard = dirs[dirID][0];
            attacks |= toBitboard;
            if (add) {
                fac.addAttack(colour, 6, dirFieldIDs[dirID][0], toBitboard);
                continue;
            }
            fac.removeAttack(colour, 6, dirFieldIDs[dirID][0], toBitboard);
        }
        return attacks;
    }

    public static long genAttacks(int fromFieldID) {
        long attacks = 0L;
        int[] validDirIDs = KingPlies.ALL_KING_VALID_DIRS[fromFieldID];
        long[][] dirs = KingPlies.ALL_KING_DIRS_WITH_BITBOARDS[fromFieldID];
        for (int dirID : validDirIDs) {
            long toBitboard = dirs[dirID][0];
            attacks |= toBitboard;
        }
        return attacks;
    }
}

