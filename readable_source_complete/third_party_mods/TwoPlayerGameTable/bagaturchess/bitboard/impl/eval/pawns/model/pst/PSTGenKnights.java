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
import bagaturchess.bitboard.impl.plies.KingPlies;
import bagaturchess.bitboard.impl.plies.KnightPlies;

public class PSTGenKnights
extends PSTGen {
    public static int genAttacks(int colour, int fromFieldID, long opPawnsAttacks, int opColour, long myKing, long opKing, long myPawns, long opPawns, PawnsModel model, IBitBoard bitboard) {
        long myAll = myKing | myPawns;
        long opAll = opKing | opPawns;
        int attacks_all = 0;
        int safe_attacks = 0;
        int attack_undefended_pawns = 0;
        int attack_king = 0;
        int[] validDirIDs = KnightPlies.ALL_KNIGHT_VALID_DIRS[fromFieldID];
        long[][] dirs = KnightPlies.ALL_KNIGHT_DIRS_WITH_BITBOARDS[fromFieldID];
        for (int dirID : validDirIDs) {
            long toBitboard = dirs[dirID][0];
            if ((toBitboard & myAll) != 0L) continue;
            if ((toBitboard & opAll) != 0L) {
                if ((toBitboard & opKing) != 0L) {
                    ++attack_king;
                    continue;
                }
                if ((toBitboard & opPawns) != 0L) {
                    if ((toBitboard & opPawnsAttacks) != 0L) {
                        ++attacks_all;
                        continue;
                    }
                    ++attack_undefended_pawns;
                    continue;
                }
                throw new IllegalStateException();
            }
            if ((toBitboard & opPawnsAttacks) != 0L) {
                ++attacks_all;
                continue;
            }
            ++safe_attacks;
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
        int opKingFieldID = colour == 0 ? bitboard.getPiecesLists().getPieces(12).getData()[0] : bitboard.getPiecesLists().getPieces(6).getData()[0];
        long all = myKing | opKing | myPawns | opPawns;
        for (int fromFieldID = 0; fromFieldID < 64; ++fromFieldID) {
            long fromBitboard = Figures.ALL_ORDERED_A1H1[fromFieldID];
            if ((fromBitboard & all) != 0L) {
                pst[fromFieldID] = 0;
                continue;
            }
            long opAttacks = colour == 0 ? model.getBattacks() : model.getWattacks();
            pst[fromFieldID] = ((opAttacks |= KingPlies.ALL_KING_MOVES[opKingFieldID]) & fromBitboard) != 0L ? PENALTY_ON_ATTACKED_SQUARE : PSTGenKnights.genAttacks(colour, fromFieldID, opAttacks, opColour, myKing, opKing, myPawns, opPawns, model, bitboard);
        }
    }

    public static void main(String[] args) {
        IBitBoard bitboard = BoardUtils.createBoard_WithPawnsCache("r1bq1r2/pp4k1/4p2p/3pPp1Q/3N1R1P/2PB4/6P1/6K1 w - - bm Rg4+");
        System.out.println(bitboard);
        int[] pst = new int[64];
        PSTGenKnights.fillPST(pst, 1, ModelBuilder.build(bitboard), bitboard);
        Utils.reverseSpecial(pst);
        for (int i = 0; i < 64; ++i) {
            if (i % 8 == 0) {
                System.out.println("");
            }
            System.out.print(pst[i] + "\t");
        }
    }
}

