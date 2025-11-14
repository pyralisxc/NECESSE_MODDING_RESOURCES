/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.run;

import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IBoard;
import bagaturchess.bitboard.api.IInternalMoveList;
import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.Board;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.movelist.BaseMoveList;

public class Simulate {
    private static long genMoves(Board bitBoard) {
        long count = 0L;
        long[][] moves = new long[256][26];
        IInternalMoveList[] lists = new IInternalMoveList[256];
        for (int i = 0; i < lists.length; ++i) {
            lists[i] = new BaseMoveList();
        }
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000000; ++i) {
        }
        long end = System.currentTimeMillis();
        System.out.println("After: " + String.valueOf(bitBoard));
        System.out.printf("Gen moves: " + count + ", Time " + (end - start) + "ms, Moves per second %f", (double)count / ((double)(end - start) / 1000.0));
        return count;
    }

    private static void simulate(SearchInfo info, IBoard bitBoard, int colour, int depth, int branching) {
        IInternalMoveList[] lists = new IInternalMoveList[40];
        for (int i = 0; i < lists.length; ++i) {
            lists[i] = new BaseMoveList();
        }
        System.out.println("Before: " + String.valueOf(bitBoard));
        long start = System.currentTimeMillis();
        Simulate.simulate1(info, bitBoard, colour, lists, depth, branching);
        long end = System.currentTimeMillis();
        System.out.println("After: " + String.valueOf(bitBoard));
        System.out.printf("Gen moves: " + (info.nodes - 1L) + ", Time " + (end - start) + "ms, Moves per second %f", (double)info.nodes / ((double)(end - start) / 1000.0));
    }

    private static void simulate1(SearchInfo info, IBoard board, int colour, IInternalMoveList[] lists, int depth, int branching) {
        ++info.nodes;
        if (depth != 0) {
            lists[depth].reserved_clear();
            if (board.isInCheck()) {
                board.genKingEscapes(lists[depth]);
            } else {
                board.genAllMoves(lists[depth]);
            }
            int curCount = lists[depth].reserved_getCurrentSize();
            Simulate.bubbleSort(0, curCount, lists[depth].reserved_getMovesBuffer());
            Utils.randomize(lists[depth].reserved_getMovesBuffer(), 0, curCount);
            int movenumber = Math.min(branching, curCount);
            for (int i = 0; i < movenumber; ++i) {
                int move = lists[depth].reserved_getMovesBuffer()[i];
                int see = 0;
                if (board.getMoveOps().isCaptureOrPromotion(move)) {
                    see = board.getSEEScore(move);
                }
                if (board.getMoveOps().isCapture(move) && board.getMoveOps().getCapturedFigureType(move) == 6) {
                    System.out.println("KING CAPTURE");
                    continue;
                }
                board.makeMoveForward(move);
                Simulate.simulate1(info, board, Figures.OPPONENT_COLOUR[colour], lists, depth - 1, branching);
                board.makeMoveBackward(move);
            }
        }
    }

    public static void main(String[] args) {
        IBitBoard bitBoard = BoardUtils.createBoard_WithPawnsCache();
        SearchInfo info = new SearchInfo();
        Simulate.simulate(info, bitBoard, 0, 22, 2);
    }

    public static void bubbleSort(int from, int to, int[] moves) {
        for (int i = from; i < to; ++i) {
            boolean change = false;
            for (int j = i + 1; j < to; ++j) {
                int j_move = moves[j];
                int i_move = moves[i];
                if (j_move <= i_move) continue;
                moves[i] = j_move;
                moves[j] = i_move;
                change = true;
            }
            if (change) continue;
            return;
        }
    }

    static class SearchInfo {
        public long nodes;

        SearchInfo() {
        }
    }
}

