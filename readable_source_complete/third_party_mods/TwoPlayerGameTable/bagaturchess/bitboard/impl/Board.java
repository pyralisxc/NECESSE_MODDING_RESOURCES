/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl;

import bagaturchess.bitboard.api.IAttackListener;
import bagaturchess.bitboard.api.IBaseEval;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IBoard;
import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.api.IFieldsAttacks;
import bagaturchess.bitboard.api.IGameStatus;
import bagaturchess.bitboard.api.IInternalMoveList;
import bagaturchess.bitboard.api.IMaterialFactor;
import bagaturchess.bitboard.api.IMaterialState;
import bagaturchess.bitboard.api.IMobility;
import bagaturchess.bitboard.api.IMoveOps;
import bagaturchess.bitboard.api.IPiecesLists;
import bagaturchess.bitboard.api.IPlayerAttacks;
import bagaturchess.bitboard.api.PawnsEvalCache;
import bagaturchess.bitboard.common.BackupInfo;
import bagaturchess.bitboard.common.BoardStat;
import bagaturchess.bitboard.common.Fen;
import bagaturchess.bitboard.common.MoveListener;
import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.Bits;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.attacks.SEE;
import bagaturchess.bitboard.impl.attacks.control.AttackListener_Mobility;
import bagaturchess.bitboard.impl.attacks.control.FieldsStateMachine;
import bagaturchess.bitboard.impl.attacks.fast.FastPlayersAttacks;
import bagaturchess.bitboard.impl.datastructs.StackLongInt;
import bagaturchess.bitboard.impl.datastructs.numbers.IndexNumberMap;
import bagaturchess.bitboard.impl.endgame.MaterialState;
import bagaturchess.bitboard.impl.eval.BaseEvaluation;
import bagaturchess.bitboard.impl.eval.MaterialFactor;
import bagaturchess.bitboard.impl.eval.pawns.model.Pawn;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnStructureConstants;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModel;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEval;
import bagaturchess.bitboard.impl.movegen.BlackPawnMovesGen;
import bagaturchess.bitboard.impl.movegen.CastleMovesGen;
import bagaturchess.bitboard.impl.movegen.KingMovesGen;
import bagaturchess.bitboard.impl.movegen.KnightMovesGen;
import bagaturchess.bitboard.impl.movegen.MoveInt;
import bagaturchess.bitboard.impl.movegen.MoveOpsImpl;
import bagaturchess.bitboard.impl.movegen.OfficerMovesGen;
import bagaturchess.bitboard.impl.movegen.QueenMovesGen;
import bagaturchess.bitboard.impl.movegen.WhitePawnMovesGen;
import bagaturchess.bitboard.impl.movelist.BaseMoveList;
import bagaturchess.bitboard.impl.plies.BlackPawnPlies;
import bagaturchess.bitboard.impl.plies.CastlePlies;
import bagaturchess.bitboard.impl.plies.KingPlies;
import bagaturchess.bitboard.impl.plies.KnightPlies;
import bagaturchess.bitboard.impl.plies.OfficerPlies;
import bagaturchess.bitboard.impl.plies.WhitePawnPlies;
import bagaturchess.bitboard.impl.plies.checking.BlackPawnsChecks;
import bagaturchess.bitboard.impl.plies.checking.Checker;
import bagaturchess.bitboard.impl.plies.checking.Checking;
import bagaturchess.bitboard.impl.plies.checking.CheckingCount;
import bagaturchess.bitboard.impl.plies.checking.KnightChecks;
import bagaturchess.bitboard.impl.plies.checking.WhitePawnsChecks;
import bagaturchess.bitboard.impl.plies.specials.Castling;
import bagaturchess.bitboard.impl.plies.specials.Enpassanting;
import bagaturchess.bitboard.impl.state.PiecesList;
import bagaturchess.bitboard.impl.state.PiecesLists;
import bagaturchess.bitboard.impl.zobrist.ConstantStructure;
import bagaturchess.bitboard.impl1.internal.CastlingConfig;
import java.util.Arrays;

public class Board
extends Fields
implements IBitBoard,
Cloneable {
    private boolean DEBUG = false;
    public BoardStat statistics;
    public long free;
    public long[] allByColour;
    public long[][] allByColourAndType;
    public int[] board;
    protected BackupInfo[] backupInfo;
    protected int lastMoveColour = 1;
    public PiecesLists pieces;
    protected long hashkey = 0L;
    protected long pawnskey = 0L;
    protected IBoard.CastlingType[] castledByColour;
    protected int lastCastledColour = -1;
    protected int lastCaptureOrPawnMoveBefore = 0;
    protected int lastCaptureFieldID = -1;
    protected int[] playedMoves;
    protected int playedMovesCount = 0;
    protected int playedMovesCount_initial = 0;
    protected int marked_playedMovesCount = 0;
    protected StackLongInt playedBoardStates;
    protected IndexNumberMap[] checkKeepersBuffer;
    protected boolean[] checkKeepersInitialized;
    protected boolean[] inCheckCache;
    protected boolean[] inCheckCacheInitialized;
    protected Checker checkerBuffer;
    protected IInternalMoveList movesBuffer;
    private boolean attacksSupport = false;
    private boolean fieldsStateSupport = false;
    private MoveListener fastPlayerAttacks;
    private MoveListener[] moveListeners;
    private FieldsStateMachine fieldAttacksCollector;
    private BaseEvaluation eval;
    private MaterialFactor materialFactor;
    private MaterialState materialState;
    private PawnsEvalCache pawnsCache;
    private SEE see;
    private IBoardConfig boardConfig;
    private IMobility attackListener;
    private IMoveOps moveOps;

    public Board(String fenStr, PawnsEvalCache _pawnsCache, IBoardConfig _boardConfig) {
        this.pawnsCache = _pawnsCache;
        this.boardConfig = _boardConfig;
        Fen fen = Fen.parse(fenStr);
        this.statistics = new BoardStat();
        this.allByColour = new long[Figures.COLOUR_MAX];
        this.allByColourAndType = new long[Figures.COLOUR_MAX][7];
        this.board = new int[64];
        this.castledByColour = new IBoard.CastlingType[Figures.COLOUR_MAX];
        int n = this.lastMoveColour = fen.getColourToMove() == 0 ? 1 : 0;
        if (fen.getColourToMove() == 0) {
            this.hashkey ^= ConstantStructure.WHITE_TO_MOVE;
            this.pawnskey ^= ConstantStructure.WHITE_TO_MOVE;
        }
        this.pieces = new PiecesLists(this);
        if (fen.getHalfmoveClock() != null) {
            try {
                this.lastCaptureOrPawnMoveBefore = Integer.parseInt(fen.getHalfmoveClock());
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        this.moveListeners = new MoveListener[0];
        this.materialFactor = new MaterialFactor();
        this.addMoveListener(this.materialFactor);
        this.materialState = new MaterialState();
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
        long enpassantTargetPawn = 0L;
        if (fen.getEnpassantTargetSquare() != null) {
            enpassantTargetPawn = Enpassanting.getEnemyBitboard(fen.getEnpassantTargetSquare());
        }
        this.backupInfo[this.playedMovesCount].enpassantPawnBitboard = enpassantTargetPawn;
        this.backupInfo[this.playedMovesCount].w_kingSideAvailable = fen.hasWhiteKingSide();
        this.backupInfo[this.playedMovesCount].w_queenSideAvailable = fen.hasWhiteQueenSide();
        this.backupInfo[this.playedMovesCount].b_kingSideAvailable = fen.hasBlackKingSide();
        this.backupInfo[this.playedMovesCount].b_queenSideAvailable = fen.hasBlackQueenSide();
        if (this.backupInfo[this.playedMovesCount].enpassantPawnBitboard != 0L) {
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
        this.playedBoardStates = new StackLongInt(27767);
        this.playedBoardStates.inc(this.hashkey);
        this.checkKeepersBuffer = new IndexNumberMap[Figures.COLOUR_MAX];
        this.checkKeepersBuffer[0] = new IndexNumberMap(64);
        this.checkKeepersBuffer[1] = new IndexNumberMap(64);
        this.checkKeepersInitialized = new boolean[]{false, false, false};
        this.inCheckCache = new boolean[]{false, false, false};
        this.inCheckCacheInitialized = new boolean[]{false, false, false};
        this.checkerBuffer = new Checker();
        this.movesBuffer = new BaseMoveList(64);
        if (this.boardConfig != null) {
            this.attacksSupport = this.boardConfig.getFieldsStatesSupport();
            this.fieldsStateSupport = this.boardConfig.getFieldsStatesSupport();
            this.initAttacksSupport();
        }
        this.see = new SEE(this);
        this.moveOps = new MoveOpsImpl(this);
        this.checkConsistency();
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
        long enpassTarget = this.backupInfo[this.playedMovesCount].enpassantPawnBitboard;
        if (enpassTarget != 0L) {
            int targetSquareID = Enpassanting.converteEnpassantTargetToFENFormat(enpassTarget);
            result = (String)result + Fields.getFieldSign(targetSquareID);
        } else {
            result = (String)result + "-";
        }
        result = (String)result + " ";
        result = (String)result + this.lastCaptureOrPawnMoveBefore;
        result = (String)result + " ";
        result = (String)result + ((this.getPlayedMovesCount_Total() + 1) / 2 + 1);
        return result;
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
    public IBaseEval getBaseEvaluation() {
        return this.eval;
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
    public void setPawnsCache(PawnsEvalCache pawnsCache) {
        this.pawnsCache = pawnsCache;
    }

    @Override
    public PawnsEvalCache getPawnsCache() {
        return this.pawnsCache;
    }

    public boolean hasUnstoppablePasser() {
        return this.hasUnstoppablePasser(this.getColourToMove());
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

    @Override
    public int getUnstoppablePasser() {
        int fieldID;
        int b_rank;
        int w_rank;
        int result = 0;
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
        if (this.getColourToMove() == 0 ? result > 0 : result < 0) {
            return result;
        }
        return 0;
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

    private final void initAttacksSupport() {
        if (this.attacksSupport) {
            if (!this.fieldsStateSupport) {
                throw new IllegalStateException();
            }
            this.attackListener = this.boardConfig == null ? null : new AttackListener_Mobility(this.boardConfig);
            this.fieldAttacksCollector = new FieldsStateMachine(this, (IAttackListener)((Object)this.attackListener));
            this.fastPlayerAttacks = new FastPlayersAttacks(this, this.fieldAttacksCollector);
            ((FastPlayersAttacks)this.fastPlayerAttacks).checkConsistency();
            this.addMoveListener(this.fastPlayerAttacks);
        }
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
    public final boolean isPossible(int move) {
        long toBitboard;
        if (move == 0) {
            throw new IllegalStateException("isPossible invoked with move = 0");
        }
        int colour = MoveInt.getColour(move);
        if (colour != this.getColourToMove()) {
            throw new IllegalStateException("isPossible invoked with move which color is not the current colour");
        }
        int type = MoveInt.getFigureType(move);
        byte opponentColour = Figures.OPPONENT_COLOUR[colour];
        boolean isInCheck = this.isInCheck(colour);
        if (this.getKingIndexSet(colour).getDataSize() == 0) {
            return false;
        }
        boolean enpassant = false;
        boolean isPossible = false;
        switch (type) {
            case 2: {
                isPossible = KnightMovesGen.isPossible(move, this.board);
                break;
            }
            case 6: {
                int opKingID = this.getKingFieldID(opponentColour);
                isPossible = KingMovesGen.isPossible(this, move, this.board, this.kingSidePossible(colour, opponentColour), this.queenSidePossible(colour, opponentColour), Fields.ALL_ORDERED_A1H1[opKingID], opKingID, this.free);
                break;
            }
            case 1: {
                BackupInfo curEnpassInfo = this.backupInfo[this.playedMovesCount];
                isPossible = colour == 0 ? WhitePawnMovesGen.isPossible(move, this.board, this.free, curEnpassInfo.enpassantPawnBitboard != 0L, curEnpassInfo.enpassantPawnBitboard) : BlackPawnMovesGen.isPossible(move, this.board, this.free, curEnpassInfo.enpassantPawnBitboard != 0L, curEnpassInfo.enpassantPawnBitboard);
                if (!isPossible || !MoveInt.isEnpassant(move)) break;
                enpassant = true;
                break;
            }
            case 3: {
                isPossible = OfficerMovesGen.isPossible(move, this.board, this.free);
                break;
            }
            case 4: {
                isPossible = CastleMovesGen.isPossible(move, this.board, this.free);
                break;
            }
            case 5: {
                isPossible = QueenMovesGen.isPossible(move, this.board, this.free);
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
        if (!isPossible) {
            return false;
        }
        this.fillCheckKeepers(colour);
        long excludedToFieldsBoard = 0L;
        if (this.checkKeepersBuffer[colour].contains(MoveInt.getFromFieldID(move))) {
            excludedToFieldsBoard |= this.checkKeepersBuffer[colour].getValue(MoveInt.getFromFieldID(move)) ^ 0xFFFFFFFFFFFFFFFFL;
        }
        if (((toBitboard = Fields.ALL_ORDERED_A1H1[MoveInt.getToFieldID(move)]) & excludedToFieldsBoard) != 0L) {
            return false;
        }
        if (isInCheck || enpassant) {
            this.makeMoveForward(move);
            isPossible = !this.isInCheck(colour);
            this.makeMoveBackward(move);
        }
        return isPossible;
    }

    @Override
    public void makeMoveForward(String ucimove) {
        int move = this.moveOps.stringToMove(ucimove);
        this.makeMoveForward(move);
    }

    @Override
    public final void makeMoveForward(int move) {
        this.makeMoveForward(move, true);
    }

    public final void makeMoveForward(int move, boolean invalidateCheckKeepers) {
        byte opponentColour;
        long adjoiningFiles;
        if (this.playedMovesCount + 1 >= 2000) {
            throw new IllegalStateException("MORE THAN 2000 MOVES: " + String.valueOf(this));
        }
        if (this.eval != null) {
            this.eval.move(move);
        }
        if (this.DEBUG) {
            this.checkConsistency();
        }
        boolean inCheck = false;
        long expected_key = 0L;
        long expected_pawnkey = 0L;
        if (this.DEBUG) {
            expected_key = this.getHashKeyAfterMove(move);
            expected_pawnkey = this.getPawnHashKeyAfterMove(move);
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
        long fromBoard = Fields.ALL_ORDERED_A1H1[fromFieldID];
        long toBoard = Fields.ALL_ORDERED_A1H1[toFieldID];
        BackupInfo curInfo = this.backupInfo[this.playedMovesCount];
        curInfo.hashkey = this.hashkey;
        curInfo.pawnshash = this.pawnskey;
        curInfo.lastCaptureOrPawnMoveBefore = this.lastCaptureOrPawnMoveBefore++;
        curInfo.lastCaptureFieldID = this.lastCaptureFieldID;
        if (MoveInt.isCapture(move) || MoveInt.isPawn(move)) {
            this.lastCaptureOrPawnMoveBefore = 0;
        }
        if (MoveInt.isCapture(move)) {
            this.lastCaptureFieldID = toFieldID;
        }
        BackupInfo nextInfo = this.backupInfo[this.playedMovesCount + 1];
        nextInfo.enpassantPawnBitboard = figureType == 1 && !MoveInt.isCapture(move) && Math.abs(fromFieldID - toFieldID) == 16 ? (((adjoiningFiles = Enpassanting.ADJOINING_FILES[figureColour][toFieldID]) & this.allByColourAndType[opponentColour = Figures.OPPONENT_COLOUR[figureColour]][1]) != 0L ? toBoard : 0L) : 0L;
        if (curInfo.enpassantPawnBitboard != nextInfo.enpassantPawnBitboard) {
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
        if (this.DEBUG && pid != Figures.getPidByColourAndType(figureColour, figureType)) {
            throw new IllegalStateException();
        }
        this.pieces.move(pid, fromFieldID, toFieldID);
        this.board[fromFieldID] = 0;
        this.board[toFieldID] = pid;
        long[] lArray = this.allByColourAndType[figureColour];
        int n = figureType;
        lArray[n] = lArray[n] & (fromBoard ^ 0xFFFFFFFFFFFFFFFFL);
        long[] lArray2 = this.allByColourAndType[figureColour];
        int n2 = figureType;
        lArray2[n2] = lArray2[n2] | toBoard;
        int n3 = figureColour;
        this.allByColour[n3] = this.allByColour[n3] & (fromBoard ^ 0xFFFFFFFFFFFFFFFFL);
        int n4 = figureColour;
        this.allByColour[n4] = this.allByColour[n4] | toBoard;
        this.hashkey ^= ConstantStructure.getMoveHash(pid, fromFieldID, toFieldID);
        if (figureType == 1 || figureType == 6) {
            this.pawnskey ^= ConstantStructure.getMoveHash(pid, fromFieldID, toFieldID);
        }
        if (MoveInt.isCapture(move)) {
            int capturedPID = MoveInt.getCapturedFigurePID(move);
            byte capturedFigureColour = Figures.OPPONENT_COLOUR[figureColour];
            int capturedFigureType = Constants.PIECE_IDENTITY_2_TYPE[capturedPID];
            long capturedTypeBitboard = this.allByColourAndType[capturedFigureColour][capturedFigureType];
            if (MoveInt.isEnpassant(move)) {
                int capturedFieldID = MoveInt.getEnpassantCapturedFieldID(move);
                long opponentPawnBitboard = Fields.ALL_ORDERED_A1H1[capturedFieldID];
                capturedTypeBitboard &= opponentPawnBitboard ^ 0xFFFFFFFFFFFFFFFFL;
                byte by = capturedFigureColour;
                this.allByColour[by] = this.allByColour[by] & (opponentPawnBitboard ^ 0xFFFFFFFFFFFFFFFFL);
                this.board[capturedFieldID] = 0;
                this.pieces.rem(capturedPID, capturedFieldID);
                this.hashkey ^= ConstantStructure.MOVES_KEYS[capturedPID][capturedFieldID];
                if (capturedFigureType == 1) {
                    this.pawnskey ^= ConstantStructure.MOVES_KEYS[capturedPID][capturedFieldID];
                }
            } else {
                capturedTypeBitboard &= toBoard ^ 0xFFFFFFFFFFFFFFFFL;
                byte by = capturedFigureColour;
                this.allByColour[by] = this.allByColour[by] & (toBoard ^ 0xFFFFFFFFFFFFFFFFL);
                this.pieces.rem(capturedPID, toFieldID);
                this.hashkey ^= ConstantStructure.MOVES_KEYS[capturedPID][toFieldID];
                if (capturedFigureType == 1) {
                    this.pawnskey ^= ConstantStructure.MOVES_KEYS[capturedPID][toFieldID];
                }
            }
            this.allByColourAndType[capturedFigureColour][capturedFigureType] = capturedTypeBitboard;
        } else if (MoveInt.isCastling(move)) {
            int castlePID = MoveInt.getCastlingRookPID(move);
            int fromCastleFieldID = MoveInt.getCastlingRookFromID(move);
            int toCastleFieldID = MoveInt.getCastlingRookToID(move);
            long fromCastleBoard = Fields.ALL_ORDERED_A1H1[fromCastleFieldID];
            long toCastleBoard = Fields.ALL_ORDERED_A1H1[toCastleFieldID];
            this.pieces.move(castlePID, fromCastleFieldID, toCastleFieldID);
            this.board[fromCastleFieldID] = 0;
            this.board[toCastleFieldID] = castlePID;
            long[] lArray3 = this.allByColourAndType[figureColour];
            lArray3[4] = lArray3[4] & (fromCastleBoard ^ 0xFFFFFFFFFFFFFFFFL);
            long[] lArray4 = this.allByColourAndType[figureColour];
            lArray4[4] = lArray4[4] | toCastleBoard;
            int n5 = figureColour;
            this.allByColour[n5] = this.allByColour[n5] & (fromCastleBoard ^ 0xFFFFFFFFFFFFFFFFL);
            int n6 = figureColour;
            this.allByColour[n6] = this.allByColour[n6] | toCastleBoard;
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
            if (this.attacksSupport) {
                this.fastPlayerAttacks.addPiece_Special(promotedFigurePID, toFieldID);
            }
            this.board[toFieldID] = promotedFigurePID;
            long[] lArray5 = this.allByColourAndType[figureColour];
            int n7 = Constants.PIECE_IDENTITY_2_TYPE[promotedFigurePID];
            lArray5[n7] = lArray5[n7] | toBoard;
            int n8 = figureColour;
            this.allByColour[n8] = this.allByColour[n8] | toBoard;
            long[] lArray6 = this.allByColourAndType[figureColour];
            int n9 = figureType;
            lArray6[n9] = lArray6[n9] & (toBoard ^ 0xFFFFFFFFFFFFFFFFL);
        }
        this.free = (this.allByColour[0] | this.allByColour[1]) ^ 0xFFFFFFFFFFFFFFFFL;
        this.switchLastMoveColour();
        if (this.DEBUG && invalidateCheckKeepers) {
            if (this.hashkey != expected_key) {
                throw new IllegalStateException("Wrong hash key");
            }
            if (this.pawnskey != expected_pawnkey) {
                throw new IllegalStateException("Wrong pawn hash key");
            }
        }
        this.playedBoardStates.inc(this.hashkey);
        this.playedMoves[this.playedMovesCount++] = move;
        if (invalidateCheckKeepers) {
            this.invalidatedCheckKeepers();
        }
        this.invalidatedInChecks();
        if (this.moveListeners.length > 0) {
            for (int i = 0; i < this.moveListeners.length; ++i) {
                this.moveListeners[i].postForwardMove(MoveInt.getColour(move), move);
            }
        }
        if (this.DEBUG) {
            this.checkConsistency();
        }
    }

    @Override
    public final void makeMoveBackward(int move) {
        if (this.DEBUG) {
            this.checkConsistency();
        }
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
        int figureColour = MoveInt.getColour(move);
        int figureType = MoveInt.getFigureType(move);
        int fromFieldID = MoveInt.getFromFieldID(move);
        int toFieldID = MoveInt.getToFieldID(move);
        long fromBoard = Fields.ALL_ORDERED_A1H1[fromFieldID];
        long toBoard = Fields.ALL_ORDERED_A1H1[toFieldID];
        if (this.playedBoardStates.dec(this.hashkey) <= -1) {
            // empty if block
        }
        BackupInfo curInfo = this.backupInfo[this.playedMovesCount];
        BackupInfo backupInfo = prevInfo = this.playedMovesCount > 0 ? this.backupInfo[this.playedMovesCount - 1] : null;
        if (prevInfo == null || curInfo.enpassantPawnBitboard != prevInfo.enpassantPawnBitboard) {
            // empty if block
        }
        curInfo.enpassantPawnBitboard = 0L;
        this.board[fromFieldID] = pid;
        this.board[toFieldID] = 0;
        long typeBitboard = this.allByColourAndType[figureColour][figureType];
        typeBitboard |= fromBoard;
        this.allByColourAndType[figureColour][figureType] = typeBitboard &= toBoard ^ 0xFFFFFFFFFFFFFFFFL;
        int n = figureColour;
        this.allByColour[n] = this.allByColour[n] | fromBoard;
        int n2 = figureColour;
        this.allByColour[n2] = this.allByColour[n2] & (toBoard ^ 0xFFFFFFFFFFFFFFFFL);
        if (!MoveInt.isPromotion(move)) {
            this.pieces.move(pid, toFieldID, fromFieldID);
        }
        if (MoveInt.isPromotion(move)) {
            int promotedFigurePID = MoveInt.getPromotionFigurePID(move);
            this.pieces.rem(promotedFigurePID, toFieldID);
            this.pieces.add(pid, fromFieldID);
            long[] lArray = this.allByColourAndType[figureColour];
            int n3 = Constants.PIECE_IDENTITY_2_TYPE[promotedFigurePID];
            lArray[n3] = lArray[n3] & (toBoard ^ 0xFFFFFFFFFFFFFFFFL);
            int n4 = figureColour;
            this.allByColour[n4] = this.allByColour[n4] & (toBoard ^ 0xFFFFFFFFFFFFFFFFL);
        }
        if (MoveInt.isCapture(move)) {
            int capturedPID = MoveInt.getCapturedFigurePID(move);
            byte capturedFigureColour = Figures.OPPONENT_COLOUR[figureColour];
            int capturedFigureType = Constants.PIECE_IDENTITY_2_TYPE[capturedPID];
            long capturedTypeBitboard = this.allByColourAndType[capturedFigureColour][capturedFigureType];
            if (MoveInt.isEnpassant(move)) {
                int capturedFieldID = MoveInt.getEnpassantCapturedFieldID(move);
                long opponentPawnBitboard = Fields.ALL_ORDERED_A1H1[capturedFieldID];
                this.board[capturedFieldID] = capturedPID;
                capturedTypeBitboard |= opponentPawnBitboard;
                byte by = capturedFigureColour;
                this.allByColour[by] = this.allByColour[by] | opponentPawnBitboard;
                this.pieces.add(capturedPID, capturedFieldID);
            } else {
                this.board[toFieldID] = capturedPID;
                capturedTypeBitboard |= toBoard;
                byte by = capturedFigureColour;
                this.allByColour[by] = this.allByColour[by] | toBoard;
                this.pieces.add(capturedPID, toFieldID);
            }
            this.allByColourAndType[capturedFigureColour][capturedFigureType] = capturedTypeBitboard;
        } else if (MoveInt.isCastling(move)) {
            int castlePID = MoveInt.getCastlingRookPID(move);
            int fromCastleFieldID = MoveInt.getCastlingRookFromID(move);
            int toCastleFieldID = MoveInt.getCastlingRookToID(move);
            long fromCastleBoard = Fields.ALL_ORDERED_A1H1[fromCastleFieldID];
            long toCastleBoard = Fields.ALL_ORDERED_A1H1[toCastleFieldID];
            this.pieces.move(castlePID, toCastleFieldID, fromCastleFieldID);
            this.board[fromCastleFieldID] = castlePID;
            this.board[toCastleFieldID] = 0;
            typeBitboard = this.allByColourAndType[figureColour][4];
            typeBitboard |= fromCastleBoard;
            this.allByColourAndType[figureColour][4] = typeBitboard &= toCastleBoard ^ 0xFFFFFFFFFFFFFFFFL;
            int n5 = figureColour;
            this.allByColour[n5] = this.allByColour[n5] | fromCastleBoard;
            int n6 = figureColour;
            this.allByColour[n6] = this.allByColour[n6] & (toCastleBoard ^ 0xFFFFFFFFFFFFFFFFL);
            this.castledByColour[figureColour] = IBoard.CastlingType.NONE;
        }
        this.free = (this.allByColour[0] | this.allByColour[1]) ^ 0xFFFFFFFFFFFFFFFFL;
        this.switchLastMoveColour();
        this.playedMoves[--this.playedMovesCount] = 0;
        if (invalidateCheckKeepers) {
            this.invalidatedCheckKeepers();
        }
        this.invalidatedInChecks();
        if (this.moveListeners.length > 0) {
            for (int i = 0; i < this.moveListeners.length; ++i) {
                this.moveListeners[i].postBackwardMove(MoveInt.getColour(move), move);
            }
        }
        if (this.DEBUG) {
            this.checkConsistency();
        }
        this.hashkey = prevInfo.hashkey;
        this.pawnskey = prevInfo.pawnshash;
        this.lastCaptureOrPawnMoveBefore = prevInfo.lastCaptureOrPawnMoveBefore;
        this.lastCaptureFieldID = prevInfo.lastCaptureFieldID;
    }

    @Override
    public void makeNullMoveForward() {
        BackupInfo curInfo = this.backupInfo[this.playedMovesCount];
        BackupInfo nextInfo = this.backupInfo[this.playedMovesCount + 1];
        nextInfo.w_kingSideAvailable = curInfo.w_kingSideAvailable;
        nextInfo.w_queenSideAvailable = curInfo.w_queenSideAvailable;
        nextInfo.b_kingSideAvailable = curInfo.b_kingSideAvailable;
        nextInfo.b_queenSideAvailable = curInfo.b_queenSideAvailable;
        nextInfo.enpassantPawnBitboard = curInfo.enpassantPawnBitboard;
        this.playedMoves[this.playedMovesCount++] = 0;
        this.switchLastMoveColour();
    }

    @Override
    public final void makeNullMoveBackward() {
        this.playedMoves[--this.playedMovesCount] = 0;
        this.switchLastMoveColour();
    }

    private final long getPawnHashKeyAfterMove(int move) {
        long pawnskey = this.pawnskey;
        int pid = MoveInt.getFigurePID(move);
        int figureColour = MoveInt.getColour(move);
        int figureType = MoveInt.getFigureType(move);
        int fromFieldID = MoveInt.getFromFieldID(move);
        int toFieldID = MoveInt.getToFieldID(move);
        BackupInfo curInfo = this.backupInfo[this.playedMovesCount];
        BackupInfo nextInfo = this.backupInfo[this.playedMovesCount + 1];
        if (figureColour == 0) {
            nextInfo.b_kingSideAvailable = curInfo.b_kingSideAvailable;
            nextInfo.b_queenSideAvailable = curInfo.b_queenSideAvailable;
            if (curInfo.w_kingSideAvailable) {
                switch (pid) {
                    case 6: {
                        pawnskey ^= ConstantStructure.CASTLE_KING_SIDE_BY_COLOUR[figureColour];
                        nextInfo.w_kingSideAvailable = false;
                        break;
                    }
                    case 4: {
                        if (fromFieldID == 7) {
                            pawnskey ^= ConstantStructure.CASTLE_KING_SIDE_BY_COLOUR[figureColour];
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
                        pawnskey ^= ConstantStructure.CASTLE_QUEEN_SIDE_BY_COLOUR[figureColour];
                        nextInfo.w_queenSideAvailable = false;
                        break;
                    }
                    case 4: {
                        if (fromFieldID == 0) {
                            pawnskey ^= ConstantStructure.CASTLE_QUEEN_SIDE_BY_COLOUR[figureColour];
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
                        pawnskey ^= ConstantStructure.CASTLE_KING_SIDE_BY_COLOUR[figureColour];
                        nextInfo.b_kingSideAvailable = false;
                        break;
                    }
                    case 10: {
                        if (fromFieldID == 63) {
                            pawnskey ^= ConstantStructure.CASTLE_KING_SIDE_BY_COLOUR[figureColour];
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
                        pawnskey ^= ConstantStructure.CASTLE_QUEEN_SIDE_BY_COLOUR[figureColour];
                        nextInfo.b_queenSideAvailable = false;
                        break;
                    }
                    case 10: {
                        if (fromFieldID == 56) {
                            pawnskey ^= ConstantStructure.CASTLE_QUEEN_SIDE_BY_COLOUR[figureColour];
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
        long[][] MOVES_KEYS = ConstantStructure.MOVES_KEYS;
        if (figureType == 1 || figureType == 6) {
            pawnskey ^= MOVES_KEYS[pid][fromFieldID];
            pawnskey ^= MOVES_KEYS[pid][toFieldID];
        }
        if (MoveInt.isCapture(move)) {
            int capturedPID = MoveInt.getCapturedFigurePID(move);
            int capturedFigureType = Constants.PIECE_IDENTITY_2_TYPE[capturedPID];
            if (MoveInt.isEnpassant(move)) {
                int capturedFieldID = MoveInt.getEnpassantCapturedFieldID(move);
                if (capturedFigureType == 1) {
                    pawnskey ^= ConstantStructure.MOVES_KEYS[capturedPID][capturedFieldID];
                }
            } else if (capturedFigureType == 1) {
                pawnskey ^= ConstantStructure.MOVES_KEYS[capturedPID][toFieldID];
            }
        }
        if (MoveInt.isPromotion(move)) {
            pawnskey ^= MOVES_KEYS[pid][toFieldID];
        }
        return pawnskey ^= ConstantStructure.WHITE_TO_MOVE;
    }

    @Override
    public final long getHashKeyAfterMove(int move) {
        byte opponentColour;
        long adjoiningFiles;
        long hashkey = this.hashkey;
        int pid = MoveInt.getFigurePID(move);
        int figureColour = MoveInt.getColour(move);
        int figureType = MoveInt.getFigureType(move);
        int dirID = MoveInt.getDir(move);
        int fromFieldID = MoveInt.getFromFieldID(move);
        int toFieldID = MoveInt.getToFieldID(move);
        long toBoard = Fields.ALL_ORDERED_A1H1[toFieldID];
        BackupInfo curInfo = this.backupInfo[this.playedMovesCount];
        BackupInfo nextInfo = this.backupInfo[this.playedMovesCount + 1];
        nextInfo.enpassantPawnBitboard = figureType == 1 && !MoveInt.isCapture(move) && dirID == 1 ? (((adjoiningFiles = Enpassanting.ADJOINING_FILES[figureColour][toFieldID]) & this.allByColourAndType[opponentColour = Figures.OPPONENT_COLOUR[figureColour]][1]) != 0L ? toBoard : 0L) : 0L;
        if (curInfo.enpassantPawnBitboard != nextInfo.enpassantPawnBitboard) {
            hashkey ^= ConstantStructure.HAS_ENPASSANT;
        }
        if (figureColour == 0) {
            nextInfo.b_kingSideAvailable = curInfo.b_kingSideAvailable;
            nextInfo.b_queenSideAvailable = curInfo.b_queenSideAvailable;
            if (curInfo.w_kingSideAvailable) {
                switch (pid) {
                    case 6: {
                        hashkey ^= ConstantStructure.CASTLE_KING_SIDE_BY_COLOUR[figureColour];
                        nextInfo.w_kingSideAvailable = false;
                        break;
                    }
                    case 4: {
                        if (fromFieldID == 7) {
                            hashkey ^= ConstantStructure.CASTLE_KING_SIDE_BY_COLOUR[figureColour];
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
                        hashkey ^= ConstantStructure.CASTLE_QUEEN_SIDE_BY_COLOUR[figureColour];
                        nextInfo.w_queenSideAvailable = false;
                        break;
                    }
                    case 4: {
                        if (fromFieldID == 0) {
                            hashkey ^= ConstantStructure.CASTLE_QUEEN_SIDE_BY_COLOUR[figureColour];
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
                        hashkey ^= ConstantStructure.CASTLE_KING_SIDE_BY_COLOUR[figureColour];
                        nextInfo.b_kingSideAvailable = false;
                        break;
                    }
                    case 10: {
                        if (fromFieldID == 63) {
                            hashkey ^= ConstantStructure.CASTLE_KING_SIDE_BY_COLOUR[figureColour];
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
                        hashkey ^= ConstantStructure.CASTLE_QUEEN_SIDE_BY_COLOUR[figureColour];
                        nextInfo.b_queenSideAvailable = false;
                        break;
                    }
                    case 10: {
                        if (fromFieldID == 56) {
                            hashkey ^= ConstantStructure.CASTLE_QUEEN_SIDE_BY_COLOUR[figureColour];
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
        long[][] MOVES_KEYS = ConstantStructure.MOVES_KEYS;
        hashkey ^= MOVES_KEYS[pid][fromFieldID];
        hashkey ^= MOVES_KEYS[pid][toFieldID];
        if (MoveInt.isCapture(move)) {
            int capturedPID = MoveInt.getCapturedFigurePID(move);
            int capturedFigureType = Constants.PIECE_IDENTITY_2_TYPE[capturedPID];
            if (MoveInt.isEnpassant(move)) {
                int capturedFieldID = MoveInt.getEnpassantCapturedFieldID(move);
                if (capturedFigureType == 1) {
                    hashkey ^= ConstantStructure.MOVES_KEYS[capturedPID][capturedFieldID];
                }
            } else {
                hashkey ^= ConstantStructure.MOVES_KEYS[capturedPID][toFieldID];
            }
        } else if (MoveInt.isCastling(move)) {
            int castlePID = figureColour == 0 ? 4 : 10;
            int fromCastleFieldID = MoveInt.isCastleKingSide(move) ? Castling.getRookFromFieldID_king(figureColour) : Castling.getRookFromFieldID_queen(figureColour);
            int toCastleFieldID = MoveInt.isCastleKingSide(move) ? Castling.getRookToFieldID_king(figureColour) : Castling.getRookToFieldID_queen(figureColour);
            hashkey ^= MOVES_KEYS[castlePID][fromCastleFieldID];
            hashkey ^= MOVES_KEYS[castlePID][toCastleFieldID];
        }
        if (MoveInt.isPromotion(move)) {
            hashkey ^= MOVES_KEYS[pid][toFieldID];
            int promotedFigurePID = MoveInt.getPromotionFigurePID(move);
            hashkey ^= MOVES_KEYS[promotedFigurePID][toFieldID];
        }
        return hashkey ^= ConstantStructure.WHITE_TO_MOVE;
    }

    private final void switchLastMoveColour() {
        this.lastMoveColour = Figures.OPPONENT_COLOUR[this.lastMoveColour];
        this.hashkey ^= ConstantStructure.WHITE_TO_MOVE;
        this.pawnskey ^= ConstantStructure.WHITE_TO_MOVE;
    }

    @Override
    public IPlayerAttacks getPlayerAttacks(int colour) {
        return this.getPlayerAttacks_fast(colour);
    }

    private IPlayerAttacks getPlayerAttacks_fast(int colour) {
        if (colour == 0) {
            return ((FastPlayersAttacks)this.fastPlayerAttacks).getWhiteAttacks();
        }
        return ((FastPlayersAttacks)this.fastPlayerAttacks).getBlackAttacks();
    }

    @Override
    public boolean getAttacksSupport() {
        return this.attacksSupport;
    }

    @Override
    public boolean getFieldsStateSupport() {
        return this.fieldsStateSupport;
    }

    @Override
    public void setAttacksSupport(boolean attacksSupport, boolean fieldsStateSupport) {
        if (this.attacksSupport && !attacksSupport) {
            throw new IllegalStateException();
        }
        if (fieldsStateSupport && !attacksSupport) {
            throw new IllegalStateException();
        }
        this.attacksSupport = attacksSupport;
        this.fieldsStateSupport = fieldsStateSupport;
        if (attacksSupport && this.fastPlayerAttacks == null) {
            this.initAttacksSupport();
        }
    }

    @Override
    public IFieldsAttacks getFieldsAttacks() {
        return this.fieldAttacksCollector;
    }

    @Override
    public SEE getSee() {
        return this.see;
    }

    protected final boolean kingSidePossible(int colour, int opponentColour) {
        long kingSideMask = Castling.MASK_KING_CASTLE_SIDE_BY_COLOUR[colour];
        return (colour == 0 ? this.backupInfo[this.playedMovesCount].w_kingSideAvailable : this.backupInfo[this.playedMovesCount].b_kingSideAvailable) && (kingSideMask & (this.free ^ 0xFFFFFFFFFFFFFFFFL)) == 0L && (this.allByColourAndType[colour][4] & (colour == 0 ? 0x100000000000000L : 1L)) != 0L && this.checkCheckingAtKingSideFields(colour, opponentColour);
    }

    protected final boolean queenSidePossible(int colour, int opponentColour) {
        long queenSideMask = Castling.MASK_QUEEN_CASTLE_SIDE_BY_COLOUR[colour];
        return (colour == 0 ? this.backupInfo[this.playedMovesCount].w_queenSideAvailable : this.backupInfo[this.playedMovesCount].b_queenSideAvailable) && (queenSideMask & (this.free ^ 0xFFFFFFFFFFFFFFFFL)) == 0L && (this.allByColourAndType[colour][4] & (colour == 0 ? Long.MIN_VALUE : 128L)) != 0L && this.checkCheckingAtQueenSideFields(colour, opponentColour);
    }

    private final boolean checkCheckingAtKingSideFields(int colour, int opponentColour) {
        boolean result = true;
        int[] fieldsIDs = Castling.CHECKING_CHECK_FIELD_IDS_ON_KING_SIDE_BY_COLOUR[colour];
        long[] fieldsBitboards = Castling.CHECKING_CHECK_FIELD_BITBOARDS_ON_KING_SIDE_BY_COLOUR[colour];
        for (int i = 0; i < fieldsIDs.length; ++i) {
            long fieldBitboard = fieldsBitboards[i];
            int fieldID = fieldsIDs[i];
            if (!Checking.isFieldAttacked(this, opponentColour, colour, fieldBitboard, fieldID, this.free, true)) continue;
            result = false;
            break;
        }
        return result;
    }

    private final boolean checkCheckingAtQueenSideFields(int colour, int opponentColour) {
        boolean result = true;
        int[] fieldsIDs = Castling.CHECKING_CHECK_FIELD_IDS_ON_QUEEN_SIDE_BY_COLOUR[colour];
        long[] fieldsBitboards = Castling.CHECKING_CHECK_FIELD_BITBOARDS_ON_QUEEN_SIDE_BY_COLOUR[colour];
        for (int i = 0; i < fieldsIDs.length; ++i) {
            long fieldBitboard = fieldsBitboards[i];
            int fieldID = fieldsIDs[i];
            if (!Checking.isFieldAttacked(this, opponentColour, colour, fieldBitboard, fieldID, this.free, true)) continue;
            result = false;
            break;
        }
        return result;
    }

    @Override
    public final boolean isInCheck() {
        return this.isInCheck(this.getColourToMove());
    }

    @Override
    public final boolean isInCheck(int colour) {
        if (this.inCheckCacheInitialized[colour]) {
            return this.inCheckCache[colour];
        }
        boolean inCheck = false;
        inCheck = this.attacksSupport ? this.isInCheckByAttacks(colour) : this.isInCheckInternal(colour, this.free);
        this.inCheckCache[colour] = inCheck;
        this.inCheckCacheInitialized[colour] = true;
        return inCheck;
    }

    private final boolean isInCheckByAttacks(int colour) {
        int kingFieldID = this.getKingFieldID(colour);
        long kingBitboard = Fields.ALL_ORDERED_A1H1[kingFieldID];
        IPlayerAttacks attacks = this.getPlayerAttacks(Figures.OPPONENT_COLOUR[colour]);
        long all = attacks.allAttacks();
        return (all & kingBitboard) != 0L;
    }

    protected final boolean isInCheckInternal(int colour, long _free) {
        int kingFieldID = this.getKingFieldID(colour);
        long kingBitboard = Fields.ALL_ORDERED_A1H1[kingFieldID];
        int lastMove = this.getLastMove();
        boolean kingAttackPossible = lastMove == 0 ? false : MoveInt.isCastling(lastMove);
        boolean inCheck = Checking.isInCheck(this, colour, Figures.OPPONENT_COLOUR[colour], kingBitboard, kingFieldID, _free, kingAttackPossible);
        return inCheck;
    }

    @Override
    public final boolean isCheckMove(int move) {
        int colour = MoveInt.getColour(move);
        byte opponentColour = Figures.OPPONENT_COLOUR[colour];
        int opponentKingFieldID = this.getKingFieldID(opponentColour);
        long opponentKingBitboard = Fields.ALL_ORDERED_A1H1[opponentKingFieldID];
        boolean isCheck = Checking.isCheckMove(this, move, colour, opponentColour, this.free, opponentKingBitboard, opponentKingFieldID);
        return isCheck;
    }

    protected final int getKingFieldID(int colour) {
        int kingFieldID = this.pieces.getPieces(colour == 0 ? 6 : 12).getData()[0];
        return kingFieldID;
    }

    protected final PiecesList getKingIndexSet(int colour) {
        return this.pieces.getPieces(colour == 0 ? 6 : 12);
    }

    public final boolean isDirectCheckMove(int move) {
        int colour = MoveInt.getColour(move);
        byte opponentColour = Figures.OPPONENT_COLOUR[colour];
        int opponentKingFieldID = this.getKingFieldID(opponentColour);
        long opponentKingBitboard = Fields.ALL_ORDERED_A1H1[opponentKingFieldID];
        boolean isCheck = Checking.isDirectCheckMove(move, colour, this.free, opponentKingBitboard, opponentKingFieldID);
        return isCheck;
    }

    private final void fillCheckKeepers_FromOfficerOrQueen(int colour, int myKingFieldID, long myPieces, long opponentPieces, long myOfficersAttacksFromMyKing, long opponentOfficersBoard, int checkingPID) {
        if ((myOfficersAttacksFromMyKing & opponentOfficersBoard) != 0L) {
            long dir = 0L;
            long dir0 = OfficerPlies.ALL_OFFICER_DIR0_MOVES[myKingFieldID];
            long dir1 = OfficerPlies.ALL_OFFICER_DIR1_MOVES[myKingFieldID];
            long dir2 = OfficerPlies.ALL_OFFICER_DIR2_MOVES[myKingFieldID];
            long dir3 = OfficerPlies.ALL_OFFICER_DIR3_MOVES[myKingFieldID];
            boolean hit = false;
            if ((opponentOfficersBoard & dir0) != 0L) {
                hit = true;
                dir |= dir0;
            }
            if ((opponentOfficersBoard & dir1) != 0L) {
                hit = true;
                dir |= dir1;
            }
            if ((opponentOfficersBoard & dir2) != 0L) {
                hit = true;
                dir |= dir2;
            }
            if ((opponentOfficersBoard & dir3) != 0L) {
                hit = true;
                dir |= dir3;
            }
            if (!hit) {
                throw new IllegalStateException();
            }
            boolean hit1 = false;
            IndexNumberMap buffer = this.checkKeepersBuffer[colour];
            PiecesList opponentOfficersIDs = this.pieces.getPieces(checkingPID);
            int size = opponentOfficersIDs.getDataSize();
            int[] ids = opponentOfficersIDs.getData();
            for (int i = 0; i < size; ++i) {
                long myAndPath;
                int opponentOfficerFieldID = ids[i];
                long opponentOfficerBitboard = Fields.ALL_ORDERED_A1H1[opponentOfficerFieldID];
                if ((opponentOfficerBitboard & dir) == 0L) continue;
                hit1 = true;
                long path = OfficerPlies.PATHS[opponentOfficerFieldID][myKingFieldID];
                if ((path & opponentPieces) != 0L || (myAndPath = path & myPieces) == 0L || !Utils.has1BitSet(myAndPath)) continue;
                int fieldID = Board.get67IDByBitboard(myAndPath);
                buffer.add(fieldID, path | opponentOfficerBitboard);
            }
            if (!hit1) {
                throw new IllegalStateException();
            }
        }
    }

    private final void fillCheckKeepers_FromCastleOrQueen(int colour, int myKingFieldID, long myPieces, long opponentPieces, long myCastlesAttacksFromMyKing, long opponentCastlesBoard, int checkingPID) {
        if ((myCastlesAttacksFromMyKing & opponentCastlesBoard) != 0L) {
            long dir = 0L;
            long dir0 = CastlePlies.ALL_CASTLE_DIR0_MOVES[myKingFieldID];
            long dir1 = CastlePlies.ALL_CASTLE_DIR1_MOVES[myKingFieldID];
            long dir2 = CastlePlies.ALL_CASTLE_DIR2_MOVES[myKingFieldID];
            long dir3 = CastlePlies.ALL_CASTLE_DIR3_MOVES[myKingFieldID];
            boolean hit = false;
            boolean enpassOpenDir = false;
            if ((opponentCastlesBoard & dir0) != 0L) {
                hit = true;
                dir |= dir0;
            }
            if ((opponentCastlesBoard & dir1) != 0L) {
                hit = true;
                enpassOpenDir = true;
                dir |= dir1;
            }
            if ((opponentCastlesBoard & dir2) != 0L) {
                hit = true;
                dir |= dir2;
            }
            if ((opponentCastlesBoard & dir3) != 0L) {
                hit = true;
                enpassOpenDir = true;
                dir |= dir3;
            }
            if (!hit) {
                throw new IllegalStateException();
            }
            boolean hit1 = false;
            IndexNumberMap buffer = this.checkKeepersBuffer[colour];
            PiecesList opponentCastlesIDs = this.pieces.getPieces(checkingPID);
            int size = opponentCastlesIDs.getDataSize();
            int[] ids = opponentCastlesIDs.getData();
            for (int i = 0; i < size; ++i) {
                long myAndPath;
                int castleFieldID = ids[i];
                long opponentCastleBitboard = Fields.ALL_ORDERED_A1H1[castleFieldID];
                if ((opponentCastleBitboard & dir) == 0L) continue;
                hit1 = true;
                long path = CastlePlies.PATHS[castleFieldID][myKingFieldID];
                if ((path & opponentPieces) != 0L || (myAndPath = path & myPieces) == 0L || !Utils.has1BitSet(myAndPath)) continue;
                int fieldID = Board.get67IDByBitboard(myAndPath);
                buffer.add(fieldID, path | opponentCastleBitboard);
            }
            if (!hit1) {
                throw new IllegalStateException("enpassOpenDir=" + enpassOpenDir + ", enpasInfo[playedMovesCount]" + Bits.toBinaryString(this.backupInfo[this.playedMovesCount].enpassantPawnBitboard) + ", bitboard=" + String.valueOf(this));
            }
        }
    }

    protected final void invalidatedCheckKeepers() {
        this.checkKeepersInitialized[0] = false;
        this.checkKeepersInitialized[1] = false;
        this.checkKeepersBuffer[0].clear();
        this.checkKeepersBuffer[1].clear();
        this.inCheckCacheInitialized[0] = false;
        this.inCheckCacheInitialized[1] = false;
    }

    protected final void invalidatedInChecks() {
        this.inCheckCacheInitialized[0] = false;
        this.inCheckCacheInitialized[1] = false;
    }

    protected final void fillCheckKeepers(int colour) {
        if (this.checkKeepersInitialized[colour]) {
            return;
        }
        int myKingFieldID = this.getKingFieldID(colour);
        byte opponentColour = Figures.OPPONENT_COLOUR[colour];
        long myOfficersAttacksFromMyKing = OfficerPlies.ALL_OFFICER_MOVES[myKingFieldID];
        this.fillCheckKeepers_FromOfficerOrQueen(colour, myKingFieldID, this.allByColour[colour], this.allByColour[opponentColour], myOfficersAttacksFromMyKing, this.allByColourAndType[opponentColour][3], Figures.getPidByColourAndType(opponentColour, 3));
        this.fillCheckKeepers_FromOfficerOrQueen(colour, myKingFieldID, this.allByColour[colour], this.allByColour[opponentColour], myOfficersAttacksFromMyKing, this.allByColourAndType[opponentColour][5], Figures.getPidByColourAndType(opponentColour, 5));
        long myCastlesAttacksFromMyKing = CastlePlies.ALL_CASTLE_MOVES[myKingFieldID];
        this.fillCheckKeepers_FromCastleOrQueen(colour, myKingFieldID, this.allByColour[colour], this.allByColour[opponentColour], myCastlesAttacksFromMyKing, this.allByColourAndType[opponentColour][4], Figures.getPidByColourAndType(opponentColour, 4));
        this.fillCheckKeepers_FromCastleOrQueen(colour, myKingFieldID, this.allByColour[colour], this.allByColour[opponentColour], myCastlesAttacksFromMyKing, this.allByColourAndType[opponentColour][5], Figures.getPidByColourAndType(opponentColour, 5));
        this.checkKeepersInitialized[colour] = true;
    }

    private final int genAllMoves(IInternalMoveList list, boolean checkKeepersAware) {
        return this.genAllMoves(0L, false, this.getColourToMove(), list, 256);
    }

    @Override
    public final int genAllMoves(IInternalMoveList list) {
        return this.genAllMoves(0L, true, this.getColourToMove(), list, 256);
    }

    private final int genAllMoves(IInternalMoveList list, long excludedToFieldsBoard) {
        return this.genAllMoves(excludedToFieldsBoard, true, this.getColourToMove(), list, 256);
    }

    protected final int genAllMoves(long excludedToFieldsBoard, boolean checkKeepersAware, int colour, IInternalMoveList list, int maxCount) {
        int count = 0;
        if (this.getKingIndexSet(colour).getDataSize() == 0) {
            return 0;
        }
        this.fillCheckKeepers(colour);
        byte opponentColour = Figures.OPPONENT_COLOUR[colour];
        if ((count += this.genAllMoves_FiguresWithSameType(excludedToFieldsBoard, true, checkKeepersAware, colour, opponentColour, 1, list, maxCount)) >= maxCount) {
            return count;
        }
        if ((count += this.genAllMoves_FiguresWithSameType(excludedToFieldsBoard, true, checkKeepersAware, colour, opponentColour, 2, list, maxCount)) >= maxCount) {
            return count;
        }
        if ((count += this.genAllMoves_FiguresWithSameType(excludedToFieldsBoard, true, checkKeepersAware, colour, opponentColour, 3, list, maxCount)) >= maxCount) {
            return count;
        }
        if ((count += this.genAllMoves_FiguresWithSameType(excludedToFieldsBoard, true, checkKeepersAware, colour, opponentColour, 4, list, maxCount)) >= maxCount) {
            return count;
        }
        if ((count += this.genAllMoves_FiguresWithSameType(excludedToFieldsBoard, true, checkKeepersAware, colour, opponentColour, 5, list, maxCount)) >= maxCount) {
            return count;
        }
        if ((count += this.genAllMoves_FiguresWithSameType(excludedToFieldsBoard, true, checkKeepersAware, colour, opponentColour, 6, list, maxCount)) >= maxCount) {
            return count;
        }
        return count;
    }

    private final int genAllMoves_FiguresWithSameType(long excludedToFieldsBoard_init, boolean interuptAtFirstExclusionHit, boolean checkKeepersAware, int colour, int opponentColour, int type, IInternalMoveList list, int maxCount) {
        int count = 0;
        int pid = Figures.getPidByColourAndType(colour, type);
        PiecesList fields = this.pieces.getPieces(pid);
        int size = fields.getDataSize();
        int[] data = fields.getData();
        for (int i = 0; i < size; ++i) {
            int fieldID = data[i];
            if ((count += this.genAllMoves_ByFigureID(fieldID, pid, excludedToFieldsBoard_init, interuptAtFirstExclusionHit, checkKeepersAware, colour, opponentColour, type, list, maxCount)) < maxCount) continue;
            return count;
        }
        return count;
    }

    @Override
    public final int genAllMoves_ByFigureID(int fieldID, long excludedToFields, IInternalMoveList list) {
        int pid = this.board[fieldID];
        if (pid == 0) {
            throw new IllegalStateException();
        }
        int colour = Constants.getColourByPieceIdentity(pid);
        int type = Constants.PIECE_IDENTITY_2_TYPE[pid];
        this.fillCheckKeepers(colour);
        return this.genAllMoves_ByFigureID(fieldID, pid, excludedToFields, true, true, colour, Figures.OPPONENT_COLOUR[colour], type, list, 100);
    }

    private final int genAllMoves_ByFigureID(int fieldID, int pid, long excludedToFieldsBoard_init, boolean interuptAtFirstExclusionHit, boolean checkKeepersAware, int colour, int opponentColour, int type, IInternalMoveList list, int maxCount) {
        int count = 0;
        long excludedToFieldsBoard = excludedToFieldsBoard_init;
        if (checkKeepersAware && this.checkKeepersBuffer[colour].contains(fieldID)) {
            excludedToFieldsBoard |= this.checkKeepersBuffer[colour].getValue(fieldID) ^ 0xFFFFFFFFFFFFFFFFL;
        }
        long fieldBitboard = Fields.ALL_ORDERED_A1H1[fieldID];
        switch (type) {
            case 2: {
                long allMoves = KnightPlies.ALL_KNIGHT_MOVES[fieldID];
                if ((allMoves & (excludedToFieldsBoard ^ 0xFFFFFFFFFFFFFFFFL)) == 0L || (count += KnightMovesGen.genAllMoves(excludedToFieldsBoard, pid, fieldID, this.allByColour[colour], this.allByColour[opponentColour], this.board, list, maxCount)) < maxCount) break;
                return count;
            }
            case 6: {
                long allMoves = KingPlies.ALL_KING_MOVES[fieldID];
                if ((allMoves & (excludedToFieldsBoard ^ 0xFFFFFFFFFFFFFFFFL)) == 0L) break;
                int opKingID = this.getKingFieldID(opponentColour);
                if ((count += KingMovesGen.genAllMoves(checkKeepersAware, this, excludedToFieldsBoard, pid, colour, opponentColour, fieldBitboard, fieldID, this.free, this.allByColour[colour], this.allByColour[opponentColour], this.board, this.kingSidePossible(colour, opponentColour), this.queenSidePossible(colour, opponentColour), Fields.ALL_ORDERED_A1H1[opKingID], opKingID, list, maxCount)) < maxCount) break;
                return count;
            }
            case 1: {
                long allMoves;
                BackupInfo curEnpassInfo = this.backupInfo[this.playedMovesCount];
                if (!(colour == 0 ? (((allMoves = WhitePawnPlies.ALL_WHITE_PAWN_MOVES[fieldID]) & (excludedToFieldsBoard ^ 0xFFFFFFFFFFFFFFFFL)) != 0L || curEnpassInfo.enpassantPawnBitboard != 0L) && (count += WhitePawnMovesGen.genAllMoves(this, excludedToFieldsBoard, interuptAtFirstExclusionHit, pid, fieldID, this.free, this.allByColour[opponentColour], this.board, curEnpassInfo.enpassantPawnBitboard != 0L, curEnpassInfo.enpassantPawnBitboard, list, maxCount)) >= maxCount : (((allMoves = BlackPawnPlies.ALL_BLACK_PAWN_MOVES[fieldID]) & (excludedToFieldsBoard ^ 0xFFFFFFFFFFFFFFFFL)) != 0L || curEnpassInfo.enpassantPawnBitboard != 0L) && (count += BlackPawnMovesGen.genAllMoves(this, excludedToFieldsBoard, interuptAtFirstExclusionHit, pid, fieldID, this.free, this.board, curEnpassInfo.enpassantPawnBitboard != 0L, curEnpassInfo.enpassantPawnBitboard, list, maxCount)) >= maxCount)) break;
                return count;
            }
            case 3: {
                long allMoves = OfficerPlies.ALL_OFFICER_MOVES[fieldID];
                if ((allMoves & (excludedToFieldsBoard ^ 0xFFFFFFFFFFFFFFFFL)) == 0L || (count += OfficerMovesGen.genAllMoves(excludedToFieldsBoard, interuptAtFirstExclusionHit, pid, fieldID, this.free, this.allByColour[opponentColour], this.board, list, maxCount)) < maxCount) break;
                return count;
            }
            case 4: {
                long allMoves = CastlePlies.ALL_CASTLE_MOVES[fieldID];
                if ((allMoves & (excludedToFieldsBoard ^ 0xFFFFFFFFFFFFFFFFL)) == 0L || (count += CastleMovesGen.genAllMoves(excludedToFieldsBoard, interuptAtFirstExclusionHit, pid, fieldID, this.free, this.allByColour[opponentColour], this.board, list, maxCount)) < maxCount) break;
                return count;
            }
            case 5: {
                if ((CastlePlies.ALL_CASTLE_MOVES[fieldID] & (excludedToFieldsBoard ^ 0xFFFFFFFFFFFFFFFFL)) == 0L && (OfficerPlies.ALL_OFFICER_MOVES[fieldID] & (excludedToFieldsBoard ^ 0xFFFFFFFFFFFFFFFFL)) == 0L || (count += QueenMovesGen.genAllMoves(excludedToFieldsBoard, interuptAtFirstExclusionHit, pid, fieldID, this.free, this.allByColour[opponentColour], this.board, list, maxCount)) < maxCount) break;
                return count;
            }
        }
        return count;
    }

    @Override
    public final int genCapturePromotionMoves(IInternalMoveList list) {
        return this.genCapturePromotionMoves(this.getColourToMove(), list, 256);
    }

    private final int genCapturePromotionMoves(int colour, IInternalMoveList list, int maxCount) {
        int count = 0;
        if (this.getKingIndexSet(colour).getDataSize() == 0) {
            return 0;
        }
        this.fillCheckKeepers(colour);
        byte opponentColour = Figures.OPPONENT_COLOUR[colour];
        if (this.getFiguresBitboardByColourAndType(colour, 1) != 0L && (count += this.genCaptureMoves_FiguresWithSameType(colour, opponentColour, 1, list, maxCount)) >= maxCount) {
            return count;
        }
        if (this.getFiguresBitboardByColourAndType(colour, 2) != 0L && (count += this.genCaptureMoves_FiguresWithSameType(colour, opponentColour, 2, list, maxCount)) >= maxCount) {
            return count;
        }
        if (this.getFiguresBitboardByColourAndType(colour, 3) != 0L && (count += this.genCaptureMoves_FiguresWithSameType(colour, opponentColour, 3, list, maxCount)) >= maxCount) {
            return count;
        }
        if (this.getFiguresBitboardByColourAndType(colour, 4) != 0L && (count += this.genCaptureMoves_FiguresWithSameType(colour, opponentColour, 4, list, maxCount)) >= maxCount) {
            return count;
        }
        if (this.getFiguresBitboardByColourAndType(colour, 5) != 0L && (count += this.genCaptureMoves_FiguresWithSameType(colour, opponentColour, 5, list, maxCount)) >= maxCount) {
            return count;
        }
        if ((count += this.genCaptureMoves_FiguresWithSameType(colour, opponentColour, 6, list, maxCount)) >= maxCount) {
            return count;
        }
        return count;
    }

    private final int genCaptureMoves_FiguresWithSameType(int colour, int opponentColour, int type, IInternalMoveList list, int maxCount) {
        if (type == 1) {
            BackupInfo curEnpassInfo = this.backupInfo[this.playedMovesCount];
            if (curEnpassInfo.enpassantPawnBitboard == 0L && !this.hasPawnsCapturePromotion(colour, this.allByColourAndType[colour][1], this.allByColour[opponentColour])) {
                return 0;
            }
        }
        int count = 0;
        int pid = Figures.getPidByColourAndType(colour, type);
        PiecesList fields = this.pieces.getPieces(pid);
        int size = fields.getDataSize();
        int[] data = fields.getData();
        block8: for (int i = 0; i < size; ++i) {
            int fieldID = data[i];
            long excludedToFieldsBoard = 0L;
            if (this.checkKeepersBuffer[colour].contains(fieldID)) {
                excludedToFieldsBoard = this.checkKeepersBuffer[colour].getValue(fieldID) ^ 0xFFFFFFFFFFFFFFFFL;
            }
            long bitboard = Fields.ALL_ORDERED_A1H1[fieldID];
            switch (type) {
                case 2: {
                    long opponentPieces = this.allByColour[opponentColour];
                    long attacks = KnightPlies.ALL_KNIGHT_MOVES[fieldID];
                    if ((opponentPieces & attacks) == 0L || (count += KnightMovesGen.genCaptureMoves(excludedToFieldsBoard, pid, fieldID, this.allByColour[colour], this.allByColour[opponentColour], this.board, list, maxCount)) < maxCount) continue block8;
                    return count;
                }
                case 6: {
                    int opKingID;
                    long opponentPieces = this.allByColour[opponentColour];
                    long attacks = KingPlies.ALL_KING_MOVES[fieldID];
                    if ((opponentPieces & attacks) == 0L || (count += KingMovesGen.genCaptureMoves(this, excludedToFieldsBoard, pid, colour, opponentColour, bitboard, fieldID, this.free, this.allByColour[colour], this.allByColour[opponentColour], this.board, Fields.ALL_ORDERED_A1H1[opKingID = this.getKingFieldID(opponentColour)], opKingID, list, maxCount)) < maxCount) continue block8;
                    return count;
                }
                case 1: {
                    BackupInfo curEnpassInfo = this.backupInfo[this.playedMovesCount];
                    if (!(colour == 0 ? (curEnpassInfo.enpassantPawnBitboard != 0L || this.hasPawnsCapturePromotion(colour, bitboard, this.allByColour[opponentColour])) && (count += WhitePawnMovesGen.genCapturePromotionEnpassantMoves(this, excludedToFieldsBoard, true, pid, fieldID, this.free, this.allByColour[opponentColour], this.board, curEnpassInfo.enpassantPawnBitboard != 0L, curEnpassInfo.enpassantPawnBitboard, list, maxCount)) >= maxCount : (curEnpassInfo.enpassantPawnBitboard != 0L || this.hasPawnsCapturePromotion(colour, bitboard, this.allByColour[opponentColour])) && (count += BlackPawnMovesGen.genCapturePromotionEnpassantMoves(this, excludedToFieldsBoard, true, pid, fieldID, this.free, this.allByColour[opponentColour], this.board, curEnpassInfo.enpassantPawnBitboard != 0L, curEnpassInfo.enpassantPawnBitboard, list, maxCount)) >= maxCount)) continue block8;
                    return count;
                }
                case 3: {
                    long opponentPieces = this.allByColour[opponentColour];
                    long attacks = OfficerPlies.ALL_OFFICER_MOVES[fieldID];
                    if ((opponentPieces & attacks) == 0L || (count += OfficerMovesGen.genCaptureMoves(excludedToFieldsBoard, true, pid, fieldID, this.free, opponentPieces, this.board, list, maxCount)) < maxCount) continue block8;
                    return count;
                }
                case 4: {
                    long opponentPieces = this.allByColour[opponentColour];
                    long attacks = CastlePlies.ALL_CASTLE_MOVES[fieldID];
                    if ((opponentPieces & attacks) == 0L || (count += CastleMovesGen.genCaptureMoves(this, excludedToFieldsBoard, true, pid, fieldID, this.free, opponentPieces, this.board, list, maxCount)) < maxCount) continue block8;
                    return count;
                }
                case 5: {
                    long opponentPieces = this.allByColour[opponentColour];
                    long attacks = CastlePlies.ALL_CASTLE_MOVES[fieldID] | OfficerPlies.ALL_OFFICER_MOVES[fieldID];
                    if ((opponentPieces & attacks) == 0L || (count += QueenMovesGen.genCaptureMoves(this, excludedToFieldsBoard, pid, fieldID, this.free, opponentPieces, this.board, list, maxCount)) < maxCount) continue block8;
                    return count;
                }
            }
        }
        return count;
    }

    private final boolean hasPawnsCapturePromotion(int colour, long pawnsBoard, long opponentBoard) {
        if (colour == 0) {
            if ((pawnsBoard & 0xFF00L) != 0L) {
                return true;
            }
            long nonactivePawns = pawnsBoard & 0x8080808080808080L;
            long activePawns = pawnsBoard & (nonactivePawns ^ 0xFFFFFFFFFFFFFFFFL);
            long attacks1 = activePawns >> 7;
            nonactivePawns = pawnsBoard & 0x101010101010101L;
            long attacks2 = (activePawns = pawnsBoard & (nonactivePawns ^ 0xFFFFFFFFFFFFFFFFL)) >> 9;
            long or = attacks1 | attacks2;
            if ((or & opponentBoard) != 0L) {
                return true;
            }
        } else {
            if ((pawnsBoard & 0xFF000000000000L) != 0L) {
                return true;
            }
            long nonactivePawns = pawnsBoard & 0x8080808080808080L;
            long activePawns = pawnsBoard & (nonactivePawns ^ 0xFFFFFFFFFFFFFFFFL);
            long attacks1 = activePawns << 9;
            nonactivePawns = pawnsBoard & 0x101010101010101L;
            long attacks2 = (activePawns = pawnsBoard & (nonactivePawns ^ 0xFFFFFFFFFFFFFFFFL)) << 7;
            long or = attacks1 | attacks2;
            if ((or & opponentBoard) != 0L) {
                return true;
            }
        }
        return false;
    }

    @Override
    public final int genNonCaptureNonPromotionMoves(IInternalMoveList list) {
        return this.genNonCaptureNonPromotionMoves(this.getColourToMove(), list, 256);
    }

    private final int genNonCaptureNonPromotionMoves(int colour, IInternalMoveList list, int maxCount) {
        int count = 0;
        if (this.getKingIndexSet(colour).getDataSize() == 0) {
            return 0;
        }
        this.fillCheckKeepers(colour);
        byte opponentColour = Figures.OPPONENT_COLOUR[colour];
        if (this.getFiguresBitboardByColourAndType(colour, 1) != 0L && (count += this.genNonCaptureMoves_FiguresWithSameType(colour, opponentColour, 1, list, maxCount)) >= maxCount) {
            return count;
        }
        if (this.getFiguresBitboardByColourAndType(colour, 2) != 0L && (count += this.genNonCaptureMoves_FiguresWithSameType(colour, opponentColour, 2, list, maxCount)) >= maxCount) {
            return count;
        }
        if (this.getFiguresBitboardByColourAndType(colour, 3) != 0L && (count += this.genNonCaptureMoves_FiguresWithSameType(colour, opponentColour, 3, list, maxCount)) >= maxCount) {
            return count;
        }
        if (this.getFiguresBitboardByColourAndType(colour, 4) != 0L && (count += this.genNonCaptureMoves_FiguresWithSameType(colour, opponentColour, 4, list, maxCount)) >= maxCount) {
            return count;
        }
        if (this.getFiguresBitboardByColourAndType(colour, 5) != 0L && (count += this.genNonCaptureMoves_FiguresWithSameType(colour, opponentColour, 5, list, maxCount)) >= maxCount) {
            return count;
        }
        if ((count += this.genNonCaptureMoves_FiguresWithSameType(colour, opponentColour, 6, list, maxCount)) >= maxCount) {
            return count;
        }
        return count;
    }

    private final int genNonCaptureMoves_FiguresWithSameType(int colour, int opponentColour, int type, IInternalMoveList list, int maxCount) {
        int count = 0;
        int pid = Figures.getPidByColourAndType(colour, type);
        PiecesList fields = this.pieces.getPieces(pid);
        int size = fields.getDataSize();
        int[] data = fields.getData();
        block8: for (int i = 0; i < size; ++i) {
            int fieldID = data[i];
            long excludedToFieldsBoard = 0L;
            if (this.checkKeepersBuffer[colour].contains(fieldID)) {
                excludedToFieldsBoard = this.checkKeepersBuffer[colour].getValue(fieldID) ^ 0xFFFFFFFFFFFFFFFFL;
            }
            long bitboard = Fields.ALL_ORDERED_A1H1[fieldID];
            switch (type) {
                case 2: {
                    if ((count += KnightMovesGen.genNonCaptureMoves(excludedToFieldsBoard, pid, fieldID, this.allByColour[colour], this.allByColour[opponentColour], list, maxCount)) < maxCount) continue block8;
                    return count;
                }
                case 6: {
                    int opKingID = this.getKingFieldID(opponentColour);
                    if ((count += KingMovesGen.genNonCaptureMoves(this, excludedToFieldsBoard, pid, colour, opponentColour, bitboard, fieldID, this.free, this.allByColour[colour], this.allByColour[opponentColour], this.kingSidePossible(colour, opponentColour), this.queenSidePossible(colour, opponentColour), Fields.ALL_ORDERED_A1H1[opKingID], opKingID, list, maxCount)) < maxCount) continue block8;
                    return count;
                }
                case 1: {
                    if (!(colour == 0 ? (count += WhitePawnMovesGen.genNonCaptureMoves(excludedToFieldsBoard, true, pid, fieldID, this.free, list, maxCount)) >= maxCount : (count += BlackPawnMovesGen.genNonCaptureMoves(excludedToFieldsBoard, true, pid, fieldID, this.free, list, maxCount)) >= maxCount)) continue block8;
                    return count;
                }
                case 3: {
                    if ((count += OfficerMovesGen.genNonCaptureMoves(excludedToFieldsBoard, true, pid, fieldID, this.free, this.allByColour[opponentColour], list, maxCount)) < maxCount) continue block8;
                    return count;
                }
                case 4: {
                    if ((count += CastleMovesGen.genNonCaptureMoves(excludedToFieldsBoard, true, pid, fieldID, this.free, this.allByColour[opponentColour], list, maxCount)) < maxCount) continue block8;
                    return count;
                }
                case 5: {
                    if ((count += QueenMovesGen.genNonCaptureMoves(excludedToFieldsBoard, pid, fieldID, this.free, this.allByColour[opponentColour], list, maxCount)) < maxCount) continue block8;
                    return count;
                }
            }
        }
        return count;
    }

    private final int genPromotions(int colour, IInternalMoveList list, int maxCount) {
        int count = 0;
        if (this.getKingIndexSet(colour).getDataSize() == 0) {
            return 0;
        }
        this.fillCheckKeepers(colour);
        byte opponentColour = Figures.OPPONENT_COLOUR[colour];
        if (colour == 0 ? (this.allByColourAndType[0][1] & 0xFF00L) == 0L : (this.allByColourAndType[1][1] & 0xFF000000000000L) == 0L) {
            return 0;
        }
        int pid = Figures.getPidByColourAndType(colour, 1);
        PiecesList fields = this.pieces.getPieces(pid);
        int size = fields.getDataSize();
        int[] data = fields.getData();
        for (int i = 0; i < size; ++i) {
            long excludedToFieldsBoard;
            int fieldID = data[i];
            long figureBitboard = Fields.ALL_ORDERED_A1H1[fieldID];
            if (colour == 0) {
                if ((figureBitboard & 0xFF00L) == 0L) continue;
                excludedToFieldsBoard = 0L;
                if (this.checkKeepersBuffer[colour].contains(fieldID)) {
                    excludedToFieldsBoard = this.checkKeepersBuffer[colour].getValue(fieldID) ^ 0xFFFFFFFFFFFFFFFFL;
                }
                if ((count += WhitePawnMovesGen.genPromotionMoves(excludedToFieldsBoard, true, figureBitboard, fieldID, this.free, this.allByColour[opponentColour], this.board, list, maxCount)) < maxCount) continue;
                return count;
            }
            if ((figureBitboard & 0xFF000000000000L) == 0L) continue;
            excludedToFieldsBoard = 0L;
            if (this.checkKeepersBuffer[colour].contains(fieldID)) {
                excludedToFieldsBoard = this.checkKeepersBuffer[colour].getValue(fieldID) ^ 0xFFFFFFFFFFFFFFFFL;
            }
            if ((count += BlackPawnMovesGen.genPromotionMoves(excludedToFieldsBoard, true, figureBitboard, fieldID, this.free, this.allByColour[opponentColour], this.board, list, maxCount)) < maxCount) continue;
            return count;
        }
        return count;
    }

    private final int gen2MovesPromotions(int colour, IInternalMoveList list, int maxCount) {
        int count = 0;
        if (this.getKingIndexSet(colour).getDataSize() == 0) {
            return 0;
        }
        this.fillCheckKeepers(colour);
        byte opponentColour = Figures.OPPONENT_COLOUR[colour];
        if (colour == 0 ? (this.allByColourAndType[0][1] & 0xFF0000L) == 0L : (this.allByColourAndType[1][1] & 0xFF0000000000L) == 0L) {
            return 0;
        }
        int pid = Figures.getPidByColourAndType(colour, 1);
        PiecesList fields = this.pieces.getPieces(pid);
        int size = fields.getDataSize();
        int[] data = fields.getData();
        for (int i = 0; i < size; ++i) {
            long excludedToFieldsBoard;
            int fieldID = data[i];
            long figureBitboard = Fields.ALL_ORDERED_A1H1[fieldID];
            BackupInfo curEnpassInfo = this.backupInfo[this.playedMovesCount];
            if (colour == 0) {
                if ((figureBitboard & 0xFF0000L) == 0L) continue;
                excludedToFieldsBoard = 0L;
                if (this.checkKeepersBuffer[colour].contains(fieldID)) {
                    excludedToFieldsBoard = this.checkKeepersBuffer[colour].getValue(fieldID) ^ 0xFFFFFFFFFFFFFFFFL;
                }
                if ((count += WhitePawnMovesGen.genAllMoves(this, excludedToFieldsBoard, true, pid, fieldID, this.free, this.allByColour[opponentColour], this.board, curEnpassInfo.enpassantPawnBitboard != 0L, curEnpassInfo.enpassantPawnBitboard, list, maxCount)) < maxCount) continue;
                return count;
            }
            if ((figureBitboard & 0xFF0000000000L) == 0L) continue;
            excludedToFieldsBoard = 0L;
            if (this.checkKeepersBuffer[colour].contains(fieldID)) {
                excludedToFieldsBoard = this.checkKeepersBuffer[colour].getValue(fieldID) ^ 0xFFFFFFFFFFFFFFFFL;
            }
            if ((count += BlackPawnMovesGen.genAllMoves(this, excludedToFieldsBoard, false, pid, fieldID, this.free, this.board, curEnpassInfo.enpassantPawnBitboard != 0L, curEnpassInfo.enpassantPawnBitboard, list, maxCount)) < maxCount) continue;
            return count;
        }
        return count;
    }

    private final int genDirectCheckMoves(int colour, IInternalMoveList list, int maxCount) {
        int count = 0;
        if (this.getKingIndexSet(colour).getDataSize() == 0) {
            return 0;
        }
        this.fillCheckKeepers(colour);
        byte opponentColour = Figures.OPPONENT_COLOUR[colour];
        int opponentKingFieldID = this.getKingFieldID(opponentColour);
        long opponentKingBitboard = Fields.ALL_ORDERED_A1H1[opponentKingFieldID];
        if ((count += this.genCheckMoves_FiguresWithSameType(colour, opponentColour, 1, opponentKingFieldID, opponentKingBitboard, list, maxCount)) >= maxCount) {
            return count;
        }
        if ((count += this.genCheckMoves_FiguresWithSameType(colour, opponentColour, 2, opponentKingFieldID, opponentKingBitboard, list, maxCount)) >= maxCount) {
            return count;
        }
        if ((count += this.genCheckMoves_FiguresWithSameType(colour, opponentColour, 3, opponentKingFieldID, opponentKingBitboard, list, maxCount)) >= maxCount) {
            return count;
        }
        if ((count += this.genCheckMoves_FiguresWithSameType(colour, opponentColour, 4, opponentKingFieldID, opponentKingBitboard, list, maxCount)) >= maxCount) {
            return count;
        }
        if ((count += this.genCheckMoves_FiguresWithSameType(colour, opponentColour, 5, opponentKingFieldID, opponentKingBitboard, list, maxCount)) >= maxCount) {
            return count;
        }
        if ((count += this.genCheckMoves_FiguresWithSameType(colour, opponentColour, 6, opponentKingFieldID, opponentKingBitboard, list, maxCount)) >= maxCount) {
            return count;
        }
        return count;
    }

    private final int genCheckMoves_FiguresWithSameType(int colour, int opponentColour, int type, int opponentKingFieldID, long opponentKingBitboard, IInternalMoveList list, int maxCount) {
        int count = 0;
        boolean promotionsPossible = false;
        boolean enpassantPossible = false;
        if (type == 1) {
            BackupInfo curEnpassInfo = this.backupInfo[this.playedMovesCount];
            boolean bl = enpassantPossible = curEnpassInfo.enpassantPawnBitboard != 0L;
            promotionsPossible = colour == 0 ? (this.allByColourAndType[0][1] & 0xFF00L) != 0L : (this.allByColourAndType[1][1] & 0xFF000000000000L) != 0L;
        }
        int pid = Figures.getPidByColourAndType(colour, type);
        PiecesList fields = this.pieces.getPieces(pid);
        int size = fields.getDataSize();
        int[] data = fields.getData();
        int enpassCount = 0;
        block8: for (int i = 0; i < size; ++i) {
            int fieldID = data[i];
            long excludedToFieldsBoard = 0L;
            if (this.checkKeepersBuffer[colour].contains(fieldID)) {
                excludedToFieldsBoard = this.checkKeepersBuffer[colour].getValue(fieldID) ^ 0xFFFFFFFFFFFFFFFFL;
            }
            long figureBitboard = Fields.ALL_ORDERED_A1H1[fieldID];
            switch (type) {
                case 2: {
                    if ((KnightChecks.FIELDS_ATTACK_2[opponentKingFieldID] & figureBitboard) == 0L || (count += KnightMovesGen.genCheckMoves(excludedToFieldsBoard, pid, fieldID, opponentKingFieldID, this.allByColour[colour], this.allByColour[opponentColour], this.board, list, maxCount)) < maxCount) continue block8;
                    return count;
                }
                case 6: {
                    IInternalMoveList toUse = list;
                    if (toUse == null) {
                        this.movesBuffer.reserved_clear();
                        toUse = this.movesBuffer;
                    }
                    int sizeBefore = toUse.reserved_getCurrentSize();
                    int fromIndex_before = count;
                    int castleSideCount = (count += KingMovesGen.genCastleSides(colour, this.kingSidePossible(colour, opponentColour), this.queenSidePossible(colour, opponentColour), toUse, maxCount)) - fromIndex_before;
                    if (castleSideCount > 2) {
                        throw new IllegalStateException();
                    }
                    if (castleSideCount > 0) {
                        if (castleSideCount == 1) {
                            if (!this.isDirectCheckMove(toUse.reserved_getMovesBuffer()[sizeBefore])) {
                                --count;
                                toUse.reserved_removeLast();
                            }
                        } else if (castleSideCount == 2) {
                            boolean first = this.isDirectCheckMove(toUse.reserved_getMovesBuffer()[sizeBefore]);
                            boolean second = this.isDirectCheckMove(toUse.reserved_getMovesBuffer()[sizeBefore + 1]);
                            if (first) {
                                if (second) {
                                    throw new IllegalStateException("Both castle sides are direct check moves");
                                }
                                --count;
                                toUse.reserved_removeLast();
                            } else if (second) {
                                int secondMove;
                                toUse.reserved_getMovesBuffer()[sizeBefore] = secondMove = toUse.reserved_getMovesBuffer()[sizeBefore + 1];
                                --count;
                                toUse.reserved_removeLast();
                            } else {
                                count -= 2;
                                toUse.reserved_removeLast();
                                toUse.reserved_removeLast();
                            }
                        }
                    }
                    if (count < maxCount) continue block8;
                    return count;
                }
                case 1: {
                    int prom;
                    int cur;
                    int[] moves;
                    int promCount;
                    int curEnpassCount;
                    long pawns;
                    int fromIndex_before;
                    int sizeBefore;
                    IInternalMoveList toUse;
                    BackupInfo curEnpassInfo = this.backupInfo[this.playedMovesCount];
                    if (colour == 0) {
                        pawns = this.allByColourAndType[0][1];
                        long possibleAttacks = WhitePawnsChecks.FIELDS_ATTACKERS_2[opponentKingFieldID];
                        if ((pawns & possibleAttacks) != 0L) {
                            if ((count += WhitePawnMovesGen.genCheckMoves(excludedToFieldsBoard, pid, fieldID, opponentKingFieldID, this.free, this.allByColour[opponentColour], this.board, list, maxCount)) >= maxCount) {
                                return count;
                            }
                            if (enpassantPossible) {
                                toUse = list;
                                if (toUse == null) {
                                    this.movesBuffer.reserved_clear();
                                    toUse = this.movesBuffer;
                                }
                                sizeBefore = toUse.reserved_getCurrentSize();
                                fromIndex_before = count;
                                curEnpassCount = (count += WhitePawnMovesGen.genEnpassantMove(this, excludedToFieldsBoard, pid, fieldID, this.allByColour[opponentColour], this.board, curEnpassInfo.enpassantPawnBitboard != 0L, curEnpassInfo.enpassantPawnBitboard, toUse, maxCount)) - fromIndex_before;
                                if (curEnpassCount > 1) {
                                    throw new IllegalStateException();
                                }
                                if (curEnpassCount == 1) {
                                    ++enpassCount;
                                    if (!this.isDirectCheckMove(toUse.reserved_getMovesBuffer()[sizeBefore])) {
                                        --count;
                                        toUse.reserved_removeLast();
                                    }
                                }
                                if (enpassCount == 2) {
                                    enpassantPossible = false;
                                }
                                if (count >= maxCount) {
                                    return count;
                                }
                            }
                        }
                        if (!promotionsPossible) continue block8;
                        toUse = list;
                        if (toUse == null) {
                            this.movesBuffer.reserved_clear();
                            toUse = this.movesBuffer;
                        }
                        sizeBefore = toUse.reserved_getCurrentSize();
                        if ((figureBitboard & 0xFF00L) == 0L) continue block8;
                        promCount = WhitePawnMovesGen.genPromotionMoves(excludedToFieldsBoard, true, figureBitboard, fieldID, this.free, this.allByColour[opponentColour], this.board, toUse, maxCount);
                        count += promCount;
                        moves = toUse.reserved_getMovesBuffer();
                        for (cur = sizeBefore; cur < sizeBefore + promCount; ++cur) {
                            prom = moves[cur];
                            if (this.isDirectCheckMove(prom)) continue;
                            moves[cur] = moves[sizeBefore + promCount - 1];
                            --cur;
                            --promCount;
                            --count;
                            toUse.reserved_removeLast();
                        }
                        if (count < maxCount) continue block8;
                        return count;
                    }
                    pawns = this.allByColourAndType[1][1];
                    long possibleAttacks = BlackPawnsChecks.FIELDS_ATTACKERS_2[opponentKingFieldID];
                    if ((pawns & possibleAttacks) != 0L) {
                        if ((count += BlackPawnMovesGen.genCheckMoves(excludedToFieldsBoard, pid, fieldID, opponentKingFieldID, this.free, this.allByColour[opponentColour], this.board, list, maxCount)) >= maxCount) {
                            return count;
                        }
                        if (enpassantPossible) {
                            toUse = list;
                            if (toUse == null) {
                                this.movesBuffer.reserved_clear();
                                toUse = this.movesBuffer;
                            }
                            sizeBefore = toUse.reserved_getCurrentSize();
                            fromIndex_before = count;
                            curEnpassCount = (count += BlackPawnMovesGen.genEnpassantMove(this, excludedToFieldsBoard, pid, fieldID, this.allByColour[opponentColour], this.board, curEnpassInfo.enpassantPawnBitboard != 0L, curEnpassInfo.enpassantPawnBitboard, toUse, maxCount)) - fromIndex_before;
                            if (curEnpassCount > 1) {
                                throw new IllegalStateException();
                            }
                            if (curEnpassCount == 1) {
                                ++enpassCount;
                                if (!this.isDirectCheckMove(toUse.reserved_getMovesBuffer()[sizeBefore])) {
                                    --count;
                                    toUse.reserved_removeLast();
                                }
                            }
                            if (enpassCount == 2) {
                                enpassantPossible = false;
                            }
                            if (count >= maxCount) {
                                return count;
                            }
                        }
                    }
                    if (!promotionsPossible) continue block8;
                    toUse = list;
                    if (toUse == null) {
                        this.movesBuffer.reserved_clear();
                        toUse = this.movesBuffer;
                    }
                    sizeBefore = toUse.reserved_getCurrentSize();
                    if ((figureBitboard & 0xFF000000000000L) == 0L) continue block8;
                    promCount = BlackPawnMovesGen.genPromotionMoves(excludedToFieldsBoard, true, figureBitboard, fieldID, this.free, this.allByColour[opponentColour], this.board, toUse, maxCount);
                    count += promCount;
                    moves = toUse.reserved_getMovesBuffer();
                    for (cur = sizeBefore; cur < sizeBefore + promCount; ++cur) {
                        prom = moves[cur];
                        if (this.isDirectCheckMove(prom)) continue;
                        moves[cur] = moves[sizeBefore + promCount - 1];
                        --cur;
                        --promCount;
                        --count;
                        toUse.reserved_removeLast();
                    }
                    if (count < maxCount) continue block8;
                    return count;
                }
                case 3: {
                    if ((count += OfficerMovesGen.genCheckMoves(excludedToFieldsBoard, pid, fieldID, opponentKingBitboard, opponentKingFieldID, this.free, this.allByColour[colour], this.allByColour[opponentColour], this.board, list, maxCount)) < maxCount) continue block8;
                    return count;
                }
                case 4: {
                    if ((count += CastleMovesGen.genCheckMoves(excludedToFieldsBoard, pid, fieldID, opponentKingFieldID, this.free, this.allByColour[colour], this.allByColour[opponentColour], this.board, list, maxCount)) < maxCount) continue block8;
                    return count;
                }
                case 5: {
                    if ((count += QueenMovesGen.genCheckMoves(excludedToFieldsBoard, pid, fieldID, opponentKingBitboard, opponentKingFieldID, this.free, this.allByColour[colour], this.allByColour[opponentColour], this.board, list, maxCount)) < maxCount) continue block8;
                    return count;
                }
            }
        }
        return count;
    }

    private final int genHiddenCheckMoves(int colour, IInternalMoveList list, int maxCount) {
        boolean enpassantPossible;
        int count = 0;
        if (this.getKingIndexSet(colour).getDataSize() == 0) {
            return 0;
        }
        this.fillCheckKeepers(colour);
        byte opponentColour = Figures.OPPONENT_COLOUR[colour];
        int opponentKingFieldID = this.getKingFieldID(opponentColour);
        long myPieces = this.getFiguresBitboardByColour(colour);
        long opponentPieces = this.getFiguresBitboardByColour(opponentColour);
        long officersAttacks = OfficerPlies.ALL_OFFICER_MOVES[opponentKingFieldID];
        if ((count += this.genHiddenChecksFromOfficers(colour, opponentColour, opponentKingFieldID, myPieces, opponentPieces, officersAttacks, this.allByColourAndType[colour][3], 3, list, maxCount)) >= maxCount) {
            return count;
        }
        if ((count += this.genHiddenChecksFromOfficers(colour, opponentColour, opponentKingFieldID, myPieces, opponentPieces, officersAttacks, this.allByColourAndType[colour][5], 5, list, maxCount)) >= maxCount) {
            return count;
        }
        long castlesAttacks = CastlePlies.ALL_CASTLE_MOVES[opponentKingFieldID];
        if ((count += this.genHiddenChecksFromCastles(colour, opponentColour, opponentKingFieldID, myPieces, opponentPieces, castlesAttacks, this.allByColourAndType[colour][4], 4, list, maxCount)) >= maxCount) {
            return count;
        }
        if ((count += this.genHiddenChecksFromCastles(colour, opponentColour, opponentKingFieldID, myPieces, opponentPieces, castlesAttacks, this.allByColourAndType[colour][5], 5, list, maxCount)) >= maxCount) {
            return count;
        }
        int enpassCount = 0;
        BackupInfo curEnpassInfo = this.backupInfo[this.playedMovesCount];
        boolean bl = enpassantPossible = curEnpassInfo.enpassantPawnBitboard != 0L;
        if (enpassantPossible) {
            int pid = Figures.getPidByColourAndType(colour, 1);
            PiecesList fields = this.pieces.getPieces(pid);
            int size = fields.getDataSize();
            int[] data = fields.getData();
            for (int i = 0; i < size; ++i) {
                int fieldID = data[i];
                if (colour == 0) {
                    excludedToFieldsIDs = 0L;
                    if (this.checkKeepersBuffer[colour].contains(fieldID)) {
                        excludedToFieldsIDs |= this.checkKeepersBuffer[colour].getValue(fieldID) ^ 0xFFFFFFFFFFFFFFFFL;
                    }
                    if ((toUse = list) == null) {
                        this.movesBuffer.reserved_clear();
                        toUse = this.movesBuffer;
                    }
                    sizeBefore = toUse.reserved_getCurrentSize();
                    fromIndex_before = count;
                    curEnpassCount = (count += WhitePawnMovesGen.genEnpassantMove(this, excludedToFieldsIDs, pid, fieldID, this.allByColour[opponentColour], this.board, curEnpassInfo.enpassantPawnBitboard != 0L, curEnpassInfo.enpassantPawnBitboard, toUse, maxCount)) - fromIndex_before;
                    if (curEnpassCount > 1) {
                        throw new IllegalStateException();
                    }
                    if (curEnpassCount == 1) {
                        ++enpassCount;
                        checkMove = this.isCheckMove(toUse.reserved_getMovesBuffer()[sizeBefore]);
                        directCheckMove = this.isDirectCheckMove(toUse.reserved_getMovesBuffer()[sizeBefore]);
                        boolean bl2 = hiddenCheckMove = checkMove && !directCheckMove;
                        if (!hiddenCheckMove) {
                            --count;
                            toUse.reserved_removeLast();
                        }
                    }
                    if (enpassCount == 2) {
                        enpassantPossible = false;
                    }
                    if (count >= maxCount) {
                        return count;
                    }
                } else {
                    excludedToFieldsIDs = 0L;
                    if (this.checkKeepersBuffer[colour].contains(fieldID)) {
                        excludedToFieldsIDs |= this.checkKeepersBuffer[colour].getValue(fieldID) ^ 0xFFFFFFFFFFFFFFFFL;
                    }
                    if ((toUse = list) == null) {
                        this.movesBuffer.reserved_clear();
                        toUse = this.movesBuffer;
                    }
                    sizeBefore = toUse.reserved_getCurrentSize();
                    fromIndex_before = count;
                    curEnpassCount = (count += BlackPawnMovesGen.genEnpassantMove(this, excludedToFieldsIDs, pid, fieldID, this.allByColour[opponentColour], this.board, curEnpassInfo.enpassantPawnBitboard != 0L, curEnpassInfo.enpassantPawnBitboard, toUse, maxCount)) - fromIndex_before;
                    if (curEnpassCount > 1) {
                        throw new IllegalStateException();
                    }
                    if (curEnpassCount == 1) {
                        ++enpassCount;
                        checkMove = this.isCheckMove(toUse.reserved_getMovesBuffer()[sizeBefore]);
                        directCheckMove = this.isDirectCheckMove(toUse.reserved_getMovesBuffer()[sizeBefore]);
                        boolean bl3 = hiddenCheckMove = checkMove && !directCheckMove;
                        if (!hiddenCheckMove) {
                            --count;
                            toUse.reserved_removeLast();
                        }
                    }
                    if (enpassCount == 2) {
                        enpassantPossible = false;
                    }
                    if (count >= maxCount) {
                        return count;
                    }
                }
                if (!enpassantPossible) break;
            }
            if (enpassCount < 1) {
                // empty if block
            }
        }
        return count;
    }

    private final int genHiddenChecksFromOfficers(int colour, int opponentColour, int opponentKingFieldID, long myPieces, long opponentPieces, long officersAttacks, long myOfficersBoard, int checkFigureType, IInternalMoveList list, int maxCount) {
        int count = 0;
        if ((officersAttacks & myOfficersBoard) != 0L) {
            long dir = 0L;
            long dir0 = OfficerPlies.ALL_OFFICER_DIR0_MOVES[opponentKingFieldID];
            long dir1 = OfficerPlies.ALL_OFFICER_DIR1_MOVES[opponentKingFieldID];
            long dir2 = OfficerPlies.ALL_OFFICER_DIR2_MOVES[opponentKingFieldID];
            long dir3 = OfficerPlies.ALL_OFFICER_DIR3_MOVES[opponentKingFieldID];
            boolean hit = false;
            if ((myOfficersBoard & dir0) != 0L) {
                hit = true;
                dir |= dir0;
            }
            if ((myOfficersBoard & dir1) != 0L) {
                hit = true;
                dir |= dir1;
            }
            if ((myOfficersBoard & dir2) != 0L) {
                hit = true;
                dir |= dir2;
            }
            if ((myOfficersBoard & dir3) != 0L) {
                hit = true;
                dir |= dir3;
            }
            if (!hit) {
                throw new IllegalStateException();
            }
            boolean hit1 = false;
            int pid = Figures.getPidByColourAndType(colour, checkFigureType);
            PiecesList fields = this.pieces.getPieces(pid);
            int size = fields.getDataSize();
            int[] ids = fields.getData();
            block8: for (int i = 0; i < size; ++i) {
                long myAndPath;
                int officerFieldID = ids[i];
                long officerBitboard = Fields.ALL_ORDERED_A1H1[officerFieldID];
                if ((officerBitboard & dir) == 0L) continue;
                hit1 = true;
                long path = OfficerPlies.PATHS[officerFieldID][opponentKingFieldID];
                if ((path & opponentPieces) != 0L || (myAndPath = path & myPieces) == 0L || !Utils.has1BitSet(myAndPath)) continue;
                int fieldID = Board.get67IDByBitboard(myAndPath);
                int f_pid = this.board[fieldID];
                int figureType = Figures.getTypeByPid(f_pid);
                switch (figureType) {
                    case 1: {
                        if ((count += this.getAllSingleFigureMoves(path, colour, opponentColour, f_pid, figureType, myAndPath, fieldID, list, maxCount)) < maxCount) continue block8;
                        return count;
                    }
                    case 3: {
                        if ((count += this.getAllSingleFigureMoves(path, colour, opponentColour, f_pid, figureType, myAndPath, fieldID, list, maxCount)) < maxCount) continue block8;
                        return count;
                    }
                    case 4: {
                        if ((count += this.getAllSingleFigureMoves(path, colour, opponentColour, f_pid, figureType, myAndPath, fieldID, list, maxCount)) < maxCount) continue block8;
                        return count;
                    }
                    case 5: {
                        if ((count += this.getAllSingleFigureMoves(path, colour, opponentColour, f_pid, figureType, myAndPath, fieldID, list, maxCount)) < maxCount) continue block8;
                        return count;
                    }
                    case 2: {
                        if ((count += this.getAllSingleFigureMoves(path, colour, opponentColour, f_pid, figureType, myAndPath, fieldID, list, maxCount)) < maxCount) continue block8;
                        return count;
                    }
                    case 6: {
                        if ((count += this.getAllSingleFigureMoves(path, colour, opponentColour, f_pid, figureType, myAndPath, fieldID, list, maxCount)) < maxCount) continue block8;
                        return count;
                    }
                }
            }
            if (!hit1) {
                throw new IllegalStateException();
            }
        }
        return count;
    }

    private final int genHiddenChecksFromCastles(int colour, int opponentColour, int opponentKingFieldID, long myPieces, long opponentPieces, long castlesAttacks, long myCastlesBoard, int checkFigureType, IInternalMoveList list, int maxCount) {
        int count = 0;
        if ((castlesAttacks & myCastlesBoard) != 0L) {
            long dir = 0L;
            long dir0 = CastlePlies.ALL_CASTLE_DIR0_MOVES[opponentKingFieldID];
            long dir1 = CastlePlies.ALL_CASTLE_DIR1_MOVES[opponentKingFieldID];
            long dir2 = CastlePlies.ALL_CASTLE_DIR2_MOVES[opponentKingFieldID];
            long dir3 = CastlePlies.ALL_CASTLE_DIR3_MOVES[opponentKingFieldID];
            boolean hit = false;
            if ((myCastlesBoard & dir0) != 0L) {
                hit = true;
                dir |= dir0;
            }
            if ((myCastlesBoard & dir1) != 0L) {
                hit = true;
                dir |= dir1;
            }
            if ((myCastlesBoard & dir2) != 0L) {
                hit = true;
                dir |= dir2;
            }
            if ((myCastlesBoard & dir3) != 0L) {
                hit = true;
                dir |= dir3;
            }
            if (!hit) {
                throw new IllegalStateException();
            }
            boolean hit1 = false;
            int pid = Figures.getPidByColourAndType(colour, checkFigureType);
            PiecesList fields = this.pieces.getPieces(pid);
            int size = fields.getDataSize();
            int[] ids = fields.getData();
            block8: for (int i = 0; i < size; ++i) {
                long myAndPath;
                int castleFieldID = ids[i];
                long castleBitboard = Fields.ALL_ORDERED_A1H1[castleFieldID];
                if ((castleBitboard & dir) == 0L) continue;
                hit1 = true;
                long path = CastlePlies.PATHS[castleFieldID][opponentKingFieldID];
                if ((path & opponentPieces) != 0L || (myAndPath = path & myPieces) == 0L || !Utils.has1BitSet(myAndPath)) continue;
                int fieldID = Board.get67IDByBitboard(myAndPath);
                int f_pid = this.board[fieldID];
                int figureType = Figures.getTypeByPid(f_pid);
                switch (figureType) {
                    case 1: {
                        if ((count += this.getAllSingleFigureMoves(path, colour, opponentColour, f_pid, figureType, myAndPath, fieldID, list, maxCount)) < maxCount) continue block8;
                        return count;
                    }
                    case 3: {
                        if ((count += this.getAllSingleFigureMoves(path, colour, opponentColour, f_pid, figureType, myAndPath, fieldID, list, maxCount)) < maxCount) continue block8;
                        return count;
                    }
                    case 4: {
                        if ((count += this.getAllSingleFigureMoves(path, colour, opponentColour, f_pid, figureType, myAndPath, fieldID, list, maxCount)) < maxCount) continue block8;
                        return count;
                    }
                    case 5: {
                        if ((count += this.getAllSingleFigureMoves(path, colour, opponentColour, f_pid, figureType, myAndPath, fieldID, list, maxCount)) < maxCount) continue block8;
                        return count;
                    }
                    case 2: {
                        if ((count += this.getAllSingleFigureMoves(path, colour, opponentColour, f_pid, figureType, myAndPath, fieldID, list, maxCount)) < maxCount) continue block8;
                        return count;
                    }
                    case 6: {
                        if ((count += this.getAllSingleFigureMoves(path, colour, opponentColour, f_pid, figureType, myAndPath, fieldID, list, maxCount)) < maxCount) continue block8;
                        return count;
                    }
                }
            }
            if (!hit1) {
                throw new IllegalStateException();
            }
        }
        return count;
    }

    private final int getAllSingleFigureMoves(long excludedToFieldsIDs, int colour, int opponentColour, int pid, int figureType, long fieldBitboard, int fieldID, IInternalMoveList list, int maxCount) {
        int count = 0;
        if (this.checkKeepersBuffer[colour].contains(fieldID)) {
            excludedToFieldsIDs |= this.checkKeepersBuffer[colour].getValue(fieldID) ^ 0xFFFFFFFFFFFFFFFFL;
        }
        switch (figureType) {
            case 2: {
                if ((count += KnightMovesGen.genAllMoves(excludedToFieldsIDs, pid, fieldID, this.allByColour[colour], this.allByColour[opponentColour], this.board, list, maxCount)) < maxCount) break;
                return count;
            }
            case 6: {
                int opKingID = this.getKingFieldID(opponentColour);
                if ((count += KingMovesGen.genAllMoves(true, this, excludedToFieldsIDs, pid, colour, opponentColour, fieldBitboard, fieldID, this.free, this.allByColour[colour], this.allByColour[opponentColour], this.board, this.kingSidePossible(colour, opponentColour), this.queenSidePossible(colour, opponentColour), Fields.ALL_ORDERED_A1H1[opKingID], opKingID, list, maxCount)) < maxCount) break;
                return count;
            }
            case 1: {
                if (colour == 0) {
                    if ((count += WhitePawnMovesGen.genPromotionMoves(excludedToFieldsIDs, true, fieldBitboard, fieldID, this.free, this.allByColour[opponentColour], this.board, list, maxCount)) >= maxCount) {
                        return count;
                    }
                    if ((count += WhitePawnMovesGen.genAllNonSpecialMoves(excludedToFieldsIDs, true, pid, fieldID, this.free, this.allByColour[opponentColour], this.board, list, maxCount)) < maxCount) break;
                    return count;
                }
                if ((count += BlackPawnMovesGen.genPromotionMoves(excludedToFieldsIDs, true, fieldBitboard, fieldID, this.free, this.allByColour[opponentColour], this.board, list, maxCount)) >= maxCount) {
                    return count;
                }
                if ((count += BlackPawnMovesGen.genAllNonSpecialMoves(excludedToFieldsIDs, true, pid, fieldID, this.free, this.allByColour[opponentColour], this.board, list, maxCount)) < maxCount) break;
                return count;
            }
            case 3: {
                if ((count += OfficerMovesGen.genAllMoves(excludedToFieldsIDs, true, pid, fieldID, this.free, this.allByColour[opponentColour], this.board, list, maxCount)) < maxCount) break;
                return count;
            }
            case 4: {
                if ((count += CastleMovesGen.genAllMoves(excludedToFieldsIDs, true, pid, fieldID, this.free, this.allByColour[opponentColour], this.board, list, maxCount)) < maxCount) break;
                return count;
            }
            case 5: {
                if ((count += QueenMovesGen.genAllMoves(excludedToFieldsIDs, true, pid, fieldID, this.free, this.allByColour[opponentColour], this.board, list, maxCount)) < maxCount) break;
                return count;
            }
        }
        return count;
    }

    @Override
    public final int genKingEscapes(IInternalMoveList list) {
        return this.genKingEscapes(this.getColourToMove(), list, 256);
    }

    private final int genKingEscapes(int colour, IInternalMoveList list, int maxCount) {
        int count = 0;
        if (this.getKingIndexSet(colour).getDataSize() == 0) {
            return 0;
        }
        this.fillCheckKeepers(colour);
        byte opponentColour = Figures.OPPONENT_COLOUR[colour];
        int myKingID = this.getKingFieldID(colour);
        int checksCount = CheckingCount.getChecksCount(this.checkerBuffer, this, colour, opponentColour, Fields.ALL_ORDERED_A1H1[myKingID], myKingID, this.free);
        if (checksCount <= 0) {
            // empty if block
        }
        if ((count += this.genAllMoves_FiguresWithSameType(0L, false, true, colour, opponentColour, 6, list, maxCount)) >= maxCount) {
            return count;
        }
        if (checksCount == 1) {
            long includedToFieldsBoard = this.checkerBuffer.fieldBitboard;
            if (this.checkerBuffer.slider) {
                includedToFieldsBoard |= this.checkerBuffer.sliderAttackRayBitboard;
            }
            if ((count += this.genAllMoves_FiguresWithSameType(includedToFieldsBoard ^ 0xFFFFFFFFFFFFFFFFL, false, true, colour, opponentColour, 1, list, maxCount)) >= maxCount) {
                return count;
            }
            if ((count += this.genAllMoves_FiguresWithSameType(includedToFieldsBoard ^ 0xFFFFFFFFFFFFFFFFL, false, true, colour, opponentColour, 2, list, maxCount)) >= maxCount) {
                return count;
            }
            if ((count += this.genAllMoves_FiguresWithSameType(includedToFieldsBoard ^ 0xFFFFFFFFFFFFFFFFL, false, true, colour, opponentColour, 3, list, maxCount)) >= maxCount) {
                return count;
            }
            if ((count += this.genAllMoves_FiguresWithSameType(includedToFieldsBoard ^ 0xFFFFFFFFFFFFFFFFL, false, true, colour, opponentColour, 4, list, maxCount)) >= maxCount) {
                return count;
            }
            if ((count += this.genAllMoves_FiguresWithSameType(includedToFieldsBoard ^ 0xFFFFFFFFFFFFFFFFL, false, true, colour, opponentColour, 5, list, maxCount)) >= maxCount) {
                return count;
            }
        } else if (checksCount > 2) {
            // empty if block
        }
        return count;
    }

    @Override
    public final boolean hasMoveInCheck() {
        boolean result = this.genKingEscapes(this.getColourToMove(), null, 1) > 0;
        return result;
    }

    @Override
    public final boolean hasMoveInNonCheck() {
        boolean result = this.genAllMoves(0L, true, this.getColourToMove(), null, 1) > 0;
        return result;
    }

    @Override
    public final boolean hasSingleMove() {
        int colour = this.getColourToMove();
        boolean inCheck = this.isInCheck(colour);
        boolean result = inCheck ? this.genKingEscapes(colour, null, 2) == 1 : this.genAllMoves(0L, true, colour, null, 2) == 1;
        return result;
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
        int fieldID;
        int i;
        for (int fieldID2 = 0; fieldID2 < 64; ++fieldID2) {
            int pid;
            this.board[fieldID2] = pid = boardArr[fieldID2];
            if (pid == 0) continue;
            this.pieces.add(pid, fieldID2);
            this.materialFactor.initially_addPiece(pid, fieldID2, 0L);
            this.materialState.initially_addPiece(pid, fieldID2, 0L);
            if (this.eval != null) {
                this.eval.initially_addPiece(pid, fieldID2, 0L);
            }
            this.hashkey ^= ConstantStructure.MOVES_KEYS[pid][fieldID2];
            if (pid != 1 && pid != 7 && pid != 6 && pid != 12) continue;
            this.pawnskey ^= ConstantStructure.MOVES_KEYS[pid][fieldID2];
        }
        PiecesList king = this.pieces.getPieces(6);
        int size = king.getDataSize();
        int[] data = king.getData();
        for (int i2 = 0; i2 < size; ++i2) {
            int fieldID3 = data[i2];
            long[] lArray = this.allByColourAndType[0];
            lArray[6] = lArray[6] | Fields.ALL_ORDERED_A1H1[fieldID3];
        }
        PiecesList knights = this.pieces.getPieces(2);
        size = knights.getDataSize();
        data = knights.getData();
        for (int i3 = 0; i3 < size; ++i3) {
            int fieldID4 = data[i3];
            long[] lArray = this.allByColourAndType[0];
            lArray[2] = lArray[2] | Fields.ALL_ORDERED_A1H1[fieldID4];
        }
        PiecesList pawns = this.pieces.getPieces(1);
        size = pawns.getDataSize();
        data = pawns.getData();
        for (int i4 = 0; i4 < size; ++i4) {
            int fieldID5 = data[i4];
            long[] lArray = this.allByColourAndType[0];
            lArray[1] = lArray[1] | Fields.ALL_ORDERED_A1H1[fieldID5];
        }
        PiecesList officers = this.pieces.getPieces(3);
        size = officers.getDataSize();
        data = officers.getData();
        for (int i5 = 0; i5 < size; ++i5) {
            int fieldID6 = data[i5];
            long[] lArray = this.allByColourAndType[0];
            lArray[3] = lArray[3] | Fields.ALL_ORDERED_A1H1[fieldID6];
        }
        PiecesList castles = this.pieces.getPieces(4);
        size = castles.getDataSize();
        data = castles.getData();
        for (int i6 = 0; i6 < size; ++i6) {
            int fieldID7 = data[i6];
            long[] lArray = this.allByColourAndType[0];
            lArray[4] = lArray[4] | Fields.ALL_ORDERED_A1H1[fieldID7];
        }
        PiecesList queens = this.pieces.getPieces(5);
        size = queens.getDataSize();
        data = queens.getData();
        for (i = 0; i < size; ++i) {
            fieldID = data[i];
            long[] lArray = this.allByColourAndType[0];
            lArray[5] = lArray[5] | Fields.ALL_ORDERED_A1H1[fieldID];
        }
        king = this.pieces.getPieces(12);
        size = king.getDataSize();
        data = king.getData();
        for (i = 0; i < size; ++i) {
            fieldID = data[i];
            long[] lArray = this.allByColourAndType[1];
            lArray[6] = lArray[6] | Fields.ALL_ORDERED_A1H1[fieldID];
        }
        knights = this.pieces.getPieces(8);
        size = knights.getDataSize();
        data = knights.getData();
        for (i = 0; i < size; ++i) {
            fieldID = data[i];
            long[] lArray = this.allByColourAndType[1];
            lArray[2] = lArray[2] | Fields.ALL_ORDERED_A1H1[fieldID];
        }
        pawns = this.pieces.getPieces(7);
        size = pawns.getDataSize();
        data = pawns.getData();
        for (i = 0; i < size; ++i) {
            fieldID = data[i];
            long[] lArray = this.allByColourAndType[1];
            lArray[1] = lArray[1] | Fields.ALL_ORDERED_A1H1[fieldID];
        }
        officers = this.pieces.getPieces(9);
        size = officers.getDataSize();
        data = officers.getData();
        for (i = 0; i < size; ++i) {
            fieldID = data[i];
            long[] lArray = this.allByColourAndType[1];
            lArray[3] = lArray[3] | Fields.ALL_ORDERED_A1H1[fieldID];
        }
        castles = this.pieces.getPieces(10);
        size = castles.getDataSize();
        data = castles.getData();
        for (i = 0; i < size; ++i) {
            fieldID = data[i];
            long[] lArray = this.allByColourAndType[1];
            lArray[4] = lArray[4] | Fields.ALL_ORDERED_A1H1[fieldID];
        }
        queens = this.pieces.getPieces(11);
        size = queens.getDataSize();
        data = queens.getData();
        for (i = 0; i < size; ++i) {
            fieldID = data[i];
            long[] lArray = this.allByColourAndType[1];
            lArray[5] = lArray[5] | Fields.ALL_ORDERED_A1H1[fieldID];
        }
        this.allByColour[0] = this.allByColourAndType[0][6] | this.allByColourAndType[0][1] | this.allByColourAndType[0][2] | this.allByColourAndType[0][3] | this.allByColourAndType[0][4] | this.allByColourAndType[0][5];
        this.allByColour[1] = this.allByColourAndType[1][6] | this.allByColourAndType[1][1] | this.allByColourAndType[1][2] | this.allByColourAndType[1][3] | this.allByColourAndType[1][4] | this.allByColourAndType[1][5];
        this.free = (this.allByColour[0] | this.allByColour[1]) ^ 0xFFFFFFFFFFFFFFFFL;
    }

    @Override
    public final IPiecesLists getPiecesLists() {
        return this.pieces;
    }

    @Override
    public final long getFiguresBitboardByColour(int colour) {
        return this.allByColour[colour];
    }

    @Override
    public final long getFiguresBitboardByColourAndType(int colour, int type) {
        return this.allByColourAndType[colour][type];
    }

    @Override
    public final long getFiguresBitboardByPID(int pid) {
        return this.allByColourAndType[Constants.getColourByPieceIdentity(pid)][Constants.PIECE_IDENTITY_2_TYPE[pid]];
    }

    @Override
    public final long getFreeBitboard() {
        return this.free;
    }

    @Override
    public final long getPawnsHashKey() {
        return this.pawnskey;
    }

    @Override
    public int getFigureID(int fieldID) {
        return this.board[fieldID];
    }

    @Override
    public int getFigureType(int fieldID) {
        return Figures.getFigureType(this.getFigureID(fieldID));
    }

    @Override
    public int getFigureColour(int fieldID) {
        return Figures.getFigureColour(this.getFigureID(fieldID));
    }

    @Override
    public IBoard.CastlingType getCastlingType(int colour) {
        return this.castledByColour[colour];
    }

    @Override
    public IBoard.CastlingPair getCastlingPair() {
        if (this.castledByColour[0] == null || this.castledByColour[1] == null) {
            throw new IllegalStateException();
        }
        switch (this.castledByColour[0]) {
            case NONE: {
                switch (this.castledByColour[1]) {
                    case NONE: {
                        return IBoard.CastlingPair.NONE_NONE;
                    }
                    case KINGSIDE: {
                        return IBoard.CastlingPair.NONE_KINGSIDE;
                    }
                    case QUEENSIDE: {
                        return IBoard.CastlingPair.NONE_QUEENSIDE;
                    }
                }
                throw new IllegalStateException();
            }
            case KINGSIDE: {
                switch (this.castledByColour[1]) {
                    case NONE: {
                        return IBoard.CastlingPair.KINGSIDE_NONE;
                    }
                    case KINGSIDE: {
                        return IBoard.CastlingPair.KINGSIDE_KINGSIDE;
                    }
                    case QUEENSIDE: {
                        return IBoard.CastlingPair.KINGSIDE_QUEENSIDE;
                    }
                }
                throw new IllegalStateException();
            }
            case QUEENSIDE: {
                switch (this.castledByColour[1]) {
                    case NONE: {
                        return IBoard.CastlingPair.QUEENSIDE_NONE;
                    }
                    case KINGSIDE: {
                        return IBoard.CastlingPair.QUEENSIDE_KINGSIDE;
                    }
                    case QUEENSIDE: {
                        return IBoard.CastlingPair.QUEENSIDE_QUEENSIDE;
                    }
                }
                throw new IllegalStateException();
            }
        }
        throw new IllegalStateException();
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

    private int getPlayedMovesCount_Total() {
        return this.playedMovesCount_initial + this.getPlayedMovesCount();
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

    public Board clone() {
        Board clone = null;
        try {
            clone = (Board)super.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        this.cloneInternal(clone);
        return clone;
    }

    public void cloneInternal(Board clone) {
        clone.free = this.free;
        clone.allByColour = Utils.copy(this.allByColour);
        clone.allByColourAndType = Utils.copy(this.allByColourAndType);
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
        if (obj instanceof Board) {
            Board other = (Board)obj;
            result = other.free == this.free;
            result = result && Arrays.equals(other.allByColour, this.allByColour);
            result = result && Utils.equals(other.allByColourAndType, this.allByColourAndType);
            result = result && Arrays.equals(other.board, this.board);
            result = result && other.lastMoveColour == this.lastMoveColour;
            result = result && Arrays.equals((Object[])other.castledByColour, (Object[])this.castledByColour);
            result = result && other.hashkey == this.hashkey;
            result = result && other.pawnskey == this.pawnskey;
        }
        return result;
    }

    public String toString() {
        String result = "\r\nWhite: " + Bits.toBinaryString(this.allByColour[0]) + "\r\n";
        result = result + "Black: " + Bits.toBinaryString(this.allByColour[1]) + "\r\n";
        result = result + "Free : " + Bits.toBinaryString(this.free) + "\r\n";
        result = result + "WKing: " + Bits.toBinaryString(this.allByColourAndType[0][6]) + "\r\n";
        result = result + "BKing: " + Bits.toBinaryString(this.allByColourAndType[1][6]) + "\r\n";
        result = result + "WPawn: " + Bits.toBinaryString(this.allByColourAndType[0][1]) + "\r\n";
        result = result + "BPawn: " + Bits.toBinaryString(this.allByColourAndType[1][1]) + "\r\n";
        result = result + "WKngh: " + Bits.toBinaryString(this.allByColourAndType[0][2]) + "\r\n";
        result = result + "BKngh: " + Bits.toBinaryString(this.allByColourAndType[1][2]) + "\r\n";
        result = result + "WOffi: " + Bits.toBinaryString(this.allByColourAndType[0][3]) + "\r\n";
        result = result + "BOffi: " + Bits.toBinaryString(this.allByColourAndType[1][3]) + "\r\n";
        result = result + "WCast: " + Bits.toBinaryString(this.allByColourAndType[0][4]) + "\r\n";
        result = result + "BCast: " + Bits.toBinaryString(this.allByColourAndType[1][4]) + "\r\n";
        result = result + "WQeen: " + Bits.toBinaryString(this.allByColourAndType[0][5]) + "\r\n";
        result = result + "BQeen: " + Bits.toBinaryString(this.allByColourAndType[1][5]) + "\r\n";
        result = result + this.matrixToString();
        result = result + "Moves: " + this.movesToString() + "\r\n";
        result = result + "EPD: " + this.toEPD() + "\r\n";
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
            result = (String)result + this.moveOps.moveToString(move) + ", ";
        }
        return result;
    }

    private void checkConsistency() {
        long allWhiteBitboard = 0L;
        allWhiteBitboard |= this.checkConsistency_AliveFiguresByTypeAndColour(0, 1, this.pieces.getPieces(1));
        allWhiteBitboard |= this.checkConsistency_AliveFiguresByTypeAndColour(0, 2, this.pieces.getPieces(2));
        allWhiteBitboard |= this.checkConsistency_AliveFiguresByTypeAndColour(0, 6, this.pieces.getPieces(6));
        allWhiteBitboard |= this.checkConsistency_AliveFiguresByTypeAndColour(0, 3, this.pieces.getPieces(3));
        allWhiteBitboard |= this.checkConsistency_AliveFiguresByTypeAndColour(0, 4, this.pieces.getPieces(4));
        if ((allWhiteBitboard |= this.checkConsistency_AliveFiguresByTypeAndColour(0, 5, this.pieces.getPieces(5))) != this.allByColour[0]) {
            throw new IllegalStateException("allWhiteBitboard=" + allWhiteBitboard + ", allByColour[Figures.COLOUR_WHITE]=" + this.allByColour[0]);
        }
        long allBlackBitboard = 0L;
        allBlackBitboard |= this.checkConsistency_AliveFiguresByTypeAndColour(1, 1, this.pieces.getPieces(7));
        allBlackBitboard |= this.checkConsistency_AliveFiguresByTypeAndColour(1, 2, this.pieces.getPieces(8));
        allBlackBitboard |= this.checkConsistency_AliveFiguresByTypeAndColour(1, 6, this.pieces.getPieces(12));
        allBlackBitboard |= this.checkConsistency_AliveFiguresByTypeAndColour(1, 3, this.pieces.getPieces(9));
        allBlackBitboard |= this.checkConsistency_AliveFiguresByTypeAndColour(1, 4, this.pieces.getPieces(10));
        if ((allBlackBitboard |= this.checkConsistency_AliveFiguresByTypeAndColour(1, 5, this.pieces.getPieces(11))) != this.allByColour[1]) {
            throw new IllegalStateException("allBlackBitboard=" + allBlackBitboard + ", allByColour[Figures.COLOUR_BLACK]=" + this.allByColour[1]);
        }
    }

    private long checkConsistency_AliveFiguresByTypeAndColour(int colour, int type, PiecesList figIDs) {
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
            if (fieldID != Board.get67IDByBitboard(figBitboard)) {
                throw new IllegalStateException("fieldID=" + fieldID + " get67IDByBitboard(figBitboard)=" + Board.get67IDByBitboard(figBitboard));
            }
            typeBitboard |= figBitboard;
        }
        if (typeBitboard != this.allByColourAndType[colour][type]) {
            throw new IllegalStateException("typeBitboard=" + typeBitboard + " allByColourAndType[colour][type]=" + this.allByColourAndType[colour][type]);
        }
        return typeBitboard;
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
    public boolean isDraw50movesRule() {
        return this.lastCaptureOrPawnMoveBefore >= 100;
    }

    @Override
    public int getDraw50movesRule() {
        return this.lastCaptureOrPawnMoveBefore;
    }

    @Override
    public final IGameStatus getStatus() {
        int colourToMove;
        if (this.getStateRepetition() > 3) {
            // empty if block
        }
        if (this.getKingIndexSet(colourToMove = this.getColourToMove()).getDataSize() == 0) {
            throw new IllegalStateException();
        }
        if (this.getStateRepetition() >= 2) {
            return IGameStatus.DRAW_3_STATES_REPETITION;
        }
        if (this.isInCheck()) {
            if (!this.hasMoveInCheck()) {
                if (colourToMove == 0) {
                    return IGameStatus.MATE_BLACK_WIN;
                }
                return IGameStatus.MATE_WHITE_WIN;
            }
        } else if (!this.hasMoveInNonCheck()) {
            if (colourToMove == 0) {
                return IGameStatus.STALEMATE_WHITE_NO_MOVES;
            }
            return IGameStatus.STALEMATE_BLACK_NO_MOVES;
        }
        if (!this.hasSufficientMatingMaterial()) {
            return IGameStatus.NO_SUFFICIENT_MATERIAL;
        }
        if (this.isDraw50movesRule()) {
            return IGameStatus.DRAW_50_MOVES_RULE;
        }
        if (this.hasUnstoppablePasser(0)) {
            return IGameStatus.PASSER_WHITE;
        }
        if (this.hasUnstoppablePasser(1)) {
            return IGameStatus.PASSER_BLACK;
        }
        if (this.pieces.getPieces(1).getDataSize() == 0 && this.pieces.getPieces(5).getDataSize() == 0 && this.pieces.getPieces(4).getDataSize() == 0) {
            if (this.pieces.getPieces(3).getDataSize() == 0) {
                return IGameStatus.NO_SUFFICIENT_WHITE_MATERIAL;
            }
            if (this.pieces.getPieces(2).getDataSize() == 0 && this.pieces.getPieces(3).getDataSize() == 1) {
                return IGameStatus.NO_SUFFICIENT_WHITE_MATERIAL;
            }
        }
        if (this.pieces.getPieces(7).getDataSize() == 0 && this.pieces.getPieces(11).getDataSize() == 0 && this.pieces.getPieces(10).getDataSize() == 0) {
            if (this.pieces.getPieces(9).getDataSize() == 0) {
                return IGameStatus.NO_SUFFICIENT_BLACK_MATERIAL;
            }
            if (this.pieces.getPieces(8).getDataSize() == 0 && this.pieces.getPieces(9).getDataSize() == 1) {
                return IGameStatus.NO_SUFFICIENT_BLACK_MATERIAL;
            }
        }
        return IGameStatus.NONE;
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
    public IBoardConfig getBoardConfig() {
        return this.boardConfig;
    }

    @Override
    public int[] getMatrix() {
        return this.board;
    }

    @Override
    public int getSEEScore(int move) {
        return this.getSee().evalExchange(move);
    }

    @Override
    public int getSEEFieldScore(int squareID) {
        return this.getSee().seeField(squareID);
    }

    @Override
    public IMoveOps getMoveOps() {
        return this.moveOps;
    }

    @Override
    public int getEnpassantSquareID() {
        throw new UnsupportedOperationException();
    }

    public double[] getNNUEInputs() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CastlingConfig getCastlingConfig() {
        return CastlingConfig.CLASSIC_CHESS;
    }
}

