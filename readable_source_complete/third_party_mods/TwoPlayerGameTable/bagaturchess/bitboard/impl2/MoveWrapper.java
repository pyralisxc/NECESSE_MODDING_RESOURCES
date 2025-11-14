/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl2;

import bagaturchess.bitboard.impl1.internal.CastlingConfig;
import bagaturchess.bitboard.impl2.ChessBoard;
import bagaturchess.bitboard.impl2.ChessConstants;
import bagaturchess.bitboard.impl2.MoveUtil;

public class MoveWrapper {
    public int fromRank;
    public char fromFile;
    public int toRank;
    public char toFile;
    public int fromIndex;
    public int toIndex;
    public int move;
    public int pieceIndex;
    public int pieceIndexAttacked;
    public boolean isNightPromotion = false;
    public boolean isQueenPromotion = false;
    public boolean isRookPromotion = false;
    public boolean isBishopPromotion = false;
    public boolean isEP = false;
    public boolean isCastling = false;

    public MoveWrapper(int move, boolean isFRC, CastlingConfig castling_cfg) {
        this.move = move;
        this.fromIndex = MoveUtil.getFromIndex(move);
        this.fromFile = (char)(104 - this.fromIndex % 8);
        this.fromRank = this.fromIndex / 8 + 1;
        this.toIndex = MoveUtil.getToIndex(move);
        this.toFile = (char)(104 - this.toIndex % 8);
        this.toRank = this.toIndex / 8 + 1;
        this.pieceIndex = MoveUtil.getSourcePieceIndex(move);
        this.pieceIndexAttacked = MoveUtil.getAttackedPieceIndex(move);
        switch (MoveUtil.getMoveType(move)) {
            case 0: {
                break;
            }
            case 6: {
                this.isCastling = true;
                break;
            }
            case 1: {
                this.isEP = true;
                break;
            }
            case 3: {
                this.isBishopPromotion = true;
                break;
            }
            case 2: {
                this.isNightPromotion = true;
                break;
            }
            case 5: {
                this.isQueenPromotion = true;
                break;
            }
            case 4: {
                this.isRookPromotion = true;
                break;
            }
            default: {
                throw new RuntimeException("Unknown movetype: " + MoveUtil.getMoveType(move));
            }
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public MoveWrapper(String moveString, ChessBoard cb, boolean isFRC) {
        if ("O-O".equals(moveString)) {
            this.isCastling = true;
            this.fromIndex = cb.color_to_move == 0 ? cb.castling_config.from_SquareID_king_w : cb.castling_config.from_SquareID_king_b;
            this.toIndex = cb.color_to_move == 0 ? 1 : 57;
            this.fromFile = (char)(104 - this.fromIndex % 8);
            this.fromRank = this.fromIndex / 8 + 1;
            this.toFile = (char)(104 - this.toIndex % 8);
            this.toRank = this.toIndex / 8 + 1;
        } else if ("O-O-O".equals(moveString)) {
            this.isCastling = true;
            this.fromIndex = cb.color_to_move == 0 ? cb.castling_config.from_SquareID_king_w : cb.castling_config.from_SquareID_king_b;
            this.toIndex = cb.color_to_move == 0 ? 5 : 61;
            this.fromFile = (char)(104 - this.fromIndex % 8);
            this.fromRank = this.fromIndex / 8 + 1;
            this.toFile = (char)(104 - this.toIndex % 8);
            this.toRank = this.toIndex / 8 + 1;
        } else {
            this.fromFile = moveString.charAt(0);
            this.fromRank = Integer.parseInt(moveString.substring(1, 2));
            this.fromIndex = (this.fromRank - 1) * 8 + 104 - this.fromFile;
            this.toFile = moveString.charAt(2);
            this.toRank = Integer.parseInt(moveString.substring(3, 4));
            this.toIndex = (this.toRank - 1) * 8 + 104 - this.toFile;
        }
        int n = (cb.getPieces(cb.color_to_move, 1) & ChessConstants.POWER_LOOKUP[this.fromIndex]) != 0L ? 1 : ((cb.getPieces(cb.color_to_move, 3) & ChessConstants.POWER_LOOKUP[this.fromIndex]) != 0L ? 3 : ((cb.getPieces(cb.color_to_move, 2) & ChessConstants.POWER_LOOKUP[this.fromIndex]) != 0L ? 2 : ((cb.getPieces(cb.color_to_move, 6) & ChessConstants.POWER_LOOKUP[this.fromIndex]) != 0L ? 6 : ((cb.getPieces(cb.color_to_move, 5) & ChessConstants.POWER_LOOKUP[this.fromIndex]) != 0L ? 5 : (this.pieceIndex = (cb.getPieces(cb.color_to_move, 4) & ChessConstants.POWER_LOOKUP[this.fromIndex]) != 0L ? 4 : -1)))));
        if (this.pieceIndex == -1) {
            throw new RuntimeException("Source piece not found at index , cb.pieces[cb.colorToMove][type]=" + cb.getPieces(cb.color_to_move, 2) + ", fromIndex=" + this.fromIndex + ", cb.colorToMove=" + cb.color_to_move + ", move=" + moveString + ", board=" + cb.toString());
        }
        int n2 = (cb.getPieces(1 - cb.color_to_move, 1) & ChessConstants.POWER_LOOKUP[this.toIndex]) != 0L ? 1 : ((cb.getPieces(1 - cb.color_to_move, 3) & ChessConstants.POWER_LOOKUP[this.toIndex]) != 0L ? 3 : ((cb.getPieces(1 - cb.color_to_move, 2) & ChessConstants.POWER_LOOKUP[this.toIndex]) != 0L ? 2 : ((cb.getPieces(1 - cb.color_to_move, 6) & ChessConstants.POWER_LOOKUP[this.toIndex]) != 0L ? 6 : ((cb.getPieces(1 - cb.color_to_move, 5) & ChessConstants.POWER_LOOKUP[this.toIndex]) != 0L ? 5 : (this.pieceIndexAttacked = (cb.getPieces(1 - cb.color_to_move, 4) & ChessConstants.POWER_LOOKUP[this.toIndex]) != 0L ? 4 : 0)))));
        if (this.pieceIndexAttacked == 0) {
            if (this.pieceIndex == 1 && (this.toRank == 1 || this.toRank == 8)) {
                if (moveString.length() == 5) {
                    if (moveString.substring(4, 5).equals("n")) {
                        this.isNightPromotion = true;
                        this.move = MoveUtil.createPromotionMove(2, this.fromIndex, this.toIndex);
                        return;
                    } else if (moveString.substring(4, 5).equals("r")) {
                        this.isRookPromotion = true;
                        this.move = MoveUtil.createPromotionMove(4, this.fromIndex, this.toIndex);
                        return;
                    } else if (moveString.substring(4, 5).equals("b")) {
                        this.isBishopPromotion = true;
                        this.move = MoveUtil.createPromotionMove(3, this.fromIndex, this.toIndex);
                        return;
                    } else {
                        if (!moveString.substring(4, 5).equals("q")) return;
                        this.isQueenPromotion = true;
                        this.move = MoveUtil.createPromotionMove(5, this.fromIndex, this.toIndex);
                    }
                    return;
                } else {
                    this.isQueenPromotion = true;
                    this.move = MoveUtil.createPromotionMove(5, this.fromIndex, this.toIndex);
                }
                return;
            } else {
                if (!this.isCastling && this.pieceIndex == 6) {
                    int from_file = this.fromIndex & 7;
                    int from_rank = this.fromIndex >>> 3;
                    int to_file = this.toIndex & 7;
                    int to_rank = this.toIndex >>> 3;
                    int delta_file = Math.abs(from_file - to_file);
                    int delta_rank = Math.abs(from_rank - to_rank);
                    if (delta_rank > 1) {
                        throw new IllegalStateException("King move with delta_rank=" + delta_rank);
                    }
                    if (!isFRC) {
                        if (delta_file > 1) {
                            this.isCastling = true;
                        }
                    } else if (this.fromIndex == this.toIndex) {
                        if (this.toIndex != 1 && this.toIndex != 5 && this.toIndex != 57 && this.toIndex != 61) {
                            throw new IllegalStateException("King move is castling and the move has the same from_to squares, which are not G1 C1 G8 C8.");
                        }
                        this.isCastling = true;
                    } else if ((cb.getPieces(cb.color_to_move, 4) & ChessConstants.POWER_LOOKUP[this.toIndex]) != 0L) {
                        if (this.toIndex == cb.castling_config.from_SquareID_rook_kingside_w) {
                            this.isCastling = true;
                            this.toIndex = 1;
                        } else if (this.toIndex == cb.castling_config.from_SquareID_rook_queenside_w) {
                            this.isCastling = true;
                            this.toIndex = 5;
                        } else if (this.toIndex == cb.castling_config.from_SquareID_rook_kingside_b) {
                            this.isCastling = true;
                            this.toIndex = 57;
                        } else {
                            if (this.toIndex != cb.castling_config.from_SquareID_rook_queenside_b) throw new IllegalStateException("King move is castling and king goes to a rook, which is not on the initial square.");
                            this.isCastling = true;
                            this.toIndex = 61;
                        }
                    } else {
                        this.isCastling = delta_file > 1;
                    }
                }
                if (this.isCastling) {
                    this.move = MoveUtil.createCastlingMove(this.fromIndex, this.toIndex);
                    if (!cb.isValidMove(this.move)) throw new IllegalStateException("Not valid castling move=" + cb.getMoveOps().moveToString(this.move) + ", cb=" + String.valueOf(cb));
                    return;
                }
                this.move = this.pieceIndex == 1 && this.toIndex % 8 != this.fromIndex % 8 ? MoveUtil.createEPMove(this.fromIndex, this.toIndex) : MoveUtil.createMove(this.fromIndex, this.toIndex, this.pieceIndex);
            }
            return;
        } else if (this.pieceIndex == 1 && (this.toRank == 1 || this.toRank == 8)) {
            if (moveString.length() == 5) {
                if (moveString.substring(4, 5).equals("n")) {
                    this.isNightPromotion = true;
                    this.move = MoveUtil.createPromotionAttack(2, this.fromIndex, this.toIndex, this.pieceIndexAttacked);
                    return;
                } else if (moveString.substring(4, 5).equals("r")) {
                    this.isRookPromotion = true;
                    this.move = MoveUtil.createPromotionAttack(4, this.fromIndex, this.toIndex, this.pieceIndexAttacked);
                    return;
                } else if (moveString.substring(4, 5).equals("b")) {
                    this.isBishopPromotion = true;
                    this.move = MoveUtil.createPromotionAttack(3, this.fromIndex, this.toIndex, this.pieceIndexAttacked);
                    return;
                } else {
                    if (!moveString.substring(4, 5).equals("q")) return;
                    this.isQueenPromotion = true;
                    this.move = MoveUtil.createPromotionAttack(5, this.fromIndex, this.toIndex, this.pieceIndexAttacked);
                }
                return;
            } else {
                this.move = MoveUtil.createPromotionAttack(5, this.fromIndex, this.toIndex, this.pieceIndexAttacked);
            }
            return;
        } else {
            this.move = MoveUtil.createAttackMove(this.fromIndex, this.toIndex, this.pieceIndex, this.pieceIndexAttacked);
        }
    }

    public String toString() {
        String moveString = "" + this.fromFile + this.fromRank + this.toFile + this.toRank;
        if (this.isQueenPromotion) {
            return moveString + "q";
        }
        if (this.isNightPromotion) {
            return moveString + "n";
        }
        if (this.isRookPromotion) {
            return moveString + "r";
        }
        if (this.isBishopPromotion) {
            return moveString + "b";
        }
        return moveString;
    }

    public void toString(StringBuilder text_buffr) {
        text_buffr.append("" + this.fromFile + this.fromRank + this.toFile + this.toRank);
        if (this.isQueenPromotion) {
            text_buffr.append("q");
        } else if (this.isNightPromotion) {
            text_buffr.append("n");
        } else if (this.isRookPromotion) {
            text_buffr.append("r");
        } else if (this.isBishopPromotion) {
            text_buffr.append("b");
        }
    }

    public boolean equals(Object obj) {
        MoveWrapper compare = (MoveWrapper)obj;
        return compare.toString().equals(this.toString());
    }
}

