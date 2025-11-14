/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl1.internal;

import bagaturchess.bitboard.api.IHistoryProvider;
import bagaturchess.bitboard.impl1.internal.Bitboard;
import bagaturchess.bitboard.impl1.internal.CastlingUtil;
import bagaturchess.bitboard.impl1.internal.ChessBoard;
import bagaturchess.bitboard.impl1.internal.ChessConstants;
import bagaturchess.bitboard.impl1.internal.MagicUtil;
import bagaturchess.bitboard.impl1.internal.MoveUtil;
import bagaturchess.bitboard.impl1.internal.SEEUtil;
import bagaturchess.bitboard.impl1.internal.StaticMoves;
import java.util.Random;

public final class MoveGenerator {
    private final int[] moves = new int[30000];
    private final long[] moveScores = new long[30000];
    private final int[] nextToGenerate = new int[8192];
    private final int[] nextToMove = new int[8192];
    private int currentPly;
    private long counter_sorting;
    private Random randomizer = new Random();
    private int root_search_first_move_index;

    public MoveGenerator() {
        this.clearHistoryHeuristics();
    }

    public void clearHistoryHeuristics() {
        this.currentPly = 0;
    }

    public void setRootSearchFirstMoveIndex(int _root_search_first_move_index) {
        this.root_search_first_move_index = _root_search_first_move_index;
    }

    public void startPly() {
        this.nextToGenerate[this.currentPly + 1] = this.nextToGenerate[this.currentPly];
        this.nextToMove[this.currentPly + 1] = this.nextToGenerate[this.currentPly];
        ++this.currentPly;
    }

    public void endPly() {
        --this.currentPly;
    }

    public int next() {
        int n = this.currentPly;
        int n2 = this.nextToMove[n];
        this.nextToMove[n] = n2 + 1;
        return this.moves[n2];
    }

    public long getScore() {
        long val = this.moveScores[this.nextToMove[this.currentPly] - 1];
        if (val < 0L) {
            throw new IllegalStateException("getScore: val=" + val);
        }
        return val;
    }

    public int previous() {
        if (this.nextToMove[this.currentPly] - 1 < 0) {
            return 0;
        }
        return this.moves[this.nextToMove[this.currentPly] - 1];
    }

    public boolean hasNext() {
        return this.nextToGenerate[this.currentPly] != this.nextToMove[this.currentPly];
    }

    public void addMove(int move) {
        int n = this.currentPly;
        int n2 = this.nextToGenerate[n];
        this.nextToGenerate[n] = n2 + 1;
        this.moves[n2] = move;
    }

    public void setMVVLVAScores(ChessBoard cb) {
        int scale = 100;
        for (int j = this.nextToMove[this.currentPly]; j < this.nextToGenerate[this.currentPly]; ++j) {
            int move = this.moves[j];
            int score = 6 * MoveUtil.getAttackedPieceIndex(move) - 1 * MoveUtil.getSourcePieceIndex(move);
            if (MoveUtil.isPromotion(move)) {
                score += 1 * MoveUtil.getMoveType(move);
            }
            this.moveScores[j] = 100 * score;
        }
    }

    public void setSEEScores(ChessBoard cb) {
        for (int j = this.nextToMove[this.currentPly]; j < this.nextToGenerate[this.currentPly]; ++j) {
            this.moveScores[j] = SEEUtil.getSeeCaptureScore(cb, this.moves[j]);
        }
    }

    public int getCountGoodAttacks(ChessBoard cb) {
        int count = 0;
        for (int j = this.nextToMove[this.currentPly]; j < this.nextToGenerate[this.currentPly]; ++j) {
            if (SEEUtil.getSeeCaptureScore(cb, this.moves[j]) <= 0) continue;
            ++count;
        }
        return count;
    }

    public int getCountEqualAttacks(ChessBoard cb) {
        int count = 0;
        for (int j = this.nextToMove[this.currentPly]; j < this.nextToGenerate[this.currentPly]; ++j) {
            if (SEEUtil.getSeeCaptureScore(cb, this.moves[j]) != 0) continue;
            ++count;
        }
        return count;
    }

    public int getCountBadAttacks(ChessBoard cb) {
        int count = 0;
        for (int j = this.nextToMove[this.currentPly]; j < this.nextToGenerate[this.currentPly]; ++j) {
            if (SEEUtil.getSeeCaptureScore(cb, this.moves[j]) >= 0) continue;
            ++count;
        }
        return count;
    }

    public int getCountGoodAndEqualAttacks(ChessBoard cb) {
        int count = 0;
        for (int j = this.nextToMove[this.currentPly]; j < this.nextToGenerate[this.currentPly]; ++j) {
            if (SEEUtil.getSeeCaptureScore(cb, this.moves[j]) < 0) continue;
            ++count;
        }
        return count;
    }

    public int getCountMoves() {
        return this.nextToGenerate[this.currentPly] - this.nextToMove[this.currentPly];
    }

    public void setHHScores(int inCheck, int colorToMove, int parentMove, IHistoryProvider history_provider) {
        for (int j = this.nextToMove[this.currentPly]; j < this.nextToGenerate[this.currentPly]; ++j) {
            int move = this.moves[j];
            long score = history_provider.getScores(colorToMove, move);
            if (score < 0L) {
                throw new IllegalStateException("score < 0");
            }
            this.moveScores[j] = score;
        }
    }

    public void setRootScores(ChessBoard cb, int parentMove, int ttMove, int ply, IHistoryProvider history_provider) {
        int killer1Move = history_provider.getKiller1(cb.colorToMove, ply);
        int killer2Move = history_provider.getKiller2(cb.colorToMove, ply);
        int counterMove1 = history_provider.getCounter1(cb.colorToMove, parentMove);
        int counterMove2 = history_provider.getCounter2(cb.colorToMove, parentMove);
        for (int j = this.nextToMove[this.currentPly]; j < this.nextToGenerate[this.currentPly]; ++j) {
            int cur_move = this.moves[j];
            this.moveScores[j] = 0L;
            if (ttMove == cur_move) {
                int n = j;
                this.moveScores[n] = this.moveScores[n] + 2000000L;
            }
            if (killer1Move == cur_move) {
                int n = j;
                this.moveScores[n] = this.moveScores[n] + 500000L;
            }
            if (killer2Move == cur_move) {
                int n = j;
                this.moveScores[n] = this.moveScores[n] + 400000L;
            }
            if (counterMove1 == cur_move) {
                int n = j;
                this.moveScores[n] = this.moveScores[n] + 300000L;
            }
            if (counterMove2 == cur_move) {
                int n = j;
                this.moveScores[n] = this.moveScores[n] + 200000L;
            }
            if (MoveUtil.isQuiet(cur_move)) {
                int n = j;
                this.moveScores[n] = this.moveScores[n] + (long)history_provider.getScores(cb.colorToMove, cur_move);
                continue;
            }
            if (SEEUtil.getSeeCaptureScore(cb, cur_move) >= 0) {
                int n = j;
                this.moveScores[n] = this.moveScores[n] + (long)(700000 + 100 * (MoveUtil.getAttackedPieceIndex(cur_move) * 6 - MoveUtil.getSourcePieceIndex(cur_move)));
                continue;
            }
            int n = j;
            this.moveScores[n] = this.moveScores[n] + (long)(-5000 + 100 * (MoveUtil.getAttackedPieceIndex(cur_move) * 6 - MoveUtil.getSourcePieceIndex(cur_move)));
        }
    }

    public void sort() {
        int i;
        int start_index = this.nextToMove[this.currentPly];
        int end_index = this.nextToGenerate[this.currentPly] - 1;
        if (this.counter_sorting == 0L || this.counter_sorting % 10L == 0L) {
            this.randomize(this.moveScores, this.moves, start_index, end_index);
        }
        int j = i = start_index;
        while (i < end_index) {
            long score = this.moveScores[i + 1];
            int move = this.moves[i + 1];
            while (score > this.moveScores[j]) {
                this.moveScores[j + 1] = this.moveScores[j];
                this.moves[j + 1] = this.moves[j];
                if (j-- != start_index) continue;
            }
            this.moveScores[j + 1] = score;
            this.moves[j + 1] = move;
            j = ++i;
        }
        ++this.counter_sorting;
    }

    private void randomize(long[] arr1, int[] arr2, int start, int end) {
        for (int i = end; i > start + 1; --i) {
            int rnd_index = start + this.randomizer.nextInt(i - start);
            long tmp1 = arr1[i - 1];
            arr1[i - 1] = arr1[rnd_index];
            arr1[rnd_index] = tmp1;
            int tmp2 = arr2[i - 1];
            arr2[i - 1] = arr2[rnd_index];
            arr2[rnd_index] = tmp2;
        }
    }

    public void generateMoves(ChessBoard cb) {
        block0 : switch (Long.bitCount(cb.checkingPieces)) {
            case 0: {
                this.generateNotInCheckMoves(cb);
                break;
            }
            case 1: {
                switch (cb.pieceIndexes[Long.numberOfTrailingZeros(cb.checkingPieces)]) {
                    case 1: 
                    case 2: {
                        this.addKingMoves(cb);
                        break block0;
                    }
                }
                this.generateOutOfSlidingCheckMoves(cb);
                break;
            }
            default: {
                this.addKingMoves(cb);
            }
        }
    }

    public void generateAttacks(ChessBoard cb) {
        switch (Long.bitCount(cb.checkingPieces)) {
            case 0: {
                this.generateNotInCheckAttacks(cb);
                break;
            }
            case 1: {
                this.generateOutOfCheckAttacks(cb);
                break;
            }
            default: {
                this.addKingAttacks(cb);
            }
        }
    }

    private void generateNotInCheckMoves(ChessBoard cb) {
        this.addKingMoves(cb);
        this.addQueenMoves(cb.pieces[cb.colorToMove][5] & (cb.pinnedPieces ^ 0xFFFFFFFFFFFFFFFFL), cb.allPieces, cb.emptySpaces);
        this.addRookMoves(cb.pieces[cb.colorToMove][4] & (cb.pinnedPieces ^ 0xFFFFFFFFFFFFFFFFL), cb.allPieces, cb.emptySpaces);
        this.addBishopMoves(cb.pieces[cb.colorToMove][3] & (cb.pinnedPieces ^ 0xFFFFFFFFFFFFFFFFL), cb.allPieces, cb.emptySpaces);
        this.addNightMoves(cb.pieces[cb.colorToMove][2] & (cb.pinnedPieces ^ 0xFFFFFFFFFFFFFFFFL), cb.emptySpaces);
        this.addPawnMoves(cb.pieces[cb.colorToMove][1] & (cb.pinnedPieces ^ 0xFFFFFFFFFFFFFFFFL), cb, cb.emptySpaces);
        block6: for (long piece = cb.friendlyPieces[cb.colorToMove] & cb.pinnedPieces; piece != 0L; piece &= piece - 1L) {
            switch (cb.pieceIndexes[Long.numberOfTrailingZeros(piece)]) {
                case 1: {
                    this.addPawnMoves(Long.lowestOneBit(piece), cb, cb.emptySpaces & ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][cb.kingIndex[cb.colorToMove]]);
                    continue block6;
                }
                case 3: {
                    this.addBishopMoves(Long.lowestOneBit(piece), cb.allPieces, cb.emptySpaces & ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][cb.kingIndex[cb.colorToMove]]);
                    continue block6;
                }
                case 4: {
                    this.addRookMoves(Long.lowestOneBit(piece), cb.allPieces, cb.emptySpaces & ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][cb.kingIndex[cb.colorToMove]]);
                    continue block6;
                }
                case 5: {
                    this.addQueenMoves(Long.lowestOneBit(piece), cb.allPieces, cb.emptySpaces & ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][cb.kingIndex[cb.colorToMove]]);
                }
            }
        }
    }

    private void generateOutOfSlidingCheckMoves(ChessBoard cb) {
        long inBetween = ChessConstants.IN_BETWEEN[cb.kingIndex[cb.colorToMove]][Long.numberOfTrailingZeros(cb.checkingPieces)];
        if (inBetween != 0L) {
            this.addNightMoves(cb.pieces[cb.colorToMove][2] & (cb.pinnedPieces ^ 0xFFFFFFFFFFFFFFFFL), inBetween);
            this.addBishopMoves(cb.pieces[cb.colorToMove][3] & (cb.pinnedPieces ^ 0xFFFFFFFFFFFFFFFFL), cb.allPieces, inBetween);
            this.addRookMoves(cb.pieces[cb.colorToMove][4] & (cb.pinnedPieces ^ 0xFFFFFFFFFFFFFFFFL), cb.allPieces, inBetween);
            this.addQueenMoves(cb.pieces[cb.colorToMove][5] & (cb.pinnedPieces ^ 0xFFFFFFFFFFFFFFFFL), cb.allPieces, inBetween);
            this.addPawnMoves(cb.pieces[cb.colorToMove][1] & (cb.pinnedPieces ^ 0xFFFFFFFFFFFFFFFFL), cb, inBetween);
        }
        this.addKingMoves(cb);
    }

    private void generateNotInCheckAttacks(ChessBoard cb) {
        long enemies = cb.friendlyPieces[cb.colorToMoveInverse];
        this.addEpAttacks(cb);
        this.addPawnAttacksAndPromotions(cb.pieces[cb.colorToMove][1] & (cb.pinnedPieces ^ 0xFFFFFFFFFFFFFFFFL), cb, enemies, cb.emptySpaces);
        this.addNightAttacks(cb.pieces[cb.colorToMove][2] & (cb.pinnedPieces ^ 0xFFFFFFFFFFFFFFFFL), cb.pieceIndexes, enemies);
        this.addRookAttacks(cb.pieces[cb.colorToMove][4] & (cb.pinnedPieces ^ 0xFFFFFFFFFFFFFFFFL), cb, enemies);
        this.addBishopAttacks(cb.pieces[cb.colorToMove][3] & (cb.pinnedPieces ^ 0xFFFFFFFFFFFFFFFFL), cb, enemies);
        this.addQueenAttacks(cb.pieces[cb.colorToMove][5] & (cb.pinnedPieces ^ 0xFFFFFFFFFFFFFFFFL), cb, enemies);
        this.addKingAttacks(cb);
        block6: for (long piece = cb.friendlyPieces[cb.colorToMove] & cb.pinnedPieces; piece != 0L; piece &= piece - 1L) {
            switch (cb.pieceIndexes[Long.numberOfTrailingZeros(piece)]) {
                case 1: {
                    this.addPawnAttacksAndPromotions(Long.lowestOneBit(piece), cb, enemies & ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][cb.kingIndex[cb.colorToMove]], 0L);
                    continue block6;
                }
                case 3: {
                    this.addBishopAttacks(Long.lowestOneBit(piece), cb, enemies & ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][cb.kingIndex[cb.colorToMove]]);
                    continue block6;
                }
                case 4: {
                    this.addRookAttacks(Long.lowestOneBit(piece), cb, enemies & ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][cb.kingIndex[cb.colorToMove]]);
                    continue block6;
                }
                case 5: {
                    this.addQueenAttacks(Long.lowestOneBit(piece), cb, enemies & ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(piece)][cb.kingIndex[cb.colorToMove]]);
                }
            }
        }
    }

    private void generateOutOfCheckAttacks(ChessBoard cb) {
        this.addEpAttacks(cb);
        this.addPawnAttacksAndPromotions(cb.pieces[cb.colorToMove][1] & (cb.pinnedPieces ^ 0xFFFFFFFFFFFFFFFFL), cb, cb.checkingPieces, cb.emptySpaces);
        this.addNightAttacks(cb.pieces[cb.colorToMove][2] & (cb.pinnedPieces ^ 0xFFFFFFFFFFFFFFFFL), cb.pieceIndexes, cb.checkingPieces);
        this.addBishopAttacks(cb.pieces[cb.colorToMove][3] & (cb.pinnedPieces ^ 0xFFFFFFFFFFFFFFFFL), cb, cb.checkingPieces);
        this.addRookAttacks(cb.pieces[cb.colorToMove][4] & (cb.pinnedPieces ^ 0xFFFFFFFFFFFFFFFFL), cb, cb.checkingPieces);
        this.addQueenAttacks(cb.pieces[cb.colorToMove][5] & (cb.pinnedPieces ^ 0xFFFFFFFFFFFFFFFFL), cb, cb.checkingPieces);
        this.addKingAttacks(cb);
    }

    private void addPawnAttacksAndPromotions(long pawns, ChessBoard cb, long enemies, long emptySpaces) {
        if (pawns == 0L) {
            return;
        }
        if (cb.colorToMove == 0) {
            int fromIndex;
            long piece;
            for (piece = pawns & Bitboard.RANK_NON_PROMOTION[0] & Bitboard.getBlackPawnAttacks(enemies); piece != 0L; piece &= piece - 1L) {
                fromIndex = Long.numberOfTrailingZeros(piece);
                for (long moves = StaticMoves.PAWN_ATTACKS[0][fromIndex] & enemies; moves != 0L; moves &= moves - 1L) {
                    int toIndex = Long.numberOfTrailingZeros(moves);
                    this.addMove(MoveUtil.createAttackMove(fromIndex, toIndex, 1, cb.pieceIndexes[toIndex]));
                }
            }
            for (piece = pawns & 0xFF000000000000L; piece != 0L; piece &= piece - 1L) {
                fromIndex = Long.numberOfTrailingZeros(piece);
                if ((Long.lowestOneBit(piece) << 8 & emptySpaces) != 0L) {
                    this.addPromotionMove(fromIndex, fromIndex + 8);
                }
                this.addPromotionAttacks(StaticMoves.PAWN_ATTACKS[0][fromIndex] & enemies, fromIndex, cb.pieceIndexes);
            }
        } else {
            int fromIndex;
            long piece;
            for (piece = pawns & Bitboard.RANK_NON_PROMOTION[1] & Bitboard.getWhitePawnAttacks(enemies); piece != 0L; piece &= piece - 1L) {
                fromIndex = Long.numberOfTrailingZeros(piece);
                for (long moves = StaticMoves.PAWN_ATTACKS[1][fromIndex] & enemies; moves != 0L; moves &= moves - 1L) {
                    int toIndex = Long.numberOfTrailingZeros(moves);
                    this.addMove(MoveUtil.createAttackMove(fromIndex, toIndex, 1, cb.pieceIndexes[toIndex]));
                }
            }
            for (piece = pawns & 0xFF00L; piece != 0L; piece &= piece - 1L) {
                fromIndex = Long.numberOfTrailingZeros(piece);
                if ((Long.lowestOneBit(piece) >>> 8 & emptySpaces) != 0L) {
                    this.addPromotionMove(fromIndex, fromIndex - 8);
                }
                this.addPromotionAttacks(StaticMoves.PAWN_ATTACKS[1][fromIndex] & enemies, fromIndex, cb.pieceIndexes);
            }
        }
    }

    private void addBishopAttacks(long piece, ChessBoard cb, long possiblePositions) {
        while (piece != 0L) {
            int fromIndex = Long.numberOfTrailingZeros(piece);
            for (long moves = MagicUtil.getBishopMoves(fromIndex, cb.allPieces) & possiblePositions; moves != 0L; moves &= moves - 1L) {
                int toIndex = Long.numberOfTrailingZeros(moves);
                this.addMove(MoveUtil.createAttackMove(fromIndex, toIndex, 3, cb.pieceIndexes[toIndex]));
            }
            piece &= piece - 1L;
        }
    }

    private void addRookAttacks(long piece, ChessBoard cb, long possiblePositions) {
        while (piece != 0L) {
            int fromIndex = Long.numberOfTrailingZeros(piece);
            for (long moves = MagicUtil.getRookMoves(fromIndex, cb.allPieces) & possiblePositions; moves != 0L; moves &= moves - 1L) {
                int toIndex = Long.numberOfTrailingZeros(moves);
                this.addMove(MoveUtil.createAttackMove(fromIndex, toIndex, 4, cb.pieceIndexes[toIndex]));
            }
            piece &= piece - 1L;
        }
    }

    private void addQueenAttacks(long piece, ChessBoard cb, long possiblePositions) {
        while (piece != 0L) {
            int fromIndex = Long.numberOfTrailingZeros(piece);
            for (long moves = MagicUtil.getQueenMoves(fromIndex, cb.allPieces) & possiblePositions; moves != 0L; moves &= moves - 1L) {
                int toIndex = Long.numberOfTrailingZeros(moves);
                this.addMove(MoveUtil.createAttackMove(fromIndex, toIndex, 5, cb.pieceIndexes[toIndex]));
            }
            piece &= piece - 1L;
        }
    }

    private void addBishopMoves(long piece, long allPieces, long possiblePositions) {
        while (piece != 0L) {
            int fromIndex = Long.numberOfTrailingZeros(piece);
            for (long moves = MagicUtil.getBishopMoves(fromIndex, allPieces) & possiblePositions; moves != 0L; moves &= moves - 1L) {
                this.addMove(MoveUtil.createMove(fromIndex, Long.numberOfTrailingZeros(moves), 3));
            }
            piece &= piece - 1L;
        }
    }

    private void addQueenMoves(long piece, long allPieces, long possiblePositions) {
        while (piece != 0L) {
            int fromIndex = Long.numberOfTrailingZeros(piece);
            for (long moves = MagicUtil.getQueenMoves(fromIndex, allPieces) & possiblePositions; moves != 0L; moves &= moves - 1L) {
                this.addMove(MoveUtil.createMove(fromIndex, Long.numberOfTrailingZeros(moves), 5));
            }
            piece &= piece - 1L;
        }
    }

    private void addRookMoves(long piece, long allPieces, long possiblePositions) {
        while (piece != 0L) {
            int fromIndex = Long.numberOfTrailingZeros(piece);
            for (long moves = MagicUtil.getRookMoves(fromIndex, allPieces) & possiblePositions; moves != 0L; moves &= moves - 1L) {
                this.addMove(MoveUtil.createMove(fromIndex, Long.numberOfTrailingZeros(moves), 4));
            }
            piece &= piece - 1L;
        }
    }

    private void addNightMoves(long piece, long possiblePositions) {
        while (piece != 0L) {
            int fromIndex = Long.numberOfTrailingZeros(piece);
            for (long moves = StaticMoves.KNIGHT_MOVES[fromIndex] & possiblePositions; moves != 0L; moves &= moves - 1L) {
                this.addMove(MoveUtil.createMove(fromIndex, Long.numberOfTrailingZeros(moves), 2));
            }
            piece &= piece - 1L;
        }
    }

    private void addPawnMoves(long pawns, ChessBoard cb, long possiblePositions) {
        if (pawns == 0L) {
            return;
        }
        if (cb.colorToMove == 0) {
            long piece;
            for (piece = pawns & possiblePositions >>> 8 & 0xFFFFFFFFFF00L; piece != 0L; piece &= piece - 1L) {
                this.addMove(MoveUtil.createWhitePawnMove(Long.numberOfTrailingZeros(piece)));
            }
            for (piece = pawns & possiblePositions >>> 16 & 0xFF00L; piece != 0L; piece &= piece - 1L) {
                if ((cb.emptySpaces & Long.lowestOneBit(piece) << 8) == 0L) continue;
                this.addMove(MoveUtil.createWhitePawn2Move(Long.numberOfTrailingZeros(piece)));
            }
        } else {
            long piece;
            for (piece = pawns & possiblePositions << 8 & 0xFFFFFFFFFF0000L; piece != 0L; piece &= piece - 1L) {
                this.addMove(MoveUtil.createBlackPawnMove(Long.numberOfTrailingZeros(piece)));
            }
            for (piece = pawns & possiblePositions << 16 & 0xFF000000000000L; piece != 0L; piece &= piece - 1L) {
                if ((cb.emptySpaces & Long.lowestOneBit(piece) >>> 8) == 0L) continue;
                this.addMove(MoveUtil.createBlackPawn2Move(Long.numberOfTrailingZeros(piece)));
            }
        }
    }

    private void addKingMoves(ChessBoard cb) {
        int fromIndex = cb.kingIndex[cb.colorToMove];
        for (long moves = StaticMoves.KING_MOVES[fromIndex] & cb.emptySpaces; moves != 0L; moves &= moves - 1L) {
            this.addMove(MoveUtil.createMove(fromIndex, Long.numberOfTrailingZeros(moves), 6));
        }
        if (cb.checkingPieces == 0L) {
            for (long castlingIndexes = CastlingUtil.getCastlingIndexes(cb.colorToMove, cb.castlingRights, cb.castlingConfig); castlingIndexes != 0L; castlingIndexes &= castlingIndexes - 1L) {
                int toIndex_king = Long.numberOfTrailingZeros(castlingIndexes);
                if (!CastlingUtil.isValidCastlingMove(cb, fromIndex, toIndex_king)) continue;
                this.addMove(MoveUtil.createCastlingMove(fromIndex, toIndex_king));
            }
        }
    }

    private void addKingAttacks(ChessBoard cb) {
        int fromIndex = cb.kingIndex[cb.colorToMove];
        for (long moves = StaticMoves.KING_MOVES[fromIndex] & cb.friendlyPieces[cb.colorToMoveInverse]; moves != 0L; moves &= moves - 1L) {
            int toIndex = Long.numberOfTrailingZeros(moves);
            this.addMove(MoveUtil.createAttackMove(fromIndex, toIndex, 6, cb.pieceIndexes[toIndex]));
        }
    }

    private void addNightAttacks(long piece, int[] pieceIndexes, long possiblePositions) {
        while (piece != 0L) {
            int fromIndex = Long.numberOfTrailingZeros(piece);
            for (long moves = StaticMoves.KNIGHT_MOVES[fromIndex] & possiblePositions; moves != 0L; moves &= moves - 1L) {
                int toIndex = Long.numberOfTrailingZeros(moves);
                this.addMove(MoveUtil.createAttackMove(fromIndex, toIndex, 2, pieceIndexes[toIndex]));
            }
            piece &= piece - 1L;
        }
    }

    private void addEpAttacks(ChessBoard cb) {
        if (cb.epIndex == 0) {
            return;
        }
        for (long piece = cb.pieces[cb.colorToMove][1] & StaticMoves.PAWN_ATTACKS[cb.colorToMoveInverse][cb.epIndex]; piece != 0L; piece &= piece - 1L) {
            this.addMove(MoveUtil.createEPMove(Long.numberOfTrailingZeros(piece), cb.epIndex));
        }
    }

    private void addPromotionMove(int fromIndex, int toIndex) {
        this.addMove(MoveUtil.createPromotionMove(5, fromIndex, toIndex));
        this.addMove(MoveUtil.createPromotionMove(2, fromIndex, toIndex));
        this.addMove(MoveUtil.createPromotionMove(3, fromIndex, toIndex));
        this.addMove(MoveUtil.createPromotionMove(4, fromIndex, toIndex));
    }

    private void addPromotionAttacks(long moves, int fromIndex, int[] pieceIndexes) {
        while (moves != 0L) {
            int toIndex = Long.numberOfTrailingZeros(moves);
            this.addMove(MoveUtil.createPromotionAttack(5, fromIndex, toIndex, pieceIndexes[toIndex]));
            this.addMove(MoveUtil.createPromotionAttack(2, fromIndex, toIndex, pieceIndexes[toIndex]));
            this.addMove(MoveUtil.createPromotionAttack(3, fromIndex, toIndex, pieceIndexes[toIndex]));
            this.addMove(MoveUtil.createPromotionAttack(4, fromIndex, toIndex, pieceIndexes[toIndex]));
            moves &= moves - 1L;
        }
    }
}

