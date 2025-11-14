/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.dummy;

import bagaturchess.bitboard.api.IBaseEval;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IBoard;
import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.api.IFieldsAttacks;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.bitboard.api.IInternalMoveList;
import bagaturchess.bitboard.api.IMaterialFactor;
import bagaturchess.bitboard.api.IMaterialState;
import bagaturchess.bitboard.api.IMobility;
import bagaturchess.bitboard.api.IMoveIterator;
import bagaturchess.bitboard.api.IMoveOps;
import bagaturchess.bitboard.api.IPiecesLists;
import bagaturchess.bitboard.api.IPlayerAttacks;
import bagaturchess.bitboard.api.PawnsEvalCache;
import bagaturchess.bitboard.common.BackupInfo;
import bagaturchess.bitboard.common.BoardStat;
import bagaturchess.bitboard.common.MoveListener;
import bagaturchess.bitboard.impl.attacks.SEE;
import bagaturchess.bitboard.impl.dummy.DummyMaterial;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEval;
import bagaturchess.bitboard.impl1.internal.CastlingConfig;

public class DummyBoard
implements IBitBoard {
    private DummyMaterial material = new DummyMaterial();
    boolean hasCaptures = false;
    int colourToMove = 0;
    static long hash = 0L;
    static long pawnshash = 0L;

    public int gen2MovesPromotions(int colour, long[][] moves) {
        throw new UnsupportedOperationException();
    }

    public int genAllCheckMoves(int colour, long[][] moves) {
        return this.dummyMoveGen(5, moves);
    }

    public int genAllMoves(int colour, long[][] moves) {
        return this.dummyMoveGen(35, moves);
    }

    public int genAllMoves(int colour, long[][] moves, boolean checkKeepersAware) {
        return this.dummyMoveGen(35, moves);
    }

    public int genAllMoves_ByFigureID(int figureID, long excludedToFields, long[][] moves) {
        throw new UnsupportedOperationException();
    }

    public int genCapturePromotionCheckMoves(int colour, long[][] moves) {
        throw new UnsupportedOperationException();
    }

    public int genDirectCheckMoves(int colour, long[][] moves) {
        throw new UnsupportedOperationException();
    }

    public int genHiddenCheckMoves(int colour, long[][] moves) {
        throw new UnsupportedOperationException();
    }

    public int genCapturePromotionMoves(int colour, long[][] moves) {
        if (this.hasCaptures) {
            this.hasCaptures = false;
            return this.dummyMoveGen(5, moves);
        }
        this.hasCaptures = true;
        return 0;
    }

    public int genKingEscapes(int colour, long[][] moves) {
        return this.dummyMoveGen(5, moves);
    }

    public int genNonCaptureNonPromotionMoves(int colour, long[][] moves) {
        return this.dummyMoveGen(10, moves);
    }

    private int dummyMoveGen(int count, long[][] moves) {
        for (int i = 0; i < count; ++i) {
            double rand = Math.random();
            long[] move = moves[i];
            move[27] = i;
            move[28] = (long)(1000.0 * rand);
            move[9] = (long)(63.0 * rand);
            move[10] = 63L - move[9];
        }
        return count;
    }

    public int genPromotions(int colour, long[][] moves) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getAttacksSupport() {
        return true;
    }

    @Override
    public int getEnpassantSquareID() {
        throw new UnsupportedOperationException();
    }

    public int getChecksCount(int colour) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getColourToMove() {
        return this.colourToMove;
    }

    public int getFieldID(int figureID) {
        throw new UnsupportedOperationException();
    }

    public long getFigureBitboardByID(int figureID) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getFigureID(int fieldID) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getFiguresBitboardByColour(int colour) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getFiguresBitboardByColourAndType(int colour, int type) {
        return hash;
    }

    @Override
    public long getFreeBitboard() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getHashKey() {
        return hash++;
    }

    public long getHashKeyAfterMove(long[] move) {
        return this.getHashKey();
    }

    @Override
    public int getLastMove() {
        return 0;
    }

    @Override
    public long getPawnsHashKey() {
        return pawnshash++;
    }

    @Override
    public int[] getPlayedMoves() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getPlayedMovesCount() {
        return 30;
    }

    @Override
    public IPlayerAttacks getPlayerAttacks(int colour) {
        return null;
    }

    @Override
    public IPiecesLists getPiecesLists() {
        return null;
    }

    @Override
    public int getStateRepetition() {
        return 0;
    }

    public int getStateRepetition(long hashkey) {
        return 0;
    }

    public BoardStat getStatistics() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IGameStatus getStatus() {
        throw new UnsupportedOperationException();
    }

    public boolean has2MovePromotions(int colour) {
        throw new UnsupportedOperationException();
    }

    public boolean hasCapturePromotionCheck(int colour) {
        return true;
    }

    public boolean hasChecks(int colour) {
        throw new UnsupportedOperationException();
    }

    public boolean hasMove(int colour) {
        throw new UnsupportedOperationException();
    }

    public boolean hasMoveInCheck(int colour) {
        return true;
    }

    public boolean hasMoveInNonCheck(int colour) {
        return true;
    }

    public boolean hasPromotions(int colour) {
        return false;
    }

    @Override
    public boolean hasRightsToKingCastle(int colour) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasRightsToQueenCastle(int colour) {
        throw new UnsupportedOperationException();
    }

    public boolean hasSingleMove(int colour) {
        return false;
    }

    @Override
    public boolean hasSufficientMatingMaterial(int color) {
        throw new UnsupportedOperationException();
    }

    public boolean isCheckMove(long[] move) {
        return false;
    }

    public boolean isDirectCheckMove(long[] move) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isInCheck(int colour) {
        return false;
    }

    public boolean isPossible(long[] move) {
        return false;
    }

    public IMoveIterator iterator(int iteratorFactoryHandler) {
        throw new UnsupportedOperationException();
    }

    public void makeMoveForward(long[] move) {
        this.switchColours();
    }

    public void makeMoveBackward(long[] move) {
        this.switchColours();
    }

    @Override
    public void makeNullMoveForward() {
        this.switchColours();
    }

    @Override
    public void makeNullMoveBackward() {
        this.switchColours();
    }

    private void switchColours() {
        this.colourToMove = this.colourToMove == 0 ? 1 : 0;
    }

    public void reinit() {
        throw new UnsupportedOperationException();
    }

    public void setAttacksSupport(boolean attacksSupport) {
    }

    public void test(int colour) {
        throw new UnsupportedOperationException();
    }

    public IBitBoard clone() {
        return new DummyBoard();
    }

    public void clearInCheckCounters() {
    }

    public int getBlackInCheckCounts() {
        return 0;
    }

    public int getWhiteInCheckCounts() {
        return 0;
    }

    public int genMinorMoves(int colour, long[][] moves) {
        return 0;
    }

    public void clearKingMovesCounters() {
    }

    public int getBlackKingMovesCounts() {
        return 0;
    }

    public int getWhiteKingMovesCounts() {
        return 0;
    }

    public boolean hasMinorOrMajorPieces(int colour) {
        return true;
    }

    public void clearQueenMovesCounters() {
    }

    public int getBlackQueensMovesCounts() {
        return 0;
    }

    public int getWhiteQueensMovesCounts() {
        return 0;
    }

    public boolean equals(Object o) {
        return true;
    }

    @Override
    public IFieldsAttacks getFieldsAttacks() {
        return null;
    }

    public int getGamePhase() {
        return 0;
    }

    @Override
    public SEE getSee() {
        return null;
    }

    public long getPawnHashKeyAfterMove(long[] move) {
        return 0L;
    }

    public int gen2MovesPromotions(IInternalMoveList list) {
        return 0;
    }

    public int genAllCheckMoves(IInternalMoveList list) {
        return 0;
    }

    @Override
    public int genAllMoves(IInternalMoveList list) {
        return this.dummyMoveGen(8, list);
    }

    private int dummyMoveGen(int count, IInternalMoveList list) {
        for (int i = 0; i < count; ++i) {
            double rand = Math.random();
            int move = i;
            move = (int)((long)move ^ (long)(1000 * i));
            move = (int)((long)move ^ (long)(63 * move));
            list.reserved_add(move);
        }
        return count;
    }

    public int genAllMoves(IInternalMoveList list, boolean checkKeepersAware) {
        return 0;
    }

    @Override
    public int genAllMoves_ByFigureID(int fieldID, long excludedToFields, IInternalMoveList list) {
        return 0;
    }

    public int genCapturePromotionCheckMoves(IInternalMoveList list) {
        return 0;
    }

    @Override
    public int genCapturePromotionMoves(IInternalMoveList list) {
        return 0;
    }

    public int genDirectCheckMoves(IInternalMoveList list) {
        return 0;
    }

    public int genHiddenCheckMoves(IInternalMoveList list) {
        return 0;
    }

    @Override
    public int genKingEscapes(IInternalMoveList list) {
        return 0;
    }

    public int genMinorMoves(IInternalMoveList list) {
        return 0;
    }

    @Override
    public int genNonCaptureNonPromotionMoves(IInternalMoveList list) {
        return 0;
    }

    public int genPromotions(IInternalMoveList list) {
        return 0;
    }

    public BackupInfo[] getBackups() {
        return null;
    }

    @Override
    public IBaseEval getBaseEvaluation() {
        return null;
    }

    @Override
    public boolean getFieldsStateSupport() {
        return false;
    }

    @Override
    public long getHashKeyAfterMove(int move) {
        return 0L;
    }

    public long getPawnHashKeyAfterMove(int move) {
        return 0L;
    }

    public int getPlayedMovesCount_Total() {
        return 0;
    }

    public boolean has2MovePromotions() {
        return false;
    }

    public boolean hasCapturePromotionCheck() {
        return false;
    }

    public boolean hasMove() {
        return false;
    }

    @Override
    public boolean hasMoveInCheck() {
        return false;
    }

    @Override
    public boolean hasMoveInNonCheck() {
        return false;
    }

    public boolean hasPromotions() {
        return false;
    }

    @Override
    public boolean hasSingleMove() {
        return false;
    }

    @Override
    public boolean isCheckMove(int move) {
        return false;
    }

    public boolean isDirectCheckMove(int move) {
        return false;
    }

    @Override
    public boolean isInCheck() {
        return false;
    }

    @Override
    public boolean isPossible(int move) {
        return false;
    }

    @Override
    public void makeMoveBackward(int move) {
    }

    @Override
    public void makeMoveForward(int move) {
    }

    @Override
    public void mark() {
    }

    @Override
    public void reset() {
    }

    @Override
    public void setAttacksSupport(boolean attacksSupport, boolean fieldsStateSupport) {
    }

    @Override
    public String toEPD() {
        return null;
    }

    @Override
    public PawnsEvalCache getPawnsCache() {
        return null;
    }

    @Override
    public PawnsModelEval getPawnsStructure() {
        return null;
    }

    @Override
    public boolean hasSufficientMatingMaterial() {
        return false;
    }

    public boolean hasUnstoppablePasser() {
        return false;
    }

    @Override
    public boolean isDraw50movesRule() {
        return false;
    }

    @Override
    public void setPawnsCache(PawnsEvalCache pawnsCache) {
    }

    @Override
    public void revert() {
    }

    public int[] getOpeningMoves() {
        return null;
    }

    @Override
    public boolean isPasserPush(int move) {
        return false;
    }

    @Override
    public int getDraw50movesRule() {
        return 0;
    }

    public int genAllMoves(IInternalMoveList list, long excludedToFieldsBoard) {
        throw new UnsupportedOperationException();
    }

    public int getLastCaptrueFieldID() {
        return 0;
    }

    @Override
    public int getUnstoppablePasser() {
        return 0;
    }

    @Override
    public IMaterialFactor getMaterialFactor() {
        return null;
    }

    public IMobility getMobility() {
        return null;
    }

    @Override
    public IMaterialState getMaterialState() {
        return null;
    }

    @Override
    public IBoardConfig getBoardConfig() {
        return null;
    }

    @Override
    public long getFiguresBitboardByPID(int pid) {
        return 0L;
    }

    @Override
    public int[] getMatrix() {
        return null;
    }

    @Override
    public void makeMoveForward(String ucimove) {
    }

    @Override
    public int getSEEScore(int move) {
        return 0;
    }

    @Override
    public IMoveOps getMoveOps() {
        return null;
    }

    @Override
    public int getFigureType(int fieldID) {
        return 0;
    }

    @Override
    public int getFigureColour(int fieldID) {
        return 0;
    }

    @Override
    public int getSEEFieldScore(int squareID) {
        return 0;
    }

    public double[] getNNUEInputs() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IBoard.CastlingType getCastlingType(int colour) {
        throw new IllegalStateException();
    }

    @Override
    public IBoard.CastlingPair getCastlingPair() {
        throw new IllegalStateException();
    }

    @Override
    public CastlingConfig getCastlingConfig() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addMoveListener(MoveListener listener) {
        throw new UnsupportedOperationException();
    }
}

