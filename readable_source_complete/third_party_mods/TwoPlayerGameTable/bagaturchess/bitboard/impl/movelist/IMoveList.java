/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.movelist;

import bagaturchess.bitboard.api.IInternalMoveList;

public interface IMoveList
extends IInternalMoveList {
    public void clear();

    public int size();

    public int next();

    public int getScore();
}

