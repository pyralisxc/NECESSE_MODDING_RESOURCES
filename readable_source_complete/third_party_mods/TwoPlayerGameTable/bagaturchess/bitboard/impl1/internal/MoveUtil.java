/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl1.internal;

public class MoveUtil {
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_EP = 1;
    public static final int TYPE_PROMOTION_N = 2;
    public static final int TYPE_PROMOTION_B = 3;
    public static final int TYPE_PROMOTION_R = 4;
    public static final int TYPE_PROMOTION_Q = 5;
    public static final int TYPE_CASTLING = 6;
    private static final int SHIFT_TO = 6;
    private static final int SHIFT_SOURCE = 12;
    private static final int SHIFT_ATTACK = 15;
    private static final int SHIFT_MOVE_TYPE = 18;
    private static final int SHIFT_PROMOTION = 21;
    private static final int MASK_3_BITS = 7;
    private static final int MASK_6_BITS = 63;
    private static final int MASK_12_BITS = 4095;
    private static final int MASK_ATTACK = 229376;
    private static final int MASK_PROMOTION = 0x200000;
    private static final int MASK_QUIET = 2326528;

    public static final int getFromIndex(int move) {
        return move & 0x3F;
    }

    public static final int getToIndex(int move) {
        return move >>> 6 & 0x3F;
    }

    public static final int getFromToIndex(int move) {
        return move & 0xFFF;
    }

    public static final int getAttackedPieceIndex(int move) {
        return move >>> 15 & 7;
    }

    public static final int getSourcePieceIndex(int move) {
        return move >>> 12 & 7;
    }

    public static final int getMoveType(int move) {
        return move >>> 18 & 7;
    }

    public static final int createMove(int fromIndex, int toIndex, int sourcePieceIndex) {
        return sourcePieceIndex << 12 | toIndex << 6 | fromIndex;
    }

    public static final int createWhitePawnMove(int fromIndex) {
        return 0x1000 | fromIndex + 8 << 6 | fromIndex;
    }

    public static final int createBlackPawnMove(int fromIndex) {
        return 0x1000 | fromIndex - 8 << 6 | fromIndex;
    }

    public static final int createWhitePawn2Move(int fromIndex) {
        return 0x1000 | fromIndex + 16 << 6 | fromIndex;
    }

    public static final int createBlackPawn2Move(int fromIndex) {
        return 0x1000 | fromIndex - 16 << 6 | fromIndex;
    }

    public static final int createPromotionMove(int promotionPiece, int fromIndex, int toIndex) {
        return 0x200000 | promotionPiece << 18 | 0x1000 | toIndex << 6 | fromIndex;
    }

    public static final int createAttackMove(int fromIndex, int toIndex, int sourcePieceIndex, int attackedPieceIndex) {
        return attackedPieceIndex << 15 | sourcePieceIndex << 12 | toIndex << 6 | fromIndex;
    }

    public static final int createSeeAttackMove(long fromSquare, int sourcePieceIndex) {
        return sourcePieceIndex << 12 | Long.numberOfTrailingZeros(fromSquare);
    }

    public static final int createPromotionAttack(int promotionPiece, int fromIndex, int toIndex, int attackedPieceIndex) {
        return 0x200000 | promotionPiece << 18 | attackedPieceIndex << 15 | 0x1000 | toIndex << 6 | fromIndex;
    }

    public static final int createEPMove(int fromIndex, int toIndex) {
        return 0x49000 | toIndex << 6 | fromIndex;
    }

    public static final int createCastlingMove(int fromIndex, int toIndex) {
        return 0x186000 | toIndex << 6 | fromIndex;
    }

    public static final boolean isPromotion(int move) {
        return (move & 0x200000) != 0;
    }

    public static final boolean isPawnPush78(int move) {
        return MoveUtil.getSourcePieceIndex(move) == 1 && (MoveUtil.getToIndex(move) > 47 || MoveUtil.getToIndex(move) < 16);
    }

    public static final boolean isPawnPush678(int move, int color) {
        if (color == 0) {
            return MoveUtil.getSourcePieceIndex(move) == 1 && MoveUtil.getToIndex(move) > 39;
        }
        return MoveUtil.getSourcePieceIndex(move) == 1 && MoveUtil.getToIndex(move) < 24;
    }

    public static final boolean isQuiet(int move) {
        return (move & 0x238000) == 0;
    }

    public static final boolean isNormalMove(int move) {
        return MoveUtil.getMoveType(move) == 0;
    }

    public static final boolean isEPMove(int move) {
        return MoveUtil.getMoveType(move) == 1;
    }

    public static final boolean isCastlingMove(int move) {
        return MoveUtil.getMoveType(move) == 6;
    }
}

