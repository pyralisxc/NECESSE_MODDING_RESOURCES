/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.engines.evaladapters.carballo;

import bagaturchess.engines.evaladapters.carballo.BitboardAttacksMagic;
import bagaturchess.engines.evaladapters.carballo.BitboardUtils;

public class BitboardAttacks {
    public long[] rook;
    public long[] bishop;
    public long[] knight;
    public long[] king;
    public long[][] pawn;
    public static boolean USE_MAGIC = true;
    static BitboardAttacks instance;

    public static BitboardAttacks getInstance() {
        if (instance == null && USE_MAGIC) {
            instance = new BitboardAttacksMagic();
        }
        return instance;
    }

    long squareAttackedAux(long square, int shift, long border) {
        if ((square & border) == 0L) {
            square = shift > 0 ? (square <<= shift) : (square >>>= -shift);
            return square;
        }
        return 0L;
    }

    long squareAttackedAuxSlider(long square, int shift, long border) {
        long ret = 0L;
        while ((square & border) == 0L) {
            square = shift > 0 ? (square <<= shift) : (square >>>= -shift);
            ret |= square;
        }
        return ret;
    }

    BitboardAttacks() {
        long time1 = System.currentTimeMillis();
        this.rook = new long[64];
        this.bishop = new long[64];
        this.knight = new long[64];
        this.king = new long[64];
        this.pawn = new long[2][64];
        long square = 1L;
        int i = 0;
        while (square != 0L) {
            this.rook[i] = this.squareAttackedAuxSlider(square, 8, -72057594037927936L) | this.squareAttackedAuxSlider(square, -8, 255L) | this.squareAttackedAuxSlider(square, -1, 0x101010101010101L) | this.squareAttackedAuxSlider(square, 1, -9187201950435737472L);
            this.bishop[i] = this.squareAttackedAuxSlider(square, 9, -35887507618889600L) | this.squareAttackedAuxSlider(square, 7, -71775015237779199L) | this.squareAttackedAuxSlider(square, -7, -9187201950435737345L) | this.squareAttackedAuxSlider(square, -9, 0x1010101010101FFL);
            this.knight[i] = this.squareAttackedAux(square, 17, -140185576636288L) | this.squareAttackedAux(square, 15, -280371153272575L) | this.squareAttackedAux(square, -15, -9187201950435704833L) | this.squareAttackedAux(square, -17, 0x10101010101FFFFL) | this.squareAttackedAux(square, 10, -17802464409370432L) | this.squareAttackedAux(square, 6, -71209857637481725L) | this.squareAttackedAux(square, -6, -4557430888798830337L) | this.squareAttackedAux(square, -10, 0x3030303030303FFL);
            this.pawn[0][i] = this.squareAttackedAux(square, 7, -71775015237779199L) | this.squareAttackedAux(square, 9, -35887507618889600L);
            this.pawn[1][i] = this.squareAttackedAux(square, -7, -9187201950435737345L) | this.squareAttackedAux(square, -9, 0x1010101010101FFL);
            this.king[i] = this.squareAttackedAux(square, 8, -72057594037927936L) | this.squareAttackedAux(square, -8, 255L) | this.squareAttackedAux(square, -1, 0x101010101010101L) | this.squareAttackedAux(square, 1, -9187201950435737472L) | this.squareAttackedAux(square, 9, -35887507618889600L) | this.squareAttackedAux(square, 7, -71775015237779199L) | this.squareAttackedAux(square, -7, -9187201950435737345L) | this.squareAttackedAux(square, -9, 0x1010101010101FFL);
            square <<= 1;
            i = (byte)(i + 1);
        }
        long time2 = System.currentTimeMillis();
    }

    public long getRookAttacks(int index, long all) {
        return this.getRookShiftAttacks(BitboardUtils.index2Square(index), all);
    }

    public long getBishopAttacks(int index, long all) {
        return this.getBishopShiftAttacks(BitboardUtils.index2Square(index), all);
    }

    public long getRookShiftAttacks(long square, long all) {
        return this.checkSquareAttackedAux(square, all, 8, -72057594037927936L) | this.checkSquareAttackedAux(square, all, -8, 255L) | this.checkSquareAttackedAux(square, all, -1, 0x101010101010101L) | this.checkSquareAttackedAux(square, all, 1, -9187201950435737472L);
    }

    public long getBishopShiftAttacks(long square, long all) {
        return this.checkSquareAttackedAux(square, all, 9, -35887507618889600L) | this.checkSquareAttackedAux(square, all, 7, -71775015237779199L) | this.checkSquareAttackedAux(square, all, -7, -9187201950435737345L) | this.checkSquareAttackedAux(square, all, -9, 0x1010101010101FFL);
    }

    private long checkSquareAttackedAux(long square, long all, int shift, long border) {
        long ret = 0L;
        while ((square & border) == 0L) {
            square = shift > 0 ? (square <<= shift) : (square >>>= -shift);
            ret |= square;
            if ((square & all) == 0L) continue;
            break;
        }
        return ret;
    }
}

