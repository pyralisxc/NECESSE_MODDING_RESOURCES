/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.utils;

import bagaturchess.bitboard.api.IBinarySemaphore;
import bagaturchess.bitboard.api.IBinarySemaphoreFactory;
import bagaturchess.bitboard.impl.utils.BinarySemaphore_Dummy;

public class BinarySemaphoreFactory_Dummy
implements IBinarySemaphoreFactory {
    @Override
    public IBinarySemaphore createSempahore() {
        return new BinarySemaphore_Dummy();
    }
}

