/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.attacks.fast;

import bagaturchess.bitboard.impl.Board;
import bagaturchess.bitboard.impl.attacks.control.FieldsStateMachine;
import bagaturchess.bitboard.impl.attacks.fast.Castles;
import bagaturchess.bitboard.impl.attacks.fast.King;
import bagaturchess.bitboard.impl.attacks.fast.Knights;
import bagaturchess.bitboard.impl.attacks.fast.Officers;
import bagaturchess.bitboard.impl.attacks.fast.Pawns;
import bagaturchess.bitboard.impl.attacks.fast.Queens;

public class AttacksBuilder {
    static long genAttacks(Board bitboard, int figureColour, int figureType, int fieldID, int dirID, int dirType, FieldsStateMachine fieldAttacksCollector, boolean add) {
        switch (figureType) {
            case 6: {
                return King.genAttacks(figureColour, fieldID, fieldAttacksCollector, add);
            }
            case 2: {
                return Knights.genAttacks(figureColour, fieldID, fieldAttacksCollector, add);
            }
            case 1: {
                return Pawns.genAttacks(figureColour, fieldID, fieldAttacksCollector, add);
            }
            case 3: {
                return Officers.genAttacks(figureColour, fieldID, 3, dirID, bitboard, fieldAttacksCollector, add);
            }
            case 4: {
                return Castles.genAttacks(figureColour, fieldID, 4, dirID, bitboard, fieldAttacksCollector, add);
            }
            case 5: {
                return Queens.genAttacks(figureColour, fieldID, dirID, dirType, bitboard, fieldAttacksCollector, add);
            }
        }
        throw new IllegalStateException();
    }

    static long genAttacks(Board bitboard, int figureColour, int figureType, int fieldID, int dirID, int dirType) {
        switch (figureType) {
            case 6: {
                return King.genAttacks(fieldID);
            }
            case 2: {
                return Knights.genAttacks(fieldID);
            }
            case 1: {
                return Pawns.genAttacks(figureColour, fieldID);
            }
            case 3: {
                return Officers.genAttacks(figureColour, fieldID, dirID, bitboard);
            }
            case 4: {
                return Castles.genAttacks(figureColour, fieldID, dirID, bitboard);
            }
            case 5: {
                return Queens.genAttacks(figureColour, fieldID, dirID, dirType, bitboard);
            }
        }
        throw new IllegalStateException();
    }
}

