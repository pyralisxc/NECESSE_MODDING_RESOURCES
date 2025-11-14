/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.eval.pawns.model.pst;

import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.eval.pawns.model.ModelBuilder;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModel;
import bagaturchess.bitboard.impl.eval.pawns.model.pst.PSTGen;
import bagaturchess.bitboard.impl.plies.OfficerPlies;

public class PSTGenBishops
extends PSTGen {
    public static int genAttacks(int colour, int fromFieldID, long opPawnsAttacks, int opColour, long myKing, long opKing, long myPawns, long opPawns, PawnsModel model, IBitBoard bitboard) {
        long myAll = myKing | myPawns;
        long opAll = opKing | opPawns;
        int attacks_all = 0;
        int safe_attacks = 0;
        int attack_undefended_pawns = 0;
        int attack_king = 0;
        long[][] dirs = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[fromFieldID];
        int[][] dirFieldIDs = OfficerPlies.ALL_OFFICER_DIRS_WITH_FIELD_IDS[fromFieldID];
        block0: for (int dirID : OfficerPlies.ALL_OFFICER_VALID_DIRS[fromFieldID]) {
            long[] dirBitboards = dirs[dirID];
            for (int seq = 0; seq < dirBitboards.length; ++seq) {
                int toFieldID = dirFieldIDs[dirID][seq];
                long toBitboard = dirs[dirID][seq];
                if ((toBitboard & myAll) != 0L) continue block0;
                if ((toBitboard & opAll) != 0L) {
                    if ((toBitboard & opKing) != 0L) {
                        ++attack_king;
                        continue block0;
                    }
                    if ((toBitboard & opPawns) != 0L) {
                        if ((toBitboard & opPawnsAttacks) != 0L) {
                            ++attacks_all;
                            continue block0;
                        }
                        ++attack_undefended_pawns;
                        continue block0;
                    }
                    throw new IllegalStateException();
                }
                if ((toBitboard & opPawnsAttacks) != 0L) {
                    ++attacks_all;
                    continue;
                }
                ++safe_attacks;
            }
        }
        int score = 0;
        if (safe_attacks <= 1) {
            int[] trapped = colour == 0 ? W_TRAPPED_MINOR : B_TRAPPED_MINOR;
            score = trapped[fromFieldID] / (safe_attacks + 1);
        } else {
            score += BONUS_ATTACK * attacks_all;
            score += BONUS_ATTACK_SAFE * safe_attacks;
            score += BONUS_ATTACK_UNDEFENDED_PAWN * attack_undefended_pawns;
        }
        return score;
    }

    public static void fillPST(int[] pst, int colour, PawnsModel model, IBitBoard bitboard) {
        byte opColour = Figures.OPPONENT_COLOUR[colour];
        long myKing = bitboard.getFiguresBitboardByColourAndType(colour, 6);
        long opKing = bitboard.getFiguresBitboardByColourAndType(opColour, 6);
        long myPawns = bitboard.getFiguresBitboardByColourAndType(colour, 1);
        long opPawns = bitboard.getFiguresBitboardByColourAndType(opColour, 1);
        long all = myKing | opKing | myPawns | opPawns;
        for (int fromFieldID = 0; fromFieldID < 64; ++fromFieldID) {
            long fromBitboard = Figures.ALL_ORDERED_A1H1[fromFieldID];
            if ((fromBitboard & all) != 0L) {
                pst[fromFieldID] = 0;
                continue;
            }
            long opPawnsAttacks = colour == 0 ? model.getBattacks() : model.getWattacks();
            pst[fromFieldID] = (opPawnsAttacks & fromBitboard) != 0L ? PENALTY_ON_ATTACKED_SQUARE : PSTGenBishops.genAttacks(colour, fromFieldID, opPawnsAttacks, opColour, myKing, opKing, myPawns, opPawns, model, bitboard);
        }
    }

    public static void main(String[] args) {
        IBitBoard bitboard = BoardUtils.createBoard_WithPawnsCache("r1bq1r2/pp4k1/4p2p/3pPp1Q/3N1R1P/2PB4/6P1/6K1 w - - bm Rg4+");
        System.out.println(bitboard);
        int[] pst = new int[64];
        PSTGenBishops.fillPST(pst, 0, ModelBuilder.build(bitboard), bitboard);
        Utils.reverseSpecial(pst);
        for (int i = 0; i < 64; ++i) {
            if (i % 8 == 0) {
                System.out.println("");
            }
            System.out.print(pst[i] + "\t");
        }
    }
}

