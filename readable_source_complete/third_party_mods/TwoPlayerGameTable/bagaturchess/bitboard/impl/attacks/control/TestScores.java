/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.attacks.control;

import bagaturchess.bitboard.api.IBitBoard;
import java.util.Comparator;

public class TestScores {
    public static void dumpAll(IBitBoard board, int colour) {
        if (board.isInCheck(colour)) {
            throw new IllegalStateException("in check");
        }
        int scores_b = board.getFieldsAttacks().getScore_BeforeMove(colour);
        System.out.println("Starting scores " + scores_b);
    }

    public static void bubbleSort(int from, int to, long[][] moves, Comparator comp) {
        for (int i = from; i < to; ++i) {
            for (int j = i + 1; j < to; ++j) {
                long[] j_move = moves[j];
                long[] i_move = moves[i];
                if (comp.compare(j_move, i_move) >= 0) continue;
                moves[i] = j_move;
                moves[j] = i_move;
            }
        }
    }

    public static class Comparator23History
    implements Comparator {
        public int compare(Object o1, Object o2) {
            return this.compare1((long[])o1, (long[])o2);
        }

        private int compare1(long[] move1, long[] move2) {
            long eval1 = move1[23];
            long eval2 = move2[23];
            if (eval1 > eval2) {
                return -1;
            }
            if (eval1 < eval2) {
                return 1;
            }
            return -1;
        }
    }
}

