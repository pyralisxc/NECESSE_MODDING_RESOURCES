/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.attacks;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IPlayerAttacks;
import bagaturchess.bitboard.api.ISEE;
import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.attacks.control.metadata.SeeMetadata;
import bagaturchess.bitboard.impl.attacks.control.metadata.singlecolour.FieldAttacksStateMachine;
import bagaturchess.bitboard.impl.eval.BaseEvalWeights;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.bitboard.impl.plies.CastlePlies;
import bagaturchess.bitboard.impl.plies.KingPlies;
import bagaturchess.bitboard.impl.plies.KnightPlies;
import bagaturchess.bitboard.impl.plies.OfficerPlies;

public class SEE
implements ISEE {
    private static final boolean DUBUG = false;
    private static final boolean STOP_AT_QUEEN_ATTACK = false;
    private IBitBoard bitboard;
    private int[] myAttacksList;
    private int[] opAttacksList;
    private int myAttacksCount;
    private int opAttacksCount;
    private boolean[] canBeCaptured = new boolean[1];
    private int[] buff = new int[3];

    public SEE(IBitBoard _bitboard) {
        this.bitboard = _bitboard;
        this.myAttacksList = new int[12];
        this.opAttacksList = new int[12];
    }

    @Override
    public int evalExchange(int move) {
        return this.evalExchange(move, this.canBeCaptured);
    }

    public int evalExchange(int move, boolean[] canBeCaptured) {
        int countAttacks = 0;
        int value = 0;
        if (MoveInt.isCastling(move)) {
            return this.getCost(1);
        }
        if (MoveInt.isEnpassant(move)) {
            return this.getCost(1);
        }
        boolean initialPawnMove = MoveInt.isPawn(move);
        boolean initialPawnCapture = false;
        if (MoveInt.isCaptureOrPromotion(move)) {
            if (MoveInt.isCapture(move)) {
                int capFigType = MoveInt.getCapturedFigureType(move);
                value += this.getCost(capFigType);
                initialPawnCapture = initialPawnMove;
            }
            ++countAttacks;
        }
        int oppColour = MoveInt.getOpponentColour(move);
        long toFieldBitboard = MoveInt.getToFieldBitboard(move);
        int figType = MoveInt.getFigureType(move);
        int colour = MoveInt.getColour(move);
        int skipType = 0;
        if (initialPawnMove) {
            if (initialPawnCapture) {
                skipType = figType;
            }
        } else {
            skipType = figType;
        }
        int toFieldID = MoveInt.getToFieldID(move);
        int c_state = -1;
        int oc_state = -1;
        if (this.bitboard.getAttacksSupport()) {
            if (this.bitboard.getFieldsStateSupport()) {
                c_state = this.bitboard.getFieldsAttacks().getControlArray(colour)[toFieldID];
                oc_state = this.bitboard.getFieldsAttacks().getControlArray(oppColour)[toFieldID];
            } else {
                c_state = colour == 0 ? this.buildAttacksListByAttacks(0, toFieldBitboard) : this.buildAttacksListByAttacks(1, toFieldBitboard);
                oc_state = oppColour == 0 ? this.buildAttacksListByAttacks(0, toFieldBitboard) : this.buildAttacksListByAttacks(1, toFieldBitboard);
            }
        } else {
            c_state = colour == 0 ? this.buildAttacksList(false, 0, toFieldID, toFieldBitboard) : this.buildAttacksList(false, 1, toFieldID, toFieldBitboard);
            oc_state = oppColour == 0 ? this.buildAttacksList(false, 0, toFieldID, toFieldBitboard) : this.buildAttacksList(false, 1, toFieldID, toFieldBitboard);
        }
        int test = 0;
        test = SeeMetadata.getSingleton().seeMove(skipType, figType, c_state, oc_state);
        if (MoveInt.isPromotion(move)) {
            if (test == 0) {
                int promFigType = MoveInt.getPromotionFigureType(move);
                value += this.getCost(promFigType) - this.getCost(1);
            } else if (test == 100 || test != 75) {
                // empty if block
            }
        }
        return value -= test;
    }

    @Override
    public int seeField(int fieldID) {
        int figurePID = this.bitboard.getFigureID(fieldID);
        int figureColour = Figures.getFigureColour(figurePID);
        int figureType = Figures.getFigureType(figurePID);
        long fieldBoard = Fields.ALL_A1H1[fieldID];
        int w_state = 0;
        int b_state = 0;
        if (this.bitboard.getAttacksSupport()) {
            if (this.bitboard.getFieldsStateSupport()) {
                w_state = this.bitboard.getFieldsAttacks().getControlArray(0)[fieldID];
                b_state = this.bitboard.getFieldsAttacks().getControlArray(1)[fieldID];
            } else {
                w_state = this.buildAttacksListByAttacks(0, fieldID);
                b_state = this.buildAttacksListByAttacks(1, fieldID);
            }
        } else {
            this.buff[0] = 0;
            this.buff[1] = 0;
            this.buildAttacksList(fieldID, fieldBoard, this.buff);
            w_state = this.buff[0];
            b_state = this.buff[1];
        }
        int result = -SeeMetadata.getSingleton().seeMove(0, figureType, figureColour == 0 ? w_state : b_state, figureColour == 0 ? b_state : w_state);
        return result;
    }

    @Override
    public int seeMove(int pieceColour, int pieceType, int toFiledID) {
        int w_state = 0;
        int b_state = 0;
        if (this.bitboard.getAttacksSupport()) {
            if (this.bitboard.getFieldsStateSupport()) {
                w_state = this.bitboard.getFieldsAttacks().getControlArray(0)[toFiledID];
                b_state = this.bitboard.getFieldsAttacks().getControlArray(1)[toFiledID];
            } else {
                w_state = this.buildAttacksListByAttacks(0, toFiledID);
                b_state = this.buildAttacksListByAttacks(1, toFiledID);
            }
        } else {
            this.buff[0] = 0;
            this.buff[1] = 0;
            long fieldBoard = Fields.ALL_A1H1[toFiledID];
            this.buildAttacksList(toFiledID, fieldBoard, this.buff);
            w_state = this.buff[0];
            b_state = this.buff[1];
        }
        int result = -SeeMetadata.getSingleton().seeMove(pieceType, pieceType, pieceColour == 0 ? w_state : b_state, pieceColour == 0 ? b_state : w_state);
        return result;
    }

    private static int nextState(int currentState, int figureType) {
        int next = FieldAttacksStateMachine.getInstance().getMachine()[0][figureType][currentState];
        if (next == -1) {
            throw new IllegalStateException("Try to add attack from " + Figures.TYPES_SIGN[figureType] + " to state " + String.valueOf(FieldAttacksStateMachine.getInstance().getAllStatesList()[currentState]));
        }
        return next;
    }

    private static int nextState(int currentState, int figureType, int count) {
        for (int i = 0; i < count; ++i) {
            currentState = SEE.nextState(currentState, figureType);
        }
        return currentState;
    }

    public int buildAttacksList(int colour, int fieldID) {
        return this.buildAttacksList(false, colour, fieldID, Fields.ALL_A1H1[fieldID]);
    }

    public int buildAttacksListByAttacks(int colour, long toFieldBitboard) {
        long queens;
        long rooks;
        long bishops;
        long knights;
        long pawns;
        int state = 0;
        IPlayerAttacks attacks = this.bitboard.getPlayerAttacks(colour);
        long all = attacks.allAttacks();
        if ((all & toFieldBitboard) == 0L) {
            return state;
        }
        if ((attacks.attacksByType(6) & toFieldBitboard) != 0L) {
            state = SEE.nextState(state, 6, 1);
        }
        if (((pawns = attacks.attacksByType(1)) & toFieldBitboard) != 0L) {
            long[] unint = attacks.attacksByTypeUnintersected(1);
            int unint_size = attacks.attacksByTypeUnintersectedSize(1);
            if (unint_size == 1) {
                state = SEE.nextState(state, 1, 1);
            } else {
                long cur;
                for (int i = 0; i < unint_size && ((cur = unint[i]) & toFieldBitboard) != 0L; ++i) {
                    state = SEE.nextState(state, 1, 1);
                }
            }
        }
        if (((knights = attacks.attacksByType(2)) & toFieldBitboard) != 0L) {
            long[] unint = attacks.attacksByTypeUnintersected(2);
            int unint_size = attacks.attacksByTypeUnintersectedSize(2);
            if (unint_size == 1) {
                state = SEE.nextState(state, 2, 1);
            } else {
                long cur;
                for (int i = 0; i < unint_size && ((cur = unint[i]) & toFieldBitboard) != 0L; ++i) {
                    state = SEE.nextState(state, 2, 1);
                }
            }
        }
        if (((bishops = attacks.attacksByType(3)) & toFieldBitboard) != 0L) {
            long[] unint = attacks.attacksByTypeUnintersected(3);
            int unint_size = attacks.attacksByTypeUnintersectedSize(3);
            if (unint_size == 1) {
                state = SEE.nextState(state, 3, 1);
            } else {
                long cur;
                for (int i = 0; i < unint_size && ((cur = unint[i]) & toFieldBitboard) != 0L; ++i) {
                    state = SEE.nextState(state, 3, 1);
                }
            }
        }
        if (((rooks = attacks.attacksByType(4)) & toFieldBitboard) != 0L) {
            long[] unint = attacks.attacksByTypeUnintersected(4);
            int unint_size = attacks.attacksByTypeUnintersectedSize(4);
            if (unint_size == 1) {
                state = SEE.nextState(state, 4, 1);
            } else {
                long cur;
                for (int i = 0; i < unint_size && ((cur = unint[i]) & toFieldBitboard) != 0L; ++i) {
                    state = SEE.nextState(state, 4, 1);
                }
            }
        }
        if (((queens = attacks.attacksByType(5)) & toFieldBitboard) != 0L) {
            long[] unint = attacks.attacksByTypeUnintersected(5);
            int unint_size = attacks.attacksByTypeUnintersectedSize(5);
            if (unint_size == 1) {
                state = SEE.nextState(state, 5, 1);
            } else {
                long cur;
                for (int i = 0; i < unint_size && ((cur = unint[i]) & toFieldBitboard) != 0L; ++i) {
                    state = SEE.nextState(state, 5, 1);
                }
            }
        }
        return state;
    }

    public int buildAttacksList(boolean stop, int colour, int toFieldID, long toFieldBitboard) {
        int j;
        long[] dirMoves;
        boolean queenAttack;
        long dir;
        int state = 0;
        long free = this.bitboard.getFreeBitboard();
        long pawns = 0L;
        if (colour == 0) {
            pawns = this.bitboard.getFiguresBitboardByColourAndType(colour, 1);
            long potentialWPawnsPlaces = 0L;
            if ((0x8080808080808080L & toFieldBitboard) == 0L) {
                potentialWPawnsPlaces |= toFieldBitboard << 9;
            }
            if ((0x101010101010101L & toFieldBitboard) == 0L) {
                potentialWPawnsPlaces |= toFieldBitboard << 7;
            }
            if ((pawns & potentialWPawnsPlaces) != 0L) {
                state = SEE.nextState(state, 1, Utils.countBits(pawns & potentialWPawnsPlaces));
            }
        } else {
            pawns = this.bitboard.getFiguresBitboardByColourAndType(colour, 1);
            long potentialBPawnsPlaces = 0L;
            if ((0x8080808080808080L & toFieldBitboard) == 0L) {
                potentialBPawnsPlaces |= toFieldBitboard >>> 7;
            }
            if ((0x101010101010101L & toFieldBitboard) == 0L) {
                potentialBPawnsPlaces |= toFieldBitboard >>> 9;
            }
            if ((pawns & potentialBPawnsPlaces) != 0L) {
                state = SEE.nextState(state, 1, Utils.countBits(pawns & potentialBPawnsPlaces));
            }
        }
        if (stop && state > 0) {
            return state;
        }
        long potentialKnightsPlaces = KnightPlies.ALL_KNIGHT_MOVES[toFieldID];
        long knights = this.bitboard.getFiguresBitboardByColourAndType(colour, 2);
        if ((knights & potentialKnightsPlaces) != 0L) {
            state = SEE.nextState(state, 2, Utils.countBits(knights & potentialKnightsPlaces));
        }
        if (stop && state > 0) {
            return state;
        }
        long queens = this.bitboard.getFiguresBitboardByColourAndType(colour, 5);
        long potentialOfficersPlaces = OfficerPlies.ALL_OFFICER_MOVES[toFieldID];
        long officers = this.bitboard.getFiguresBitboardByColourAndType(colour, 3);
        long officer_sliders = officers | queens;
        if ((officer_sliders & potentialOfficersPlaces) != 0L) {
            long officerMove;
            dir = OfficerPlies.ALL_OFFICER_DIR0_MOVES[toFieldID];
            if ((officer_sliders & dir & potentialOfficersPlaces) != 0L) {
                queenAttack = false;
                dirMoves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[toFieldID][0];
                for (j = 0; j < dirMoves.length; ++j) {
                    officerMove = dirMoves[j];
                    if ((officerMove & officers) != 0L) {
                        if (queenAttack) {
                            state = SEE.nextState(state, 5);
                            continue;
                        }
                        state = SEE.nextState(state, 3);
                        continue;
                    }
                    if ((officerMove & queens) != 0L) {
                        state = SEE.nextState(state, 5);
                        queenAttack = true;
                        continue;
                    }
                    if ((officerMove & free) == 0L && (colour != 1 || j != 0 || (pawns & officerMove) == 0L)) break;
                }
            }
            if ((officer_sliders & (dir = OfficerPlies.ALL_OFFICER_DIR1_MOVES[toFieldID]) & potentialOfficersPlaces) != 0L) {
                queenAttack = false;
                dirMoves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[toFieldID][1];
                for (j = 0; j < dirMoves.length; ++j) {
                    officerMove = dirMoves[j];
                    if ((officerMove & officers) != 0L) {
                        if (queenAttack) {
                            state = SEE.nextState(state, 5);
                            continue;
                        }
                        state = SEE.nextState(state, 3);
                        continue;
                    }
                    if ((officerMove & queens) != 0L) {
                        state = SEE.nextState(state, 5);
                        queenAttack = true;
                        continue;
                    }
                    if ((officerMove & free) == 0L && (colour != 0 || j != 0 || (pawns & officerMove) == 0L)) break;
                }
            }
            if ((officer_sliders & (dir = OfficerPlies.ALL_OFFICER_DIR2_MOVES[toFieldID]) & potentialOfficersPlaces) != 0L) {
                queenAttack = false;
                dirMoves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[toFieldID][2];
                for (j = 0; j < dirMoves.length; ++j) {
                    officerMove = dirMoves[j];
                    if ((officerMove & officers) != 0L) {
                        if (queenAttack) {
                            state = SEE.nextState(state, 5);
                            continue;
                        }
                        state = SEE.nextState(state, 3);
                        continue;
                    }
                    if ((officerMove & queens) != 0L) {
                        state = SEE.nextState(state, 5);
                        queenAttack = true;
                        continue;
                    }
                    if ((officerMove & free) == 0L && (colour != 0 || j != 0 || (pawns & officerMove) == 0L)) break;
                }
            }
            if ((officer_sliders & (dir = OfficerPlies.ALL_OFFICER_DIR3_MOVES[toFieldID]) & potentialOfficersPlaces) != 0L) {
                queenAttack = false;
                dirMoves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[toFieldID][3];
                for (j = 0; j < dirMoves.length; ++j) {
                    officerMove = dirMoves[j];
                    if ((officerMove & officers) != 0L) {
                        if (queenAttack) {
                            state = SEE.nextState(state, 5);
                            continue;
                        }
                        state = SEE.nextState(state, 3);
                        continue;
                    }
                    if ((officerMove & queens) != 0L) {
                        state = SEE.nextState(state, 5);
                        queenAttack = true;
                        continue;
                    }
                    if ((officerMove & free) == 0L && (colour != 1 || j != 0 || (pawns & officerMove) == 0L)) break;
                }
            }
        }
        if (stop && state > 0) {
            return state;
        }
        long potentialCastlesPlaces = CastlePlies.ALL_CASTLE_MOVES[toFieldID];
        long castles = this.bitboard.getFiguresBitboardByColourAndType(colour, 4);
        long castle_sliders = castles | queens;
        if ((castle_sliders & potentialCastlesPlaces) != 0L) {
            long castleMove;
            dir = CastlePlies.ALL_CASTLE_DIR0_MOVES[toFieldID];
            if ((castle_sliders & dir & potentialCastlesPlaces) != 0L) {
                queenAttack = false;
                dirMoves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[toFieldID][0];
                for (j = 0; j < dirMoves.length; ++j) {
                    castleMove = dirMoves[j];
                    if ((castleMove & castles) != 0L) {
                        if (queenAttack) {
                            state = SEE.nextState(state, 5);
                            continue;
                        }
                        state = SEE.nextState(state, 4);
                        continue;
                    }
                    if ((castleMove & queens) != 0L) {
                        state = SEE.nextState(state, 5);
                        queenAttack = true;
                        continue;
                    }
                    if ((castleMove & free) == 0L) break;
                }
            }
            if ((castle_sliders & (dir = CastlePlies.ALL_CASTLE_DIR1_MOVES[toFieldID]) & potentialCastlesPlaces) != 0L) {
                queenAttack = false;
                dirMoves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[toFieldID][1];
                for (j = 0; j < dirMoves.length; ++j) {
                    castleMove = dirMoves[j];
                    if ((castleMove & castles) != 0L) {
                        if (queenAttack) {
                            state = SEE.nextState(state, 5);
                            continue;
                        }
                        state = SEE.nextState(state, 4);
                        continue;
                    }
                    if ((castleMove & queens) != 0L) {
                        state = SEE.nextState(state, 5);
                        queenAttack = true;
                        continue;
                    }
                    if ((castleMove & free) == 0L) break;
                }
            }
            if ((castle_sliders & (dir = CastlePlies.ALL_CASTLE_DIR2_MOVES[toFieldID]) & potentialCastlesPlaces) != 0L) {
                queenAttack = false;
                dirMoves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[toFieldID][2];
                for (j = 0; j < dirMoves.length; ++j) {
                    castleMove = dirMoves[j];
                    if ((castleMove & castles) != 0L) {
                        if (queenAttack) {
                            state = SEE.nextState(state, 5);
                            continue;
                        }
                        state = SEE.nextState(state, 4);
                        continue;
                    }
                    if ((castleMove & queens) != 0L) {
                        state = SEE.nextState(state, 5);
                        queenAttack = true;
                        continue;
                    }
                    if ((castleMove & free) == 0L) break;
                }
            }
            if ((castle_sliders & (dir = CastlePlies.ALL_CASTLE_DIR3_MOVES[toFieldID]) & potentialCastlesPlaces) != 0L) {
                queenAttack = false;
                dirMoves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[toFieldID][3];
                for (j = 0; j < dirMoves.length; ++j) {
                    castleMove = dirMoves[j];
                    if ((castleMove & castles) != 0L) {
                        if (queenAttack) {
                            state = SEE.nextState(state, 5);
                            continue;
                        }
                        state = SEE.nextState(state, 4);
                        continue;
                    }
                    if ((castleMove & queens) != 0L) {
                        state = SEE.nextState(state, 5);
                        queenAttack = true;
                        continue;
                    }
                    if ((castleMove & free) == 0L) break;
                }
            }
        }
        if (stop && state > 0) {
            return state;
        }
        long potentialKingPlaces = KingPlies.ALL_KING_MOVES[toFieldID];
        long king = this.bitboard.getFiguresBitboardByColourAndType(colour, 6);
        if ((king & potentialKingPlaces) != 0L) {
            state = SEE.nextState(state, 6);
        }
        return state;
    }

    private void buildAttacksList(int toFieldID, long toFieldBitboard, int[] states) {
        long b_castles;
        long b_castle_sliders;
        int j;
        long[] dirMoves;
        boolean queenAttack;
        long b_officers;
        long b_officer_sliders;
        long[] dirMoves2;
        long dir;
        long b_king;
        long b_knights;
        long free = this.bitboard.getFreeBitboard();
        long potentialKnightsPlaces = KnightPlies.ALL_KNIGHT_MOVES[toFieldID];
        long w_knights = this.bitboard.getFiguresBitboardByColourAndType(0, 2);
        if ((w_knights & potentialKnightsPlaces) != 0L) {
            states[0] = SEE.nextState(states[0], 2, Utils.countBits(w_knights & potentialKnightsPlaces));
        }
        if (((b_knights = this.bitboard.getFiguresBitboardByColourAndType(1, 2)) & potentialKnightsPlaces) != 0L) {
            states[1] = SEE.nextState(states[1], 2, Utils.countBits(b_knights & potentialKnightsPlaces));
        }
        long potentialKingPlaces = KingPlies.ALL_KING_MOVES[toFieldID];
        long w_king = this.bitboard.getFiguresBitboardByColourAndType(0, 6);
        if ((w_king & potentialKingPlaces) != 0L) {
            states[0] = SEE.nextState(states[0], 6);
        }
        if (((b_king = this.bitboard.getFiguresBitboardByColourAndType(1, 6)) & potentialKingPlaces) != 0L) {
            states[1] = SEE.nextState(states[1], 6);
        }
        long w_pawns = this.bitboard.getFiguresBitboardByColourAndType(0, 1);
        long potentialWPawnsPlaces = 0L;
        if ((0x8080808080808080L & toFieldBitboard) == 0L) {
            potentialWPawnsPlaces |= toFieldBitboard << 9;
        }
        if ((0x101010101010101L & toFieldBitboard) == 0L) {
            potentialWPawnsPlaces |= toFieldBitboard << 7;
        }
        if ((w_pawns & potentialWPawnsPlaces) != 0L) {
            states[0] = SEE.nextState(states[0], 1, Utils.countBits(w_pawns & potentialWPawnsPlaces));
        }
        long b_pawns = this.bitboard.getFiguresBitboardByColourAndType(1, 1);
        long potentialBPawnsPlaces = 0L;
        if ((0x8080808080808080L & toFieldBitboard) == 0L) {
            potentialBPawnsPlaces |= toFieldBitboard >>> 7;
        }
        if ((0x101010101010101L & toFieldBitboard) == 0L) {
            potentialBPawnsPlaces |= toFieldBitboard >>> 9;
        }
        if ((b_pawns & potentialBPawnsPlaces) != 0L) {
            states[1] = SEE.nextState(states[1], 1, Utils.countBits(b_pawns & potentialBPawnsPlaces));
        }
        long w_queens = this.bitboard.getFiguresBitboardByColourAndType(0, 5);
        long b_queens = this.bitboard.getFiguresBitboardByColourAndType(1, 5);
        long potentialOfficersPlaces = OfficerPlies.ALL_OFFICER_MOVES[toFieldID];
        long w_officers = this.bitboard.getFiguresBitboardByColourAndType(0, 3);
        long w_officer_sliders = w_officers | w_queens;
        if ((w_officer_sliders & potentialOfficersPlaces) != 0L) {
            long officerMove;
            int j2;
            boolean queenAttack2;
            dir = OfficerPlies.ALL_OFFICER_DIR0_MOVES[toFieldID];
            if ((w_officer_sliders & dir & potentialOfficersPlaces) != 0L) {
                queenAttack2 = false;
                dirMoves2 = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[toFieldID][0];
                for (j2 = 0; j2 < dirMoves2.length; ++j2) {
                    officerMove = dirMoves2[j2];
                    if ((officerMove & w_officers) != 0L) {
                        if (queenAttack2) {
                            states[0] = SEE.nextState(states[0], 5);
                            continue;
                        }
                        states[0] = SEE.nextState(states[0], 3);
                        continue;
                    }
                    if ((officerMove & w_queens) != 0L) {
                        states[0] = SEE.nextState(states[0], 5);
                        queenAttack2 = true;
                        continue;
                    }
                    if ((officerMove & free) == 0L) break;
                }
            }
            if ((w_officer_sliders & (dir = OfficerPlies.ALL_OFFICER_DIR1_MOVES[toFieldID]) & potentialOfficersPlaces) != 0L) {
                queenAttack2 = false;
                dirMoves2 = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[toFieldID][1];
                for (j2 = 0; j2 < dirMoves2.length; ++j2) {
                    officerMove = dirMoves2[j2];
                    if ((officerMove & w_officers) != 0L) {
                        if (queenAttack2) {
                            states[0] = SEE.nextState(states[0], 5);
                            continue;
                        }
                        states[0] = SEE.nextState(states[0], 3);
                        continue;
                    }
                    if ((officerMove & w_queens) != 0L) {
                        states[0] = SEE.nextState(states[0], 5);
                        queenAttack2 = true;
                        continue;
                    }
                    if ((officerMove & free) == 0L && (j2 != 0 || (w_pawns & officerMove) == 0L)) break;
                }
            }
            if ((w_officer_sliders & (dir = OfficerPlies.ALL_OFFICER_DIR2_MOVES[toFieldID]) & potentialOfficersPlaces) != 0L) {
                queenAttack2 = false;
                dirMoves2 = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[toFieldID][2];
                for (j2 = 0; j2 < dirMoves2.length; ++j2) {
                    officerMove = dirMoves2[j2];
                    if ((officerMove & w_officers) != 0L) {
                        if (queenAttack2) {
                            states[0] = SEE.nextState(states[0], 5);
                            continue;
                        }
                        states[0] = SEE.nextState(states[0], 3);
                        continue;
                    }
                    if ((officerMove & w_queens) != 0L) {
                        states[0] = SEE.nextState(states[0], 5);
                        queenAttack2 = true;
                        continue;
                    }
                    if ((officerMove & free) == 0L && (j2 != 0 || (w_pawns & officerMove) == 0L)) break;
                }
            }
            if ((w_officer_sliders & (dir = OfficerPlies.ALL_OFFICER_DIR3_MOVES[toFieldID]) & potentialOfficersPlaces) != 0L) {
                queenAttack2 = false;
                dirMoves2 = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[toFieldID][3];
                for (j2 = 0; j2 < dirMoves2.length; ++j2) {
                    officerMove = dirMoves2[j2];
                    if ((officerMove & w_officers) != 0L) {
                        if (queenAttack2) {
                            states[0] = SEE.nextState(states[0], 5);
                            continue;
                        }
                        states[0] = SEE.nextState(states[0], 3);
                        continue;
                    }
                    if ((officerMove & w_queens) != 0L) {
                        states[0] = SEE.nextState(states[0], 5);
                        queenAttack2 = true;
                        continue;
                    }
                    if ((officerMove & free) == 0L) break;
                }
            }
        }
        if (((b_officer_sliders = (b_officers = this.bitboard.getFiguresBitboardByColourAndType(1, 3)) | b_queens) & potentialOfficersPlaces) != 0L) {
            long officerMove;
            long dir2 = OfficerPlies.ALL_OFFICER_DIR0_MOVES[toFieldID];
            if ((b_officer_sliders & dir2 & potentialOfficersPlaces) != 0L) {
                queenAttack = false;
                dirMoves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[toFieldID][0];
                for (j = 0; j < dirMoves.length; ++j) {
                    officerMove = dirMoves[j];
                    if ((officerMove & b_officers) != 0L) {
                        if (queenAttack) {
                            states[1] = SEE.nextState(states[1], 5);
                            continue;
                        }
                        states[1] = SEE.nextState(states[1], 3);
                        continue;
                    }
                    if ((officerMove & b_queens) != 0L) {
                        states[1] = SEE.nextState(states[1], 5);
                        queenAttack = true;
                        continue;
                    }
                    if ((officerMove & free) == 0L && (j != 0 || (b_pawns & officerMove) == 0L)) break;
                }
            }
            if ((b_officer_sliders & (dir2 = OfficerPlies.ALL_OFFICER_DIR1_MOVES[toFieldID]) & potentialOfficersPlaces) != 0L) {
                queenAttack = false;
                dirMoves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[toFieldID][1];
                for (j = 0; j < dirMoves.length; ++j) {
                    officerMove = dirMoves[j];
                    if ((officerMove & b_officers) != 0L) {
                        if (queenAttack) {
                            states[1] = SEE.nextState(states[1], 5);
                            continue;
                        }
                        states[1] = SEE.nextState(states[1], 3);
                        continue;
                    }
                    if ((officerMove & b_queens) != 0L) {
                        states[1] = SEE.nextState(states[1], 5);
                        queenAttack = true;
                        continue;
                    }
                    if ((officerMove & free) == 0L) break;
                }
            }
            if ((b_officer_sliders & (dir2 = OfficerPlies.ALL_OFFICER_DIR2_MOVES[toFieldID]) & potentialOfficersPlaces) != 0L) {
                queenAttack = false;
                dirMoves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[toFieldID][2];
                for (j = 0; j < dirMoves.length; ++j) {
                    officerMove = dirMoves[j];
                    if ((officerMove & b_officers) != 0L) {
                        if (queenAttack) {
                            states[1] = SEE.nextState(states[1], 5);
                            continue;
                        }
                        states[1] = SEE.nextState(states[1], 3);
                        continue;
                    }
                    if ((officerMove & b_queens) != 0L) {
                        states[1] = SEE.nextState(states[1], 5);
                        queenAttack = true;
                        continue;
                    }
                    if ((officerMove & free) == 0L) break;
                }
            }
            if ((b_officer_sliders & (dir2 = OfficerPlies.ALL_OFFICER_DIR3_MOVES[toFieldID]) & potentialOfficersPlaces) != 0L) {
                queenAttack = false;
                dirMoves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[toFieldID][3];
                for (j = 0; j < dirMoves.length; ++j) {
                    officerMove = dirMoves[j];
                    if ((officerMove & b_officers) != 0L) {
                        if (queenAttack) {
                            states[1] = SEE.nextState(states[1], 5);
                            continue;
                        }
                        states[1] = SEE.nextState(states[1], 3);
                        continue;
                    }
                    if ((officerMove & b_queens) != 0L) {
                        states[1] = SEE.nextState(states[1], 5);
                        queenAttack = true;
                        continue;
                    }
                    if ((officerMove & free) == 0L && (j != 0 || (b_pawns & officerMove) == 0L)) break;
                }
            }
        }
        long potentialCastlesPlaces = CastlePlies.ALL_CASTLE_MOVES[toFieldID];
        long w_castles = this.bitboard.getFiguresBitboardByColourAndType(0, 4);
        long w_castle_sliders = w_castles | w_queens;
        if ((w_castle_sliders & potentialCastlesPlaces) != 0L) {
            long castleMove;
            dir = CastlePlies.ALL_CASTLE_DIR0_MOVES[toFieldID];
            if ((w_castle_sliders & dir & potentialCastlesPlaces) != 0L) {
                boolean queenAttack3 = false;
                dirMoves2 = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[toFieldID][0];
                for (int j3 = 0; j3 < dirMoves2.length; ++j3) {
                    castleMove = dirMoves2[j3];
                    if ((castleMove & w_castles) != 0L) {
                        if (queenAttack3) {
                            states[0] = SEE.nextState(states[0], 5);
                            continue;
                        }
                        states[0] = SEE.nextState(states[0], 4);
                        continue;
                    }
                    if ((castleMove & w_queens) != 0L) {
                        states[0] = SEE.nextState(states[0], 5);
                        queenAttack3 = true;
                        continue;
                    }
                    if ((castleMove & free) == 0L) break;
                }
            }
            if ((w_castle_sliders & (dir = CastlePlies.ALL_CASTLE_DIR1_MOVES[toFieldID]) & potentialCastlesPlaces) != 0L) {
                boolean queenAttack4 = false;
                dirMoves2 = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[toFieldID][1];
                for (int j4 = 0; j4 < dirMoves2.length; ++j4) {
                    castleMove = dirMoves2[j4];
                    if ((castleMove & w_castles) != 0L) {
                        if (queenAttack4) {
                            states[0] = SEE.nextState(states[0], 5);
                            continue;
                        }
                        states[0] = SEE.nextState(states[0], 4);
                        continue;
                    }
                    if ((castleMove & w_queens) != 0L) {
                        states[0] = SEE.nextState(states[0], 5);
                        queenAttack4 = true;
                        continue;
                    }
                    if ((castleMove & free) == 0L) break;
                }
            }
            if ((w_castle_sliders & (dir = CastlePlies.ALL_CASTLE_DIR2_MOVES[toFieldID]) & potentialCastlesPlaces) != 0L) {
                boolean queenAttack5 = false;
                dirMoves2 = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[toFieldID][2];
                for (int j5 = 0; j5 < dirMoves2.length; ++j5) {
                    castleMove = dirMoves2[j5];
                    if ((castleMove & w_castles) != 0L) {
                        if (queenAttack5) {
                            states[0] = SEE.nextState(states[0], 5);
                            continue;
                        }
                        states[0] = SEE.nextState(states[0], 4);
                        continue;
                    }
                    if ((castleMove & w_queens) != 0L) {
                        states[0] = SEE.nextState(states[0], 5);
                        queenAttack5 = true;
                        continue;
                    }
                    if ((castleMove & free) == 0L) break;
                }
            }
            if ((w_castle_sliders & (dir = CastlePlies.ALL_CASTLE_DIR3_MOVES[toFieldID]) & potentialCastlesPlaces) != 0L) {
                boolean queenAttack6 = false;
                dirMoves2 = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[toFieldID][3];
                for (int j6 = 0; j6 < dirMoves2.length; ++j6) {
                    castleMove = dirMoves2[j6];
                    if ((castleMove & w_castles) != 0L) {
                        if (queenAttack6) {
                            states[0] = SEE.nextState(states[0], 5);
                            continue;
                        }
                        states[0] = SEE.nextState(states[0], 4);
                        continue;
                    }
                    if ((castleMove & w_queens) != 0L) {
                        states[0] = SEE.nextState(states[0], 5);
                        queenAttack6 = true;
                        continue;
                    }
                    if ((castleMove & free) == 0L) break;
                }
            }
        }
        if (((b_castle_sliders = (b_castles = this.bitboard.getFiguresBitboardByColourAndType(1, 4)) | b_queens) & potentialCastlesPlaces) != 0L) {
            long castleMove;
            long dir3 = CastlePlies.ALL_CASTLE_DIR0_MOVES[toFieldID];
            if ((b_castle_sliders & dir3 & potentialCastlesPlaces) != 0L) {
                queenAttack = false;
                dirMoves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[toFieldID][0];
                for (j = 0; j < dirMoves.length; ++j) {
                    castleMove = dirMoves[j];
                    if ((castleMove & b_castles) != 0L) {
                        if (queenAttack) {
                            states[1] = SEE.nextState(states[1], 5);
                            continue;
                        }
                        states[1] = SEE.nextState(states[1], 4);
                        continue;
                    }
                    if ((castleMove & b_queens) != 0L) {
                        states[1] = SEE.nextState(states[1], 5);
                        queenAttack = true;
                        continue;
                    }
                    if ((castleMove & free) == 0L) break;
                }
            }
            if ((b_castle_sliders & (dir3 = CastlePlies.ALL_CASTLE_DIR1_MOVES[toFieldID]) & potentialCastlesPlaces) != 0L) {
                queenAttack = false;
                dirMoves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[toFieldID][1];
                for (j = 0; j < dirMoves.length; ++j) {
                    castleMove = dirMoves[j];
                    if ((castleMove & b_castles) != 0L) {
                        if (queenAttack) {
                            states[1] = SEE.nextState(states[1], 5);
                            continue;
                        }
                        states[1] = SEE.nextState(states[1], 4);
                        continue;
                    }
                    if ((castleMove & b_queens) != 0L) {
                        states[1] = SEE.nextState(states[1], 5);
                        queenAttack = true;
                        continue;
                    }
                    if ((castleMove & free) == 0L) break;
                }
            }
            if ((b_castle_sliders & (dir3 = CastlePlies.ALL_CASTLE_DIR2_MOVES[toFieldID]) & potentialCastlesPlaces) != 0L) {
                queenAttack = false;
                dirMoves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[toFieldID][2];
                for (j = 0; j < dirMoves.length; ++j) {
                    castleMove = dirMoves[j];
                    if ((castleMove & b_castles) != 0L) {
                        if (queenAttack) {
                            states[1] = SEE.nextState(states[1], 5);
                            continue;
                        }
                        states[1] = SEE.nextState(states[1], 4);
                        continue;
                    }
                    if ((castleMove & b_queens) != 0L) {
                        states[1] = SEE.nextState(states[1], 5);
                        queenAttack = true;
                        continue;
                    }
                    if ((castleMove & free) == 0L) break;
                }
            }
            if ((b_castle_sliders & (dir3 = CastlePlies.ALL_CASTLE_DIR3_MOVES[toFieldID]) & potentialCastlesPlaces) != 0L) {
                queenAttack = false;
                dirMoves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[toFieldID][3];
                for (j = 0; j < dirMoves.length; ++j) {
                    castleMove = dirMoves[j];
                    if ((castleMove & b_castles) != 0L) {
                        if (queenAttack) {
                            states[1] = SEE.nextState(states[1], 5);
                            continue;
                        }
                        states[1] = SEE.nextState(states[1], 4);
                        continue;
                    }
                    if ((castleMove & b_queens) != 0L) {
                        states[1] = SEE.nextState(states[1], 5);
                        queenAttack = true;
                        continue;
                    }
                    if ((castleMove & free) == 0L) break;
                }
            }
        }
    }

    private int buildAttacksList(long toFieldBitboard, IPlayerAttacks attacks, int[] attacksList, int startIndex, int skipOneAttackOfType) {
        int count = startIndex;
        if ((attacks.allAttacks() & toFieldBitboard) != 0L) {
            int curType = 1;
            while (curType != 7) {
                long curTypeAttacks = attacks.attacksByType(curType);
                if ((curTypeAttacks & toFieldBitboard) != 0L) {
                    int size = attacks.attacksByTypeUnintersectedSize(curType);
                    long[] bitboard = attacks.attacksByTypeUnintersected(curType);
                    for (int i = 0; i < size; ++i) {
                        if ((bitboard[i] & toFieldBitboard) == 0L) continue;
                        if (skipOneAttackOfType == curType) {
                            skipOneAttackOfType = 0;
                            continue;
                        }
                        attacksList[count] = this.getCost(curType);
                        ++count;
                    }
                }
                curType = Figures.nextType(curType);
            }
        }
        return count;
    }

    public final int getCost(int figType) {
        return BaseEvalWeights.getFigureMaterialSEE(figType);
    }
}

