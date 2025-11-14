/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.engines.evaladapters.carballo.eval;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Bits;
import bagaturchess.engines.evaladapters.carballo.IBoard;

class BoardImpl
implements IBoard {
    private IBitBoard board;

    BoardImpl(IBitBoard _board) {
        this.board = _board;
    }

    private static final long convertBB(long bb) {
        return Bits.reverse(bb);
    }

    @Override
    public int getColourToMove() {
        return this.board.getColourToMove() == 0 ? 0 : 1;
    }

    @Override
    public long getPawns() {
        return BoardImpl.convertBB(this.board.getFiguresBitboardByColourAndType(0, 1) | this.board.getFiguresBitboardByColourAndType(1, 1));
    }

    @Override
    public long getKnights() {
        return BoardImpl.convertBB(this.board.getFiguresBitboardByColourAndType(0, 2) | this.board.getFiguresBitboardByColourAndType(1, 2));
    }

    @Override
    public long getBishops() {
        return BoardImpl.convertBB(this.board.getFiguresBitboardByColourAndType(0, 3) | this.board.getFiguresBitboardByColourAndType(1, 3));
    }

    @Override
    public long getRooks() {
        return BoardImpl.convertBB(this.board.getFiguresBitboardByColourAndType(0, 4) | this.board.getFiguresBitboardByColourAndType(1, 4));
    }

    @Override
    public long getQueens() {
        return BoardImpl.convertBB(this.board.getFiguresBitboardByColourAndType(0, 5) | this.board.getFiguresBitboardByColourAndType(1, 5));
    }

    @Override
    public long getKings() {
        return BoardImpl.convertBB(this.board.getFiguresBitboardByColourAndType(0, 6) | this.board.getFiguresBitboardByColourAndType(1, 6));
    }

    @Override
    public long getWhites() {
        return BoardImpl.convertBB(this.board.getFiguresBitboardByColour(0));
    }

    @Override
    public long getBlacks() {
        return BoardImpl.convertBB(this.board.getFiguresBitboardByColour(1));
    }

    @Override
    public long getAll() {
        return BoardImpl.convertBB(this.board.getFiguresBitboardByColour(0) | this.board.getFiguresBitboardByColour(1));
    }
}

