/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl2;

import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IInternalMoveList;
import bagaturchess.bitboard.impl.movelist.BaseMoveList;
import bagaturchess.bitboard.impl1.internal.MoveWrapper;
import bagaturchess.bitboard.impl2.CheckUtil;
import bagaturchess.bitboard.impl2.ChessBoard;
import bagaturchess.bitboard.impl2.ChessBoardBuilder;
import bagaturchess.bitboard.impl2.MoveGeneration;

public class Simulate {
    public static void main(String[] args) {
        IBitBoard board_test = BoardUtils.createBoard_WithPawnsCache("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        ChessBoard board = ChessBoardBuilder.getNewCB("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        ChessBoard board_copy = board.clone();
        IInternalMoveList[] lists = new IInternalMoveList[64];
        for (int i = 0; i < lists.length; ++i) {
            lists[i] = new BaseMoveList();
        }
        SearchInfo info = new SearchInfo();
        long start_time = System.currentTimeMillis();
        Simulate.simulate(board, board_test, 6, lists, info);
        System.out.println("Leafs: " + info.leafs);
        if (System.currentTimeMillis() - start_time > 1000L) {
            System.out.println(info.moves / ((System.currentTimeMillis() - start_time) / 1000L) + " NPS");
        }
        if (!board.equals(board_copy)) {
            throw new IllegalStateException();
        }
    }

    private static final void simulate(ChessBoard board, IBitBoard board_test, int depth, IInternalMoveList[] lists, SearchInfo info) {
        if (depth == 0) {
            ++info.leafs;
            return;
        }
        ++info.moves;
        IInternalMoveList list = lists[depth];
        list.reserved_clear();
        MoveGeneration.generateMoves(board, list);
        MoveGeneration.generateAttacks(board, list);
        for (int i = 0; i < list.reserved_getCurrentSize(); ++i) {
            int move = list.reserved_getMovesBuffer()[i];
            int color_to_move = board.color_to_move;
            if (!board.isValidMove(move)) continue;
            board.doMove(move);
            if (CheckUtil.isInCheck(board, color_to_move)) {
                throw new IllegalStateException(ChessBoardBuilder.toString(board, true) + "\t" + new MoveWrapper(move, true, board.castling_config).toString());
            }
            Simulate.simulate(board, board_test, depth - 1, lists, info);
            board.undoMove(move);
        }
    }

    static class SearchInfo {
        public long leafs;
        public long moves;

        SearchInfo() {
        }
    }
}

