/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.plies.checking;

import bagaturchess.bitboard.api.IPiecesLists;
import bagaturchess.bitboard.impl.Board;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.plies.CastlePlies;
import bagaturchess.bitboard.impl.plies.KingPlies;
import bagaturchess.bitboard.impl.plies.KnightPlies;
import bagaturchess.bitboard.impl.plies.OfficerPlies;
import bagaturchess.bitboard.impl.plies.checking.Checker;
import bagaturchess.bitboard.impl.state.PiecesList;

public class CheckingCount
extends Fields {
    public static final int getChecksCount(Board board, int colour, int opponentColour, long kingBitboard, int kingFieldID, long free) {
        return CheckingCount.getFieldAttacksCount(null, board, opponentColour, colour, kingBitboard, kingFieldID, free);
    }

    public static final int getChecksCount(Checker buff, Board board, int colour, int opponentColour, long kingBitboard, int kingFieldID, long free) {
        return CheckingCount.getFieldAttacksCount(buff, board, opponentColour, colour, kingBitboard, kingFieldID, free);
    }

    public static final int getFieldAttacksCount(Checker buff, Board board, int attackingColour, int opponentColour, long fieldBitboard, int fieldID, long free) {
        long opponentCastles;
        long castleMoves;
        int result = 0;
        result += CheckingCount.getUncoveredChecksCount(buff, board, opponentColour, attackingColour, fieldBitboard, fieldID);
        IPiecesLists stateManager = board.getPiecesLists();
        long officerMoves = OfficerPlies.ALL_OFFICER_MOVES[fieldID];
        long opponentOfficers = board.allByColourAndType[attackingColour][3];
        if ((officerMoves & opponentOfficers) != 0L) {
            PiecesList oppOfficers = stateManager.getPieces(Figures.getPidByColourAndType(attackingColour, 3));
            result += CheckingCount.getOfficersChecksCount(buff, board, attackingColour, fieldID, free, officerMoves, oppOfficers, 3);
        }
        long opponentQueens = board.allByColourAndType[attackingColour][5];
        PiecesList oppQueens = stateManager.getPieces(Figures.getPidByColourAndType(attackingColour, 5));
        if ((officerMoves & opponentQueens) != 0L) {
            result += CheckingCount.getOfficersChecksCount(buff, board, attackingColour, fieldID, free, officerMoves, oppQueens, 5);
        }
        if (((castleMoves = CastlePlies.ALL_CASTLE_MOVES[fieldID]) & (opponentCastles = board.allByColourAndType[attackingColour][4])) != 0L) {
            PiecesList oppCastles = stateManager.getPieces(Figures.getPidByColourAndType(attackingColour, 4));
            result += CheckingCount.getCastlesChecksCount(buff, board, attackingColour, fieldID, free, castleMoves, oppCastles, 4);
        }
        if ((castleMoves & opponentQueens) != 0L) {
            result += CheckingCount.getCastlesChecksCount(buff, board, attackingColour, fieldID, free, castleMoves, oppQueens, 5);
        }
        return result;
    }

    public static final int getUncoveredChecksCount(Checker buff, Board board, int colour, int attackingColour, long kingBitboard, int kingFieldID) {
        long opponentPawns;
        long potentialKingAttacks;
        long opponentKing;
        long potentialKnightsAttacks;
        int result = 0;
        long opponentKnights = board.allByColourAndType[attackingColour][2];
        if (opponentKnights != 0L && (opponentKnights & (potentialKnightsAttacks = KnightPlies.ALL_KNIGHT_MOVES[kingFieldID])) != 0L) {
            ++result;
            if (buff != null) {
                PiecesList opponentKnightsIDs = board.getPiecesLists().getPieces(Figures.getPidByColourAndType(attackingColour, 2));
                int size = opponentKnightsIDs.getDataSize();
                int[] ids = opponentKnightsIDs.getData();
                for (int i = 0; i < size; ++i) {
                    int fieldID = ids[i];
                    long figureBoard = Fields.ALL_ORDERED_A1H1[fieldID];
                    if ((figureBoard & potentialKnightsAttacks) != 0L) {
                        buff.slider = false;
                        buff.figureType = 2;
                        buff.fieldID = fieldID;
                        buff.fieldBitboard = figureBoard;
                        buff.sliderAttackRayBitboard = 0L;
                        break;
                    }
                    if (i != size - 1) continue;
                }
            }
        }
        if ((opponentKing = board.allByColourAndType[attackingColour][6]) == 0L || (opponentKing & (potentialKingAttacks = KingPlies.ALL_KING_MOVES[kingFieldID])) != 0L) {
            // empty if block
        }
        if ((opponentPawns = board.allByColourAndType[attackingColour][1]) != 0L) {
            block0 : switch (colour) {
                case 0: {
                    long figureBoard;
                    int fieldID;
                    int i;
                    int[] ids;
                    int size;
                    PiecesList opponentPawnsIDs;
                    long nonactivePawns = opponentPawns & 0x8080808080808080L;
                    long activePawns = opponentPawns & (nonactivePawns ^ 0xFFFFFFFFFFFFFFFFL);
                    long attacks = activePawns << 9;
                    if ((kingBitboard & attacks) != 0L) {
                        ++result;
                        if (buff != null) {
                            opponentPawnsIDs = board.getPiecesLists().getPieces(Figures.getPidByColourAndType(attackingColour, 1));
                            size = opponentPawnsIDs.getDataSize();
                            ids = opponentPawnsIDs.getData();
                            for (i = 0; i < size; ++i) {
                                fieldID = ids[i];
                                figureBoard = Fields.ALL_ORDERED_A1H1[fieldID];
                                if ((figureBoard << 9 & kingBitboard) != 0L) {
                                    buff.slider = false;
                                    buff.figureType = 1;
                                    buff.fieldID = fieldID;
                                    buff.fieldBitboard = figureBoard;
                                    buff.sliderAttackRayBitboard = 0L;
                                    break;
                                }
                                if (i != size - 1) continue;
                            }
                        }
                    }
                    if ((kingBitboard & (attacks = (activePawns = opponentPawns & ((nonactivePawns = opponentPawns & 0x101010101010101L) ^ 0xFFFFFFFFFFFFFFFFL)) << 7)) == 0L) break;
                    ++result;
                    if (buff == null) break;
                    opponentPawnsIDs = board.getPiecesLists().getPieces(Figures.getPidByColourAndType(attackingColour, 1));
                    size = opponentPawnsIDs.getDataSize();
                    ids = opponentPawnsIDs.getData();
                    for (i = 0; i < size; ++i) {
                        fieldID = ids[i];
                        figureBoard = Fields.ALL_ORDERED_A1H1[fieldID];
                        if ((figureBoard << 7 & kingBitboard) != 0L) {
                            buff.slider = false;
                            buff.figureType = 1;
                            buff.fieldID = fieldID;
                            buff.fieldBitboard = figureBoard;
                            buff.sliderAttackRayBitboard = 0L;
                            break block0;
                        }
                        if (i != size - 1) continue;
                    }
                    break;
                }
                case 1: {
                    long figureBoard;
                    int fieldID;
                    int i;
                    int[] ids;
                    int size;
                    PiecesList opponentPawnsIDs;
                    long nonactivePawns = opponentPawns & 0x8080808080808080L;
                    long activePawns = opponentPawns & (nonactivePawns ^ 0xFFFFFFFFFFFFFFFFL);
                    long attacks = activePawns >> 7;
                    if ((kingBitboard & attacks) != 0L) {
                        ++result;
                        if (buff != null) {
                            opponentPawnsIDs = board.getPiecesLists().getPieces(Figures.getPidByColourAndType(attackingColour, 1));
                            size = opponentPawnsIDs.getDataSize();
                            ids = opponentPawnsIDs.getData();
                            for (i = 0; i < size; ++i) {
                                fieldID = ids[i];
                                figureBoard = Fields.ALL_ORDERED_A1H1[fieldID];
                                if ((figureBoard >> 7 & kingBitboard) != 0L) {
                                    buff.slider = false;
                                    buff.figureType = 1;
                                    buff.fieldID = fieldID;
                                    buff.fieldBitboard = figureBoard;
                                    buff.sliderAttackRayBitboard = 0L;
                                    break;
                                }
                                if (i != size - 1) continue;
                            }
                        }
                    }
                    if ((kingBitboard & (attacks = (activePawns = opponentPawns & ((nonactivePawns = opponentPawns & 0x101010101010101L) ^ 0xFFFFFFFFFFFFFFFFL)) >> 9)) == 0L) break;
                    ++result;
                    if (buff == null) break;
                    opponentPawnsIDs = board.getPiecesLists().getPieces(Figures.getPidByColourAndType(attackingColour, 1));
                    size = opponentPawnsIDs.getDataSize();
                    ids = opponentPawnsIDs.getData();
                    for (i = 0; i < size; ++i) {
                        fieldID = ids[i];
                        figureBoard = Fields.ALL_ORDERED_A1H1[fieldID];
                        if ((figureBoard >> 9 & kingBitboard) != 0L) {
                            buff.slider = false;
                            buff.figureType = 1;
                            buff.fieldID = fieldID;
                            buff.fieldBitboard = figureBoard;
                            buff.sliderAttackRayBitboard = 0L;
                            break block0;
                        }
                        if (i != size - 1) continue;
                    }
                    break;
                }
            }
        }
        return result;
    }

    private static int getOfficersChecksCount(Checker buff, Board board, int attackColour, int kingFieldID, long free, long officerMoves, PiecesList oppOfficers, int checkingFigureType) {
        int result = 0;
        int count = oppOfficers.getDataSize();
        int[] data = oppOfficers.getData();
        for (int i = 0; i < count; ++i) {
            int oppOfficerID = data[i];
            long oppOfficerBitboard = Fields.ALL_ORDERED_A1H1[oppOfficerID];
            if ((officerMoves & oppOfficerBitboard) == 0L) continue;
            result += CheckingCount.getOfficerDirsChecksCount(buff, board, attackColour, kingFieldID, free, oppOfficerBitboard, checkingFigureType);
        }
        return result;
    }

    private static int getCastlesChecksCount(Checker buff, Board board, int attackColour, int kingFieldID, long free, long castleMoves, PiecesList oppCastles, int checkingFigureType) {
        int result = 0;
        int count = oppCastles.getDataSize();
        int[] data = oppCastles.getData();
        for (int i = 0; i < count; ++i) {
            int oppCastleID = data[i];
            long oppCastleBitboard = Fields.ALL_ORDERED_A1H1[oppCastleID];
            if ((castleMoves & oppCastleBitboard) == 0L) continue;
            result += CheckingCount.getCastleDirsChecksCount(buff, board, attackColour, kingFieldID, free, oppCastleBitboard, checkingFigureType);
        }
        return result;
    }

    private static int getOfficerDirsChecksCount(Checker buff, Board board, int attackColour, int kingFieldID, long free, long oppOfficerBitboard, int checkingFigureType) {
        long[] dir3Moves;
        long dir3;
        long[] dir2Moves;
        long dir2;
        long[] dir1Moves;
        long dir1;
        long[] dir0Moves;
        int result = 0;
        long dir0 = OfficerPlies.ALL_OFFICER_DIR0_MOVES[kingFieldID];
        if ((dir0 & oppOfficerBitboard) != 0L && CheckingCount.checkSlidingDir(buff, board, attackColour, free, oppOfficerBitboard, dir0Moves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[kingFieldID][0], checkingFigureType)) {
            ++result;
        }
        if (((dir1 = OfficerPlies.ALL_OFFICER_DIR1_MOVES[kingFieldID]) & oppOfficerBitboard) != 0L && CheckingCount.checkSlidingDir(buff, board, attackColour, free, oppOfficerBitboard, dir1Moves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[kingFieldID][1], checkingFigureType)) {
            ++result;
        }
        if (((dir2 = OfficerPlies.ALL_OFFICER_DIR2_MOVES[kingFieldID]) & oppOfficerBitboard) != 0L && CheckingCount.checkSlidingDir(buff, board, attackColour, free, oppOfficerBitboard, dir2Moves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[kingFieldID][2], checkingFigureType)) {
            ++result;
        }
        if (((dir3 = OfficerPlies.ALL_OFFICER_DIR3_MOVES[kingFieldID]) & oppOfficerBitboard) != 0L && CheckingCount.checkSlidingDir(buff, board, attackColour, free, oppOfficerBitboard, dir3Moves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[kingFieldID][3], checkingFigureType)) {
            ++result;
        }
        return result;
    }

    private static int getCastleDirsChecksCount(Checker buff, Board board, int attackColour, int kingFieldID, long free, long oppCastleBitboard, int checkingFigureType) {
        long[] dir3Moves;
        long dir3;
        long[] dir2Moves;
        long dir2;
        long[] dir1Moves;
        long dir1;
        long[] dir0Moves;
        int result = 0;
        long dir0 = CastlePlies.ALL_CASTLE_DIR0_MOVES[kingFieldID];
        if ((dir0 & oppCastleBitboard) != 0L && CheckingCount.checkSlidingDir(buff, board, attackColour, free, oppCastleBitboard, dir0Moves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[kingFieldID][0], checkingFigureType)) {
            ++result;
        }
        if (((dir1 = CastlePlies.ALL_CASTLE_DIR1_MOVES[kingFieldID]) & oppCastleBitboard) != 0L && CheckingCount.checkSlidingDir(buff, board, attackColour, free, oppCastleBitboard, dir1Moves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[kingFieldID][1], checkingFigureType)) {
            ++result;
        }
        if (((dir2 = CastlePlies.ALL_CASTLE_DIR2_MOVES[kingFieldID]) & oppCastleBitboard) != 0L && CheckingCount.checkSlidingDir(buff, board, attackColour, free, oppCastleBitboard, dir2Moves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[kingFieldID][2], checkingFigureType)) {
            ++result;
        }
        if (((dir3 = CastlePlies.ALL_CASTLE_DIR3_MOVES[kingFieldID]) & oppCastleBitboard) != 0L && CheckingCount.checkSlidingDir(buff, board, attackColour, free, oppCastleBitboard, dir3Moves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[kingFieldID][3], checkingFigureType)) {
            ++result;
        }
        return result;
    }

    private static boolean checkSlidingDir(Checker buff, Board board, int attackingColour, long free, long oppAttackFigure, long[] dirMoves, int checkingFigureType) {
        long ray = 0L;
        for (int j = 0; j < dirMoves.length; ++j) {
            long officerMove = dirMoves[j];
            if ((officerMove & oppAttackFigure) != 0L) {
                if (buff != null) {
                    PiecesList opponentKnightsIDs = board.getPiecesLists().getPieces(Figures.getPidByColourAndType(attackingColour, checkingFigureType));
                    int size = opponentKnightsIDs.getDataSize();
                    int[] ids = opponentKnightsIDs.getData();
                    for (int i = 0; i < size; ++i) {
                        int fieldID = ids[i];
                        long figureBoard = Fields.ALL_ORDERED_A1H1[fieldID];
                        if ((figureBoard & officerMove) != 0L) {
                            buff.slider = true;
                            buff.figureType = checkingFigureType;
                            buff.fieldID = fieldID;
                            buff.fieldBitboard = figureBoard;
                            buff.sliderAttackRayBitboard = ray;
                            break;
                        }
                        if (i != size - 1) continue;
                        throw new IllegalStateException();
                    }
                }
                return true;
            }
            if ((officerMove & free) == 0L) break;
            ray |= officerMove;
        }
        return false;
    }
}

