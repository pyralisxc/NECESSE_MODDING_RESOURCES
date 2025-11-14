/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.engines.evaladapters.carballo;

import bagaturchess.engines.evaladapters.carballo.BitboardAttacks;
import bagaturchess.engines.evaladapters.carballo.BitboardUtils;
import bagaturchess.engines.evaladapters.carballo.IBoard;
import bagaturchess.engines.evaladapters.carballo.KPKBitbase;

public class Endgame {
    public static final int SCALE_FACTOR_DRAW = 0;
    public static final int SCALE_FACTOR_DRAWISH = 100;
    public static final int SCALE_FACTOR_DEFAULT = 1000;
    public static final int[] closerSquares = new int[]{0, 0, 100, 80, 60, 40, 20, 10};
    private static final int[] toCorners = new int[]{100, 90, 80, 70, 70, 80, 90, 100, 90, 70, 60, 50, 50, 60, 70, 90, 80, 60, 40, 30, 30, 40, 60, 80, 70, 50, 30, 20, 20, 30, 50, 70, 70, 50, 30, 20, 20, 30, 50, 70, 80, 60, 40, 30, 30, 40, 60, 80, 90, 70, 60, 50, 50, 60, 70, 90, 100, 90, 80, 70, 70, 80, 90, 100};
    private static final int[] toColorCorners = new int[]{200, 190, 180, 170, 160, 150, 140, 130, 190, 180, 170, 160, 150, 140, 130, 140, 180, 170, 155, 140, 140, 125, 140, 150, 170, 160, 140, 120, 110, 140, 150, 160, 160, 150, 140, 110, 120, 140, 160, 170, 150, 140, 125, 140, 140, 155, 170, 180, 140, 130, 140, 150, 160, 170, 180, 190, 130, 140, 150, 160, 170, 180, 190, 200};
    static KPKBitbase kpkBitbase = new KPKBitbase();

    public static int evaluateEndgame(IBoard board, int[] scaleFactor, int whitePawns, int blackPawns, int whiteKnights, int blackKnights, int whiteBishops, int blackBishops, int whiteRooks, int blackRooks, int whiteQueens, int blackQueens) {
        scaleFactor[0] = 1000;
        int whiteNoPawnMaterial = whiteKnights + whiteBishops + whiteRooks + whiteQueens;
        int blackNoPawnMaterial = blackKnights + blackBishops + blackRooks + blackQueens;
        int whiteMaterial = whiteNoPawnMaterial + whitePawns;
        int blackMaterial = blackNoPawnMaterial + blackPawns;
        if (whitePawns == 0 && blackPawns == 0) {
            if (blackMaterial == 0 && whiteMaterial == 2 && whiteKnights == 1 && whiteBishops == 1 || whiteMaterial == 0 && blackMaterial == 2 && blackKnights == 1 && blackBishops == 1) {
                return Endgame.endgameKBNK(board, whiteMaterial > blackMaterial);
            }
            if (whiteMaterial == 1 && blackMaterial == 1) {
                if (whiteRooks == 1 && blackRooks == 1) {
                    return 0;
                }
                if (whiteQueens == 1 && blackQueens == 1) {
                    return 0;
                }
            }
        } else if (whitePawns == 1 && blackPawns == 0 || whitePawns == 0 && blackPawns == 1) {
            if (whiteNoPawnMaterial == 0 && blackNoPawnMaterial == 0) {
                return Endgame.endgameKPK(board, whiteMaterial > blackMaterial);
            }
            if ((whiteNoPawnMaterial == 1 && blackNoPawnMaterial == 0 || whiteNoPawnMaterial == 0 && blackNoPawnMaterial == 1) && (whiteQueens == 1 && blackPawns == 1 || blackQueens == 1 && whitePawns == 1)) {
                return Endgame.endgameKQKP(board, whiteQueens > blackQueens);
            }
            if (whiteNoPawnMaterial == 1 && blackNoPawnMaterial == 1) {
                if (whiteRooks == 1 && blackRooks == 1) {
                    scaleFactor[0] = Endgame.scaleKRPKR(board, whitePawns > blackPawns);
                }
                if (whiteBishops == 1 && blackBishops == 1) {
                    return Endgame.endgameKBPKB(board, whitePawns > blackPawns);
                }
                if (whiteBishops == 1 && whitePawns == 1 && blackKnights == 1 || blackBishops == 1 && blackPawns == 1 && whiteKnights == 1) {
                    return Endgame.endgameKBPKN(board, whitePawns > blackPawns);
                }
            }
        }
        if (blackMaterial == 0 && (whiteBishops >= 2 || whiteRooks > 0 || whiteQueens > 0) || whiteMaterial == 0 && (whiteBishops >= 2 || blackRooks > 0 || blackQueens > 0)) {
            return Endgame.endgameKXK(board, whiteMaterial > blackMaterial, whiteKnights + blackKnights, whiteBishops + blackBishops, whiteRooks + blackRooks, whiteQueens + blackQueens);
        }
        if (whiteRooks == 1 && blackRooks == 1 && (whitePawns == 2 && blackPawns == 1 || whitePawns == 1 && blackPawns == 2)) {
            scaleFactor[0] = Endgame.scaleKRPPKRP(board, whitePawns > blackPawns);
        }
        if (scaleFactor[0] == 0) {
            return 0;
        }
        return Short.MAX_VALUE;
    }

    private static int endgameKXK(IBoard board, boolean whiteDominant, int knights, int bishops, int rooks, int queens) {
        byte whiteKingIndex = BitboardUtils.square2Index(board.getKings() & board.getWhites());
        byte blackKingIndex = BitboardUtils.square2Index(board.getKings() & board.getBlacks());
        int value = 20000 + knights * 325 + bishops * 325 + rooks * 500 + queens * 975 + closerSquares[BitboardUtils.distance(whiteKingIndex, blackKingIndex)] + (whiteDominant ? toCorners[blackKingIndex] : toCorners[whiteKingIndex]);
        return whiteDominant ? value : -value;
    }

    private static int endgameKBNK(IBoard board, boolean whiteDominant) {
        int whiteKingIndex = BitboardUtils.square2Index(board.getKings() & board.getWhites());
        int blackKingIndex = BitboardUtils.square2Index(board.getKings() & board.getBlacks());
        if (BitboardUtils.isBlackSquare(board.getBishops())) {
            whiteKingIndex = BitboardUtils.flipHorizontalIndex(whiteKingIndex);
            blackKingIndex = BitboardUtils.flipHorizontalIndex(blackKingIndex);
        }
        int value = 20000 + closerSquares[BitboardUtils.distance(whiteKingIndex, blackKingIndex)] + (whiteDominant ? toColorCorners[blackKingIndex] : toColorCorners[whiteKingIndex]);
        return whiteDominant ? value : -value;
    }

    private static int endgameKPK(IBoard board, boolean whiteDominant) {
        if (!kpkBitbase.probe(board)) {
            return 0;
        }
        return whiteDominant ? 20100 + BitboardUtils.getRankOfIndex(BitboardUtils.square2Index(board.getPawns())) : -20100 - (7 - BitboardUtils.getRankOfIndex(BitboardUtils.square2Index(board.getPawns())));
    }

    private static int scaleKRPKR(IBoard board, boolean whiteDominant) {
        int dominantColor = whiteDominant ? 0 : 1;
        long otherRook = board.getRooks() & (whiteDominant ? board.getBlacks() : board.getWhites());
        long dominantKing = board.getKings() & (whiteDominant ? board.getWhites() : board.getBlacks());
        long otherKing = board.getKings() & (whiteDominant ? board.getBlacks() : board.getWhites());
        byte dominantKingIndex = BitboardUtils.square2Index(dominantKing);
        int rank8 = whiteDominant ? 7 : 0;
        int rank7 = whiteDominant ? 6 : 1;
        int rank6 = whiteDominant ? 5 : 2;
        int rank2 = whiteDominant ? 1 : 6;
        long pawn = board.getPawns();
        byte pawnIndex = BitboardUtils.square2Index(pawn);
        int pawnFileIndex = 7 - (pawnIndex & 7);
        long pawnFile = BitboardUtils.FILE[pawnFileIndex];
        long pawnFileAndAdjacents = BitboardUtils.FILE[pawnFileIndex] | BitboardUtils.FILES_ADJACENT[pawnFileIndex];
        if ((BitboardUtils.RANKS_BACKWARD[dominantColor][rank6] & pawn) != 0L && (BitboardUtils.RANKS_BACKWARD[dominantColor][rank6] & dominantKing) != 0L && (BitboardUtils.RANKS_FORWARD[dominantColor][rank6] & pawnFileAndAdjacents & otherKing) != 0L && (BitboardUtils.RANK[rank6] & otherRook) != 0L) {
            return 0;
        }
        if ((BitboardUtils.RANK[rank6] & pawn) != 0L && (BitboardUtils.RANKS_FORWARD[dominantColor][rank6] & pawnFileAndAdjacents & otherKing) != 0L && ((BitboardUtils.RANK_AND_BACKWARD[dominantColor][rank2] & otherRook) != 0L || board.getColourToMove() == 0 != whiteDominant && BitboardUtils.distance(pawnIndex, dominantKingIndex) >= 3)) {
            return 0;
        }
        if ((BitboardUtils.RANK[rank7] & pawn) != 0L && (BitboardUtils.RANKS_FORWARD[dominantColor][rank6] & pawnFile & otherKing) != 0L && (BitboardUtils.RANK_AND_BACKWARD[dominantColor][rank2] & otherRook) != 0L && (board.getColourToMove() == 0 != whiteDominant || BitboardUtils.distance(pawnIndex, dominantKingIndex) >= 2)) {
            return 0;
        }
        if ((0xC3C3C3C3C3C3C3C3L & pawn) != 0L && (BitboardUtils.RANK[rank8] & pawnFileAndAdjacents & otherKing) != 0L && (BitboardUtils.RANK[rank8] & otherRook) != 0L) {
            return 0;
        }
        return 1000;
    }

    private static int endgameKQKP(IBoard board, boolean whiteDominant) {
        long pawnZone;
        long ranks12 = whiteDominant ? 65535L : -281474976710656L;
        long pawn = board.getPawns();
        if ((0x8080808080808080L & pawn) != 0L) {
            pawnZone = BitboardUtils.FILES_LEFT[3] & ranks12;
        } else if ((0x2020202020202020L & pawn) != 0L) {
            pawnZone = BitboardUtils.FILES_LEFT[4] & ranks12;
        } else if ((0x404040404040404L & pawn) != 0L) {
            pawnZone = BitboardUtils.FILES_RIGHT[3] & ranks12;
        } else if ((0x101010101010101L & pawn) != 0L) {
            pawnZone = BitboardUtils.FILES_RIGHT[4] & ranks12;
        } else {
            return Short.MAX_VALUE;
        }
        long dominantKing = board.getKings() & (whiteDominant ? board.getWhites() : board.getBlacks());
        long otherKing = board.getKings() & (whiteDominant ? board.getBlacks() : board.getWhites());
        byte dominantKingIndex = BitboardUtils.square2Index(dominantKing);
        byte pawnIndex = BitboardUtils.square2Index(pawn);
        if ((pawnZone & otherKing) != 0L && BitboardUtils.distance(dominantKingIndex, pawnIndex) >= 1) {
            return 0;
        }
        return Short.MAX_VALUE;
    }

    private static int endgameKBPKN(IBoard board, boolean whiteDominant) {
        long otherKing;
        int dominantColor = whiteDominant ? 0 : 1;
        long dominantBishop = board.getBishops() & (whiteDominant ? board.getWhites() : board.getBlacks());
        long dominantBishopSquares = BitboardUtils.getSameColorSquares(dominantBishop);
        long pawn = board.getPawns();
        long pawnRoute = BitboardUtils.frontFile(pawn, dominantColor);
        if ((pawnRoute & (otherKing = board.getKings() & (whiteDominant ? board.getBlacks() : board.getWhites()))) != 0L && (dominantBishopSquares & otherKing) == 0L) {
            return 0;
        }
        return Short.MAX_VALUE;
    }

    private static int endgameKBPKB(IBoard board, boolean whiteDominant) {
        long otherKing;
        int dominantColor = whiteDominant ? 0 : 1;
        long dominantBishop = board.getBishops() & (whiteDominant ? board.getWhites() : board.getBlacks());
        long dominantBishopSquares = BitboardUtils.getSameColorSquares(dominantBishop);
        long otherBishop = board.getBishops() & (whiteDominant ? board.getBlacks() : board.getWhites());
        long pawn = board.getPawns();
        long pawnRoute = BitboardUtils.frontFile(pawn, dominantColor);
        if ((pawnRoute & (otherKing = board.getKings() & (whiteDominant ? board.getBlacks() : board.getWhites()))) != 0L && (dominantBishopSquares & otherKing) == 0L) {
            return 0;
        }
        long otherBishopSquares = BitboardUtils.getSameColorSquares(otherBishop);
        if (dominantBishopSquares != otherBishopSquares) {
            byte otherBishopIndex = BitboardUtils.square2Index(otherBishop);
            if ((otherBishop & pawnRoute) != 0L || (BitboardAttacks.getInstance().bishop[otherBishopIndex] & pawnRoute) != 0L) {
                return 0;
            }
        }
        return Short.MAX_VALUE;
    }

    private static int scaleKRPPKRP(IBoard board, boolean whiteDominant) {
        int dominantColor = whiteDominant ? 0 : 1;
        long dominantPawns = board.getPawns() & (whiteDominant ? board.getWhites() : board.getBlacks());
        long p1Front = BitboardUtils.frontPawnSpan(BitboardUtils.lsb(dominantPawns), dominantColor);
        long p2Front = BitboardUtils.frontPawnSpan(BitboardUtils.msb(dominantPawns), dominantColor);
        long otherPawn = board.getPawns() & (whiteDominant ? board.getBlacks() : board.getWhites());
        if ((p1Front & otherPawn) == 0L || (p2Front & otherPawn) == 0L) {
            return 1000;
        }
        long otherKing = board.getKings() & (whiteDominant ? board.getBlacks() : board.getWhites());
        if ((p1Front & otherKing) != 0L && (p1Front & otherKing) != 1L) {
            return 100;
        }
        return 1000;
    }
}

