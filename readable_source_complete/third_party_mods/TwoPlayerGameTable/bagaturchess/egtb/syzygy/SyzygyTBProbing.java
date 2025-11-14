/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.egtb.syzygy;

import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.movelist.BaseMoveList;
import bagaturchess.egtb.syzygy.SyzygyJNIBridge;
import bagaturchess.uci.api.ChannelManager;
import java.util.ArrayList;
import java.util.Collections;

public class SyzygyTBProbing {
    private static int MAX_PIECES_COUNT = 7;
    private static boolean loadingInitiated;
    private static SyzygyTBProbing instance;
    private static boolean switched_off;

    public static final SyzygyTBProbing getSingleton() {
        if (switched_off) {
            return null;
        }
        if (instance == null && !loadingInitiated) {
            loadingInitiated = true;
            instance = new SyzygyTBProbing();
            if (!instance.loadNativeLibrary()) {
                instance = null;
                switched_off = true;
            }
        }
        return instance;
    }

    public static final void disableSingleton() {
        switched_off = true;
        if (ChannelManager.getChannel() != null) {
            ChannelManager.getChannel().dump("SyzygyTBProbing.clearSingleton() called: Syzygy TBs are now switched off");
        }
    }

    public final synchronized void load(String path) {
        if (path == null) {
            if (ChannelManager.getChannel() != null) {
                ChannelManager.getChannel().dump("SyzygyTBProbing.load(path) called: Syzygy tablebases NOT loaded, because path is null");
            }
            return;
        }
        SyzygyJNIBridge.load(path);
    }

    private SyzygyTBProbing() {
        loadingInitiated = false;
    }

    private synchronized boolean loadNativeLibrary() {
        return SyzygyJNIBridge.loadNativeLibrary();
    }

    public boolean isAvailable(int piecesLeft) {
        if (piecesLeft > MAX_PIECES_COUNT) {
            return false;
        }
        return SyzygyJNIBridge.isAvailable(piecesLeft);
    }

    public int probeDTM(IBitBoard board) {
        if (board.getMaterialState().getPiecesCount() > MAX_PIECES_COUNT) {
            return -1;
        }
        if (board.hasRightsToKingCastle(0) || board.hasRightsToQueenCastle(0) || board.hasRightsToKingCastle(1) || board.hasRightsToQueenCastle(1)) {
            return -1;
        }
        if (board.getEnpassantSquareID() != 0) {
            return -1;
        }
        return SyzygyJNIBridge.probeSyzygyDTM(SyzygyTBProbing.convertBB(board.getFiguresBitboardByColour(0)), SyzygyTBProbing.convertBB(board.getFiguresBitboardByColour(1)), SyzygyTBProbing.convertBB(board.getFiguresBitboardByColourAndType(0, 6)) | SyzygyTBProbing.convertBB(board.getFiguresBitboardByColourAndType(1, 6)), SyzygyTBProbing.convertBB(board.getFiguresBitboardByColourAndType(0, 5)) | SyzygyTBProbing.convertBB(board.getFiguresBitboardByColourAndType(1, 5)), SyzygyTBProbing.convertBB(board.getFiguresBitboardByColourAndType(0, 4)) | SyzygyTBProbing.convertBB(board.getFiguresBitboardByColourAndType(1, 4)), SyzygyTBProbing.convertBB(board.getFiguresBitboardByColourAndType(0, 3)) | SyzygyTBProbing.convertBB(board.getFiguresBitboardByColourAndType(1, 3)), SyzygyTBProbing.convertBB(board.getFiguresBitboardByColourAndType(0, 2)) | SyzygyTBProbing.convertBB(board.getFiguresBitboardByColourAndType(1, 2)), SyzygyTBProbing.convertBB(board.getFiguresBitboardByColourAndType(0, 1)) | SyzygyTBProbing.convertBB(board.getFiguresBitboardByColourAndType(1, 1)), board.getDraw50movesRule(), 0, board.getColourToMove() == 0);
    }

    public int probeWDL(IBitBoard board) {
        if (board.getMaterialState().getPiecesCount() > MAX_PIECES_COUNT) {
            return -1;
        }
        if (board.hasRightsToKingCastle(0) || board.hasRightsToQueenCastle(0) || board.hasRightsToKingCastle(1) || board.hasRightsToQueenCastle(1)) {
            return -1;
        }
        if (board.getEnpassantSquareID() != 0) {
            return -1;
        }
        return SyzygyJNIBridge.probeSyzygyWDL(SyzygyTBProbing.convertBB(board.getFiguresBitboardByColour(0)), SyzygyTBProbing.convertBB(board.getFiguresBitboardByColour(1)), SyzygyTBProbing.convertBB(board.getFiguresBitboardByColourAndType(0, 6)) | SyzygyTBProbing.convertBB(board.getFiguresBitboardByColourAndType(1, 6)), SyzygyTBProbing.convertBB(board.getFiguresBitboardByColourAndType(0, 5)) | SyzygyTBProbing.convertBB(board.getFiguresBitboardByColourAndType(1, 5)), SyzygyTBProbing.convertBB(board.getFiguresBitboardByColourAndType(0, 4)) | SyzygyTBProbing.convertBB(board.getFiguresBitboardByColourAndType(1, 4)), SyzygyTBProbing.convertBB(board.getFiguresBitboardByColourAndType(0, 3)) | SyzygyTBProbing.convertBB(board.getFiguresBitboardByColourAndType(1, 3)), SyzygyTBProbing.convertBB(board.getFiguresBitboardByColourAndType(0, 2)) | SyzygyTBProbing.convertBB(board.getFiguresBitboardByColourAndType(1, 2)), SyzygyTBProbing.convertBB(board.getFiguresBitboardByColourAndType(0, 1)) | SyzygyTBProbing.convertBB(board.getFiguresBitboardByColourAndType(1, 1)), board.getDraw50movesRule(), 0, board.getColourToMove() == 0);
    }

    public int probeDTZ(IBitBoard board) {
        if (board.getMaterialState().getPiecesCount() > MAX_PIECES_COUNT) {
            return -1;
        }
        if (board.hasRightsToKingCastle(0) || board.hasRightsToQueenCastle(0) || board.hasRightsToKingCastle(1) || board.hasRightsToQueenCastle(1)) {
            return -1;
        }
        if (board.getEnpassantSquareID() != 0) {
            return -1;
        }
        int score = SyzygyJNIBridge.probeSyzygyDTZ(SyzygyTBProbing.convertBB(board.getFiguresBitboardByColour(0)), SyzygyTBProbing.convertBB(board.getFiguresBitboardByColour(1)), SyzygyTBProbing.convertBB(board.getFiguresBitboardByColourAndType(0, 6)) | SyzygyTBProbing.convertBB(board.getFiguresBitboardByColourAndType(1, 6)), SyzygyTBProbing.convertBB(board.getFiguresBitboardByColourAndType(0, 5)) | SyzygyTBProbing.convertBB(board.getFiguresBitboardByColourAndType(1, 5)), SyzygyTBProbing.convertBB(board.getFiguresBitboardByColourAndType(0, 4)) | SyzygyTBProbing.convertBB(board.getFiguresBitboardByColourAndType(1, 4)), SyzygyTBProbing.convertBB(board.getFiguresBitboardByColourAndType(0, 3)) | SyzygyTBProbing.convertBB(board.getFiguresBitboardByColourAndType(1, 3)), SyzygyTBProbing.convertBB(board.getFiguresBitboardByColourAndType(0, 2)) | SyzygyTBProbing.convertBB(board.getFiguresBitboardByColourAndType(1, 2)), SyzygyTBProbing.convertBB(board.getFiguresBitboardByColourAndType(0, 1)) | SyzygyTBProbing.convertBB(board.getFiguresBitboardByColourAndType(1, 1)), board.getDraw50movesRule(), 0, board.getColourToMove() == 0);
        return score;
    }

    public void probeMove(IBitBoard board, long[] out) {
        int cur_move;
        out[0] = -1L;
        out[1] = -1L;
        if (board.getMaterialState().getPiecesCount() > MAX_PIECES_COUNT) {
            return;
        }
        if (board.hasRightsToKingCastle(0) || board.hasRightsToQueenCastle(0) || board.hasRightsToKingCastle(1) || board.hasRightsToQueenCastle(1)) {
            return;
        }
        if (board.getEnpassantSquareID() != 0) {
            return;
        }
        BaseMoveList temp_moves_list = new BaseMoveList();
        temp_moves_list.clear();
        board.genAllMoves(temp_moves_list);
        ArrayList<MoveWDLPair> moves = new ArrayList<MoveWDLPair>();
        while ((cur_move = temp_moves_list.next()) != 0) {
            board.makeMoveForward(cur_move);
            if (board.getStateRepetition() <= 2) {
                int probe_result = SyzygyTBProbing.getSingleton().probeWDL(board);
                int wdl = (probe_result & 0xF) >> 0;
                int dtz = this.probeDTZ(board);
                int dtm = -1;
                System.out.println(board.getMoveOps().moveToString(cur_move) + ", dtz=" + dtz + ", wdl=" + wdl + ", dtm=" + dtm + ", probe_result=" + probe_result);
                int distanceToDraw_50MoveRule = 99 - board.getDraw50movesRule();
                if (distanceToDraw_50MoveRule >= dtz && wdl == 0) {
                    moves.add(new MoveWDLPair(wdl, dtz, cur_move));
                }
            }
            board.makeMoveBackward(cur_move);
        }
        if (moves.size() > 0) {
            Collections.sort(moves);
            MoveWDLPair best = (MoveWDLPair)moves.get(0);
            out[0] = best.dtz;
            out[1] = best.move;
        }
    }

    public static int toMove(int result) {
        int from = (result & 0xFC00) >> 10;
        int to = (result & 0x3F0) >> 4;
        int promotes = (result & 0x70000) >> 16;
        return SyzygyTBProbing.getMove(from, to, promotes);
    }

    public static int getMove(int fromSquare, int toSquare, int promotes) {
        return fromSquare | toSquare << 6 | promotes << 12;
    }

    public static int getWDLScore(int wdl, int depth) {
        switch (wdl) {
            case 0: {
                return -28000 + depth;
            }
            case 1: {
                return 0;
            }
            case 2: {
                return 0;
            }
            case 3: {
                return 0;
            }
            case 4: {
                return 28000 - depth;
            }
        }
        throw new IllegalStateException("wdl=" + wdl);
    }

    private static long convertBB(long pieces) {
        return pieces;
    }

    public static void main(String[] args) {
        IBitBoard board = BoardUtils.createBoard_WithPawnsCache("4k3/8/8/8/8/8/3R4/4K3 w - - 0 1");
        long[] result = new long[2];
        SyzygyTBProbing.getSingleton().load(System.getenv("SYZYGY_HOME"));
        SyzygyTBProbing.getSingleton().probeMove(board, result);
        System.out.println("result[0]=" + result[0] + ", result[1]=" + result[1]);
        if (result[1] != 0L) {
            System.out.println("BESTMOVE: " + board.getMoveOps().moveToString((int)result[1]));
        }
    }

    static {
        switched_off = false;
    }

    private class MoveWDLPair
    implements Comparable {
        long wdl;
        long dtz;
        long move;

        private MoveWDLPair(long _wdl, long _dtz, long _move) {
            if (_wdl != 0L) {
                throw new IllegalStateException("Use this method only in the root search");
            }
            this.wdl = _wdl;
            this.dtz = _dtz;
            this.move = _move;
        }

        public int compareTo(Object other) {
            if (!(other instanceof MoveWDLPair)) {
                return -1;
            }
            long diff = this.dtz - ((MoveWDLPair)other).dtz;
            if (diff == 0L) {
                return -1;
            }
            return (int)diff;
        }

        public boolean equals(Object o) {
            if (o instanceof MoveWDLPair) {
                MoveWDLPair other = (MoveWDLPair)o;
                return this.dtz == other.dtz && this.move == other.move;
            }
            return false;
        }

        public int hashCode() {
            return (int)(100L * (this.dtz + 1L) + this.move);
        }
    }
}

