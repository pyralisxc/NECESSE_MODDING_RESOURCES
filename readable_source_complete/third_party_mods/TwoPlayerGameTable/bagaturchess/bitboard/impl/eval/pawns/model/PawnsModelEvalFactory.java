/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.eval.pawns.model;

import bagaturchess.bitboard.impl.datastructs.lrmmap.DataObjectFactory;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEval;

public class PawnsModelEvalFactory
implements DataObjectFactory<PawnsModelEval> {
    @Override
    public PawnsModelEval createObject() {
        return new PawnsModelEval();
    }
}

