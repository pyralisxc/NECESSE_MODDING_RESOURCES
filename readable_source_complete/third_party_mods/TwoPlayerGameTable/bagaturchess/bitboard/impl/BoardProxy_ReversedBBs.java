/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl;

import bagaturchess.bitboard.api.IBaseEval;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IBoard;
import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.api.IFieldsAttacks;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.bitboard.api.IInternalMoveList;
import bagaturchess.bitboard.api.IMaterialFactor;
import bagaturchess.bitboard.api.IMaterialState;
import bagaturchess.bitboard.api.IMoveOps;
import bagaturchess.bitboard.api.IPiecesLists;
import bagaturchess.bitboard.api.IPlayerAttacks;
import bagaturchess.bitboard.api.ISEE;
import bagaturchess.bitboard.api.PawnsEvalCache;
import bagaturchess.bitboard.common.MoveListener;
import bagaturchess.bitboard.impl.Bits;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEval;
import bagaturchess.bitboard.impl1.internal.CastlingConfig;

public class BoardProxy_ReversedBBs
implements IBitBoard {
    private final IBitBoard bitboard;

    public BoardProxy_ReversedBBs(IBitBoard _bitboard) {
        this.bitboard = _bitboard;
    }

    @Override
    public long getFreeBitboard() {
        return BoardProxy_ReversedBBs.convertBB(this.bitboard.getFreeBitboard());
    }

    @Override
    public long getFiguresBitboardByPID(int pid) {
        return BoardProxy_ReversedBBs.convertBB(this.bitboard.getFiguresBitboardByPID(pid));
    }

    @Override
    public long getFiguresBitboardByColourAndType(int colour, int type) {
        return BoardProxy_ReversedBBs.convertBB(this.bitboard.getFiguresBitboardByColourAndType(colour, type));
    }

    @Override
    public long getFiguresBitboardByColour(int colour) {
        return BoardProxy_ReversedBBs.convertBB(this.bitboard.getFiguresBitboardByColour(colour));
    }

    private static final long convertBB(long bb) {
        return Bits.reverse(bb);
    }

    @Override
    public int[] getMatrix() {
        return this.bitboard.getMatrix();
    }

    @Override
    public PawnsEvalCache getPawnsCache() {
        return this.bitboard.getPawnsCache();
    }

    @Override
    public void setPawnsCache(PawnsEvalCache pawnsCache) {
        this.bitboard.setPawnsCache(pawnsCache);
    }

    @Override
    public PawnsModelEval getPawnsStructure() {
        return this.bitboard.getPawnsStructure();
    }

    @Override
    public IBoardConfig getBoardConfig() {
        return this.bitboard.getBoardConfig();
    }

    @Override
    public IPiecesLists getPiecesLists() {
        return this.bitboard.getPiecesLists();
    }

    @Override
    public int getColourToMove() {
        return this.bitboard.getColourToMove();
    }

    @Override
    public int genAllMoves(IInternalMoveList list) {
        return this.bitboard.genAllMoves(list);
    }

    @Override
    public int genKingEscapes(IInternalMoveList list) {
        return this.bitboard.genKingEscapes(list);
    }

    @Override
    public int genCapturePromotionMoves(IInternalMoveList list) {
        return this.bitboard.genCapturePromotionMoves(list);
    }

    @Override
    public int genNonCaptureNonPromotionMoves(IInternalMoveList list) {
        return this.bitboard.genNonCaptureNonPromotionMoves(list);
    }

    @Override
    public int genAllMoves_ByFigureID(int fieldID, long excludedToFields, IInternalMoveList list) {
        return this.bitboard.genAllMoves_ByFigureID(fieldID, excludedToFields, list);
    }

    @Override
    public void makeMoveForward(int move) {
        this.bitboard.makeMoveForward(move);
    }

    @Override
    public void makeMoveForward(String ucimove) {
        this.bitboard.makeMoveForward(ucimove);
    }

    @Override
    public void makeMoveBackward(int move) {
        this.bitboard.makeMoveBackward(move);
    }

    @Override
    public void makeNullMoveForward() {
        this.bitboard.makeNullMoveForward();
    }

    @Override
    public void makeNullMoveBackward() {
        this.bitboard.makeNullMoveBackward();
    }

    @Override
    public long getHashKey() {
        return this.bitboard.getHashKey();
    }

    @Override
    public long getPawnsHashKey() {
        return this.bitboard.getPawnsHashKey();
    }

    @Override
    public int getStateRepetition() {
        return this.bitboard.getStateRepetition();
    }

    @Override
    public int getEnpassantSquareID() {
        return this.bitboard.getEnpassantSquareID();
    }

    @Override
    public int getFigureID(int fieldID) {
        return this.bitboard.getFigureID(fieldID);
    }

    @Override
    public ISEE getSee() {
        return this.bitboard.getSee();
    }

    @Override
    public void mark() {
        this.bitboard.mark();
    }

    @Override
    public void reset() {
        this.bitboard.reset();
    }

    @Override
    public void revert() {
        this.bitboard.revert();
    }

    @Override
    public String toEPD() {
        return this.bitboard.toEPD();
    }

    @Override
    public IMaterialState getMaterialState() {
        return this.bitboard.getMaterialState();
    }

    @Override
    public IMaterialFactor getMaterialFactor() {
        return this.bitboard.getMaterialFactor();
    }

    @Override
    public IBaseEval getBaseEvaluation() {
        return this.bitboard.getBaseEvaluation();
    }

    @Override
    public boolean isPasserPush(int move) {
        return this.bitboard.isPasserPush(move);
    }

    @Override
    public int getUnstoppablePasser() {
        return this.bitboard.getUnstoppablePasser();
    }

    @Override
    public boolean isDraw50movesRule() {
        return this.bitboard.isDraw50movesRule();
    }

    @Override
    public int getDraw50movesRule() {
        return this.bitboard.getDraw50movesRule();
    }

    @Override
    public boolean hasSufficientMatingMaterial() {
        return this.bitboard.hasSufficientMatingMaterial();
    }

    @Override
    public boolean hasSufficientMatingMaterial(int color) {
        return this.bitboard.hasSufficientMatingMaterial(color);
    }

    @Override
    public boolean isInCheck() {
        return this.bitboard.isInCheck();
    }

    @Override
    public boolean isInCheck(int colour) {
        return this.bitboard.isInCheck(colour);
    }

    @Override
    public boolean hasMoveInCheck() {
        return this.bitboard.hasMoveInCheck();
    }

    @Override
    public boolean hasMoveInNonCheck() {
        return this.bitboard.hasMoveInNonCheck();
    }

    @Override
    public boolean isCheckMove(int move) {
        return this.bitboard.isCheckMove(move);
    }

    @Override
    public boolean isPossible(int move) {
        return this.bitboard.isPossible(move);
    }

    @Override
    public boolean hasSingleMove() {
        return this.bitboard.hasSingleMove();
    }

    @Override
    public boolean hasRightsToKingCastle(int colour) {
        return this.bitboard.hasRightsToKingCastle(colour);
    }

    @Override
    public boolean hasRightsToQueenCastle(int colour) {
        return this.bitboard.hasRightsToQueenCastle(colour);
    }

    @Override
    public int getPlayedMovesCount() {
        return this.bitboard.getPlayedMovesCount();
    }

    @Override
    public int[] getPlayedMoves() {
        return this.bitboard.getPlayedMoves();
    }

    @Override
    public int getLastMove() {
        return this.bitboard.getLastMove();
    }

    @Override
    public IGameStatus getStatus() {
        return this.bitboard.getStatus();
    }

    @Override
    public boolean getAttacksSupport() {
        return this.bitboard.getAttacksSupport();
    }

    @Override
    public boolean getFieldsStateSupport() {
        return this.bitboard.getFieldsStateSupport();
    }

    @Override
    public void setAttacksSupport(boolean attacksSupport, boolean fieldsStateSupport) {
        this.bitboard.setAttacksSupport(attacksSupport, fieldsStateSupport);
    }

    @Override
    public IPlayerAttacks getPlayerAttacks(int colour) {
        return this.bitboard.getPlayerAttacks(colour);
    }

    @Override
    public IFieldsAttacks getFieldsAttacks() {
        return this.bitboard.getFieldsAttacks();
    }

    @Override
    public int getSEEScore(int move) {
        return this.bitboard.getSEEScore(move);
    }

    @Override
    public IMoveOps getMoveOps() {
        return this.bitboard.getMoveOps();
    }

    @Override
    public int getFigureType(int fieldID) {
        return this.bitboard.getFigureType(fieldID);
    }

    @Override
    public long getHashKeyAfterMove(int move) {
        return this.bitboard.getHashKeyAfterMove(move);
    }

    public String toString() {
        return this.bitboard.toString();
    }

    @Override
    public int getFigureColour(int fieldID) {
        return this.bitboard.getFigureColour(fieldID);
    }

    @Override
    public int getSEEFieldScore(int squareID) {
        return this.bitboard.getSEEFieldScore(squareID);
    }

    @Override
    public Object getNNUEInputs() {
        return this.bitboard.getNNUEInputs();
    }

    @Override
    public IBoard.CastlingType getCastlingType(int colour) {
        return this.bitboard.getCastlingType(colour);
    }

    @Override
    public IBoard.CastlingPair getCastlingPair() {
        return this.bitboard.getCastlingPair();
    }

    @Override
    public CastlingConfig getCastlingConfig() {
        return this.bitboard.getCastlingConfig();
    }

    @Override
    public void addMoveListener(MoveListener listener) {
        this.bitboard.addMoveListener(listener);
    }
}

