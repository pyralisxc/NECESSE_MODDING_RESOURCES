/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl1.internal;

import bagaturchess.bitboard.impl.datastructs.StackLongInt;
import bagaturchess.bitboard.impl1.internal.CastlingConfig;
import bagaturchess.bitboard.impl1.internal.CastlingUtil;
import bagaturchess.bitboard.impl1.internal.CheckUtil;
import bagaturchess.bitboard.impl1.internal.ChessBoardUtil;
import bagaturchess.bitboard.impl1.internal.ChessConstants;
import bagaturchess.bitboard.impl1.internal.EvalConstants;
import bagaturchess.bitboard.impl1.internal.MagicUtil;
import bagaturchess.bitboard.impl1.internal.MaterialUtil;
import bagaturchess.bitboard.impl1.internal.MoveUtil;
import bagaturchess.bitboard.impl1.internal.StaticMoves;
import bagaturchess.bitboard.impl1.internal.Util;
import bagaturchess.bitboard.impl1.internal.Zobrist;

public final class ChessBoard {
    private static ChessBoard[] instances;
    public final long[][] pieces = new long[2][7];
    public final long[] friendlyPieces = new long[2];
    public int castlingRights;
    public int psqtScore_mg;
    public int psqtScore_eg;
    public int colorToMove;
    public int colorToMoveInverse;
    public int epIndex;
    public int materialKey;
    public int material_factor_white;
    public int material_factor_black;
    public long allPieces;
    public long emptySpaces;
    public long zobristKey;
    public long pawnZobristKey;
    public long checkingPieces;
    public long pinnedPieces;
    public long discoveredPieces;
    public final int[] pieceIndexes = new int[64];
    public final int[] kingIndex = new int[2];
    public final long[] kingArea = new long[2];
    public int moveCounter = 0;
    public final int[] castlingHistory = new int[2048];
    public final int[] epIndexHistory = new int[2048];
    public final long[] zobristKeyHistory = new long[2048];
    public final long[] checkingPiecesHistory = new long[2048];
    public final long[] pinnedPiecesHistory = new long[2048];
    public final long[] discoveredPiecesHistory = new long[2048];
    public final int[] lastCaptureOrPawnMoveBeforeHistory = new int[2048];
    public int[] playedMoves = new int[2048];
    public int playedMovesCount = 0;
    public int lastCaptureOrPawnMoveBefore = 0;
    public StackLongInt playedBoardStates = new StackLongInt(9631);
    public CastlingConfig castlingConfig;
    private int[] buff_castling_rook_from_to = new int[2];

    ChessBoard() {
    }

    public static ChessBoard getInstance() {
        return instances[0];
    }

    public static ChessBoard getInstance(int instanceNumber) {
        return instances[instanceNumber];
    }

    public static ChessBoard getTestInstance() {
        return instances[1];
    }

    public static void initInstances(int numberOfInstances) {
        instances = new ChessBoard[numberOfInstances];
        for (int i = 0; i < numberOfInstances; ++i) {
            ChessBoard.instances[i] = new ChessBoard();
        }
    }

    public String toString() {
        return ChessBoardUtil.toString(this, true);
    }

    public boolean isDrawishByMaterial(int color) {
        return false;
    }

    public void changeSideToMove() {
        this.colorToMove = this.colorToMoveInverse;
        this.colorToMoveInverse = 1 - this.colorToMove;
    }

    public boolean isDiscoveredMove(int fromIndex) {
        return (this.discoveredPieces & 1L << fromIndex) != 0L;
    }

    private void pushHistoryValues(int move) {
        this.castlingHistory[this.moveCounter] = this.castlingRights;
        this.epIndexHistory[this.moveCounter] = this.epIndex;
        this.zobristKeyHistory[this.moveCounter] = this.zobristKey;
        this.pinnedPiecesHistory[this.moveCounter] = this.pinnedPieces;
        this.discoveredPiecesHistory[this.moveCounter] = this.discoveredPieces;
        this.checkingPiecesHistory[this.moveCounter] = this.checkingPieces;
        this.lastCaptureOrPawnMoveBeforeHistory[this.moveCounter] = this.lastCaptureOrPawnMoveBefore;
        ++this.moveCounter;
        this.playedMoves[this.playedMovesCount] = move;
        ++this.playedMovesCount;
    }

    private void popHistoryValues() {
        --this.playedMovesCount;
        --this.moveCounter;
        this.epIndex = this.epIndexHistory[this.moveCounter];
        this.zobristKey = this.zobristKeyHistory[this.moveCounter];
        this.castlingRights = this.castlingHistory[this.moveCounter];
        this.pinnedPieces = this.pinnedPiecesHistory[this.moveCounter];
        this.discoveredPieces = this.discoveredPiecesHistory[this.moveCounter];
        this.checkingPieces = this.checkingPiecesHistory[this.moveCounter];
        this.lastCaptureOrPawnMoveBefore = this.lastCaptureOrPawnMoveBeforeHistory[this.moveCounter];
    }

    public void doNullMove() {
        this.pushHistoryValues(0);
        this.zobristKey ^= Zobrist.sideToMove;
        if (this.epIndex != 0) {
            this.zobristKey ^= Zobrist.epIndex[this.epIndex];
            this.epIndex = 0;
        }
        this.changeSideToMove();
        this.playedBoardStates.inc(this.zobristKey);
    }

    public void undoNullMove() {
        this.playedBoardStates.dec(this.zobristKey);
        this.popHistoryValues();
        this.changeSideToMove();
    }

    public void doMove(int move) {
        if (MoveUtil.isCastlingMove(move)) {
            this.doCastling960(move);
            return;
        }
        int fromIndex = MoveUtil.getFromIndex(move);
        int toIndex = MoveUtil.getToIndex(move);
        long toMask = 1L << toIndex;
        long fromToMask = 1L << fromIndex ^ toMask;
        int sourcePieceIndex = MoveUtil.getSourcePieceIndex(move);
        int attackedPieceIndex = MoveUtil.getAttackedPieceIndex(move);
        if (fromIndex == toIndex) {
            throw new IllegalStateException("doMove: fromIndex == toIndex");
        }
        this.pushHistoryValues(move);
        this.lastCaptureOrPawnMoveBefore = attackedPieceIndex != 0 || sourcePieceIndex == 1 ? 0 : ++this.lastCaptureOrPawnMoveBefore;
        this.zobristKey ^= Zobrist.piece[fromIndex][this.colorToMove][sourcePieceIndex] ^ Zobrist.piece[toIndex][this.colorToMove][sourcePieceIndex] ^ Zobrist.sideToMove;
        if (this.epIndex != 0) {
            this.zobristKey ^= Zobrist.epIndex[this.epIndex];
            this.epIndex = 0;
        }
        int n = this.colorToMove;
        this.friendlyPieces[n] = this.friendlyPieces[n] ^ fromToMask;
        this.pieceIndexes[fromIndex] = 0;
        this.pieceIndexes[toIndex] = sourcePieceIndex;
        long[] lArray = this.pieces[this.colorToMove];
        int n2 = sourcePieceIndex;
        lArray[n2] = lArray[n2] ^ fromToMask;
        this.psqtScore_mg += EvalConstants.PSQT_MG[sourcePieceIndex][this.colorToMove][toIndex] - EvalConstants.PSQT_MG[sourcePieceIndex][this.colorToMove][fromIndex];
        this.psqtScore_eg += EvalConstants.PSQT_EG[sourcePieceIndex][this.colorToMove][toIndex] - EvalConstants.PSQT_EG[sourcePieceIndex][this.colorToMove][fromIndex];
        switch (sourcePieceIndex) {
            case 1: {
                this.pawnZobristKey ^= Zobrist.piece[fromIndex][this.colorToMove][1];
                if (MoveUtil.isPromotion(move)) {
                    if (this.colorToMove == 0) {
                        this.material_factor_white += EvalConstants.PHASE[MoveUtil.getMoveType(move)];
                    } else {
                        this.material_factor_black += EvalConstants.PHASE[MoveUtil.getMoveType(move)];
                    }
                    this.materialKey += MaterialUtil.VALUES[this.colorToMove][MoveUtil.getMoveType(move)] - MaterialUtil.VALUES[this.colorToMove][1];
                    long[] lArray2 = this.pieces[this.colorToMove];
                    lArray2[1] = lArray2[1] ^ toMask;
                    long[] lArray3 = this.pieces[this.colorToMove];
                    int n3 = MoveUtil.getMoveType(move);
                    lArray3[n3] = lArray3[n3] | toMask;
                    this.pieceIndexes[toIndex] = MoveUtil.getMoveType(move);
                    this.zobristKey ^= Zobrist.piece[toIndex][this.colorToMove][1] ^ Zobrist.piece[toIndex][this.colorToMove][MoveUtil.getMoveType(move)];
                    this.psqtScore_mg += EvalConstants.PSQT_MG[MoveUtil.getMoveType(move)][this.colorToMove][toIndex] - EvalConstants.PSQT_MG[1][this.colorToMove][toIndex];
                    this.psqtScore_eg += EvalConstants.PSQT_EG[MoveUtil.getMoveType(move)][this.colorToMove][toIndex] - EvalConstants.PSQT_EG[1][this.colorToMove][toIndex];
                    break;
                }
                this.pawnZobristKey ^= Zobrist.piece[toIndex][this.colorToMove][1];
                if (ChessConstants.IN_BETWEEN[fromIndex][toIndex] == 0L || (StaticMoves.PAWN_ATTACKS[this.colorToMove][Long.numberOfTrailingZeros(ChessConstants.IN_BETWEEN[fromIndex][toIndex])] & this.pieces[this.colorToMoveInverse][1]) == 0L) break;
                this.epIndex = Long.numberOfTrailingZeros(ChessConstants.IN_BETWEEN[fromIndex][toIndex]);
                this.zobristKey ^= Zobrist.epIndex[this.epIndex];
                break;
            }
            case 4: {
                if (this.castlingRights == 0) break;
                this.zobristKey ^= Zobrist.castling[this.castlingRights];
                this.castlingRights = CastlingUtil.getRookMovedOrAttackedCastlingRights(this.castlingRights, fromIndex, this.castlingConfig);
                this.zobristKey ^= Zobrist.castling[this.castlingRights];
                break;
            }
            case 6: {
                this.updateKingValues(this.colorToMove, toIndex);
                if (this.castlingRights == 0) break;
                if (MoveUtil.isCastlingMove(move)) {
                    throw new IllegalStateException("Castling");
                }
                this.zobristKey ^= Zobrist.castling[this.castlingRights];
                this.castlingRights = CastlingUtil.getKingMovedCastlingRights(this.castlingRights, this.colorToMove, this.castlingConfig);
                this.zobristKey ^= Zobrist.castling[this.castlingRights];
            }
        }
        switch (attackedPieceIndex) {
            case 0: {
                break;
            }
            case 1: {
                if (MoveUtil.isEPMove(move)) {
                    toMask = Util.POWER_LOOKUP[toIndex += ChessConstants.COLOR_FACTOR_8[this.colorToMoveInverse]];
                    this.pieceIndexes[toIndex] = 0;
                }
                this.pawnZobristKey ^= Zobrist.piece[toIndex][this.colorToMoveInverse][1];
                this.psqtScore_mg -= EvalConstants.PSQT_MG[1][this.colorToMoveInverse][toIndex];
                this.psqtScore_eg -= EvalConstants.PSQT_EG[1][this.colorToMoveInverse][toIndex];
                int n4 = this.colorToMoveInverse;
                this.friendlyPieces[n4] = this.friendlyPieces[n4] ^ toMask;
                long[] lArray4 = this.pieces[this.colorToMoveInverse];
                lArray4[1] = lArray4[1] ^ toMask;
                this.zobristKey ^= Zobrist.piece[toIndex][this.colorToMoveInverse][1];
                this.materialKey -= MaterialUtil.VALUES[this.colorToMoveInverse][1];
                break;
            }
            case 4: {
                if (this.castlingRights != 0) {
                    this.zobristKey ^= Zobrist.castling[this.castlingRights];
                    this.castlingRights = CastlingUtil.getRookMovedOrAttackedCastlingRights(this.castlingRights, toIndex, this.castlingConfig);
                    this.zobristKey ^= Zobrist.castling[this.castlingRights];
                }
            }
            default: {
                if (this.colorToMoveInverse == 0) {
                    this.material_factor_white -= EvalConstants.PHASE[attackedPieceIndex];
                } else {
                    this.material_factor_black -= EvalConstants.PHASE[attackedPieceIndex];
                }
                this.psqtScore_mg -= EvalConstants.PSQT_MG[attackedPieceIndex][this.colorToMoveInverse][toIndex];
                this.psqtScore_eg -= EvalConstants.PSQT_EG[attackedPieceIndex][this.colorToMoveInverse][toIndex];
                int n5 = this.colorToMoveInverse;
                this.friendlyPieces[n5] = this.friendlyPieces[n5] ^ toMask;
                long[] lArray5 = this.pieces[this.colorToMoveInverse];
                int n6 = attackedPieceIndex;
                lArray5[n6] = lArray5[n6] ^ toMask;
                this.zobristKey ^= Zobrist.piece[toIndex][this.colorToMoveInverse][attackedPieceIndex];
                this.materialKey -= MaterialUtil.VALUES[this.colorToMoveInverse][attackedPieceIndex];
            }
        }
        this.allPieces = this.friendlyPieces[this.colorToMove] | this.friendlyPieces[this.colorToMoveInverse];
        this.emptySpaces = this.allPieces ^ 0xFFFFFFFFFFFFFFFFL;
        this.changeSideToMove();
        this.checkingPieces = this.isDiscoveredMove(fromIndex) ? CheckUtil.getCheckingPieces(this) : (MoveUtil.isNormalMove(move) ? CheckUtil.getCheckingPieces(this, sourcePieceIndex) : CheckUtil.getCheckingPieces(this));
        this.setPinnedAndDiscoPieces();
        this.playedBoardStates.inc(this.zobristKey);
    }

    private void doCastling960(int move) {
        this.pushHistoryValues(move);
        int fromIndex_king = MoveUtil.getFromIndex(move);
        int toIndex_king = MoveUtil.getToIndex(move);
        int sourcePieceIndex = MoveUtil.getSourcePieceIndex(move);
        if (sourcePieceIndex != 6 || !MoveUtil.isCastlingMove(move)) {
            throw new IllegalStateException("sourcePieceIndex != KING || !MoveUtil.isCastlingMove(move)");
        }
        CastlingUtil.getRookFromToSquareIDs(this, toIndex_king, this.buff_castling_rook_from_to);
        int fromIndex_rook = this.buff_castling_rook_from_to[0];
        int toIndex_rook = this.buff_castling_rook_from_to[1];
        if (fromIndex_king == toIndex_king) {
            long bb = Util.POWER_LOOKUP[fromIndex_rook] | Util.POWER_LOOKUP[toIndex_rook];
            long[] lArray = this.pieces[this.colorToMove];
            lArray[4] = lArray[4] ^ bb;
            int n = this.colorToMove;
            this.friendlyPieces[n] = this.friendlyPieces[n] ^ bb;
            this.pieceIndexes[fromIndex_rook] = 0;
            this.pieceIndexes[toIndex_rook] = 4;
        } else if (fromIndex_rook == toIndex_rook) {
            long bb = Util.POWER_LOOKUP[fromIndex_king] | Util.POWER_LOOKUP[toIndex_king];
            long[] lArray = this.pieces[this.colorToMove];
            lArray[6] = lArray[6] ^ bb;
            int n = this.colorToMove;
            this.friendlyPieces[n] = this.friendlyPieces[n] ^ bb;
            this.pieceIndexes[fromIndex_king] = 0;
            this.pieceIndexes[toIndex_king] = 6;
        } else if (fromIndex_rook == toIndex_king && toIndex_rook == fromIndex_king) {
            long bb_king = Util.POWER_LOOKUP[fromIndex_king] | Util.POWER_LOOKUP[toIndex_king];
            long[] lArray = this.pieces[this.colorToMove];
            lArray[6] = lArray[6] ^ bb_king;
            long bb_rook = Util.POWER_LOOKUP[fromIndex_rook] | Util.POWER_LOOKUP[toIndex_rook];
            long[] lArray2 = this.pieces[this.colorToMove];
            lArray2[4] = lArray2[4] ^ bb_rook;
            this.pieceIndexes[toIndex_rook] = 4;
            this.pieceIndexes[toIndex_king] = 6;
        } else if (fromIndex_rook == toIndex_king) {
            long bb_king = Util.POWER_LOOKUP[fromIndex_king] | Util.POWER_LOOKUP[toIndex_king];
            long[] lArray = this.pieces[this.colorToMove];
            lArray[6] = lArray[6] ^ bb_king;
            long bb_rook = Util.POWER_LOOKUP[fromIndex_rook] | Util.POWER_LOOKUP[toIndex_rook];
            long[] lArray3 = this.pieces[this.colorToMove];
            lArray3[4] = lArray3[4] ^ bb_rook;
            int n = this.colorToMove;
            this.friendlyPieces[n] = this.friendlyPieces[n] ^ (Util.POWER_LOOKUP[fromIndex_king] | Util.POWER_LOOKUP[toIndex_rook]);
            this.pieceIndexes[toIndex_rook] = 4;
            this.pieceIndexes[toIndex_king] = 6;
            this.pieceIndexes[fromIndex_king] = 0;
        } else if (toIndex_rook == fromIndex_king) {
            long bb_king = Util.POWER_LOOKUP[fromIndex_king] | Util.POWER_LOOKUP[toIndex_king];
            long[] lArray = this.pieces[this.colorToMove];
            lArray[6] = lArray[6] ^ bb_king;
            long bb_rook = Util.POWER_LOOKUP[fromIndex_rook] | Util.POWER_LOOKUP[toIndex_rook];
            long[] lArray4 = this.pieces[this.colorToMove];
            lArray4[4] = lArray4[4] ^ bb_rook;
            int n = this.colorToMove;
            this.friendlyPieces[n] = this.friendlyPieces[n] ^ (Util.POWER_LOOKUP[toIndex_king] | Util.POWER_LOOKUP[fromIndex_rook]);
            this.pieceIndexes[toIndex_rook] = 4;
            this.pieceIndexes[toIndex_king] = 6;
            this.pieceIndexes[fromIndex_rook] = 0;
        } else {
            long bb_king = Util.POWER_LOOKUP[fromIndex_king] | Util.POWER_LOOKUP[toIndex_king];
            long[] lArray = this.pieces[this.colorToMove];
            lArray[6] = lArray[6] ^ bb_king;
            int n = this.colorToMove;
            this.friendlyPieces[n] = this.friendlyPieces[n] ^ bb_king;
            long bb_rook = Util.POWER_LOOKUP[fromIndex_rook] | Util.POWER_LOOKUP[toIndex_rook];
            long[] lArray5 = this.pieces[this.colorToMove];
            lArray5[4] = lArray5[4] ^ bb_rook;
            int n2 = this.colorToMove;
            this.friendlyPieces[n2] = this.friendlyPieces[n2] ^ bb_rook;
            this.pieceIndexes[fromIndex_rook] = 0;
            this.pieceIndexes[fromIndex_king] = 0;
            this.pieceIndexes[toIndex_rook] = 4;
            this.pieceIndexes[toIndex_king] = 6;
        }
        this.updateKingValues(this.colorToMove, toIndex_king);
        this.zobristKey ^= Zobrist.piece[fromIndex_king][this.colorToMove][6] ^ Zobrist.piece[toIndex_king][this.colorToMove][6];
        this.zobristKey ^= Zobrist.piece[fromIndex_rook][this.colorToMove][4] ^ Zobrist.piece[toIndex_rook][this.colorToMove][4];
        if (this.epIndex != 0) {
            this.zobristKey ^= Zobrist.epIndex[this.epIndex];
            this.epIndex = 0;
        }
        this.zobristKey ^= Zobrist.sideToMove;
        if (this.castlingRights != 0) {
            this.zobristKey ^= Zobrist.castling[this.castlingRights];
            this.castlingRights = CastlingUtil.getKingMovedCastlingRights(this.castlingRights, this.colorToMove, this.castlingConfig);
            this.zobristKey ^= Zobrist.castling[this.castlingRights];
        }
        this.psqtScore_mg += EvalConstants.PSQT_MG[6][this.colorToMove][toIndex_king] - EvalConstants.PSQT_MG[6][this.colorToMove][fromIndex_king];
        this.psqtScore_eg += EvalConstants.PSQT_EG[6][this.colorToMove][toIndex_king] - EvalConstants.PSQT_EG[6][this.colorToMove][fromIndex_king];
        this.psqtScore_mg += EvalConstants.PSQT_MG[4][this.colorToMove][toIndex_rook] - EvalConstants.PSQT_MG[4][this.colorToMove][fromIndex_rook];
        this.psqtScore_eg += EvalConstants.PSQT_EG[4][this.colorToMove][toIndex_rook] - EvalConstants.PSQT_EG[4][this.colorToMove][fromIndex_rook];
        this.allPieces = this.friendlyPieces[this.colorToMove] | this.friendlyPieces[this.colorToMoveInverse];
        this.emptySpaces = this.allPieces ^ 0xFFFFFFFFFFFFFFFFL;
        this.changeSideToMove();
        this.checkingPieces = CheckUtil.getCheckingPieces(this);
        this.setPinnedAndDiscoPieces();
        this.playedBoardStates.inc(this.zobristKey);
    }

    public void undoMove(int move) {
        if (MoveUtil.isCastlingMove(move)) {
            this.undoCastling960(move);
            return;
        }
        this.playedBoardStates.dec(this.zobristKey);
        int fromIndex = MoveUtil.getFromIndex(move);
        int toIndex = MoveUtil.getToIndex(move);
        long toMask = 1L << toIndex;
        long fromToMask = 1L << fromIndex ^ toMask;
        int sourcePieceIndex = MoveUtil.getSourcePieceIndex(move);
        int attackedPieceIndex = MoveUtil.getAttackedPieceIndex(move);
        if (fromIndex == toIndex) {
            throw new IllegalStateException("undoMove: fromIndex == toIndex");
        }
        this.popHistoryValues();
        int n = this.colorToMoveInverse;
        this.friendlyPieces[n] = this.friendlyPieces[n] ^ fromToMask;
        this.pieceIndexes[fromIndex] = sourcePieceIndex;
        long[] lArray = this.pieces[this.colorToMoveInverse];
        int n2 = sourcePieceIndex;
        lArray[n2] = lArray[n2] ^ fromToMask;
        this.psqtScore_mg += EvalConstants.PSQT_MG[sourcePieceIndex][this.colorToMoveInverse][fromIndex] - EvalConstants.PSQT_MG[sourcePieceIndex][this.colorToMoveInverse][toIndex];
        this.psqtScore_eg += EvalConstants.PSQT_EG[sourcePieceIndex][this.colorToMoveInverse][fromIndex] - EvalConstants.PSQT_EG[sourcePieceIndex][this.colorToMoveInverse][toIndex];
        switch (sourcePieceIndex) {
            case 0: {
                break;
            }
            case 1: {
                this.pawnZobristKey ^= Zobrist.piece[fromIndex][this.colorToMoveInverse][1];
                if (MoveUtil.isPromotion(move)) {
                    if (this.colorToMoveInverse == 0) {
                        this.material_factor_white -= EvalConstants.PHASE[MoveUtil.getMoveType(move)];
                    } else {
                        this.material_factor_black -= EvalConstants.PHASE[MoveUtil.getMoveType(move)];
                    }
                    this.materialKey -= MaterialUtil.VALUES[this.colorToMoveInverse][MoveUtil.getMoveType(move)] - MaterialUtil.VALUES[this.colorToMoveInverse][1];
                    long[] lArray2 = this.pieces[this.colorToMoveInverse];
                    lArray2[1] = lArray2[1] ^ toMask;
                    long[] lArray3 = this.pieces[this.colorToMoveInverse];
                    int n3 = MoveUtil.getMoveType(move);
                    lArray3[n3] = lArray3[n3] ^ toMask;
                    this.psqtScore_mg += EvalConstants.PSQT_MG[1][this.colorToMoveInverse][toIndex] - EvalConstants.PSQT_MG[MoveUtil.getMoveType(move)][this.colorToMoveInverse][toIndex];
                    this.psqtScore_eg += EvalConstants.PSQT_EG[1][this.colorToMoveInverse][toIndex] - EvalConstants.PSQT_EG[MoveUtil.getMoveType(move)][this.colorToMoveInverse][toIndex];
                    break;
                }
                this.pawnZobristKey ^= Zobrist.piece[toIndex][this.colorToMoveInverse][1];
                break;
            }
            case 6: {
                if (MoveUtil.isCastlingMove(move)) {
                    throw new IllegalStateException("Castling");
                }
                this.updateKingValues(this.colorToMoveInverse, fromIndex);
            }
        }
        switch (attackedPieceIndex) {
            case 0: {
                break;
            }
            case 1: {
                if (MoveUtil.isEPMove(move)) {
                    this.pieceIndexes[toIndex] = 0;
                    toMask = Util.POWER_LOOKUP[toIndex += ChessConstants.COLOR_FACTOR_8[this.colorToMove]];
                }
                this.psqtScore_mg += EvalConstants.PSQT_MG[1][this.colorToMove][toIndex];
                this.psqtScore_eg += EvalConstants.PSQT_EG[1][this.colorToMove][toIndex];
                this.pawnZobristKey ^= Zobrist.piece[toIndex][this.colorToMove][1];
                long[] lArray4 = this.pieces[this.colorToMove];
                int n4 = attackedPieceIndex;
                lArray4[n4] = lArray4[n4] | toMask;
                int n5 = this.colorToMove;
                this.friendlyPieces[n5] = this.friendlyPieces[n5] | toMask;
                this.materialKey += MaterialUtil.VALUES[this.colorToMove][1];
                break;
            }
            default: {
                this.psqtScore_mg += EvalConstants.PSQT_MG[attackedPieceIndex][this.colorToMove][toIndex];
                this.psqtScore_eg += EvalConstants.PSQT_EG[attackedPieceIndex][this.colorToMove][toIndex];
                if (this.colorToMove == 0) {
                    this.material_factor_white += EvalConstants.PHASE[attackedPieceIndex];
                } else {
                    this.material_factor_black += EvalConstants.PHASE[attackedPieceIndex];
                }
                this.materialKey += MaterialUtil.VALUES[this.colorToMove][attackedPieceIndex];
                long[] lArray5 = this.pieces[this.colorToMove];
                int n6 = attackedPieceIndex;
                lArray5[n6] = lArray5[n6] | toMask;
                int n7 = this.colorToMove;
                this.friendlyPieces[n7] = this.friendlyPieces[n7] | toMask;
            }
        }
        this.pieceIndexes[toIndex] = attackedPieceIndex;
        this.allPieces = this.friendlyPieces[this.colorToMove] | this.friendlyPieces[this.colorToMoveInverse];
        this.emptySpaces = this.allPieces ^ 0xFFFFFFFFFFFFFFFFL;
        this.changeSideToMove();
    }

    public void undoCastling960(int move) {
        this.playedBoardStates.dec(this.zobristKey);
        this.popHistoryValues();
        int fromIndex_king = MoveUtil.getFromIndex(move);
        int toIndex_king = MoveUtil.getToIndex(move);
        int sourcePieceIndex = MoveUtil.getSourcePieceIndex(move);
        if (sourcePieceIndex != 6 || !MoveUtil.isCastlingMove(move)) {
            throw new IllegalStateException("sourcePieceIndex != KING || !MoveUtil.isCastlingMove(move)");
        }
        CastlingUtil.getRookFromToSquareIDs(this, toIndex_king, this.buff_castling_rook_from_to);
        int fromIndex_rook = this.buff_castling_rook_from_to[0];
        int toIndex_rook = this.buff_castling_rook_from_to[1];
        if (fromIndex_king == toIndex_king) {
            long bb = Util.POWER_LOOKUP[fromIndex_rook] | Util.POWER_LOOKUP[toIndex_rook];
            long[] lArray = this.pieces[this.colorToMoveInverse];
            lArray[4] = lArray[4] ^ bb;
            int n = this.colorToMoveInverse;
            this.friendlyPieces[n] = this.friendlyPieces[n] ^ bb;
            this.pieceIndexes[fromIndex_rook] = 4;
            this.pieceIndexes[toIndex_rook] = 0;
        } else if (fromIndex_rook == toIndex_rook) {
            long bb = Util.POWER_LOOKUP[fromIndex_king] | Util.POWER_LOOKUP[toIndex_king];
            long[] lArray = this.pieces[this.colorToMoveInverse];
            lArray[6] = lArray[6] ^ bb;
            int n = this.colorToMoveInverse;
            this.friendlyPieces[n] = this.friendlyPieces[n] ^ bb;
            this.pieceIndexes[fromIndex_king] = 6;
            this.pieceIndexes[toIndex_king] = 0;
        } else if (fromIndex_rook == toIndex_king && toIndex_rook == fromIndex_king) {
            long bb_king = Util.POWER_LOOKUP[fromIndex_king] | Util.POWER_LOOKUP[toIndex_king];
            long[] lArray = this.pieces[this.colorToMoveInverse];
            lArray[6] = lArray[6] ^ bb_king;
            long bb_rook = Util.POWER_LOOKUP[fromIndex_rook] | Util.POWER_LOOKUP[toIndex_rook];
            long[] lArray2 = this.pieces[this.colorToMoveInverse];
            lArray2[4] = lArray2[4] ^ bb_rook;
            this.pieceIndexes[toIndex_rook] = 6;
            this.pieceIndexes[toIndex_king] = 4;
        } else if (fromIndex_rook == toIndex_king) {
            long bb_king = Util.POWER_LOOKUP[fromIndex_king] | Util.POWER_LOOKUP[toIndex_king];
            long[] lArray = this.pieces[this.colorToMoveInverse];
            lArray[6] = lArray[6] ^ bb_king;
            long bb_rook = Util.POWER_LOOKUP[fromIndex_rook] | Util.POWER_LOOKUP[toIndex_rook];
            long[] lArray3 = this.pieces[this.colorToMoveInverse];
            lArray3[4] = lArray3[4] ^ bb_rook;
            int n = this.colorToMoveInverse;
            this.friendlyPieces[n] = this.friendlyPieces[n] ^ (Util.POWER_LOOKUP[fromIndex_king] | Util.POWER_LOOKUP[toIndex_rook]);
            this.pieceIndexes[toIndex_rook] = 0;
            this.pieceIndexes[toIndex_king] = 4;
            this.pieceIndexes[fromIndex_king] = 6;
        } else if (toIndex_rook == fromIndex_king) {
            long bb_king = Util.POWER_LOOKUP[fromIndex_king] | Util.POWER_LOOKUP[toIndex_king];
            long[] lArray = this.pieces[this.colorToMoveInverse];
            lArray[6] = lArray[6] ^ bb_king;
            long bb_rook = Util.POWER_LOOKUP[fromIndex_rook] | Util.POWER_LOOKUP[toIndex_rook];
            long[] lArray4 = this.pieces[this.colorToMoveInverse];
            lArray4[4] = lArray4[4] ^ bb_rook;
            int n = this.colorToMoveInverse;
            this.friendlyPieces[n] = this.friendlyPieces[n] ^ (Util.POWER_LOOKUP[toIndex_king] | Util.POWER_LOOKUP[fromIndex_rook]);
            this.pieceIndexes[toIndex_rook] = 6;
            this.pieceIndexes[toIndex_king] = 0;
            this.pieceIndexes[fromIndex_rook] = 4;
        } else {
            long bb_king = Util.POWER_LOOKUP[fromIndex_king] | Util.POWER_LOOKUP[toIndex_king];
            long[] lArray = this.pieces[this.colorToMoveInverse];
            lArray[6] = lArray[6] ^ bb_king;
            int n = this.colorToMoveInverse;
            this.friendlyPieces[n] = this.friendlyPieces[n] ^ bb_king;
            long bb_rook = Util.POWER_LOOKUP[fromIndex_rook] | Util.POWER_LOOKUP[toIndex_rook];
            long[] lArray5 = this.pieces[this.colorToMoveInverse];
            lArray5[4] = lArray5[4] ^ bb_rook;
            int n2 = this.colorToMoveInverse;
            this.friendlyPieces[n2] = this.friendlyPieces[n2] ^ bb_rook;
            this.pieceIndexes[fromIndex_rook] = 4;
            this.pieceIndexes[fromIndex_king] = 6;
            this.pieceIndexes[toIndex_rook] = 0;
            this.pieceIndexes[toIndex_king] = 0;
        }
        this.updateKingValues(this.colorToMoveInverse, fromIndex_king);
        this.psqtScore_mg += EvalConstants.PSQT_MG[6][this.colorToMoveInverse][fromIndex_king] - EvalConstants.PSQT_MG[6][this.colorToMoveInverse][toIndex_king];
        this.psqtScore_eg += EvalConstants.PSQT_EG[6][this.colorToMoveInverse][fromIndex_king] - EvalConstants.PSQT_EG[6][this.colorToMoveInverse][toIndex_king];
        this.psqtScore_mg += EvalConstants.PSQT_MG[4][this.colorToMoveInverse][fromIndex_rook] - EvalConstants.PSQT_MG[4][this.colorToMoveInverse][toIndex_rook];
        this.psqtScore_eg += EvalConstants.PSQT_EG[4][this.colorToMoveInverse][fromIndex_rook] - EvalConstants.PSQT_EG[4][this.colorToMoveInverse][toIndex_rook];
        this.allPieces = this.friendlyPieces[this.colorToMove] | this.friendlyPieces[this.colorToMoveInverse];
        this.emptySpaces = this.allPieces ^ 0xFFFFFFFFFFFFFFFFL;
        this.changeSideToMove();
    }

    public void setPinnedAndDiscoPieces() {
        this.pinnedPieces = 0L;
        this.discoveredPieces = 0L;
        for (int kingColor = 0; kingColor <= 1; ++kingColor) {
            int enemyColor = 1 - kingColor;
            if (!MaterialUtil.hasSlidingPieces(this.materialKey, enemyColor)) continue;
            for (long enemyPiece = (this.pieces[enemyColor][3] | this.pieces[enemyColor][5]) & MagicUtil.getBishopMovesEmptyBoard(this.kingIndex[kingColor]) | (this.pieces[enemyColor][4] | this.pieces[enemyColor][5]) & MagicUtil.getRookMovesEmptyBoard(this.kingIndex[kingColor]); enemyPiece != 0L; enemyPiece &= enemyPiece - 1L) {
                long checkedPiece = ChessConstants.IN_BETWEEN[this.kingIndex[kingColor]][Long.numberOfTrailingZeros(enemyPiece)] & this.allPieces;
                if (Long.bitCount(checkedPiece) != 1) continue;
                this.pinnedPieces |= checkedPiece & this.friendlyPieces[kingColor];
                this.discoveredPieces |= checkedPiece & this.friendlyPieces[enemyColor];
            }
        }
    }

    public void updateKingValues(int kingColor, int index) {
        if (index == 64) {
            return;
        }
        this.kingIndex[kingColor] = index;
        this.kingArea[kingColor] = ChessConstants.KING_AREA[kingColor][index];
    }

    public boolean isLegal(int move) {
        if (MoveUtil.getSourcePieceIndex(move) == 6) {
            return !CheckUtil.isInCheckIncludingKing(MoveUtil.getToIndex(move), this.colorToMove, this.pieces[this.colorToMoveInverse], this.allPieces ^ Util.POWER_LOOKUP[MoveUtil.getFromIndex(move)], MaterialUtil.getMajorPieces(this.materialKey, this.colorToMoveInverse));
        }
        if (MoveUtil.getAttackedPieceIndex(move) != 0) {
            if (MoveUtil.isEPMove(move)) {
                return this.isLegalEPMove(MoveUtil.getFromIndex(move));
            }
            return true;
        }
        if (this.checkingPieces != 0L) {
            return !CheckUtil.isInCheck(this.kingIndex[this.colorToMove], this.colorToMove, this.pieces[this.colorToMoveInverse], this.allPieces ^ Util.POWER_LOOKUP[MoveUtil.getFromIndex(move)] ^ Util.POWER_LOOKUP[MoveUtil.getToIndex(move)]);
        }
        return true;
    }

    private boolean isLegalEPMove(int fromIndex) {
        long fromToMask = Util.POWER_LOOKUP[fromIndex] ^ Util.POWER_LOOKUP[this.epIndex];
        int n = this.colorToMove;
        this.friendlyPieces[n] = this.friendlyPieces[n] ^ fromToMask;
        long[] lArray = this.pieces[this.colorToMoveInverse];
        lArray[1] = lArray[1] ^ Util.POWER_LOOKUP[this.epIndex + ChessConstants.COLOR_FACTOR_8[this.colorToMoveInverse]];
        this.allPieces = this.friendlyPieces[this.colorToMove] | this.friendlyPieces[this.colorToMoveInverse] ^ Util.POWER_LOOKUP[this.epIndex + ChessConstants.COLOR_FACTOR_8[this.colorToMoveInverse]];
        boolean isInCheck = CheckUtil.getCheckingPieces(this) != 0L;
        int n2 = this.colorToMove;
        this.friendlyPieces[n2] = this.friendlyPieces[n2] ^ fromToMask;
        long[] lArray2 = this.pieces[this.colorToMoveInverse];
        lArray2[1] = lArray2[1] | Util.POWER_LOOKUP[this.epIndex + ChessConstants.COLOR_FACTOR_8[this.colorToMoveInverse]];
        this.allPieces = this.friendlyPieces[this.colorToMove] | this.friendlyPieces[this.colorToMoveInverse];
        return !isInCheck;
    }

    public boolean isValidMove(int move) {
        int fromIndex = MoveUtil.getFromIndex(move);
        long fromSquare = Util.POWER_LOOKUP[fromIndex];
        if ((this.pieces[this.colorToMove][MoveUtil.getSourcePieceIndex(move)] & fromSquare) == 0L) {
            return false;
        }
        int toIndex = MoveUtil.getToIndex(move);
        long toSquare = Util.POWER_LOOKUP[toIndex];
        int attackedPieceIndex = MoveUtil.getAttackedPieceIndex(move);
        if (attackedPieceIndex == 0 ? (MoveUtil.isCastlingMove(move) ? this.pieceIndexes[toIndex] != 0 && this.pieceIndexes[toIndex] != 4 && this.pieceIndexes[toIndex] != 6 : this.pieceIndexes[toIndex] != 0) : (this.pieces[this.colorToMoveInverse][attackedPieceIndex] & toSquare) == 0L && !MoveUtil.isEPMove(move)) {
            return false;
        }
        switch (MoveUtil.getSourcePieceIndex(move)) {
            case 1: {
                if (MoveUtil.isEPMove(move)) {
                    if (toIndex != this.epIndex) {
                        return false;
                    }
                    return this.isLegalEPMove(fromIndex);
                }
                if (this.colorToMove == 0) {
                    if (fromIndex > toIndex) {
                        return false;
                    }
                    if (toIndex - fromIndex != 16 || (this.allPieces & Util.POWER_LOOKUP[fromIndex + 8]) == 0L) break;
                    return false;
                }
                if (fromIndex < toIndex) {
                    return false;
                }
                if (fromIndex - toIndex != 16 || (this.allPieces & Util.POWER_LOOKUP[fromIndex - 8]) == 0L) break;
                return false;
            }
            case 2: {
                break;
            }
            case 3: 
            case 4: 
            case 5: {
                if ((ChessConstants.IN_BETWEEN[fromIndex][toIndex] & this.allPieces) == 0L) break;
                return false;
            }
            case 6: {
                if (MoveUtil.isCastlingMove(move)) {
                    for (long castlingIndexes = CastlingUtil.getCastlingIndexes(this.colorToMove, this.castlingRights, this.castlingConfig); castlingIndexes != 0L; castlingIndexes &= castlingIndexes - 1L) {
                        if (toIndex != Long.numberOfTrailingZeros(castlingIndexes)) continue;
                        return CastlingUtil.isValidCastlingMove(this, fromIndex, toIndex);
                    }
                    return false;
                }
                return this.isLegalKingMove(move);
            }
        }
        if ((fromSquare & this.pinnedPieces) != 0L && (ChessConstants.PINNED_MOVEMENT[fromIndex][this.kingIndex[this.colorToMove]] & toSquare) == 0L) {
            return false;
        }
        if (this.checkingPieces != 0L) {
            if (attackedPieceIndex == 0) {
                return this.isLegalNonKingMove(move);
            }
            if (Long.bitCount(this.checkingPieces) >= 2) {
                return false;
            }
            return (toSquare & this.checkingPieces) != 0L;
        }
        return true;
    }

    private boolean isLegalKingMove(int move) {
        return !CheckUtil.isInCheckIncludingKing(MoveUtil.getToIndex(move), this.colorToMove, this.pieces[this.colorToMoveInverse], this.allPieces ^ Util.POWER_LOOKUP[MoveUtil.getFromIndex(move)]);
    }

    private boolean isLegalNonKingMove(int move) {
        return !CheckUtil.isInCheck(this.kingIndex[this.colorToMove], this.colorToMove, this.pieces[this.colorToMoveInverse], this.allPieces ^ Util.POWER_LOOKUP[MoveUtil.getFromIndex(move)] ^ Util.POWER_LOOKUP[MoveUtil.getToIndex(move)]);
    }

    public int getRepetition() {
        int count = this.playedBoardStates.get(this.zobristKey);
        if (count == -1) {
            return 0;
        }
        return count;
    }

    static {
        ChessBoard.initInstances(0);
    }
}

