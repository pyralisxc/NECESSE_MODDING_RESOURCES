/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.api;

import bagaturchess.bitboard.api.IBinarySemaphore;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.api.PawnsEvalCache;
import bagaturchess.bitboard.impl.Board;
import bagaturchess.bitboard.impl.BoardProxy_ReversedBBs;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.datastructs.lrmmap.DataObjectFactory;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEvalFactory;
import bagaturchess.bitboard.impl.movelist.BaseMoveList;
import bagaturchess.bitboard.impl.utils.BinarySemaphore_Dummy;
import bagaturchess.bitboard.impl1.BoardImpl;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class BoardUtils {
    public static boolean isFRC = false;

    public static IBitBoard createBoard_WithPawnsCache() {
        return BoardUtils.createBoard_WithPawnsCache("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", PawnsModelEvalFactory.class.getName(), null, 1000);
    }

    public static IBitBoard createBoard_WithPawnsCache(IBoardConfig boardConfig) {
        return BoardUtils.createBoard_WithPawnsCache("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", boardConfig);
    }

    public static IBitBoard createBoard_WithPawnsCache(String fen) {
        return BoardUtils.createBoard_WithPawnsCache(fen, null);
    }

    public static IBitBoard createBoard_WithPawnsCache(String fen, IBoardConfig boardConfig) {
        return BoardUtils.createBoard_WithPawnsCache(fen, PawnsModelEvalFactory.class.getName(), boardConfig, 1000);
    }

    public static IBitBoard createBoard_WithPawnsCache(String fen, String cacheFactoryClassName, IBoardConfig boardConfig, int pawnsCacheSize) {
        return BoardUtils.createBoard_WithPawnsCache(fen, cacheFactoryClassName, boardConfig, pawnsCacheSize, true);
    }

    public static IBitBoard createBoard_WithPawnsCache(String fen, String cacheFactoryClassName, IBoardConfig boardConfig, int pawnsCacheSize, boolean impl1) {
        IBitBoard bitboard;
        if (impl1) {
            bitboard = new BoardImpl(fen, boardConfig, isFRC);
        } else {
            DataObjectFactory pawnsCacheFactory = null;
            try {
                pawnsCacheFactory = (DataObjectFactory)BoardUtils.class.getClassLoader().loadClass(cacheFactoryClassName).newInstance();
            }
            catch (Exception e) {
                throw new IllegalStateException(e);
            }
            PawnsEvalCache pawnsCache = new PawnsEvalCache(pawnsCacheFactory, pawnsCacheSize, false, (IBinarySemaphore)new BinarySemaphore_Dummy());
            bitboard = new BoardProxy_ReversedBBs(new Board(fen, pawnsCache, boardConfig));
        }
        if (boardConfig != null) {
            bitboard.setAttacksSupport(boardConfig.getFieldsStatesSupport(), boardConfig.getFieldsStatesSupport());
        }
        return bitboard;
    }

    public static final int[] getMoves(String[] pv, IBitBoard board) {
        int[] result = null;
        if (pv != null && pv.length > 0) {
            result = new int[pv.length];
            int cur = 0;
            for (String move : pv) {
                result[cur++] = board.getMoveOps().stringToMove(move.trim());
                board.makeMoveForward(result[cur - 1]);
            }
            for (int i = pv.length - 1; i >= 0; --i) {
                board.makeMoveBackward(result[i]);
            }
        }
        return result;
    }

    public static final String getPlayedMoves(IBitBoard bitboard) {
        Object result = "";
        int count = bitboard.getPlayedMovesCount();
        int[] moves = bitboard.getPlayedMoves();
        for (int i = 0; i < count; ++i) {
            int curMove = moves[i];
            StringBuilder message = new StringBuilder(32);
            message.append(bitboard.getMoveOps().moveToString(curMove));
            result = (String)result + message.toString() + " ";
        }
        return result;
    }

    public static String movesToString(int[] pv, IBitBoard bitboard) {
        Object pvStr = "";
        for (int i = 0; i < pv.length; ++i) {
            pvStr = (String)pvStr + bitboard.getMoveOps().moveToString(pv[i]);
            if (i == pv.length - 1) continue;
            pvStr = (String)pvStr + ", ";
        }
        return pvStr;
    }

    public static void playGameUCI(IBitBoard board, String movesSign) {
        ArrayList<String> moves = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(movesSign, " ");
        while (st.hasMoreTokens()) {
            moves.add(st.nextToken());
        }
        int size = moves.size();
        for (int i = 0; i < size; ++i) {
            String moveSign = (String)moves.get(i);
            if (moveSign.equals("...")) continue;
            int move = board.getMoveOps().stringToMove(moveSign);
            if (board.getMoveOps().isCastling(move)) {
                // empty if block
            }
            board.makeMoveForward(move);
        }
    }

    public static int parseSingleUCIMove(IBitBoard board, String moveSign) {
        int move = 0;
        BaseMoveList moves_list = new BaseMoveList();
        int movesCount = board.genAllMoves(moves_list);
        String fromFieldSign = moveSign.substring(0, 2).toLowerCase();
        String toFieldSign = moveSign.substring(2, 4).toLowerCase();
        String promTypeSign = moveSign.length() == 5 ? moveSign.substring(4, 5).toLowerCase() : null;
        int fromFieldID = Fields.getFieldID(fromFieldSign);
        int toFieldID = Fields.getFieldID(toFieldSign);
        int[] moves = moves_list.reserved_getMovesBuffer();
        for (int i = 0; i < movesCount; ++i) {
            int curMove = moves[i];
            int curFromID = board.getMoveOps().getFromFieldID(curMove);
            int curToID = board.getMoveOps().getToFieldID(curMove);
            if (fromFieldID != curFromID || toFieldID != curToID) continue;
            if (promTypeSign == null) {
                move = curMove;
                break;
            }
            if (BoardUtils.getPromotionTypeUCI(promTypeSign) != board.getMoveOps().getPromotionFigureType(curMove)) continue;
            move = curMove;
            break;
        }
        if (move == 0) {
            throw new IllegalStateException("moveSign=" + moveSign + "\r\n" + String.valueOf(board));
        }
        return move;
    }

    private static int getPromotionTypeUCI(String promTypeSign) {
        int type = -1;
        if (promTypeSign.equals("n")) {
            type = 2;
        } else if (promTypeSign.equals("b")) {
            type = 3;
        } else if (promTypeSign.equals("r")) {
            type = 4;
        } else if (promTypeSign.equals("q")) {
            type = 5;
        } else {
            throw new IllegalStateException("Invalid promotion figure type '" + promTypeSign + "'");
        }
        return type;
    }
}

