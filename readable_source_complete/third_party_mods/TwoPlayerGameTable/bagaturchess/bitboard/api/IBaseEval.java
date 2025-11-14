/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.api;

import bagaturchess.bitboard.common.MoveListener;

public interface IBaseEval
extends MoveListener {
    public int getMaterial_o();

    public int getMaterial_e();

    public int getWhiteMaterialPawns_o();

    public int getWhiteMaterialPawns_e();

    public int getBlackMaterialPawns_o();

    public int getBlackMaterialPawns_e();

    public int getWhiteMaterialNonPawns_o();

    public int getWhiteMaterialNonPawns_e();

    public int getBlackMaterialNonPawns_o();

    public int getBlackMaterialNonPawns_e();

    public int getMaterial_BARIER_NOPAWNS_O();

    public int getMaterial_BARIER_NOPAWNS_E();

    public int getPST_o();

    public int getPST_e();

    public int getMaterial(int var1);

    public int getMaterialGain(int var1);
}

