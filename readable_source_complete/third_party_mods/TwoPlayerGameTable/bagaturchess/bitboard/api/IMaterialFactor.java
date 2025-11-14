/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.api;

import bagaturchess.bitboard.common.MoveListener;

public interface IMaterialFactor
extends MoveListener {
    public int getBlackFactor();

    public int getWhiteFactor();

    public int getTotalFactor();

    public double getOpenningPart();

    public int interpolateByFactor(int var1, int var2);

    public int interpolateByFactor(double var1, double var3);
}

