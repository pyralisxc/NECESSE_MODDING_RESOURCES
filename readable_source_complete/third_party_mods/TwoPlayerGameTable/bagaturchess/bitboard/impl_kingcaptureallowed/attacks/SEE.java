/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl_kingcaptureallowed.attacks;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.ISEE;
import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.Constants;
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
    private static final boolean STOP_AT_QUEEN_ATTACK = false;
    private IBitBoard bitboard;
    private boolean[] canBeCaptured = new boolean[1];
    private int[] buff = new int[3];

    public SEE(IBitBoard _bitboard) {
        this.bitboard = _bitboard;
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
        int[] matrix = this.bitboard.getMatrix();
        c_state = colour == 0 ? SEE.getFieldState(toFieldID, 0, matrix) : SEE.getFieldState(toFieldID, 1, matrix);
        oc_state = oppColour == 0 ? SEE.getFieldState(toFieldID, 0, matrix) : SEE.getFieldState(toFieldID, 1, matrix);
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
        this.buff[0] = 0;
        this.buff[1] = 0;
        this.buildAttacksList(fieldID, fieldBoard, this.buff);
        w_state = this.buff[0];
        b_state = this.buff[1];
        int result = -SeeMetadata.getSingleton().seeMove(0, figureType, figureColour == 0 ? w_state : b_state, figureColour == 0 ? b_state : w_state);
        return result;
    }

    @Override
    public int seeMove(int pieceColour, int pieceType, int toFiledID) {
        int w_state = 0;
        int b_state = 0;
        this.buff[0] = 0;
        this.buff[1] = 0;
        long fieldBoard = Fields.ALL_A1H1[toFiledID];
        this.buildAttacksList(toFiledID, fieldBoard, this.buff);
        w_state = this.buff[0];
        b_state = this.buff[1];
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

    public static final int getFieldState(int fieldID, int attackingColour, int[] board) {
        int expectedPID;
        int targetPID;
        int expectedPID2;
        int targetPID2;
        int toFieldID;
        int seq;
        int[] dirIDs;
        int state = 0;
        int[] validDirIDs = CastlePlies.ALL_CASTLE_VALID_DIRS[fieldID];
        int[][] dirs_ids = CastlePlies.ALL_CASTLE_DIRS_WITH_FIELD_IDS[fieldID];
        block4: for (int dirID : validDirIDs) {
            dirIDs = dirs_ids[dirID];
            for (seq = 0; seq < dirIDs.length; ++seq) {
                toFieldID = dirIDs[seq];
                targetPID2 = board[toFieldID];
                if (targetPID2 == 0) continue;
                int n = expectedPID2 = attackingColour == 0 ? 5 : 11;
                if (targetPID2 == expectedPID2) {
                    state = SEE.nextState(state, 5);
                }
                int n2 = expectedPID2 = attackingColour == 0 ? 4 : 10;
                if (targetPID2 != expectedPID2) continue block4;
                state = SEE.nextState(state, 4);
                continue block4;
            }
        }
        validDirIDs = OfficerPlies.ALL_OFFICER_VALID_DIRS[fieldID];
        dirs_ids = OfficerPlies.ALL_OFFICER_DIRS_WITH_FIELD_IDS[fieldID];
        block6: for (int dirID : validDirIDs) {
            dirIDs = dirs_ids[dirID];
            for (seq = 0; seq < dirIDs.length; ++seq) {
                toFieldID = dirIDs[seq];
                targetPID2 = board[toFieldID];
                if (targetPID2 == 0) continue;
                int n = expectedPID2 = attackingColour == 0 ? 5 : 11;
                if (targetPID2 == expectedPID2) {
                    state = SEE.nextState(state, 5);
                }
                int n3 = expectedPID2 = attackingColour == 0 ? 3 : 9;
                if (targetPID2 != expectedPID2) continue block6;
                state = SEE.nextState(state, 3);
                continue block6;
            }
        }
        validDirIDs = KnightPlies.ALL_KNIGHT_VALID_DIRS[fieldID];
        dirs_ids = KnightPlies.ALL_KNIGHT_DIRS_WITH_FIELD_IDS[fieldID];
        for (int dirID : validDirIDs) {
            int toFieldID2 = dirs_ids[dirID][0];
            targetPID = board[toFieldID2];
            if (targetPID == 0) continue;
            int n = expectedPID = attackingColour == 0 ? 2 : 8;
            if (targetPID != expectedPID) continue;
            state = SEE.nextState(state, 2);
        }
        validDirIDs = KingPlies.ALL_KING_VALID_DIRS[fieldID];
        dirs_ids = KingPlies.ALL_KING_DIRS_WITH_FIELD_IDS[fieldID];
        for (int dirID : validDirIDs) {
            int toFieldID3 = dirs_ids[dirID][0];
            targetPID = board[toFieldID3];
            if (targetPID == 0) continue;
            int n = expectedPID = attackingColour == 0 ? 6 : 12;
            if (targetPID != expectedPID) continue;
            state = SEE.nextState(state, 6);
        }
        switch (attackingColour) {
            case 0: {
                int targetPID3;
                int targetPID4;
                int targetFieldID;
                int letter = Fields.LETTERS[fieldID];
                if (letter != 0 && (targetFieldID = fieldID - 9) >= 0 && (targetPID4 = board[targetFieldID]) == 1) {
                    state = SEE.nextState(state, 1);
                }
                if (letter == 7 || (targetFieldID = fieldID - 7) < 0 || (targetPID3 = board[targetFieldID]) != 1) break;
                state = SEE.nextState(state, 1);
                break;
            }
            case 1: {
                int targetPID5;
                int targetPID6;
                int targetFieldID;
                int letter = Fields.LETTERS[fieldID];
                if (letter != 0 && (targetFieldID = fieldID + 7) <= 63 && (targetPID6 = board[targetFieldID]) == 7) {
                    state = SEE.nextState(state, 1);
                }
                if (letter == 7 || (targetFieldID = fieldID + 9) > 63 || (targetPID5 = board[targetFieldID]) != 7) break;
                state = SEE.nextState(state, 1);
                break;
            }
        }
        return state;
    }

    private int buildAttacksList(boolean stop, int colour, int toFieldID, long toFieldBitboard) {
        int fieldID;
        int j;
        int[] dirIDs;
        long[] dirMoves;
        boolean queenAttack;
        long dir;
        int state = 0;
        long pawns = 0L;
        if (colour == 0) {
            pawns = this.bitboard.getFiguresBitboardByPID(Constants.COLOUR_AND_TYPE_2_PIECE_IDENTITY[colour][1]);
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
            pawns = this.bitboard.getFiguresBitboardByPID(Constants.COLOUR_AND_TYPE_2_PIECE_IDENTITY[colour][1]);
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
        long knights = this.bitboard.getFiguresBitboardByPID(Constants.COLOUR_AND_TYPE_2_PIECE_IDENTITY[colour][2]);
        if ((knights & potentialKnightsPlaces) != 0L) {
            state = SEE.nextState(state, 2, Utils.countBits(knights & potentialKnightsPlaces));
        }
        if (stop && state > 0) {
            return state;
        }
        long queens = this.bitboard.getFiguresBitboardByPID(Constants.COLOUR_AND_TYPE_2_PIECE_IDENTITY[colour][5]);
        long potentialOfficersPlaces = OfficerPlies.ALL_OFFICER_MOVES[toFieldID];
        long officers = this.bitboard.getFiguresBitboardByPID(Constants.COLOUR_AND_TYPE_2_PIECE_IDENTITY[colour][3]);
        long officer_sliders = officers | queens;
        if ((officer_sliders & potentialOfficersPlaces) != 0L) {
            long officerMove;
            dir = OfficerPlies.ALL_OFFICER_DIR0_MOVES[toFieldID];
            if ((officer_sliders & dir & potentialOfficersPlaces) != 0L) {
                queenAttack = false;
                dirMoves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[toFieldID][0];
                dirIDs = OfficerPlies.ALL_OFFICER_DIRS_WITH_FIELD_IDS[toFieldID][0];
                for (j = 0; j < dirMoves.length; ++j) {
                    officerMove = dirMoves[j];
                    fieldID = dirIDs[j];
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
                    if (this.bitboard.getFigureID(fieldID) != 0 && (colour != 1 || j != 0 || (pawns & officerMove) == 0L)) break;
                }
            }
            if ((officer_sliders & (dir = OfficerPlies.ALL_OFFICER_DIR1_MOVES[toFieldID]) & potentialOfficersPlaces) != 0L) {
                queenAttack = false;
                dirMoves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[toFieldID][1];
                dirIDs = OfficerPlies.ALL_OFFICER_DIRS_WITH_FIELD_IDS[toFieldID][1];
                for (j = 0; j < dirMoves.length; ++j) {
                    officerMove = dirMoves[j];
                    fieldID = dirIDs[j];
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
                    if (this.bitboard.getFigureID(fieldID) != 0 && (colour != 0 || j != 0 || (pawns & officerMove) == 0L)) break;
                }
            }
            if ((officer_sliders & (dir = OfficerPlies.ALL_OFFICER_DIR2_MOVES[toFieldID]) & potentialOfficersPlaces) != 0L) {
                queenAttack = false;
                dirMoves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[toFieldID][2];
                dirIDs = OfficerPlies.ALL_OFFICER_DIRS_WITH_FIELD_IDS[toFieldID][2];
                for (j = 0; j < dirMoves.length; ++j) {
                    officerMove = dirMoves[j];
                    fieldID = dirIDs[j];
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
                    if (this.bitboard.getFigureID(fieldID) != 0 && (colour != 0 || j != 0 || (pawns & officerMove) == 0L)) break;
                }
            }
            if ((officer_sliders & (dir = OfficerPlies.ALL_OFFICER_DIR3_MOVES[toFieldID]) & potentialOfficersPlaces) != 0L) {
                queenAttack = false;
                dirMoves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[toFieldID][3];
                dirIDs = OfficerPlies.ALL_OFFICER_DIRS_WITH_FIELD_IDS[toFieldID][3];
                for (j = 0; j < dirMoves.length; ++j) {
                    officerMove = dirMoves[j];
                    fieldID = dirIDs[j];
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
                    if (this.bitboard.getFigureID(fieldID) != 0 && (colour != 1 || j != 0 || (pawns & officerMove) == 0L)) break;
                }
            }
        }
        if (stop && state > 0) {
            return state;
        }
        long potentialCastlesPlaces = CastlePlies.ALL_CASTLE_MOVES[toFieldID];
        long castles = this.bitboard.getFiguresBitboardByPID(Constants.COLOUR_AND_TYPE_2_PIECE_IDENTITY[colour][4]);
        long castle_sliders = castles | queens;
        if ((castle_sliders & potentialCastlesPlaces) != 0L) {
            long castleMove;
            dir = CastlePlies.ALL_CASTLE_DIR0_MOVES[toFieldID];
            if ((castle_sliders & dir & potentialCastlesPlaces) != 0L) {
                queenAttack = false;
                dirMoves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[toFieldID][0];
                dirIDs = CastlePlies.ALL_CASTLE_DIRS_WITH_FIELD_IDS[toFieldID][0];
                for (j = 0; j < dirMoves.length; ++j) {
                    castleMove = dirMoves[j];
                    fieldID = dirIDs[j];
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
                    if (this.bitboard.getFigureID(fieldID) != 0) break;
                }
            }
            if ((castle_sliders & (dir = CastlePlies.ALL_CASTLE_DIR1_MOVES[toFieldID]) & potentialCastlesPlaces) != 0L) {
                queenAttack = false;
                dirMoves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[toFieldID][1];
                dirIDs = CastlePlies.ALL_CASTLE_DIRS_WITH_FIELD_IDS[toFieldID][1];
                for (j = 0; j < dirMoves.length; ++j) {
                    castleMove = dirMoves[j];
                    fieldID = dirIDs[j];
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
                    if (this.bitboard.getFigureID(fieldID) != 0) break;
                }
            }
            if ((castle_sliders & (dir = CastlePlies.ALL_CASTLE_DIR2_MOVES[toFieldID]) & potentialCastlesPlaces) != 0L) {
                queenAttack = false;
                dirMoves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[toFieldID][2];
                dirIDs = CastlePlies.ALL_CASTLE_DIRS_WITH_FIELD_IDS[toFieldID][2];
                for (j = 0; j < dirMoves.length; ++j) {
                    castleMove = dirMoves[j];
                    fieldID = dirIDs[j];
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
                    if (this.bitboard.getFigureID(fieldID) != 0) break;
                }
            }
            if ((castle_sliders & (dir = CastlePlies.ALL_CASTLE_DIR3_MOVES[toFieldID]) & potentialCastlesPlaces) != 0L) {
                queenAttack = false;
                dirMoves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[toFieldID][3];
                dirIDs = CastlePlies.ALL_CASTLE_DIRS_WITH_FIELD_IDS[toFieldID][3];
                for (j = 0; j < dirMoves.length; ++j) {
                    castleMove = dirMoves[j];
                    fieldID = dirIDs[j];
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
                    if (this.bitboard.getFigureID(fieldID) != 0) break;
                }
            }
        }
        if (stop && state > 0) {
            return state;
        }
        long potentialKingPlaces = KingPlies.ALL_KING_MOVES[toFieldID];
        long king = this.bitboard.getFiguresBitboardByPID(Constants.COLOUR_AND_TYPE_2_PIECE_IDENTITY[colour][6]);
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
        long w_knights = this.bitboard.getFiguresBitboardByPID(2);
        if ((w_knights & potentialKnightsPlaces) != 0L) {
            states[0] = SEE.nextState(states[0], 2, Utils.countBits(w_knights & potentialKnightsPlaces));
        }
        if (((b_knights = this.bitboard.getFiguresBitboardByPID(8)) & potentialKnightsPlaces) != 0L) {
            states[1] = SEE.nextState(states[1], 2, Utils.countBits(b_knights & potentialKnightsPlaces));
        }
        long potentialKingPlaces = KingPlies.ALL_KING_MOVES[toFieldID];
        long w_king = this.bitboard.getFiguresBitboardByPID(6);
        if ((w_king & potentialKingPlaces) != 0L) {
            states[0] = SEE.nextState(states[0], 6);
        }
        if (((b_king = this.bitboard.getFiguresBitboardByPID(12)) & potentialKingPlaces) != 0L) {
            states[1] = SEE.nextState(states[1], 6);
        }
        long w_pawns = this.bitboard.getFiguresBitboardByPID(1);
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
        long b_pawns = this.bitboard.getFiguresBitboardByPID(7);
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
        long w_queens = this.bitboard.getFiguresBitboardByPID(5);
        long b_queens = this.bitboard.getFiguresBitboardByPID(11);
        long potentialOfficersPlaces = OfficerPlies.ALL_OFFICER_MOVES[toFieldID];
        long w_officers = this.bitboard.getFiguresBitboardByPID(3);
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
        if (((b_officer_sliders = (b_officers = this.bitboard.getFiguresBitboardByPID(9)) | b_queens) & potentialOfficersPlaces) != 0L) {
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
        long w_castles = this.bitboard.getFiguresBitboardByPID(4);
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
        if (((b_castle_sliders = (b_castles = this.bitboard.getFiguresBitboardByPID(10)) | b_queens) & potentialCastlesPlaces) != 0L) {
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

    public final int getCost(int figType) {
        return BaseEvalWeights.getFigureMaterialSEE(figType);
    }
}

