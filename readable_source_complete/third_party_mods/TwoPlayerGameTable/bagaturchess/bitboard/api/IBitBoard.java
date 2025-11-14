/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.api;

import bagaturchess.bitboard.api.IBoard;
import bagaturchess.bitboard.api.IFieldsAttacks;
import bagaturchess.bitboard.api.IPlayerAttacks;

public interface IBitBoard
extends IBoard {
    public static final boolean IMPL1 = true;

    public long getFreeBitboard();

    public long getFiguresBitboardByPID(int var1);

    public long getFiguresBitboardByColourAndType(int var1, int var2);

    public long getFiguresBitboardByColour(int var1);

    public boolean getAttacksSupport();

    public boolean getFieldsStateSupport();

    public void setAttacksSupport(boolean var1, boolean var2);

    public IPlayerAttacks getPlayerAttacks(int var1);

    public IFieldsAttacks getFieldsAttacks();
}

