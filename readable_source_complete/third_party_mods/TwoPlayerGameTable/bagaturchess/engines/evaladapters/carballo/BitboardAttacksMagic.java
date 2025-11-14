/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.engines.evaladapters.carballo;

import bagaturchess.engines.evaladapters.carballo.BitboardAttacks;

public class BitboardAttacksMagic
extends BitboardAttacks {
    public static final byte[] rookShiftBits = new byte[]{12, 11, 11, 11, 11, 11, 11, 12, 11, 10, 10, 10, 10, 10, 10, 11, 11, 10, 10, 10, 10, 10, 10, 11, 11, 10, 10, 10, 10, 10, 10, 11, 11, 10, 10, 10, 10, 10, 10, 11, 11, 10, 10, 10, 10, 10, 10, 11, 11, 10, 10, 10, 10, 10, 10, 11, 12, 11, 11, 11, 11, 11, 11, 12};
    public static final byte[] bishopShiftBits = new byte[]{6, 5, 5, 5, 5, 5, 5, 6, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 7, 7, 7, 7, 5, 5, 5, 5, 7, 9, 9, 7, 5, 5, 5, 5, 7, 9, 9, 7, 5, 5, 5, 5, 7, 7, 7, 7, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 5, 5, 5, 5, 5, 5, 6};
    public static final long[] rookMagicNumber = new long[]{1188968443571863584L, 18049583150022656L, 72066527574364416L, 324263639937187968L, 72066390214836752L, 72059827437700096L, 180145084614836736L, 72058144334938368L, 288371115791745056L, 70437467856896L, 72198400247857280L, 0x801000800800L, 0x800400080080L, 140746086679552L, 281483566907648L, 5206301907819167872L, 9007751158054912L, 70643890528256L, 141287512612864L, 143486535862272L, 0x808004000800L, 5630049323582592L, 4398081116161L, 2199040049284L, 140877074808832L, 35186523766912L, 580964422799851648L, 0x10008080080010L, 0x4000080080040080L, 4400194125952L, 281479271940608L, 36029355364729124L, 141012374650912L, 141012374659072L, 140806216228864L, 2305983815429916676L, 0x80080800400L, 36033197221347840L, 140741791711744L, 550863110404L, 35735201677320L, 4591561094479872L, 17592722948224L, 2305860601533988992L, 144119586256617600L, 2253998904082560L, 281483566710788L, 72061994236248065L, 36029071898968128L, 35185446879552L, 9007508493434944L, 17594334052992L, -9151173688148098944L, -9222243935776931712L, -8070169040057794304L, 4400198255104L, 35735202759170L, 576602589873848338L, 281509873336337L, 281612483821577L, 18295942243942405L, 36310280720089089L, 4611686576790964228L, 172889244674L};
    public static final long[] bishopMagicNumber = new long[]{1161933170632310912L, 565166173978624L, 2254016556040192L, 289360676336460800L, 577626372067819784L, 2410198350168064L, 282579055935616L, 70523380318212L, 1152956723609239682L, 0x20802208200L, 4755818803018342400L, 283951042396192L, 0x20210000000L, 558618378496L, 0x10110022000L, 36038694905382944L, 2251868571437056L, 2269531619754496L, 18295942209896704L, 38280599013703680L, 145241096582332416L, 71476863566080L, 0x2000101012000L, 288794427797848576L, 4521191882637824L, 571746583381056L, 79164904317952L, 0x2008008008202L, 282576636887040L, 283674016760070L, 285881618532352L, 2323268078141697L, 316728070374400L, 565767452102656L, 1126054526191680L, 5764609724205498496L, 290279659676160L, 1126458286311424L, 571754904879232L, 576602116857037312L, 290408575799296L, 290279697485952L, 145136742891777L, 18014673660019200L, 360429876454360064L, 9042384708960320L, 1155174575739439616L, 285946071224384L, 0x11010120200000L, 0x2020222020C00L, 288230530804615168L, 0x20880000L, 73585001472L, 4802676588560384L, 148637548129845248L, 567902084399232L, 1152939655273005056L, 281754158505984L, 0x41044100L, 0x840400L, 69239300L, 576460889784451596L, 9483306926336L, 567352362270848L};
    public long[] rookMask;
    public long[][] rookMagic;
    public long[] bishopMask;
    public long[][] bishopMagic;

    public BitboardAttacksMagic() {
        long time1 = System.currentTimeMillis();
        this.rookMask = new long[64];
        this.rookMagic = new long[64][];
        this.bishopMask = new long[64];
        this.bishopMagic = new long[64][];
        long square = 1L;
        int i = 0;
        while (square != 0L) {
            this.rookMask[i] = this.squareAttackedAuxSliderMask(square, 8, -72057594037927936L) | this.squareAttackedAuxSliderMask(square, -8, 255L) | this.squareAttackedAuxSliderMask(square, -1, 0x101010101010101L) | this.squareAttackedAuxSliderMask(square, 1, -9187201950435737472L);
            this.bishopMask[i] = this.squareAttackedAuxSliderMask(square, 9, -35887507618889600L) | this.squareAttackedAuxSliderMask(square, 7, -71775015237779199L) | this.squareAttackedAuxSliderMask(square, -7, -9187201950435737345L) | this.squareAttackedAuxSliderMask(square, -9, 0x1010101010101FFL);
            int rookPositions = 1 << rookShiftBits[i];
            this.rookMagic[i] = new long[rookPositions];
            for (int j = 0; j < rookPositions; ++j) {
                long pieces = this.generatePieces(j, rookShiftBits[i], this.rookMask[i]);
                int magicIndex = BitboardAttacksMagic.magicTransform(pieces, rookMagicNumber[i], rookShiftBits[i]);
                this.rookMagic[i][magicIndex] = this.getRookShiftAttacks(square, pieces);
            }
            int bishopPositions = 1 << bishopShiftBits[i];
            this.bishopMagic[i] = new long[bishopPositions];
            for (int j = 0; j < bishopPositions; ++j) {
                long pieces = this.generatePieces(j, bishopShiftBits[i], this.bishopMask[i]);
                int magicIndex = BitboardAttacksMagic.magicTransform(pieces, bishopMagicNumber[i], bishopShiftBits[i]);
                this.bishopMagic[i][magicIndex] = this.getBishopShiftAttacks(square, pieces);
            }
            square <<= 1;
            i = (byte)(i + 1);
        }
        long time2 = System.currentTimeMillis();
    }

    private long generatePieces(int index, int bits, long mask) {
        long result = 0L;
        for (int i = 0; i < bits; ++i) {
            long lsb = mask & -mask;
            mask ^= lsb;
            if ((index & 1 << i) == 0) continue;
            result |= lsb;
        }
        return result;
    }

    private long squareAttackedAuxSliderMask(long square, int shift, long border) {
        long ret = 0L;
        while ((square & border) == 0L) {
            square = shift > 0 ? (square <<= shift) : (square >>>= -shift);
            if ((square & border) != 0L) continue;
            ret |= square;
        }
        return ret;
    }

    public static int magicTransform(long b, long magic, byte bits) {
        return (int)(b * magic >>> 64 - bits);
    }

    @Override
    public long getRookAttacks(int index, long all) {
        int i = BitboardAttacksMagic.magicTransform(all & this.rookMask[index], rookMagicNumber[index], rookShiftBits[index]);
        return this.rookMagic[index][i];
    }

    @Override
    public long getBishopAttacks(int index, long all) {
        int i = BitboardAttacksMagic.magicTransform(all & this.bishopMask[index], bishopMagicNumber[index], bishopShiftBits[index]);
        return this.bishopMagic[index][i];
    }
}

