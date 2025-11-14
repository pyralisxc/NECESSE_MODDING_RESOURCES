/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl2;

import bagaturchess.bitboard.impl1.internal.CastlingConfig;
import bagaturchess.bitboard.impl1.internal.Zobrist;
import bagaturchess.bitboard.impl2.CheckUtil;
import bagaturchess.bitboard.impl2.ChessBoard;
import bagaturchess.bitboard.impl2.ChessConstants;
import java.util.Arrays;

public class ChessBoardBuilder {
    public static final String[] FEN_WHITE_PIECES = new String[]{"1", "P", "N", "B", "R", "Q", "K"};
    public static final String[] FEN_BLACK_PIECES = new String[]{"1", "p", "n", "b", "r", "q", "k"};
    public static final String FEN_START = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    public static final String[] ALL_FIELD_NAMES = new String[]{"a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1", "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2", "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3", "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4", "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5", "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6", "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7", "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8"};

    public static ChessBoard getNewCB() {
        return ChessBoardBuilder.getNewCB(FEN_START);
    }

    public static ChessBoard getNewCB(String fen) {
        String[] fenArray = fen.split(" ");
        ChessBoard cb = new ChessBoard();
        ChessBoardBuilder.setFenValues(fenArray, cb);
        boolean[] castling_rights = new boolean[4];
        ChessBoardBuilder.init(cb, castling_rights);
        cb.played_board_states.inc(cb.zobrist_key);
        ChessBoardBuilder.setCastling960Configuration(cb);
        if (fenArray.length > 2) {
            cb.played_board_states.dec(cb.zobrist_key);
            cb.zobrist_key ^= Zobrist.castling[cb.castling_rights];
            ChessBoardBuilder.getCastlingRights(fenArray[2], cb.castling_config, castling_rights);
            ChessBoardBuilder.setCastlingRights(castling_rights, cb);
            cb.zobrist_key ^= Zobrist.castling[cb.castling_rights];
            cb.played_board_states.inc(cb.zobrist_key);
        }
        return cb;
    }

    private static void setFenValues(String[] fenArray, ChessBoard cb) {
        cb.played_moves_count = 0;
        ChessBoardBuilder.setPieces(cb, fenArray[0]);
        int n = cb.color_to_move = fenArray[1].equals("w") ? 0 : 1;
        if (fenArray.length > 3) {
            cb.ep_index = fenArray[3].equals("-") || fenArray[3].equals("\u2013") ? 0 : 104 - fenArray[3].charAt(0) + 8 * (Integer.parseInt(fenArray[3].substring(1)) - 1);
        }
        if (fenArray.length > 4) {
            String lastCaptureOrPawnMoveBefore = fenArray[4].equals("-") ? "1" : fenArray[4];
            cb.last_capture_or_pawn_move_before = Integer.parseInt(lastCaptureOrPawnMoveBefore);
            cb.played_moves_count = Integer.parseInt(fenArray[5].equals("-") ? "1" : fenArray[5]) * 2;
            if (cb.color_to_move == 1) {
                ++cb.played_moves_count;
            }
        }
    }

    private static void setCastlingRights(boolean[] rights, ChessBoard cb) {
        cb.castling_rights = 15;
        if (!rights[0]) {
            cb.castling_rights &= 7;
        }
        if (!rights[1]) {
            cb.castling_rights &= 0xB;
        }
        if (!rights[2]) {
            cb.castling_rights &= 0xD;
        }
        if (!rights[3]) {
            cb.castling_rights &= 0xE;
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
        cb.zobrist_key = 0L;
        for (int color = 0; color < 2; ++color) {
            for (int piece = 1; piece <= 6; ++piece) {
                for (long pieces = cb.getPieces(color, piece); pieces != 0L; pieces &= pieces - 1L) {
                    cb.zobrist_key ^= Zobrist.piece[Long.numberOfTrailingZeros(pieces)][color][piece];
                }
            }
        }
        cb.zobrist_key ^= Zobrist.castling[cb.castling_rights];
        if (cb.color_to_move == 0) {
            cb.zobrist_key ^= Zobrist.sideToMove;
        }
        cb.zobrist_key ^= Zobrist.epIndex[cb.ep_index];
    }

    private static void setPieces(ChessBoard cb, String fenPieces) {
        for (int color = 0; color < 2; ++color) {
            for (int type = 1; type <= 6; ++type) {
                cb.setPieces(color, type, 0L);
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
                    cb.w_pawns |= ChessConstants.POWER_LOOKUP[positionCount--];
                    continue block18;
                }
                case 'N': {
                    cb.w_knights |= ChessConstants.POWER_LOOKUP[positionCount--];
                    continue block18;
                }
                case 'B': {
                    cb.w_bishops |= ChessConstants.POWER_LOOKUP[positionCount--];
                    continue block18;
                }
                case 'R': {
                    cb.w_rooks |= ChessConstants.POWER_LOOKUP[positionCount--];
                    continue block18;
                }
                case 'Q': {
                    cb.w_queens |= ChessConstants.POWER_LOOKUP[positionCount--];
                    continue block18;
                }
                case 'K': {
                    cb.w_king |= ChessConstants.POWER_LOOKUP[positionCount--];
                    continue block18;
                }
                case 'p': {
                    cb.b_pawns |= ChessConstants.POWER_LOOKUP[positionCount--];
                    continue block18;
                }
                case 'n': {
                    cb.b_knights |= ChessConstants.POWER_LOOKUP[positionCount--];
                    continue block18;
                }
                case 'b': {
                    cb.b_bishops |= ChessConstants.POWER_LOOKUP[positionCount--];
                    continue block18;
                }
                case 'r': {
                    cb.b_rooks |= ChessConstants.POWER_LOOKUP[positionCount--];
                    continue block18;
                }
                case 'q': {
                    cb.b_queens |= ChessConstants.POWER_LOOKUP[positionCount--];
                    continue block18;
                }
                case 'k': {
                    cb.b_king |= ChessConstants.POWER_LOOKUP[positionCount--];
                }
            }
        }
    }

    private static final void init(ChessBoard cb, boolean[] rights) {
        cb.w_all = cb.w_pawns | cb.w_bishops | cb.w_knights | cb.w_king | cb.w_rooks | cb.w_queens;
        cb.b_all = cb.b_pawns | cb.b_bishops | cb.b_knights | cb.b_king | cb.b_rooks | cb.b_queens;
        cb.all_pieces = cb.w_all | cb.b_all;
        cb.empty_spaces = cb.all_pieces ^ 0xFFFFFFFFFFFFFFFFL;
        Arrays.fill(cb.piece_indexes, 0);
        for (int color = 0; color <= 1; ++color) {
            for (int type = 1; type <= 6; ++type) {
                for (long piece = cb.getPieces(color, type); piece != 0L; piece &= piece - 1L) {
                    cb.piece_indexes[Long.numberOfTrailingZeros((long)piece)] = type;
                }
            }
        }
        cb.checking_pieces = CheckUtil.getCheckingPieces(cb);
        cb.setPinnedAndDiscoPieces();
        ChessBoardBuilder.setCastlingRights(rights, cb);
        ChessBoardBuilder.calculateZobristKeys(cb);
    }

    private static final void setCastling960Configuration(ChessBoard cb) {
        CastlingConfig castlingConfig;
        long bb_king_w = cb.getPieces(0, 6);
        long bb_king_b = cb.getPieces(1, 6);
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
            int source_piece_type = cb.piece_indexes[square_id];
            if (source_piece_type != 4) continue;
            rook_kingside_w = square_id;
            break;
        }
        int rook_queenside_w = 7;
        for (int square_id = king_w_square_id; square_id <= 7; ++square_id) {
            int source_piece_type = cb.piece_indexes[square_id];
            if (source_piece_type != 4) continue;
            rook_queenside_w = square_id;
            break;
        }
        int rook_kingside_b = 56;
        for (int square_id = king_b_square_id; square_id >= 56; --square_id) {
            int source_piece_type = cb.piece_indexes[square_id];
            if (source_piece_type != 4) continue;
            rook_kingside_b = square_id;
            break;
        }
        int rook_queenside_b = 63;
        for (int square_id = king_b_square_id; square_id <= 63; ++square_id) {
            int source_piece_type = cb.piece_indexes[square_id];
            if (source_piece_type != 4) continue;
            rook_queenside_b = square_id;
            break;
        }
        cb.castling_config = castlingConfig = new CastlingConfig(king_w_square_id, rook_kingside_w, rook_queenside_w, king_b_square_id, rook_kingside_b, rook_queenside_b);
    }

    public static String toString(ChessBoard cb, boolean add_ep) {
        StringBuilder sb = new StringBuilder();
        for (int i = 63; i >= 0; --i) {
            if ((cb.getPieces_All(0) & ChessConstants.POWER_LOOKUP[i]) != 0L) {
                sb.append(FEN_WHITE_PIECES[cb.piece_indexes[i]]);
            } else {
                sb.append(FEN_BLACK_PIECES[cb.piece_indexes[i]]);
            }
            if (i % 8 != 0 || i == 0) continue;
            sb.append("/");
        }
        String colorToMove = cb.color_to_move == 0 ? "w" : "b";
        sb.append(" ").append(colorToMove).append(" ");
        if (cb.castling_rights == 0) {
            sb.append("-");
        } else {
            if ((cb.castling_rights & 8) != 0) {
                sb.append("K");
            }
            if ((cb.castling_rights & 4) != 0) {
                sb.append("Q");
            }
            if ((cb.castling_rights & 2) != 0) {
                sb.append("k");
            }
            if ((cb.castling_rights & 1) != 0) {
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
        fen = add_ep && cb.ep_index != 0 ? (String)fen + ALL_FIELD_NAMES[cb.ep_index] : (String)fen + "-";
        fen = (String)fen + " ";
        fen = (String)fen + cb.last_capture_or_pawn_move_before;
        fen = (String)fen + " ";
        fen = (String)fen + ((cb.played_moves_count + 1) / 2 + 1);
        return fen;
    }
}

