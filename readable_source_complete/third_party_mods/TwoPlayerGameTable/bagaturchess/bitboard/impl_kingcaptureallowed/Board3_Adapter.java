/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl_kingcaptureallowed;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IBoard;
import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.api.IFieldsAttacks;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.bitboard.api.IInternalMoveList;
import bagaturchess.bitboard.api.IMoveOps;
import bagaturchess.bitboard.api.IPlayerAttacks;
import bagaturchess.bitboard.api.PawnsEvalCache;
import bagaturchess.bitboard.common.MoveListener;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.movegen.MoveOpsImpl;
import bagaturchess.bitboard.impl.state.PiecesList;
import bagaturchess.bitboard.impl1.internal.CastlingConfig;
import bagaturchess.bitboard.impl_kingcaptureallowed.Board3;

public class Board3_Adapter
extends Board3
implements IBitBoard {
    private IMoveOps moveOps = new MoveOpsImpl(this);

    public Board3_Adapter(String fenStr, IBoardConfig boardConfig) {
        super(fenStr, boardConfig);
    }

    public Board3_Adapter(String fenStr, PawnsEvalCache pawnsCache, IBoardConfig boardConfig) {
        super(fenStr, pawnsCache, boardConfig);
    }

    public Board3_Adapter() {
    }

    public Board3_Adapter(String fen) {
        super(fen);
    }

    @Override
    public int genKingEscapes(IInternalMoveList list) {
        return this.genAllMoves(list);
    }

    @Override
    public int getFigureType(int fieldID) {
        return Figures.getFigureType(this.getFigureID(fieldID));
    }

    @Override
    public int getFigureColour(int fieldID) {
        return Figures.getFigureColour(this.getFigureID(fieldID));
    }

    @Override
    public boolean isPossible(int move) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setPawnsCache(PawnsEvalCache _pawnsCache) {
        this.pawnsCache = _pawnsCache;
    }

    @Override
    public boolean isInCheck(int colour) {
        return super.isInCheck(colour);
    }

    @Override
    public final long getFiguresBitboardByPID(int pid) {
        PiecesList piecesList = this.pieces.getPieces(pid);
        int size = piecesList.getDataSize();
        int[] ids = piecesList.getData();
        long bitboard = 0L;
        for (int i = 0; i < size; ++i) {
            int fieldID = ids[i];
            bitboard |= Fields.ALL_A1H1[fieldID];
        }
        return bitboard;
    }

    @Override
    public long getFiguresBitboardByColourAndType(int colour, int type) {
        return this.getFiguresBitboardByPID(Constants.COLOUR_AND_TYPE_2_PIECE_IDENTITY[colour][type]);
    }

    @Override
    public long getFiguresBitboardByColour(int colour) {
        long result = 0L;
        if (colour == 0) {
            result |= this.getFiguresBitboardByPID(6);
            result |= this.getFiguresBitboardByPID(1);
            result |= this.getFiguresBitboardByPID(3);
            result |= this.getFiguresBitboardByPID(2);
            result |= this.getFiguresBitboardByPID(5);
            result |= this.getFiguresBitboardByPID(4);
        } else {
            result |= this.getFiguresBitboardByPID(12);
            result |= this.getFiguresBitboardByPID(7);
            result |= this.getFiguresBitboardByPID(9);
            result |= this.getFiguresBitboardByPID(8);
            result |= this.getFiguresBitboardByPID(11);
            result |= this.getFiguresBitboardByPID(10);
        }
        return result;
    }

    @Override
    public final long getFreeBitboard() {
        long all = this.getFiguresBitboardByColour(0) | this.getFiguresBitboardByColour(1);
        return all ^ 0xFFFFFFFFFFFFFFFFL;
    }

    @Override
    public IGameStatus getStatus() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IPlayerAttacks getPlayerAttacks(int colour) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IFieldsAttacks getFieldsAttacks() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasSingleMove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCheckMove(int move) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getAttacksSupport() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getFieldsStateSupport() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAttacksSupport(boolean attacksSupport, boolean fieldsStateSupport) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int genAllMoves_ByFigureID(int fieldID, long excludedToFields, IInternalMoveList list) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void makeMoveForward(String ucimove) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getSEEScore(int move) {
        return this.getSee().evalExchange(move);
    }

    @Override
    public int getSEEFieldScore(int squareID) {
        return this.getSee().seeField(squareID);
    }

    @Override
    public long getHashKeyAfterMove(int move) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IMoveOps getMoveOps() {
        return this.moveOps;
    }

    @Override
    public int getEnpassantSquareID() {
        throw new UnsupportedOperationException();
    }

    public double[] getNNUEInputs() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IBoard.CastlingPair getCastlingPair() {
        throw new UnsupportedOperationException();
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

