/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.egtb.syzygy.run;

import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl1.internal.MoveWrapper;
import bagaturchess.egtb.syzygy.SyzygyTBProbing;
import bagaturchess.uci.api.ChannelManager;
import bagaturchess.uci.impl.Channel_Console;

public class SyzygyTest {
    public static void main(String[] args) {
        try {
            ChannelManager.setChannel(new Channel_Console(System.in, System.out, System.out));
            IBitBoard board = BoardUtils.createBoard_WithPawnsCache("8/6P1/8/2kB2K1/8/8/8/4r3 w - - 1 19");
            System.out.println(board);
            System.out.println("board.getDraw50movesRule()=" + board.getDraw50movesRule());
            if (SyzygyTBProbing.getSingleton() != null) {
                System.out.println("Loading TBs");
                SyzygyTBProbing.getSingleton().load(System.getenv("SYZYGY_HOME"));
                boolean available = SyzygyTBProbing.getSingleton().isAvailable(3);
                System.out.println("isAvailable(3)=" + available);
                System.out.println("start probing");
                long[] out = new long[2];
                SyzygyTBProbing.getSingleton().probeMove(board, out);
                MoveWrapper best_move = new MoveWrapper((int)out[1], false, board.getCastlingConfig());
                System.out.println("best_move=" + String.valueOf(best_move) + ", dtz=" + out[0]);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

