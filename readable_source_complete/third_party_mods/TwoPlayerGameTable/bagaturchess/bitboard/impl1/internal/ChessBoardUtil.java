/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl1.internal;

import bagaturchess.bitboard.impl1.internal.CastlingConfig;
import bagaturchess.bitboard.impl1.internal.CheckUtil;
import bagaturchess.bitboard.impl1.internal.ChessBoard;
import bagaturchess.bitboard.impl1.internal.ChessConstants;
import bagaturchess.bitboard.impl1.internal.EvalConstants;
import bagaturchess.bitboard.impl1.internal.MaterialUtil;
import bagaturchess.bitboard.impl1.internal.Util;
import bagaturchess.bitboard.impl1.internal.Zobrist;
import java.util.Arrays;

public class ChessBoardUtil {
    public static final String[] ALL_FIELD_NAMES = new String[]{"a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1", "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2", "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3", "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4", "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5", "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6", "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7", "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8"};

    public static ChessBoard getNewCB() {
        return ChessBoardUtil.getNewCB("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    }

    public static ChessBoard getNewCB(String fen) {
        String[] fenArray = fen.split(" ");
        ChessBoard cb = new ChessBoard();
        ChessBoardUtil.setFenValues(fenArray, cb);
        boolean[] castling_rights = new boolean[4];
        ChessBoardUtil.init(cb, castling_rights);
        cb.playedBoardStates.inc(cb.zobristKey);
        ChessBoardUtil.setCastling960Configuration(cb);
        if (fenArray.length > 2) {
            cb.playedBoardStates.dec(cb.zobristKey);
            cb.zobristKey ^= Zobrist.castling[cb.castlingRights];
            ChessBoardUtil.getCastlingRights(fenArray[2], cb.castlingConfig, castling_rights);
            ChessBoardUtil.setCastlingRights(castling_rights, cb);
            cb.zobristKey ^= Zobrist.castling[cb.castlingRights];
            cb.playedBoardStates.inc(cb.zobristKey);
        }
        return cb;
    }

    private static void setFenValues(String[] fenArray, ChessBoard cb) {
        cb.moveCounter = 0;
        ChessBoardUtil.setPieces(cb, fenArray[0]);
        int n = cb.colorToMove = fenArray[1].equals("w") ? 0 : 1;
        if (fenArray.length > 3) {
            cb.epIndex = fenArray[3].equals("-") || fenArray[3].equals("\u2013") ? 0 : 104 - fenArray[3].charAt(0) + 8 * (Integer.parseInt(fenArray[3].substring(1)) - 1);
        }
        if (fenArray.length > 4) {
            String lastCaptureOrPawnMoveBefore = fenArray[4].equals("-") ? "1" : fenArray[4];
            cb.lastCaptureOrPawnMoveBefore = Integer.parseInt(lastCaptureOrPawnMoveBefore);
            cb.moveCounter = Integer.parseInt(fenArray[5].equals("-") ? "1" : fenArray[5]) * 2;
            if (cb.colorToMove == 1) {
                ++cb.moveCounter;
            }
        } else {
            int pawnsNotAtStartingPosition = 16 - Long.bitCount(cb.pieces[0][1] & 0xFF00L) - Long.bitCount(cb.pieces[1][1] & 0xFF000000000000L);
            cb.moveCounter = pawnsNotAtStartingPosition * 2;
        }
    }

    private static void setCastlingRights(boolean[] rights, ChessBoard cb) {
        cb.castlingRights = 15;
        if (!rights[0]) {
            cb.castlingRights &= 7;
        }
        if (!rights[1]) {
            cb.castlingRights &= 0xB;
        }
        if (!rights[2]) {
            cb.castlingRights &= 0xD;
        }
        if (!rights[3]) {
            cb.castlingRights &= 0xE;
        }
    }

    private static final void getCastlingRights(String str, CastlingConfig castlingConfig, boolean[] result) {
        if (str.length() == 0) {
            return;
        }
        if (str.contains("K") || str.contains("Q") || str.contains("k") || str.contains("q")) {
            if (str.contains("K")) {
                result[0] = true;
            }
            if (str.contains("Q")) {
                result[1] = true;
            }
            if (str.contains("k")) {
                result[2] = true;
            }
            if (str.contains("q")) {
                result[3] = true;
            }
        } else {
            String rook_file_kingside_w = "" + (char)(104 - castlingConfig.from_SquareID_rook_kingside_w % 8);
            String rook_file_queenside_w = "" + (char)(104 - castlingConfig.from_SquareID_rook_queenside_w % 8);
            String rook_file_kingside_b = "" + (char)(104 - castlingConfig.from_SquareID_rook_kingside_b % 8);
            String rook_file_queenside_b = "" + (char)(104 - castlingConfig.from_SquareID_rook_queenside_b % 8);
            for (int i = 0; i < str.length(); ++i) {
                String current_file_name = str.substring(i, i + 1);
                if (current_file_name.equals(current_file_name.toUpperCase())) {
                    if ((current_file_name = current_file_name.toLowerCase()).equals(rook_file_kingside_w)) {
                        result[0] = true;
                    }
                    if (!current_file_name.equals(rook_file_queenside_w)) continue;
                    result[1] = true;
                    continue;
                }
                if (current_file_name.equals(rook_file_kingside_b)) {
                    result[2] = true;
                }
                if (!current_file_name.equals(rook_file_queenside_b)) continue;
                result[3] = true;
            }
        }
    }

    private static void calculateZobristKeys(ChessBoard cb) {
        cb.zobristKey = 0L;
        for (int color = 0; color < 2; ++color) {
            for (int piece = 1; piece <= 6; ++piece) {
                for (long pieces = cb.pieces[color][piece]; pieces != 0L; pieces &= pieces - 1L) {
                    cb.zobristKey ^= Zobrist.piece[Long.numberOfTrailingZeros(pieces)][color][piece];
                }
            }
        }
        cb.zobristKey ^= Zobrist.castling[cb.castlingRights];
        if (cb.colorToMove == 0) {
            cb.zobristKey ^= Zobrist.sideToMove;
        }
        cb.zobristKey ^= Zobrist.epIndex[cb.epIndex];
    }

    private static void calculatePawnZobristKeys(ChessBoard cb) {
        long pieces;
        cb.pawnZobristKey = 0L;
        for (pieces = cb.pieces[0][1]; pieces != 0L; pieces &= pieces - 1L) {
            cb.pawnZobristKey ^= Zobrist.piece[Long.numberOfTrailingZeros(pieces)][0][1];
        }
        for (pieces = cb.pieces[1][1]; pieces != 0L; pieces &= pieces - 1L) {
            cb.pawnZobristKey ^= Zobrist.piece[Long.numberOfTrailingZeros(pieces)][1][1];
        }
    }

    private static void setPieces(ChessBoard cb, String fenPieces) {
        for (int color = 0; color < 2; ++color) {
            for (int pieceIndex = 1; pieceIndex <= 6; ++pieceIndex) {
                cb.pieces[color][pieceIndex] = 0L;
            }
        }
        int positionCount = 63;
        block18: for (int i = 0; i < fenPieces.length(); ++i) {
            char character = fenPieces.charAt(i);
            switch (character) {
                case '/': {
                    continue block18;
                }
                case '1': 
                case '2': 
                case '3': 
                case '4': 
                case '5': 
                case '6': 
                case '7': 
                case '8': {
                    positionCount -= Character.digit(character, 10);
                    continue block18;
                }
                case 'P': {
                    long[] lArray = cb.pieces[0];
                    lArray[1] = lArray[1] | Util.POWER_LOOKUP[positionCount--];
                    continue block18;
                }
                case 'N': {
                    long[] lArray = cb.pieces[0];
                    lArray[2] = lArray[2] | Util.POWER_LOOKUP[positionCount--];
                    continue block18;
                }
                case 'B': {
                    long[] lArray = cb.pieces[0];
                    lArray[3] = lArray[3] | Util.POWER_LOOKUP[positionCount--];
                    continue block18;
                }
                case 'R': {
                    long[] lArray = cb.pieces[0];
                    lArray[4] = lArray[4] | Util.POWER_LOOKUP[positionCount--];
                    continue block18;
                }
                case 'Q': {
                    long[] lArray = cb.pieces[0];
                    lArray[5] = lArray[5] | Util.POWER_LOOKUP[positionCount--];
                    continue block18;
                }
                case 'K': {
                    long[] lArray = cb.pieces[0];
                    lArray[6] = lArray[6] | Util.POWER_LOOKUP[positionCount--];
                    continue block18;
                }
                case 'p': {
                    long[] lArray = cb.pieces[1];
                    lArray[1] = lArray[1] | Util.POWER_LOOKUP[positionCount--];
                    continue block18;
                }
                case 'n': {
                    long[] lArray = cb.pieces[1];
                    lArray[2] = lArray[2] | Util.POWER_LOOKUP[positionCount--];
                    continue block18;
                }
                case 'b': {
                    long[] lArray = cb.pieces[1];
                    lArray[3] = lArray[3] | Util.POWER_LOOKUP[positionCount--];
                    continue block18;
                }
                case 'r': {
                    long[] lArray = cb.pieces[1];
                    lArray[4] = lArray[4] | Util.POWER_LOOKUP[positionCount--];
                    continue block18;
                }
                case 'q': {
                    long[] lArray = cb.pieces[1];
                    lArray[5] = lArray[5] | Util.POWER_LOOKUP[positionCount--];
                    continue block18;
                }
                case 'k': {
                    long[] lArray = cb.pieces[1];
                    lArray[6] = lArray[6] | Util.POWER_LOOKUP[positionCount--];
                }
            }
        }
    }

    static void init(ChessBoard cb, boolean[] rights) {
        ChessBoardUtil.calculateMaterialZobrist(cb);
        cb.updateKingValues(0, Long.numberOfTrailingZeros(cb.pieces[0][6]));
        cb.updateKingValues(1, Long.numberOfTrailingZeros(cb.pieces[1][6]));
        cb.colorToMoveInverse = 1 - cb.colorToMove;
        cb.friendlyPieces[0] = cb.pieces[0][1] | cb.pieces[0][3] | cb.pieces[0][2] | cb.pieces[0][6] | cb.pieces[0][4] | cb.pieces[0][5];
        cb.friendlyPieces[1] = cb.pieces[1][1] | cb.pieces[1][3] | cb.pieces[1][2] | cb.pieces[1][6] | cb.pieces[1][4] | cb.pieces[1][5];
        cb.allPieces = cb.friendlyPieces[0] | cb.friendlyPieces[1];
        cb.emptySpaces = cb.allPieces ^ 0xFFFFFFFFFFFFFFFFL;
        Arrays.fill(cb.pieceIndexes, 0);
        for (int color = 0; color < cb.pieces.length; ++color) {
            for (int pieceIndex = 1; pieceIndex < cb.pieces[0].length; ++pieceIndex) {
                for (long piece = cb.pieces[color][pieceIndex]; piece != 0L; piece &= piece - 1L) {
                    cb.pieceIndexes[Long.numberOfTrailingZeros((long)piece)] = pieceIndex;
                }
            }
        }
        cb.checkingPieces = CheckUtil.getCheckingPieces(cb);
        cb.setPinnedAndDiscoPieces();
        cb.psqtScore_mg = 0;
        cb.psqtScore_eg = 0;
        ChessBoardUtil.calculatePositionScores(cb);
        cb.material_factor_white = Long.bitCount(cb.pieces[0][2]) * EvalConstants.PHASE[2] + Long.bitCount(cb.pieces[0][3]) * EvalConstants.PHASE[3] + Long.bitCount(cb.pieces[0][4]) * EvalConstants.PHASE[4] + Long.bitCount(cb.pieces[0][5]) * EvalConstants.PHASE[5];
        cb.material_factor_black = Long.bitCount(cb.pieces[1][2]) * EvalConstants.PHASE[2] + Long.bitCount(cb.pieces[1][3]) * EvalConstants.PHASE[3] + Long.bitCount(cb.pieces[1][4]) * EvalConstants.PHASE[4] + Long.bitCount(cb.pieces[1][5]) * EvalConstants.PHASE[5];
        ChessBoardUtil.setCastlingRights(rights, cb);
        ChessBoardUtil.calculatePawnZobristKeys(cb);
        ChessBoardUtil.calculateZobristKeys(cb);
    }

    private static final void setCastling960Configuration(ChessBoard cb) {
        CastlingConfig castlingConfig;
        long bb_king_w = cb.pieces[0][6];
        long bb_king_b = cb.pieces[1][6];
        if (bb_king_w == 0L) {
            throw new IllegalStateException("No white king");
        }
        if (bb_king_w == 0L) {
            throw new IllegalStateException("No black king");
        }
        int king_w_square_id = 3;
        int king_b_square_id = 59;
        int count_w_kings = 0;
        while (bb_king_w != 0L) {
            king_w_square_id = Long.numberOfTrailingZeros(bb_king_w);
            bb_king_w &= bb_king_w - 1L;
            ++count_w_kings;
        }
        int count_b_kings = 0;
        while (bb_king_b != 0L) {
            king_b_square_id = Long.numberOfTrailingZeros(bb_king_b);
            bb_king_b &= bb_king_b - 1L;
            ++count_b_kings;
        }
        if (count_w_kings > 2) {
            throw new IllegalStateException("More than 2 white king");
        }
        if (count_b_kings > 2) {
            throw new IllegalStateException("More than 2 black king");
        }
        int rook_kingside_w = 0;
        for (int square_id = king_w_square_id; square_id >= 0; --square_id) {
            int source_piece_type = cb.pieceIndexes[square_id];
            if (source_piece_type != 4) continue;
            rook_kingside_w = square_id;
            break;
        }
        int rook_queenside_w = 7;
        for (int square_id = king_w_square_id; square_id <= 7; ++square_id) {
            int source_piece_type = cb.pieceIndexes[square_id];
            if (source_piece_type != 4) continue;
            rook_queenside_w = square_id;
            break;
        }
        int rook_kingside_b = 56;
        for (int square_id = king_b_square_id; square_id >= 56; --square_id) {
            int source_piece_type = cb.pieceIndexes[square_id];
            if (source_piece_type != 4) continue;
            rook_kingside_b = square_id;
            break;
        }
        int rook_queenside_b = 63;
        for (int square_id = king_b_square_id; square_id <= 63; ++square_id) {
            int source_piece_type = cb.pieceIndexes[square_id];
            if (source_piece_type != 4) continue;
            rook_queenside_b = square_id;
            break;
        }
        cb.castlingConfig = castlingConfig = new CastlingConfig(king_w_square_id, rook_kingside_w, rook_queenside_w, king_b_square_id, rook_kingside_b, rook_queenside_b);
    }

    private static void calculatePositionScores(ChessBoard cb) {
        for (int color = 0; color <= 1; ++color) {
            for (int pieceType = 1; pieceType <= 6; ++pieceType) {
                for (long piece = cb.pieces[color][pieceType]; piece != 0L; piece &= piece - 1L) {
                    cb.psqtScore_mg += EvalConstants.PSQT_MG[pieceType][color][Long.numberOfTrailingZeros(piece)];
                    cb.psqtScore_eg += EvalConstants.PSQT_EG[pieceType][color][Long.numberOfTrailingZeros(piece)];
                }
            }
        }
    }

    private static void calculateMaterialZobrist(ChessBoard cb) {
        cb.materialKey = 0;
        for (int color = 0; color <= 1; ++color) {
            for (int piece = 1; piece <= 5; ++piece) {
                cb.materialKey += Long.bitCount(cb.pieces[color][piece]) * MaterialUtil.VALUES[color][piece];
            }
        }
    }

    public static String toString(ChessBoard cb, boolean add_ep) {
        StringBuilder sb = new StringBuilder();
        for (int i = 63; i >= 0; --i) {
            if ((cb.friendlyPieces[0] & Util.POWER_LOOKUP[i]) != 0L) {
                sb.append(ChessConstants.FEN_WHITE_PIECES[cb.pieceIndexes[i]]);
            } else {
                sb.append(ChessConstants.FEN_BLACK_PIECES[cb.pieceIndexes[i]]);
            }
            if (i % 8 != 0 || i == 0) continue;
            sb.append("/");
        }
        String colorToMove = cb.colorToMove == 0 ? "w" : "b";
        sb.append(" ").append(colorToMove).append(" ");
        if (cb.castlingRights == 0) {
            sb.append("-");
        } else {
            if ((cb.castlingRights & 8) != 0) {
                sb.append("K");
            }
            if ((cb.castlingRights & 4) != 0) {
                sb.append("Q");
            }
            if ((cb.castlingRights & 2) != 0) {
                sb.append("k");
            }
            if ((cb.castlingRights & 1) != 0) {
                sb.append("q");
            }
        }
        Object fen = sb.toString();
        fen = ((String)fen).replaceAll("11111111", "8");
        fen = ((String)fen).replaceAll("1111111", "7");
        fen = ((String)fen).replaceAll("111111", "6");
        fen = ((String)fen).replaceAll("11111", "5");
        fen = ((String)fen).replaceAll("1111", "4");
        fen = ((String)fen).replaceAll("111", "3");
        fen = ((String)fen).replaceAll("11", "2");
        fen = (String)fen + " ";
        fen = add_ep && cb.epIndex != 0 ? (String)fen + ALL_FIELD_NAMES[cb.epIndex] : (String)fen + "-";
        fen = (String)fen + " ";
        fen = (String)fen + cb.lastCaptureOrPawnMoveBefore;
        fen = (String)fen + " ";
        fen = (String)fen + ((cb.playedMovesCount + 1) / 2 + 1);
        return fen;
    }
}

