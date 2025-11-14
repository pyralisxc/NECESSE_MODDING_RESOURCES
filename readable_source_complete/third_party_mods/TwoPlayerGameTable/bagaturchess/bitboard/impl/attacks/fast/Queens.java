/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.attacks.fast;

import bagaturchess.bitboard.impl.Board;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.attacks.control.FieldsStateMachine;
import bagaturchess.bitboard.impl.attacks.fast.Castles;
import bagaturchess.bitboard.impl.attacks.fast.Officers;

public class Queens
extends Fields {
    public static final long genAttacks(int colour, int queenFieldID, int dirID, int dirType, Board bitboard, FieldsStateMachine fac, boolean add) {
        long attacks = 0L;
        if (dirType != -1 && dirType != 4 && dirType != 3) {
            throw new IllegalStateException();
        }
        if (dirType == -1) {
            attacks |= Castles.genAttacks(colour, queenFieldID, 5, dirID, bitboard, fac, add);
            attacks |= Officers.genAttacks(colour, queenFieldID, 5, dirID, bitboard, fac, add);
        } else {
            attacks = dirType == 4 ? (attacks |= Castles.genAttacks(colour, queenFieldID, 5, dirID, bitboard, fac, add)) : (attacks |= Officers.genAttacks(colour, queenFieldID, 5, dirID, bitboard, fac, add));
        }
        return attacks;
    }

    public static long genAttacks(int colour, int queenFieldID, int dirID, int dirType, Board bitboard) {
        long attacks = 0L;
        if (dirType != -1 && dirType != 4 && dirType != 3) {
            throw new IllegalStateException();
        }
        if (dirType == -1) {
            attacks |= Castles.genAttacks(colour, queenFieldID, dirID, bitboard);
            attacks |= Officers.genAttacks(colour, queenFieldID, dirID, bitboard);
        } else {
            attacks = dirType == 4 ? (attacks |= Castles.genAttacks(colour, queenFieldID, dirID, bitboard)) : (attacks |= Officers.genAttacks(colour, queenFieldID, dirID, bitboard));
        }
        return attacks;
    }
}

