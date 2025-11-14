/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl_kingcaptureallowed;

import bagaturchess.bitboard.api.IBaseEval;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IBoard;
import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.api.IInternalMoveList;
import bagaturchess.bitboard.api.IMaterialFactor;
import bagaturchess.bitboard.api.IMaterialState;
import bagaturchess.bitboard.api.IPiecesLists;
import bagaturchess.bitboard.api.PawnsEvalCache;
import bagaturchess.bitboard.common.BackupInfo;
import bagaturchess.bitboard.common.Fen;
import bagaturchess.bitboard.common.MoveListener;
import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.datastructs.StackLongInt;
import bagaturchess.bitboard.impl.endgame.MaterialState;
import bagaturchess.bitboard.impl.eval.BaseEvaluation;
import bagaturchess.bitboard.impl.eval.MaterialFactor;
import bagaturchess.bitboard.impl.eval.pawns.model.Pawn;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnStructureConstants;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModel;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEval;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.bitboard.impl.state.PiecesList;
import bagaturchess.bitboard.impl.state.PiecesLists;
import bagaturchess.bitboard.impl.zobrist.ConstantStructure;
import bagaturchess.bitboard.impl_kingcaptureallowed.attacks.FieldAttack;
import bagaturchess.bitboard.impl_kingcaptureallowed.attacks.SEE;
import bagaturchess.bitboard.impl_kingcaptureallowed.movegen.BlackPawnMovesGen;
import bagaturchess.bitboard.impl_kingcaptureallowed.movegen.CastleMovesGen;
import bagaturchess.bitboard.impl_kingcaptureallowed.movegen.KingMovesGen;
import bagaturchess.bitboard.impl_kingcaptureallowed.movegen.KnightMovesGen;
import bagaturchess.bitboard.impl_kingcaptureallowed.movegen.OfficerMovesGen;
import bagaturchess.bitboard.impl_kingcaptureallowed.movegen.QueenMovesGen;
import bagaturchess.bitboard.impl_kingcaptureallowed.movegen.WhitePawnMovesGen;
import bagaturchess.bitboard.impl_kingcaptureallowed.plies.Castling;
import bagaturchess.bitboard.impl_kingcaptureallowed.plies.Enpassanting;
import java.util.Arrays;

abstract class Board3
extends Fields
implements IBitBoard,
Cloneable {
    private static final boolean DEBUG = false;
    private IBoardConfig boardConfig;
    public PiecesLists pieces;
    public int[] board;
    protected StackLongInt playedBoardStates;
    protected long hashkey = 0L;
    protected long pawnskey = 0L;
    protected BackupInfo[] backupInfo;
    protected int lastMoveColour = 1;
    protected int[] playedMoves;
    protected int playedMovesCount = 0;
    protected int marked_playedMovesCount = 0;
    protected int lastCaptureOrPawnMoveBefore = 0;
    private SEE see;
    private MoveListener[] moveListeners;
    private MaterialState materialState;
    private MaterialFactor materialFactor;
    private BaseEvaluation eval;
    protected PawnsEvalCache pawnsCache;
    protected IBoard.CastlingType[] castledByColour;
    protected boolean[] inCheckCache;
    protected boolean[] inCheckCacheInitialized;

    public Board3() {
        this((IBoardConfig)null);
    }

    public Board3(IBoardConfig boardConfig) {
        this("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", boardConfig);
    }

    public Board3(String fenStr) {
        this(fenStr, null);
    }

    public Board3(String fenStr, IBoardConfig boardConfig) {
        this(fenStr, null, boardConfig);
    }

    public Board3(String fenStr, PawnsEvalCache _pawnsCache, IBoardConfig _boardConfig) {
        this.pawnsCache = _pawnsCache;
        this.boardConfig = _boardConfig;
        Fen fen = Fen.parse(fenStr);
        this.board = new int[64];
        this.castledByColour = new IBoard.CastlingType[Figures.COLOUR_MAX];
        int n = this.lastMoveColour = fen.getColourToMove() == 0 ? 1 : 0;
        if (fen.getColourToMove() == 0) {
            this.hashkey ^= ConstantStructure.WHITE_TO_MOVE;
            this.pawnskey ^= ConstantStructure.WHITE_TO_MOVE;
        }
        this.pieces = new PiecesLists(this);
        this.moveListeners = new MoveListener[0];
        this.materialFactor = new MaterialFactor();
        this.materialState = new MaterialState();
        this.addMoveListener(this.materialFactor);
        this.addMoveListener(this.materialState);
        if (this.boardConfig != null) {
            this.eval = new BaseEvaluation(this.boardConfig, this.materialFactor);
            this.addMoveListener(this.eval);
        }
        this.init(fen.getBoardArray());
        this.playedMoves = new int[2000];
        this.playedMovesCount = 0;
        this.backupInfo = new BackupInfo[2000];
        for (int i = 0; i < this.backupInfo.length; ++i) {
            this.backupInfo[i] = new BackupInfo();
        }
        int enpassantTargetFieldID = -1;
        if (fen.getEnpassantTargetSquare() != null) {
            enpassantTargetFieldID = Enpassanting.getEnemyFieldID(fen.getEnpassantTargetSquare());
        }
        this.backupInfo[this.playedMovesCount].enpassantPawnFieldID = enpassantTargetFieldID;
        this.backupInfo[this.playedMovesCount].w_kingSideAvailable = fen.hasWhiteKingSide();
        this.backupInfo[this.playedMovesCount].w_queenSideAvailable = fen.hasWhiteQueenSide();
        this.backupInfo[this.playedMovesCount].b_kingSideAvailable = fen.hasBlackKingSide();
        this.backupInfo[this.playedMovesCount].b_queenSideAvailable = fen.hasBlackQueenSide();
        if (this.backupInfo[this.playedMovesCount].enpassantPawnFieldID != -1) {
            this.hashkey ^= ConstantStructure.HAS_ENPASSANT;
        }
        if (this.backupInfo[this.playedMovesCount].w_kingSideAvailable) {
            this.hashkey ^= ConstantStructure.CASTLE_KING_SIDE_BY_COLOUR[0];
        }
        if (this.backupInfo[this.playedMovesCount].b_kingSideAvailable) {
            this.hashkey ^= ConstantStructure.CASTLE_KING_SIDE_BY_COLOUR[1];
        }
        if (this.backupInfo[this.playedMovesCount].w_queenSideAvailable) {
            this.hashkey ^= ConstantStructure.CASTLE_QUEEN_SIDE_BY_COLOUR[0];
        }
        if (this.backupInfo[this.playedMovesCount].b_queenSideAvailable) {
            this.hashkey ^= ConstantStructure.CASTLE_QUEEN_SIDE_BY_COLOUR[1];
        }
        this.playedBoardStates = new StackLongInt(9631);
        this.playedBoardStates.inc(this.hashkey);
        this.see = new SEE(this);
        this.inCheckCache = new boolean[]{false, false, false};
        this.inCheckCacheInitialized = new boolean[]{false, false, false};
        this.checkConsistency();
    }

    @Override
    public void addMoveListener(MoveListener listener) {
        MoveListener[] oldMoveListeners = this.moveListeners;
        MoveListener[] newMoveListeners = new MoveListener[this.moveListeners.length + 1];
        if (oldMoveListeners.length > 0) {
            for (int i = 0; i < oldMoveListeners.length; ++i) {
                newMoveListeners[i] = oldMoveListeners[i];
            }
        }
        newMoveListeners[oldMoveListeners.length] = listener;
        this.moveListeners = newMoveListeners;
    }

    @Override
    public String toEPD() {
        Object result = "";
        int empty = 0;
        for (int digit = 7; digit >= 0; --digit) {
            Object line = "";
            for (int letter = 0; letter < 8; ++letter) {
                int fieldID = 8 * digit + letter;
                int pid = this.board[fieldID];
                if (pid == 0) {
                    ++empty;
                } else {
                    if (empty != 0) {
                        line = (String)line + empty;
                    }
                    String sign = Constants.PIECE_IDENTITY_2_SIGN[pid];
                    line = (String)line + sign;
                    empty = 0;
                }
                if (letter != 7 || empty == 0) continue;
                line = (String)line + empty;
                empty = 0;
            }
            if (digit != 0) {
                line = (String)line + "/";
            }
            result = (String)result + (String)line;
        }
        result = (String)result + " ";
        result = (String)result + (this.getColourToMove() == 0 ? "w" : "b");
        result = (String)result + " ";
        if (this.backupInfo[this.playedMovesCount].w_kingSideAvailable) {
            result = (String)result + "K";
        }
        if (this.backupInfo[this.playedMovesCount].w_queenSideAvailable) {
            result = (String)result + "Q";
        }
        if (this.backupInfo[this.playedMovesCount].b_kingSideAvailable) {
            result = (String)result + "k";
        }
        if (this.backupInfo[this.playedMovesCount].b_queenSideAvailable) {
            result = (String)result + "q";
        }
        if (((String)result).endsWith(" ")) {
            result = (String)result + "-";
        }
        result = (String)result + " ";
        int enpassTargetFieldID = this.backupInfo[this.playedMovesCount].enpassantPawnFieldID;
        result = enpassTargetFieldID != -1 ? (String)result + Fields.getFieldSign(enpassTargetFieldID) : (String)result + "-";
        result = (String)result + " ";
        result = (String)result + " ";
        return result;
    }

    @Override
    public PawnsEvalCache getPawnsCache() {
        return this.pawnsCache;
    }

    @Override
    public PawnsModelEval getPawnsStructure() {
        long pawnskey = this.getPawnsHashKey();
        PawnsModelEval result = this.pawnsCache.get(pawnskey);
        if (result == null) {
            result = this.pawnsCache.put(pawnskey);
            result.rebuild(this);
        }
        return result;
    }

    @Override
    public boolean isPasserPush(int move) {
        boolean sameVertical;
        int colour = MoveInt.getColour(move);
        if (this.getColourToMove() != colour) {
            throw new IllegalStateException();
        }
        if (!MoveInt.isPawn(move)) {
            return false;
        }
        int fromFieldID = MoveInt.getFromFieldID(move);
        boolean bl = sameVertical = Fields.LETTERS[fromFieldID] == Fields.LETTERS[MoveInt.getToFieldID(move)];
        if (!sameVertical) {
            return false;
        }
        if (this.getPawnsCache() == null) {
            return false;
        }
        this.getPawnsCache().lock();
        PawnsModelEval modelEval = this.getPawnsStructure();
        PawnsModel model = modelEval.getModel();
        if (colour == 0) {
            int w_passed_count = model.getWPassedCount();
            if (w_passed_count <= 0) {
                this.getPawnsCache().unlock();
                return false;
            }
            if (w_passed_count > 0) {
                Pawn[] w_passed = model.getWPassed();
                for (int i = 0; i < w_passed_count; ++i) {
                    if (w_passed[i].getFieldID() != fromFieldID) continue;
                    this.getPawnsCache().unlock();
                    return true;
                }
            }
        } else {
            int b_passed_count = model.getBPassedCount();
            if (b_passed_count <= 0) {
                this.getPawnsCache().unlock();
                return false;
            }
            if (b_passed_count > 0) {
                Pawn[] b_passed = model.getBPassed();
                for (int i = 0; i < b_passed_count; ++i) {
                    if (b_passed[i].getFieldID() != fromFieldID) continue;
                    this.getPawnsCache().unlock();
                    return true;
                }
            }
        }
        this.getPawnsCache().unlock();
        return false;
    }

    private boolean hasUnstoppablePasser(int colourToMove) {
        if (colourToMove != this.getColourToMove()) {
            // empty if block
        }
        if (colourToMove == 0 ? this.materialFactor.getBlackFactor() > 0 : this.materialFactor.getWhiteFactor() > 0) {
            return false;
        }
        boolean result = false;
        PawnsModelEval modelEval = this.getPawnsStructure();
        PawnsModel model = modelEval.getModel();
        this.getPawnsCache().lock();
        int w_passed_count = model.getWPassedCount();
        int b_passed_count = model.getBPassedCount();
        if (colourToMove == 0) {
            if (w_passed_count > 0) {
                int w_rank = model.getWUnstoppablePasserRank();
                int b_rank = model.getBMaxPassedRank();
                if (w_rank != 0 && w_rank > b_rank + 1) {
                    result = true;
                }
            }
        } else if (b_passed_count > 0) {
            int w_rank = model.getWMaxPassedRank();
            int b_rank = model.getBUnstoppablePasserRank();
            if (b_rank != 0 && b_rank > w_rank + 1) {
                result = true;
            }
        }
        this.getPawnsCache().unlock();
        return result;
    }

    @Override
    public int getUnstoppablePasser() {
        int fieldID;
        int b_rank;
        int w_rank;
        int result = 0;
        this.getPawnsCache().lock();
        PawnsModelEval modelEval = this.getPawnsStructure();
        PawnsModel model = modelEval.getModel();
        int w_passed_count = model.getWPassedCount();
        int b_passed_count = model.getBPassedCount();
        if (w_passed_count > 0 && this.materialFactor.getBlackFactor() == 0) {
            w_rank = model.getWUnstoppablePasserRank();
            b_rank = model.getBMaxPassedRank();
            if (w_rank != 0) {
                if (w_rank > b_rank + 1) {
                    ++result;
                }
            } else if (w_rank == 0) {
                long w_passers_keysquares = 0L;
                Pawn[] w_passed = model.getWPassed();
                for (int i = 0; i < w_passed_count; ++i) {
                    if (w_passed[i].getRank() <= b_rank + 2) continue;
                    w_passers_keysquares |= PawnStructureConstants.WHITE_KEY_SQUARES[w_passed[i].getFieldID()];
                }
                PiecesList w_king = this.getPiecesLists().getPieces(6);
                fieldID = w_king.getData()[0];
                long bb_wking = Fields.ALL_A1H1[fieldID];
                if ((w_passers_keysquares & bb_wking) != 0L) {
                    ++result;
                }
            }
        }
        if (b_passed_count > 0 && this.materialFactor.getWhiteFactor() == 0) {
            w_rank = model.getWMaxPassedRank();
            b_rank = model.getBUnstoppablePasserRank();
            if (b_rank != 0) {
                if (b_rank > w_rank + 1) {
                    --result;
                }
            } else if (b_rank == 0) {
                long b_passers_keysquares = 0L;
                Pawn[] b_passed = model.getBPassed();
                for (int i = 0; i < b_passed_count; ++i) {
                    if (b_passed[i].getRank() <= w_rank + 2) continue;
                    b_passers_keysquares |= PawnStructureConstants.BLACK_KEY_SQUARES[b_passed[i].getFieldID()];
                }
                PiecesList b_king = this.getPiecesLists().getPieces(12);
                fieldID = b_king.getData()[0];
                long bb_bking = Fields.ALL_A1H1[fieldID];
                if ((b_passers_keysquares & bb_bking) != 0L) {
                    --result;
                }
            }
        }
        this.getPawnsCache().unlock();
        return result;
    }

    @Override
    public IBoard.CastlingType getCastlingType(int colour) {
        return this.castledByColour[colour];
    }

    @Override
    public void mark() {
        this.marked_playedMovesCount = this.playedMovesCount;
    }

    @Override
    public void reset() {
        for (int i = this.playedMovesCount - 1; i >= this.marked_playedMovesCount; --i) {
            int move = this.playedMoves[i];
            if (move == 0) {
                this.makeNullMoveBackward();
                continue;
            }
            this.makeMoveBackward(move);
        }
    }

    @Override
    public final void makeMoveForward(int move) {
        this.makeMoveForward(move, true);
    }

    public final void makeMoveForward(int move, boolean invalidateCheckKeepers) {
        if (this.eval != null) {
            this.eval.move(move);
        }
        if (this.moveListeners.length > 0) {
            for (int i = 0; i < this.moveListeners.length; ++i) {
                this.moveListeners[i].preForwardMove(MoveInt.getColour(move), move);
            }
        }
        int pid = MoveInt.getFigurePID(move);
        int figureColour = MoveInt.getColour(move);
        int figureType = MoveInt.getFigureType(move);
        int fromFieldID = MoveInt.getFromFieldID(move);
        int toFieldID = MoveInt.getToFieldID(move);
        BackupInfo curInfo = this.backupInfo[this.playedMovesCount];
        curInfo.hashkey = this.hashkey;
        curInfo.pawnshash = this.pawnskey;
        curInfo.lastCaptureOrPawnMoveBefore = this.lastCaptureOrPawnMoveBefore++;
        if (MoveInt.isCapture(move) || MoveInt.isPawn(move)) {
            this.lastCaptureOrPawnMoveBefore = 0;
        }
        BackupInfo nextInfo = this.backupInfo[this.playedMovesCount + 1];
        nextInfo.enpassantPawnFieldID = -1;
        if (figureType == 1 && !MoveInt.isCapture(move) && Math.abs(fromFieldID - toFieldID) == 16) {
            int[] adjoiningFiles = Enpassanting.ADJOINING_FILES_FIELD_IDS[figureColour][toFieldID];
            for (int i = 0; i < adjoiningFiles.length; ++i) {
                int adjoining_field_id = adjoiningFiles[i];
                int adjoining_field_pid = this.board[adjoining_field_id];
                if (Constants.PIECE_IDENTITY_2_TYPE[adjoining_field_pid] != 1 || !Constants.hasDiffColour(pid, adjoining_field_pid)) continue;
                nextInfo.enpassantPawnFieldID = toFieldID;
                break;
            }
        }
        if (curInfo.enpassantPawnFieldID != nextInfo.enpassantPawnFieldID) {
            this.hashkey ^= ConstantStructure.HAS_ENPASSANT;
        }
        if (figureColour == 0) {
            nextInfo.b_kingSideAvailable = curInfo.b_kingSideAvailable;
            nextInfo.b_queenSideAvailable = curInfo.b_queenSideAvailable;
            if (curInfo.w_kingSideAvailable) {
                switch (pid) {
                    case 6: {
                        this.hashkey ^= ConstantStructure.CASTLE_KING_SIDE_BY_COLOUR[figureColour];
                        this.pawnskey ^= ConstantStructure.CASTLE_KING_SIDE_BY_COLOUR[figureColour];
                        nextInfo.w_kingSideAvailable = false;
                        break;
                    }
                    case 4: {
                        if (fromFieldID == 7) {
                            this.hashkey ^= ConstantStructure.CASTLE_KING_SIDE_BY_COLOUR[figureColour];
                            this.pawnskey ^= ConstantStructure.CASTLE_KING_SIDE_BY_COLOUR[figureColour];
                            nextInfo.w_kingSideAvailable = false;
                            break;
                        }
                        nextInfo.w_kingSideAvailable = true;
                        break;
                    }
                    default: {
                        nextInfo.w_kingSideAvailable = true;
                        break;
                    }
                }
            } else {
                nextInfo.w_kingSideAvailable = false;
            }
            if (curInfo.w_queenSideAvailable) {
                switch (pid) {
                    case 6: {
                        this.hashkey ^= ConstantStructure.CASTLE_QUEEN_SIDE_BY_COLOUR[figureColour];
                        this.pawnskey ^= ConstantStructure.CASTLE_QUEEN_SIDE_BY_COLOUR[figureColour];
                        nextInfo.w_queenSideAvailable = false;
                        break;
                    }
                    case 4: {
                        if (fromFieldID == 0) {
                            this.hashkey ^= ConstantStructure.CASTLE_QUEEN_SIDE_BY_COLOUR[figureColour];
                            this.pawnskey ^= ConstantStructure.CASTLE_QUEEN_SIDE_BY_COLOUR[figureColour];
                            nextInfo.w_queenSideAvailable = false;
                            break;
                        }
                        nextInfo.w_queenSideAvailable = true;
                        break;
                    }
                    default: {
                        nextInfo.w_queenSideAvailable = true;
                        break;
                    }
                }
            } else {
                nextInfo.w_queenSideAvailable = false;
            }
        } else {
            nextInfo.w_kingSideAvailable = curInfo.w_kingSideAvailable;
            nextInfo.w_queenSideAvailable = curInfo.w_queenSideAvailable;
            if (curInfo.b_kingSideAvailable) {
                switch (pid) {
                    case 12: {
                        this.hashkey ^= ConstantStructure.CASTLE_KING_SIDE_BY_COLOUR[figureColour];
                        this.pawnskey ^= ConstantStructure.CASTLE_KING_SIDE_BY_COLOUR[figureColour];
                        nextInfo.b_kingSideAvailable = false;
                        break;
                    }
                    case 10: {
                        if (fromFieldID == 63) {
                            this.hashkey ^= ConstantStructure.CASTLE_KING_SIDE_BY_COLOUR[figureColour];
                            this.pawnskey ^= ConstantStructure.CASTLE_KING_SIDE_BY_COLOUR[figureColour];
                            nextInfo.b_kingSideAvailable = false;
                            break;
                        }
                        nextInfo.b_kingSideAvailable = true;
                        break;
                    }
                    default: {
                        nextInfo.b_kingSideAvailable = true;
                        break;
                    }
                }
            } else {
                nextInfo.b_kingSideAvailable = false;
            }
            if (curInfo.b_queenSideAvailable) {
                switch (pid) {
                    case 12: {
                        this.hashkey ^= ConstantStructure.CASTLE_QUEEN_SIDE_BY_COLOUR[figureColour];
                        this.pawnskey ^= ConstantStructure.CASTLE_QUEEN_SIDE_BY_COLOUR[figureColour];
                        nextInfo.b_queenSideAvailable = false;
                        break;
                    }
                    case 10: {
                        if (fromFieldID == 56) {
                            this.hashkey ^= ConstantStructure.CASTLE_QUEEN_SIDE_BY_COLOUR[figureColour];
                            this.pawnskey ^= ConstantStructure.CASTLE_QUEEN_SIDE_BY_COLOUR[figureColour];
                            nextInfo.b_queenSideAvailable = false;
                            break;
                        }
                        nextInfo.b_queenSideAvailable = true;
                        break;
                    }
                    default: {
                        nextInfo.b_queenSideAvailable = true;
                        break;
                    }
                }
            } else {
                nextInfo.b_queenSideAvailable = false;
            }
        }
        this.pieces.move(pid, fromFieldID, toFieldID);
        this.board[fromFieldID] = 0;
        this.board[toFieldID] = pid;
        this.hashkey ^= ConstantStructure.getMoveHash(pid, fromFieldID, toFieldID);
        if (figureType == 1 || figureType == 6) {
            this.pawnskey ^= ConstantStructure.getMoveHash(pid, fromFieldID, toFieldID);
        }
        if (MoveInt.isCapture(move)) {
            int capturedPID = MoveInt.getCapturedFigurePID(move);
            int capturedFigureType = Constants.PIECE_IDENTITY_2_TYPE[capturedPID];
            if (MoveInt.isEnpassant(move)) {
                int capturedFieldID = MoveInt.getEnpassantCapturedFieldID(move);
                this.board[capturedFieldID] = 0;
                this.pieces.rem(capturedPID, capturedFieldID);
                this.hashkey ^= ConstantStructure.MOVES_KEYS[capturedPID][capturedFieldID];
                if (capturedFigureType == 1) {
                    this.pawnskey ^= ConstantStructure.MOVES_KEYS[capturedPID][capturedFieldID];
                }
            } else {
                this.pieces.rem(capturedPID, toFieldID);
                this.hashkey ^= ConstantStructure.MOVES_KEYS[capturedPID][toFieldID];
                if (capturedFigureType == 1) {
                    this.pawnskey ^= ConstantStructure.MOVES_KEYS[capturedPID][toFieldID];
                }
            }
        } else if (MoveInt.isCastling(move)) {
            int castlePID = MoveInt.getCastlingRookPID(move);
            int fromCastleFieldID = MoveInt.getCastlingRookFromID(move);
            int toCastleFieldID = MoveInt.getCastlingRookToID(move);
            this.pieces.move(castlePID, fromCastleFieldID, toCastleFieldID);
            this.board[fromCastleFieldID] = 0;
            this.board[toCastleFieldID] = castlePID;
            this.hashkey ^= ConstantStructure.MOVES_KEYS[castlePID][fromCastleFieldID];
            this.hashkey ^= ConstantStructure.MOVES_KEYS[castlePID][toCastleFieldID];
            IBoard.CastlingType castlingType = this.castledByColour[figureColour] = MoveInt.isCastleKingSide(move) ? IBoard.CastlingType.KINGSIDE : IBoard.CastlingType.QUEENSIDE;
        }
        if (MoveInt.isPromotion(move)) {
            this.pieces.rem(pid, toFieldID);
            this.hashkey ^= ConstantStructure.MOVES_KEYS[pid][toFieldID];
            this.pawnskey ^= ConstantStructure.MOVES_KEYS[pid][toFieldID];
            int promotedFigurePID = MoveInt.getPromotionFigurePID(move);
            this.pieces.add(promotedFigurePID, toFieldID);
            this.hashkey ^= ConstantStructure.MOVES_KEYS[promotedFigurePID][toFieldID];
            this.board[toFieldID] = promotedFigurePID;
        }
        this.switchLastMoveColour();
        this.playedBoardStates.inc(this.hashkey);
        this.playedMoves[this.playedMovesCount++] = move;
        if (this.moveListeners.length > 0) {
            for (int i = 0; i < this.moveListeners.length; ++i) {
                this.moveListeners[i].postForwardMove(MoveInt.getColour(move), move);
            }
        }
        this.invalidatedInChecksCache();
    }

    @Override
    public final void makeMoveBackward(int move) {
        this.makeMoveBackward(move, true);
    }

    public final void makeMoveBackward(int move, boolean invalidateCheckKeepers) {
        BackupInfo prevInfo;
        if (this.eval != null) {
            this.eval.unmove(move);
        }
        if (this.moveListeners.length > 0) {
            for (int i = 0; i < this.moveListeners.length; ++i) {
                this.moveListeners[i].preBackwardMove(MoveInt.getColour(move), move);
            }
        }
        int pid = MoveInt.getFigurePID(move);
        int fromFieldID = MoveInt.getFromFieldID(move);
        int toFieldID = MoveInt.getToFieldID(move);
        BackupInfo curInfo = this.backupInfo[this.playedMovesCount];
        BackupInfo backupInfo = prevInfo = this.playedMovesCount > 0 ? this.backupInfo[this.playedMovesCount - 1] : null;
        if (prevInfo == null || curInfo.enpassantPawnFieldID != prevInfo.enpassantPawnFieldID) {
            // empty if block
        }
        curInfo.enpassantPawnFieldID = -1;
        this.board[fromFieldID] = pid;
        this.board[toFieldID] = 0;
        if (!MoveInt.isPromotion(move)) {
            this.pieces.move(pid, toFieldID, fromFieldID);
        }
        if (MoveInt.isPromotion(move)) {
            int promotedFigurePID = MoveInt.getPromotionFigurePID(move);
            this.pieces.rem(promotedFigurePID, toFieldID);
            this.pieces.add(pid, fromFieldID);
        }
        if (MoveInt.isCapture(move)) {
            int capturedPID = MoveInt.getCapturedFigurePID(move);
            if (MoveInt.isEnpassant(move)) {
                int capturedFieldID = MoveInt.getEnpassantCapturedFieldID(move);
                this.board[capturedFieldID] = capturedPID;
                this.pieces.add(capturedPID, capturedFieldID);
            } else {
                this.board[toFieldID] = capturedPID;
                this.pieces.add(capturedPID, toFieldID);
            }
        } else if (MoveInt.isCastling(move)) {
            int castlePID = MoveInt.getCastlingRookPID(move);
            int fromCastleFieldID = MoveInt.getCastlingRookFromID(move);
            int toCastleFieldID = MoveInt.getCastlingRookToID(move);
            this.pieces.move(castlePID, toCastleFieldID, fromCastleFieldID);
            this.board[fromCastleFieldID] = castlePID;
            this.board[toCastleFieldID] = 0;
            this.castledByColour[MoveInt.getColour((int)move)] = IBoard.CastlingType.NONE;
        }
        this.switchLastMoveColour();
        this.playedMoves[--this.playedMovesCount] = 0;
        if (this.moveListeners.length > 0) {
            for (int i = 0; i < this.moveListeners.length; ++i) {
                this.moveListeners[i].postBackwardMove(MoveInt.getColour(move), move);
            }
        }
        this.hashkey = prevInfo.hashkey;
        this.pawnskey = prevInfo.pawnshash;
        this.lastCaptureOrPawnMoveBefore = prevInfo.lastCaptureOrPawnMoveBefore;
        this.invalidatedInChecksCache();
    }

    @Override
    public void makeNullMoveForward() {
        BackupInfo curInfo = this.backupInfo[this.playedMovesCount];
        BackupInfo nextInfo = this.backupInfo[this.playedMovesCount + 1];
        nextInfo.w_kingSideAvailable = curInfo.w_kingSideAvailable;
        nextInfo.w_queenSideAvailable = curInfo.w_queenSideAvailable;
        nextInfo.b_kingSideAvailable = curInfo.b_kingSideAvailable;
        nextInfo.b_queenSideAvailable = curInfo.b_queenSideAvailable;
        nextInfo.enpassantPawnFieldID = curInfo.enpassantPawnFieldID;
        this.playedMoves[this.playedMovesCount++] = 0;
        this.switchLastMoveColour();
    }

    @Override
    public final void makeNullMoveBackward() {
        this.playedMoves[--this.playedMovesCount] = 0;
        this.switchLastMoveColour();
    }

    public final void switchLastMoveColour() {
        this.lastMoveColour = Figures.OPPONENT_COLOUR[this.lastMoveColour];
        this.hashkey ^= ConstantStructure.WHITE_TO_MOVE;
        this.pawnskey ^= ConstantStructure.WHITE_TO_MOVE;
    }

    @Override
    public SEE getSee() {
        return this.see;
    }

    protected final boolean kingSidePossible(int colour, int opponentColour) {
        int[] fieldIDs = Castling.CHECKING_CHECK_FIELD_IDS_ON_KING_SIDE_BY_COLOUR[colour];
        boolean notOccupied = true;
        for (int i = 0; i < fieldIDs.length; ++i) {
            if (this.board[fieldIDs[i]] == 0) continue;
            notOccupied = false;
            break;
        }
        return (colour == 0 ? this.backupInfo[this.playedMovesCount].w_kingSideAvailable : this.backupInfo[this.playedMovesCount].b_kingSideAvailable) && notOccupied && this.board[colour == 0 ? 7 : 63] == (colour == 0 ? 4 : 10) && this.checkCheckingAtKingSideFields(colour, opponentColour);
    }

    protected final boolean queenSidePossible(int colour, int opponentColour) {
        int[] fieldIDs = Castling.CHECKING_CHECK_FIELD_IDS_ON_QUEEN_SIDE_BY_COLOUR[colour];
        boolean notOccupied = true;
        for (int i = 0; i < fieldIDs.length; ++i) {
            if (this.board[fieldIDs[i]] == 0) continue;
            notOccupied = false;
            break;
        }
        if (notOccupied) {
            int BfieldID;
            int n = BfieldID = colour == 0 ? 1 : 57;
            if (this.board[BfieldID] != 0) {
                notOccupied = false;
            }
        }
        return (colour == 0 ? this.backupInfo[this.playedMovesCount].w_queenSideAvailable : this.backupInfo[this.playedMovesCount].b_queenSideAvailable) && notOccupied && this.board[colour == 0 ? 0 : 56] == (colour == 0 ? 4 : 10) && this.checkCheckingAtQueenSideFields(colour, opponentColour);
    }

    private final boolean checkCheckingAtKingSideFields(int colour, int opponentColour) {
        int kingFieldID = this.getKingFieldID(colour);
        if (FieldAttack.isFieldAttacked(kingFieldID, opponentColour, this.board, this.pieces)) {
            return false;
        }
        int[] fieldsIDs = Castling.CHECKING_CHECK_FIELD_IDS_ON_KING_SIDE_BY_COLOUR[colour];
        for (int i = 0; i < fieldsIDs.length; ++i) {
            int fieldID = fieldsIDs[i];
            if (!FieldAttack.isFieldAttacked(fieldID, opponentColour, this.board, this.pieces)) continue;
            return false;
        }
        return true;
    }

    private final boolean checkCheckingAtQueenSideFields(int colour, int opponentColour) {
        int kingFieldID = this.getKingFieldID(colour);
        if (FieldAttack.isFieldAttacked(kingFieldID, opponentColour, this.board, this.pieces)) {
            return false;
        }
        int[] fieldsIDs = Castling.CHECKING_CHECK_FIELD_IDS_ON_QUEEN_SIDE_BY_COLOUR[colour];
        for (int i = 0; i < fieldsIDs.length; ++i) {
            int fieldID = fieldsIDs[i];
            if (!FieldAttack.isFieldAttacked(fieldID, opponentColour, this.board, this.pieces)) continue;
            return false;
        }
        return true;
    }

    protected final int getKingFieldID(int colour) {
        int kingFieldID = this.pieces.getPieces(colour == 0 ? 6 : 12).getData()[0];
        return kingFieldID;
    }

    protected final PiecesList getKingIndexSet(int colour) {
        return this.pieces.getPieces(colour == 0 ? 6 : 12);
    }

    public final int genAllMoves(IInternalMoveList list, boolean checkKeepersAware) {
        return this.genAllMoves(0L, false, this.getColourToMove(), list, 256);
    }

    @Override
    public final int genAllMoves(IInternalMoveList list) {
        return this.genAllMoves(0L, true, this.getColourToMove(), list, 256);
    }

    public final int genAllMoves(IInternalMoveList list, long excludedToFieldsBoard) {
        return this.genAllMoves(excludedToFieldsBoard, true, this.getColourToMove(), list, 256);
    }

    protected final int genAllMoves(long excludedToFieldsBoard, boolean checkKeepersAware, int colour, IInternalMoveList list, int maxCount) {
        byte opponentColour = Figures.OPPONENT_COLOUR[colour];
        this.genAllMoves_FiguresWithSameType(colour, opponentColour, 1, list);
        this.genAllMoves_FiguresWithSameType(colour, opponentColour, 2, list);
        this.genAllMoves_FiguresWithSameType(colour, opponentColour, 3, list);
        this.genAllMoves_FiguresWithSameType(colour, opponentColour, 4, list);
        this.genAllMoves_FiguresWithSameType(colour, opponentColour, 5, list);
        this.genAllMoves_FiguresWithSameType(colour, opponentColour, 6, list);
        return 0;
    }

    private final void genAllMoves_FiguresWithSameType(int colour, int opponentColour, int type, IInternalMoveList list) {
        int pid = Figures.getPidByColourAndType(colour, type);
        PiecesList fields = this.pieces.getPieces(pid);
        int size = fields.getDataSize();
        int[] data = fields.getData();
        for (int i = 0; i < size; ++i) {
            int fieldID = data[i];
            this.genAllMoves_ByFigureID(fieldID, pid, colour, opponentColour, type, list);
        }
    }

    private final void genAllMoves_ByFigureID(int fieldID, int pid, int colour, int opponentColour, int type, IInternalMoveList list) {
        switch (type) {
            case 2: {
                KnightMovesGen.genAllMoves(pid, fieldID, this.board, list);
                break;
            }
            case 6: {
                int opKingID = this.getKingFieldID(opponentColour);
                KingMovesGen.genAllMoves(pid, fieldID, this.board, opKingID, this.kingSidePossible(colour, opponentColour), this.queenSidePossible(colour, opponentColour), list);
                break;
            }
            case 1: {
                if (colour == 0) {
                    WhitePawnMovesGen.genAllMoves(fieldID, this.board, this.backupInfo[this.playedMovesCount].enpassantPawnFieldID, list);
                    break;
                }
                BlackPawnMovesGen.genAllMoves(fieldID, this.board, this.backupInfo[this.playedMovesCount].enpassantPawnFieldID, list);
                break;
            }
            case 3: {
                OfficerMovesGen.genAllMoves(pid, fieldID, this.board, list);
                break;
            }
            case 4: {
                CastleMovesGen.genAllMoves(pid, fieldID, this.board, list);
                break;
            }
            case 5: {
                QueenMovesGen.genAllMoves(pid, fieldID, this.board, list);
            }
        }
    }

    @Override
    public final int genCapturePromotionMoves(IInternalMoveList list) {
        return this.genCapturePromotionMoves(this.getColourToMove(), list, 256);
    }

    private final int genCapturePromotionMoves(int colour, IInternalMoveList list, int maxCount) {
        byte opponentColour = Figures.OPPONENT_COLOUR[colour];
        this.genCaptureMoves_FiguresWithSameType(colour, opponentColour, 1, list, maxCount);
        this.genCaptureMoves_FiguresWithSameType(colour, opponentColour, 2, list, maxCount);
        this.genCaptureMoves_FiguresWithSameType(colour, opponentColour, 3, list, maxCount);
        this.genCaptureMoves_FiguresWithSameType(colour, opponentColour, 4, list, maxCount);
        this.genCaptureMoves_FiguresWithSameType(colour, opponentColour, 5, list, maxCount);
        this.genCaptureMoves_FiguresWithSameType(colour, opponentColour, 6, list, maxCount);
        return 0;
    }

    public final void genCaptureMoves_FiguresWithSameType(int colour, int opponentColour, int type, IInternalMoveList list, int maxCount) {
        int pid = Figures.getPidByColourAndType(colour, type);
        PiecesList fields = this.pieces.getPieces(pid);
        int size = fields.getDataSize();
        int[] data = fields.getData();
        for (int i = 0; i < size; ++i) {
            int fieldID = data[i];
            this.genCaptureMoves_ByFigureID(fieldID, pid, colour, opponentColour, type, list);
        }
    }

    private final void genCaptureMoves_ByFigureID(int fieldID, int pid, int colour, int opponentColour, int type, IInternalMoveList list) {
        switch (type) {
            case 2: {
                KnightMovesGen.genCaptureMoves(pid, fieldID, this.board, list);
                break;
            }
            case 6: {
                int opKingID = this.getKingFieldID(opponentColour);
                KingMovesGen.genCaptureMoves(pid, fieldID, this.board, opKingID, list);
                break;
            }
            case 1: {
                if (colour == 0) {
                    WhitePawnMovesGen.genCapturePromotionMoves(fieldID, this.board, list);
                    break;
                }
                BlackPawnMovesGen.genCapturePromotionMoves(fieldID, this.board, list);
                break;
            }
            case 3: {
                OfficerMovesGen.genCaptureMoves(pid, fieldID, this.board, list);
                break;
            }
            case 4: {
                CastleMovesGen.genCaptureMoves(pid, fieldID, this.board, list);
                break;
            }
            case 5: {
                QueenMovesGen.genCaptureMoves(pid, fieldID, this.board, list);
            }
        }
    }

    @Override
    public final int genNonCaptureNonPromotionMoves(IInternalMoveList list) {
        return this.genNonCaptureNonPromotionMoves(this.getColourToMove(), list, 256);
    }

    private final int genNonCaptureNonPromotionMoves(int colour, IInternalMoveList list, int maxCount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void revert() {
        int count = this.getPlayedMovesCount();
        int[] moves = this.getPlayedMoves();
        for (int i = count - 1; i >= 0; --i) {
            if (moves[i] == 0) {
                this.makeNullMoveBackward();
                continue;
            }
            this.makeMoveBackward(moves[i]);
        }
    }

    private void init(int[] boardArr) {
        for (int fieldID = 0; fieldID < 64; ++fieldID) {
            int pid;
            this.board[fieldID] = pid = boardArr[fieldID];
            if (pid == 0) continue;
            this.pieces.add(pid, fieldID);
            this.materialFactor.initially_addPiece(pid, fieldID, 0L);
            this.materialState.initially_addPiece(pid, fieldID, 0L);
            if (this.eval != null) {
                this.eval.initially_addPiece(pid, fieldID, 0L);
            }
            this.hashkey ^= ConstantStructure.MOVES_KEYS[pid][fieldID];
            if (pid != 1 && pid != 7 && pid != 6 && pid != 12) continue;
            this.pawnskey ^= ConstantStructure.MOVES_KEYS[pid][fieldID];
        }
    }

    @Override
    public final IPiecesLists getPiecesLists() {
        return this.pieces;
    }

    @Override
    public final long getPawnsHashKey() {
        return this.pawnskey;
    }

    @Override
    public IBaseEval getBaseEvaluation() {
        return this.eval;
    }

    @Override
    public IMaterialFactor getMaterialFactor() {
        return this.materialFactor;
    }

    @Override
    public IMaterialState getMaterialState() {
        return this.materialState;
    }

    @Override
    public int getFigureID(int fieldID) {
        return this.board[fieldID];
    }

    @Override
    public boolean hasRightsToKingCastle(int colour) {
        return colour == 0 ? this.backupInfo[this.playedMovesCount].w_kingSideAvailable : this.backupInfo[this.playedMovesCount].b_kingSideAvailable;
    }

    @Override
    public boolean hasRightsToQueenCastle(int colour) {
        return colour == 0 ? this.backupInfo[this.playedMovesCount].w_queenSideAvailable : this.backupInfo[this.playedMovesCount].b_queenSideAvailable;
    }

    @Override
    public int getPlayedMovesCount() {
        return this.playedMovesCount;
    }

    @Override
    public int[] getPlayedMoves() {
        return this.playedMoves;
    }

    @Override
    public int getLastMove() {
        if (this.playedMovesCount > 0) {
            return this.playedMoves[this.playedMovesCount - 1];
        }
        return 0;
    }

    public int getLastLastMove() {
        if (this.playedMovesCount > 1) {
            return this.playedMoves[this.playedMovesCount - 2];
        }
        return 0;
    }

    @Override
    public final int getColourToMove() {
        return Figures.OPPONENT_COLOUR[this.lastMoveColour];
    }

    @Override
    public final int getStateRepetition() {
        return this.getStateRepetition(this.hashkey);
    }

    public final int getStateRepetition(long hashkey) {
        int count = this.playedBoardStates.get(hashkey);
        if (count == -1) {
            return 0;
        }
        return count;
    }

    @Override
    public final long getHashKey() {
        return this.hashkey;
    }

    public final StackLongInt getPlayedBoardStates() {
        return this.playedBoardStates;
    }

    public IBoard clone() {
        Board3 clone = null;
        try {
            clone = (Board3)super.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        this.cloneInternal(clone);
        return clone;
    }

    public void cloneInternal(Board3 clone) {
        clone.board = Utils.copy(this.board);
        clone.lastMoveColour = this.lastMoveColour;
        clone.hashkey = this.hashkey;
        clone.pawnskey = this.pawnskey;
    }

    public int hashCode() {
        return (int)this.hashkey;
    }

    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof Board3) {
            Board3 other = (Board3)obj;
            result = Arrays.equals(other.board, this.board);
            result = result && this.checkAliveFiguresData(other);
            result = result && Arrays.equals((Object[])other.castledByColour, (Object[])this.castledByColour);
            result = result && other.lastMoveColour == this.lastMoveColour;
            result = result && other.hashkey == this.hashkey;
            result = result && other.pawnskey == this.pawnskey;
        }
        return result;
    }

    private boolean checkAliveFiguresData(Board3 other) {
        return true;
    }

    public String toString() {
        Object result = "\r\nWhite: \r\n";
        result = (String)result + "Black: \r\n";
        result = (String)result + this.matrixToString();
        result = (String)result + "Moves: " + this.movesToString() + "\r\n";
        result = (String)result + "EPD: " + this.toEPD() + "\r\n";
        return result;
    }

    private String matrixToString() {
        Object result = "";
        int counter = 0;
        Object line = "";
        for (int square = 0; square < this.board.length; ++square) {
            int pieceID = this.board[square];
            Object squareStr = Constants.getPieceIDString(pieceID);
            squareStr = (String)squareStr + "  ";
            line = (String)line + (String)squareStr;
            if (++counter != 8) continue;
            counter = 0;
            result = (String)line + "\r\n" + (String)result;
            line = "";
        }
        result = (String)result + "\r\n";
        result = (String)result + "\r\n";
        result = (String)result + "Hashkey : " + this.hashkey;
        result = (String)result + "\r\n";
        result = (String)result + "Pawnkey : " + this.pawnskey;
        result = (String)result + "\r\n";
        return result;
    }

    private String movesToString() {
        Object result = "";
        for (int i = 0; i < this.playedMovesCount; ++i) {
            int move = this.playedMoves[i];
            result = (String)result + this.getMoveOps().moveToString(move) + ", ";
        }
        return result;
    }

    protected void checkConsistency() {
        long allWhiteBitboard = 0L;
        allWhiteBitboard |= this.checkConsistency_AliveFiguresByTypeAndColour(0, 1, this.pieces.getPieces(1));
        allWhiteBitboard |= this.checkConsistency_AliveFiguresByTypeAndColour(0, 2, this.pieces.getPieces(2));
        allWhiteBitboard |= this.checkConsistency_AliveFiguresByTypeAndColour(0, 6, this.pieces.getPieces(6));
        allWhiteBitboard |= this.checkConsistency_AliveFiguresByTypeAndColour(0, 3, this.pieces.getPieces(3));
        allWhiteBitboard |= this.checkConsistency_AliveFiguresByTypeAndColour(0, 4, this.pieces.getPieces(4));
        allWhiteBitboard |= this.checkConsistency_AliveFiguresByTypeAndColour(0, 5, this.pieces.getPieces(5));
        long allBlackBitboard = 0L;
        allBlackBitboard |= this.checkConsistency_AliveFiguresByTypeAndColour(1, 1, this.pieces.getPieces(7));
        allBlackBitboard |= this.checkConsistency_AliveFiguresByTypeAndColour(1, 2, this.pieces.getPieces(8));
        allBlackBitboard |= this.checkConsistency_AliveFiguresByTypeAndColour(1, 6, this.pieces.getPieces(12));
        allBlackBitboard |= this.checkConsistency_AliveFiguresByTypeAndColour(1, 3, this.pieces.getPieces(9));
        allBlackBitboard |= this.checkConsistency_AliveFiguresByTypeAndColour(1, 4, this.pieces.getPieces(10));
        allBlackBitboard |= this.checkConsistency_AliveFiguresByTypeAndColour(1, 5, this.pieces.getPieces(11));
    }

    protected long checkConsistency_AliveFiguresByTypeAndColour(int colour, int type, PiecesList figIDs) {
        long typeBitboard = 0L;
        for (int i = 0; i < 64; ++i) {
            int pid = this.board[i];
            if (pid != Figures.getPidByColourAndType(colour, type) || figIDs.contains(i)) continue;
            throw new IllegalStateException();
        }
        int size = figIDs.getDataSize();
        int[] data = figIDs.getData();
        for (int i = 0; i < size; ++i) {
            int fieldID = data[i];
            if (fieldID < 0 || fieldID >= 64) {
                throw new IllegalStateException("figureID == " + fieldID);
            }
            if (this.board[fieldID] != Figures.getPidByColourAndType(colour, type)) {
                throw new IllegalStateException("board[fieldID]=" + this.board[fieldID] + ", Figures.getPidByColourAndType(colour, type)=" + Figures.getPidByColourAndType(colour, type));
            }
            long figBitboard = Fields.ALL_ORDERED_A1H1[fieldID];
            if (fieldID != Board3.get67IDByBitboard(figBitboard)) {
                throw new IllegalStateException("fieldID=" + fieldID + " figBitboard=" + figBitboard);
            }
            typeBitboard |= figBitboard;
        }
        return typeBitboard;
    }

    public BackupInfo[] getBackups() {
        return this.backupInfo;
    }

    public int[] getOpeningMoves() {
        int movesCount = this.getPlayedMovesCount();
        int[] moves = this.getPlayedMoves();
        int[] o_moves = new int[movesCount];
        for (int i = 0; i < movesCount; ++i) {
            o_moves[i] = moves[i];
        }
        return o_moves;
    }

    @Override
    public IBoardConfig getBoardConfig() {
        return this.boardConfig;
    }

    @Override
    public boolean isDraw50movesRule() {
        return this.lastCaptureOrPawnMoveBefore >= 100;
    }

    @Override
    public int getDraw50movesRule() {
        return this.lastCaptureOrPawnMoveBefore;
    }

    @Override
    public boolean hasSufficientMatingMaterial() {
        return this.hasSufficientMatingMaterial(0) || this.hasSufficientMatingMaterial(1);
    }

    @Override
    public boolean hasSufficientMatingMaterial(int color) {
        long pawns = this.getFiguresBitboardByColourAndType(color, 1);
        if (pawns != 0L) {
            return true;
        }
        long queens = this.getFiguresBitboardByColourAndType(color, 5);
        if (queens != 0L) {
            return true;
        }
        long rooks = this.getFiguresBitboardByColourAndType(color, 4);
        if (rooks != 0L) {
            return true;
        }
        long bishops = this.getFiguresBitboardByColourAndType(color, 3);
        long knights = this.getFiguresBitboardByColourAndType(color, 2);
        if (Utils.countBits(bishops) + Utils.countBits(knights) >= 3) {
            return true;
        }
        if (bishops != 0L && (bishops & 0x55AA55AA55AA55AAL) != 0L && (bishops & 0xAA55AA55AA55AA55L) != 0L) {
            return true;
        }
        return Utils.countBits(bishops) == 1 && Utils.countBits(knights) == 1 && (bishops & 0x55AA55AA55AA55AAL) != 0L && (bishops & 0xAA55AA55AA55AA55L) != 0L;
    }

    @Override
    public boolean isInCheck() {
        int colourToMove = this.getColourToMove();
        boolean result = this.isInCheck(colourToMove);
        return result;
    }

    @Override
    public boolean isInCheck(int colour) {
        if (this.inCheckCacheInitialized[colour]) {
            return this.inCheckCache[colour];
        }
        if (this.pieces.getPieces(6).getDataSize() == 0) {
            return false;
        }
        if (this.pieces.getPieces(12).getDataSize() == 0) {
            return false;
        }
        int kingFieldID = colour == 0 ? this.pieces.getPieces(6).getData()[0] : this.pieces.getPieces(12).getData()[0];
        boolean result = FieldAttack.isFieldAttacked(kingFieldID, Constants.COLOUR_OP[colour], this.board, this.pieces);
        this.inCheckCacheInitialized[colour] = true;
        this.inCheckCache[colour] = result;
        return result;
    }

    protected final void invalidatedInChecksCache() {
        this.inCheckCacheInitialized[0] = false;
        this.inCheckCacheInitialized[1] = false;
    }

    @Override
    public boolean hasMoveInCheck() {
        return true;
    }

    @Override
    public boolean hasMoveInNonCheck() {
        return true;
    }

    @Override
    public int[] getMatrix() {
        return this.board;
    }
}

