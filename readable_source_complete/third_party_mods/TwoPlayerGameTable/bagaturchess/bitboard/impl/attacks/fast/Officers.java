/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.attacks.fast;

import bagaturchess.bitboard.impl.Board;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.attacks.control.FieldsStateMachine;
import bagaturchess.bitboard.impl.plies.OfficerPlies;

public class Officers
extends Fields {
    public static final long genAttacks(int colour, int officerFieldID, int figureType, int dirID2, Board bitboard, FieldsStateMachine fac, boolean add) {
        long attacks = 0L;
        long[][] dirs = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[officerFieldID];
        int[][] dirFieldIDs = OfficerPlies.ALL_OFFICER_DIRS_WITH_FIELD_IDS[officerFieldID];
        if (dirID2 == -1) {
            block0: for (int dirID2 : OfficerPlies.ALL_OFFICER_VALID_DIRS[officerFieldID]) {
                long[] dirBitboards = dirs[dirID2];
                boolean stop = false;
                for (int seq = 0; seq < dirBitboards.length; ++seq) {
                    long field = dirs[dirID2][seq];
                    attacks |= field;
                    if (add) {
                        fac.addAttack(colour, figureType, dirFieldIDs[dirID2][seq], field);
                    } else {
                        fac.removeAttack(colour, figureType, dirFieldIDs[dirID2][seq], field);
                    }
                    if (stop || (field & bitboard.free) == 0L && !stop) continue block0;
                }
            }
        } else {
            long[] dirBitboards = dirs[dirID2];
            boolean stop = false;
            for (int seq = 0; seq < dirBitboards.length; ++seq) {
                long field = dirs[dirID2][seq];
                attacks |= field;
                if (add) {
                    fac.addAttack(colour, figureType, dirFieldIDs[dirID2][seq], field);
                } else {
                    fac.removeAttack(colour, figureType, dirFieldIDs[dirID2][seq], field);
                }
                if (!stop && ((field & bitboard.free) != 0L || stop)) {
                    continue;
                }
                break;
            }
        }
        return attacks;
    }

    public static long genAttacks(int colour, int officerFieldID, int dirID2, Board bitboard) {
        long attacks = 0L;
        long[][] dirs = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[officerFieldID];
        if (dirID2 == -1) {
            block0: for (int dirID2 : OfficerPlies.ALL_OFFICER_VALID_DIRS[officerFieldID]) {
                long[] dirBitboards = dirs[dirID2];
                boolean stop = false;
                for (int seq = 0; seq < dirBitboards.length; ++seq) {
                    long field = dirs[dirID2][seq];
                    attacks |= field;
                    if (stop || (field & bitboard.free) == 0L && !stop) continue block0;
                }
            }
        } else {
            long[] dirBitboards = dirs[dirID2];
            boolean stop = false;
            for (int seq = 0; seq < dirBitboards.length; ++seq) {
                long field = dirs[dirID2][seq];
                attacks |= field;
                if (!stop && ((field & bitboard.free) != 0L || stop)) {
                    continue;
                }
                break;
            }
        }
        return attacks;
    }
}

