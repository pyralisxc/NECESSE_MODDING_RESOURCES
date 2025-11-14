/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl1.internal;

import bagaturchess.bitboard.impl1.internal.Assert;
import bagaturchess.bitboard.impl1.internal.ChessBoard;
import bagaturchess.bitboard.impl1.internal.ChessBoardUtil;

public class ChessBoardTestUtil {
    private static int[] testPieceIndexes = new int[64];

    public static void testValues(ChessBoard cb) {
        int castlingRights = cb.castlingRights;
        long iterativeZK = cb.zobristKey;
        long iterativeZKPawn = cb.pawnZobristKey;
        long iterativeWhitePieces = cb.friendlyPieces[0];
        long iterativeBlackPieces = cb.friendlyPieces[1];
        long iterativeAllPieces = cb.allPieces;
        long pinnedPieces = cb.pinnedPieces;
        long discoveredPieces = cb.discoveredPieces;
        int iterativePsqt_mg = cb.psqtScore_mg;
        int iterativePsqt_eg = cb.psqtScore_eg;
        long whiteKingArea = cb.kingArea[0];
        long blackKingArea = cb.kingArea[1];
        int material_factor_white = cb.material_factor_white;
        int material_factor_black = cb.material_factor_black;
        long materialKey = cb.materialKey;
        System.arraycopy(cb.pieceIndexes, 0, testPieceIndexes, 0, cb.pieceIndexes.length);
        Assert.isTrue(Long.numberOfTrailingZeros(cb.pieces[0][6]) == cb.kingIndex[0], "Long.numberOfTrailingZeros(cb.pieces[WHITE][KING]) == cb.kingIndex[WHITE]");
        Assert.isTrue(Long.numberOfTrailingZeros(cb.pieces[1][6]) == cb.kingIndex[1], "Long.numberOfTrailingZeros(cb.pieces[BLACK][KING]) == cb.kingIndex[BLACK]");
        boolean[] castling_rights = new boolean[]{(cb.castlingRights & 8) != 0, (cb.castlingRights & 4) != 0, (cb.castlingRights & 2) != 0, (cb.castlingRights & 1) != 0};
        ChessBoardUtil.init(cb, castling_rights);
        Assert.isTrue(castlingRights == cb.castlingRights, "castlingRights == cb.castlingRights, castlingRights=" + castlingRights + ", cb.castlingRights=" + cb.castlingRights);
        Assert.isTrue(iterativeZK == cb.zobristKey, "iterativeZK == cb.zobristKey, iterativeZK=" + iterativeZK + ", cb.zobristKey=" + cb.zobristKey);
        Assert.isTrue(iterativeZKPawn == cb.pawnZobristKey, "iterativeZKPawn == cb.pawnZobristKey");
        Assert.isTrue(whiteKingArea == cb.kingArea[0], "whiteKingArea == cb.kingArea[WHITE]");
        Assert.isTrue(blackKingArea == cb.kingArea[1], "blackKingArea == cb.kingArea[BLACK]");
        Assert.isTrue(pinnedPieces == cb.pinnedPieces, "pinnedPieces == cb.pinnedPieces");
        Assert.isTrue(discoveredPieces == cb.discoveredPieces, "discoveredPieces == cb.discoveredPieces");
        Assert.isTrue(iterativeWhitePieces == cb.friendlyPieces[0], "iterativeWhitePieces == cb.friendlyPieces[WHITE]");
        Assert.isTrue(iterativeBlackPieces == cb.friendlyPieces[1], "iterativeBlackPieces == cb.friendlyPieces[BLACK]");
        Assert.isTrue(iterativeAllPieces == cb.allPieces, "iterativeAllPieces == cb.allPieces");
        Assert.isTrue((iterativeBlackPieces & iterativeWhitePieces) == 0L, "(iterativeBlackPieces & iterativeWhitePieces) == 0");
        Assert.isTrue(iterativePsqt_mg == cb.psqtScore_mg, "iterativePsqt_mg == cb.psqtScore_mg, iterativePsqt_mg=" + iterativePsqt_mg + ", cb.psqtScore_mg=" + cb.psqtScore_mg);
        Assert.isTrue(iterativePsqt_eg == cb.psqtScore_eg, "iterativePsqt_eg == cb.psqtScore_eg, iterativePsqt_eg=" + iterativePsqt_eg + ", cb.psqtScore_eg=" + cb.psqtScore_eg);
        for (int i = 0; i < testPieceIndexes.length; ++i) {
            Assert.isTrue(testPieceIndexes[i] == cb.pieceIndexes[i], "testPieceIndexes[i] == cb.pieceIndexes[i]");
        }
        Assert.isTrue(material_factor_white == cb.material_factor_white, "material_factor_white == cb.material_factor_white");
        Assert.isTrue(material_factor_black == cb.material_factor_black, "material_factor_black == cb.material_factor_black");
        Assert.isTrue(materialKey == (long)cb.materialKey, "materialKey == cb.materialKey");
    }
}

