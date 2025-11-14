/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.state;

import bagaturchess.bitboard.api.IBoard;
import bagaturchess.bitboard.api.IPiecesLists;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.state.PiecesList;

public class PiecesLists
extends Figures
implements IPiecesLists {
    private PiecesList[] pieces;
    private IBoard board;

    public PiecesLists(IBoard _board) {
        this.board = _board;
        this.pieces = new PiecesList[13];
        this.pieces[1] = new PiecesList(this.board, 8);
        this.pieces[2] = new PiecesList(this.board, 8);
        this.pieces[6] = new PiecesList(this.board, 8);
        this.pieces[3] = new PiecesList(this.board, 8);
        this.pieces[4] = new PiecesList(this.board, 8);
        this.pieces[5] = new PiecesList(this.board, 8);
        this.pieces[7] = new PiecesList(this.board, 8);
        this.pieces[8] = new PiecesList(this.board, 8);
        this.pieces[12] = new PiecesList(this.board, 8);
        this.pieces[9] = new PiecesList(this.board, 8);
        this.pieces[10] = new PiecesList(this.board, 8);
        this.pieces[11] = new PiecesList(this.board, 8);
    }

    @Override
    public void rem(int pid, int fieldID) {
        this.pieces[pid].remove(fieldID);
    }

    @Override
    public void add(int pid, int fieldID) {
        this.pieces[pid].add(fieldID);
    }

    @Override
    public void move(int pid, int fromFieldID, int toFieldID) {
        this.pieces[pid].set(fromFieldID, toFieldID);
    }

    @Override
    public PiecesList getPieces(int pid) {
        return this.pieces[pid];
    }
}

