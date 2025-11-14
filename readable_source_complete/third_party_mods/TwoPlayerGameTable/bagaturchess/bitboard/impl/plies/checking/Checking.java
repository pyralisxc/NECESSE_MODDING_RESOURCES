/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.plies.checking;

import bagaturchess.bitboard.api.IPiecesLists;
import bagaturchess.bitboard.impl.Board;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.bitboard.impl.plies.BlackPawnPlies;
import bagaturchess.bitboard.impl.plies.CastlePlies;
import bagaturchess.bitboard.impl.plies.KingPlies;
import bagaturchess.bitboard.impl.plies.KnightPlies;
import bagaturchess.bitboard.impl.plies.OfficerPlies;
import bagaturchess.bitboard.impl.plies.WhitePawnPlies;
import bagaturchess.bitboard.impl.plies.specials.Castling;
import bagaturchess.bitboard.impl.state.PiecesList;

public class Checking
extends Fields {
    public static final boolean isFieldAttacked(Board board, int attackingColour, int opponentColour, long fieldBitboard, int fieldID, long free, boolean kingAttacksPossible) {
        PiecesList oppCastles;
        PiecesList oppOfficers;
        if (Checking.isInUncoveredCheck(board, opponentColour, attackingColour, fieldBitboard, fieldID, kingAttacksPossible)) {
            return true;
        }
        IPiecesLists stateManager = board.getPiecesLists();
        long officerMoves = OfficerPlies.ALL_OFFICER_MOVES[fieldID];
        long opponentQueens = board.allByColourAndType[attackingColour][5];
        PiecesList oppQueens = stateManager.getPieces(Figures.getPidByColourAndType(attackingColour, 5));
        if ((officerMoves & opponentQueens) != 0L && Checking.checkOfficers(board, fieldID, free, officerMoves, oppQueens)) {
            return true;
        }
        long opponentOfficers = board.allByColourAndType[attackingColour][3];
        if ((officerMoves & opponentOfficers) != 0L && Checking.checkOfficers(board, fieldID, free, officerMoves, oppOfficers = stateManager.getPieces(Figures.getPidByColourAndType(attackingColour, 3)))) {
            return true;
        }
        long castleMoves = CastlePlies.ALL_CASTLE_MOVES[fieldID];
        if ((castleMoves & opponentQueens) != 0L && Checking.checkCastles(board, fieldID, free, castleMoves, oppQueens)) {
            return true;
        }
        long opponentCastles = board.allByColourAndType[attackingColour][4];
        return (castleMoves & opponentCastles) != 0L && Checking.checkCastles(board, fieldID, free, castleMoves, oppCastles = stateManager.getPieces(Figures.getPidByColourAndType(attackingColour, 4)));
    }

    public static final boolean isInCheck(Board board, int colour, int opponentColour, long kingBitboard, int kingFieldID, long free, boolean kingAttackPossible) {
        return Checking.isFieldAttacked(board, opponentColour, colour, kingBitboard, kingFieldID, free, kingAttackPossible);
    }

    public static final boolean isInUncoveredCheck(Board board, int colour, int opponentColour, long kingBitboard, int kingFieldID, boolean kingAttacksPossible) {
        long potentialKingAttacks;
        long potentialKnightsAttacks;
        long opponentKnights = board.allByColourAndType[opponentColour][2];
        if (opponentKnights != 0L && (opponentKnights & (potentialKnightsAttacks = KnightPlies.ALL_KNIGHT_MOVES[kingFieldID])) != 0L) {
            return true;
        }
        long opponentKing = board.allByColourAndType[opponentColour][6];
        if (opponentKing != 0L && (opponentKing & (potentialKingAttacks = KingPlies.ALL_KING_MOVES[kingFieldID])) != 0L) {
            if (!kingAttacksPossible) {
                throw new IllegalStateException("King attack");
            }
            return true;
        }
        long opponentPawns = board.allByColourAndType[opponentColour][1];
        if (opponentPawns != 0L) {
            switch (colour) {
                case 0: {
                    long nonactivePawns = opponentPawns & 0x8080808080808080L;
                    long activePawns = opponentPawns & (nonactivePawns ^ 0xFFFFFFFFFFFFFFFFL);
                    long attacks = activePawns << 9;
                    if ((kingBitboard & attacks) != 0L) {
                        return true;
                    }
                    nonactivePawns = opponentPawns & 0x101010101010101L;
                    activePawns = opponentPawns & (nonactivePawns ^ 0xFFFFFFFFFFFFFFFFL);
                    attacks = activePawns << 7;
                    if ((kingBitboard & attacks) == 0L) break;
                    return true;
                }
                case 1: {
                    long nonactivePawns = opponentPawns & 0x8080808080808080L;
                    long activePawns = opponentPawns & (nonactivePawns ^ 0xFFFFFFFFFFFFFFFFL);
                    long attacks = activePawns >> 7;
                    if ((kingBitboard & attacks) != 0L) {
                        return true;
                    }
                    nonactivePawns = opponentPawns & 0x101010101010101L;
                    activePawns = opponentPawns & (nonactivePawns ^ 0xFFFFFFFFFFFFFFFFL);
                    attacks = activePawns >> 9;
                    if ((kingBitboard & attacks) == 0L) break;
                    return true;
                }
            }
        }
        return false;
    }

    public static final boolean isAttackedFromPawns(Board board, int colour, int opponentColour, long kingBitboard, int kingFieldID) {
        long opponentPawns = board.allByColourAndType[opponentColour][1];
        if (opponentPawns != 0L) {
            switch (colour) {
                case 0: {
                    long nonactivePawns = opponentPawns & 0x8080808080808080L;
                    long activePawns = opponentPawns & (nonactivePawns ^ 0xFFFFFFFFFFFFFFFFL);
                    long attacks = activePawns << 9;
                    if ((kingBitboard & attacks) != 0L) {
                        return true;
                    }
                    nonactivePawns = opponentPawns & 0x101010101010101L;
                    activePawns = opponentPawns & (nonactivePawns ^ 0xFFFFFFFFFFFFFFFFL);
                    attacks = activePawns << 7;
                    if ((kingBitboard & attacks) == 0L) break;
                    return true;
                }
                case 1: {
                    long nonactivePawns = opponentPawns & 0x8080808080808080L;
                    long activePawns = opponentPawns & (nonactivePawns ^ 0xFFFFFFFFFFFFFFFFL);
                    long attacks = activePawns >> 7;
                    if ((kingBitboard & attacks) != 0L) {
                        return true;
                    }
                    nonactivePawns = opponentPawns & 0x101010101010101L;
                    activePawns = opponentPawns & (nonactivePawns ^ 0xFFFFFFFFFFFFFFFFL);
                    attacks = activePawns >> 9;
                    if ((kingBitboard & attacks) == 0L) break;
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean checkOfficers(Board board, int kingFieldID, long free, long officerMoves, PiecesList oppOfficers) {
        int count = oppOfficers.getDataSize();
        int[] data = oppOfficers.getData();
        for (int i = 0; i < count; ++i) {
            int oppOfficerID = data[i];
            long oppOfficerBitboard = Fields.ALL_ORDERED_A1H1[oppOfficerID];
            if ((officerMoves & oppOfficerBitboard) == 0L || !Checking.checkOfficerDirs(kingFieldID, free, oppOfficerBitboard)) continue;
            return true;
        }
        return false;
    }

    private static boolean checkCastles(Board board, int kingFieldID, long free, long castleMoves, PiecesList oppCastles) {
        int count = oppCastles.getDataSize();
        int[] data = oppCastles.getData();
        for (int i = 0; i < count; ++i) {
            int oppCastleID = data[i];
            long oppCastleBitboard = Fields.ALL_ORDERED_A1H1[oppCastleID];
            if ((castleMoves & oppCastleBitboard) == 0L || !Checking.checkCastleDirs(kingFieldID, free, oppCastleBitboard)) continue;
            return true;
        }
        return false;
    }

    private static boolean checkOfficerDirs(int kingFieldID, long free, long oppOfficerBitboard) {
        long[] dir3Moves;
        long[] dir2Moves;
        long[] dir1Moves;
        long[] dir0Moves;
        long dir0 = OfficerPlies.ALL_OFFICER_DIR0_MOVES[kingFieldID];
        if ((dir0 & oppOfficerBitboard) != 0L && Checking.checkSlidingDir(free, oppOfficerBitboard, dir0Moves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[kingFieldID][0])) {
            return true;
        }
        long dir1 = OfficerPlies.ALL_OFFICER_DIR1_MOVES[kingFieldID];
        if ((dir1 & oppOfficerBitboard) != 0L && Checking.checkSlidingDir(free, oppOfficerBitboard, dir1Moves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[kingFieldID][1])) {
            return true;
        }
        long dir2 = OfficerPlies.ALL_OFFICER_DIR2_MOVES[kingFieldID];
        if ((dir2 & oppOfficerBitboard) != 0L && Checking.checkSlidingDir(free, oppOfficerBitboard, dir2Moves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[kingFieldID][2])) {
            return true;
        }
        long dir3 = OfficerPlies.ALL_OFFICER_DIR3_MOVES[kingFieldID];
        return (dir3 & oppOfficerBitboard) != 0L && Checking.checkSlidingDir(free, oppOfficerBitboard, dir3Moves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[kingFieldID][3]);
    }

    private static boolean checkCastleDirs(int kingFieldID, long free, long oppCastleBitboard) {
        long[] dir3Moves;
        long[] dir2Moves;
        long[] dir1Moves;
        long[] dir0Moves;
        long dir0 = CastlePlies.ALL_CASTLE_DIR0_MOVES[kingFieldID];
        if ((dir0 & oppCastleBitboard) != 0L && Checking.checkSlidingDir(free, oppCastleBitboard, dir0Moves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[kingFieldID][0])) {
            return true;
        }
        long dir1 = CastlePlies.ALL_CASTLE_DIR1_MOVES[kingFieldID];
        if ((dir1 & oppCastleBitboard) != 0L && Checking.checkSlidingDir(free, oppCastleBitboard, dir1Moves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[kingFieldID][1])) {
            return true;
        }
        long dir2 = CastlePlies.ALL_CASTLE_DIR2_MOVES[kingFieldID];
        if ((dir2 & oppCastleBitboard) != 0L && Checking.checkSlidingDir(free, oppCastleBitboard, dir2Moves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[kingFieldID][2])) {
            return true;
        }
        long dir3 = CastlePlies.ALL_CASTLE_DIR3_MOVES[kingFieldID];
        return (dir3 & oppCastleBitboard) != 0L && Checking.checkSlidingDir(free, oppCastleBitboard, dir3Moves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[kingFieldID][3]);
    }

    private static boolean checkSlidingDir(long free, long oppAttackFigure, long[] dirMoves) {
        for (int j = 0; j < dirMoves.length; ++j) {
            long officerMove = dirMoves[j];
            if ((officerMove & oppAttackFigure) != 0L) {
                return true;
            }
            if ((officerMove & free) == 0L) break;
        }
        return false;
    }

    public static final boolean isInDoubleCheck(Board board, int colour) {
        boolean result = false;
        return result;
    }

    public static boolean isCheckMove(Board board, int move, int colour, int opponentColour, long free, long opponentKingBitboard, int opponentKingFieldID) {
        if (Checking.isDirectCheckMove(move, colour, free, opponentKingBitboard, opponentKingFieldID)) {
            return true;
        }
        return Checking.isHiddenCheckMove(board, move, colour, free, opponentKingBitboard, opponentKingFieldID);
    }

    public static boolean isDirectCheckMove(int move, int colour, long free, long opponentKingBitboard, int opponentKingFieldID) {
        int toFieldID = MoveInt.getToFieldID(move);
        int figureType = MoveInt.getFigureType(move);
        switch (figureType) {
            case 1: {
                if (!MoveInt.isPromotion(move)) {
                    long pawnMoves;
                    if (!(colour == 0 ? (opponentKingBitboard & (pawnMoves = WhitePawnPlies.ALL_WHITE_PAWN_ATTACKS_MOVES[toFieldID])) != 0L : (opponentKingBitboard & (pawnMoves = BlackPawnPlies.ALL_BLACK_PAWN_ATTACKS_MOVES[toFieldID])) != 0L)) break;
                    return true;
                }
                int promotionFigureType = MoveInt.getPromotionFigureType(move);
                long fromFieldBoard = Fields.ALL_ORDERED_A1H1[MoveInt.getFromFieldID(move)];
                switch (promotionFigureType) {
                    case 2: {
                        long knightMoves = KnightPlies.ALL_KNIGHT_MOVES[toFieldID];
                        if ((opponentKingBitboard & knightMoves) == 0L) break;
                        return true;
                    }
                    case 3: {
                        long officerMoves = OfficerPlies.ALL_OFFICER_MOVES[toFieldID];
                        if ((opponentKingBitboard & officerMoves) == 0L || !Checking.checkOfficerDirs(toFieldID, free | fromFieldBoard, opponentKingBitboard)) break;
                        return true;
                    }
                    case 4: {
                        long castleMoves = CastlePlies.ALL_CASTLE_MOVES[toFieldID];
                        if ((opponentKingBitboard & castleMoves) == 0L || !Checking.checkCastleDirs(toFieldID, free | fromFieldBoard, opponentKingBitboard)) break;
                        return true;
                    }
                    case 5: {
                        long officerMoves = OfficerPlies.ALL_OFFICER_MOVES[toFieldID];
                        if ((opponentKingBitboard & officerMoves) != 0L && Checking.checkOfficerDirs(toFieldID, free | fromFieldBoard, opponentKingBitboard)) {
                            return true;
                        }
                        long castleMoves = CastlePlies.ALL_CASTLE_MOVES[toFieldID];
                        if ((opponentKingBitboard & castleMoves) == 0L || !Checking.checkCastleDirs(toFieldID, free | fromFieldBoard, opponentKingBitboard)) break;
                        return true;
                    }
                }
                break;
            }
            case 2: {
                long knightMoves = KnightPlies.ALL_KNIGHT_MOVES[toFieldID];
                if ((opponentKingBitboard & knightMoves) == 0L) break;
                return true;
            }
            case 6: {
                int toCastleFieldID;
                long castleMoves;
                long kingMoves = KingPlies.ALL_KING_MOVES[toFieldID];
                if ((opponentKingBitboard & kingMoves) != 0L) {
                    // empty if block
                }
                if (!MoveInt.isCastling(move) || (opponentKingBitboard & (castleMoves = CastlePlies.ALL_CASTLE_MOVES[toCastleFieldID = MoveInt.isCastleKingSide(move) ? Castling.getRookToFieldID_king(colour) : Castling.getRookToFieldID_queen(colour)])) == 0L || !Checking.checkCastleDirs(toCastleFieldID, free, opponentKingBitboard)) break;
                return true;
            }
            case 3: {
                long officerMoves = OfficerPlies.ALL_OFFICER_MOVES[toFieldID];
                if ((opponentKingBitboard & officerMoves) == 0L || !Checking.checkOfficerDirs(toFieldID, free, opponentKingBitboard)) break;
                return true;
            }
            case 4: {
                long castleMoves = CastlePlies.ALL_CASTLE_MOVES[toFieldID];
                if ((opponentKingBitboard & castleMoves) == 0L || !Checking.checkCastleDirs(toFieldID, free, opponentKingBitboard)) break;
                return true;
            }
            case 5: {
                long officerMoves = OfficerPlies.ALL_OFFICER_MOVES[toFieldID];
                if ((opponentKingBitboard & officerMoves) != 0L && Checking.checkOfficerDirs(toFieldID, free, opponentKingBitboard)) {
                    return true;
                }
                long castleMoves = CastlePlies.ALL_CASTLE_MOVES[toFieldID];
                if ((opponentKingBitboard & castleMoves) == 0L || !Checking.checkCastleDirs(toFieldID, free, opponentKingBitboard)) break;
                return true;
            }
        }
        return false;
    }

    public static boolean isHiddenCheckMove(Board board, int move, int colour, long free, long opponentKingBitboard, int opponentKingFieldID) {
        PiecesList myQueens;
        long myQueensBoard;
        PiecesList myQueens2;
        long myQueensBoard2;
        PiecesList myCastles;
        long myCastlesBoard;
        PiecesList myOfficers;
        long myOfficersBoard;
        long fromBoard = Fields.ALL_ORDERED_A1H1[MoveInt.getFromFieldID(move)];
        long toBoard = Fields.ALL_ORDERED_A1H1[MoveInt.getToFieldID(move)];
        if (MoveInt.isEnpassant(move)) {
            long opponentPawnBitboard = Fields.ALL_ORDERED_A1H1[MoveInt.getEnpassantCapturedFieldID(move)];
            free |= opponentPawnBitboard;
            fromBoard |= opponentPawnBitboard;
        }
        IPiecesLists stateManager = board.getPiecesLists();
        long officerMoves = OfficerPlies.ALL_OFFICER_MOVES[opponentKingFieldID];
        if ((officerMoves & fromBoard) != 0L && (officerMoves & (myOfficersBoard = board.allByColourAndType[colour][3])) != 0L && Checking.checkHiddenOfficers(board, opponentKingFieldID, free, officerMoves, myOfficers = stateManager.getPieces(Figures.getPidByColourAndType(colour, 3)), fromBoard, toBoard)) {
            return true;
        }
        long castleMoves = CastlePlies.ALL_CASTLE_MOVES[opponentKingFieldID];
        if ((castleMoves & fromBoard) != 0L && (castleMoves & (myCastlesBoard = board.allByColourAndType[colour][4])) != 0L && Checking.checkHiddenCastles(board, opponentKingFieldID, free, castleMoves, myCastles = stateManager.getPieces(Figures.getPidByColourAndType(colour, 4)), fromBoard, toBoard)) {
            return true;
        }
        long queensOfficerMoves = OfficerPlies.ALL_OFFICER_MOVES[opponentKingFieldID];
        if ((queensOfficerMoves & fromBoard) != 0L && (queensOfficerMoves & (myQueensBoard2 = board.allByColourAndType[colour][5])) != 0L && Checking.checkHiddenOfficers(board, opponentKingFieldID, free, queensOfficerMoves, myQueens2 = stateManager.getPieces(Figures.getPidByColourAndType(colour, 5)), fromBoard, toBoard)) {
            return true;
        }
        long queensCastleMoves = CastlePlies.ALL_CASTLE_MOVES[opponentKingFieldID];
        return (queensCastleMoves & fromBoard) != 0L && (queensCastleMoves & (myQueensBoard = board.allByColourAndType[colour][5])) != 0L && Checking.checkHiddenCastles(board, opponentKingFieldID, free, queensCastleMoves, myQueens = stateManager.getPieces(Figures.getPidByColourAndType(colour, 5)), fromBoard, toBoard);
    }

    private static boolean checkHiddenOfficers(Board board, int kingFieldID, long free, long officerMoves, PiecesList oppOfficers, long unstopperFromBoard, long unstopperToBoard) {
        int count = oppOfficers.getDataSize();
        int[] data = oppOfficers.getData();
        for (int i = 0; i < count; ++i) {
            int oppOfficerID = data[i];
            long oppOfficerBitboard = Fields.ALL_ORDERED_A1H1[oppOfficerID];
            if ((officerMoves & oppOfficerBitboard) == 0L || (officerMoves & unstopperFromBoard) == 0L || !Checking.checkHiddenOfficerDirs(kingFieldID, free, oppOfficerBitboard, unstopperFromBoard, unstopperToBoard)) continue;
            return true;
        }
        return false;
    }

    private static boolean checkHiddenCastles(Board board, int opponentKingFieldID, long free, long castleMoves, PiecesList oppCastles, long unstopperFromBoard, long unstopperToBoard) {
        int count = oppCastles.getDataSize();
        int[] data = oppCastles.getData();
        for (int i = 0; i < count; ++i) {
            int oppCastleID = data[i];
            long oppCastleBitboard = Fields.ALL_ORDERED_A1H1[oppCastleID];
            if ((castleMoves & oppCastleBitboard) == 0L || (castleMoves & unstopperFromBoard) == 0L || !Checking.checkHiddenCastleDirs(opponentKingFieldID, free, oppCastleBitboard, unstopperFromBoard, unstopperToBoard)) continue;
            return true;
        }
        return false;
    }

    private static boolean checkHiddenOfficerDirs(int opponentKingFieldID, long free, long oppOfficerBitboard, long unstopperFromBoard, long unstopperToBoard) {
        long[] dir3Moves;
        long[] dir2Moves;
        long[] dir1Moves;
        long[] dir0Moves;
        long dir0 = OfficerPlies.ALL_OFFICER_DIR0_MOVES[opponentKingFieldID];
        if ((dir0 & oppOfficerBitboard) != 0L && (dir0 & unstopperFromBoard) != 0L && (dir0 & unstopperToBoard) == 0L && Checking.checkSlidingDir(free | unstopperFromBoard, oppOfficerBitboard, dir0Moves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[opponentKingFieldID][0])) {
            return true;
        }
        long dir1 = OfficerPlies.ALL_OFFICER_DIR1_MOVES[opponentKingFieldID];
        if ((dir1 & oppOfficerBitboard) != 0L && (dir1 & unstopperFromBoard) != 0L && (dir1 & unstopperToBoard) == 0L && Checking.checkSlidingDir(free | unstopperFromBoard, oppOfficerBitboard, dir1Moves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[opponentKingFieldID][1])) {
            return true;
        }
        long dir2 = OfficerPlies.ALL_OFFICER_DIR2_MOVES[opponentKingFieldID];
        if ((dir2 & oppOfficerBitboard) != 0L && (dir2 & unstopperFromBoard) != 0L && (dir2 & unstopperToBoard) == 0L && Checking.checkSlidingDir(free | unstopperFromBoard, oppOfficerBitboard, dir2Moves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[opponentKingFieldID][2])) {
            return true;
        }
        long dir3 = OfficerPlies.ALL_OFFICER_DIR3_MOVES[opponentKingFieldID];
        return (dir3 & oppOfficerBitboard) != 0L && (dir3 & unstopperFromBoard) != 0L && (dir3 & unstopperToBoard) == 0L && Checking.checkSlidingDir(free | unstopperFromBoard, oppOfficerBitboard, dir3Moves = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[opponentKingFieldID][3]);
    }

    private static boolean checkHiddenCastleDirs(int opponentKingFieldID, long free, long oppCastleBitboard, long unstopperFromBoard, long unstopperToBoard) {
        long[] dir3Moves;
        long[] dir2Moves;
        long[] dir1Moves;
        long[] dir0Moves;
        long dir0 = CastlePlies.ALL_CASTLE_DIR0_MOVES[opponentKingFieldID];
        if ((dir0 & oppCastleBitboard) != 0L && (dir0 & unstopperFromBoard) != 0L && (dir0 & unstopperToBoard) == 0L && Checking.checkSlidingDir(free | unstopperFromBoard, oppCastleBitboard, dir0Moves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[opponentKingFieldID][0])) {
            return true;
        }
        long dir1 = CastlePlies.ALL_CASTLE_DIR1_MOVES[opponentKingFieldID];
        if ((dir1 & oppCastleBitboard) != 0L && (dir1 & unstopperFromBoard) != 0L && (dir1 & unstopperToBoard) == 0L && Checking.checkSlidingDir(free | unstopperFromBoard, oppCastleBitboard, dir1Moves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[opponentKingFieldID][1])) {
            return true;
        }
        long dir2 = CastlePlies.ALL_CASTLE_DIR2_MOVES[opponentKingFieldID];
        if ((dir2 & oppCastleBitboard) != 0L && (dir2 & unstopperFromBoard) != 0L && (dir2 & unstopperToBoard) == 0L && Checking.checkSlidingDir(free | unstopperFromBoard, oppCastleBitboard, dir2Moves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[opponentKingFieldID][2])) {
            return true;
        }
        long dir3 = CastlePlies.ALL_CASTLE_DIR3_MOVES[opponentKingFieldID];
        return (dir3 & oppCastleBitboard) != 0L && (dir3 & unstopperFromBoard) != 0L && (dir3 & unstopperToBoard) == 0L && Checking.checkSlidingDir(free | unstopperFromBoard, oppCastleBitboard, dir3Moves = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[opponentKingFieldID][3]);
    }
}

