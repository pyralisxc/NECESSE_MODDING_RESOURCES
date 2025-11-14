/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.api;

import bagaturchess.bitboard.api.IBaseEval;
import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.bitboard.api.IInternalMoveList;
import bagaturchess.bitboard.api.IMaterialFactor;
import bagaturchess.bitboard.api.IMaterialState;
import bagaturchess.bitboard.api.IMoveOps;
import bagaturchess.bitboard.api.IPiecesLists;
import bagaturchess.bitboard.api.ISEE;
import bagaturchess.bitboard.api.PawnsEvalCache;
import bagaturchess.bitboard.common.MoveListener;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEval;
import bagaturchess.bitboard.impl1.internal.CastlingConfig;

public interface IBoard {
    public CastlingConfig getCastlingConfig();

    public int[] getMatrix();

    public PawnsEvalCache getPawnsCache();

    public void setPawnsCache(PawnsEvalCache var1);

    public PawnsModelEval getPawnsStructure();

    public IBoardConfig getBoardConfig();

    public IPiecesLists getPiecesLists();

    public int getColourToMove();

    public int genAllMoves(IInternalMoveList var1);

    public int genKingEscapes(IInternalMoveList var1);

    public int genCapturePromotionMoves(IInternalMoveList var1);

    public int genNonCaptureNonPromotionMoves(IInternalMoveList var1);

    public int genAllMoves_ByFigureID(int var1, long var2, IInternalMoveList var4);

    public int getEnpassantSquareID();

    public void makeMoveForward(int var1);

    public void makeMoveForward(String var1);

    public void makeMoveBackward(int var1);

    public void makeNullMoveForward();

    public void makeNullMoveBackward();

    public long getHashKey();

    public long getHashKeyAfterMove(int var1);

    public long getPawnsHashKey();

    public int getStateRepetition();

    public int getFigureID(int var1);

    public int getFigureType(int var1);

    public int getFigureColour(int var1);

    public ISEE getSee();

    public int getSEEScore(int var1);

    public int getSEEFieldScore(int var1);

    public IMoveOps getMoveOps();

    public void mark();

    public void reset();

    public void revert();

    public String toEPD();

    public IMaterialState getMaterialState();

    public IMaterialFactor getMaterialFactor();

    public IBaseEval getBaseEvaluation();

    public boolean isPasserPush(int var1);

    public int getUnstoppablePasser();

    public boolean isDraw50movesRule();

    public int getDraw50movesRule();

    public boolean hasSufficientMatingMaterial();

    public boolean hasSufficientMatingMaterial(int var1);

    public boolean isInCheck();

    public boolean isInCheck(int var1);

    public boolean hasMoveInCheck();

    public boolean hasMoveInNonCheck();

    public boolean isCheckMove(int var1);

    public boolean isPossible(int var1);

    public boolean hasSingleMove();

    public CastlingType getCastlingType(int var1);

    public CastlingPair getCastlingPair();

    public boolean hasRightsToKingCastle(int var1);

    public boolean hasRightsToQueenCastle(int var1);

    public int getPlayedMovesCount();

    public int[] getPlayedMoves();

    public int getLastMove();

    public IGameStatus getStatus();

    public Object getNNUEInputs();

    public void addMoveListener(MoveListener var1);

    public static enum CastlingPair {
        NONE_NONE,
        NONE_KINGSIDE,
        KINGSIDE_NONE,
        KINGSIDE_KINGSIDE,
        NONE_QUEENSIDE,
        QUEENSIDE_NONE,
        QUEENSIDE_QUEENSIDE,
        KINGSIDE_QUEENSIDE,
        QUEENSIDE_KINGSIDE;

    }

    public static enum CastlingType {
        NONE,
        KINGSIDE,
        QUEENSIDE;

    }
}

