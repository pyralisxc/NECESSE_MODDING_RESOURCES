/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.api;

import bagaturchess.bitboard.api.IBinarySemaphore;
import bagaturchess.bitboard.impl.datastructs.lrmmap.DataObjectFactory;
import bagaturchess.bitboard.impl.datastructs.lrmmap.LRUMapLongObject;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModelEval;

public class PawnsEvalCache
extends LRUMapLongObject<PawnsModelEval> {
    public PawnsEvalCache(DataObjectFactory<PawnsModelEval> _factory, int _maxSize, boolean fillWithDummyEntries, IBinarySemaphore _semaphore) {
        super(_factory, _maxSize, fillWithDummyEntries, _semaphore);
    }

    @Override
    public PawnsModelEval get(long key) {
        PawnsModelEval result = (PawnsModelEval)super.getAndUpdateLRU(key);
        return result;
    }

    public PawnsModelEval put(long hashkey) {
        PawnsModelEval entry = (PawnsModelEval)super.getAndUpdateLRU(hashkey);
        if (entry != null) {
            throw new IllegalStateException();
        }
        entry = (PawnsModelEval)this.associateEntry(hashkey);
        return entry;
    }
}

