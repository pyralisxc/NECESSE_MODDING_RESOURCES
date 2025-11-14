/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.attacks.fast;

import bagaturchess.bitboard.impl.Board;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.attacks.control.FieldsStateMachine;
import bagaturchess.bitboard.impl.plies.CastlePlies;

public class Castles
extends Fields {
    public static final long genAttacks(int colour, int castleFieldID, int figureType, int dirID2, Board bitboard, FieldsStateMachine fac, boolean add) {
        long attacks = 0L;
        long[][] dirs = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[castleFieldID];
        int[][] dirFieldIDs = CastlePlies.ALL_CASTLE_DIRS_WITH_FIELD_IDS[castleFieldID];
        if (dirID2 == -1) {
            block0: for (int dirID2 : CastlePlies.ALL_CASTLE_VALID_DIRS[castleFieldID]) {
                long[] dirBitboards = dirs[dirID2];
                for (int seq = 0; seq < dirBitboards.length; ++seq) {
                    long field = dirs[dirID2][seq];
                    attacks |= field;
                    if (add) {
                        fac.addAttack(colour, figureType, dirFieldIDs[dirID2][seq], field);
                    } else {
                        fac.removeAttack(colour, figureType, dirFieldIDs[dirID2][seq], field);
                    }
                    if ((field & bitboard.free) == 0L) continue block0;
                }
            }
        } else {
            long[] dirBitboards = dirs[dirID2];
            for (int seq = 0; seq < dirBitboards.length; ++seq) {
                long field = dirs[dirID2][seq];
                attacks |= field;
                if (add) {
                    fac.addAttack(colour, figureType, dirFieldIDs[dirID2][seq], field);
                } else {
                    fac.removeAttack(colour, figureType, dirFieldIDs[dirID2][seq], field);
                }
                if ((field & bitboard.free) != 0L) {
                    continue;
                }
                break;
            }
        }
        return attacks;
    }

    public static long genAttacks(int colour, int castleFieldID, int dirID2, Board bitboard) {
        long attacks = 0L;
        long[][] dirs = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[castleFieldID];
        if (dirID2 == -1) {
            block0: for (int dirID2 : CastlePlies.ALL_CASTLE_VALID_DIRS[castleFieldID]) {
                long[] dirBitboards = dirs[dirID2];
                for (int seq = 0; seq < dirBitboards.length; ++seq) {
                    long field = dirs[dirID2][seq];
                    attacks |= field;
                    if ((field & bitboard.free) == 0L) continue block0;
                }
            }
        } else {
            long[] dirBitboards = dirs[dirID2];
            for (int seq = 0; seq < dirBitboards.length; ++seq) {
                long field = dirs[dirID2][seq];
                attacks |= field;
                if ((field & bitboard.free) != 0L) {
                    continue;
                }
                break;
            }
        }
        return attacks;
    }
}

