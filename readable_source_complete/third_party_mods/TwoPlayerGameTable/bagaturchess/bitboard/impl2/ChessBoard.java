/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl2;

import bagaturchess.bitboard.api.IBaseEval;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IBoard;
import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.api.IFieldsAttacks;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.bitboard.api.IInternalMoveList;
import bagaturchess.bitboard.api.IMaterialFactor;
import bagaturchess.bitboard.api.IMaterialState;
import bagaturchess.bitboard.api.IMoveOps;
import bagaturchess.bitboard.api.IPiecesLists;
import bagaturchess.bitboard.api.IPlayerAttacks;
import bagaturchess.bitboard.api.ISEE;
import bagaturchess.bitboard.api.PawnsEvalCache;
import bagaturchess.bitboard.common.MoveListener;
import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.datastructs.StackLongInt;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEval;
import bagaturchess.bitboard.impl.movelist.BaseMoveList;
import bagaturchess.bitboard.impl.movelist.IMoveList;
import bagaturchess.bitboard.impl.state.PiecesList;
import bagaturchess.bitboard.impl1.internal.CastlingConfig;
import bagaturchess.bitboard.impl1.internal.Zobrist;
import bagaturchess.bitboard.impl2.CastlingUtil;
import bagaturchess.bitboard.impl2.CheckUtil;
import bagaturchess.bitboard.impl2.ChessBoardBuilder;
import bagaturchess.bitboard.impl2.ChessConstants;
import bagaturchess.bitboard.impl2.MagicUtil;
import bagaturchess.bitboard.impl2.MoveGeneration;
import bagaturchess.bitboard.impl2.MoveUtil;
import bagaturchess.bitboard.impl2.MoveWrapper;
import bagaturchess.bitboard.impl2.SEEUtil;
import java.util.Arrays;

public class ChessBoard
implements IBitBoard {
    public long w_king;
    public long w_queens;
    public long w_rooks;
    public long w_bishops;
    public long w_knights;
    public long w_pawns;
    public long w_all;
    public long b_king;
    public long b_queens;
    public long b_rooks;
    public long b_bishops;
    public long b_knights;
    public long b_pawns;
    public long b_all;
    public long all_pieces;
    public long empty_spaces;
    public long pinned_pieces;
    public long discovered_pieces;
    public long checking_pieces;
    public int color_to_move;
    public int ep_index;
    public int[] piece_indexes;
    public int castling_rights;
    public CastlingConfig castling_config;
    public int last_capture_or_pawn_move_before;
    public StackLongInt played_board_states = new StackLongInt(9631);
    public long zobrist_key;
    public int played_moves_count;
    public int moves_count;
    private BoardState[] states = new BoardState[2048];
    private int[] played_moves = new int[2048];
    private int[] buff_castling_rook_from_to = new int[2];
    private IMoveList hasMovesList = new BaseMoveList(333);
    private IMoveOps moveOps = new MoveOpsImpl();
    private IMaterialFactor materialFactor = new MaterialFactorImpl();
    private IPiecesLists pieces = new PiecesListsImpl(this);
    private MoveListener[] move_listeners = new MoveListener[0];
    private IMaterialState material_state = new MaterialStateImpl();
    private boolean isFRC = false;

    public ChessBoard() {
        this.piece_indexes = new int[64];
        for (int i = 0; i < this.states.length; ++i) {
            this.states[i] = new BoardState();
        }
    }

    public long getPieces(int color, int type) {
        switch (color) {
            case 0: {
                switch (type) {
                    case 1: {
                        return this.w_pawns;
                    }
                    case 2: {
                        return this.w_knights;
                    }
                    case 3: {
                        return this.w_bishops;
                    }
                    case 4: {
                        return this.w_rooks;
                    }
                    case 5: {
                        return this.w_queens;
                    }
                    case 6: {
                        return this.w_king;
                    }
                }
                throw new IllegalStateException("type=" + type);
            }
            case 1: {
                switch (type) {
                    case 1: {
                        return this.b_pawns;
                    }
                    case 2: {
                        return this.b_knights;
                    }
                    case 3: {
                        return this.b_bishops;
                    }
                    case 4: {
                        return this.b_rooks;
                    }
                    case 5: {
                        return this.b_queens;
                    }
                    case 6: {
                        return this.b_king;
                    }
                }
                throw new IllegalStateException("type=" + type);
            }
        }
        throw new IllegalStateException("color=" + color);
    }

    public void setPieces(int color, int type, long bitboard) {
        block0 : switch (color) {
            case 0: {
                switch (type) {
                    case 1: {
                        this.w_pawns = bitboard;
                        break block0;
                    }
                    case 2: {
                        this.w_knights = bitboard;
                        break block0;
                    }
                    case 3: {
                        this.w_bishops = bitboard;
                        break block0;
                    }
                    case 4: {
                        this.w_rooks = bitboard;
                        break block0;
                    }
                    case 5: {
                        this.w_queens = bitboard;
                        break block0;
                    }
                    case 6: {
                        this.w_king = bitboard;
                        break block0;
                    }
                }
                throw new IllegalStateException("type=" + type);
            }
            case 1: {
                switch (type) {
                    case 1: {
                        this.b_pawns = bitboard;
                        break block0;
                    }
                    case 2: {
                        this.b_knights = bitboard;
                        break block0;
                    }
                    case 3: {
                        this.b_bishops = bitboard;
                        break block0;
                    }
                    case 4: {
                        this.b_rooks = bitboard;
                        break block0;
                    }
                    case 5: {
                        this.b_queens = bitboard;
                        break block0;
                    }
                    case 6: {
                        this.b_king = bitboard;
                        break block0;
                    }
                }
                throw new IllegalStateException("type=" + type);
            }
            default: {
                throw new IllegalStateException("color=" + color);
            }
        }
    }

    public void xorPieces(int color, int type, long bitboard) {
        block0 : switch (color) {
            case 0: {
                switch (type) {
                    case 1: {
                        this.w_pawns ^= bitboard;
                        break block0;
                    }
                    case 2: {
                        this.w_knights ^= bitboard;
                        break block0;
                    }
                    case 3: {
                        this.w_bishops ^= bitboard;
                        break block0;
                    }
                    case 4: {
                        this.w_rooks ^= bitboard;
                        break block0;
                    }
                    case 5: {
                        this.w_queens ^= bitboard;
                        break block0;
                    }
                    case 6: {
                        this.w_king ^= bitboard;
                        break block0;
                    }
                }
                throw new IllegalStateException("type=" + type);
            }
            case 1: {
                switch (type) {
                    case 1: {
                        this.b_pawns ^= bitboard;
                        break block0;
                    }
                    case 2: {
                        this.b_knights ^= bitboard;
                        break block0;
                    }
                    case 3: {
                        this.b_bishops ^= bitboard;
                        break block0;
                    }
                    case 4: {
                        this.b_rooks ^= bitboard;
                        break block0;
                    }
                    case 5: {
                        this.b_queens ^= bitboard;
                        break block0;
                    }
                    case 6: {
                        this.b_king ^= bitboard;
                        break block0;
                    }
                }
                throw new IllegalStateException("type=" + type);
            }
            default: {
                throw new IllegalStateException("color=" + color);
            }
        }
    }

    public void orPieces(int color, int type, long bitboard) {
        block0 : switch (color) {
            case 0: {
                switch (type) {
                    case 1: {
                        this.w_pawns |= bitboard;
                        break block0;
                    }
                    case 2: {
                        this.w_knights |= bitboard;
                        break block0;
                    }
                    case 3: {
                        this.w_bishops |= bitboard;
                        break block0;
                    }
                    case 4: {
                        this.w_rooks |= bitboard;
                        break block0;
                    }
                    case 5: {
                        this.w_queens |= bitboard;
                        break block0;
                    }
                    case 6: {
                        this.w_king |= bitboard;
                        break block0;
                    }
                }
                throw new IllegalStateException("type=" + type);
            }
            case 1: {
                switch (type) {
                    case 1: {
                        this.b_pawns |= bitboard;
                        break block0;
                    }
                    case 2: {
                        this.b_knights |= bitboard;
                        break block0;
                    }
                    case 3: {
                        this.b_bishops |= bitboard;
                        break block0;
                    }
                    case 4: {
                        this.b_rooks |= bitboard;
                        break block0;
                    }
                    case 5: {
                        this.b_queens |= bitboard;
                        break block0;
                    }
                    case 6: {
                        this.b_king |= bitboard;
                        break block0;
                    }
                }
                throw new IllegalStateException("type=" + type);
            }
            default: {
                throw new IllegalStateException("color=" + color);
            }
        }
    }

    public long getPiecesOfSideToMove(int type) {
        return this.getPieces(this.color_to_move, type);
    }

    public long getPiecesOfSideNotToMove(int type) {
        return this.getPieces(1 - this.color_to_move, type);
    }

    public long getPieces_All(int color) {
        return color == 0 ? this.w_all : this.b_all;
    }

    public long getPiecesOfSideToMove_All() {
        return this.getPieces_All(this.color_to_move);
    }

    public long getPiecesOfSideNotToMove_All() {
        return this.getPieces_All(1 - this.color_to_move);
    }

    public int getKingIndex(int color) {
        int index;
        int n = index = color == 0 ? Long.numberOfTrailingZeros(this.w_king) : Long.numberOfTrailingZeros(this.b_king);
        if (index == 64) {
            throw new IllegalStateException("No king: color=" + color);
        }
        return index;
    }

    public int getKingIndexOfSideToMove() {
        return this.getKingIndex(this.color_to_move);
    }

    public int getKingIndexOfSideNotToMove() {
        return this.getKingIndex(1 - this.color_to_move);
    }

    public void doMove(int move) {
        if (MoveUtil.isCastlingMove(move)) {
            this.doCastling960(move);
            return;
        }
        BoardState state_backup = this.states[this.moves_count];
        state_backup.pinned_pieces = this.pinned_pieces;
        state_backup.discovered_pieces = this.discovered_pieces;
        state_backup.checking_pieces = this.checking_pieces;
        state_backup.ep_index = this.ep_index;
        state_backup.castling_rights = this.castling_rights;
        state_backup.last_capture_or_pawn_move_before = this.last_capture_or_pawn_move_before++;
        state_backup.zobrist_key = this.zobrist_key;
        this.played_moves[this.moves_count] = move;
        ++this.moves_count;
        ++this.played_moves_count;
        int fromIndex = MoveUtil.getFromIndex(move);
        int toIndex = MoveUtil.getToIndex(move);
        long toMask = 1L << toIndex;
        long fromToMask = 1L << fromIndex ^ toMask;
        int sourcePieceIndex = MoveUtil.getSourcePieceIndex(move);
        int attackedPieceIndex = MoveUtil.getAttackedPieceIndex(move);
        if (fromIndex == toIndex) {
            throw new IllegalStateException("doMove: fromIndex == toIndex");
        }
        if (attackedPieceIndex != 0 || sourcePieceIndex == 1) {
            this.last_capture_or_pawn_move_before = 0;
        }
        this.zobrist_key ^= Zobrist.piece[fromIndex][this.color_to_move][sourcePieceIndex] ^ Zobrist.piece[toIndex][this.color_to_move][sourcePieceIndex] ^ Zobrist.sideToMove;
        if (this.ep_index != 0) {
            this.zobrist_key ^= Zobrist.epIndex[this.ep_index];
            this.ep_index = 0;
        }
        if (this.color_to_move == 0) {
            this.w_all ^= fromToMask;
        } else {
            this.b_all ^= fromToMask;
        }
        this.xorPieces(this.color_to_move, sourcePieceIndex, fromToMask);
        this.piece_indexes[fromIndex] = 0;
        this.piece_indexes[toIndex] = sourcePieceIndex;
        switch (sourcePieceIndex) {
            case 1: {
                if (MoveUtil.isPromotion(move)) {
                    this.xorPieces(this.color_to_move, 1, toMask);
                    this.orPieces(this.color_to_move, MoveUtil.getMoveType(move), toMask);
                    this.piece_indexes[toIndex] = MoveUtil.getMoveType(move);
                    this.zobrist_key ^= Zobrist.piece[toIndex][this.color_to_move][1] ^ Zobrist.piece[toIndex][this.color_to_move][MoveUtil.getMoveType(move)];
                    break;
                }
                if (ChessConstants.IN_BETWEEN[fromIndex][toIndex] == 0L || (ChessConstants.PAWN_ATTACKS[this.color_to_move][Long.numberOfTrailingZeros(ChessConstants.IN_BETWEEN[fromIndex][toIndex])] & this.getPieces(1 - this.color_to_move, 1)) == 0L) break;
                this.ep_index = Long.numberOfTrailingZeros(ChessConstants.IN_BETWEEN[fromIndex][toIndex]);
                this.zobrist_key ^= Zobrist.epIndex[this.ep_index];
                break;
            }
            case 4: {
                if (this.castling_rights == 0) break;
                this.zobrist_key ^= Zobrist.castling[this.castling_rights];
                this.castling_rights = CastlingUtil.getRookMovedOrAttackedCastlingRights(this.castling_rights, fromIndex, this.castling_config);
                this.zobrist_key ^= Zobrist.castling[this.castling_rights];
                break;
            }
            case 6: {
                if (this.castling_rights == 0) break;
                if (MoveUtil.isCastlingMove(move)) {
                    throw new IllegalStateException("Castling");
                }
                this.zobrist_key ^= Zobrist.castling[this.castling_rights];
                this.castling_rights = CastlingUtil.getKingMovedCastlingRights(this.castling_rights, this.color_to_move, this.castling_config);
                this.zobrist_key ^= Zobrist.castling[this.castling_rights];
            }
        }
        switch (attackedPieceIndex) {
            case 0: {
                break;
            }
            case 1: {
                if (MoveUtil.isEPMove(move)) {
                    toMask = ChessConstants.POWER_LOOKUP[toIndex += ChessConstants.COLOR_FACTOR_8[1 - this.color_to_move]];
                    this.piece_indexes[toIndex] = 0;
                }
                if (1 - this.color_to_move == 0) {
                    this.w_all ^= toMask;
                } else {
                    this.b_all ^= toMask;
                }
                this.xorPieces(1 - this.color_to_move, 1, toMask);
                this.zobrist_key ^= Zobrist.piece[toIndex][1 - this.color_to_move][1];
                break;
            }
            case 4: {
                if (this.castling_rights != 0) {
                    this.zobrist_key ^= Zobrist.castling[this.castling_rights];
                    this.castling_rights = CastlingUtil.getRookMovedOrAttackedCastlingRights(this.castling_rights, toIndex, this.castling_config);
                    this.zobrist_key ^= Zobrist.castling[this.castling_rights];
                }
            }
            default: {
                if (1 - this.color_to_move == 0) {
                    this.w_all ^= toMask;
                } else {
                    this.b_all ^= toMask;
                }
                this.xorPieces(1 - this.color_to_move, attackedPieceIndex, toMask);
                this.zobrist_key ^= Zobrist.piece[toIndex][1 - this.color_to_move][attackedPieceIndex];
            }
        }
        this.all_pieces = this.w_all | this.b_all;
        this.empty_spaces = this.all_pieces ^ 0xFFFFFFFFFFFFFFFFL;
        this.color_to_move = 1 - this.color_to_move;
        this.checking_pieces = this.isDiscoveredMove(fromIndex) ? CheckUtil.getCheckingPieces(this) : (MoveUtil.isNormalMove(move) ? CheckUtil.getCheckingPieces(this, sourcePieceIndex) : CheckUtil.getCheckingPieces(this));
        this.setPinnedAndDiscoPieces();
        this.played_board_states.inc(this.zobrist_key);
    }

    private void doCastling960(int move) {
        BoardState state_backup = this.states[this.moves_count];
        state_backup.pinned_pieces = this.pinned_pieces;
        state_backup.discovered_pieces = this.discovered_pieces;
        state_backup.checking_pieces = this.checking_pieces;
        state_backup.ep_index = this.ep_index;
        state_backup.castling_rights = this.castling_rights;
        state_backup.last_capture_or_pawn_move_before = this.last_capture_or_pawn_move_before;
        state_backup.zobrist_key = this.zobrist_key;
        this.played_moves[this.moves_count] = move;
        ++this.moves_count;
        ++this.played_moves_count;
        int fromIndex_king = MoveUtil.getFromIndex(move);
        int toIndex_king = MoveUtil.getToIndex(move);
        int sourcePieceIndex = MoveUtil.getSourcePieceIndex(move);
        if (sourcePieceIndex != 6 || !MoveUtil.isCastlingMove(move)) {
            throw new IllegalStateException("sourcePieceIndex != KING || !MoveUtil.isCastlingMove(move)");
        }
        CastlingUtil.getRookFromToSquareIDs(this, toIndex_king, this.buff_castling_rook_from_to);
        int fromIndex_rook = this.buff_castling_rook_from_to[0];
        int toIndex_rook = this.buff_castling_rook_from_to[1];
        if (fromIndex_king == toIndex_king) {
            long bb = ChessConstants.POWER_LOOKUP[fromIndex_rook] | ChessConstants.POWER_LOOKUP[toIndex_rook];
            if (this.color_to_move == 0) {
                this.w_all ^= bb;
            } else {
                this.b_all ^= bb;
            }
            this.xorPieces(this.color_to_move, 4, bb);
            this.piece_indexes[fromIndex_rook] = 0;
            this.piece_indexes[toIndex_rook] = 4;
        } else if (fromIndex_rook == toIndex_rook) {
            long bb = ChessConstants.POWER_LOOKUP[fromIndex_king] | ChessConstants.POWER_LOOKUP[toIndex_king];
            if (this.color_to_move == 0) {
                this.w_all ^= bb;
            } else {
                this.b_all ^= bb;
            }
            this.xorPieces(this.color_to_move, 6, bb);
            this.piece_indexes[fromIndex_king] = 0;
            this.piece_indexes[toIndex_king] = 6;
        } else if (fromIndex_rook == toIndex_king && toIndex_rook == fromIndex_king) {
            long bb_king = ChessConstants.POWER_LOOKUP[fromIndex_king] | ChessConstants.POWER_LOOKUP[toIndex_king];
            this.xorPieces(this.color_to_move, 6, bb_king);
            long bb_rook = ChessConstants.POWER_LOOKUP[fromIndex_rook] | ChessConstants.POWER_LOOKUP[toIndex_rook];
            this.xorPieces(this.color_to_move, 4, bb_rook);
            this.piece_indexes[toIndex_rook] = 4;
            this.piece_indexes[toIndex_king] = 6;
        } else if (fromIndex_rook == toIndex_king) {
            long bb_king = ChessConstants.POWER_LOOKUP[fromIndex_king] | ChessConstants.POWER_LOOKUP[toIndex_king];
            this.xorPieces(this.color_to_move, 6, bb_king);
            long bb_rook = ChessConstants.POWER_LOOKUP[fromIndex_rook] | ChessConstants.POWER_LOOKUP[toIndex_rook];
            this.xorPieces(this.color_to_move, 4, bb_rook);
            if (this.color_to_move == 0) {
                this.w_all ^= ChessConstants.POWER_LOOKUP[fromIndex_king] | ChessConstants.POWER_LOOKUP[toIndex_rook];
            } else {
                this.b_all ^= ChessConstants.POWER_LOOKUP[fromIndex_king] | ChessConstants.POWER_LOOKUP[toIndex_rook];
            }
            this.piece_indexes[toIndex_rook] = 4;
            this.piece_indexes[toIndex_king] = 6;
            this.piece_indexes[fromIndex_king] = 0;
        } else if (toIndex_rook == fromIndex_king) {
            long bb_king = ChessConstants.POWER_LOOKUP[fromIndex_king] | ChessConstants.POWER_LOOKUP[toIndex_king];
            this.xorPieces(this.color_to_move, 6, bb_king);
            long bb_rook = ChessConstants.POWER_LOOKUP[fromIndex_rook] | ChessConstants.POWER_LOOKUP[toIndex_rook];
            this.xorPieces(this.color_to_move, 4, bb_rook);
            if (this.color_to_move == 0) {
                this.w_all ^= ChessConstants.POWER_LOOKUP[toIndex_king] | ChessConstants.POWER_LOOKUP[fromIndex_rook];
            } else {
                this.b_all ^= ChessConstants.POWER_LOOKUP[toIndex_king] | ChessConstants.POWER_LOOKUP[fromIndex_rook];
            }
            this.piece_indexes[toIndex_rook] = 4;
            this.piece_indexes[toIndex_king] = 6;
            this.piece_indexes[fromIndex_rook] = 0;
        } else {
            long bb_king = ChessConstants.POWER_LOOKUP[fromIndex_king] | ChessConstants.POWER_LOOKUP[toIndex_king];
            this.xorPieces(this.color_to_move, 6, bb_king);
            if (this.color_to_move == 0) {
                this.w_all ^= bb_king;
            } else {
                this.b_all ^= bb_king;
            }
            long bb_rook = ChessConstants.POWER_LOOKUP[fromIndex_rook] | ChessConstants.POWER_LOOKUP[toIndex_rook];
            this.xorPieces(this.color_to_move, 4, bb_rook);
            if (this.color_to_move == 0) {
                this.w_all ^= bb_rook;
            } else {
                this.b_all ^= bb_rook;
            }
            this.piece_indexes[fromIndex_rook] = 0;
            this.piece_indexes[fromIndex_king] = 0;
            this.piece_indexes[toIndex_rook] = 4;
            this.piece_indexes[toIndex_king] = 6;
        }
        this.zobrist_key ^= Zobrist.piece[fromIndex_king][this.color_to_move][6] ^ Zobrist.piece[toIndex_king][this.color_to_move][6];
        this.zobrist_key ^= Zobrist.piece[fromIndex_rook][this.color_to_move][4] ^ Zobrist.piece[toIndex_rook][this.color_to_move][4];
        if (this.ep_index != 0) {
            this.zobrist_key ^= Zobrist.epIndex[this.ep_index];
            this.ep_index = 0;
        }
        this.zobrist_key ^= Zobrist.sideToMove;
        if (this.castling_rights != 0) {
            this.zobrist_key ^= Zobrist.castling[this.castling_rights];
            this.castling_rights = CastlingUtil.getKingMovedCastlingRights(this.castling_rights, this.color_to_move, this.castling_config);
            this.zobrist_key ^= Zobrist.castling[this.castling_rights];
        }
        this.all_pieces = this.w_all | this.b_all;
        this.empty_spaces = this.all_pieces ^ 0xFFFFFFFFFFFFFFFFL;
        this.color_to_move = 1 - this.color_to_move;
        this.checking_pieces = CheckUtil.getCheckingPieces(this);
        this.setPinnedAndDiscoPieces();
        this.played_board_states.inc(this.zobrist_key);
    }

    public void undoMove(int move) {
        if (MoveUtil.isCastlingMove(move)) {
            this.undoCastling960(move);
            return;
        }
        this.played_board_states.dec(this.zobrist_key);
        --this.played_moves_count;
        --this.moves_count;
        BoardState state_backup = this.states[this.moves_count];
        this.pinned_pieces = state_backup.pinned_pieces;
        this.discovered_pieces = state_backup.discovered_pieces;
        this.checking_pieces = state_backup.checking_pieces;
        this.ep_index = state_backup.ep_index;
        this.castling_rights = state_backup.castling_rights;
        this.last_capture_or_pawn_move_before = state_backup.last_capture_or_pawn_move_before;
        this.zobrist_key = state_backup.zobrist_key;
        int fromIndex = MoveUtil.getFromIndex(move);
        int toIndex = MoveUtil.getToIndex(move);
        long toMask = 1L << toIndex;
        long fromToMask = 1L << fromIndex ^ toMask;
        int sourcePieceIndex = MoveUtil.getSourcePieceIndex(move);
        int attackedPieceIndex = MoveUtil.getAttackedPieceIndex(move);
        if (fromIndex == toIndex) {
            throw new IllegalStateException("undoMove: fromIndex == toIndex");
        }
        if (1 - this.color_to_move == 0) {
            this.w_all ^= fromToMask;
        } else {
            this.b_all ^= fromToMask;
        }
        this.xorPieces(1 - this.color_to_move, sourcePieceIndex, fromToMask);
        this.piece_indexes[fromIndex] = sourcePieceIndex;
        switch (sourcePieceIndex) {
            case 0: {
                break;
            }
            case 1: {
                if (!MoveUtil.isPromotion(move)) break;
                this.xorPieces(1 - this.color_to_move, 1, toMask);
                this.xorPieces(1 - this.color_to_move, MoveUtil.getMoveType(move), toMask);
                break;
            }
            case 6: {
                if (!MoveUtil.isCastlingMove(move)) break;
                throw new IllegalStateException("Castling");
            }
        }
        switch (attackedPieceIndex) {
            case 0: {
                break;
            }
            case 1: {
                if (MoveUtil.isEPMove(move)) {
                    this.piece_indexes[toIndex] = 0;
                    toMask = ChessConstants.POWER_LOOKUP[toIndex += ChessConstants.COLOR_FACTOR_8[this.color_to_move]];
                }
                this.orPieces(this.color_to_move, attackedPieceIndex, toMask);
                if (this.color_to_move == 0) {
                    this.w_all |= toMask;
                    break;
                }
                this.b_all |= toMask;
                break;
            }
            default: {
                this.orPieces(this.color_to_move, attackedPieceIndex, toMask);
                if (this.color_to_move == 0) {
                    this.w_all |= toMask;
                    break;
                }
                this.b_all |= toMask;
            }
        }
        this.piece_indexes[toIndex] = attackedPieceIndex;
        this.all_pieces = this.w_all | this.b_all;
        this.empty_spaces = this.all_pieces ^ 0xFFFFFFFFFFFFFFFFL;
        this.color_to_move = 1 - this.color_to_move;
    }

    public void undoCastling960(int move) {
        this.played_board_states.dec(this.zobrist_key);
        --this.played_moves_count;
        --this.moves_count;
        BoardState state_backup = this.states[this.moves_count];
        this.pinned_pieces = state_backup.pinned_pieces;
        this.discovered_pieces = state_backup.discovered_pieces;
        this.checking_pieces = state_backup.checking_pieces;
        this.ep_index = state_backup.ep_index;
        this.castling_rights = state_backup.castling_rights;
        this.last_capture_or_pawn_move_before = state_backup.last_capture_or_pawn_move_before;
        this.zobrist_key = state_backup.zobrist_key;
        int fromIndex_king = MoveUtil.getFromIndex(move);
        int toIndex_king = MoveUtil.getToIndex(move);
        int sourcePieceIndex = MoveUtil.getSourcePieceIndex(move);
        if (sourcePieceIndex != 6 || !MoveUtil.isCastlingMove(move)) {
            throw new IllegalStateException("sourcePieceIndex != KING || !MoveUtil.isCastlingMove(move)");
        }
        CastlingUtil.getRookFromToSquareIDs(this, toIndex_king, this.buff_castling_rook_from_to);
        int fromIndex_rook = this.buff_castling_rook_from_to[0];
        int toIndex_rook = this.buff_castling_rook_from_to[1];
        if (fromIndex_king == toIndex_king) {
            long bb = ChessConstants.POWER_LOOKUP[fromIndex_rook] | ChessConstants.POWER_LOOKUP[toIndex_rook];
            this.xorPieces(1 - this.color_to_move, 4, bb);
            if (1 - this.color_to_move == 0) {
                this.w_all ^= bb;
            } else {
                this.b_all ^= bb;
            }
            this.piece_indexes[fromIndex_rook] = 4;
            this.piece_indexes[toIndex_rook] = 0;
        } else if (fromIndex_rook == toIndex_rook) {
            long bb = ChessConstants.POWER_LOOKUP[fromIndex_king] | ChessConstants.POWER_LOOKUP[toIndex_king];
            this.xorPieces(1 - this.color_to_move, 6, bb);
            if (1 - this.color_to_move == 0) {
                this.w_all ^= bb;
            } else {
                this.b_all ^= bb;
            }
            this.piece_indexes[fromIndex_king] = 6;
            this.piece_indexes[toIndex_king] = 0;
        } else if (fromIndex_rook == toIndex_king && toIndex_rook == fromIndex_king) {
            long bb_king = ChessConstants.POWER_LOOKUP[fromIndex_king] | ChessConstants.POWER_LOOKUP[toIndex_king];
            this.xorPieces(1 - this.color_to_move, 6, bb_king);
            long bb_rook = ChessConstants.POWER_LOOKUP[fromIndex_rook] | ChessConstants.POWER_LOOKUP[toIndex_rook];
            this.xorPieces(1 - this.color_to_move, 4, bb_rook);
            this.piece_indexes[toIndex_rook] = 6;
            this.piece_indexes[toIndex_king] = 4;
        } else if (fromIndex_rook == toIndex_king) {
            long bb_king = ChessConstants.POWER_LOOKUP[fromIndex_king] | ChessConstants.POWER_LOOKUP[toIndex_king];
            this.xorPieces(1 - this.color_to_move, 6, bb_king);
            long bb_rook = ChessConstants.POWER_LOOKUP[fromIndex_rook] | ChessConstants.POWER_LOOKUP[toIndex_rook];
            this.xorPieces(1 - this.color_to_move, 4, bb_rook);
            if (1 - this.color_to_move == 0) {
                this.w_all ^= ChessConstants.POWER_LOOKUP[fromIndex_king] | ChessConstants.POWER_LOOKUP[toIndex_rook];
            } else {
                this.b_all ^= ChessConstants.POWER_LOOKUP[fromIndex_king] | ChessConstants.POWER_LOOKUP[toIndex_rook];
            }
            this.piece_indexes[toIndex_rook] = 0;
            this.piece_indexes[toIndex_king] = 4;
            this.piece_indexes[fromIndex_king] = 6;
        } else if (toIndex_rook == fromIndex_king) {
            long bb_king = ChessConstants.POWER_LOOKUP[fromIndex_king] | ChessConstants.POWER_LOOKUP[toIndex_king];
            this.xorPieces(1 - this.color_to_move, 6, bb_king);
            long bb_rook = ChessConstants.POWER_LOOKUP[fromIndex_rook] | ChessConstants.POWER_LOOKUP[toIndex_rook];
            this.xorPieces(1 - this.color_to_move, 4, bb_rook);
            if (1 - this.color_to_move == 0) {
                this.w_all ^= ChessConstants.POWER_LOOKUP[toIndex_king] | ChessConstants.POWER_LOOKUP[fromIndex_rook];
            } else {
                this.b_all ^= ChessConstants.POWER_LOOKUP[toIndex_king] | ChessConstants.POWER_LOOKUP[fromIndex_rook];
            }
            this.piece_indexes[toIndex_rook] = 6;
            this.piece_indexes[toIndex_king] = 0;
            this.piece_indexes[fromIndex_rook] = 4;
        } else {
            long bb_king = ChessConstants.POWER_LOOKUP[fromIndex_king] | ChessConstants.POWER_LOOKUP[toIndex_king];
            this.xorPieces(1 - this.color_to_move, 6, bb_king);
            if (1 - this.color_to_move == 0) {
                this.w_all ^= bb_king;
            } else {
                this.b_all ^= bb_king;
            }
            long bb_rook = ChessConstants.POWER_LOOKUP[fromIndex_rook] | ChessConstants.POWER_LOOKUP[toIndex_rook];
            this.xorPieces(1 - this.color_to_move, 4, bb_rook);
            if (1 - this.color_to_move == 0) {
                this.w_all ^= bb_rook;
            } else {
                this.b_all ^= bb_rook;
            }
            this.piece_indexes[fromIndex_rook] = 4;
            this.piece_indexes[fromIndex_king] = 6;
            this.piece_indexes[toIndex_rook] = 0;
            this.piece_indexes[toIndex_king] = 0;
        }
        this.all_pieces = this.w_all | this.b_all;
        this.empty_spaces = this.all_pieces ^ 0xFFFFFFFFFFFFFFFFL;
        this.color_to_move = 1 - this.color_to_move;
    }

    public void doNullMove() {
        BoardState state_backup = this.states[this.moves_count];
        state_backup.pinned_pieces = this.pinned_pieces;
        state_backup.discovered_pieces = this.discovered_pieces;
        state_backup.checking_pieces = this.checking_pieces;
        state_backup.ep_index = this.ep_index;
        state_backup.castling_rights = this.castling_rights;
        state_backup.last_capture_or_pawn_move_before = this.last_capture_or_pawn_move_before;
        state_backup.zobrist_key = this.zobrist_key;
        this.played_moves[this.moves_count] = 0;
        ++this.moves_count;
        ++this.played_moves_count;
        this.zobrist_key ^= Zobrist.sideToMove;
        if (this.ep_index != 0) {
            this.zobrist_key ^= Zobrist.epIndex[this.ep_index];
            this.ep_index = 0;
        }
        this.color_to_move = 1 - this.color_to_move;
        this.played_board_states.inc(this.zobrist_key);
    }

    public void undoNullMove() {
        this.played_board_states.dec(this.zobrist_key);
        --this.played_moves_count;
        --this.moves_count;
        BoardState state_backup = this.states[this.moves_count];
        this.pinned_pieces = state_backup.pinned_pieces;
        this.discovered_pieces = state_backup.discovered_pieces;
        this.checking_pieces = state_backup.checking_pieces;
        this.ep_index = state_backup.ep_index;
        this.castling_rights = state_backup.castling_rights;
        this.last_capture_or_pawn_move_before = state_backup.last_capture_or_pawn_move_before;
        this.zobrist_key = state_backup.zobrist_key;
        this.color_to_move = 1 - this.color_to_move;
    }

    public void setPinnedAndDiscoPieces() {
        this.pinned_pieces = 0L;
        this.discovered_pieces = 0L;
        for (int kingColor = 0; kingColor <= 1; ++kingColor) {
            int enemyColor = 1 - kingColor;
            if ((this.getPieces(enemyColor, 3) | this.getPieces(enemyColor, 4) | this.getPieces(enemyColor, 5)) == 0L) continue;
            int kingIndex = this.getKingIndex(kingColor);
            for (long enemyPiece = (this.getPieces(enemyColor, 3) | this.getPieces(enemyColor, 5)) & MagicUtil.getBishopMovesEmptyBoard(kingIndex) | (this.getPieces(enemyColor, 4) | this.getPieces(enemyColor, 5)) & MagicUtil.getRookMovesEmptyBoard(kingIndex); enemyPiece != 0L; enemyPiece &= enemyPiece - 1L) {
                long checkedPiece = ChessConstants.IN_BETWEEN[kingIndex][Long.numberOfTrailingZeros(enemyPiece)] & this.all_pieces;
                if (Long.bitCount(checkedPiece) != 1) continue;
                this.pinned_pieces |= checkedPiece & this.getPieces_All(kingColor);
                this.discovered_pieces |= checkedPiece & this.getPieces_All(enemyColor);
            }
        }
    }

    private boolean isDiscoveredMove(int fromIndex) {
        return (this.discovered_pieces & 1L << fromIndex) != 0L;
    }

    public boolean isValidMove(int move) {
        int fromIndex = MoveUtil.getFromIndex(move);
        long fromSquare = ChessConstants.POWER_LOOKUP[fromIndex];
        if ((this.getPiecesOfSideToMove(MoveUtil.getSourcePieceIndex(move)) & fromSquare) == 0L) {
            return false;
        }
        int toIndex = MoveUtil.getToIndex(move);
        long toSquare = ChessConstants.POWER_LOOKUP[toIndex];
        int attackedPieceIndex = MoveUtil.getAttackedPieceIndex(move);
        if (attackedPieceIndex == 0 ? (MoveUtil.isCastlingMove(move) ? this.piece_indexes[toIndex] != 0 && this.piece_indexes[toIndex] != 4 && this.piece_indexes[toIndex] != 6 : this.piece_indexes[toIndex] != 0) : (this.getPiecesOfSideNotToMove(attackedPieceIndex) & toSquare) == 0L && !MoveUtil.isEPMove(move)) {
            return false;
        }
        switch (MoveUtil.getSourcePieceIndex(move)) {
            case 1: {
                if (MoveUtil.isEPMove(move)) {
                    if (toIndex != this.ep_index) {
                        return false;
                    }
                    return this.isLegalEPMove(fromIndex);
                }
                if (this.color_to_move == 0) {
                    if (fromIndex > toIndex) {
                        return false;
                    }
                    if (toIndex - fromIndex != 16 || (this.all_pieces & ChessConstants.POWER_LOOKUP[fromIndex + 8]) == 0L) break;
                    return false;
                }
                if (fromIndex < toIndex) {
                    return false;
                }
                if (fromIndex - toIndex != 16 || (this.all_pieces & ChessConstants.POWER_LOOKUP[fromIndex - 8]) == 0L) break;
                return false;
            }
            case 2: {
                break;
            }
            case 3: 
            case 4: 
            case 5: {
                if ((ChessConstants.IN_BETWEEN[fromIndex][toIndex] & this.all_pieces) == 0L) break;
                return false;
            }
            case 6: {
                if (MoveUtil.isCastlingMove(move)) {
                    for (long castlingIndexes = CastlingUtil.getCastlingIndexes(this.color_to_move, this.castling_rights, this.castling_config); castlingIndexes != 0L; castlingIndexes &= castlingIndexes - 1L) {
                        if (toIndex != Long.numberOfTrailingZeros(castlingIndexes)) continue;
                        return CastlingUtil.isValidCastlingMove(this, fromIndex, toIndex);
                    }
                    return false;
                }
                return this.isLegalKingMove(move);
            }
        }
        if ((fromSquare & this.pinned_pieces) != 0L && (ChessConstants.PINNED_MOVEMENT[fromIndex][this.getKingIndexOfSideToMove()] & toSquare) == 0L) {
            return false;
        }
        if (this.checking_pieces != 0L) {
            if (attackedPieceIndex == 0) {
                return this.isLegalNonKingMove(move);
            }
            if (Long.bitCount(this.checking_pieces) >= 2) {
                return false;
            }
            return (toSquare & this.checking_pieces) != 0L;
        }
        return true;
    }

    private boolean isLegalKingMove(int move) {
        return !CheckUtil.isInCheckIncludingKing(this, MoveUtil.getToIndex(move), this.color_to_move, this.all_pieces ^ ChessConstants.POWER_LOOKUP[MoveUtil.getFromIndex(move)]);
    }

    private boolean isLegalNonKingMove(int move) {
        return !CheckUtil.isInCheck(this, this.getKingIndexOfSideToMove(), this.color_to_move, this.all_pieces ^ ChessConstants.POWER_LOOKUP[MoveUtil.getFromIndex(move)] ^ ChessConstants.POWER_LOOKUP[MoveUtil.getToIndex(move)]);
    }

    private boolean isLegalEPMove(int fromIndex) {
        boolean isInCheck;
        long fromToMask = ChessConstants.POWER_LOOKUP[fromIndex] ^ ChessConstants.POWER_LOOKUP[this.ep_index];
        if (this.color_to_move == 0) {
            this.w_all ^= fromToMask;
        } else {
            this.b_all ^= fromToMask;
        }
        this.xorPieces(1 - this.color_to_move, 1, ChessConstants.POWER_LOOKUP[this.ep_index + ChessConstants.COLOR_FACTOR_8[1 - this.color_to_move]]);
        this.all_pieces = this.w_all | this.b_all ^ ChessConstants.POWER_LOOKUP[this.ep_index + ChessConstants.COLOR_FACTOR_8[1 - this.color_to_move]];
        boolean bl = isInCheck = CheckUtil.getCheckingPieces(this) != 0L;
        if (this.color_to_move == 0) {
            this.w_all ^= fromToMask;
        } else {
            this.b_all ^= fromToMask;
        }
        this.xorPieces(1 - this.color_to_move, 1, ChessConstants.POWER_LOOKUP[this.ep_index + ChessConstants.COLOR_FACTOR_8[1 - this.color_to_move]]);
        this.all_pieces = this.w_all | this.b_all;
        return !isInCheck;
    }

    public int getRepetition() {
        int count = this.played_board_states.get(this.zobrist_key);
        if (count == -1) {
            return 0;
        }
        return count;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard)obj;
        return this.w_king == that.w_king && this.w_queens == that.w_queens && this.w_rooks == that.w_rooks && this.w_bishops == that.w_bishops && this.w_knights == that.w_knights && this.w_pawns == that.w_pawns && this.w_all == that.w_all && this.b_king == that.b_king && this.b_queens == that.b_queens && this.b_rooks == that.b_rooks && this.b_bishops == that.b_bishops && this.b_knights == that.b_knights && this.b_pawns == that.b_pawns && this.b_all == that.b_all && this.all_pieces == that.all_pieces && this.empty_spaces == that.empty_spaces && this.pinned_pieces == that.pinned_pieces && this.discovered_pieces == that.discovered_pieces && this.checking_pieces == that.checking_pieces && this.color_to_move == that.color_to_move && this.ep_index == that.ep_index && this.castling_rights == that.castling_rights && this.last_capture_or_pawn_move_before == that.last_capture_or_pawn_move_before && this.played_moves_count == that.played_moves_count && this.moves_count == that.moves_count && this.zobrist_key == that.zobrist_key && Arrays.equals(this.piece_indexes, that.piece_indexes);
    }

    public ChessBoard clone() {
        ChessBoard clone = new ChessBoard();
        clone.w_king = this.w_king;
        clone.w_queens = this.w_queens;
        clone.w_rooks = this.w_rooks;
        clone.w_bishops = this.w_bishops;
        clone.w_knights = this.w_knights;
        clone.w_pawns = this.w_pawns;
        clone.w_all = this.w_all;
        clone.b_king = this.b_king;
        clone.b_queens = this.b_queens;
        clone.b_rooks = this.b_rooks;
        clone.b_bishops = this.b_bishops;
        clone.b_knights = this.b_knights;
        clone.b_pawns = this.b_pawns;
        clone.b_all = this.b_all;
        clone.all_pieces = this.all_pieces;
        clone.empty_spaces = this.empty_spaces;
        clone.pinned_pieces = this.pinned_pieces;
        clone.discovered_pieces = this.discovered_pieces;
        clone.checking_pieces = this.checking_pieces;
        clone.color_to_move = this.color_to_move;
        clone.ep_index = this.ep_index;
        clone.castling_rights = this.castling_rights;
        clone.last_capture_or_pawn_move_before = this.last_capture_or_pawn_move_before;
        clone.played_moves_count = this.played_moves_count;
        clone.moves_count = this.moves_count;
        clone.zobrist_key = this.zobrist_key;
        clone.piece_indexes = (int[])this.piece_indexes.clone();
        return clone;
    }

    @Override
    public int getColourToMove() {
        return this.color_to_move;
    }

    @Override
    public int genAllMoves(IInternalMoveList list) {
        MoveGeneration.generateMoves(this, list);
        MoveGeneration.generateAttacks(this, list);
        return list.reserved_getCurrentSize();
    }

    @Override
    public int genKingEscapes(IInternalMoveList list) {
        return this.genAllMoves(list);
    }

    @Override
    public int genCapturePromotionMoves(IInternalMoveList list) {
        MoveGeneration.generateAttacks(this, list);
        return list.reserved_getCurrentSize();
    }

    @Override
    public int genNonCaptureNonPromotionMoves(IInternalMoveList list) {
        MoveGeneration.generateMoves(this, list);
        return list.reserved_getCurrentSize();
    }

    @Override
    public void makeMoveForward(int move) {
        int i;
        if (this.move_listeners.length > 0) {
            for (i = 0; i < this.move_listeners.length; ++i) {
                this.move_listeners[i].preForwardMove(this.color_to_move, move);
            }
        }
        this.doMove(move);
        if (this.move_listeners.length > 0) {
            for (i = 0; i < this.move_listeners.length; ++i) {
                this.move_listeners[i].postForwardMove(1 - this.color_to_move, move);
            }
        }
    }

    @Override
    public void makeMoveBackward(int move) {
        int i;
        if (this.move_listeners.length > 0) {
            for (i = 0; i < this.move_listeners.length; ++i) {
                this.move_listeners[i].preBackwardMove(1 - this.color_to_move, move);
            }
        }
        this.undoMove(move);
        if (this.move_listeners.length > 0) {
            for (i = 0; i < this.move_listeners.length; ++i) {
                this.move_listeners[i].postBackwardMove(this.color_to_move, move);
            }
        }
    }

    @Override
    public void makeNullMoveForward() {
        this.doNullMove();
    }

    @Override
    public void makeNullMoveBackward() {
        this.undoNullMove();
    }

    @Override
    public long getHashKey() {
        return this.zobrist_key;
    }

    @Override
    public int getStateRepetition() {
        return this.getRepetition();
    }

    @Override
    public String toEPD() {
        return ChessBoardBuilder.toString(this, true);
    }

    @Override
    public IMoveOps getMoveOps() {
        return this.moveOps;
    }

    @Override
    public int getPlayedMovesCount() {
        return this.moves_count;
    }

    @Override
    public int[] getPlayedMoves() {
        return this.played_moves;
    }

    @Override
    public int getLastMove() {
        if (this.moves_count == 0) {
            return 0;
        }
        return this.played_moves[this.moves_count - 1];
    }

    @Override
    public boolean isDraw50movesRule() {
        return this.last_capture_or_pawn_move_before >= 100;
    }

    @Override
    public int getDraw50movesRule() {
        return this.last_capture_or_pawn_move_before;
    }

    @Override
    public boolean hasSufficientMatingMaterial() {
        return this.hasSufficientMatingMaterial(0) || this.hasSufficientMatingMaterial(1);
    }

    @Override
    public boolean hasSufficientMatingMaterial(int color) {
        long pawns = this.getPieces(color, 1);
        if (pawns != 0L) {
            return true;
        }
        long queens = this.getPieces(color, 5);
        if (queens != 0L) {
            return true;
        }
        long rooks = this.getPieces(color, 4);
        if (rooks != 0L) {
            return true;
        }
        long bishops = this.getPieces(color, 3);
        long knights = this.getPieces(color, 2);
        if (Utils.countBits(bishops) + Utils.countBits(knights) >= 3) {
            return true;
        }
        if (bishops != 0L && (bishops & 0x55AA55AA55AA55AAL) != 0L && (bishops & 0xAA55AA55AA55AA55L) != 0L) {
            return true;
        }
        return Utils.countBits(bishops) == 1 && Utils.countBits(knights) == 1;
    }

    @Override
    public boolean isInCheck() {
        return this.checking_pieces != 0L;
    }

    @Override
    public boolean isInCheck(int colour) {
        return CheckUtil.isInCheck(this, colour);
    }

    @Override
    public boolean hasMoveInCheck() {
        this.hasMovesList.clear();
        this.genKingEscapes(this.hasMovesList);
        return this.hasMovesList.reserved_getCurrentSize() > 0;
    }

    @Override
    public boolean hasMoveInNonCheck() {
        this.hasMovesList.clear();
        this.genAllMoves(this.hasMovesList);
        return this.hasMovesList.reserved_getCurrentSize() > 0;
    }

    @Override
    public boolean isPossible(int move) {
        return this.isValidMove(move);
    }

    @Override
    public CastlingConfig getCastlingConfig() {
        return this.castling_config;
    }

    @Override
    public void revert() {
        for (int i = this.moves_count - 1; i >= 0; --i) {
            int move = this.played_moves[i];
            if (move == 0) {
                this.makeNullMoveBackward();
                continue;
            }
            this.makeMoveBackward(move);
        }
    }

    @Override
    public IMaterialFactor getMaterialFactor() {
        return this.materialFactor;
    }

    @Override
    public IBaseEval getBaseEvaluation() {
        return null;
    }

    @Override
    public IPiecesLists getPiecesLists() {
        return this.pieces;
    }

    @Override
    public IMaterialState getMaterialState() {
        return this.material_state;
    }

    @Override
    public boolean hasSingleMove() {
        this.hasMovesList.clear();
        this.genAllMoves(this.hasMovesList);
        return this.hasMovesList.reserved_getCurrentSize() == 1;
    }

    @Override
    public boolean hasRightsToKingCastle(int colour) {
        if (colour == 0) {
            return (this.castling_rights & 8) != 0;
        }
        return (this.castling_rights & 2) != 0;
    }

    @Override
    public boolean hasRightsToQueenCastle(int colour) {
        if (colour == 0) {
            return (this.castling_rights & 4) != 0;
        }
        return (this.castling_rights & 1) != 0;
    }

    public String toString() {
        Object moves_str = "";
        int[] moves = this.played_moves;
        for (int i = 0; i < this.moves_count; ++i) {
            moves_str = (String)moves_str + this.moveOps.moveToString(moves[i]) + " ";
        }
        return ChessBoardBuilder.toString(this, true) + " moves " + (String)moves_str;
    }

    @Override
    public int getSEEScore(int move) {
        return SEEUtil.getSeeCaptureScore(this, move);
    }

    @Override
    public void makeMoveForward(String ucimove) {
        MoveWrapper move = new MoveWrapper(ucimove, this, this.isFRC);
        this.makeMoveForward(move.move);
    }

    @Override
    public int getEnpassantSquareID() {
        return this.ep_index;
    }

    @Override
    public boolean isCheckMove(int move) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int[] getMatrix() {
        throw new UnsupportedOperationException();
    }

    @Override
    public PawnsEvalCache getPawnsCache() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setPawnsCache(PawnsEvalCache pawnsCache) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PawnsModelEval getPawnsStructure() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IBoardConfig getBoardConfig() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int genAllMoves_ByFigureID(int fieldID, long excludedToFields, IInternalMoveList list) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getHashKeyAfterMove(int move) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getPawnsHashKey() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getFigureID(int fieldID) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getFigureType(int fieldID) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getFigureColour(int fieldID) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ISEE getSee() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getSEEFieldScore(int squareID) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void mark() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isPasserPush(int move) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getUnstoppablePasser() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IBoard.CastlingType getCastlingType(int colour) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IBoard.CastlingPair getCastlingPair() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IGameStatus getStatus() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getNNUEInputs() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addMoveListener(MoveListener listener) {
        MoveListener[] oldMoveListeners = this.move_listeners;
        MoveListener[] newMoveListeners = new MoveListener[this.move_listeners.length + 1];
        if (oldMoveListeners.length > 0) {
            for (int i = 0; i < oldMoveListeners.length; ++i) {
                newMoveListeners[i] = oldMoveListeners[i];
            }
        }
        newMoveListeners[oldMoveListeners.length] = listener;
        this.move_listeners = newMoveListeners;
    }

    @Override
    public long getFiguresBitboardByColourAndType(int color, int type) {
        return this.getPieces(color, type);
    }

    @Override
    public long getFiguresBitboardByColour(int color) {
        return color == 0 ? this.w_all : this.b_all;
    }

    @Override
    public long getFreeBitboard() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getFiguresBitboardByPID(int pid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getAttacksSupport() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getFieldsStateSupport() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAttacksSupport(boolean attacksSupport, boolean fieldsStateSupport) {
    }

    @Override
    public IPlayerAttacks getPlayerAttacks(int colour) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IFieldsAttacks getFieldsAttacks() {
        throw new UnsupportedOperationException();
    }

    private static class BoardState {
        public long pinned_pieces;
        public long discovered_pieces;
        public long checking_pieces;
        public int ep_index;
        public int castling_rights;
        public int last_capture_or_pawn_move_before;
        public long zobrist_key;

        private BoardState() {
        }
    }

    private class MoveOpsImpl
    implements IMoveOps {
        private final int[] FILES = new int[]{7, 6, 5, 4, 3, 2, 1, 0};
        private final int[] RANKS = new int[]{0, 1, 2, 3, 4, 5, 6, 7};

        private MoveOpsImpl() {
        }

        @Override
        public final int getFigureType(int move) {
            return MoveUtil.getSourcePieceIndex(move);
        }

        @Override
        public final int getToFieldID(int move) {
            return MoveUtil.getToIndex(move);
        }

        @Override
        public final boolean isCapture(int move) {
            return MoveUtil.getAttackedPieceIndex(move) != 0;
        }

        @Override
        public final boolean isPromotion(int move) {
            return MoveUtil.isPromotion(move);
        }

        @Override
        public final boolean isCaptureOrPromotion(int move) {
            return this.isCapture(move) || this.isPromotion(move);
        }

        @Override
        public final boolean isEnpassant(int move) {
            return MoveUtil.isEPMove(move);
        }

        @Override
        public final boolean isCastling(int move) {
            return MoveUtil.isCastlingMove(move);
        }

        @Override
        public final int getFigurePID(int move) {
            int pieceType = MoveUtil.getSourcePieceIndex(move);
            int colour = ChessBoard.this.color_to_move;
            if (colour == 0) {
                switch (pieceType) {
                    case 1: {
                        return 1;
                    }
                    case 2: {
                        return 2;
                    }
                    case 3: {
                        return 3;
                    }
                    case 4: {
                        return 4;
                    }
                    case 5: {
                        return 5;
                    }
                    case 6: {
                        return 6;
                    }
                }
            } else {
                switch (pieceType) {
                    case 1: {
                        return 7;
                    }
                    case 2: {
                        return 8;
                    }
                    case 3: {
                        return 9;
                    }
                    case 4: {
                        return 10;
                    }
                    case 5: {
                        return 11;
                    }
                    case 6: {
                        return 12;
                    }
                }
            }
            throw new IllegalStateException("pieceType=" + pieceType);
        }

        @Override
        public final boolean isCastlingKingSide(int move) {
            if (this.isCastling(move)) {
                int index = MoveUtil.getToIndex(move);
                return index == 1 || index == 57;
            }
            return false;
        }

        @Override
        public final boolean isCastlingQueenSide(int move) {
            if (this.isCastling(move)) {
                int index = MoveUtil.getToIndex(move);
                return index == 5 || index == 61;
            }
            return false;
        }

        @Override
        public final int getFromFieldID(int move) {
            return MoveUtil.getFromIndex(move);
        }

        @Override
        public final int getPromotionFigureType(int move) {
            if (!this.isPromotion(move)) {
                return 0;
            }
            return MoveUtil.getMoveType(move);
        }

        @Override
        public final int getCapturedFigureType(int cur_move) {
            return MoveUtil.getAttackedPieceIndex(cur_move);
        }

        @Override
        public final String moveToString(int move) {
            return new MoveWrapper(move, ChessBoard.this.isFRC, ChessBoard.this.castling_config).toString();
        }

        @Override
        public final void moveToString(int move, StringBuilder text_buffer) {
            new MoveWrapper(move, ChessBoard.this.isFRC, ChessBoard.this.castling_config).toString(text_buffer);
        }

        @Override
        public final int stringToMove(String move) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final int getToField_File(int move) {
            return this.FILES[this.getToFieldID(move) & 7];
        }

        @Override
        public final int getToField_Rank(int move) {
            return this.RANKS[this.getToFieldID(move) >>> 3];
        }

        @Override
        public final int getFromField_File(int move) {
            return this.FILES[this.getFromFieldID(move) & 7];
        }

        @Override
        public final int getFromField_Rank(int move) {
            return this.RANKS[this.getFromFieldID(move) >>> 3];
        }
    }

    private class MaterialFactorImpl
    implements IMaterialFactor {
        private static final int TOTAL_FACTOR_MAX = 62;

        @Override
        public int getBlackFactor() {
            return 9 * Long.bitCount(ChessBoard.this.b_queens) + 5 * Long.bitCount(ChessBoard.this.b_rooks) + 3 * Long.bitCount(ChessBoard.this.b_knights | ChessBoard.this.b_bishops);
        }

        @Override
        public int getWhiteFactor() {
            return 9 * Long.bitCount(ChessBoard.this.w_queens) + 5 * Long.bitCount(ChessBoard.this.w_rooks) + 3 * Long.bitCount(ChessBoard.this.w_knights | ChessBoard.this.w_bishops);
        }

        @Override
        public int getTotalFactor() {
            return Math.min(62, this.getWhiteFactor() + this.getBlackFactor());
        }

        @Override
        public double getOpenningPart() {
            if (this.getTotalFactor() < 0) {
                throw new IllegalStateException();
            }
            return Math.min(1.0, (double)this.getTotalFactor() / 62.0);
        }

        @Override
        public int interpolateByFactor(int val_o, int val_e) {
            double openningPart = this.getOpenningPart();
            int result = (int)((double)val_o * openningPart + (double)val_e * (1.0 - openningPart));
            return result;
        }

        @Override
        public int interpolateByFactor(double val_o, double val_e) {
            double openningPart = this.getOpenningPart();
            double result = val_o * openningPart + val_e * (1.0 - openningPart);
            return (int)result;
        }

        @Override
        public void addPiece_Special(int pid, int fieldID) {
        }

        @Override
        public void preForwardMove(int color, int move) {
        }

        @Override
        public void postForwardMove(int color, int move) {
        }

        @Override
        public void preBackwardMove(int color, int move) {
        }

        @Override
        public void postBackwardMove(int color, int move) {
        }

        @Override
        public void initially_addPiece(int color, int type, long bb_pieces) {
        }
    }

    private static class PiecesListsImpl
    implements IPiecesLists {
        private PiecesList list;

        PiecesListsImpl(IBoard board) {
            this.list = new PiecesList(board, 8);
            this.list.add(16);
            this.list.add(32);
        }

        @Override
        public PiecesList getPieces(int pid) {
            return this.list;
        }

        @Override
        public void rem(int pid, int fieldID) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(int pid, int fieldID) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void move(int pid, int fromFieldID, int toFieldID) {
            throw new UnsupportedOperationException();
        }
    }

    private class MaterialStateImpl
    implements IMaterialState {
        private MaterialStateImpl() {
        }

        @Override
        public int getPiecesCount() {
            return Long.bitCount(ChessBoard.this.all_pieces);
        }

        @Override
        public int[] getPIDsCounts() {
            throw new UnsupportedOperationException();
        }
    }
}

