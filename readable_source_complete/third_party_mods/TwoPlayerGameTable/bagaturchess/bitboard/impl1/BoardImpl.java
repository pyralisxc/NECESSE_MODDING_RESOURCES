/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl1;

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
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEval;
import bagaturchess.bitboard.impl.movelist.BaseMoveList;
import bagaturchess.bitboard.impl.movelist.IMoveList;
import bagaturchess.bitboard.impl.state.PiecesList;
import bagaturchess.bitboard.impl1.BaseEvaluation;
import bagaturchess.bitboard.impl1.NNUE_Input;
import bagaturchess.bitboard.impl1.internal.CastlingConfig;
import bagaturchess.bitboard.impl1.internal.CheckUtil;
import bagaturchess.bitboard.impl1.internal.ChessBoard;
import bagaturchess.bitboard.impl1.internal.ChessBoardUtil;
import bagaturchess.bitboard.impl1.internal.EvalConstants;
import bagaturchess.bitboard.impl1.internal.MoveGenerator;
import bagaturchess.bitboard.impl1.internal.MoveUtil;
import bagaturchess.bitboard.impl1.internal.MoveWrapper;
import bagaturchess.bitboard.impl1.internal.SEEUtil;

public class BoardImpl
implements IBitBoard {
    private ChessBoard chessBoard;
    private MoveGenerator generator;
    private IPiecesLists pieces;
    private IMaterialFactor materialFactor;
    private IBaseEval baseEval;
    private IMaterialState materialState;
    private IBoardConfig boardConfig;
    private IMoveOps moveOps;
    private IMoveList hasMovesList;
    private MoveListener[] moveListeners;
    private NNUE_Input nnue_input;
    private boolean enable_NNUE_Input = false;
    protected IBoard.CastlingType[] castledByColour;
    private boolean isFRC;

    public BoardImpl(String fen, IBoardConfig _boardConfig, boolean _isFRC) {
        long pieces;
        int piece;
        int color;
        this.boardConfig = _boardConfig;
        this.isFRC = _isFRC;
        this.generator = new MoveGenerator();
        this.pieces = new PiecesListsImpl(this);
        this.materialFactor = new MaterialFactorImpl();
        this.materialState = new MaterialStateImpl();
        this.moveOps = new MoveOpsImpl();
        this.hasMovesList = new BaseMoveList(250);
        this.castledByColour = new IBoard.CastlingType[2];
        this.castledByColour[0] = IBoard.CastlingType.NONE;
        this.castledByColour[1] = IBoard.CastlingType.NONE;
        this.moveListeners = new MoveListener[0];
        this.addMoveListener(this.materialFactor);
        if (this.boardConfig != null) {
            EvalConstants.initPSQT(this.boardConfig);
            this.chessBoard = ChessBoardUtil.getNewCB(fen);
            this.baseEval = new BaseEvaluation(this.boardConfig, this);
            for (color = 0; color < 2; ++color) {
                for (piece = 1; piece <= 6; ++piece) {
                    for (pieces = this.chessBoard.pieces[color][piece]; pieces != 0L; pieces &= pieces - 1L) {
                        this.baseEval.initially_addPiece(color, piece, pieces);
                    }
                }
            }
            this.addMoveListener(this.baseEval);
        } else {
            this.chessBoard = ChessBoardUtil.getNewCB(fen);
        }
        if (this.enable_NNUE_Input) {
            this.nnue_input = new NNUE_Input(this);
            for (color = 0; color < 2; ++color) {
                for (piece = 1; piece <= 6; ++piece) {
                    pieces = this.chessBoard.pieces[color][piece];
                    this.nnue_input.initially_addPiece(color, piece, pieces);
                }
            }
            this.addMoveListener(this.nnue_input);
        }
    }

    public boolean isFRC() {
        return this.isFRC;
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

    public ChessBoard getChessBoard() {
        return this.chessBoard;
    }

    public MoveGenerator getMoveGenerator() {
        return this.generator;
    }

    @Override
    public boolean isInCheck() {
        return this.chessBoard.checkingPieces != 0L;
    }

    @Override
    public boolean isInCheck(int colour) {
        return CheckUtil.isInCheck(this.chessBoard, colour);
    }

    public String toString() {
        Object moves_str = "";
        int[] moves = this.chessBoard.playedMoves;
        for (int i = 0; i < this.chessBoard.playedMovesCount; ++i) {
            moves_str = (String)moves_str + this.moveOps.moveToString(moves[i]) + " ";
        }
        return this.chessBoard.toString() + " moves " + (String)moves_str;
    }

    @Override
    public int genAllMoves(IInternalMoveList list) {
        this.generator.startPly();
        this.generator.generateAttacks(this.chessBoard);
        this.generator.generateMoves(this.chessBoard);
        int counter = 0;
        while (this.generator.hasNext()) {
            int cur_move = this.generator.next();
            if (!this.isPossible(cur_move)) continue;
            list.reserved_add(cur_move);
            ++counter;
        }
        this.generator.endPly();
        return counter;
    }

    @Override
    public int genKingEscapes(IInternalMoveList list) {
        return this.genAllMoves(list);
    }

    @Override
    public int genCapturePromotionMoves(IInternalMoveList list) {
        this.generator.startPly();
        this.generator.generateAttacks(this.chessBoard);
        int counter = 0;
        while (this.generator.hasNext()) {
            int cur_move = this.generator.next();
            if (!this.isPossible(cur_move)) continue;
            list.reserved_add(cur_move);
            ++counter;
        }
        this.generator.endPly();
        return counter;
    }

    @Override
    public int genNonCaptureNonPromotionMoves(IInternalMoveList list) {
        this.generator.startPly();
        this.generator.generateMoves(this.chessBoard);
        int counter = 0;
        while (this.generator.hasNext()) {
            int cur_move = this.generator.next();
            if (!this.isPossible(cur_move)) continue;
            list.reserved_add(cur_move);
            ++counter;
        }
        this.generator.endPly();
        return counter;
    }

    @Override
    public void makeMoveForward(int move) {
        try {
            int i;
            if (this.moveOps.isCastling(move)) {
                IBoard.CastlingType castlingType = this.castledByColour[this.getColourToMove()] = this.moveOps.isCastlingKingSide(move) ? IBoard.CastlingType.KINGSIDE : IBoard.CastlingType.QUEENSIDE;
            }
            if (this.moveListeners.length > 0) {
                for (i = 0; i < this.moveListeners.length; ++i) {
                    this.moveListeners[i].preForwardMove(this.chessBoard.colorToMove, move);
                }
            }
            this.chessBoard.doMove(move);
            if (this.moveListeners.length > 0) {
                for (i = 0; i < this.moveListeners.length; ++i) {
                    this.moveListeners[i].postForwardMove(this.chessBoard.colorToMoveInverse, move);
                }
            }
        }
        catch (Exception cause) {
            throw new IllegalStateException(this.toString(), cause);
        }
    }

    @Override
    public void makeMoveBackward(int move) {
        try {
            int i;
            if (this.moveListeners.length > 0) {
                for (i = 0; i < this.moveListeners.length; ++i) {
                    this.moveListeners[i].preBackwardMove(this.chessBoard.colorToMoveInverse, move);
                }
            }
            this.chessBoard.undoMove(move);
            if (this.moveOps.isCastling(move)) {
                this.castledByColour[this.getColourToMove()] = IBoard.CastlingType.NONE;
            }
            if (this.moveListeners.length > 0) {
                for (i = 0; i < this.moveListeners.length; ++i) {
                    this.moveListeners[i].postBackwardMove(this.getColourToMove(), move);
                }
            }
        }
        catch (Exception cause) {
            throw new IllegalStateException(this.toString(), cause);
        }
    }

    @Override
    public void makeMoveForward(String ucimove) {
        MoveWrapper move = new MoveWrapper(ucimove, this.chessBoard, this.isFRC);
        this.makeMoveForward(move.move);
    }

    @Override
    public void makeNullMoveForward() {
        this.chessBoard.doNullMove();
    }

    @Override
    public void makeNullMoveBackward() {
        this.chessBoard.undoNullMove();
    }

    @Override
    public int getColourToMove() {
        return this.chessBoard.colorToMove;
    }

    @Override
    public int getEnpassantSquareID() {
        return this.chessBoard.epIndex;
    }

    @Override
    public int getSEEScore(int move) {
        return SEEUtil.getSeeCaptureScore(this.chessBoard, move);
    }

    @Override
    public int getSEEFieldScore(int squareID) {
        return SEEUtil.getSeeFieldScore(this.chessBoard, squareID);
    }

    @Override
    public void revert() {
        for (int i = this.chessBoard.playedMovesCount - 1; i >= 0; --i) {
            int move = this.chessBoard.playedMoves[i];
            if (move == 0) {
                this.makeNullMoveBackward();
                continue;
            }
            this.makeMoveBackward(move);
        }
    }

    @Override
    public long getHashKey() {
        return this.chessBoard.zobristKey;
    }

    @Override
    public IPiecesLists getPiecesLists() {
        return this.pieces;
    }

    @Override
    public IMaterialFactor getMaterialFactor() {
        return this.materialFactor;
    }

    @Override
    public IBaseEval getBaseEvaluation() {
        return this.baseEval;
    }

    @Override
    public long getFiguresBitboardByColourAndType(int colour, int type) {
        return this.chessBoard.pieces[colour][type];
    }

    @Override
    public long getFiguresBitboardByColour(int colour) {
        return this.getFiguresBitboardByColourAndType(colour, 1) | this.getFiguresBitboardByColourAndType(colour, 2) | this.getFiguresBitboardByColourAndType(colour, 3) | this.getFiguresBitboardByColourAndType(colour, 4) | this.getFiguresBitboardByColourAndType(colour, 5) | this.getFiguresBitboardByColourAndType(colour, 6);
    }

    @Override
    public long getFreeBitboard() {
        return this.chessBoard.emptySpaces;
    }

    @Override
    public boolean hasRightsToKingCastle(int colour) {
        if (colour == 0) {
            return (this.chessBoard.castlingRights & 8) != 0;
        }
        return (this.chessBoard.castlingRights & 2) != 0;
    }

    @Override
    public boolean hasRightsToQueenCastle(int colour) {
        if (colour == 0) {
            return (this.chessBoard.castlingRights & 4) != 0;
        }
        return (this.chessBoard.castlingRights & 1) != 0;
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
    public CastlingConfig getCastlingConfig() {
        return this.chessBoard.castlingConfig;
    }

    @Override
    public int getFigureID(int squareID) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getFigureType(int squareID) {
        int pieceType = this.chessBoard.pieceIndexes[squareID];
        return pieceType;
    }

    @Override
    public int getFigureColour(int squareID) {
        long bb = 1L << squareID;
        if ((bb & this.getFiguresBitboardByColour(0)) != 0L) {
            return 0;
        }
        return 1;
    }

    @Override
    public boolean isDraw50movesRule() {
        return this.chessBoard.lastCaptureOrPawnMoveBefore >= 100;
    }

    @Override
    public int getDraw50movesRule() {
        return this.chessBoard.lastCaptureOrPawnMoveBefore;
    }

    @Override
    public PawnsEvalCache getPawnsCache() {
        return null;
    }

    @Override
    public void setPawnsCache(PawnsEvalCache pawnsCache) {
    }

    @Override
    public int getStateRepetition() {
        return this.chessBoard.getRepetition();
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
        return Utils.countBits(bishops) == 1 && Utils.countBits(knights) == 1;
    }

    @Override
    public int getLastMove() {
        if (this.chessBoard.playedMovesCount == 0) {
            return 0;
        }
        return this.chessBoard.playedMoves[this.chessBoard.playedMovesCount - 1];
    }

    @Override
    public boolean isCheckMove(int move) {
        boolean inCheck = false;
        this.chessBoard.doMove(move);
        inCheck = this.chessBoard.checkingPieces != 0L;
        this.chessBoard.undoMove(move);
        return inCheck;
    }

    @Override
    public boolean isPossible(int move) {
        return this.chessBoard.isValidMove(move) && this.chessBoard.isLegal(move);
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
        return this.chessBoard.pieceIndexes;
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
    public boolean hasSingleMove() {
        this.hasMovesList.clear();
        this.genAllMoves(this.hasMovesList);
        return this.hasMovesList.reserved_getCurrentSize() == 1;
    }

    @Override
    public IMoveOps getMoveOps() {
        return this.moveOps;
    }

    @Override
    public int getPlayedMovesCount() {
        return this.chessBoard.playedMovesCount;
    }

    @Override
    public int[] getPlayedMoves() {
        return this.chessBoard.playedMoves;
    }

    @Override
    public String toEPD() {
        return this.chessBoard.toString();
    }

    @Override
    public void setAttacksSupport(boolean attacksSupport, boolean fieldsStateSupport) {
    }

    @Override
    public long getHashKeyAfterMove(int move) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public final IGameStatus getStatus() {
        int colourToMove = this.getColourToMove();
        if (this.getStateRepetition() >= 3) {
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
        return IGameStatus.NONE;
    }

    public float[] getNNUEInputs() {
        if (!this.enable_NNUE_Input) {
            throw new UnsupportedOperationException();
        }
        return this.nnue_input.getInputs();
    }

    @Override
    public PawnsModelEval getPawnsStructure() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int genAllMoves_ByFigureID(int fieldID, long excludedToFields, IInternalMoveList list) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getPawnsHashKey() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ISEE getSee() {
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
    public IPlayerAttacks getPlayerAttacks(int colour) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IFieldsAttacks getFieldsAttacks() {
        throw new UnsupportedOperationException();
    }

    protected class PiecesListsImpl
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

    protected class MaterialFactorImpl
    implements IMaterialFactor {
        private static final int TOTAL_FACTOR_MAX = 62;

        @Override
        public int getBlackFactor() {
            return BoardImpl.this.chessBoard.material_factor_black;
        }

        @Override
        public int getWhiteFactor() {
            return BoardImpl.this.chessBoard.material_factor_white;
        }

        @Override
        public int getTotalFactor() {
            return this.getWhiteFactor() + this.getBlackFactor();
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

    protected class MaterialStateImpl
    implements IMaterialState {
        protected MaterialStateImpl() {
        }

        @Override
        public int getPiecesCount() {
            return Long.bitCount(BoardImpl.this.chessBoard.allPieces);
        }

        @Override
        public int[] getPIDsCounts() {
            throw new UnsupportedOperationException();
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
            int colour = BoardImpl.this.chessBoard.colorToMove;
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
            return new MoveWrapper(move, BoardImpl.this.isFRC, BoardImpl.this.chessBoard.castlingConfig).toString();
        }

        @Override
        public final void moveToString(int move, StringBuilder text_buffer) {
            new MoveWrapper(move, BoardImpl.this.isFRC, BoardImpl.this.chessBoard.castlingConfig).toString(text_buffer);
        }

        @Override
        public final int stringToMove(String move) {
            MoveWrapper moveObj = new MoveWrapper(move, BoardImpl.this.chessBoard, BoardImpl.this.isFRC);
            return moveObj.move;
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
}

